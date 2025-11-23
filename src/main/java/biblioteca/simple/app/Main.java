package biblioteca.simple.app;

import biblioteca.simple.contratos.Prestable;
import biblioteca.simple.modelo.*;
import biblioteca.simple.servicios.Catalogo;
import biblioteca.simple.servicios.PersistenciaUsuarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static final Catalogo catalogo = new Catalogo();
    private static final List<Usuario> usuarios = new ArrayList<>();

    public static void main(String[] args) {
        cargarDatos();
        menu();
    }

    private static void cargarDatos() {
        catalogo.altaProducto(new Libro(1, "El quijote", "1600", Formato.FISICO, "446464464", "cervantes"));
        catalogo.altaProducto(new Libro(2, "Harry Potter", "2003", Formato.FISICO, "4454544464", "jk,Rowling"));
        catalogo.altaProducto(new Pelicula(3, "El padrino", "1972", Formato.DIGITAL, "Francis", 175));
        catalogo.altaProducto(new Pelicula(4, "Parasitos", "2002", Formato.DIGITAL, "Bong-jon", 132));
        catalogo.altaProducto(new Videojuego(5, "Gran Turismo 7", "2022", Formato.DIGITAL, "ps5", "conducción"));
        catalogo.altaProducto(new Videojuego(6, "GTA V", "2013", Formato.DIGITAL, "ps4", "acción"));

        usuarios.add(new Usuario(1, "fran"));
        usuarios.add(new Usuario(2, "Laura"));
    }

    private static final Scanner sc = new Scanner(System.in);

    private static void menu() {
        int opcion;
        do {
            System.out.println("\n==== Menu biblioteca ====");
            System.out.println("1. Listar Productos");
            System.out.println("2. Buscar por titulo");
            System.out.println("3. Buscar por año");
            System.out.println("4. Prestar producto");
            System.out.println("5. Devolver producto");
            System.out.println("6. Crear nuevo usuario");
            System.out.println("7. Listar usuarios");
            System.out.println("8. Exportar usuarios");
            System.out.println("9. Importar usuarios");
            System.out.println("0. salir");
            while (!sc.hasNextInt()) sc.nextInt();
            opcion = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1 -> listar();
                case 2 -> buscarPorTitulo();
                case 3 -> buscarPorAnio();
                case 4 -> prestar();
                case 5 -> devolver();
                case 6 -> crearUsuario();
                case 7 -> listarUsuarios();
                case 8 -> exportarUsuarios();
                case 9 -> importarUsuarios();
                case 0 -> System.out.println("Hasta luego");
                default -> System.out.println("Opción no valida");
            }
        } while (opcion != 0);
    }

