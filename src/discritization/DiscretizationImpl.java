package discritization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import model.DataText;
import model.IntermediateData;

import com.google.common.collect.Sets;

public class DiscretizationImpl {

	private List<DataText> equalIntervalDataTexts;
	private List<String> equalIntervalAttributes;
	private List<IntermediateData> intermediateDatas = new ArrayList<IntermediateData>();
	private Map<String, Float> lowest = new HashMap<String, Float>();
	private Map<String, Float> highest = new HashMap<String, Float>();
	private Map<String, Integer> kValue = new HashMap<String, Integer>();
	private Map<String, List<Float>> cutPoints = new HashMap<String, List<Float>>();
	private Map<Float, Integer> frequency = new LinkedHashMap<Float, Integer>();

	public DiscretizationImpl(List<DataText> equalIntervalDataTexts, List<String> equalIntervalAttributes) {
		this.equalIntervalDataTexts = equalIntervalDataTexts;
		this.equalIntervalAttributes = equalIntervalAttributes;
	}

	/**
	 * Calculate the median for case k=2 Column wise data.
	 * 
	 * @return
	 */
	public Map<String, Float> calculateMedian() {
		float lowestValue = 0, median, highestValue = 0;
		Map<String, Float> result = new HashMap<String, Float>();

		for (String attribute : equalIntervalAttributes) {
			int i = 0;
			for (DataText dataText : equalIntervalDataTexts) {
				List<Map<String, Float>> attributeMapList = dataText.getAttributeMapList();
				for (Map<String, Float> attributeMap : attributeMapList) {

					if (attributeMap.containsKey(attribute) && i == 0) {
						lowestValue = attributeMap.get(attribute);
						highestValue = attributeMap.get(attribute);
						i++;
					} else if (attributeMap.containsKey(attribute)) {
						if (lowestValue > attributeMap.get(attribute))
							lowestValue = attributeMap.get(attribute);

						if (highestValue < attributeMap.get(attribute))
							highestValue = attributeMap.get(attribute);
					}
				}

				lowest.put(attribute, lowestValue);
				highest.put(attribute, highestValue);
				median = (lowestValue + highestValue) / 2;
				result.put(attribute, median);
			}
		}

		return result;
	}

	/**
	 * Calculate the cut point for worst attributes
	 * 
	 * @param worstAttribute
	 * @return
	 */
	public Map<String, Float> calculateCutPoint(String worstAttribute) {

		float lowestValue = 0, cutPoint, highestValue = 0;
		Map<String, Float> CutPointResult = new HashMap<String, Float>();
		String tempAttribute = null;

		for (String attribute : equalIntervalAttributes) {
			if (attribute.equalsIgnoreCase(worstAttribute)) {
				tempAttribute = attribute;
				int i = 0;
				for (DataText dataText : equalIntervalDataTexts) {
					List<Map<String, Float>> attributeMapList = dataText.getAttributeMapList();
					for (Map<String, Float> attributeMap : attributeMapList) {

						if (attributeMap.containsKey(attribute) && i == 0) {
							lowestValue = attributeMap.get(attribute);
							highestValue = attributeMap.get(attribute);
							i++;
						} else if (attributeMap.containsKey(attribute)) {
							if (lowestValue > attributeMap.get(attribute))
								lowestValue = attributeMap.get(attribute);

							if (highestValue < attributeMap.get(attribute))
								highestValue = attributeMap.get(attribute);
						}
					}

				}
			}
		}
		int k = kValue.get(worstAttribute.trim()) + 1;
		kValue.put(worstAttribute.trim(), k);
		cutPoint = (highestValue - lowestValue) / k;
		CutPointResult.put(tempAttribute, cutPoint);

		return CutPointResult;

	}

	/**
	 * Modify table for k=2
	 * 
	 * @param median
	 * @return
	 */
	public List<IntermediateData> modifyTable(Map<String, Float> median) {
		for (DataText dataText : equalIntervalDataTexts) {
			IntermediateData intermediateData = new IntermediateData();
			List<Map<String, String>> tempAttributeMapList = new ArrayList<Map<String, String>>();
			List<Map<String, Float>> attributeMapList = dataText.getAttributeMapList();
			for (Map<String, Float> attributeMap : attributeMapList) {
				Map<String, String> tempAttributeValue = new HashMap<String, String>();
				for (String attribute : equalIntervalAttributes) {
					if (attributeMap.containsKey(attribute)) {
						String temp = null;
						if (attributeMap.get(attribute) < median.get(attribute)) {
							temp = lowest.get(attribute).toString().trim() + ".."
									+ median.get(attribute).toString().trim();
						} else {
							temp = median.get(attribute).toString().trim() + ".."
									+ highest.get(attribute).toString().trim();
						}
						tempAttributeValue.put(attribute, temp);
					}

				}
				tempAttributeMapList.add(tempAttributeValue);

			}
			intermediateData.setAttributeMapList(tempAttributeMapList);
			intermediateData.setDecision(dataText.getDecision());
			intermediateDatas.add(intermediateData);
		}

		return intermediateDatas;
	}

