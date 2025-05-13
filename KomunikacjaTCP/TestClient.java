import java.io.*;
import java.net.*;

public class TestClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final int TIMEOUT_SECONDS = 10;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        ) {
            String line;
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                // Odbierz pytanie (5 linii: pytanie + 4 odpowiedzi)
                for (int i = 0; i < 5; i++) {
                    line = in.readLine();
                    if (line == null) return;
                    System.out.println(line);
                }

                System.out.print("Twoja odpowiedz (A/B/C/D lub exit): ");
                String answer = readLineWithTimeout(userInput, TIMEOUT_SECONDS);

                if (answer == null) {
                    System.out.println("\n Czas minal! Przechodzimy dalej.");
                    answer = "";
                }

                out.write(answer + "\n");
                out.flush();

                if (answer.equalsIgnoreCase("exit")) break;
            }

            line = in.readLine();
            if (line != null) System.out.println(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Funkcja czytajaca linie z timeoutem
    private static String readLineWithTimeout(BufferedReader reader, int timeoutSeconds) throws IOException {
        long endTime = System.currentTimeMillis() + timeoutSeconds * 1000;
        while (System.currentTimeMillis() < endTime) {
            if (System.in.available() > 0) {
                return reader.readLine();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return null;
            }
        }
        return null; // Timeout
    }
}
