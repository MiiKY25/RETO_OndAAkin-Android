package com.example.ondaakin4

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginController : AppCompatActivity() {

    private lateinit var txtUsuario: EditText
    private lateinit var txtContrasena: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        txtUsuario = findViewById(R.id.txtUsuario)
        txtContrasena = findViewById(R.id.txtContrasena)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val nombre = txtUsuario.text.toString().trim()
            val contrasena = txtContrasena.text.toString().trim()

            if (nombre.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = Usuario(nombre, contrasena)
            val authService = RetrofitClient.getLoginService()
            val call = authService.login(usuario)

            call.enqueue(object : Callback<Respuesta> {
                override fun onResponse(call: Call<Respuesta>, response: Response<Respuesta>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginController, response.body()?.mensaje, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginController, "Usuario ya existe", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Respuesta>, t: Throwable) {
                    Toast.makeText(this@LoginController, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    System.err.println("Error de conexión: ${t.message}")
                }

            })
        }
    }
}
