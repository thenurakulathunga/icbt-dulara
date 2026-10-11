package com.example.printexpress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.printexpress.data.AppException;
import com.example.printexpress.data.model.OrderDetail;
import com.example.printexpress.data.model.Requests;
import com.example.printexpress.data.model.User;
import com.example.printexpress.data.repository.Repositories;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;

/**
 * Runs on a phone / emulator against the REAL SQLite database code.
 * Starts from a fresh database with the sample data (demo user = id 1).
 * Run with:  gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4.class)
public class RepositoryTest {

    private static Repositories repo;
    private static final long DEMO = 1;

    @BeforeClass
    public static void freshDatabase() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase("printxpress.db");
        repo = Repositories.get(context);
    }

    // ---------- Authentication ----------

    @Test
    public void demoUserCanLogInWithEmailOrPhone() {
        assertEquals("Demo Customer", repo.users.login("demo@printxpress.lk", "Demo@123").getFullName());
        assertEquals(DEMO, repo.users.login("0771234567", "Demo@123").getUserId());
        assertEquals(DEMO, repo.users.login("+94771234567", "Demo@123").getUserId());
    }

    @Test
    public void wrongPasswordIsRejected() {
        expectError("Incorrect email/phone or password", () -> repo.users.login("demo@printxpress.lk", "wrong"));
    }

    @Test
    public void registerStoresNormalisedPhoneAndRejectsDuplicates() {
        User user = repo.users.register(new Requests.Register("Kamal Perera", "Kamal@Test.lk", "+94712345678", "Pass1234"));
        assertEquals("kamal@test.lk", user.getEmail());
        assertEquals("0712345678", user.getPhone());
        assertEquals(1, repo.notifications.countUnread(user.getUserId()));   // welcome message

        expectError("already registered",
                () -> repo.users.register(new Requests.Register("Someone Else", "kamal@test.lk", "0719999999", "Pass1234")));
    }

    @Test
    public void invalidRegistrationIsRejectedByTheRepositoryToo() {
        expectError("Sri Lankan mobile",
                () -> repo.users.register(new Requests.Register("Nimal Silva", "nimal@test.lk", "123", "Pass1234")));
    }

    // ---------- Ordering rules ----------

    @Test
    public void validOrderIsPricedCorrectly() {
        // 600 standard cards (12.00) + gloss laminated (+4.00), BULK15, delivery
        OrderDetail order = repo.orders.placeOrder(DEMO, order(600, "DELIVERY", 1L, 5, "BULK15"));
        assertEquals(9600.00, order.getSubtotal(), 0.001);
        assertEquals(1440.00, order.getDiscountAmount(), 0.001);
        assertEquals(350.00, order.getDeliveryFee(), 0.001);
        assertEquals(8510.00, order.getTotalAmount(), 0.001);
        assertEquals("PENDING", order.getStatus());
        assertEquals("BULK15", order.getPromoCode());
        assertNotNull(order.getDeliveryAddress());
    }

    @Test
    public void quantityBelowMinimumIsRejected() {
        expectError("between 50 and 5000", () -> repo.orders.placeOrder(DEMO, order(10, "PICKUP", null, 5, null)));
    }

    @Test
    public void dateBeforeTurnaroundIsRejected() {
        expectError("earliest available date", () -> repo.orders.placeOrder(DEMO, order(100, "PICKUP", null, 0, null)));
    }

    @Test
    public void deliveryNeedsAnAddress() {
        expectError("delivery address", () -> repo.orders.placeOrder(DEMO, order(100, "DELIVERY", null, 5, null)));
    }

    @Test
    public void promoBelowMinimumQuantityIsRejected() {
        expectError("needs at least 500", () -> repo.orders.placeOrder(DEMO, order(100, "PICKUP", null, 5, "BULK15")));
    }

    @Test
    public void artworkIsRequired() {
        Requests.Order req = order(100, "PICKUP", null, 5, null);
        req.customText = "  ";
        expectError("Upload a design or enter the text", () -> repo.orders.placeOrder(DEMO, req));
    }

    // ---------- Changing orders ----------

    @Test
    public void pendingOrderCanBeRescheduledAndCancelled() {
        long id = repo.orders.placeOrder(DEMO, order(100, "PICKUP", null, 5, null)).getOrderId();
        String newDate = LocalDate.now().plusDays(9).toString();
        assertEquals(newDate, repo.orders.reschedule(DEMO, id, newDate).getScheduledDate());
        assertEquals("CANCELLED", repo.orders.cancel(DEMO, id).getStatus());
    }

    @Test
    public void orderCannotBeCancelledAfterPrintingStarts() {
        long id = repo.orders.placeOrder(DEMO, order(100, "PICKUP", null, 5, null)).getOrderId();
        repo.orders.updateStatus(id, "PRINTING");
        assertFalse(repo.orders.getOrder(DEMO, id).isCanChange());
        expectError("can no longer be changed", () -> repo.orders.cancel(DEMO, id));
    }

    @Test
    public void customerCannotSeeSomeoneElsesOrder() {
        long id = repo.orders.placeOrder(DEMO, order(100, "PICKUP", null, 5, null)).getOrderId();
        expectError("Order not found", () -> repo.orders.getOrder(999, id));
    }

    // ---------- Other data ----------

    @Test
    public void addressPostalCodeIsValidated() {
        expectError("5 digits",
                () -> repo.addresses.save(DEMO, 0, new Requests.AddressBody("Shop", "12 Main Street", "Kandy", "2000", false)));
    }

    @Test
    public void sampleContentIsLoaded() {
        assertEquals(7, repo.catalog.getCategories().size());
        assertEquals(2, repo.catalog.getProducts(null, "mug").size());
        assertTrue(repo.content.getGuidelines().size() >= 6);
        assertTrue(repo.content.getFaqs().size() >= 6);
        assertFalse(repo.content.getActivePromotions().isEmpty());
    }

    // ---------- helpers ----------

    /** Standard Business Cards (product 1), standard size (1), gloss laminated (4), custom text. */
    private static Requests.Order order(int quantity, String fulfilment, Long addressId, int daysAhead, String promo) {
        Requests.Order r = new Requests.Order();
        r.productId = 1;
        r.sizeOptionId = 1L;
        r.materialOptionId = 4L;
        r.customText = "Kamal Perera - CEO";
        r.quantity = quantity;
        r.fulfilmentType = fulfilment;
        r.addressId = addressId;
        r.scheduledDate = LocalDate.now().plusDays(daysAhead).toString();
        r.promoCode = promo;
        return r;
    }

    private static void expectError(String messagePart, Runnable action) {
        try {
            action.run();
            fail("Expected an error containing: " + messagePart);
        } catch (AppException e) {
            assertTrue("Got: " + e.getMessage(), e.getMessage().contains(messagePart));
        }
    }
}
