package com.genielee.projectboard.domain.constant;

/**
 * 게시판 카테고리 - 코인판 스타일 분류
 */
public enum BoardCategory {
    COIN_INFO("코인정보", "💰", "특정 코인에 대한 정보와 토론"),
    CHART_ANALYSIS("차트분석", "📊", "기술적 분석과 차트 패턴 토론"),
    COIN_NEWS("코인뉴스", "📰", "암호화폐 관련 최신 뉴스와 시장 동향"),
    MINING("채굴정보", "⛏️", "채굴 관련 정보와 하드웨어 토론"),
    AIRDROP("에어드랍", "🎁", "무료 코인 정보와 이벤트 공유"),
    NFT_P2E("NFT/P2E", "🎮", "NFT와 게임파이 관련 토론"),
    FREE_BOARD("자유게시판", "💭", "자유주제 토론과 소통"),
    REALTIME("실시간", "🔥", "실시간 핫 토픽과 속보"),
    GENERAL("일반", "📝", "카테고리 미분류 게시글"); // 기존 게시글 호환용

    private final String displayName;
    private final String emoji;
    private final String description;

    BoardCategory(String displayName, String emoji, String description) {
        this.displayName = displayName;
        this.emoji = emoji;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayNameWithEmoji() {
        return emoji + " " + displayName;
    }
}

