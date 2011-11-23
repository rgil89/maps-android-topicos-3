package com.trabalho3.topicos2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	 public static final String KEY_ROWID = "_id";
	 public static final String KEY_LONGITUDE = "longi";
	 public static final String KEY_LATITUDE = "lati";
	 public static final String KEY_NOME = "nome";    
	 private static final String TAG = "DBAdapter";
	    
	 private static final String DATABASE_NAME = "agenda";
	 private static final String DATABASE_TABLE = "entradas";
	 private static final int DATABASE_VERSION = 1;

	 private static final String CRIA_DATABASE = "create table entradas (_id integer primary key autoincrement, "
	        + "longi text not null, lati text not null, nome text not null);";
	        
	    private final Context context;  

	    private DatabaseHelper DBHelper;
	    private SQLiteDatabase db;

	    public DBAdapter(Context ctx){
	        this.context = ctx;
	        DBHelper = new DatabaseHelper(context);
	    }
	        
	    //classe interna que manipula o banco
	    private static class DatabaseHelper extends SQLiteOpenHelper{
	        DatabaseHelper(Context context){
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	        }

	        @Override
	        public void onCreate(SQLiteDatabase db){
	            db.execSQL(CRIA_DATABASE);
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion){
	            Log.w(TAG, "Atualizando a base de dados a partir da versao " + oldVersion 
	                  + " para "
	                  + newVersion + ",isso irá destruir todos os dados antigos");
	            db.execSQL("DROP TABLE IF EXISTS entradas");
	            onCreate(db);
	        }
	        
	    }    
	    
// *******************************************************************************
	    //--- abre a base de dados ---
	    public DBAdapter open() throws SQLException{
	        db = DBHelper.getWritableDatabase();
	        return this;
	    }

	    //--- fecha a base de dados ---    
	    public void close(){
	        DBHelper.close();
	    }
	    
	    //---insere uma entrada na base da dados ---
	    public long insereEntrada(String longi, String lati, String nome){
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_LONGITUDE, longi);
	        initialValues.put(KEY_LATITUDE, lati);
	        initialValues.put(KEY_NOME, nome);
	        return db.insert(DATABASE_TABLE, null, initialValues);
	    }

	    //--- exclui uma entrada---
	    public boolean excluiEntrada(long rowId){
	        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	    }

	    //--- retorna todas as entradas---
	    public Cursor getTodasEntradas(){
	        return db.query(DATABASE_TABLE, 
	        				new String[] {KEY_ROWID,KEY_LONGITUDE,KEY_LATITUDE,KEY_NOME}, 
	                null, 
	                null, 
	                null, 
	                null, 
	                null);
	    }

	    //--- retorna uma linha (entrada) ---
	    public Cursor getEntrada(long rowId) throws SQLException{
	        Cursor mCursor = db.query(true, DATABASE_TABLE, 
	        					new String[] {KEY_ROWID,KEY_LONGITUDE,KEY_LATITUDE,KEY_NOME}, 
	        					KEY_ROWID + "=" + rowId, 
	                		null,
	                		null, 
	                		null, 
	                		null, 
	                		null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;
	    }

	    //--- atualiza uma entrada---
	    public boolean alteraEntrada(long rowId, String longi, String lati, String nome){
	        ContentValues args = new ContentValues();
	        args.put(KEY_LONGITUDE, longi);
	        args.put(KEY_LATITUDE, lati);
	        args.put(KEY_NOME, nome);
	        return db.update(DATABASE_TABLE, args, 
	                         KEY_ROWID + "=" + rowId, null) > 0;
	    }
}