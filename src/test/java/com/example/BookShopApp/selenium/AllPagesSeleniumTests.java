package com.example.BookShopApp.selenium;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.HashSet;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class AllPagesSeleniumTests {
    private static ChromeDriver driver;
    private final HashSet<String> links = new HashSet<>();

    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.chrome.driver", "/Users/serg/Downloads/chromedriver/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

    @Test
    public void testAllPage() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        showPage(mainPage);
    }

    private void showPage(MainPage mainPage) throws InterruptedException {
        Document doc = mainPage.getDocFromUrl();
        if (doc != null) {
            Elements elements = doc.select("a");

            for (Element element : elements) {
                String href = element.attr("abs:href");
                if (!href.isEmpty()
                        && !href.contains("?")
                        && !href.contains("#")
                        && !href.contains("facebook")
                        && !href.endsWith(".pdf")
                        && !href.endsWith(".jpg")
                        && !href.endsWith(".jpeg")
                        && !href.endsWith(".zip")
                        && !href.endsWith(".rar")
                        && !href.endsWith(".png")) {
                    if (!links.contains(href)) {
                        Logger.getLogger(this.getClass().getSimpleName()).info(href);
                        links.add(href);
                        mainPage.setUrl(href);
                        mainPage
                                .callPage()
                                .pause();
                        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
                        showPage(mainPage);
                    }
                }
            }
        }
    }
}