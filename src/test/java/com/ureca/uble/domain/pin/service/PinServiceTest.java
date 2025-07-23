package com.ureca.uble.domain.pin.service;

import com.ureca.uble.domain.pin.dto.request.CreatePinReq;
import com.ureca.uble.domain.pin.dto.response.InitialDataRes;
import com.ureca.uble.domain.pin.dto.response.GetPinRes;
import com.ureca.uble.domain.pin.exception.PinErrorCode;
import com.ureca.uble.domain.pin.repository.PinRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Pin;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PinServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PinRepository pinRepository;

    @InjectMocks
    private PinService pinService;

    GeometryFactory gf = new GeometryFactory();

    @Test
    @DisplayName("자주 가는 곳 조회")
    void getInitialDataSuccess() {
        // given
        Long userId = 111L;

        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        GeometryFactory gf = new GeometryFactory();
        Point p1 = gf.createPoint(new Coordinate(127.0, 37.0));
        Point p2 = gf.createPoint(new Coordinate(128.0, 36.0));

        Pin pin1 = mock(Pin.class);
        when(pin1.getId()).thenReturn(10L);
        when(pin1.getName()).thenReturn("집");
        when(pin1.getLocation()).thenReturn(p1);

        Pin pin2 = mock(Pin.class);
        when(pin2.getId()).thenReturn(11L);
        when(pin2.getName()).thenReturn("회사");
        when(pin2.getLocation()).thenReturn(p2);
        when(pinRepository.findByUserIdOrderByIdAsc(userId)).thenReturn(List.of(pin1, pin2));

        // when
        InitialDataRes result = pinService.getInitialData(userId);

        // then
        assertThat(result.getLocations())
                .extracting("id", "name", "longitude", "latitude")
                .containsExactly(tuple(10L, "집",   127.0, 37.0), tuple(11L, "회사", 128.0, 36.0));
        verify(pinRepository).findByUserIdOrderByIdAsc(userId);
    }

    @Test
    @DisplayName("createPin: 정상 생성 시 GetPinRes 반환")
    void createPinSuccess() {
        // given
        Long userId = 222L;
        User mockUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(pinRepository.countByUserId(userId)).thenReturn(1L);

        CreatePinReq req = CreatePinReq.builder()
                .name("카페").longitude(128.0)
                .latitude(36.5).address("강남구 어딘가")
                .build();

        Point p = gf.createPoint(new Coordinate(128.0, 36.5));
        Pin savedPin = Pin.of(mockUser, "카페", p, "강남구 어딘가");
        Pin spyPin = spy(savedPin);
        doReturn(99L).when(spyPin).getId();

        when(pinRepository.save(any(Pin.class))).thenReturn(spyPin);

        // when
        GetPinRes res = pinService.createPin(userId, req);

        // then
        assertThat(res.getPinId()).isEqualTo(99L);
        assertThat(res.getName()).isEqualTo("카페");
        assertThat(res.getLongitude()).isEqualTo(128.0);
        assertThat(res.getLatitude()).isEqualTo(36.5);
        assertThat(res.getAddress()).isEqualTo("강남구 어딘가");
        verify(pinRepository).save(any(Pin.class));
    }

    @Test
    @DisplayName("createPin: 2개 초과 시 PIN_LIMIT_EXCEEDED 예외")
    void createPinLimitExceeded() {
        // given
        Long userId = 333L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(pinRepository.countByUserId(userId)).thenReturn(2L);

        CreatePinReq req = CreatePinReq.builder()
                .name("집").longitude(127.0)
                .latitude(37.0).address("어딘가")
                .build();

        // when / then
        GlobalException ex = catchThrowableOfType(
                () -> pinService.createPin(userId, req), GlobalException.class
        );
        assertThat(ex.getResultCode()).isEqualTo(PinErrorCode.PIN_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("deletePin: 정상 삭제 시 PinRepository.delete 호출")
    void deletePinSuccess() {
        // given
        Long userId = 444L;
        Long pinId = 55L;
        Pin mockPin = mock(Pin.class);
        when(pinRepository.findByIdAndUserId(pinId, userId)).thenReturn(Optional.of(mockPin));

        // when
        pinService.deletePin(userId, pinId);

        // then
        verify(pinRepository).deleteByIdAndUserId(pinId, userId);
    }

    @Test
    @DisplayName("deletePin: 존재하지 않는 핀일 때 PIN_NOT_FOUND 예외")
    void deletePinNotFound() {
        // given
        Long userId = 555L;
        Long pinId = 66L;
        when(pinRepository.findByIdAndUserId(pinId, userId)).thenReturn(Optional.empty());

        // when / then
        GlobalException ex = catchThrowableOfType(
                () -> pinService.deletePin(userId, pinId), GlobalException.class
        );
        assertThat(ex.getResultCode()).isEqualTo(PinErrorCode.PIN_NOT_FOUND);
    }
}
