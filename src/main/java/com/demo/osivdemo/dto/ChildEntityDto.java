package com.demo.osivdemo.dto;

import com.demo.osivdemo.domain.ChildEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * DTO for {@link ChildEntity}
 */
public class ChildEntityDto implements Serializable {
    private final Long id;
    private final String name;
    private final String description;
    private final List<GrandchildEntityDto> grandchildEntities;

    public ChildEntityDto(Long id, String name, String description, List<GrandchildEntityDto> grandchildEntities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.grandchildEntities = grandchildEntities;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<GrandchildEntityDto> getGrandchildEntities() {
        return grandchildEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChildEntityDto entity = (ChildEntityDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.name, entity.name) &&
                Objects.equals(this.description, entity.description) &&
                Objects.equals(this.grandchildEntities, entity.grandchildEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, grandchildEntities);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "description = " + description + ", " +
                "grandchildEntities = " + grandchildEntities + ")";
    }
}