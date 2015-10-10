package com.programyourhome.shop;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.javamoney.moneta.internal.MoneyAmountBuilder;
import org.springframework.stereotype.Component;

import com.programyourhome.common.util.StreamUtil;
import com.programyourhome.shop.dao.CompanyRepository;
import com.programyourhome.shop.dao.CompanyTypeRepository;
import com.programyourhome.shop.dao.DepartmentRepository;
import com.programyourhome.shop.dao.ProductRepository;
import com.programyourhome.shop.dao.ShopRepository;
import com.programyourhome.shop.model.ImageMimeType;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.api.PyhProductImpl;
import com.programyourhome.shop.model.jpa.Company;
import com.programyourhome.shop.model.jpa.CompanyProduct;
import com.programyourhome.shop.model.jpa.CompanyType;
import com.programyourhome.shop.model.jpa.Department;
import com.programyourhome.shop.model.jpa.Product;
import com.programyourhome.shop.model.jpa.ProductImage;
import com.programyourhome.shop.model.jpa.Shop;
import com.programyourhome.shop.model.jpa.ShopDepartment;

@Component
public class ShoppingImpl implements Shopping {

    @Inject
    private ProductRepository productRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private CompanyTypeRepository companyTypeRepository;

    @Inject
    private DepartmentRepository departmentRepository;

    @Inject
    private ShopRepository shopRepository;

    @PostConstruct
    public void tempAddSomeData() {
        final CompanyType supermarket = new CompanyType("Supermarket", "Where you can buy your groceries");
        this.companyTypeRepository.save(supermarket);

        final Product p1 = this.productRepository.save(new Product("1234", "Spappel", "SPA Fruit Appel", new ProductImage(ImageMimeType.JPG, "1234")));
        final Product p2 = this.productRepository.save(new Product("5678", "Pindakaas", "AH Pindakaas met stukjes noot", new ProductImage(ImageMimeType.PNG,
                "5678")));

        final Department d = this.departmentRepository.save(new Department("Groente & Fruit", "De versafdeling GFT"));

        final Company ah = new Company("AH", "Albert Heijn", supermarket);
        ah.addDepartment(d);

        ah.addCompanyProduct(new CompanyProduct(ah, p1, new MoneyAmountBuilder().setCurrency("EUR").setNumber(1.20).create()));
        ah.addCompanyProduct(new CompanyProduct(ah, p2, new MoneyAmountBuilder().setCurrency("EUR").setNumber(2.35).create()));
        this.companyRepository.save(ah);

        final Shop s = this.shopRepository.save(new Shop("Hoofdstraat Driebergen", "De AH aan de hoofdstraat in Driebergen", ah, "Hoofdstraat xx, Driebergen"));
        s.addShopDepartment(new ShopDepartment(s, d, 1));
        this.shopRepository.save(s);
    }

    @Override
    public Collection<PyhProduct> getProducts() {
        return StreamUtil.fromIterable(this.productRepository.findAll())
                .map(product -> new PyhProductImpl(product))
                .collect(Collectors.toList());
    }

    @Override
    public PyhProduct getProduct(final int productId) {
        return new PyhProductImpl(this.productRepository.findOne(productId));
    }

}
