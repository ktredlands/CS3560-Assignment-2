Mini Twitter
==================

What the Program Does
---------------------
- Add users
- Add user groups
- Open a user view
- Follow another user
- Post tweets
- View a user's news feed
- Show total users
- Show total groups
- Show total tweets
- Show positive tweet percentage
- Validate IDs for duplicate IDs and spaces
- Show a user's creation time in the User View
- Show a user's last update time in the User View
- Update last update time when a user posts or receives a tweet
- Show the last updated user from the Admin Control Panel

Design Patterns Used
--------------------
Singleton:
MiniTwitterService and AdminControlPanel each have one shared instance.

Composite:
User and UserGroup both use UserComponent. A UserGroup can contain users and other groups.

Observer:
When a user posts a tweet, all followers are updated automatically.

Visitor:
StatsVisitor counts users, groups, tweets, and positive tweets.