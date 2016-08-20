package school.camera.persistence.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Video;

public interface IVideoRepo extends JpaRepository<Video, Long>{
public List<Video> findByCameraAndDateBetween(Camera camera, Date from, Date to);
	
	public List<Video> findByCamera(Camera camera);
}
