package com.example.jpa_h2.controller;

import com.example.jpa_h2.entity.CompanyProfile;
import com.example.jpa_h2.entity.Person;
import com.example.jpa_h2.entity.PersonStock;
import com.example.jpa_h2.entity.Stock;
import com.example.jpa_h2.model.FileParser;
import com.example.jpa_h2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class MainController {

    private PersonJPARepository personJPARepository;

    private StockJPARepository stockJPARepository;

    private PasswordEncoder passwordEncoder;

    private PersonStockJPARepository personStockJPARepository;

    private StockMongoRepository stockMongoRepository;

    public static HashMap<String, Double> cache;

    //public static Long currentTime;

    @Autowired
    public MainController(PersonJPARepository personJPARepository, PasswordEncoder passwordEncoder, StockJPARepository stockJPARepository, PersonStockJPARepository personStockJPARepository, StockMongoRepository stockMongoRepository) {
        this.personJPARepository = personJPARepository;
        this.passwordEncoder = passwordEncoder;
        this.stockJPARepository = stockJPARepository;
        this.personStockJPARepository = personStockJPARepository;
        this.stockMongoRepository = stockMongoRepository;
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

    @Scheduled(fixedRate = 10000)
    public void updateInfo() {

        synchronized (cache) {
            //System.out.println(cache);
            for (String symbol: cache.keySet()) {
                //Stock stock = stockJPARepository.findBySymbol(symbol);
                //Stock stock = stockMongoRepository.findBySymbol(symbol);
                Stock stock = new Stock();
                //if (stock == null) {
                  //  stock = new Stock();
                    Long id = stockMongoRepository.count()+1;
                    stock.setId(id);
                    stock.setSymbol(symbol);

                //}
                stock.setLongName(FileParser.tickerName.get(symbol));
                stock.setRegularMarketPrice(cache.get(symbol));
                stock.setTradeable(true);
                Date date = new Date();
                stock.setTime(date.getTime());
                stockMongoRepository.save(stock);
            }
            cache.clear();
        }
    }

    @GetMapping(value = "/stock/all")
    public String allStocks() {
        //Iterable<Stock> all = stockJPARepository.findAll();
        StringBuilder sb = new StringBuilder();
        for (String ticker: FileParser.tickerName.keySet()) {
            Stock s = stockMongoRepository.findTop1BySymbolOrderByTimeDesc(ticker);
            if (s != null) {
                sb.append(s.getSymbol() + "   " + s.getLongName() + "   " + s.getRegularMarketPrice() + "<br>");
            }

        }
        return sb.toString();
    }

    @PostMapping(value = "/stock/{username}/favorites/add/{ticker}")
    public String addFavorite(@PathVariable String username, @PathVariable String ticker, Principal principal) {
        StringBuilder sb = new StringBuilder();
        if (principal.getName().compareTo(username) == 0) {
            Person person = new Person();
            try {
                person = personJPARepository.findByUsername(username).get();
            }
            catch (NoSuchElementException e) {
                System.out.println("Username not found.");
                person = null;
            }
            if (person != null) {
                PersonStock personStock = new PersonStock();
                Stock stock = stockMongoRepository.findTop1BySymbolOrderByTimeDesc(ticker);
                personStock.setStockTicker(ticker);
                personStock.setPersonId(person.getId());
                personStockJPARepository.save(personStock);
            }
        }
        else {
            sb.append("Access denied");
        }
        return sb.toString();
    }

    @DeleteMapping(value = "/stock/{username}/favorites/delete/{ticker}")
    public String deleteFavorite(@PathVariable String username, @PathVariable String ticker, Principal principal) {
        StringBuilder sb = new StringBuilder();
        if (principal.getName().compareTo(username) == 0) {
            Person person = new Person();
            try {
                person = personJPARepository.findByUsername(username).get();
            }
            catch (NoSuchElementException e) {
                System.out.println("Username not found.");
                person = null;
            }
            if (person != null) {
                PersonStock personStock = personStockJPARepository.findByPersonIdAndStockTicker(person.getId(), ticker).get();
                personStockJPARepository.delete(personStock);
            }
        }
        else {
            sb.append("Access denied");
        }
        return sb.toString();
    }

    @GetMapping(value = "/stock/{username}/favorites")
    public String getFavorites(@PathVariable String username, Principal principal) {
        StringBuilder sb = new StringBuilder();
        if (principal.getName().compareTo(username) == 0) {
            Person person = new Person();
            try {
                person = personJPARepository.findByUsername(username).get();
            }
            catch (NoSuchElementException e) {
                System.out.println("Username not found.");
                person = null;
            }
            if (person != null) {
                Long personId = person.getId();
                List<PersonStock> personStocks = personStockJPARepository.findByPersonId(personId);
                for (PersonStock personStock: personStocks) {
                    String ticker = personStock.getStockTicker();
                    Stock newest = stockMongoRepository.findTop1BySymbolOrderByTimeDesc(ticker);
                    sb.append(newest.getSymbol() + " " + newest.getLongName() + " " + newest.getRegularMarketPrice() + "<br>");
                }
            }
        }
        else {
            sb.append("Access denied");
        }
        return sb.toString();
    }

    @GetMapping(value = "/stock/{ticker}")
    public String getInfoStock(@PathVariable String ticker) {
        StringBuilder sb = new StringBuilder();
        Stock stock = stockMongoRepository.findTop1BySymbolOrderByTimeDesc(ticker);
        if (stock != null) {
            Long now = stock.getTime();
            Long dayAgo = now - 24 * 60 * 60 * 1000;
            Stock yesterday = stockMongoRepository.findTop1BySymbolAndTimeGreaterThanOrderByTimeAsc(ticker, dayAgo).get();
            Double percent = (stock.getRegularMarketPrice() - yesterday.getRegularMarketPrice()) / yesterday.getRegularMarketPrice() * 100;
            String result = String.format("%.2f", percent);

            if (percent > 0.000001) {
                sb.append(stock.getSymbol() + "   " + stock.getLongName() + "   " + stock.getRegularMarketPrice() + "    +" + result + "%" + "<br>");
            } else {
                sb.append(stock.getSymbol() + "   " + stock.getLongName() + "   " + stock.getRegularMarketPrice() + "   " + result + "%" + "<br>");
            }

            String url = "https://finnhub.io/api/v1/stock/profile2?symbol=" + ticker + "&token=c3jfck2ad3i82raod360";
            RestTemplate restTemplate = new RestTemplate();
            CompanyProfile companyProfile = restTemplate.getForObject(url, CompanyProfile.class);
            //ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            //String body = responseEntity.getBody();
            sb.append("<br>");
            sb.append("<br>");
            sb.append("Industry: " + companyProfile.getFinnhubIndustry() + "<br>");
            sb.append("Country of company's headquater: " + companyProfile.getCountry() + "<br>");
            sb.append("Currency used in company filings: " + companyProfile.getCurrency() + "<br>");
            sb.append("IPO date: " + companyProfile.getIpo() + "<br>");
            sb.append("Market capitalization: " + companyProfile.getMarketCapitalization() + "<br>");
            sb.append("Company website: " + companyProfile.getWeburl() + "<br>");


        } else {
            sb.append("Stock not found");
        }
        return sb.toString();
    }
}
