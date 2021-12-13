package org.wcdevs.blog.core.common;

/**
 * Entity transformer.
 *
 * @param <E> Type of entityTransform
 * @param <D> TYpe of the dto to transform
 */
public interface EntityTransformer<E, D> {
  E newEntityFromDto(D dto);

  D dtoFromEntity(E entity);
}
