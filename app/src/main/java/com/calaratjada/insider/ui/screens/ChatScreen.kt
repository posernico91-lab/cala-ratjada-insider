package com.calaratjada.insider.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.calaratjada.insider.data.model.ChatMessage
import com.calaratjada.insider.data.service.VivoxParticipant
import com.calaratjada.insider.ui.theme.*
import com.calaratjada.insider.ui.viewmodel.ChatRoomViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    roomName: String,
    onBack: () -> Unit,
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val participants by viewModel.participants.collectAsStateWithLifecycle()
    val isMicMuted by viewModel.isMicMuted.collectAsStateWithLifecycle()
    val messageText by viewModel.messageText.collectAsStateWithLifecycle()
    val coinBalance by viewModel.coinBalance.collectAsStateWithLifecycle()
    var showParticipants by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.sendImage(it) }
    }

    // Listen for insufficient coins messages
    LaunchedEffect(Unit) {
        viewModel.insufficientCoins.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = roomName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${participants.size} Teilnehmer",
                            fontSize = 12.sp,
                            color = Stone400
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    // Coin balance chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Amber400.copy(alpha = 0.15f),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.MonetizationOn, null,
                                tint = Amber500, modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("$coinBalance", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Stone800)
                        }
                    }
                    // Mic toggle
                    IconButton(onClick = { viewModel.toggleMic() }) {
                        Icon(
                            imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isMicMuted) "Mikrofon ein" else "Mikrofon aus",
                            tint = if (isMicMuted) Red500 else Emerald500
                        )
                    }
                    // Participants
                    IconButton(onClick = { showParticipants = !showParticipants }) {
                        BadgedBox(
                            badge = {
                                if (participants.isNotEmpty()) {
                                    Badge { Text("${participants.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.People, contentDescription = "Teilnehmer")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Stone50)
            )
        },
        containerColor = Stone100
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Participants bar (collapsible)
            if (showParticipants && participants.isNotEmpty()) {
                Surface(
                    color = Stone50,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Teilnehmer",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Stone700
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        participants.forEach { participant ->
                            ParticipantRow(
                                participant = participant,
                                onAddFriend = { viewModel.addFriend(participant) }
                            )
                        }
                    }
                }
            }

            // Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageBubble(message = message)
                }
            }

            // Input bar
            Surface(
                color = Stone50,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Image picker button
                    IconButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Bild senden",
                            tint = Emerald500
                        )
                    }

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { viewModel.onMessageTextChanged(it) },
                        placeholder = { Text("Nachricht schreiben...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald500,
                            unfocusedBorderColor = Stone200,
                            cursorColor = Emerald500
                        )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    FilledIconButton(
                        onClick = { viewModel.sendMessage() },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Emerald500
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Senden",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val isImage = message.imageUri.isNotBlank() || message.body.startsWith("[IMG]")
    val rawUri = if (message.imageUri.isNotBlank()) message.imageUri
                   else if (message.body.startsWith("[IMG]")) message.body.removePrefix("[IMG]")
                   else null

    // Validate image URI: only allow content:// and https:// schemes
    val imageUri = rawUri?.let { uri ->
        when {
            uri.startsWith("content://") -> uri
            uri.startsWith("https://") -> uri
            else -> null // Block file://, javascript:, data:, etc.
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        if (!message.isFromMe) {
            Text(
                text = message.senderDisplayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Emerald700,
                modifier = Modifier.padding(start = 12.dp, bottom = 2.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromMe) 16.dp else 4.dp,
                bottomEnd = if (message.isFromMe) 4.dp else 16.dp
            ),
            color = if (message.isFromMe) Emerald500 else Stone50,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                if (isImage && imageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUri)
                            .crossfade(true)
                            .size(600) // Limit decoded size to prevent memory exhaustion
                            .build(),
                        contentDescription = "Geteiltes Bild",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    Text(
                        text = message.body,
                        fontSize = 15.sp,
                        color = if (message.isFromMe) Color.White else Stone900
                    )
                }
                Text(
                    text = timeFormat.format(Date(message.timestamp)),
                    fontSize = 10.sp,
                    color = if (message.isFromMe) Color.White.copy(alpha = 0.7f) else Stone400,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun ParticipantRow(
    participant: VivoxParticipant,
    onAddFriend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Surface(
            shape = CircleShape,
            color = Emerald100,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = participant.displayName.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Emerald700
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = participant.displayName,
            fontSize = 14.sp,
            color = Stone800,
            modifier = Modifier.weight(1f)
        )

        // Speaking indicator
        if (participant.isSpeaking) {
            Icon(
                Icons.Default.GraphicEq,
                contentDescription = "Spricht",
                tint = Emerald500,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        if (participant.isMuted) {
            Icon(
                Icons.Default.MicOff,
                contentDescription = "Stumm",
                tint = Red500,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        IconButton(
            onClick = onAddFriend,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.PersonAdd,
                contentDescription = "Als Freund hinzufügen",
                tint = Stone400,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
