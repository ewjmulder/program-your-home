package com.programyourhome.shop;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.javamoney.moneta.internal.MoneyAmountBuilder;
import org.springframework.stereotype.Component;

import com.programyourhome.common.serialize.SerializationSettings;
import com.programyourhome.common.util.BeanCopier;
import com.programyourhome.common.util.StreamUtil;
import com.programyourhome.shop.dao.CompanyRepository;
import com.programyourhome.shop.dao.CompanyTypeRepository;
import com.programyourhome.shop.dao.ProductAggregationRepository;
import com.programyourhome.shop.dao.ProductRepository;
import com.programyourhome.shop.dao.ShopRepository;
import com.programyourhome.shop.model.ImageMimeType;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.PyhProductAggregation;
import com.programyourhome.shop.model.PyhProductAggregationPart;
import com.programyourhome.shop.model.PyhProductAggregationPartProperties;
import com.programyourhome.shop.model.PyhProductAggregationProperties;
import com.programyourhome.shop.model.PyhProductAggregationState;
import com.programyourhome.shop.model.PyhProductImage;
import com.programyourhome.shop.model.PyhProductProperties;
import com.programyourhome.shop.model.PyhProductState;
import com.programyourhome.shop.model.api.PyhProductAggregationStateImpl;
import com.programyourhome.shop.model.api.PyhProductStateImpl;
import com.programyourhome.shop.model.jpa.Company;
import com.programyourhome.shop.model.jpa.CompanyProduct;
import com.programyourhome.shop.model.jpa.CompanyType;
import com.programyourhome.shop.model.jpa.Department;
import com.programyourhome.shop.model.jpa.Product;
import com.programyourhome.shop.model.jpa.ProductAggregation;
import com.programyourhome.shop.model.jpa.ProductAggregationPart;
import com.programyourhome.shop.model.jpa.ProductImage;
import com.programyourhome.shop.model.jpa.Shop;
import com.programyourhome.shop.model.jpa.ShopDepartment;

@Component
@Transactional
public class ShoppingImpl implements Shopping {

    @Inject
    private ProductRepository productRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private CompanyTypeRepository companyTypeRepository;

    @Inject
    private ShopRepository shopRepository;

    @Inject
    private ProductAggregationRepository productAggregationRepository;

    @Inject
    private SerializationSettings serializationSettings;

    @Inject
    private BeanCopier beanCopier;

    @PostConstruct
    public void provideSerializationSettings() {
        this.serializationSettings.fixSerializationScope(PyhProduct.class, PyhProductAggregationState.class, PyhProductAggregation.class,
                PyhProductAggregationPart.class, PyhProductImage.class);
    }

