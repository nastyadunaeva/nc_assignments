package com.example.jpa_h2.controller;

import com.example.jpa_h2.entity.Person;
import com.example.jpa_h2.entity.PersonStock;
import com.example.jpa_h2.entity.Stock;
import com.example.jpa_h2.model.Counter;
import com.example.jpa_h2.model.FileParser;
import com.example.jpa_h2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
public class MainController {

    private PersonJPARepository personJPARepository;

    private StockJPARepository stockJPARepository;

    private PasswordEncoder passwordEncoder;

    private PersonStockJPARepository personStockJPARepository;

    private StockMongoRepository stockMongoRepository;

    public static HashMap<String, Double> cache;

    //private Long id_table;

    @Autowired
    public MainController(PersonJPARepository personJPARepository, PasswordEncoder passwordEncoder, StockJPARepository stockJPARepository, PersonStockJPARepository personStockJPARepository, StockMongoRepository stockMongoRepository) {
        this.personJPARepository = personJPARepository;
        this.passwordEncoder = passwordEncoder;
        this.stockJPARepository = stockJPARepository;
        this.personStockJPARepository = personStockJPARepository;
        this.stockMongoRepository = stockMongoRepository;
        //this.id_table = new Long(0);
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
                Stock stock = stockMongoRepository.findBySymbol(symbol);
                if (stock == null) {
                    stock = new Stock();
                    Counter.count++;
                    stock.setId(Counter.count);
                    stock.setSymbol(symbol);

                }
                stock.setLongName(FileParser.tickerName.get(symbol));
                stock.setRegularMarketPrice(cache.get(symbol));

                stock.setTradeable(true);
                //stockJPARepository.save(stock);
                stockMongoRepository.save(stock);
            }
            cache.clear();
        }
    }

    @GetMapping(value = "/stock/all")
    public String allStocks() {
        //Iterable<Stock> all = stockJPARepository.findAll();
        Iterable<Stock> all = stockMongoRepository.findAll();
        StringBuilder sb = new StringBuilder();
        all.forEach(s -> sb.append(s.getSymbol() + "   " + s.getLongName() + "   " + s.getRegularMarketPrice() + "<br>"));
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
                //Stock stock = stockJPARepository.findBySymbol(ticker);
                Stock stock = stockMongoRepository.findBySymbol(ticker);
                personStock.setStockId(stock.getId());
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
                //Stock stock = stockJPARepository.findBySymbol(ticker);
                Stock stock = stockMongoRepository.findBySymbol(ticker);
                PersonStock personStock = personStockJPARepository.findByPersonIdAndStockId(person.getId(), stock.getId()).get();
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
                    Long stockId = personStock.getStockId();
                    //Stock stock = stockJPARepository.findById(stockId).get();
                    Stock stock = stockMongoRepository.findById(stockId).get();
                    sb.append(stock.getSymbol() + " " + stock.getLongName() + " " + stock.getRegularMarketPrice() + "<br>");
                }
            }
        }
        else {
            sb.append("Access denied");
        }
        return sb.toString();
    }
}
