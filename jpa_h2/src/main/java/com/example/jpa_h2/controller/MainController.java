package com.example.jpa_h2.controller;

import com.example.jpa_h2.entity.NumberInfo;
import com.example.jpa_h2.entity.Person;
import com.example.jpa_h2.entity.Stock;
import com.example.jpa_h2.model.FileParser;
import com.example.jpa_h2.repository.NumberJPARepository;
import com.example.jpa_h2.repository.PersonJPARepository;
import com.example.jpa_h2.repository.StockJPARepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
public class MainController {

    private PersonJPARepository personJPARepository;

    private NumberJPARepository numberJPARepository;

    private StockJPARepository stockJPARepository;

    private PasswordEncoder passwordEncoder;


    public static HashMap<String, Double> cache;

    @Autowired
    public MainController(PersonJPARepository personJPARepository, PasswordEncoder passwordEncoder, NumberJPARepository numberJPARepository, StockJPARepository stockJPARepository) {
        this.personJPARepository = personJPARepository;
        this.passwordEncoder = passwordEncoder;
        this.numberJPARepository = numberJPARepository;
        this.stockJPARepository = stockJPARepository;
        cache = new HashMap<>();
    }
 
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public String all() {
        Iterable<Person> all = personJPARepository.findAll();
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + " " + p.getUsername() + " " + p.getRole().name() + "<br>"));
        return sb.toString();
    }
    
    @GetMapping("/{id}")
    public String index(@PathVariable long id) {
    	Long i = new Long(id);
        Iterable<Person> all = personJPARepository.findByIdLike(i);
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + " " + p.getUsername() + " " + p.getRole().name() + "<br>"));
        return sb.toString();
    }
    
    @GetMapping("/name/{lastName}")
    public String lastName(@PathVariable String lastName) {
        Iterable<Person> all = personJPARepository.findByLastNameLike(lastName);
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + " " + p.getUsername() + " " + p.getRole().name() + "<br>"));
        return sb.toString();
    }
    
    @PostMapping("/add")
    public String add(@RequestBody Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personJPARepository.save(person);
    	return person.getFirstName() + " " + person.getLastName() + " was added to the database";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        personJPARepository.deleteById(id);
        return "Person was removed from the database";
    }

    /*@Scheduled(fixedRate = 10000)
    @GetMapping("/get")
    public void getInfo() {
        RestTemplate restTemplate = new RestTemplate();
        int number =  (int)Math.round(Math.random() * 10);

        String url = "http://numbersapi.com/" + number;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String body = responseEntity.getBody();

        NumberInfo num = new NumberInfo();
        num.setNumber(number);
        num.setInfo(body);
        try {
            numberJPARepository.save(num);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }*/


    @GetMapping(value = "/numbers")
    public String allNumbers() {
        Iterable<NumberInfo> all = numberJPARepository.findAll();
        StringBuilder sb = new StringBuilder();
        all.forEach(n -> sb.append(n.getInfo() + "<br>"));
        return sb.toString();
    }

    /*@Scheduled(fixedRate = 10000)
    public void updateInfo() {
        RestTemplate restTemplate = new RestTemplate();
        List<String> symbols = new ArrayList<String>();
        symbols.add("AAPL");

        for (String symbol: symbols) {
            //System.out.println(symbol);
            String url = "https://mboum.com/api/v1/qu/quote/?symbol=" + symbol + "&apikey=1Yti306TjP9UzmDPeqd9nDkDq1sXUzLTzP6tC3nVjdSxUZPQrwo2teEZajMn";
            var responseEntity = restTemplate.getForEntity(url, ArrayList.class);
            ArrayList<Map> body = responseEntity.getBody();
            Map<String,Object> result = new HashMap<String,Object>(body.get(0));

            Stock stock = stockJPARepository.findBySymbol(symbol);
            if (stock == null) {
                stock = new Stock();
                stock.setSymbol(symbol);
                stock.setLongName(result.get("longName").toString());
            }
            double price = Double.parseDouble(result.get("regularMarketPrice").toString());
            stock.setRegularMarketPrice(price);
            boolean tradeable = Boolean.parseBoolean(result.get("tradeable").toString());
            stock.setTradeable(tradeable);
            stockJPARepository.save(stock);
        }


    }*/

    @Scheduled(fixedRate = 10000)
    public void updateInfo() {

        synchronized (cache) {
            for (String symbol: cache.keySet()) {
                Stock stock = stockJPARepository.findBySymbol(symbol);
                if (stock == null) {
                    stock = new Stock();
                    stock.setSymbol(symbol);

                }
                stock.setLongName(FileParser.tickerName.get(symbol));
                stock.setRegularMarketPrice(cache.get(symbol));

                stock.setTradeable(true);
                stockJPARepository.save(stock);
            }
            cache.clear();
        }
    }
    @GetMapping(value = "/stock/all")
    public String allStocks() {
        Iterable<Stock> all = stockJPARepository.findAll();
        StringBuilder sb = new StringBuilder();
        all.forEach(s -> sb.append(s.getSymbol() + "   " + s.getLongName() + "   " + s.getRegularMarketPrice() + "<br>"));
        return sb.toString();
    }
}
