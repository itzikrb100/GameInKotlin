package com.itzik.game.uimodel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import com.itzik.game.gl.GameBoard
import com.itzik.game.gl.GameThread
import com.itzik.game.models.Bullet
import com.itzik.game.models.DefenceBrick
import com.itzik.game.models.Invader
import com.itzik.game.models.PlayerShip
import com.itzik.game.uils.UtilsDisplay

class InvadersView(
    context: Context,
    private val size: Point): SurfaceView(context){

    private val TAG: String = "InvadersView"

    private val game: GameThread = object: GameThread(TAG){
        override fun run() {
            runGame()
        }
    }


    private val gameBoard: GameBoard = GameBoard(context,size)


    // A Canvas and a Paint object
    private var canvas: Canvas = Canvas()
    private val paint: Paint = Paint()






    fun start(){
        Log.d(TAG,"start")
        game.startGame()
    }

    fun pause() {
        Log.d(TAG,"pause")
        gameBoard.setPlaying(false)
        game.pauseGame()
    }


    fun resume() {
        Log.d(TAG,"resume")
        gameBoard.setPlaying(true)
        gameBoard.prepareLevel()
        game.resumeGame()
    }

    fun stop() {

    }

    fun destroy() {

    }




    private fun drawGame() {
        // Make sure our drawing surface is valid or the game will crash
        if (holder.surface.isValid) {
            // Lock the canvas ready to draw
            canvas = holder.lockCanvas()

            // Draw the background color
            canvas.drawColor(Color.argb(255, 0, 0, 0))

            // Choose the brush color for drawing
            paint.color = Color.argb(255, 0, 255, 0)

            // Draw all the game objects here
            // Now draw the player spaceship
            val playerShip = gameBoard.getPlayerShip()
            canvas.drawBitmap(playerShip.bitmap, playerShip.position.left,
                playerShip.position.top
                , paint)



            // Draw all the game objects here
              // Now draw the player spaceship
            canvas.drawBitmap(playerShip.bitmap, playerShip.position.left,
                playerShip.position.top
                , paint)

            // Draw the invaders
            val invaders = gameBoard.getInvaders()
            for (invader in invaders) {
                if (invader.isVisible) {
                    if (gameBoard.uhOrOh) {
                        canvas.drawBitmap(Invader.bitmap1,
                            invader.position.left,
                            invader.position.top,
                            paint)
                    } else {
                        canvas.drawBitmap(Invader.bitmap2,
                            invader.position.left,
                            invader.position.top,
                            paint)
                    }
                }
            }


            val bricks = gameBoard.getBricks()
            // Draw the bricks if visible
            for (brick in bricks) {
                if (brick.isVisible) {
                    canvas.drawRect(brick.position, paint)
                }
            }


            // Draw the bricks if visible
            for (brick in bricks) {
                if (brick.isVisible) {
                    canvas.drawRect(brick.position, paint)
                }
            }

            val playerBullet = gameBoard.getPlayerBullet()
            // Draw the players playerBullet if active
            if (playerBullet.isActive) {
                canvas.drawRect(playerBullet.position, paint)
            }

            // Draw the invaders bullets
            val invadersBullets = gameBoard.getInvenvadersBullets()
            for (bullet in invadersBullets) {
                if (bullet.isActive) {
                    canvas.drawRect(bullet.position, paint)
                }
            }

            // Draw the score and remaining lives
            // Change the brush color
            paint.color = Color.argb(255, 255, 255, 255)
            paint.textSize = 70f
            canvas.drawText(
                "Score: ${gameBoard.score}   Lives: ${gameBoard.lives} Wave: " +
                        "${gameBoard.waves} HI: ${gameBoard.highScore}", 20f, 75f, paint
            )

            // Draw everything to the screen
            holder.unlockCanvasAndPost(canvas)
        }
    }



    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {

        when (motionEvent.action and MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            // Or moved their finger while touching screen
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE-> {
                gameBoard.setPause(false)
                val playerShip = gameBoard.getPlayerShip()
                if (motionEvent.y > size.y - size.y / 8) {
                    if (motionEvent.x > size.x / 2) {
                        playerShip.moving = PlayerShip.right
                    } else {
                        playerShip.moving = PlayerShip.left
                    }

                }


                val playerBullet = gameBoard.getPlayerBullet()
                if (motionEvent.y < size.y - size.y / 8) {
                    // Shots fired
                    if (playerBullet.shoot(
                            playerShip.position.left + playerShip.width / 2f,
                            playerShip.position.top,
                            playerBullet.up)) {

                        //soundPlayer.playSound(SoundPlayer.shootID)
                    }
                }
            }

            // Player has removed finger from screen
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP -> {
                val playerShip = gameBoard.getPlayerShip()
                if (motionEvent.y > size.y - size.y / 10) {
                    playerShip.moving = PlayerShip.stopped
                }
            }

        }
        return true
    }



    private fun runGame(){
        var fps: Long = 0

        while (gameBoard.isPlaying()) {

            // Capture the current time
            val startFrameTime = System.currentTimeMillis()

            // Update the frame
           gameBoard.checkIsNeedUpdate(fps)

            //Log.d(TAG,"**************** GAME PLAYING ************** ")
            // Draw the frame
            drawGame()

            // Calculate the fps rate this frame
            val timeThisFrame = System.currentTimeMillis() - startFrameTime
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame
            }
        }
    }

}