package biblioteca.simple.servicios;

import biblioteca.simple.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class PersistenciaUsuarios {

    private static final String ARCHIVO = "usuarios.json";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private PersistenciaUsuarios () {}

    public static void exportar (List<Usuario> usuarios) throws IOException{
        try(Writer w = new OutputStreamWriter(new FileOutputStream(ARCHIVO), StandardCharsets.UTF_8 )){

            gson.toJson(usuarios,w);
        }
    }

    public static List<Usuario> importar() throws IOException{

        File f= new File(ARCHIVO);
        if(!f.exists()) return new ArrayList<>();

        Type tipoLista = new TypeToken<ArrayList<Usuario>>() {}.getType();

        try (Reader r =new InputStreamReader(new FileInputStream(f),StandardCharsets.UTF_8)) {
            List<Usuario> lista = gson.fromJson(r,tipoLista);

            return (lista != null) ? lista:new ArrayList<>();
        }


    }

}
