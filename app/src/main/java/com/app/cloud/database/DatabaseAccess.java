package com.app.cloud.database;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.app.cloud.request.User;
import com.app.cloud.utility.AppSharedPref;
import com.app.cloud.utility.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseAccess {
    private static final String TAG = DatabaseAccess.class.getSimpleName();
    private static final String TABLE_NAME = "User";
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    static  DatabaseAccess instance;

    private DatabaseAccess(Context context){
        Log.d(TAG,"Database Access");
        User user =  new AppSharedPref(context).getUser();
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-west-2:f1118cda-bd6b-418f-8065-fb431c59112e", // Identity pool ID
                Regions.US_WEST_2 // Region
        );

        Map<String, String> logins = new HashMap<String, String>();
        logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_OeU9UzVEK", "idToken");
        credentialsProvider.setLogins(logins);

        AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));

        List<KeySchemaElement> list = new ArrayList<>();
        list.add(new KeySchemaElement("Name" , "String"));
        list.add(new KeySchemaElement("Age" , "Number"));

        //dbClient.createTable(new CreateTableRequest("Profile" , list));

       // dbTable = Table.loadTable(dbClient,TABLE_NAME);
        //Log.d(TAG , dbTable.getTableName());
    }

    public static synchronized DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }
}
