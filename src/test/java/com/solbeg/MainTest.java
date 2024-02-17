package com.solbeg;

import com.solbeg.model.User;
import com.solbeg.model.UserBankAccount;
import com.solbeg.service.UserBankAccountProvider;
import com.solbeg.service.UserProvider;
import com.solbeg.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainTest {

    @Mock
    private UserProvider userProvider;

    @Mock
    private User user;

    @Mock
    private UserService userService;

    @Mock
    UserBankAccountProvider userBankAccountProvider;

    @Mock
    UserBankAccount userBankAccount;

    @Nested
    class OptionalOfString {


        @Test
        void shouldReturnOptionalOfString() {
            // given
            String expected = "optional";

            // when
            Optional<String> result = Main.optionalOfString(expected);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result).hasValue(expected);
        }

        @Test
        void shouldReturnOptionalOfStringWithNull() {
            // given, when
            Optional<String> actual = Main.optionalOfString(null);

            // then
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class Deposit {

        @Test
        void shouldCheckExpectedValue() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.of(user));
            when(user.getBalance()).thenReturn(BigDecimal.valueOf(100));

            // when
            Main.deposit(userProvider, BigDecimal.valueOf(50));

            // then
            verify(user).setBalance(BigDecimal.valueOf(150));
            assertThatCode(() -> Main.deposit(userProvider, BigDecimal.valueOf(50))).doesNotThrowAnyException();
        }

        @Test
        void shouldReturnThrowExceptionWhenUserDoesNotExist() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> Main.deposit(userProvider, BigDecimal.valueOf(50)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("User not found!");
        }
    }

    @Nested
    class OptionalOfUser {

        @Test
        void shouldReturnExpectedUser() {
            // given
            User user = new User();

            // when
            Optional<User> actual = Main.optionalOfUser(user);

            // then
            assertThat(actual).isNotEmpty();
            assertThat(actual).hasValue(user);
        }

        @Test
        void shouldReturnOptionalOfStringWithNull() {
            // given, when
            Optional<User> actual = Main.optionalOfUser(null);

            // then
            assertThat(actual).isEmpty();
        }

    }

    @Nested
    class GetUser {

        @Test
        void shouldReturnExpectedUser() {
            // given
            User expected = User.builder()
                    .id(1L)
                    .name("Jon")
                    .email("jon123@gmail.com")
                    .balance(BigDecimal.TEN)
                    .build();
            when(userProvider.getUser()).thenReturn(Optional.of(expected));

            // when
            User actual = Main.getUser(userProvider, null);

            // then
            assertThat(actual).isSameAs(expected);
        }

        @Test
        void shouldReturnDefaultUser() {
            // given
            User defaultUser = User.builder()
                    .id(1L)
                    .name("Jon")
                    .email("jon123@gmail.com")
                    .balance(BigDecimal.TEN)
                    .build();
            when(userProvider.getUser()).thenReturn(Optional.empty());

            // when
            User actual = Main.getUser(userProvider, defaultUser);

            // then
            assertThat(actual).isSameAs(defaultUser);
        }
    }

    @Nested
    class ProcessUser {

        @Test
        void shouldReturnExpectedUser() {
            // given
            User expected = User.builder()
                    .id(1L)
                    .name("Jon")
                    .email("jon123@gmail.com")
                    .balance(BigDecimal.TEN)
                    .build();

            when(userProvider.getUser()).thenReturn(Optional.of(expected));

            // when
            Main.processUser(userProvider, userService);

            // then
            verify(userService).processUser(expected);
            verify(userService, never()).processWithNoUser();
            assertThatCode(() -> Main.processUser(userProvider, userService)).doesNotThrowAnyException();
        }

        @Test
        void shouldReturnThrowExceptionWhenDoesNotUser() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.empty());

            // when
            Main.processUser(userProvider, userService);

            // then
            verify(userService).processWithNoUser();
            verify(userService, never()).processUser(any());
        }
    }

    @Nested
    class GetGenerateUser {

        @Test
        void shouldReturnExpectedUser() {
            // given
            User expected = User.builder()
                    .id(1L)
                    .name("Jon")
                    .email("jon123@gmail.com")
                    .balance(BigDecimal.TEN)
                    .build();
            when(userProvider.getUser()).thenReturn(Optional.of(expected));

            // when
            User actual = Main.getOrGenerateUser(userProvider);

            // then
            assertThat(actual).isSameAs(expected);
        }

        @Test
        void shouldReturnGenerateUserWhenDoesNotUser() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.empty());

            // when, then
            assertThat(Main.getOrGenerateUser(userProvider)).isNotNull();
        }
    }

    @Nested
    class RetrieveBalance {

        @Test
        void shouldReturnExpectedValue() {
            // given
            BigDecimal expected = BigDecimal.valueOf(400L);
            when(userProvider.getUser()).thenReturn(Optional.of(user));
            when(user.getBalance()).thenReturn(expected);

            // when
            Optional<BigDecimal> actual = Main.retrieveBalance(userProvider);

            // then
            assertThat(actual).contains(expected);
        }

        @Test
        void shouldReturnOptionalEmptyWhenDoesNotUser() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.empty());

            // when
            Optional<BigDecimal> actual = Main.retrieveBalance(userProvider);

            // then
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class GetUserFromUserProvider {

        @Test
        void shouldReturnExpectedUser() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.of(user));

            // when
            User actual = Main.getUser(userProvider);

            // then
            assertThat(actual).isEqualTo(user);
        }

        @Test
        void shouldReturnThrowExceptionWhenDoesNotUser() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> Main.getUser(userProvider))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("No user provider!");
        }
    }

    @Nested
    class RetrieveCreditBalance {

        @Test
        void shouldReturnExpectedCreditBalance() {
            // given
            BigDecimal creditBalance = BigDecimal.TEN;
            when(userBankAccountProvider.getUserBankAccount()).thenReturn(Optional.of(userBankAccount));
            when(userBankAccount.getCreditBalance()).thenReturn(Optional.of(creditBalance));

            // when
            Optional<BigDecimal> actual = Main.retrieveCreditBalance(userBankAccountProvider);

            // then
            assertThat(actual).isEqualTo(Optional.of(creditBalance));
        }

        @Test
        void shouldReturnOptionalEmptyWhenDoesNotUser() {
            // given
            when(userBankAccountProvider.getUserBankAccount()).thenReturn(Optional.empty());

            // when
            Optional<BigDecimal> actual = Main.retrieveCreditBalance(userBankAccountProvider);

            // then
            assertThat(actual).isEmpty();
        }

        @Test
        void shouldReturnOptionalEmptyWhenUserNotHasCreditBalance() {
            // given
            when(userBankAccountProvider.getUserBankAccount()).thenReturn(Optional.of(userBankAccount));
            when(userBankAccount.getCreditBalance()).thenReturn(Optional.empty());

            // when
            Optional<BigDecimal> actual = Main.retrieveCreditBalance(userBankAccountProvider);

            // then
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class RetrieveUserGmail {

        @Test
        void shouldReturnExpectedUser() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.of(user));
            when(user.getEmail()).thenReturn("alice@gmail.com");

            // when
            Optional<User> actual = Main.retrieveUserGmail(userProvider);

            // then
            assertThat(actual).isEqualTo(Optional.of(user));
        }

        @Test
        void shouldReturnOptionalEmptyWhenDoesNotUser() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.empty());

            // when
            Optional<User> actual = Main.retrieveUserGmail(userProvider);

            // then
            assertThat(actual).isEmpty();
        }

        @Test
        void shouldReturnOptionalEmptyWhenUserNotHasEmail() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.of(user));
            when(user.getEmail()).thenReturn("bob@yahoo.com");

            // when
            Optional<User> actual = Main.retrieveUserGmail(userProvider);

            // then
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class GetUserWithFallback {

        @Test
        void shouldReturnExpectedUser() {
            // given
            User userFirst = User.builder()
                    .id(2L)
                    .name("Mick")
                    .email("mick245@gmail.com")
                    .balance(BigDecimal.TEN)
                    .build();
            when(userProvider.getUser()).thenReturn(Optional.of(userFirst));

            // when
            User actual = Main.getUserWithFallback(userProvider, userProvider);

            // then
            assertThat(actual).isEqualTo(userFirst);
        }

        @Test
        void shouldReturnFallbackUser() {
            // given
            User fallbackUser = User.builder()
                    .id(1L)
                    .name("Jon")
                    .email("jon123@gmail.com")
                    .balance(BigDecimal.TEN)
                    .build();
            when(userProvider.getUser()).thenReturn(Optional.of(fallbackUser));

            // when
            User actual = Main.getUserWithFallback(userProvider, userProvider);

            // then
            assertThat(actual).isEqualTo(fallbackUser);
        }

        @Test
        void shouldReturnThrowExceptionWhenTwoProvidersNotReturnUser() {
            // given
            when(userProvider.getUser()).thenReturn(Optional.empty());

            // when, then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> Main.getUserWithFallback(userProvider, userProvider));
        }
    }

    @Nested
    class GetUserWithMaxBalance {

        @Test
        void shouldReturnExpectedUser() {
            // given
            List<User> users = Arrays.asList(
                    new User(1L, "Alice", "alice@gmail.com", BigDecimal.valueOf(200)),
                    new User(2L, "Bob", "bob@gmail.com", BigDecimal.valueOf(300)),
                    new User(3L, "Charlie", "charlie@gmail.com", BigDecimal.valueOf(400)),
                    new User(4L, "David", "david@gmail.com", BigDecimal.valueOf(100))
            );

            // when
            User actual = Main.getUserWithMaxBalance(users);

            // then
            assertThat(actual.getId()).isEqualTo(3L);
        }

        @Test
        void shouldReturnThrowExceptionWhenListOfUsersEmpty() {
            // when, then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> Main.getUserWithMaxBalance(List.of()));
        }
    }

    @Nested
    class FindMinBalanceValue {

        @Test
        void shouldExpectedValue() {
            // given
            List<User> users = Arrays.asList(
                    new User(1L, "Alice", "alice@gmail.com", BigDecimal.valueOf(200)),
                    new User(2L, "Bob", "bob@gmail.com", BigDecimal.valueOf(300)),
                    new User(3L, "Charlie", "charlie@gmail.com", BigDecimal.valueOf(400)),
                    new User(4L, "David", "david@gmail.com", BigDecimal.valueOf(100))
            );

            // when
            OptionalDouble actual = Main.findMinBalanceValue(users);
            assertThat(actual).hasValue(100);
        }

        @Test
        void shouldReturnOptionalEmptyWhenListOfUserEmpty() {
            // when
            OptionalDouble actual = Main.findMinBalanceValue(List.of());

            // then
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class CalculateTotalCreditBalance {

        @Test
        void shouldReturnExpectedValue() {
            // given
            List<UserBankAccount> bankAccounts = Arrays.asList(
                    new UserBankAccount(new User(1L, "Alice", "alice@gmail.com", BigDecimal.valueOf(200)), BigDecimal.valueOf(200)),
                    new UserBankAccount(new User(2L, "Bob", "bob@gmail.com", BigDecimal.valueOf(300)), BigDecimal.valueOf(300)),
                    new UserBankAccount(new User(3L, "Charlie", "charlie@gmail.com", BigDecimal.valueOf(400)), BigDecimal.valueOf(400)),
                    new UserBankAccount(new User(4L, "David", "david@gmail.com", BigDecimal.valueOf(100)), BigDecimal.valueOf(100))
            );

            // when
            double actual = Main.calculateTotalCreditBalance(bankAccounts);

            // then
            assertThat(actual).isEqualTo(1000);
        }
    }
}