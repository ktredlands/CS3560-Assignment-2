
package code;

/**
 * Subject interface used by the observer
 * 
 * a user is a subject when other users follow them. The subject keeps a list of observers and notifies those observers when a tweet is posted
 */
public interface Subject
{
    /**
     * Adds an observer to this subject's follower list
     * 
     * @param observer the observer that should receive future updates
     */
    void attach(Observer observer);

    /**
     * sends a new tweet update to all observers
     * 
     * @param message the tweet text to send to observers
     */
    void notifyObservers(String message);
}
