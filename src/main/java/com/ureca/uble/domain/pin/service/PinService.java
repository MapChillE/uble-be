package com.ureca.uble.domain.pin.service;

import com.ureca.uble.domain.pin.dto.request.CreatePinReq;
import com.ureca.uble.domain.pin.dto.response.CreatePinRes;
import com.ureca.uble.domain.pin.dto.response.GetPinRes;
import com.ureca.uble.domain.pin.dto.response.GetPinDetailRes;
import com.ureca.uble.domain.pin.exception.PinErrorCode;
import com.ureca.uble.domain.pin.repository.PinRepository;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.Pin;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.exception.GlobalException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ureca.uble.domain.pin.exception.PinErrorCode.PIN_LIMIT_EXCEEDED;
import static com.ureca.uble.domain.users.exception.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinService {

    private final UserRepository userRepository;
    private final PinRepository pinRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final EntityManager em;
    private static final int MAX_PINS = 2;

    /**
     * 자주 가는 곳 조회
     */
    @Transactional(readOnly = true)
    public GetPinRes getInitialData(Long userId) {
        List<GetPinDetailRes> locations = pinRepository
                .findByUserIdOrderByIdAsc(userId).stream()
                .map(this::toLocationRes)
                .toList();
        return new GetPinRes(locations);
    }

    /**
     * 자주 가는 곳 추가
     */
    @Transactional
    public CreatePinRes createPin(Long userId, CreatePinReq req) {
        int count = pinRepository.countByUserId(userId);
        if (count >= MAX_PINS) {
            throw new GlobalException(PIN_LIMIT_EXCEEDED);
        }

        User userRef = em.getReference(User.class, userId);

        Point location = geometryFactory.createPoint(
                new Coordinate(req.getLongitude(), req.getLatitude())
        );

        Pin pin = Pin.of(userRef, req.getName(), location, req.getAddress());
        Pin saved = pinRepository.save(pin);

        return CreatePinRes.of(
                saved.getId(), saved.getName(),
                saved.getLocation().getX(), saved.getLocation().getY(),
                saved.getAddress()
        );
    }

    /**
     * 자주 가는 곳 삭제
     */
    @Transactional
    public void deletePin(Long userId, Long pinId) {
        Pin pin = findPin(pinId, userId);
        pinRepository.deleteByIdAndUserId(pinId, userId);
    }

    private Pin findPin(Long pinId, Long userId) {
        return pinRepository.findByIdAndUserId(pinId, userId).orElseThrow(() -> new GlobalException(PinErrorCode.PIN_NOT_FOUND));
    }

    private GetPinDetailRes toLocationRes(Pin pin) {
        return GetPinDetailRes.of(
                pin.getId(), pin.getName(),
                pin.getLocation().getX(), pin.getLocation().getY(),
                pin.getAddress()
        );
    }

}
