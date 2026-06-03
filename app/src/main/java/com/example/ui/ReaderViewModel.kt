package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MyReaderDatabase.getDatabase(application, viewModelScope)
    private val repository = ReaderRepository(database.readerDao(), viewModelScope)

    init {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val user = database.readerDao().getActiveUserSync()
                if (user == null) {
                    MyReaderDatabase.populateInitialData(database.readerDao())
                } else {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        currentScreen = "main"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Observable states from database
    val activeUser = repository.activeUser
    val allBooks = repository.allBooks
    val categories = repository.categories
    val favoriteBooks = repository.favoriteBooks
    val downloadedBooks = repository.downloadedBooks
    val readingHistoryBooks = repository.readingHistoryBooks
    val achievements = repository.achievements
    val notifications = repository.notifications

    // Selected book for detail & active reader instance
    private val _selectedBookId = MutableStateFlow<String?>(null)
    var selectedBookId: String?
        get() = _selectedBookId.value
        set(value) {
            _selectedBookId.value = value
        }

    val selectedBook: Flow<BookEntity?> = _selectedBookId
        .flatMapLatest { id ->
            if (id != null) repository.getBookById(id) else flowOf<BookEntity?>(null)
        }

    val selectedBookBookmarks: Flow<List<BookmarkEntity>> = _selectedBookId
        .flatMapLatest { id ->
            if (id != null) repository.getBookmarksForBook(id) else flowOf<List<BookmarkEntity>>(emptyList())
        }

    val selectedBookNotes: Flow<List<NoteEntity>> = _selectedBookId
        .flatMapLatest { id ->
            if (id != null) repository.getNotesForBook(id) else flowOf<List<NoteEntity>>(emptyList())
        }

    // App Preferences / Settings States
    var currentLanguage by mutableStateOf("English") // English, Bengali, Arabic
    var currentTheme by mutableStateOf("System") // Light, Dark, System

    // Reader Preferences
    var readerColorMode by mutableStateOf("Light") // Light, Dark, Sepia
    var readerFontSize by mutableStateOf(16f) // raw sp
    var readerLineSpacing by mutableStateOf(1.4f) // multiple
    var readerMargin by mutableStateOf(16f) // dp
    var readerFontFamily by mutableStateOf("Serif") // Serif, Sans-Serif, Monospace

    // Search query
    private val _searchQuery = MutableStateFlow("")
    var searchQuery: String
        get() = _searchQuery.value
        set(value) {
            _searchQuery.value = value
        }

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    var selectedCategoryFilter: String?
        get() = _selectedCategoryFilter.value
        set(value) {
            _selectedCategoryFilter.value = value
        }

    // Admin state simulations
    var showAdminPanel by mutableStateOf(false)

    // Navigation state helper (for simple app states backstack inside main flow)
    var currentScreen by mutableStateOf("splash") // splash, onboarding, login, main, details, reader

    // Selected tab inside main dashboard
    var selectedDashboardTab by mutableStateOf("home") // home, categories, library, stats, profile, admin

    // Filter books based on query & category
    val filteredBooks: Flow<List<BookEntity>> = combine(allBooks, _searchQuery, _selectedCategoryFilter) { books, query, categoryId ->
        var list = books
        if (!categoryId.isNullOrEmpty()) {
            list = list.filter { it.categoryId == categoryId }
        }
        if (query.isNotEmpty()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.author.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.tags.contains(query, ignoreCase = true) ||
                        it.language.contains(query, ignoreCase = true) ||
                        it.metaTitle.contains(query, ignoreCase = true) ||
                        it.metaDescription.contains(query, ignoreCase = true) ||
                        it.contentMarkdown.contains(query, ignoreCase = true)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Language Content translations
    fun translate(key: String): String {
        return when (currentLanguage) {
            "Bengali" -> when (key) {
                "app_name" -> "মাইরিডার প্রো"
                "onboard_1_title" -> "যে কোন জায়গায় পড়ুন"
                "onboard_1_desc" -> "আপনার প্রিয় বই এবং ঐতিহাসিক নথিপত্রগুলো অফলাইনে যেকোনো সময় সাথে রাখুন।"
                "onboard_2_title" -> "অফলাইন লাইব্রেরি"
                "onboard_2_desc" -> "একটি ক্লিক দিয়ে ডাউনলোড করুন এবং সম্পূর্ণ বিনা ইন্টারনেট সংযোগে পড়ুন।"
                "onboard_3_title" -> "অগ্রগতি ট্র্যাকিং"
                "onboard_3_desc" -> "অ্যাডভান্সড রিডিং টাইম, স্ট্রিক এবং রিয়েল-টাইম পরিসংখ্যান দিয়ে অনুপ্রেরণা পান।"
                "skip" -> "এড়িয়ে যান"
                "continue" -> "চালিয়ে যান"
                "get_started" -> "শুরু করুন"
                "login" -> "লগইন করুন"
                "signup" -> "নিবন্ধন করুন"
                "guest" -> "অতিথি হিসেবে ব্যবহার করুন"
                "password" -> "পাসওয়ার্ড"
                "email" -> "ইমেইল"
                "name" -> "নাম"
                "search_hint" -> "বই, লেখক বা ক্যাটাগরি খুঁজুন..."
                "featured" -> "নির্বাচিত বই"
                "continue_reading" -> "পড়া চালিয়ে যান"
                "categories" -> "ক্যাটাগরি"
                "library" -> "লাইব্রেরি"
                "profile" -> "প্রোফাইল"
                "stats" -> "পরিসংখ্যান"
                "achievements" -> "অর্জনসমূহ"
                "download" -> "ডাউনলোড"
                "downloaded" -> "ডাউনলোড সম্পন্ন"
                "read_now" -> "এখনই পড়ুন"
                "favorites" -> "প্রিয় তালিকা"
                "streak" -> "দিনের স্ট্রিক"
                "hours" -> "ঘণ্টা"
                "pages" -> "পৃষ্ঠা"
                "settings" -> "সেটিংস"
                else -> key
            }
            "Arabic" -> when (key) {
                "app_name" -> "قارئي برو"
                "onboard_1_title" -> "اقرأ في أي مكان"
                "onboard_1_desc" -> "احمل كتبك المفضلة ومخطوطاتك التاريخية في جيبك أينما ذهبت دون قيود."
                "onboard_2_title" -> "المكتبة الذكية"
                "onboard_2_desc" -> "قم بتنزيل الكتب بضغطة زر واحدة واقرأها بالكامل دون الحاجة للإنترنت."
                "onboard_3_title" -> "الإحصائيات والتقدم"
                "onboard_3_desc" -> "تتبع ساعات القراءة والخطوط اليومية لتحفيز عقلك على مواصلة التعلم والنمو."
                "skip" -> "تخطى"
                "continue" -> "استمرار"
                "get_started" -> "ابدأ الآن"
                "login" -> "تسجيل الدخول"
                "signup" -> "إنشاء حساب"
                "guest" -> "دخول كضيف"
                "password" -> "كلمة المرور"
                "email" -> "البريد الإلكتروني"
                "name" -> "الاسم"
                "search_hint" -> "ابحث عن الكتب، المؤلف، أو التصنيف..."
                "featured" -> "الكتب المميزة"
                "continue_reading" -> "مواصلة القراءة"
                "categories" -> "الأقسام"
                "library" -> "المكتبة"
                "profile" -> "الملف الشخصي"
                "stats" -> "الإحصائيات"
                "achievements" -> "الإنجازات"
                "download" -> "تحميل"
                "downloaded" -> "تم التنزيل"
                "read_now" -> "اقرأ الآن"
                "favorites" -> "المفضلة"
                "streak" -> "أيام متتالية"
                "hours" -> "ساعة"
                "pages" -> "صفحة"
                "settings" -> "الإعدادات"
                else -> key
            }
            else -> when (key) {
                "app_name" -> "MyReaderPro"
                "onboard_1_title" -> "Read Anywhere"
                "onboard_1_desc" -> "Access your complete personal library and digital manuscripts anywhere, anytime without boundaries."
                "onboard_2_title" -> "Offline Library"
                "onboard_2_desc" -> "Download books in high speed and read them fully offline with progress preservation."
                "onboard_3_title" -> "Progress Tracking"
                "onboard_3_desc" -> "Monitor reading speeds, daily streaks, historical page turns, and earn special reader badges."
                "skip" -> "Skip"
                "continue" -> "Continue"
                "get_started" -> "Get Started"
                "login" -> "Log In"
                "signup" -> "Sign Up"
                "guest" -> "Guest Mode"
                "password" -> "Password"
                "email" -> "Email"
                "name" -> "Name"
                "search_hint" -> "Search books, authors, or categories..."
                "featured" -> "Featured Books"
                "continue_reading" -> "Continue Reading"
                "categories" -> "Categories"
                "library" -> "My Library"
                "profile" -> "Profile"
                "stats" -> "Statistics"
                "achievements" -> "Achievements"
                "download" -> "Download"
                "downloaded" -> "Downloaded"
                "read_now" -> "Read Now"
                "favorites" -> "Favorites"
                "streak" -> "Days Streak"
                "hours" -> "Hours"
                "pages" -> "Pages"
                "settings" -> "Settings"
                else -> key
            }
        }
    }

    // Nav actions
    fun navigateToBookDetails(bookId: String) {
        selectedBookId = bookId
        currentScreen = "details"
    }

    fun navigateToReader(bookId: String) {
        selectedBookId = bookId
        currentScreen = "reader"
    }

    fun goBack() {
        if (currentScreen == "reader") {
            currentScreen = "details"
        } else if (currentScreen == "details") {
            currentScreen = "main"
        } else if (currentScreen == "main" && selectedDashboardTab != "home") {
            selectedDashboardTab = "home"
        }
    }

    // Live Authentication States
    var authLoading by mutableStateOf(false)
        private set
    var authError by mutableStateOf<String?>(null)
        private set

    fun handleLoginWithEmail(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            authLoading = true
            authError = null
            val result = repository.loginWithFirebase(email, pass)
            authLoading = false
            result.fold(
                onSuccess = {
                    currentScreen = "main"
                    onSuccess()
                },
                onFailure = {
                    authError = it.localizedMessage ?: "Invalid login credentials."
                }
            )
        }
    }

    fun handleSignupWithEmail(email: String, name: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            authLoading = true
            authError = null
            val result = repository.signupWithFirebase(email, name, pass)
            authLoading = false
            result.fold(
                onSuccess = {
                    currentScreen = "main"
                    onSuccess()
                },
                onFailure = {
                    authError = it.localizedMessage ?: "Invalid signup credentials."
                }
            )
        }
    }

    fun handleGuestLogin(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            authLoading = true
            authError = null
            val result = repository.loginGuestWithFirebase()
            authLoading = false
            result.fold(
                onSuccess = {
                    currentScreen = "main"
                    onSuccess()
                },
                onFailure = {
                    authError = it.localizedMessage ?: "Guest login failed"
                }
            )
        }
    }

    fun handleGoogleLogin(email: String, name: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            authLoading = true
            authError = null
            val result = repository.loginGoogleWithFirebase(email, name)
            authLoading = false
            result.fold(
                onSuccess = {
                    currentScreen = "main"
                    onSuccess()
                },
                onFailure = {
                    authError = it.localizedMessage ?: "Google sign-in failed."
                }
            )
        }
    }

    fun clearAuthError() {
        authError = null
    }

    // User actions (for backwards compatibility/legacy triggers)
    fun handleLogin(email: String, name: String, isGuest: Boolean) {
        viewModelScope.launch {
            repository.loginUser(email, name, isGuest)
            currentScreen = "main"
        }
    }

    fun handleSignup(email: String, name: String) {
        viewModelScope.launch {
            repository.signupUser(email, name)
            currentScreen = "main"
        }
    }

    fun handleLogout() {
        viewModelScope.launch {
            repository.logoutUser()
            currentScreen = "login"
        }
    }

    fun saveProfileName(newName: String) {
        viewModelScope.launch {
            repository.updateProfileName(newName)
        }
    }

    // Book Actions
    fun toggleFavorite(bookId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(bookId)
        }
    }

    fun triggerDownload(bookId: String) {
        repository.startDownload(bookId)
    }

    fun pauseDownload(bookId: String) {
        repository.pauseDownload(bookId)
    }

    fun resumeDownload(bookId: String) {
        repository.resumeDownload(bookId)
    }

    fun cancelDownload(bookId: String) {
        repository.cancelDownload(bookId)
    }

    fun clearDownloadedFile(bookId: String) {
        viewModelScope.launch {
            repository.clearDownloadedFile(bookId)
        }
    }

    // Reader Actions
    fun updateReadingPosition(bookId: String, page: Int) {
        viewModelScope.launch {
            repository.trackReadingProgress(bookId, page)
        }
    }

    fun saveBookmark(bookId: String, page: Int, title: String, snippet: String) {
        viewModelScope.launch {
            repository.addBookmark(bookId, page, title, snippet)
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmark)
        }
    }

    fun saveNote(bookId: String, page: Int, text: String, highlight: String, colorHex: String) {
        viewModelScope.launch {
            repository.addNote(bookId, page, text, highlight, colorHex)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // Admin operations simulation
    fun adminAddBook(title: String, author: String, categoryId: String, description: String, pages: Int, size: String) {
        viewModelScope.launch {
            val databaseInstance = MyReaderDatabase.getDatabase(getApplication(), viewModelScope)
            val randomId = "b_" + System.currentTimeMillis()
            val newBook = BookEntity(
                id = randomId,
                title = title,
                author = author,
                categoryId = categoryId,
                description = description,
                pages = pages,
                fileSize = size,
                language = "English",
                rating = 4.5f,
                downloads = 0,
                isFeatured = true,
                contentMarkdown = """
                    # Chapter 1: $title Introduction
                    This is a custom book published instantly via the live admin web emulation portal.
                    
                    # Chapter 2: Expanding Minds
                    This is the dynamic content auto-generated for offline-reading exploration.
                """.trimIndent()
            )
            databaseInstance.readerDao().insertBooks(listOf(newBook))
            databaseInstance.readerDao().insertNotification(
                NotificationEntity(
                    title = "Admin: New Book Added",
                    message = "Title: '$title' is now available in category: $categoryId!"
                )
            )
        }
    }

    fun adminDeleteBook(bookId: String) {
        viewModelScope.launch {
            val databaseInstance = MyReaderDatabase.getDatabase(getApplication(), viewModelScope)
            val dao = databaseInstance.readerDao()
            dao.getBookByIdSync(bookId)?.let { book ->
                // Room update status or deletion
                val deletedBook = book.copy(id = bookId)
                // Just remove featured status to simulate hiding it, or actual delete
                // Let's hide it from main categories
                dao.updateBook(book.copy(categoryId = "hidden"))
            }
        }
    }

    fun clearHistoryForBook(bookId: String) {
        viewModelScope.launch {
            repository.clearHistory(bookId)
        }
    }

    // --- Book Request System ---
    val allBookRequests = repository.allBookRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered / Trending Requests (Popular requests of PENDING state sorted by count)
    val popularRequests = repository.allBookRequests
        .map { list -> list.filter { it.status == "PENDING" }.sortedByDescending { it.requestCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyRequested = repository.allBookRequests
        .map { list -> list.sortedByDescending { it.requestedAt } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun submitBookRequest(title: String, author: String, publisher: String, notes: String) {
        viewModelScope.launch {
            repository.submitBookRequest(title, author, publisher, notes)
        }
    }

    fun updateBookRequestStatus(requestId: Int, status: String) {
        viewModelScope.launch {
            repository.updateBookRequestStatus(requestId, status)
        }
    }

    // --- DMCA / Copyright System ---
    val allCopyrightClaims = repository.allCopyrightClaims
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun submitCopyrightClaim(
        fullName: String,
        email: String,
        org: String,
        title: String,
        url: String,
        desc: String,
        docs: String
    ) {
        viewModelScope.launch {
            repository.submitCopyrightClaim(fullName, email, org, title, url, desc, docs)
        }
    }

    fun updateCopyrightStatus(claimId: Int, status: String, decision: String, hideContent: Boolean) {
        viewModelScope.launch {
            repository.updateCopyrightClaimStatus(claimId, status, decision, hideContent)
        }
    }

    fun restoreCopyrightContent(claimId: Int) {
        viewModelScope.launch {
            repository.restoreCopyrightedContent(claimId)
        }
    }

    // --- Ad management (Future Ready Monetization Blocks) ---
    val allAdBlocks = repository.allAdBlocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleAdBlock(adId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateAdBlockStatus(adId, isEnabled)
        }
    }

    fun recordAdClick(adId: String) {
        viewModelScope.launch {
            repository.recordAdMetric(adId, isClick = true)
        }
    }

    fun recordAdImpression(adId: String) {
        viewModelScope.launch {
            repository.recordAdMetric(adId, isClick = false)
        }
    }

    // --- Backup & Recovery System ---
    var lastExportedBackupJson by mutableStateOf("")

    fun exportBackup() {
        viewModelScope.launch {
            lastExportedBackupJson = repository.exportBackupJson()
        }
    }

    fun restoreBackup(json: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.restorePreferencesFromBackup(json)
            onResult(success)
        }
    }

    // --- Monetization Systems Simulation ---
    fun changeMembership(type: String) {
        viewModelScope.launch {
            repository.changeUserMembership(type)
        }
    }

    fun toggleUserDeveloperRole() {
        viewModelScope.launch {
            repository.toggleUserRole()
        }
    }

    // --- Admin Dashboard State Handles ---
    val allUsers = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun adminAddCategory(id: String, name: String, iconName: String) {
        viewModelScope.launch {
            repository.addCategory(id, name, iconName)
        }
    }

    fun adminDeleteCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCategory(id)
        }
    }

    fun adminDeleteNotification(id: Int) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun adminModifyUser(email: String, name: String, role: String, membershipType: String) {
        viewModelScope.launch {
            repository.adminModifyUser(email, name, role, membershipType)
        }
    }

    fun adminBroadcastNotification(title: String, message: String) {
        viewModelScope.launch {
            val databaseInstance = MyReaderDatabase.getDatabase(getApplication(), viewModelScope)
            databaseInstance.readerDao().insertNotification(
                NotificationEntity(
                    title = title,
                    message = message
                )
            )
        }
    }

    // --- Gemini Book Summary State ---
    var bookSummaryLoading by mutableStateOf(false)
    var bookSummaryText by mutableStateOf<String?>(null)
    var bookSummaryError by mutableStateOf<String?>(null)

    fun fetchBookSummary(title: String, author: String, description: String) {
        viewModelScope.launch {
            bookSummaryLoading = true
            bookSummaryError = null
            try {
                val summary = GeminiService.generateBookSummary(
                    title = title,
                    author = author,
                    description = description,
                    language = currentLanguage
                )
                bookSummaryText = summary
            } catch (e: Exception) {
                bookSummaryError = e.localizedMessage ?: "Failed to generate summary."
            } finally {
                bookSummaryLoading = false
            }
        }
    }

    fun clearBookSummary() {
        bookSummaryText = null
        bookSummaryError = null
        bookSummaryLoading = false
    }

    // --- Research Chatbot State ---
    val chatMessages = androidx.compose.runtime.mutableStateListOf<Pair<String, String>>() // "user" / "model" to text
    var chatLoading by mutableStateOf(false)

    fun sendChatMessage(messageTxt: String) {
        val trimmed = messageTxt.trim()
        if (trimmed.isEmpty()) return

        chatMessages.add("user" to trimmed)
        viewModelScope.launch {
            chatLoading = true
            try {
                val history = chatMessages.dropLast(1).toList()
                val response = GeminiService.generateChatResponse(
                    history = history,
                    userMessage = trimmed,
                    language = currentLanguage
                )
                chatMessages.add("model" to response)
            } catch (e: Exception) {
                chatMessages.add("model" to "Error processing research request: ${e.localizedMessage}")
            } finally {
                chatLoading = false
            }
        }
    }

    fun clearChat() {
        chatMessages.clear()
        val welcome = when (currentLanguage) {
            "Bengali" -> "হ্যালো! আমি আপনার সাহিত্য বিষয়ক গবেষক ও বই বন্ধু। যেকোনো বই, লেখক বা সাহিত্যিক ধারণা নিয়ে জিজ্ঞাসা করুন!"
            "Arabic" -> "مرحباً! أنا مساعدك البحثي للأدب والكتب. اطرح عليّ أي سؤال حول الروايات، الكتاب، أو المفاهيم الأدبية!"
            else -> "Hello! I'm your literary research partner. Ask me design aspects, history, key insights or book recommendations!"
        }
        chatMessages.add("model" to welcome)
    }
}
