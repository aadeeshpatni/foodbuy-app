package com.example.dashboard.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dashboard.model.Product;

@Database(entities = Product.class, exportSchema = false, version = 2)
public abstract class ProductDatabase extends RoomDatabase {
    private static final String DB_NAME = "cartDB";
    private static ProductDatabase instance;

    public static synchronized ProductDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ProductDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract ProductDao productDao();
}
