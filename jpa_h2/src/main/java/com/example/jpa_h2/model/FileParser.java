package com.example.jpa_h2.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser {
    public static Map<String, String> tickerName;

    public static void initialize() {
        Map<String, String> result = new HashMap<>();
        final String dir = System.getProperty("user.dir");
        try (BufferedReader br = new BufferedReader(new FileReader(dir+"/src/main/java/com/example/jpa_h2/sp.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                int space = line.indexOf(' ');
                String symbol = line.substring(0, space);
                String name = line.substring(space+1);
                result.put(symbol, name);
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        tickerName = result;
    }

    public static List<String> spList() {
        List<String> result = new ArrayList<String>();
        final String dir = System.getProperty("user.dir");
        try (BufferedReader br = new BufferedReader(new FileReader(dir+"/src/main/java/com/example/jpa_h2/sp.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                int space = line.indexOf(' ');
                String symbol = line.substring(0, space);
                result.add(symbol);
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return result;
    }
}
