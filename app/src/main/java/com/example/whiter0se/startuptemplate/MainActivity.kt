package com.example.whiter0se.startuptemplate

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.Intent



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun buClick(view:View) {
        val currentButton:Button = view as Button
        var cellID = 0
        when(currentButton.id) {
            R.id.btn1->cellID=1
            R.id.btn2->cellID=2
            R.id.btn3->cellID=3
            R.id.btn4->cellID=4
            R.id.btn5->cellID=5
            R.id.btn6->cellID=6
            R.id.btn7->cellID=7
            R.id.btn8->cellID=8
            R.id.btn9->cellID=9
        }
        playGame(cellID, currentButton)
        //Toast.makeText(this, "ID: " + cellID, Toast.LENGTH_LONG).show()
    }

    var player1Area = ArrayList<Int>()
    var player2Area = ArrayList<Int>()
    var winner = -1
    var turnToPlay = 1
    var gameOver = false
    var gameDrawn = false

    fun newGame(view:View) {
        val i = baseContext.packageManager
                .getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }
    fun playGame(cellID: Int, currentButton: Button) {

        if(turnToPlay == 1) {
            if(isMovesLeft()) {
                player1Area.add(cellID)
                currentButton.setTextColor(Color.parseColor("#ff79c6"))
                currentButton.text = "X"
                turnToPlay = 2
                calculateWinner()
                if(isMovesLeft()) {
                    if (!gameOver) {
                        //Computer makes move if game is not over
                        autoPlay()
                    }
                } else {
                    Toast.makeText(this, "Game Drawn", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Game Drawn", Toast.LENGTH_LONG).show()
            }
        } else {
            player2Area.add(cellID)
            currentButton.setTextColor(Color.parseColor("#66d9ef"))
            currentButton.text = "O"
            turnToPlay = 1
            calculateWinner()
        }
        currentButton.isEnabled = false
    }

    fun autoPlay() {
        val cellID = findBestMove()
        if(cellID == -1) {
            Toast.makeText(this, "Game Drawn", Toast.LENGTH_LONG).show()
        }
        var currentButton:Button=btn1
        when(cellID) {
            1 -> currentButton = btn1
            2 -> currentButton = btn2
            3 -> currentButton = btn3
            4 -> currentButton = btn4
            5 -> currentButton = btn5
            6 -> currentButton = btn6
            7 -> currentButton = btn7
            8 -> currentButton = btn8
            9 -> currentButton = btn9
        }

        playGame(cellID, currentButton)
    }

    //This function evaluates the position on the board. The computer is player2
    // and the user is player 1. If player 1 is missing then score is -10 and
    //if computer is winning then the score is +10
    fun evaluate(): Int {
        if ((player1Area.contains(1) && player1Area.contains(2) && player1Area.contains(3))
                || (player1Area.contains(4) && player1Area.contains(5) && player1Area.contains(6))
                || (player1Area.contains(7) && player1Area.contains(8) && player1Area.contains(9))
                || (player1Area.contains(1) && player1Area.contains(4) && player1Area.contains(7))
                || (player1Area.contains(2) && player1Area.contains(5) && player1Area.contains(8))
                || (player1Area.contains(3) && player1Area.contains(6) && player1Area.contains(9))
                || (player1Area.contains(1) && player1Area.contains(5) && player1Area.contains(9))
                || (player1Area.contains(3) && player1Area.contains(5) && player1Area.contains(7))) {
            return -10
        } else if ((player2Area.contains(1) && player2Area.contains(2) && player2Area.contains(3))
                || (player2Area.contains(4) && player2Area.contains(5) && player2Area.contains(6))
                || (player2Area.contains(7) && player2Area.contains(8) && player2Area.contains(9))
                || (player2Area.contains(1) && player2Area.contains(4) && player2Area.contains(7))
                || (player2Area.contains(2) && player2Area.contains(5) && player2Area.contains(8))
                || (player2Area.contains(3) && player2Area.contains(6) && player2Area.contains(9))
                || (player2Area.contains(1) && player2Area.contains(5) && player2Area.contains(9))
                || (player2Area.contains(3) && player2Area.contains(5) && player2Area.contains(7))) {
            return 10
        } else {
            return 0
        }
    }

    fun max(a: Int, b: Int): Int {
        if (a > b)
            return a
        else
            return b
    }

    fun min(a: Int, b: Int): Int {
        if (a < b)
            return a
        else
            return b
    }

    fun isEmpty(cellID: Int): Boolean {
        if(!(player1Area.contains(cellID) || player2Area.contains(cellID))) {
            return true
        }
        return false
    }

    fun isMovesLeft(): Boolean {
        for(cellID in 1..9) {
            if(isEmpty(cellID)) {
                return true
            }
        }
        return false
    }

    fun minmax(depth: Int, isMax: Boolean): Int {
        val score = evaluate()

        if(score == 10) {
            return score
        }
        if(score == -10) {
            return score
        }
        if(isMovesLeft() == false) {
            return 0
        }
        if(isMax) {

            //Maximizer is player 2
            var best = -1000
            for(cellID in 1..9) {
                if(isEmpty(cellID)) {

                    //Make move
                    player2Area.add(cellID)

                    //Evaluate best
                    best = max(best, minmax(depth+1, !isMax))

                    //Undo the move
                    player2Area.remove(cellID)

                }
            }
            return best
        } else {
            // Minimizer is player 1
            var best = 1000
            for(cellID in 1..9) {
                if(isEmpty(cellID)) {

                    //Make move
                    player1Area.add(cellID)

                    //Evaluate best
                    best = min(best, minmax(depth+1, !isMax))

                    //Undo the move
                    player1Area.remove(cellID)

                }
            }
            return best
        }
    }

    fun findBestMove(): Int {
        var bestVal = -1000
        var bestMove:Int=-1

        if(isMovesLeft() == false) {
            //This is becaues we have initialized bestMove to -1 so it shouldn't proceed
            return -1
        }

        for(cellID in 1..9) {
            if(isEmpty(cellID)) {

                //Make move
                player2Area.add(cellID)

                //Find move value
                val moveVal = minmax(0, false)

                //Undo move
                player2Area.remove(cellID)

                if(moveVal > bestVal) {
                    bestVal = moveVal
                    bestMove = cellID
                }
            }
        }
        return bestMove
    }

    fun disableButtons() {
        btn1.isEnabled = false
        btn2.isEnabled = false
        btn3.isEnabled = false
        btn4.isEnabled = false
        btn5.isEnabled = false
        btn6.isEnabled = false
        btn7.isEnabled = false
        btn8.isEnabled = false
        btn9.isEnabled = false
    }

    fun calculateWinner() {
        if ((player1Area.contains(1) && player1Area.contains(2) && player1Area.contains(3))
            || (player1Area.contains(4) && player1Area.contains(5) && player1Area.contains(6))
            || (player1Area.contains(7) && player1Area.contains(8) && player1Area.contains(9))
            || (player1Area.contains(1) && player1Area.contains(4) && player1Area.contains(7))
            || (player1Area.contains(2) && player1Area.contains(5) && player1Area.contains(8))
            || (player1Area.contains(3) && player1Area.contains(6) && player1Area.contains(9))
            || (player1Area.contains(1) && player1Area.contains(5) && player1Area.contains(9))
            || (player1Area.contains(3) && player1Area.contains(5) && player1Area.contains(7))) {
            winner = 1
        } else if ((player2Area.contains(1) && player2Area.contains(2) && player2Area.contains(3))
                || (player2Area.contains(4) && player2Area.contains(5) && player2Area.contains(6))
                || (player2Area.contains(7) && player2Area.contains(8) && player2Area.contains(9))
                || (player2Area.contains(1) && player2Area.contains(4) && player2Area.contains(7))
                || (player2Area.contains(2) && player2Area.contains(5) && player2Area.contains(8))
                || (player2Area.contains(3) && player2Area.contains(6) && player2Area.contains(9))
                || (player2Area.contains(1) && player2Area.contains(5) && player2Area.contains(9))
                || (player2Area.contains(3) && player2Area.contains(5) && player2Area.contains(7))) {
            winner = 2
        }
        if (winner == 1) {
            Toast.makeText(this, "Player 1 wins the game", Toast.LENGTH_LONG).show()
            gameOver = true
            disableButtons()
        } else if (winner == 2){
            Toast.makeText(this, "Player 2 wins the game", Toast.LENGTH_LONG).show()
            gameOver = true
            disableButtons()
        }
    }
}
