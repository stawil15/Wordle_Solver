import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*

class Main {
    companion object {
        private val scanner = Scanner(System.`in`)
        var wordBank: List<Word> = emptyList()

        var one: Char? = null
        var two: Char? = null
        var three: Char? = null
        var four: Char? = null
        var five: Char? = null

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

            println("Guess #1: ${makeGuess()}")

            filter(recieveGuess())

            println("Guess #2: ${makeGuess()}")

            filter(recieveGuess())

            println("Guess #3: ${makeGuess()}")

            filter(recieveGuess())

            println("Guess #4: ${makeGuess()}")

            filter(recieveGuess())

            println("Guess #5: ${makeGuess()}")

            filter(recieveGuess())

            println("Guess #6: ${makeGuess()}")

        }

        private fun makeGuess(): String {
            val guess = wordBank.sortedByDescending { it.position_weight }.first().word
            one = guess[0]
            two = guess[1]
            three = guess[2]
            four = guess[3]
            five = guess[4]
            return guess
        }

        private fun filter(response: Response) {
            wordBank = when (response.one) {
                Answer.G -> {
                    wordBank.filter { it.a == one }
                }
                Answer.Y -> {
                    wordBank.filter { it.a != one && it.word.contains(one!!) }
                }
                Answer.N -> {
                    eliminate(one!!, response)
                }
            }
            wordBank = when (response.two) {
                Answer.G -> {
                    wordBank.filter { it.b == two }
                }
                Answer.Y -> {
                    wordBank.filter { it.b != two && it.word.contains(two!!) }
                }
                Answer.N -> {
                    eliminate(two!!, response)
                }
            }
            wordBank = when (response.three) {
                Answer.G -> {
                    wordBank.filter { it.c == three }
                }
                Answer.Y -> {
                    wordBank.filter { it.c != three && it.word.contains(three!!) }
                }
                Answer.N -> {
                    eliminate(three!!, response)
                }
            }
            wordBank = when (response.four) {
                Answer.G -> {
                    wordBank.filter { it.d == four }
                }
                Answer.Y -> {
                    wordBank.filter { it.d != four && it.word.contains(four!!) }
                }
                Answer.N -> {
                    eliminate(four!!, response)
                }
            }
            wordBank = when (response.five) {
                Answer.G -> {
                    wordBank.filter { it.e == five }
                }
                Answer.Y -> {
                    wordBank.filter { it.e != five && it.word.contains(five!!) }
                }
                Answer.N -> {
                    eliminate(five!!, response)
                }
            }
        }

        private fun eliminate(char: Char, response: Response): List<Word>{
            var count = 0
            if(one == char && response.one != Answer.N){
                count++
            }
            if (two == char && response.two != Answer.N){
                count++
            }
            if (three == char && response.three != Answer.N){
                count++
            }
            if (four == char && response.four != Answer.N){
                count++
            }
            if (five == char && response.five != Answer.N){
                count++
            }
            return if(count == 0){
                println("removing all letter $char")
                wordBank.filterNot { it.a == char || it.b == char || it.c == char || it.d == char || it.e == char }
            }else{
                println("removing letter $char past $count")
                wordBank.filterNot { it.word.count { it == char } > count }
            }
        }

        fun recieveGuess(): Response {
            var response: Response? = null

            while (response == null) {
                val input = scanner.nextLine().lowercase()
                if (input.length >= 5) {
                    response = Response(
                        getAnswer(input[0]),
                        getAnswer(input[1]),
                        getAnswer(input[2]),
                        getAnswer(input[3]),
                        getAnswer(input[4])
                    )
                }
            }
            return response
        }

        private fun getAnswer(char: Char): Answer {
            return when (char) {
                'y' -> Answer.Y
                'g' -> Answer.G
                else -> Answer.N
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