    @PostConstruct
    public void tempAddSomeData() {
        final CompanyType supermarket = new CompanyType("Supermarket", "Where you can buy your groceries");
        this.companyTypeRepository.save(supermarket);

        Product p1 = new Product("Spappel", "SPA Fruit Appel", "1234");
        p1.setImage(new ProductImage(p1, ImageMimeType.PNG, "1234"));
        p1 = this.productRepository.save(p1);
        Product p2 = new Product("Pindakaas met nootjes", "AH Pindakaas met stukjes noot", "5678");
        p2.setImage(new ProductImage(p2, ImageMimeType.PNG, "5678"));
        p2 = this.productRepository.save(p2);
        Product p3 = new Product("Pindakaas zonder nootjes", "AH Pindakaas zonder stukjes noot", "90");
        p3.setImage(new ProductImage(p3, ImageMimeType.PNG, "90"));
        p3 = this.productRepository.save(p3);

        final Company ah = new Company("AH", "Albert Heijn", supermarket);
        final Department d1 = new Department(ah, "Groente & Fruit", "De versafdeling GFT");
        final Department d2 = new Department(ah, "Frisdrank", "Drankies");
        final Department d3 = new Department(ah, "Zoet beleg", "Belegjes");
        ah.addDepartment(d1);
        ah.addDepartment(d2);
        ah.addDepartment(d3);

        ah.addCompanyProduct(new CompanyProduct(ah, p1, d2, new MoneyAmountBuilder().setCurrency("EUR").setNumber(1.20).create()));
        ah.addCompanyProduct(new CompanyProduct(ah, p2, d3, new MoneyAmountBuilder().setCurrency("EUR").setNumber(2.35).create()));
        ah.addCompanyProduct(new CompanyProduct(ah, p3, d3, new MoneyAmountBuilder().setCurrency("EUR").setNumber(2.30).create()));
        this.companyRepository.save(ah);

        final Shop s = this.shopRepository.save(new Shop("Hoofdstraat Driebergen", "De AH aan de hoofdstraat in Driebergen", ah, "Hoofdstraat xx, Driebergen"));
        s.addShopDepartment(new ShopDepartment(s, d1, 1));
        this.shopRepository.save(s);

        final ProductAggregation pa = this.productAggregationRepository.save(new ProductAggregation("Pindakaas", "Zoet broodbeleg pindakaas", BigDecimal
                .valueOf(2), BigDecimal.valueOf(4)));
        pa.addAggregationPart(new ProductAggregationPart(pa, p2, BigDecimal.ONE, 1));
        pa.addAggregationPart(new ProductAggregationPart(pa, p3, BigDecimal.ONE, 2));
        this.productAggregationRepository.save(pa);
    }

    @Override
    public Collection<Product> getProducts() {
        return StreamUtil.fromIterable(this.productRepository.findAll())
                .collect(Collectors.toList());
    }

    @Override
    public Product getProduct(final int productId) {
        return this.productRepository.findOne(productId);
    }

    @Override
    public Product createProduct(final PyhProductProperties pyhProductProperties) {
        return this.productRepository.save(this.beanCopier.copyToNew(pyhProductProperties, Product.class));
    }

    @Override
    public PyhProduct updateProduct(final int productId, final PyhProductProperties pyhProductProperties) {
        final Product product = this.productRepository.findOne(productId);
        return this.productRepository.save(this.beanCopier.copyTo(pyhProductProperties, product));
    }

    @Override
    public void deleteProduct(final int productId) {
        this.productRepository.delete(productId);
    }

    @Override
    public ProductImage getProductImage(final int productId) {
        return this.getProduct(productId).getImage();
    }

    @Override
    public PyhProductImage setImageForProduct(final int productId, final PyhProductImage pyhProductImage) {
        final Product product = this.getProduct(productId);
        if (product.hasImage()) {
            this.beanCopier.copyTo(pyhProductImage, product.getImage());
        } else {
            final ProductImage productImage = new ProductImage(product);
            product.setImage(this.beanCopier.copyTo(pyhProductImage, productImage));
        }
        this.productRepository.save(product);
        return product.getImage();
    }

    @Override
    public void removeImageFromProduct(final int productId) {
        final Product product = this.getProduct(productId);
        product.setImage(null);
        this.productRepository.save(product);
    }

    @Override
    public Collection<ProductAggregation> getProductAggregations() {
        return StreamUtil.fromIterable(this.productAggregationRepository.findAll())
                .collect(Collectors.toList());
    }

    @Override
    public ProductAggregation getProductAggregation(final int productAggregationId) {
        return this.productAggregationRepository.findOne(productAggregationId);
    }

    @Override
    public ProductAggregation createProductAggregation(final PyhProductAggregationProperties pyhProductAggregationProperties) {
        return this.productAggregationRepository.save(this.beanCopier.copyToNew(pyhProductAggregationProperties, ProductAggregation.class));
    }

    @Override
    public PyhProductAggregation updateProductAggregation(final int productAggregationId, final PyhProductAggregationProperties pyhProductAggregationProperties) {
        final ProductAggregation productAggregation = this.productAggregationRepository.findOne(productAggregationId);
        this.beanCopier.copyTo(pyhProductAggregationProperties, productAggregation);
        return this.productAggregationRepository.save(productAggregation);
    }

