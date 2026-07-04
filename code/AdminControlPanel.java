package code;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * main admin GUI window
 */
public class AdminControlPanel extends JFrame
{
    // single admin panel instance
    private static final AdminControlPanel INSTANCE = new AdminControlPanel();

    // shared mini twitter service
    private final MiniTwitterService service;

    // swing tree showing the composite structure
    private JTree tree;

    // tree model that is rebuilt after adding users/groups
    private DefaultTreeModel treeModel;

    // text field for entering a new user ID
    private final JTextField userIdField;

    // text field for entering a new group ID
    private final JTextField groupIdField;

    /**
     * private constructor
     */
    private AdminControlPanel()
    {
        super("Mini Twitter Admin Control Panel");
        this.service = MiniTwitterService.getInstance();
        this.userIdField = new JTextField();
        this.groupIdField = new JTextField();

        buildLayout();
        refreshTree();

        setSize(720, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * returns the single admin control panel instance
     *
     * @return singleton admin panel
     */
    public static AdminControlPanel getInstance()
    {
        return INSTANCE;
    }

    /**
     * builds the complete admin GUI layout
     */
    private void buildLayout()
    {
        setLayout(new BorderLayout(8, 8));

        tree = new JTree();
        JScrollPane treeScroll = new JScrollPane(tree);
        treeScroll.setBorder(BorderFactory.createTitledBorder("User Tree"));
        add(treeScroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridLayout(8, 1, 6, 6));
        add(rightPanel, BorderLayout.EAST);

        userIdField.setBorder(BorderFactory.createTitledBorder("User Id"));
        groupIdField.setBorder(BorderFactory.createTitledBorder("Group Id"));

        JButton addUserButton = new JButton("Add User");
        JButton addGroupButton = new JButton("Add Group");
        JButton openUserViewButton = new JButton("Open User View");
        JButton showUserTotalButton = new JButton("Show User Total");
        JButton showGroupTotalButton = new JButton("Show Group Total");
        JButton showMessageTotalButton = new JButton("Show Messages Total");
        JButton showPositivePercentageButton = new JButton("Show Positive Percentage");
        JButton validateIdsButton = new JButton("Validate IDs");
        JButton lastUpdatedButton = new JButton("Show Last Updated User");

        rightPanel.add(userIdField);
        rightPanel.add(addUserButton);
        rightPanel.add(groupIdField);
        rightPanel.add(addGroupButton);
        rightPanel.add(openUserViewButton);
        rightPanel.add(validateIdsButton);
        rightPanel.add(lastUpdatedButton);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 4, 6, 6));
        bottomPanel.add(showUserTotalButton);
        bottomPanel.add(showGroupTotalButton);
        bottomPanel.add(showMessageTotalButton);
        bottomPanel.add(showPositivePercentageButton);
        add(bottomPanel, BorderLayout.SOUTH);

        addUserButton.addActionListener(e -> addUser());
        addGroupButton.addActionListener(e -> addGroup());
        openUserViewButton.addActionListener(e -> openSelectedUserView());
        showUserTotalButton.addActionListener(e -> showUserTotal());
        showGroupTotalButton.addActionListener(e -> showGroupTotal());
        showMessageTotalButton.addActionListener(e -> showMessageTotal());
        showPositivePercentageButton.addActionListener(e -> showPositivePercentage());
        validateIdsButton.addActionListener(e -> showIdValidationResult());
        lastUpdatedButton.addActionListener(e -> showLastUpdatedUser());
    }

    /**
     * adds a user under the currently selected group
     */
    private void addUser()
    {
        String userId = userIdField.getText();
        UserComponent selected = getSelectedComponent();

        boolean added = service.addUser(userId, selected);
        if (!added)
        {
            showMessage("Could not add user. User ID cannot be empty.");
            return;
        }

        userIdField.setText("");
        refreshTree();
    }

    /**
     * adds a group under the currently selected group
     */
    private void addGroup()
    {
        String groupId = groupIdField.getText();
        UserComponent selected = getSelectedComponent();

        boolean added = service.addGroup(groupId, selected);
        if (!added)
        {
            showMessage("Could not add group. Group ID cannot be empty.");
            return;
        }

        groupIdField.setText("");
        refreshTree();
    }

