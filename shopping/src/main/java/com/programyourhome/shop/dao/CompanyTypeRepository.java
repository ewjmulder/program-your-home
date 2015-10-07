package com.programyourhome.shop.dao;

import org.springframework.data.repository.CrudRepository;

import com.programyourhome.shop.model.jpa.CompanyType;

public interface CompanyTypeRepository extends CrudRepository<CompanyType, Integer> {

}
