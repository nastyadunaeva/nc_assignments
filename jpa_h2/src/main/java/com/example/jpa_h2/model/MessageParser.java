package com.example.jpa_h2.model;

import com.example.jpa_h2.controller.MainController;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {
    public static void getPrices(String message) {
        //message = "{\"data\":[{\"p\":290.00,\"s\":\"MSFT\",\"t\":1575526691134,\"v\":0.011467}],\"type\":\"trade\"}";
        //System.out.println(message);
        List<String> strings = new ArrayList<>();
        if (message.matches(".*trade.*")) {
            Pattern pattern = Pattern.compile("\"p\":[0-9\\.]*,\"s\":\"[A-Z]*\"");
            Matcher matcher = pattern.matcher(message);
            while(matcher.find()) {
                String str = matcher.group();
                if (!strings.contains(str)) {
                    strings.add(str);
                }
            }

        }
        //Map<String, Double> prices = new HashMap<String, Double>();
        for (String s: strings) {
            int i1 = s.indexOf(":");
            int i2 = s.indexOf(",");
            Double price = Double.parseDouble(s.substring(i1+1, i2));
            int i3 = s.indexOf(":",i2);
            int i4 = s.lastIndexOf("\"");
            String ticker = s.substring(i3+2, i4);
            //prices.put(ticker, price);

            if (MainController.cache != null) {
                synchronized (MainController.cache) {
                    MainController.cache.put(ticker, price);
                }
            }


        }
        if (MainController.cache != null) {
            synchronized (MainController.cache) {
                //System.out.println(MainController.cache);
                //System.out.println();
                //System.out.println();
            }
        }
    }
}
