
import java.util.Scanner;

//* Punto de entrada del programa.
//* Recibe N, M y la semilla (ya sea por argumentos de ejecucion o por consola)
//* Los evia como parametros a Laberinto.configurar(n, m, semilla).
//* No contiene ciclos ni recursividad
public class Main{
    public static void main(String[] args) {
        if (args.length >=3){
            int n = Math.max(5, Integer.parseInt(args[0]));
            int m = Math.max(5, Integer.parseInt(args[1]));
            long semilla = Long.parseLong(args[2]);
            ejecutar(n, m, semilla);
        }else{
    
            Scanner sc = new Scanner(System.in);
        System.out.print("Filas (N): ");
        int n = Math.max(5, sc.nextInt());
        System.out.print("Columnas (M): ");
        int m = Math.max(5, sc.nextInt());
        System.out.print("semilla");
        long semilla = sc.nextLong();
        ejecutar(n, m, semilla);
        }
    }
    static void ejecutar(int n, int m, long semilla){
        Laberinto.configurar(n, m, semilla);
        System.out.println("\nLaberinto " + n + "x" + m + " (semilla " + semilla + "):\n");
        Laberinto.procesar(Laberinto.IMPRIMIR, 0, 0, 0, 0, 0);
        System.out.println("\nPortales: ");
        System.out.println("  " + Laberinto.describirPortal(0));
        System.out.println("  " + Laberinto.describirPortal(1));
        System.out.println("  " + Laberinto.describirPortal(2));


        Laberinto.evaluarRutas();

        

        System.out.println();
        if (Laberinto.mejor == Integer.MAX_VALUE){
            //* Caso limite 5: informa cuando el laberinto no tiene solucion
            System.out.println("El lsberinto no tiene solucion (no existe camino).");
        }else{

            Laberinto.reconstruirRuta();
            System.out.println("Camino mas corto: " + Laberinto.mejor + "pasos ('*' = camino)\n");
            Laberinto.procesar(Laberinto.IMPRIMIR, 0, 0, 1, 0, 0);
            System.out.print("\nRecorrido: ");
            Laberinto.procesar(Laberinto.VOLCAR, 0, 0, 0, 0, 0);
            System.out.println();
        

        }
    }
}

        
