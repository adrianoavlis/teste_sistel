package br.com.sistelteste;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ScreenshotOnFailureExtension.class)
public class LoginTest implements WebDriverProvider {

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
@DisplayName("Login com credenciais válidas")
void deveLogarComSucesso() {
    LoginHelper.realizarLogin(driver, "administrador", "123456");

    try {
        Thread.sleep(1000);
    } catch (InterruptedException ignored) {}

    WebElement conectado = driver.findElement(By.xpath("//div[contains(@class,'pDiv') and contains(text(),'Conectado como')]"));
    Assertions.assertTrue(conectado.isDisplayed(), "Usuário deve estar logado com sucesso");
}


    @Test
    @DisplayName("Login com credenciais inválidas")
    void deveExibirMensagemDeErro() {
        driver.findElement(By.id("j_username")).sendKeys("invalido");
        driver.findElement(By.name("j_password")).sendKeys("senhaerrada");
        driver.findElement(By.cssSelector("input[type='submit'][value='Entrar']")).click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        WebElement erro = driver.findElement(By.xpath("//html/body/div[2]/div/div/div/form/div[1]"));
        Assertions.assertTrue(erro.getText().contains("Usuário e/ou senha inválida !"), "Mensagem de erro deve ser exibida");
        
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
