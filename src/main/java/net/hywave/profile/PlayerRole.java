package net.hywave.profile;

public enum PlayerRole {
    USER("&7", "User"),
    FRIEND("&a", "Friend"),
    STREAMER("&d", "Streamer"),
    DEV("&b", "Developer"),
    ADMIN("&c", "Admin");

    private final String color;
    private final String displayName;

    PlayerRole(String color, String displayName) {
        this.color = color;
        this.displayName = displayName;
    }

    public String getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }
}
