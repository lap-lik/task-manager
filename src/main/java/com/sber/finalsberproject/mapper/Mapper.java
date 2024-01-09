package com.sber.finalsberproject.mapper;

import com.sber.finalsberproject.dto.GenericDTO;
import com.sber.finalsberproject.model.GenericModel;

import java.util.List;

public interface Mapper<E extends GenericModel, D extends GenericDTO> {
    E toEntity(D dto);

    D toDTO(E entity);

    List<E> toEntities(List<D> dtos);

    List<D> toDTOs(List<E> entities);
}
