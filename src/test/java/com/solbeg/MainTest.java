package com.solbeg;

import com.solbeg.model.User;
import com.solbeg.model.UserBankAccount;
import com.solbeg.service.UserBankAccountProvider;
import com.solbeg.service.UserProvider;
import com.solbeg.service.UserService;
import com.solbeg.service.Users;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    private static final List<User> users = Arrays.asList(
            User.builder().id(1L).name("Justin").email("justin.butler@gmail.com").balance(BigDecimal.valueOf(172966)).build(),
            User.builder().id(2L).name("Olivia").email("cardenas@mail.com").balance(BigDecimal.valueOf(38029)).build(),
            User.builder().id(3L).name("Nolan").email("nolandonovan@gmail.com").balance(BigDecimal.valueOf(13889)).build(),
            User.builder().id(4L).name("Lucas").email("lucas.lynn@yahoo.com").balance(BigDecimal.valueOf(16980)).build());

    private static final List<UserBankAccount> bankAccounts = Arrays.asList(
            new UserBankAccount(new User(1L, "Justin", "justin.butler@gmail.com", BigDecimal.valueOf(172966)), BigDecimal.valueOf(172966)),
            new UserBankAccount(new User(2L, "Olivia", "cardenas@mail.com", BigDecimal.valueOf(38029)), BigDecimal.valueOf(38029)),
            new UserBankAccount(new User(3L, "Nolan", "nolandonovan@gmail.com", BigDecimal.valueOf(13889)), BigDecimal.valueOf(13889)),
            new UserBankAccount(new User(4L, "Lucas", "lucas.lynn@yahoo.com", BigDecimal.valueOf(16980)), BigDecimal.valueOf(16980)));

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;


    @Test
    void ifStringIsPresent_isPresentShouldReturnTrueIsEmptyFalse() {

        assertTrue(Main.optionalOfString("text").isPresent());
        assertFalse(Main.optionalOfString("text").isEmpty());
    }

    @Test
    void ifStringIsNull_isPresentShouldReturnFalseIsEmptyTrue() {

        assertFalse(Main.optionalOfString(null).isPresent());
        assertTrue(Main.optionalOfString(null).isEmpty());
    }

    @Test
    void ifBalanceZero_balanceShouldBeEqualsAmount() {

        BigDecimal amount = new BigDecimal("100");
        User user = Users.generateUser();
        user.setBalance(new BigDecimal("0"));
        UserProvider userProvider = () -> Optional.of(user);

        Main.deposit(userProvider, amount);

        assertEquals(amount, user.getBalance());
    }

    @Test
    void ifIsNull_shouldTrowNPE() {

        User user = Users.generateUser();

        assertTrue(Main.optionalOfUser(user).isPresent());
    }

    @Test
    void ifUserProviderDoNotProvideUser_shouldReturnDefaultUser() {

        User defaultUser = Users.generateUser();
        UserProvider userProvider = Optional::empty;

        assertEquals(defaultUser, Main.getUser(userProvider, defaultUser));
    }

    @BeforeEach
    public void setUpStreams() {

        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {

        System.setOut(originalOut);
    }

    @Test
    void ifUserIsNotProvided_shouldPrintMessage() {

        String expectedMessage = "No user found\r\n";
        UserProvider userProvider = Optional::empty;
        UserService userService = new UserService() {
            @Override
            public void processUser(User user) {
            }
        };

        Main.processUser(userProvider, userService);

        assertEquals(expectedMessage, outContent.toString());
    }

    @Test
    void ifUserNotProvided_shouldGenerateOneUser() {

        assertEquals(Users.generateUser(), Main.getOrGenerateUser(Optional::empty));
    }

    @Test
    void ifUserNotProvided_shouldTrowNoSuchElementException() {

        UserProvider userProvider = Optional::empty;

        assertThrows(NoSuchElementException.class, () -> Main.retrieveBalance(userProvider));
    }

    @Test
    void ifUserProvided_shouldReturnNotNullValue() {

        UserProvider userProvider = new UserProvider() {
            @Override
            public Optional<User> getUser() {
                return Optional.ofNullable(Users.generateUser());
            }
        };

        assertNotNull(Main.retrieveBalance(userProvider));
    }

    @Test
    void ifUserNotProvided_shouldThrowRuntimeExceptionWithMessage() {

        UserProvider userProvider = Optional::empty;

        assertThrowsExactly(RuntimeException.class, () -> Main.getUser(userProvider), "No user provided");
    }

    @Test
    void ifCreditBalanceIsPresent_shouldReturnNotNull() {

        UserBankAccountProvider userBankAccountProvider = new UserBankAccountProvider() {
            @Override
            public Optional<UserBankAccount> getUserBankAccount() {
                return Optional.of(new UserBankAccount());
            }
        };

        assertNotNull(Main.retrieveCreditBalance(userBankAccountProvider));
    }

    @Test
    void ifUserWithEndOfEMailIsPresent_shouldReturnThisUser() {

        UserProvider user = () -> Optional.ofNullable(Users.generateUser());

        assertNotNull(Main.retrieveUserGmail(user));
        assertEquals("m@gmail.com", Main.retrieveUserGmail(user).get().getEmail());
    }

    @Test
    void ifUserNotProvidedFromAllProvider_shouldThrowNoSuchElementException() {

        UserProvider userProvider = Optional::empty;
        UserProvider userProviderFallBack = Optional::empty;

        assertThrows(NoSuchElementException.class, () -> Main.getUserWithFallback(userProvider, userProviderFallBack));
    }

    @Test
    void ifUserNotProvidedFromUserProvider_userShouldBeProvideFromFallBackProvider() {

        UserProvider userProvider = Optional::empty;
        UserProvider userProviderFallBack = new UserProvider() {
            @Override
            public Optional<User> getUser() {
                return Optional.ofNullable(Users.generateUser());
            }
        };

        assertNotNull(Main.getUserWithFallback(userProvider, userProviderFallBack));
    }

    @Test
    void ifInputListIsEmpty_shouldThrowNoSuchElementException() {

        assertThrows(NoSuchElementException.class, () -> Main.getUserWithMaxBalance(List.of()));
    }

    @Test
    void shouldReturnCorrectBalance() {

        OptionalDouble falseBalance = bankAccounts.stream()
                .mapToDouble(bankAccount -> bankAccount.getCreditBalance().get().doubleValue())
                .max();
        OptionalDouble expectedBalance = bankAccounts.stream()
                .mapToDouble(bankAccount -> bankAccount.getCreditBalance().get().doubleValue())
                .min();

        assertEquals(expectedBalance, Main.findMinBalanceValue(users));
        assertNotEquals(falseBalance, Main.findMinBalanceValue(users));
    }

    @Test
    void shouldReturnCorrectBalanceSum() {

        Double falseSum = 12345.899;
        Double expectedSum = bankAccounts.stream()
                .mapToDouble(bankAccount -> bankAccount.getCreditBalance().get().doubleValue())
                .sum();

        assertEquals(expectedSum, Main.calculateTotalCreditBalance(bankAccounts));
        assertNotEquals(falseSum, Main.calculateTotalCreditBalance(bankAccounts));
    }
}