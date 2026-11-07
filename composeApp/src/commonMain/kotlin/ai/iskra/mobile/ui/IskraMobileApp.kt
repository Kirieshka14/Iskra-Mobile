package ai.iskra.mobile.ui

import ai.iskra.mobile.model.*
import ai.iskra.mobile.network.IskraMobileApi
import ai.iskra.mobile.network.IskraMobileApiException
import ai.iskra.mobile.network.generateOperationKey
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class MobileTierStyle(val bg: Color, val fg: Color, val border: Color, val label: String)

@Composable
fun UserTierBadge(tier: String) {
    val t = tier.lowercase()
    val style = when (t) {
        "max", "ultra", "admin" -> MobileTierStyle(Color(0x33A855F7), Color(0xFFC084FC), Color(0x66A855F7), "MAX")
        "pro", "plus", "vip" -> MobileTierStyle(Color(0x26F59E0B), Color(0xFFF59E0B), Color(0x4DF59E0B), "PRO")
        else -> MobileTierStyle(Color(0x2678716C), Color(0xFFA8A29E), Color(0x3378716C), "FREE")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(style.bg)
            .border(1.dp, style.border, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(style.label, fontSize = 10.sp, fontWeight = FontWeight.Black, color = style.fg)
    }
}

@Composable
fun MobileTelegramIcon(modifier: Modifier = Modifier.size(18.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val scale = w / 24f
        val path = Path().apply {
            moveTo(21.8f * scale, 3.2f * scale)
            lineTo(2.4f * scale, 10.7f * scale)
            quadraticTo(1.4f * scale, 11.1f * scale, 2.5f * scale, 11.7f * scale)
            lineTo(7.6f * scale, 13.3f * scale)
            lineTo(17.9f * scale, 6.8f * scale)
            lineTo(9.5f * scale, 15.0f * scale)
            lineTo(9.1f * scale, 19.8f * scale)
            quadraticTo(9.3f * scale, 20.4f * scale, 9.9f * scale, 20.0f * scale)
            lineTo(12.7f * scale, 17.4f * scale)
            lineTo(17.7f * scale, 21.0f * scale)
            quadraticTo(18.5f * scale, 21.5f * scale, 18.8f * scale, 20.5f * scale)
            lineTo(22.5f * scale, 4.3f * scale)
            quadraticTo(22.8f * scale, 3.1f * scale, 21.8f * scale, 3.2f * scale)
            close()
        }
        drawPath(path, color = Color.White)
    }
}

@Composable
fun MobileTikTokIcon(modifier: Modifier = Modifier.size(18.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val scale = w / 24f
        fun buildNote(ox: Float, oy: Float): Path = Path().apply {
            val hx = 9.0f * scale + ox
            val hy = 16.0f * scale + oy
            val hr = 4.2f * scale
            addOval(androidx.compose.ui.geometry.Rect(hx - hr, hy - hr, hx + hr, hy + hr))
            addRect(androidx.compose.ui.geometry.Rect(11.2f * scale + ox, 5.0f * scale + oy, 13.8f * scale + ox, 16.0f * scale + oy))
            moveTo(13.8f * scale + ox, 9.2f * scale + oy)
            cubicTo(
                16.2f * scale + ox, 11.2f * scale + oy,
                18.5f * scale + ox, 11.8f * scale + oy,
                21.2f * scale + ox, 11.8f * scale + oy
            )
            lineTo(21.2f * scale + ox, 7.8f * scale + oy)
            cubicTo(
                18.2f * scale + ox, 7.8f * scale + oy,
                15.8f * scale + ox, 6.2f * scale + oy,
                14.8f * scale + ox, 4.0f * scale + oy
            )
            lineTo(13.8f * scale + ox, 4.0f * scale + oy)
            close()
        }
        drawPath(buildNote(-1.2f * scale, -0.8f * scale), color = Color(0xFF00F2FE))
        drawPath(buildNote(1.2f * scale, 0.8f * scale), color = Color(0xFFFE2C55))
        drawPath(buildNote(0f, 0f), color = Color.White)
    }
}

@Composable
fun MobileBrandMark(size: Dp = 56.dp, tint: Color = MaterialTheme.colorScheme.onSurface) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w * 0.56f
        val cy = h * 0.44f
        val rOuter = w * 0.36f
        val rInner = w * 0.09f
        val starPath = Path().apply {
            moveTo(cx, cy - rOuter)
            quadraticTo(cx + rInner * 0.4f, cy - rInner * 0.4f, cx + rOuter, cy)
            quadraticTo(cx + rInner * 0.4f, cy + rInner * 0.4f, cx, cy + rOuter)
            quadraticTo(cx - rInner * 0.4f, cy + rInner * 0.4f, cx - rOuter, cy)
            quadraticTo(cx - rInner * 0.4f, cy - rInner * 0.4f, cx, cy - rOuter)
            close()
        }
        drawPath(starPath, color = tint)
        drawArc(
            color = tint,
            startAngle = 35f,
            sweepAngle = 210f,
            useCenter = false,
            topLeft = Offset(w * 0.14f, h * 0.18f),
            size = androidx.compose.ui.geometry.Size(w * 0.68f, h * 0.68f),
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        )
    }
}

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

    LaunchedEffect(Unit) {
        api.warmBackend()
    }

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
                                api = api,
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
    var legalDocToShow by remember { mutableStateOf<MobileLegalDocType?>(null) }
    val scope = rememberCoroutineScope()

    if (legalDocToShow != null) {
        MobileLegalDocumentDialog(
            initialTab = legalDocToShow!!,
            onDismiss = { legalDocToShow = null }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MobileBrandMark(size = 36.dp)
                    Text("IskraAi", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                }

                AnimatedContent(
                    targetState = mode,
                    transitionSpec = {
                        (fadeIn(tween(240)) + slideInVertically(tween(240)) { it / 3 })
                            .togetherWith(fadeOut(tween(160)))
                    },
                    label = "MobileAuthHeading"
                ) { currentMode ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (currentMode == "register") "Создай аккаунт" else "С возвращением",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentMode == "register") "Истории и персонажи ждут тебя" else "Продолжи свои истории",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

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
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                MobileAnimatedEyeSlashIcon(
                                    visible = showPassword,
                                    onClick = { showPassword = !showPassword }
                                )
                            },
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
                            enabled = !busy && otpCode.length == 6 && newPassword.length >= 6,
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text(if (busy) "Сохранение…" else "Сохранить пароль")
                        }
                    }

                    else -> {
                        MobileAnimatedAuthTabs(
                            mode = mode,
                            onSelectMode = { nextMode ->
                                if (mode != nextMode) {
                                    mode = nextMode
                                    error = null
                                }
                            }
                        )

                        AnimatedContent(
                            targetState = mode,
                            transitionSpec = {
                                val dir = if (targetState == "login") 1 else -1
                                (slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { w -> dir * (w / 5) } +
                                    fadeIn(tween(260)))
                                    .togetherWith(
                                        slideOutHorizontally(tween(240, easing = FastOutSlowInEasing)) { w -> -dir * (w / 5) } +
                                            fadeOut(tween(180))
                                    )
                            },
                            label = "MobileAuthFormTransition"
                        ) { currentMode ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (currentMode == "register") {
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
                                        MobileAnimatedEyeSlashIcon(
                                            visible = showPassword,
                                            onClick = { showPassword = !showPassword }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (currentMode == "login") {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        TextButton(onClick = { step = "reset-request"; error = null }) {
                                            Text("Забыли пароль?", fontSize = 12.sp)
                                        }
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth().clickable { termsAccepted = !termsAccepted }
                                        ) {
                                            Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
                                            Text("Мне исполнилось 18 лет, принимаю условия", fontSize = 12.sp)
                                        }
                                        Row(
                                            modifier = Modifier.padding(start = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(
                                                text = "Условия (18+)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.clickable { legalDocToShow = MobileLegalDocType.TERMS }
                                            )
                                            Text("•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(
                                                text = "Конфиденциальность",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.clickable { legalDocToShow = MobileLegalDocType.PRIVACY }
                                            )
                                        }
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
                                                if (currentMode == "login") {
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
                                    enabled = !busy && email.isNotBlank() && password.length >= 6 && (currentMode == "login" || (displayName.isNotBlank() && termsAccepted)),
                                    modifier = Modifier.fillMaxWidth().height(44.dp)
                                ) {
                                    Text(if (busy) "Подождите…" else if (currentMode == "login") "Войти" else "Продолжить")
                                }
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Политика конфиденциальности",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { legalDocToShow = MobileLegalDocType.PRIVACY }
                    )
                    Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "Условия (18+)",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { legalDocToShow = MobileLegalDocType.TERMS }
                    )
                }
            }
        }
    }
}

