import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    public static final int PORT = 1504;

    // Храним PrintWriter и имя клиента вместе
    private static final CopyOnWriteArrayList<ClientInfo> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Поток для чтения с консоли и рассылки всем клиентам
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String message = scanner.nextLine();
                    String time = currentTime();
                    for (ClientInfo client : clients) {
                        client.writer.println("[SERVER] [" + time + "]: " + message);
                        client.writer.flush();
                    }
                    System.out.println("[TO ALL] [" + time + "]: " + message);
                }
            }).start();

            // Принимаем новых клиентов
            while (true) {
                Socket socket = serverSocket.accept();

                new Thread(() -> {
                    try (
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
                    ) {
                        // Первое сообщение от клиента — его имя
                        String name = in.readLine();
                        clients.add(new ClientInfo(name, out));
                        System.out.println("Client connected: " + name + " (" + socket + ")");

                        String line;
                        while ((line = in.readLine()) != null) {
                            String time = currentTime();
                            String fullMessage = "[" + name + "] [" + time + "]: " + line;
                            System.out.println(fullMessage);
                            for (ClientInfo client : clients) {
                                client.writer.println(fullMessage);
                            }
                        }

                    } catch (IOException e) {
                        System.out.println("A client disconnected.");
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Класс для хранения имени клиента и его PrintWriter
    private static class ClientInfo {
        String name;
        PrintWriter writer;

        ClientInfo(String name, PrintWriter writer) {
            this.name = name;
            this.writer = writer;
        }
    }

    // Возвращает текущее время в формате HH:mm:ss
    private static String currentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}
