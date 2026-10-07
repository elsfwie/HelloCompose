package com.example.ch07.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

// Satu objek = satu baris tabel "notes". Skema awal (versi 1).
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,

    @ColumnInfo (defaultValue = "0")
    val updatedAt: Long = createdAt
    // TODO [T4.1] Tambahkan kolom `tags: List<String> = emptyList()`. Room tidak
    //   mengenal List<String>, jadi butuh TypeConverter (T4.2) dan Migration (T4.3).
)
