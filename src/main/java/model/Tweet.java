package model;

import com.google.gson.annotations.SerializedName;

public class Tweet {

    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("favorite_count")
    private int favoriteCount;
    private String text;
    @SerializedName("timestamp_ms")
    private long timestamp;
    @SerializedName("retweet_count")
    private int retweetCount;
    @SerializedName("quote_count")
    private int quoteCount;
    private User user;
    private float sentiment;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public float getSentiment() {
        return sentiment;
    }

    public void setSentiment(float sentiment) {
        this.sentiment = sentiment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getQuoteCount() {
        return quoteCount;
    }

    public void setQuoteCount(int quoteCount) {
        this.quoteCount = quoteCount;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "createdAt='" + createdAt + '\'' +
                ", favoriteCount=" + favoriteCount +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", retweetCount=" + retweetCount +
                ", quoteCount=" + quoteCount +
                ", user=" + user +
                ", sentiment=" + sentiment +
                '}';
    }

    public class User {
        @SerializedName("followers_count")
        private int followersCount;
        @SerializedName("friends_count")
        private int friendsCount;
        private String location;
        private boolean verified;

        public int getFollowersCount() {
            return followersCount;
        }

        public void setFollowersCount(int followersCount) {
            this.followersCount = followersCount;
        }

        public int getFriendsCount() {
            return friendsCount;
        }

        public void setFriendsCount(int friendsCount) {
            this.friendsCount = friendsCount;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        @Override
        public String toString() {
            return "User{" +
                    "followersCount=" + followersCount +
                    ", friendsCount=" + friendsCount +
                    ", location='" + location + '\'' +
                    ", verified=" + verified +
                    '}';
        }
    }


}
