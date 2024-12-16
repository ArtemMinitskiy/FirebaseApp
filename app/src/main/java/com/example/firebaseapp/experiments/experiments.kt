package com.example.firebaseapp.experiments

import android.util.Log
import com.example.firebaseapp.adapters.FigureAdapter
import com.example.firebaseapp.model.Figure
import com.example.firebaseapp.model.King
import com.example.firebaseapp.model.Pawn
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder

val list =
    arrayListOf(
        Pawn(""),
        Pawn("vdsbdf", 34L),
        King(),
        null,
        null,
        null,
        Pawn(gdg = 23423L)
    )

val gson = GsonBuilder()
    .registerTypeAdapter(Figure::class.java, FigureAdapter())
    .create()

val json = gson.toJson(list)

fun sendToDB(db: FirebaseFirestore) {
    db.collection("figures")
        .document("list")
        .set(mapOf("data" to json))
        .addOnSuccessListener { println("Data saved successfully!") }
        .addOnFailureListener { e -> println("Error saving data: $e") }

    db.collection("figures")
        .document("list")
        .get()
        .addOnSuccessListener { document ->
            val jsonData = document.getString("data")
            if (jsonData != null) {
                val restoredList =
                    gson.fromJson(jsonData, Array<Figure?>::class.java).toList()
                Log.e("mLogFire", "restoredList $restoredList")
                restoredList.forEach {
                    when (it) {
                        is Pawn -> {
                            Log.i("mLogFire", "Pawn ${it.f} ${it.gdg} ")
                        }

                        is King -> {
                            Log.i("mLogFire", "King ${it.e}")
                        }

                        null -> {

                        }
                    }
                }
            }
        }
        .addOnFailureListener { e -> println("Error loading data: $e") }
}
