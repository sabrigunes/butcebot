package com.sabrigunes.tgbots.butcebot;

import com.pengrad.telegrambot.model.Update;
import com.sabrigunes.utils.DatabaseUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

public class Database {

    private DatabaseUtil ms_connection;

    public Database(String username, String password, String database) {
        ms_connection = new DatabaseUtil(username, password, database);
    }

    public int getLastUpdateId(){
        try{
            var x = ms_connection.fetchData("SELECT update_id FROM updates ORDER BY id DESC LIMIT 1;");
            if(x.next())
                return x.getInt(1);
        }
        catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
        return 0;
    }

    public void saveUpdate(Update update) {
        String query;
        // TODO: use prepareStatement.setString() etc.
        if(update.editedMessage() != null)
            query = String.format("INSERT INTO updates(update_id, message_id, from_id, from_isbot, from_firstname, " +
                            "from_username, chat_id, chat_firstname, chat_username, chat_type, message_date_utc, message_text) " +
                            "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
                    update.updateId(), update.editedMessage().messageId(), update.editedMessage().from().id(),
                    update.editedMessage().from().isBot() ? 1 : 0, update.editedMessage().from().firstName(), update.editedMessage().from().username(),
                    update.editedMessage().chat().id(), update.editedMessage().chat().firstName(), update.editedMessage().chat().username(),
                    update.editedMessage().chat().type(), new Date((long)update.editedMessage().date()*1000).toInstant(), update.editedMessage().text());
        else
            query = String.format("INSERT INTO updates(update_id, message_id, from_id, from_isbot, from_firstname, " +
                            "from_username, chat_id, chat_firstname, chat_username, chat_type, message_date_utc, message_text) " +
                            "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
                    update.updateId(), update.message().messageId(), update.message().from().id(),
                    update.message().from().isBot() ? 1 : 0, update.message().from().firstName(), update.message().from().username(),
                    update.message().chat().id(), update.message().chat().firstName(), update.message().chat().username(),
                    update.message().chat().type(), new Date((long)update.message().date()*1000).toInstant(), update.message().text());
        ms_connection.executeQuery(query);
    }

    public int saveExpense(Expense expense) {
        // TODO: use prepareStatement.setString() etc.
            String query = String.format("INSERT INTO expenses(chat_id,from_id,message_id,expense_id,category,amount,description,created_at) VALUES (%d,%d,%d,%d,%d, %f,'%s','%s');",expense.getChatId(),expense.getFromId(),expense.getMessageId(),expense.getExpenseId(), expense.getCategoryId(),expense.getAmount(),expense.getDescription(),expense.getCreatedAtAsString());
            return ms_connection.executeQuery(query);
    }

    public int updateExpense(Expense expense){
        // TODO: use prepareStatement.setString() etc.
        String query = String.format("UPDATE expenses SET category=%d, amount=%f,description='%s',updated_at='%s' WHERE chat_id=%d AND message_id=%d AND deleted_at IS NULL;"
                ,expense.getCategoryId(), expense.getAmount(), expense.getDescription(), expense.getUpdatedAtAsString(), expense.getChatId(), expense.getMessageId());
        return ms_connection.executeQuery(query);
    }

    public int getLastExpenseId(long chat_id){
        try{
            String query = String.format("SELECT expense_id FROM expenses WHERE chat_id=%d AND deleted_at IS NULL ORDER BY expense_id DESC LIMIT 1;", chat_id);
            var x = ms_connection.fetchData(query);
            if(x.next())
                return x.getInt(1);
        }
        catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
        return 0;
    }

    public int getExpenseId(long chat_id, long message_id){
        try{
            String query = String.format("SELECT expense_id FROM expenses WHERE chat_id=%d AND message_id=%d AND deleted_at IS NULL;", chat_id, message_id);
            var x = ms_connection.fetchData(query);
            if(x.next())
                return x.getInt(1);
        }
        catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
        return 0;
    }

