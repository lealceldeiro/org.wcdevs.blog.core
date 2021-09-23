open module org.wcdevs.blog.core.common {
  requires transitive org.wcdevs.blog.core.persistance;
  requires spring.context;
  requires transitive spring.tx;
  requires spring.aop;

  exports org.wcdevs.blog.core.common.post;
  exports org.wcdevs.blog.core.common.exception;
}
