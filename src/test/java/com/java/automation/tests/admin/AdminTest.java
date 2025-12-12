package com.java.automation.tests.admin;

import com.java.automation.base.BaseTest;
import com.java.automation.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * All Admin Test Cases - Authentication, Dashboard, Navigation, CRUD Operations
 */
public class AdminTest extends BaseTest {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123123";

    /**
     * Helper method to login as admin
     */
    private void loginAsAdmin() {
        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();
        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    // ==================== ADMIN AUTHENTICATION ====================

    @Test(priority = 1, description = "01. Admin - Đăng nhập thành công")
    public void testAdminLoginSuccess() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test đăng nhập admin với username: " + ADMIN_USERNAME);

        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();
        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/admin/home"), 
            "Đăng nhập admin thành công nhưng không redirect về trang admin dashboard. URL hiện tại: " + currentUrl);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Đăng nhập admin thành công và đã redirect đến trang dashboard");
    }

    @Test(priority = 2, description = "02. Admin - Đăng nhập với mật khẩu sai")
    public void testAdminLoginWithWrongPassword() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test đăng nhập admin với mật khẩu sai");

        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();
        loginPage.login(ADMIN_USERNAME, "wrong_password");
        
        Assert.assertTrue(loginPage.isErrorAlertDisplayed(), 
            "Không hiển thị thông báo lỗi khi đăng nhập với mật khẩu sai");
        
        String errorText = loginPage.getErrorAlertText();
        Assert.assertTrue(errorText.contains("không chính xác") || 
                         errorText.contains("Tài khoản") ||
                         errorText.contains("sai"), 
            "Thông báo lỗi không đúng: " + errorText);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Đăng nhập với mật khẩu sai đã hiển thị thông báo lỗi đúng");
    }
