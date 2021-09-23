open module org.wcdevs.blog.core.rest {
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.beans;
  requires spring.core;
  requires spring.web;
  requires spring.webmvc;

  requires org.wcdevs.blog.core.common;
}
