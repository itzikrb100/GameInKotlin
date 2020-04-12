package com.itzik.game.gl

import android.util.Log


open class GameThread(name: String): Thread(name){

    private val TAG: String = "GameThread"

    private var gameState: STATE = STATE.INIT

    enum class STATE(state: Int) {
        NOT_SUPPORT(-1),INIT(0),START(1),STOP(2),RESUME(3),PAUSE(4);

        private var state = state

    }


    fun startGame(){
        gameState = STATE.START
        start()
    }

    fun pauseGame(){
        gameState = STATE.PAUSE
        try {
            join()
        }catch (e: InterruptedException){
            Log.e("Error:", "joining thread")
        }

    }

    fun resumeGame(){
        gameState = STATE.RESUME
        start()
    }
}