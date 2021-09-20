module com.wcdevs.blog.core.persistance {
  requires java.sql;
  requires java.persistence;
  requires java.validation;
  requires spring.data.jpa;

  exports com.wcdevs.blog.core.persistence.user to com.wcdevs.blog.core.common;
}
