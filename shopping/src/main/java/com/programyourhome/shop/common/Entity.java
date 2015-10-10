package com.programyourhome.shop.common;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    public Entity() {
        // The id will be auto-generated.
    }

    public int getId() {
        return this.id;
    }

}
