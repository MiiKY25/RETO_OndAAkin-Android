const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const bodyParser = require("body-parser");

// Conectar a MongoDB
mongoose.connect("mongodb://localhost:27017/alumno", {
  useNewUrlParser: true,
  useUnifiedTopology: true
}).then(() => console.log("MongoDB conectado"))
  .catch(err => console.log(err));

// Definir el esquema de usuario
const UsuarioSchema = new mongoose.Schema({
  nombre: { type: String, unique: true, required: true },
  contrasena: { type: String, required: true }
});

const Usuario = mongoose.model("alumno", UsuarioSchema);

// Iniciar Express
const app = express();
app.use(cors());
app.use(bodyParser.json());

// Ruta para iniciar sesión o registrar usuario
app.post("/login", async (req, res) => {
  const { nombre, contrasena } = req.body;

  try {
    // Verificar si el usuario ya existe
    let usuario = await Usuario.findOne({ nombre });

    if (usuario) {
      return res.status(400).json({ mensaje: "El usuario ya existe" });
    }

    // Si no existe, lo creamos
    usuario = new Usuario({ nombre, contrasena });
    await usuario.save();
    res.status(201).json({ mensaje: "Usuario registrado con éxito" });

  } catch (error) {
    res.status(500).json({ mensaje: "Error del servidor" });
  }
});

// Iniciar servidor
app.listen(5000, () => console.log("Servidor corriendo en puerto 5000"));