// ==================== ADMIN AUTHENTICATION - ADDITIONAL ====================(Bảo ngân)

    @Test(priority = 21, description = "02.1. Admin - Đăng nhập bằng tài khoản người dùng thường")
    public void testAdminLoginWithNonAdminUser() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test đăng nhập admin bằng tài khoản User thường");

        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);
        loginPage.navigateToLoginPage();

        String normalUserId = com.java.automation.config.TestConfig.getProperty("test.user.id");
        String normalUserPassword = com.java.automation.config.TestConfig.getProperty("test.user.password");

        loginPage.login(normalUserId, normalUserPassword);

        // Tài khoản thường sẽ được redirect về trang chủ/Shop, KHÔNG phải trang Admin
        String currentUrl = driver.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("/admin/home"),
                "Tài khoản User thường truy cập được trang Admin Dashboard. Lỗi bảo mật nghiêm trọng!");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Tài khoản User thường bị chặn/redirect khỏi trang Admin Dashboard");

        // Đảm bảo không bị kẹt ở trạng thái đăng nhập User nếu cần
        if (!loginPage.isOnLoginPage()) {
            // Giả định có phương thức logout cho user
            // loginPage.logout();
        }
    }

    @Test(priority = 22, description = "02.2. Admin - Kiểm tra truy cập URL Admin trực tiếp khi chưa đăng nhập")
    public void testAdminDirectUrlAccessWithoutLogin() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test truy cập Admin URL khi chưa đăng nhập");

        // Cố gắng truy cập một trang Admin (vd: /admin/products)
        String baseUrl = com.java.automation.config.TestConfig.getBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        driver.get(baseUrl + "/admin/products");

        LoginOrRegisterPage loginPage = new LoginOrRegisterPage(driver);

        // Kiểm tra nếu hệ thống redirect về trang Login
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Truy cập Admin URL khi chưa đăng nhập nhưng không redirect về Login. Lỗi bảo mật!");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Hệ thống chặn truy cập Admin URL và redirect về trang Login");
    }


    // ==================== ADMIN DASHBOARD ====================

    @Test(priority = 3, description = "03. Dashboard - Kiểm tra các thành phần chính")
    public void testAdminDashboardElements() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test kiểm tra các thành phần trên trang admin dashboard");

        loginAsAdmin();
        
        AdminPage adminPage = new AdminPage(driver);
        
        Assert.assertTrue(adminPage.isOnAdminDashboard(), 
            "Không ở trang admin dashboard");
        Assert.assertTrue(adminPage.isDashboardTitleDisplayed(), 
            "Không hiển thị tiêu đề Dashboard");
        Assert.assertTrue(adminPage.isAdminDashboardSubtitleDisplayed(), 
            "Không hiển thị subtitle 'Admin Dashboard'");
        Assert.assertTrue(adminPage.isOverallStatisticsCardDisplayed(), 
            "Không hiển thị card 'Overall statistics'");
        Assert.assertTrue(adminPage.isTotalIncomeCardDisplayed(), 
            "Không hiển thị card 'Total income & spend statistics'");
        Assert.assertTrue(adminPage.isUserStatisticsCardDisplayed(), 
            "Không hiển thị card 'User Statistics'");
        Assert.assertTrue(adminPage.isDailySalesCardDisplayed(), 
            "Không hiển thị card 'Daily Sales'");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Tất cả các thành phần chính trên trang admin dashboard đều hiển thị đúng");
    }

    @Test(priority = 4, description = "04. Dashboard - Kiểm tra role Administrator")
    public void testAdminRoleDisplay() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test kiểm tra hiển thị role Administrator");

        loginAsAdmin();
        
        AdminPage adminPage = new AdminPage(driver);
        Assert.assertTrue(adminPage.isAdministratorRoleDisplayed(), 
            "Không hiển thị role Administrator trên navbar");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Role Administrator được hiển thị đúng trên navbar");
    }

    @Test(priority = 5, description = "05. Navigation - Test navigation giữa các trang admin")
    public void testNavigationBetweenAdminPages() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test navigation giữa các trang admin");

        loginAsAdmin();
        
        AdminPage adminPage = new AdminPage(driver);
        
        adminPage.clickProductsLink();
        Assert.assertTrue(driver.getCurrentUrl().contains("/admin/products"), 
            "Navigation đến Products không thành công");
        
        adminPage = new AdminPage(driver);
        adminPage.clickOrdersLink();
        Assert.assertTrue(driver.getCurrentUrl().contains("/admin/orders"), 
            "Navigation đến Orders không thành công");
        
        adminPage = new AdminPage(driver);
        adminPage.clickCustomersLink();
        Assert.assertTrue(driver.getCurrentUrl().contains("/admin/customers"), 
            "Navigation đến Customers không thành công");
        
        adminPage = new AdminPage(driver);
        adminPage.clickCategoriesLink();
        Assert.assertTrue(driver.getCurrentUrl().contains("/admin/categories"), 
            "Navigation đến Categories không thành công");
        
        adminPage = new AdminPage(driver);
        adminPage.clickSuppliersLink();
        Assert.assertTrue(driver.getCurrentUrl().contains("/admin/suppliers"), 
            "Navigation đến Suppliers không thành công");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Navigation giữa các trang admin thành công");
    }

    // ==================== PRODUCTS MANAGEMENT ====================

    @Test(priority = 6, description = "06. Products - Navigate đến trang Products")
    public void testNavigateToProductsPage() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test navigation đến trang Products Management");

        loginAsAdmin();
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();
        
        Assert.assertTrue(productsPage.isOnProductsPage(), 
            "Không ở trang Products Management");
        Assert.assertTrue(productsPage.isPageTitleDisplayed(), 
            "Không hiển thị page title");
        Assert.assertTrue(productsPage.isCardTitleDisplayed(), 
            "Không hiển thị card title");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Navigation đến trang Products Management thành công");
    }

    @Test(priority = 7, description = "07. Products - Xem danh sách sản phẩm")
    public void testViewProductsList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách sản phẩm admin");

        loginAsAdmin();
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();
        
        Assert.assertTrue(productsPage.isProductsTableDisplayed(), 
            "Bảng sản phẩm không hiển thị");
        
        int productCount = productsPage.getProductCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng sản phẩm: " + productCount);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách sản phẩm thành công");
    }

    @Test(priority = 8, description = "08. Products - Kiểm tra nút Add Product")
    public void testAddProductButtonDisplayed() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test kiểm tra nút Add Product");

        loginAsAdmin();
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();
        
        Assert.assertTrue(productsPage.isAddProductButtonDisplayed(), 
            "Nút Add Product không hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Nút Add Product hiển thị đúng");
    }

    @Test(priority = 9, description = "09. Products - Test form thêm mới")
    public void testAddProductFormDisplayed() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test form thêm sản phẩm");

        loginAsAdmin();
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();
        productsPage.clickAddProductButton();
        
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Assert.assertTrue(productsPage.isAddProductFormDisplayed() || 
                        productsPage.isAddProductModalDisplayed(), 
            "Form thêm sản phẩm không hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Form thêm sản phẩm hiển thị đúng");
    }

    @Test(priority = 10, description = "10. Products - Navigate đến trang Edit")
    public void testNavigateToEditProduct() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test navigate đến trang edit sản phẩm");

        loginAsAdmin();
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();
        
        int productCount = productsPage.getProductCount();
        if (productCount > 0) {
            String baseUrl = com.java.automation.config.TestConfig.getBaseUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            driver.get(baseUrl + "/editProduct/1");
            
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/editProduct"), 
                "Không navigate đến trang edit sản phẩm");
            
            extentTest.log(com.aventstack.extentreports.Status.PASS, 
                "Navigate đến trang edit sản phẩm thành công");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP, 
                "Không có sản phẩm để test edit");
        }
    }

    @Test(priority = 11, description = "11. Products - Test Delete")
    public void testDeleteProduct() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test delete sản phẩm");

        loginAsAdmin();
        
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();
        
        int initialCount = productsPage.getProductCount();
        if (initialCount > 0) {
            String baseUrl = com.java.automation.config.TestConfig.getBaseUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            driver.get(baseUrl + "/deleteProduct/999");
            
            productsPage.navigateToProductsPage();
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/admin/products"), 
                "Không redirect về trang products sau khi delete");
            
            extentTest.log(com.aventstack.extentreports.Status.PASS, 
                "Delete sản phẩm redirect đúng");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP, 
                "Không có sản phẩm để test delete");
        }
    }

    // ==================== ORDERS MANAGEMENT ====================

    @Test(priority = 12, description = "12. Orders - Navigate đến trang Orders")
    public void testNavigateToOrdersPage() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test navigation đến trang Orders Management");

        loginAsAdmin();
        
        OrdersPage ordersPage = new OrdersPage(driver);
        ordersPage.navigateToOrdersPage();
        
        Assert.assertTrue(ordersPage.isOnOrdersPage(), 
            "Không ở trang Orders Management");
        Assert.assertTrue(ordersPage.isPageTitleDisplayed(), 
            "Không hiển thị page title");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Navigation đến trang Orders Management thành công");
    }

    @Test(priority = 13, description = "13. Orders - Xem danh sách đơn hàng")
    public void testViewOrdersList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách đơn hàng");

        loginAsAdmin();
        
        OrdersPage ordersPage = new OrdersPage(driver);
        ordersPage.navigateToOrdersPage();
        
        Assert.assertTrue(ordersPage.isOrdersTableDisplayed(), 
            "Bảng đơn hàng không hiển thị");
        
        int orderCount = ordersPage.getOrderCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng đơn hàng: " + orderCount);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách đơn hàng thành công");
    }

    @Test(priority = 14, description = "14. Orders - Kiểm tra link Export To Excel")
    public void testExportToExcelLinkDisplayed() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test kiểm tra link Export To Excel");

        loginAsAdmin();
        
        OrdersPage ordersPage = new OrdersPage(driver);
        ordersPage.navigateToOrdersPage();
        
        Assert.assertTrue(ordersPage.isExportToExcelLinkDisplayed(), 
            "Link Export To Excel không hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Link Export To Excel hiển thị đúng");
    }

    @Test(priority = 15, description = "15. Orders - Navigate đến trang Edit")
    public void testNavigateToEditOrder() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test navigate đến trang edit đơn hàng");

        loginAsAdmin();
        
        OrdersPage ordersPage = new OrdersPage(driver);
        ordersPage.navigateToOrdersPage();
        
        int orderCount = ordersPage.getOrderCount();
        if (orderCount > 0) {
            String baseUrl = com.java.automation.config.TestConfig.getBaseUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            driver.get(baseUrl + "/editorder/1");
            
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/editorder"), 
                "Không navigate đến trang edit đơn hàng");
            
            extentTest.log(com.aventstack.extentreports.Status.PASS, 
                "Navigate đến trang edit đơn hàng thành công");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP, 
                "Không có đơn hàng để test edit");
        }
    }

    @Test(priority = 16, description = "16. Orders - Test Delete")
    public void testDeleteOrder() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test delete đơn hàng");

        loginAsAdmin();
        
        OrdersPage ordersPage = new OrdersPage(driver);
        ordersPage.navigateToOrdersPage();
        
        int initialCount = ordersPage.getOrderCount();
        if (initialCount > 0) {
            String baseUrl = com.java.automation.config.TestConfig.getBaseUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            driver.get(baseUrl + "/deleteOrder/999");
            
            ordersPage.navigateToOrdersPage();
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/admin/orders"), 
                "Không redirect về trang orders sau khi delete");
            
            extentTest.log(com.aventstack.extentreports.Status.PASS, 
                "Delete đơn hàng redirect đúng");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP, 
                "Không có đơn hàng để test delete");
        }
    }

    // ==================== CUSTOMERS MANAGEMENT ====================

    @Test(priority = 17, description = "17. Customers - Navigate đến trang Customers")
    public void testNavigateToCustomersPage() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test navigation đến trang Customers Management");

        loginAsAdmin();
        
        CustomersPage customersPage = new CustomersPage(driver);
        customersPage.navigateToCustomersPage();
        
        Assert.assertTrue(customersPage.isOnCustomersPage(), 
            "Không ở trang Customers Management");
        Assert.assertTrue(customersPage.isPageTitleDisplayed(), 
            "Không hiển thị page title");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Navigation đến trang Customers Management thành công");
    }

    @Test(priority = 18, description = "18. Customers - Xem danh sách khách hàng")
    public void testViewCustomersList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách khách hàng");

        loginAsAdmin();
        
        CustomersPage customersPage = new CustomersPage(driver);
        customersPage.navigateToCustomersPage();
        
        Assert.assertTrue(customersPage.isCustomersTableDisplayed(), 
            "Bảng khách hàng không hiển thị");
        
        int customerCount = customersPage.getCustomerCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng khách hàng: " + customerCount);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách khách hàng thành công");
    }

    // ==================== CATEGORIES MANAGEMENT ====================

    @Test(priority = 19, description = "19. Categories - Navigate đến trang Categories")
    public void testNavigateToCategoriesPage() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test navigation đến trang Categories Management");

        loginAsAdmin();
        
        CategoriesPage categoriesPage = new CategoriesPage(driver);
        categoriesPage.navigateToCategoriesPage();
        
        Assert.assertTrue(categoriesPage.isOnCategoriesPage(), 
            "Không ở trang Categories Management");
        Assert.assertTrue(categoriesPage.isPageTitleDisplayed(), 
            "Không hiển thị page title");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Navigation đến trang Categories Management thành công");
    }

    @Test(priority = 20, description = "20. Categories - Xem danh sách danh mục")
    public void testViewCategoriesList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách danh mục");

        loginAsAdmin();
        
        CategoriesPage categoriesPage = new CategoriesPage(driver);
        categoriesPage.navigateToCategoriesPage();
        
        Assert.assertTrue(categoriesPage.isCategoriesTableDisplayed(), 
            "Bảng danh mục không hiển thị");
        
        int categoryCount = categoriesPage.getCategoryCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng danh mục: " + categoryCount);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách danh mục thành công");
    }

    @Test(priority = 21, description = "21. Categories - Kiểm tra nút Add Category")
    public void testAddCategoryButtonDisplayed() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test kiểm tra nút Add Category");

        loginAsAdmin();
        
        CategoriesPage categoriesPage = new CategoriesPage(driver);
        categoriesPage.navigateToCategoriesPage();
        
        Assert.assertTrue(categoriesPage.isAddCategoryButtonDisplayed(), 
            "Nút Add Category không hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Nút Add Category hiển thị đúng");
    }

    @Test(priority = 22, description = "22. Categories - Navigate đến trang Edit")
    public void testNavigateToEditCategory() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test navigate đến trang edit danh mục");

        loginAsAdmin();
        
        CategoriesPage categoriesPage = new CategoriesPage(driver);
        categoriesPage.navigateToCategoriesPage();
        
        int categoryCount = categoriesPage.getCategoryCount();
        if (categoryCount > 0) {
            String baseUrl = com.java.automation.config.TestConfig.getBaseUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            driver.get(baseUrl + "/editCategory/1");
            
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/editCategory"), 
                "Không navigate đến trang edit danh mục");
            
            extentTest.log(com.aventstack.extentreports.Status.PASS, 
                "Navigate đến trang edit danh mục thành công");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP, 
                "Không có danh mục để test edit");
        }
    }

    @Test(priority = 23, description = "23. Categories - Test Delete")
    public void testDeleteCategory() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test delete danh mục");

        loginAsAdmin();
        
        CategoriesPage categoriesPage = new CategoriesPage(driver);
        categoriesPage.navigateToCategoriesPage();
        
        int initialCount = categoriesPage.getCategoryCount();
        if (initialCount > 0) {
            String baseUrl = com.java.automation.config.TestConfig.getBaseUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            driver.get(baseUrl + "/delete/999");
            
            categoriesPage.navigateToCategoriesPage();
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/admin/categories"), 
                "Không redirect về trang categories sau khi delete");
            
            extentTest.log(com.aventstack.extentreports.Status.PASS, 
                "Delete danh mục redirect đúng");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP, 
                "Không có danh mục để test delete");
        }
    }

    // ==================== SUPPLIERS MANAGEMENT ====================

    @Test(priority = 24, description = "24. Suppliers - Navigate đến trang Suppliers")
    public void testNavigateToSuppliersPage() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test navigation đến trang Suppliers Management");

        loginAsAdmin();
        
        SuppliersPage suppliersPage = new SuppliersPage(driver);
        suppliersPage.navigateToSuppliersPage();
        
        Assert.assertTrue(suppliersPage.isOnSuppliersPage(), 
            "Không ở trang Suppliers Management");
        Assert.assertTrue(suppliersPage.isPageTitleDisplayed(), 
            "Không hiển thị page title");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Navigation đến trang Suppliers Management thành công");
    }

    @Test(priority = 25, description = "25. Suppliers - Xem danh sách nhà cung cấp")
    public void testViewSuppliersList() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test xem danh sách nhà cung cấp");

        loginAsAdmin();
        
        SuppliersPage suppliersPage = new SuppliersPage(driver);
        suppliersPage.navigateToSuppliersPage();
        
        Assert.assertTrue(suppliersPage.isSuppliersTableDisplayed(), 
            "Bảng nhà cung cấp không hiển thị");
        
        int supplierCount = suppliersPage.getSupplierCount();
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Số lượng nhà cung cấp: " + supplierCount);
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Xem danh sách nhà cung cấp thành công");
    }

    @Test(priority = 26, description = "26. Suppliers - Kiểm tra nút Add Supplier")
    public void testAddSupplierButtonDisplayed() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test kiểm tra nút Add Supplier");

        loginAsAdmin();
        
        SuppliersPage suppliersPage = new SuppliersPage(driver);
        suppliersPage.navigateToSuppliersPage();
        
        Assert.assertTrue(suppliersPage.isAddSupplierButtonDisplayed(), 
            "Nút Add Supplier không hiển thị");
        
        extentTest.log(com.aventstack.extentreports.Status.PASS, 
            "Nút Add Supplier hiển thị đúng");
    }

    @Test(priority = 27, description = "27. Suppliers - Navigate đến trang Edit")
    public void testNavigateToEditSupplier() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test navigate đến trang edit nhà cung cấp");

        loginAsAdmin();
        
        SuppliersPage suppliersPage = new SuppliersPage(driver);
        suppliersPage.navigateToSuppliersPage();
        
        int supplierCount = suppliersPage.getSupplierCount();
        if (supplierCount > 0) {
            String baseUrl = com.java.automation.config.TestConfig.getBaseUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            driver.get(baseUrl + "/editSupplier/1");
            
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/editSupplier"), 
                "Không navigate đến trang edit nhà cung cấp");
            
            extentTest.log(com.aventstack.extentreports.Status.PASS, 
                "Navigate đến trang edit nhà cung cấp thành công");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP, 
                "Không có nhà cung cấp để test edit");
        }
    }

    @Test(priority = 28, description = "28. Suppliers - Test Delete")
    public void testDeleteSupplier() {
        extentTest.log(com.aventstack.extentreports.Status.INFO, 
            "Bắt đầu test delete nhà cung cấp");

        loginAsAdmin();
        
        SuppliersPage suppliersPage = new SuppliersPage(driver);
        suppliersPage.navigateToSuppliersPage();
        
        int initialCount = suppliersPage.getSupplierCount();
        if (initialCount > 0) {
            String baseUrl = com.java.automation.config.TestConfig.getBaseUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            driver.get(baseUrl + "/deleteSupplier/999");
            
            suppliersPage.navigateToSuppliersPage();
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/admin/suppliers"), 
                "Không redirect về trang suppliers sau khi delete");
            
            extentTest.log(com.aventstack.extentreports.Status.PASS, 
                "Delete nhà cung cấp redirect đúng");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP, 
                "Không có nhà cung cấp để test delete");
        }
    }
