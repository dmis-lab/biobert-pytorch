import json,time
import numpy as np
import pandas as pd
import os
import subprocess
import operator
import random
import logging

import collections
from collections import Counter, OrderedDict

from transformers.data.processors.squad import SquadExample

import pdb

logger = logging.getLogger(__name__)

def transform_n2b_yesno(nbest_path, output_path):
    
    ### Setting basic strings 

    #### Checking nbest_BioASQ-test prediction.json
    if not os.path.exists(nbest_path):
        print("No file exists!\n#### Fatal Error : Abort!")
        raise

    #### Reading Pred File
    with open(nbest_path, "r") as reader:
        test=json.load(reader)

    qidDict=dict()
    if True: # multi output
        for multiQid in test:
            assert len(multiQid)==(24+4) # all multiQid should have length of 24 + 3
            if not multiQid[:-4] in qidDict:
                qidDict[multiQid[:-4]]=[test[multiQid]]
            else :
                qidDict[multiQid[:-4]].append(test[multiQid])
    else: # single output
        qidDict={qid:[test[qid]] for qid in test}    

    entryList=[]
    print(len(qidDict), 'number of questions')
    for qid in qidDict:
        yesno_prob = {'yes': [], 'no': []}
        yesno_cnt = 0
        for ans, prob in qidDict[qid]:
            yesno_prob['yes'] += [float('{:.3f}'.format(prob[0]))] # For sigmoid
            yesno_cnt += 1
        
        mean = lambda x: sum(x)/len(x)
        final_answer = 'yes' if mean(yesno_prob['yes']) > 0.5 else 'no'

        entry={u"type": "yesno",
        u"id": qid,
        u"ideal_answer": ["Dummy"],
        u"exact_answer": final_answer,
        }
        entryList.append(entry)
    finalformat={u'questions':entryList}

    if os.path.isdir(output_path):
        outfilepath=os.path.join(output_path, "BioASQform_BioASQ-answer.json") # For unified output name
    else:
        outfilepath=output_path

    with open(outfilepath, "w") as outfile:
        json.dump(finalformat, outfile, indent=2)
        print("outfilepath={}".format(outfilepath))

def textrip(text):
    if text=="":
        return text
    if text[-1]==',' or text[-1]=='.' or text[-1]==' ':
        return text[:-1]
    if len(text)>2 and text[0]=='(' and text[-1]==')':
        if text.count('(')==1 and text.count(')')==1:
            return text[1:-1]
    if ('(' in text) and (')' not in text):
        return ""
    if ('(' not in text) and (')' in text):
        return ""
    return text

