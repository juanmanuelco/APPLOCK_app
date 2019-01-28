package apps.savvisingh.applocker.NEGOCIO;

public class Servidor {
    public static String servicio(String subred){
        return "http://192.168.1.7:3000"+subred;
    }
}
