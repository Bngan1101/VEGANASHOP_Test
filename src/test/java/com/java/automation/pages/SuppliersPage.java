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
 * Page Object Model for Admin Suppliers Management page
 */
public class SuppliersPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Page Title
    @FindBy(xpath = "//h4[contains(@class, 'page-title') and contains(text(), 'Supplier Management')]")
    private WebElement pageTitle;

    @FindBy(xpath = "//h4[contains(@class, 'card-title') and contains(text(), 'Supplier Management')]")
    private WebElement cardTitle;

    // Add Supplier Button
    @FindBy(xpath = "//button[contains(@class, 'btn-primary') and contains(text(), 'Add Supplier')]")
    private WebElement addSupplierButton;

    // Table elements
    @FindBy(xpath = "//table[@id='add-row']")
    private WebElement suppliersTable;

    @FindBy(xpath = "//table[@id='add-row']//tbody//tr")
    private java.util.List<WebElement> supplierRows;

    public SuppliersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        PageFactory.initElements(driver, this);
    }

    public void navigateToSuppliersPage() {
        String baseUrl = TestConfig.getBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        driver.get(baseUrl + "/admin/suppliers");
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public boolean isOnSuppliersPage() {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl.contains("/admin/suppliers");
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

    public boolean isAddSupplierButtonDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(addSupplierButton));
            return addSupplierButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSuppliersTableDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(suppliersTable));
            return suppliersTable.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickAddSupplierButton() {
        wait.until(ExpectedConditions.elementToBeClickable(addSupplierButton));
        addSupplierButton.click();
    }

    public int getSupplierCount() {
        try {
            wait.until(ExpectedConditions.visibilityOf(suppliersTable));
            return supplierRows.size();
        } catch (Exception e) {
            return 0;
        }
    }
}

