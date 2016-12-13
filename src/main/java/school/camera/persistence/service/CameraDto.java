package school.camera.persistence.service;

public class CameraDto {

	private Long cameraId;

	private String cameraUrl;

	private String alias;

	private String name;

	private String streamUrl;

	private boolean enabled;

	private boolean security;

	private boolean capture;

	private boolean record;

	private boolean captureRepeat;

	private boolean recordRepeat;

	private int captureTime;

	private String recordTime;

	private String recordSchedule;

	private String captureFrom;

	private String captureTo;

	private String recordFrom;

	private String recordTo;

	private Long securityId;

	private int port;

	private double objectWith;

	private double objectHeight;

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

	public boolean isCaptureRepeat() {
		return captureRepeat;
	}

	public void setCaptureRepeat(boolean captureRepeat) {
		this.captureRepeat = captureRepeat;
	}

	public boolean isRecordRepeat() {
		return recordRepeat;
	}

	public void setRecordRepeat(boolean recordRepeat) {
		this.recordRepeat = recordRepeat;
	}

	public String getCaptureFrom() {
		return captureFrom;
	}

	public void setCaptureFrom(String captureFrom) {
		this.captureFrom = captureFrom;
	}

	public String getCaptureTo() {
		return captureTo;
	}

	public void setCaptureTo(String captureTo) {
		this.captureTo = captureTo;
	}

	public String getRecordFrom() {
		return recordFrom;
	}

	public void setRecordFrom(String recordFrom) {
		this.recordFrom = recordFrom;
	}

	public String getRecordTo() {
		return recordTo;
	}

	public void setRecordTo(String recordTo) {
		this.recordTo = recordTo;
	}

	public boolean isCapture() {
		return capture;
	}

	public void setCapture(boolean capture) {
		this.capture = capture;
	}

	public boolean isRecord() {
		return record;
	}

	public void setRecord(boolean record) {
		this.record = record;
	}

	public int getCaptureTime() {
		return captureTime;
	}

	public void setCaptureTime(int captureTime) {
		this.captureTime = captureTime;
	}


	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public String getRecordSchedule() {
		return recordSchedule;
	}

	public void setRecordSchedule(String recordSchedule) {
		this.recordSchedule = recordSchedule;
	}

	public Long getCameraId() {
		return cameraId;
	}

	public void setCameraId(Long cameraId) {
		this.cameraId = cameraId;
	}

	public String getStreamUrl() {
		return streamUrl;
	}

	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}

	public String getCameraUrl() {
		return cameraUrl;
	}

	public void setCameraUrl(String cameraUrl) {
		this.cameraUrl = cameraUrl;
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Long getSecurityId() {
		return securityId;
	}

	public void setSecurityId(Long securityId) {
		this.securityId = securityId;
	}

}
