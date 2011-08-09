package com.shortList.app.db;

import static com.shortList.app.db.Constants.*;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.shortList.app.model.Event;

public class DBAdapter extends SQLiteOpenHelper {

	private static final String LOG_TAG = "DBAdapter";

	private SQLiteDatabase db;

	public DBAdapter(Context ctx) { 
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION); 
		db = getWritableDatabase();
		db.execSQL(CREATE_TABLE_EVENT);
		db.execSQL(CREATE_TABLE_PERSON);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "onCreate");
		db.execSQL(CREATE_TABLE_EVENT);
		db.execSQL(CREATE_TABLE_PERSON);
	}
	
	protected void clear() {
		db.execSQL("drop table " + DATABASE_TABLE_DEBTOR); 
		db.execSQL("drop table " + DATABASE_TABLE_EVENT); 
		db.execSQL("drop table " + DATABASE_TABLE_EVENT_PAYMENTS);
		db.execSQL("drop table " + DATABASE_TABLE_PARTICIPANTS);
		db.execSQL("drop table " + DATABASE_TABLE_PAYMENT);
		db.execSQL("drop table " + DATABASE_TABLE_PERSON);
	}
		
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_DEBTOR);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_EVENT);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_EVENT_PAYMENTS);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_PARTICIPANTS);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_PAYMENT);
		db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_PERSON);
		onCreate(db);
	}


	public Cursor getParticipants(Event event) {
		// TODO Auto-generated method stub
		return null;
	}

}
