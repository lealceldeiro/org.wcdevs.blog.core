open module com.wcdevs.blog.core.common {
  requires transitive com.wcdevs.blog.core.persistance;
  requires spring.context;
  requires spring.tx;
  requires spring.aop;

  exports com.wcdevs.blog.core.common.user;
  exports com.wcdevs.blog.core.common.exception;
}
