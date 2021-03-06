package edu.psu.ist402.projectfittness;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses Android SQLiteAssetHelper (http://github.com/jgilfelt/android-sqlite-asset-helper)
 *
 *
 * PUBLIC METHODS:
 *
 * updateUser(String name, String birthdate, float height, float weight)
 *
 * getUserInfo()
 *      RETURNS User OBJECT
 *
 * addExerciseProgress(int datetime, int id, int sets, int reps, int length)
 *
 * getExerciseProgressList()
 *      RETURNS List<ExerciseProgress> OBJECT
 *
 * getInfoForID(int id)
 *      RETURNS ExerciseInfo OBJECT
 *
 * getExerciseNameList()
 *      RETURNS List<ExerciseInfo> OBJECT
 */




public class Exercise_DB extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "exercise.db";

    // Change this value when database changes
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USER = "User";
    private static final String TABLE_PROGRESS = "Progress";
    private static final String TABLE_EXERCISE_LIST = "Exercise_List";

    // Column Names
    // User
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_BIRTHDATE = "BIRTHDATE";
    private static final String COLUMN_HEIGHT = "HEIGHT";
    private static final String COLUMN_WEIGHT = "WEIGHT";
    // Progress
    private static final String COLUMN_START_DATETIME = "START_DATETIME";
    private static final String COLUMN_END_DATETIME = "END_DATETIME";
    private static final String COLUMN_EXERCISE_ID = "EXERCISE_ID";
    private static final String COLUMN_SETS = "SETS";
    // Exercise_List
    private static final String COLUMN_EXERCISE_NAME = "EXERCISE_NAME";
    private static final String COLUMN_TARGETED_MUSCLE = "TARGETED_MUSCLE";
    private static final String COLUMN_DIFFICULTY = "DIFFICULTY";
    private static final String COLUMN_IMAGE_REF = "IMAGE_REF";
    private static final String COLUMN_SUGGESTED_PATTERN = "SUGGESTED_PATTERN";


    public Exercise_DB (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //
    // User DAO
    //

    // Update User
    public void updateUser(String name, String birthdate, float height, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Delete existing data
        String sql = "DELETE FROM " + TABLE_USER;
        db.execSQL(sql);

        //Add user
        sql = "INSERT INTO " + TABLE_USER
                + " (" + COLUMN_NAME + ", " + COLUMN_BIRTHDATE + ", " + COLUMN_HEIGHT + ", " + COLUMN_WEIGHT
                + ") VALUES('" + name + "', '" + birthdate + "', " + height + ", " + weight + ")";
        db.execSQL(sql);

        db.close();
    }


    // Get UserInfo
    // Returns User object
    public User getUserInfo() {
        User user = new User();

        String selectQuery = "SELECT * FROM " + TABLE_USER + " ORDER BY " + COLUMN_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                User temp = new User(
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_BIRTHDATE)),
                        cursor.getFloat(cursor.getColumnIndex(COLUMN_HEIGHT)),
                        cursor.getFloat(cursor.getColumnIndex(COLUMN_WEIGHT))
                );
                user = temp;
            } while (cursor.moveToNext());
        }
        db.close();
        
        return user;
    }

    //
    // Progress DAO
    //


    // Add ExerciseEntry
    public void addExerciseProgress(String start_datetime, String end_datetime, int sets, int exercise_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Add entry
        String sql = "INSERT INTO " + TABLE_PROGRESS
                + " (" + COLUMN_START_DATETIME + ", " + COLUMN_END_DATETIME + ", " + COLUMN_SETS + ", " + COLUMN_EXERCISE_ID
                + ") VALUES(" + start_datetime + ", " + end_datetime + ", " + sets + ", " + exercise_id + ")";
        db.execSQL(sql);

        db.close();
    }

    // Add new exercise
    public void addExercise(String exercise_name, String suggested_pattern) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Add entry
        String sql = "INSERT INTO " + TABLE_EXERCISE_LIST
                + " (" + COLUMN_EXERCISE_NAME + ", " + COLUMN_SUGGESTED_PATTERN
                + ") VALUES(\"" + exercise_name + "\", \"" + suggested_pattern + "\")";
        db.execSQL(sql);

        db.close();
    }


    // Get ExerciseProgressList
    // Returns a list of ExerciseProgress objects
    public List<ExerciseProgress> getExerciseProgressList() {
        List<ExerciseProgress> ExerciseProgressList = new ArrayList<ExerciseProgress>();

        String selectQuery = "SELECT * FROM " + TABLE_PROGRESS + " ORDER BY " + COLUMN_START_DATETIME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExerciseProgress entry = new ExerciseProgress(
                        cursor.getString(cursor.getColumnIndex(COLUMN_START_DATETIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_END_DATETIME)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_EXERCISE_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_SETS))
                );
                ExerciseProgressList.add(entry);
            } while (cursor.moveToNext());
        }
        db.close();

        return ExerciseProgressList;
    }






    //
    // Exercise_List DAO
    //


    // Get InfoForId
    // Returns ExerciseInfo object
    public ExerciseInfo getInfoForID(int id) {
        ExerciseInfo info = new ExerciseInfo();
        info.setExercise_id(id);

        String selectQuery = "SELECT * FROM " + TABLE_EXERCISE_LIST + " ORDER BY " + COLUMN_EXERCISE_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Get info for matching id
        if (cursor.moveToFirst()) {
            do {
                int current_id = cursor.getInt(cursor.getColumnIndex(COLUMN_EXERCISE_ID));
                if ( current_id == id ) {
                    info.setDifficulty(cursor.getString(cursor.getColumnIndex(COLUMN_DIFFICULTY)));
                    info.setExercise_name(cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISE_NAME)));
                    info.setImage_ref(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_REF)));
                    info.setSuggested_pattern(cursor.getString(cursor.getColumnIndex(COLUMN_SUGGESTED_PATTERN)));
                    info.setTargeted_muscle(cursor.getString(cursor.getColumnIndex(COLUMN_TARGETED_MUSCLE)));
                    break;
                }
            } while (cursor.moveToNext());
        }

        db.close();

        return info;
    }


    // Get ExerciseNameList
    // Returns a list of ExerciseInfo objects
    public List<ExerciseInfo> getExerciseNameList() {
        List<ExerciseInfo> ExerciseInfoList = new ArrayList<ExerciseInfo>();

        String selectQuery = "SELECT * FROM " + TABLE_EXERCISE_LIST + " ORDER BY " + COLUMN_EXERCISE_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ExerciseInfo info = new ExerciseInfo();
                info.setExercise_id(cursor.getInt(cursor.getColumnIndex(COLUMN_EXERCISE_ID)));
                info.setDifficulty(cursor.getString(cursor.getColumnIndex(COLUMN_DIFFICULTY)));
                info.setExercise_name(cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISE_NAME)));
                info.setImage_ref(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_REF)));
                info.setSuggested_pattern(cursor.getString(cursor.getColumnIndex(COLUMN_SUGGESTED_PATTERN)));
                info.setTargeted_muscle(cursor.getString(cursor.getColumnIndex(COLUMN_TARGETED_MUSCLE)));
                ExerciseInfoList.add(info);
            } while (cursor.moveToNext());
        }
		db.close();

        return ExerciseInfoList;
    }

}