package school.camera.persistence.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Image;

public interface IImageRepo extends JpaRepository<Image, Long>{
	public List<Image> findByCameraAndDateBetween(Camera camera, Date from, Date to);
	
	public List<Image> findByCamera(Camera camera);
	
}
