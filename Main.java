
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        ejecutar();
    }
    static void ejecutar(){
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
        //calcula la secuencia optima de portals usando distancia LCA
        Laberinto.procesar(Laberinto.EVALUAR_RUTAS, Laberinto.inicioX, Laberinto.inicioY, 0,0,0 );
        System.out.println();
        
        if (Laberinto.mejor == Integer.MAX_VALUE){
            System.out.println("No existe camino");
        }else{
            //recorre el laberinto sobre la pila sigiendo la secuancia ganadora
            Laberinto.mejorRuta.push(Laberinto.inicioX, Laberinto.inicioY);
            int seqInv = Laberinto.procesar(Laberinto.INVERTIR_SEQ, Laberinto.mejorSecuencia, 0, 0, 0, 0);
            Laberinto.procesar(Laberinto.RECORRER_SEQ, Laberinto.inicioX, Laberinto.inicioY, seqInv, 0, 0);
            System.out.println("Camino más corto: " + Laberinto.mejor + " pasos ('*' = camino)\n");
            Laberinto.procesar(Laberinto.IMPRIMIR, 0, 0, 1, 0, 0);
            System.out.println("\nRecorrido: ");
            Laberinto.procesar(Laberinto.VOLCAR,0,0,0,0,0);
            System.out.println();
        }
    }
}