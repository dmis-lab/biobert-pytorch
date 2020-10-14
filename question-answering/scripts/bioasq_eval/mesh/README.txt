Mesh 2016 Hierarchy corresponding to https://www.nlm.nih.gov/pubs/techbull/nd15/nd15_2016_mesh_avail.html

mesh_hierarchy.txt 
	parent-child relations in original DescriptorID format
	contains a line "DescriptorID1 DescriptorID2" for each line "is_a: DescriptorID1" in a [Term] block with "id: DescriptorID2" in mesh_2016.obo		
			
mesh_hiearchy_int.txt	
	parent-child relations using integer mapping
	contains a line "Integer1 Integer2" for each line "DescriptorID1 DescriptorID2" in mesh_hierarchy.txt		

mapping.txt				
	DescriptorID -  Integer mapping 
	contains a line "DescriptorID1=Integer1" for each DescriptorID 