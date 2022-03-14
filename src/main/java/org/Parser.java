package org;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Parser {
    public String cookie;
    Parser(String cookie) {
        this.cookie = cookie;
    }

    public String bid() throws IOException {
        Document page = Jsoup.connect("https://daazweb.com/dashboard/orders")
                .cookie("sessionid", cookie)
                .get();

        Element bid = page.getElementById("user_stavka");
        return bid.text();
    }

    public String percent() throws IOException {
        Document page = Jsoup.connect("https://daazweb.com/dashboard/orders")
                .cookie("sessionid", cookie)
                .get();

        Element percent = page.getElementById("user_percent");
        return percent.text();
    }
}
