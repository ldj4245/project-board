package com.genielee.projectboard.infrastructure.crypto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * CoinGecko API Coin Detail 엔드포인트 응답 데이터
 */
@Data
public class CoinGeckoCoinDetail {
    
    private String id;
    private String symbol;
    private String name;
    
    @JsonProperty("asset_platform_id")
    private String assetPlatformId;
    
    @JsonProperty("block_time_in_minutes")
    private Integer blockTimeInMinutes;
    
    @JsonProperty("hashing_algorithm")
    private String hashingAlgorithm;
    
    private Map<String, String> categories;
    
    @JsonProperty("public_notice")
    private String publicNotice;
    
    @JsonProperty("additional_notices")
    private String[] additionalNotices;
    
    private Map<String, String> description;
    
    private Links links;
    
    private Image image;
    
    @JsonProperty("country_origin")
    private String countryOrigin;
    
    @JsonProperty("genesis_date")
    private String genesisDate;
    
    @JsonProperty("sentiment_votes_up_percentage")
    private BigDecimal sentimentVotesUpPercentage;
    
    @JsonProperty("sentiment_votes_down_percentage")
    private BigDecimal sentimentVotesDownPercentage;
    
    @JsonProperty("market_cap_rank")
    private Integer marketCapRank;
    
    @JsonProperty("coingecko_rank")
    private Integer coingeckoRank;
    
    @JsonProperty("coingecko_score")
    private BigDecimal coingeckoScore;
    
    @JsonProperty("developer_score")
    private BigDecimal developerScore;
    
    @JsonProperty("community_score")
    private BigDecimal communityScore;
    
    @JsonProperty("liquidity_score")
    private BigDecimal liquidityScore;
    
    @JsonProperty("public_interest_score")
    private BigDecimal publicInterestScore;
    
    @JsonProperty("market_data")
    private MarketData marketData;
    
    @JsonProperty("community_data")
    private CommunityData communityData;
    
    @JsonProperty("developer_data")
    private DeveloperData developerData;
    
    @JsonProperty("public_interest_stats")
    private PublicInterestStats publicInterestStats;
    
    @JsonProperty("status_updates")
    private Object[] statusUpdates;
    
    @JsonProperty("last_updated")
    private String lastUpdated;
    
    @Data
    public static class Links {
        private String[] homepage;
        private String[] blockchain_site;
        private String[] official_forum_url;
        private String[] chat_url;
        private String[] announcement_url;
        private String twitter_screen_name;
        private String facebook_username;
        private String bitcointalk_thread_identifier;
        private String telegram_channel_identifier;
        private String subreddit_url;
        private Map<String, String> repos_url;
    }
    
    @Data
    public static class Image {
        private String thumb;
        private String small;
        private String large;
    }
    
    @Data
    public static class MarketData {
        @JsonProperty("current_price")
        private Map<String, BigDecimal> currentPrice;
        
        @JsonProperty("total_value_locked")
        private Map<String, BigDecimal> totalValueLocked;
        
        @JsonProperty("mcap_to_tvl_ratio")
        private BigDecimal mcapToTvlRatio;
        
        @JsonProperty("fdv_to_tvl_ratio")
        private BigDecimal fdvToTvlRatio;
        
        private Map<String, BigDecimal> roi;
        
        @JsonProperty("ath")
        private Map<String, BigDecimal> ath;
        
        @JsonProperty("ath_change_percentage")
        private Map<String, BigDecimal> athChangePercentage;
        
        @JsonProperty("ath_date")
        private Map<String, String> athDate;
        
        @JsonProperty("atl")
        private Map<String, BigDecimal> atl;
        
        @JsonProperty("atl_change_percentage")
        private Map<String, BigDecimal> atlChangePercentage;
        
        @JsonProperty("atl_date")
        private Map<String, String> atlDate;
        
        @JsonProperty("market_cap")
        private Map<String, BigDecimal> marketCap;
        
        @JsonProperty("market_cap_rank")
        private Integer marketCapRank;
        
        @JsonProperty("fully_diluted_valuation")
        private Map<String, BigDecimal> fullyDilutedValuation;
        
        @JsonProperty("total_volume")
        private Map<String, BigDecimal> totalVolume;
        
        @JsonProperty("high_24h")
        private Map<String, BigDecimal> high24h;
        
        @JsonProperty("low_24h")
        private Map<String, BigDecimal> low24h;
        
        @JsonProperty("price_change_24h")
        private BigDecimal priceChange24h;
        
        @JsonProperty("price_change_percentage_24h")
        private BigDecimal priceChangePercentage24h;
        
