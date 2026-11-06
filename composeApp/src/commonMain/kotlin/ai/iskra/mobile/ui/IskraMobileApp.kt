package ai.iskra.mobile.ui

import ai.iskra.mobile.model.*
import ai.iskra.mobile.network.IskraMobileApi
import ai.iskra.mobile.network.IskraMobileApiException
import ai.iskra.mobile.network.generateOperationKey
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MobileTab(val title: String) {
    CATALOG("Каталог"),
    CHAT("Диалог"),
    TAVERN("Таверна"),
    STORIES("Истории"),
    PROFILE("Профиль")
}

private val AFFINITY_REGEX = Regex("<!--AFFINITY:[^>]*-->", RegexOption.IGNORE_CASE)
private val AFFINITY_LEVEL_REGEX = Regex("<!--AFFINITY:(?:[^>]*level:)?\\s*([a-zA-Z]+)[^>]*-->", RegexOption.IGNORE_CASE)

fun extractAffinity(raw: String): Pair<String, String?> {
    val match = AFFINITY_LEVEL_REGEX.find(raw)
    val level = match?.groupValues?.getOrNull(1)?.lowercase()
    val clean = raw.replace(AFFINITY_REGEX, "").trim()
    return clean to level
}

fun parseInitialAffinity(systemPrompt: String?): String {
    if (systemPrompt.isNullOrBlank()) return "neutral"
    val line = Regex("Начальный уровень отношений:\\s*([^\\n]+)").find(systemPrompt)?.groupValues?.getOrNull(1)?.lowercase() ?: return "neutral"
    return when {
        line.contains("ненавист") || line.contains("hatred") -> "hatred"
        line.contains("недовер") || line.contains("distrust") -> "distrust"
        line.contains("довери") || line.contains("trust") -> "trust"
        line.contains("любов") || line.contains("love") -> "love"
        else -> "neutral"
    }
}

@Composable
fun MobileFormattedMessageText(text: String, baseColor: Color) {
    val annotated = remember(text, baseColor) {
        buildAnnotatedString {
            val tokenRegex = Regex("(\\*\\*[^*]+?\\*\\*|\\*[^*]+?\\*)")
            var lastIdx = 0
            for (match in tokenRegex.findAll(text)) {
                if (match.range.first > lastIdx) {
                    withStyle(SpanStyle(color = baseColor)) {
                        append(text.substring(lastIdx, match.range.first))
                    }
                }
                val token = match.value
                if (token.startsWith("**") && token.endsWith("**") && token.length >= 4) {
                    withStyle(SpanStyle(color = baseColor, fontWeight = FontWeight.ExtraBold)) {
                        append(token.substring(2, token.length - 2))
                    }
                } else if (token.startsWith("*") && token.endsWith("*") && token.length >= 2) {
                    withStyle(SpanStyle(color = baseColor.copy(alpha = 0.65f))) {
                        append(token.substring(1, token.length - 1))
                    }
                }
                lastIdx = match.range.last + 1
            }
            if (lastIdx < text.length) {
                withStyle(SpanStyle(color = baseColor)) {
                    append(text.substring(lastIdx))
                }
            }
        }
    }
    Text(text = annotated, fontSize = 14.sp, lineHeight = 20.sp)
}

