package info.rolandkrueger.userservice.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Roland Krüger
 */
@Entity
public class User implements UserDetails {
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String fullName;
    @Email
    private String email;
    private boolean enabled = true;
    private boolean accountNonLocked = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private String rememberMeToken;
    private String registrationConfirmationToken;
    @NotNull
    private LocalDate registrationDate;
    private LocalDateTime lastLogin;
    private List<Authority> authorities;

    public User() {
        registrationDate = LocalDate.now();
    }

    public User(String username) {
        this();
        setUsername(username);
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Column(unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(MoreObjects.firstNonNull(username, "").trim()));
        this.username = username.trim();
    }

    @Override
    @ManyToMany(fetch = FetchType.EAGER)
    public Collection<Authority> getAuthorities() {
        if (authorities == null) {
            return Collections.emptyList();
        }
        return authorities;
    }

    private void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public void setUnencryptedPassword(String password) {
        setPassword(new BCryptPasswordEncoder().encode(password));
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void addAuthority(Authority authority) {
        if (authorities == null) {
            authorities = new ArrayList<>();
        }
        authorities.add(authority);
    }

    public String getRememberMeToken() {
        return rememberMeToken;
    }

    public void setRememberMeToken(String rememberMeToken) {
        this.rememberMeToken = rememberMeToken;
    }

    @Column(unique = true, name = "CONFIRMATION_TOKEN")
    public String getRegistrationConfirmationToken() {
        return registrationConfirmationToken;
    }

    private void setRegistrationConfirmationToken(String registrationConfirmationToken) {
        this.registrationConfirmationToken = registrationConfirmationToken;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean hasAuthority(String authority) {
        Preconditions.checkArgument(authority != null);
        return hasAuthority(new Authority(authority));
    }

    public boolean hasAuthority(Authority authority) {
        Preconditions.checkArgument(authority != null);
        return !getAuthorities().isEmpty() && getAuthorities().contains(authority);
    }

    public String createRegistrationConfirmationToken() {
        final String uuid = UUID.randomUUID().toString();
        setRegistrationConfirmationToken(uuid);
        return uuid;
    }

    public void clearRegistrationConfirmationToken() {
        this.registrationConfirmationToken = null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("username", username)
                .add("fullname", fullName)
                .add("roles", authorities)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof User)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        User other = (User) obj;
        return Objects.equals(username, other.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    public void clearPassword() {
        password = null;
    }
}