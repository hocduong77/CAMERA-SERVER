package school.camera.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.CamearSchedule;
import school.camera.persistence.model.Camera;

public interface ScheduleRepo extends JpaRepository<CamearSchedule, Long>{
	public CamearSchedule findBycamera(Camera camera);
}
