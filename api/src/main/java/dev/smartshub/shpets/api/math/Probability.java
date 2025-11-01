package dev.smartshub.shpets.api.math;

import java.text.DecimalFormat;
import java.util.random.RandomGenerator;

public final class Probability {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#####");

    /**
     * Checks if an event occurs,
     * a base probability, and an additional probability per level.
     *
     * @param probability base chance in percent (0-100)
     * @param generator random number generator
     * @return true if the event occurs, false otherwise
     */
    public static boolean checkProbability(
        final double probability,
        final RandomGenerator generator
    ) {
        return probability > 0 && (probability >= 100 || generator.nextDouble(100) < probability);
    }

    /**
     * Returns the total probability as a formatted string.
     *
     * @param probabilityPerLevel additional chance per level in percent
     * @return probability formatted with two decimals (0-100)
     */
    public static String getProbabilityInString(final double probabilityPerLevel) {
        return DECIMAL_FORMAT.format(Math.min(100, probabilityPerLevel));
    }
}