package code;

import java.util.ArrayList;
import java.util.List;

/**
 * central service class that stores and manages all Mini Twitter data
 */
public class MiniTwitterService
{
    
    //single shared service instance
    private static final MiniTwitterService INSTANCE = new MiniTwitterService();

    // root group of composite tree
    private final UserGroup root;

    // list of all users
    private final List<User> users;

    
    // list of all groups
    private final List<UserGroup> groups;

    /**
     * private constructor for Singleton pattern
     */
    private MiniTwitterService()
    {
        root = new UserGroup("Root");
        users = new ArrayList<>();
        groups = new ArrayList<>();
        groups.add(root);
    }

    /**
     * Returns the single shared service instance.
     *
     * @return singleton service
     */
    public static MiniTwitterService getInstance() {
        return INSTANCE;
    }

    /**
     * returns the root group used by the admin tree
     *
     * @return root user group
     */
    public UserGroup getRoot()
    {
        return root;
    }

    /**
     * adds a user to the selected parent group
     *
     * @param userId new user's ID
     * @param parent parent group; root is used if parent is null or not a group
     * @return true if user was created and added; false if invalid or duplicate
     */
    public boolean addUser(String userId, UserComponent parent)
    {
        String cleanId = cleanId(userId);
        if (cleanId == null || findUser(cleanId) != null || findGroup(cleanId) != null)
        {
            return false;
        }

        User user = new User(cleanId);
        UserGroup parentGroup = resolveParentGroup(parent);
        parentGroup.addChild(user);
        users.add(user);
        return true;
    }

    /**
     * adds a group to the selected parent group
     *
     * @param groupId new group's ID
     * @param parent parent group; root is used if parent is null or not a group
     * @return true if group was created and added; false if invalid or duplicate
     */
    public boolean addGroup(String groupId, UserComponent parent)
    {
        String cleanId = cleanId(groupId);
        if (cleanId == null || findUser(cleanId) != null || findGroup(cleanId) != null)
        {
            return false;
        }

        UserGroup group = new UserGroup(cleanId);
        UserGroup parentGroup = resolveParentGroup(parent);
        parentGroup.addChild(group);
        groups.add(group);
        return true;
    }

    /**
     * makes one user follow another user
     *
     * @param followerId ID of user doing the following
     * @param targetId ID of user to follow
     * @return true if follow succeeds; false otherwise
     */
    public boolean followUser(String followerId, String targetId)
    {
        User follower = findUser(cleanId(followerId));
        User target = findUser(cleanId(targetId));

        if (follower == null || target == null)
        {
            return false;
        }

        return follower.follow(target);
    }

    /**
     * posts a tweet for a specific user
     *
     * @param userId ID of posting user
     * @param message tweet text
     * @return true if posted; false if user does not exist or message is empty
     */
    public boolean postTweet(String userId, String message)
    {
        User user = findUser(cleanId(userId));
        if (user == null || message == null || message.trim().isEmpty())
        {
            return false;
        }

        user.postTweet(message);
        return true;
    }

    /**
     * finds a user by ID using a linear search
     *
     * @param userId ID to find
     * @return matching user, or null if not found
     */
    public User findUser(String userId)
    {
        if (userId == null)
        {
            return null;
        }

        for (User user : users)
        {
            if (user.getId().equals(userId))
            {
                return user;
            }
        }
        return null;
    }

    /**
     * finds a group by ID using a linear search
     *
     * @param groupId ID to find
     * @return matching group, or null if not found
     */
    public UserGroup findGroup(String groupId)
    {
        if (groupId == null)
        {
            return null;
        }

        for (UserGroup group : groups)
        {
            if (group.getId().equals(groupId))
            {
                return group;
            }
        }
        return null;
    }

    /**
     * validates all stored IDs
     *
     * @return true if all IDs are valid
     */
    public boolean validateAllIds()
    {
        for (User user : users)
        {
            if (hasInvalidId(user.getId()))
            {
                return false;
            }
        }

        for (UserGroup group : groups)
        {
            if (hasInvalidId(group.getId()))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * finds the user with the latest update time
     *
     * @return most recently updated user, or null if there are no users
     */
    public User getLastUpdatedUser()
    {
        if (users.isEmpty())
        {
            return null;
        }

        User latest = users.get(0);
        for (User user : users)
        {
            if (user.getLastUpdateTime() > latest.getLastUpdateTime())
            {
                latest = user;
            }
        }

        return latest;
    }

    /**
     * runs StatsVisitor on the whole tree
     *
     * @return visitor containing the calculated statistics
     */
    public StatsVisitor collectStats()
    {
        StatsVisitor visitor = new StatsVisitor();
        root.accept(visitor);
        return visitor;
    }

    /**
     * cleans an ID by trimming whitespace
     *
     * @param id raw ID
     * @return trimmed ID, or null if empty
     */
    private String cleanId(String id)
    {
        if (id == null)
        {
            return null;
        }

        String clean = id.trim();
        if (clean.isEmpty())
        {
            return null;
        }
        return clean;
    }

    /**
     * checks whether an ID violates the no spaces rule
     *
     * @param id ID to check
     * @return true if the ID contains a space
     */
    private boolean hasInvalidId(String id)
    {
        return id == null || id.contains(" ");
    }

    /**
     * sesolves where a new component should be inserted
     *
     * @param parent selected tree component
     * @return selected group, or root if selected component is not a group
     */
    private UserGroup resolveParentGroup(UserComponent parent)
    {
        if (parent instanceof UserGroup)
        {
            return (UserGroup) parent;
        }
        return root;
    }
}