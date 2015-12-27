NAME: Ragaprabha Chinnaswamy
KUID#: 2830383

GLOBAL DISCRETIZATION METHODS:
	The process of converting data sets with continuous attributes into input data sets with discrete attributes, called discretization algorithms. In this project, we have implemented three algorithms :
	1) Equal Interval Width Method:
			This is a simplest method to discretize a continuous attribute by partitioning its domain into equal width intervals.
	2) Equal Frequency per Interval Method:
			This process involves partitioning the domain of the continuous attribute such that the sample frequency in each interval is approximately equal.
	3) Conditional Entropy method:
			This discretization involves class-entropy as a criterion to evaluate a list of "best" breakpoints which together with the domain boundary points induce the desired intervals.
			
PREREQUISITE:
	1. Any Java supported machine- eg: Windows, Mac, Linux.
	2. Required Java version 1.7 or higher
	3. Required Java Compiler 1.7 or higher
	4. Referenced Libraries - guava-18.0 jar (Attached to project workspace)
	5. Input Text are to be placed directly in the workspace folder. EECS837/<input.txt>
	
PROJECT DETAILS:
	1. This project is implemented for the following 3 mentioned algorithm.
	2. The project must have the required libraries before running.
	3. The Main() function is available in the InputData.Java
	3. This project expects the input data as a text file and then requires an input from the user to to determine the algorithm to be executed.
	
********************************************************************************************************************************************************************************************
OUTPUT:
1. When Input File is not available in the workspace location.
		
Enter the input file name: 
idd.txt
File not found
_________________________________________________________________
2. When the input file is invalid.

Enter the input file name: 
in.txt
Invalid File Data. Check input file Data
_________________________________________________________________
3. When the Input File is Valid, it expects the user to specify the type of algorithm to execute.

Enter the input file name: 
input.txt
No of Attributes :  3
No of Decision :  1
Total No.of Rows: 7

Enter the Algorithm to Execute(1,2,3):
1. Equal Interval Width
2. Equal Frequency per Interval
3. Conditional Entropy

(If the user enters Invalid number other than 1,2 or 3)
Enter the input file name: 
input.txt
No of Attributes :  3
No of Decision :  1
Total No.of Rows: 7

Enter the Algorithm to Execute(1,2,3):
1. Equal Interval Width
2. Equal Frequency per Interval
3. Conditional Entropy
4
Enter a Valid Input: 
1
____________________________________________________________________
4. Output for the FIRST algorithm(Equal Interval Width),

INPUT DATA USED:
< a a a d >
[ A B C D ]
0.8 0.3 7.2 very-small
0.8 1.1 7.2 small
0.8 1.1 10.2 medium
1.2 0.3 10.2 medium
1.2 2.3 10.2 medium
2.0 2.3 10.2 high
2.0 2.3 15.2 very-high
++++++++++++++++++++++++++++++++++++++++
Output:
Average Block Entropies for Iteration: 1
A = 0.63248235
B = 0.7682063
C = 0.7682063
Worst Attribute is: B

Average Block Entropies for Iteration: 2
A = 0.63248235
B = 0.41689944
C = 0.7682063
Worst Attribute is: C

[ A	B	C	D ]
0.8..1.4	0.3..0.9666667	7.2..9.866667	very-small
0.8..1.4	0.9666667..2.3	7.2..9.866667	small
0.8..1.4	0.9666667..2.3	9.866667..12.533334	medium
0.8..1.4	0.3..0.9666667	9.866667..12.533334	medium
0.8..1.4	0.9666667..2.3	9.866667..12.533334	medium
1.4..2.0	0.9666667..2.3	9.866667..12.533334	high
1.4..2.0	0.9666667..2.3	12.533334..15.2	very-high

'A' CutPoints are:
0.8	1.4	2.0	
'B' CutPoints are:
0.3	0.9666667	2.3	
'C' CutPoints are:
7.2	9.866667	12.533334	15.2	
____________________________________________________________________
5. Output for the SECOND algorithm(Equal Frequency per Interval),

