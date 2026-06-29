
package code;

/**
 * Visitor interface used by the Visitor pattern
 */
public interface Visitor
{
    /**
     * runs the visitor's user specific behavior
     * 
     * @param user the user being visited
     */
    void visitUser(User user);

    /**
     * runs the visitor's group specific behavior
     * 
     * @param group the group being visited
     */
    void visitGroup(UserGroup group);
}
