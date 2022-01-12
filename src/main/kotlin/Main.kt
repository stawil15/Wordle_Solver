import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*

class Main {
    companion object {
        private val scanner = Scanner(System.`in`)
        private var wordBank: List<Word> = emptyList()
        private var guess: String = ""
        private var attempt = 1

        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello Wordle!")
            println()
            println("valid inputs:")
            println("'n' = not in word")
            println("'y' = Yellow, in word but wrong position")
            println("'g' = Green, correct position")
            println("other characters will be interpreted as 'n'")
            println()

            try {
                wordBank = readData()
            } catch (e: Exception) {
                println("ERROR: ${e.message}")
            }

            while (wordBank.size > 1) {
                println("${wordBank.size} possible words")
                makeGuess()
                filter(receiveGuess())
                attempt += 1
            }
            if (wordBank.size == 1) {
                println("Final guess: ${wordBank.first().word.uppercase()}")
            }
        }

        private fun makeGuess(index: Int = 0) {
            if (attempt == 1) {
                guess = "unlay"
            }
            else if (attempt == 2) {
                guess = "forks"
            }
            else if(attempt == 3){
                guess = "imped"
            }
            else {
                guess = wordBank.sortedByDescending { it.letter_weight }[index].word
            }
            println("Guess #$attempt: ${guess.uppercase()}")
        }

        private fun filter(response: List<State>) {
            response.forEachIndexed { index, answer ->
                wordBank = when (answer) {
                    State.G -> {
                        wordBank.filter { it.word[index] == guess[index] }
                    }
                    State.Y -> {
                        wordBank.filter { it.word[index] != guess[index] && it.word.contains(guess[index]) }
                    }
                    State.N -> {
                        eliminate(guess[index], response)
                    }
                }
            }
        }

        //Handle words with multiples of the same letter
        private fun eliminate(char: Char, response: List<State>): List<Word> {
            var count = 0
            response.forEachIndexed { index, answer ->
                if (guess[index] == char && answer != State.N) {
                    count++
                }
            }
            return if (count == 0) {
                wordBank.filterNot { it.word.contains(char, true) }
            } else {
                wordBank.filterNot { it.word.count { it == char } > count }
            }
        }

        private fun receiveGuess(): List<State> {
            var skips = 0
            while (true) {
                val input = scanner.nextLine().lowercase()
                if (input.length >= 5) {
                    return listOf(
                        getAnswer(input[0]),
                        getAnswer(input[1]),
                        getAnswer(input[2]),
                        getAnswer(input[3]),
                        getAnswer(input[4])
                    )
                } else if (input == "skip") {
                    //for testing on http://foldr.moe/hello-wordl/#
                    //skipping is necessary because it has a smaller word bank
                    skips += 1
                    makeGuess(skips)
                }
            }
        }

        private fun getAnswer(char: Char): State {
            return when (char) {
                'y' -> State.Y
                'g' -> State.G
                else -> State.N
            }
        }

        @Throws(IOException::class)
        fun readData(): ArrayList<Word> {
            val file = File("words_db_sum.csv")
            val content: ArrayList<Word> = ArrayList()
            try {
                BufferedReader(FileReader(file)).forEachLine {
                    it.split(",").apply {
                        content.add(
                            Word(
                                this[0],
                                this[6].toInt(),
                                this[7].toInt(),
                                this[8].toInt()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                println("ERROR: ${e.message}")
                e.stackTrace.forEach {
                    println(it)
                }
            }
            return content
        }
    }
}
