package apps.savvisingh.applocker.NEGOCIO;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by juanm on 17/01/2018.
 */

public class recorrido extends  recorre {
    private EditText[] camp;
    public recorrido(EditText[] campos)
    {
        super(campos);
        this.camp=campos;
    }
    public final Boolean Recorrer(EditText[] campos)
    {
        int visor=0;
        ArrayList list = new ArrayList();
        for (int i = 0; i < campos.length; i++){list.add(campos[i].getText().toString().trim());}
        Iterator e = list.iterator();

        while (e.hasNext()){
            Object obj = e.next();
            if (!obj.toString().equals("")) {visor+=1;}
        }
        if(visor==campos.length){return true;}else{return false;}
    }
}
