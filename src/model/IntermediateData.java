package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntermediateData {

	private List<Map<String, String>> attributeMapList = new ArrayList<Map<String, String>>();
	private String decision;

	public IntermediateData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IntermediateData(IntermediateData intermediateDatas) {
		List<Map<String, String>> tempList = new ArrayList<Map<String, String>>(intermediateDatas.getAttributeMapList()
				.size());
		for (int i = 0; i < intermediateDatas.attributeMapList.size(); i++) {
			Map<String, String> tempMap = new HashMap<String, String>(intermediateDatas.attributeMapList.get(i));
			tempList.add(tempMap);
		}
		this.attributeMapList = tempList;
		this.decision = intermediateDatas.decision;
	}

	public List<Map<String, String>> getAttributeMapList() {
		return attributeMapList;
	}

	public void setAttributeMapList(List<Map<String, String>> attributeMapList) {
		this.attributeMapList = attributeMapList;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}
}
