package com.orange.dop.poc.servicechannel.externalrouting.helpers;

import com.orange.dop.poc.servicechannel.externalrouting.Util;
import com.salesforce.emp.connector.example.BearerTokenProvider;

public class WorkAgent {
	
	private final static String WORK_AGENT_URI = "https://dop-orange--b2bdev4.my.salesforce.com/services/data/v50.0/sobjects/agentwork/";

	public static void createWorkAgent(BearerTokenProvider tokenProvider, String agentId, String workId, String requestId)
	{
		
    	Util.httpPostCallout(
    			WORK_AGENT_URI, 
    			tokenProvider.getToken(), 
    			"{ \"UserId\" : \""+agentId+ "\", " + "\"WorkItemId\" : \"" + workId +"\", " + "\"PendingServiceRoutingId\" : \""
    					+ requestId +"\", \"ServiceChannelId\" : \"0N93N0000008VPSSA2\"}"
    	);
    	
	}
}
