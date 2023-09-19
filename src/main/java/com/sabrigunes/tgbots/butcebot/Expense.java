package com.sabrigunes.tgbots.butcebot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Expense {
    private long chatId;
    private long fromId;
    private long messageId;
    private int expenseId;
    private Category category;
    private double amount;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    public static final DateTimeFormatter dateFormatPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Expense(long chatId, long fromId, long messageId, Category category, double amount, String description) {
        this(chatId, fromId, messageId, -1, category, amount, description);
    }

    public Expense(long chatId, long fromId, long messageId, int expenseId, Category category, double amount, String description) {
        this.chatId = chatId;
        this.fromId = fromId;
        this.messageId = messageId;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public boolean save() {
        getNewExpenseId();
        return ButceBot.ms_database.saveExpense(this) > 0;
    }

    public boolean update(){
        updatedAt = LocalDateTime.now();
        getCurrentExpenseId();
        return ButceBot.ms_database.updateExpense(this) > 0;
    }


    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getFromId() {
        return fromId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public void setFromId(long fromId) {
        this.fromId = fromId;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public Category getCategory() {
        return category;
    }

    public int getCategoryId() {
        return category.ordinal();
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtAsString() {
        return createdAt.format(dateFormatPattern);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedAtAsString() {
        return updatedAt.format(dateFormatPattern);
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public String getDeletedAtAsString() {
        return deletedAt.format(dateFormatPattern);
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void getNewExpenseId() {
        expenseId = ButceBot.ms_database.getLastExpenseId(chatId) + 1;
    }

    public void getCurrentExpenseId(){
        expenseId = ButceBot.ms_database.getExpenseId(chatId, messageId);
    }

    public static double convertInputToAmount(String input) {
        input = input.trim().replace(",", ".");
        return Double.parseDouble(input);

    }


}
