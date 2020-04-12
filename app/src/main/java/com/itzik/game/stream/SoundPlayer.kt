package com.itzik.game.stream

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import java.io.IOException

class SoundPlayer() {

    private val TAG = "SoundPlayer"

    // For sound FX
    private lateinit var mSoundPool: SoundPool
    private lateinit var mContext: Context
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()


    private constructor(context: Context) : this() {
        mContext = context
        mSoundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()
        initSoundIds()
        Log.d(TAG, "init constructor")
    }

    private fun initSoundIds(){
        Log.d(TAG,"init sound ids")
        try {
            // Create objects of the 2 required classes
            val assetManager = mContext.assets
            var descriptor: AssetFileDescriptor


            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("shoot.ogg")
            shootID = mSoundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("invaderexplode.ogg")
            invaderExplodeID = mSoundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("playerexplode.ogg")
            playerExplodeID = mSoundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("damageshelter.ogg")
            damageShelterID = mSoundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("uh.ogg")
            uhID = mSoundPool.load(descriptor, 0)

            descriptor = assetManager.openFd("oh.ogg")
            ohID = mSoundPool.load(descriptor, 0)


        } catch (e: IOException) {
            // Print an error message to the console
            Log.e("error", "failed to load sound files")
        }
    }



    companion object {
         var instance: SoundPlayer? = null

        var playerExplodeID = -1
        var invaderExplodeID = -1
        var shootID = -1
        var damageShelterID = -1
        var uhID = -1
        var ohID = -1


        fun getSoundPlayer(context: Context): SoundPlayer{
            synchronized(this) {
                if (instance == null) {
                    instance = SoundPlayer(context)
                }
            }
            return instance!!
        }
    }

    fun playSound(id: Int){
        mSoundPool.play(id, 1f, 1f, 0, 0, 1f)
    }
}