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

            var guess = 1
            while (wordBank.size > 1) {
                println("${wordBank.size} possible words")
                println("Guess #$guess: ${makeGuess()}")

                filter(receiveGuess())
                guess += 1
            }
            if (wordBank.size == 1) {
                println("Final guess: ${makeGuess()}")
            }
        }

        private fun makeGuess(): String {
            guess = wordBank.sortedByDescending { it.position_weight }.first().word
            return guess
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

        private fun eliminate(char: Char, response: List<State>): List<Word> {
            var count = 0
            response.forEachIndexed { index, answer ->
                if (guess[index] == char && answer != State.N) {
                    count++
                }
            }
            return if (count == 0) {
                wordBank.filterNot { it.a == char || it.b == char || it.c == char || it.d == char || it.e == char }
            } else {
                wordBank.filterNot { it.word.count { it == char } > count }
            }
        }

        private fun receiveGuess(): List<State> {
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
                                this[1][0],
                                this[2][0],
                                this[3][0],
                                this[4][0],
                                this[5][0],
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
