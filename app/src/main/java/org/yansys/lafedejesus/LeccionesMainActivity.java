package org.yansys.lafedejesus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class LeccionesMainActivity extends AppCompatActivity {

    ListView lista;
    final public static String myKey = "noLeccion";
    public static String URL_PAYPAL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=4AHD2SP9ABHXY";
    public static String URL_PAYPAL2 = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=yansyr%40gmail%2ecom&lc=US&item_name=YanSys%20%2d%20La%20Fe%20de%20Jesus&amount=|%2e00&currency_code=USD&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest";
    String valoraDonar = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecciones_main);


        ListView lista = (ListView) findViewById(R.id.lecciones_list_view);  //getListView();


        lista.setAdapter(new ArrayAdapter<String>(this, R.layout.lista_item,
               getResources().getStringArray(R.array.lista)));

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView textView = (TextView) view;

                String noLeccion = textView.getText().toString().substring(0, 2);

                Log.i("este", noLeccion);


                if (noLeccion.equals("AA")) {
                    Log.i("este AA", noLeccion);
                    Toast.makeText(getApplicationContext(),
                            "Las Doctrinas Fundamentales de la Fe de Jesús", Toast.LENGTH_LONG).show();
                    return;
                } else if (noLeccion.equals("BB")) {
                    Toast.makeText(getApplicationContext(),
                            "Estudios Profundos de la Biblia que Revelan el Futuro", Toast.LENGTH_LONG).show();
                    return;
                } else if (noLeccion.equals("CC")) {
                    Toast.makeText(getApplicationContext(),
                            "Orientación Bíblica para resolver los principales problemas que encara la juventud cristiana.", Toast.LENGTH_LONG).show();
                    return;
                } else if (noLeccion.equals("DD")) {
                    Toast.makeText(getApplicationContext(),
                            "La Palabra de Dios responde a preguntas frecuentes sobre temas importantes", Toast.LENGTH_LONG).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString(myKey, noLeccion);

               Intent llamaWebView = new Intent(getApplicationContext(), LaFeDeJesusActivity.class);
               llamaWebView.putExtras(bundle);

               startActivityForResult(llamaWebView, 0);





            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_la_fe_de_jesus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acercaDe:
                new AlertDialog.Builder(this)
                        .setTitle("Acerca de La Fe de Jesus")
                        .setMessage("La Fe de Jesus para Android \nVersion 1.0.0.2 (2017)\n\nDesarrollado por: \nYansy R. Gonzalez D. \nyansyr@gmail.com \n\nAutor Original:\nPr. Carlos E. Aeschlimann H. \n\nPor favor, envienos sus comentarios, solicite caracteristicas, o reporte errores a: \nyansysoporte@gmail.com")
                        .setCancelable(true)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return true;
            case R.id.donar:
                final Spinner donarSpinner = (Spinner) findViewById(R.layout.donarlista);

                final String[] datos =
                        new String[]{"$1", "$2", "$5", "$10", "$20", "$25"};

                ArrayAdapter<String> adaptador =
                        new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_dropdown_item, datos);
                donarSpinner.setAdapter(adaptador);

                donarSpinner.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent,
                                                       View v, int position, long id) {
                                valoraDonar = datos[position];
                            }

                            public void onNothingSelected(AdapterView<?> parent) {
                                valoraDonar = "5";
                            }
                        }
                );

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(URL_PAYPAL2.replaceFirst("|", valoraDonar)));
                startActivity(intent);
                return true;
            case R.id.ayuda:
                return true;
            case R.id.contato:
                Intent intent2 = new Intent();
                intent2.setClassName("org.yansys.android", "org.yansys.android.EnviarEmail");

                startActivity(intent2);
                return true;
            case R.id.salir:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}