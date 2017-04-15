package org.yansys.lafedejesus;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.ContextWrapper;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CitaHelper extends SQLiteOpenHelper {

	//La carpeta por defecto donde Android espera encontrar la Base de Datos de tu aplicacion
	private static String DB_PATH = "/data/data/org.yansys.lafedejesus/databases/";
	private static String DB_NAME = "RV60.sqlite"; //.mp3 para que no comprima el archivo y se pueda copiar, por el archivo es  mayor de 1.5MB
	//private static String VERSION = "(RV60)"; //nombre de la version de la biblia
	private SQLiteDatabase myDataBase;

	private final Context myContext;

	/*
     * Constructor
     *
     * Guarda una referencia al contexto para acceder a la carpeta assets de la aplicacion y a los recursos
     * @param contexto
     */
	public CitaHelper(Context contexto) {

		super(contexto, DB_NAME, null, 1);
		this.myContext = contexto;
	}


	/*
     * Crea una base de datos vacia en el sistema y la sobreescribe con la que hemos puesto en Assets
     */
	public void crearDataBase() throws IOException{

		boolean dbExist = comprobarBaseDatos();

		if(dbExist){
			//Si ya existe no hacemos nada
		}else{
			//Si no existe, creamos una nueva Base de datos en la carpeta por defecto de nuestra aplicacion,
			//de esta forma el Sistema nos permitir sobreescribirla con la que tenemos en la carpeta Assets
			this.getReadableDatabase();
			try {
				copiarBaseDatos();
			} catch (IOException e) {
				throw new Error("Error al copiar la Base de Datos");
			}
		}
	}

	/*
     * Comprobamos si la base de datos existe
     * @return true si existe, false en otro caso
     *
     */
	private boolean comprobarBaseDatos(){
		SQLiteDatabase checkDB = null;
		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){
			//No existe
		}

		if(checkDB != null){
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/*
     * Copia la base de datos desde la carpeta Assets sobre la base de datos vac�a reci�n creada en la carpeta del sistema,
     * desde donde es accesible
     */
	private void copiarBaseDatos() throws IOException{

		//Abrimos la BBDD de la carpeta Assets como un InputStream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		//Carpeta de destino (donde hemos creado la BBDD vacia)
		String outFileName = DB_PATH + DB_NAME;

		//Abrimos la BBDD vacia como OutputStream
		OutputStream myOutput = new FileOutputStream(outFileName);

		//Transfiere los Bytes entre el Stream de entrada y el de Salida
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Cerramos los ficheros abiertos
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/*
     * Abre la base de datos
     */
	public void abrirBaseDatos() throws SQLException{
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

	}

	/*
     * Cierra la base de datos
     */
	@Override
	public synchronized void close() {
		if(myDataBase != null)
			myDataBase.close();

		super.close();
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		//No usamos este metodo
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//No usamos este metodo
	}

	//Podemos a�adir m�todos p�blicos que accedan al contenido de la base de datos,
	//para realizar consultas, u operaciones CRUD (create, read, update, delete)



	/*
     * Obtiene todos los libros desde la Base de Datos
     */
	public String GetCita(int book, int chapter, int verse) {
		String[] args={String.valueOf(book),String.valueOf(chapter),String.valueOf(verse)};
		Cursor c = getReadableDatabase()
				.rawQuery("SELECT Book, Chapter, Verse, Scripture FROM Bible WHERE Book=? and Chapter=? and Verse=?", args);
		c.moveToFirst();
		return c.getString(3);
	}

	public Cursor getByVerseRange(int book, int chapter, int verse1, int verse2) {
		String[] args={String.valueOf(book), String.valueOf(chapter), String.valueOf(verse1)};

		return(getReadableDatabase()
				.rawQuery("SELECT Book, Chapter, Verse, Scripture FROM Bible WHERE Book=? and Chapter=? and Verse1>=? and <Verse2<=?", args));
	}

	public String GetVerseByCita(String cita)
	{

		StringBuilder sb = new StringBuilder();

		Cursor c = getReadableDatabase()
				.rawQuery("SELECT Book, Chapter, Verse, Scripture FROM Bible WHERE " + getWhere(cita), null);

		if (c.getCount() == 0)
			return "¡Vaya! Pasaje no encontrado";

		c.moveToFirst();

		String capitulo = "";
		String versiculo = "";
		String escritura = "";


		while (!c.isAfterLast()) {
			capitulo = String.valueOf(getChapter(c));
			versiculo = String.valueOf(getVerse(c));
			escritura = String.valueOf(getScripture(c));
			if (c.getCount() > 1)
				sb.append("(" + capitulo + ":" + versiculo + ") " + escritura + "\n");
			else
				sb.append(escritura);
			c.moveToNext();
		}
		return sb.toString();
	}

	public int getNumeroLibro(String libro)
	{
		android.content.ContextWrapper w = new ContextWrapper(this.myContext);
		String[] books = w.getResources().getStringArray(R.array.libros);

		Log.i("este libro", books[0]);
		ArrayList<String> libros = new ArrayList<String>();
		for (int i=0;i<books.length;i++)
		{
			libros.add(books[i]);
		}
		if (libro.trim().equals("Salmo"))
			libro = "Salmos";
		int numero = libros.indexOf(libro) + 1;
		Log.i("este Numero", String.valueOf(numero) );

		return numero;
	}


	public String getWhere(String cita)
	{


		Log.i("este", "inicio");
		StringBuilder sb = new StringBuilder();


		//if (2==1+1) {
		String s = cita;
		StringBuilder strb = new StringBuilder();

		s = s.replace("S.", ""); // para eliminar "S."
		s = s.trim();
		s = s.replaceAll("\\s+", " "); //para dejar un solo espacio

		Log.i("este", s);

		Pattern p = Pattern.compile("(\\d{1}\\s*)?(S.\\s*)?([a-zA-ZZáéíóúÁÉÍÓÚ]+)");

		Matcher m = p.matcher(s);
		m.find();
		String libro = m.group();

		Log.i("este ->", libro);

		s = s.replace(libro, String.valueOf(getNumeroLibro(libro.trim())));
		s = s.trim();
		Log.i("este", s);

		int cont=0;
		for (int i=0;i<s.length();i++) //elimina los espacios en blanco
		{
			if (s.charAt(i) !=' ')
			{
				strb.append(s.charAt(i));
			}
			else
			{
				cont+=1;
				if (cont==1) //para que no elimine el primer espacio
					strb.append(s.charAt(i));
			}
		}


		s = strb.toString();
		String l = "";
		String c = "";
		String v = "";


		ArrayList<String> lista2 = new ArrayList<String>();

		String[] citas = s.split("\\;");

		for (int i = 0; i<citas.length; i++)
		{
			if (i==0)
			{
				l = citas[i].split("\\s")[0];
				c = citas[i].split("\\s")[1];

			}
			else
			{
				c = citas[i].trim();
			}
			String[] cv = c.split("\\:"); //cap�tulo y vers�culo
			c = cv[0];
			v = cv[1];

			String vv = v.replaceAll("(\\d{1,3}-\\d{1,3})", "|");

			String vv1[] = vv.split("\\|");
			for (int t=0; t<vv1.length; t++)
			{
				if (vv1[t].startsWith(","))
				{
					vv1[t]=vv1[t].replaceFirst(",", "");
				}
				if (vv1[t].endsWith(","))
				{
					vv1[t]=vv1[t].substring(0, vv1[t].length()-1);
				}

				if (vv1[t].trim().length() != 0)
					lista2.add("(Book=" + l +  " And Chapter=" + c + " And Verse IN ("+vv1[t]+"))");

			}

			String vv2[] = v.split("\\,");
			for (int t=0; t<vv2.length; t++)
			{
				if (vv2[t].contains("-"))
				{
					String[] rango = vv2[t].split("\\-");
					lista2.add("(Book=" + l +  " And Chapter=" + c + " AND (Verse>=" + rango[0] + " AND Verse<=" + rango[1] + ")) ");
				}
			}
		}

		for (int x = 0; x < lista2.size(); x++)
		{
			if (x==0)
				sb.append(lista2.get(x));
			else
			{
				sb.append(" OR ");
				sb.append(lista2.get(x));
			}

		}
		//}
		//catch (Exception ex)
		//{
		//	Log.e("aqui", "aqui", ex);
		//}


		return sb.toString();
	} //

	public String getBook(Cursor c) {
		return(c.getString(0));
	}
	public String getChapter(Cursor c) {
		return(c.getString(1));
	}
	public String getVerse(Cursor c) {
		return(c.getString(2));
	}
	public String getScripture(Cursor c) {
		return(c.getString(3));
	}
}
