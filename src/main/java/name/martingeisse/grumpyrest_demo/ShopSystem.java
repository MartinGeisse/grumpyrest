/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest_demo;

import name.martingeisse.grumpyrest.RestApi;

public final class ShopSystem {

    private final Table<Category> categories = new Table<>();
    private final Table<Product> products = new Table<>();
    private final Table<User> users = new Table<>();
    private final Table<Order> orders = new Table<>();
    private final Table<OrderLineItem> orderLineItems = new Table<>();

    public ShopSystem() {
        // insert some demo data

        int rootCategoryId = categories.insert(new Category("Products", -1));
        int usefulStuffCategoryId = categories.insert(new Category("Useful Stuff", rootCategoryId));
        int widgetsCategoryId = categories.insert(new Category("Widgets", usefulStuffCategoryId));
        int gadgetsCategoryId = categories.insert(new Category("Gadgets", usefulStuffCategoryId));
        int uselessStuffCategory = categories.insert(new Category("Useless Stuff", rootCategoryId));
        int garbageCategoryId = categories.insert(new Category("Garbage", uselessStuffCategory));
        int trinketsCategoryId = categories.insert(new Category("Trinkets", uselessStuffCategory));

        products.insert(new Product(widgetsCategoryId, "Left-handed Hammer", 10));
        products.insert(new Product(widgetsCategoryId, "Left-handed Screwdriver", 12));
        products.insert(new Product(gadgetsCategoryId, "Portable Quantum-Tunneling Device", 250));
        products.insert(new Product(gadgetsCategoryId, "Intelligent Self-Heating Coffee Cup", 24));
        products.insert(new Product(uselessStuffCategory, "Useless Plastic Cube", 3));
        products.insert(new Product(garbageCategoryId, "Empty Soda Can", 1));
        products.insert(new Product(garbageCategoryId, "Plastic Wrapping", 1));
        products.insert(new Product(trinketsCategoryId, "Ugly Necklace", 45));

        users.insert(new User("joe_user", "password123"));
        users.insert(new User("dude51", "correct horse battery staple"));

    }

    public RestApi buildApi() {
        RestApi api = new RestApi();
        api.addRoute("/categories/:id", requestCycle -> {
            int id = requestCycle.getPathArguments().get(0).getValue(Integer.class);
            return id;
        });
        return api;
    }
}
