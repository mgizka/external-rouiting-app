package com.orange.dop.poc.servicechannel.externalrouting;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

public class Util {
	
	public static String httpGetCallout(String uri, String token) {
    	HttpClient client = HttpClient.newHttpClient();
    	
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .setHeader("Authorization", "Bearer " + token)
                .build();
        
        HttpResponse<String> response;
		try {
			response = client.send(request,
			        HttpResponse.BodyHandlers.ofString());
			 		System.out.println(response.body());
			 		
			 return response.body();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		
		return null;

	}
	
	public static String httpPostCallout(String uri, String token, String body) {
    	HttpClient client = HttpClient.newHttpClient();
    	
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .setHeader("Authorization", "Bearer " + token)
                .setHeader("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(body))
                .build();      
        
        HttpResponse<String> response;
		try {
			response = client.send(request,
			        HttpResponse.BodyHandlers.ofString());
			 		System.out.println(response.body());
			 		
			 return response.body();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		
		return null;

	}

}