    public int getSpentLastMonthForCategory(long chat_id, int category){
        try{
            var firstDayInLastMonth = LocalDateTime.now().withDayOfMonth(1).plusMonths(-1).withDayOfMonth(1).format(Expense.dateFormatPattern);
            var lastDayInLastMonth = LocalDateTime.now().withDayOfMonth(1).format(Expense.dateFormatPattern);;
            String query = String.format("SELECT sum(amount) FROM expenses WHERE chat_id=%d AND category=%d AND " +
                    "created_at >= '%s' AND created_at < '%s' AND deleted_at IS NULL;", chat_id, category, firstDayInLastMonth, lastDayInLastMonth);
            var x = ms_connection.fetchData(query);
            if(x.next())
                return x.getInt(1);
        }
        catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
        return 0;
    }

    public int spentOnThisMonthForCategory(long chat_id, int category){
        try{
            var firstDayInThisMonth = LocalDateTime.now().withDayOfMonth(1).format(Expense.dateFormatPattern);
            var lastDayInThisMonth = LocalDateTime.now().plusMonths(1).withDayOfMonth(1).format(Expense.dateFormatPattern);
            String query = String.format("SELECT sum(amount) FROM expenses WHERE chat_id=%d AND category=%d AND " +
                    "created_at >= '%s' AND created_at < '%s' AND deleted_at IS NULL;", chat_id, category, firstDayInThisMonth, lastDayInThisMonth);
            var x = ms_connection.fetchData(query);
            if(x.next())
                return x.getInt(1);
        }
        catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
        return 0;
    }

    public String getSummary(String firstName, long chat_id){
        var firstDayInThisMonth = LocalDateTime.now().withDayOfMonth(1).format(Expense.dateFormatPattern);
        var lastDayInThisMonth = LocalDateTime.now().plusMonths(1).withDayOfMonth(1).format(Expense.dateFormatPattern);
        return getSummary(firstName, chat_id, firstDayInThisMonth, lastDayInThisMonth);
    }

    public String getSummary(String firstName, long chat_id, String startDate, String endDate){
        try{
            String query = String.format("SELECT category,sum(amount) FROM expenses WHERE chat_id=%d AND created_at " +
                    "BETWEEN '%s' AND '%s' and deleted_at IS NULL GROUP BY category;", chat_id, startDate, endDate);
            var x = ms_connection.fetchData(query);
            return prepareSummaryMessage(firstName, startDate, x);
        }
        catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    private String prepareSummaryMessage(String firstName, String startDate, ResultSet resultSet){
        String [] months = new String[]{"", "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};

        try{
            var date = LocalDateTime.parse(startDate, Expense.dateFormatPattern);
            StringBuilder stringBuilder = new StringBuilder(String.format("<b>Harcama Özeti - %d %s</b>\n\n<code>", date.getYear(),months[date.getMonth().ordinal() + 1]));
            double total = 0;
            while(resultSet.next()){
                stringBuilder.append(String.format("%-10s %.2f TL\n", ResponseBuilder.toCapitalize(Category.values()[resultSet.getInt(1)].toString()), resultSet.getDouble(2)));
                total += resultSet.getDouble(2);
            }
            stringBuilder.append(String.format("\n%-10s %.2f TL\n", "Toplam", total));
            if (total == 0)
                return new ResponseBuilder("").getExpenseNotFoundForSummary();
            return stringBuilder.append("</code>").toString();
        }
        catch (SQLException ex){
            throw new RuntimeException(ex.getMessage());
        }

    }

    public boolean isExpenseCreated(long chatId, int expenseId){
        try{
            String query = String.format("SELECT * FROM expenses WHERE chat_id=%d AND expense_id=%d AND deleted_at IS NULL;", chatId, expenseId);
            System.out.println(query);
            var x = ms_connection.fetchData(query);
            return x.next();
        }
        catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void deleteExpense(long chatId, int expenseId){
        // TODO: use prepareStatement.setString() etc.
        String query = String.format("UPDATE expenses SET deleted_at='%s' WHERE chat_id=%d AND expense_id=%d", LocalDateTime.now().format(Expense.dateFormatPattern), chatId, expenseId);
        System.out.println(query);
        ms_connection.executeQuery(query);
    }






}
