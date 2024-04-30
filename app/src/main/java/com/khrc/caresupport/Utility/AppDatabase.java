package com.khrc.caresupport.Utility;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.khrc.caresupport.Dao.ChatDao;
import com.khrc.caresupport.Dao.CodeBookDao;
import com.khrc.caresupport.Dao.ComplaintsDao;
import com.khrc.caresupport.Dao.DailyConditionDao;
import com.khrc.caresupport.Dao.LogDao;
import com.khrc.caresupport.Dao.MedHistoryDao;
import com.khrc.caresupport.Dao.ObstericDao;
import com.khrc.caresupport.Dao.PregnancyDao;
import com.khrc.caresupport.Dao.ProfileDao;
import com.khrc.caresupport.Dao.UsersDao;
import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.CodeBook;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.DailyCondition;
import com.khrc.caresupport.entity.LogBook;
import com.khrc.caresupport.entity.MedHistory;
import com.khrc.caresupport.entity.MomProfile;
import com.khrc.caresupport.entity.Obsteric;
import com.khrc.caresupport.entity.Pregnancy;
import com.khrc.caresupport.entity.Users;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(  entities = {Complaints.class, Users.class, LogBook.class, ChatResponse.class, Pregnancy.class, MedHistory.class,
        MomProfile.class, CodeBook.class, DailyCondition.class, Obsteric.class},
        version = 4 , exportSchema = true)

@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract ComplaintsDao complaintsDao();
    public abstract UsersDao usersDao();
    public abstract LogDao logDao();
    public abstract ChatDao chatDao();
    public abstract PregnancyDao pregnancyDao();
    public abstract MedHistoryDao medHistoryDao();
    public abstract ProfileDao profileDao();
    public abstract CodeBookDao codeBookDao();
    public abstract DailyConditionDao dailyConditionDao();
    public abstract ObstericDao obstericDao();

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
