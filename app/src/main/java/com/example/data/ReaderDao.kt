package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReaderDao {
    // Users
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getActiveUserSync(): UserEntity?

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun clearActiveSessions()

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Categories
    @Query("SELECT * FROM categories")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: String)

    // Books
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: String): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookByIdSync(id: String): BookEntity?

    @Query("SELECT * FROM books WHERE isFavorite = 1")
    fun getFavoriteBooks(): Flow<List<BookEntity>>

    @Query("SELECT b.* FROM books b INNER JOIN reading_history h ON b.id = h.bookId ORDER BY h.timestamp DESC")
    fun getReadingHistoryBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE downloadStatus = 'DOWNLOADED'")
    fun getDownloadedBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Update
    suspend fun updateBook(book: BookEntity)

    // Bookmarks
    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY pageNumber ASC")
    fun getBookmarksForBook(bookId: String): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    // Notes
    @Query("SELECT * FROM reading_notes WHERE bookId = :bookId ORDER BY pageNumber ASC")
    fun getNotesForBook(bookId: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    // History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("DELETE FROM reading_history WHERE bookId = :bookId")
    suspend fun deleteHistoryForBook(bookId: String)

    // Achievements
    @Query("SELECT * FROM achievements")
    fun getAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Int)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Int)

    // Book Requests
    @Query("SELECT * FROM book_requests ORDER BY requestedAt DESC")
    fun getAllBookRequests(): Flow<List<BookRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookRequest(request: BookRequestEntity)

    @Update
    suspend fun updateBookRequest(request: BookRequestEntity)

    @Delete
    suspend fun deleteBookRequest(request: BookRequestEntity)

    @Query("SELECT * FROM book_requests WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchBookRequests(query: String): Flow<List<BookRequestEntity>>

    // Copyright / DMCA Claims
    @Query("SELECT * FROM copyright_claims ORDER BY claimDate DESC")
    fun getAllCopyrightClaims(): Flow<List<CopyrightClaimEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCopyrightClaim(claim: CopyrightClaimEntity)

    @Update
    suspend fun updateCopyrightClaim(claim: CopyrightClaimEntity)

    // Ad Blocks management
    @Query("SELECT * FROM ad_blocks")
    fun getAllAdBlocks(): Flow<List<AdBlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdBlock(adBlock: AdBlockEntity)

    @Update
    suspend fun updateAdBlock(adBlock: AdBlockEntity)
}
