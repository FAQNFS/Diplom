package ru.netology.project.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.project.data.DataUtils;
import ru.netology.project.data.DatabaseHelper;
import ru.netology.project.page.PurchasePage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.project.data.DatabaseHelper.getOrderCount;

public class CheckoutTest {

    private PurchasePage buy;
    String url = System.getProperty("sut.url");

    @BeforeEach
    public void openPage() {
        open(url);
        buy = new PurchasePage();
        buy.buyCard();

    }

    @BeforeAll
    static void setAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDown() {
        SelenideLogger.removeListener("allure");
    }

    @AfterEach
    public void cleanDataBase() {
        DatabaseHelper.cleanDatabase();
    }

    @Test
    @DisplayName("01_Карта одобрена (статус APPROVED)")
    public void shouldSuccessfulPurchase() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.buySuccess();
        assertEquals("APPROVED", DatabaseHelper.getPaymentStatus());
    }

    @Test
    @DisplayName("02_Карта отклонена (статус DECLINED)")
    public void shouldUnsuccessfulPurchase() {
        buy.setCardNumber(DataUtils.getDeclinedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.buyError();
        assertEquals("DECLINED", DatabaseHelper.getPaymentStatus());
    }

    @Test
    @DisplayName("03_Не заполнен номер карты")
    public void shouldErrorEmptyCardNumber() {
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("04_Карта одобрена (статус APPROVED), не заполнен месяц")
    public void shouldErrorEmptyMonth() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("05_Карта одобрена (статус APPROVED), не заполнен год")
    public void shouldErrorEmptyYear() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("06_Карта одобрена (статус APPROVED), не заполнен Владелец")
    public void shouldErrorEmptyCardHolder() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.incorrectFormatHidden();
        buy.fieldNecessarily();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("07_Карта одобрена (статус APPROVED), не заполнен код CVC")
    public void shouldErrorEmptyCvc() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("08_Не корректный номер карты")
    public void shouldErrorIncorrectCardNumber() {
        buy.setCardNumber(DataUtils.getCardNumber15Digits());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("09_Карта одобрена (статус APPROVED), срок карты истёк")
    public void shouldErrorCardExpired() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumberLessThanThisMonth());
        buy.setCardYear(DataUtils.getCurrentYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormatHidden();
        buy.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("10_Карта одобрена (статус APPROVED), не валидный месяц")
    public void shouldErrorMonthInvalid() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getNumberFrom13To99());
        buy.setCardYear(DataUtils.getCurrentYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormatHidden();
        buy.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("11_Карта одобрена (статус APPROVED), некорректный месяц")
    public void shouldErrorMonthIncorrect() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.get1Digit());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("12_Карта одобрена (статус APPROVED), некорректный год")
    public void shouldErrorYearIncorrect() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.get1Digit());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("13_Карта одобрена (статус APPROVED), превышен срок карты")
    public void shouldErrorDeadlineExceeded() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getYearsAfterEndOfExpiration());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormatHidden();
        buy.cardExpirationError();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("14_Карта одобрена (статус APPROVED), некорректный Владелец")
    public void shouldErrorIncorrectCardHolder() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getIncorrectCardHolder());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("15_Карта одобрена (статус APPROVED), короткое имя Владельца")
    public void shouldErrorShotNameCardHolder() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getShotName());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("16_Карта одобрена (статус APPROVED), длинное имя Владельца")
    public void shouldErrorLongNameCardHolder() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getLongName());
        buy.setCardCvv(DataUtils.get3Digits());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("17_Карта одобрена (статус APPROVED), некорректный код CVC - нули")
    public void shouldErrorCvcSetNulls() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get000());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

    @Test
    @DisplayName("18_Карта одобрена (статус APPROVED), некорректный код CVC - 1 цифра")
    public void shouldErrorCvcSetTwoDigit() {
        buy.setCardNumber(DataUtils.getApprovedCard());
        buy.setCardMonth(DataUtils.getMonthNumber());
        buy.setCardYear(DataUtils.getValidYear());
        buy.setCardholder(DataUtils.getNameCardholder());
        buy.setCardCvv(DataUtils.get1Digit());
        buy.clickContinueButton();
        buy.fieldNecessarilyHidden();
        buy.incorrectFormat();
        assertEquals(0, getOrderCount());
    }

}
