package es.adolfo.openweather;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import es.adolfo.openweather.model.City;

public class DataBaseModel extends SQLiteOpenHelper{
	
	private static DataBaseModel mInstance = null;
	
	private static String DB_PATH = "/data/data/es.adolfo.openweather/databases/";
	private static String DB_NAME = "open_weather_new.sqlite";
    private static String DB_NAME_OLD = "database.sqlite";
	private static int DATABASE_VERSION = 1;
	
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	
	 public static DataBaseModel getInstance(Context ctx) {

		if (mInstance == null) {
		      mInstance = new DataBaseModel(ctx.getApplicationContext());
		}
		return mInstance;
	}
	 
	public DataBaseModel(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
		this.myContext = context;
	}
	
	
	
	public void open() throws SQLException{
		 
		//Abre la base de datos
		try {
			createDataBase();
		} catch (IOException e) {
		throw new Error("Ha sido imposible crear la Base de Datos");
		}
		 
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		 
	}
	
	/**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		this.getWritableDatabase();
    	}else{
    		
        	boolean dbExistOld = checkDataBaseOld();
			
        	this.getReadableDatabase().close();

        	try {
        	   if(dbExistOld){
        		   this.myContext.deleteDatabase(DB_NAME_OLD);
        	   }
        	   copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
	
	/**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }

	private boolean checkDataBaseOld(){

		SQLiteDatabase checkDB = null;

		try{
			String myPath = DB_PATH + DB_NAME_OLD;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);

		}catch(SQLiteException e){

			//database does't exist yet.

		}

		if(checkDB != null){

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
    
    private void copyDataBase(String fichero) throws IOException{
   	 
    	 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
    	//InputStream is = new FileInputStream(fichero);
    	InputStream is = myContext.openFileInput(fichero);
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = is.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	is.close();
    	myOutput.close();
    }
    
    private void copyDataBase(InputStream is) throws IOException{
    	 
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = is.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);
 
    }
	

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public List<City> getCityByProv(String prov) {


		List<City> resultList = new ArrayList<City>();
		SQLiteDatabase db = getReadableDatabase();
		String[] columns = {"id","nm","lat","lon"};
		String where = "prov = ?";
		String[] values = {prov};
		String orderBy = "nm ASC";
		Cursor c =
				db.query("CITY",
						columns,
						where,
						values,
						null,
						null,
						orderBy);


		try {
			if (c.moveToFirst()) {
				do {
					City city = new City(c.getString(0), c.getString(1));
					city.setLat(c.getString(2));
					city.setLat(c.getString(3));
					resultList.add(city);
				} while(c.moveToNext());
			}
		} finally {
			c.close();
		}

		return resultList;
	}
}