// FUNCIONES

    // LISTAR PRODUCTOS
    private static void listar() {
        List<Producto> lista = catalogo.listar();
        if (lista.isEmpty()) {
            System.out.println("Catalogo vacío");
            return;
        }
        System.out.println("== Lista de productos ==");

        for (Producto p : lista) System.out.println("-" + p);
    }

    // BUSCAR POR TITULO
    private static void buscarPorTitulo() {
        System.out.println("Titulo (escribe parte del titulo)");
        String tit = sc.nextLine();
        catalogo.buscar(tit).forEach(p -> System.out.println("-" + p));
    }


    // BUSCAR POR AÑO
    private static void buscarPorAnio() {
        System.out.println("Escribe el año: ");
        int a = sc.nextInt();
        sc.nextLine();
        catalogo.buscar(a).forEach(p -> System.out.println("-" + p));
    }

    // LISTAR USUARIOS
    private static void listarUsuarios() {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados");
            return;
        }
        System.out.println("== Lista de usuarios ==");
        usuarios.forEach(usuario -> System.out.println("Codigo: " + usuario.getId() + "Nombre: " + usuario.getNombre()));
    }

    private static Usuario getUsuarioPorCodigo(int id) {
        return usuarios.stream()
                .filter(usuario -> usuario.getId() == id)
                .findFirst()
                .orElse(null);
    }


    //GESTION PRESTAMOS
    private static void prestar() {

        //1) mostrar productos disponibles

        List<Producto> disponibles = catalogo.listar().stream()
                .filter(p -> p instanceof Prestable pN && !pN.estaPrestado()).collect(Collectors.toList());

        if (disponibles.isEmpty()) {
            System.out.println("no hay productos para prestar");
            return;
        }
        System.out.println("== PRODUCTOS DISPONIBLES ==");
        disponibles.forEach(p -> System.out.println("- ID: " + p.getId() + "|" + p));

        System.out.println("Escribe el ID del producto: ");
        int id = sc.nextInt();
        sc.nextLine();

        Producto pEncontrado = disponibles.stream()
                .filter(p -> {
                    try {
                        return p.getId() == id;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);

        if (pEncontrado == null) {
            System.out.println("El id no existe");
            return;
        }

        listarUsuarios();
        System.out.println("Ingresa codigo usuario");

        int cUsuario = sc.nextInt();
        sc.nextLine();
        Usuario u1 = getUsuarioPorCodigo(cUsuario);

        //OFRECEMOS LA OPCION DE CREAR USUARIO
        if (u1 == null) {
            System.out.println("Usuario no encontrado");
            System.out.println("¿Desear Crear el usuario ahora? (Si/No): ");
            String respuesta = sc.nextLine().toUpperCase().trim();
            if (respuesta.equals("SI")) {
                u1 = crearUsuario();
            } else {
                System.out.println("operacion prestamo cancelada");
                return;
            }
        }

        Prestable pPrestable = (Prestable) pEncontrado;
        pPrestable.prestar(u1);
        System.out.println("El producto ha sido prestado correctamente");
    }

//DEVOLUCIONES

    public static void devolver() {
        List<Producto> pPrestados = catalogo.listar().stream()
                .filter(p -> p instanceof Prestable pN && pN.estaPrestado()).collect(Collectors.toList());

        if (pPrestados.isEmpty()) {
            System.out.println("no hay productos para prestar");
            return;
        }
        System.out.println("== PRODUCTOS PRESTADOS ==");
        pPrestados.forEach(p -> System.out.println("- ID: " + p.getId() + "|" + p));


        System.out.println("Escribe el ID del producto: ");
        int id = sc.nextInt();
        sc.nextLine();

        Producto pEncontrado = pPrestados.stream()
                .filter(p -> {
                    try {
                        return p.getId() == id;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);

        if (pEncontrado == null) {
            System.out.println("El id no existe");
            return;
        }

        Prestable pE = (Prestable) pEncontrado;
        pE.devolver();
        System.out.println("Devuelto correctamente");
    }

    // CREACION USUARIOS
    private static Usuario crearUsuario() {
        System.out.println("¿Cual es el nombre del nuevo usuario?");
        String nombre = sc.nextLine();

        //Generamos un nuevo ID
        int nuevoId = 1;
        if (!usuarios.isEmpty()) {
            Usuario ultimoUsuario = usuarios.get(usuarios.size() - 1);
            nuevoId = ultimoUsuario.getId() + 1;
        }

        //SE CREA EL NUEVO USUARIO Y GUARDAMOS
        Usuario nuevoUsuario = new Usuario(nuevoId, nombre);
        usuarios.add(nuevoUsuario);
        System.out.println("El usuario " + nombre + " con Id " + nuevoId + " Se ha creado correctamente");
        return nuevoUsuario;
    }

    private static void exportarUsuarios() {
        try {
            PersistenciaUsuarios.exportar(usuarios);
            System.out.println("Usuarios exportados correctamente");
        } catch (Exception e) {
            System.out.println("Error al exportar usuarios" + e.getMessage());
        }
    }

    private static void importarUsuarios() {
        try {
            List<Usuario> cargados = PersistenciaUsuarios.importar();
            usuarios.clear();
            usuarios.addAll(cargados);
            System.out.println("Usuarios cargados con exito");
        } catch (Exception e) {
            System.out.println("error al importar: " + e.getMessage());
        }
    }

}





