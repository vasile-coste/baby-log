package com.vasilecoste.babylog.data.json

import com.vasilecoste.babylog.data.model.ExportedBabyData
import com.vasilecoste.babylog.data.model.ImportedBabyData
import com.vasilecoste.babylog.data.model.ImportedDiaperSummary
import com.vasilecoste.babylog.data.model.ImportedEntry
import com.vasilecoste.babylog.data.model.ImportedWeight
import java.time.LocalDate
import java.time.LocalTime
import org.json.JSONArray
import org.json.JSONObject

/**
 * The on-disk schema is intentionally simple (plain JSON, no library) so exported files
 * stay human-readable and can be hand-edited or produced by other tools.
 */
object BabyDataJson {

    fun parse(jsonText: String): ImportedBabyData {
        val root = JSONObject(jsonText)
        val babyName = root.optJSONObject("baby")?.optString("name")?.takeIf { it.isNotBlank() }
            ?: "Imported baby"

        val entries = root.optJSONArray("entries").orEmpty().map { obj ->
            ImportedEntry(
                date = LocalDate.parse(obj.getString("date")),
                time = LocalTime.parse(obj.getString("time")),
                foodMl = obj.optIntOrNull("foodMl"),
                poop = obj.optBoolean("poop", false),
                pee = obj.optBoolean("pee", false),
                puke = obj.optBoolean("puke", false),
                vitamin = obj.optBoolean("vitamin", false),
                breastfed = obj.optBoolean("breastfed", false),
            )
        }

        val weights = root.optJSONArray("weights").orEmpty().map { obj ->
            ImportedWeight(
                date = LocalDate.parse(obj.getString("date")),
                weightKg = obj.optDoubleOrNull("weightKg"),
                heightCm = obj.optDoubleOrNull("heightCm"),
            )
        }

        val diaperSummaries = root.optJSONArray("dailySummaries").orEmpty().map { obj ->
            ImportedDiaperSummary(
                date = LocalDate.parse(obj.getString("date")),
                poopCount = obj.optInt("poopCount", 0),
                peeCount = obj.optInt("peeCount", 0),
            )
        }

        return ImportedBabyData(babyName, entries, weights, diaperSummaries)
    }

    fun serialize(data: ExportedBabyData): String {
        val root = JSONObject()
        root.put("baby", JSONObject().put("name", data.babyName))

        root.put(
            "weights",
            JSONArray(
                data.weights.map { w ->
                    JSONObject()
                        .put("date", w.date.toString())
                        .put("weightKg", w.weightKg)
                        .put("heightCm", w.heightCm)
                },
            ),
        )

        root.put(
            "dailySummaries",
            JSONArray(
                data.diaperSummaries.map { s ->
                    JSONObject()
                        .put("date", s.date.toString())
                        .put("poopCount", s.poopCount)
                        .put("peeCount", s.peeCount)
                },
            ),
        )

        root.put(
            "entries",
            JSONArray(
                data.entries.map { e ->
                    JSONObject()
                        .put("date", e.date.toString())
                        .put("time", e.time.toString())
                        .put("foodMl", e.foodMl)
                        .put("poop", e.poop)
                        .put("pee", e.pee)
                        .put("puke", e.puke)
                        .put("vitamin", e.vitamin)
                        .put("breastfed", e.breastfed)
                },
            ),
        )

        return root.toString(2)
    }
}

private fun JSONArray?.orEmpty(): List<JSONObject> {
    if (this == null) return emptyList()
    return (0 until length()).map { i -> getJSONObject(i) }
}

private fun JSONObject.optIntOrNull(name: String): Int? =
    if (isNull(name) || !has(name)) null else optInt(name)

private fun JSONObject.optDoubleOrNull(name: String): Double? =
    if (isNull(name) || !has(name)) null else optDouble(name)
