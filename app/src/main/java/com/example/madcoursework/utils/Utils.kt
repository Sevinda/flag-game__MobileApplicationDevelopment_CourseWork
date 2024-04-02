package com.example.madcoursework.utils

import android.content.Context
import android.content.res.Resources
import org.json.JSONObject

// READING THE JSON FILE WHICH IS IN THE ASSETS FOLDER
// REFERENCE (STACK OVERFLOW) => https://stackoverflow.com/questions/56962608/how-to-read-json-file-from-assests-in-android-using-kotlin
fun readJsonFromAssets(context: Context, fileName: String): String {
    val inputStream = context.assets.open(fileName)
    val bufferReader = inputStream.bufferedReader()
    val string = bufferReader.use { it.readText() }
    inputStream.close()
    return string
}

// CONVERTING THE JSON STRING INTO A MAP<STRING, STRING>
fun convertJsonStringToMap(jsonString: String): Map<String, String> {
    val jsonObject = JSONObject(jsonString) // JSON STRING BECOMES A JSON OBJECT
    val map = mutableMapOf<String, String>()
    for (key in jsonObject.keys()) { // LOOPING THE JSON OBJECT AND ASSIGNING VALUES TO THE MAP
        val lowerCaseKey = key.lowercase()
        map[lowerCaseKey] = jsonObject.getString(key)
    }
    return map
}

// ADD IMAGES INTO A LIST IN THE FORM OF RESOURCES
// REFERENCE (STACK OVERFLOW) => https://stackoverflow.com/questions/3476430/how-to-get-a-resource-id-with-a-known-resource-name
fun addResourcesToList(
    countryMap: Map<String, String>,
    resources: Resources,
    packageName: String
): MutableList<Int> {
    val listOfFlagsIDs = mutableListOf<Int>()
    for (entry in countryMap.entries) {
        val resourceName = entry.key
        val resourceID =
            resources.getIdentifier(resourceName, "drawable", packageName)
        listOfFlagsIDs.add(resourceID)
    }
    return listOfFlagsIDs
}
