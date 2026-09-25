package co.icesi.buscaminas.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

public class MainClient {

    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    private static Gson gson = new Gson();
    private static BuscaminasTCPClient client = new BuscaminasTCPClient();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("=============================================");
            System.out.println("     BUSCAMINAS DISTRIBUIDO - CLIENTE TCP");
            System.out.println("=============================================");
            System.out.println("[1] Iniciar nueva partida (Filas, Columnas, Minas)");
            System.out.println("[2] Destapar celda (Fila, Columna)");
            System.out.println("[3] Marcar/Desmarcar bandera (Fila, Columna)");
            System.out.println("[4] Consultar estado actual del tablero");
            System.out.println("[5] Rendirse y revelar tablero completo");
            System.out.println("[6] Salir");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();

            try {
                switch (opcion) {
                    case "1": {
                        System.out.print("Filas: ");
                        String n = scanner.nextLine().trim();
                        System.out.print("Columnas: ");
                        String m = scanner.nextLine().trim();
                        System.out.print("Minas: ");
                        String minas = scanner.nextLine().trim();

                        Map<String, String> data = new HashMap<>();
                        data.put("n", n);
                        data.put("m", m);
                        data.put("minas", minas);

                        Response response = client.sendRequest(HOST, PORT, new Request("INIT_GAME", data));
                        printBoard(response);
                        break;
                    }
                    case "2": {
                        System.out.print("Fila: ");
                        String i = scanner.nextLine().trim();
                        System.out.print("Columna: ");
                        String j = scanner.nextLine().trim();

                        Map<String, String> data = new HashMap<>();
                        data.put("i", i);
                        data.put("j", j);

                        Response response = client.sendRequest(HOST, PORT, new Request("SELECT_CELL", data));
                        printBoard(response);
                        checkGameEnd(response);
                        break;
                    }
                    case "3": {
                        System.out.print("Fila: ");
                        String i = scanner.nextLine().trim();
                        System.out.print("Columna: ");
                        String j = scanner.nextLine().trim();

                        Map<String, String> data = new HashMap<>();
                        data.put("i", i);
                        data.put("j", j);

                        Response response = client.sendRequest(HOST, PORT, new Request("MARK_CELL", data));
                        printBoard(response);
                        break;
                    }
                    case "4": {
                        Response response = client.sendRequest(HOST, PORT, new Request("GET_BOARD", new HashMap<>()));
                        printBoard(response);
                        break;
                    }
                    case "5": {
                        Response response = client.sendRequest(HOST, PORT, new Request("SOW_ALL", new HashMap<>()));
                        printBoard(response);
                        System.out.println("Te rendiste. Tablero completo revelado.");
                        break;
                    }
                    case "6":
                        running = false;
                        System.out.println("Saliendo del cliente. Hasta luego!");
                        break;
                    default:
                        System.out.println("Opcion no valida.");
                }
            } catch (IOException e) {
                System.out.println("Error de conexion con el servidor: " + e.getMessage());
            }
        }
        scanner.close();
    }

    // Reconstruye el Cell[][] desde el Map<String,Object> generico que deja Gson
    private static Cell[][] extractBoard(Response response) {
        Object boardRaw = response.data.get("board");
        String boardJson = gson.toJson(boardRaw);
        return gson.fromJson(boardJson, Cell[][].class);
    }

    private static void printBoard(Response response) {
        if (response == null || response.data == null || !response.data.containsKey("board")) {
            System.out.println("No se recibio tablero en la respuesta.");
            return;
        }
        Cell[][] board = extractBoard(response);

        System.out.print("   ");
        for (int j = 0; j < board[0].length; j++) {
            System.out.print(" " + j);
        }
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.print(i + " [");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(" " + board[i][j]);
            }
            System.out.println(" ]");
        }
    }

    private static void checkGameEnd(Response response) {
        if (response == null || response.data == null) return;
        Object gameEndObj = response.data.get("gameEnd");
        Object winObj = response.data.get("win");
        boolean gameEnd = gameEndObj != null && Boolean.parseBoolean(gameEndObj.toString());
        boolean win = winObj != null && Boolean.parseBoolean(winObj.toString());

        if (gameEnd && win) {
            System.out.println("\u001B[32m¡GANASTE! Felicidades, encontraste todas las celdas seguras.\u001B[0m");
        } else if (gameEnd && !win) {
            System.out.println("\u001B[31m¡BOOM! Pisaste una mina. Derrota.\u001B[0m");
        }
    }
}