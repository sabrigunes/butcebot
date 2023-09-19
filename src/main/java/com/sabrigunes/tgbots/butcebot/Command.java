package com.sabrigunes.tgbots.butcebot;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class Command {
    private final String m_command;
    private static final String[] OTHER_COMMANDS = new String[]{"start", "ozet", "yardim", "sil", "rapor"};
    private String m_param0;
    private Category m_category;
    private String m_param1;
    private String m_param2;

    public Command(String command) {
        if (!command.startsWith("/"))
            throw new IllegalArgumentException("The command is not in a suitable format for processing.");

        m_command = command;
        System.out.println(m_command);

        parseCommand();
    }

    public void execute(long chat_id, long from_id, long message_id, String firstname, boolean isEditedMessage) {
        if (m_category == null) {
            workForOtherCommands(chat_id, firstname);
        } else {
            String message;
            var expense = new Expense(chat_id, from_id, message_id, m_category, Expense.convertInputToAmount(m_param1), m_param2);
            if (isEditedMessage) {
                if (expense.update())
                    message = new ResponseBuilder(firstname).getExpenseUpdated(expense.getAmount(), expense.getCategory().toString().toLowerCase(), expense.getExpenseId());
                else
                    message = new ResponseBuilder(firstname).getAnErrorOccured();
            } else {
                if (expense.save())
                    message = new ResponseBuilder(firstname).getExpenseSaved(expense.getAmount(), expense.getCategory().toString().toLowerCase(), expense.getExpenseId());
                else
                    message = new ResponseBuilder(firstname).getAnErrorOccured();

            }

            Telegram.sendMessage(chat_id, message);
        }

    }


    public void execute(long chat_id, long from_id, long message_id, String firstname) {
        execute(chat_id, from_id, message_id, firstname, false);
    }

    private void parseCommand() {
        m_param0 = m_command.toLowerCase().split(" ")[0].replace("/", "");
        if (isCommand()) {
            String[] tmp = m_command.split(" ");
            if (tmp.length > 1)
                m_param1 = tmp[1];
            else if (m_category != null)
                throw new NoSuchElementException(); // no amount found in message content
            if (tmp.length > 2) {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < tmp.length; ++i) {
                    sb.append(tmp[i]).append(" ");
                }
                m_param2 = sb.substring(0, sb.length() - 1);

            }
        } else
            throw new UnsupportedOperationException("The command is not in a suitable format for processing.");


    }

    private boolean isCommand() {
        boolean res0 = isCommandTypeACategory();
        boolean res1 = Arrays.asList(OTHER_COMMANDS).contains(m_param0);
        return res0 || res1;
    }

    private boolean isCommandTypeACategory() {
        try {
            m_category = Category.valueOf(Category.prepareKeyword(m_param0));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void workForOtherCommands(long chat_id, String firstname) {
        if (m_param0.equals(OTHER_COMMANDS[0])) // /start
            workForStart(chat_id, firstname);
        else if (m_param0.equals(OTHER_COMMANDS[1])) // /ozet
            workForSummary(chat_id, firstname);
        else if (m_param0.equals(OTHER_COMMANDS[2])) // /yardim
            workForHelp(chat_id, firstname);
        else if (m_param0.equals(OTHER_COMMANDS[3])) // /sil
            workForDelete(chat_id, firstname);
        else if (m_param0.equals(OTHER_COMMANDS[4])) // /rapor
            workForReport(chat_id, firstname);


    }

    private void workForStart(long chat_id, String firstname) {
        String msg = new ResponseBuilder(firstname).getStart();
        Telegram.sendMessage(chat_id, msg);

        String msg2 = new ResponseBuilder(firstname).getStartPart2();
        Telegram.sendMessage(chat_id, msg2);
    }

    private void workForSummary(long chat_id, String firstname) {
        String summary;
        if (m_param1 == null || m_param2 == null)
            summary = ButceBot.ms_database.getSummary(firstname, chat_id);
        else {
            int year = Integer.parseInt(m_param1);
            int month = Integer.parseInt(m_param2);
            summary = ButceBot.ms_database.getSummary(firstname, chat_id, String.format("%d-%02d-01 00:00:00", year, month), String.format("%d-%02d-01 00:00:00", year, month + 1));
        }
        Telegram.sendMessage(chat_id, summary);
    }

    private void workForHelp(long chat_id, String firstname) {
        String msg = new ResponseBuilder(firstname).getHelp();
        Telegram.sendMessage(chat_id, msg);

        Telegram.sendFile(chat_id, "S:\\Projeler\\ProjectDocuments\\ButceBotManual.pdf");
    }

    private void workForDelete(long chat_id, String firstname) {
        try {
            if(m_param1 == null || m_param1.isEmpty()){
                String msg = new ResponseBuilder(firstname).getExpenseIdNotFound();
                Telegram.sendMessage(chat_id, msg);
                return;
            }

            int expenseId = Integer.parseInt(m_param1.replace("#", ""));
            String msg;
            if (ButceBot.ms_database.isExpenseCreated(chat_id, expenseId)) {
                ButceBot.ms_database.deleteExpense(chat_id, expenseId);
                msg = new ResponseBuilder(firstname).getDelete(expenseId);
            } else
                msg = new ResponseBuilder(firstname).getExpenseNotFoundForDelete(expenseId);

            Telegram.sendMessage(chat_id, msg);
        } catch (NumberFormatException ex) {
                String msg = new ResponseBuilder(firstname).getExpenseIdParseError();
                Telegram.sendMessage(chat_id, msg);
        }

    }

    private void workForReport(long chat_id, String firstname) {
        String msg = new ResponseBuilder(firstname).getComingSoon();
        Telegram.sendMessage(chat_id, msg);

    }
}
