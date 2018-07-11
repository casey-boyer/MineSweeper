package edu.umsl.minesweeper


import android.util.Log
import java.lang.ref.WeakReference
import java.util.HashMap

class ClassHolder {

    companion object {
        val MINESWEEPER_MODEL = "minesweeperModel"
        val MAIN_MODEL = "MainModel"
        val instance = ClassHolder()
    }

    private val modelData = HashMap<String, WeakReference<Any?>>()

    fun saveClass(modelKey: String, model: Any?) {
        modelData[modelKey] = WeakReference(model)
    }

    fun getClass(modelKey: String): Any? {
        val weakObject = modelData[modelKey]
        Log.e("getModel", "weakObject?.get() for $modelKey null is ${weakObject?.get() == null}")
        return weakObject?.get()
    }
}