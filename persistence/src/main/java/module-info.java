open module com.wcdevs.blog.core.persistance {
  requires java.sql;
  requires java.persistence;
  requires java.validation;
  requires spring.data.jpa;
  requires spring.context;
  requires org.hibernate.orm.core;
  requires spring.boot.autoconfigure;
  requires com.fasterxml.jackson.annotation;

  exports com.wcdevs.blog.core.persistence.post;
}
