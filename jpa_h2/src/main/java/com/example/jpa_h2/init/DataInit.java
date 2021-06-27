package com.example.jpa_h2.init;

import com.example.jpa_h2.repository.PersonCrudRepository; 
import com.example.jpa_h2.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
 
@Component
public class DataInit implements ApplicationRunner {
 
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
            
            Person p2 = new Person();
            p2.setFirstName("Anne");
            p2.setLastName("Hathaway");
            
            Person p3 = new Person();
            p3.setFirstName("Jessica");
            p3.setLastName("Chastain");
            
            Person p4 = new Person();
            p4.setFirstName("John");
            p4.setLastName("Smith");
            
            Person p5 = new Person();
            p5.setFirstName("Sam");
            p5.setLastName("Smith");
            
            personCrudRepository.save(p1);
            personCrudRepository.save(p2);
            personCrudRepository.save(p3);
            personCrudRepository.save(p4);
            personCrudRepository.save(p5);
        }
 
    }
     
}
