package com.demo.student.centipedegame;

/**
 * Created by butle on 4/28/2018.
 */

public class PlayerScoreInfo {
    private String playerName;
    private long playerScore;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlayerScoreInfo(String playerName, long playerScore) {
        this.playerName = playerName;
        this.playerScore = playerScore;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(long playerScore) {
        this.playerScore = playerScore;
    }

}
