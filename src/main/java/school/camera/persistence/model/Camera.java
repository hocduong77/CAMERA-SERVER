package school.camera.persistence.model;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Camera {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long cameraid;

	private String cameraUrl;

	private String alias;
	
	private String name;
	
	private String streamUrl;
	
	private boolean enabled;
	
	private int port;

	private boolean security;
	
	private double objectWith;
	
	private double objectHeight;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid", nullable = false)
	private User user;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "camera")
	private Set<Image> image = new HashSet<Image>(0);
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "camera")
	private Set<Video> video = new HashSet<Video>(0);

	@OneToOne(mappedBy = "camera", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private CamearSchedule schedule;
	
	

	public double getObjectWith() {
		return objectWith;
	}

	public void setObjectWith(double objectWith) {
		this.objectWith = objectWith;
	}

	public double getObjectHeight() {
		return objectHeight;
	}

	public void setObjectHeight(double objectHeight) {
		this.objectHeight = objectHeight;
	}	

	public boolean isSecurity() {
		return security;
	}

	public void setSecurity(boolean security) {
		this.security = security;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Set<Image> getImage() {
		return image;
	}

	public void setImage(Set<Image> image) {
		this.image = image;
	}

	public Set<Video> getVideo() {
		return video;
	}

	public void setVideo(Set<Video> video) {
		this.video = video;
	}

	public CamearSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(CamearSchedule schedule) {
		this.schedule = schedule;
	}

	public String getStreamUrl() {
		return streamUrl;
	}

	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCameraid() {
		return cameraid;
	}

	public void setCameraid(Long cameraid) {
		this.cameraid = cameraid;
	}

	public String getCameraUrl() {
		return cameraUrl;
	}

	public void setCameraUrl(String cameraUrl) {
		this.cameraUrl = cameraUrl;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	


}
