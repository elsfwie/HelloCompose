package com.example.ch07.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// TODO [T4.3] Buat `MIGRATION_1_2 = object : Migration(1, 2)` yang menjalankan
//   ALTER TABLE notes ADD COLUMN tags TEXT NOT NULL DEFAULT '[]'
//   ('[]' = daftar tag kosong, supaya catatan lama tetap terbaca).
val MIGRATION_1_2 = object : Migration(1, 2){
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notes ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("UPDATE notes updatedAt = createdAt")
    }
}

@Database(
    entities = [NoteEntity::class],
    version = 2,                 // TODO [T3.2][T4.3] naikkan setiap skema berubah
    exportSchema = true          // JSON skema -> app/schemas/ (lihat build.gradle.kts)
)
// TODO [T4.2] Daftarkan converter: @TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        // @Volatile: INSTANCE selalu dibaca dari memori utama, bukan cache thread
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        // synchronized: mencegah dua thread membuat instance bersamaan
        fun getInstance(context: Context): NoteDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    // TODO [T4.3] Ganti baris di bawah dengan
                    //   .addMigrations(MIGRATION_1_2, ...). fallbackToDestructiveMigration
                    //   MENGHAPUS data pengguna saat versi naik: hanya boleh saat
                    //   pengembangan awal, jangan dipakai di produksi.
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
