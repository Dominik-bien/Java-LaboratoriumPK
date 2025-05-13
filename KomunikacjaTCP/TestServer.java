import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TestServer {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 250;
    private static final int TIMEOUT_MS = 25000;

    private static List<List<String>> questions = new ArrayList<>();
    private static List<String> correctAnswers = new ArrayList<>(); 
    private static final Object fileLock = new Object();

    public static void main(String[] args) {
        loadQuestions("bazaPytan.txt");

        ExecutorService executor = Executors.newFixedThreadPool(MAX_CLIENTS);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serwer dziala na porcie " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static void loadQuestions(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> questionBlock = new ArrayList<>();
                questionBlock.add(line); // pytanie

                String correct = "";
                for (int i = 0; i < 4; i++) {
                    String answerLine = reader.readLine();
                    if (answerLine.startsWith("*")) {
                        correct = answerLine.substring(1, 2); // np. A, B, ...
                        answerLine = answerLine.substring(1); // usuwamy "*" dla klienta
                    }
                    questionBlock.add(answerLine);
                }
                questions.add(questionBlock);
                correctAnswers.add(correct);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            Socket clientSocket = this.socket; // lokalna kopia

            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
            ) {
                List<String> userAnswers = new ArrayList<>();
                int score = 0;

                for (int i = 0; i < questions.size(); i++) {
                    // Wysylamy pytanie
                    List<String> q = questions.get(i);
                    for (String line : q) {
                        try {
                            out.write(line + "\n");
                        } catch (IOException e) {
                            System.out.println("Klient przerwal połączenie. Zakonczono sesje.");
                            return; // konczymy obsluge tego klienta
                        }
                    }
                    out.flush();

                    clientSocket.setSoTimeout(TIMEOUT_MS);
                    String response;
                    try {
                        response = in.readLine();
                        if (response == null || response.equalsIgnoreCase("exit")) {
                            System.out.println("Klient zakonczyl test.");
                            break;
                        }
                        /*if (response == null) {
                            System.out.println("Klient zakonczyl polaczenie.");
                            break;
                        }
                        if (response.equalsIgnoreCase("exit")) {
                            System.out.println("Klient zakonczyl test.");
                            userAnswers.add("Pytanie " + (i + 1) + ": exit");
                            break;
                        }*/

                    } catch (SocketTimeoutException e) {
                        response = "";
                    } catch (IOException e) {
                        System.out.println("Blad odczytu od klienta – zakonczono.");
                        break;
                    }

                    userAnswers.add("Pytanie " + (i + 1) + ": " + response);
                    if (response.equalsIgnoreCase(correctAnswers.get(i))) {
                        score++;
                    }
                }

                /*for (int i = 0; i < questions.size(); i++) {
                    List<String> q = questions.get(i);
                    for (String line : q) {
                        out.write(line + "\n");
                    }
                    out.flush();

                    clientSocket.setSoTimeout(TIMEOUT_MS);
                    String response;
                    try {
                        response = in.readLine();
                        if (response == null || response.equalsIgnoreCase("exit")) break;
                    } catch (SocketTimeoutException e) {
                        response = "";
                    }


                    userAnswers.add("Pytanie " + (i + 1) + ": " + response);
                    if (response.equalsIgnoreCase(correctAnswers.get(i))) {
                        score++;
                    }
                }*/

                synchronized (fileLock) {
                    try (BufferedWriter answersWriter = new BufferedWriter(new FileWriter("bazaOdpowiedzi.txt", true))) {
                        for (String ans : userAnswers) {
                            answersWriter.write(ans + "\n");
                        }
                        answersWriter.write("----\n");
                    }

                    try (BufferedWriter resultsWriter = new BufferedWriter(new FileWriter("wyniki.txt", true))) {
                        resultsWriter.write("Wynik: " + score + "/" + questions.size() + "\n");
                    }
                }

                //System.out.println("Klient ukonczyl test. Wynik: " + score + "/" + questions.size());


                out.write("Twoj wynik: " + score + "/" + questions.size() + "\n");
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
