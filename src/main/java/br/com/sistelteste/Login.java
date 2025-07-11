package br.com.sistelteste;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ScreenshotOnFailureExtension.class)
public class Login implements WebDriverProvider {

    private WebDriver driver;
    private final String URL = "http://192.100.100.128:9393/Sistel_Previdenciario/";

    @BeforeAll
    void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get(URL);
    }

    @Test
    @DisplayName("Login reutilizando método do LoginTest")
    void login() {
        LoginHelper.realizarLogin(driver, "administrador", "123456");


        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        WebElement conectado = driver.findElement(
            By.xpath("//div[contains(@class,'pDiv') and contains(text(),'Conectado como')]")
        );

        Assertions.assertTrue(conectado.isDisplayed(), "Usuário deve estar logado com sucesso");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Override
    public WebDriver getWebDriver() {
        return this.driver;
    }
}
