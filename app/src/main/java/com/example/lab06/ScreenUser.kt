package com.example.lab06

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, UserDatabase::class.java, "user_db").build()
    }
    val dao = db.userDao()

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val dataUser = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(50.dp))
        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name:") },
            singleLine = true
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name:") },
            singleLine = true
        )
        Button(
            onClick = {
                val user = User(0, firstName, lastName)
                coroutineScope.launch {
                    AgregarUsuario(user = user, dao = dao)
                }
                firstName = ""
                lastName = ""
            }
        ) {
            Text("Agregar Usuario", fontSize = 16.sp)
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    dataUser.value = getUsers(dao = dao)
                }
            }
        ) {
            Text("Listar Usuarios", fontSize = 16.sp)
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    EliminarUltimo(dao = dao)
                    dataUser.value = getUsers(dao = dao)
                }
            }
        ) {
            Text("Eliminar Último", fontSize = 16.sp)
        }

        Text(text = dataUser.value, fontSize = 20.sp)
    }
}
suspend fun getUsers(dao: UserDao): String {
    val users = dao.getAll()
    return users.joinToString("\n") { "${it.firstName} - ${it.lastName}" }
}

suspend fun AgregarUsuario(user: User, dao: UserDao) {
    try {
        dao.insert(user)
    } catch (e: Exception) {
        Log.e("User", "Error: insert: ${e.message}")
    }
}
suspend fun EliminarUltimo(dao: UserDao) {
    try {
        dao.deleteLast()
    } catch (e: Exception) {
        Log.e("User", "Error: delete: ${e.message}")
    }
}