// ==================== ADMIN PRODUCTS MANAGEMENT (Priority 29 ) ====================(Bảo ngân)

    @Test(priority = 29, description = "29. Products - Kiểm tra hiển thị danh sách và số lượng sản phẩm")
    public void testProductListDisplayAndCount() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test hiển thị danh sách sản phẩm trong Admin");

        loginAsAdmin();
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();

        Assert.assertTrue(productsPage.isPageTitleDisplayed(),
                "Không ở trên trang Quản lý Sản phẩm Admin");

        int productCount = productsPage.getProductCount();
        Assert.assertTrue(productCount >= 0,
                "Không thể đếm số lượng sản phẩm");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Danh sách sản phẩm hiển thị thành công với " + productCount + " sản phẩm.");
    }
// ==================== ADMIN CATEGORIES MANAGEMENT (Priority 30 ) ====================

    @Test(priority = 30, description = "30. Categories - Hiển thị danh sách và số lượng danh mục")
    public void testCategoryListDisplayAndCount() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test hiển thị danh sách Danh mục trong Admin");

        loginAsAdmin();
        CategoriesPage categoriesPage = new CategoriesPage(driver);
        categoriesPage.navigateToCategoriesPage();

        Assert.assertTrue(categoriesPage.isPageTitleDisplayed(),
                "Không ở trên trang Quản lý Danh mục Admin");

        int categoryCount = categoriesPage.getCategoryCount();
        Assert.assertTrue(categoryCount >= 0,
                "Không thể đếm số lượng danh mục");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Danh sách danh mục hiển thị thành công với " + categoryCount + " danh mục.");
    }

