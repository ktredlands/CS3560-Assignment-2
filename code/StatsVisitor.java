package code;

/**
 * visitor that calculates all required Mini Twitter statistics
 */
public class StatsVisitor implements Visitor
{
    // number of User objects visited
    private int userCount;

    // number of UserGroup objects visited
    private int groupCount;

    // number of tweets posted by all users
    private int tweetCount;

    // number of tweets that contain a positive word
    private int positiveTweetCount;

    /**
     * counts one user and scans that user's own tweets
     *
     * @param user user being visited
     */
    @Override
    public void visitUser(User user)
    {
        userCount++;

        for (String tweet : user.getOwnTweets())
        {
            tweetCount++;
            if (isPositive(tweet))
            {
                positiveTweetCount++;
            }
        }
    }

    /**
     * counts one group
     *
     * @param group group being visited
     */
    @Override
    public void visitGroup(UserGroup group)
    {
        groupCount++;
    }

    /**
     * returns total user count
     *
     * @return user count
     */
    public int getUserCount()
    {
        return userCount;
    }

    /**
     * returns total group count
     *
     * @return group count
     */
    public int getGroupCount()
    {
        return groupCount;
    }

    /**
     * returns total tweet count
     *
     * @return tweet count
     */
    public int getTweetCount()
    {
        return tweetCount;
    }

    /**
     * returns percentage of positive tweets.
     *
     * @return positive tweet percentage, or 0 if there are no tweets
     */
    public double getPositivePercentage()
    {
        if (tweetCount == 0)
        {
            return 0.0;
        }
        return positiveTweetCount * 100.0 / tweetCount;
    }

    /**
     * determines whether a tweet contains one of the simple positive words
     *
     * @param tweet tweet text
     * @return true if tweet contains a positive word
     */
    private boolean isPositive(String tweet)
    {
        String lower = tweet.toLowerCase();
        return lower.contains("good") || lower.contains("great") || lower.contains("excellent") || lower.contains("happy") || lower.contains("love") || lower.contains("awesome");
    }
}