@Composable
private fun MobileAnimatedAuthTabs(
    mode: String,
    onSelectMode: (String) -> Unit
) {
    val isLogin = mode == "login"
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        val pillWidth = (maxWidth - 4.dp) / 2
        val pillOffset by animateDpAsState(
            targetValue = if (isLogin) pillWidth + 4.dp else 0.dp,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            label = "MobileAuthTabPillOffset"
        )

        Box(
            modifier = Modifier
                .offset(x = pillOffset)
                .width(pillWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(9.dp))
                .background(MaterialTheme.colorScheme.primary)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("register" to "Регистрация", "login" to "Вход").forEach { (tabMode, label) ->
                val active = mode == tabMode
                val textColor by animateColorAsState(
                    targetValue = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 240),
                    label = "MobileAuthTabTextColor"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(9.dp))
                        .clickable { onSelectMode(tabMode) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MobileAnimatedEyeSlashIcon(
    visible: Boolean,
    onClick: () -> Unit
) {
    val openProgress by animateFloatAsState(
        targetValue = if (visible) 1f else 0.72f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "MobileEyeOpen"
    )
    val slashProgress by animateFloatAsState(
        targetValue = if (visible) 0f else 1f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "MobileEyeSlash"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (visible) 1.07f else 1f,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "MobileEyeScale"
    )
    val tintColor by animateColorAsState(
        targetValue = if (visible) Color(0xFFFB7185) else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 240),
        label = "MobileEyeTint"
    )

    Box(
        modifier = Modifier
            .padding(end = 6.dp)
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(20.dp)
                .scale(iconScale)
        ) {
            val w = size.width
            val h = size.height
            val strokePx = w * 0.095f
            val cy = h * 0.5f
            val archHeight = h * 0.34f * openProgress

            val eyePath = Path().apply {
                moveTo(w * 0.08f, cy)
                cubicTo(
                    w * 0.25f, cy - archHeight,
                    w * 0.75f, cy - archHeight,
                    w * 0.92f, cy
                )
                cubicTo(
                    w * 0.75f, cy + archHeight,
                    w * 0.25f, cy + archHeight,
                    w * 0.08f, cy
                )
                close()
            }

            drawPath(
                path = eyePath,
                color = tintColor,
                style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            val pupilRadius = w * (0.095f + 0.035f * openProgress)
            drawCircle(
                color = tintColor,
                radius = pupilRadius,
                center = Offset(w * 0.5f, cy),
                style = Stroke(width = strokePx)
            )

            if (slashProgress > 0.01f) {
                val startX = w * 0.15f
                val startY = h * 0.15f
                val endX = startX + (w * 0.85f - startX) * slashProgress
                val endY = startY + (h * 0.85f - startY) * slashProgress
                drawLine(
                    color = tintColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )
            }
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MobileBrandMark(size = 32.dp)
            Column {
                Text("IskraAi", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Начните новую историю", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

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
    api: IskraMobileApi,
    profile: UserProfile?,
    points: Points?,
    themeMode: String,
    paletteId: String,
    onThemeModeChange: (String) -> Unit,
    onPaletteChange: (String) -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var legalDocToShow by remember { mutableStateOf<MobileLegalDocType?>(null) }
    var devApps by remember { mutableStateOf<List<ClientApp>>(emptyList()) }
    var maxDevKeys by remember { mutableStateOf(3) }
    var newAppName by remember { mutableStateOf("") }
    var newAppPlatform by remember { mutableStateOf("android") }
    var revealedRawKey by remember { mutableStateOf<String?>(null) }
    var keyBusy by remember { mutableStateOf(false) }
    var keyMsg by remember { mutableStateOf<String?>(null) }

    fun reloadKeys() {
        scope.launch {
            runCatching { api.getDeveloperKeys() }
                .onSuccess { res ->
                    devApps = res.apps
                    maxDevKeys = res.maxKeys
                }
        }
    }

    LaunchedEffect(profile?.id) {
        reloadKeys()
    }

    if (legalDocToShow != null) {
        MobileLegalDocumentDialog(
            initialTab = legalDocToShow!!,
            onDismiss = { legalDocToShow = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Профиль", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

        val uriHandler = LocalUriHandler.current
        var selectedPeriod by remember { mutableStateOf("month") }
        val currentTier = (profile?.tier ?: "free").lowercase()

        data class MobilePlanPrice(val label: String, val proPrice: String, val maxPrice: String)
        val periodPricing = mapOf(
            "7d" to MobilePlanPrice("7 дней", "199 ₽", "299 ₽"),
            "month" to MobilePlanPrice("1 месяц", "499 ₽", "699 ₽"),
            "year" to MobilePlanPrice("1 год", "3 990 ₽", "5 490 ₽")
        )
        val pricing = periodPricing[selectedPeriod] ?: periodPricing["month"]!!

        val mobilePalettes = listOf(
            Triple("classic", "Классика", Pair(Color(0xFFF5F5F4), Color(0xFF1A1A1A))),
            Triple("lavender", "Лаванда", Pair(Color(0xFFC9B6DF), Color(0xFF7A5FA8))),
            Triple("terracotta", "Терракота", Pair(Color(0xFFE8A87C), Color(0xFF9D5626))),
            Triple("blue", "Сапфир", Pair(Color(0xFF8EC7EA), Color(0xFF2B6EA8))),
            Triple("emerald", "Изумруд", Pair(Color(0xFF6EE7B7), Color(0xFF059669)))
        )

        // CARD 1: USER INFO
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profile?.displayName?.ifBlank { "Пользователь" } ?: "Пользователь",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    UserTierBadge(profile?.tier ?: "free")
                }
                if (!profile?.email.isNullOrBlank()) {
                    Text(profile?.email ?: "", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    text = "Баллы: ${points?.remaining ?: profile?.points?.remaining ?: 0}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // CARD 2: ТАРИФ (1:1 with website)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Тариф", fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        UserTierBadge(profile?.tier ?: "free")
                    }
                    Text(
                        text = when (currentTier) {
                            "max" -> "Максимум возможностей"
                            "pro" -> "Неограниченные диалоги"
                            else -> "Лимит сообщений, обычная память"
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 3 Periods Segment
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("7d" to "7 дней", "month" to "1 месяц", "year" to "1 год").forEach { (periodKey, label) ->
                        val isSelected = selectedPeriod == periodKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { selectedPeriod = periodKey }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // PRO Action Card
                if (currentTier == "pro") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x2610B981))
                            .border(1.dp, Color(0x4D10B981), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(15.dp))
                            Text("ТАРИФ PRO", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF34D399))
                        }
                        Text("Приобретено", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                    }
                } else if (currentTier == "max") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                            Text("ВКЛЮЧЕНО В MAX", fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                uriHandler.openUri("https://iskraai.run.place/profile")
                            }
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                            Text("ОФОРМИТЬ PRO", fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimary)
                        }
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(pricing.proPrice, fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimary)
                            Text("/ ${pricing.label}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f))
                        }
                    }
                }

                // MAX Action Card
                if (currentTier == "max") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33A855F7))
                            .border(1.dp, Color(0x66A855F7), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(15.dp))
                            Text("ТАРИФ MAX", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFC084FC))
                        }
                        Text("Приобретено", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC084FC))
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                uriHandler.openUri("https://iskraai.run.place/profile")
                            }
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Text(
                                if (currentTier == "pro") "ПЕРЕЙТИ НА MAX" else "ОФОРМИТЬ MAX",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(pricing.maxPrice, fontSize = 14.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                            Text("/ ${pricing.label}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // CARD 3: ОФОРМЛЕНИЕ (1:1 with website)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("ОФОРМЛЕНИЕ", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Темы и акцентная палитра", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Mode Segment
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("dark" to "Тёмная", "light" to "Светлая").forEach { (modeKey, label) ->
                        val active = themeMode == modeKey
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { onThemeModeChange(modeKey) }
                                .padding(vertical = 7.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (modeKey == "dark") Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 2-column Grid of 5 Palettes
                val rowPairs = listOf(
                    listOf(mobilePalettes[0], mobilePalettes[1]),
                    listOf(mobilePalettes[2], mobilePalettes[3]),
                    listOf(mobilePalettes[4])
                )
                rowPairs.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { (id, label, colors) ->
                            val active = paletteId == id
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        if (active) 1.5.dp else 1.dp,
                                        if (active) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onPaletteChange(id) }
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                colorStops = arrayOf(
                                                    0.0f to colors.first,
                                                    0.5f to colors.first,
                                                    0.5f to colors.second,
                                                    1.0f to colors.second
                                                ),
                                                start = Offset.Zero,
                                                end = Offset(40f, 40f)
                                            )
                                        )
                                )
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // CARD 4: СОЦИАЛЬНЫЕ СЕТИ (1:1 with website)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("СОЦИАЛЬНЫЕ СЕТИ", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF229ED9))
                            .clickable {
                                uriHandler.openUri("https://t.me/IskraAi_ru")
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MobileTelegramIcon(modifier = Modifier.size(19.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Канал Telegram", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                            .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
                            .clickable {
                                uriHandler.openUri("https://www.tiktok.com/@iskraai_chat")
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MobileTikTokIcon(modifier = Modifier.size(19.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("TikTok", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("API ключи (${devApps.count { it.status == "active" }}/$maxDevKeys)", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Доступно всем",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF34D399)
                    )
                }

                if (revealedRawKey != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x2234D399))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Ваш новый ключ:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                        Text(revealedRawKey!!, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                OutlinedTextField(
                    value = newAppName,
                    onValueChange = { newAppName = it },
                    label = { Text("Название приложения") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (newAppName.trim().length < 2) {
                            keyMsg = "Введите название от 2 символов"
                            return@Button
                        }
                        scope.launch {
                            keyBusy = true
                            keyMsg = null
                            runCatching { api.createDeveloperKey(newAppName.trim(), newAppPlatform) }
                                .onSuccess { created ->
                                    revealedRawKey = created.rawKey
                                    newAppName = ""
                                    reloadKeys()
                                }
                                .onFailure { e -> keyMsg = e.message }
                            keyBusy = false
                        }
                    },
                    enabled = !keyBusy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (keyBusy) "Создание…" else "+ Создать API ключ")
                }

                devApps.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${item.appName} (${item.platform.uppercase()})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${item.keyPrefix}... • ${item.status}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        runCatching { api.rotateDeveloperKey(item.id) }
                                            .onSuccess { rotated ->
                                                revealedRawKey = rotated.rawKey
                                                reloadKeys()
                                            }
                                    }
                                }
                            ) {
                                Text("↻", fontSize = 13.sp)
                            }
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        runCatching { api.revokeDeveloperKey(item.id, true) }
                                            .onSuccess { reloadKeys() }
                                    }
                                }
                            ) {
                                Text("✕", fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                if (keyMsg != null) {
                    Text(keyMsg!!, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Правовая информация (18+)", fontWeight = FontWeight.Bold)
                OutlinedButton(
                    onClick = { legalDocToShow = MobileLegalDocType.PRIVACY },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Политика конфиденциальности (152-ФЗ)")
                }
                OutlinedButton(
                    onClick = { legalDocToShow = MobileLegalDocType.TERMS },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Пользовательское соглашение (18+)")
                }
            }
        }

        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Выйти из аккаунта")
        }
    }
}
