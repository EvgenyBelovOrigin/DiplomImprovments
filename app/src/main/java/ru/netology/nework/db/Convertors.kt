package ru.netology.nework.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nework.dto.UserPreview

class Convertors {
    private val gson = Gson()
    private val typeTokenListInt = object : TypeToken<List<Int>>() {}
    private val typeTokenMapStringUserPreview = object : TypeToken<Map<String, UserPreview?>>() {}


    @TypeConverter
    fun fromListIntToString(value : List<Int>) : String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromStringToListInt(value : String) : List<Int>{
        return gson.fromJson(value,typeTokenListInt.type)
    }
    @TypeConverter
    fun fromMapStringUserPreviewToString(value : Map<String, UserPreview?>) : String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromStringToMapStringUserPreview(value : String) : Map<String, UserPreview?>{
        return gson.fromJson(value,typeTokenMapStringUserPreview.type)
    }
}