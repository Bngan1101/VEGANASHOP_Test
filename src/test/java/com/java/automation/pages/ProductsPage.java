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
 * Page Object Model for Admin Products Management page
 */
public class ProductsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Page Title
    @FindBy(xpath = "//h4[contains(@class, 'page-title') and contains(text(), 'Product Management')]")
    private WebElement pageTitle;

    @FindBy(xpath = "//h4[contains(@class, 'card-title') and contains(text(), 'Product Management')]")
    private WebElement cardTitle;

    // Add Product Button - using data-target attribute
    @FindBy(xpath = "//button[@data-target='#addRowModal']")
    private WebElement addProductButton;

    // Table elements
    @FindBy(xpath = "//table[@id='add-row']")
    private WebElement productsTable;

    @FindBy(xpath = "//table[@id='add-row']//tbody//tr")
    private java.util.List<WebElement> productRows;

    // Add Product Modal elements
    @FindBy(xpath = "//div[@id='addRowModal']")
    private WebElement addProductModal;

    @FindBy(xpath = "//form[@action='/addProduct']//input[@name='name']")
    private WebElement productNameInput;

    @FindBy(xpath = "//form[@action='/addProduct']")
    private WebElement addProductForm;

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        PageFactory.initElements(driver, this);
    }

    public void navigateToProductsPage() {
        String baseUrl = TestConfig.getBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        driver.get(baseUrl + "/admin/products");
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
    }

    public boolean isOnProductsPage() {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl.contains("/admin/products");
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

    public boolean isAddProductButtonDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(addProductButton));
            return addProductButton.isDisplayed();
        } catch (Exception e) {
            // Try alternative xpath if first one fails
            try {
                WebElement altButton = driver.findElement(
                    org.openqa.selenium.By.xpath("//button[contains(@class, 'btn-primary') and contains(., 'Add Product')]"));
                return altButton.isDisplayed();
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public boolean isProductsTableDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(productsTable));
            return productsTable.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickAddProductButton() {
        try {
            wait.until(ExpectedConditions.visibilityOf(addProductButton));
            // Try to click, if fails use JavaScript
            try {
                addProductButton.click();
            } catch (Exception e) {
                // Fallback to JavaScript click
                ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", addProductButton);
            }
        } catch (Exception e) {
            // Button might not be visible, that's OK for test
            throw new RuntimeException("Cannot click Add Product button: " + e.getMessage(), e);
        }
    }

    public int getProductCount() {
        try {
            wait.until(ExpectedConditions.visibilityOf(productsTable));
            return productRows.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isAddProductModalDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(addProductModal));
            return addProductModal.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAddProductFormDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(addProductForm));
            return addProductForm.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    // ==================== BỔ SUNG CÁC PHƯƠNG THỨC CRUD CƠ BẢN CHO PRODUCTS ====================


    // Helper for table search (assuming a generic search input on the page)
    @FindBy(xpath = "//input[@type='search' or @placeholder='Search' or @name='searchTerm']")
    private WebElement searchInput;

    public void searchTable(String keyword) {
        wait.until(ExpectedConditions.visibilityOf(searchInput));
        searchInput.clear();
        searchInput.sendKeys(keyword);
        searchInput.submit(); // Assuming pressing Enter/Submit performs the search
        try { Thread.sleep(500); } catch (InterruptedException e) {} // Wait for table refresh
    }

    // CRUD helpers (Assumed structure - you need to define actual locators and logic)
    // NOTE: CÁC LOCATOR CHO FORM/MODAL CREATE/EDIT CHƯA ĐƯỢC ĐỊNH NGHĨA Ở ĐÂY, CẦN BỔ SUNG TRONG CLASS

    public void fillAddProductForm(String name, String sku, Double price, Integer quantity, String description, String imagePath, String category, String supplier) {
        // Example: Need to implement actual element interaction here
        // If the form is complex, you may need separate methods for image upload, dropdowns, etc.
        System.out.println("LOG: Filling Add Product form with: " + name + ", " + sku);
        // ... (replace with actual Selenium code to fill fields)
    }

    public void submitAddProductForm() {
        // Example: Need to implement actual submit button click here
        // Find and click the submit button in the modal/form
        System.out.println("LOG: Submitting Add Product form");
        // ... (replace with actual submit button click)
    }

    public void addProductQuick(String name, String sku) {
        // Helper to quickly create a product for negative tests
        clickAddProductButton();
        fillAddProductForm(name, sku, 10000.0, 10, "Quick product", null, "Category A", "Supplier B");
        submitAddProductForm();
    }

    public String getProductName(int index) {
        // Assuming the product name is in the first or second column of the row
        // You need to adjust the index and xpath based on your table structure
        return productRows.get(index).findElement(org.openqa.selenium.By.xpath("./td[2]")).getText();
    }

    public String getProductSKU(int index) {
        // Assuming SKU is in the third column
        return productRows.get(index).findElement(org.openqa.selenium.By.xpath("./td[3]")).getText();
    }

    public String getProductCategory(int index) {
        // Assuming Category is in the fourth column
        return productRows.get(index).findElement(org.openqa.selenium.By.xpath("./td[4]")).getText();
    }

    public String getProductSupplier(int index) {
        // Assuming Supplier is in the fifth column
        return productRows.get(index).findElement(org.openqa.selenium.By.xpath("./td[5]")).getText();
    }

    @FindBy(xpath = "//div[contains(@class, 'modal-content')]//div[contains(@class, 'alert-danger') or contains(@class, 'alert-error')]")
    private WebElement errorMessageAlert;

    public boolean isErrorMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessageAlert));
            return errorMessageAlert.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickEditButton(int index) {
        // Assuming edit button is in the last column of the row
        WebElement editButton = productRows.get(index).findElement(org.openqa.selenium.By.xpath(".//button[contains(@title, 'Edit') or contains(@class, 'edit')]"));
        wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();
    }

    public void fillEditProductForm(String name, String sku, Double price, Integer quantity, String description, String imagePath, String category, String supplier) {
        // Reuse form filling logic, only filling non-null values
        System.out.println("LOG: Filling Edit Product form with updates for: " + name + ", " + sku);
        // ... (replace with actual Selenium code to fill fields)
    }

    public void submitEditProductForm() {
        // Find and click the submit button in the edit modal/form
        System.out.println("LOG: Submitting Edit Product form");
        // ... (replace with actual submit button click)
    }
}

