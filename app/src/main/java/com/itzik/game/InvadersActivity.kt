package com.itzik.game

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.itzik.game.uils.UtilsDisplay
import com.itzik.game.uimodel.InvadersView

class InvadersActivity : AppCompatActivity() {

    private var invadersView: InvadersView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val size = Point()
        UtilsDisplay.getDisplaySize(this,size)
        invadersView = InvadersView(this, size)
        setContentView(invadersView)
    }


    override fun onResume() {
        super.onResume()
        // Tell the gameView resume method to execute
        invadersView?.resume()
    }

    // This method executes when the player quits the game
    override fun onPause() {
        super.onPause()
        // Tell the gameView pause method to execute
        invadersView?.pause()
    }
}
