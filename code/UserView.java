package code;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * window that displays one user's Mini Twitter account
 */
public class UserView extends JFrame
{
    // shared service used to find users and post tweets
    private final MiniTwitterService service;

    // user represented by this window
    private final User user;

    // text field where the user enters another user's ID to follow
    private final JTextField followUserIdField;

    // text field where the user writes a tweet
    private final JTextField tweetField;

    // swing list model that stores IDs of followed users
    private final DefaultListModel<String> followingModel;

    // swing list model that stores news feed messages
    private final DefaultListModel<String> newsFeedModel;

    // listener registered with User so this window refreshes after updates
    private final Runnable refreshListener;

    /**
     * creates a user view for a specific user
     *
     * @param user user whose data should be displayed in this window
     */
    public UserView(User user)
    {
        super("User View - " + user.getId());
        this.service = MiniTwitterService.getInstance();
        this.user = user;
        this.followUserIdField = new JTextField();
        this.tweetField = new JTextField();
        this.followingModel = new DefaultListModel<>();
        this.newsFeedModel = new DefaultListModel<>();
        this.refreshListener = () -> SwingUtilities.invokeLater(this::refreshLists);

        user.addViewListener(refreshListener);
        buildLayout();
        refreshLists();

        setSize(480, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * builds the Swing layout and connects buttons to their actions
     */
    private void buildLayout()
    {
        setLayout(new BorderLayout(8, 8));

        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        add(mainPanel, BorderLayout.CENTER);

        JPanel followPanel = new JPanel(new BorderLayout(6, 6));
        JButton followButton = new JButton("Follow User");
        followUserIdField.setBorder(BorderFactory.createTitledBorder("User Id"));
        followPanel.add(followUserIdField, BorderLayout.CENTER);
        followPanel.add(followButton, BorderLayout.EAST);
        mainPanel.add(followPanel);

        JList<String> followingList = new JList<>(followingModel);
        JScrollPane followingPane = new JScrollPane(followingList);
        followingPane.setBorder(BorderFactory.createTitledBorder("Current Following"));
        mainPanel.add(followingPane);

        JPanel tweetPanel = new JPanel(new BorderLayout(6, 6));
        JButton postButton = new JButton("Post Tweet");
        tweetField.setBorder(BorderFactory.createTitledBorder("Tweet Message"));
        tweetPanel.add(tweetField, BorderLayout.CENTER);
        tweetPanel.add(postButton, BorderLayout.EAST);
        mainPanel.add(tweetPanel);

        JList<String> newsFeedList = new JList<>(newsFeedModel);
        JScrollPane newsFeedPane = new JScrollPane(newsFeedList);
        newsFeedPane.setBorder(BorderFactory.createTitledBorder("News Feed"));
        mainPanel.add(newsFeedPane);

        followButton.addActionListener(e -> followUser());
        postButton.addActionListener(e -> postTweet());
    }

    /**
     * attempts to follow the user ID entered in the follow text field
     */
    private void followUser()
    {
        String targetId = followUserIdField.getText().trim();
        boolean followed = service.followUser(user.getId(), targetId);
        if (!followed)
        {
            showMessage("Could not follow user. Check that the user exists and is not already followed.");
            return;
        }

        followUserIdField.setText("");
        refreshLists();
    }

    /**
     * posts the message entered in the tweet text field
     */
    private void postTweet()
    {
        String message = tweetField.getText().trim();
        if (message.isEmpty())
        {
            showMessage("Please enter a tweet message.");
            return;
        }

        service.postTweet(user.getId(), message);
        tweetField.setText("");
        refreshLists();
    }

    /**
     * rebuilds the following list and news feed list from the User model
     */
    private void refreshLists()
    {
        followingModel.clear();
        for (User followedUser : user.getFollowing())
        {
            followingModel.addElement(followedUser.getId());
        }

        newsFeedModel.clear();
        for (String message : user.getNewsFeed())
        {
            newsFeedModel.addElement(message);
        }
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
     * removes this window's refresh listener before closing the window
     */
    @Override
    public void dispose()
    {
        user.removeViewListener(refreshListener);
        super.dispose();
    }
}