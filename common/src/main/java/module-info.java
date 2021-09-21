open module com.wcdevs.blog.core.common {
  requires transitive com.wcdevs.blog.core.persistance;
  requires spring.context;

  exports com.wcdevs.blog.core.common.user;
}
