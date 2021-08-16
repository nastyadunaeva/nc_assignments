package com.example.jpa_h2.controller;

import com.example.jpa_h2.entity.Person;
import com.example.jpa_h2.repository.PersonJPARepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class PersonController {
    private PersonJPARepository personJPARepository;

    private PasswordEncoder passwordEncoder;

    public static HashMap<String, Double> cache;

    @Autowired
    public PersonController(PersonJPARepository personJPARepository, PasswordEncoder passwordEncoder) {
        this.personJPARepository = personJPARepository;
        this.passwordEncoder = passwordEncoder;
        cache = new HashMap<>();
    }

    @RequestMapping(value = "/api/all", method = RequestMethod.GET)
    public ResponseEntity<?> all() {
        Iterable<Person> all = personJPARepository.findAll();
        JSONArray jsonArray = new JSONArray();
        for (Person p : all) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstName", p.getFirstName());
            jsonObject.put("lastName", p.getLastName());
            jsonObject.put("username", p.getUsername());
            jsonObject.put("role", p.getRole().name());
            jsonArray.put(jsonObject);
        }
        return ResponseEntity.ok(jsonArray.toString());
    }

    @GetMapping("/api/{id}")
    public String index(@PathVariable long id) {
        Long i = new Long(id);
        Iterable<Person> all = personJPARepository.findByIdLike(i);
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + " " + p.getUsername() + " " + p.getRole().name() + "<br>"));
        return sb.toString();
    }

    @GetMapping("/api/name/{lastName}")
    public String lastName(@PathVariable String lastName) {
        Iterable<Person> all = personJPARepository.findByLastNameLike(lastName);
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + " " + p.getUsername() + " " + p.getRole().name() + "<br>"));
        return sb.toString();
    }

    @PostMapping("/api/add")
    public String add(@RequestBody Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personJPARepository.save(person);
        return person.getFirstName() + " " + person.getLastName() + " was added to the database";
    }

    @DeleteMapping("/api/delete/{id}")
    public String delete(@PathVariable Long id) {
        personJPARepository.deleteById(id);
        return "Person was removed from the database";
    }
}
