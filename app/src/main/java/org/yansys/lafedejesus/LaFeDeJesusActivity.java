package org.yansys.lafedejesus;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JsResult;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.Toast;


@SuppressLint("SetJavaScriptEnabled")
public class LaFeDeJesusActivity extends AppCompatActivity {

    private static String VERSION = "(RV60)";
    private WebView webView;
    private CitaHelper citaHelper;
    private Button aceptoButton;
    private Button saberMasButton;

    String parametro;
    /** Called when the activity is first created. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_la_fe_de_jesus);

        setContentView(R.layout.main);

        aceptoButton = (Button)findViewById(R.id.aceptoButton);
        aceptoButton.setOnClickListener(onAcepto);

        saberMasButton = (Button)findViewById(R.id.saberMasButton);
        saberMasButton.setOnClickListener(onSaberMas);

        citaHelper = new CitaHelper(this);

        parametro = "01";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            parametro = extras.getString(ListaActivity.myKey);
        }

        webView = (WebView)findViewById(R.id.webView);
        //webView.addJavascriptInterface(this, "jsNativeInterface");
        webView.addJavascriptInterface(this, "jsNativeInterface");
        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(false);
        //webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/lafedjlec" + parametro +".htm");

        //Asignamos a la vista web el cliente (navegador)
        //que hemos creado como clase privada (ver mas abajo
        //y que extiende del que trae Android por defecto.
        //Esta clase maneja el navegador:
        webView.setWebViewClient(new MiWebViewClient());

        //Asignamos a la vista web la clase MiWebViewClient
        //que hemos creado como clase privada (ver mï¿½s abajo)
        //y que extiende del que trae Android por defecto.
        //Esta clase permite controlar los eventos que se producen
        //en el navegador:
        webView.setWebChromeClient(new MiWebCromeClient());
    }

    //Extendemos el navegador por defecto
    private class MiWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    //Con esta clase controlamos algunos eventos javascript del navegador
    final class MiWebCromeClient extends WebChromeClient
    {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result)
        {

            new AlertDialog.Builder(view.getContext())
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton("Acepto", new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
            result.confirm();
            return true;
        }
    }


    @JavascriptInterface
    public void verCita(String cita) {
        new AlertDialog.Builder(webView.getContext())
                .setMessage(getScriptureByVerse(cita))
                .setCancelable(true)
                .setTitle(cita +" "+ VERSION)
                .setPositiveButton("Acepto", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public String getScriptureByVerse(String cita) {

        crearBBDD();
        citaHelper.abrirBaseDatos();
        String c = citaHelper.GetVerseByCita(cita);
        citaHelper.close();
        return c;
    }

    public void crearBBDD() {
        citaHelper = new CitaHelper(this);
        try {
            citaHelper.crearDataBase();
        } catch (IOException ioe) {
            throw new Error("No se pudo crear la base de datos");
        }
    }

    private View.OnClickListener onAcepto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),
                    "¡Muchas Felicidades!", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private View.OnClickListener onSaberMas = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer numero = Integer.valueOf(parametro);
            if (numero > 20 || numero == 0) {
                Toast.makeText(getApplicationContext(),"Opción no disponible para esta Lección", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = webView.getUrl();
            //int indexPunto = webView.getUrl().indexOf(".");
            if (url.endsWith("a.htm")) {
                webView.loadUrl("file:///android_asset/lafedjlec" + parametro +".htm");
                saberMasButton.setText("Quiero Saber Más...");
            } else {
                webView.loadUrl("file:///android_asset/lafedjlec" + parametro +"a.htm");
                saberMasButton.setText("Volver al Estudio");
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_la_fe_de_jesus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.acercaDe:
                new AlertDialog.Builder(this)
                        .setTitle("Acerca de La Fe de Jesús")
                        .setMessage("La Fe de Jesús para Android \nVersion 1.0.0.0 (2012)\n\nDesarrollado por: \nYansy R. González D. \nyansyr@gmail.com \n\nAutor Original:\nPr. Carlos E. Aeschlimann H. \n\nPor favor, envienos sus comentarios, solicite características, o reporte errores a: \nyansyr@gmail.com")
                        .setCancelable(true)
                        .setPositiveButton("Aceptar", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return true;
            case R.id.donar:
                return true;
            case R.id.ayuda:
                return true;
            case R.id.contato:
                return true;
            case R.id.salir:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}


