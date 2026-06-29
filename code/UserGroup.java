package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * represents a group of users and/or subgroups
 */
public class UserGroup implements UserComponent
{
    // unique group ID entered from the admin panel
    private final String id;

    // time this group was created
    private final long creationTime;

    // children inside of this group
    private final List<UserComponent> children;

    /**
     * creates a group with the given ID
     *
     * @param id unique group ID
     */
    public UserGroup(String id)
    {
        this.id = id;
        this.creationTime = System.currentTimeMillis();
        this.children = new ArrayList<>();
    }

    /**
     * returns this group's unique ID
     *
     * @return group ID
     */
    @Override
    public String getId()
    {
        return id;
    }

    /**
     * returns the time this group was created
     *
     * @return creation time in milliseconds
     */
    @Override
    public long getCreationTime()
    {
        return creationTime;
    }

    /**
     * adds a user or subgroup to this group
     *
     * @param component child component to add
     * @return true if added; false if invalid or already contained
     */
    public boolean addChild(UserComponent component)
    {
        if (component == null || children.contains(component))
        {
            return false;
        }

        children.add(component);
        return true;
    }

    /**
     * returns a list of this group's children
     *
     * @return unmodifiable child list
     */
    public List<UserComponent> getChildren()
    {
        return Collections.unmodifiableList(children);
    }

    /**
     * lets a visitor process this group and then all children recursively.
     *
     * @param visitor visitor that will process this group and its descendants
     */
    @Override
    public void accept(Visitor visitor)
    {
        visitor.visitGroup(this);

        for (UserComponent child : children)
        {
            child.accept(visitor);
        }
    }

    /**
     * displays the group ID
     *
     * @return group ID
     */
    @Override
    public String toString()
    {
        return id;
    }
}