package com.itzik.game.gl

import android.content.Context
import android.graphics.Point
import android.graphics.RectF
import com.itzik.game.models.Bullet
import com.itzik.game.models.DefenceBrick
import com.itzik.game.models.Invader
import com.itzik.game.models.PlayerShip
import com.itzik.game.stream.SoundPlayer

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
    var startFrameTime: Long? = null


    fun getInvenvadersBullets(): List<Bullet> {
        return invadersBullets
    }


    fun getPlayerBullet(): Bullet {
        return playerBullet
    }

    fun getBricks(): List<DefenceBrick> {
        return bricks
    }


    fun getInvaders(): List<Invader> {
        return invaders
    }


    fun getPlayerShip(): PlayerShip {
        return playerShip
    }

    fun prepareLevel() {
        // Here we will initialize the game objects

        Invader.numberOfInvaders = 0
        numInvaders = 0
        for (column in 0..10) {
            for (row in 0..5) {
                invaders.add(
                    Invader(
                        mContext,
                        row,
                        column,
                        sizeDisplay.x,
                        sizeDisplay.y
                    )
                )

                numInvaders++
            }
        }

        // Build the shelters
        numBricks = 0
        for (shelterNumber in 0..4) {
            for (column in 0..18) {
                for (row in 0..8) {
                    bricks.add(
                        DefenceBrick(
                            row,
                            column,
                            shelterNumber,
                            sizeDisplay.x,
                            sizeDisplay.y
                        )
                    )

                    numBricks++
                }
            }
        }

        // Initialize the invadersBullets array
        for (i in 0 until maxInvaderBullets) {
            invadersBullets.add(Bullet(sizeDisplay.y))
        }
    }

    fun checkIsNeedUpdate(fps: Long) {

        if (paused) {
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

                if (invader.takeAim(
                        playerShip.position.left,
                        playerShip.width,
                        waves
                    )
                ) {

                    // If so try and spawn a bullet
                    if (invadersBullets[nextBullet].shoot(
                            invader.position.left
                                    + invader.width / 2,
                            invader.position.top, playerBullet.down
                        )
                    ) {

                        // Shot fired
                        // Prepare for the next shot
                        nextBullet++

                        // Loop back to the first one if we have reached the last
                        if (nextBullet == maxInvaderBullets) {
                            // This stops the firing of bullet
                            // until one completes its journey
                            // Because if bullet 0 is still active
                            // shoot returns false.
                            nextBullet = 0
                        }
                    }

                    // If that move caused them to bump
                    // the screen change bumped to true
                    if (invader.position.left > sizeDisplay.x - invader.width
                        || invader.position.left < 0
                    ) {

                        bumped = true

                    }
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



        if (playerBullet.position.bottom < 0) {
            playerBullet.isActive = false
        }

        // Has an invaders playerBullet
        // hit the bottom of the screen
        for (bullet in invadersBullets) {
            if (bullet.position.top > sizeDisplay.y) {
                bullet.isActive = false
            }
        }

        // Has the player's playerBullet hit an invader
        if (playerBullet.isActive) {
            for (invader in invaders) {
                if (invader.isVisible) {
                    if (RectF.intersects(
                            playerBullet.position,
                            invader.position
                        )
                    ) {
                        invader.isVisible = false

                        SoundPlayer.getSoundPlayer(mContext).playSound(
                            SoundPlayer.invaderExplodeID
                        )

                        playerBullet.isActive = false
                        Invader.numberOfInvaders--
                        score += 10
                        if (score > highScore) {
                            highScore = score
                        }

                        // Has the player cleared the level
                        //if (score == numInvaders * 10 * waves) {
                        if (Invader.numberOfInvaders == 0) {
                            paused = true
                            lives++
                            invaders.clear()
                            bricks.clear()
                            invadersBullets.clear()
                            prepareLevel()
                            waves++
                            break
                        }

                        // Don't check any more invaders
                        break
                    }
                }
            }
        }

// Has an alien playerBullet hit a shelter brick
        for (bullet in invadersBullets) {
            if (bullet.isActive) {
                for (brick in bricks) {
                    if (brick.isVisible) {
                        if (RectF.intersects(bullet.position, brick.position)) {
                            // A collision has occurred
                            bullet.isActive = false
                            brick.isVisible = false
                            SoundPlayer.getSoundPlayer(mContext)
                                .playSound(SoundPlayer.damageShelterID)
                        }
                    }
                }
            }
        }

// Has a player playerBullet hit a shelter brick
        if (playerBullet.isActive) {
            for (brick in bricks) {
                if (brick.isVisible) {
                    if (RectF.intersects(playerBullet.position, brick.position)) {
                        // A collision has occurred
                        playerBullet.isActive = false
                        brick.isVisible = false
                        SoundPlayer.getSoundPlayer(mContext).playSound(SoundPlayer.damageShelterID)
                    }
                }
            }
        }

// Has an invader playerBullet hit the player ship
        for (bullet in invadersBullets) {
            if (bullet.isActive) {
                if (RectF.intersects(playerShip.position, bullet.position)) {
                    bullet.isActive = false
                    lives--
                    SoundPlayer.getSoundPlayer(mContext).playSound(SoundPlayer.playerExplodeID)

                    // Is it game over?
                    if (lives == 0) {
                        lost = true
                        break
                    }
                }
            }
        }




        if (lost) {
            paused = true
            lives = 3
            score = 0
            waves = 1
            invaders.clear()
            bricks.clear()
            invadersBullets.clear()
            prepareLevel()
        }
    }


    fun checkIsNeedMenacePlayer() {
        if (!paused && ((startFrameTime!! - lastMenaceTime) > menaceInterval)) {

            if (uhOrOh) {
                // Play Uh
                SoundPlayer.getSoundPlayer(mContext).playSound(SoundPlayer.uhID)

            } else {
                // Play Oh
                SoundPlayer.getSoundPlayer(mContext).playSound(SoundPlayer.ohID)
            }

            // Reset the last menace time
            lastMenaceTime = System.currentTimeMillis()
            // Alter value of uhOrOh
            uhOrOh = !uhOrOh
        }

    }

    fun isPlaying(): Boolean {
        return playing
    }

    fun setPause(pause: Boolean) {
        paused = pause
    }

    fun setPlaying(play: Boolean) {
        playing = play
    }
}