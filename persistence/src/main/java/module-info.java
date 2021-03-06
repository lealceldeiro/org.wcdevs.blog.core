open module org.wcdevs.blog.core.persistance {
  requires java.sql;
  requires java.persistence;
  requires java.validation;
  requires spring.data.jpa;
  requires spring.context;
  requires spring.data.commons;
  requires org.hibernate.orm.core;
  requires spring.boot.autoconfigure;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.databind;
  requires lombok;

  exports org.wcdevs.blog.core.persistence.post;
  exports org.wcdevs.blog.core.persistence.comment;
  exports org.wcdevs.blog.core.persistence.util;
}
