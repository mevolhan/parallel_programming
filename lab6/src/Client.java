import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static final String HOST = "localhost";
    public static final int PORT = 1504;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(HOST, PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

            // Отправляем имя на сервер
            out.println(name);

            // Поток для приёма сообщений от сервера
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Server disconnected.");
                }
            }).start();

            // Основной поток: отправка сообщений на сервер
            while (true) {
                String input = scanner.nextLine();
                out.println(input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
