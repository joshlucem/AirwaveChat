package com.joshlucem.airwavechat.util;

import java.util.Random;

public class StaticUtil {
    private static final Random RANDOM = new Random();
    private static final char[] STATIC_CHARS = {'▓', '▒', '░', '█', '▄', '▀', '■', '□', '▪', '▫'};
    
    /**
     * Apply static distortion to a message based on distance ratio
     * @param message Original message
     * @param distanceRatio How close to max distance (0.0 = at sender, 1.0 = at limit)
     * @param threshold Distance ratio where static begins (e.g., 0.75 = 75% of max distance)
     * @param intensity Static effect multiplier (0.0 = no static, 1.0 = max static)
     * @return Distorted message with static
     */
    public static String applyStatic(String message, double distanceRatio, double threshold, double intensity) {
        if (distanceRatio < threshold || intensity <= 0) {
            return message; // No static needed
        }
        
        // Calculate static chance based on how far past threshold
        double excessRatio = (distanceRatio - threshold) / (1.0 - threshold);
        double staticChance = excessRatio * intensity;
        
        if (staticChance <= 0) return message;
        if (staticChance > 1.0) staticChance = 1.0;
        
        StringBuilder result = new StringBuilder();
        boolean inTag = false;
        
        for (char c : message.toCharArray()) {
            // Don't distort MiniMessage tags
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
                // Preserve tags and spaces
                result.append(c);
            } else if (RANDOM.nextDouble() < staticChance) {
                // Replace with static character
                result.append(STATIC_CHARS[RANDOM.nextInt(STATIC_CHARS.length)]);
            } else {
                // Keep original character
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Calculate distance ratio (0.0 to 1.0) based on actual distance and max distance
     */
    public static double calculateDistanceRatio(double actualDistance, double maxDistance) {
        if (maxDistance <= 0) return 0.0;
        double ratio = actualDistance / maxDistance;
        return Math.min(ratio, 1.0);
    }
}
