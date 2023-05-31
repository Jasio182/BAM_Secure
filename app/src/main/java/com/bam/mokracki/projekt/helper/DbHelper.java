package com.bam.mokracki.projekt.helper;

import static com.bam.mokracki.projekt.helper.Cryptography.ToBase64;
import static com.bam.mokracki.projekt.helper.Cryptography.decryptFile;
import static com.bam.mokracki.projekt.helper.Cryptography.encryptFile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import com.bam.mokracki.projekt.entity.SecretEntity;

import net.zetetic.database.sqlcipher.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;

public class DbHelper {

    private static final String NAME = "database.db";
    private static final String TABLENAME = "creditCardNumbers";
    public static final String ID = "id";
    public static final String CREDITCARDNUMBER = "creditCardNumber";

    private static final String CREATE = "create table " + TABLENAME + " (" +
            ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + CREDITCARDNUMBER + " TEXT NOT NULL);";

    private static final String QUERY = "select * from " + TABLENAME;
    File databaseFile;
    File backupDbFolder;
    File backupDb;


    public DbHelper(Context context) {
        System.loadLibrary("sqlcipher");
        databaseFile = context.getDatabasePath(NAME);
        String path = Environment.getExternalStorageDirectory()+"/Documents";
        backupDbFolder = new File(path);
        backupDb = new File(path+"/dbBackup.db");
    }

    public boolean CheckRegister() {
        return databaseFile.exists();
    }

    public void Register(String login, String password) {
        databaseFile.delete();
        String credentials = ToBase64(login, password);
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, credentials, null, null, null);
        database.execSQL(CREATE);
    }

    public boolean Login(String credentials) {
        try (SQLiteDatabase database = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), credentials, null, SQLiteDatabase.OPEN_READONLY, null, null)) {
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    public void AddEntry(String credentials, SecretEntity secretEntity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.CREDITCARDNUMBER, secretEntity.getCreditCardNumber());
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), credentials, null, SQLiteDatabase.OPEN_READWRITE, null, null);
        database.insert(this.TABLENAME, null, contentValues);
    }

    public ArrayList<SecretEntity> GetEntries(String credentials) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), credentials, null, SQLiteDatabase.OPEN_READONLY, null, null);
        ArrayList<SecretEntity> entities = new ArrayList<>();
        Cursor cursor = database.rawQuery(QUERY, null);
        if(cursor.moveToFirst())
        {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String value = cursor.getString(1);
                entities.add(new SecretEntity(id, value));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entities;
    }

    public Cursor GetCursor(String credentials) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), credentials, null, SQLiteDatabase.OPEN_READONLY, null, null);
        ArrayList<SecretEntity> entities = new ArrayList<>();
        return database.rawQuery(QUERY, null);
    }

    public void UpdateEntry(String credentials, SecretEntity secretEntity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.CREDITCARDNUMBER, secretEntity.getCreditCardNumber());
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), credentials, null, SQLiteDatabase.OPEN_READWRITE, null, null);
        database.update(this.TABLENAME, contentValues, ID + " = ?", new String[] {
                String.valueOf(secretEntity.getId())
        });
    }

    public void DeleteEntry(String credentials, int id) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), credentials, null, SQLiteDatabase.OPEN_READWRITE, null, null);
        database.delete(this.TABLENAME, ID + " = ?", new String[] {
                String.valueOf(id)
        });
    }

    public void Backup(String password) throws Exception {
        if(!backupDbFolder.exists())
            backupDbFolder.mkdirs();
        encryptFile(databaseFile, password, backupDb);
    }

    public void Restore(String password) throws Exception {
        decryptFile(databaseFile, password, backupDb);
    }
}
