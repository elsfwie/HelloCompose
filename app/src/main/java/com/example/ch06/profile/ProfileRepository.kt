package com.example.ch06.profile

data class UserProfile(
    val username: String,
    val notificationsEnabled: Boolean
)

interface ProfileRepository {
    fun getProfile(): UserProfile
}

// Data awal lokal. Perubahan sesi dikelola oleh ProfileViewModel.
class FakeProfileRepository : ProfileRepository {
    override fun getProfile() = UserProfile(
        username = "Mahasiswa Android",
        notificationsEnabled = true
    )
}
