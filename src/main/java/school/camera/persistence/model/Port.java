package school.camera.persistence.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Port {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer portId;

	private Integer port;

	private boolean status;

	@OneToOne(mappedBy = "port", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Camera camera;

	public Integer getPortId() {
		return portId;
	}

	public void setPortId(Integer portId) {
		this.portId = portId;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
