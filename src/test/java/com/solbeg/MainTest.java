package com.solbeg;

import com.solbeg.model.User;
import com.solbeg.model.UserBankAccount;
import com.solbeg.service.UserBankAccountProvider;
import com.solbeg.service.UserProvider;
import com.solbeg.service.UserService;
import com.solbeg.service.Users;
import org.junit.jupiter.api.Test;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Test
    void ifStringIsPresent_isPresentShouldReturnTrue() {
        assertTrue(Main.optionalOfString("text").isPresent());
    }

    @Test
    void ifStringIsNull_isPresentShouldReturnFalse() {
        assertFalse(Main.optionalOfString(null).isPresent());
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

    @Test
    void ifUserIsNotProvided_shouldCallProcessWithoutNoUserOneTime() {
        UserProvider userProvider = mock(UserProvider.class);
        UserService userService = mock(UserService.class);
        Main.processUser(userProvider, userService);
        verify(userService, times(1)).processWithNoUser();
    }

    @Test
    void ifUserNotProvided_shouldGenerateOneUser() {
        assertEquals(Users.generateUser(), Main.getOrGenerateUser(Optional::empty));
    }

    @Test
    void ifUserNotProvided_shouldReturnEmpty() {
        UserProvider userProvider = Optional::empty;
        assertTrue(Main.retrieveBalance(userProvider).isEmpty());
    }

    @Test
    void ifUserProvided_shouldReturnNotNullValue() {
        UserProvider userProvider = () -> Optional.ofNullable(Users.generateUser());
        assertNotNull(Main.retrieveBalance(userProvider));
    }

    @Test
    void ifUserNotProvided_shouldThrowRuntimeExceptionWithMessage() {
        UserProvider userProvider = Optional::empty;
        assertThrowsExactly(RuntimeException.class, () -> Main.getUser(userProvider), "No user provided");
    }

    @Test
    void ifUserBankAccountNotProvided_shouldThrowNoSuchElementException() {
        UserBankAccountProvider userBankAccountProvider = Optional::empty;
        assertThrows(NoSuchElementException.class, () -> Main.retrieveCreditBalance(userBankAccountProvider));
    }

    @Test
    void ifUserWithEndOfEMailIsPresent_shouldReturnThisUser() {
        UserProvider user = () -> Optional.ofNullable(Users.generateUser());
        assertNotNull(Main.retrieveUserGmail(user));
        assertEquals(Optional.of("m@gmail.com"), Main.retrieveUserGmail(user).map(User::getEmail));
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
        UserProvider userProviderFallBack = () -> Optional.ofNullable(Users.generateUser());
        assertNotNull(Main.getUserWithFallback(userProvider, userProviderFallBack));
    }

    @Test
    void ifInputListIsEmpty_shouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> Main.getUserWithMaxBalance(List.of()));
    }

    @Test
    void shouldReturnCorrectBalance() {
        OptionalDouble falseBalance = OptionalDouble.of(12);
        OptionalDouble expectedBalance = OptionalDouble.of(13889);
        assertEquals(expectedBalance, Main.findMinBalanceValue(users));
        assertNotEquals(falseBalance, Main.findMinBalanceValue(users));
    }

    @Test
    void shouldReturnCorrectBalanceSum() {
        Double falseSum = 12345.899;
        Double expectedSum = 241864.0;
        assertEquals(expectedSum, Main.calculateTotalCreditBalance(bankAccounts));
        assertNotEquals(falseSum, Main.calculateTotalCreditBalance(bankAccounts));
    }
}