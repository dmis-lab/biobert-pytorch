#!/usr/bin/python

import json
import csv
import re
import time
from argparse import ArgumentParser


def evaluate_grants(path_to_gt_json, path_to_test_json):
    """
    This function measures the micro recall of the grantIDs identified by a system.
    Input:
        - gt_json: Path to ground truth .json file
        - test_json: Path to user submitted .json file
    Output:
        - Float value of micro recall        
    ATTENTION: The .json files are expected to have the structure (keywords, number of elements etc.)
               as  explicitly mentioned in the corresponding guidelines
    """

    # Load gt data from json
    with open(path_to_gt_json, 'r') as f:
        gt_json = json.load(f)
    # Load test data from json
    with open(path_to_test_json, 'r') as f:
        test_json = json.load(f)

    # Predicted True Positive Count
    tp = 0
    # True Positive
    denom = 0
    # Mapping between articles-grants in ground truth json
    y_true = {}
    # Mapping between articles-grants in test json
    y_pred = {}
    for i, ar in enumerate(gt_json['articles']):
        # temporary list of grants for each article
        tmp = []
        for grant in ar['grantList']:
            # if a specific grant is bound to some grantID
            if 'grantID' in grant:
                # !! LOWERCASE NORMALISATION !!
                tmp.append(grant['grantID'].lower())
        # Keep unique grantIDs for each article
        y_true[ar['pmid']] = set(tmp)
        # Do the same for the test .json. Lowercase and uniquify as well
        y_pred[test_json['articles'][i]['pmid']] = set([gr['grantID'].lower() for gr in test_json['articles'][i]['grantList'] if 'grantID' in gr])

    # For each article
    for pmid, true in y_true.iteritems():
        # Find the count of intersecting grantIDs between the
        # ground truth and the predicted lists.
        # These are the true predicted positive grantIDs
        tp += len(true.intersection(y_pred[pmid]))
        # True Positive count
        denom += len(true)

    return tp/float(denom)


def get_hier_from_txt(path_to_txt):
    """
    Function to get the hiearchy dictionary, from the .txt file provided by
    the Task 5c Organizers.
    Input:
        - path_to_txt: Str, Absolute path of the .txt file
    Output:
        - hier: Dictionary with the hierarchy of the agencies in the form of
                dic[parent_agency]=[child1, child2, ..., childN]
    """
    # Initialize
    hier = {}
    # Open as .csv
    with open(path_to_txt, 'rb') as csvfile:
        # Tab-delimited file
        spamreader = csv.reader(csvfile, delimiter='\t')
        for i, row in enumerate(spamreader):
            # Skip the first row that contains the 'Parent-Child' text
            if i > 0:
                parent, child = row[0], row[1]
                # If first time coming across this agency, create new association
                if not(parent in hier):
                    hier[parent] = [child]
                # Else append the child in the list containing the previously found children
                else:
                    hier[parent].append(child)
    return hier


def get_kids(dic, resource, current):
    """
    Function to get iteratively all kids regarding a parent agency.
    Input:
        - dic: Dictionary with the hierarchy of the agencies in the form of
               dic[parent_agency]=[child1, child2, ..., childN]
        - resource: String, the name of the parent agency
        - current: [], empty list to gather all kids
    Output:
        - List, containing the parent and all the children agencies, if any
    """

    # Update the children list with the current agency
    current.append(resource)
    # If a leaf or no kids return
    if not(resource) in dic:
        pass
    else:
        # For each child agency
        for kid in dic[resource]:
            # Iteratively get their children as well
            current.extend(get_kids(dic, kid, current))
    return sorted(list(set(current)))



def check_equals(true_ag, test_ag, hier=None, check_hier=True):
    """
    Checks if one agency is equal to another, also taking into account hierarcy.
    Input:
        - true_ag: Str of the true agency
        - test_ag: Str of the predicted agency
        - hier: Dictionary with the hierarchy of the agencies in the form of
                hier[parent_agency]=[child1, child2, ..., childN]
        - check_hier: Boolean, to take hierarchy into account or not
    Output:
        - Boolean, True if equal or Not
    """
    flag = False
    # If direct string equality
    if true_ag == test_ag:
        flag = True
    # Of if we want to check for hierarchical equality
    if check_hier:
        # If the predicted agency is a children of the true agency
        if test_ag in get_kids(hier, true_ag, []):
            flag = True
        # Special case to deal with the default value 'Public Health Service'. 
        # When a valid two-letter code cannot be found in the grant 
        # number string supplied by the author, then a default value of 'Public
        # Health Service' is put in the MEDLINE/PubMed records. So if the 
        # 'Department of Health and Human Services' or any of it's descendants
        # is provided as an answer, it is thought to be correct for this case.
        if true_ag == 'Public Health Service':
            if (test_ag == 'Department of Health and Human Services') or (test_ag in get_kids(hier, 'Department of Health and Human Services', [])):
                flag = True
    return flag


