package com.calaratjada.insider.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.calaratjada.insider.ui.theme.*
import com.calaratjada.insider.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onCoinStore: () -> Unit,
    onPrivacyPolicy: () -> Unit = {},
    onImpressum: () -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val coinBalance by viewModel.coinBalance.collectAsStateWithLifecycle()
    val isSignedIn by viewModel.isSignedIn.collectAsStateWithLifecycle()
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var editingNickname by remember { mutableStateOf(false) }
    var nicknameText by remember(nickname) { mutableStateOf(nickname ?: "") }

    // Image picker with secure storage
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            MainScope().launch {
                withContext(Dispatchers.IO) {
                    // Decode and resize to prevent memory attacks
                    val inputStream = context.contentResolver.openInputStream(it) ?: return@withContext
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(inputStream, null, options)
                    inputStream.close()

                    // Calculate sample size for ~256px target
                    val maxDim = maxOf(options.outWidth, options.outHeight)
                    val sampleSize = maxOf(1, maxDim / 256)

                    val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
                    val stream2 = context.contentResolver.openInputStream(it) ?: return@withContext
                    val bitmap = BitmapFactory.decodeStream(stream2, null, decodeOptions)
                    stream2.close()

                    bitmap?.let { bmp ->
                        // Save to app-private directory
                        val profileDir = File(context.filesDir, "profile_pictures")
                        profileDir.mkdirs()
                        val file = File(profileDir, "profile.jpg")
                        file.outputStream().use { out ->
                            bmp.compress(Bitmap.CompressFormat.JPEG, 85, out)
                        }
                        bmp.recycle()

                        // Use FileProvider URI instead of file:// URI
                        val secureUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        viewModel.authService.updateProfilePic(secureUri.toString())
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mein Profil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Zurück")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
            )
        },
        containerColor = Stone50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                val picUri = profile?.profilePicUri
                if (!picUri.isNullOrBlank()) {
                    AsyncImage(
                        model = picUri,
                        contentDescription = "Profilbild",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        shape = CircleShape,
                        color = Emerald100,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { imagePickerLauncher.launch("image/*") }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = Emerald500
                            )
                        }
                    }
                }

                // Camera button overlay
                Surface(
                    shape = CircleShape,
                    color = Emerald600,
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Bild ändern",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nickname
            if (editingNickname) {
                OutlinedTextField(
                    value = nicknameText,
                    onValueChange = { nicknameText = it.take(20) },
                    label = { Text("Nickname") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            if (nicknameText.trim().length >= 2) {
                                viewModel.setNickname(nicknameText.trim())
                                editingNickname = false
                            }
                        }) {
                            Icon(Icons.Default.Check, "Speichern")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = nickname ?: "Kein Nickname",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Stone900
                    )
                    IconButton(onClick = { editingNickname = true }) {
                        Icon(Icons.Default.Edit, "Bearbeiten", modifier = Modifier.size(18.dp))
                    }
                }
            }

            profile?.email?.let { email ->
                if (email.isNotBlank()) {
                    Text(text = email, fontSize = 13.sp, color = Stone400)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Coin Balance Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald50),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCoinStore)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$coinBalance Coins",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Stone900
                        )
                        Text(
                            text = "Tippe zum Aufladen",
                            fontSize = 13.sp,
                            color = Emerald700
                        )
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = Emerald500)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Premium status
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Stone100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = if (profile?.isPremium == true) Amber400 else Stone300,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (profile?.isPremium == true) "Premium aktiv" else "Standard-Mitglied",
                            fontWeight = FontWeight.SemiBold,
                            color = Stone900
                        )
                        Text(
                            text = if (profile?.isPremium == true) "Unbegrenzt chatten"
                            else "Premium: Keine Coin-Kosten für Nachrichten",
                            fontSize = 12.sp,
                            color = Stone500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // DSGVO Legal Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onPrivacyPolicy) {
                    Icon(Icons.Default.Shield, null, modifier = Modifier.size(16.dp), tint = Emerald600)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Datenschutz", color = Emerald600, fontSize = 13.sp)
                }
                TextButton(onClick = onImpressum) {
                    Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp), tint = Emerald600)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Impressum", color = Emerald600, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // DSGVO Art. 17 - Right to erasure
            var showDeleteDialog by remember { mutableStateOf(false) }

            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Red500)
            ) {
                Icon(Icons.Default.DeleteForever, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Alle meine Daten löschen")
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Daten löschen?", fontWeight = FontWeight.Bold) },
                    text = {
                        Text("Alle deine Daten (Profil, Nachrichten, Freunde, Münzen) werden unwiderruflich gelöscht. Diese Aktion kann nicht rückgängig gemacht werden.")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            viewModel.deleteAllUserData()
                        }) {
                            Text("Endgültig löschen", color = Red500)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Abbrechen")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sign out
            if (isSignedIn) {
                TextButton(onClick = { viewModel.signOut() }) {
                    Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Abmelden", color = Red500)
                }
            }
        }
    }
}
