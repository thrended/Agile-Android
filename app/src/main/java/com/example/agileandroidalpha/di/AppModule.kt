package com.example.agileandroidalpha.di

import android.app.Application
import androidx.room.Room
import com.example.agileandroidalpha.feature_board.data.data_source.AllDB
import com.example.agileandroidalpha.feature_board.data.repository.RoomRepoObj
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import com.example.agileandroidalpha.feature_board.domain.use_case.*
import com.example.agileandroidalpha.feature_board.domain.use_case.admin.AddUser
import com.example.agileandroidalpha.feature_board.domain.use_case.admin.LoadUsers
import com.example.agileandroidalpha.feature_board.domain.use_case.admin.ModifyUser
import com.example.agileandroidalpha.feature_board.domain.use_case.admin.WipeData
import com.example.agileandroidalpha.feature_board.domain.use_case.sprint.*
import com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask.*
import com.example.agileandroidalpha.feature_board.presentation.sprint.AddEditSprint
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAgileDroidRoomDB(app: Application): AllDB {
        return Room.databaseBuilder(
            app,
            AllDB::class.java,
            AllDB.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAllRepo(db: AllDB): RoomRepo {
        return RoomRepoObj(db.allDao)
    }

    @Provides
    @Singleton
    fun provideTaskUCs(repo: RoomRepo): TaskUseCases {
        return TaskUseCases(
            getTasks = GetTasks(repo),
            deleteTask = DeleteTask(repo),
            doneTask = DoneTask(repo),
            markSubtask = MarkSubtask(repo),
            addTask = AddTask(repo),
            addSubtask = AddSubtask(repo),
            addSubtasks = AddSubtasks(repo),
            deleteSubtask = DeleteSubtask(repo),
            dragSubtask = DragSubtask(repo),
            editSubtask = EditSubtask(repo),
            getSubtasks = GetSubtasks(repo),
            getTask = GetTask(repo),
            getSprint = GetSprint(repo)
        )
    }

    @Provides
    @Singleton
    fun provideSprintUCs(repo: RoomRepo): SprintUseCases {
        return SprintUseCases(
            cloneSprint = CloneSprint(repo),
            quickAddSprint = QuickAddSprint(repo),
            startSprint = StartSprint(repo),
            deleteSprint = DeleteSprint(repo),
            loadSprints = LoadSprints(repo),
            addEditSprint = AddEditSprint(repo),
            getSprint = GetSprint(repo),
            loadUsers = LoadUsers(repo)
        )
    }

    @Provides
    @Singleton
    fun provideBoardUCs(repo: RoomRepo): BoardUseCases {
        return BoardUseCases(
            loadSprints = LoadSprints(repo),
            loadActiveSprint = LoadActiveSprint(repo)
        )
    }

    @Provides
    @Singleton
    fun provideAdminUCs(repo: RoomRepo): AdminUseCases {
        return AdminUseCases(
            addUser = AddUser(repo),
            loadUsers = LoadUsers(repo),
            modifyUser = ModifyUser(repo),
            wipeData = WipeData(repo)
        )
    }

    @Provides
    @Singleton
    fun provideUserUCs(repo: RoomRepo): UserUseCases {
        return UserUseCases(
            loadUsers = LoadUsers(repo),
            modifyUser = ModifyUser(repo)
        )
    }
}