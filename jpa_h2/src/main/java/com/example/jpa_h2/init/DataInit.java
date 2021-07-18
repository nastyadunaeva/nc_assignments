package com.example.jpa_h2.init;

import com.example.jpa_h2.entity.Cat;
import com.example.jpa_h2.model.Role;
import com.example.jpa_h2.entity.Person;
import com.example.jpa_h2.repository.CatsRepository;
import com.example.jpa_h2.repository.PersonJPARepository;
import com.example.jpa_h2.repository.StockJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements ApplicationRunner {

    private PasswordEncoder passwordEncoder;

    private PersonJPARepository personJPARepository;

    private StockJPARepository stockJPARepository;

    private CatsRepository catsRepository;
 
    @Autowired
    public DataInit(PersonJPARepository personJPARepository, PasswordEncoder passwordEncoder, StockJPARepository stockJPARepository, CatsRepository catsRepository) {
        this.personJPARepository = personJPARepository;
        this.passwordEncoder = passwordEncoder;
        this.stockJPARepository = stockJPARepository;
        this.catsRepository = catsRepository;
    }
 
    @Override
    public void run(ApplicationArguments args) throws Exception {
        long count = personJPARepository.count();
 
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
            
            personJPARepository.save(p1);
            personJPARepository.save(p2);
            personJPARepository.save(p3);
            personJPARepository.save(p4);
        }

        /*if (catsRepository.count() == 0) {
            Cat cat = new Cat();
            Long id = new Long(1);
            cat.setId(id);
            cat.setName("Murka");
            catsRepository.save(cat);
        }*/
    }
     
}
