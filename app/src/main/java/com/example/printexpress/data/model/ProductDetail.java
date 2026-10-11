package com.example.printexpress.data.model;

import java.util.List;

/** A product together with its size and material choices. */
public class ProductDetail {
    private final Product product;
    private final List<ProductOption> sizes;
    private final List<ProductOption> materials;

    public ProductDetail(Product product, List<ProductOption> sizes, List<ProductOption> materials) {
        this.product = product;
        this.sizes = sizes;
        this.materials = materials;
    }

    public Product getProduct() { return product; }
    public List<ProductOption> getSizes() { return sizes; }
    public List<ProductOption> getMaterials() { return materials; }
}
