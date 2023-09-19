package com.sabrigunes.tgbots.butcebot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.File;
import java.util.List;

public class Telegram {
    private static final TelegramBot bot = new TelegramBot("6293691290:AAF0dNIbFcdbsOQr_idM6DNtFGsIfkZ58v0");
    public static boolean sendMessage(long chatId, String text){
        var sm = new SendMessage(chatId, text);
        sm.parseMode(ParseMode.HTML);
        SendResponse response = bot.execute(sm);
        return response.isOk();
    }

    public static List<Update> getUpdates(int offset){
        GetUpdates getUpdates = new GetUpdates().limit(100).offset(offset).timeout(0);
        GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
        return updatesResponse.updates();
    }

    public static boolean sendFile(long chatId, String path){
        try{
            File file = new File(path);
            var sf = new SendDocument(chatId, file);

            SendResponse response = bot.execute(sf);
            return response.isOk();
        }
        catch (Exception ex)
        {
            throw ex;
        }

    }

}
