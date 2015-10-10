package com.programyourhome.shop.common;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NamedEntityRepository<T extends NamedEntity, ID extends Serializable> extends CrudRepository<T, ID> {

    public T findByName(String name);

}