def evaluate_agencies(path_to_gt_json, path_to_test_json, path_to_hier_txt, check_hier):
    """
    This function measures the micro recall of the agencies identified by a system.
    Input:
        - path_to_gt_json: Absolute path to ground truth .json file
        - path_to_test_json: Absolute path to user submitted .json file
        - path_to_hier_txt: Absolute path to hierarcy list .txt file provided by the organizers
        - check_hier: Boolean, to take hierarchy into account or not
    Output:
        - Float value of micro recall
    ATTENTION: 1) The .json files and the .txt files are expected to have the structure 
                  (keywords, number of elements etc.) as  explicitly mentioned in the 
                  corresponding guidelines.
               2) In Task 5C for 2017, there are no child-parent relationships between 
                  agencies of the same article, present in the ground truth data.
    """

    # Load gt data from json
    with open(path_to_gt_json, 'r') as f:
        gt_json = json.load(f)
    # Load test data from json
    with open(path_to_test_json, 'r') as f:
        test_json = json.load(f)
    # True Predicted Positive Count
    tp = 0
    # True Positive
    denom = 0
    # Mapping between articles-agencies in ground truth json
    y_true = {}
    # Mapping between articles-agencies in test json
    y_pred = {}
    # Get the hierarchy from the .txt file
    if check_hier:
        hier = get_hier_from_txt(path_to_hier_txt)
    else:
        hier = None
    for i, ar in enumerate(gt_json['articles']):
        # temporary list of agencies for each article
        tmp = []
        for grant in ar['grantList']:
            if 'agency' in grant:
                tmp.append(grant['agency'])
        # Keep unique agencies for each article
        y_true[ar['pmid']] = set(tmp)
        # Do the same for the test .json. Uniquify as well.
        y_pred[test_json['articles'][i]['pmid']] = set([gr['agency'] for gr in test_json['articles'][i]['grantList'] if 'agency' in gr])
    # For each article
    for pmid, true in y_true.iteritems():
        # Increment the True Positive count
        denom += len(true)
        # For each correct agency
        for true_ag in true:
            for test_ag in y_pred[pmid]:
                # Cross-check if the current agency is equal with one
                # of the submitted ones for the specific article
                # , also taking into account hierarchy 
                if check_equals(true_ag, test_ag, hier, check_hier):
                    # If yes, increment the True Predicted Positive Count
                    # And stop checking for the current agency
                    tp += 1
                    break
    return tp/float(denom)


def evaluate_joint(path_to_gt_json, path_to_test_json, path_to_hier_txt, check_hier):
    """
    This function measures the micro recall of the joint grantID-agency identified by a system.
    Input:
        - path_to_gt_json: Absolute path to ground truth .json file
        - path_to_test_json: Absolute path to user submitted .json file
        - path_to_hier_txt: Absolute path to hierarcy list .txt file provided by the organizers
        - check_hier: Boolean, to take hierarchy into account or not
    Output:
        - Float value of micro recall
    ATTENTION: 1) The .json files and the .txt files are expected to have the structure 
                  (keywords, number of elements etc.) as  explicitly mentioned in the 
                  corresponding guidelines.
               2) In Task 5C for 2017, there are no child-parent relationships between 
                  agencies of the same article, present in the ground truth data.
    """
    # Load gt data from json
    with open(path_to_gt_json, 'r') as f:
        gt_json = json.load(f)
    # Load test data from json
    with open(path_to_test_json, 'r') as f:
        test_json = json.load(f)

    # Predicted True Positive Count
    tp = 0
    # True Positive
    denom = 0
    # Mapping between articles and joint info in ground truth json
    y_true = {}
    # Mapping between articles and joint info in test json
    y_pred = {}
    # Get the hierarchy from the .txt file
    if check_hier:
        hier = get_hier_from_txt(path_to_hier_txt)
    else:
        hier = None
    for i, ar in enumerate(gt_json['articles']):
        # temporary list of joint grantIDs-agency per article
        tmp = []
        for grant in ar['grantList']:
            # If we have a prediction for both
            if 'agency' in grant and 'grantID' in grant:
                tmp.append(grant)
        y_true[ar['pmid']] = tmp
        # Do the same for the test .json.
        y_pred[test_json['articles'][i]['pmid']] = [gr for gr in test_json['articles'][i]['grantList'] if 'agency' in gr and 'grantID' in gr]
    # For each article
    for pmid, true in y_true.iteritems():
        # Increment the True Positive count
        denom += len(true)
        # For each correct joint grant-agency
        for true_gr in true:
            # For each submitted joint grant-agency
            for test_gr in y_pred[pmid]:
                # Check if the id matches first (lowercased)
                if true_gr['grantID'].lower() == test_gr['grantID'].lower():
                    # If yes, then check equality (or hierarchical relation) of predicted agency
                    if check_equals(true_gr['agency'], test_gr['agency'], hier, check_hier):
                        # If yes, increment the True Predicted Positive Count
                        # And stop checking for the current joint grantID-agency
                        tp += 1
                        break
    return tp/float(denom)

if __name__ == '__main__':
    t0 = time.time()
    parser = ArgumentParser(description='Script for evaluating .json files of submitted systems for Task 5C.')
    parser.add_argument('-x', '--true', type=str,
                        required=True, dest='true_path',
                        help='Absolute path to ground truth .json')
    parser.add_argument('-y', '--test', type=str,
                        required=True, dest='test_path',
                        help='Absolute path to test .json')
    parser.add_argument('-z', '--hier', type=str,
                        required=False, dest='hier_path', default=None,
                        help='Absolute path to hier .txt file')

    args = parser.parse_args()
    true = args.true_path
    test = args.test_path
    hier = args.hier_path
    if hier:
        check_hier = True
    else:
        check_hier = False
    print("~"*50)
    print('Currently evaluating submitted .json file: %s' % test)
    print('Against grount truth .json file: %s' % true)
    print('Taking into account hierarcy: %s' % str(check_hier))
    mr_gr = evaluate_grants(true, test)
    mr_ag = evaluate_agencies(true, test, hier, check_hier)
    mr_jo = evaluate_joint(true, test, hier, check_hier)
    print("~"*50)
    print("Grant ID MiR: %0.4f\t|\tGrant Agency MiR: %0.4f\t|\tFull Grant MiR: %0.4f" % (mr_gr, mr_ag, mr_jo))
    print("Completed in time: %0.2f sec" % (time.time() - t0))
    print("~"*50)
