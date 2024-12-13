package com.example.firebaseapp.experiments

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

open class Figure(val type: String)
class Pawn(val f: String = "dfgd", val gdg: Long = 34L) : Figure("Pawn")
class King(val e: Double = 0.67) : Figure("King")

// Адаптер для сериализации и десериализации
class FigureAdapter : JsonSerializer<Figure?>, JsonDeserializer<Figure?> {
    override fun serialize(
        src: Figure?,
        typeOfSrc: java.lang.reflect.Type,
        context: JsonSerializationContext
    ): JsonElement {
        return if (src == null) JsonNull.INSTANCE
        else {
            val jsonObject = JsonObject()
            jsonObject.addProperty("type", src::class.simpleName)
            jsonObject
        }
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: java.lang.reflect.Type,
        context: JsonDeserializationContext
    ): Figure? {
        if (json == null || json.isJsonNull) return null
        val type = json.asJsonObject.get("type").asString
        return when (type) {
            "Pawn" -> Pawn(
                json.asJsonObject.get("f").asString,
                json.asJsonObject.get("gdg").asLong
            )

            "King" -> King(json.asJsonObject.get("e").asDouble)
            else -> null
        }
    }
}

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
