
package code;
/**
 * Observer interface used by the observer pattern
 * 
 * A user is an observer when they follow another user. When the followed user posts a tweet, the observer receives an update
 */
public interface Observer
{
    /**
     * receives a new tweet from a user that is being followed
     * 
     * @param user the user who posed the tweet
     * @param message the tweet text
     */
    void update(User user, String message);
}