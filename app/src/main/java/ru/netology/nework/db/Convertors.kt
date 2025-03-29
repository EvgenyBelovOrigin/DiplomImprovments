package ru.netology.nework.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Convertors {
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Int>>() {}

    @TypeConverter
    fun fromListIntToString(value : List<Int>) : String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromStringToListInt(value : String) : List<Int>{
        return gson.fromJson(value,typeToken.type)
    }
}