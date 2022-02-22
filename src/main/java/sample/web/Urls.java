package sample.web;

public enum Urls {

    FACEBOOK_MAIN_PAGE("https://facebook.com"),
    FACEBOOK_MARKETPLACE("https://www.facebook.com/marketplace/create/item");

    String url;

    Urls(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}