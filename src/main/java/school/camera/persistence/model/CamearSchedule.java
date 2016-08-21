package school.camera.persistence.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class CamearSchedule {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long scheduleId;
	
	private boolean capture;
	
	private boolean record;
	
	private boolean captureRepeat;
	
	private boolean recordRepeat;
	
	private int captureTime;
	
	private Date captureFrom;
	
	private Date captureTo;
	
	private Date recordFrom;
	
	private Date recordTo;
	
	private int recordTime;
	
	private Date recordSchedule;
	
    @OneToOne(targetEntity = Camera.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "cameraid")
    private Camera camera;
    
    

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

	public Date getCaptureFrom() {
		return captureFrom;
	}

	public void setCaptureFrom(Date captureFrom) {
		this.captureFrom = captureFrom;
	}

	public Date getCaptureTo() {
		return captureTo;
	}

	public void setCaptureTo(Date captureTo) {
		this.captureTo = captureTo;
	}

	public Date getRecordFrom() {
		return recordFrom;
	}

	public void setRecordFrom(Date recordFrom) {
		this.recordFrom = recordFrom;
	}

	public Date getRecordTo() {
		return recordTo;
	}

	public void setRecordTo(Date recordTo) {
		this.recordTo = recordTo;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
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

	public int getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(int recordTime) {
		this.recordTime = recordTime;
	}

	public Date getRecordSchedule() {
		return recordSchedule;
	}

	public void setRecordSchedule(Date recordSchedule) {
		this.recordSchedule = recordSchedule;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
    
    
    
}
