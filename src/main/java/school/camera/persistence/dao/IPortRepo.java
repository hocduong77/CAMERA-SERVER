package school.camera.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.Port;

public interface IPortRepo extends JpaRepository<Port, Integer> {

}
