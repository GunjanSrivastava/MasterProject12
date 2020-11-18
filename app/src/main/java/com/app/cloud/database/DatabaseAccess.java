package com.app.cloud.database;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.app.cloud.utility.Constants;

public class DatabaseAccess {
    private static final String TAG = DatabaseAccess.class.getSimpleName();
    private static final String TABLE_NAME = "User";
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    static  DatabaseAccess instance;

    private DatabaseAccess(Context context){
        Log.d(TAG,"Database Access");
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(context, Constants.POOL_ID, Regions.US_WEST_2);
        AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        //dbTable = Table.loadTable(dbClient,TABLE_NAME);
        Log.d(TAG , dbTable.getTableName());
    }

    public static synchronized DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }


}
