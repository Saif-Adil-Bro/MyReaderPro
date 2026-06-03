package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(viewModel: ReaderViewModel) {
    val activeThemeMode = viewModel.currentTheme
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (activeThemeMode) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemDark
    }

    MyApplicationTheme(darkTheme = isDark, dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (viewModel.currentScreen) {
                "splash" -> SplashScreen(viewModel)
                "onboarding" -> OnboardingScreen(viewModel)
                "login" -> AuthenticationScreen(viewModel)
                "main" -> MainDashboardScreen(viewModel)
                "details" -> BookDetailsScreen(viewModel)
                "reader" -> ReaderScreen(viewModel)
            }
        }
    }
}

// 1. SPLASH SCREEN
@Composable
fun SplashScreen(viewModel: ReaderViewModel) {
    var startAnimation by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "loader_rotation")
    val angleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
    }

    // Auto navigate to onboarding after 2.5 seconds
    LaunchedEffect(key1 = true) {
        delay(2500)
        viewModel.currentScreen = "onboarding"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TealPrimary, TealDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Elegant Vector Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "App Logo",
                    tint = OrangeAccent,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name with wide tracking
            Text(
                text = viewModel.translate("app_name"),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )

            Text(
                text = "Premium Digital Reading Engine",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Beautiful rotating custom canvas loader
            Canvas(modifier = Modifier.size(48.dp)) {
                drawArc(
                    color = OrangeAccent,
                    startAngle = angleRotation,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color.White.copy(alpha = 0.3f),
                    startAngle = angleRotation + 90f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

// 2. ONBOARDING SCREEN
@Composable
fun OnboardingScreen(viewModel: ReaderViewModel) {
    var currentPage by remember { mutableStateOf(0) }
    val pages = listOf(
        OnboardPageData(
            titleKey = "onboard_1_title",
            descKey = "onboard_1_desc",
            icon = Icons.Default.Public,
            tint = TealLight
        ),
        OnboardPageData(
            titleKey = "onboard_2_title",
            descKey = "onboard_2_desc",
            icon = Icons.Default.CloudDownload,
            tint = OrangeAccent
        ),
        OnboardPageData(
            titleKey = "onboard_3_title",
            descKey = "onboard_3_desc",
            icon = Icons.Default.TrendingUp,
            tint = TealLight
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Skip Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { viewModel.currentScreen = "login" }
            ) {
                Text(
                    text = viewModel.translate("skip"),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.4f))

        // Onboarding Graphics Illustration (Canvas backed with primary colors)
        Box(
            modifier = Modifier
                .size(220.dp)
                .drawBehind {
                    drawCircle(
                        color = pages[currentPage].tint.copy(alpha = 0.12f),
                        radius = size.width / 2f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = pages[currentPage].icon,
                contentDescription = null,
                tint = pages[currentPage].tint,
                modifier = Modifier.size(110.dp)
            )
        }

        Spacer(modifier = Modifier.weight(0.4f))

        // Onboarding Typography
        Text(
            text = viewModel.translate(pages[currentPage].titleKey),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = viewModel.translate(pages[currentPage].descKey),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(0.8f))

        // Bullet Progress Indicators
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            pages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = if (index == currentPage) 24.dp else 8.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentPage) TealPrimary else MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.2f
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Continue Button
        Button(
            onClick = {
                if (currentPage < pages.lastIndex) {
                    currentPage++
                } else {
                    viewModel.currentScreen = "login"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (currentPage == pages.lastIndex) viewModel.translate("get_started") else viewModel.translate("continue"),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class OnboardPageData(
    val titleKey: String,
    val descKey: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tint: Color
)

// 3. AUTHENTICATION (LOGIN / SIGNUP) SCREEN
@Composable
fun AuthenticationScreen(viewModel: ReaderViewModel) {
    var isLoginTab by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showGoogleAccountChooser by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Elegant App Header in Auth
        Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            tint = TealPrimary,
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = viewModel.translate("app_name"),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TealPrimary,
            letterSpacing = 1.sp
        )

        Text(
            text = "Welcome to your premium library experience",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Toggle Tab Selector (Custom styled rounded tabs)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isLoginTab) TealPrimary else Color.Transparent)
                    .clickable { 
                        isLoginTab = true
                        errorMessage = null
                        viewModel.clearAuthError()
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.translate("login"),
                    color = if (isLoginTab) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isLoginTab) TealPrimary else Color.Transparent)
                    .clickable { 
                        isLoginTab = false
                        errorMessage = null
                        viewModel.clearAuthError()
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.translate("signup"),
                    color = if (!isLoginTab) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Display Auth Errors elegantly
        val displayedError = errorMessage ?: viewModel.authError
        if (displayedError != null) {
            Text(
                text = displayedError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp),
                textAlign = TextAlign.Center
            )
        }

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null; viewModel.clearAuthError() },
            label = { Text(viewModel.translate("email")) },
            placeholder = { Text("enter your email address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TealPrimary) },
            singleLine = true,
            enabled = !viewModel.authLoading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name field (Sign Up only)
        if (!isLoginTab) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; errorMessage = null; viewModel.clearAuthError() },
                label = { Text(viewModel.translate("name")) },
                placeholder = { Text("enter your full name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary) },
                singleLine = true,
                enabled = !viewModel.authLoading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null; viewModel.clearAuthError() },
            label = { Text(viewModel.translate("password")) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TealPrimary) },
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(icon, contentDescription = null, tint = TealPrimary)
                }
            },
            singleLine = true,
            enabled = !viewModel.authLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Remember Me & Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    enabled = !viewModel.authLoading,
                    colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
                )
                Text(
                    text = "Remember me",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            TextButton(
                onClick = {
                    errorMessage = "Password reset link simulated and sent to your email!"
                },
                enabled = !viewModel.authLoading
            ) {
                Text(
                    text = "Forgot Password?",
                    fontSize = 14.sp,
                    color = TealPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = {
                if (viewModel.authLoading) return@Button
                if (email.isBlank() || password.isBlank() || (!isLoginTab && name.isBlank())) {
                    errorMessage = "Please fulfill all required text fields."
                } else if (!email.contains("@")) {
                    errorMessage = "Please enter a valid email address."
                } else {
                    errorMessage = null
                    viewModel.clearAuthError()
                    if (isLoginTab) {
                        viewModel.handleLoginWithEmail(email, password)
                    } else {
                        viewModel.handleSignupWithEmail(email, name, password)
                    }
                }
            },
            enabled = !viewModel.authLoading,
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (viewModel.authLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = if (isLoginTab) viewModel.translate("login") else viewModel.translate("signup"),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google / Social and guest logins
        Text(
            text = "--- OR CONNECT VIA ---",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Google Login Button
            OutlinedButton(
                onClick = {
                    if (!viewModel.authLoading) {
                        errorMessage = null
                        viewModel.clearAuthError()
                        showGoogleAccountChooser = true
                    }
                },
                enabled = !viewModel.authLoading,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Launch,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Google",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }

            // Guest Login Button
            Button(
                onClick = {
                    if (!viewModel.authLoading) {
                        errorMessage = null
                        viewModel.clearAuthError()
                        viewModel.handleGuestLogin()
                    }
                },
                enabled = !viewModel.authLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsWalk,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = viewModel.translate("guest"),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (showGoogleAccountChooser) {
            GoogleChooserDialog(
                onDismiss = { showGoogleAccountChooser = false },
                onSelect = { selectedEmail, selectedName ->
                    showGoogleAccountChooser = false
                    viewModel.clearAuthError()
                    viewModel.handleGoogleLogin(selectedEmail, selectedName)
                }
            )
        }
    }
}

@Composable
fun GoogleChooserDialog(
    onDismiss: () -> Unit,
    onSelect: (String, String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Google Logo Simulation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(text = "G", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFF4285F4), modifier = Modifier.padding(end = 1.dp))
                    Text(text = "o", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFFEA4335), modifier = Modifier.padding(end = 1.dp))
                    Text(text = "o", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFFFBBC05), modifier = Modifier.padding(end = 1.dp))
                    Text(text = "g", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFF4285F4), modifier = Modifier.padding(end = 1.dp))
                    Text(text = "l", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFF34A853), modifier = Modifier.padding(end = 1.dp))
                    Text(text = "e", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFFEA4335))
                }

                Text(
                    text = "Sign in with Google",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = "Choose an account to continue to MyReader",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // List of accounts
                val googleAccounts = listOf(
                    Triple("rafuse2024@gmail.com", "Official Admin", "A"),
                    Triple("google_user@gmail.com", "Google Scholar", "G")
                )

                googleAccounts.forEach { (email, name, initial) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelect(email, name) }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TealPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initial,
                                color = TealPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = email,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )
                }

                // Add Google Account Simulator
                var showAddSimulated by remember { mutableStateOf(false) }
                var customEmail by remember { mutableStateOf("") }
                var customName by remember { mutableStateOf("") }

                if (!showAddSimulated) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showAddSimulated = true }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Add another account",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TealPrimary
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("Display Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customEmail,
                            onValueChange = { customEmail = it },
                            label = { Text("Gmail Address") },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showAddSimulated = false }) {
                                Text("Cancel", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (customEmail.isNotBlank() && customEmail.contains("@")) {
                                        val finalName = if (customName.isNotBlank()) customName else customEmail.substringBefore("@")
                                        onSelect(customEmail, finalName)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) {
                                Text("Add", color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// 4. MAIN DASHBOARD SCREEN (Multi tab based)
@Composable
fun MainDashboardScreen(viewModel: ReaderViewModel) {
    val activeUser by viewModel.activeUser.collectAsState(initial = null)
    var showResearchChat by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.clearChat()
                    showResearchChat = true
                },
                containerColor = TealPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 12.dp, end = 4.dp)
                    .testTag("ai_research_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Scholars Research Chat",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (viewModel.currentLanguage) {
                        "Bengali" -> "এআই গবেষণা"
                        "Arabic" -> "جيمي الباحث"
                        else -> "AI Scholar"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = viewModel.selectedDashboardTab == "home",
                    onClick = { viewModel.selectedDashboardTab = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(viewModel.translate("home"), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        selectedTextColor = TealPrimary,
                        indicatorColor = TealPrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = viewModel.selectedDashboardTab == "categories",
                    onClick = { viewModel.selectedDashboardTab = "categories" },
                    icon = { Icon(Icons.Default.Category, contentDescription = null) },
                    label = { Text(viewModel.translate("categories"), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        selectedTextColor = TealPrimary,
                        indicatorColor = TealPrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = viewModel.selectedDashboardTab == "library",
                    onClick = { viewModel.selectedDashboardTab = "library" },
                    icon = { Icon(Icons.Default.LibraryBooks, contentDescription = null) },
                    label = { Text(viewModel.translate("library"), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        selectedTextColor = TealPrimary,
                        indicatorColor = TealPrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = viewModel.selectedDashboardTab == "stats",
                    onClick = { viewModel.selectedDashboardTab = "stats" },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    label = { Text(viewModel.translate("stats"), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        selectedTextColor = TealPrimary,
                        indicatorColor = TealPrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = viewModel.selectedDashboardTab == "profile",
                    onClick = { viewModel.selectedDashboardTab = "profile" },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(viewModel.translate("profile"), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        selectedTextColor = TealPrimary,
                        indicatorColor = TealPrimary.copy(alpha = 0.1f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (viewModel.selectedDashboardTab) {
                "home" -> HomeScreenTab(viewModel, activeUser)
                "categories" -> CategoriesGridTab(viewModel)
                "library" -> MyLibraryTab(viewModel)
                "stats" -> StatsTrackingTab(viewModel, activeUser)
                "profile" -> UserProfileTab(viewModel, activeUser)
            }
        }

        if (showResearchChat) {
            ResearchChatbotDialog(
                viewModel = viewModel,
                onDismiss = { showResearchChat = false }
            )
        }
    }
}

// 4.A HOME CONTENT TAB
@Composable
fun HomeScreenTab(viewModel: ReaderViewModel, activeUser: UserEntity?) {
    val booksList by viewModel.filteredBooks.collectAsState(initial = emptyList())
    val categoriesList by viewModel.categories.collectAsState(initial = emptyList())
    val historyBooks by viewModel.readingHistoryBooks.collectAsState(initial = emptyList())
    val adBlocksHome by viewModel.allAdBlocks.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcoming Card Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Salam & Welcome,",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    Text(
                        text = activeUser?.name ?: "Reader Enthusiast",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                }

                // Beautiful interactive Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(TealPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (activeUser?.name ?: "R").take(1).uppercase(),
                        color = TealPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Search Bar (Dynamic live search)
        item {
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                placeholder = { Text(viewModel.translate("search_hint"), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TealPrimary) },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = TealPrimary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Interactive Simulated Ad Banner (For Free memberships)
        val activeUserHome = activeUser
        val homeAd = adBlocksHome.find { it.id == "banner_home" }
        if (homeAd != null && homeAd.isEnabled) {
            val showAd = activeUserHome == null || activeUserHome.membershipType == "FREE"
            if (showAd) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.recordAdClick(homeAd.id)
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = OrangeAccent.copy(alpha = 0.05f)),
                        border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(OrangeAccent)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("SPONSORED", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("MyReaderPro High Quality Premium", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Upgrade to Premium or VIP tier to hide all banner & interstitial ads permanently!", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                            Button(
                                onClick = {
                                    viewModel.recordAdClick(homeAd.id)
                                    viewModel.selectedDashboardTab = "profile"
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier.height(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Upgrade Now", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                    // Trigger impression log
                    LaunchedEffect(key1 = homeAd.id) {
                        viewModel.recordAdImpression(homeAd.id)
                    }
                }
            } else {
                item {
                    // Premium indicator badge
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = TealLight.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = TealLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("✨ Ad-Free Premium Experience Activated cleanly! All sponsor blocks filtered.", fontSize = 11.sp, color = TealLight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Horizontal Category Capsules
        item {
            Text(
                text = "Explore Categories",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    val isAllSelected = viewModel.selectedCategoryFilter == null
                    FilterChip(
                        selected = isAllSelected,
                        onClick = { viewModel.selectedCategoryFilter = null },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(categoriesList) { cat ->
                    val isSel = viewModel.selectedCategoryFilter == cat.id
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.selectedCategoryFilter = cat.id },
                        label = { Text(cat.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // CONTINUE READING (Dynamic from historical logs)
        if (historyBooks.isNotEmpty() && viewModel.searchQuery.isEmpty() && viewModel.selectedCategoryFilter == null) {
            item {
                Text(
                    text = viewModel.translate("continue_reading"),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(historyBooks) { book ->
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .clickable { viewModel.navigateToBookDetails(book.id) },
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Cover mock
                                CanvasBookCover(
                                    title = book.title, 
                                    author = book.author, 
                                    modifier = Modifier
                                        .size(width = 60.dp, height = 84.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = book.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "by " + book.author,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Reading Progress Indicator
                                    val progressVal = if (book.pages > 0) book.lastReadPosition.toFloat() / book.pages.toFloat() else 0f
                                    val progressPercentage = (progressVal * 100).toInt()
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LinearProgressIndicator(
                                            progress = progressVal,
                                            color = OrangeAccent,
                                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(6.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "$progressPercentage%",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OrangeAccent
                                        )
                                    }
                                    Text(
                                        text = "Page ${book.lastReadPosition} of ${book.pages}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // INTELLIGENT RECOMMENDATION ENGINE (Kindle / Play Books style)
        // -------------------------------------------------------------
        if (viewModel.searchQuery.isEmpty() && viewModel.selectedCategoryFilter == null && booksList.isNotEmpty()) {
            item {
                // Compute content-based recommendation matches
                val readBookIds = historyBooks.map { it.id }.toSet()
                val favoredCategories = historyBooks.map { it.categoryId }.toSet()
                
                // Content recommendation formula:
                // Filter catalog elements matching favoredCategories, excluding already read books.
                var recommendedBooks = booksList.filter { 
                    favoredCategories.contains(it.categoryId) && !readBookIds.contains(it.id) 
                }
                
                // Fallback: If no matches are found, select premium rated books, featured entries, or fallback trends!
                if (recommendedBooks.isEmpty()) {
                    recommendedBooks = booksList.filter { !readBookIds.contains(it.id) }.sortedByDescending { it.downloads }.take(5)
                }

                if (recommendedBooks.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 Matches Recommended For You",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TealLight.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("SMART MATCH", color = TealLight, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(recommendedBooks) { book ->
                            Card(
                                modifier = Modifier
                                    .width(140.dp)
                                    .clickable { viewModel.navigateToBookDetails(book.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(0.5.dp, Color.Gray.copy(alpha = 0.2f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CanvasBookCover(
                                        title = book.title,
                                        author = book.author,
                                        modifier = Modifier
                                            .size(width = 110.dp, height = 150.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = book.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = book.author,
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                    if (book.isPremium) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(OrangeAccent.copy(alpha = 0.15f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("PREMIUM", color = OrangeAccent, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FEATURED BOOKS SLIDER
        if (viewModel.searchQuery.isEmpty() && viewModel.selectedCategoryFilter == null) {
            val featured = booksList.filter { it.isFeatured }
            if (featured.isNotEmpty()) {
                item {
                    Text(
                        text = viewModel.translate("featured"),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(featured) { fBook ->
                            FeaturedBookCard(fBook) {
                                viewModel.navigateToBookDetails(fBook.id)
                            }
                        }
                    }
                }
            }
        }

        // REMAINING BOOKS (Primary listing context)
        item {
            Text(
                text = "Discover Books",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (booksList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No books match your queries.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            items(booksList) { bItem ->
                BookItemRow(bItem) {
                    viewModel.navigateToBookDetails(bItem.id)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FeaturedBookCard(book: BookEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TealPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(OrangeAccent.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Featured Title",
                            color = OrangeAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = book.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "by " + book.author,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${book.rating} (${book.downloads} downloads)",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Cover graphics drawing
            CanvasBookCover(
                title = book.title,
                author = book.author,
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
fun BookItemRow(book: BookEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CanvasBookCover(
                title = book.title,
                author = book.author,
                modifier = Modifier
                    .size(width = 62.dp, height = 90.dp)
                    .clip(RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "by " + book.author,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TealPrimary.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = book.categoryId.replace("_", " ").uppercase(),
                            color = TealPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "•  ${book.pages} pages",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Text(
                        text = "•  ${book.fileSize}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TealPrimary.copy(alpha = 0.6f)
            )
        }
    }
}

// 4.B CATEGORIES FULL GRID TAB
@Composable
fun CategoriesGridTab(viewModel: ReaderViewModel) {
    val categoriesList by viewModel.categories.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Browse Categories",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TealPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(categoriesList) { cat ->
                // Custom visually rich cards based on category keys
                val bgGradients = when (cat.id) {
                    "islamic", "quran_tafsir", "hadith", "fiqh" -> listOf(TealPrimary, TealDark)
                    "novel" -> listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)) // purple
                    "science" -> listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)) // blue
                    "history" -> listOf(Color(0xFFF59E0B), Color(0xFFD97706)) // amber
                    "tech" -> listOf(Color(0xFF10B981), Color(0xFF047857)) // green
                    "bio" -> listOf(Color(0xFFEC4899), Color(0xFFBE185D)) // pink
                    "self_dev" -> listOf(Color(0xFFEF4444), Color(0xFFB91C1C)) // red
                    else -> listOf(Color(0xFF6B7280), Color(0xFF374151)) // grey
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable {
                            viewModel.selectedCategoryFilter = cat.id
                            viewModel.selectedDashboardTab = "home"
                        },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(bgGradients)
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Beautiful vector placeholder for each category
                            val iconData = when (cat.id) {
                                "islamic" -> Icons.Default.Mosque
                                "quran_tafsir" -> Icons.Default.Book
                                "hadith" -> Icons.Default.Book
                                "fiqh" -> Icons.Default.Gavel
                                "novel" -> Icons.Default.MenuBook
                                "science" -> Icons.Default.Science
                                "history" -> Icons.Default.History
                                "tech" -> Icons.Default.DeveloperBoard
                                "bio" -> Icons.Default.Person
                                "self_dev" -> Icons.Default.TrendingUp
                                else -> Icons.Default.ChildCare
                            }

                            Icon(
                                imageVector = iconData,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(34.dp)
                            )

                            Column {
                                Text(
                                    text = cat.name,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Tap to view list",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4.C LIBRARY TAB (Favorites, downloads, bookmarks)
@Composable
fun MyLibraryTab(viewModel: ReaderViewModel) {
    var libraryTabState by remember { mutableStateOf("favorites") } // favorites, downloaded, history

    val favoriteBooks by viewModel.favoriteBooks.collectAsState(initial = emptyList())
    val downloadedBooks by viewModel.downloadedBooks.collectAsState(initial = emptyList())
    val historyBooks by viewModel.readingHistoryBooks.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = viewModel.translate("library"),
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TealPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Triple Tab Filter Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf("favorites", "downloaded", "history")
            tabs.forEach { t ->
                val title = when (t) {
                    "favorites" -> "Favorites"
                    "downloaded" -> "Downloaded"
                    else -> "History"
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (libraryTabState == t) TealPrimary else Color.Transparent)
                        .clickable { libraryTabState = t }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (libraryTabState == t) Color.White else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Content render
        val activeList = when (libraryTabState) {
            "favorites" -> favoriteBooks
            "downloaded" -> downloadedBooks
            else -> historyBooks
        }

        if (activeList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = when (libraryTabState) {
                            "favorites" -> Icons.Default.FavoriteBorder
                            "downloaded" -> Icons.Default.CloudDownload
                            else -> Icons.Default.History
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (libraryTabState) {
                            "favorites" -> "Your favorites list is empty"
                            "downloaded" -> "No books downloaded yet. Go online to download!"
                            else -> "No read history. Jump into a book to track details!"
                        },
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activeList) { book ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateToBookDetails(book.id) },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CanvasBookCover(
                                title = book.title,
                                author = book.author,
                                modifier = Modifier
                                    .size(width = 54.dp, height = 76.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = book.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "by " + book.author,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )

                                if (libraryTabState == "history" && book.pages > 0) {
                                    val progress = book.lastReadPosition.toFloat() / book.pages.toFloat()
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = progress,
                                        color = OrangeAccent,
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height(4.dp)
                                            .clip(CircleShape)
                                    )
                                    Text(
                                        text = "Read page ${book.lastReadPosition} of ${book.pages}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            // Dynamic action helpers in row
                            if (libraryTabState == "downloaded") {
                                IconButton(
                                    onClick = { viewModel.clearDownloadedFile(book.id) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete downloaded file",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = TealPrimary.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4.D READING STATISTICS TAB
@Composable
fun StatsTrackingTab(viewModel: ReaderViewModel, activeUser: UserEntity?) {
    val accomplishments by viewModel.achievements.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "My Statistics",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TealPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Streak Card displaying fire flame custom drawing
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TealPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reading Streak",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${activeUser?.readingStreak ?: 0} ${viewModel.translate("streak")}",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Maintain learning daily. Read today to protect. Keep reading!",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }

                    // Flame Canvas Art decoration
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = OrangeAccent,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }
        }

        // Quantitative Stat Metrics grid values
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatValueChip(
                    label = "Hours Read",
                    value = "%.1f".format(activeUser?.readingHours ?: 0.0f) + " hrs",
                    icon = Icons.Default.AccessTime,
                    tint = TealPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatValueChip(
                    label = "Total Pages",
                    value = "${activeUser?.totalPagesRead ?: 0}",
                    icon = Icons.Default.Book,
                    tint = OrangeAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // CUSTOM CANVAS READING BAR CHART conforming to frontend-design guidelines
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weekly Activity (Minutes Read)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Draw coordinates, paths, and rectangular bar nodes
                    val barMinutes = listOf(15f, 30f, 45f, 20f, 60f, 35f, 10f)
                    val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val chartPrimaryColor = TealPrimary
                    val textSecondaryColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val barCount = 7
                        val barSpacing = canvasWidth / (barCount + 1)
                        val maxMinutes = 60f

                        // Draw baseline axis coordinate
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(0f, canvasHeight - 20.dp.toPx()),
                            end = Offset(canvasWidth, canvasHeight - 20.dp.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Render each bar beautifully with rounded top paths
                        for (i in 0 until barCount) {
                            val min = barMinutes[i]
                            val barX = barSpacing * (i + 1)
                            val barHeightRatio = min / maxMinutes
                            val barWidth = 20.dp.toPx()
                            val barHeight = (canvasHeight - 30.dp.toPx()) * barHeightRatio

                            drawRect(
                                color = chartPrimaryColor,
                                topLeft = Offset(barX - (barWidth / 2f), canvasHeight - 20.dp.toPx() - barHeight),
                                size = Size(barWidth, barHeight)
                            )

                            // Tweak top outline with Orange accent value if Friday (active reading height peak)
                            if (i == 4) {
                                drawRect(
                                    color = OrangeAccent,
                                    topLeft = Offset(barX - (barWidth / 2f), canvasHeight - 20.dp.toPx() - barHeight),
                                    size = Size(barWidth, 6.dp.toPx())
                                )
                            }
                        }
                    }

                    // Labels Row under Canvas chart
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weekdays.forEach { wr ->
                            Text(
                                text = wr,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // ACHIEVEMENT SYSTEM VISUAL BADGES
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Achievement Badges",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(OrangeAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    val completedCount = accomplishments.count { it.isUnlocked }
                    Text(
                        text = "$completedCount/${accomplishments.size} Unlocked",
                        color = OrangeAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(accomplishments) { badge ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.5f
                    )
                ),
                border = if (badge.isUnlocked) BorderStroke(1.dp, TealLight.copy(alpha = 0.3f)) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (badge.isUnlocked) OrangeAccent.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (badge.iconName) {
                                "ic_book" -> Icons.Default.WorkspacePremium
                                "ic_streak", "ic_streak_30" -> Icons.Default.Whatshot
                                "ic_download" -> Icons.Default.FileDownloadDone
                                else -> Icons.Default.Stars
                            },
                            contentDescription = null,
                            tint = if (badge.isUnlocked) OrangeAccent else Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = badge.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            )
                        )
                        Text(
                            text = badge.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            lineHeight = 15.sp
                        )
                    }

                    if (badge.isUnlocked) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Badge Unlocked",
                            tint = TealLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatValueChip(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(tint.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// 4.E USER PROFILE TAB (Theme selection, notification centers, language)
@Composable
fun UserProfileTab(viewModel: ReaderViewModel, activeUser: UserEntity?) {
    val alerts by viewModel.notifications.collectAsState(initial = emptyList())
    var editNameState by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(activeUser?.name ?: "") }

    var expandedRequests by remember { mutableStateOf(false) }
    var expandedDMCA by remember { mutableStateOf(false) }
    var expandedMonetization by remember { mutableStateOf(false) }
    var expandedBackup by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "My Profile Information",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TealPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Large Premium User Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(TealPrimary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (activeUser?.name ?: "R").take(1).uppercase(),
                            color = TealPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (editNameState) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                value = editedName,
                                onValueChange = { editedName = it },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                viewModel.saveProfileName(editedName)
                                editNameState = false
                            }) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = TealPrimary)
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = activeUser?.name ?: "Reader Enthusiast",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = { editNameState = true; editedName = activeUser?.name ?: "" }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Name", modifier = Modifier.size(16.dp), tint = TealPrimary)
                            }
                        }
                    }

                    Text(
                        text = activeUser?.email ?: "guest@myreader.com",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (activeUser?.isGuest == true) OrangeAccent.copy(alpha = 0.15f) else TealPrimary.copy(
                                    alpha = 0.15f
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (activeUser?.isGuest == true) "GUEST MODE ACCOUNT" else " PREMIUM FULL SYNCED USER",
                            color = if (activeUser?.isGuest == true) OrangeAccent else TealPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // MULTI LANGUAGE SELECTOR
        item {
            Text(
                text = "Language Configuration",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val langs = listOf("English", "Bengali", "Arabic")
                langs.forEach { ln ->
                    val isS = viewModel.currentLanguage == ln
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isS) TealPrimary else MaterialTheme.colorScheme.surface)
                            .clickable { viewModel.currentLanguage = ln }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ln,
                            color = if (isS) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // APP THEME SELECTOR
        item {
            Text(
                text = "Custom Visual Theme Mode",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val thMode = listOf("Light", "Dark", "System")
                thMode.forEach { tm ->
                    val isS = viewModel.currentTheme == tm
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isS) TealPrimary else MaterialTheme.colorScheme.surface)
                            .clickable { viewModel.currentTheme = tm }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tm,
                            color = if (isS) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 1. REQUEST A BOOK HUB (TACTILE CARD)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .clickable { expandedRequests = !expandedRequests }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Request Book",
                        tint = TealPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Book Requests & Tracking System",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Text(
                            text = "Request unavailable books, track status and manage indexing.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = if (expandedRequests) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TealPrimary
                    )
                }
            }
        }

        if (expandedRequests) {
            item {
                BookRequestHub(viewModel, activeUser)
            }
        }

        // 2. DMCA & COPYRIGHT DISPUTE HUB
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .clickable { expandedDMCA = !expandedDMCA }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Copyright DMCA",
                        tint = Color.Red,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DMCA & Copyright Takedown Center",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Text(
                            text = "Submit legal trademark infringement claims and review audits.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = if (expandedDMCA) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }
        }

        if (expandedDMCA) {
            item {
                CopyrightClaimHub(viewModel, activeUser)
            }
        }

        // 3. SAAS MONETIZATION, SUBSCRIPTIONS & ADS SWITCHBOARD
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TealLight.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, TealLight.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .clickable { expandedMonetization = !expandedMonetization }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Monetization Options",
                        tint = TealLight,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Revenue, Ad Network & Member Levels",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealLight
                        )
                        Text(
                            text = "Setup commercial subscriptions, track donations & block ads.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = if (expandedMonetization) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TealLight
                    )
                }
            }
        }

        if (expandedMonetization) {
            item {
                MonetizationHub(viewModel, activeUser)
            }
        }

        // 4. DATABASE BACKUP & CLOUD RESTORE HUB
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .clickable { expandedBackup = !expandedBackup }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Backups",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "System Backup & JSON Import/Export",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Manage backup signatures and restore database coordinates.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = if (expandedBackup) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.DarkGray
                    )
                }
            }
        }

        if (expandedBackup) {
            item {
                BackupAndRestoreHub(viewModel)
            }
        }

        // 5. LIVE ORIGINAL ADMIN BOOK CONSOLE
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = OrangeAccent.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .clickable { viewModel.showAdminPanel = !viewModel.showAdminPanel }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Area",
                        tint = OrangeAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Catalog Publisher Web Console",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangeAccent
                        )
                        Text(
                            text = "Emulate back-end content updates, user logs, and categories.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = if (viewModel.showAdminPanel) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = OrangeAccent
                    )
                }
            }
        }

        // Live Admin Panel expansion layout
        if (viewModel.showAdminPanel) {
            item {
                AdminConsoleDialog(viewModel)
            }
        }

        // NOTIFICATION LOG CENTRE
        item {
            Text(
                text = "System Alert Center",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (alerts.isEmpty()) {
            item {
                Text(
                    text = "No notifications available yet.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        } else {
            items(alerts) { nt ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (nt.isRead) Color.Gray else TealLight)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = nt.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = nt.message,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                        )
                    }
                }
            }
        }

        // Logout Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.handleLogout() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log Out Account",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// 4.E.a LIVE ADMIN CONSOLE PANEL EMULATION
@Composable
fun AdminConsoleDialog(viewModel: ReaderViewModel) {
    var adminTab by remember { mutableStateOf("analytics") } // analytics, books, categories, users, alerts, claims, requests

    // Collected states from VM
    val books by viewModel.allBooks.collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    val users by viewModel.allUsers.collectAsState(initial = emptyList())
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())
    val copyrightClaims by viewModel.allCopyrightClaims.collectAsState(initial = emptyList())
    val bookRequests by viewModel.allBookRequests.collectAsState(initial = emptyList())

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Admin Simulation Command Suite",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeAccent
                    )
                }
            }
            Text(
                text = "Back-end dashboard simulation. All actions modify the local Room database instantly.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Scrollable tabs row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "analytics" to "📈 Stats",
                    "books" to "📚 Books",
                    "categories" to "📁 Categories",
                    "users" to "👥 Users",
                    "alerts" to "🔔 Alerts",
                    "claims" to "⚖️ DMCA",
                    "requests" to "📨 Requests"
                ).forEach { (id, label) ->
                    val isSelected = adminTab == id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) OrangeAccent else MaterialTheme.colorScheme.surface)
                            .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .clickable { adminTab = id }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Content Selection
            when (adminTab) {
                "analytics" -> AdminAnalyticsTab(books, categories, users, copyrightClaims, bookRequests, notifications)
                "books" -> AdminBooksTab(viewModel, books, categories)
                "categories" -> AdminCategoriesTab(viewModel, categories)
                "users" -> AdminUsersTab(viewModel, users)
                "alerts" -> AdminAlertsTab(viewModel, notifications)
                "claims" -> AdminClaimsTab(viewModel, copyrightClaims)
                "requests" -> AdminRequestsTab(viewModel, bookRequests)
            }
        }
    }
}

@Composable
fun AdminAnalyticsTab(
    books: List<BookEntity>,
    categories: List<CategoryEntity>,
    users: List<UserEntity>,
    claims: List<CopyrightClaimEntity>,
    requests: List<BookRequestEntity>,
    notifications: List<NotificationEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("📈 System Status Metrics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)

        // Grid-like layout for stat cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatItem(
                label = "Total Books",
                value = "${books.size}",
                caption = "${books.filter { it.isFeatured }.size} Featured",
                icon = Icons.Default.Book,
                color = TealPrimary,
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = "Categories",
                value = "${categories.size}",
                caption = "Active subjects",
                icon = Icons.Default.FolderOpen,
                color = OrangeAccent,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatItem(
                label = "Active Accounts",
                value = "${users.size}",
                caption = "${users.filter { it.role == "ADMIN" }.size} Admins | ${users.filter { it.role == "MODERATOR" }.size} Mods",
                icon = Icons.Default.People,
                color = TealLight,
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = "DMCA Claims",
                value = "${claims.size}",
                caption = "${claims.filter { it.status == "PENDING" }.size} Pending",
                icon = Icons.Default.Security,
                color = Color.Red,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatItem(
                label = "Live Requests",
                value = "${requests.filter { it.status == "PENDING" }.size}",
                caption = "${requests.filter { it.status == "COMPLETED" }.size} Handled",
                icon = Icons.Default.Email,
                color = Color.Magenta,
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = "Alerts Broadcasted",
                value = "${notifications.size}",
                caption = "Active board",
                icon = Icons.Default.Campaign,
                color = Color.Blue,
                modifier = Modifier.weight(1f)
            )
        }

        // Plan distribution simulator
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Membership Split (Demographics)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                val freeCount = users.filter { it.membershipType == "FREE" }.size
                val premiumCount = users.filter { it.membershipType == "PREMIUM" }.size
                val vipCount = users.filter { it.membershipType == "VIP" }.size
                val total = (freeCount + premiumCount + vipCount).toFloat().coerceAtLeast(1f)

                ProgressBarWithLabel(label = "VIP Members (${vipCount})", progress = vipCount / total, color = OrangeAccent)
                Spacer(modifier = Modifier.height(6.dp))
                ProgressBarWithLabel(label = "Premium Members (${premiumCount})", progress = premiumCount / total, color = TealPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                ProgressBarWithLabel(label = "Free Tier (${freeCount})", progress = freeCount / total, color = Color.Gray)
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    caption: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(caption, fontSize = 9.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProgressBarWithLabel(label: String, progress: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("${(progress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { progress },
            color = color,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            strokeCap = StrokeCap.Round,
            modifier = Modifier.fillMaxWidth().height(6.dp)
        )
    }
}

@Composable
fun AdminBooksTab(
    viewModel: ReaderViewModel,
    books: List<BookEntity>,
    categories: List<CategoryEntity>
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var catId by remember { mutableStateOf("novel") }
    var desc by remember { mutableStateOf("") }
    var pagesCount by remember { mutableStateOf("120") }
    var fileSizeText by remember { mutableStateOf("1.5 MB") }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("📐 Publish New Volume", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)

        if (toastMessage != null) {
            Text(toastMessage!!, color = TealLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Book Title *", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author / Reporter *", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Category selection chip row
        Text("Classification Group *", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = catId == cat.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) TealPrimary else MaterialTheme.colorScheme.surface)
                        .border(0.5.dp, if (isSelected) Color.Transparent else Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable { catId = cat.id }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat.name,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Syllated Synopsis Note *", fontSize = 12.sp) },
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = pagesCount,
                onValueChange = { pagesCount = it },
                label = { Text("Pages *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = fileSizeText,
                onValueChange = { fileSizeText = it },
                label = { Text("File Size *") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                if (title.isBlank() || author.isBlank() || desc.isBlank()) {
                    toastMessage = "Error: Please complete all required specifications."
                } else {
                    viewModel.adminAddBook(
                        title = title,
                        author = author,
                        categoryId = catId,
                        description = desc,
                        pages = pagesCount.toIntOrNull() ?: 120,
                        size = fileSizeText
                    )
                    toastMessage = "Success: Book published & system crawlers alerted!"
                    title = ""
                    author = ""
                    desc = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Publish to Repository Portal", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("📚 Active Catalog Manager", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)

        // List existing books with a retires modifier
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val activeBooks = books.filter { it.categoryId != "hidden" }
                if (activeBooks.isEmpty()) {
                    item {
                        Text("No active catalog books found.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))
                    }
                } else {
                    items(activeBooks) { book ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(book.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("by ${book.author} | Group: ${book.categoryId}", fontSize = 10.sp, color = Color.Gray)
                            }
                            IconButton(
                                onClick = {
                                    viewModel.adminDeleteBook(book.id)
                                    toastMessage = "Book '${book.title}' retired successfully."
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Retire", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCategoriesTab(
    viewModel: ReaderViewModel,
    categories: List<CategoryEntity>
) {
    var catIdInput by remember { mutableStateOf("") }
    var catNameInput by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("ic_star") }
    var catMessage by remember { mutableStateOf<String?>(null) }

    val iconChoices = listOf(
        "ic_star" to Icons.Default.Stars,
        "ic_book" to Icons.Default.LibraryBooks,
        "ic_general" to Icons.Default.FolderOpen,
        "ic_school" to Icons.Default.School,
        "ic_heart" to Icons.Default.Favorite
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("📁 Register Category Subject", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)

        if (catMessage != null) {
            Text(catMessage!!, color = TealLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedTextField(
            value = catIdInput,
            onValueChange = { catIdInput = it.lowercase().trim() },
            label = { Text("Category ID Code * (lowercase, unique)", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = catNameInput,
            onValueChange = { catNameInput = it },
            label = { Text("Category Display Name * (e.g. Science)", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Select Subject Visual Symbol *", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            iconChoices.forEach { (name, vec) ->
                val isSel = selectedIcon == name
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) OrangeAccent else MaterialTheme.colorScheme.surface)
                        .border(1.dp, if (isSel) Color.Transparent else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { selectedIcon = name }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = vec, contentDescription = null, tint = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        Button(
            onClick = {
                if (catIdInput.isBlank() || catNameInput.isBlank()) {
                    catMessage = "Error: Classification ID and Display name are required parameters."
                } else {
                    viewModel.adminAddCategory(catIdInput, catNameInput, selectedIcon)
                    catMessage = "Success: Category Group '$catNameInput' registered!"
                    catIdInput = ""
                    catNameInput = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Register Category Group", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text("📂 Configured Subject Groups", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 180.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (categories.isEmpty()) {
                    item {
                        Text("No custom categories registered.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))
                    }
                } else {
                    items(categories) { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(OrangeAccent.copy(alpha = 0.15f), RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val vc = iconChoices.find { it.first == cat.iconName }?.second ?: Icons.Default.FolderOpen
                                    Icon(imageVector = vc, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(cat.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Database ID: ${cat.id}", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            IconButton(
                                onClick = {
                                    viewModel.adminDeleteCategory(cat.id)
                                    catMessage = "Category '${cat.name}' deleted from indices."
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUsersTab(
    viewModel: ReaderViewModel,
    users: List<UserEntity>
) {
    var searchQuery by remember { mutableStateOf("") }
    var editingUserEmail by remember { mutableStateOf<String?>(null) }
    var editName by remember { mutableStateOf("") }
    var editRole by remember { mutableStateOf("USER") }
    var editPlan by remember { mutableStateOf("FREE") }
    var operationMsg by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("👥 System Accounts & Role Access", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search accounts (name/email)", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangeAccent) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (operationMsg != null) {
            Text(operationMsg!!, color = TealLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        // Active editing area
        if (editingUserEmail != null) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, TealLight.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Manage Account: $editingUserEmail", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                        IconButton(onClick = { editingUserEmail = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("User Real Name", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Role toggle selection
                    Text("System Security Level Role", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("USER", "MODERATOR", "ADMIN").forEach { rl ->
                            val isSel = editRole == rl
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) OrangeAccent else MaterialTheme.colorScheme.surface)
                                    .clickable { editRole = rl }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(rl, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Membership plan selection
                    Text("SaaS Membership Access level", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("FREE", "PREMIUM", "VIP").forEach { plan ->
                            val isSel = editPlan == plan
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) TealPrimary else MaterialTheme.colorScheme.surface)
                                    .clickable { editPlan = plan }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(plan, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.adminModifyUser(editingUserEmail!!, editName, editRole, editPlan)
                            operationMsg = "Success: Settings synced down for $editingUserEmail!"
                            editingUserEmail = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text("Save Account Specifications", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Search listings list
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val filtered = users.filter { 
                    it.email.contains(searchQuery, ignoreCase = true) ||
                    it.name.contains(searchQuery, ignoreCase = true)
                }

                if (filtered.isEmpty()) {
                    item {
                        Text("No matching accounts search found.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(10.dp))
                    }
                } else {
                    items(filtered) { usr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(usr.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(usr.email, fontSize = 10.sp, color = Color.Gray)
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                when (usr.role) {
                                                    "ADMIN" -> Color.Red.copy(alpha = 0.15f)
                                                    "MODERATOR" -> Color.Blue.copy(alpha = 0.15f)
                                                    else -> Color.Gray.copy(alpha = 0.15f)
                                                }
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = usr.role,
                                            color = when (usr.role) {
                                                "ADMIN" -> Color.Red
                                                "MODERATOR" -> Color.Blue
                                                else -> Color.DarkGray
                                            },
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(OrangeAccent.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(usr.membershipType, color = OrangeAccent, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    editingUserEmail = usr.email
                                    editName = usr.name
                                    editRole = usr.role
                                    editPlan = usr.membershipType
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit User", tint = TealPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAlertsTab(
    viewModel: ReaderViewModel,
    notifications: List<NotificationEntity>
) {
    var bTitle by remember { mutableStateOf("") }
    var bMsg by remember { mutableStateOf("") }
    var sendStatus by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("🔔 System Alert Broadcaster", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)

        if (sendStatus != null) {
            Text(sendStatus!!, color = TealLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedTextField(
            value = bTitle,
            onValueChange = { bTitle = it },
            label = { Text("Broadcast Bulletin Headline *", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = bMsg,
            onValueChange = { bMsg = it },
            label = { Text("Broadcast Body / Detailed message *", fontSize = 12.sp) },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (bTitle.isBlank() || bMsg.isBlank()) {
                    sendStatus = "Error: Headline and Body details are mandatory."
                } else {
                    viewModel.adminBroadcastNotification(bTitle, bMsg)
                    sendStatus = "Success: Notification broadcasted immediately!"
                    bTitle = ""
                    bMsg = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Broadcast Message", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text("🗂️ Currently Active Alerts Logs", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (notifications.isEmpty()) {
                    item {
                        Text("No active broadcasts reported.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))
                    }
                } else {
                    items(notifications) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(item.message, fontSize = 10.sp, color = Color.DarkGray)
                                Text("Date: 2026-06-02", fontSize = 7.sp, color = Color.Gray)
                            }
                            IconButton(
                                onClick = {
                                    viewModel.adminDeleteNotification(item.id)
                                    sendStatus = "Alert ID ${item.id} deleted."
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Status Alert", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminClaimsTab(
    viewModel: ReaderViewModel,
    claims: List<CopyrightClaimEntity>
) {
    var processMsg by remember { mutableStateOf<String?>(null) }
    var claimNote by remember { mutableStateOf("") }
    var selectedClaimId by remember { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("⚖️ DMCA Claims Reviews Desk", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)

        if (processMsg != null) {
            Text(processMsg!!, color = TealLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        if (selectedClaimId != null) {
            val claim = claims.find { it.id == selectedClaimId }
            if (claim != null) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Resolve DMCA Claim ID: ${claim.id}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                            IconButton(onClick = { selectedClaimId = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }

                        Text("Claimant: ${claim.fullName} | Org: ${claim.organizationName}", fontSize = 11.sp)
                        Text("Affected Content Title: ${claim.contentTitle}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Detailed Argument: ${claim.description}", fontSize = 10.sp, color = Color.Gray)

                        OutlinedTextField(
                            value = claimNote,
                            onValueChange = { claimNote = it },
                            label = { Text("Lawful verdict decisions notes", fontSize = 11.sp) },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateCopyrightStatus(claim.id, "APPROVED", claimNote, hideContent = true)
                                    processMsg = "License verified. Infringing volume secluded!"
                                    selectedClaimId = null
                                    claimNote = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text("Approve & Seclude", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    viewModel.updateCopyrightStatus(claim.id, "REJECTED", claimNote, hideContent = false)
                                    processMsg = "Claim rejected. Content remained active."
                                    selectedClaimId = null
                                    claimNote = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text("Dismiss Claim", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (claims.isEmpty()) {
                    item {
                        Text("No active claims pending resolution.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))
                    }
                } else {
                    items(claims) { claim ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(claim.contentTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                                Text("Submitted Name: ${claim.fullName} (${claim.organizationName})", fontSize = 10.sp, color = Color.Gray)
                                Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                when (claim.status) {
                                                    "PENDING" -> OrangeAccent.copy(alpha = 0.15f)
                                                    "APPROVED" -> Color.Red.copy(alpha = 0.15f)
                                                    else -> TealLight.copy(alpha = 0.15f)
                                                }
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            claim.status,
                                            color = when (claim.status) {
                                                "PENDING" -> OrangeAccent
                                                "APPROVED" -> Color.Red
                                                else -> TealLight
                                            },
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (claim.temporaryHidden) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color.Red.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("HIDDEN", color = Color.Red, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    selectedClaimId = claim.id
                                    claimNote = claim.decisionNotes
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Decide", tint = TealPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminRequestsTab(
    viewModel: ReaderViewModel,
    requests: List<BookRequestEntity>
) {
    var reqMsg by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("📨 Book Requests Moderator Review", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)

        if (reqMsg != null) {
            Text(reqMsg!!, color = TealLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (requests.isEmpty()) {
                    item {
                        Text("No active book catalog requests found.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))
                    }
                } else {
                    items(requests) { req ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(req.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("by ${req.author} | Submitter: ${req.userEmail}", fontSize = 10.sp, color = Color.Gray)
                                if (req.notes.isNotBlank()) {
                                    Text("Note: ${req.notes}", fontSize = 9.sp, fontStyle = FontStyle.Italic, color = Color.DarkGray)
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (req.status) {
                                                "PENDING" -> OrangeAccent.copy(alpha = 0.15f)
                                                "COMPLETED" -> TealLight.copy(alpha = 0.15f)
                                                else -> Color.Red.copy(alpha = 0.15f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        req.status,
                                        color = when (req.status) {
                                            "PENDING" -> OrangeAccent
                                            "COMPLETED" -> TealLight
                                            else -> Color.Red
                                        },
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (req.status == "PENDING") {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            viewModel.updateBookRequestStatus(req.id, "COMPLETED")
                                            reqMsg = "Request COMPLETED. Book generated and published!"
                                        },
                                        modifier = Modifier.size(28.dp).background(Color.Green.copy(alpha = 0.2f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Satisfy", tint = Color.DarkGray, modifier = Modifier.size(14.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.updateBookRequestStatus(req.id, "REJECTED")
                                            reqMsg = "Book Request set to REJECTED."
                                        },
                                        modifier = Modifier.size(28.dp).background(Color.Red.copy(alpha = 0.2f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Reject", tint = Color.Red, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 5. BOOK DETAILS SCREEN
@Composable
fun BookDetailsScreen(viewModel: ReaderViewModel) {
    val bookState = viewModel.selectedBook.collectAsState(initial = null)
    val book = bookState.value

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TealPrimary)
        }
        return
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Book Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back",
                            tint = TealPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Book cover preview
            Box(
                modifier = Modifier
                    .size(width = 160.dp, height = 230.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(6.dp, RoundedCornerShape(12.dp))
            ) {
                CanvasBookCover(title = book.title, author = book.author, modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Book basic values
            Text(
                text = book.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "by " + book.author,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Highlight Specs block (Rating, sizes)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Grade, contentDescription = null, tint = OrangeAccent)
                    Text(
                        text = "${book.rating}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Rating",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.InsertPageBreak, contentDescription = null, tint = TealPrimary)
                    Text(
                        text = "${book.pages}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Pages",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = TealLight)
                    Text(
                        text = book.language,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Language",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.Gray)
                    Text(
                        text = book.fileSize,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Size",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Download manager status box
            DownloadManagerPanel(book, viewModel)

            Spacer(modifier = Modifier.height(20.dp))

            // Read action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Read button
                Button(
                    onClick = { viewModel.navigateToReader(book.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = "Read Book", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(viewModel.translate("read_now"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                // Favorite click action
                IconButton(
                    onClick = { viewModel.toggleFavorite(book.id) },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = if (book.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite Toggle",
                        tint = if (book.isFavorite) Color.Red else TealPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Share simulation
                var showShareMsg by remember { mutableStateOf(false) }
                IconButton(
                    onClick = { showShareMsg = true },
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (showShareMsg) {
                    AlertDialog(
                        onDismissRequest = { showShareMsg = false },
                        confirmButton = {
                            TextButton(onClick = { showShareMsg = false }) { Text("OK") }
                        },
                        title = { Text("Share '${book.title}'") },
                        text = { Text("Secure share link copied to clipboard!\nShare this premium digital book with classmates easily.") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Book Description content
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Synopsis & Context",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = book.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Report Issues / Typos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Notice translation errors? Click to report directly to editing staff.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Gemini Intelligence Book Summary Card ---
            val summaryText = viewModel.bookSummaryText
            val summaryLoading = viewModel.bookSummaryLoading
            val summaryError = viewModel.bookSummaryError

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TealPrimary.copy(alpha = 0.05f)
                ),
                border = BorderStroke(1.5.dp, TealPrimary.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gemini_summary_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini AI",
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (viewModel.currentLanguage) {
                                    "Bengali" -> "জেমিনি স্মার্ট সারাংশ ✨"
                                    "Arabic" -> "ملخص جيمي الذكي ✨"
                                    else -> "Gemini Intelligent Summary ✨"
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = TealPrimary
                            )
                        }

                        // Sparkle Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TealPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "FLASH-3.5",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (summaryLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = TealPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = when (viewModel.currentLanguage) {
                                    "Bengali" -> "জেমিনি অধ্যায়গুলো বিশ্লেষণ করে আপনার ভাষায় সারাংশ তৈরি করছে..."
                                    "Arabic" -> "يقوم جيمي بتحليل فصول الكتاب وصياغة الملخص بلغتك المفضلة..."
                                    else -> "Gemini is analyzing book chapters & synthesizing summary in your language..."
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else if (summaryText != null) {
                        Column {
                            Text(
                                text = summaryText,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                lineHeight = 21.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { viewModel.clearBookSummary() },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Clear Summary", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.fetchBookSummary(book.title, book.author, book.description)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Regenerate", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Column {
                            Text(
                                text = when (viewModel.currentLanguage) {
                                    "Bengali" -> "গুগল জেমিনি এআই মডেলের বুদ্ধিমত্তা ব্যবহার করে এই বইটির চমৎকার অধ্যায়ভিত্তিক আলোচনা, মূল প্রতিপাদ্য এবং বিশেষ অর্ন্তদৃষ্টি আপনার নির্বাচিত ভাষায় (${viewModel.currentLanguage}) তৈরি করুন।"
                                    "Arabic" -> "استخدم قوة نموذج الذكاء الاصطناعي Google Gemini للحصول على تحليل ذكي، الأفكار الرئيسية، والدروس المستفادة بلغاتك المفضلة (${viewModel.currentLanguage})."
                                    else -> "Leverage Google's advanced Gemini AI model to digest key themes, analyze chapter layouts, and compile an comprehensive literary assessment in **${viewModel.currentLanguage}**."
                                },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                lineHeight = 19.sp
                            )

                            if (summaryError != null) {
                                Text(
                                    text = "Error: $summaryError",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    viewModel.fetchBookSummary(book.title, book.author, book.description)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (viewModel.currentLanguage) {
                                        "Bengali" -> "স্মার্ট জেমিনি সারাংশ তৈরি করুন"
                                        "Arabic" -> "توليد ملخص جيمي الذكي"
                                        else -> "Generate Gemini Summary"
                                    },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// 5.a DOWNLOAD MANAGER INTERNAL EXPANSION
@Composable
fun DownloadManagerPanel(book: BookEntity, viewModel: ReaderViewModel) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (book.downloadStatus) {
                        "DOWNLOADED" -> Icons.Default.CloudDone
                        "DOWNLOADING" -> Icons.Default.CloudDownload
                        "PAUSED" -> Icons.Default.PauseCircle
                        else -> Icons.Default.CloudDownload
                    },
                    contentDescription = null,
                    tint = if (book.downloadStatus == "DOWNLOADED") TealLight else TealPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (book.downloadStatus) {
                            "DOWNLOADED" -> "Available Offline"
                            "DOWNLOADING" -> "Downloading Offline Copy"
                            "PAUSED" -> "Download Paused"
                            else -> "Download for Offline Reading"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (book.downloadStatus) {
                            "DOWNLOADED" -> "Open files any time, progress keeps offline."
                            "DOWNLOADING" -> "Transferring secured pages: ${(book.downloadProgress * 100).toInt()}%"
                            "PAUSED" -> "Download halted. Click to resume."
                            else -> "Size: ${book.fileSize}. Saved directly locally."
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                // Interactive Toggles in panel
                when (book.downloadStatus) {
                    "DOWNLOADED" -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TealLight.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("100% Ready", color = TealLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    "DOWNLOADING" -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { viewModel.pauseDownload(book.id) }) {
                                Icon(Icons.Default.Pause, contentDescription = "Pause", tint = OrangeAccent)
                            }
                            IconButton(onClick = { viewModel.cancelDownload(book.id) }) {
                                Icon(Icons.Default.Cancel, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    "PAUSED" -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { viewModel.resumeDownload(book.id) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Resume", tint = TealPrimary)
                            }
                            IconButton(onClick = { viewModel.cancelDownload(book.id) }) {
                                Icon(Icons.Default.Cancel, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    else -> {
                        IconButton(
                            onClick = { viewModel.triggerDownload(book.id) }
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download Book", tint = TealPrimary)
                        }
                    }
                }
            }

            // Downloader visual horizontal progress bar
            if (book.downloadStatus == "DOWNLOADING" || book.downloadStatus == "PAUSED") {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = book.downloadProgress,
                    color = TealPrimary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}

// 6. DISTRACTION FREE READER ENGINE SCREEN
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReaderScreen(viewModel: ReaderViewModel) {
    val bookState = viewModel.selectedBook.collectAsState(initial = null)
    val book = bookState.value

    val bookmarksList by viewModel.selectedBookBookmarks.collectAsState(initial = emptyList())
    val notesList by viewModel.selectedBookNotes.collectAsState(initial = emptyList())

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TealPrimary)
        }
        return
    }

    // Capture reading text pages
    val textPages = remember(book) {
        // split dummy chapters/markdown by raw titles to represent pages cleanly
        val splitList = book.contentMarkdown.split("\n\n").filter { it.isNotBlank() }
        if (splitList.isEmpty()) {
            listOf("The digital manuscript file appears to be empty. Check back later or request a premium copy.")
        } else {
            splitList
        }
    }

    val initialPage = book.lastReadPosition.coerceIn(0, textPages.size - 1)
    var localPageNumber by remember(book.id) { mutableStateOf(initialPage) }

    var showControlsDrawer by remember { mutableStateOf(true) }
    var noteDialogState by remember { mutableStateOf<String?>(null) } // holds highlighted string if saving note

    // Configure theme palettes based on Reader Preferences
    val readerBg = when (viewModel.readerColorMode) {
        "Dark" -> CharcoalBg
        "Sepia" -> SepiaBg
        else -> LightBg
    }
    val readerText = when (viewModel.readerColorMode) {
        "Dark" -> TextLight
        "Sepia" -> SepiaText
        else -> TextDark
    }
    val readerCard = when (viewModel.readerColorMode) {
        "Dark" -> CardDark
        "Sepia" -> SepiaCard
        else -> CardLight
    }

    val readerFont = when (viewModel.readerFontFamily) {
        "Sans-Serif" -> FontFamily.SansSerif
        "Monospace" -> FontFamily.Monospace
        else -> FontFamily.Serif
    }

    // Save history periodically
    LaunchedEffect(key1 = localPageNumber) {
        viewModel.updateReadingPosition(book.id, localPageNumber)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Core distraction-free reading content (Single Click toggles margins, search and bottom drawers!)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { showControlsDrawer = !showControlsDrawer }
                .padding(horizontal = viewModel.readerMargin.dp)
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            // Main readable passage container
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                val currentText = textPages.getOrNull(localPageNumber) ?: "The digital manuscript file appears to be empty."
                
                // Represent headers or simple paragraphs cleanly
                if (currentText.trim().startsWith("#")) {
                    Text(
                        text = currentText.replace("#", "").trim(),
                        fontSize = (viewModel.readerFontSize + 6).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = readerText,
                        fontFamily = readerFont,
                        lineHeight = (viewModel.readerFontSize * viewModel.readerLineSpacing).sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Text(
                        text = currentText,
                        fontSize = viewModel.readerFontSize.sp,
                        color = readerText,
                        fontFamily = readerFont,
                        lineHeight = (viewModel.readerFontSize * viewModel.readerLineSpacing).sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Custom Highlight helper: simulate double-tap/long press trigger
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(readerCard.copy(alpha = 0.5f))
                        .clickable { noteDialogState = "Double-click key quote to add highlighters" }
                        .padding(12.dp)
                ) {
                    Text(
                        text = "+ Highlight sentence and append Personal Notes",
                        fontSize = 12.sp,
                        color = readerText.copy(alpha = 0.6f),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Compact bottom tracking index
            Text(
                text = "Page ${localPageNumber + 1} of ${textPages.size}  •  ${book.title}",
                fontSize = 11.sp,
                color = readerText.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }

        // Animated Top Header (Controls)
        AnimatedVisibility(
            visible = showControlsDrawer,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                color = readerCard,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = readerText)
                        }
                        Text(
                            text = book.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = readerText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(180.dp)
                        )
                    }

                    Row {
                        // Bookmark click toggler
                        val isBookmarked = bookmarksList.any { it.pageNumber == localPageNumber }
                        IconButton(
                            onClick = {
                                if (isBookmarked) {
                                    val target = bookmarksList.find { it.pageNumber == localPageNumber }
                                    if (target != null) viewModel.deleteBookmark(target)
                                } else {
                                    viewModel.saveBookmark(
                                        bookId = book.id,
                                        page = localPageNumber,
                                        title = "Bookmark Page ${localPageNumber + 1}",
                                        snippet = textPages.getOrNull(localPageNumber)?.take(30) ?: "Page text"
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Toggle Bookmark",
                                tint = if (isBookmarked) OrangeAccent else readerText
                            )
                        }
                    }
                }
            }
        }

        // Animated Bottom Controls & Panel Preferences drawer
        AnimatedVisibility(
            visible = showControlsDrawer,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                color = readerCard,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    // Manual Back/Next page quick pointers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { if (localPageNumber > 0) localPageNumber-- },
                            enabled = localPageNumber > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = readerBg, disabledContainerColor = readerCard.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.NavigateBefore, contentDescription = "Prev", tint = readerText)
                            Text("Prev Page", color = readerText, fontSize = 12.sp)
                        }

                        Text(
                            text = "Page ${localPageNumber + 1} of ${textPages.size}",
                            color = readerText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Button(
                            onClick = { if (localPageNumber < textPages.size - 1) localPageNumber++ },
                            enabled = localPageNumber < textPages.size - 1,
                            colors = ButtonDefaults.buttonColors(containerColor = readerBg, disabledContainerColor = readerCard.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Next Page", color = readerText, fontSize = 12.sp)
                            Icon(Icons.Default.NavigateNext, contentDescription = "Next", tint = readerText)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Color Themes selecting mode grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = listOf("Light", "Dark", "Sepia")
                        modes.forEach { m ->
                            val isS = viewModel.readerColorMode == m
                            val cBg = when (m) {
                                "Dark" -> CharcoalBg
                                "Sepia" -> SepiaBg
                                else -> LightBg
                            }
                            val cTxt = when (m) {
                                "Dark" -> TextLight
                                "Sepia" -> SepiaText
                                else -> TextDark
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(BorderStroke(2.dp, if (isS) TealPrimary else Color.Transparent), RoundedCornerShape(10.dp))
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(cBg)
                                    .clickable { viewModel.readerColorMode = m }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = m,
                                    color = cTxt,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Font Sizing Adjusting Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TextFields, contentDescription = "Font Size", tint = readerText, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("A-", color = readerText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = viewModel.readerFontSize,
                            onValueChange = { viewModel.readerFontSize = it },
                            valueRange = 12f..28f,
                            steps = 8,
                            colors = SliderDefaults.colors(thumbColor = TealPrimary, activeTrackColor = TealPrimary),
                            modifier = Modifier.weight(1f)
                        )
                        Text("A+", color = readerText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Margin & Font Typeface Selector tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Font Choice
                        Column(modifier = Modifier.weight(1.3f)) {
                            Text("Font Family", fontSize = 11.sp, color = readerText.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.padding(top = 4.dp)) {
                                val families = listOf("Serif", "Sans-Serif", "Mono")
                                families.forEach { f ->
                                    val isS = viewModel.readerFontFamily == f
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isS) TealPrimary else readerBg)
                                            .clickable { viewModel.readerFontFamily = f }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(f, color = if (isS) Color.White else readerText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Margin setting
                        Column(modifier = Modifier.weight(0.9f)) {
                            Text("Margin width", fontSize = 11.sp, color = readerText.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                            Row(modifier = Modifier.padding(top = 4.dp)) {
                                val marginsMap = listOf(8, 16, 24)
                                marginsMap.forEach { mg ->
                                    val isS = viewModel.readerMargin == mg.toFloat()
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isS) TealPrimary else readerBg)
                                            .clickable { viewModel.readerMargin = mg.toFloat() }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${mg}dp", color = if (isS) Color.White else readerText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. ANNOTATIONS & BOOKMARKS COLLAPSIBLE INDEX
                    Text(
                        text = "Review Bookmarks & Annotation Notes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = readerText,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick bookmarks list anchors
                        bookmarksList.forEach { bk ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OrangeAccent.copy(alpha = 0.15f))
                                    .clickable { localPageNumber = bk.pageNumber }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Page ${bk.pageNumber + 1}", color = OrangeAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Notes anchors
                        notesList.forEach { nt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TealLight.copy(alpha = 0.15f))
                                    .clickable { localPageNumber = nt.pageNumber }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EditNote, contentDescription = null, tint = TealLight, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Page ${nt.pageNumber + 1}: ${nt.text.take(15)}...", color = TealLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // CUSTOM NOTE HIGHLIGHT INPUT DIALOGUE
        if (noteDialogState != null) {
            var noteContent by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { noteDialogState = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (noteContent.isNotBlank()) {
                                viewModel.saveNote(
                                    bookId = book.id,
                                    page = localPageNumber,
                                    text = noteContent,
                                    highlight = textPages.getOrNull(localPageNumber)?.take(50) ?: "Highlight sample",
                                    colorHex = "#F59E0B"
                                )
                            }
                            noteDialogState = null
                            noteContent = ""
                        }
                    ) {
                        Text("Add Note", color = TealPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { noteDialogState = null }) { Text("Cancel") }
                },
                title = { Text("Highlight Annotation Note", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            text = "Add reference notes, translation links, or thoughts directly synchronized with Page ${localPageNumber + 1} text.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = noteContent,
                            onValueChange = { noteContent = it },
                            placeholder = { Text("write down your note interpretation...") },
                            singleLine = false,
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }
    }
}

// 7. HIGH-FIDESTY DYNAMIC CANVAS COVER DESIGN
@Composable
fun CanvasBookCover(title: String, author: String, modifier: Modifier = Modifier) {
    // Generate stable color palette seeded by title length
    val seed = title.length
    val primaryBg = when (seed % 5) {
        0 -> TealPrimary
        1 -> Color(0xFF14B8A6) // custom teal lighter
        2 -> Color(0xFF6D28D9) // purple
        3 -> Color(0xFF1E3A8A) // deep blue
        else -> Color(0xFF854D0E) // wood brown
    }

    val patternGold = Color(0xFFF59E0B)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Background color fill
        drawRect(color = primaryBg)

        // Draw elegant circular islamic or classic border designs on book
        drawCircle(
            color = patternGold.copy(alpha = 0.25f),
            radius = width * 0.35f,
            center = Offset(width / 2f, height * 0.4f),
            style = Stroke(width = 2.dp.toPx())
        )

        drawCircle(
            color = patternGold.copy(alpha = 0.15f),
            radius = width * 0.26f,
            center = Offset(width / 2f, height * 0.4f),
            style = Stroke(width = 1.dp.toPx())
        )

        // Horizontal line banners
        drawLine(
            color = patternGold.copy(alpha = 0.3f),
            start = Offset(width * 0.15f, height * 0.76f),
            end = Offset(width * 0.85f, height * 0.76f),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = patternGold.copy(alpha = 0.3f),
            start = Offset(width * 0.15f, height * 0.18f),
            end = Offset(width * 0.85f, height * 0.18f),
            strokeWidth = 1.dp.toPx()
        )
    }

    // Overlay descriptive content in beautiful vertical stack
    Box(
        modifier = modifier.padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "MyReaderPro Vol.",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title.take(24) + if (title.length > 24) "..." else "",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                Text(
                    text = "by " + author,
                    color = patternGold,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(patternGold.copy(alpha = 0.25f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "SECURED",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

// Book cover background renderer is fully handled by CanvasBookCover above

// ==========================================
// MYREADERPRO COMMERCIAL SAAS MODULES
// ==========================================

@Composable
fun BookRequestHub(viewModel: ReaderViewModel, activeUser: UserEntity?) {
    val requestsList by viewModel.allBookRequests.collectAsState(initial = emptyList())
    val popularList by viewModel.popularRequests.collectAsState(initial = emptyList())
    val recentList by viewModel.recentlyRequested.collectAsState(initial = emptyList())

    var requestTitle by remember { mutableStateOf("") }
    var requestAuthor by remember { mutableStateOf("") }
    var requestPublisher by remember { mutableStateOf("") }
    var requestNotes by remember { mutableStateOf("") }
    var submitSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Admin state filters
    var adminSearchQuery by remember { mutableStateOf("") }
    var statusFilter   by remember { mutableStateOf("ALL") } // ALL, PENDING, COMPLETED, REJECTED

    val canModerate = activeUser?.role == "ADMIN" || activeUser?.role == "MODERATOR"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. USER SUBMISSION FORM ---
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, TealLight.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Request a Book",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
                Text(
                    text = "Request unavailable books. Our automatic indexing crawler is triggered on submission.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                if (submitSuccessMessage != null) {
                    Text(
                        text = submitSuccessMessage!!,
                        color = TealLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                OutlinedTextField(
                    value = requestTitle,
                    onValueChange = { requestTitle = it },
                    label = { Text("Book Title *", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = requestAuthor,
                    onValueChange = { requestAuthor = it },
                    label = { Text("Author Name *", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = requestPublisher,
                    onValueChange = { requestPublisher = it },
                    label = { Text("Publisher (Optional)", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = requestNotes,
                    onValueChange = { requestNotes = it },
                    label = { Text("Add Notes / Particular comments", fontSize = 12.sp) },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (requestTitle.isBlank() || requestAuthor.isBlank()) {
                            submitSuccessMessage = "Error: Title & Author name are required!"
                        } else {
                            viewModel.submitBookRequest(
                                title = requestTitle,
                                author = requestAuthor,
                                publisher = requestPublisher,
                                notes = requestNotes
                            )
                            submitSuccessMessage = "Success: Request added to automatic crawler queues! De-duplication triggered."
                            requestTitle = ""
                            requestAuthor = ""
                            requestPublisher = ""
                            requestNotes = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Book Request", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // --- 2. AUTOMATIC SYSTEM DISCOVERABILITY STATISTICS ---
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "🔥 Most Requested / Trending Books",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeAccent,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (popularList.isEmpty()) {
                    Text("No trending requests found.", fontSize = 11.sp, color = Color.Gray)
                } else {
                    popularList.take(3).forEach { req ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(req.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("by ${req.author}", fontSize = 10.sp, color = Color.Gray)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(OrangeAccent.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("${req.requestCount} Requests", color = OrangeAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🗄️ Recently Requested",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (recentList.isEmpty()) {
                    Text("No recent requests available.", fontSize = 11.sp, color = Color.Gray)
                } else {
                    recentList.take(3).forEach { req ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(req.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Author: ${req.author} | Status: ${req.status}", fontSize = 10.sp, color = if (req.status == "COMPLETED") TealLight else Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // --- 3. ADMIN MANAGEMENT PANEL ---
        if (canModerate) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = OrangeAccent.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(OrangeAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Moderation: Requests List",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangeAccent
                        )
                    }

                    OutlinedTextField(
                        value = adminSearchQuery,
                        onValueChange = { adminSearchQuery = it },
                        placeholder = { Text("Search catalog requests...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(16.dp)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Filters tabs
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val filters = listOf("ALL", "PENDING", "COMPLETED", "REJECTED")
                        filters.forEach { f ->
                            val isS = statusFilter == f
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isS) OrangeAccent else MaterialTheme.colorScheme.surface)
                                    .clickable { statusFilter = f }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .border(1.dp, if (isS) Color.Transparent else Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            ) {
                                Text(
                                    text = f,
                                    color = if (isS) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Simulated list execution
                    var filteredReqs = requestsList
                    if (adminSearchQuery.isNotBlank()) {
                        filteredReqs = filteredReqs.filter {
                            it.title.contains(adminSearchQuery, ignoreCase = true) ||
                            it.author.contains(adminSearchQuery, ignoreCase = true)
                        }
                    }
                    if (statusFilter != "ALL") {
                        filteredReqs = filteredReqs.filter { it.status == statusFilter }
                    }

                    if (filteredReqs.isEmpty()) {
                        Text("No requests match filters.", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                    } else {
                        filteredReqs.forEach { req ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(0.5.dp, Color.Gray.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(req.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Author: ${req.author} | Publisher: ${req.publisher}", fontSize = 10.sp, color = Color.Gray)
                                            Text("Requester: ${req.userEmail}", fontSize = 9.sp, color = Color.Gray)
                                            if (req.notes.isNotBlank()) {
                                                Text("Notes: ${req.notes}", fontSize = 9.sp, color = Color.Gray, maxLines = 1)
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    when (req.status) {
                                                        "COMPLETED" -> TealLight.copy(alpha = 0.15f)
                                                        "REJECTED"  -> Color.Red.copy(alpha = 0.15f)
                                                        else         -> OrangeAccent.copy(alpha = 0.15f)
                                                    }
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = req.status,
                                                color = when (req.status) {
                                                    "COMPLETED" -> TealLight
                                                    "REJECTED"  -> Color.Red
                                                    else        -> OrangeAccent
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Action buttons for Admins
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (req.status == "PENDING") {
                                            Button(
                                                onClick = { viewModel.updateBookRequestStatus(req.id, "COMPLETED") },
                                                colors = ButtonDefaults.buttonColors(containerColor = TealLight),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f).height(28.dp),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text("Approve & Index", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { viewModel.updateBookRequestStatus(req.id, "REJECTED") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f).height(28.dp),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text("Reject", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Simulate merging duplicate requests to consolidate count
                                        Button(
                                            onClick = {
                                                // Change duplicate status or simple complete to simulate merging
                                                viewModel.updateBookRequestStatus(req.id, "COMPLETED")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.weight(1f).height(28.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("Merge Duplicate", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CopyrightClaimHub(viewModel: ReaderViewModel, activeUser: UserEntity?) {
    val claimsList by viewModel.allCopyrightClaims.collectAsState(initial = emptyList())

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var orgName by remember { mutableStateOf("") }
    var contentTitle by remember { mutableStateOf("") }
    var contentUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var supportingDocs by remember { mutableStateOf("") }
    var outputMessage by remember { mutableStateOf<String?>(null) }

    val canModerate = activeUser?.role == "ADMIN" || activeUser?.role == "MODERATOR"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. COPYRIGHT SUBMISSION DMCA FORM ---
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)),
            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Submit DMCA & Copyright Infringement Claim",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Text(
                    text = "If you hold copyright over digital manuscripts listed on our platform, submit instant takedown requests below.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                if (outputMessage != null) {
                    Text(
                        text = outputMessage!!,
                        color = OrangeAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Rights Owner Contact) *", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = orgName,
                    onValueChange = { orgName = it },
                    label = { Text("Organization Name Name (Optional)", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contentTitle,
                    onValueChange = { contentTitle = it },
                    label = { Text("Authorized Content Title *", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contentUrl,
                    onValueChange = { contentUrl = it },
                    label = { Text("Content URL in App *", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed Infringement Argument *", fontSize = 12.sp) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = supportingDocs,
                    onValueChange = { supportingDocs = it },
                    label = { Text("Supporting Evidence / Trademarks Registration ID", fontSize = 12.sp) },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (fullName.isBlank() || email.isBlank() || contentTitle.isBlank() || contentUrl.isBlank() || description.isBlank()) {
                            outputMessage = "Error: All marked (*) parameters are required!"
                        } else {
                            viewModel.submitCopyrightClaim(
                                fullName = fullName,
                                email = email,
                                org = orgName,
                                title = contentTitle,
                                url = contentUrl,
                                desc = description,
                                docs = supportingDocs
                            )
                            outputMessage = "Success: DMCA Trademark Claim recorded. Temporary hold process activated."
                            fullName = ""
                            email = ""
                            orgName = ""
                            contentTitle = ""
                            contentUrl = ""
                            description = ""
                            supportingDocs = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit DMCA Infringement Claim", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // --- 2. ADMIN MODERATION AUDIT LOG CENTER ---
        if (canModerate) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "⚖️ DMCA Audit Trail & Moderation Reviews",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )

                    if (claimsList.isEmpty()) {
                        Text("No active claims submitted yet.", fontSize = 11.sp, color = Color.Gray)
                    } else {
                        claimsList.forEach { claim ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(0.5.dp, Color.Gray.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(claim.contentTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                                            Text("Owner: ${claim.fullName} (${claim.organizationName})", fontSize = 10.sp, color = Color.Gray)
                                            Text("Date submitted: 2026-06-02", fontSize = 8.sp, color = Color.Gray)
                                            Text("Claim ID: dmca_claim_${claim.id}", fontSize = 9.sp, color = Color.Gray)
                                            Text("Arguments: ${claim.description}", fontSize = 10.sp, maxLines = 2)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    when (claim.status) {
                                                        "PENDING"   -> OrangeAccent.copy(alpha = 0.15f)
                                                        "REVIEWING" -> Color.Blue.copy(alpha = 0.15f)
                                                        "APPROVED"  -> Color.Red.copy(alpha = 0.15f)
                                                        else         -> TealLight.copy(alpha = 0.15f)
                                                    }
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = claim.status,
                                                color = when (claim.status) {
                                                    "PENDING"   -> OrangeAccent
                                                    "REVIEWING" -> Color.Blue
                                                    "APPROVED"  -> Color.Red
                                                    else         -> TealLight
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Action buttons
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (claim.status == "PENDING") {
                                            Button(
                                                onClick = {
                                                    viewModel.updateCopyrightStatus(
                                                        claimId = claim.id,
                                                        status = "APPROVED",
                                                        decision = "Takedown approved due to valid DMCA identification.",
                                                        hideContent = true
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f).height(28.dp),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text("Takedown / Block", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    viewModel.updateCopyrightStatus(
                                                        claimId = claim.id,
                                                        status = "REVIEWING",
                                                        decision = "Claim under review pending further verification.",
                                                        hideContent = true
                                                    )
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f).height(28.dp),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text("Needs Evidence", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Restore content button
                                        if (claim.status == "APPROVED" || claim.temporaryHidden) {
                                            Button(
                                                onClick = {
                                                    viewModel.restoreCopyrightContent(claim.id)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = TealLight),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f).height(28.dp),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text("Restore Content", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonetizationHub(viewModel: ReaderViewModel, activeUser: UserEntity?) {
    val adBlocks by viewModel.allAdBlocks.collectAsState(initial = emptyList())

    // Simulated Donations stats
    var supportAmount by remember { mutableStateOf("10") }
    var totalDonationVal by remember { mutableStateOf(activeUser?.totalDonationAmount ?: 25.0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. MEMBERSHIP PLANS SWITCH PANEL ---
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, TealLight.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Subscription & Member Roles Management",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val plans = listOf("FREE", "PREMIUM", "VIP")
                    plans.forEach { plan ->
                        val isCurrent = activeUser?.membershipType == plan
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) TealLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable {
                                    viewModel.changeMembership(plan)
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = plan,
                                color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Simulated Moderation Role Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(OrangeAccent.copy(alpha = 0.1f))
                        .clickable { viewModel.toggleUserDeveloperRole() }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Developer Permission Level", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Simulate: FREE, GUEST, MODERATOR, or ADMIN instantly", fontSize = 9.sp, color = Color.Gray)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(OrangeAccent)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(activeUser?.role ?: "ADMIN", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 2. FUTURE REWARD DONATIONS SYSTEM ---
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💸 Support Platform & Donations",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
                Text(
                    text = "Become a backer! Complete simulations with offline-ready support credentials.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = supportAmount,
                        onValueChange = { supportAmount = it },
                        label = { Text("Donation ($ USD)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            val addition = supportAmount.toFloatOrNull() ?: 0.0f
                            totalDonationVal += addition
                            supportAmount = "10"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Donate USD", fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Your simulation lifetime donation:", fontSize = 11.sp, color = Color.Gray)
                    Text("$${totalDonationVal} USD", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = TealLight)
                }
            }
        }

        // --- 3. AD SYSTEM & REPORTING ---
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "📢 Ad Network Block Planner (Future Ready)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                if (adBlocks.isEmpty()) {
                    Text("Initializing ad network configuration blocks...", fontSize = 11.sp, color = Color.Gray)
                } else {
                    adBlocks.forEach { block ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(block.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Type: ${block.adType} | Imp: ${block.impressions} | Clicks: ${block.clicks}", fontSize = 9.sp, color = Color.Gray)
                            }

                            Switch(
                                checked = block.isEnabled,
                                onCheckedChange = { viewModel.toggleAdBlock(block.id, it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = TealLight)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- SAAS REVENUE ANALYTICS VISUALIZER ---
                Text("📈 Revenue and Traffic Insights Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(0.5.dp, Color.Gray.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Premium / VIP users ratio", fontSize = 9.sp, color = Color.Gray)
                                Text("84% Subscribers", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealLight)
                            }
                            Column {
                                Text("Est. Ad Earnings", fontSize = 9.sp, color = Color.Gray)
                                Text("$1,532.40 USD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangeAccent)
                            }
                        }

                        // Simulated bar graph
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val bars = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.8f, 0.95f)
                            bars.forEachIndexed { idx, barHeight ->
                                val color = if (idx == 6) OrangeAccent else TealLight
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(barHeight)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(color)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackupAndRestoreHub(viewModel: ReaderViewModel) {
    var rawBackupText by remember { mutableStateOf("") }
    var currentStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Professional Backup System (Room-to-JSON)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
                Text(
                    text = "Export user metrics, preferences and requested database structures locally, or copy files to import later.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                if (currentStatus != null) {
                    Text(
                        text = currentStatus!!,
                        color = TealLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            viewModel.exportBackup()
                            currentStatus = "Database exported successfully!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Export Backup JSON", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = {
                            if (rawBackupText.isBlank()) {
                                currentStatus = "Error: Please paste a valid backup JSON payload!"
                            } else {
                                viewModel.restoreBackup(rawBackupText) { ok ->
                                    currentStatus = if (ok) "Success: Preferences & configuration recovered!" else "Error: Signature mismatch or invalid JSON payload!"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Import & Restore", fontSize = 11.sp, color = Color.White)
                    }
                }

                // Show backup output or input field
                OutlinedTextField(
                    value = if (viewModel.lastExportedBackupJson.isNotEmpty()) viewModel.lastExportedBackupJson else rawBackupText,
                    onValueChange = {
                        if (viewModel.lastExportedBackupJson.isEmpty()) {
                            rawBackupText = it
                        }
                    },
                    maxLines = 4,
                    label = { Text("Backup Payload Signature Data", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                )

                if (viewModel.lastExportedBackupJson.isNotEmpty()) {
                    Button(
                        onClick = {
                            rawBackupText = ""
                            viewModel.lastExportedBackupJson = ""
                            currentStatus = "Backup visual reset."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.align(Alignment.End).height(32.dp)
                    ) {
                        Text("Clear Buffer", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ResearchChatbotDialog(
    viewModel: ReaderViewModel,
    onDismiss: () -> Unit
) {
    val messages = viewModel.chatMessages
    val loading = viewModel.chatLoading
    var userInput by remember { mutableStateOf("") }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Scroll to bottom when list grows
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with Beautiful AI Logo & Close Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TealPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = when (viewModel.currentLanguage) {
                                    "Bengali" -> "এআই সাহিত্য গবেষক"
                                    "Arabic" -> "جيمي الباحث الأدبي"
                                    else -> "AI Literary Scholar"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Powered by Gemini 3.5 Flash",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { viewModel.clearChat() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Chat",
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Divider line
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                // Message Stream Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    androidx.compose.foundation.lazy.LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages) { (role, content) ->
                            val isUser = role == "user"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isUser) TealPrimary else MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .weight(1f, fill = false)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = if (isUser) "You" else "Gemini Scholar",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isUser) Color.White.copy(alpha = 0.7f) else TealPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = content,
                                            fontSize = 13.sp,
                                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 19.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (loading) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(
                                                color = TealPrimary,
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = when (viewModel.currentLanguage) {
                                                    "Bengali" -> "জেমিনী অনুসন্ধান করছে..."
                                                    "Arabic" -> "يبحث جيمي في الكتب..."
                                                    else -> "Gemini is researching..."
                                                },
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick scholarly research prompts to guide the user (AI buddy suggestions)
                if (messages.size <= 1) {
                    Text(
                        text = "Suggested Research Topics:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                    )
                    
                    val suggestions = when (viewModel.currentLanguage) {
                        "Bengali" -> listOf(
                            "রবীন্দ্রনাথ ঠাকুরের জীবন দর্শন আলোচনা করুন।",
                            "বাংলা সাহিত্যের সুবর্ণ যুগ কোনটি এবং কেন?",
                            "মাইকেল মধুসূদন দত্তের মেঘনাদবধ কাব্যের বৈশিষ্ট।"
                        )
                        "Arabic" -> listOf(
                            "ما هو أثر نجيب محفوظ في الرواية العربية؟",
                            "تحليل لقصيدة البؤساء لأحمد شوقي.",
                            "تحدث عن تطور الشعر العربي الحديث."
                        )
                        else -> listOf(
                            "Explore William Shakespeare's tragic heroes.",
                            "What is the impact of magical realism in modern fiction?",
                            "Compare the writing styles of Ernest Hemingway and F. Scott Fitzgerald."
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggestions.take(3).forEach { prompt ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        userInput = prompt
                                    }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prompt,
                                    fontSize = 10.sp,
                                    color = TealPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Input Bar Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        placeholder = {
                            Text(
                                text = when (viewModel.currentLanguage) {
                                    "Bengali" -> "সাহিত্য বিষয়ক জিজ্ঞাসা করুন..."
                                    "Arabic" -> "اسأل عن أي نظرية أدبية أو رواية..."
                                    else -> "Ask about literary logic, genres, history..."
                                },
                                fontSize = 13.sp
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_research_input_text")
                    )

                    IconButton(
                        onClick = {
                            val msg = userInput.trim()
                            if (msg.isNotEmpty()) {
                                viewModel.sendChatMessage(msg)
                                userInput = ""
                            }
                        },
                        enabled = userInput.isNotBlank() && !loading,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (userInput.isNotBlank() && !loading) TealPrimary else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (userInput.isNotBlank() && !loading) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

