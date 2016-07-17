package school.camera.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import school.camera.persistence.model.User;
import school.camera.persistence.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    public VerificationToken findByToken(String token);

    public VerificationToken findByUser(User user);
}
