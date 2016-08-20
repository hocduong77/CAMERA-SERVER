package school.camera.persistence.service;

import java.io.IOException;

public interface ICameraService {
	void capture(Long cameraId) throws IOException, InterruptedException;
	
	void record(Long cameraId) throws IOException, InterruptedException;
	
}
