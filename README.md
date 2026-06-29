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