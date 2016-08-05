package school.camera.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.Camera;
import school.camera.persistence.model.User;;

public interface CameraRepo extends JpaRepository<Camera, Long>{
	public List<Camera> findByUser(User user);
	public Camera findByCameraid(Long cameraId);
}
