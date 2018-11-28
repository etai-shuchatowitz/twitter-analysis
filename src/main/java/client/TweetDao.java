package client;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import model.Tweet;

public class TweetDao {

    private BasicAWSCredentials awsCredentials;
    private AWSCredentialsProviderChain credentials;
    private AmazonDynamoDB amazonDynamoDB;
    private String tableName;
    private Table table;

    public TweetDao(String username, String password, String tableName) {
        awsCredentials = new BasicAWSCredentials(username, password);
        credentials = new AWSCredentialsProviderChain(new AWSStaticCredentialsProvider(awsCredentials));
        amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withCredentials(credentials).withRegion("us-east-1").build();
        this.tableName = tableName;
        table = new DynamoDB(amazonDynamoDB).getTable(tableName);
    }

    public void putItem(Tweet tweet) {

    }
}
