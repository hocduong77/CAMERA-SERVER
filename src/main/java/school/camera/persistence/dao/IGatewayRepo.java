package school.camera.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Gateway;
import school.camera.persistence.model.User;;

public interface IGatewayRepo extends JpaRepository<Gateway, Integer> {

}
