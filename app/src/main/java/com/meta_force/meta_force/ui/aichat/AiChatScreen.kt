package com.meta_force.meta_force.ui.aichat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.meta_force.meta_force.R
import com.meta_force.meta_force.data.model.ChatSession
import kotlinx.coroutines.launch

// Tailwind-like colors for the theme
private val PrimaryCyan = Color(0xFF22d3ee) // cyan-400
private val PrimaryBlue = Color(0xFF3b82f6) // blue-500
private val DarkBg = Color(0xFF0f172a) // slate-900
private val DarkSurface = Color(0xFF1e293b) // slate-800
private val DarkSurfaceVariant = Color(0xFF334155) // slate-700
private val GradientColors = listOf(PrimaryCyan, PrimaryBlue)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Auto-scroll to bottom when messages change
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.messages.size - 1)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DarkSurface,
                drawerContentColor = Color.White
            ) {
                Text(
                    text = stringResource(R.string.ai_chat_history),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryCyan
                )
                HorizontalDivider(color = DarkSurfaceVariant)
                
                Button(
                    onClick = {
                        viewModel.startNewSession()
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.ai_chat_new_chat))
                }
                
                if (uiState.sessions.isEmpty()) {
                    Text(
                        text = stringResource(R.string.ai_chat_empty_history),
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn {
                        items(uiState.sessions, key = { it.id }) { session ->
                            SwipeToDeleteSessionItem(
                                session = session,
                                isSelected = session.id == uiState.currentSessionId,
                                onClick = {
                                    viewModel.loadSession(session.id)
                                    coroutineScope.launch { drawerState.close() }
                                },
                                onDelete = {
                                    viewModel.deleteSession(session.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = stringResource(R.string.ai_chat_title), 
                            color = PrimaryCyan,
                            fontWeight = FontWeight.ExtraBold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_recent_history),
                                contentDescription = "History",
                                tint = PrimaryCyan
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkSurface.copy(alpha = 0.95f)
                    )
                )
            },
            containerColor = DarkBg
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Error Message
                uiState.error?.let { err ->
                    Surface(color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = err, color = MaterialTheme.colorScheme.onErrorContainer)
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("OK", color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                }

                // Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.messages.isEmpty() && !uiState.isLoading) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(Brush.linearGradient(GradientColors)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "🤖",
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    stringResource(R.string.ai_chat_greeting_title),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    stringResource(R.string.ai_chat_greeting_desc),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.LightGray
                                )
                                
                                Spacer(modifier = Modifier.height(40.dp))
                                
                                val suggestions = listOf(
                                    stringResource(R.string.ai_chat_suggestion_force) to "💪",
                                    stringResource(R.string.ai_chat_suggestion_def) to "🥗",
                                    stringResource(R.string.ai_chat_suggestion_hiit) to "🔥"
                                )
                                
                                suggestions.forEach { (text, icon) ->
                                    SuggestionChip(
                                        onClick = { viewModel.sendMessage(text) },
                                        label = { Text("$icon $text", color = PrimaryCyan, fontWeight = FontWeight.Medium) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = DarkSurface,
                                            labelColor = PrimaryCyan
                                        ),
                                        border = SuggestionChipDefaults.suggestionChipBorder(
                                            enabled = true,
                                            borderColor = PrimaryBlue
                                        ),
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth()
                                            .height(50.dp)
                                    )
                                }
                            }
                        }
                    }

                    items(uiState.messages) { message ->
                        MessageBubble(message = message)
                    }

                    if (uiState.isLoading) {
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                                        .background(DarkSurface)
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = PrimaryCyan
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(stringResource(R.string.ai_chat_thinking), style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Input Area
                Surface(
                    color = DarkSurface,
                    tonalElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 16.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(stringResource(R.string.ai_chat_input_hint), color = Color.Gray) },
                            maxLines = 4,
                            shape = RoundedCornerShape(24.dp),
                            enabled = !uiState.isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = DarkSurfaceVariant,
                                cursorColor = PrimaryCyan,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = DarkBg,
                                unfocusedContainerColor = DarkBg
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        FloatingActionButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                }
                            },
                            containerColor = Color.Transparent,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                            modifier = Modifier
                                .size(56.dp)
                                .background(Brush.linearGradient(GradientColors), shape = RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteSessionItem(
    session: ChatSession,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Color.Red
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_delete),
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        content = {
            Surface(
                onClick = onClick,
                color = if (isSelected) DarkSurfaceVariant else DarkSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = session.title ?: "Chat del ${session.createdAt.take(10)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isSelected) PrimaryCyan else Color.White
                    )
                    Text(
                        text = session.createdAt.take(16).replace("T", " "),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
            HorizontalDivider(color = DarkSurfaceVariant)
        }
    )
}

@Composable
fun MessageBubble(message: UiMessage) {
    val isUser = message.role == "user"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.Bottom)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🤖", style = MaterialTheme.typography.labelLarge)
            }
        }
        
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .background(
                    if (isUser) Brush.linearGradient(GradientColors) else Brush.linearGradient(listOf(DarkSurface, DarkSurface))
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.content,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        if (isUser) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.Bottom)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧑", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
