package com.example.ui.components

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.DeepDarkBg
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.SlateDarkText
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SyneFontFamily
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthScreen"

@Composable
fun AuthScreen(
    initialIdentifier: String = "",
    errorMessage: String? = null,
    onClearError: () -> Unit = {},
    onLogin: (identifier: String, password: String) -> Unit,
    onRegister: (fullName: String, username: String, phone: String, password: String) -> Unit,
    onGoogleSignIn: (fullName: String, username: String, phone: String, googleUid: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val webClientId = remember {
        runCatching { context.getString(R.string.default_web_client_id) }.getOrDefault("")
    }

    var isCreateAccountMode by rememberSaveable { mutableStateOf(false) }
    var isGoogleSigningIn by remember { mutableStateOf(false) }

    // Login state
    var loginIdentifier by rememberSaveable(initialIdentifier) { mutableStateOf(initialIdentifier) }
    var loginPassword by rememberSaveable { mutableStateOf("") }

    // Create Account state
    var fullName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var mobileNumber by rememberSaveable { mutableStateOf("") }
    var registerPassword by rememberSaveable { mutableStateOf("") }
    var agreedToTerms by rememberSaveable { mutableStateOf(true) }

    var localValidationError by remember { mutableStateOf<String?>(null) }
    var showTermsDialog by remember { mutableStateOf(false) }

    suspend fun authenticateWithGoogleIdToken(
        googleIdTokenCredential: GoogleIdTokenCredential
    ) {
        val firebaseCred = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        val authResult = Firebase.auth.signInWithCredential(firebaseCred).await()
        val firebaseUser = authResult.user

        val fallbackName = googleIdTokenCredential.id
            .substringBefore("@")
            .replace('.', ' ')
            .replaceFirstChar { it.uppercase() }
            .ifBlank { "DhanRatan Player" }

        val resolvedName = firebaseUser?.displayName?.takeIf { it.isNotBlank() }
            ?: googleIdTokenCredential.displayName?.takeIf { it.isNotBlank() }
            ?: fallbackName

        val resolvedEmail = firebaseUser?.email?.takeIf { it.isNotBlank() }
            ?: googleIdTokenCredential.id.ifBlank { "dhanratan_user" }

        val resolvedPhone = firebaseUser?.phoneNumber.orEmpty()
        val resolvedUid = firebaseUser?.uid.orEmpty()

        onGoogleSignIn(resolvedName, resolvedEmail, resolvedPhone, resolvedUid)
    }

    // Attempt auto sign-in for returning authorized Google accounts
    LaunchedEffect(Unit) {
        if (webClientId.isBlank()) return@LaunchedEffect
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(context = context, request = request)
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            isGoogleSigningIn = true
            authenticateWithGoogleIdToken(googleIdTokenCredential)
        } catch (_: NoCredentialException) {
            // Normal for first-time users; wait for explicit button tap
        } catch (_: GetCredentialCancellationException) {
            // User dismissed the auto sign-in prompt
        } catch (e: GetCredentialException) {
            Log.w(TAG, "Auto sign-in failed", e)
        } catch (e: Exception) {
            Log.w(TAG, "Firebase auto sign-in failed", e)
        } finally {
            isGoogleSigningIn = false
        }
    }

    fun launchDirectGoogleSignIn() {
        if (isGoogleSigningIn) return
        localValidationError = null
        onClearError()

        if (webClientId.isBlank()) {
            localValidationError = "Google Sign-In configuration is missing Web Client ID."
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(webClientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        coroutineScope.launch {
            isGoogleSigningIn = true
            try {
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                authenticateWithGoogleIdToken(googleIdTokenCredential)
            } catch (_: GetCredentialCancellationException) {
                // User cancelled the Google account selector sheet
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Google Sign-In failed", e)
                localValidationError = e.localizedMessage
                    ?: "Could not sign in with Google. Please ensure a Google account is signed in on this device."
            } catch (e: Exception) {
                Log.e(TAG, "Firebase Google Sign-In failed", e)
                localValidationError = e.localizedMessage ?: "Google Sign-In failed. Please try again."
            } finally {
                isGoogleSigningIn = false
            }
        }
    }

    BackHandler(enabled = isCreateAccountMode) {
        localValidationError = null
        onClearError()
        isCreateAccountMode = false
    }

    val activeError = localValidationError ?: errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF102A26), ObsidianBg, DeepDarkBg)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 440.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // APK Logo on top with subtle halo frame
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(GoldPrimary.copy(alpha = 0.08f))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.28f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                DhanRatanLogo(
                    sizeDp = 70.dp,
                    showText = false,
                    useGeneratedIcon = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Welcome + APK Name on top
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GoldPrimary.copy(alpha = 0.14f))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "WELCOME TO",
                    color = GoldLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.8.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = SlateTextPrimary)) {
                        append("Dhan")
                    }
                    withStyle(SpanStyle(color = GoldPrimary)) {
                        append("Ratan ")
                    }
                    withStyle(SpanStyle(color = EmeraldLight)) {
                        append("Games")
                    }
                },
                fontFamily = SyneFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Page Heading & Welcome Subtitle
            Text(
                text = if (isCreateAccountMode) "Create an Account" else "Welcome Back!",
                fontFamily = SyneFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SlateTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (isCreateAccountMode) {
                    "Join DhanRatan Games and start your winning journey"
                } else {
                    "Sign in to your DhanRatan account to continue"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = SlateTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error Banner if any
            AnimatedVisibility(
                visible = !activeError.isNullOrBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(RosePrimary.copy(alpha = 0.16f))
                        .border(1.dp, RosePrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = activeError.orEmpty(),
                        color = RoseLight,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Framed Mobile Card Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(CardSurface.copy(alpha = 0.78f))
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(22.dp))
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isCreateAccountMode) {
                    // ==================== LOGIN PAGE ====================
                    AuthLabeledField(
                        label = "Username",
                        value = loginIdentifier,
                        onValueChange = {
                            loginIdentifier = it
                            localValidationError = null
                            onClearError()
                        },
                        placeholder = "Enter User Name / Mobile No.",
                        leadingIcon = Icons.Default.PersonOutline,
                        keyboardType = KeyboardType.Text,
                        testTag = "auth_login_username_input"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AuthLabeledField(
                        label = "Password",
                        value = loginPassword,
                        onValueChange = {
                            loginPassword = it
                            localValidationError = null
                            onClearError()
                        },
                        placeholder = "Enter password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        keyboardType = KeyboardType.Password,
                        testTag = "auth_login_password_input"
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 1. Login Button (Primary Gold matching APK theme)
                    Button(
                        onClick = {
                            localValidationError = null
                            onClearError()
                            val trimmedId = loginIdentifier.trim()
                            val trimmedPass = loginPassword.trim()
                            when {
                                trimmedId.isEmpty() -> {
                                    localValidationError = "Please enter your User Name or Mobile No."
                                }
                                trimmedPass.isEmpty() -> {
                                    localValidationError = "Please enter your password."
                                }
                                else -> {
                                    onLogin(trimmedId, trimmedPass)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = SlateDarkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 1.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_login_submit_button")
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SlateDarkText
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Sign up with Google Button — Directly signs in via Google Account Credential Manager
                    Button(
                        onClick = { launchDirectGoogleSignIn() },
                        enabled = !isGoogleSigningIn,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardSurfaceElevated,
                            contentColor = SlateTextPrimary,
                            disabledContainerColor = CardSurfaceElevated.copy(alpha = 0.7f),
                            disabledContentColor = SlateTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 1.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
                            .testTag("auth_google_signup_button")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isGoogleSigningIn) {
                                CircularProgressIndicator(
                                    color = GoldPrimary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.CenterStart)
                                )
                            } else {
                                GoogleGLogo(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.CenterStart)
                                )
                            }
                            Text(
                                text = if (isGoogleSigningIn) "Signing in with Google..." else "Sign up with Google",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Create an account Button (Emerald APK theme accent)
                    Button(
                        onClick = {
                            localValidationError = null
                            onClearError()
                            isCreateAccountMode = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = SlateDarkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 1.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_switch_to_create_account_button")
                    ) {
                        Text(
                            text = "Create an account",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SlateDarkText
                        )
                    }
                } else {
                    // ==================== CREATE AN ACCOUNT PAGE ====================
                    AuthLabeledField(
                        label = "Full Name",
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            localValidationError = null
                            onClearError()
                        },
                        placeholder = "Enter full name",
                        leadingIcon = Icons.Default.PersonOutline,
                        keyboardType = KeyboardType.Text,
                        testTag = "auth_register_fullname_input"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AuthLabeledField(
                        label = "Username",
                        value = username,
                        onValueChange = {
                            username = it
                            localValidationError = null
                            onClearError()
                        },
                        placeholder = "Enter unique username",
                        leadingIcon = Icons.Default.PersonOutline,
                        keyboardType = KeyboardType.Text,
                        testTag = "auth_register_username_input"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AuthLabeledField(
                        label = "Mobile Number",
                        value = mobileNumber,
                        onValueChange = { input ->
                            mobileNumber = input.filter { it.isDigit() }.take(10)
                            localValidationError = null
                            onClearError()
                        },
                        placeholder = "Enter mobile no",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        testTag = "auth_register_mobile_input"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AuthLabeledField(
                        label = "Password",
                        value = registerPassword,
                        onValueChange = {
                            registerPassword = it
                            localValidationError = null
                            onClearError()
                        },
                        placeholder = "Enter password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        keyboardType = KeyboardType.Password,
                        testTag = "auth_register_password_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Terms & Conditions Checkbox Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (agreedToTerms) GoldPrimary else CardSurface)
                                .border(
                                    width = 1.5.dp,
                                    color = if (agreedToTerms) GoldPrimary else SlateTextMuted,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable {
                                    agreedToTerms = !agreedToTerms
                                    localValidationError = null
                                }
                                .testTag("auth_terms_checkbox"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (agreedToTerms) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Agreed to Terms",
                                    tint = SlateDarkText,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "I agree to all ",
                            fontSize = 13.sp,
                            color = SlateTextSecondary,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Terms & Conditions",
                            fontSize = 13.sp,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable { showTermsDialog = true }
                                .testTag("auth_terms_link")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sign Up Button (Gold Primary matching APK theme)
                    Button(
                        onClick = {
                            localValidationError = null
                            onClearError()
                            val cleanName = fullName.trim()
                            val cleanUser = username.trim()
                            val cleanPhone = mobileNumber.filter { it.isDigit() }
                            val cleanPass = registerPassword.trim()

                            when {
                                cleanName.length < 2 -> {
                                    localValidationError = "Please enter your full name."
                                }
                                cleanUser.length < 3 -> {
                                    localValidationError = "Please enter a unique username (at least 3 characters)."
                                }
                                cleanPhone.length != 10 -> {
                                    localValidationError = "Please enter a valid 10-digit mobile number."
                                }
                                cleanPass.length < 4 -> {
                                    localValidationError = "Password must be at least 4 characters."
                                }
                                !agreedToTerms -> {
                                    localValidationError = "Please agree to the Terms & Conditions to continue."
                                }
                                else -> {
                                    onRegister(cleanName, cleanUser, cleanPhone, cleanPass)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = SlateDarkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 1.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_register_submit_button")
                    ) {
                        Text(
                            text = "Sign Up",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SlateDarkText
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Footer: Already have an account? Sign In
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Already have an account? ",
                            fontSize = 13.5.sp,
                            color = SlateTextMuted,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "Sign In",
                            fontSize = 13.5.sp,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    localValidationError = null
                                    onClearError()
                                    isCreateAccountMode = false
                                }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                                .testTag("auth_switch_to_signin_link")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "DhanRatan Games Official APK • V1.0.6",
                fontSize = 11.sp,
                color = SlateTextMuted,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            containerColor = CardSurface,
            titleContentColor = SlateTextPrimary,
            textContentColor = SlateTextSecondary,
            title = {
                Text(
                    text = "Terms & Conditions",
                    fontFamily = SyneFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = GoldPrimary
                )
            },
            text = {
                Text(
                    text = "1. Players must be 18 years or older to create an account.\n" +
                        "2. Please use your accurate mobile number and bank/UPI details for seamless deposits and withdrawals.\n" +
                        "3. Keep your account password confidential.\n" +
                        "4. Market opening and closing times follow official DhanRatan Games schedules.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        agreedToTerms = true
                        showTermsDialog = false
                    }
                ) {
                    Text("I Agree", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun AuthLabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    testTag: String
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SlateTextPrimary,
            modifier = Modifier.padding(start = 2.dp, bottom = 5.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = SlateTextPrimary,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Medium
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = SlateTextMuted,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = label,
                    tint = GoldPrimary,
                    modifier = Modifier.size(19.dp)
                )
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = SlateTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                null
            },
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ObsidianBg,
                unfocusedContainerColor = ObsidianBg,
                disabledContainerColor = ObsidianBg,
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = CardBorderSubtle,
                cursorColor = GoldPrimary,
                focusedTextColor = SlateTextPrimary,
                unfocusedTextColor = SlateTextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag(testTag)
        )
    }
}

/**
 * Multi-color Google "G" icon matching the "Sign up with Google" button.
 */
@Composable
private fun GoogleGLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.20f
        val inset = strokeWidth / 2f
        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
        val topLeft = Offset(inset, inset)

        // Red top arc
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = -145f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Yellow left arc
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 145f,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Green bottom arc
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 45f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Blue right arc
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -15f,
            sweepAngle = 65f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Horizontal blue crossbar of the 'G'
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(size.width * 0.50f, size.height * 0.50f),
            end = Offset(size.width * 0.92f, size.height * 0.50f),
            strokeWidth = strokeWidth * 0.92f,
            cap = StrokeCap.Butt
        )
    }
}
