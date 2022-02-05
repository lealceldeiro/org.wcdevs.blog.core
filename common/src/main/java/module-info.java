open module org.wcdevs.blog.core.common {
  requires transitive org.wcdevs.blog.core.persistance;
  requires spring.context;
  requires spring.tx;
  requires spring.aop;
  requires spring.data.commons;
  requires lombok;

  exports org.wcdevs.blog.core.common.post;
  exports org.wcdevs.blog.core.common.comment;
  exports org.wcdevs.blog.core.common.exception;
  exports org.wcdevs.blog.core.common.util;
}
