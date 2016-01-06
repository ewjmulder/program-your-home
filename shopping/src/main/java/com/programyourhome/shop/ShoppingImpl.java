package com.programyourhome.shop;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.javamoney.moneta.internal.MoneyAmountBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.programyourhome.common.eventstore.EventStore;
import com.programyourhome.common.serialize.SerializationSettings;
import com.programyourhome.common.util.BeanCopier;
import com.programyourhome.common.util.StreamUtil;
import com.programyourhome.shop.dao.BulkProductRepository;
import com.programyourhome.shop.dao.CompanyRepository;
import com.programyourhome.shop.dao.CompanyTypeRepository;
import com.programyourhome.shop.dao.ProductAggregationRepository;
import com.programyourhome.shop.dao.ProductRepository;
import com.programyourhome.shop.model.EventAmount;
import com.programyourhome.shop.model.ImageMimeType;
import com.programyourhome.shop.model.PyhBarcodeSearchResult;
import com.programyourhome.shop.model.PyhBulkProduct;
import com.programyourhome.shop.model.PyhBulkProductProperties;
import com.programyourhome.shop.model.PyhCompany;
import com.programyourhome.shop.model.PyhCompanyProduct;
import com.programyourhome.shop.model.PyhCompanyProductProperties;
import com.programyourhome.shop.model.PyhCompanyProductToCompany;
import com.programyourhome.shop.model.PyhCompanyProperties;
import com.programyourhome.shop.model.PyhCompanyType;
import com.programyourhome.shop.model.PyhCompanyTypeProperties;
import com.programyourhome.shop.model.PyhDepartment;
import com.programyourhome.shop.model.PyhDepartmentProperties;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.PyhProductAggregation;
import com.programyourhome.shop.model.PyhProductAggregationPart;
import com.programyourhome.shop.model.PyhProductAggregationPartProperties;
import com.programyourhome.shop.model.PyhProductAggregationPartToProductAggregation;
import com.programyourhome.shop.model.PyhProductAggregationProperties;
import com.programyourhome.shop.model.PyhProductAggregationState;
import com.programyourhome.shop.model.PyhProductImage;
import com.programyourhome.shop.model.PyhProductProperties;
import com.programyourhome.shop.model.PyhProductState;
import com.programyourhome.shop.model.PyhShop;
import com.programyourhome.shop.model.PyhShopDepartment;
import com.programyourhome.shop.model.PyhShopDepartmentProperties;
import com.programyourhome.shop.model.PyhShopDepartmentToShop;
import com.programyourhome.shop.model.PyhShopProperties;
import com.programyourhome.shop.model.PyhSizeUnitIdentification;
import com.programyourhome.shop.model.StockChange;
import com.programyourhome.shop.model.StockCount;
import com.programyourhome.shop.model.StockEventType;
import com.programyourhome.shop.model.jpa.BulkProduct;
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
import com.programyourhome.shop.model.pojo.PyhBarcodeSearchResultImpl;
import com.programyourhome.shop.model.pojo.PyhProductAggregationStateImpl;
import com.programyourhome.shop.model.pojo.PyhProductStateImpl;
import com.programyourhome.shop.model.size.AreaUnit;
import com.programyourhome.shop.model.size.LengthUnit;
import com.programyourhome.shop.model.size.PieceUnit;
import com.programyourhome.shop.model.size.SizeUnit;
import com.programyourhome.shop.model.size.UnitType;
import com.programyourhome.shop.model.size.VolumeUnit;
import com.programyourhome.shop.model.size.WeightUnit;

