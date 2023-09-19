package com.sabrigunes.tgbots.butcebot;

public class ResponseBuilder {
    private String firstName;
    private static final String START = "<i>Merhaba %s, ButceBot'a hoşgeldin.\n@ButceBot bir kişisel bütçe takip botudur ve tamamen ücretsizdir.\n\nBütçe botu kişisel olarak kullanmak istiyorsan bota mesaj atarak, birden fazla kişi ile kullanmak istiyorsan oluşturduğun gruba botu ekleyerek kullanabilirsin. Bot sadece komutlara odaklanacak ve grupta yapılan diğer konuşmalara müdahalede bulunmayacak şekilde geliştirildi.</i>";
    private static final String START_PART2 = "<i>Bot ile ilgili daha ayrıntılı bilgi almak istiyorsan /yardim komutunu gönderebilirsin.\n\n<b>Birikimlerinle hayallerini gerçekleştirmen dileğiyle...</b></i>";
    private static final String HELP = "<i>Merhaba %s,\nButceBot'u kullanman için gerekli tüm bilgiler ve açıklamaların bulunduğu kullanım kılavuzunu gönderiyorum. Kılavuzu inceleyerek bot hakkında detaylı bilgi edinebilirsin.</i>";
    private static final String AN_ERROR_OCCURED = "<i>Merhaba %s,\nÜzgünüm, bir hata oluştu. Lütfen daha sonra tekrar deneyin.</i>";
    private static final String EXPENSE_ID_PARSE_ERROR = "<i>Merhaba %s,\nHarcama kimliği kurallara uygun değil. Lütfen kontrol edip tekrar deneyin.</i>";
    private static final String EXPENSE_ID_NOT_FOUND = "<i>Merhaba %s,\nKomutunuzda harcama kimliği bulunmuyor. Lütfen kontrol edip tekrar deneyin.</i>";
    private static final String COMING_SOON = "<i>Merhaba %s,\nBu bölüm henüz geliştirme aşamasındadır. Anlayaşınız için teşekkürler.</i>";
    private static final String DELETE = "<i>Merhaba %s,\n#%d kimlik numaralı harcaman başarıyla silindi.</i>";
    private static final String EXPENSE_NOT_FOUND_FOR_DELETE = "<i>Merhaba %s,\n#%d kimlik numaralı harcama bulunamadı, lütfen kontrol edip tekrar deneyin.</i>";
    private static final String EXPENSE_NOT_FOUND_FOR_SUMMARY = "<i>Özet talep ettiğiniz ayda bota kaydedilmiş harcamanız bulunamadı.</i>";
    private static final String COMMAND_NOT_FOUND = "<i>Merhaba %s,\nGönderdiğin mesajda uygun komut bulunamadı.\nLütfen mesajınızı gözden geçirin veya dökümantasyonu inceleyin.</i>";
    private static final String AMOUNT_NOT_FOUND = "<i>Merhaba %s,\nGirdiğiniz komutta tutar bulunmuyor. Lütfen komutu gözden geçirin.</i>";
    private static final String INCORRECT_AMOUNT = "<i>Merhaba %s,\nGirdiğiniz tutar maalesef kurallara uygun değil. Lütfen komutu gözden geçirin.</i>";
    private static final String EXPENSE_SAVED = "<i>Merhaba %s,\n<b>%.2f TL</b> tutarındaki <b>%s</b> harcamanız kayıt edildi.\n#%d</i>";
    private static final String EXPENSE_UPDATED = "<i>Merhaba %s,\nharcamanız, <b>%.2f TL</b> tutarındaki <b>%s</b> harcaması olarak güncellendi.\n#%d</i>";


    public ResponseBuilder(String firstName) {
        setFirstName(firstName);
    }

    public void setFirstName(String firstName) {
        this.firstName = toCapitalize(firstName);
    }
    
    public String getStart() {
        return String.format(START, firstName);
    }

    public String getStartPart2() {
        return String.format(START_PART2);
    }

    public String getHelp() {
        return String.format(HELP, firstName);
    }

    public String getDelete(int expenseId) {
        return String.format(DELETE, firstName, expenseId);
    }

    public String getExpenseNotFoundForDelete(int expenseId) {
        return String.format(EXPENSE_NOT_FOUND_FOR_DELETE, firstName, expenseId);
    }

    public String getComingSoon() {
        return String.format(COMING_SOON, firstName);
    }

    public String getExpenseNotFoundForSummary() {
        return String.format(EXPENSE_NOT_FOUND_FOR_SUMMARY);
    }

    public String getCommandNotFound() {
        return String.format(COMMAND_NOT_FOUND, firstName);
    }

    public String getAmountNotFound() {
        return String.format(AMOUNT_NOT_FOUND, firstName);
    }

    public String getIncorrectAmount() {
        return String.format(INCORRECT_AMOUNT, firstName);
    }

    public String getExpenseSaved(double amount, String category, int expenseId) {
        return String.format(EXPENSE_SAVED, firstName, amount, category, expenseId);
    }

    public String getAnErrorOccured() {
        return String.format(AN_ERROR_OCCURED, firstName);
    }

    public String getExpenseUpdated(double amount, String category, int expenseId) {
        return String.format(EXPENSE_UPDATED, firstName, amount, category, expenseId);
    }

    public String getExpenseIdParseError(){
        return String.format(EXPENSE_ID_PARSE_ERROR, firstName);
    }
    public String getExpenseIdNotFound(){
        return String.format(EXPENSE_ID_NOT_FOUND, firstName);
    }


    static String toCapitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }


}
