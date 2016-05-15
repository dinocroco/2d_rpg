# 2d_rpg
A multiplayer game project for UT OOP course.

This is a super fun multiplayer roleplaying game. It sets in an awesome two-dimensional world with diggable walls.
It has colourful units wandering around for players to defeat. Beware! When three of them combine then a new, more powerful unit is created.
Fight with them and other players to gain experience points and increase your level.

Server has to be running before clients connect to it. Game will automatically save players' progress.

For setting up your player name, password and server IP edit file "clientData.txt" as follows:
*First line - your player name
*Second line - your password. Server will use combination of name and password to check if there is a saved player with that information. (This information will be shared with all players).
*Third line - server IP.

Example:
MyPlayerName
MyPlaintextPassword
127.0.0.1

Game controls:
Arrow keys - movement
Z - freezes unit
Space - attack unit or player
Shift + arrow keys - digging

Have a great time playing our game!
