package com.example.BotForDuty.pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name =  "person")
public class Person {

    @Id
    private Long chatId;
    private String name;
    private int number_room;

    @Override
    public String toString() {
        return "Person{" +
                "chatId=" + chatId +
                ", name='" + name + '\'' +
                ", room_number=" + number_room +
                '}';
    }

    public String getInformPerson(){
        return "Имя: " + name + ", комната: " + number_room;
    }
}
