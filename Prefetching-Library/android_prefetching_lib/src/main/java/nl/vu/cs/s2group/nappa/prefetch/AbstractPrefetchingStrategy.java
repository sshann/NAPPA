package nl.vu.cs.s2group.nappa.prefetch;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.util.NappaConfigMap;

/**
 * This class defines common configuration shared among the prefetching strategies.
 * <p>
 * Configurations shared between all strategies are:
 *
 * <ul>
 *     <li> {@link PrefetchingStrategyConfigKeys#MAX_URL_TO_PREFETCH} </li>
 *     <li> {@link PrefetchingStrategyConfigKeys#LAST_N_SESSIONS} </li>
 *     <li> {@link PrefetchingStrategyConfigKeys#LOWER_THRESHOLD_SCORE} </li>
 * </ul>
 * <p>
 * Configurations shared between some strategies are:
 *
 * <ul>
 *     <li> {@link PrefetchingStrategyConfigKeys#USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS} </li>
 *     <li> {@link PrefetchingStrategyConfigKeys#NUMBER_OF_ITERATIONS} </li>
 * </ul>
 */
public abstract class AbstractPrefetchingStrategy implements PrefetchingStrategy {
    public static final float DEFAULT_SCORE_LOWER_THRESHOLD = 0.6f;
    public static final float DEFAULT_DAMPING_FACTOR = 0.85f;
    public static final int DEFAULT_LAST_N_SESSIONS = 5;
    public static final int DEFAULT_MAX_URL_TO_PREFETCH = 2;
    public static final int DEFAULT_NUMBER_OF_ITERATIONS = 10;
    public static final boolean DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS = true;

    protected final int maxNumberOfUrlToPrefetch;
    protected final int lastNSessions;
    protected final int numberOfIterations;
    protected final float scoreLowerThreshold;
    protected final float dampingFactor;
    protected final boolean useAllSessionsAsScoreForLastNSessions;

    public AbstractPrefetchingStrategy() {
        maxNumberOfUrlToPrefetch = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.MAX_URL_TO_PREFETCH,
                DEFAULT_MAX_URL_TO_PREFETCH);
        if (maxNumberOfUrlToPrefetch < 1)
            throw new IllegalArgumentException("The number of URLs to prefetch must be greater than 0. "
                    + maxNumberOfUrlToPrefetch + " provided.");

        useAllSessionsAsScoreForLastNSessions = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS,
                DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS);

        lastNSessions = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.LAST_N_SESSIONS,
                DEFAULT_LAST_N_SESSIONS);
        if (lastNSessions < -1 || lastNSessions == 0)
            throw new IllegalArgumentException("The number N of the N last sessions must be greater than 0 or -1. "
                    + lastNSessions + " provided.");

        scoreLowerThreshold = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.LOWER_THRESHOLD_SCORE,
                DEFAULT_SCORE_LOWER_THRESHOLD);
        if (scoreLowerThreshold < 0 || scoreLowerThreshold > 1)
            throw new IllegalArgumentException("The lower threshold score must be a number between 0 and 1. "
                    + scoreLowerThreshold + " provided.");

        numberOfIterations = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.NUMBER_OF_ITERATIONS,
                DEFAULT_NUMBER_OF_ITERATIONS);
        if (numberOfIterations < 1)
            throw new IllegalArgumentException("The number of iterations must be greater than 0. "
                    + numberOfIterations + " provided.");

        dampingFactor = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.PAGE_RANK_DAMPING_FACTOR,
                DEFAULT_DAMPING_FACTOR);
        if (dampingFactor < 0 || dampingFactor > 1)
            throw new IllegalArgumentException("The damping factor must be a number between 0 and 1. "
                    + dampingFactor + " provided.");
    }

    @Override
    public boolean needVisitTime() {
        return false;
    }

    @Override
    public boolean needSuccessorsVisitTime() {
        return false;
    }

    /**
     * Log how long it took to run the strategy
     *
     * @param tag       The tag to be used in the logging statement
     * @param activity  The current activity received in {@link #getTopNUrlToPrefetchForNode(ActivityNode, Integer)}
     * @param startTime A timestamp on when the calculations stared.
     */
    protected void logStrategyExecutionDuration(String tag, @NotNull ActivityNode activity, long startTime) {
        Log.d(tag, "Prefetching strategy calculations for " +
                activity.getSuccessorsVisitTimeList() +
                " was finished in " +
                (new Date().getTime() - startTime) + " ms");
    }
}