INPUT DATA USED:
< a a a d >
[ Wind Humidity Temp Trip ]
5 20 26 yes
10 40 20 yes
5 60 20 yes
10 20 16 no
15 40 20 no
10 50 26 no
++++++++++++++++++++++++++++++++++++++++
Enter the input file name: 
in.txt
No of Attributes :  3
No of Decision :  1
Total No.of Rows: 6

Enter the Algorithm to Execute(1,2,3):
1. Equal Interval Width
2. Equal Frequency per Interval
3. Conditional Entropy
2

Average Block Entropies for Iteration: 1
Humidity = 0.5
Temp = 0.5
Wind = 0.27042603
Worst Attribute is: Humidity

Average Block Entropies for Iteration: 2
Humidity = 0.33333334
Temp = 0.5
Wind = 0.27042603
Worst Attribute is: Temp

Average Block Entropies for Iteration: 3
Humidity = 0.33333334
Temp = 0.26416042
Wind = 0.27042603
Worst Attribute is: Humidity

Average Block Entropies for Iteration: 4
Humidity = 0.16666667
Temp = 0.26416042
Wind = 0.27042603
Worst Attribute is: Wind

[ Wind	Humidity	Temp	Trip ]
5.0..12.5	20.0..45.0	23.0..26.0	yes
5.0..12.5	20.0..45.0	18.0..23.0	yes
5.0..12.5	45.0..60.0	18.0..23.0	yes
5.0..12.5	20.0..45.0	16.0..18.0	no
12.5..15.0	20.0..45.0	18.0..23.0	no
5.0..12.5	45.0..60.0	23.0..26.0	no

'Wind' CutPoints are:
5.0	12.5	15.0	
'Humidity' CutPoints are:
20.0	45.0	60.0	
'Temp' CutPoints are:
16.0	18.0	23.0	26.0
____________________________________________________________________
6. Output for the THIRD algorithm(Conditional Entropy),

INPUT DATA USED:
< a a a d >
[ Weight Length Height D ]
0.8 0.3 7.2 very-small
0.8 1.1 7.2 small
0.8 1.1 10.2 medium
1.2 0.3 10.2 medium
1.2 2.3 10.2 medium
2.0 2.3 10.2 high
2.0 2.3 15.2 very-high
++++++++++++++++++++++++++++++++++++++++
Enter the input file name: 
in2.txt
No of Attributes :  3
No of Decision :  1
Total No.of Rows: 7

Enter the Algorithm to Execute(1,2,3):
1. Equal Interval Width
2. Equal Frequency per Interval
3. Conditional Entropy
3

Average Block Entropies for Iteration: 1
Weight = 0.63248235
Length = 0.7682063
Height = 0.63248235
Worst Attribute is: Length

Average Block Entropies for Iteration: 2
Weight = 0.63248235
Length = 0.41689944
Height = 0.63248235
Worst Attribute is: Weight

Average Block Entropies for Iteration: 3
Weight = 0.32166132
Length = 0.41689944
Height = 0.63248235
Worst Attribute is: Height

[ Weight	Length	Height	D ]
0.8..1.0	0.3..0.70000005	7.2..8.7	very-small
0.8..1.0	0.70000005..1.7	7.2..8.7	small
0.8..1.0	0.70000005..1.7	8.7..12.7	medium
1.0..1.6	0.3..0.70000005	8.7..12.7	medium
1.0..1.6	1.7..2.3	8.7..12.7	medium
1.6..2.0	1.7..2.3	8.7..12.7	high
1.6..2.0	1.7..2.3	12.7..15.2	very-high

'Weight' CutPoints are:
1.0	1.6	
'Length' CutPoints are:
0.70000005	1.7	
'Height' CutPoints are:
8.7	12.7	


