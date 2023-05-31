package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class UserRepositoryTest {
    @Autowired
    UserRepositoryDb userRepository;

    User user;

    @Test
    void findById() {
        user = User.builder()
                .id(1)
                .name("Bademus")
                .email("cur@cur.com")
                .build();
        userRepository.save(user);

        User userResponse = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(userResponse.getId(), user.getId());
        assertEquals(userResponse.getName(), user.getName());
        assertEquals(userResponse.getEmail(), user.getEmail());
    }
}
