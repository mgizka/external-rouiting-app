package com.orange.dop.poc.servicechannel.externalrouting.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import com.orange.dop.poc.servicechannel.externalrouting.Util;
import com.salesforce.emp.connector.example.BearerTokenProvider;

public class User {
	
	private final static String USER_URI = "https://dop-orange--b2bdev4.my.salesforce.com/services/data/v36.0/sobjects/user/";
	
	private String id;
	private String skill;
	private String name;
	
	private int numberOfAllocatedUnits=0;
	
	public User(String id, String name, String skill) {
		this.id=id;
		this.name=name;
		this.skill=skill;
	}
	
	public String getId() { return id; }
	public String getSkill() {return skill;}
	public String getName() { return name; }
	
	public String toString() {
		return "[name="+name+" ,"+"skill="+skill+" , number of units="+numberOfAllocatedUnits+"]\n";
	}

	public static User getUserDetails(BearerTokenProvider tokenProvider, String userId) throws JSONException {
		
		JSONObject sobject = new JSONObject(
				Util.httpGetCallout(
						USER_URI+userId, 
						tokenProvider.getToken()
				)
		);
		return new User(sobject.getString("Id"),sobject.getString("Username"),sobject.getString("Skill__c"));
	}

	public int getNumberOfAllocatedUnits() {
		return numberOfAllocatedUnits;
	}

	public void setNumberOfAllocatedUnits(int numberOfAllocatedUnits) {
		this.numberOfAllocatedUnits = numberOfAllocatedUnits;
	}

	public User increaseNumberOfAllocatedUnits() {
		++numberOfAllocatedUnits;
		return this;
		
	}
}
