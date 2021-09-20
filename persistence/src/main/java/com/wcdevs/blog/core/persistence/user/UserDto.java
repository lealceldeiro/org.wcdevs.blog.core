package com.wcdevs.blog.core.persistence.user;

import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Data transfer object which contains a user information.
 */
public class UserDto {
  private static final String PSW_DESCRIPTION = "The password must be between 8 and 500 characters "
                                                + "and must contain at least: one digit, one lower "
                                                + "case letter, one upper case letter, and one of "
                                                + "each of the following characters: !@#$%^&*";
  private Long id;

  @NotNull
  @NotBlank
  @Size(max = 30, min = 2)
  private String username;

  @NotNull
  @NotBlank
  @Email
  @Size(max = 30, min = 3)
  private String email;

  @NotNull
  @NotBlank
  @Size(min = 8, max = 500)
  @Pattern(regexp = ".*[0-9]+.*", message = PSW_DESCRIPTION)
  @Pattern(regexp = ".*[a-z]+.*", message = PSW_DESCRIPTION)
  @Pattern(regexp = ".*[A-Z]+.*", message = PSW_DESCRIPTION)
  @Pattern(regexp = ".*[!@#$%^&*]+.*", message = PSW_DESCRIPTION)
  private String password;

  @NotBlank
  private String name;

  @NotBlank
  private String lastName;

  private LocalDate signUpDate;

  public UserDto() {
  }

  /**
   * Creates a new {@link UserDto} instance.
   *
   * @param id         - the autogenerated id
   * @param username   - User's username
   * @param email      - User's email
   * @param password   - User's password hash.
   * @param name       - User's
   * @param lastName   - User's lastname
   * @param signUpDate - User's date when user signed up
   */
  public UserDto(final Long id, final String username, final String email, final String password,
                 final String name, final String lastName, final LocalDate signUpDate) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.name = name;
    this.lastName = lastName;
    this.signUpDate = signUpDate;
  }

  // region getters and setters
  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  public LocalDate getSignUpDate() {
    return signUpDate;
  }

  public void setSignUpDate(final LocalDate signUpDate) {
    this.signUpDate = signUpDate;
  }
  // endregion
}