/**
 * Implementation of the Shopping service interface.
 *
 * Please note the dynamic use of (un)fixing the serialization scope of join table entities. This is to allow the same
 * entity type to be serialized in 2 different views. This could be solved using direct JsonView annotations on the
 * interface or entity classes, but we prefer not to 'spoil' the clean interface definition.
 * Alternative is to use another dynamic influence that can be configured at initialization time and does not need to be
 * changed anymore after that. If needed, have a closer look at:
 * http://wiki.fasterxml.com/JacksonJsonViews
 * http://wiki.fasterxml.com/JacksonFeatureJsonFilter
 * http://wiki.fasterxml.com/JacksonMixInAnnotations
 *
 * NB: This does not work, since deserializers will be cached once used. Workaround is to clear cache (for that type),
 * but no public API for that in Jackson. Could be done through reflection, but that is getting nastier and nastier.
 * Better find another solution using JsonView on Mixins or so.
 * http://stackoverflow.com/questions/8475980/using-jackson-json-views-without-annotating-original-bean-class
 */
@Service
@Transactional
public class ShoppingImpl implements Shopping {

    private static final String PRODUCT_PROJECTION_NAME = "product-stock";
    private static final String STREAM_PREFIX = "product-";
    private static final String PRODUCT_PROJECTION_PARTITION_PREFIX = STREAM_PREFIX;

    @Inject
    private ProductRepository productRepository;

    @Inject
    private BulkProductRepository bulkProductRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private CompanyTypeRepository companyTypeRepository;

    @Inject
    private ProductAggregationRepository productAggregationRepository;

    @Inject
    private SerializationSettings serializationSettings;

    @Inject
    private BeanCopier beanCopier;

    @Value("${eventstore.host}")
    private String host;

    @Value("${eventstore.port}")
    private int port;

    private EventStore eventStore;

    @PostConstruct
    public void init() {
        this.serializationSettings.fixSerializationScopeTo(WeightUnit.class, UnitType.class);
        this.serializationSettings.fixSerializationScopeTo(VolumeUnit.class, UnitType.class);
        this.serializationSettings.fixSerializationScopeTo(AreaUnit.class, UnitType.class);
        this.serializationSettings.fixSerializationScopeTo(LengthUnit.class, UnitType.class);
        this.serializationSettings.fixSerializationScopeTo(PieceUnit.class, UnitType.class);

        this.eventStore = new EventStore(this.host, this.port);
    }