@Composable
fun IskraMobileApp(api: IskraMobileApi = remember { IskraMobileApi() }) {
    var isAuthenticated by remember { mutableStateOf(api.session?.accessToken?.isNotBlank() == true) }
    var themeMode by remember { mutableStateOf(api.session?.themeMode ?: "dark") }
    var paletteId by remember { mutableStateOf(api.session?.paletteId ?: "classic") }
    var activeTab by remember { mutableStateOf(MobileTab.CATALOG) }
    var selectedBot by remember { mutableStateOf<ApiBot?>(null) }
    var bots by remember { mutableStateOf<List<ApiBot>>(emptyList()) }
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var points by remember { mutableStateOf<Points?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            try {
                bots = api.getBots()
                points = api.getPoints()
                profile = api.getProfile()
            } catch (e: IskraMobileApiException) {
                if (e.status == 401) isAuthenticated = false
            } catch (_: Exception) {}
        }
    }

    IskraMobileTheme(themeMode = themeMode, paletteId = paletteId) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (!isAuthenticated) {
                MobileAuthScreen(
                    api = api,
                    onAuthenticated = { isAuthenticated = true }
                )
            } else {
                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                            NavigationBarItem(
                                selected = activeTab == MobileTab.CATALOG,
                                onClick = { activeTab = MobileTab.CATALOG },
                                icon = { Icon(Icons.Default.Explore, contentDescription = null) },
                                label = { Text("Каталог", fontSize = 10.sp) }
                            )
                            NavigationBarItem(
                                selected = activeTab == MobileTab.CHAT,
                                onClick = { activeTab = MobileTab.CHAT },
                                icon = { Icon(Icons.Default.ChatBubbleOutline, contentDescription = null) },
                                label = { Text("Диалог", fontSize = 10.sp) }
                            )
                            NavigationBarItem(
                                selected = activeTab == MobileTab.TAVERN,
                                onClick = { activeTab = MobileTab.TAVERN },
                                icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                                label = { Text("Таверна", fontSize = 10.sp) }
                            )
                            NavigationBarItem(
                                selected = activeTab == MobileTab.STORIES,
                                onClick = { activeTab = MobileTab.STORIES },
                                icon = { Icon(Icons.Default.AutoStories, contentDescription = null) },
                                label = { Text("Истории", fontSize = 10.sp) }
                            )
                            NavigationBarItem(
                                selected = activeTab == MobileTab.PROFILE,
                                onClick = { activeTab = MobileTab.PROFILE },
                                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                                label = { Text("Профиль", fontSize = 10.sp) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (activeTab) {
                            MobileTab.CATALOG -> MobileCatalogScreen(
                                api = api,
                                bots = bots,
                                onBotsUpdated = { bots = it },
                                onSelectBot = { bot ->
                                    selectedBot = bot
                                    activeTab = MobileTab.CHAT
                                }
                            )
                            MobileTab.CHAT -> MobileChatScreen(
                                api = api,
                                bots = bots,
                                initialBot = selectedBot,
                                remainingPoints = points?.remaining,
                                onPointsUpdated = { rem ->
                                    points = (points ?: Points()).copy(remaining = rem)
                                }
                            )
                            MobileTab.TAVERN -> MobileTavernScreen(api = api)
                            MobileTab.STORIES -> MobileStoriesScreen(api = api)
                            MobileTab.PROFILE -> MobileProfileScreen(
                                profile = profile,
                                points = points,
                                themeMode = themeMode,
                                paletteId = paletteId,
                                onThemeModeChange = {
                                    themeMode = it
                                    api.updateThemePreferences(it, paletteId)
                                },
                                onPaletteChange = {
                                    paletteId = it
                                    api.updateThemePreferences(themeMode, it)
                                },
                                onLogout = {
                                    scope.launch {
                                        api.logout()
                                        isAuthenticated = false
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MobileAuthScreen(api: IskraMobileApi, onAuthenticated: () -> Unit) {
    var mode by remember { mutableStateOf("login") }
    var step by remember { mutableStateOf("form") }
    var email by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("I", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Black, fontSize = 26.sp)
                }
                Text("IskraAi", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

                when (step) {
                    "otp" -> {
                        Text("Введите 6-значный код из письма на $email", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { otpCode = it.filter(Char::isDigit).take(6) },
                            label = { Text("Код подтверждения") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (error != null) {
                            Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    busy = true
                                    error = null
                                    try {
                                        api.verifyOtp(email, otpCode)
                                        onAuthenticated()
                                    } catch (e: Exception) {
                                        error = e.message ?: "Код неверен"
                                    } finally {
                                        busy = false
                                    }
                                }
                            },
                            enabled = !busy && otpCode.length == 6,
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text(if (busy) "Проверка…" else "Подтвердить")
                        }
                        TextButton(onClick = { step = "form"; error = null }) {
                            Text("Изменить почту")
                        }
                    }

                    "reset-request" -> {
                        Text("Сброс пароля", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Электронная почта") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (error != null) {
                            Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    busy = true
                                    error = null
                                    try {
                                        api.requestPasswordReset(email)
                                        step = "reset-verify"
                                    } catch (e: Exception) {
                                        error = e.message ?: "Ошибка отправки"
                                    } finally {
                                        busy = false
                                    }
                                }
                            },
                            enabled = !busy && email.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text(if (busy) "Отправка…" else "Получить код")
                        }
                        TextButton(onClick = { step = "form"; error = null }) {
                            Text("Назад ко входу")
                        }
                    }

                    "reset-verify" -> {
                        Text("Новый пароль", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { otpCode = it.filter(Char::isDigit).take(6) },
                            label = { Text("6-значный код") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Новый пароль") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (error != null) {
                            Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    busy = true
                                    error = null
                                    try {
                                        val res = api.resetPassword(email, otpCode, newPassword)
                                        if (res.session != null) {
                                            onAuthenticated()
                                        } else {
                                            password = newPassword
                                            mode = "login"
                                            step = "form"
                                        }
                                    } catch (e: Exception) {
                                        error = e.message ?: "Не удалось сменить пароль"
                                    } finally {
                                        busy = false
                                    }
                                }
                            },
                            enabled = !busy && otpCode.length == 6 && newPassword.length >= 8,
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text(if (busy) "Сохранение…" else "Сохранить пароль")
                        }
                    }

                    else -> {
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp)
                        ) {
                            MobileTabPill("Вход", mode == "login", Modifier.weight(1f)) { mode = "login"; error = null }
                            MobileTabPill("Регистрация", mode == "register", Modifier.weight(1f)) { mode = "register"; error = null }
                        }

                        if (mode == "register") {
                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { displayName = it },
                                label = { Text("Имя или псевдоним") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Электронная почта") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Пароль") },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (mode == "login") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { step = "reset-request"; error = null }) {
                                    Text("Забыли пароль?", fontSize = 12.sp)
                                }
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().clickable { termsAccepted = !termsAccepted }
                            ) {
                                Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
                                Text("Принимаю условия и политику (18+)", fontSize = 12.sp)
                            }
                        }

                        if (error != null) {
                            Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    busy = true
                                    error = null
                                    try {
                                        if (mode == "login") {
                                            api.login(email, password)
                                            onAuthenticated()
                                        } else {
                                            api.requestOtp(email, displayName, password, false)
                                            otpCode = ""
                                            step = "otp"
                                        }
                                    } catch (e: Exception) {
                                        error = e.message ?: "Ошибка авторизации"
                                    } finally {
                                        busy = false
                                    }
                                }
                            },
                            enabled = !busy && email.isNotBlank() && password.length >= 8 && (mode == "login" || (displayName.isNotBlank() && termsAccepted)),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text(if (busy) "Подождите…" else if (mode == "login") "Войти" else "Создать аккаунт")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MobileTabPill(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(9.dp)).clickable(onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
        shape = RoundedCornerShape(9.dp)
    ) {
        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text(text, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
        }
    }
}

