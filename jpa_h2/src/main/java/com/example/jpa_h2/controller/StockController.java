package com.example.jpa_h2.controller;

import com.example.jpa_h2.entity.CompanyProfile;
import com.example.jpa_h2.entity.Person;
import com.example.jpa_h2.entity.PersonStock;
import com.example.jpa_h2.entity.Stock;
import com.example.jpa_h2.repository.PersonJPARepository;
import com.example.jpa_h2.repository.PersonStockJPARepository;
import com.example.jpa_h2.repository.StockJPARepository;
import com.example.jpa_h2.repository.StockMongoRepository;
import com.example.jpa_h2.utils.FileParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
public class StockController {
    private PersonJPARepository personJPARepository;

    private StockJPARepository stockJPARepository;

    private PasswordEncoder passwordEncoder;

    private PersonStockJPARepository personStockJPARepository;

    private StockMongoRepository stockMongoRepository;

    public static HashMap<String, Double> cache;

    @Autowired
    public StockController(PersonJPARepository personJPARepository, PasswordEncoder passwordEncoder, StockJPARepository stockJPARepository, PersonStockJPARepository personStockJPARepository, StockMongoRepository stockMongoRepository) {
        this.personJPARepository = personJPARepository;
        this.passwordEncoder = passwordEncoder;
        this.stockJPARepository = stockJPARepository;
        this.personStockJPARepository = personStockJPARepository;
        this.stockMongoRepository = stockMongoRepository;
        cache = new HashMap<>();
    }

    @Scheduled(fixedRate = 10000)
    public void updateInfo() {

        synchronized (cache) {
            for (String symbol: cache.keySet()) {
                Stock stock = new Stock();
                Long id = stockMongoRepository.count()+1;
                stock.setId(id);
                stock.setSymbol(symbol);

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

    @GetMapping(value = "/api/stock/all")
    public ResponseEntity<?> allStocks() {
        JSONArray jsonArray = new JSONArray();
        for (String ticker: FileParser.tickerName.keySet()) {
            Stock s = stockMongoRepository.findTop1BySymbolOrderByTimeDesc(ticker);
            Double percent = 0.0;


            if (s != null) {
                Long now = s.getTime();
                Long dayAgo = now - 24 * 60 * 60 * 1000;
                Stock yesterday = stockMongoRepository.findTop1BySymbolAndTimeGreaterThanOrderByTimeAsc(ticker, dayAgo).get();
                percent = (s.getRegularMarketPrice() - yesterday.getRegularMarketPrice()) / yesterday.getRegularMarketPrice() * 100;
                //percent = Math.round(percent, 2);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("symbol", s.getSymbol());
                jsonObject.put("longName", s.getLongName());
                jsonObject.put("regularMarketPrice", s.getRegularMarketPrice());
                jsonObject.put("change", percent);
                jsonObject.put("description", "");
                jsonArray.put(jsonObject);
            }
        }
        return ResponseEntity.ok(jsonArray.toString());
    }

    @PostMapping(value = "/api/stock/favorites/add/{ticker}")
    public void addFavorite(@PathVariable String ticker, Principal principal) {
        StringBuilder sb = new StringBuilder();
        String username = principal.getName();
        Person person = personJPARepository.findByUsername(username).get();

        if (person != null) {
            List<PersonStock> personStocks = personStockJPARepository.findByPersonIdAndStockTicker(person.getId(), ticker);
            if (personStocks.size() == 0) {
                PersonStock personStock = new PersonStock();
                Stock stock = stockMongoRepository.findTop1BySymbolOrderByTimeDesc(ticker);
                personStock.setStockTicker(ticker);
                personStock.setPersonId(person.getId());
                personStockJPARepository.save(personStock);
            }
        }
    }

    @DeleteMapping(value = "/api/stock/favorites/delete/{ticker}")
    public void deleteFavorite(@PathVariable String ticker, Principal principal) {
        String username = principal.getName();
        Person person = personJPARepository.findByUsername(username).get();

        if (person != null) {
            PersonStock personStock = personStockJPARepository.findByPersonIdAndStockTicker(person.getId(), ticker).get(0);
            personStockJPARepository.delete(personStock);
        }
    }

    @GetMapping(value = "/api/stock/favorites")
    public ResponseEntity<?> getFavorites(Principal principal) {
        JSONArray jsonArray = new JSONArray();
        String username = principal.getName();
        Person person = personJPARepository.findByUsername(username).get();

        if (person != null) {
            Long personId = person.getId();
            List<PersonStock> personStocks = personStockJPARepository.findByPersonId(personId);
            for (PersonStock personStock: personStocks) {
                String ticker = personStock.getStockTicker();
                Stock newest = stockMongoRepository.findTop1BySymbolOrderByTimeDesc(ticker);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("symbol", newest.getSymbol());
                jsonObject.put("longName", newest.getLongName());
                jsonObject.put("regularMarketPrice", newest.getRegularMarketPrice());
                jsonObject.put("change", 0);
                jsonObject.put("description", "");
                jsonArray.put(jsonObject);
            }
        }
        return ResponseEntity.ok(jsonArray.toString());
    }

    @GetMapping(value = "/api/stock/{ticker}")
    public ResponseEntity<?> getInfoStock(@PathVariable String ticker) {
        StringBuilder sb = new StringBuilder();
        JSONObject jsonStock = new JSONObject();
        Stock stock = stockMongoRepository.findTop1BySymbolOrderByTimeDesc(ticker);
        if (stock != null) {
            Long now = stock.getTime();
            Long dayAgo = now - 24 * 60 * 60 * 1000;
            Stock yesterday = stockMongoRepository.findTop1BySymbolAndTimeGreaterThanOrderByTimeAsc(ticker, dayAgo).get();
            Double percent = (stock.getRegularMarketPrice() - yesterday.getRegularMarketPrice()) / yesterday.getRegularMarketPrice() * 100;
            String result = String.format("%.2f", percent);

            jsonStock.put("symbol", stock.getSymbol());
            jsonStock.put("longName", stock.getLongName());
            jsonStock.put("regularMarketPrice", stock.getRegularMarketPrice());
            jsonStock.put("change", percent);

            String url = "https://finnhub.io/api/v1/stock/profile2?symbol=" + ticker + "&token=c3jfck2ad3i82raod360";
            RestTemplate restTemplate = new RestTemplate();
            CompanyProfile companyProfile = restTemplate.getForObject(url, CompanyProfile.class);

            sb.append("Industry: " + companyProfile.getFinnhubIndustry() + "\n");
            sb.append("Country of company's headquater: " + companyProfile.getCountry() + "<br>");
            sb.append("Currency used in company filings: " + companyProfile.getCurrency() + "<br>");
            sb.append("IPO date: " + companyProfile.getIpo() + "<br>");
            sb.append("Market capitalization: " + companyProfile.getMarketCapitalization() + "<br>");
            sb.append("Company website: " + companyProfile.getWeburl() + "<br>");

            jsonStock.put("description", sb.toString());


        }
        return ResponseEntity.ok(jsonStock.toString());
    }
}
