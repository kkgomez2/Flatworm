# Flatworm

This is a Java Applet that runs a simple fighting game project that I've called "Flatworm"

1. Within src: 

	* Gameloop.java is the actual application.

	* Flatworm.java represents the Flatworm objects.

	* NameParser.java parses text files for names used by the game loop and the Flatworm objects. (The text files parsed come from the Unites States Census Bureau's website: http://www.census.gov/topics/population/genealogy/data/1990_census/1990_census_namefiles.html)

	* FlatwormTest.java and NameParserTest.java serve as test classes for the respective namesake classes.

	* The other java files serve as classes which I used to test certain functionalities (Hashing and Text Fields).

2. Within src/img:

	* Simple illustrations that I made for the game.

3. Within src/txt:

	* The Census text files which I described above.

----

## Here is my original proposal:

### BACKGROUND

"The flatworms, or Platyhelminthes, Plathelminthes, or platyhelminths (from the Greek πλατύ, platy, meaning "flat" and ἕλμινς (root: ἑλμινθ-), helminth-, meaning "worm")[2] are a phylum of relatively simple bilaterian, unsegmented, soft-bodied invertebrates. " -- Wikipedia entry for Flatworm

Flatworms are hermaphoditic creatures, meaning they have both male and female reproductive organs.

From a National Geographic video description, "For flatworms, "Who's your daddy?" is a loaded question. In a bizarre bout lasting up to an hour, the first flatworm to stab and inseminate its mate becomes the father."

"The flatworms "fence" using extendable two-headed dagger-like stylets. These stylets are pointed (and in some species hooked) in order to pierce their mate's epidermis and inject sperm into the haemocoel in an act known as intradermal hypodermic insemination, or traumatic insemination. Pairs can either compete, with only one individual transferring sperm to the other, or the pair can transfer sperm bilaterally. Both forms of sperm transfer can occur in the same species, depending on various factors.[2]
[...]
One organism will inseminate the other, with the inseminating individual acting as the "father." The sperm is absorbed through pores or sometimes wounds in the skin from the partner's stylet, causing fertilization in the second, who becomes the "mother."[3][4][5] The battle may last for up to an hour in some species.[6]" -- Wikipedia entry for Penis fencing

### PROJECT

Since the above was quite verbose, I'll try to keep the rest in very short terms. I found this sort of behavior rather humorous, and an idea of a simple two dimensional fighting game with very simple graphics, with rock-paper-scissors-esque logic (local players have a real-time selection of high-mid-low attacks that result in either a hit, block, or miss) in which the loser is respectfully made into the mother and the winner becomes the father.

One more aspect to it would allow each respective player a random or user generated surname. If a rematch is requested, then the losing player's flatworm would come back with the winning player's surname hyphenated onto the end of their own. The winning flatworm is replaced with a new user queried surname or generated from a random list of surnames, ex. Flatworm Johnson vs. Flatworm Michaels -(P1 win)-> Flatworm Simpson vs. Flatworm Michaels-Johnson -(P2 win)-> Flatworm Simpson-Michaels-Johnson vs. Flatworm McGill and so on.