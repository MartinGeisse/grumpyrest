/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest_demo.shop;

import io.github.grumpystuff.grumpyjson.builtin.helper_types.NullableField;
import io.github.grumpystuff.grumpyjson.gson.GsonBasedJsonEngine;
import io.github.grumpystuff.grumpyrest.RestApi;
import io.github.grumpystuff.grumpyrest.request.HttpMethod;
import io.github.grumpystuff.grumpyrest.request.Request;
import io.github.grumpystuff.grumpyrest.response.FinishRequestException;
import io.github.grumpystuff.grumpyrest.response.standard.StandardErrorResponse;

import java.util.List;

/**
 *
 */
public final class ShopSystem {

    // ----------------------------------------------------------------------------------------------------------------
    // database
    // ----------------------------------------------------------------------------------------------------------------

    public record Category(String name, int parentId) {}

    private final Table<Category> categories = new Table<>();

    public record Product(int categoryId, String name, String description, int unitPrice) {}

    private final Table<Product> products = new Table<>();

    // in a real application, don't store passwords like that!
    public record User(String username, String password) {}

    @SuppressWarnings("FieldCanBeLocal")
    private final Table<User> users = new Table<>();

    public record CartLineItem(int userId, int productId, int quantity) {}

    private final Table<CartLineItem> cartLineItems = new Table<>();

    public record Order(int userId) {}

    private final Table<Order> orders = new Table<>();

    // not linked to a Product anymore because the Product can change, but the OrderLineItem is unaffected by that
    public record OrderLineItem(int orderId, int quantity, String name, int unitPrice) {}

    private final Table<OrderLineItem> orderLineItems = new Table<>();

    // ----------------------------------------------------------------------------------------------------------------
    // demo data
    // ----------------------------------------------------------------------------------------------------------------

    public ShopSystem() {
        // insert some demo data

        int rootCategoryId = categories.insert(new Category("Products", -1));
        int usefulStuffCategoryId = categories.insert(new Category("Useful Stuff", rootCategoryId));
        int widgetsCategoryId = categories.insert(new Category("Widgets", usefulStuffCategoryId));
        int gadgetsCategoryId = categories.insert(new Category("Gadgets", usefulStuffCategoryId));
        int uselessStuffCategory = categories.insert(new Category("Useless Stuff", rootCategoryId));
        int garbageCategoryId = categories.insert(new Category("Garbage", uselessStuffCategory));
        int trinketsCategoryId = categories.insert(new Category("Trinkets", uselessStuffCategory));

        products.insert(new Product(widgetsCategoryId, "Left-handed Hammer", "Excellent choice for left-handed people!", 10));
        products.insert(new Product(widgetsCategoryId, "Left-handed Screwdriver", "Excellent choice for left-handed people!", 12));
        products.insert(new Product(gadgetsCategoryId, "Portable Quantum-Tunneling Device", "You can use this to shorten your commute.", 250));
        products.insert(new Product(gadgetsCategoryId, "Intelligent Self-Heating Coffee Cup", "Keeps your coffee at exactly the right temperature", 24));
        products.insert(new Product(uselessStuffCategory, "Useless Plastic Cube", "Who even builds these things?", 3));
        products.insert(new Product(garbageCategoryId, "Empty Soda Can", "We just collect them. You might get a refund for these in your country.", 1));
        products.insert(new Product(garbageCategoryId, "Plastic Wrapping", "Somebody might still need it.", 1));
        products.insert(new Product(trinketsCategoryId, "Ugly Necklace", "Bob says it isn't ugly.", 45));

        users.insert(new User("joe_user", "password123"));
        users.insert(new User("dude51", "correct horse battery staple"));

    }

    // ----------------------------------------------------------------------------------------------------------------
    // API overview
    // ----------------------------------------------------------------------------------------------------------------

