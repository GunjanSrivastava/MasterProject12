package com.app.cloud.database;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.app.cloud.request.Action;
import com.app.cloud.request.User;
import com.app.cloud.request.UserCognitoSessionToken;
import com.app.cloud.utility.AppSharedPref;
import com.app.cloud.utility.Constants;

import java.util.HashMap;
import java.util.Map;

public class DatabaseAccess {
    private static final String TAG = DatabaseAccess.class.getSimpleName();
    private static final String TABLE_NAME = "User";
    static  DatabaseAccess instance;
    User user;
    UserCognitoSessionToken sessionTokens;
    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonDynamoDBClient dbClient;
    Context context;

    private DatabaseAccess(Context context){
        Log.d(TAG,"Database Access");
        this.context = context;
        AppSharedPref pref = new AppSharedPref(context);
        user =  pref.getUser();
        sessionTokens = pref.getUserSession();

        credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-west-2:1264fe8a-6803-4fc8-bcf4-7a8ebe68c156", // Identity pool ID
                Regions.US_WEST_2 // Region
        );

        Map<String, String> logins = new HashMap<String, String>();
        logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_OeU9UzVEK", sessionTokens.getIdToken().getJWTToken());
        credentialsProvider.setLogins(logins);

        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));

        Table table = Table.loadTable(dbClient , TABLE_NAME);
        table.getAttributes();
    }

    public void dbInteraction(Action action){
        if(action == Action.DBINSERT){
            insertRow();
        }else if(action == Action.DBUPDATE){
            updateRow();
        }
    }

    public void insertRow(){
        Log.d(TAG , "Inserting New Row...");
        Map<String, AttributeValue> map = new HashMap<>();
        map.put(Constants.DB_USER_ID , new AttributeValue(user.getEmail()));
        map.put(Constants.DB_NAME , new AttributeValue(user.getName()));
        map.put(Constants.DB_AGE , new AttributeValue(user.getAge()));
        map.put(Constants.DB_PHONE , new AttributeValue(user.getPhone()));
        map.put(Constants.DB_GENDER , new AttributeValue(user.getGender()));
        map.put(Constants.DB_DOB , new AttributeValue(user.getDob()));
        map.put(Constants.DB_TOKEN , new AttributeValue(new AppSharedPref(context).getString(Constants.FCM_TOKEN)));
        PutItemRequest putItemRequest = new PutItemRequest(TABLE_NAME, map);
        PutItemResult putItemResult = dbClient.putItem(putItemRequest);
        Log.d(TAG , "Inserting New Row Completed... " +putItemResult.toString());
    }

    public void updateRow(){
        Map<String, AttributeValue> map = new HashMap<>();
        map.put(Constants.DB_USER_ID , new AttributeValue(user.getEmail()));

        Map<String , AttributeValueUpdate> updateMap = new HashMap<>();
        updateMap.put(Constants.DB_TOKEN , new AttributeValueUpdate(new AttributeValue(new AppSharedPref(context).getString(Constants.FCM_TOKEN)) , AttributeAction.PUT));

        UpdateItemRequest putItemRequest = new UpdateItemRequest(TABLE_NAME, map,updateMap);
        UpdateItemResult putItemResult = dbClient.updateItem(putItemRequest);

        Log.d(TAG , putItemResult.toString());
    }

    public static synchronized DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }
}