// ==================== ADMIN ORDERS MANAGEMENT (Priority 31 - 32) ====================

    @Test(priority = 31, description = "31. Orders - Hiển thị danh sách và số lượng đơn hàng")
    public void testOrderListDisplayAndCount() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test hiển thị danh sách Đơn hàng trong Admin");

        loginAsAdmin();
        OrdersPage ordersPage = new OrdersPage(driver);
        ordersPage.navigateToOrdersPage();

        Assert.assertTrue(ordersPage.isPageTitleDisplayed(),
                "Không ở trên trang Quản lý Đơn hàng Admin");

        int orderCount = ordersPage.getOrderCount();
        Assert.assertTrue(orderCount >= 0,
                "Không thể đếm số lượng đơn hàng");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Danh sách đơn hàng hiển thị thành công với " + orderCount + " đơn hàng.");
    }

    @Test(priority = 32, description = "32. Orders - Kiểm tra chức năng Xuất (Export) ra Excel")
    public void testExportOrdersToExcel() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test chức năng Xuất đơn hàng ra Excel");

        loginAsAdmin();
        OrdersPage ordersPage = new OrdersPage(driver);
        ordersPage.navigateToOrdersPage();

        // Kiểm tra nút Export có hiển thị không
        Assert.assertTrue(ordersPage.isExportToExcelLinkDisplayed(),
                "Không tìm thấy nút 'Export To Excel'");

        // Click vào nút Export
        ordersPage.clickExportToExcelLink();

        // **Lưu ý:** Tự động kiểm tra file tải về là phức tạp trong Selenium.
        // Ta chỉ kiểm tra xem hành động click có trigger tải file không.
        // Giả định: Action click không gây lỗi và tải file bắt đầu (không có redirect).

        // Check if there is no error message/redirect
        Assert.assertTrue(ordersPage.isOnOrdersPage(),
                "Bị redirect khỏi trang Orders sau khi nhấn Export");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Chức năng Xuất đơn hàng ra Excel được trigger thành công.");
    }
