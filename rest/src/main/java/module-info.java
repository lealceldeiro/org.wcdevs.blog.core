open module org.wcdevs.blog.core.rest {
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.boot.starter.validation;
  requires spring.context;
  requires spring.beans;
  requires spring.core;
  requires spring.data.commons;
  requires spring.web;
  requires spring.webmvc;
  requires spring.security.config;
  requires spring.security.web;
  requires spring.security.core;
  requires spring.security.oauth2.resource.server;
  requires spring.security.oauth2.jose;
  requires spring.tx;

  requires lombok;

  requires org.slf4j;
  requires com.fasterxml.jackson.databind;

  requires aws.java.sdk.cognitoidp;
  requires aws.java.sdk.core;

  requires org.wcdevs.blog.core.common;
}
