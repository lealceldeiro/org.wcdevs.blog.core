open module com.wcdevs.blog.core.common {
  requires transitive com.wcdevs.blog.core.persistance;
  requires spring.context;
  requires transitive spring.tx;
  requires spring.aop;

  exports com.wcdevs.blog.core.common.post;
  exports com.wcdevs.blog.core.common.exception;
}
