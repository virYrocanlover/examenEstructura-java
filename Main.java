import java.util.Scanner;

/**
 * Programa principal: pide N, M y semilla, crea el laberinto, lo resuelve
 * y muestra el resultado. No tiene ciclos ni recursión: todo el trabajo
 * lo hace Laberinto.procesar().
 *
 * Archivos: Main.java, Laberinto.java, Pila.java
 * Compilar:  javac *.java      Ejecutar:  java Main
 */
public class Main {

    public static void main(String[] args) {
        // Hilo con pila grande: la recursión puede ser profunda en laberintos grandes
        new Thread(null, Main::ejecutar, "hiloLaberinto", 1L << 28).start();
    }

    static void ejecutar() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Filas (N): ");
        int n = Math.max(7, sc.nextInt()) | 1;       // mínimo 7 y siempre impar
        System.out.print("Columnas (M): ");
        int m = Math.max(7, sc.nextInt()) | 1;
        System.out.print("Semilla: ");
        long semilla = sc.nextLong();

        Laberinto.configurar(n, m, semilla);

        System.out.println("\nLaberinto " + n + " x " + m + " (semilla " + semilla + "):\n");
        Laberinto.procesar(Laberinto.IMPRIMIR, 0, 0, 0, 0, 0);

        System.out.println("\nPortales:");
        System.out.println("  " + Laberinto.describirPortal(0));
        System.out.println("  " + Laberinto.describirPortal(1));
        System.out.println("  " + Laberinto.describirPortal(2));

        // x, y = inicio; pasos = 0; desde = -1 (sin movimiento previo); usados = 0
        Laberinto.procesar(Laberinto.RESOLVER, Laberinto.inicioX, Laberinto.inicioY, 0, -1, 0);

        System.out.println();
        if (Laberinto.mejorRuta == null) {
            System.out.println("No existe camino.");
        } else {
            System.out.println("Camino más corto: " + Laberinto.mejor + " pasos ('*' = camino)\n");
            Laberinto.procesar(Laberinto.IMPRIMIR, 0, 0, 1, 0, 0);
            System.out.print("\nRecorrido: ");
            Laberinto.procesar(Laberinto.VOLCAR, 0, 0, 0, 0, 0);
            System.out.println();
        }
    }
}


