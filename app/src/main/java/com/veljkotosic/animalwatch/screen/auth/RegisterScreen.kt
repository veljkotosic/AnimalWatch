package com.veljkotosic.animalwatch.screen.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.veljkotosic.animalwatch.screen.Screens
import com.veljkotosic.animalwatch.component.Logo
import com.veljkotosic.animalwatch.viewmodel.auth.AuthViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel
import java.io.File

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    val registrationUiState by authViewModel.registrationUiState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false)}

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val avatarFile = remember {
        File(context.cacheDir, "avatar_image.jpg").apply {
            createNewFile()
        }
    }

    val uri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            avatarFile
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            authViewModel.onAvatarUriChanged(uri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        uri : Uri? ->
        if (uri !== null) {
            authViewModel.onAvatarUriChanged(uri)
        }
    }

    LaunchedEffect(registrationUiState.processing.isSuccess) {
        if (registrationUiState.processing.isSuccess) {
            val user = authViewModel.buildUser(registrationUiState)
            userViewModel.createUser(user, registrationUiState.avatarUri!!, context.contentResolver)
            navController.navigate(Screens.RegistrationDone.route) {
                popUpTo(Screens.Register.route) {
                    inclusive = true
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Color(0xffeff5e9))
            .pointerInput(Unit){
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
    ){
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Logo(
                modifier = Modifier.size(280.dp).align(Alignment.CenterHorizontally)
            )

            Text("Register", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = registrationUiState.email,
                onValueChange = { authViewModel.onRegistrationEmailChanged(it) },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = registrationUiState.password,
                onValueChange = { authViewModel.onRegistrationPasswordChanged(it) },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )
            OutlinedTextField(
                value = registrationUiState.confirmPassword,
                onValueChange = { authViewModel.onRegistrationConfirmPasswordChanged(it) },
                label = { Text("Confirm Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )

            OutlinedTextField(
                value = registrationUiState.name,
                onValueChange = { authViewModel.onRegistrationNameChanged(it) },
                label = { Text("Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = registrationUiState.surname,
                onValueChange = { authViewModel.onRegistrationSurnameChanged(it) },
                label = { Text("Surname") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = registrationUiState.displayName,
                onValueChange = { authViewModel.onRegistrationDisplayNameChanged(it) },
                label = { Text("Display Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = registrationUiState.phoneNumber,
                onValueChange = { authViewModel.onRegistrationPhoneNumberChanged(it) },
                label = { Text("Phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Phone, contentDescription = "Phone number")
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 40.dp))
                }
            )

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ){
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp).weight(1f)
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            galleryLauncher.launch("image/*")
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Folder, contentDescription = "Pick from Gallery")
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            cameraLauncher.launch(uri)
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "Take picture")
                    }
                }

                if (registrationUiState.avatarUri === null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .border(BorderStroke(2.dp, Color.Gray), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Choose an image", color = Color.Gray)
                    }
                } else {
                    AsyncImage(
                        model = registrationUiState.avatarUri,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(160.dp)
                            .clip(CircleShape)
                            .border(BorderStroke(2.dp, Color.Gray), CircleShape)
                            .fillMaxWidth()
                    )
                }
            }

            registrationUiState.processing.errorMessage?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Button(
                onClick = {
                    if (registrationUiState.email.isBlank()) {
                        authViewModel.setRegistrationError("Email field is empty.")
                        return@Button
                    }
                    if (registrationUiState.password.isBlank()) {
                        authViewModel.setRegistrationError("Password field is empty")
                        return@Button
                    }
                    if (registrationUiState.confirmPassword.isBlank()) {
                        authViewModel.setRegistrationError("Repeated password field is empty")
                        return@Button
                    }
                    if (authViewModel.passwordsMatch())
                    {
                        authViewModel.setRegistrationError("Repeated password does not match the password")
                        return@Button
                    }
                    if (registrationUiState.name.isBlank()) {
                        authViewModel.setRegistrationError("Name field is empty")
                        return@Button
                    }
                    if (registrationUiState.surname.isBlank()) {
                        authViewModel.setRegistrationError("Surname field is empty")
                        return@Button
                    }
                    if (registrationUiState.phoneNumber.isBlank()) {
                        authViewModel.setRegistrationError("Phone field is empty")
                        return@Button
                    }

                    if (!registrationUiState.processing.isLoading) {
                        authViewModel.register()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                if (registrationUiState.processing.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Register")
                }
            }

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Go back to Login.")
            }

            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}
