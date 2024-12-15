package net.hywave.profile;

import com.google.gson.annotations.SerializedName;

public class PlayerStats {
    @SerializedName("kills")
    private int kills;
    
    @SerializedName("deaths")
    private int deaths;

    @SerializedName("assists")
    private int assists;

    @SerializedName("killStreak")
    private int currentKillStreak;

    @SerializedName("bestKillStreak")
    private int bestKillStreak;

    public PlayerStats() {
        this.kills = 0;
        this.deaths = 0;
        this.assists = 0;
        this.currentKillStreak = 0;
        this.bestKillStreak = 0;
    }

    public void incrementKills() {
        this.kills++;
        this.currentKillStreak++;
        if (currentKillStreak > bestKillStreak) {
            bestKillStreak = currentKillStreak;
        }
    }

    public void incrementDeaths() {
        this.deaths++;
        this.currentKillStreak = 0;
    }

    public void incrementAssists() {
        this.assists++;
    }

    public void decrementKills() {
        if (this.kills > 0) {
            this.kills--;
        }
    }

    public void decrementDeaths() {
        if (this.deaths > 0) {
            this.deaths--;
        }
    }

    public void decrementAssists() {
        if (this.assists > 0) {
            this.assists--;
        }
    }

    public void resetStats() {
        this.kills = 0;
        this.deaths = 0;
        this.assists = 0;
        this.currentKillStreak = 0;
        this.bestKillStreak = 0;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }

    public int getCurrentKillStreak() {
        return currentKillStreak;
    }

    public int getBestKillStreak() {
        return bestKillStreak;
    }

    public double getKDRatio() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }

    public double getKDARatio() {
        return deaths == 0 ? (kills + assists) : (double) (kills + assists) / deaths;
    }
}
