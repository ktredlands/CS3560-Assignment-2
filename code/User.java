package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * represents one "twitter" user
 */
public class User implements UserComponent, Subject, Observer
{
    // unique user ID entered from the admin panel
    private final String id;

    // time this user was created
    private final long creationTime;

    // most recent time of user activity
    private long lastUpdateTime;

    // following list
    private final List<User> following;

    // observer (followers) list
    private final List<Observer> followers;

    // list of user's own tweets and tweets from followed users
    private final List<String> newsFeed;

    // only the tweets written by this user
    private final List<String> ownTweets;

    // GUI refresh callbacks for any open UserView windows showing this user
    private final List<Runnable> viewListeners;

    /**
     * creates a new user with the given ID
     * 
     * @param id unique user ID
     */
    public User(String id)
    {
        this.id = id;
        this.creationTime = System.currentTimeMillis();
        this.lastUpdateTime = 0;
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.newsFeed = new ArrayList<>();
        this.ownTweets = new ArrayList<>();
        this.viewListeners = new ArrayList<>();
    }

    /**
     * returns this user's unique id
     * 
     * @return user ID
     */
    @Override
    public String getId()
    {
        return id;
    }

    /**
     * returns the time of this user's latest activity or received update
     * 
     * @return last update time in milliseconds
     */
    public long getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    /**
     * returns a list of users this user follows
     * 
     * @return unmodifiable following list
     */
    public List<User> getFollowing()
    {
        return Collections.unmodifiableList(following);
    }

    /**
     * returns a list of messages visible in the user's news feed
     * 
     * @return unmodifiable news list feed
     */
    public List<String> getNewsFeed()
    {
        return Collections.unmodifiableList(newsFeed);
    }

    /**
     * returns a list of tweets posted directly by this user
     * 
     * @return unmodifiable own tweets list
    */
   public List<String> getOwnTweets()
   {
        return Collections.unmodifiableList(ownTweets);
   }

   /**
    * makes this user follow another user
    * 
    * @param target the user to follow
    * @return true if the follow succeeded; false if invalid or already followed
    */
   public boolean follow(User target)
   {
        if (target == null || target == this || following.contains(target))
        {
            return false;
        }

        following.add(target);
        target.attach(this);

        notifyViewListeners();
        
        return true;
    }

    /**
     * posts a new tweet from this user
     * 
     * @param message tweet text entered in the user view
     */
    public void postTweet(String message)
    {
        if (message == null || message.trim().isEmpty())
        {
            return;
        }

        String cleanMessage = message.trim();
        ownTweets.add(cleanMessage);
        newsFeed.add(0, id + ": " + cleanMessage);
        
        lastUpdateTime = System.currentTimeMillis();

        notifyObservers(cleanMessage);
        notifyViewListeners();
    }

    /**
     * adds a follower to this user
     * 
     * @param observer follower that should receive this user's future tweets
     */
    @Override
    public void attach(Observer observer)
    {
        if (observer != null && !followers.contains(observer))
        {
            followers.add(observer);
        }
    }

    /**
     * notifies every follower that this user has posted a tweet
     * 
     * @param message tweet text to send to followers
     */
    @Override
    public void notifyObservers(String message)
    {
        for (Observer observer : followers)
        {
            observer.update(this, message);
        }
    }

    /**
     * receives a tweet from a user this user follows
     * 
     * @param user the user who posted the tweet
     * @param message the tweet text
     */
    @Override
    public void update(User user, String message)
    {
        newsFeed.add(0, user.getId() + ": " + message);
        lastUpdateTime = System.currentTimeMillis();
        notifyViewListeners();
    }

    /**
     * registers a GUI callback so an open UserView can refresh automatically
     * 
     * @param listener refresh action to run after this user changes
     */
    public void addViewListener(Runnable listener)
    {
        if (listener != null && !viewListeners.contains(listener))
        {
            viewListeners.add(listener);
        }
    }

    /**
     * removes a GUI refresh callback when a UserView window closes
     * 
     * @param listener refresh action to remove
     */
    public void removeViewListener(Runnable listener)
    {
        viewListeners.remove(listener);
    }

    /**
     * runs all registered GUI refresh callbacks
     */
    private void notifyViewListeners()
    {
        List<Runnable> copy = new ArrayList<>(viewListeners);
        for (Runnable listener : copy)
        {
            listener.run();
        }
    }

    /**
     * lets a visitor process the user
     * 
     * @param visitor visitor that will process the user
     */
    @Override
    public void accept(Visitor visitor)
    {
        visitor.visitUser(this);
    }

    /**
     * displays the user ID
     * 
     * @return user ID
     */
    @Override
    public String toString()
    {
        return id;
    }

    /**
     * gets the user creation time
     * 
     * @return user creation time
     */
    @Override
    public long getCreationTime()
    {
        return creationTime;
    }
}
