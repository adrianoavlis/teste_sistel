package br.com.sistelteste.extensions;

import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback, BeforeEachCallback, ParameterResolver {

    private WebDriver driver;

    @Override
    public void beforeEach(ExtensionContext context) {
        // nada aqui por enquanto
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        boolean failed = context.getExecutionException().isPresent();
        String testName = context.getDisplayName();
        String status = failed ? "falha" : "sucesso";

        Object testInstance = context.getRequiredTestInstance();
        if (testInstance instanceof WebDriverProvider) {
            driver = ((WebDriverProvider) testInstance).getWebDriver();
            takeScreenshot(testName, status);
        }
    }

    private void takeScreenshot(String testName, String status) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path destDir = Paths.get("target", "screenshots", status);
            Files.createDirectories(destDir);
            Path destFile = destDir.resolve(testName.replace(" ", "_") + "_" + timestamp + ".png");
            Files.copy(screenshot.toPath(), destFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("📸 Screenshot salva: " + destFile);
        } catch (Exception e) {
            System.err.println("Erro ao salvar screenshot: " + e.getMessage());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
        return null;
    }
}
