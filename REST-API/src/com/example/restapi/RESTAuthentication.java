package com.example.restapi;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RESTAuthentication{
 
	@Value("{!USERNAME}")
	public String USERNAME;
    //static final String USERNAME = "aftab@infy.com";
	@Value("{!PASSWORD}")
	public String PASSWORD;
    //static final String PASSWORD = "october123LVUQ6MsnCOMZRmgz1ObPyirr";
    @Value("{!LOGINURL}")
    public String  LOGINURL;
	//static final String LOGINURL = "https://ap4.salesforce.com";
    @Value("{!GRANTSERVICE}")
    public String  GRANTSERVICE;
    //static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    @Value("{!CLIENTID}")
    public String CLIENTID;
    //static final String CLIENTID = "3MVG9YDQS5WtC11oPDL0UqItJLPT9YRdvsOAM1D_JHs0FAijmTHYKWiY7acgZs5nlQgeleXqqDL1zmqqon58R";
    @Value("{!CLIENTSECRET}")
    public String CLIENTSECRET;
    //static final String CLIENTSECRET = "3148616487113454715";
    
    @RequestMapping(value="/auth")
    public String getAuth() {
 
        HttpClient httpclient = HttpClientBuilder.create().build();
 
        // Assemble the login request URL
        String loginURL = LOGINURL +
                          GRANTSERVICE +
                          "&client_id=" + CLIENTID +
                          "&client_secret=" + CLIENTSECRET +
                          "&username=" + USERNAME +
                          "&password=" + PASSWORD;
 
        // Login requests must be POSTs
        HttpPost httpPost = new HttpPost(loginURL);
        HttpResponse response = null;
 
        try {
            // Execute the login POST request
            response = httpclient.execute(httpPost);
            System.out.println(response);
        } catch (ClientProtocolException cpException) {
            cpException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
 
        // verify response is HTTP OK
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            System.out.println("Error authenticating to Force.com: "+statusCode);
            // Error is in EntityUtils.toString(response.getEntity())
            return "Error auth failed with status"+statusCode;
        }
 
        String getResult = null;
        try {
            getResult = EntityUtils.toString(response.getEntity());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JSONObject jsonObject = null;
        String loginAccessToken = null;
        String loginInstanceUrl = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
            loginAccessToken = jsonObject.getString("access_token");
            loginInstanceUrl = jsonObject.getString("instance_url");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        System.out.println(response.getStatusLine());
        System.out.println("Successful login");
        System.out.println("instance URL: "+loginInstanceUrl);
        System.out.println("access token/session ID: "+loginAccessToken);
 
        // release connection
        httpPost.releaseConnection();
        
        return "access_token: "+loginAccessToken;
}
}
