package com.example.BotForDuty.model;

import com.example.BotForDuty.pojo.Person;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Person, Long> {}
