package service;

import dao.UserDao;
import dto.User;
import exension.ConditionalExtension;
import exension.GlobalExtension;
import exension.ThrowableExtension;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import exension.UserServiceParamResolver;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;

//@Tag("fast")
//@Tag("user")
//@TestMethodOrder(MethodOrderer.Random.class) и тд
@ExtendWith({
        UserServiceParamResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        ThrowableExtension.class,
        GlobalExtension.class,
        MockitoExtension.class
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@RunWith()
public class UserServiceTest extends TestBase {
    private static final User user1 = User.of(1, "Ivan", "123");
    private static final User user2 = User.of(2, "Petr", "111");

    //    @Rule
//    ExpectedException

    @InjectMocks
    private UserService userService;

    @Mock(lenient = true)
    private UserDao userDao;

    @Captor
    private ArgumentCaptor<Integer> argumentCaptor;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    static void init() {
        System.out.println("Before all: ");
    }

    @BeforeEach
    void prepare() {                       //UserService
        System.out.println("Before each: " + this);
        Mockito.lenient().when(userDao.delete(user1.getId())).thenReturn(true);
 //        this.userService = userService;
//        this.userDao = Mockito.mock(UserDao.class);
        Mockito.doReturn(true).when(userDao).delete(user1.getId());
        Mockito.mock(UserDao.class, Mockito.withSettings().lenient());
        this.userDao = Mockito.spy(new UserDao());
        this.userService = new UserService(userDao);
    }

    @Test
    void throwExceptionIfDatabaseIsNotAvailable() {
        Mockito.doThrow(RuntimeException.class).when(userDao).delete(user1.getId());
        Assertions.assertThrows(RuntimeException.class, () -> userService.delete(user1.getId()));
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(user1);
        Mockito.doReturn(true).when(userDao).delete(user1.getId());
        BDDMockito.given(userDao.delete(user1.getId())).willReturn(true);
        BDDMockito.willReturn(true).given(userDao).delete(user1.getId());
//        Mockito.doReturn(true).when(userDao).delete(Mockito.any());
        boolean deletedResult = userService.delete(user1.getId());
//        boolean deletedResult = userService.delete(2);
/*        Mockito.when(userDao.delete(user1.getId()))
                .thenReturn(true)
                .thenReturn(false);*/
        System.out.println(userService.delete(user1.getId()));
        ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(userDao, Mockito.times(2)).delete(argumentCaptor.capture());
//        assertThat(argumentCaptor.getValue()).isEqualsTo(user1.getId());
//        Mockito.reset(userDao);
//        assertTh at(deletedResult).isTrue();

    }

    @Test
    @Order(1)
    void usersEmptyIfNoUserAdded() throws IOException {
        if (true) {
//            throw new IOException();
            throw new RuntimeException();
        }
        System.out.println("Test 1" + this);
        List<User> users = userService.getAll();

        MatcherAssert.assertThat(users, IsEmptyCollection.empty());
        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2" + this);

        userService.add(user1);
        userService.add(user2);

        List<User> users = userService.getAll();
        Assertions.assertEquals(2, users.size());
//        assertThat(users).hasSize(2);
    }

    @Test
    void loginSuccessIfUserExists() {
        userService.add(user1);
        Optional<User> user = userService.login(user1.getUsername(), user1.getPassword());

//        assertThat(user).isPresent();

        Assertions.assertTrue(user.isPresent());

//        user.ifPresent(user3 -> assertThat(user3).isEqualTo(user1));
        user.ifPresent(user3 -> Assertions.assertEquals(user1, user3));
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(user1, user2);
        Map<Integer, User> userMap = userService.getAllConvertedById();

        MatcherAssert.assertThat(userMap, IsMapContaining.hasKey(user1.getId()));

//        assertAll(
//                () -> assertThat(userMap).constainsKeys(user1.getId(), user2.getId()),
//                () -> assertThat(userMap).constainsValues(user1, user2)
//        );
    }


    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all: ");

    }

    @Nested
    @Tag("login")
    class LoginTest {
        @Test
//        @Tag("login")
        @Disabled("flaky, need to see")
        void loginFailIfPasswordIsNotCorrect() {
            userService.add(user1);
            Optional<User> maybeUser = userService.login(user1.getUsername(), "dummy");
            Assertions.assertTrue(maybeUser.isEmpty());
        }

        //        @Test
        @RepeatedTest(value = 5, name = RepeatedTest.LONG_DISPLAY_NAME)
//        @Tag("login")
        void loginFailIfUserDoesNotExist(RepetitionInfo repetitionInfo) {
            userService.add(user1);
            var maybeUser = userService.login("dummy", user1.getPassword());
            Assertions.assertTrue(maybeUser.isEmpty());
        }

        @Test
        @Timeout(value = 200L, unit = TimeUnit.MILLISECONDS)
        void checkLoginFunctionalityPerformance() {
//            Optional<User> result1 = Assertions.assertTimeout(Duration.ofMillis(200L), () -> {
//                Thread.sleep(300L);
//                return userService.login("dummy", user1.getPassword());
//            });
//
//            System.out.println(Thread.currentThread().getName());
//            Optional<User> result2 = Assertions.assertTimeoutPreemptively(Duration.ofMillis(200L), () -> {
//                Thread.sleep(300L);
//                System.out.println(Thread.currentThread().getName());
//                return userService.login("dummy", user1.getPassword());
//            });
        }

        @Test
//    @Test(expected = IllegalArgumentException.class )
//        @Tag("login")
        void throwExceptionIfUsernameOrPasswordIsNull() {
            Assertions.assertAll(
                    () -> {
                        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy"));
//                    assertThat(exception.getMessage()).isEqualsTo("Username of password is null");
                    },
                    () -> Assertions.assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
            );

//        try {
//            userService.login(null, "dummy");
//            Assertions.fail("Login should throw exception on null username");
//        }catch (IllegalArgumentException e) {
//            Assertions.assertTrue(true) ;
//        }
        }

        @ParameterizedTest
//    @ArgumentsSource()
        @NullSource
        @EmptySource
        @ValueSource(strings = {
                "Ivan", "Petr"
        })
        @EnumSource
        @MethodSource("service.UserServiceTest#getArgumentForLoginTest")
        @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
//        @CsvSource({
//                "Ivan,123",
//                "Petr,11"
//        })
//    @NullAndEmptySource
        void loginParametrizedTest(String username, String password, Optional<User> user) {
            userService.add(user1, user2);
            var maybeUser = userService.login(username, password);
//            assertThat(maybeUser).isEqualTo(user);
        }

    }

    static Stream<Arguments> getArgumentForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(user1)),
                Arguments.of("Petr", "111", Optional.of(user2)),
                Arguments.of("Petr", "dummy", Optional.empty(), Arguments.of("dummy", "123", Optional.of(user2))
                )
        );
    }
}
