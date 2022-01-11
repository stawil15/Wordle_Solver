# Wordle_Solver
Solves the Wordle

By default, words are weighted by each letter's concentration in each position of the word. 
So while T's are a popular letter of the alphabet, by default "CARES" is a better first play 
than "TARES" because 'C' is more common in the first position of all words than 'T'.

popularity and weighting of words was determined based on only Wordle's word bank. This is
contrary to common advice found online that considers popularity of letters througout all
English words. Weighting is also adjusted for words with repeat letters so that words like 
"Eerie" aren't inflated in value. There are two different weight values for each word. By
default they're waited by letter position but you can also forgo this and weight by overal
letter popularity within the word bank. You can also sort by the sum of both values which
solved the puzzle a whole guess quicker on the day of me writing this. Might be a fluke.