@Composable
fun MobileCatalogScreen(
    api: IskraMobileApi,
    bots: List<ApiBot>,
    onBotsUpdated: (List<ApiBot>) -> Unit,
    onSelectBot: (ApiBot) -> Unit
) {
    var search by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("all") }
    var loading by remember { mutableStateOf(bots.isEmpty()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (bots.isEmpty()) {
            loading = true
            try {
                onBotsUpdated(api.getBots())
            } catch (_: Exception) {}
            loading = false
        }
    }

    val filtered = remember(bots, search, selectedGenre) {
        bots.filter { bot ->
            val genreOk = selectedGenre == "all" || bot.genre == selectedGenre
            val queryOk = search.isBlank() ||
                bot.name.contains(search, ignoreCase = true) ||
                (bot.description?.contains(search, ignoreCase = true) == true)
            genreOk && queryOk
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Персонажи IskraAi", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Поиск персонажей…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(GENRE_LABELS.entries.toList()) { (key, label) ->
                FilterChip(
                    selected = selectedGenre == key,
                    onClick = { selectedGenre = key },
                    label = { Text(label, fontSize = 12.sp) }
                )
            }
        }

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                items(filtered, key = { it.id }) { bot ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth().clickable { onSelectBot(bot) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier.size(42.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(bot.name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bot.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(GENRE_LABELS[bot.genre] ?: bot.genre, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val res = api.toggleBotLike(bot.id)
                                                onBotsUpdated(
                                                    bots.map {
                                                        if (it.id == bot.id) it.copy(isLiked = res.isLiked, likesCount = res.likesCount) else it
                                                    }
                                                )
                                            } catch (_: Exception) {}
                                        }
                                    }
                                ) {
                                    Icon(
                                        if (bot.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (bot.isLiked) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(bot.likesCount.toString(), fontSize = 12.sp)
                                }
                            }
                            Text(
                                bot.description ?: "Без описания",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MobileChatScreen(
    api: IskraMobileApi,
    bots: List<ApiBot>,
    initialBot: ApiBot?,
    remainingPoints: Int?,
    onPointsUpdated: (Int) -> Unit
) {
    var activeBot by remember(initialBot) { mutableStateOf(initialBot ?: bots.firstOrNull()) }
    var conversationId by remember { mutableStateOf<String?>(null) }
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var affinity by remember { mutableStateOf("neutral") }
    var input by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }
    var editText by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var sending by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(activeBot?.id) {
        val bot = activeBot ?: return@LaunchedEffect
        loading = true
        error = null
        affinity = parseInitialAffinity(bot.systemPrompt)
        try {
            val convs = api.getConversations()
            val conv = convs.find { it.botId == bot.id } ?: api.createConversation(bot.id)
            conversationId = conv.id
            val page = api.getMessages(conv.id)
            messages = page.items.map { msg ->
                val (clean, level) = extractAffinity(msg.content)
                if (level != null && AFFINITY_LABELS.containsKey(level)) affinity = level
                msg.copy(content = clean, versions = listOf(clean), versionIndex = 0)
            }
        } catch (e: Exception) {
            error = e.message ?: "Ошибка загрузки чата"
        } finally {
            loading = false
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (activeBot == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Выберите персонажа в каталоге", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(activeBot!!.name, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        "${GENRE_LABELS[activeBot!!.genre] ?: activeBot!!.genre} · ${AFFINITY_LABELS[affinity] ?: "Нейтрально"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (remainingPoints != null) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Text(
                            "$remainingPoints",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        if (loading) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 14.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isUser = msg.sender == "user"
                    val isLatestAssistant = !isUser && messages.lastOrNull()?.id == msg.id
                    val isLatestUser = isUser && messages.dropLast(1).lastOrNull()?.id == msg.id

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.widthIn(max = 310.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (editingId == msg.id) {
                                    OutlinedTextField(
                                        value = editText,
                                        onValueChange = { editText = it },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 6.dp)) {
                                        Button(
                                            onClick = {
                                                val convId = conversationId ?: return@Button
                                                val updated = editText.trim()
                                                if (updated.isEmpty()) return@Button
                                                scope.launch {
                                                    sending = true
                                                    try {
                                                        val res = api.editMessage(convId, msg.id, updated)
                                                        val (cleanReply, level) = extractAffinity(res.assistantMessage.content)
                                                        if (level != null && AFFINITY_LABELS.containsKey(level)) affinity = level
                                                        messages = messages.map { m ->
                                                            when (m.id) {
                                                                res.userMessage.id -> res.userMessage.copy(versions = listOf(res.userMessage.content))
                                                                res.assistantMessage.id -> res.assistantMessage.copy(content = cleanReply, versions = listOf(cleanReply))
                                                                else -> m
                                                            }
                                                        }
                                                        onPointsUpdated(res.remaining)
                                                        editingId = null
                                                    } catch (e: Exception) {
                                                        error = e.message
                                                    } finally {
                                                        sending = false
                                                    }
                                                }
                                            },
                                            enabled = !sending
                                        ) { Text("ОК", fontSize = 12.sp) }
                                        TextButton(onClick = { editingId = null }) { Text("Отмена", fontSize = 12.sp) }
                                    }
                                } else {
                                    MobileFormattedMessageText(
                                        text = msg.content,
                                        baseColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (!isUser && msg.versions.size > 1) {
                                TextButton(
                                    onClick = {
                                        val prev = (msg.versionIndex - 1).coerceAtLeast(0)
                                        messages = messages.map {
                                            if (it.id == msg.id) it.copy(versionIndex = prev, content = it.versions[prev]) else it
                                        }
                                    },
                                    enabled = msg.versionIndex > 0
                                ) { Text("<", fontSize = 11.sp) }
                                Text("${msg.versionIndex + 1}/${msg.versions.size}", fontSize = 11.sp)
                                TextButton(
                                    onClick = {
                                        val next = (msg.versionIndex + 1).coerceAtMost(msg.versions.lastIndex)
                                        messages = messages.map {
                                            if (it.id == msg.id) it.copy(versionIndex = next, content = it.versions[next]) else it
                                        }
                                    },
                                    enabled = msg.versionIndex < msg.versions.lastIndex
                                ) { Text(">", fontSize = 11.sp) }
                            }
                            if (isLatestUser && editingId == null) {
                                TextButton(onClick = { editingId = msg.id; editText = msg.content }, enabled = !sending) {
                                    Text("Изменить", fontSize = 11.sp)
                                }
                            }
                            if (isLatestAssistant && messages.size > 1) {
                                TextButton(
                                    onClick = {
                                        val convId = conversationId ?: return@TextButton
                                        scope.launch {
                                            sending = true
                                            try {
                                                val res = api.regenerateMessage(convId, msg.id)
                                                val (cleanReply, level) = extractAffinity(res.assistantMessage.content)
                                                if (level != null && AFFINITY_LABELS.containsKey(level)) affinity = level
                                                val updatedVersions = (msg.versions.ifEmpty { listOf(msg.content) }) + cleanReply
                                                messages = messages.map {
                                                    if (it.id == msg.id) {
                                                        it.copy(
                                                            content = cleanReply,
                                                            versions = updatedVersions,
                                                            versionIndex = updatedVersions.lastIndex
                                                        )
                                                    } else it
                                                }
                                                onPointsUpdated(res.remaining)
                                            } catch (e: Exception) {
                                                error = e.message
                                            } finally {
                                                sending = false
                                            }
                                        }
                                    },
                                    enabled = !sending && (remainingPoints == null || remainingPoints > 0)
                                ) {
                                    Text("Ещё ответ", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }

        Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Сообщение…") },
                    enabled = !sending && editingId == null && (remainingPoints == null || remainingPoints > 0),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        val convId = conversationId ?: return@Button
                        val content = input.trim()
                        if (content.isEmpty() || sending) return@Button
                        input = ""
                        sending = true
                        error = null
                        val tempUser = Message(id = "temp-${generateOperationKey()}", conversationId = convId, sender = "user", content = content)
                        messages = messages + tempUser

                        scope.launch {
                            try {
                                val res = api.sendMessage(convId, content)
                                val (cleanReply, level) = extractAffinity(res.assistantMessage.content)
                                if (level != null && AFFINITY_LABELS.containsKey(level)) affinity = level
                                messages = messages.filterNot { it.id == tempUser.id } + listOf(
                                    res.userMessage.copy(versions = listOf(res.userMessage.content)),
                                    res.assistantMessage.copy(content = cleanReply, versions = listOf(cleanReply))
                                )
                                onPointsUpdated(res.remaining)
                            } catch (e: Exception) {
                                messages = messages.filterNot { it.id == tempUser.id }
                                input = content
                                error = e.message
                            } finally {
                                sending = false
                            }
                        }
                    },
                    enabled = !sending && input.isNotBlank() && editingId == null && (remainingPoints == null || remainingPoints > 0)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Отправить")
                }
            }
        }
    }
}

@Composable
fun MobileTavernScreen(api: IskraMobileApi) {
    var rooms by remember { mutableStateOf<List<ActiveTavernRoom>>(emptyList()) }
    var activeRoomId by remember { mutableStateOf<String?>(null) }
    var roomData by remember { mutableStateOf<TavernRoomData?>(null) }
    var messageInput by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            rooms = api.getActiveTavernRooms()
        } catch (_: Exception) {}
    }

    LaunchedEffect(activeRoomId) {
        val roomId = activeRoomId ?: return@LaunchedEffect
        while (activeRoomId == roomId) {
            try {
                roomData = api.getTavernRoom(roomId)
            } catch (_: Exception) {}
            delay(3500)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Таверна", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    scope.launch {
                        statusText = "Поиск Duo…"
                        try {
                            val res = api.joinTavernQueue("duo")
                            if (res.roomId != null) {
                                activeRoomId = res.roomId
                                statusText = null
                            }
                        } catch (e: Exception) {
                            statusText = e.message
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Найти Duo") }
            OutlinedButton(
                onClick = {
                    scope.launch {
                        statusText = "Поиск Trio…"
                        try {
                            val res = api.joinTavernQueue("trio")
                            if (res.roomId != null) {
                                activeRoomId = res.roomId
                                statusText = null
                            }
                        } catch (e: Exception) {
                            statusText = e.message
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Найти Trio") }
        }

        if (statusText != null) {
            Text(statusText!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
        }

        if (activeRoomId != null && roomData != null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Комната #${activeRoomId!!.take(6)}", fontWeight = FontWeight.Bold)
                TextButton(onClick = { activeRoomId = null; roomData = null }) { Text("Закрыть") }
            }
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(roomData!!.messages, key = { it.id }) { m ->
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(m.senderName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            MobileFormattedMessageText(m.content, MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = { Text("Реплика…") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        val rId = activeRoomId ?: return@Button
                        val txt = messageInput.trim()
                        if (txt.isEmpty()) return@Button
                        messageInput = ""
                        scope.launch {
                            try {
                                api.sendTavernMessage(rId, txt)
                                roomData = api.getTavernRoom(rId)
                            } catch (_: Exception) {}
                        }
                    }
                ) { Text("OK") }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(rooms, key = { it.id }) { room ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth().clickable { activeRoomId = room.id }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("${room.partner.displayName} (${room.mode.uppercase()})", fontWeight = FontWeight.Bold)
                            Text(room.lastMessage?.content ?: "Открыть комнату", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MobileStoriesScreen(api: IskraMobileApi) {
    var stories by remember { mutableStateOf<List<StorySummary>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            stories = api.getStories()
        } catch (_: Exception) {}
        loading = false
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Истории", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(stories, key = { it.id }) { story ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(story.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            if (story.subtitle.isNotBlank()) {
                                Text(story.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(story.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MobileProfileScreen(
    profile: UserProfile?,
    points: Points?,
    themeMode: String,
    paletteId: String,
    onThemeModeChange: (String) -> Unit,
    onPaletteChange: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Профиль", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(profile?.displayName?.ifBlank { "Пользователь" } ?: "Пользователь", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(profile?.email ?: "", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Тариф: ${(profile?.tier ?: "free").uppercase()} · Баллы: ${points?.remaining ?: profile?.points?.remaining ?: 0}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Тема оформления", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = themeMode == "dark", onClick = { onThemeModeChange("dark") }, label = { Text("Тёмная") })
                    FilterChip(selected = themeMode == "light", onClick = { onThemeModeChange("light") }, label = { Text("Светлая") })
                }
                Text("Палитра", fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ISKRA_PALETTES) { p ->
                        FilterChip(
                            selected = paletteId == p.id,
                            onClick = { onPaletteChange(p.id) },
                            label = { Text(p.label, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Выйти из аккаунта")
        }
    }
}
