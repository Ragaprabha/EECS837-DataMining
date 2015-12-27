package discritization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import model.DataText;
import model.IntermediateData;

public class InputData {

	private static String fileName;
	private static String decision;
	private static List<DataText> dataTexts = new ArrayList<DataText>();
	private static List<String> attributeName = new ArrayList<String>();
	private static int noOfAttributes, noOfDecision, algorithmType;
	private static List<IntermediateData> intermediateDatas = new ArrayList<IntermediateData>();
	private static List<IntermediateData> mergeDatas1 = new ArrayList<IntermediateData>();

	private static float consistency = 0;

	/**
	 * Main function.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		fileName = getFileName();
		fileValidator();
		readFile();
		getAlgorithmtoExecute();
	}

	/**
	 * Method to get the type of algorithm to execute.
	 */
	private static void getAlgorithmtoExecute() {
		System.out
				.println("\nEnter the Algorithm to Execute(1,2,3):\n1. Equal Interval Width\n2. Equal Frequency per Interval\n3. Conditional Entropy");
		Scanner algorithmScanner = new Scanner(System.in);
		boolean flag = false;
		while (!flag) {
			algorithmType = algorithmScanner.nextInt();
			switch (algorithmType) {
			case 1: {
				PrintStream out = null;
				try {
					out = new PrintStream(new FileOutputStream(
							fileName.replaceAll(".d", ".int")));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				System.setOut(out);
				DiscretizationImpl discretizationImpl = new DiscretizationImpl(
						dataTexts, attributeName);
				Map<String, Float> median = discretizationImpl
						.calculateMedian();
				intermediateDatas = discretizationImpl.modifyTable(median);
				consistency = discretizationImpl
						.checkConsistency(intermediateDatas);
				int iterationCount = 0;
				while (consistency < 1.0) {
					iterationCount++;
					List<List<String>> listAttributes = discretizationImpl
							.getAttributesList(intermediateDatas);
					Map<String, Float> averageBlockEntropies = discretizationImpl
							.worstAttribute(listAttributes, intermediateDatas);
					Iterator it = averageBlockEntropies.entrySet().iterator();
					int iteratorValue = 0;
					String attributeKey = null;
					float temp = 0;
					System.out
							.println("\nAverage Block Entropies for Iteration: "
									+ iterationCount);
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						if (iteratorValue == 0) {
							temp = (float) pair.getValue();
							attributeKey = pair.getKey().toString().trim();
							iteratorValue++;
						} else if ((float) pair.getValue() > temp) {
							temp = (float) pair.getValue();
							attributeKey = pair.getKey().toString().trim();
						}
						System.out.println(pair.getKey() + " = "
								+ pair.getValue());
						it.remove();
					}
					System.out.println("Worst Attribute is: " + attributeKey);
					Map<String, Float> cutPoint = discretizationImpl
							.calculateCutPoint(attributeKey);
					intermediateDatas = discretizationImpl
							.updateIntermediateData(attributeKey,
									intermediateDatas);
					intermediateDatas = discretizationImpl
							.modifyWorstAttributeColumn(cutPoint, attributeKey,
									intermediateDatas);
					consistency = discretizationImpl
							.checkConsistency(intermediateDatas);
					Random rand = new Random();
					int randomNum = rand.nextInt((110 - 80) + 1) + 80;
					if (iterationCount > randomNum) {
						break;
					}
				}
				Map<String, List<Float>> cutPoints = discretizationImpl
						.getAttributeCutPoints(intermediateDatas);
				consistency = 0;
				for (String attribute : attributeName) {
					for (int i = 1; i < cutPoints.get(attribute).size() - 1; i++) {
						for (IntermediateData intermediateData : intermediateDatas) {
							mergeDatas1.add(new IntermediateData(
									intermediateData));
						}
						mergeDatas1 = discretizationImpl.mergeTableData(
								mergeDatas1, cutPoints.get(attribute)
										.get(i - 1), cutPoints.get(attribute)
										.get(i),
								cutPoints.get(attribute).get(i + 1), attribute);
						consistency = discretizationImpl
								.checkConsistency(mergeDatas1);
						if (consistency < 1) {
							mergeDatas1 = new ArrayList<IntermediateData>();
						} else {
							cutPoints.get(attribute).remove(i);
							intermediateDatas = new ArrayList<IntermediateData>();
							for (IntermediateData MergeData : mergeDatas1) {
								intermediateDatas.add(new IntermediateData(
										MergeData));
							}
							mergeDatas1 = new ArrayList<IntermediateData>();
						}
					}
				}
				printCutPoints(cutPoints);
				printFinalTable(intermediateDatas);
				flag = true;
				break;
			}
			case 2: {
				PrintStream out = null;
				try {
					out = new PrintStream(new FileOutputStream(
							fileName.replaceAll(".d", ".int")));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				System.setOut(out);
				DiscretizationImpl discretizationImpl = new DiscretizationImpl(
						dataTexts, attributeName);
				discretizationImpl.frequencyCutPoint();
				Map<String, Float> cutPointtoUse = discretizationImpl
						.calculateFrequency();
				intermediateDatas = discretizationImpl
						.modifyTable(cutPointtoUse);
				consistency = discretizationImpl
						.checkConsistency(intermediateDatas);
				int iterationCount = 0;
				while (consistency < 1.0) {
					iterationCount++;
					List<List<String>> listAttributes = discretizationImpl
							.getAttributesList(intermediateDatas);
					Map<String, Float> averageBlockEntropies = discretizationImpl
							.worstAttribute(listAttributes, intermediateDatas);
					Iterator it = averageBlockEntropies.entrySet().iterator();
					int iteratorValue = 0;
					String attributeKey = null;
					float temp = 0;
					System.out
							.println("\nAverage Block Entropies for Iteration: "
									+ iterationCount);
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						if (iteratorValue == 0) {
							temp = (float) pair.getValue();
							attributeKey = pair.getKey().toString().trim();
							iteratorValue++;
						} else if ((float) pair.getValue() > temp) {
							temp = (float) pair.getValue();
							attributeKey = pair.getKey().toString().trim();
						}
						System.out.println(pair.getKey() + " = "
								+ pair.getValue());
						it.remove();
					}
					System.out.println("Worst Attribute is: " + attributeKey);
					intermediateDatas = discretizationImpl
							.updateIntermediateData(attributeKey,
									intermediateDatas);
					Map<String, List<List<Float>>> cutPointsGroup = discretizationImpl
							.createCutPointGroups(attributeKey);
					Map<String, List<Float>> cutPointsToUse = discretizationImpl
							.getCutPointsFromBucket(attributeKey,
									cutPointsGroup);
					intermediateDatas = discretizationImpl
							.modifyWorstAttributeForFrequency(cutPointsToUse,
									attributeKey, intermediateDatas);
					consistency = discretizationImpl
							.checkConsistency(intermediateDatas);
				}
				Map<String, List<Float>> cutPoints = discretizationImpl
						.getAttributeCutPoints(intermediateDatas);
				consistency = 0;
				for (String attribute : attributeName) {
					for (int i = 1; i < cutPoints.get(attribute).size() - 1; i++) {
						for (IntermediateData intermediateData : intermediateDatas) {
							mergeDatas1.add(new IntermediateData(
									intermediateData));
						}
						mergeDatas1 = discretizationImpl.mergeTableData(
								mergeDatas1, cutPoints.get(attribute)
										.get(i - 1), cutPoints.get(attribute)
										.get(i),
								cutPoints.get(attribute).get(i + 1), attribute);
						consistency = discretizationImpl
								.checkConsistency(mergeDatas1);
						if (consistency < 1) {
							mergeDatas1 = new ArrayList<IntermediateData>();
						} else {
							cutPoints.get(attribute).remove(i);
							intermediateDatas = new ArrayList<IntermediateData>();
							for (IntermediateData MergeData : mergeDatas1) {
								intermediateDatas.add(new IntermediateData(
										MergeData));
							}
							mergeDatas1 = new ArrayList<IntermediateData>();
						}
					}
				}
				printCutPoints(cutPoints);
				printFinalTable(intermediateDatas);
				flag = true;
				break;
			}
			case 3: {
				PrintStream out = null;
				try {
					out = new PrintStream(new FileOutputStream(
							fileName.replaceAll(".d", ".int")));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				System.setOut(out);
				Map<String, List<Float>> actualCutPoints = new HashMap<String, List<Float>>();
				DiscretizationImpl discretizationImpl = new DiscretizationImpl(
						dataTexts, attributeName);
				Map<String, List<Float>> cutPoints = discretizationImpl
						.frequencyCutPoint();

				Map<String, Float> cutPointToUse = new HashMap<String, Float>();
				for (String attribute : attributeName) {
					Map<Float, Float> tempBestAttribute = new HashMap<Float, Float>();
					for (int i = 0; i < cutPoints.get(attribute).size(); i++) {
						Map<String, Float> cutPoint = new HashMap<String, Float>();
						cutPoint.put(attribute, cutPoints.get(attribute).get(i));
						for (String tempAttribute : attributeName) {
							if (tempAttribute != attribute) {
								cutPoint.put(tempAttribute,
										cutPoints.get(tempAttribute).get(0));
							}
						}
						List<IntermediateData> intermediates = new ArrayList<IntermediateData>();
						intermediates = discretizationImpl
								.determineCutpointforConditionalEntropy(cutPoint);
						List<List<String>> listAttributes = discretizationImpl
								.getAttributesList(intermediates);
						Map<String, Float> averageBlockEntropies = discretizationImpl
								.bestAttribute(listAttributes, intermediates);
						tempBestAttribute.put(cutPoints.get(attribute).get(i),
								averageBlockEntropies.get(attribute));
					}
					Iterator it = tempBestAttribute.entrySet().iterator();
					int iteratorValue = 0;
					String attributeKey = null;
					float temp = 0;
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						if (iteratorValue == 0) {
							temp = (float) pair.getValue();
							attributeKey = pair.getKey().toString().trim();
							iteratorValue++;
						} else if ((float) pair.getValue() < temp) {
							temp = (float) pair.getValue();
							attributeKey = pair.getKey().toString().trim();
						}
						it.remove();
					}
					cutPointToUse
							.put(attribute, Float.parseFloat(attributeKey));
				}
				intermediateDatas = discretizationImpl
						.modifyTable(cutPointToUse);
				consistency = discretizationImpl
						.checkConsistency(intermediateDatas);
				for (String attribute : attributeName) {
					List<Float> tempCut = new ArrayList<Float>();
					tempCut.add(cutPointToUse.get(attribute));
					actualCutPoints.put(attribute, tempCut);
				}
				int iterationCount = 0;
				while (consistency < 1.0) {
					iterationCount++;
					List<List<String>> listAttributes = discretizationImpl
							.getAttributesList(intermediateDatas);
					Map<String, Float> averageBlockEntropies = discretizationImpl
							.worstAttribute(listAttributes, intermediateDatas);
					Iterator it = averageBlockEntropies.entrySet().iterator();
					int iteratorValue = 0;
					String attributeKey = null;
					float temp = 0;
					System.out
							.println("\nAverage Block Entropies for Iteration: "
									+ iterationCount);
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						if (iteratorValue == 0) {
							temp = (float) pair.getValue();
							attributeKey = pair.getKey().toString().trim();
							iteratorValue++;
						} else if ((float) pair.getValue() > temp) {
							temp = (float) pair.getValue();
							attributeKey = pair.getKey().toString().trim();
						}
						System.out.println(pair.getKey() + " = "
								+ pair.getValue());
						it.remove();
					}
					System.out.println("Worst Attribute is: " + attributeKey);
					Set<Set<Integer>> disctinctValue = discretizationImpl
							.determinedistinctValues(intermediateDatas,
									attributeKey);
					String cutPoint = discretizationImpl
							.partitionWorstAttributeColumn(disctinctValue,
									attributeKey);
					if (cutPoint != "-1") {
						Float cutPointtoAdd = Float.parseFloat(cutPoint);
						actualCutPoints.get(attributeKey).add(cutPointtoAdd);
					}
					Collections.sort(actualCutPoints.get(attributeKey));
					intermediateDatas = discretizationImpl
							.updateIntermediateData(attributeKey,
									intermediateDatas);
					intermediateDatas = discretizationImpl
							.modifyWorstAttributeForFrequency(actualCutPoints,
									attributeKey, intermediateDatas);
					consistency = discretizationImpl
							.checkConsistency(intermediateDatas);
					Random rand = new Random();
					int randomNum = rand.nextInt((100 - 75) + 1) + 75;
					if (iterationCount > randomNum) {
						break;
					}
				}
				consistency = 0;
				for (String attribute : attributeName) {
					for (int i = 1; i < actualCutPoints.get(attribute).size() - 1; i++) {
						for (IntermediateData intermediateData : intermediateDatas) {
							mergeDatas1.add(new IntermediateData(
									intermediateData));
						}
						mergeDatas1 = discretizationImpl.mergeTableData(
								mergeDatas1, actualCutPoints.get(attribute)
										.get(i - 1),
								actualCutPoints.get(attribute).get(i),
								actualCutPoints.get(attribute).get(i + 1),
								attribute);
						consistency = discretizationImpl
								.checkConsistency(mergeDatas1);
						if (consistency < 1) {
							mergeDatas1 = new ArrayList<IntermediateData>();
						} else {
							actualCutPoints.get(attribute).remove(i);
							intermediateDatas = new ArrayList<IntermediateData>();
							for (IntermediateData MergeData : mergeDatas1) {
								intermediateDatas.add(new IntermediateData(
										MergeData));
							}
							mergeDatas1 = new ArrayList<IntermediateData>();
						}
					}
				}
				printCutPoints(actualCutPoints);
				printFinalTable(intermediateDatas);

				flag = true;
				break;
			}
			default: {
				System.out.println("Enter a Valid Input: ");
				flag = false;
				break;
			}
			}
		}
	}

	/**
	 * Method to check the file exists or not.
	 */
	private static void fileValidator() {
		try {
			File inputFile = new File(fileName);
			Scanner fileNameValidator = new Scanner(inputFile);
		} catch (Exception exception) {
			System.out.println("File not found");
			System.exit(1);
		}
	}

	/**
	 * Method to read the data from the file data
	 */
	private static void readFile() {
		try {
			File inputFile = new File(fileName);
			Scanner fileNameValidatorScanner = new Scanner(inputFile);
			int tempCount = 0;
			String attributStart = null;

			while (fileNameValidatorScanner.hasNext()) {
				String token = fileNameValidatorScanner.next();

				if (!token.equals(">")) {
					if (token.equalsIgnoreCase("a")) {
						noOfAttributes++;
					} else if (token.equalsIgnoreCase("d")) {
						noOfDecision++;
					}
				} else {
					break;
				}
			}

			while (fileNameValidatorScanner.hasNext()) {
				String token = fileNameValidatorScanner.next();

				if (token.equals("[")) {
					attributStart = token;
				}

				if (attributStart.equals("[") && !token.equals("[")
						&& !token.equals("]")) {
					if (tempCount < noOfAttributes) {
						attributeName.add(token);
						tempCount++;
					} else if (tempCount == noOfAttributes) {
						decision = token;
					}
				} else if (token.equals("]")) {
					break;
				}
			}

			while (fileNameValidatorScanner.hasNext()) {
				tempCount = 0;
				DataText dataText = new DataText();
				List<Map<String, Float>> tempAttributeMapList = new ArrayList<Map<String, Float>>();
				for (int i = 0; i < (noOfAttributes + noOfDecision); i++) {
					if (i < noOfAttributes) {
						Map<String, Float> tempAttributeValue = new HashMap<String, Float>();
						tempAttributeValue.put(attributeName.get(tempCount),
								fileNameValidatorScanner.nextFloat());
						tempCount++;
						tempAttributeMapList.add(tempAttributeValue);
					} else {
						dataText.setDecision(fileNameValidatorScanner.next());
					}
				}
				dataText.setAttributeMapList(tempAttributeMapList);
				dataTexts.add(dataText);
			}
			System.out.println("No of Attributes :  " + noOfAttributes);
			System.out.println("No of Decision :  " + noOfDecision);
			System.out.println("Total No.of Rows: " + dataTexts.size());

		} catch (Exception exception) {
			System.out.println("Invalid File Data. Check input file Data");
			System.exit(2);
		}
	}

	/**
	 * Method to get the input file name from the user.
	 * 
	 * @return userDefinedFileName
	 */
	private static String getFileName() {
		System.out.println("Enter the input file name: ");
		String userDefinedFileName = null;

		try {
			Scanner fileInputNameScanner = new Scanner(System.in);
			userDefinedFileName = fileInputNameScanner.next();
		} finally {
			// fileInputNameScanner.close();
		}
		return userDefinedFileName;
	}

	/**
	 * Print Final table
	 * 
	 * @param intermediateDatas
	 */
	public static void printFinalTable(List<IntermediateData> intermediateDatas) {

		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(fileName.replaceAll(
					".d", ".data")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.setOut(out);

		System.out.print("< ");
		for (int i = 0; i < attributeName.size(); i++) {
			System.out.print("a");
			System.out.print("\t");
		}
		System.out.print("d >");

		System.out.print("\n[ ");
		for (String printAttributeName : attributeName) {
			System.out.print(printAttributeName);
			System.out.print("\t");
		}
		System.out.print(decision + " ]");
		System.out.println();

		for (IntermediateData intermediateData : intermediateDatas) {
			List<Map<String, String>> attributeMapList = intermediateData
					.getAttributeMapList();
			for (Map<String, String> attributeMap : attributeMapList) {
				Iterator iterator = attributeMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry pair = (Map.Entry) iterator.next();
					System.out.print(pair.getValue());
					System.out.print("\t");
					iterator.remove();
				}
			}
			System.out.print(intermediateData.getDecision());
			System.out.println();
		}
	}

	/**
	 * Print cutPoint values
	 * 
	 * @param cutPoints
	 */
	public static void printCutPoints(Map<String, List<Float>> cutPoints) {

		System.out.println();
		for (String attribute : attributeName) {
			System.out.println("'" + attribute + "'" + " CutPoints are:");
			for (float value : cutPoints.get(attribute)) {
				System.out.print(value + "\t");
			}
			System.out.println();
		}

	}
}
