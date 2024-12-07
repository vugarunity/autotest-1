import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public class GeekBrainsTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    void createGroupTest() throws IOException {
        driver.get("https://test-stand.gb.ru/login");

        // Ввод логина и пароля
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form#login input[type='text']")));
        WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form#login input[type='password']")));

        usernameField.sendKeys(System.getProperty("geekbrains_username", System.getenv("geekbrains_username")));
        passwordField.sendKeys(System.getProperty("geekbrains_password", System.getenv("geekbrains_password")));


        WebElement loginButton = driver.findElement(By.cssSelector("form#login button"));
        loginButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("Hello,")));

        WebElement addGroupButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.addGroup")));
        addGroupButton.click();

        WebElement modalWindow = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".modal-class")));

        String groupName = "Testing Group";
        WebElement groupNameInput = modalWindow.findElement(By.cssSelector("input[name='group-name']"));
        groupNameInput.sendKeys(groupName);

        WebElement saveButton = modalWindow.findElement(By.cssSelector("button.save-group"));
        saveButton.click();

        WebElement createdGroupTitle = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[contains(text(), '" + groupName + "')]")));
        Assertions.assertTrue(createdGroupTitle.isDisplayed());

        saveScreenshot("src/test/resources/screenshot.png");
    }

    private void saveScreenshot(String filePath) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path destination = Path.of("src", "test", "resources", filePath);
        Files.createDirectories(destination.getParent());
        Files.copy(screenshot.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterEach
    void tearDownTest() {
        driver.quit();
    }
}
