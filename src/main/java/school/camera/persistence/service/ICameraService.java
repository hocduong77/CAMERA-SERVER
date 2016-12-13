package school.camera.persistence.service;

import java.io.IOException;

public interface ICameraService {
	
	String captureNew(Long cameraId) throws IOException, InterruptedException;
	
	String recordNew(Long cameraId) throws IOException, InterruptedException;
}
