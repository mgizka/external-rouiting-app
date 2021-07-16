package com.orange.dop.poc.servicechannel.externalrouting.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import com.orange.dop.poc.servicechannel.externalrouting.Util;
import com.salesforce.emp.connector.example.BearerTokenProvider;

public class Case {
	
	private final static String CASE_URI = "https://dop-orange--b2bdev4.my.salesforce.com/services/data/v36.0/sobjects/case/";
	
	private String id;
	private String type;
	
	public Case(String id, String type) {
		this.id=id;
		this.type=type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public static Case getCaseDetails(BearerTokenProvider tokenProvider, String caseId) throws JSONException {
		
		JSONObject sobject = new JSONObject(
				Util.httpGetCallout(
						CASE_URI+caseId, 
						tokenProvider.getToken()
				)
		);
		return new Case(sobject.getString("Id"),sobject.getString("Type"));
	}
}
