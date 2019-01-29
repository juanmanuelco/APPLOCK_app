package apps.savvisingh.applocker.NEGOCIO;

public class Servidor {
    public static String servicio(String subred){
        return "http://192.168.1.6:3000"+subred;
    }
}
