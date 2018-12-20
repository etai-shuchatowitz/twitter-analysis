package client;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import model.AggregatedTweets;

public class TweetDao {

    private static final String hashkeyName = "timestamp";
    private static final String sortkeyName = "filename";

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

    public void putItem(AggregatedTweets aggregatedTweets) {

        try {
            Item putItem =
                    new Item()
                            .withPrimaryKey(hashkeyName, aggregatedTweets.getTimestamp(), sortkeyName, aggregatedTweets.getFilename())
                            .withInt("numberOfTweets", aggregatedTweets.getNumberOfTweets())
                            .withFloat("sentiment", aggregatedTweets.getSentimentScore());
            PutItemSpec putItemSpec =
                    new PutItemSpec().withItem(putItem);
            table.putItem(putItemSpec);
        } catch (Exception e) {
            System.out.println("Caught error");
        }
    }

    public AggregatedTweets getAllTweetsWithTimestamp(String timestamp) {
        QuerySpec querySpec =
                new QuerySpec()
                        .withHashKey(hashkeyName, timestamp)
                        .withScanIndexForward(true)
                        .withMaxResultSize(20)
                        .withMaxPageSize(5);

        ItemCollection<QueryOutcome> itemCollection = table.query(querySpec);
        float totalSentiment = 0;
        int totalTweetsSent = 0;
        float totalPrice = 0;
        for (Item item : itemCollection) {
            if(item.getNumber("bitcoinClosingPrice") != null && item.getNumber("sentiment") != null) {
                totalSentiment += item.getFloat("sentiment");
                totalTweetsSent += item.getInt("numberOfTweets");
                totalPrice += Float.parseFloat(String.valueOf(item.getNumber("bitcoinClosingPrice")));
            }
        }

        totalSentiment /= itemCollection.getAccumulatedItemCount();

        AggregatedTweets aggregatedTweets = new AggregatedTweets();
        aggregatedTweets.setTimestamp(timestamp);
        aggregatedTweets.setNumberOfTweets(totalTweetsSent);
        aggregatedTweets.setSentimentScore(totalSentiment);
        aggregatedTweets.setBitcoinClosingPrice(totalPrice);

//        System.out.println(aggregatedTweets);
        return aggregatedTweets;
    }
}
