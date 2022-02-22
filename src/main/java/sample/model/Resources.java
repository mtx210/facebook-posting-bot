package sample.model;

public enum Resources {

    ACCOUNTS_FILE_ERROR("Accounts file cannot be created but no exception occurred"),
    ACCOUNTS_FILE_EXCEPTION("Exception occurred while creating accounts file"),
    LISTING_FILE_ERROR("Listing file cannot be created but no exception occurred"),
    LISTING_FILE_EXCEPTION("Exception occurred while creating listing file"),
    LOCATIONS_FILE_ERROR("Locations file cannot be created but no exception occurred"),
    LOCATIONS_FILE_EXCEPTION("Exception occurred while creating locations file"),
    PHOTOS_FOLDER_ERROR("Photos folder cannot be created but no exception occurred"),
    PHOTOS_FOLDER_EXCEPTION("Exception occurred while creating photos folder");

    String message;

    Resources(String message){
        this.message = message;
    }
}