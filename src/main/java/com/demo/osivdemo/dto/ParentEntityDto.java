package com.demo.osivdemo.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * DTO for {@link com.demo.osivdemo.domain.ParentEntity}
 */
public class ParentEntityDto implements Serializable {
    private final Long id;
    private final String name;
    private final String description;
    private final List<ChildEntityDto> childEntities;

    public ParentEntityDto(Long id, String name, String description, List<ChildEntityDto> childEntities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.childEntities = childEntities;
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

    public List<ChildEntityDto> getChildEntities() {
        return childEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParentEntityDto entity = (ParentEntityDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.name, entity.name) &&
                Objects.equals(this.description, entity.description) &&
                Objects.equals(this.childEntities, entity.childEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, childEntities);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "description = " + description + ", " +
                "childEntities = " + childEntities + ")";
    }
}