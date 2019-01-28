package apps.savvisingh.applocker.Data;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppInfo {

    /**Atributos del objeto AppInfo*/
    private String name;
    private String packageName;
    private String versionName;
    private int versionCode = 0;
    private Drawable icon;

    public AppInfo() {
    }
    public AppInfo(String nombre, String paquete, String version, int code){
        this.name=nombre;
        this.packageName = paquete;
        this.versionName =version;
        this.versionCode=code;
        this.icon=null;
    }

    /**Métodos del objeto AppInfo*/
    public String getNombre() {return name; }
    public void setNombre(String name) {
        this.name = name;
    }
    public String getNombrePaquete() {
        return packageName;
    }
    public void setNombrePaquete(String packageName) {
        this.packageName = packageName;
    }
    public String getNombreVersion(){return this.versionName;}
    public void setNombreVersion(String versionName) {
        this.versionName = versionName;
    }
    public int getVersionCode(){return this.versionCode;}
    public void setCodigoVersion(int versionCode) { this.versionCode = versionCode;   }
    public Drawable getIcono() {
        return icon;
    }
    public void setIcono(Drawable icon) {
        this.icon = icon;
    }
}

