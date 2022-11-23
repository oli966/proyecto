package com.example.app_mqtt_conexion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Build;//para obtener el nombre del dispositivo
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    String nombre_Dispositivo;   //string para obtener el nombre del dispositivo
    String publicaste;                         //string para mostras el mensaje apublicar
    boolean permiso_publicar=false;          //para permitir o no hacer publicaciones
    boolean intento_publicar=false;           //para saber si intento publicar
    //private TextView tvNombreDispositivo;      //TexView para monitorear

    //parametros del broker la siguiente variable con el broker de shiftr.io
    static String MQTTHOST = "tcp://68.183.119.177"; //el broker
    //static String USERNAME = "accesobroker";          //el token de acceso
    //static String PASSWORD = "zxcvbnmz";             //la contraceña del token
    MqttAndroidClient client;              //  clienteMQTT este dispositivo
        MqttConnectOptions options;            // para meter parametros a la conexion
    /*
    textview se ocupara para mostrar datos de temperatura en el mqtt
     */
     private  TextView temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//obtenemos el nombre del Dispositivo
       //obtenemos el nombre del Dispositivo
       obtener_nombre_Dispositivo();
       //para conextar al broker   //generamos un clienteMQTT
       String clientId = nombre_Dispositivo;//MqttClient.generateClientId();//noombre del celular
       client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
       //para agregar los parametros
       options = new MqttConnectOptions();
       //options.setUserName(USERNAME);
       //options.setPassword(PASSWORD.toCharArray());
       checar_conexion();//revisamos la conexion
   //traer objeto
        temp= findViewById(R.id.ttemp);
    }
    private void obtener_nombre_Dispositivo() {
        String fabricante = Build.MANUFACTURER;
        String modelo = Build.MODEL;
        nombre_Dispositivo=fabricante+" "+modelo;
        //tvNombreDispositivo =(TextView) findViewById(R.id.tv_g);//para mostrar el modelo del celular
        //tvNombreDispositivo.setText(nombre_Dispositivo);//para mostrar en el tv_g e modelo del celular
    }
    public void conexionBroker() {

        //para conextar al broker   //generamos un clienteMQTT
        String clientId = nombre_Dispositivo; //MqttClient.generateClientId();//noombre del celular
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        //para agregar los parametros
        MqttConnectOptions options = new MqttConnectOptions();
        //options.setUserName(USERNAME);
        //options.setPassword(PASSWORD.toCharArray());
        try {
            IMqttToken token = client.connect(options);//intenta la conexion
            token.setActionCallback(new IMqttActionListener() {

                @Override//metodo de conectado con exito
                public void onSuccess(IMqttToken asyncActionToken) {
                    // mensaje de conectado
                    Toast.makeText(getBaseContext(), "Conectado ", Toast.LENGTH_SHORT).show();
                subscribe();
                }
                @Override//si falla la conexion
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // mensaje de que no se conecto
                    Toast.makeText(getBaseContext(), "NO Conectado ", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public  void checar_conexion(){
//si el cliente esta desconectado se conecta falso=no conectado
        if(!client.isConnected()) {
            permiso_publicar=false;// no tienes permiso para publiar
            conexionBroker();//intenta conectarce

        }else{permiso_publicar=true;}//si puedes publicar
    }
    public void subscribe(){
        try {
            client.subscribe("st/01",0);
            // listener que espera respuesta
         client.setCallback(new MqttCallback() {
             @Override
             public void connectionLost(Throwable cause) {
             }
             @Override
             public void messageArrived(String topic, MqttMessage message) throws Exception {
                 String oli;
                  if (topic.matches("st/01")) {
                      oli=new String(message.getPayload());
                      temp.setText(oli);
                 }
             }
             @Override
             public void deliveryComplete(IMqttDeliveryToken token) {
             }
         });
        }catch (MqttException e) {
            e.printStackTrace();
        }
    }

}