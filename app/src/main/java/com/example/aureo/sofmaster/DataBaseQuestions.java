package com.example.aureo.sofmaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;

/**
 * Aureo:
 * DataBaseQuestions - executes SQLite operations such as creating, inserting and droping tables
 */

public class DataBaseQuestions extends SQLiteOpenHelper {

    Context ctx;
    SQLiteDatabase db;
    public String table_name = "questions";

    public static String DATABASE_NAME = "sof_database";
    public static final String COLUMN_01 = "ID";
    public static final String COLUMN_02 = "TITLE";
    public static final String COLUMN_03 = "USER";
    public static final String COLUMN_04 = "IMAGE_URL";
    public static final String COLUMN_05 = "CLASSIFICATION";
    public static final String COLUMN_06 = "ID_STACK";
    public static final String COLUMN_07 = "BODY";
    public static final String COLUMN_08 = "ANSWERS";

    public DataBaseQuestions(Context context, String tag) {
        super(context, DATABASE_NAME+"_"+tag, null, 1);
        this.ctx = context;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE questions (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " TITLE TEXT, " +
                    " USER TEXT, " +
                    " IMAGE_URL TEXT, " +
                    " CLASSIFICATION TEXT, " +
                    " ID_STACK TEXT, " +
                    " BODY TEXT, " +
                    " ANSWERS TEXT " +
                    ")");

        }catch (SQLException exp){
            openAlertDialog("",exp.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void deletePreviosTable(){
        try {
        db.execSQL("DROP TABLE IF EXISTS questions");
        db.execSQL("CREATE TABLE questions (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " TITLE TEXT, " +
                " USER TEXT, " +
                " IMAGE_URL TEXT, " +
                " CLASSIFICATION TEXT, " +
                " ID_STACK TEXT, " +
                " BODY TEXT, " +
                " ANSWERS TEXT " +
                ")");
        }catch (SQLException exp){
            openAlertDialog("",exp.getMessage());
        }
    }

    public boolean insertQuestionLine (
                                        String title,
                                        String user,
                                        String image,
                                        String classification,
                                        String id_stack,
                                        String body,
                                        String answers
                                       ){

        ContentValues contentValues =  new ContentValues();
        contentValues.put(COLUMN_02, title);
        contentValues.put(COLUMN_03, user);
        contentValues.put(COLUMN_04, image);
        contentValues.put(COLUMN_05, classification);
        contentValues.put(COLUMN_06, id_stack);
        contentValues.put(COLUMN_07, body);
        contentValues.put(COLUMN_08, answers);
        Long result=new Long("-1");
        try {
            result = db.insertOrThrow(table_name, null, contentValues);
        }catch (Exception exp){
            //openAlertDialog("",exp.getMessage());
        }

        if (result<0){
            return false;
        }else {
            return true;
        }


    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM questions",null);

        return res;
    }

    public void openAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }
}
