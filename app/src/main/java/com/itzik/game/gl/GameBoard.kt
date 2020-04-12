package com.itzik.game.gl

import android.content.Context
import android.graphics.Point
import com.itzik.game.models.Bullet
import com.itzik.game.models.DefenceBrick
import com.itzik.game.models.Invader
import com.itzik.game.models.PlayerShip

class GameBoard(context: Context, size: Point) {


    private var mContext = context

    private val sizeDisplay = size

    // A boolean which we will set and unset
    private var playing = false

    // Game is paused at the start
    private var paused = true

    // The players ship
    private var playerShip: PlayerShip = PlayerShip(mContext, sizeDisplay.x, sizeDisplay.y)

    // Some Invaders
    private val invaders = ArrayList<Invader>()
    private var numInvaders = 0

    // The player's shelters are built from bricks
    private val bricks = ArrayList<DefenceBrick>()
    private var numBricks: Int = 0


    // The player's playerBullet
    // much faster and half the length
    // compared to invader's bullet
    private var playerBullet = Bullet(size.y, 1200f, 40f)


    // The invaders bullets
    private val invadersBullets = ArrayList<Bullet>()
    private var nextBullet = 0
    private val maxInvaderBullets = 10

    // The score
    var score = 0

    // The wave number
    var waves = 1

    // Lives
    var lives = 3

    var highScore = 0

    // How menacing should the sound be?
    private var menaceInterval: Long = 1000

    // Which menace sound should play next
    var uhOrOh: Boolean = false
    // When did we last play a menacing sound
    private var lastMenaceTime = System.currentTimeMillis()



    fun getInvenvadersBullets(): List<Bullet>{
        return invadersBullets
    }


    fun getPlayerBullet(): Bullet{
        return playerBullet
    }

    fun getBricks(): List<DefenceBrick>{
        return bricks
    }


    fun getInvaders(): List<Invader>{
        return invaders
    }


    fun getPlayerShip(): PlayerShip{
        return playerShip
    }

    fun prepareLevel() {
        // Here we will initialize the game objects

        Invader.numberOfInvaders = 0
        numInvaders = 0
        for (column in 0..10) {
            for (row in 0..5) {
                invaders.add(Invader(mContext,
                    row,
                    column,
                    sizeDisplay.x,
                    sizeDisplay.y))

                numInvaders++
            }
        }

        // Build the shelters
        numBricks = 0
        for (shelterNumber in 0..4) {
            for (column in 0..18) {
                for (row in 0..8) {
                    bricks.add(DefenceBrick(row,
                        column,
                        shelterNumber,
                        sizeDisplay.x,
                        sizeDisplay.y))

                    numBricks++
                }
            }
        }

        // Initialize the invadersBullets array
        for (i in 0 until maxInvaderBullets) {
            invadersBullets.add(Bullet(sizeDisplay.y))
        }
    }

    fun checkIsNeedUpdate(fps: Long){

        if(paused){
            return
        }

        // Update the state of all the game objects

        // Move the player's ship
        playerShip.update(fps)

        // Did an invader bump into the side of the screen
        var bumped = false

        // Has the player lost
        var lost = false

        // Update all the invaders if visible
        for (invader in invaders) {

            if (invader.isVisible) {
                // Move the next invader
                invader.update(fps)

                // If that move caused them to bump
                // the screen change bumped to true
                if (invader.position.left > sizeDisplay.x - invader.width
                    || invader.position.left < 0) {

                    bumped = true

                }
            }
        }

        if (playerBullet.isActive) {
            playerBullet.update(fps)
        }

        // Update all the invaders bullets if active
        for (bullet in invadersBullets) {
            if (bullet.isActive) {
                bullet.update(fps)
            }
        }


        // Did an invader bump into the edge of the screen
        if (bumped) {

            // Move all the invaders down and change direction
            for (invader in invaders) {
                invader.dropDownAndReverse(waves)
                // Have the invaders landed
                if (invader.position.bottom >= sizeDisplay.y && invader.isVisible) {
                    lost = true
                }
            }
        }
    }


    fun isPlaying(): Boolean{
        return playing
    }

    fun setPause(pause: Boolean){
        paused = pause
    }

    fun setPlaying(play: Boolean){
        playing = play
    }
}