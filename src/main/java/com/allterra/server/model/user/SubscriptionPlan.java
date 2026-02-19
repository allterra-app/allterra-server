package com.allterra.server.model.user;

/**
 * User subscription plans.
 */
public enum SubscriptionPlan {
    FREE(0),
    MONTHLY(30),
    YEARLY(365);

    private final int durationDays;

    SubscriptionPlan(final int durationDays) {
        this.durationDays = durationDays;
    }

    public int getDurationDays() {
        return durationDays;
    }
}
