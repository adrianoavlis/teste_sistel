package br.com.sistelteste;

import br.com.sistelteste.extensions.ScreenshotOnFailureExtension;
import br.com.sistelteste.extensions.WebDriverProvider;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        driver = new ChromeDriver(options);
        driver.get(URL);
    }

 /*
    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://192.100.100.128:9393/Sistel_Previdenciario/");
    }
 */

    @Test
    @DisplayName("Login com credenciais válidas")
    void deveLogarComSucesso() {
        driver.findElement(By.id("j_username")).sendKeys("administrador");
        driver.findElement(By.name("j_password")).sendKeys("123456");
        driver.findElement(By.cssSelector("input[type='submit'][value='Entrar']")).click();

        // Aguarda DOM carregar parcialmente (poderia ser melhor com WebDriverWait)
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        WebElement conectado = driver.findElement(By.xpath("//div[contains(@class,'pDiv') and contains(text(),'Conectado como')]"));
        Assertions.assertTrue(conectado.isDisplayed(), "Usuário deve estar logado com sucesso");
    }

    @Test
    @DisplayName("Login com credenciais inválidas")
    void deveExibirMensagemDeErro() {
        driver.findElement(By.id("j_username")).sendKeys("invalido");
        driver.findElement(By.name("j_password")).sendKeys("senhaerrada");
        driver.findElement(By.cssSelector("input[type='submit'][value='Entrar']")).click();

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        WebElement erro = driver.findElement(By.xpath("//div[contains(@class,'alert-danger') and contains(text(),'Usuário e/ou senha inválida')]"));
        Assertions.assertTrue(erro.isDisplayed(), "Mensagem de erro deve ser exibida");
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
