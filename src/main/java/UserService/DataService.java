package UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataService {

    public static List<String> getChatIdFromBroadcastList() {
        List<String> chatIdList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/UserService/broadCastChatId.txt"))) {
            String line = br.readLine();
            while (line != null) {
                chatIdList.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chatIdList;
    }

    public static void addChatIdToBroadcastList(String chatId) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/java/UserService/broadCastChatId.txt", true))) {
            String allChatId = new String(Files.readAllBytes(Paths.get("src/main/java/UserService/broadCastChatId.txt")));
            if (!allChatId.contains(chatId)) bw.write(chatId + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeChatIdFromBroadcastList(String chatId) {
        try {
            String allChatId = new String(Files.readAllBytes(Paths.get("src/main/java/UserService/broadCastChatId.txt")));
            BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/java/UserService/broadCastChatId.txt"));
            allChatId = allChatId.replaceFirst(chatId + "\n", "");
            bw.write(allChatId);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addChatIdAmountOfTests(String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/java/UserService/chatId_amountOfTests.txt", true))) {
            bw.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getAmountOfTestsFromChatId(Long chatId) {
        int amountOfTests = 10;
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/UserService/chatId_amountOfTests.txt"))) {
            String line = br.readLine();
            while (line != null) {
                if (line.contains(chatId.toString())) {
                    amountOfTests = Integer.parseInt(line.substring(line.indexOf(":") + 1));
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return amountOfTests;
    }
}
