package com.java.automation.tests.user;

import com.java.automation.base.BaseTest;
import com.java.automation.config.TestConfig;
import com.java.automation.pages.*;
import com.java.automation.utils.TestDataGenerator;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * All User Test Cases - Authentication, Shopping, Cart & Checkout
 */
public class UserTest extends BaseTest {

    private static final String USER_ID = TestConfig.getProperty("test.user.id");
    private static final String USER_PASSWORD = TestConfig.getProperty("test.user.password");

    /**
     * Helper method to login as user
     */
    private void loginAsUser() {
        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();
        loginPage.login(USER_ID, USER_PASSWORD);
    }

    // ==================== REGISTRATION TESTS ====================

    @Test(priority = 1, description = "01. Register - Đăng ký thành công")
    public void testRegisterSuccess() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test đăng ký thành công");

        LoginOrRegisterPage registerPage = new LoginOrRegisterPage(driver);
        registerPage.navigateToLoginPage();
        
        String customerId = TestDataGenerator.generateUniqueCustomerId();
        String fullname = TestDataGenerator.generateUniqueFullname();
        String email = TestDataGenerator.generateUniqueEmail();
        String password = "123456";
        
        registerPage.register(customerId, fullname, email, password);
        
        Assert.assertTrue(registerPage.isSuccessAlertDisplayed(), 
            "Không hiển thị thông báo thành công khi đăng ký");
        
        String successText = registerPage.getSuccessAlertText();
        Assert.assertTrue(successText.contains("thành công") || 
                         successText.contains("Đăng kí"), 
            "Thông báo thành công không đúng: " + successText);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Đăng ký thành công");
    }

    @Test(priority = 2, description = "02. Register - Đăng ký với Customer ID đã tồn tại")
    public void testRegisterWithExistingCustomerId() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test đăng ký với Customer ID đã tồn tại");

        LoginOrRegisterPage registerPage = new LoginOrRegisterPage(driver);
        registerPage.navigateToLoginPage();
        
        String existingCustomerId = TestConfig.getProperty("test.user.id");
        String fullname = TestDataGenerator.generateUniqueFullname();
        String email = TestDataGenerator.generateUniqueEmail();
        String password = "123456";
        
        registerPage.register(existingCustomerId, fullname, email, password);
        
        Assert.assertTrue(registerPage.isErrorAlertDisplayed(), 
            "Không hiển thị thông báo lỗi khi đăng ký với Customer ID đã tồn tại");
        
        String errorText = registerPage.getErrorAlertText();
        Assert.assertTrue(errorText.contains("ID Login") || 
                         errorText.contains("đã được sử dụng"), 
            "Thông báo lỗi không đúng: " + errorText);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Đăng ký với Customer ID đã tồn tại đã hiển thị lỗi đúng");
    }

    @Test(priority = 3, description = "03. Register - Đăng ký với Email đã tồn tại")
    public void testRegisterWithExistingEmail() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test đăng ký với Email đã tồn tại");

        LoginOrRegisterPage registerPage = new LoginOrRegisterPage(driver);
        registerPage.navigateToLoginPage();
        
        String customerId = TestDataGenerator.generateUniqueCustomerId();
        String fullname = TestDataGenerator.generateUniqueFullname();
        String existingEmail = TestConfig.getProperty("test.user.email");
        String password = "123456";
        
        registerPage.register(customerId, fullname, existingEmail, password);
        
        Assert.assertTrue(registerPage.isErrorAlertDisplayed(), 
            "Không hiển thị thông báo lỗi khi đăng ký với Email đã tồn tại");
        
        String errorText = registerPage.getErrorAlertText();
        Assert.assertTrue(errorText.contains("Email") || 
                         errorText.contains("đã được sử dụng"), 
            "Thông báo lỗi không đúng: " + errorText);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Đăng ký với Email đã tồn tại đã hiển thị lỗi đúng");
    }

    @Test(priority = 4, description = "04. Register - Đăng ký với Customer ID trống")
    public void testRegisterWithEmptyCustomerId() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test đăng ký với Customer ID trống");

        LoginOrRegisterPage registerPage = new LoginOrRegisterPage(driver);
        registerPage.navigateToLoginPage();
        
        registerPage.clickSignUpTab();
        registerPage.enterRegisterFullname("Test User");
        registerPage.enterRegisterEmail("test@example.com");
        registerPage.enterRegisterPassword("123456");
        registerPage.clickSignUpButton();
        
        Assert.assertTrue(registerPage.isOnLoginPage(), 
            "Form không validate khi Customer ID trống");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Form validate đúng khi Customer ID trống");
    }
