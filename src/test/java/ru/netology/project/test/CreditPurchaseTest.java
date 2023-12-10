package ru.netology.project.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.project.data.DataUtils;
import ru.netology.project.data.DatabaseHelper;
import ru.netology.project.page.CreditPurchase;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.project.data.DatabaseHelper.getOrderCount;

public class CreditPurchaseTest {

    private CreditPurchase buyInCredit;

    String url = System.getProperty("sut.url");

    @BeforeEach
    public void openPage() {
        open(url);
        buyInCredit = new CreditPurchase();
        buyInCredit.buyCredit();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @AfterEach
    public void cleanDataBase() {
        DatabaseHelper.cleanDatabase();
    }

    @Test
    @DisplayName("01_Карта одобрена (статус APPROVED)")
    public void shouldSuccessfullyPurchase() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.buySuccess();
        assertEquals("APPROVED", DatabaseHelper.getCreditStatus());
    }

    @Test
    @DisplayName("02_Карта отклонена (статус DECLINED)")
    public void shouldUnsuccessfullyPurchase() {
        buyInCredit.setCardNumber(DataUtils.getDeclinedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.buyError();
        assertEquals("DECLINED", DatabaseHelper.getCreditStatus());
    }

    @Test
    @DisplayName("03_Не заполнен номер карты")
    public void shouldErrorEmptyCardNumber() {
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("04_Карта одобрена (статус APPROVED), не заполнен месяц")
    public void shouldErrorEmptyMonth() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("05_Карта одобрена (статус APPROVED), не заполнен год")
    public void shouldErrorEmptyYear() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("06_Карта одобрена (статус APPROVED), не заполнен Владелец")
    public void shouldErrorEmptyCardHolder() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.incorrectFormatHidden();
        buyInCredit.fieldNecessarily();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("07_Карта одобрена (статус APPROVED), не заполнен код CVC")
    public void shouldErrorEmptyCvc() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("08_Не корректный номер карты")
    public void shouldErrorInvalidCardNumber() {
        buyInCredit.setCardNumber(DataUtils.getCardNumber15Digits());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("09_Карта одобрена (статус APPROVED), срок карты истёк")
    public void shouldErrorExpiredCard() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getYearNumberLessCurrentYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormatHidden();
        buyInCredit.cardExpired();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("10_Карта одобрена (статус APPROVED), не валидный месяц")
    public void  shouldErrorInvalidMonth() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.get00());
        buyInCredit.setCardYear(DataUtils.getCurrentYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormatHidden();
        buyInCredit.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("11_Карта одобрена (статус APPROVED), некорректный месяц")
    public void  shouldErrorIncorrectMonth() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.get1Digit());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("12_Карта одобрена (статус APPROVED), некорректный год")
    public void shouldErrorIncorrectYear() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.get1Digit());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("13_Карта одобрена (статус APPROVED), превышен срок карты")
    public void shouldErrorExceededCardDeadline() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getYearsAfterEndOfExpiration());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormatHidden();
        buyInCredit.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("14_Карта одобрена (статус APPROVED), некорректный Владелец")
    public void  shouldErrorInvalidCardHolder() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholderWithCyrillic());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("15_Карта одобрена (статус APPROVED), короткое имя Владельца")
    public void shouldErrorShotNameCardHolder() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getShotName());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("16_Карта одобрена (статус APPROVED), длинное имя Владельца")
    public void shouldErrorLongNameCardHolder() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getLongName());
        buyInCredit.setCardCvv(DataUtils.get3Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("17_Карта одобрена (статус APPROVED), некорректный код CVC - нули")
    public void shouldErrorInvalidCvcNulls() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get000());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("18_Карта одобрена (статус APPROVED), некорректный код CVC - 2 цифры")
    public void  shouldErrorInvalidCvcTwoDigits() {
        buyInCredit.setCardNumber(DataUtils.getApprovedCard());
        buyInCredit.setCardMonth(DataUtils.getMonthNumber());
        buyInCredit.setCardYear(DataUtils.getValidYear());
        buyInCredit.setCardholder(DataUtils.getNameCardholder());
        buyInCredit.setCardCvv(DataUtils.get2Digits());
        buyInCredit.clickContinueButton();
        buyInCredit.fieldNecessarilyHidden();
        buyInCredit.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

}
