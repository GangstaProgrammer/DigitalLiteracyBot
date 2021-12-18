package Tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Tests {
    public static ArrayList<String> easyTests = new ArrayList<>();
    public static ArrayList<String> mediumTests = new ArrayList<>();
    public static ArrayList<String> hardTests = new ArrayList<>();

    static {
        testListsCreator(easyTests, "easyTests.txt");
        testListsCreator(mediumTests, "mediumTests.txt");
        testListsCreator(hardTests, "hardTests.txt");
    }

    private static void testListsCreator(ArrayList<String> testList, String fileName) {
        try (FileReader fileReader = new FileReader("src/main/java/Tests/" + fileName);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line = bufferedReader.readLine();
            while (line != null) {
                if (line.contains("<q>")) {
                    StringBuilder quiz = new StringBuilder();
                    quiz.append(line);
                    line = bufferedReader.readLine();
                    while (line != null && !line.contains("<q>")) {
                        quiz.append("\n").append(line);
                        line = bufferedReader.readLine();
                    }
                    testList.add(quiz.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
