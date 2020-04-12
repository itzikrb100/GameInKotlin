package com.itzik.game.uils

import android.app.Activity
import android.graphics.Point

class UtilsDisplay {

    companion object{
       fun  getDisplaySize(context: Activity, size: Point){
           val display = context.windowManager.defaultDisplay
           // Load the resolution into a Point object
           display.getSize(size)
       }
   }

}