package school.camera.persistence.service;

import school.camera.persistence.model.User;
import school.camera.persistence.model.VerificationToken;
import school.camera.validation.service.EmailExistsException;

public interface IUserService {

    User registerNewUserAccount(UserDto accountDto) throws EmailExistsException;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

}
