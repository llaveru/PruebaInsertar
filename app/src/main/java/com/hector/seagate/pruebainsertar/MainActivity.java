package com.hector.seagate.pruebainsertar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import static android.R.drawable.stat_notify_missed_call;

public class MainActivity extends AppCompatActivity implements LocationListener{
    Random alea;
    TextView texto;
    EditText etlon,etlat;
    Button btnInsertar;
    TareaInsertar tarea;
    ImageView icoSat;
    private boolean estadoGPS;
    RelativeLayout layout;
    receptorOnOffGps receptor;
    LocationManager  gestorPosicion;
    @Override

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receptor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        comprobarGPS();
        receptor = new receptorOnOffGps();
        registerReceiver(receptor, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        //guardamos en el bundle el estado del gps

        bundle.putBoolean("ESTADOGPS",this.estadoGPS);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //segun el valor de la variable estadoGPS activamos o no
        boolean gpsactivo = savedInstanceState.getBoolean("ESTADOGPS");
        if(gpsactivo){
            texto.setText("gps disponible");
            icoSat.setImageDrawable(getResources().getDrawable(R.drawable.sat));
            icoSat.invalidate();
        }else {texto.setText("gps no disponible");
            icoSat.setImageDrawable(getResources().getDrawable(R.drawable.satdown));
            icoSat.invalidate();}
    }


    @Override
    protected void onRestart() {
        super.onRestart();
         gestorPosicion = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(gestorPosicion.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            texto.setText("gps disponible");estadoGPS=true;
            icoSat.setImageDrawable(getResources().getDrawable(R.drawable.sat));
        }else {
            estadoGPS =false;
            texto.setText("gps no disponible");
            icoSat.setImageDrawable(getResources().getDrawable(R.drawable.satdown));}

    }


    //al girar la pantalla se crea otra vez la activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout= (RelativeLayout) findViewById(R.id.layout);
        etlon= (EditText) findViewById(R.id.etlon);
        etlat= (EditText) findViewById(R.id.etlat);
        texto= (TextView) findViewById(R.id.texto);
        icoSat = (ImageView) findViewById(R.id.icoSat);
        btnInsertar= (Button) findViewById(R.id.boton);


        gestorPosicion = (LocationManager) getSystemService(LOCATION_SERVICE);
        ArrayList<String> listaProveedores = (ArrayList<String>) gestorPosicion.getAllProviders();
        comprobarGPS();
        if(gestorPosicion.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            texto.setText("gps disponible");estadoGPS=true;
            icoSat.setImageDrawable(getResources().getDrawable(R.drawable.sat));
            icoSat.invalidate();
        }else {
            estadoGPS =false;
            texto.setText("gps no disponible");
            icoSat.setImageDrawable(getResources().getDrawable(R.drawable.satdown));
            icoSat.invalidate();
        }



        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tarea = new TareaInsertar(MainActivity.this);
                alea = new Random();



                etlon.setText(String.valueOf((float) -((alea.nextFloat() / 4) + 5.2)));


                etlat.setText(String.valueOf((float) (alea.nextFloat() / 4) + 43));




                tarea.execute(etlon.getText().toString(), etlat.getText().toString());

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                etlat.setText("");
                etlon.setText("");

            }
        });
    }

    public void comprobarGPS() {

        ArrayList<String> listaProveedores = (ArrayList<String>) gestorPosicion.getAllProviders();

        if(!gestorPosicion.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            texto.setText("gps no disponible");estadoGPS=false;
            icoSat.setImageDrawable(getResources().getDrawable(R.drawable.satdown));
            icoSat.invalidate();
            Toast.makeText(getApplicationContext(),"el GPS esta desactivado \n debe" +
                    "activarlo para empezar  la geolocalización",Toast.LENGTH_LONG).show();
        }else {
            estadoGPS =true;
            texto.setText("gps  disponible");
            icoSat.setImageDrawable(getResources().getDrawable(R.drawable.sat));
            icoSat.invalidate();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    if ((provider.equals("gps")) && (status== LocationProvider.OUT_OF_SERVICE)){
        icoSat.animate().setDuration(400).rotation(120);
        icoSat.setImageDrawable(getResources().getDrawable(R.drawable.satdown));
        icoSat.invalidate();}
    else
    if ((provider.equals("gps")) && (status== LocationProvider.AVAILABLE)){
        icoSat.animate().setDuration(400).rotation(120);
        icoSat.setImageDrawable(getResources().getDrawable(R.drawable.sat));
        icoSat.invalidate();}

    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider.equals("gps")) {
            Toast.makeText(getApplicationContext(),"gps activado",Toast.LENGTH_LONG).show();
            estadoGPS=true;
            texto.setText("onProviderEnabled");
            icoSat.animate().setDuration(400).rotation(120);
            icoSat.setImageDrawable(getResources().getDrawable(R.drawable.sat));
            icoSat.invalidate();
        }
        }



    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals("gps")) {
            estadoGPS=false;
            texto.setText("onProviderDisabled");
            icoSat.animate().setDuration(300).rotation(50);
            icoSat.setImageDrawable(getResources().getDrawable(R.drawable.satdown));


            Toast.makeText(getApplicationContext(),"gps desaactivado",Toast.LENGTH_LONG).show();
            icoSat.invalidate();
            }

        }


}


    class TareaInsertar extends AsyncTask<String,Void,String> {
    TextView textoUI;
    Context contexto;
    Activity activity;
    String lon = null;
    String lat = null;

    //necesario crear este constructor, para al instanciar la clase, obtener el contexto de los edittext, que
    //estan en la actividad de la UI, por eso se pasa como parametro la activity

    public TareaInsertar(Activity actividadUI) {
        this.contexto = actividadUI.getApplicationContext();
        this.activity = actividadUI;
    }

    //en preexecute obtenemos los datos de los editText, por eso necesitamos el contexto
    @Override
    protected void onPreExecute() {

        EditText etlat = (EditText) this.activity.findViewById(R.id.etlat);
        EditText etlon = (EditText) this.activity.findViewById(R.id.etlon);


        super.onPreExecute();

    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        textoUI = (TextView) this.activity.findViewById(R.id.texto);
        textoUI.setText(s);
        //noinspection ResourceType
        //textoUI.setTextColor(android.R.color.holo_green_light);
    }

    @Override
    protected String doInBackground(String... params) {
        //aqui es donde se conecta
        String response = "";
        lat = params[0];
        lon = params[1];


        //HashMap<String,String> parametros = new HashMap<>();
        //parametros.put("a", lat);
        //parametros.put("b", lon);

        String data="";
        URL url = null;
        try {
            url = new URL("http://www.motosmieres.com/pruebacongb.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }


        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        try {
            //en el php del servidor, debe estar tambien en POST
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        conn.setDoOutput(true);
        OutputStream os = null;
        try {
            os = conn.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            data = URLEncoder.encode("a", "UTF-8") + "=" + URLEncoder.encode(lon, "UTF-8") + "&" + URLEncoder.encode("b", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        try {

            bw.write(data);
            bw.flush();

            bw.close();

            os.close();
            InputStream is  = conn.getInputStream();

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }




        return "registro correcto";


        }



    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private void insertarEnBD(HashMap<String, String> parametros) throws IOException {


    }


}