	/**
	 * Modify the worst attribute column in the IntermediateData Object
	 * 
	 * @param cutPoint
	 * @return
	 */
	public List<IntermediateData> modifyWorstAttributeColumn(Map<String, Float> cutPoint, String worstAttribute,
			List<IntermediateData> intermediateDatas) {

		float cutPointIterator[] = new float[kValue.get(worstAttribute) + 1];
		for (int i = 0; i <= kValue.get(worstAttribute); i++) {
			if (i == 0) {
				cutPointIterator[i] = lowest.get(worstAttribute);
			} else if (i == kValue.get(worstAttribute)) {
				cutPointIterator[i] = highest.get(worstAttribute);
			} else {
				cutPointIterator[i] = cutPointIterator[i - 1] + cutPoint.get(worstAttribute);
			}
		}

		for (IntermediateData intermediateData : intermediateDatas) {
			for (Map<String, String> listIntermediate : intermediateData.getAttributeMapList()) {
				if (listIntermediate.containsKey(worstAttribute)) {
					String temp = null;
					for (int i = 0; i < cutPointIterator.length; i++) {
						if (Float.parseFloat(listIntermediate.get(worstAttribute)) < cutPointIterator[i]) {
							temp = cutPointIterator[i - 1] + ".." + cutPointIterator[i];
							break;
						} else if (Float.parseFloat(listIntermediate.get(worstAttribute)) == highest
								.get(worstAttribute)) {
							temp = cutPointIterator[cutPointIterator.length - 2] + ".."
									+ cutPointIterator[cutPointIterator.length - 1];
							break;
						}
					}
					listIntermediate.put(worstAttribute, temp);
				}
			}
		}
		return intermediateDatas;
	}

	/**
	 * Method to update the column which had worst attribute
	 * 
	 * @param worsteAttribute
	 * @return
	 */
	public List<IntermediateData> updateIntermediateData(String worsteAttribute,
			List<IntermediateData> intermediateDatas) {

		List<String> tempWorstAttributesDataList = new ArrayList<String>();
		for (DataText dataText : equalIntervalDataTexts) {
			for (Map<String, Float> dataTextAttributList : dataText.getAttributeMapList()) {
				if (dataTextAttributList.containsKey(worsteAttribute)) {
					tempWorstAttributesDataList.add(dataTextAttributList.get(worsteAttribute).toString().trim());
				}
			}
		}

		int tempIterartor = 0;
		for (IntermediateData intermediateData : intermediateDatas) {
			for (Map<String, String> listIntermediate : intermediateData.getAttributeMapList()) {
				if (listIntermediate.containsKey(worsteAttribute)) {
					listIntermediate.put(worsteAttribute, tempWorstAttributesDataList.get(tempIterartor).trim());
					tempIterartor++;
				}
			}
		}
		return intermediateDatas;
	}

	/**
	 * Calculate Consistency
	 * 
	 * @return
	 */
	public float checkConsistency(List<IntermediateData> intermediateDatas) {
		Set<Set<Integer>> attributePartitions = new HashSet<Set<Integer>>();
		Set<Set<Integer>> concepts = new HashSet<Set<Integer>>();
		Set<Set<Integer>> lowerApproximation = new HashSet<Set<Integer>>();
		int lowerApproximationSize = 0;
		float consistency;

		for (IntermediateData intermediateData : intermediateDatas) {
			Set<Integer> tempAttribute = new HashSet<Integer>();
			Set<Integer> tempDecision = new HashSet<Integer>();

			for (int i = 0; i < intermediateDatas.size(); i++) {
				if (intermediateData.getAttributeMapList().equals(intermediateDatas.get(i).getAttributeMapList())) {
					tempAttribute.add(i);
				}

				if (intermediateData.getDecision().equals(intermediateDatas.get(i).getDecision())) {
					tempDecision.add(i);
				}
			}
			attributePartitions.add(tempAttribute);
			concepts.add(tempDecision);
		}

		for (Set<Integer> concept : concepts) {
			Set<Integer> tempLowerApproximation = new HashSet<Integer>();

			for (Set<Integer> attributePartition : attributePartitions) {
				if (concept.containsAll(attributePartition)) {
					tempLowerApproximation.addAll(attributePartition);
				}
			}
			if (!tempLowerApproximation.isEmpty()) {
				lowerApproximation.add(tempLowerApproximation);
				lowerApproximationSize = lowerApproximationSize + tempLowerApproximation.size();
			}
		}

		consistency = (float) lowerApproximationSize / (float) intermediateDatas.size();
		return consistency;
	}

