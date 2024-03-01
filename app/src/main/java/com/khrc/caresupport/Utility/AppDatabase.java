package com.khrc.caresupport.Utility;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.khrc.caresupport.Dao.ComplaintsDao;
import com.khrc.caresupport.Dao.LogDao;
import com.khrc.caresupport.Dao.UsersDao;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.LogBook;
import com.khrc.caresupport.entity.Users;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(  entities = {Complaints.class, Users.class, LogBook.class},
        version = 2 , exportSchema = true)

@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract ComplaintsDao complaintsDao();
    public abstract UsersDao usersDao();
    public abstract LogDao logDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, "providers")
                        .addCallback(sRoomDatabaseCallback)
                        //.addMigrations(MIGRATION_1_2)
                        //.fallbackToDestructiveMigrationOnDowngrade()
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        //Return Database
        return INSTANCE;
    }

    private static final Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };


    // Callback interface to notify when all entities are reset
    public interface ResetCallback {
        void onResetComplete();
    }
}