    @PostConstruct
    public void tempAddSomeData() {
        final CompanyType supermarket = new CompanyType("Supermarket", "Where you can buy your groceries");
        this.companyTypeRepository.save(supermarket);

        Product p1 = new Product("Spappel", "SPA Fruit Appel", "1234", BigDecimal.valueOf(1.5), SizeUnit.VOLUME_LITER);
        p1.setImage(new ProductImage(p1, ImageMimeType.PNG, "1234"));
        p1 = this.productRepository.save(p1);
        Product p11 = new Product("Spappel blikje", "SPA Fruit Appel Blikje", "12345", BigDecimal.valueOf(330), SizeUnit.VOLUME_MILLILITER);
        p11.setImage(new ProductImage(p11, ImageMimeType.PNG, "12345"));
        p11.addBulkProduct(new BulkProduct(p11, "6-pack spappel blikjes", "SPA Fruit Appel Blikjes in een 6 pack", "123456", 6));
        p11 = this.productRepository.save(p11);

        Product p2 = new Product("Pindakaas met nootjes", "AH Pindakaas met stukjes noot", "5678", BigDecimal.valueOf(400), SizeUnit.WEIGHT_GRAM);
        p2.setImage(new ProductImage(p2, ImageMimeType.PNG, "5678"));
        p2 = this.productRepository.save(p2);
        Product p3 = new Product("Pindakaas zonder nootjes", "AH Pindakaas zonder stukjes noot", "90", BigDecimal.valueOf(0.5), SizeUnit.WEIGHT_KILOGRAM);
        p3.setImage(new ProductImage(p3, ImageMimeType.PNG, "90"));
        p3 = this.productRepository.save(p3);

        final Company ah = new Company("AH", "Albert Heijn", supermarket);
        final Department d1 = new Department(ah, "Groente & Fruit", "De versafdeling GFT");
        final Department d2 = new Department(ah, "Frisdrank", "Drankies");
        final Department d3 = new Department(ah, "Zoet beleg", "Belegjes");
        ah.addDepartment(d1);
        ah.addDepartment(d2);
        ah.addDepartment(d3);

        ah.addCompanyProduct(new CompanyProduct(ah, p1, d2, new MoneyAmountBuilder().setCurrency("EUR").setNumber(1.20).create(), null));
        ah.addCompanyProduct(new CompanyProduct(ah, p2, d3, new MoneyAmountBuilder().setCurrency("EUR").setNumber(2.35).create(), null));
        ah.addCompanyProduct(new CompanyProduct(ah, p3, d3, new MoneyAmountBuilder().setCurrency("EUR").setNumber(2.30).create(), null));
        final Shop s = new Shop(ah, "Hoofdstraat Driebergen", "De AH aan de hoofdstraat in Driebergen", "Hoofdstraat xx, Driebergen");
        s.addShopDepartment(new ShopDepartment(s, d1, 1));
        ah.addShop(s);
        this.companyRepository.save(ah);

        final ProductAggregation pa = this.productAggregationRepository.save(new ProductAggregation("Pindakaas", "Zoet broodbeleg pindakaas",
                SizeUnit.WEIGHT_KILOGRAM, BigDecimal.valueOf(2), BigDecimal.valueOf(4)));
        pa.addAggregationPart(new ProductAggregationPart(pa, p2, 1));
        pa.addAggregationPart(new ProductAggregationPart(pa, p3, 2));
        final ProductAggregation pa2 = this.productAggregationRepository.save(new ProductAggregation("Test aggr", "desc",
                SizeUnit.VOLUME_LITER, BigDecimal.valueOf(3), BigDecimal.valueOf(10)));
        pa2.addAggregationPart(new ProductAggregationPart(pa2, p1, 1));
        pa2.addAggregationPart(new ProductAggregationPart(pa2, p11, 2));
        this.productAggregationRepository.save(pa);
        this.productAggregationRepository.save(pa2);
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
    public PyhBarcodeSearchResult searchProductByBarcode(final String barcode) {
        PyhBarcodeSearchResult searchResult;
        final Product product = this.productRepository.findByBarcode(barcode);
        if (product != null) {
            searchResult = PyhBarcodeSearchResultImpl.product(product);
        } else {
            final BulkProduct bulkProduct = this.bulkProductRepository.findByBarcode(barcode);
            if (bulkProduct != null) {
                searchResult = PyhBarcodeSearchResultImpl.bulkProduct(bulkProduct);
            } else {
                searchResult = PyhBarcodeSearchResultImpl.none();
            }
        }
        return searchResult;
    }

    @Override
    public Product createProduct(final PyhProductProperties productProperties) {
        final Product product = this.beanCopier.copyToNew(productProperties, Product.class);
        return this.productRepository.save(this.updateWithSize(product, productProperties));
    }

    @Override
    public PyhProduct updateProduct(final int productId, final PyhProductProperties productProperties) {
        final Product product = this.productRepository.findOne(productId);
        this.beanCopier.copyTo(productProperties, product);
        return this.productRepository.save(this.updateWithSize(product, productProperties));
    }

    private Product updateWithSize(final Product product, final PyhProductProperties productProperties) {
        product.setSizeAmount(productProperties.getSize().getAmount());
        product.setSizeUnit(this.findSizeUnit(productProperties.getSize()));
        return product;
    }

    private SizeUnit findSizeUnit(final PyhSizeUnitIdentification sizeUnitIdentification) {
        return SizeUnit.findByIdentification(
                sizeUnitIdentification.getUnit().getTypeName(),
                sizeUnitIdentification.getUnit().getAbbreviation());
    }

    @Override
    public void deleteProduct(final int productId) {
        this.productRepository.delete(productId);
    }

    @Override
    public Collection<? extends PyhBulkProduct> getBulkProducts(final int productId) {
        return this.productRepository.findOne(productId).getBulkProducts();
    }

    @Override
    public PyhBulkProduct getBulkProduct(final int productId, final int bulkProductId) {
        return this.productRepository.findOne(productId).findBulkProduct(bulkProductId).get();
    }

    @Override
    public PyhBulkProduct addBulkProduct(final int productId, final PyhBulkProductProperties bulkProductProperties) {
        final Product product = this.productRepository.findOne(productId);
        product.addBulkProduct(this.beanCopier.copyTo(bulkProductProperties, new BulkProduct(product)));
        return this.productRepository.save(product).findBulkProduct(bulkProductProperties.getName()).get();
    }

    @Override
    public PyhBulkProduct updateBulkProduct(final int productId, final int bulkProductId, final PyhBulkProductProperties bulkProductProperties) {
        final Product product = this.productRepository.findOne(productId);
        final BulkProduct bulkProduct = product.findBulkProduct(bulkProductId).get();
        this.beanCopier.copyTo(bulkProductProperties, bulkProduct);
        return this.productRepository.save(product).findBulkProduct(bulkProductId).get();
    }

    @Override
    public void deleteBulkProduct(final int productId, final int bulkProductId) {
        final Product product = this.productRepository.findOne(productId);
        product.removeBulkProduct(product.findBulkProduct(bulkProductId).get());
        this.productRepository.save(product);
    }

    @Override
    public Collection<? extends PyhProductAggregationPartToProductAggregation> getProductAggregationPartsToProductAggregation(final int productId) {
        return this.productRepository.findOne(productId).getAggregationParts();
    }

    @Override
    public Collection<? extends PyhCompanyProductToCompany> getCompanyProductsToCompany(final int productId) {
        return this.productRepository.findOne(productId).getCompanyProducts();
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
    public ProductAggregation createProductAggregation(final PyhProductAggregationProperties productAggregationProperties) {
        final ProductAggregation productAggregation = this.beanCopier.copyToNew(productAggregationProperties, ProductAggregation.class);
        productAggregation.setSizeUnit(this.findSizeUnit(productAggregationProperties));
        return this.productAggregationRepository.save(productAggregation);
    }

    @Override
    public PyhProductAggregation updateProductAggregation(final int productAggregationId, final PyhProductAggregationProperties productAggregationProperties) {
        final ProductAggregation productAggregation = this.productAggregationRepository.findOne(productAggregationId);
        this.beanCopier.copyTo(productAggregationProperties, productAggregation);
        productAggregation.setSizeUnit(this.findSizeUnit(productAggregationProperties));
        return this.productAggregationRepository.save(productAggregation);
    }

    @Override
    public void deleteProductAggregation(final int productAggregationId) {
        this.productAggregationRepository.delete(productAggregationId);
    }

    @Override
    public Collection<? extends PyhProductAggregationPart> getProductAggregationParts(final int productAggregationId) {
        return this.productAggregationRepository.findOne(productAggregationId).getAggregationParts();
    }

    @Override
    public PyhProductAggregationPart getProductAggregationPart(final int productAggregationId, final int productId) {
        return this.productAggregationRepository.findOne(productAggregationId).findAggregationPart(productId).get();
    }

    @Override
    public ProductAggregationPart setProductInProductAggregationPart(final int productAggregationId, final int productId,
            final PyhProductAggregationPartProperties productAggregationPartProperties) {
        final ProductAggregation productAggregation = this.productAggregationRepository.findOne(productAggregationId);
        final Product product = this.productRepository.findOne(productId);
        final Optional<ProductAggregationPart> optionalProductAggregationPart = productAggregation.findAggregationPart(productId);
        if (optionalProductAggregationPart.isPresent()) {
            this.beanCopier.copyTo(productAggregationPartProperties, optionalProductAggregationPart.get());
        } else {
            final ProductAggregationPart productAggregationPart = new ProductAggregationPart(productAggregation, product);
            productAggregation.addAggregationPart(this.beanCopier.copyTo(productAggregationPartProperties, productAggregationPart));
        }
        return this.productAggregationRepository.save(productAggregation).findAggregationPart(productId).get();
    }

    @Override
    public void removeProductFromProductAggregationPart(final int productAggregationId, final int productId) {
        final ProductAggregation productAggregation = this.productAggregationRepository.findOne(productAggregationId);
        productAggregation.removeAggregationPart(productId);
        this.productAggregationRepository.save(productAggregation);
    }

    @Override
    public Collection<PyhProductState> getProductStates() {
        return StreamUtil.fromIterable(this.productRepository.findAll())
                .map(Product::getId)
                .map(this::getProductState)
                .collect(Collectors.toList());
    }

    @Override
    public PyhProductState getProductState(final int productId) {
        final StockCount stockCount = this.eventStore.getProjectionState(
                PRODUCT_PROJECTION_NAME, PRODUCT_PROJECTION_PARTITION_PREFIX + productId, StockCount.class);
        return new PyhProductStateImpl(productId, stockCount.getCount());
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
                // For every part, get the corresponding aggregation amount by unit corrected calculation based on the current product amount.
                .map(this::calculateAggregationAmount)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        return new PyhProductAggregationStateImpl(productAggregation, amount);
    }

    private BigDecimal calculateAggregationAmount(final ProductAggregationPart part) {
        final BigDecimal productAmount = BigDecimal.valueOf(this.getProductState(part.getProduct().getId()).getAmount());
        final BigDecimal productAmountInSmallestUnit = productAmount
                .multiply(part.getProduct().getSizeAmount())
                .multiply(part.getProduct().getSize().getUnit().getAmountInSmallestUnit());
        return productAmountInSmallestUnit.divide(part.getProductAggregation().getSizeUnit().getUnit().getAmountInSmallestUnit());
    }

    @Override
    public PyhProductState increaseByBarcode(final String barcode) {
        final StockChange stockChange = this.getStockChange(barcode);
        return this.increaseByProductId(stockChange.getProductId(), stockChange.getAmount());
    }

    @Override
    public PyhProductState increaseByProductId(final int productId, final int amount) {
        return this.postEvent(productId, StockEventType.INCREASE, amount);
    }

    @Override
    public PyhProductState decreaseByBarcode(final String barcode) {
        final StockChange stockChange = this.getStockChange(barcode);
        return this.decreaseByProductId(stockChange.getProductId(), stockChange.getAmount());
    }

    @Override
    public PyhProductState decreaseByProductId(final int productId, final int amount) {
        if (this.getProductState(productId).getAmount() - amount < 0) {
            throw new IllegalStateException("Product stock cannot be decreased below zero.");
        }
        return this.postEvent(productId, StockEventType.DECREASE, amount);
    }

    private PyhProductState postEvent(final int productId, final StockEventType stockEventType, final int amount) {
        this.eventStore.postEvent(STREAM_PREFIX + productId, stockEventType.getTypeName(), new EventAmount(amount));
        return this.getProductState(productId);
    }

    private StockChange getStockChange(final String barcode) {
        final Product product = this.productRepository.findByBarcode(barcode);
        final StockChange stockChange;
        if (product != null) {
            stockChange = new StockChange(product.getId(), 1);
        } else {
            // If the barcode does not match on a product, it must be a bulk product.
            final BulkProduct bulkProduct = this.bulkProductRepository.findByBarcode(barcode);
            if (bulkProduct != null) {
                stockChange = new StockChange(bulkProduct.getProduct().getId(), bulkProduct.getAmount());
            } else {
                throw new IllegalArgumentException("No (bulk)product found for barcode: ' " + barcode + "'.");
            }
        }
        return stockChange;
    }

    @Override
    public Collection<? extends PyhCompanyType> getCompanyTypes() {
        return StreamUtil.fromIterable(this.companyTypeRepository.findAll())
                .collect(Collectors.toList());
    }

    @Override
    public PyhCompanyType getCompanyType(final int companyTypeId) {
        return this.companyTypeRepository.findOne(companyTypeId);
    }

    @Override
    public PyhCompanyType createCompanyType(final PyhCompanyTypeProperties companyTypeProperties) {
        return this.companyTypeRepository.save(this.beanCopier.copyToNew(companyTypeProperties, CompanyType.class));
    }

    @Override
    public PyhCompanyType updateCompanyType(final int companyTypeId, final PyhCompanyTypeProperties companyTypeProperties) {
        final CompanyType companyType = this.companyTypeRepository.findOne(companyTypeId);
        return this.companyTypeRepository.save(this.beanCopier.copyTo(companyTypeProperties, companyType));
    }

    @Override
    public void deleteCompanyType(final int companyTypeId) {
        this.companyTypeRepository.delete(companyTypeId);
    }

    @Override
    public Collection<? extends PyhCompany> getCompanies(final int companyTypeId) {
        return this.companyTypeRepository.findOne(companyTypeId).getCompanies();
    }

    @Override
    public Collection<? extends PyhCompany> getCompanies() {
        return StreamUtil.fromIterable(this.companyRepository.findAll())
                .collect(Collectors.toList());
    }

    @Override
    public PyhCompany getCompany(final int companyId) {
        return this.companyRepository.findOne(companyId);
    }

    @Override
    public PyhCompany createCompany(final PyhCompanyProperties companyProperties) {
        final Company company = new Company();
        company.setName(companyProperties.getName());
        company.setDescription(companyProperties.getDescription());
        company.setType(this.companyTypeRepository.findOne(companyProperties.getCompanyTypeId()));
        return this.companyRepository.save(company);
    }

    @Override
    public PyhCompany updateCompany(final int companyId, final PyhCompanyProperties companyProperties) {
        final Company company = this.companyRepository.findOne(companyId);
        company.setName(companyProperties.getName());
        company.setDescription(companyProperties.getDescription());
        company.setType(this.companyTypeRepository.findOne(companyProperties.getCompanyTypeId()));
        return this.companyRepository.save(company);
    }

    @Override
    public void deleteCompany(final int companyId) {
        this.companyRepository.delete(companyId);
    }

    @Override
    public Collection<? extends PyhShop> getShops(final int companyId) {
        return this.companyRepository.findOne(companyId).getShops();
    }

    @Override
    public PyhShop getShop(final int companyId, final int shopId) {
        return this.companyRepository.findOne(companyId).findShop(shopId).get();
    }

    @Override
    public PyhShop addShop(final int companyId, final PyhShopProperties shopProperties) {
        final Company company = this.companyRepository.findOne(companyId);
        company.addShop(this.beanCopier.copyTo(shopProperties, new Shop(company)));
        return this.companyRepository.save(company).findShop(shopProperties.getName()).get();
    }

    @Override
    public PyhShop updateShop(final int companyId, final int shopId, final PyhShopProperties shopProperties) {
        final Company company = this.companyRepository.findOne(companyId);
        final Shop shop = company.findShop(shopId).get();
        this.beanCopier.copyTo(shopProperties, shop);
        return this.companyRepository.save(company).findShop(shopId).get();
    }

    @Override
    public void deleteShop(final int companyId, final int shopId) {
        final Company company = this.companyRepository.findOne(companyId);
        company.removeShop(company.findShop(shopId).get());
        this.companyRepository.save(company);
    }

    @Override
    public Collection<? extends PyhDepartment> getDepartments(final int companyId) {
        return this.companyRepository.findOne(companyId).getDepartments();
    }

    @Override
    public PyhDepartment getDepartment(final int companyId, final int departmentId) {
        return this.companyRepository.findOne(companyId).findDepartment(departmentId).get();
    }

    @Override
    public PyhDepartment addDepartment(final int companyId, final PyhDepartmentProperties departmentProperties) {
        final Company company = this.companyRepository.findOne(companyId);
        company.addDepartment(this.beanCopier.copyTo(departmentProperties, new Department(company)));
        return this.companyRepository.save(company).findDepartment(departmentProperties.getName()).get();
    }

    @Override
    public PyhDepartment updateDepartment(final int companyId, final int departmentId, final PyhDepartmentProperties departmentProperties) {
        final Company company = this.companyRepository.findOne(companyId);
        final Department department = company.findDepartment(departmentId).get();
        this.beanCopier.copyTo(departmentProperties, department);
        return this.companyRepository.save(company).findDepartment(departmentId).get();
    }

    @Override
    public void deleteDepartment(final int companyId, final int departmentId) {
        final Company company = this.companyRepository.findOne(companyId);
        company.removeDepartment(company.findDepartment(departmentId).get());
        this.companyRepository.save(company);
    }

    @Override
    public Collection<? extends PyhShopDepartmentToShop> getShopDepartmentsToShop(final int companyId, final int departmentId) {
        return this.companyRepository.findOne(companyId).findDepartment(departmentId).get().getShopDepartments();
    }

    @Override
    public Collection<? extends PyhShopDepartment> getShopDepartments(final int companyId, final int shopId) {
        return this.companyRepository.findOne(companyId).findShop(shopId).get().getShopDepartments();
    }

    @Override
    public PyhShopDepartment getShopDepartment(final int companyId, final int shopId, final int departmentId) {
        return this.companyRepository.findOne(companyId).findShop(shopId).get().findShopDepartment(departmentId).get();
    }

    @Override
    public PyhShopDepartment setDepartmentInShopDepartment(final int companyId, final int shopId, final int departmentId,
            final PyhShopDepartmentProperties shopDepartmentProperties) {
        final Company company = this.companyRepository.findOne(companyId);
        final Shop shop = company.findShop(shopId).get();
        final Department department = company.findDepartment(departmentId).get();
        final Optional<ShopDepartment> optionalShopDepartment = shop.findShopDepartment(departmentId);
        if (optionalShopDepartment.isPresent()) {
            this.beanCopier.copyTo(shopDepartmentProperties, optionalShopDepartment.get());
        } else {
            final ShopDepartment shopDepartment = new ShopDepartment(shop, department);
            shop.addShopDepartment(this.beanCopier.copyTo(shopDepartmentProperties, shopDepartment));
        }
        final Company savedCompany = this.companyRepository.save(company);
        return savedCompany.findShop(shopId).get().findShopDepartment(departmentId).get();
    }

    @Override
    public void removeDepartmentFromShopDepartment(final int companyId, final int shopId, final int departmentId) {
        final Company company = this.companyRepository.findOne(companyId);
        final Shop shop = company.findShop(shopId).get();
        shop.removeShopDepartment(departmentId);
        this.companyRepository.save(company);
    }

    @Override
    public Collection<? extends PyhCompanyProduct> getCompanyProducts(final int companyId) {
        return this.companyRepository.findOne(companyId).getCompanyProducts();
    }

    @Override
    public PyhCompanyProduct getCompanyProduct(final int companyId, final int productId) {
        return this.companyRepository.findOne(companyId).findCompanyProduct(productId).get();
    }

    @Override
    public PyhCompanyProduct setProductInCompanyProduct(final int companyId, final int productId, final PyhCompanyProductProperties companyProductProperties) {
        final Company company = this.companyRepository.findOne(companyId);
        final Product product = this.productRepository.findOne(productId);
        final Department department = company.findDepartment(companyProductProperties.getDepartmentId()).get();
        final Optional<CompanyProduct> optionalCompanyProduct = company.findCompanyProduct(productId);
        final CompanyProduct companyProduct = optionalCompanyProduct.orElseGet(() -> {
            final CompanyProduct newCompanyProduct = new CompanyProduct(company, product);
            company.addCompanyProduct(newCompanyProduct);
            return newCompanyProduct;
        });
        companyProduct.setDepartment(department);
        companyProduct.setPrice(companyProductProperties.getPrice());
        companyProduct.setIdentifier(companyProductProperties.getIdentifier());
        return this.companyRepository.save(company).findCompanyProduct(productId).get();
    }

    @Override
    public void removeProductFromCompanyProduct(final int companyId, final int productId) {
        final Company company = this.companyRepository.findOne(companyId);
        company.removeCompanyProduct(productId);
        this.companyRepository.save(company);
    }

}
