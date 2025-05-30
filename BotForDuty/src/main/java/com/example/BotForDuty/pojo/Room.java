package com.example.BotForDuty.pojo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Room {
    Map<Integer, Person> personMap = new HashMap<>();

    public Room(int number, Person person){
        personMap.put(number,person);
    }
}
