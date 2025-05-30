package com.example.BotForDuty.service;
import com.example.BotForDuty.model.UserRepository;
import com.example.BotForDuty.pojo.Person;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RegisterUserService{

    @Autowired
    private UserRepository userRepository;
    private Map<Long, RegistrationState> UserState = new HashMap<>();


    private enum RegistrationState{
        AWAITING_ROOM_NUMBER
    }

    public boolean isUserRegistrationProcess(long chatId){
        return UserState.containsKey(chatId);
    }
    public String ProcessRoomNumber(Message msg){
        long chatId = msg.getChatId();
        String [] parts = msg.getText().trim().split(" ");

        if (parts.length != 2) {
            return "❌ Ошибка! Пожалуйста, введите имя и номер комнаты через пробел.\nПример: <code>Иван 305</code>";
        }

        try {
            int roomNum = Integer.parseInt(parts[1]);

            boolean isValidRoom = (roomNum >= 302 && roomNum <= 310) ||
                    (roomNum >= 312 && roomNum <= 320);

            if (!isValidRoom) {
                return "❌ Ошибка! Допустимые номера комнат: 302-310 и 312-320\n" +
                        "Комната 311 не доступна для регистрации";
            }

            String name = parts[0];
            UserState.remove(chatId);
            return registerUser(chatId, roomNum, name);
        } catch (NumberFormatException e) {
            return "❌ Неверный формат номера комнаты! Пожалуйста, введите цифры.\nПример: <code>305</code>";
        }
    }

    public String RegisterUser(Message msg){
        long chatId = msg.getChatId();
        if (userRepository.findById(chatId).isEmpty()){
            Person person = new Person();
            UserState.put(chatId, RegistrationState.AWAITING_ROOM_NUMBER);
            return "✨ <b>Регистрация нового пользователя</b> ✨\n\n" +
                    "Пожалуйста, введите ваше <b>Имя</b> и <b>Номер комнаты</b> через пробел.\n" +
                    "Пример: <code>Анна 307</code>";
        }else{
            return "ℹ️ Вы уже зарегистрированы в системе!";
        }
    }

    public String registerUser(long chatId, int number_room, String name){
        Person person = new Person();
        person.setName(name);
        person.setChatId(chatId);
        person.setNumber_room(number_room);
        userRepository.save(person);
        return "✅ <b>Регистрация успешно завершена!</b>\n\n" +
                "👤 <b>Имя:</b> " + name + "\n" +
                "🏠 <b>Комната:</b> " + number_room + "\n\n" +
                "Теперь вы можете пользоваться всеми функциями бота!";
    }

    public String getInformForUser(long chatId){
        if(userRepository.findById(chatId).isEmpty()){
            return "🔴 <b>Вы не зарегистрированы!</b>\n\n" +
                    "Для доступа к функциям бота выполните команду /register";
        }

        Optional<Person> userOptional = userRepository.findById(chatId);
        Person person = userOptional.get();
        String name = person.getName();
        int roomNumber = person.getNumber_room();

        String result = "📋 <b>Ваш профиль</b>\n\n" +
                "👤 <b>Имя:</b> " + name + "\n" +
                "🏠 <b>Комната:</b> " + roomNumber + "\n\n" +
                "Для изменения данных обратитесь к администратору.";
        return result;
    }
}


