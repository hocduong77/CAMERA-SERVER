package school.camera.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.Notification;
import school.camera.persistence.model.Video;

public interface INotificationRepo extends JpaRepository<Notification, Integer>{

}
