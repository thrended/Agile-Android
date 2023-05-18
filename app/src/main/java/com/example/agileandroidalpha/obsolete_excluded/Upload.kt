package com.example.agileandroidalpha.firebase.firestore

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Upload(

) {

    private val sprintRef = Firebase.firestore.collection("sprints")
    private val storyRef = Firebase.firestore.collection("stories")
    private val subtaskRef = Firebase.firestore.collection("subtasks")
    private val userRef = Firebase.firestore.collection("users")


//    suspend fun saveSprintHelper() {
//        val countQ = userRef.whereNotEqualTo("name", null).count()
//        countQ.get(AggregateSource.SERVER).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val snapshot = task.result
//                Log.d(tag, "Count: ${snapshot.count}")
//            } else {
//                Log.d(tag, "Count failed: ", task.exception)
//            }
//
//        }
//
//        val n = sprintRef.count()
//        saveSprint(
//            SprintRoom(
//                title = taskTitle.value.text,
//                content = taskBody.value.text,
//                desc = taskBody.value.text.substring(
//                    min(taskBody.value.text.length , max(75, taskBody.value.text.length / 5) )
//                ),
//                DoD = "Basic DoD = All Subtasks marked as done + All paperwork done",
//                timestamp = System.currentTimeMillis(),
//                points = taskPoints.value,
//                priority = Priority.valueOf(taskPri.value.text),
//                color = taskColor.value,
//                assignee = taskAss.value.text,
//                reporter = taskRep.value.text,
//                status = taskStatus.value,
//                done = taskDone.value,
//                SID = sprintId ?: taskSID.value,
//                UID = taskAss.value.id,
//                UID2 = taskRep.value.id,
//                taskId = currentId
//            )
//        )
//    }

//    private fun saveSprint(sprint: Sprint) = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            sprintRef.add(sprint).await()
//            withContext(Dispatchers.Main) {
//                Log.d("Upload","Sprint online data successfully saved.")
//            }
//        } catch(e: Exception) {
//            withContext(Dispatchers.Main) {
//                Log.d("Upload", e.message?: "Error saving online sprint data")
//            }
//        }
//
//    }

//    private fun saveStoryHelper() {
//        saveStory(
//            Story(
//                title = taskTitle.value.text,
//                body = taskBody.value.text,
//                desc = taskBody.value.text.substring(
//                    min(taskBody.value.text.length , max(75, taskBody.value.text.length / 5) )
//                ),
//                DoD = "Basic DoD = All Subtasks marked as done + All paperwork done",
//                timestamp = System.currentTimeMillis(),
//                points = taskPoints.value,
//                priority = Priority.valueOf(taskPri.value.text),
//                color = taskColor.value,
//                assignee = taskAss.value.text,
//                reporter = taskRep.value.text,
//                status = taskStatus.value,
//                done = taskDone.value,
//                SID = sprintId ?: taskSID.value,
//                assId = taskAss.value.id,
//                repId = taskRep.value.id,
//                taskId = currentId
//            )
//        )
//    }

//    private fun saveStory(story: Story) = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            storyRef.add(story).await()
//            withContext(Dispatchers.Main) {
//                Log.d("Upload","Story/task online data successfully saved.")
//            }
//        } catch(e: Exception) {
//            withContext(Dispatchers.Main) {
//                Log.d("Upload", e.message?: "Error saving online story data")
//            }
//        }
//
//    }


//    private fun saveStory(story: Story) = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            storyRef.add(story).await()
//            withContext(Dispatchers.Main) {
//                Toast.makeText(this@MainActivity, "Story/task online data successfully saved.", Toast.LENGTH_SHORT)
//            }
//        } catch(e: Exception) {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(this@MainActivity, "Error saving online story data", Toast.LENGTH_SHORT)
//            }
//        }
//
//    }

}