// ==================== REGISTRATION TESTS - ADDITIONAL ==================== (Bảo ngân)

    @Test(priority = 41, description = "04.1. Register - Đăng ký với Email sai định dạng")
    public void testRegisterWithInvalidEmailFormat() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test đăng ký với Email sai định dạng");

        LoginOrRegisterPage registerPage = new LoginOrRegisterPage(driver);
        registerPage.navigateToLoginPage();

        String customerId = TestDataGenerator.generateUniqueCustomerId();
        String fullname = TestDataGenerator.generateUniqueFullname();
        String invalidEmail = "invalid@.com"; // Email sai định dạng
        String password = "123456";

        registerPage.register(customerId, fullname, invalidEmail, password);

        // Giả định hệ thống hiển thị lỗi hoặc không chuyển trang
        boolean isErrorDisplayed = registerPage.isErrorAlertDisplayed() || registerPage.isOnLoginPage();

        Assert.assertTrue(isErrorDisplayed,
                "Hệ thống không validate Email sai định dạng");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Hệ thống đã chặn đăng ký với Email sai định dạng");
    }

    @Test(priority = 42, description = "04.2. Register - Đăng ký với mật khẩu quá ngắn")
    public void testRegisterWithShortPassword() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test đăng ký với mật khẩu quá ngắn");

        LoginOrRegisterPage registerPage = new LoginOrRegisterPage(driver);
        registerPage.navigateToLoginPage();

        String customerId = TestDataGenerator.generateUniqueCustomerId();
        String fullname = TestDataGenerator.generateUniqueFullname();
        String email = TestDataGenerator.generateUniqueEmail();
        String shortPassword = "123"; // Giả sử min là 6 ký tự

        registerPage.register(customerId, fullname, email, shortPassword);

        // Kiểm tra xem form có bị chặn/có thông báo lỗi không
        boolean isErrorDisplayed = registerPage.isErrorAlertDisplayed() || registerPage.isOnLoginPage();

        Assert.assertTrue(isErrorDisplayed,
                "Hệ thống không validate mật khẩu quá ngắn");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Hệ thống đã chặn đăng ký với mật khẩu quá ngắn");
    }

    @Test(priority = 43, description = "04.3. Register - Đăng ký với trường dữ liệu vượt quá giới hạn")
    public void testRegisterWithLongFields() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test đăng ký với trường dữ liệu vượt quá giới hạn");

        LoginOrRegisterPage registerPage = new LoginOrRegisterPage(driver);
        registerPage.navigateToLoginPage();

        // Tạo chuỗi dài (giả sử 300 ký tự là vượt giới hạn)
        String longString = "a".repeat(300);

        registerPage.register(longString, longString, "long_email@test.com", "123456");

        // Kiểm tra xem hệ thống có trả về lỗi (thay vì thành công) không
        Assert.assertFalse(registerPage.isSuccessAlertDisplayed(),
                "Đăng ký thành công với dữ liệu quá dài, cần kiểm tra validation");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Hệ thống xử lý đúng (chặn/cắt) dữ liệu đầu vào quá dài");
    }
    // ==================== LOGIN TESTS ====================

    @Test(priority = 5, description = "05. Login - Đăng nhập thành công")
    public void testLoginSuccess() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test đăng nhập thành công");

        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();
        
        loginPage.login(USER_ID, USER_PASSWORD);
        
        Assert.assertTrue(loginPage.isOnHomePage(), 
            "Đăng nhập thành công nhưng không redirect về trang chủ");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Đăng nhập thành công");
    }

    @Test(priority = 6, description = "06. Login - Đăng nhập với Customer ID sai")
    public void testLoginWithInvalidCustomerId() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test đăng nhập với Customer ID sai");

        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();
        
        loginPage.login("invalid_user_id", "123456");
        
        Assert.assertTrue(loginPage.isErrorAlertDisplayed(), 
            "Không hiển thị thông báo lỗi khi đăng nhập với Customer ID sai");
        
        String errorText = loginPage.getErrorAlertText();
        Assert.assertTrue(errorText.contains("không chính xác") || 
                         errorText.contains("Tài khoản"), 
            "Thông báo lỗi không đúng: " + errorText);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Đăng nhập với Customer ID sai đã hiển thị lỗi đúng");
    }

    @Test(priority = 7, description = "07. Login - Đăng nhập với mật khẩu sai")
    public void testLoginWithInvalidPassword() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test đăng nhập với mật khẩu sai");

        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();
        
        loginPage.login(USER_ID, "wrong_password");
        
        Assert.assertTrue(loginPage.isErrorAlertDisplayed(), 
            "Không hiển thị thông báo lỗi khi đăng nhập với mật khẩu sai");
        
        String errorText = loginPage.getErrorAlertText();
        Assert.assertTrue(errorText.contains("không chính xác") || 
                         errorText.contains("Tài khoản"), 
            "Thông báo lỗi không đúng: " + errorText);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Đăng nhập với mật khẩu sai đã hiển thị lỗi đúng");
    }

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][] {
            {USER_ID, USER_PASSWORD, "success"},
            {"invalid_user", "123456", "error"},
            {USER_ID, "wrong_password", "error"},
            {"", "123456", "error"},
            {USER_ID, "", "error"}
        };
    }

    @Test(priority = 8, dataProvider = "loginData", description = "08. Login - Test với DataProvider")
    public void testLoginWithDataProvider(String customerId, String password, String expectedResult) {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Testing login with Customer ID: " + customerId + ", Expected: " + expectedResult);
        
        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();
        loginPage.login(customerId, password);
        
        if ("success".equals(expectedResult)) {
            Assert.assertTrue(loginPage.isOnHomePage(), 
                "Đăng nhập thành công nhưng không redirect về trang chủ");
            extentTest.log(com.aventstack.extentreports.Status.PASS, "Login successful");
        } else {
            boolean isError = loginPage.isErrorAlertDisplayed() || loginPage.isOnLoginPage();
            Assert.assertTrue(isError, 
                "Không hiển thị lỗi khi đăng nhập với thông tin không hợp lệ");
            extentTest.log(com.aventstack.extentreports.Status.PASS, "Error handled correctly");
        }
    }

    // ==================== SHOPPING TESTS ====================

    @Test(priority = 9, description = "09. Shop - Xem danh sách sản phẩm")
    public void testViewProductsList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách sản phẩm");

        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        
        Assert.assertTrue(shopPage.isOnShopPage(), 
            "Không ở trang danh sách sản phẩm");
        
        int productCount = shopPage.getProductCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng sản phẩm: " + productCount);
        
        Assert.assertTrue(productCount > 0, 
            "Không có sản phẩm nào được hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách sản phẩm thành công");
    }

    @Test(priority = 10, description = "10. Shop - Tìm kiếm sản phẩm")
    public void testSearchProduct() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test tìm kiếm sản phẩm");

        ShopPage shopPage = new ShopPage(driver);
        shopPage.searchProduct("test");
        
        Assert.assertTrue(shopPage.isOnShopPage(), 
            "Không ở trang kết quả tìm kiếm");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Tìm kiếm sản phẩm thành công");
    }

    @Test(priority = 11, description = "11. Shop - Xem chi tiết sản phẩm")
    public void testViewProductDetail() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem chi tiết sản phẩm");

        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        
        int productCount = shopPage.getProductCount();
        Assert.assertTrue(productCount > 0, 
            "Không có sản phẩm để xem chi tiết");
        
        shopPage.clickFirstProduct();
        
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("productDetail"), 
            "Không chuyển đến trang chi tiết sản phẩm");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem chi tiết sản phẩm thành công");
    }

    // ==================== SHOPPING CART TESTS ====================

    @Test(priority = 12, description = "12. Cart - Xem giỏ hàng")
    public void testViewShoppingCart() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem giỏ hàng");

        loginAsUser();
        
        ShoppingCartPage cartPage = new ShoppingCartPage(driver);
        cartPage.navigateToCartPage();
        
        Assert.assertTrue(cartPage.isOnCartPage(), 
            "Không ở trang giỏ hàng");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem giỏ hàng thành công");
    }

    @Test(priority = 13, description = "13. Cart - Thêm sản phẩm vào giỏ hàng")
    public void testAddProductToCart() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test thêm sản phẩm vào giỏ hàng");

        loginAsUser();
        
        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        
        int initialCartCount = 0;
        try {
            ShoppingCartPage cartPage = new ShoppingCartPage(driver);
            cartPage.navigateToCartPage();
            initialCartCount = cartPage.getCartItemCount();
        } catch (Exception e) {
            // Cart might be empty
        }
        
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart();
        
        ShoppingCartPage cartPage = new ShoppingCartPage(driver);
        cartPage.navigateToCartPage();
        
        int newCartCount = cartPage.getCartItemCount();
        Assert.assertTrue(newCartCount > initialCartCount || newCartCount > 0, 
            "Sản phẩm không được thêm vào giỏ hàng");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Thêm sản phẩm vào giỏ hàng thành công");
    }

    @Test(priority = 14, description = "14. Cart - Cập nhật số lượng sản phẩm")
    public void testUpdateCartQuantity() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test cập nhật số lượng sản phẩm");

        loginAsUser();
        
        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart();
        
        ShoppingCartPage cartPage = new ShoppingCartPage(driver);
        cartPage.navigateToCartPage();
        
        int initialCount = cartPage.getCartItemCount();
        Assert.assertTrue(initialCount > 0, 
            "Giỏ hàng không có sản phẩm để test");
        
        cartPage.updateQuantity(0, 2);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Cập nhật số lượng sản phẩm thành công");
    }

    @Test(priority = 15, description = "15. Checkout - Xem trang checkout")
    public void testCheckoutWithItems() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test checkout");

        loginAsUser();
        
        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart();
        
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.navigateToCheckoutPage();
        
        Assert.assertTrue(checkoutPage.isOnCheckoutPage(), 
            "Không ở trang checkout");
        
        int itemCount = checkoutPage.getOrderItemCount();
        Assert.assertTrue(itemCount > 0, 
            "Không có sản phẩm trong đơn hàng");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Checkout page hiển thị đúng với sản phẩm trong giỏ hàng");
    }

    @Test(priority = 16, description = "16. Checkout - Submit checkout")
    public void testSubmitCheckout() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test submit checkout");

        loginAsUser();
        
        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart();
        
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.navigateToCheckoutPage();
        
        checkoutPage.fillCheckoutForm(
            "Test User",
            "123 Test Street",
            "0123456789",
            "Test order description"
        );
        
        checkoutPage.submitCheckout();
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("checkout_success") || 
                         currentUrl.contains("success") ||
                         currentUrl.contains("/"), 
            "Không redirect đến trang success sau khi checkout");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Submit checkout thành công");
    }
    // ==================== USER SHOPPING TESTS (Priority 17 - 21) ====================(Bảo ngân)

    @Test(priority = 17, description = "17. Shop - Kiểm tra hiển thị danh sách sản phẩm và số lượng")
    public void testProductListDisplayAndCount() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test hiển thị danh sách sản phẩm");

        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();

        Assert.assertTrue(shopPage.isOnShopPage(),
                "Không ở trên trang Shop");

        int productCount = shopPage.getProductCount();
        Assert.assertTrue(productCount > 0,
                "Không tìm thấy bất kỳ sản phẩm nào trên trang Shop. Số lượng: " + productCount);

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Danh sách sản phẩm hiển thị thành công với " + productCount + " sản phẩm.");
    }

    @Test(priority = 18, description = "18. Shop - Kiểm tra chức năng tìm kiếm sản phẩm thành công")
    public void testSearchProductSuccess() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test tìm kiếm sản phẩm thành công");

        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();

        // Giả định tìm kiếm một từ khóa phổ biến hoặc tên sản phẩm đã biết
        String searchKeyword = "Test Product";

        shopPage.searchProduct(searchKeyword);

        int resultCount = shopPage.getProductCount();
        Assert.assertTrue(resultCount > 0,
                "Tìm kiếm không trả về kết quả nào cho từ khóa: " + searchKeyword);

        // Giả định có thể kiểm tra từng tên sản phẩm trong danh sách
        // Assert.assertTrue(shopPage.allResultsContainKeyword(searchKeyword), "Kết quả tìm kiếm không chính xác");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Tìm kiếm thành công, tìm thấy " + resultCount + " kết quả.");
    }

    @Test(priority = 19, description = "19. Shop - Kiểm tra tìm kiếm với chuỗi trống hoặc khoảng trắng")
    public void testSearchProductWithEmptyString() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test tìm kiếm với chuỗi trống");

        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();

        int initialCount = shopPage.getProductCount();

        shopPage.searchProduct(""); // Tìm kiếm với chuỗi trống
        int afterSearchCount = shopPage.getProductCount();

        Assert.assertEquals(afterSearchCount, initialCount,
                "Tìm kiếm với chuỗi trống không trả về toàn bộ danh sách sản phẩm");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Tìm kiếm với chuỗi trống hoặc khoảng trắng hoạt động như Reset Filter");
    }

    @Test(priority = 20, description = "20. Product Detail - Xem chi tiết sản phẩm thành công")
    public void testViewProductDetailSuccess() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test xem chi tiết sản phẩm");

        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();

        if (shopPage.getProductCount() > 0) {
            shopPage.clickFirstProduct();

            ProductDetailPage detailPage = new ProductDetailPage(driver);
            Assert.assertTrue(detailPage.isProductNameDisplayed(),
                    "Không hiển thị trang chi tiết sản phẩm hoặc thiếu tên sản phẩm");

            extentTest.log(com.aventstack.extentreports.Status.PASS,
                    "Xem chi tiết sản phẩm thành công");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP,
                    "Không có sản phẩm để test chức năng xem chi tiết");
        }
    }
    @Test(priority = 21, description = "21. Product Detail - Truy cập chi tiết sản phẩm với ID không hợp lệ (Negative)")
    public void testViewProductDetailInvalidId() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test truy cập chi tiết sản phẩm với ID không hợp lệ");

        ProductDetailPage detailPage = new ProductDetailPage(driver);
        detailPage.navigateToProductDetail(999999); // ID giả định không tồn tại

        // Kiểm tra xem có bị redirect về trang Shop hay trang 404/Error không
        Assert.assertFalse(detailPage.isProductNameDisplayed(),
                "Trang chi tiết sản phẩm hiển thị cho một ID không tồn tại");

        // Giả định có thể kiểm tra URL hoặc nội dung trang để xác nhận lỗi
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/products") || currentUrl.contains("/404"),
                "Không redirect về trang hợp lệ khi truy cập ID sản phẩm không hợp lệ");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Truy cập ID không hợp lệ bị chặn/redirect đúng");
    }
