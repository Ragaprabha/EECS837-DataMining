package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataText {

	private List<Map<String, Float>> attributeMapList = new ArrayList<Map<String, Float>>();
	private String decision;

	public List<Map<String, Float>> getAttributeMapList() {
		return attributeMapList;
	}

	public void setAttributeMapList(List<Map<String, Float>> attributeMapList) {
		this.attributeMapList = attributeMapList;
	}

	public String getDecision() {
		return decision;
	}

	public void setDecision(String decision) {
		this.decision = decision;
	}

}
