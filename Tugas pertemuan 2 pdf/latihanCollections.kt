data class Mahasiswa(
    val nim: String,
    val nama: String,
    val ipk: Double
)

sealed class HasilOperasi {
    data class Success(val data: List<Mahasiswa>) : HasilOperasi()
    data class Error(val pesan: String) : HasilOperasi()
}

fun prosesMahasiswa(mahasiswa: List<Mahasiswa>): HasilOperasi {

    return try {
        val hasil = mahasiswa
            .filter { it.ipk >= 3.5 }
            .sortedBy { it.nama }

        if (hasil.isEmpty()) {
            HasilOperasi.Error("Tidak ada mahasiswa dengan IPK >= 3.5")
        } else {
            HasilOperasi.Success(hasil)
        }

    } catch (e: Exception) {
        HasilOperasi.Error("Terjadi kesalahan: ${e.message}")
    }
}

fun main() {

    val daftarMahasiswa = listOf(
        Mahasiswa("001", "Budi", 3.80),
        Mahasiswa("002", "Andi", 3.20),
        Mahasiswa("003", "Citra", 3.90),
        Mahasiswa("004", "Dina", 3.45),
        Mahasiswa("005", "Eka", 3.70)
    )

    val hasil = prosesMahasiswa(daftarMahasiswa)

    when (hasil) {
        is HasilOperasi.Success -> {
            println("Mahasiswa dengan IPK >= 3.5:")
            
            hasil.data.forEach {
                println(
                    "NIM: ${it.nim}, " +
                    "Nama: ${it.nama}, " +
                    "IPK: ${it.ipk}"
                )
            }
        }

        is HasilOperasi.Error -> {
            println("Error: ${hasil.pesan}")
        }
    }
}