package com.wcdevs.blog.core.persistence.user;

import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class that represents a DB table abstraction containing a user information.
 */
@Entity
@Table(name = "user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "username", unique = true, nullable = false, length = 30)
  private String username;

  @Column(name = "email", unique = true, nullable = false, length = 30)
  private String email;

  @Column(name = "password", unique = true, nullable = false, length = 500)
  private String password;

  @Column(name = "name", length = 50)
  private String name;

  @Column(name = "last_name", length = 100)
  private String lastName;

  @Column(name = "sign_up_date", nullable = false)
  private LocalDate signUpDate;

  public User() {
  }

  /**
   * Instantiates a new User.
   *
   * @param username   - User's username
   * @param email      - User's email
   * @param password   - User's password hash.
   * @param name       - User's
   * @param lastName   - User's lastname
   * @param signUpDate - User's date when user signed up
   */
  public User(final String username, final String email, final String password,
              final String name, final String lastName, final LocalDate signUpDate) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.name = name;
    this.lastName = lastName;
    this.signUpDate = signUpDate;
  }

  // region getters and setters
  Long getId() {
    return id;
  }

  void setId(final Long id) {
    this.id = id;
  }

  String getUsername() {
    return username;
  }

  void setUsername(final String username) {
    this.username = username;
  }

  String getEmail() {
    return email;
  }

  void setEmail(final String email) {
    this.email = email;
  }

  String getPassword() {
    return password;
  }

  void setPassword(final String password) {
    this.password = password;
  }

  String getName() {
    return name;
  }

  void setName(final String name) {
    this.name = name;
  }

  String getLastName() {
    return lastName;
  }

  void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  LocalDate getSignUpDate() {
    return signUpDate;
  }

  void setSignUpDate(final LocalDate signUpDate) {
    this.signUpDate = signUpDate;
  }
  // endregion

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    boolean userNameEquals = getUsername().equals(user.getUsername());
    boolean emailEquals = getEmail().equals(user.getEmail());

    if (getId() == null && user.getId() == null && !userNameEquals && !emailEquals) {
      return false;  // two newly created entities
    }
    return getId().equals(user.getId()) && userNameEquals && emailEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUsername(), getEmail());
  }

  @Override
  public String toString() {
    return "User{"
           + "id=" + id
           + ", username='" + username + '\''
           + ", email='" + email + '\''
           + ", name='" + name + '\''
           + ", lastName='" + lastName + '\''
           + ", signUpDate=" + signUpDate
           + '}';
  }
}