def transform_n2b_factoid(nbest_path, output_path):
    
    #### Checking nbest_BioASQ-test prediction.json
    if not os.path.exists(nbest_path):
        print("No file exists!\n#### Fatal Error : Abort!")
        raise

    #### Reading Pred File
    with open(nbest_path, "r") as reader:
        test=json.load(reader)

    qidDict=dict()
    if True:
        for multiQid in test:
            assert len(multiQid)==(24+4) # all multiQid should have length of 24 + 3
            if not multiQid[:-4] in qidDict:
                qidDict[multiQid[:-4]]=[test[multiQid]]
            else :
                qidDict[multiQid[:-4]].append(test[multiQid])
    else: # single output
        qidDict={qid:[test[qid]] for qid in test}    

    entryList=[]
    entryListWithProb=[]
    for qid in qidDict:

        jsonList=[]
        for jsonele in qidDict[qid]: # value of qidDict is a list
            jsonList+=jsonele

        qidDf=pd.DataFrame().from_dict(jsonList)
                
        sortedDf=qidDf.sort_values(by='probability', axis=0, ascending=False)

        sortedSumDict=OrderedDict()
        sortedSumDictKeyDict=dict() # key : noramlized key
            
        for index in sortedDf.index:
            text=sortedDf.loc[index]["text"]
            text=textrip(text)
            if text=="":
                pass
            elif len(text)>100:
                    pass
            elif text.lower() in sortedSumDictKeyDict:
                sortedSumDict[sortedSumDictKeyDict[text.lower()]] += sortedDf.loc[index]["probability"]
            else:
                sortedSumDictKeyDict[text.lower()]=text
                sortedSumDict[sortedSumDictKeyDict[text.lower()]] = sortedDf.loc[index]["probability"]        
        finalSorted=sorted(sortedSumDict.items(), key=operator.itemgetter(1), reverse=True) # for python 2, use sortedSumDict.iteritems() instead of sortedSumDict.items()
        
        entry={u"type":"factoid", 
        #u"body":qas, 
        u"id":qid, # must be 24 char
        u"ideal_answer":["Dummy"],
        u"exact_answer":[[ans[0]] for ans in finalSorted[:5]],
        # I think enough?
        }
        entryList.append(entry)
        
        entryWithProb={u"type":"factoid", 
        u"id":qid, # must be 24 char
        u"ideal_answer":["Dummy"],
        u"exact_answer":[ans for ans in finalSorted[:20]],
        }
        entryListWithProb.append(entryWithProb)
    finalformat={u'questions':entryList}
    finalformatWithProb={u'questions':entryListWithProb}

    if os.path.isdir(output_path):
        outfilepath=os.path.join(output_path, "BioASQform_BioASQ-answer.json")
        outWithProbfilepath=os.path.join(output_path, "WithProb_BioASQform_BioASQ-answer.json")
    else:
        outfilepath=output_path
        outWithProbfilepath= output_path+"_WithProb"

    with open(outfilepath, "w") as outfile:
        json.dump(finalformat, outfile, indent=2)
    with open(outWithProbfilepath, "w") as outfile_prob:
        json.dump(finalformatWithProb, outfile_prob, indent=2)


def eval_bioasq_standard(task_num, outfile, golden, cwd):
    # 1: [1, 2],  3: [3, 4],  5: [5, 6, 7, 8]

    task_e = {
        '1': 1, '2': 1,
        '3': 3, '4': 3,
        '5': 5, '6': 5, '7': 5, '8': 5
    }
    
    golden = os.path.join(os.getcwd(), golden)
    outfile = os.path.join(os.getcwd(), outfile)

    evalproc1 = subprocess.Popen(
        ['java', '-Xmx10G', '-cp',
         '$CLASSPATH:./flat/BioASQEvaluation/dist/BioASQEvaluation.jar',
         'evaluation.EvaluatorTask1b', '-phaseB',
         '-e', '{}'.format(task_e[task_num]),
         golden,
         outfile],
        cwd=cwd,
        stdout=subprocess.PIPE
    )
    stdout1, _ = evalproc1.communicate()
    
    result = [float(v) for v in stdout1.decode('utf-8').split(' ')]
    
    return result

