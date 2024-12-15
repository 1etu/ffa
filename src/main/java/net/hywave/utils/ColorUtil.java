package net.hywave.utils;

import org.bukkit.ChatColor;

public class ColorUtil {

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static class Colors {
        public static final String BLACK = "&0";
        public static final String DARK_BLUE = "&1";
        public static final String DARK_GREEN = "&2";
        public static final String DARK_AQUA = "&3";
        public static final String DARK_RED = "&4";
        public static final String DARK_PURPLE = "&5";
        public static final String GOLD = "&6";
        public static final String GRAY = "&7";
        public static final String DARK_GRAY = "&8";
        public static final String BLUE = "&9";
        public static final String GREEN = "&a";
        public static final String AQUA = "&b";
        public static final String RED = "&c";
        public static final String LIGHT_PURPLE = "&d";
        public static final String YELLOW = "&e";
        public static final String WHITE = "&f";

        public static final String OBFUSCATED = "&k";
        public static final String BOLD = "&l";
        public static final String STRIKETHROUGH = "&m";
        public static final String UNDERLINE = "&n";
        public static final String ITALIC = "&o";
        public static final String RESET = "&r";
    }

    public static class Format {
        public static final String ERROR = Colors.RED;
        public static final String SUCCESS = Colors.GREEN;
        public static final String INFO = Colors.YELLOW;
        public static final String HIGHLIGHT = Colors.AQUA;
        public static final String HEADER = Colors.GOLD + Colors.BOLD;
        public static final String SUBHEADER = Colors.YELLOW + Colors.BOLD;
    }

    public static String stripColor(String message) {
        return ChatColor.stripColor(message);
    }
    public static String gradient(String message, String start, String end) {
        StringBuilder result = new StringBuilder();
        String[] colors = {start, end};
        int length = message.length();
        
        for (int i = 0; i < length; i++) {
            double ratio = (double) i / (length - 1);
            String color = colors[(int) Math.round(ratio)];
            result.append(color).append(message.charAt(i));
        }
        
        return colorize(result.toString());
    }
}