
package code;

/**
 * Common interface for every object that can appear in the Mini Twitter tree
 */
public interface UserComponent
{
    /**
     * Returns the unique ID shown in the tree and used for lookup
     * 
     * @return this component's ID
     */
    String getId();

    /**
     * Returns the time this component was created
     * 
     * @return creation time in milliseconds
     */
    long getCreationTime();

    /**
     * Allows a Visitor object to perform an operation on this component
     * 
     * @param visitor the visitor that will process this component
     */
    void accept(Visitor visitor);
}
