package sample.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import sample.Fbbot;
import sample.model.Account;
import sample.model.ResourceFiles;
import sample.web.Urls;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    public Button bLogin;
    public ComboBox<String> accountCombobox;
    private WebDriver driver;

    @FXML
    public void initialize() {
        List<String> accNames = new ArrayList<>();
        for(Account account : Fbbot.accounts){
            accNames.add(account.getLogin());
        }

        accountCombobox.setItems(FXCollections.observableArrayList(accNames));
    }


    //
    // Event listeners
    //
    public void startBot(MouseEvent mouseEvent) {
        initBrowser();

        Account chosenAcc = null;
        for(Account account : Fbbot.accounts){
            if(account.getLogin().equals(accountCombobox.getValue())){
                chosenAcc = account;
            }
        }
        if(chosenAcc == null){
            System.out.println("acc error");
            return;
        }

        login(chosenAcc);

        int photonumber = 0;
        for(String locationName : Fbbot.locations){
            goToMarketplace();
            postListing(locationName, photonumber);
            photonumber++;
            if(photonumber >= Fbbot.photos.length){
                photonumber = 0;
            }
        }

        driver.quit();

        /*final int listingsPerAccount = spinner.getValue();
        int locationsIterator = 1;
        List<String> locations = Fbbot.locations;

        initBrowser();

        for(Account account : Fbbot.accounts){
            int currentListingsPosted = 0;
            while(currentListingsPosted < listingsPerAccount){

                if(locationsIterator > locations.size()){
                    locationsIterator = 1;
                }

                login(account);
                goToMarketplace();
                postListing(locations.get(locationsIterator));
                locationsIterator++;

                currentListingsPosted++;
            }
            logout();
        }

        driver.quit();*/
    }

    //
    // Private util methods
    //
    private void initBrowser(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=/Users/macbook/Library/Application Support/Google/Chrome");
        options.addArguments("--profile-directory=Profile 1");
        options.addArguments("--disable-extensions");
        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.get(Urls.FACEBOOK_MAIN_PAGE.getUrl());
    }

    private void login(Account account){
        //dismiss cookies alert
        try{
            driver.findElement(By.cssSelector("button[title*='Zezwól na wszystkie pliki cookie']")).click();
        } catch (NoSuchElementException ignored){

        }

        driver.findElement(By.xpath("//*[@id=\"email\"]")).sendKeys(account.getLogin());
        driver.findElement(By.xpath("//*[@id=\"pass\"]")).sendKeys(account.getPassword());
        driver.findElement(By.name("login")).click();

    }

    private void goToMarketplace(){
        while(!driver.getCurrentUrl().equals(Urls.FACEBOOK_MARKETPLACE.getUrl())){
            driver.get(Urls.FACEBOOK_MARKETPLACE.getUrl());
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        }
    }

    private void postListing(String location, int photonumber){

        //0

        for(int i=0;i<50;i++){
            driver.findElement(By.cssSelector("label[aria-label*='Tytuł']")).findElement(By.cssSelector("div")).findElement(By.cssSelector("div")).findElement(By.cssSelector("input"))
                    .sendKeys(Keys.BACK_SPACE);
        }
        driver.findElement(By.cssSelector("label[aria-label*='Tytuł']")).findElement(By.cssSelector("div")).findElement(By.cssSelector("div")).findElement(By.cssSelector("input"))
                .sendKeys(Fbbot.listing.getTitle());

        //1

        driver.findElement(By.cssSelector("label[aria-label*='Cena']")).findElement(By.cssSelector("div")).findElement(By.cssSelector("div")).findElement(By.cssSelector("input"))
                .sendKeys(Fbbot.listing.getPrice().toString());

        //2

        driver.findElement(By.cssSelector("label[aria-label*='Opis']")).findElement(By.cssSelector("div")).findElement(By.cssSelector("div")).findElement(By.cssSelector("textarea"))
                .sendKeys(Fbbot.listing.getDescription());

        //3

        driver.findElement(By.cssSelector("label[aria-label*='Kategoria']"))
                .click();

        driver.findElement(By.xpath("//span[.='Narzędzia']"))
                .click();

        //4

        driver.findElement(By.cssSelector("label[aria-label*='Stan']"))
                .click();

        driver.findElement(By.xpath("//span[.='Nowy']"))
                .click();

        //5

        //String location = Fbbot.locations.get(0 /*ThreadLocalRandom.current().nextInt(0, Main.locations.size())*/);
        for(int i=0;i<50;i++){
            driver.findElement(By.cssSelector("input[aria-label*='Wprowadź nazwę miejscowości']"))
                    .sendKeys(Keys.BACK_SPACE);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.cssSelector("input[aria-label*='Wprowadź nazwę miejscowości']"))
                .sendKeys(location);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.cssSelector("ul[aria-label*='5 sugerowanych wyszukiwań']")).findElement(By.xpath("//span[.='" + location + "']"))
                .click();

        //6

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        try {
            File photosFolder = ResourceFiles.PHOTOS_FOLDER.getFile();
            File[] photos = photosFolder.listFiles();
            StringSelection ss = new StringSelection(photos[photonumber].getAbsolutePath().replace("./", ""));
            System.out.println("sciezka do wklejenia:" + photos[photonumber].getAbsolutePath().replace("./", ""));
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
            driver.findElement(By.xpath("//span[.='Dodaj zdjęcia']"))
                    .click();

            Thread.sleep(5000);

            //WINDOWS
            /*Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);*/

            //MAC
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_META);
            robot.delay(2000);
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.delay(2000);
            robot.keyPress(KeyEvent.VK_G);
            robot.delay(2000);
            robot.keyRelease(KeyEvent.VK_META);
            robot.delay(2000);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.delay(2000);
            robot.keyRelease(KeyEvent.VK_G);
            robot.delay(2000);

            robot.keyPress(KeyEvent.VK_META);
            robot.delay(2000);
            robot.keyPress(KeyEvent.VK_V);
            robot.delay(2000);
            robot.keyRelease(KeyEvent.VK_META);
            robot.delay(2000);
            robot.keyRelease(KeyEvent.VK_V);
            robot.delay(2000);

            robot.keyPress(KeyEvent.VK_ENTER);
            robot.delay(2000);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(2000);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.delay(2000);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.delay(2000);


        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("czekam");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //7

        driver.findElement(By.cssSelector("div[aria-label*='Dalej']"))
                .click();

        //8

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try{
            driver.findElement(By.cssSelector("div[aria-label*='Opublikuj']"))
                    .click();
        } catch(Exception e) {
            driver.findElement(By.xpath("//span[.='Zapisz wersję roboczą']"))
                    .click();
        }

        //9

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.get("https://www.facebook.com/marketplace/you/selling");

        //10
    }

    private void logout() {
        driver.findElement(By.cssSelector("div[aria-label*='Konto']"))
                .click();
        driver.findElement(By.xpath("//span[.='Wyloguj się']"))
                .click();
        driver.get(Urls.FACEBOOK_MAIN_PAGE.getUrl());
    }
}
