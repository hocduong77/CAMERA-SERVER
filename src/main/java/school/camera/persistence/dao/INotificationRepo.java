package school.camera.persistence.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Image;
import school.camera.persistence.model.Notification;
import school.camera.persistence.model.Video;

public interface INotificationRepo extends JpaRepository<Notification, Integer> {

	List<Notification> findByCameraId(Long cameraid);

	List<Notification> findByCameraIdAndStartTimeBetween(Long cameraid, Date from, Date to);

}
