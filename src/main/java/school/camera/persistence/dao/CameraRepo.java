package school.camera.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.Camera;;

public interface CameraRepo extends JpaRepository<Camera, Long>{

}
