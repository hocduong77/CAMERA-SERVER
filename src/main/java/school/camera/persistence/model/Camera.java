package school.camera.persistence.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Camera {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long cameraid;

	private String cameraUrl;

	private boolean enabled;

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
