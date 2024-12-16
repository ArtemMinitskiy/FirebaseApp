package com.example.firebaseapp.adapters

import com.example.firebaseapp.model.Bishop
import com.example.firebaseapp.model.Figure
import com.example.firebaseapp.model.King
import com.example.firebaseapp.model.Knight
import com.example.firebaseapp.model.Pawn
import com.example.firebaseapp.model.Queen
import com.example.firebaseapp.model.Rook
import com.example.firebaseapp.utils.Constants.PAWN
import com.example.firebaseapp.utils.Constants.QUEEN
import com.example.firebaseapp.utils.Constants.KING
import com.example.firebaseapp.utils.Constants.BISHOP
import com.example.firebaseapp.utils.Constants.KNIGHT
import com.example.firebaseapp.utils.Constants.ROOK
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

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
            PAWN -> Pawn(
                json.asJsonObject.get("f").asString,
                json.asJsonObject.get("gdg").asLong
            )

            KING -> King(json.asJsonObject.get("e").asDouble)
            QUEEN -> Queen(json.asJsonObject.get("name").asString)
            ROOK -> Rook(json.asJsonObject.get("name").asString)
            KNIGHT -> Knight(json.asJsonObject.get("name").asString)
            BISHOP -> Bishop(json.asJsonObject.get("name").asString)
            else -> null
        }
    }
}