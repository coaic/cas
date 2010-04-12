package org.jasig.cas.server.session;

import org.jasig.cas.server.authentication.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Min;

/**
 * Implementation of an expiration policy that adds the concept of saying that a
 * ticket can only be used once every X milliseconds to prevent misconfigured
 * clients from consuming resources by doing constant redirects.
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.5
 */
public final class ThrottledUseAndTimeoutExpirationPolicy implements ExpirationPolicy {

    private static final Logger log = LoggerFactory.getLogger(ThrottledUseAndTimeoutExpirationPolicy.class);

    /** Static ID for serialization. */
    private static final long serialVersionUID = -848036845536731268L;

    /** The time to kill in milliseconds. */
    @Min(1)
    private final long timeToKillInMilliSeconds;

    /** Time time between which a ticket must wait to be used again. */
    @Min(1)
    private final long timeInBetweenUsesInMilliSeconds;

    public ThrottledUseAndTimeoutExpirationPolicy(final long timeToKillInMilliSeconds, final long timeInBetweenUsesInMilliSeconds) {
        this.timeToKillInMilliSeconds = timeToKillInMilliSeconds;
        this.timeInBetweenUsesInMilliSeconds = timeInBetweenUsesInMilliSeconds;
    }

    public boolean isExpired(final State state) {
    if (state.getUsageCount() == 0
            && (System.currentTimeMillis() - state.getLastUsedTime() < this.timeToKillInMilliSeconds)) {
            if (log.isDebugEnabled()) {
                log.debug("Ticket is not expired due to a count of zero and the time being less than the timeToKillInMilliseconds");
            }
            return false;
        }

        if ((System.currentTimeMillis() - state.getLastUsedTime() >= this.timeToKillInMilliSeconds)) {
            if (log.isDebugEnabled()) {
                log.debug("Ticket is expired due to the time being greater than the timeToKillInMilliseconds");
            }
            return true;
        }

        if ((System.currentTimeMillis() - state.getLastUsedTime() <= this.timeInBetweenUsesInMilliSeconds)) {
            log.warn("Ticket is expired due to the time being less than the waiting period.");
            return true;
        }

        return false;
    }
}