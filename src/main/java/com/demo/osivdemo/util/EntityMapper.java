package com.demo.osivdemo.util;

import com.demo.osivdemo.domain.ChildEntity;
import com.demo.osivdemo.domain.GrandchildEntity;
import com.demo.osivdemo.domain.ParentEntity;
import com.demo.osivdemo.dto.ChildEntityDto;
import com.demo.osivdemo.dto.GrandchildEntityDto;
import com.demo.osivdemo.dto.ParentEntityDto;

import java.util.ArrayList;

public class EntityMapper {


    public static ParentEntityDto mapEntityToDTO(ParentEntity parentEntity) {
        ParentEntityDto parentEntityDto = new ParentEntityDto(parentEntity.getId(), parentEntity.getName(),
                parentEntity.getDescription(), new ArrayList<>());

        for (ChildEntity childEntity : parentEntity.getChildEntities()) {
            ChildEntityDto childEntityDto = new ChildEntityDto(childEntity.getId(), childEntity.getName(),
                    childEntity.getDescription(), new ArrayList<>());
            parentEntityDto.getChildEntities().add(childEntityDto);
            for (GrandchildEntity grandchildEntity : childEntity.getGrandchildEntities()) {
                GrandchildEntityDto grandchildEntityDto = new GrandchildEntityDto(grandchildEntity.getId(),
                        grandchildEntity.getName(), grandchildEntity.getDescription());
                childEntityDto.getGrandchildEntities().add(grandchildEntityDto);
            }
        }

        return parentEntityDto;
    }
}
