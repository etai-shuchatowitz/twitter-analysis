import client.TweetDao;
import model.AggregatedTweets;
import model.Wallet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

public class BackTradeMain {

    private static String username = "AWS_USER_KEY";
    private static String password = "AWS_SECRET_KEY";
    private static String tableName = "etai-twitter-by-hour";

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    private static Map<String, AggregatedTweets> timeStampToTweet = new HashMap<>();

    private static final float initialCash = 100000f;

    private static float CORR_THRESHOLD = 0.6f;
    private static float POS_SENTIMENT_THRESHOLD = 1.4f;
    private static float NEG_SENTIMENT_THRESHOLD = 1.3f;

    private static TweetDao tweetDao = new TweetDao(username, password, tableName);

    public static void main(String[] args) {

        fillUpMap();

        String csvFile = "../correlations_masses.csv";
        BufferedReader br;
        String line;
        String cvsSplitBy = ",";

        float bestPosThresh = Float.POSITIVE_INFINITY;
        float bestCorr = Float.POSITIVE_INFINITY;
        float bestNegThresh = Float.POSITIVE_INFINITY;
        float bestCash = 100000f;

        float currentCorThresh = CORR_THRESHOLD;

        int tally = 0;

        for (int corrIncrement = 0; corrIncrement < 100; corrIncrement++) {

            float currentPositiveThresh = POS_SENTIMENT_THRESHOLD;

            for (int posSentThres = 0; posSentThres < 100; posSentThres++) {

                float currentNegativeThresh = NEG_SENTIMENT_THRESHOLD;

                for (int negSentThresh = 0; negSentThresh < 100; negSentThresh++) {

                    Wallet wallet = new Wallet();
                    wallet.setCash(initialCash);
                    wallet.setNumberOfBitcoin(0);

                    if(tally % 5000 == 0) {
                        System.out.println("I have $" + bestCash);

                        System.out.println(tally);
                    }


                    try {

                        br = new BufferedReader(new FileReader(csvFile));
                        while ((line = br.readLine()) != null) {

//                            System.out.println(line);

                            String[] data = line.split(cvsSplitBy);
                            String timestamp = data[0].replace(".csv", "");
                            float correlation = Float.parseFloat(data[1]);
                            int lag = Integer.parseInt(data[2]);
                            handleTime(timestamp, correlation, lag, wallet, currentCorThresh, currentPositiveThresh, currentNegativeThresh);
                        }

                        br.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (wallet.getCash() > bestCash) {
                        bestCash = wallet.getCash();
                        bestCorr = currentCorThresh;
                        bestPosThresh = currentPositiveThresh;
                        bestNegThresh = currentNegativeThresh;
                    }

                    currentNegativeThresh += .001;

                    tally++;

                }
                currentPositiveThresh += .001;

            }
            currentCorThresh += .001;
        }

        System.out.println("Cash: " + bestCash + " made with corr: " + bestCorr + ", " + bestPosThresh + ", neg: " + bestNegThresh);
    }

    private static void handleTime(String timestamp, float correlation, int lag, Wallet wallet,
                                   float currentCorrThresh, float currentPosThresh, float currentNegThresh) {

        if(correlation > currentCorrThresh) {
            Instant instant = Instant.from(timeFormatter.parse(timestamp)).minus(lag, ChronoUnit.HOURS);
            AggregatedTweets aggregatedTweets = timeStampToTweet.get(instant.toString());

            if(aggregatedTweets != null) {

                float sentiment = aggregatedTweets.getSentimentScore();

                if (sentiment > currentPosThresh) {
                    float diff = sentiment - currentPosThresh;
                    wallet.setNumberOfBitcoin(wallet.getNumberOfBitcoin() + diff);
                    wallet.setCash(wallet.getCash() - diff * (aggregatedTweets.getBitcoinClosingPrice()));
                } else if (sentiment < currentNegThresh && wallet.getNumberOfBitcoin() > currentNegThresh - sentiment) {
                    float diff = currentNegThresh - sentiment;
                    wallet.setNumberOfBitcoin(wallet.getNumberOfBitcoin() - diff);
                    wallet.setCash(wallet.getCash() + diff * (aggregatedTweets.getBitcoinClosingPrice()));
                }
            }
        }
    }

    private static void fillUpMap() {

        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        String beginDate = "2018-10-28T00:00:00Z";
        String endDate = "2018-11-28T00:00:00Z";
        TemporalAccessor beginTemporalAccessor = timeFormatter.parse(beginDate);
        TemporalAccessor endTemporalAccessor = timeFormatter.parse(endDate);
        Instant beginTime = Instant.from(beginTemporalAccessor);
        Instant endTime = Instant.from(endTemporalAccessor);

        Instant timeTally = beginTime;

        while(timeTally.isBefore(endTime)) {
            AggregatedTweets aggregatedTweets = tweetDao.getAllTweetsWithTimestamp(timeTally.toString());
            timeStampToTweet.put(timeTally.toString(), aggregatedTweets);
            timeTally = timeTally.plus(1, ChronoUnit.HOURS);
        }

        for (Map.Entry<String, AggregatedTweets> entry : timeStampToTweet.entrySet()) {
            System.out.println(entry);
        }
    }
}
