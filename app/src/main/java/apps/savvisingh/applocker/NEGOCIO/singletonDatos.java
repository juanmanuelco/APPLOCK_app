package apps.savvisingh.applocker.NEGOCIO;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by juanm on 19/01/2018.
 */

public class singletonDatos {

    /*
    permite restringir la creación de objetos pertenecientes a una clase o el valor de un tipo a un único objeto.
    Su intención consiste en garantizar que una clase sólo tenga una instancia y proporcionar un punto de acceso global a ella.
    se implementa creando en nuestra clase un método que crea una instancia del objeto sólo si todavía no existe alguna.
    Para asegurar que la clase no puede ser instanciada nuevamente se regula el alcance del constructor
    (con modificadores de acceso como protegido o privado).

La instrumentación del patrón puede ser delicada en programas con múltiples hilos de ejecución.
Si dos hilos de ejecución intentan crear la instancia al mismo tiempo y esta no existe todavía,
sólo uno de ellos debe lograr crear el objeto. La solución clásica para este problema es
utilizar exclusión mutua en el método de creación de la clase que implementa el patrón.
     */

    private static singletonDatos instanciaSingleton;
    private RequestQueue requestDatos;
    private static Context mCtx;

    private  singletonDatos (Context context){
        mCtx=context;
        requestDatos=getRequestDatos();
    }

    public static synchronized singletonDatos getInstancia(Context context){
        if(instanciaSingleton==null){
            instanciaSingleton=new singletonDatos(context);
        }
        return instanciaSingleton;
    }

    public RequestQueue getRequestDatos(){
        if(requestDatos==null){
            requestDatos= Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestDatos;
    }
    public <T>void addToRequest(Request<T> request){
        requestDatos.add(request);
    }
}
