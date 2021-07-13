package com.example.jpa_h2.security;

import com.example.jpa_h2.entity.Person;
import com.example.jpa_h2.repository.PersonJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PersonJPARepository personJPARepository;

    @Autowired
    public UserDetailsServiceImpl(PersonJPARepository personJPARepository) {
        this.personJPARepository = personJPARepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personJPARepository.findByUsername(username).orElseThrow(() ->new UsernameNotFoundException("User doesn't exist"));
        return SecurityUser.fromPerson(person);
    }
}
