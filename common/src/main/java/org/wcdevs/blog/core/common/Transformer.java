package org.wcdevs.blog.core.common;

/**
 * Entity transformer.
 *
 * @param <E> Type of entity to transform
 * @param <D> Type of the dto to transform
 * @param <P> Type of the partial dto to use to get the new values for the entity. The same type
 *            as the one used for {@code D} can be used if there's no different {@code P} type
 */
public interface Transformer<E, D, P> {
  E newEntityFromDto(D dto);

  D dtoFromEntity(E entity);

  void update(E entity, D dto);

  void updateNonNullValues(E entity, P partialDto);
}
