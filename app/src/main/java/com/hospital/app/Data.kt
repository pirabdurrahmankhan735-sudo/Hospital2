package com.hospital.app
import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "patients") data class Patient(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val phone: String)
@Entity(tableName = "doctors") data class Doctor(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val specialty: String, val city: String, val fee: String, val verified: Boolean)
@Entity(tableName = "appointments") data class Appointment(@PrimaryKey(autoGenerate = true) val id: Long = 0, val patientName: String, val doctorName: String, val date: String)

@Dao interface AppDao {
    @Insert suspend fun add(p: Patient)
    @Insert suspend fun add(d: Doctor)
    @Insert suspend fun add(a: Appointment)
    @Delete suspend fun del(p: Patient)
    @Delete suspend fun del(d: Doctor)
    @Delete suspend fun del(a: Appointment)
    @Query("SELECT * FROM patients ORDER BY id DESC") fun patients(): Flow<List<Patient>>
    @Query("SELECT * FROM doctors ORDER BY verified DESC, id DESC") fun doctors(): Flow<List<Doctor>>
    @Query("SELECT * FROM appointments ORDER BY date") fun appointments(): Flow<List<Appointment>>
}

@Database(entities = [Patient::class, Doctor::class, Appointment::class], version = 1, exportSchema = false)
abstract class Db : RoomDatabase() {
    abstract fun dao(): AppDao
    companion object {
        @Volatile private var i: Db? = null
        fun get(c: Context): Db = i ?: synchronized(this) {
            i ?: Room.databaseBuilder(c.applicationContext, Db::class.java, "hospital.db").build().also { i = it }
        }
    }
}
