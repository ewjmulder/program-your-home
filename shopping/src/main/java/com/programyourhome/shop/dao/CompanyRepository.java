package com.programyourhome.shop.dao;

import org.springframework.data.repository.CrudRepository;

import com.programyourhome.shop.model.jpa.Company;

public interface CompanyRepository extends CrudRepository<Company, Integer> {

}
