package biblioteca.simple.modelo;

import biblioteca.simple.contratos.Prestable;

public class Videojuego extends Producto implements Prestable {
    //atributos especifico de un videojuego
    private String plataforma;
    private String genero;

    // Control del estado de préstamo
    private boolean prestado;
    private Usuario prestadoA;

    // Constructor para modificar objetos que ya existen en la base de datos
    public Videojuego(int id, String titulo, String anho, Formato formato, String plataforma, String genero) {
        super(id, titulo, anho, formato);
        this.plataforma = plataforma;
        this.genero = genero;
    }

    // Constructor para crear libros nuevos
    public Videojuego(String titulo, String anho, Formato formato, String plataforma, String genero) {
        super(titulo, anho, formato);
        this.plataforma = plataforma;
        this.genero = genero;
    }

    // Getters para obtener información específica del videojuego


    public String getPlataforma() {
        return plataforma;
    }

    public String getGenero() {
        return genero;
    }

    // Implementación interfaz prestable
    @Override
    public void prestar (Usuario u){
        // No se puede prestar si ya está prestado
        if (prestado) throw new IllegalStateException("El videojuego ya esta prestado");

        this.prestado= true;
        this.prestadoA= u;
    }

    @Override
    public void devolver(){
        this.prestado=false;
        this.prestadoA= null;
    }
    @Override
    public boolean estaPrestado (){
        return this.prestado;
    }

    @Override
    public String toString() {
        return "Videojuego{" +
                "plataforma='" + plataforma + '\'' +
                ", genero='" + genero + '\'' +
                ", prestado=" + prestado +
                ", prestadoA=" + prestadoA +
                ", id=" + id +
                ", titulo='" + titulo + '\'' +
                ", anho='" + anho + '\'' +
                ", formato=" + formato +
                '}';
    }
}
