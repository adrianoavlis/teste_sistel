package br.com.sistelteste;

import br.com.sistelteste.extensions.ScreenshotOnFailureExtension;
import br.com.sistelteste.extensions.WebDriverProvider;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ScreenshotOnFailureExtension.class)
public class RelacaoBeneficosReajustadosTest implements WebDriverProvider {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://192.100.100.128:9393/Sistel_Previdenciario/";

    @BeforeAll
    void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

@BeforeEach
void setup() {
    ChromeOptions options = new ChromeOptions();

    // Desabilita o gerenciamento de senhas e alertas
    options.addArguments("--disable-notifications");
    options.addArguments("--disable-popup-blocking");
    options.addArguments("--disable-save-password-bubble");
    options.addArguments("--disable-features=PasswordManagerEnabled,PasswordCheck");

    // Define preferências diretamente
    Map<String, Object> prefs = new HashMap<>();
    prefs.put("credentials_enable_service", false); // Desativa serviço de credenciais
    prefs.put("profile.password_manager_enabled", false); // Desativa o gerenciador de senhas

    options.setExperimentalOption("prefs", prefs);

    driver = new ChromeDriver(options);
    driver.manage().window().maximize();
    wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    driver.get(BASE_URL);
}

    /*
     * @BeforeEach
     * void setup() {
     * ChromeOptions options = new ChromeOptions();
     * options.addArguments("--headless=new");
     * options.addArguments("--window-size=1920,1080");
     * options.addArguments("--disable-gpu");
     * options.addArguments("--no-sandbox");
     * 
     * 
     * 
     * driver.get(BASE_URL);
     * }
     */

    @Test
    @Order(1)
    @DisplayName("Deve acessar a funcionalidade Relação de Benefícios Reajustados após o login")
    void deveAcessarRelacaoDeBeneficiosReajustados() {
        driver.findElement(By.id("j_username")).sendKeys("administrador");
        driver.findElement(By.name("j_password")).sendKeys("123456");
        driver.findElement(By.cssSelector("input[type='submit'][value='Entrar']")).click();

        // Trata alerta, se houver
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            System.out.println("Alerta encontrado: " + alert.getText());
            alert.accept();
        } catch (TimeoutException | NoAlertPresentException e) {
            System.out.println("Nenhum alerta inesperado após login.");
        }

        // Hover no menu pai
        Actions actions = new Actions(driver);
        WebElement menuPai = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/table/tbody/tr[2]/td[1]/div/ul/li[4]")));
        actions.moveToElement(menuPai).perform();

        // Trata alerta, se houver
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            System.out.println("Alerta encontrado: " + alert.getText());
            alert.accept();
        } catch (TimeoutException | NoAlertPresentException e) {
            System.out.println("Nenhum alerta inesperado após login.");
        }

        // Tenta clicar no menu
        WebElement menuItem = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[1]/div/ul/li[4]/a")));
        menuItem.click();

        // Após o submenu aparecer e clica
        WebElement submenu = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#M00040Actuator")));
        submenu.click();

        // Após o submenu aparecer e clica
        WebElement submenu1 = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#M00040Menu > li:nth-child(4) > a")));
        submenu1.click(); 

        // Seleciona Fundo
        WebElement combo = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#identificadorFundo")));
        Select select = new Select(combo);

        // Seleciona a opção desejada identificadorFundo
        select.selectByVisibleText("001 - SISTEL DE SEGURIDADE SOCIAL");
        // select.selectByValue("1"); // se souber o valor

        // Preenche o input de número de registro
        WebElement inputRegistro = wait
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("#numeroRegistro")));
        inputRegistro.clear();
        inputRegistro.sendKeys("1018");
        inputRegistro.sendKeys(Keys.TAB);

        // Seleciona Benefício
        WebElement bnf = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#identificadorBeneficio")));
        Select selectbnf = new Select(bnf);
        selectbnf.selectByVisibleText("APOSENTADORIA POR INVALIDEZ");


         // Preenche o input  Mês/Ano de Competência do Reajuste
        WebElement mesAnoIni = wait
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("#layout_col_principal > table > tbody > tr > td > form > table:nth-child(23) > tbody > tr:nth-child(1) > td > fieldset > table > tbody > tr > td:nth-child(2) > input")));
        mesAnoIni.clear();
        mesAnoIni.sendKeys("01/2010");
        mesAnoIni.sendKeys(Keys.TAB);

           WebElement mesAnoFin = wait
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("#layout_col_principal > table > tbody > tr > td > form > table:nth-child(23) > tbody > tr:nth-child(1) > td > fieldset > table > tbody > tr > td:nth-child(5) > input")));
        mesAnoFin.clear();
        mesAnoFin.sendKeys("12/2024");
        mesAnoFin.sendKeys(Keys.TAB);


         // Clicar Emitir Relatório
        WebElement emtRel = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[4]/table/tbody/tr/td/form/table[1]/tbody/tr/td[2]/table/tbody/tr/td/input[2]")));
        emtRel.click();   

        System.out.println("Cliquei no submenu, URL atual: " + driver.getCurrentUrl());

        // Opcional: esperar URL esperada se ela mudar após o clique
        // wait.until(ExpectedConditions.urlContains("relespelhoreajusteman.do"));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            // driver.quit();
        }
    }

    @Override
    public WebDriver getWebDriver() {
        return this.driver;
    }
}
