package model;

import java.util.List;

public class NewsResponse {

    private int totalResults;
    private List<Article> articles;

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "NewsResponse{" +
                "totalResults=" + totalResults +
                ", articles=" + articles +
                '}';
    }

    public static class Article {
        private String title;
        private String description;
        private String publishedAt;
        private String content;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "Article{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", publishedAt='" + publishedAt + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}


//{
//        "status": "ok",
//        "totalResults": 6048,
//        -"articles": [
//        -{
//        -"source": {
//        "id": null,
//        "name": "Newsbtc.com"
//        },
//        "author": "Cole Petersen",
//        "title": "Bitcoin Drops to $4,000 as Sell-Off Reignites, There Could Be a Larger Trend in Play",
//        "description": "After a couple days of upwards trading, the cryptocurrency markets have continued to sell-off, led by Bitcoin, but are still sitting above their recently established lows. Today’s drop could be the result of traders taking profits on short-term long positions…",
//        "url": "https://www.newsbtc.com/2018/11/30/bitcoin-drops-to-4000-as-sell-off-reignites-there-could-be-a-larger-trend-in-play/",
//        "urlToImage": "https://www.newsbtc.com/wp-content/uploads/2018/11/Bitcoin-Sell-Off.jpg",
//        "publishedAt": "2018-11-30T18:30:15Z",
//        "content": "After a couple days of upwards trading, the cryptocurrency markets have continued to sell-off, led by Bitcoin, but are still sitting above their recently established lows. Todays drop could be the result of traders taking profits on short-term long positions,… [+2771 chars]"
//        }