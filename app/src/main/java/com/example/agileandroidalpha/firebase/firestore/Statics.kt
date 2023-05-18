package com.example.agileandroidalpha.firebase.firestore

import androidx.compose.runtime.mutableStateOf
import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.User
import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow

data class Statics(
    var binSprints: MutableList<Sprint> = mutableListOf(),
    var binMessages: MutableList<ChatMessage> = mutableListOf(),
    var binUsers: MutableList<FireUser> = mutableListOf(),
    var binStories: MutableList<Story> = mutableListOf(),
    var binSubtasks: MutableList<SubTask> = mutableListOf(),

    ) {
    companion object Cache {
        @JvmStatic var oldSprints: MutableList<Sprint> = mutableListOf()
        @JvmStatic var oldMessages: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic var oldUsers: MutableList<FireUser> = mutableListOf()
        @JvmStatic var oldStories: MutableList<Story> = mutableListOf()
        @JvmStatic var oldSubtasks: MutableList<SubTask> = mutableListOf()
        @JvmStatic var addedSprints: MutableList<Sprint> = mutableListOf()
        @JvmStatic var addedMessages: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic var addedUsers: MutableList<FireUser> = mutableListOf()
        @JvmStatic var addedStories: MutableList<Story> = mutableListOf()
        @JvmStatic var addedSubtasks: MutableList<SubTask> = mutableListOf()
        @JvmStatic var modifiedSprints: MutableList<Sprint> = mutableListOf()
        @JvmStatic var modifiedMessages: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic var modifiedUsers: MutableList<FireUser> = mutableListOf()
        @JvmStatic var modifiedStories: MutableList<Story> = mutableListOf()
        @JvmStatic var modifiedSubtasks: MutableList<SubTask> = mutableListOf()
        @JvmStatic var removedSprints: MutableList<Sprint> = mutableListOf()
        @JvmStatic var removedMessages: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic var removedUsers: MutableList<FireUser> = mutableListOf()
        @JvmStatic var removedStories: MutableList<Story> = mutableListOf()
        @JvmStatic var removedSubtasks: MutableList<SubTask> = mutableListOf()
        @JvmStatic var newSprints: MutableList<Sprint> = mutableListOf()
        @JvmStatic var newMessages: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic var newUsers: MutableList<FireUser> = mutableListOf()
        @JvmStatic var newStories: MutableList<Story> = mutableListOf()
        @JvmStatic var newSubtasks: MutableList<SubTask> = mutableListOf()
        @JvmStatic var sprintsList: MutableList<Sprint> = mutableListOf()
        @JvmStatic var messagesList: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic var usersList: MutableList<FireUser> = mutableListOf()
        @JvmStatic var storiesList: MutableList<Story> = mutableListOf()
        @JvmStatic var subtasksList: MutableList<SubTask> = mutableListOf()
        @JvmStatic var recentUsers: MutableList<FireUser> = mutableListOf()
        @JvmStatic var recentMessages: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic var recentMessage = mutableStateOf(ChatMessage())
        @JvmStatic var recentSprints: MutableList<Sprint> = mutableListOf()
        @JvmStatic var recentSprint = mutableStateOf(Sprint())
        @JvmStatic var recentStories: MutableList<Story> = mutableListOf()
        @JvmStatic var recentStory = mutableStateOf(Story())
        @JvmStatic var recentSubTasks: MutableList<SubTask> = mutableListOf()
        @JvmStatic var recentSubTask = mutableStateOf(SubTask())
        @JvmStatic var list: MutableList<Any> = mutableListOf()
        @JvmStatic var result: MutableStateFlow<Any> = MutableStateFlow(Any())
        @JvmStatic var ids: MutableList<String> = mutableListOf()
        @JvmStatic var oldIds: MutableList<String> = mutableListOf()
        @JvmStatic var currentUser: FireUser? = null
        @JvmStatic var chatLog: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic var unreadMessages: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic val chatMap: MutableMap<ChatMessage, FireUser> = mutableMapOf()
        @JvmStatic val messageMap: MutableMap<ChatMessage, FireUser> = mutableMapOf()
        @JvmStatic var blocked: MutableList<String> = mutableListOf()
        @JvmStatic var unreadCount: Int = 0
    }

    object Updated {

        @JvmStatic var updatedSprints: MutableList<Sprint> = mutableListOf()
        @JvmStatic var updatedChats: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic var updatedUsers: MutableList<FireUser> = mutableListOf()
        @JvmStatic var updatedStories: MutableList<Story> = mutableListOf()
        @JvmStatic var updatedSubtasks: MutableList<SubTask> = mutableListOf()
        @JvmStatic val gudMap = mutableMapOf<Story, List<SubTask>>()
        @JvmStatic val yukMap = mutableMapOf<Task, List<Subtask>>()
        @JvmStatic val fullTasks: MutableList<TaskAndSubtasks> = mutableListOf()
        @JvmStatic val sprintUserMap = mutableMapOf<Sprint, Set<Long>>()

    }

    object Deleted {
        @JvmStatic var deletedSprints: MutableList<Sprint> = mutableListOf()
        @JvmStatic var deletedMessages: MutableList<ChatMessage> = mutableListOf()
        @JvmStatic var deletedUsers: MutableList<FireUser> = mutableListOf()
        @JvmStatic var deletedStories: MutableList<Story> = mutableListOf()
        @JvmStatic var deletedSubtasks: MutableList<SubTask> = mutableListOf()
        @JvmStatic var deletedRooms: MutableList<SprintRoom> = mutableListOf()
        @JvmStatic var deletedUrs: MutableList<User> = mutableListOf()
        @JvmStatic var deletedTasks: MutableList<Task> = mutableListOf()
        @JvmStatic var deletedSubs: MutableList<Subtask> = mutableListOf()
    }

    object Cloner {
        @JvmStatic var clonedSprint: Sprint? = null
        @JvmStatic var clonedRoom: SprintRoom? = null
        @JvmStatic var clonedTasks: MutableList<Task> = mutableListOf()
        @JvmStatic var clonedTask: Task? = null
        @JvmStatic var clonedSubtasks: MutableList<Subtask> = mutableListOf()
        @JvmStatic var clonedSubtask: Subtask? = null
    }

    object Recycler {
        @JvmStatic var lastDelSprints: MutableList<Sprint> = mutableListOf()
        @JvmStatic var lastDelUsers: MutableList<FireUser> = mutableListOf()
        @JvmStatic var lastDelStories: MutableList<Story> = mutableListOf()
        @JvmStatic var lastDelSubtasks: MutableList<SubTask> = mutableListOf()
    }

    object Users {
        @JvmStatic var IDNameMap: MutableMap<Long, String?> = mutableMapOf()
        @JvmStatic var userMap: MutableMap<FirebaseUser, FireUser> = mutableMapOf()
        @JvmStatic var usernameMap: MutableMap<FirebaseUser, String> = mutableMapOf()
        @JvmStatic var uidFireMap: MutableMap<String, FireUser> = mutableMapOf()
        @JvmStatic var uidIDMap: MutableMap<String, Long?> = mutableMapOf()
        @JvmStatic var uidNameMap: MutableMap<String, String?> = mutableMapOf()
        @JvmStatic var uidUidMap: MutableMap<String, String?> = mutableMapOf()
    }

}