    public RestApi buildApi() {
        RestApi api = new RestApi(new GsonBasedJsonEngine());
        addBrowsingRoutes(api);
        addCartRoutes(api);
        addOrderRoutes(api);
        api.seal();
        return api;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // general-purpose API types and associated helper methods
    // ----------------------------------------------------------------------------------------------------------------

    public record CategoryLink(int id, String name) {}

    @SuppressWarnings("unused")
    private CategoryLink getCategoryLink(int id) {
        return new CategoryLink(id, categories.get(id).name);
    }

    public record ProductLink(int id, String name) {}

    private ProductLink getProductLink(int id) {
        return new ProductLink(id, products.get(id).name);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // browsing the store
    // ----------------------------------------------------------------------------------------------------------------

    private void addBrowsingRoutes(RestApi api) {
        api.addRoute(HttpMethod.GET, "/categories/:id", this::handleGetCategory);
        api.addRoute(HttpMethod.GET, "/products/:id", this::handleGetProduct);
    }

    public record CategoryResponse(
        String name,
        NullableField<CategoryLink> parentCategory,
        List<CategoryLink> childCategories,
        List<ProductLink> products
    ) {}

    public CategoryResponse handleGetCategory(Request request) throws Exception {
        int id = request.getPathArguments().get(0).getValue(Integer.class);
        Category category = categories.getRestEquivalent(id);
        Category parentCategory = category.parentId() < 0 ? null : categories.get(category.parentId());
        return new CategoryResponse(
            category.name(),
            parentCategory == null ? NullableField.ofNull() :
                NullableField.ofValue(new CategoryLink(category.parentId(), parentCategory.name())),
            categories.filterMap((otherId, otherCategory) -> otherCategory.parentId() == id
                ? new CategoryLink(otherId, otherCategory.name())
                : null
            ),
            products.filterMap((productId, product) -> product.categoryId() == id
                ? new ProductLink(productId, product.name())
                : null
            )
        );
    }

    public record ProductResponse(
        CategoryLink category,
        String name,
        String description,
        int unitPrice
    ) {}

    public ProductResponse handleGetProduct(Request request) throws Exception {
        int id = request.getPathArguments().get(0).getValue(Integer.class);
        Product product = products.getRestEquivalent(id);
        Category category = categories.get(product.categoryId());
        return new ProductResponse(
            new CategoryLink(product.categoryId(), category.name()),
            product.name(),
            product.description(),
            product.unitPrice()
        );
    }

    // ----------------------------------------------------------------------------------------------------------------
    // shopping cart
    // ----------------------------------------------------------------------------------------------------------------

    private void addCartRoutes(RestApi api) {
        api.addRoute(HttpMethod.GET, "/cart/:userId", this::handleGetCart);
        api.addRoute(HttpMethod.POST, "/cart/:userId/add", this::handleAddToCart);
        api.addRoute(HttpMethod.POST, "/cart/:userId/clear", this::handleClearCart);
    }

    public record GetCartResponse(List<GetCartResponseLineItem> lineItems) {}

    public record GetCartResponseLineItem(ProductLink productLink, int quantity) {}

    public GetCartResponse handleGetCart(Request request) throws Exception {
        int userId = request.getPathArguments().get(0).getValue(Integer.class);
        return new GetCartResponse(List.copyOf(cartLineItems.filterMap((_id, cartLineItem) ->
            cartLineItem.userId == userId
                ? new GetCartResponseLineItem(getProductLink(cartLineItem.productId), cartLineItem.quantity)
                : null
        )));
    }

    // the user comes from the URL, in a later version probably from the authentication header
    public record AddToCartRequest(int productId, int quantity) {}

    public Void handleAddToCart(Request request) throws Exception {
        int userId = request.getPathArguments().get(0).getValue(Integer.class);
        AddToCartRequest requestBody = request.parseBody(AddToCartRequest.class);
        if (!products.exists(requestBody.productId)) {
            throw new FinishRequestException(new StandardErrorResponse(400, "unknown product id"));
        }
        var existingCartLineItem = cartLineItems.getFirst(c -> c.userId == userId && c.productId == requestBody.productId);
        if (existingCartLineItem == null) {
            cartLineItems.insert(new CartLineItem(userId, requestBody.productId, requestBody.quantity));
        } else {
            cartLineItems.replace(existingCartLineItem.getLeft(), new CartLineItem(userId, requestBody.productId,
                existingCartLineItem.getRight().quantity + requestBody.quantity));
        }
        return null;
    }


    // the user comes from the URL
    public Void handleClearCart(Request request) throws Exception {
        int userId = request.getPathArguments().get(0).getValue(Integer.class);
        cartLineItems.deleteIf(c -> c.userId == userId);
        return null;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // orders
    // ----------------------------------------------------------------------------------------------------------------

    private void addOrderRoutes(RestApi api) {
        api.addRoute(HttpMethod.GET, "/orders/:userId", this::handleGetOrderHistory);
        api.addRoute(HttpMethod.POST, "/orders/:userId/place", this::handlePlaceOrder);
    }

    public record GetOrderHistoryResponse(List<GetOrderHistoryResponseOrder> orders) {}

    public record GetOrderHistoryResponseOrder(List<GetOrderHistoryResponseLineItem> lineItems) {}

    public record GetOrderHistoryResponseLineItem(int quantity, String name, int unitPrice) {}

    public GetOrderHistoryResponse handleGetOrderHistory(Request request) throws Exception {
        int userId = request.getPathArguments().get(0).getValue(Integer.class);
        return new GetOrderHistoryResponse(orders.filterMap((orderId, order) ->
            order.userId != userId ? null : new GetOrderHistoryResponseOrder(orderLineItems.filterMap((_ignored, lineItem) ->
                lineItem.orderId != orderId ? null : new GetOrderHistoryResponseLineItem(lineItem.quantity, lineItem.name, lineItem.unitPrice)
            ))
        ));
    }

    public Void handlePlaceOrder(Request request) throws Exception {
        int userId = request.getPathArguments().get(0).getValue(Integer.class);
        if (!cartLineItems.existsAny(c -> c.userId == userId)) {
            throw new FinishRequestException(new StandardErrorResponse(400, "cart is empty"));
        }
        int orderId = orders.insert(new Order(userId));
        cartLineItems.foreach((_ignored, cartLineItem) -> {
            Product product = products.get(cartLineItem.productId);
            orderLineItems.insert(new OrderLineItem(orderId, cartLineItem.quantity, product.name, product.unitPrice));
        });
        cartLineItems.deleteIf(c -> c.userId == userId);
        return null;
    }

}
