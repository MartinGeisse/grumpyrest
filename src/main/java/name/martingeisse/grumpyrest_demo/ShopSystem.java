/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest_demo;

import com.google.common.collect.ImmutableList;
import name.martingeisse.grumpyjson.builtin.helper_types.NullableField;
import name.martingeisse.grumpyrest.RestApi;

/**
 *
 */
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

    public RestApi buildApi() {
        RestApi api = new RestApi();

        api.addRoute("/categories/:id", requestCycle -> {
            int id = requestCycle.getPathArguments().get(0).getValue(Integer.class);
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
        });

        api.addRoute("/products/:id", requestCycle -> {
            int id = requestCycle.getPathArguments().get(0).getValue(Integer.class);
            Product product = products.getRestEquivalent(id);
            Category category = categories.get(product.categoryId());
            return new ProductResponse(
                    new CategoryLink(product.categoryId(), category.name()),
                    product.name(),
                    product.description(),
                    product.unitPrice()
            );
        });

        return api;
    }

    record CategoryLink(int id, String name) {}

    record ProductLink(int id, String name) {}

    record CategoryResponse(
            String name,
            NullableField<CategoryLink> parentCategory,
            ImmutableList<CategoryLink> childCategories,
            ImmutableList<ProductLink> products
    ) {}

    record ProductResponse(
            CategoryLink category,
            String name,
            String description,
            int unitPrice
    ) {}

}
