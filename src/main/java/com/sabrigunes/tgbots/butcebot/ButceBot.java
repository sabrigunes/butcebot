package com.sabrigunes.tgbots.butcebot;

import com.pengrad.telegrambot.model.Update;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class ButceBot {
    static final Database ms_database;
    private static int ms_offset = -1;

    static final Language language = Language.TR;

    static {
        ms_database = new Database("postgres", "Aa10203040**", "butcebot");
        if (ms_offset == -1)
            ms_offset = ms_database.getLastUpdateId() + 1;
    }

    public static void main(String[] args) {
        ButceBot.run();
    }

    public static void run() {
        try{
            while (true) {
                workForUpdates();
                Thread.sleep(1000);
            }
        }
        catch (Exception ex){
            System.err.println(ex.getMessage());
            run();
        }

    }

    private static void workForUpdates() {
        var updates = Telegram.getUpdates(ms_offset);

        for (Update update : updates) {
            if (update.editedMessage() != null) {
                workForAUpdate(update, true);
            } else if (update.message() == null || update.message().sticker() != null)
                continue;
            else
                workForAUpdate(update);
        }

    }

    private static void workForAUpdate(Update update) {
        workForAUpdate(update, false);
    }

    public static void workForAUpdate(Update update, boolean isEditedMessage) {
        ms_database.saveUpdate(update); // for edited messages
        try {
            if (isEditedMessage) {
                Command command = new Command(update.editedMessage().text());
                command.execute(update.editedMessage().chat().id(), update.editedMessage().from().id(), update.editedMessage().messageId(), update.editedMessage().from().firstName(), true);
            } else {
                Command command = new Command(update.message().text());
                command.execute(update.message().chat().id(), update.message().from().id(), update.message().messageId(), update.message().from().firstName());
            }
        } catch (NumberFormatException ex) {  // amount entry is incorrect, it could not be parsed into double.
            var msg = new ResponseBuilder(update.editedMessage() == null ? update.message().from().firstName() : update.editedMessage().from().firstName()).getIncorrectAmount();
            Telegram.sendMessage(update.message().chat().id(), msg);
        } catch (NoSuchElementException ex) { // no amount found in message content
            var msg = new ResponseBuilder(update.editedMessage() == null ? update.message().from().firstName() : update.editedMessage().from().firstName()).getAmountNotFound();
            Telegram.sendMessage(update.message().chat().id(), msg);
        } catch (UnsupportedOperationException ex) { // an order that is not in the list was entered
            var msg = new ResponseBuilder(update.editedMessage() == null ? update.message().from().firstName() : update.editedMessage().from().firstName()).getCommandNotFound();
            Telegram.sendMessage(update.message().chat().id(), msg);
        } catch (IllegalArgumentException ex) { // there is no order in the message
            ;
        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        } finally {
            ms_offset += 1;
        }
    }


}
