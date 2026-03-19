package com.calaratjada.insider.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.calaratjada.insider.data.model.ChatRoom
import com.calaratjada.insider.data.service.VivoxConnectionState
import com.calaratjada.insider.ui.theme.*
import com.calaratjada.insider.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomsScreen(
    onRoomClick: (String) -> Unit,
    onFriendsClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onCoinStoreClick: () -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    val rooms by viewModel.rooms.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()
    val coinBalance by viewModel.coinBalance.collectAsStateWithLifecycle()
    val unlockedRooms by viewModel.unlockedRooms.collectAsStateWithLifecycle()

    // Unlock dialog state
    var showUnlockDialog by remember { mutableStateOf<ChatRoom?>(null) }
    var unlockFailed by remember { mutableStateOf(false) }

    // Unlock dialog
    showUnlockDialog?.let { room ->
        AlertDialog(
            onDismissRequest = { showUnlockDialog = null; unlockFailed = false },
            icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Amber500) },
            title = { Text("Raum entsperren", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("\"${room.name}\" kostet 5 Coins für 24 Stunden.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, null, tint = Amber500, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dein Guthaben: $coinBalance Coins", fontWeight = FontWeight.Medium)
                    }
                    if (unlockFailed) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nicht genug Coins!", color = Red500, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.unlockRoom(room.id) { success ->
                            if (success) {
                                showUnlockDialog = null
                                unlockFailed = false
                                viewModel.joinRoom(room)
                                onRoomClick(room.id)
                            } else {
                                unlockFailed = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                ) {
                    Text("Entsperren (5 Coins)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlockDialog = null; unlockFailed = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Community Chat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = nickname ?: "",
                            fontSize = 12.sp,
                            color = Stone400
                        )
                    }
                },
                actions = {
                    // Connection status indicator
                    Surface(
                        shape = CircleShape,
                        color = when (connectionState) {
                            VivoxConnectionState.IN_CHANNEL,
                            VivoxConnectionState.LOGGED_IN -> Emerald500
                            VivoxConnectionState.CONNECTING,
                            VivoxConnectionState.CONNECTED -> Amber400
                            VivoxConnectionState.DISCONNECTED -> Red500
                        },
                        modifier = Modifier.size(10.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    // Coin balance
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Amber400.copy(alpha = 0.15f),
                        onClick = onCoinStoreClick,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.MonetizationOn,
                                null,
                                tint = Amber500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$coinBalance",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Stone800
                            )
                        }
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profil")
                    }
                    IconButton(onClick = onFriendsClick) {
                        Icon(Icons.Default.People, contentDescription = "Freunde")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Stone50
                )
            )
        },
        containerColor = Stone50
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(rooms, key = { it.id }) { room ->
                val isUnlocked = viewModel.isRoomUnlocked(room.id)
                ChatRoomCard(
                    room = room,
                    isLocked = !isUnlocked,
                    onClick = {
                        if (isUnlocked) {
                            viewModel.joinRoom(room)
                            onRoomClick(room.id)
                        } else {
                            showUnlockDialog = room
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ChatRoomCard(
    room: ChatRoom,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) Stone100.copy(alpha = 0.7f) else Stone100
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isLocked) Stone200 else Emerald100,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Gesperrt",
                            tint = Stone500,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = when (room.id) {
                                "general" -> Icons.Default.Forum
                                "strand" -> Icons.Default.WbSunny
                                "gastro" -> Icons.Default.Restaurant
                                "nightlife" -> Icons.Default.NightlightRound
                                "hilfe" -> Icons.Default.Help
                                else -> Icons.Default.Chat
                            },
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (isLocked) Stone500 else Stone900
                )
                Text(
                    text = if (isLocked) "🔒 5 Coins für 24h" else room.description,
                    fontSize = 13.sp,
                    color = if (isLocked) Amber500 else Stone500,
                    maxLines = 1
                )
            }

            if (room.isVoiceEnabled) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice",
                    tint = Emerald500,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Stone400
            )
        }
    }
}
