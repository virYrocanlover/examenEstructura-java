import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ejecutar();
    }

    static void ejecutar(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Filas (N): ");
        int n = Math.max(7, sc.nextInt()) | 1;
        System.out.println("Columnas (M): ");
        int m = Math.max(7, sc.nextInt()) | 1;
        System.out.println("Semilla: ");
        long semilla = sc.nextLong();

        Laberinto.configurar(n, m, semilla);

        System.out.println("Laberinto ");


        

        
    }
 }



