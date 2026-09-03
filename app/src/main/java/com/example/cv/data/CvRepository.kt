package com.example.cv.data

import android.content.Context
import com.example.cv.model.CvDocument
import com.example.cv.model.CvHeader
import com.example.cv.model.CvSection
import com.example.cv.model.DefaultCvData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CvRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("cv_prefs", Context.MODE_PRIVATE)
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    companion object {
        const val BACKEND_URL = "https://cv-q3p4.onrender.com"
        private const val KEY_HEADER = "saved_header"
        private const val KEY_SECTIONS = "saved_sections"
        private const val KEY_DOCUMENTS = "saved_documents"
    }

    suspend fun getHeader(): CvHeader = withContext(Dispatchers.IO) {
        val raw = prefs.getString(KEY_HEADER, null) ?: return@withContext DefaultCvData.defaultHeader
        try {
            val json = JSONObject(raw)
            CvHeader(
                name = json.optString("name", DefaultCvData.defaultHeader.name),
                address = json.optString("address", DefaultCvData.defaultHeader.address),
                phone = json.optString("phone", DefaultCvData.defaultHeader.phone),
                email = json.optString("email", DefaultCvData.defaultHeader.email),
                linkedin = json.optString("linkedin", DefaultCvData.defaultHeader.linkedin),
                photo = json.optString("photo", "profile"),
                signature = json.optString("signature", "signature")
            )
        } catch (e: Exception) {
            DefaultCvData.defaultHeader
        }
    }

    suspend fun saveHeader(header: CvHeader) = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("name", header.name)
            put("address", header.address)
            put("phone", header.phone)
            put("email", header.email)
            put("linkedin", header.linkedin)
            put("photo", header.photo)
            put("signature", header.signature)
        }
        prefs.edit().putString(KEY_HEADER, json.toString()).apply()
    }

    suspend fun getSections(): List<CvSection> = withContext(Dispatchers.IO) {
        val raw = prefs.getString(KEY_SECTIONS, null) ?: return@withContext DefaultCvData.defaultSections
        try {
            val array = JSONArray(raw)
            val list = mutableListOf<CvSection>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CvSection(
                        id = obj.optString("id", "sec_$i"),
                        title = obj.optString("title", ""),
                        body = obj.optString("body", "")
                    )
                )
            }
            if (list.isNotEmpty()) list else DefaultCvData.defaultSections
        } catch (e: Exception) {
            DefaultCvData.defaultSections
        }
    }

    suspend fun saveSections(sections: List<CvSection>) = withContext(Dispatchers.IO) {
        val array = JSONArray()
        sections.forEach { sec ->
            val obj = JSONObject().apply {
                put("id", sec.id)
                put("title", sec.title)
                put("body", sec.body)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_SECTIONS, array.toString()).apply()
    }

    suspend fun getDocuments(): List<CvDocument> = withContext(Dispatchers.IO) {
        val raw = prefs.getString(KEY_DOCUMENTS, null) ?: return@withContext DefaultCvData.defaultDocuments
        try {
            val array = JSONArray(raw)
            val list = mutableListOf<CvDocument>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CvDocument(
                        id = obj.optString("id", "doc_$i"),
                        name = obj.optString("name", "Document"),
                        dateAdded = obj.optString("dateAdded", "Added"),
                        imageUri = if (obj.has("imageUri") && !obj.isNull("imageUri")) obj.getString("imageUri") else null
                    )
                )
            }
            if (list.isNotEmpty()) list else DefaultCvData.defaultDocuments
        } catch (e: Exception) {
            DefaultCvData.defaultDocuments
        }
    }

    suspend fun saveDocuments(docs: List<CvDocument>) = withContext(Dispatchers.IO) {
        val array = JSONArray()
        docs.forEach { doc ->
            val obj = JSONObject().apply {
                put("id", doc.id)
                put("name", doc.name)
                put("dateAdded", doc.dateAdded)
                put("imageUri", doc.imageUri ?: JSONObject.NULL)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_DOCUMENTS, array.toString()).apply()
    }

    suspend fun resetToDefault() = withContext(Dispatchers.IO) {
        prefs.edit()
            .remove(KEY_HEADER)
            .remove(KEY_SECTIONS)
            .remove(KEY_DOCUMENTS)
            .apply()
    }

    suspend fun verifyAdminLogin(phone: String, pass: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val trimmedPhone = phone.trim()
        val trimmedPass = pass.trim()

        // Fallback local check (from server.js credentials)
        val matchesLocal = (trimmedPhone == "01720094069" && trimmedPass == "Rabbi+AA") ||
                (trimmedPhone == "01720094069" && trimmedPass == "admin")

        try {
            val payload = JSONObject().apply {
                put("phone", trimmedPhone)
                put("password", trimmedPass)
            }
            val reqBody = payload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BACKEND_URL/api/login")
                .post(reqBody)
                .build()

            val response = client.newCall(request).execute()
            val respBody = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val json = JSONObject(respBody)
                if (json.optBoolean("success", false)) {
                    return@withContext Result.success(true)
                }
            }
        } catch (e: Exception) {
            // Server might be sleeping on Render or offline, fall back to registered credentials
        }

        if (matchesLocal) {
            Result.success(true)
        } else {
            Result.failure(Exception("Invalid phone number or password."))
        }
    }

    suspend fun syncRemoteData(): Pair<CvHeader?, List<CvSection>?> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$BACKEND_URL/api/cv-data")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val raw = response.body?.string() ?: return@withContext Pair(null, null)
                val json = JSONObject(raw)
                var header: CvHeader? = null
                if (json.has("header")) {
                    val hObj = json.getJSONObject("header")
                    header = CvHeader(
                        name = hObj.optString("name", DefaultCvData.defaultHeader.name),
                        address = hObj.optString("address", DefaultCvData.defaultHeader.address),
                        phone = hObj.optString("phone", DefaultCvData.defaultHeader.phone),
                        email = hObj.optString("email", DefaultCvData.defaultHeader.email),
                        linkedin = hObj.optString("linkedin", ""),
                        photo = hObj.optString("photo", "profile"),
                        signature = hObj.optString("signature", "signature")
                    )
                }
                var sections: List<CvSection>? = null
                if (json.has("sections")) {
                    val sArr = json.getJSONArray("sections")
                    val list = mutableListOf<CvSection>()
                    for (i in 0 until sArr.length()) {
                        val sObj = sArr.getJSONObject(i)
                        list.add(
                            CvSection(
                                id = sObj.optString("id", "sec_$i"),
                                title = sObj.optString("title", ""),
                                body = sObj.optString("body", "")
                            )
                        )
                    }
                    if (list.isNotEmpty()) sections = list
                }
                return@withContext Pair(header, sections)
            }
        } catch (e: Exception) {
            // Offline or fallback
        }
        Pair(null, null)
    }

    suspend fun syncSaveToRemote(header: CvHeader, sections: List<CvSection>): Boolean = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                val h = JSONObject().apply {
                    put("name", header.name)
                    put("address", header.address)
                    put("phone", header.phone)
                    put("email", header.email)
                    put("linkedin", header.linkedin)
                    put("photo", header.photo)
                    put("signature", header.signature)
                }
                put("header", h)

                val sArr = JSONArray()
                sections.forEach { s ->
                    val obj = JSONObject().apply {
                        put("id", s.id)
                        put("title", s.title)
                        put("body", s.body)
                    }
                    sArr.put(obj)
                }
                put("sections", sArr)
            }

            val reqBody = payload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BACKEND_URL/api/cv-data")
                .post(reqBody)
                .build()

            val response = client.newCall(request).execute()
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            return@withContext false
        }
    }
}
