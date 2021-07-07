package com.example.jpa_h2.controller;

import com.example.jpa_h2.entity.NumberInfo;
import com.example.jpa_h2.entity.Person;
import com.example.jpa_h2.repository.NumberJPARepository;
import com.example.jpa_h2.repository.PersonJPARepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class MainController {

    private PersonJPARepository personJPARepository;

    private NumberJPARepository numberJPARepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public MainController(PersonJPARepository personJPARepository, PasswordEncoder passwordEncoder, NumberJPARepository numberJPARepository) {
        this.personJPARepository = personJPARepository;
        this.passwordEncoder = passwordEncoder;
        this.numberJPARepository = numberJPARepository;
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
    @GetMapping("/get")
    public void getInfo() {
        RestTemplate restTemplate = new RestTemplate();
        int number =  (int)Math.round(Math.random() * 1000);

        String url = "http://numbersapi.com/" + number;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String body = responseEntity.getBody();

        NumberInfo num = new NumberInfo();
        num.setNumber(number);
        num.setInfo(body);
        numberJPARepository.save(num);
    }

    @GetMapping(value = "/numbers")
    public String allNumbers() {
        Iterable<NumberInfo> all = numberJPARepository.findAll();
        StringBuilder sb = new StringBuilder();
        all.forEach(n -> sb.append(n.getInfo() + "<br>"));
        return sb.toString();
    }
}
