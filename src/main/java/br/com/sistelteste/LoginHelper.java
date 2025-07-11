package br.com.sistelteste;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginHelper {

    public static void realizarLogin(WebDriver driver, String usuario, String senha) {
        driver.findElement(By.id("j_username")).sendKeys(usuario);
        driver.findElement(By.name("j_password")).sendKeys(senha);
        driver.findElement(By.cssSelector("input[type='submit'][value='Entrar']")).click();
    }
}