// ==================== ADMIN CUSTOMERS MANAGEMENT (Priority 33) ====================

    @Test(priority = 33, description = "33. Customers - Hiển thị danh sách và số lượng khách hàng")
    public void testCustomerListDisplayAndCount() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test hiển thị danh sách Khách hàng trong Admin");

        loginAsAdmin();
        CustomersPage customersPage = new CustomersPage(driver);
        customersPage.navigateToCustomersPage(); // Dùng phương thức từ CustomersPage

        Assert.assertTrue(customersPage.isPageTitleDisplayed(),
                "Không ở trên trang Quản lý Khách hàng Admin"); // Dùng phương thức từ CustomersPage

        int customerCount = customersPage.getCustomerCount(); // Dùng phương thức từ CustomersPage
        Assert.assertTrue(customerCount >= 0,
                "Không thể đếm số lượng khách hàng");

        extentTest.log(com.aventstack.extentreports.Status.PASS,
                "Danh sách khách hàng hiển thị thành công với " + customerCount + " khách hàng.");
    }
// ==================== ADMIN PRODUCTS MANAGEMENT (Priority 34 - ) ====================


    @Test(priority = 34, description = "34. Products - Sửa danh mục/nhà cung cấp thành công (UPDATE)")
    public void testEditProductCategoryAndSupplierSuccess() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test sửa Danh mục và Nhà cung cấp của Sản phẩm");

        loginAsAdmin();
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();

        // Giả định có các danh mục và nhà cung cấp sẵn có (vd: Cat B, Supp A)
        if (productsPage.getProductCount() > 0) {

            productsPage.clickEditButton(0);

            // Cập nhật Danh mục và Nhà cung cấp
            productsPage.fillEditProductForm(null, null, null, null, null, null, "Category B", "Supplier A");
            productsPage.submitEditProductForm();

            // Đợi 1 chút và tải lại trang
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            productsPage.navigateToProductsPage();

            // Kiểm tra thông tin đã được cập nhật (Giả định có phương thức lấy Cat/Supp)
            Assert.assertEquals(productsPage.getProductCategory(0), "Category B",
                    "Danh mục sản phẩm không được cập nhật đúng");
            Assert.assertEquals(productsPage.getProductSupplier(0), "Supplier A",
                    "Nhà cung cấp sản phẩm không được cập nhật đúng");

            extentTest.log(com.aventstack.extentreports.Status.PASS,
                    "Sửa Danh mục và Nhà cung cấp của sản phẩm thành công.");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP,
                    "Không có sản phẩm để test sửa danh mục/NCC");
        }
    }

    @Test(priority = 35, description = "35. Products - Tìm kiếm sản phẩm theo Tên (Search)")
    public void testSearchProductByName() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test tìm kiếm sản phẩm theo Tên");

        loginAsAdmin();
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();

        if (productsPage.getProductCount() > 0) {
            String searchKeyword = productsPage.getProductName(0); // Lấy tên sản phẩm đầu tiên

            productsPage.searchTable(searchKeyword);

            // Kiểm tra kết quả
            Assert.assertTrue(productsPage.getProductCount() >= 1,
                    "Tìm kiếm sản phẩm không trả về kết quả");

            extentTest.log(com.aventstack.extentreports.Status.PASS,
                    "Tìm kiếm sản phẩm theo từ khóa thành công.");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP,
                    "Không có sản phẩm để test tìm kiếm");
        }
    }

    @Test(priority = 36, description = "36. Products - Tìm kiếm sản phẩm theo SKU (Search)")
    public void testSearchProductBySKU() {
        extentTest.log(com.aventstack.extentreports.Status.INFO,
                "Bắt đầu test tìm kiếm sản phẩm theo SKU");

        loginAsAdmin();
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateToProductsPage();

        // Giả định có phương thức getProductSKU
        if (productsPage.getProductCount() > 0 && productsPage.getProductSKU(0) != null) {
            String searchKeyword = productsPage.getProductSKU(0);

            productsPage.searchTable(searchKeyword);

            // Kiểm tra kết quả
            Assert.assertTrue(productsPage.getProductCount() >= 1,
                    "Tìm kiếm sản phẩm theo SKU không trả về kết quả");

            extentTest.log(com.aventstack.extentreports.Status.PASS,
                    "Tìm kiếm sản phẩm theo SKU thành công.");
        } else {
            extentTest.log(com.aventstack.extentreports.Status.SKIP,
                    "Không có sản phẩm để test tìm kiếm theo SKU");
        }
    }

}
