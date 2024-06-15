package com.artsolo.bookswap.user;

import com.artsolo.bookswap.exceptions.NoDataFoundException;
import com.artsolo.bookswap.enums.Role;
import com.artsolo.bookswap.user.dto.LocationChangeRequest;
import com.artsolo.bookswap.user.dto.PasswordChangeRequest;
import com.artsolo.bookswap.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void userFoundById() {
        Long userId = 6L;
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        User result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    void thrownExceptionWhileGettingUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoDataFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getUserResponseIsValid() {
        User user = User.builder()
                .id(3L)
                .nickname("user212")
                .email("use22@gmail.com")
                .password("232f23f23j2fjcmb433m2")
                .country("USA")
                .city("Los Angeles")
                .points(10)
                .role(Role.READER)
                .registrationDate(LocalDate.now())
                .photo("some file".getBytes())
                .activity(true)
                .build();

        UserResponse response = userService.getUserResponse(user);

        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getNickname()).isEqualTo(user.getNickname());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getCountry()).isEqualTo(user.getCountry());
        assertThat(response.getCity()).isEqualTo(user.getCity());
        assertThat(response.getPoints()).isEqualTo(10);
        assertThat(response.getRole()).isEqualTo(user.getRole());
        assertThat(response.getRegistrationDate()).isEqualTo(user.getRegistrationDate());
        assertThat(response.getActivity()).isTrue();
        assertThat(response.getPhoto()).isEqualTo(user.getPhoto());
    }

    @Test
    void userActivityChanged() {
        User user = User.builder().activity(Boolean.FALSE).build();
        User svdUser = User.builder().activity(Boolean.TRUE).build();

        when(userRepository.save(user)).thenReturn(svdUser);
        boolean userActivity = userService.changeUserActivity(user);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(userActivity).isTrue();
        assertThat(capturedUser.getActivity()).isTrue();
    }

    @Test
    void userRoleChanged() {
        User user = User.builder().role(Role.READER).build();
        User svdUser = User.builder().role(Role.MODERATOR).build();

        when(userRepository.save(user)).thenReturn(svdUser);
        boolean isRoleChanged = userService.setUserRole(user, Role.MODERATOR);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(isRoleChanged).isTrue();
        assertThat(capturedUser.getRole()).isEqualTo(svdUser.getRole());
    }

    @Test
    void userPhotoChanged() {
        User user = User.builder().photo("some file".getBytes()).build();
        MockMultipartFile file = new MockMultipartFile("photo", "some new file".getBytes());
        User svdUser = User.builder().photo("some new file".getBytes()).build();

        when(userRepository.save(user)).thenReturn(svdUser);
        userService.changeUserPhoto(file, user);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getPhoto()).isEqualTo(svdUser.getPhoto());
    }

    @Test
    void userLocationChanged() {
        User user = User.builder().country("United Kingdom").city("London").build();
        User svdUser = User.builder().country("USA").city("Los Angeles").build();
        LocationChangeRequest request = new LocationChangeRequest("USA","Los Angeles");

        when(userRepository.save(user)).thenReturn(svdUser);
        userService.changeUserLocation(request, user);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getCountry()).isEqualTo(request.getCountry());
        assertThat(capturedUser.getCity()).isEqualTo(request.getCity());
    }

    @Test
    void userPointsIncreased() {
        User user = User.builder().points(10).build();
        User svdUser = User.builder().points(25).build();

        when(userRepository.save(user)).thenReturn(svdUser);
        userService.increaseUserPoints(15, user);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getPoints()).isEqualTo(25);
    }

    @Test
    void illegalArgumentExceptionThrownWhileIncreasingUserPoints() {
        User user = User.builder().points(10).build();
        assertThrows(IllegalArgumentException.class, () -> userService.increaseUserPoints(-15, user));
    }

    @Test
    void userPointsDecreased() {
        User user = User.builder().points(10).build();
        User svdUser = User.builder().points(-5).build();

        when(userRepository.save(user)).thenReturn(svdUser);
        userService.decreaseUserPoints(15, user);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getPoints()).isEqualTo(-5);
    }

    @Test
    void illegalArgumentExceptionThrownWhileDecreasingUserPoints() {
        User user = User.builder().points(10).build();
        assertThrows(IllegalArgumentException.class, () -> userService.decreaseUserPoints(-15, user));
    }

    @Test
    void passwordChanged() {
        User user = User.builder().password("232f@f23j2fjCMb433m2").build();
        PasswordChangeRequest request = new PasswordChangeRequest(
                "f@f23jk3k5n223433ASD2",
                "232f@f23j2fjCMb433m2"
        );

        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(false);

        String result = userService.changeUserPassword(request, user);
        assertThat(result).isEqualTo("Password changed successfully");
    }
}