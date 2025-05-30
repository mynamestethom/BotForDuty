package com.example.BotForDuty.service;

import com.example.BotForDuty.config.BotConfig;
import com.example.BotForDuty.pojo.Person;
import com.example.BotForDuty.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    private final BotConfig botConfig;
    private final RegisterUserService registerService;


    public TelegramBot(BotConfig botConfig, RegisterUserService registerService) {
        this.botConfig = botConfig;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start" , "Start telegram bot"));
        listofCommands.add(new BotCommand("/register" , "Register user in bot"));
        listofCommands.add(new BotCommand("/help" , "Give command bot"));
        listofCommands.add(new BotCommand("/info" , "Give info for user"));
        listofCommands.add(new BotCommand("/duty" , "Get duty schedule"));
        try{
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault() , null));
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
        this.registerService = registerService;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if( update.hasMessage() &&  update.getMessage().hasText() ){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if(registerService.isUserRegistrationProcess(chatId)){
                String responce = registerService.ProcessRoomNumber(update.getMessage());
                sendMessage(chatId , responce);
                return;
            }
            switch (messageText){
                case "/start":
                    informUser(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/register":
                    String regMessage = registerService.RegisterUser(update.getMessage());
                    sendMessage(chatId,regMessage);
                    break;
                case "/info":
                    String infoMessage = registerService.getInformForUser(update.getMessage().getChatId());
                    sendMessage(chatId,infoMessage);
                    break;
                case "/help":
                    sendHellpMes(chatId);
                    break;
                case "/duty":
                    sendSchedulePhoto(chatId);
                    break;
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    public void registerUser(Message msg){
        if(userRepository.findById(msg.getChatId()).isEmpty()){
            Person person = new Person();
            var chatId = msg.getChatId();
            person.setName(msg.getChat().getUserName());
            person.setChatId(chatId);
            person.setNumber_room(0);
            userRepository.save(person);
        }
    }

    public void informUser(long chatId, String name){
        String answer = "👋 Привет, " + name + "!\n\n" +
                "✨ Добро пожаловать в официального бота для жителей \n" +
                "🏠 <b>Общежития №5 СГ 1 (3 этаж) ТЕСТ РЕЖИМ</b>!\n\n" +
                "Здесь ты сможешь:\n" +
                "• 📅 Узнать о своих дежурствах на кухне\n" +
                "• 🔔 Получать напоминания о предстоящих уборках\n" +
                "• ℹ️ Быть в курсе важных объявлений этажа\n\n" +"" +
                "<b>Узнать все возможности бота по команде <code>/help</code></b>\n\n" +
                "Бот создан, чтобы сделать нашу жизнь в общежитии более организованной и комфортной!";
        sendMessage(chatId,answer);
    }

    public void sendHellpMes(long chatId){
        String helpText = "ℹ️ <b>Доступные команды бота</b>:\n\n" +
                "🏠 <b>Общежитие №5 СГ 1 (3 этаж)</b>\n\n" +
                "▫️ <code>/start</code> – Начать работу с ботом\n" +
                "▫️ <code>/register</code> – 📝 Зарегистрироваться (для новых жителей)\n" +
                "▫️ <code>/myinfo</code> – 👤 Проверить свои данные\n" +
                "▫️ <code>/help</code> – ❓ Список всех команд\n\n" +
                "<i>По всем вопросам обращайтесь к старосте этажа!</i>";
        sendMessage(chatId,helpText);
    }

    public void sendMessage(long chatId, String textSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textSend);
        message.setParseMode("HTML");
        executeMess(message);
    }

    private void sendSchedulePhoto(long chatId) {
        try {
            ClassPathResource resource = new ClassPathResource("duty_schedule.png");
            InputStream inputStream = resource.getInputStream();

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(chatId));
            sendPhoto.setPhoto(new InputFile(inputStream, "schedule.jpg"));
            sendPhoto.setCaption("📅 График дежурств на кухне");

            execute(sendPhoto);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "⚠️ Не удалось загрузить график дежурств. Попробуйте позже.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMessage(chatId, "⚠️ Ошибка при отправке фото. Попробуйте позже.");
        }
    }


    public void executeMess(SendMessage message){
        try{
            execute(message);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