def read_squad_examples(input_file, is_training):
    """Read a SQuAD json file into a list of SquadExample."""
    is_bioasq=True # for BioASQ

    with open(input_file, "r") as reader:
        input_data = json.load(reader)["data"]

    def is_whitespace(c):
        if c == " " or c == "\t" or c == "\r" or c == "\n" or ord(c) == 0x202F:
            return True
        return False

    examples = []
    for entry in input_data:
        for paragraph in entry["paragraphs"]:
            paragraph_text = paragraph["context"]
            doc_tokens = []
            char_to_word_offset = []
            prev_is_whitespace = True
            if is_bioasq:
                paragraph_text.replace('/',' ')  # need review
            for c in paragraph_text:
                if is_whitespace(c):
                    prev_is_whitespace = True
                else:
                    if prev_is_whitespace:
                        doc_tokens.append(c)
                    else:
                        doc_tokens[-1] += c
                    prev_is_whitespace = False
                char_to_word_offset.append(len(doc_tokens) - 1)

            for qa in paragraph["qas"]:
                qas_id = qa["id"]
                question_text = qa["question"]
                answer = None
                is_impossible = False
                if is_training:
                    assert (qa["is_impossible"] == True) != (qa["answers"] == "yes")
                    assert qa["answers"] in ["yes", "no"]
                    # answer = 1 if qa["answers"] == 'yes' else 0
                    is_impossible = qa["is_impossible"]

                example = SquadExample(
                    qas_id=qas_id,
                    question_text=question_text,
                    context_text=paragraph_text,
                    answer_text='',
                    start_position_character=None,
                    title='',
                    answers=[],
                    is_impossible=is_impossible,
                )
                examples.append(example)

    # target_cnt = 500
    if is_training:
        pos_cnt = sum([1 for example in examples if example.is_impossible == False])
        neg_cnt = sum([1 for example in examples if example.is_impossible == True])
        target_cnt = min(pos_cnt,neg_cnt)
        print()
        print('Imbalance btw {} vs {}'.format(pos_cnt, neg_cnt))
        random.shuffle(examples)

        new_examples = []
        new_pos_cnt = 0
        new_neg_cnt = 0
        for example in examples:
            if example.is_impossible == False and new_pos_cnt >= target_cnt:
                continue
            if example.is_impossible == True and new_neg_cnt >= target_cnt:
                continue
            else:
                new_examples.append(example)
                new_pos_cnt += (1 if example.is_impossible == False else 0)
                new_neg_cnt += (1 if example.is_impossible == True else 0)

        pos_cnt = sum([1 for example in new_examples if example.is_impossible == False])
        neg_cnt = sum([1 for example in new_examples if example.is_impossible == True])
        random.shuffle(new_examples)
        print('Balanced as {} vs {}'.format(pos_cnt, neg_cnt))
        print('Sample: {}'.format(new_examples[0]))
        return new_examples
    else:
        return examples

def write_predictions(all_examples, all_features, all_results, output_prediction_file):
    """Write final predictions to the json file and log-odds of null if needed."""
    logger.info("Writing predictions to: %s" % (output_prediction_file))
    
    example_index_to_features = collections.defaultdict(list)
    for feature in all_features:
        example_index_to_features[feature.example_index].append(feature)

    unique_id_to_result = {}
    for result in all_results:
        unique_id_to_result[result.unique_id] = result

    _PrelimPrediction = collections.namedtuple(  # pylint: disable=invalid-name
        "PrelimPrediction",
        ["feature_index", "answer", "logit"])

    all_predictions = collections.OrderedDict()
    all_nbest_json = collections.OrderedDict()
    scores_diff_json = collections.OrderedDict()

    for (example_index, example) in enumerate(all_examples):
        features = example_index_to_features[example_index]

        prelim_predictions = []
        # keep track of the minimum score of null start+end of position 0
        score_null = 1000000  # large and positive
        for (feature_index, feature) in enumerate(features):
            result = unique_id_to_result[feature.unique_id]
            logits = result.logits
            answer = 'yes' if logits[0] > 0.5 else 'no'

            prelim_predictions.append(
                _PrelimPrediction(
                    feature_index=feature_index,
                    answer=answer,
                    logit=logits))

            break
        assert len(prelim_predictions) == 1
        probs = logits
        all_predictions[example.qas_id] = [prelim_predictions[0].answer, probs]

    with open(output_prediction_file, "w") as writer:
        writer.write(json.dumps(all_predictions, indent=4) + "\n")



if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--output_dir",
        default=None,
        type=str,
        required=True,
        help="The output directory where the model checkpoints will be written."
    )
    parser.add_argument(
        "--nbest_path",
        default=None,
        type=str,
        help="The output directory where the model checkpoints will be written."
    )
    parser.add_argument(
        "--golden_file",
        default=None,
        type=str,
        help="BioASQ official golden answer file"
    )
    parser.add_argument(
        "--official_eval_dir",
        default='./scripts/bioasq_eval',
        type=str,
        help="BioASQ official evaluation code"
    )
    args = parser.parse_args()

    transform_n2b_factoid(args.nbest_path, args.output_dir)


    """ Evaluation - Measure
    pred_file = os.path.join(args.output_dir, "BioASQform_BioASQ-answer.json")

    eval_bioasq_standard(str(5), 
                        pred_file,
                        args.golden_file,
                        args.official_eval_dir)
    """

