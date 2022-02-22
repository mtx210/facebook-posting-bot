package sample.model;

import java.io.File;

public enum ResourceFiles {

    ACCOUNTS("./Downloads/bot/pliki/konta.txt"),
    LISTING("./Downloads/bot/pliki/ogloszenie.txt"),
    LOCALIZATIONS("./Downloads/bot/pliki/lokalizacje.txt"),
    PHOTOS_FOLDER("./Downloads/bot/pliki/zdjecia");

    File file;

    ResourceFiles(String location){
        file = new File(location);
    }

    public File getFile() {
        return file;
    }
}
