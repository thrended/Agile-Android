package com.example.agileandroidalpha.obsolete_excluded

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.agileandroidalpha.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

@Database(
    entities = [
        //UserOLD::class,
        //BoardOLD::class,
        //SprintRoom::class,
        TaskOLD::class,
        OldSubTask::class],
    version = 1,
    exportSchema = false)
@TypeConverters(ConvertersOLD::class)
abstract class AgileDroidRoomDatabase : RoomDatabase() {

    abstract fun taskDao(): OldTaskDao

    companion object {
        // Singleton Design Pattern
        @Volatile
        private var INSTANCE: AgileDroidRoomDatabase? = null

        fun getDB(
            context: Context,
            scope:CoroutineScope) : AgileDroidRoomDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgileDroidRoomDatabase::class.java,
                    "agile_android_localDB"
                    ).addCallback(AgileDroidDBCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AgileDroidDBCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.taskDao())
                }
            }
        }

        fun genTasks(): TaskOLD {
            return TaskOLD(0)
        }

        fun rndPri(): Priority {
            val n = Random.nextInt(8)
            return enumValues<Priority>()[n]
        }

        fun titleDescGen(): Triple<String, String, String> {

            val templates: List<String> = listOf(
                "a task_subtask",
                "another task_subtask",
                "call me i want job",
                 )

            val descriptions: List<String> = listOf(
                "a description <Insert description here>",
                "another description <Your ad here>",
                "yet another description <Was something needed?>",
                "good luck getting this",
                "you're not getting this",
                "UNTRADEABLE",
                 )

            return Triple(templates[Random.nextInt(templates.size)],
                        descriptions[Random.nextInt(descriptions.size)],
                        descriptions[Random.nextInt(descriptions.size)])
        }

        fun storyPtGen(n: Int = 100): Int {
            var y = n
            if (n < 2 || n > 100) {
                y = if (n < 2) 2 else 100
            }
            val ary: IntArray = intArrayOf(1,2,3,5,8,13,20,40,80,99)
            var x = Random.nextInt(y)
            while (!ary.contains(x)) {
                x = Random.nextInt(y)
            }
            return x
        }

        suspend fun populateDatabase(oldTaskDao: OldTaskDao) {

            oldTaskDao.deleteAll()

            for(i in 0 until 20) {
                val triple = titleDescGen()
                val taskOLD = TaskOLD(
                    i,
                    BasicInfo(
                        triple.first,
                        triple.first,
                        triple.second,
                        triple.third,
                        rndPri(),
                        storyPtGen(),
                    )
                )
                oldTaskDao.insertTask(taskOLD)
            }
        }
    }
}