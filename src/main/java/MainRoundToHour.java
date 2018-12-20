import client.AggregateDao;
import client.TweetDao;
import model.AggregatedTweets;

import java.io.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainRoundToHour {

    public static void main(String[] args) {

        String username = "AWS_USER_KEY";
        String password = "AWS_SECRET_KEY";
        String tableName = "etai-50000-by-hour";

        AggregateDao aggregateDao = new AggregateDao(username, password, tableName);

        String csvFile = "influencer_tweet_sentiment.csv";
        BufferedReader br;
        String line;
        String cvsSplitBy = ",";

        Map<String, List<Float>> tweetSentimentByHour = new HashMap<>();
        Map<String, Integer> tweetNumberByHour = new HashMap<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                String[] data = line.split(cvsSplitBy);
                if(!data[0].equals( "sentiment") && !data[0].equals("2")) {
                    float sentiment = Float.parseFloat(data[0]);
                    String timestamp = data[1];
                    int numberOfTweets = Integer.parseInt(data[2]);
                    Instant instant = Instant.from(timeFormatter.parse(timestamp)).truncatedTo(ChronoUnit.HOURS);

                    List<Float> sentiments;
                    int number = 0;
                    if(tweetSentimentByHour.containsKey(instant.toString())) {
                        sentiments = tweetSentimentByHour.get(instant.toString());
                        number = tweetNumberByHour.get(instant.toString());
                    } else {
                        sentiments = new ArrayList<>();
                    }
                    sentiments.add(sentiment);
                    number += numberOfTweets;
                    tweetSentimentByHour.put(instant.toString(), sentiments);
                    tweetNumberByHour.put(instant.toString(), number);
                }
            }

            List<AggregatedTweets> aggregatedTweets = new ArrayList<>();
            for(Map.Entry<String, List<Float>> entry : tweetSentimentByHour.entrySet()) {
                float avg = getAverageFloatOfList(entry.getValue());
                int number = tweetNumberByHour.get(entry.getKey());
                AggregatedTweets tempAggregatedTweets = new AggregatedTweets();
                tempAggregatedTweets.setSentimentScore(avg);
                tempAggregatedTweets.setNumberOfTweets(number);
                tempAggregatedTweets.setTimestamp(entry.getKey());
                aggregatedTweets.add(tempAggregatedTweets);
            }

            for(AggregatedTweets aggregatedTweets1 : aggregatedTweets) {
                aggregateDao.putItem(aggregatedTweets1);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static float getAverageFloatOfList(List<Float> numbers) {
        float avg = 0;
        for(Float number : numbers) {
            avg += number;
        }

        return avg / (float) numbers.size();
    }

}

