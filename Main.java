
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        ejecutar();
    }
    satic void ejecutar(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Filas (N): ");
        int n = Math.max(5, sc.nextInt());
        System.out.print("Columnas (M): ");
        int m = Math.max(5, sc.nextInt());
        System.out.println("semilla");
        long semilla = sc.nextLong();

        Laberinto.configurar(n, m, semilla);

        System.out.println("\nLaberinto " + n + " x " + m + " (semilla " + semilla + "):\n");
        Laberinto.procesar(Laberinto.IMPRIMIR, 0, 0, 0, 0, 0);

        System.out.println("\nPortales");
        System.out.println(" "+ Laberinto.describirPortal(0));
        System.out.println(" "+ Laberinto.describirPortal(1));
        System.out.println(" "+ Laberinto.describirPortal(2));
        
    }
}