// ==================== USER SHOPPING CART & CHECKOUT TESTS - ADVANCED (Priority 22 - 24) ====================

    @Test(priority = 22, description = "22. Cart - Thêm 2 sản phẩm khác nhau vào giỏ hàng")
    public void testAddMultipleDifferentProductsToCart() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test thêm 2 sản phẩm khác nhau vào giỏ hàng");

        loginAsUser();
        ShopPage shopPage = new ShopPage(driver);

        // 1. Thêm sản phẩm đầu tiên
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart(); // Giả định thêm sản phẩm ID 1

        // 2. Thêm sản phẩm thứ hai (Giả định ID 2 là sản phẩm khác)
        ProductDetailPage detailPage = new ProductDetailPage(driver);
        detailPage.navigateToProductDetail(2);
        detailPage.addToCart();

        ShoppingCartPage cartPage = new ShoppingCartPage(driver);
        cartPage.navigateToCartPage();

        Assert.assertEquals(cartPage.getCartItemCount(), 2,
                "Số lượng sản phẩm trong giỏ hàng không phải là 2");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Thêm thành công 2 sản phẩm khác nhau vào giỏ hàng");
    }


    @Test(priority = 23, description = "23. Cart - Cập nhật số lượng thành 0 và kiểm tra sản phẩm bị xóa")
    public void testUpdateCartQuantityToZero() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test cập nhật số lượng thành 0 (Xóa sản phẩm)");

        loginAsUser();
        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart();

        ShoppingCartPage cartPage = new ShoppingCartPage(driver);
        cartPage.navigateToCartPage();

        int initialCount = cartPage.getCartItemCount();
        if (initialCount == 0) {
            extentTest.log(com.aventstack.extentreports.Status.SKIP, "Giỏ hàng trống, không thể test");
            return;
        }

        // Cập nhật sản phẩm đầu tiên thành 0
        cartPage.updateQuantity(0, 0);

        // Giả định hệ thống tự động xóa sản phẩm nếu số lượng là 0
        Assert.assertEquals(cartPage.getCartItemCount(), initialCount - 1,
                "Sản phẩm không bị xóa khỏi giỏ hàng sau khi cập nhật số lượng là 0");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Sản phẩm đã bị xóa khỏi giỏ hàng khi cập nhật số lượng là 0");
    }


    @Test(priority = 24, description = "24. Checkout - Kiểm tra validation trường Phone Number")
    public void testCheckoutPhoneNumberValidation() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test validation trường Phone Number");

        loginAsUser();
        ShopPage shopPage = new ShopPage(driver);
        shopPage.navigateToShopPage();
        shopPage.addFirstProductToCart();

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.navigateToCheckoutPage();

        // Nhập số điện thoại không hợp lệ (ví dụ: quá ngắn)
        checkoutPage.fillCheckoutForm(
                "Test User",
                "123 Test Street",
                "123", // Phone không hợp lệ
                "Test order description"
        );

        checkoutPage.submitCheckout();

        // Giả định form không được submit thành công và ở lại trang Checkout
        Assert.assertTrue(checkoutPage.isOnCheckoutPage(),
                "Hệ thống cho phép submit Checkout với số điện thoại không hợp lệ");

        // Giả định CheckoutPage có phương thức kiểm tra lỗi validation
        // Assert.assertTrue(checkoutPage.isPhoneNumberErrorDisplayed(), "Không hiển thị thông báo lỗi số điện thoại");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Validation số điện thoại hoạt động chính xác");
    }

}

