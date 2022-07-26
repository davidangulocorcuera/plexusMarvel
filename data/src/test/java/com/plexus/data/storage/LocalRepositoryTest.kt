package com.plexus.data.storage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.plexus.data.storage.database.AppDatabase
import com.plexus.data.storage.database.LocalRepository
import com.plexus.domain.Character
import com.plexus.domain.Image
import junit.framework.Assert.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.times

class LocalRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: LocalRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repository = mock(LocalRepository::class.java)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `save all characters in database`() = kotlin.run {
        Mockito.verify(repository, times(1)).saveAllCharacters(arrayListOf(
            Character(
                id = 1,
                name = "example",
                description = "example description",
                thumbnail = Image(
                    path = "example path",
                    extension = "jpg"
                )
            )
        ),db)
    }
    @Test
    fun `get all characters from database`() = kotlin.run {
        assertNotNull(Mockito.verify(repository, times(1)).getAllCharacters(db))
    }
}