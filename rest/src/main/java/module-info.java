module com.wcdevs.blog.core.rest {
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.beans;
  requires spring.core;
  requires spring.web;

  opens com.wcdevs.blog.core.rest;
}
