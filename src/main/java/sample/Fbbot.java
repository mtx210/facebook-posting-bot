package sample;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.model.Account;
import sample.model.Listing;
import sample.model.ResourceFiles;
import sample.model.Resources;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Fbbot extends Application {

    private static final Logger log = java.util.logging.Logger.getLogger(Fbbot.class.getName());
    public static List<Account> accounts;
    public static Listing listing;
    public static List<String> locations;
    public static File[] photos;

    public static void main(String[] args) throws Exception {
        initAccounts();
        initListing();
        initLocations();
        initPhotos();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    public static void initAccounts() throws Exception {
        File accountsFile = ResourceFiles.ACCOUNTS.getFile();
        if(!accountsFile.exists() || !accountsFile.isFile()){
            try {
                boolean success = accountsFile.createNewFile();
                if(!success){
                    log.log(Level.WARNING, Resources.ACCOUNTS_FILE_ERROR.toString());
                    return;
                }
            } catch (Exception e) {
                log.log(Level.WARNING, Resources.ACCOUNTS_FILE_EXCEPTION.toString() + e.getStackTrace());
                return;
            }
        }

        byte[] fileContent = Files.readAllBytes(accountsFile.toPath());
        accounts = new Gson().fromJson(new String(fileContent), new TypeToken<List<Account>>(){}.getType());
    }

    public static void initListing() throws Exception {
        File listingFile = ResourceFiles.LISTING.getFile();
        if(!listingFile.exists() || !listingFile.isFile()){
            try {
                boolean success = listingFile.createNewFile();
                if(!success){
                    log.log(Level.WARNING, Resources.LISTING_FILE_ERROR.toString());
                    return;
                }
            } catch (Exception e) {
                log.log(Level.WARNING, Resources.LISTING_FILE_EXCEPTION.toString() + e.getStackTrace());
                return;
            }
        }

        try (FileInputStream fis = new FileInputStream(listingFile);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)
        ){
            String fileContent = reader.lines().collect(Collectors.joining());
            listing = new Gson().fromJson(fileContent, new TypeToken<Listing>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initLocations() throws Exception {
        File locationsFile = ResourceFiles.LOCALIZATIONS.getFile();
        System.out.println("sciezka kont: " + locationsFile.getAbsolutePath());
        if(!locationsFile.exists() || !locationsFile.isFile()){
            try {
                boolean success = locationsFile.createNewFile();
                if(!success){
                    log.log(Level.WARNING, Resources.LOCATIONS_FILE_ERROR.toString());
                    return;
                }
            } catch (Exception e) {
                log.log(Level.WARNING, Resources.LOCATIONS_FILE_EXCEPTION.toString() + e.getStackTrace());
                return;
            }
        }

        byte[] fileContent = Files.readAllBytes(locationsFile.toPath());
        locations = new Gson().fromJson(new String(fileContent), new TypeToken<List<String>>(){}.getType());
    }

    private static void initPhotos() {
        File photosFolder = ResourceFiles.PHOTOS_FOLDER.getFile();
        System.out.println("photosfolder sciezka : " + photosFolder.getAbsolutePath());
        System.out.println("photosfolder jest folderem: " + photosFolder.isDirectory());
        System.out.println("photosfolder jest plikiem: " + photosFolder.isFile());
        for(File file : photosFolder.listFiles()){
            System.out.println("sciezka pliku: " + file.getAbsolutePath());
        }
        if(!photosFolder.exists() || !photosFolder.isDirectory()){
            try {
                boolean success = photosFolder.mkdir();
                if(!success){
                    log.log(Level.WARNING, Resources.PHOTOS_FOLDER_ERROR.toString());
                    return;
                }
            } catch (Exception e) {
                log.log(Level.WARNING, Resources.PHOTOS_FOLDER_EXCEPTION.toString() + e.getStackTrace());
                return;
            }
        }

        photos = photosFolder.listFiles();
    }
}
