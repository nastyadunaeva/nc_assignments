package com.example.jpa_h2.init;

import com.example.jpa_h2.model.Role;
import com.example.jpa_h2.repository.PersonCrudRepository;
import com.example.jpa_h2.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
 
@Component
public class DataInit implements ApplicationRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PersonCrudRepository personCrudRepository;
 
    @Autowired
    public DataInit(PersonCrudRepository personCrudRepository) {
        this.personCrudRepository = personCrudRepository;
    }
 
    @Override
    public void run(ApplicationArguments args) throws Exception {
        long count = personCrudRepository.count();
 
        if (count == 0) {
            Person p1 = new Person();
            p1.setFirstName("Matthew");
            p1.setLastName("McConaughey");
            p1.setUsername("mch");
            p1.setPassword(passwordEncoder.encode("mch"));
            
            Person p2 = new Person();
            p2.setFirstName("John");
            p2.setLastName("Smith");
            p2.setUsername("agentSmth1");
            p2.setPassword(passwordEncoder.encode("agent"));
            
            Person p3 = new Person();
            p3.setFirstName("Sam");
            p3.setLastName("Smith");
            p3.setUsername("agentSmth2");
            p3.setPassword(passwordEncoder.encode("agent"));

            Person p4 = new Person();
            p4.setFirstName("Admin");
            p4.setLastName("Adminov");
            p4.setUsername("admin");
            p4.setPassword(passwordEncoder.encode("admin"));
            p4.setRole(Role.ADMIN);
            
            personCrudRepository.save(p1);
            personCrudRepository.save(p2);
            personCrudRepository.save(p3);
            personCrudRepository.save(p4);
        }
 
    }
     
}
