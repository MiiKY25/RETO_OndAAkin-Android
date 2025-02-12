const mongoose = require("mongoose");

// Definir esquema para la colección "alumno"
const UsuarioSchema = new mongoose.Schema({
  nombre: { type: String, unique: true, required: true },  // Nombre único y obligatorio
  contrasena: { type: String, required: true }  // Contraseña obligatoria
});

// Crear el modelo basado en el esquema
const Usuario = mongoose.model("alumno", UsuarioSchema);

module.exports = Usuario;
