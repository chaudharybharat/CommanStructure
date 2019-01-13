/*
*   Copyright 2016 Marco Gomiero
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*
*/

package com.example.agc_linux.accounting.TestDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.agc_linux.accounting.db.MyDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    //Database Version
    private static final int DATABASE_VERSION = 1;

    //Database Name
    public static final String DATABASE_NAME = "Account";

    //Data Type
    private static final String TEXT = " TEXT ";
    private static final String INTEGER = " INTEGER ";

    //Tables Name
    private static final String TABLE_CUSTOMER = "Customer";

    //STUDENTS Table - column name
    private static final String STUD_ID = "id";
    private static final String STUD_NAME = "name";
    private static final String STUD_SURNAME = "surname";
    private static final String STUD_BORN = "bornDate";

    //EXAMS Table - column name
    private static final String EX_ID = "id";
    private static final String EX_STUDENT = "student";
    private static final String EX_VAL = "evaluation";

    //Table Create Statement

    //Students table
    private static final String CREATE_TABLE_STUDENTS = "CREATE TABLE " + TABLE_CUSTOMER + " ( " +
            STUD_ID + INTEGER + "," +
            STUD_NAME + TEXT + "," +
            STUD_SURNAME + TEXT + "," +
            STUD_BORN + INTEGER + "," +
            "PRIMARY KEY (" + STUD_ID + ")" +
            ")";


    //Table Delete Statement

    //Students Table
    private static final String DELETE_STUDENTS = "DROP TABLE IF EXISTS " + TABLE_CUSTOMER;

    //context
    private Context mContext;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create tables
        db.execSQL(CREATE_TABLE_STUDENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //reinitialize db
        db.execSQL(DELETE_STUDENTS);
        onCreate(db);
    }

    public void deleteAll(SQLiteDatabase db) {

        db.execSQL(DELETE_STUDENTS);

    }

    //create a new student
    public long addStudent(Student stud) {

        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(STUD_ID, stud.getId());
        values.put(STUD_NAME, stud.getName());
        values.put(STUD_SURNAME, stud.getSurname());
        values.put(STUD_BORN, stud.getBorn());

        // Create a new map of values, where column names are the keys
        return db.insert(TABLE_CUSTOMER, null, values);
    }



    //get all students
    public ArrayList<Student> getAllStudent() {

        ArrayList<Student> students = new ArrayList<>();
        //select all query
        String query = "SELECT * FROM " + TABLE_CUSTOMER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {

                Student student = new Student();
                student.setId(c.getInt(c.getColumnIndex(STUD_ID)));
                student.setName(c.getString(c.getColumnIndex(STUD_NAME)));
                student.setSurname(c.getString(c.getColumnIndex(STUD_SURNAME)));
                student.setBorn(c.getLong(c.getColumnIndex(STUD_BORN)));

                students.add(student);
            }
        }
        c.close();
        return students;
    }


    //get all stud id
    public ArrayList<String> getStudId() {

        ArrayList<String> idList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + STUD_ID + " FROM " + TABLE_CUSTOMER;

        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex(STUD_ID));
                idList.add(String.valueOf(id));
            }
        }
        c.close();
        return idList;
    }

    //delete a student
    public void deleteStud(int id) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CUSTOMER, STUD_ID + " = ? ", new String[]{String.valueOf(id)});
    }

    //delete an exam
    public void deleteExam(int id) {

        SQLiteDatabase db = getWritableDatabase();
    }

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    //close database
    public void closeDB() {

        SQLiteDatabase db = this.getReadableDatabase();

        if (db != null && db.isOpen())
            db.close();
    }

    public void backup(String outFileName) {

        //database path
        final String inFileName = mContext.getDatabasePath(MyDatabase.NAME+".db").toString();

        // final String inFileName = mContext.getDatabasePath(DATABASE_NAME).toString();


        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(mContext, "Backup Completed", Toast.LENGTH_SHORT).show();



        } catch (Exception e) {
            Toast.makeText(mContext, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public void importDB(String inFileName) {

       // final String outFileName = mContext.getDatabasePath(DATABASE_NAME).toString();
        final String outFileName = mContext.getDatabasePath(MyDatabase.NAME+".db").toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(mContext, "Import Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(mContext, "Unable to import database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
