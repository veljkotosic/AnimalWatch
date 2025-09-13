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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.veljkotosic.animalwatch.composable.logo.Logo
import com.veljkotosic.animalwatch.screen.Screens
import com.veljkotosic.animalwatch.viewmodel.auth.RegistrationViewModel
import com.veljkotosic.animalwatch.viewmodel.user.UserViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    registrationViewModel: RegistrationViewModel,
    userViewModel: UserViewModel
) {
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)
    val cameraPermissionCoroutineScope = rememberCoroutineScope()

    val processingUiState by registrationViewModel.processingUiState.collectAsState()
    val registrationUiState by registrationViewModel.registrationUiState.collectAsState()

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
            registrationViewModel.onAvatarUriChanged(uri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        uri : Uri? ->
        if (uri !== null) {
            val newUri = uri.buildUpon().appendQueryParameter("ts", System.currentTimeMillis().toString()).build()
            registrationViewModel.onAvatarUriChanged(newUri)
        }
    }

    LaunchedEffect(processingUiState.isSuccess) {
        if (processingUiState.isSuccess) {
            val uid = registrationViewModel.newUserUid
            val user = registrationViewModel.buildUser(registrationUiState, uid.value!!)
            userViewModel.createUser(user, registrationUiState.avatarUri!!, context)
            navController.navigate(Screens.RegistrationDone.route) {
                popUpTo(Screens.Register.route) {
                    inclusive = true
                }
            }
        }
    }

    // Pokretanje kamere nakon davanja dozvole, a samo na pritisak dugmeta za kameru
    LaunchedEffect(cameraPermission.status, registrationUiState.userRequestedCamera) {
        if (cameraPermission.status.isGranted && registrationUiState.userRequestedCamera) {
            registrationViewModel.resetCameraRequest()
            cameraLauncher.launch(uri)
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
                onValueChange = { registrationViewModel.onEmailChanged(it) },
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
                onValueChange = { registrationViewModel.onPasswordChanged(it) },
                label = { Text("Password") },
                visualTransformation = if (registrationUiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    val image = if (registrationUiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (registrationUiState.passwordVisible) "Hide password" else "Show password"

                    IconButton(
                        onClick = {
                            registrationViewModel.togglePasswordVisibility()
                        }
                    ) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )
            OutlinedTextField(
                value = registrationUiState.confirmPassword,
                onValueChange = { registrationViewModel.onConfirmPasswordChanged(it) },
                label = { Text("Confirm Password") },
                visualTransformation = if (registrationUiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                trailingIcon = {
                    val image = if (registrationUiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (registrationUiState.passwordVisible) "Hide password" else "Show password"

                    IconButton(
                        onClick = {
                            registrationViewModel.togglePasswordVisibility()
                        }
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
                onValueChange = { registrationViewModel.onNameChanged(it) },
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
                onValueChange = { registrationViewModel.onSurnameChanged(it) },
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
                onValueChange = { registrationViewModel.onDisplayNameChanged(it) },
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
                onValueChange = { registrationViewModel.onPhoneNumberChanged(it) },
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
                            if (cameraPermission.status.isGranted) {
                                cameraLauncher.launch(uri)
                            } else {
                                cameraPermissionCoroutineScope.launch {
                                    registrationViewModel.onCameraRequested()
                                    cameraPermission.launchPermissionRequest()
                                }
                            }
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

            processingUiState.errorMessage?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Button(
                onClick = {
                    if (registrationUiState.email.isBlank()) {
                        registrationViewModel.setError("Email field is empty.")
                        return@Button
                    }
                    if (registrationUiState.password.isBlank()) {
                        registrationViewModel.setError("Password field is empty")
                        return@Button
                    }
                    if (registrationUiState.confirmPassword.isBlank()) {
                        registrationViewModel.setError("Repeated password field is empty")
                        return@Button
                    }
                    if (registrationViewModel.passwordsMatch())
                    {
                        registrationViewModel.setError("Repeated password does not match the password")
                        return@Button
                    }
                    if (registrationUiState.name.isBlank()) {
                        registrationViewModel.setError("Name field is empty")
                        return@Button
                    }
                    if (registrationUiState.surname.isBlank()) {
                        registrationViewModel.setError("Surname field is empty")
                        return@Button
                    }
                    if (registrationUiState.phoneNumber.isBlank()) {
                        registrationViewModel.setError("Phone field is empty")
                        return@Button
                    }
                    if (registrationUiState.avatarUri === null)
                    {
                        registrationViewModel.setError("Avatar Uri invalid")
                        return@Button
                    }

                    if (!processingUiState.isLoading) {
                        registrationViewModel.register()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                if (processingUiState.isLoading) {
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
