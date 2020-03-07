# cs0320 Term Project 2020

**Team Members:** Nasheath Ahmed, Andrew Boden, Henry Lucco, Jake Zhang

**Team Strengths and Weaknesses:** 
- Strengths: Graphics & GUI, Algorithms & Datastructures, Class Heirarchy/Organization, Databases
- Weaknesses: AI, Performance/Optimization

**Project Idea(s):** _Fill this in with three unique ideas! (Due by March 2)_
### Idea 1: Farming Simulator

TA Review: Approved! Looks good now, if you don't want to do both map generation and AI you do not have to.

------------------------------------------------------------------------------
There is a finite two dimensional space and every player can pick a plot of land in this space and grow crops for profit. After a certain amount of time, you can harvest the crops and make a profit (Think Stardew Valley). However, there is a catch: if your friend(s) gets to your crops before you, they can steal a portion of your crops and profit. With the money you can make, you can buy more things that increase profit (such as upgrading your farm to not only sell crops but also handle livestock), cosmetic upgrades, or more fun cosmetic items. After development is finished we can add a virtually infinite amount of crops and animals. 

#### Features:
-	A simple (or complex if we have time) farming system where users can raise their crops from planting to harvesting and raise animals for greater profit
-	This one is pretty self explanatory, players plant crops and water them, over time the crops grow, the amount of time (real life time) based on the soil and fertilizer given. When the crops have grown they can be harvested and sold for profit. Higher level crops generate more profit. 
-	Players do not have to stay online for their crops to grow. 
-	A bonus feature here could be email notifications when a player’s crops are ready for harvest.

-	The ability to steal from friends, with restrictions: e.g. cooldown after each steal, have to leave a minimum amount for the owner, etc.
-	Players can go through a list of all other players in their world and choose a player to steal from. Within the stealing system players can buy different upgrades or powerups in order to make stealing easier or harder. After a player has been raided their money will have been reduced to the minimum and parts of their farm may have been destroyed. Players will also be able to purchase upgrades such as a coyo that will help them defend their farm against theft.

-	Some reward system for users to spend their hard earned money on (maybe in-game real estate or even real world charities if we can find partners)

-	Account system, duh.

-	Research system
-	Players can “accidentally” stumble upon or intentionally look for newer technologies that can greatly enhance their farming experience. For example, someone who has grown a lot of tomatoes might be able to find a new variation of their current tomatoes that yield higher profit by cross breeding.
-	Players can complete/speed up their research by simply staying online, interacting with others, or playing minigames. 
-	Research progress can also be (partially) stolen. And this is what we call a big grief. 

-	Economy system
-	Players can have the options to trade with each other and the price of each type or product may fluctuate 
-	There is only a finite amount of land and players have the option to expand their farm if they have enough money, which means the price of land will also become an interesting factor
-	In addition, we want to set points of interest on the map (river where players can get higher quality water and etc.), so the location of a player’s farm matters too, which means players can compete for land/locations that they desire. 

-	Friend system. This feature is critical since the focus of this game is to bring friends together and allow people to interact with each other as much/as little as they want
	- Friend network to discover more friends who play this game
- Leave an anonymous note for your friend after stealing
- Help your friends build their farm, water their plants, feed their animals, etc.

-	Good graphics and animations cuz it's no longer the late 2000s

#### Algorithms (UPDATED)
Obviously not all of these features need to be implemented, but the more the better.
- Random map/terrain generation
    - an algorithm that can generate points of interest on the map randomly (rivers, resources, hills and etc.) based on given inputs (number of points, type of terrain, etc.).
    - allows players who want to play as a group to have their own unique map/landscape.
- AI players
    - algorithms that sporadically "spawn" AI players to own land and build their own farms.
    - AI players have to behave like real players and make decisions on how to develop their farms based on given conditions (how much resource they have, what stage of research they are in, and etc.).
    - AI players can propose or accept trades with real players (potential exploits here, AI players have to be able to recognize good trades and bad trades).
- Random events
    - algorithms that randomly generate worldwide events for players to take advantage of or give players unique, time-sensitive challenges.
    - (potential update feature) event algorithms analyze player data and decide what percentage of palyers might benefit from a certain event to avoid sudden overflow of wealth or generating events that nobody wants to participate in.
- Trade center
    - algorithm that analyze the amount of goods being traded and decide market value based on supply and demand.
    - sorting algorithm that makes it easier for players to find the items they want at the price they want.

#### Potential Challenges:
Creating one unified instance in which all players data is saved with be the biggest challenge of this project. Especially as the number of players grow, the server must be able to expand and hold all of these players in the same world. Thus the worldspace in which the farms exist must be able to handle players deleting their accounts, players adding new accounts, players taking over other players farms, and other player related features that we may implement. In addition to this, we must find a way to solve the problem that in order for people to play the server must always be running thus we will need to find a way to host the server somewhere that is not one of our personal laptops. Finally, we must implement an account system that keeps track of each player and their data and so we will need a database that contains each player's login information and the details of their account and what they have in the game. This will be difficult as none of us are particularly skilled at writing databases but we are excited to learn and believe that we are up to the challenge of creating this database. There will also be some challenges with player interaction for example, we need to be able to facilitate updating in real time for example if a player is playing and gets raided they need to see that it is happening or be able to catch it and have a way to stop it.


**Note:** You must submit updated ideas for your term project. Also please remove the word document and put the information in this README file.

**Mentor TA:** _Put your mentor TA's name and email here once you're assigned one!_

## Meetings
_On your first meeting with your mentor TA, you should plan dates for at least the following meetings:_

**Specs, Mockup, and Design Meeting:** _(Schedule for on or before March 13)_

**4-Way Checkpoint:** _(Schedule for on or before April 23)_

**Adversary Checkpoint:** _(Schedule for on or before April 29 once you are assigned an adversary TA)_

## How to Build and Run
_A necessary part of any README!_
