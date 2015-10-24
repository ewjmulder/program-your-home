package com.programyourhome.server.controllers.shop;

import java.util.Collection;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.server.controllers.AbstractProgramYourHomeController;
import com.programyourhome.server.response.ServiceResult;
import com.programyourhome.shop.Shopping;
import com.programyourhome.shop.model.PyhCompany;
import com.programyourhome.shop.model.PyhCompanyType;
import com.programyourhome.shop.model.PyhCompanyTypeProperties;

@RestController
@RequestMapping("shop/companyTypes")
public class ProgramYourHomeControllerShopCompanyTypes extends AbstractProgramYourHomeController {

    @Inject
    private Shopping shopping;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhCompanyType>> getCompanyTypes() {
        return this.produce("CompanyTypes", () -> this.shopping.getCompanyTypes());
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ServiceResult<PyhCompanyType> getCompanyType(@PathVariable("id") final int companyTypeId) {
        return this.produce("CompanyType", () -> this.shopping.getCompanyType(companyTypeId));
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<PyhCompanyType> createCompanyType(@RequestBody final PyhCompanyTypeProperties companyTypeProperties) {
        return this.produce("CompanyType", () -> this.shopping.createCompanyType(companyTypeProperties));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MIME_JSON)
    public ServiceResult<PyhCompanyType> updateCompanyType(@PathVariable("id") final int companyTypeId,
            @RequestBody final PyhCompanyTypeProperties companyTypeProperties) {
        return this.produce("CompanyType", () -> this.shopping.updateCompanyType(companyTypeId, companyTypeProperties));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ServiceResult<Void> deleteCompanyType(@PathVariable("id") final int companyTypeId) {
        return this.run(() -> this.shopping.deleteCompanyType(companyTypeId));
    }

    @RequestMapping(value = "{id}/companies", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhCompany>> getCompanies(@PathVariable("id") final int companyTypeId) {
        return this.produce("Companies", () -> this.shopping.getCompanies(companyTypeId));
    }

}
