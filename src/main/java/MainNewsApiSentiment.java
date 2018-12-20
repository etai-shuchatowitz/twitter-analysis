import client.AggregateDao;
import client.HttpClient;
import com.google.gson.Gson;
import model.NewsAggregation;
import model.NewsResponse;
import sentiment.SentimentAnalyzer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainNewsApiSentiment {

    private static final Gson gson = new Gson();
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) throws IOException {

        String username = "AWS_USER_KEY";
        String password = "AWS_SECRET_KEY";
        String tableName = "etai-news-sentiment";

        AggregateDao aggregateDao = new AggregateDao(username, password, tableName);
        LocalDate ld = LocalDate.parse("2018-11-03", timeFormatter);
        LocalDateTime ldt = LocalDateTime.of(ld, LocalDateTime.now().toLocalTime());
        ThreadPoolExecutor threads = new ThreadPoolExecutor(1, 1, 10000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));
        threads.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        while(ldt.isBefore(LocalDateTime.now())) {
            int pageNumber = 0;
            do {
                String endpointURL = attachQueryParameters("https://newsapi.org/v2/everything?q=bitcoin", ldt, pageNumber);
                threads.submit(new Thread(() -> {
                    try {
                        System.out.println(endpointURL);
                        String newsResponseString = HttpClient.returnJsonFromEndpoint(endpointURL);
                        NewsResponse newsResponse = gson.fromJson(newsResponseString, NewsResponse.class);
                        System.out.println("parsed: " + newsResponseString);
                        Map<String, List<NewsAggregation>> newsAggregations = new HashMap<>();
                        int tally = 0;
                        for (NewsResponse.Article article : newsResponse.getArticles()) {
                            System.out.println("There are " + newsResponse.getArticles().size() + " articles in the news response");
                            Instant instant = Instant.from(timeFormatter.parse(article.getPublishedAt())).truncatedTo(ChronoUnit.HOURS);

                            String title = article.getTitle();
                            System.out.println("title is: " + title);
                            float titleSentiment = SentimentAnalyzer.findSentiment(title);
                            System.out.println("title sentiment: " + titleSentiment);

                            String description = article.getDescription();
                            float descSentiment = SentimentAnalyzer.findSentiment(description);
                            System.out.println("description sentiment: " + descSentiment);

                            String content = article.getContent();
                            float contentSentiment = SentimentAnalyzer.findSentiment(content);
                            System.out.println("content sentiment: " + contentSentiment);

                            NewsAggregation newsAggregation = new NewsAggregation();
                            newsAggregation.setContentSentiments(contentSentiment);
                            newsAggregation.setDescriptionSentiments(descSentiment);
                            newsAggregation.setTimestamp(instant.toString());
                            newsAggregation.setTitleSentiments(titleSentiment);
                            newsAggregation.setUrl(endpointURL);

                            List<NewsAggregation> tempAggs;
                            if (newsAggregations.containsKey(instant.toString())) {
                                tempAggs = newsAggregations.get(instant.toString());
                            } else {
                                tempAggs = new ArrayList<>();
                            }

                            tempAggs.add(newsAggregation);

                            newsAggregations.put(instant.toString(), tempAggs);
                            tally++;
                            System.out.println(tally);
                        }

                        for (Map.Entry<String, List<NewsAggregation>> entry : newsAggregations.entrySet()) {
                            for(NewsAggregation na : entry.getValue()) {
                                aggregateDao.updateAndSaveNewsSentiments(na);
                            }
                        }

                    } catch (Exception e) {
                       System.out.println(e.getMessage());
                    }
                }));
                pageNumber++;
            } while (pageNumber < 10);
            ldt = ldt.plus(1, ChronoUnit.DAYS);
        }
//
//        List<NewsAggregation> finalNewsAggregations = new ArrayList<>();
//        for(Map.Entry<String, List<NewsAggregation>> entry : newsAggregations.entrySet()) {
//            float finalTitleSent = 0f;
//            float finalContentSent = 0f;
//            float finalDescSent = 0f;
//
//            for(NewsAggregation na : entry.getValue()) {
//                finalContentSent += na.getContentSentiments();
//                finalTitleSent += na.getTitleSentiments();
//                finalDescSent += na.getDescriptionSentiments();
//            }
//
//            finalTitleSent /= (float) entry.getValue().size();
//            finalContentSent /= (float) entry.getValue().size();
//            finalDescSent /= (float) entry.getValue().size();
//
//            NewsAggregation finalNewsAgg = new NewsAggregation();
//            finalNewsAgg.setTitleSentiments(finalTitleSent);
//            finalNewsAgg.setDescriptionSentiments(finalDescSent);
//            finalNewsAgg.setContentSentiments(finalContentSent);
//            finalNewsAgg.setTimestamp(entry.getKey());
//
//            System.out.println(finalNewsAgg);
//
////            aggregateDao.updateAndSaveNewsSentiments(finalNewsAgg);
//
//            finalNewsAggregations.add(finalNewsAgg);
//        }



    }

    public static String attachQueryParameters(String endpointURL, LocalDateTime date, int tally) {
        String today_mmddyyyy = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(date);
        LocalDateTime tomorrow = date.plus(1, ChronoUnit.DAYS);
        String tomorrow_mmddyyyy =
                DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(tomorrow);
        return endpointURL + "&from=" + today_mmddyyyy + "&to=" + tomorrow_mmddyyyy + "&page=" + tally + "&pageSize=100&apiKey=5068dceda5af4583a059bf6f0fc7f14a";
    }
}