	/**
	 * Store Attribute Data in the AttributeList
	 */
	public List<List<String>> getAttributesList(List<IntermediateData> intermediateDatas) {

		List<List<String>> listAttributes = new ArrayList<List<String>>();

		for (String equalIntervalAttribute : equalIntervalAttributes) {
			List<String> tempList = new ArrayList<String>();
			for (IntermediateData intermediateData : intermediateDatas) {
				List<Map<String, String>> attributeMapList = intermediateData.getAttributeMapList();
				for (Map<String, String> attributeMap : attributeMapList) {
					if (attributeMap.containsKey(equalIntervalAttribute)) {
						tempList.add(attributeMap.get(equalIntervalAttribute));
					}
				}
			}
			listAttributes.add(tempList);
		}
		return listAttributes;
	}

	public Map<String, Float> worstAttribute(List<List<String>> listAttributes, List<IntermediateData> intermediateDatas) {
		Map<String, Float> averageBlockEntropies = new HashMap<String, Float>();
		int attributePosition = 0;
		for (List<String> listAttribute : listAttributes) {
			Set<Set<Integer>> worstSets = new HashSet<Set<Integer>>();

			for (String attributValue : listAttribute) {
				Set<Integer> tempWorstSet = new HashSet<Integer>();
				for (int i = 0; i < listAttribute.size(); i++) {
					if (listAttribute.get(i).equalsIgnoreCase(attributValue)) {
						tempWorstSet.add(i);
					}
				}
				worstSets.add(tempWorstSet);
			}

			float totalSum = 0;
			for (Set<Integer> worstSet : worstSets) {
				float sum = 0;
				Map<String, Integer> decisionCount = new HashMap<String, Integer>();
				for (Integer position : worstSet) {
					if (!decisionCount.containsKey(intermediateDatas.get(position).getDecision().trim())) {
						decisionCount.put(intermediateDatas.get(position).getDecision(), 1);
					} else {
						int value = decisionCount.get(intermediateDatas.get(position).getDecision().trim());

						decisionCount.put(intermediateDatas.get(position).getDecision(), ++value);
					}
				}

				Iterator it = decisionCount.entrySet().iterator();

				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					int value = (int) pair.getValue();
					float commonValue = (float) value / (float) worstSet.size();
					float logValue = (float) ((Math.log(value) / Math.log(2)) - (Math.log(worstSet.size()) / Math
							.log(2)));
					sum = (float) (sum + (-1 * (commonValue) * (logValue)));
					it.remove(); // avoids a ConcurrentModificationException
				}

				float blocks = (float) worstSet.size() / (float) intermediateDatas.size();
				sum = (float) (sum * blocks);
				totalSum = totalSum + sum;

			}

			totalSum = (float) totalSum / (float) worstSets.size();
			averageBlockEntropies.put(equalIntervalAttributes.get(attributePosition), totalSum);
			kValue.put(equalIntervalAttributes.get(attributePosition), worstSets.size());
			attributePosition++;
		}
		return averageBlockEntropies;
	}

	/**
	 * Get the cut point values for each attribute
	 * 
	 * @param mergeDatas
	 * @return
	 */
	public Map<String, List<Float>> getAttributeCutPoints(List<IntermediateData> mergeDatas) {

		for (String equalIntervalAttribute : equalIntervalAttributes) {
			List<Float> cutPointsList = new ArrayList<Float>();
			for (IntermediateData mergeData : mergeDatas) {
				String[] tempCutPoints = null;
				List<Map<String, String>> attributeMapList = mergeData.getAttributeMapList();
				for (Map<String, String> attributeMap : attributeMapList) {
					if (attributeMap.containsKey(equalIntervalAttribute)) {
						tempCutPoints = attributeMap.get(equalIntervalAttribute).split(Pattern.quote(".."));
					}
				}
				for (String cupPoint : tempCutPoints) {
					if (!cutPointsList.contains(Float.parseFloat(cupPoint))) {
						cutPointsList.add(Float.parseFloat(cupPoint));
					}
				}
			}
			cutPoints.put(equalIntervalAttribute, cutPointsList);
			Collections.sort(cutPoints.get(equalIntervalAttribute));
		}
		return cutPoints;
	}

	/**
	 * 
	 * @param mergeDatas
	 * @param cutpoint1
	 * @param cutpoint2
	 * @param cutpoint3
	 * @param equalIntervalAttribute
	 * @return
	 */
	public List<IntermediateData> mergeTableData(List<IntermediateData> mergeDatas, float cutpoint1, float cutpoint2,
			float cutpoint3, String equalIntervalAttribute) {

		float consistency = 0;
		if (kValue.get(equalIntervalAttribute) == 2) {
			String temp = lowest.get(equalIntervalAttribute).toString().trim() + ".."
					+ highest.get(equalIntervalAttribute).toString().trim();
			for (IntermediateData mergeData : mergeDatas) {
				List<Map<String, String>> attributeMapList = mergeData.getAttributeMapList();
				for (Map<String, String> attributeMap : attributeMapList) {
					if (attributeMap.containsKey(equalIntervalAttribute)) {
						attributeMap.put(equalIntervalAttribute, temp);
					}
				}
			}
		} else {
			for (IntermediateData mergeData : mergeDatas) {
				List<Map<String, String>> attributeMapList = mergeData.getAttributeMapList();
				for (Map<String, String> attributeMap : attributeMapList) {
					if (attributeMap.containsKey(equalIntervalAttribute)) {
						String[] tempCutPoints = null;
						String temp = null;
						tempCutPoints = attributeMap.get(equalIntervalAttribute).split(Pattern.quote(".."));
						if (tempCutPoints[0].contains(String.valueOf(cutpoint2))) {
							temp = String.valueOf(cutpoint1) + ".." + String.valueOf(tempCutPoints[1]);
							attributeMap.put(equalIntervalAttribute, temp);
						} else if (tempCutPoints[1].contains(String.valueOf(cutpoint2))) {
							temp = String.valueOf(tempCutPoints[0]) + ".." + String.valueOf(cutpoint3);
							attributeMap.put(equalIntervalAttribute, temp);
						}
					}
				}
			}
		}
		consistency = checkConsistency(mergeDatas);
		if (consistency >= 1) {
			kValue.put(equalIntervalAttribute, kValue.get(equalIntervalAttribute) - 1);
		}
		return mergeDatas;
	}

	/**
	 * Get Frequency cut points
	 * 
	 * @return
	 */
	public Map<String, List<Float>> frequencyCutPoint() {

		float lowestValue = 0, highestValue = 0;
		for (String attribute : equalIntervalAttributes) {
			List<Float> averageList = new ArrayList<Float>();
			List<Float> tempCutPoints = new ArrayList<Float>();
			int i = 0;
			for (DataText dataText : equalIntervalDataTexts) {
				List<Map<String, Float>> attributeMapList = dataText.getAttributeMapList();
				for (Map<String, Float> attributeMap : attributeMapList) {
					if (attributeMap.containsKey(attribute) && i == 0) {
						lowestValue = attributeMap.get(attribute);
						highestValue = attributeMap.get(attribute);
						averageList.add(attributeMap.get(attribute));
						i++;
					} else if (attributeMap.containsKey(attribute)
							&& !averageList.contains(attributeMap.get(attribute))) {
						averageList.add(attributeMap.get(attribute));
						if (lowestValue > attributeMap.get(attribute))
							lowestValue = attributeMap.get(attribute);

						if (highestValue < attributeMap.get(attribute))
							highestValue = attributeMap.get(attribute);
					} else if (attributeMap.containsKey(attribute)) {
						if (lowestValue > attributeMap.get(attribute))
							lowestValue = attributeMap.get(attribute);

						if (highestValue < attributeMap.get(attribute))
							highestValue = attributeMap.get(attribute);
					}
				}
				lowest.put(attribute, lowestValue);
				highest.put(attribute, highestValue);
			}
			Collections.sort(averageList);
			for (i = 0; i < averageList.size() - 1; i++) {
				float avg = (averageList.get(i) + averageList.get(i + 1)) / 2;
				tempCutPoints.add(avg);
			}
			cutPoints.put(attribute, tempCutPoints);
			kValue.put(attribute, 2);
		}
		return cutPoints;
	}

	/**
	 * Create CutPoint Group for 2nd algorithm when k>2
	 * 
	 * @param attribute
	 * @return
	 */
	public Map<String, List<List<Float>>> createCutPointGroups(String attribute) {
		// List<Float> temp = new ArrayList<Float>();
		// temp = cutPoints.get(attribute);
		// temp.add((float) 60.0);
		// temp.add((float) 65.0);
		// cutPoints.put(attribute, temp);
		kValue.put(attribute, kValue.get(attribute) + 1);
		Map<String, List<List<Float>>> cutPointGroups = new HashMap<String, List<List<Float>>>();
		List<List<Float>> tempListGroup = new ArrayList<List<Float>>();
		Set<Float> tempSet = new HashSet<Float>(cutPoints.get(attribute));
		Set<Set<Float>> subSets = Sets.powerSet(tempSet);
		for (final Set<Float> subSet : subSets) {
			List<Float> tempCutPointGroup = new ArrayList<Float>();
			if (subSet.size() == kValue.get(attribute) - 1) {
				tempCutPointGroup.addAll(subSet);
				Collections.sort(tempCutPointGroup);
				tempListGroup.add(tempCutPointGroup);
			}
		}
		cutPointGroups.put(attribute, tempListGroup);
		return cutPointGroups;
	}

	/**
	 * Create buckets and determine the cutpoints to use
	 * 
	 * @param attribute
	 * @param cutPointGroups
	 * @return
	 */
	public Map<String, List<Float>> getCutPointsFromBucket(String attribute,
			Map<String, List<List<Float>>> cutPointGroups) {
		Map<String, List<Float>> cutPointResult = new HashMap<String, List<Float>>();
		List<List<Float>> cutPointGroup = cutPointGroups.get(attribute);
		Map<List<Float>, List<Integer>> intermediateCutPointCount = new HashMap<List<Float>, List<Integer>>();

		for (List<Float> cutPoint : cutPointGroup) {
			Map<String, Integer> countNoOfCutPoints = new HashMap<String, Integer>();
			for (DataText dataText : equalIntervalDataTexts) {
				List<Map<String, Float>> attributeMapList = dataText.getAttributeMapList();
				for (Map<String, Float> attributeMap : attributeMapList) {
					if (attributeMap.containsKey(attribute)) {

						// Each value comes in here
						Float value = attributeMap.get(attribute);
						int lastPosition = cutPoint.size() - 1;
						// For First Position
						if (cutPoint.get(0) > value) {
							String condition = "lessthan " + cutPoint.get(0);
							if (countNoOfCutPoints.containsKey(condition)) {
								int count = countNoOfCutPoints.get(condition);
								count++;
								countNoOfCutPoints.put(condition, count);
							} else {
								countNoOfCutPoints.put(condition, 1);
							}
						} else
						// For Last Position
						if (cutPoint.get(lastPosition) < value) {
							String condition = "Greaterthan " + cutPoint.get(lastPosition);
							if (countNoOfCutPoints.containsKey(condition)) {
								int count = countNoOfCutPoints.get(condition);
								count++;
								countNoOfCutPoints.put(condition, count);
							} else {
								countNoOfCutPoints.put(condition, 1);
							}
						} else {
							// For something in the middle
							for (int i = 0; i < lastPosition; i++) {
								String condition = "Inthemiddleof " + cutPoint.get(i) + ":" + cutPoint.get(i + 1);
								if (value >= cutPoint.get(i) && value < cutPoint.get(i + 1)) {
									if (countNoOfCutPoints.containsKey(condition)) {
										int count = countNoOfCutPoints.get(condition);
										count++;
										countNoOfCutPoints.put(condition, count);
									} else {
										countNoOfCutPoints.put(condition, 1);
									}
								}
							}
						}
					}
				}
			}
			List<Integer> values = new ArrayList<Integer>();
			for (Map.Entry<String, Integer> entry : countNoOfCutPoints.entrySet()) {
				values.add(entry.getValue());
			}

			intermediateCutPointCount.put(cutPoint, values);
		}
		int totalRows = equalIntervalDataTexts.size();
		int noOfBuckets = intermediateCutPointCount.size();
		int meanValue = totalRows / noOfBuckets;

		Map<List<Float>, Integer> meanMapChecker = new HashMap<List<Float>, Integer>();
		for (Map.Entry<List<Float>, List<Integer>> cutPointCount : intermediateCutPointCount.entrySet()) {
			List<Integer> uniformityCalculator = cutPointCount.getValue();
			int meanValueChecker = 0;
			int totalMeanChecker = 0;
			for (Integer digit : uniformityCalculator) {
				meanValueChecker = digit - meanValue;
				if (meanValueChecker < 0) {
					meanValueChecker = meanValueChecker * -1;
				}
				totalMeanChecker = totalMeanChecker + meanValueChecker;
			}
			meanMapChecker.put(cutPointCount.getKey(), totalMeanChecker);
		}

		final List<Entry<List<Float>, Integer>> sortedMeanMapChecker = new LinkedList<Entry<List<Float>, Integer>>(
				meanMapChecker.entrySet());

		Collections.sort(sortedMeanMapChecker, new Comparator<Entry<List<Float>, Integer>>() {
			public int compare(final Entry<List<Float>, Integer> o1, final Entry<List<Float>, Integer> o2) {

				return o1.getValue().compareTo(o2.getValue());

			}
		});

		final Map<List<Float>, Integer> sortedMap = new LinkedHashMap<List<Float>, Integer>();
		for (final Entry<List<Float>, Integer> entry : sortedMeanMapChecker) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		cutPointResult.put(attribute, sortedMeanMapChecker.iterator().next().getKey());
		return cutPointResult;
	}

	/**
	 * Calculate Frequency for k = 2
	 * 
	 * @return
	 */
	public Map<String, Float> calculateFrequency() {
		Map<String, Float> cutPointtoUse = new HashMap<String, Float>();
		String tempKey = null;
		for (String attribute : equalIntervalAttributes) {
			for (DataText dataText : equalIntervalDataTexts) {
				List<Map<String, Float>> attributeMapList = dataText.getAttributeMapList();
				for (Map<String, Float> attributeMap : attributeMapList) {
					if (attributeMap.containsKey(attribute)) {
						if (frequency.containsKey(attributeMap.get(attribute))) {
							int count = frequency.get(attributeMap.get(attribute));
							count++;
							frequency.put(attributeMap.get(attribute), count);
						} else {
							frequency.put(attributeMap.get(attribute), 1);
						}
					}
				}
			}

			List<Float> tempCutPoints = cutPoints.get(attribute);
			Map<String, Integer> cutPointDifference = new HashMap<String, Integer>();
			for (Float cutPoint : tempCutPoints) {
				int lowest = 0, highest = 0;
				for (Map.Entry<Float, Integer> entry : frequency.entrySet()) {
					if (entry.getKey() <= cutPoint) {
						lowest = lowest + entry.getValue();
					} else {
						highest = highest + entry.getValue();
					}
				}
				int difference = highest - lowest;

				if (difference < 0) {
					difference = difference * (-1);
				}

				cutPointDifference.put(cutPoint.toString(), difference);
			}

			frequency = new LinkedHashMap<Float, Integer>();
			int difference = 0, i = 0;

			for (Entry<String, Integer> entry : cutPointDifference.entrySet()) {
				if (i == 0) {
					difference = entry.getValue();
					tempKey = entry.getKey();
					i++;
				} else {
					if (entry.getValue() < difference) {
						difference = entry.getValue();
						tempKey = entry.getKey();
					}
				}
			}
			cutPointtoUse.put(attribute, Float.parseFloat(tempKey));
		}

		// for (Entry<String, Float> entry : cutPointtoUse.entrySet()) {
		// System.out.println(entry.getKey() + " : " + entry.getValue());
		// }

		return cutPointtoUse;
	}

	public List<IntermediateData> modifyWorstAttributeForFrequency(Map<String, List<Float>> cutPoint,
			String worstAttribute, List<IntermediateData> intermediateDatas) {

		List<Float> cutPointList = cutPoint.get(worstAttribute);
		float cutPointIterator[] = new float[cutPointList.size() + 2];
		for (int i = 0; i <= cutPointList.size() + 1; i++) {
			if (i == 0) {
				cutPointIterator[i] = lowest.get(worstAttribute);
			} else if (i == kValue.get(worstAttribute)) {
				cutPointIterator[i] = highest.get(worstAttribute);
			} else {
				cutPointIterator[i] = cutPointList.get(i - 1);
			}
		}

		for (IntermediateData intermediateData : intermediateDatas) {
			for (Map<String, String> listIntermediate : intermediateData.getAttributeMapList()) {
				if (listIntermediate.containsKey(worstAttribute)) {
					String temp = null;
					for (int i = 0; i < cutPointIterator.length; i++) {
						if (Float.parseFloat(listIntermediate.get(worstAttribute)) < cutPointIterator[i]) {
							temp = cutPointIterator[i - 1] + ".." + cutPointIterator[i];
							break;
						} else if (Float.parseFloat(listIntermediate.get(worstAttribute)) == highest
								.get(worstAttribute)) {
							temp = cutPointIterator[cutPointIterator.length - 2] + ".."
									+ cutPointIterator[cutPointIterator.length - 1];
							break;
						}
					}
					listIntermediate.put(worstAttribute, temp);
				}
			}
		}
		return intermediateDatas;
	}

	/**
	 * Method to determine best Attribute for Conditional Entropy
	 * 
	 * @param listAttributes
	 * @param intermediateDatas
	 * @return
	 */
	public Map<String, Float> bestAttribute(List<List<String>> listAttributes, List<IntermediateData> intermediateDatas) {
		Map<String, Float> averageBlockEntropies = new HashMap<String, Float>();
		int attributePosition = 0;
		for (List<String> listAttribute : listAttributes) {
			Set<Set<Integer>> worstSets = new HashSet<Set<Integer>>();

			for (String attributValue : listAttribute) {
				Set<Integer> tempWorstSet = new HashSet<Integer>();
				for (int i = 0; i < listAttribute.size(); i++) {
					if (listAttribute.get(i).equalsIgnoreCase(attributValue)) {
						tempWorstSet.add(i);
					}
				}
				worstSets.add(tempWorstSet);
			}

			float totalSum = 0;
			for (Set<Integer> worstSet : worstSets) {
				float sum = 0;
				Map<String, Integer> decisionCount = new HashMap<String, Integer>();
				for (Integer position : worstSet) {
					if (!decisionCount.containsKey(intermediateDatas.get(position).getDecision().trim())) {
						decisionCount.put(intermediateDatas.get(position).getDecision(), 1);
					} else {
						int value = decisionCount.get(intermediateDatas.get(position).getDecision().trim());

						decisionCount.put(intermediateDatas.get(position).getDecision(), ++value);
					}
				}

				Iterator it = decisionCount.entrySet().iterator();

				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					int value = (int) pair.getValue();
					float commonValue = (float) value / (float) worstSet.size();
					float logValue = (float) ((Math.log(value) / Math.log(2)) - (Math.log(worstSet.size()) / Math
							.log(2)));
					sum = (float) (sum + (-1 * (commonValue) * (logValue)));
					it.remove(); // avoids a ConcurrentModificationException
				}

				float blocks = (float) worstSet.size() / (float) intermediateDatas.size();
				sum = (float) (sum * blocks);
				totalSum = totalSum + sum;

			}

			// totalSum = (float) totalSum / (float) worstSets.size();
			averageBlockEntropies.put(equalIntervalAttributes.get(attributePosition), totalSum);
			// kValue.put(equalIntervalAttributes.get(attributePosition),
			// worstSets.size());
			attributePosition++;
		}
		return averageBlockEntropies;
	}

	/**
	 * Determine CutPoint for Conditional Entropy Algorithm
	 * 
	 * @param median
	 * @return
	 */
	public List<IntermediateData> determineCutpointforConditionalEntropy(Map<String, Float> median) {
		List<IntermediateData> intermediates = new ArrayList<IntermediateData>();
		for (DataText dataText : equalIntervalDataTexts) {
			IntermediateData intermediateData = new IntermediateData();
			List<Map<String, String>> tempAttributeMapList = new ArrayList<Map<String, String>>();
			List<Map<String, Float>> attributeMapList = dataText.getAttributeMapList();
			for (Map<String, Float> attributeMap : attributeMapList) {
				Map<String, String> tempAttributeValue = new HashMap<String, String>();
				for (String attribute : equalIntervalAttributes) {
					if (attributeMap.containsKey(attribute)) {
						String temp = null;
						if (attributeMap.get(attribute) < median.get(attribute)) {
							temp = lowest.get(attribute).toString().trim() + ".."
									+ median.get(attribute).toString().trim();
						} else {
							temp = median.get(attribute).toString().trim() + ".."
									+ highest.get(attribute).toString().trim();
						}
						tempAttributeValue.put(attribute, temp);
					}

				}
				tempAttributeMapList.add(tempAttributeValue);

			}
			intermediateData.setAttributeMapList(tempAttributeMapList);
			intermediateData.setDecision(dataText.getDecision());
			intermediates.add(intermediateData);
		}

		return intermediates;
	}
	
	/**
	 * Determine unique values for worst attribute in conditional entropy.
	 * @param intermDatas
	 * @param attribute
	 * @return
	 */
	public Set<Set<Integer>> determinedistinctValues(List<IntermediateData> intermDatas, String attribute){
		Set<Set<Integer>> disctinctValue = new HashSet<Set<Integer>>();
		List<String> tempList = new ArrayList<String>();
		List<String> uniqueData = new ArrayList<String>();
		for (IntermediateData intermData : intermDatas) {
			List<Map<String, String>> attributeMapList = intermData.getAttributeMapList();
			for (Map<String, String> attributeMap : attributeMapList) {
				if (attributeMap.containsKey(attribute)) {
					tempList.add(attributeMap.get(attribute));
				}
			}
		}
		int i=0;
		for(String tempValue: tempList){
			if(i==0){
				uniqueData.add(tempValue);
				i++;
			}else if(!uniqueData.contains(tempValue)){
				uniqueData.add(tempValue);
			}
		}
		
		for(String tempUniqueValue: uniqueData){
			Set<Integer> tempUniqueSet = new HashSet<Integer>();
			for(i = 0; i< tempList.size(); i++){
				if(tempList.get(i).trim().equals(tempUniqueValue.trim())){
					tempUniqueSet.add(i);
				}
			}
			disctinctValue.add(tempUniqueSet);
		}
		return disctinctValue;
	}
	
	/**
	 * Method to determine cut points for Conditional Entropy.
	 * @param disctinctValue
	 * @param attribute
	 * @return
	 */
	public String partitionWorstAttributeColumn(Set<Set<Integer>> disctinctValue, String attribute){
		List<List<Float>> partitionAttributes = new ArrayList<List<Float>>();
		List<List<Float>> partitionUniqueAttributes = new ArrayList<List<Float>>();
		List<List<String>> decision = new ArrayList<List<String>>();
		List<String> tempList = new ArrayList<String>();
		for (DataText dataText : equalIntervalDataTexts) {
			List<Map<String, Float>> attributeMapList = dataText.getAttributeMapList();
			for (Map<String, Float> attributeMap : attributeMapList) {
				if (attributeMap.containsKey(attribute)) {
					tempList.add(attributeMap.get(attribute).toString());
				}
			}
		}
		List<String> tempDecision = new ArrayList<String>();
		for (DataText dataText : equalIntervalDataTexts) {
			tempDecision.add(dataText.getDecision());
		}
		
		for(Set<Integer> subSet : disctinctValue){
			List<Float> tempSubSetList = new ArrayList<Float>();
			List<Float> uniqueTempSubSetList = new ArrayList<Float>();
			List<String> temp = new ArrayList<String>();
			for(Integer Position : subSet){
				tempSubSetList.add(Float.parseFloat(tempList.get(Position)));
				temp.add(tempDecision.get(Position));
			}
			decision.add(temp);
			partitionAttributes.add(tempSubSetList);
			int i = 0;
			for(Float value : tempSubSetList){
				if(i==0){
					uniqueTempSubSetList.add(value);
					i++;
				}else if(!uniqueTempSubSetList.contains(value)){
					uniqueTempSubSetList.add(value);
				}
			}
			Collections.sort(uniqueTempSubSetList);
			partitionUniqueAttributes.add(uniqueTempSubSetList);
		}
		Map<Float, Float> iterationCutPointToUse = new HashMap<Float, Float>();
		for(int i = 0; i < partitionUniqueAttributes.size(); i++){
			List<Float> cutPoint = new ArrayList<Float>(); 
			if(partitionUniqueAttributes.get(i).size() > 1){
				List<Float> tempPartitionUniqueAttributes = new ArrayList<Float>();
				tempPartitionUniqueAttributes = partitionUniqueAttributes.get(i);
				for(int j = 0; j < tempPartitionUniqueAttributes.size() - 1; j++){
					float cutPointAvg = (tempPartitionUniqueAttributes.get(j) + tempPartitionUniqueAttributes.get(j+1)) / (float) 2;
					cutPoint.add(cutPointAvg);
				}
				
				for(Float tempCutPoint : cutPoint){
					float cutPointResult = determineBestAttribute(partitionAttributes.get(i), tempCutPoint, decision.get(i));
					iterationCutPointToUse.put(tempCutPoint, cutPointResult);
				}
			} 
		}
		
		Iterator it = iterationCutPointToUse.entrySet().iterator();
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
		if(attributeKey != null){
			kValue.put(attribute, kValue.get(attribute)+1);
			return attributeKey;
		}else{
			return "-1";
		}
		
	}
	
	public float determineBestAttribute(List<Float> partitionAttributes, float cutPoint, List<String> decision){
		List<String> decisionCount1 = new ArrayList<String>();
		List<String> decisionCount2 = new ArrayList<String>();
		
		for(int i = 0; i<partitionAttributes.size(); i++){
			if(partitionAttributes.get(i) < cutPoint){
				decisionCount1.add(decision.get(i));
			}else {
				decisionCount2.add(decision.get(i));
			}
		}
		
		Map<String, Integer> mapDecision1 = new HashMap<String, Integer>();
		Map<String, Integer> mapDecision2 = new HashMap<String, Integer>();
		for(String decisionValue : decisionCount1){
			if(!mapDecision1.containsKey(decisionValue)){
				mapDecision1.put(decisionValue, 1);
			}else{
				mapDecision1.put(decisionValue, mapDecision1.get(decisionValue)+1);
			}
		}
		
		for(String decisionValue : decisionCount2){
			if(!mapDecision2.containsKey(decisionValue)){
				mapDecision2.put(decisionValue, 1);
			}else{
				mapDecision2.put(decisionValue, mapDecision2.get(decisionValue)+1);
			}
		}

		float totalSum = 0, sum1 = 0, sum2 = 0;
		Iterator it = mapDecision1.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int value = (int) pair.getValue();
			float commonValue = (float) value / (float) decisionCount1.size();
			float logValue = (float) ((Math.log(value) / Math.log(2)) - (Math.log(decisionCount1.size()) / Math
					.log(2)));
			sum1 = (float) (sum1 + (-1 * (commonValue) * (logValue)));
			it.remove(); // avoids a ConcurrentModificationException
		}
		float blocks1 = (float) decisionCount1.size() / (float) partitionAttributes.size();
		sum1 = (float) (sum1 * blocks1);
		
		
		Iterator it2 = mapDecision2.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry pair = (Map.Entry) it2.next();
			int value = (int) pair.getValue();
			float commonValue = (float) value / (float) decisionCount2.size();
			float logValue = (float) ((Math.log(value) / Math.log(2)) - (Math.log(decisionCount2.size()) / Math
					.log(2)));
			sum2 = (float) (sum2 + (-1 * (commonValue) * (logValue)));
			it2.remove(); // avoids a ConcurrentModificationException
		}
		float blocks2 = (float) decisionCount2.size() / (float) partitionAttributes.size();
		sum2 = (float) (sum2 * blocks2);
		totalSum = sum1 + sum2;

		return totalSum;
	}
}
