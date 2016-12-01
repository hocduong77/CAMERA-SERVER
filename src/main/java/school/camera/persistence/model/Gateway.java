package school.camera.persistence.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Gateway {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer gatewayId;

	private String gatewayIP;

	public Integer getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Integer gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getGatewayIP() {
		return gatewayIP;
	}

	public void setGatewayIP(String gatewayIP) {
		this.gatewayIP = gatewayIP;
	}
	
	
}
