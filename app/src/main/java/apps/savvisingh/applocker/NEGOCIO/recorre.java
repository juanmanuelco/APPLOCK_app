package apps.savvisingh.applocker.NEGOCIO;

import android.widget.EditText;

/**
 * Created by juanm on 17/01/2018.
 */

public class recorre {
    private EditText[] campos;
    public recorre(EditText[] camp) {
        campos=camp;
    }
    public final EditText[] getCampos(){
        return campos;
    }
    public final void setCampos(EditText[] datos) {
        campos=datos;
    }

}
