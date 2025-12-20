package com.joshlucem.airwavechat.util;

import java.util.Random;

public class StaticUtil {
    private static final Random RANDOM = new Random();
    private static final char[] STATIC_CHARS = {'▓', '▒', '░', '█', '▄', '▀', '■', '□', '▪', '▫'};
    
    public static String applyStatic(String message, double distanceRatio, double threshold, double intensity) {
        if (distanceRatio < threshold || intensity <= 0) {
            return message;
        }
        
        double excessRatio = (distanceRatio - threshold) / (1.0 - threshold);
        double staticChance = excessRatio * intensity;
        
        if (staticChance <= 0) return message;
        if (staticChance > 1.0) staticChance = 1.0;
        
        StringBuilder result = new StringBuilder();
        boolean inTag = false;
        
        for (char c : message.toCharArray()) {
            if (c == '<') {
                inTag = true;
                result.append(c);
                continue;
            }
            if (c == '>') {
                inTag = false;
                result.append(c);
                continue;
            }
            
            if (inTag || c == ' ') {
                result.append(c);
            } else if (RANDOM.nextDouble() < staticChance) {
                result.append(STATIC_CHARS[RANDOM.nextInt(STATIC_CHARS.length)]);
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    public static double calculateDistanceRatio(double actualDistance, double maxDistance) {
        if (maxDistance <= 0) return 0.0;
        double ratio = actualDistance / maxDistance;
        return Math.min(ratio, 1.0);
    }
}
