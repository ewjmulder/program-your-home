package com.programyourhome.shop.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class NamedEntity extends Entity {

    @Column(nullable = false, unique = true, length = 512)
    private String name;
    @Column(nullable = true, length = 4096)
    private String description;

    public NamedEntity() {
    }

    public NamedEntity(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

}
