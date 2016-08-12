package school.camera.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.Video;

public interface IVideoRepo extends JpaRepository<Video, Long>{
	
}
