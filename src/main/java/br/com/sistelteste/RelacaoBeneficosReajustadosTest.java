package br.com.sistelteste;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.nio.file.*;
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
    private final Path downloadDir = Paths.get(System.getProperty("user.home"), "Download");

    @BeforeAll
    void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();

        // Configurações para evitar bloqueios de download
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadDir.toString());
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);
        prefs.put("safebrowsing.disable_download_protection", true); // 🔑 evita bloqueio de arquivos não seguros

        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get(BASE_URL);
    }

    @Test
    @Order(1)
    @DisplayName("Deve acessar a funcionalidade Relação de Benefícios Reajustados após o login e baixar relatório")
    void deveAcessarRelacaoDeBeneficiosReajustados() {
        driver.findElement(By.id("j_username")).sendKeys("administrador");
        driver.findElement(By.name("j_password")).sendKeys("123456");
        driver.findElement(By.cssSelector("input[type='submit'][value='Entrar']")).click();

        try {
            wait.until(ExpectedConditions.alertIsPresent()).accept();
        } catch (TimeoutException | NoAlertPresentException ignored) {
            System.out.println("Nenhum alerta após login.");
        }

        Actions actions = new Actions(driver);
        WebElement menuPai = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/table/tbody/tr[2]/td[1]/div/ul/li[4]")));
        actions.moveToElement(menuPai).perform();

        try {
            wait.until(ExpectedConditions.alertIsPresent()).accept();
        } catch (TimeoutException | NoAlertPresentException ignored) {
            System.out.println("Nenhum alerta após hover no menu.");
        }

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[1]/div/ul/li[4]/a"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[1]/div/ul/li[4]/ul/li[4]/a"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[1]/div/ul/li[4]/ul/li[4]/ul/li[4]/a"))).click();

        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[4]/table/tbody/tr/td/form/table[3]/tbody/tr[2]/td/select"))));
        select.selectByVisibleText("001 - SISTEL DE SEGURIDADE SOCIAL");

        WebElement inputRegistro = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[4]/table/tbody/tr/td/form/table[4]/tbody/tr[2]/td[1]/input")));
        inputRegistro.clear();
        inputRegistro.sendKeys("1018", Keys.TAB);

        Select selectbnf = new Select(wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[4]/table/tbody/tr/td/form/table[6]/tbody/tr[2]/td/select"))));
        selectbnf.selectByVisibleText("APOSENTADORIA POR INVALIDEZ");

        WebElement mesAnoIni = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[4]/table/tbody/tr/td/form/table[7]/tbody/tr[1]/td/fieldset/table/tbody/tr/td[2]/input")));
        mesAnoIni.clear();
        mesAnoIni.sendKeys("01/2010", Keys.TAB);

        WebElement mesAnoFin = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[4]/table/tbody/tr/td/form/table[7]/tbody/tr[1]/td/fieldset/table/tbody/tr/td[5]/input")));
        mesAnoFin.clear();
        mesAnoFin.sendKeys("12/2024", Keys.TAB);

        WebElement emitir = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/table/tbody/tr[2]/td[4]/table/tbody/tr/td/form/table[1]/tbody/tr/td[2]/table/tbody/tr/td/input[2]")));
        emitir.click();

        // Aguarda download do arquivo
        boolean arquivoBaixado = aguardarDownload(downloadDir, "RelEspelhoReajuste", 20);
        Assertions.assertTrue(arquivoBaixado, "O arquivo 'RelEspelhoReajuste' não foi baixado como esperado.");
    }

    private boolean aguardarDownload(Path dir, String parteNomeArquivo, int timeoutSegundos) {
        try {
            Files.createDirectories(dir);
            int tentativas = 0;
            while (tentativas < timeoutSegundos) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                    for (Path file : stream) {
                        String nome = file.getFileName().toString();
                        if (Files.isRegularFile(file)
                                && nome.contains(parteNomeArquivo)
                                && !nome.endsWith(".crdownload")) {
                            System.out.println("📥 Arquivo baixado com sucesso: " + nome);
                            return true;
                        }
                    }
                }
                Thread.sleep(1000);
                tentativas++;
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao verificar download: " + e.getMessage());
        }
        return false;
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