    @Override
    public void deleteProductAggregation(final int productAggregationId) {
        this.productAggregationRepository.delete(productAggregationId);
    }

    @Override
    public ProductAggregation setProductInProductAggregation(final int productId, final int productAggregationId,
            final PyhProductAggregationPartProperties pyhProductAggregationPartProperties) {
        final Product product = this.productRepository.findOne(productId);
        final ProductAggregation productAggregation = this.productAggregationRepository.findOne(productAggregationId);
        final Optional<ProductAggregationPart> optionalProductAggregationPart = productAggregation.findAggregationPart(productId);
        if (optionalProductAggregationPart.isPresent()) {
            this.beanCopier.copyTo(pyhProductAggregationPartProperties, optionalProductAggregationPart.get());
        } else {
            final ProductAggregationPart productAggregationPart = new ProductAggregationPart(productAggregation, product);
            productAggregation.addAggregationPart(this.beanCopier.copyTo(pyhProductAggregationPartProperties, productAggregationPart));
        }
        return this.productAggregationRepository.save(productAggregation);
    }

    @Override
    public PyhProductAggregation removeProductFromProductAggregation(final int productId, final int productAggregationId) {
        final ProductAggregation productAggregation = this.productAggregationRepository.findOne(productAggregationId);
        productAggregation.removeAggregationPart(productId);
        return this.productAggregationRepository.save(productAggregation);
    }

    // FIXME: temp for test
    private final Map<Integer, Integer> stock = new HashMap<>();

    // TODO: testing:
    // add/remove product items
    // get state of product
    // get state of product aggregation (contribution check)
    // one product in multiple aggregations (check arithmetic)
    @Override
    public Collection<PyhProductState> getProductStates() {
        return StreamUtil.fromIterable(this.productRepository.findAll())
                .map(Product::getId)
                .map(this::getProductState)
                .collect(Collectors.toList());
    }

    @Override
    public PyhProductState getProductState(final int productId) {
        // TODO Call to EventStore
        return new PyhProductStateImpl(productId, this.stock.getOrDefault(productId, 0));
    }

    @Override
    public Collection<PyhProductAggregationState> getProductAggregationStates() {
        return StreamUtil.fromIterable(this.productAggregationRepository.findAll())
                .map(ProductAggregation::getId)
                .map(this::getProductAggregationState)
                .collect(Collectors.toList());
    }

    @Override
    public PyhProductAggregationState getProductAggregationState(final int productAggregationId) {
        final ProductAggregation productAggregation = this.productAggregationRepository.findOne(productAggregationId);
        final BigDecimal amount = productAggregation.getAggregationParts().stream()
                // For every part, get the corresponding aggregation amount by multiplying the contribution with the current product amount.
                .map(part -> part.getContribution().multiply(BigDecimal.valueOf(this.getProductState(part.getProduct().getId()).getAmount())))
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        return new PyhProductAggregationStateImpl(productAggregationId, amount);
    }

    @Override
    public PyhProductState addProductItem(final String barcode) {
        return this.addProductItem(this.productRepository.findByBarcode(barcode).getId());
    }

    @Override
    public PyhProductState addProductItem(final int productId) {
        final int currentValue = this.stock.computeIfAbsent(productId, p -> 0);
        this.stock.put(productId, currentValue + 1);
        return this.getProductState(productId);
    }

    @Override
    public PyhProductState removeProductItem(final String barcode) {
        return this.removeProductItem(this.productRepository.findByBarcode(barcode).getId());
    }

    @Override
    public PyhProductState removeProductItem(final int productId) {
        // TODO Call to EventStore, todo: not below zero
        final int currentValue = this.stock.computeIfAbsent(productId, p -> 0);
        this.stock.put(productId, currentValue - 1);
        return this.getProductState(productId);
    }

}