    /**
     * shows whether all IDs are valid
     */
    private void showIdValidationResult()
    {
        if (service.validateAllIds())
        {
            showMessage("All IDs are valid.");
        }
        else
        {
            showMessage("Invalid ID found. IDs must be unique and not contain any spaces.");
        }
    }

    /**
     * shows the user with the latest update time
     */
    private void showLastUpdatedUser()
    {
        User latest = service.getLastUpdatedUser();
        if (latest == null)
        {
            showMessage("No user has posted or received a tweet yet.");
            return;
        }

        showMessage("Last updated user: " + latest.getId() + "\nLast update time: " + formatTime(latest.getLastUpdateTime()));
    }

    /**
     * converts a millisecond timestamp into a readable date and time string
     * 
     * @param time timestamp from System.currentTimeMillis()
     * @return formatted date and time
     */
    private String formatTime(long time)
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }

    /**
     * opens a UserView for the currently selected user
     */
    private void openSelectedUserView()
    {
        UserComponent selected = getSelectedComponent();
        if (!(selected instanceof User))
        {
            showMessage("Please select a user in the tree first.");
            return;
        }

        User user = (User) selected;
        UserView view = new UserView(user);
        view.setVisible(true);
    }

    /**
     * reads the current tree selection and returns the stored UserComponent
     *
     * @return selected component, or null if nothing valid is selected
     */
    private UserComponent getSelectedComponent()
    {
        TreePath path = tree.getSelectionPath();
        if (path == null)
        {
            return null;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object value = node.getUserObject();
        if (value instanceof UserComponent)
        {
            return (UserComponent) value;
        }
        return null;
    }

    /**
     * rebuilds the Swing tree from the service's Composite root
     */
    private void refreshTree()
    {
        DefaultMutableTreeNode rootNode = buildTreeNode(service.getRoot());
        treeModel = new DefaultTreeModel(rootNode);
        tree.setModel(treeModel);
        expandAllRows();
    }

    /**
     * converts a UserComponent tree branch into a Swing tree node branch.
     *
     * @param component component to convert
     * @return Swing tree node representing the component and its children
     */
    private DefaultMutableTreeNode buildTreeNode(UserComponent component)
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(component);
        if (component instanceof UserGroup)
        {
            UserGroup group = (UserGroup) component;
            for (UserComponent child : group.getChildren())
            {
                node.add(buildTreeNode(child));
            }
        }
        return node;
    }

    /**
     * expands every row so newly added users and groups are immediately visible
     */
    private void expandAllRows()
    {
        for (int i = 0; i < tree.getRowCount(); i++)
        {
            tree.expandRow(i);
        }
    }

    /**
     * displays the total number of users calculated by StatsVisitor
     */
    private void showUserTotal()
    {
        StatsVisitor stats = service.collectStats();
        showMessage("Total users: " + stats.getUserCount());
    }

    /**
     * displays the total number of groups calculated by StatsVisitor
     */
    private void showGroupTotal()
    {
        StatsVisitor stats = service.collectStats();
        showMessage("Total groups: " + stats.getGroupCount());
    }

    /**
     * displays the total number of posted tweets calculated by StatsVisitor
     */
    private void showMessageTotal()
    {
        StatsVisitor stats = service.collectStats();
        showMessage("Total tweets: " + stats.getTweetCount());
    }

    /**
     * displays the percentage of tweets that contain positive words
     */
    private void showPositivePercentage()
    {
        StatsVisitor stats = service.collectStats();
        showMessage(String.format("Positive tweet percentage: %.2f%%", stats.getPositivePercentage()));
    }

    /**
     * shows a simple Swing message dialog
     *
     * @param message text to display
     */
    private void showMessage(String message)
    {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * starts the GUI on the Swing event dispatch thread
     */
    public static void launch()
    {
        SwingUtilities.invokeLater(() -> AdminControlPanel.getInstance().setVisible(true));
    }
}