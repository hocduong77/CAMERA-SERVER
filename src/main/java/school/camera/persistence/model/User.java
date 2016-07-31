package school.camera.persistence.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userid;

	private String firstName;

	private String lastName;

	private String email;

	private String password;

	private boolean enabled;

	private boolean tokenExpired;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private Set<Camera> cameras = new HashSet<Camera>(0);

	public Set<Camera> getCameras() {
		return cameras;
	}

	public void setCameras(Set<Camera> cameras) {
		this.cameras = cameras;
	}

	@OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Role role;

	public User() {
		super();
		this.enabled = false;
		this.tokenExpired = false;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String username) {
		this.email = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isTokenExpired() {
		return tokenExpired;
	}

	public void setTokenExpired(boolean expired) {
		this.tokenExpired = expired;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final User user = (User) obj;
		if (!email.equals(user.email))
			return false;
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("User [firstName=").append(firstName).append("]").append("[lastName=").append(lastName)
				.append("]").append("[username").append(email).append("]");
		return builder.toString();
	}

}