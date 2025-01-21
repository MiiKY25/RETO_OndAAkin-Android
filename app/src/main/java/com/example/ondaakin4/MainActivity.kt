package com.example.ondaakin4
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register ActivityResult handler
        setContentView(R.layout.activity_main)
        val uploadButton = findViewById<Button>(R.id.button)
        val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            // Handle permission requests results
            // See the permission example in the Android platform samples: https://github.com/android/platform-samples
        }

// Permission request logic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }
        requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE,READ_MEDIA_IMAGES))

        uploadButton.setOnClickListener {
            selectPhotos()
        }



    }

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedUri: Uri? = result.data?.data
                if (selectedUri != null) {
                    uploadImage(this, selectedUri)
                    // Maneja la URI de la imagen seleccionada
                    Log.d("Selected Image", "URI: $selectedUri")
                    Toast.makeText(this, "Imagen seleccionada: $selectedUri", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show()
            }
        }

    private fun selectPhotos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        selectImageLauncher.launch(intent)
    }

    fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image.jpg")
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("image", file.name, requestBody) // Nombre del campo esperado
    }
    fun uploadImage(context: Context, uri: Uri) {
        val imagePart = uriToMultipart(context, uri)

        if (imagePart != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.apiService.uploadImage(imagePart)
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string() // Procesar respuesta si es necesario
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Imagen subida con éxito", Toast.LENGTH_SHORT).show()
                            Log.d("Upload Response", responseBody ?: "Sin respuesta")
                        }
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Error al subir la imagen: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "No se pudo preparar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

}