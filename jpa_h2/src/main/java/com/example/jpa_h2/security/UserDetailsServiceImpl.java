package com.example.jpa_h2.security;

import com.example.jpa_h2.entity.Person;
import com.example.jpa_h2.model.Permission;
import com.example.jpa_h2.repository.PersonCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PersonCrudRepository personCrudRepository;

    @Autowired
    public UserDetailsServiceImpl(PersonCrudRepository personCrudRepository) {
        this.personCrudRepository = personCrudRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personCrudRepository.findByUsername(username).orElseThrow(() ->new UsernameNotFoundException("User doesn't exist"));
        return SecurityUser.fromPerson(person);
    }
}
