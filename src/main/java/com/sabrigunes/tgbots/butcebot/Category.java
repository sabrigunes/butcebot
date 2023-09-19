package com.sabrigunes.tgbots.butcebot;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

public enum Category {
    YEMEK, MARKET, KIRA, FATURA, AIDAT, KREDI, BIRIKIM, VERGI, SIGORTA, SAGLIK, TATIL, ALISVERIS, GIYIM, MOBILYA,
    HEDIYE, PET, COCUK, KISISELBAKIM, EGITIM, ARABA, ULASIM, YAKIT, ETKINLIK;

    private final String keyword;
    private final boolean isIncome;

    Category(boolean isIncome) {
        this.keyword = createKeyword();
        this.isIncome = isIncome;
    }

    Category() {
        this(false);
    }


    public boolean isIncome() {
        return isIncome;
    }

    private String createKeyword() {
        return this.toString().toLowerCase().replace('ı', 'i');
    }

    public static String prepareKeyword(String keyword) {
        return keyword.toUpperCase().replace('İ', 'I');
    }

    public static String getCompareSummaryForCategoryLastMonth(long chat_id, Category category){
        int monthCompleted = getPercentOfMonthCompleted();
        double spentOnLastMonthForCategory = ButceBot.ms_database.getSpentLastMonthForCategory(chat_id, category.ordinal());
        double spentOnThisMonthForCategory = ButceBot.ms_database.spentOnThisMonthForCategory(chat_id, category.ordinal());
        int pencentage = (int)Math.floor(spentOnThisMonthForCategory*100/spentOnLastMonthForCategory);
        pencentage = pencentage > 100 ? pencentage - 100 : 100 - pencentage;
        if (spentOnLastMonthForCategory == 0)
            return "";
        String message = String.format("Ay başından bu yana %s kategorisinde toplam <b>%.2f TL</b> tutarında harcama yaptınız. ", category.toString().toLowerCase(), spentOnThisMonthForCategory);
        if (spentOnLastMonthForCategory < spentOnThisMonthForCategory)
            message += String.format("Geçen aya göre bu kategorideki harcamanız <b>%%%d</b> oranında <b>arttı.</b>", pencentage);
        else
            message += String.format("Geçen aya göre bu kategorideki harcamanız <b>%%%d</b> oranında <b>azaldı.</b>", pencentage);
        return message;
    }
    private static int getPercentOfMonthCompleted(){
        var today = LocalDate.now();
        return (int)Math.ceil((double)today.getDayOfMonth()*100/today.lengthOfMonth());
    }

}
