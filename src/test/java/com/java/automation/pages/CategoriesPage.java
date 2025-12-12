package com.java.automation.pages;

import com.java.automation.config.TestConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object Model for Admin Categories Management page
 */
public class CategoriesPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Page Title
    @FindBy(xpath = "//h4[contains(@class, 'page-title') and contains(text(), 'Category Management')]")
    private WebElement pageTitle;

    @FindBy(xpath = "//h4[contains(@class, 'card-title') and contains(text(), 'Category Management')]")
    private WebElement cardTitle;

    // Add Category Button
    @FindBy(xpath = "//button[contains(@class, 'btn-primary') and contains(text(), 'Add Category')]")
    private WebElement addCategoryButton;

    // Table elements
    @FindBy(xpath = "//table[@id='add-row']")
    private WebElement categoriesTable;

    @FindBy(xpath = "//table[@id='add-row']//tbody//tr")
    private java.util.List<WebElement> categoryRows;

    public CategoriesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        PageFactory.initElements(driver, this);
    }

    public void navigateToCategoriesPage() {
        String baseUrl = TestConfig.getBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        driver.get(baseUrl + "/admin/categories");
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public boolean isOnCategoriesPage() {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl.contains("/admin/categories");
    }

    public boolean isPageTitleDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitle));
            return pageTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCardTitleDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(cardTitle));
            return cardTitle.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAddCategoryButtonDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(addCategoryButton));
            return addCategoryButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCategoriesTableDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(categoriesTable));
            return categoriesTable.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickAddCategoryButton() {
        wait.until(ExpectedConditions.elementToBeClickable(addCategoryButton));
        addCategoryButton.click();
    }

    public int getCategoryCount() {
        try {
            wait.until(ExpectedConditions.visibilityOf(categoriesTable));
            return categoryRows.size();
        } catch (Exception e) {
            return 0;
        }
    }
}