        @JsonProperty("price_change_percentage_7d")
        private BigDecimal priceChangePercentage7d;
        
        @JsonProperty("price_change_percentage_14d")
        private BigDecimal priceChangePercentage14d;
        
        @JsonProperty("price_change_percentage_30d")
        private BigDecimal priceChangePercentage30d;
        
        @JsonProperty("price_change_percentage_60d")
        private BigDecimal priceChangePercentage60d;
        
        @JsonProperty("price_change_percentage_200d")
        private BigDecimal priceChangePercentage200d;
        
        @JsonProperty("price_change_percentage_1y")
        private BigDecimal priceChangePercentage1y;
        
        @JsonProperty("market_cap_change_24h")
        private BigDecimal marketCapChange24h;
        
        @JsonProperty("market_cap_change_percentage_24h")
        private BigDecimal marketCapChangePercentage24h;
        
        @JsonProperty("price_change_24h_in_currency")
        private Map<String, BigDecimal> priceChange24hInCurrency;
        
        @JsonProperty("price_change_percentage_1h_in_currency")
        private Map<String, BigDecimal> priceChangePercentage1hInCurrency;
        
        @JsonProperty("price_change_percentage_24h_in_currency")
        private Map<String, BigDecimal> priceChangePercentage24hInCurrency;
        
        @JsonProperty("price_change_percentage_7d_in_currency")
        private Map<String, BigDecimal> priceChangePercentage7dInCurrency;
        
        @JsonProperty("price_change_percentage_14d_in_currency")
        private Map<String, BigDecimal> priceChangePercentage14dInCurrency;
        
        @JsonProperty("price_change_percentage_30d_in_currency")
        private Map<String, BigDecimal> priceChangePercentage30dInCurrency;
        
        @JsonProperty("price_change_percentage_60d_in_currency")
        private Map<String, BigDecimal> priceChangePercentage60dInCurrency;
        
        @JsonProperty("price_change_percentage_200d_in_currency")
        private Map<String, BigDecimal> priceChangePercentage200dInCurrency;
        
        @JsonProperty("price_change_percentage_1y_in_currency")
        private Map<String, BigDecimal> priceChangePercentage1yInCurrency;
        
        @JsonProperty("market_cap_change_24h_in_currency")
        private Map<String, BigDecimal> marketCapChange24hInCurrency;
        
        @JsonProperty("market_cap_change_percentage_24h_in_currency")
        private Map<String, BigDecimal> marketCapChangePercentage24hInCurrency;
        
        @JsonProperty("total_supply")
        private BigDecimal totalSupply;
        
        @JsonProperty("max_supply")
        private BigDecimal maxSupply;
        
        @JsonProperty("circulating_supply")
        private BigDecimal circulatingSupply;
        
        @JsonProperty("last_updated")
        private String lastUpdated;
    }
    
    @Data
    public static class CommunityData {
        @JsonProperty("facebook_likes")
        private Integer facebookLikes;
        
        @JsonProperty("twitter_followers")
        private Integer twitterFollowers;
        
        @JsonProperty("reddit_average_posts_48h")
        private BigDecimal redditAveragePosts48h;
        
        @JsonProperty("reddit_average_comments_48h")
        private BigDecimal redditAverageComments48h;
        
        @JsonProperty("reddit_subscribers")
        private Integer redditSubscribers;
        
        @JsonProperty("reddit_accounts_active_48h")
        private Integer redditAccountsActive48h;
        
        @JsonProperty("telegram_channel_user_count")
        private Integer telegramChannelUserCount;
    }
    
    @Data
    public static class DeveloperData {
        private Integer forks;
        private Integer stars;
        private Integer subscribers;
        
        @JsonProperty("total_issues")
        private Integer totalIssues;
        
        @JsonProperty("closed_issues")
        private Integer closedIssues;
        
        @JsonProperty("pull_requests_merged")
        private Integer pullRequestsMerged;
        
        @JsonProperty("pull_request_contributors")
        private Integer pullRequestContributors;
        
        @JsonProperty("code_additions_4_weeks")
        private Integer codeAdditions4Weeks;
        
        @JsonProperty("code_deletions_4_weeks")
        private Integer codeDeletions4Weeks;
        
        @JsonProperty("commit_count_4_weeks")
        private Integer commitCount4Weeks;
        
        @JsonProperty("last_4_weeks_commit_activity_series")
        private Integer[] last4WeeksCommitActivitySeries;
    }
    
    @Data
    public static class PublicInterestStats {
        @JsonProperty("alexa_rank")
        private Integer alexaRank;
        
        @JsonProperty("bing_matches")
        private Integer bingMatches;
    }
}