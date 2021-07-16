package com.orange.dop.poc.servicechannel.externalrouting;

import static com.salesforce.emp.connector.LoginHelper.login;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.jetty.util.ajax.JSON;
import org.json.*;

import com.orange.dop.poc.servicechannel.externalrouting.helpers.Case;
import com.orange.dop.poc.servicechannel.externalrouting.helpers.User;
import com.orange.dop.poc.servicechannel.externalrouting.helpers.WorkAgent;
import com.salesforce.emp.connector.BayeuxParameters;
import com.salesforce.emp.connector.EmpConnector;
import com.salesforce.emp.connector.TopicSubscription;
import com.salesforce.emp.connector.example.BearerTokenProvider;


public class ExternalRoutingApp {
	

	public final static HashMap<String,String> routingTable = new HashMap() {
		{
			put ("Problem","Accounting Specialist");
			put ("Payments","Accounting Specialist");
			put ("Network troubleshooting","Network Specialist");
			put ("Data transmission","Network Specialist") ;
			put ("Voice calls","Network Specialist");
		}
	};
		
	
	private static HashMap<String,User> users = new HashMap<String,User>();
	
	private static BearerTokenProvider tokenProvider = null;
	
    public static void main(String[] argv) throws Exception {
    	
    String login = System.getenv("DC_LOGIN");
    String password = System.getenv("DC_PASSWD");

        tokenProvider = new BearerTokenProvider(() -> {
            try {
				System.out.print("login:"+login);
				System.out.print("password:"+password);
				
                return login(login, password);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.exit(1);
                throw new RuntimeException(e);
            }
        });

        BayeuxParameters params = tokenProvider.login();

        Consumer<Map<String, Object>> psr = event -> {
        	
        	System.out.println(String.format("Received [PendingServiceRequest]:\n%s", JSON.toString(event)));
        	
			try {
				
				JSONObject sobject = new JSONObject(JSON.toString(event))
						.getJSONObject("sobject");
	        	
	        	String caseId=sobject.getString("WorkItemId"),
	        			psrId=sobject.getString("Id");
			  
				Case scase = Case.getCaseDetails(tokenProvider, caseId);
	        	
	        	String agentId = selectHandlingAgent(scase, users);
	        	
	        	WorkAgent.createWorkAgent(tokenProvider, agentId, caseId, psrId);
	        	
			} catch (Exception e1) {
				e1.printStackTrace();
			}   
       	
        };
        
        Consumer<Map<String, Object>> usp = event -> {
        	         	
        	try {
/*            	
               	System.out.println(String.format("Received [UserServicePresence]"
               			+ ":\n%s", event));
*/               	
               	JSONObject sobject = new JSONObject(JSON.toString(event))
               			.getJSONObject("sobject");
               	
               	JSONObject sevent = new JSONObject(JSON.toString(event))
               			.getJSONObject("event");
               	
               	
               	String userId = sobject.getString("OwnerId");
               	
               	boolean action = (("true".equals(sobject.getString("IsAway")))
              			|| (("updated".equals(sevent.getString("type")))&&("false".equals(sobject.getString("IsCurrentState")))));
               	
				if (action) {
					users.remove(userId);
				}else if (!users.containsKey(userId)){
						users.put(
								userId, 
								User.getUserDetails(tokenProvider, userId)
								);
				}     					
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.exit(2);
			}
        	
         };
  

        EmpConnector connector = new EmpConnector(params);

        connector.setBearerTokenProvider(tokenProvider);

        connector.start().get(5, TimeUnit.SECONDS);

        TopicSubscription psrSubscription = connector.subscribe(System.getenv("PSR_TOPIC_NAME"), -1, psr).get(5, TimeUnit.SECONDS);
        TopicSubscription uspSubscription = connector.subscribe(System.getenv("USP_TOPIC_NAME"), -1, usp).get(5, TimeUnit.SECONDS);

        System.out.println(String.format("Subscribed for PSRs: %s", psrSubscription));
        System.out.println(String.format("Subscribed for USP: %s", uspSubscription));
    }
    
	private static String selectHandlingAgent(Case scase, HashMap<String,User> users ) {
		
		System.out.println("case Type:"+scase.getType());
		
		int minValue = Integer.MAX_VALUE;
		String userId = null;
		
		for(Iterator<User> it = users.values().iterator();it.hasNext();) {
			User usr = ((User)it.next());
			int cv = usr.getNumberOfAllocatedUnits();
			if(cv<minValue && routingTable.get(scase.getType()).equals(usr.getSkill())) {
				minValue=cv;
				userId=usr.getId();
			}
		}   

	    System.out.println("selected user:"+userId);
	    
	    if (!userId.equals(null)) 	    
		    users.put(userId, (User) users.get(userId).increaseNumberOfAllocatedUnits());
	   
		return userId;
	}
}

