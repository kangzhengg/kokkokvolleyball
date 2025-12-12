package com.example.bookcare_qy;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages user credits for the exchange system.
 * Users earn 1 credit when they add a book for exchange.
 * Users spend 1 credit when they request an exchange from others.
 */
public class CreditManager {
    private static final String PREFS_NAME = "UserCredits";
    private static final String KEY_CREDITS = "credits";
    private static final int DEFAULT_CREDITS = 0;

    private final SharedPreferences prefs;

    public CreditManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get the current credit balance
     */
    public int getCredits() {
        return prefs.getInt(KEY_CREDITS, DEFAULT_CREDITS);
    }

    /**
     * Add credits to the user's account
     * @param amount Number of credits to add
     */
    public void addCredits(int amount) {
        int current = getCredits();
        prefs.edit().putInt(KEY_CREDITS, current + amount).apply();
    }

    /**
     * Deduct credits from the user's account
     * @param amount Number of credits to deduct
     * @return true if deduction was successful, false if insufficient credits
     */
    public boolean deductCredits(int amount) {
        int current = getCredits();
        if (current >= amount) {
            prefs.edit().putInt(KEY_CREDITS, current - amount).apply();
            return true;
        }
        return false;
    }

    /**
     * Check if user has enough credits
     * @param amount Required credits
     * @return true if user has sufficient credits
     */
    public boolean hasEnoughCredits(int amount) {
        return getCredits() >= amount;
    }

    /**
     * Reset credits (for testing purposes)
     */
    public void resetCredits() {
        prefs.edit().putInt(KEY_CREDITS, DEFAULT_CREDITS).apply();
    }
}