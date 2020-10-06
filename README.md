# amink-mtg-app

A small app that simulates a growing collection of Magic: The Gathering cards. I made it help me play Magic with my friends.

I love MTG but I don't like spending money or doing card research. I like the early stage of the game when a small group of friends gets their starter sets and then slowly builds up card collections over time, making decks with whatever cards they have available instead of looking up optimal decks. The Draftsim app simulates sealed drafts, which is close to what I want, but they don't support all the card sets and they aren't for building persistent collections. All card data is available through the MTG API, so I decided to make an app that supports arbitrarily sized collections from arbitrary sets.

Each user begins with an empty card collection. Cards can be added to the collection by adding virtual booster packs. For example, a group might begin by giving themselves 12 booster packs worth of cards from the Revised edition. These virtual boosters have the same distribution as real packs (i.e. 10 commons, 3 uncommons, 1 rare each), so that the relative rarity of each card is simulated.

I'm currently hosting the app on Heroku. If I get around to adding authentication and refactoring the data model to be more scalable, I might make it a real app for public use.
