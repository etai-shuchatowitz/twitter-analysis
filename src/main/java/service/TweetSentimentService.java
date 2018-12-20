package service;

import com.google.gson.Gson;
import model.AggregatedTweets;
import model.Tweet;
import sentiment.SentimentAnalyzer;

import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TweetSentimentService {

    private static final Gson gson = new Gson();

    public Map<String, List<Float>> getTweetsFromJsonFile(InputStream inputStream) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("tweet_with_sentiment.txt"));

        Map<String, List<Float>> averageSentimentPerHour = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            int tally = 0;
            while ((line = br.readLine()) != null) {
                Tweet tweet = gson.fromJson(line, Tweet.class);
                if(tweet.getUser() != null) {
                    if (tweet.getUser().getFollowersCount() > 50000 && !tweet.getText().contains("http")) {
                        float sentiment = SentimentAnalyzer.findSentiment(tweet.getText());
                        tweet.setSentiment(sentiment);
                        long timestamp = tweet.getTimestamp();
                        Instant instant = Instant.ofEpochMilli(timestamp).truncatedTo(ChronoUnit.MINUTES);
                        long numberOfMinutesToOffset = (instant.getEpochSecond() % 600) / 60;
                        Instant offSetInstant = instant.minus(numberOfMinutesToOffset, ChronoUnit.MINUTES);
                        System.out.println(offSetInstant);

                        String tweet_with_sentiment = "tweet: " + tweet.getText() + " has score: " + tweet.getSentiment() + "\n";
//                        System.out.println(tweet_with_sentiment);
                        writer.write(tweet_with_sentiment);

                        List<Float> sentiments;
                        if (averageSentimentPerHour.get(offSetInstant.toString()) == null) {
                            sentiments = new ArrayList<>();
                        } else {
                            sentiments = averageSentimentPerHour.get(offSetInstant.toString());
                        }
                        sentiments.add(sentiment);
                        averageSentimentPerHour.put(offSetInstant.toString(), sentiments);

                        tally++;
                    }
                }
            }

            System.out.println("Final tally is: " + tally);

        } catch (IOException e) {
            e.printStackTrace();
        }

        writer.close();

        return averageSentimentPerHour;
    }

    public List<AggregatedTweets> createAggregatedTweetsFromMap(Map<String, List<Float>> averageSentimentPerHour, String key) {
        List<AggregatedTweets> aggregatedTweetsList = new ArrayList<>();
        for(Map.Entry<String, List<Float>> entry : averageSentimentPerHour.entrySet()) {
            AggregatedTweets aggregatedTweets = new AggregatedTweets();
            aggregatedTweets.setFilename(key);
            aggregatedTweets.setTimestamp(entry.getKey());
            aggregatedTweets.setNumberOfTweets(entry.getValue().size());
            aggregatedTweets.setSentimentScore(getAverageOfList(entry.getValue()));
            aggregatedTweetsList.add(aggregatedTweets);
        }
        return aggregatedTweetsList;
    }

    public float getAverageOfList(List<Float> numbers) {
        float avg = 0;
        for(Float number : numbers) {
            avg += number;
        }

        return avg / (float) numbers.size();
    }
}
