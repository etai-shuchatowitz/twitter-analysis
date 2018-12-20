package client;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import model.AggregatedTweets;
import model.NewsAggregation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class AggregateDao {

    private static final String hashkeyName = "timestamp";

    private BasicAWSCredentials awsCredentials;
    private AWSCredentialsProviderChain credentials;
    private AmazonDynamoDB amazonDynamoDB;
    private Table table;

    public AggregateDao(String username, String password, String tableName) {
        awsCredentials = new BasicAWSCredentials(username, password);
        credentials = new AWSCredentialsProviderChain(new AWSStaticCredentialsProvider(awsCredentials));
        amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withCredentials(credentials).withRegion("us-east-1").build();
        table = new DynamoDB(amazonDynamoDB).getTable(tableName);
    }

    public void putItem(AggregatedTweets aggregatedTweets) {

        try {
            Item putItem =
                    new Item()
                            .withPrimaryKey(hashkeyName, aggregatedTweets.getTimestamp())
                            .withInt("numberOfTweets", aggregatedTweets.getNumberOfTweets())
                            .withFloat("sentiment", aggregatedTweets.getSentimentScore());
            PutItemSpec putItemSpec =
                    new PutItemSpec().withItem(putItem);
            table.putItem(putItemSpec);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateAndSaveNewsSentiments(
            NewsAggregation newsAggregation) {

        // Save the campaignId and segmentid in a attributeName map
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#T", "newsTitleSentiment");
        expressionAttributeNames.put("#C", "newsContentSentiment");
        expressionAttributeNames.put("#D", "newsDescriptionSentiment");
        expressionAttributeNames.put("#U", "newsURL");

        // Save the campaignId and segmentId values in a attributeValue map
        Map<String, Object> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":newsTitleSentiment", newsAggregation.getTitleSentiments());
        expressionAttributeValues.put(":newsContentSentiment", newsAggregation.getContentSentiments());
        expressionAttributeValues.put(
                ":newsDescriptionSentiment", newsAggregation.getDescriptionSentiments());
        expressionAttributeValues.put(":newsURL", newsAggregation.getUrl());

        table.updateItem(
                hashkeyName, newsAggregation.getTimestamp(),
                "set #T = :newsTitleSentiment, #C = :newsContentSentiment, #D = :newsDescriptionSentiment, #U = :newsURL",
                expressionAttributeNames,
                expressionAttributeValues);
    }

    public void updateWithBTCPrice(String timestamp, float price) {
        // Save the campaignId and segmentid in a attributeName map
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#B", "bitcoinClosingPrice");


        // Save the campaignId and segmentId values in a attributeValue map
        Map<String, Object> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":bitcoinClosingPrice", price);

        table.updateItem(
                hashkeyName, timestamp,
                "set #B = :bitcoinClosingPrice",
                expressionAttributeNames,
                expressionAttributeValues);
    }
}
