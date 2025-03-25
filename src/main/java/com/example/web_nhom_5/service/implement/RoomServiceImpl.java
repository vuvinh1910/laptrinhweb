package com.example.web_nhom_5.service.implement;

import com.example.web_nhom_5.conventer.RoomMapper;
import com.example.web_nhom_5.dto.request.RoomCreateRequest;
import com.example.web_nhom_5.dto.request.RoomUpdateRequest;
import com.example.web_nhom_5.dto.response.RoomResponse;
import com.example.web_nhom_5.entity.LocationEntity;
import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.enums.SearchOperation;
import com.example.web_nhom_5.exception.ErrorCode;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.LocationRepository;
import com.example.web_nhom_5.repository.RoomRepository;
import com.example.web_nhom_5.repository.ServiceRepository;
import com.example.web_nhom_5.search.SearchCriteria;
import com.google.common.base.Joiner;
import com.example.web_nhom_5.search.EntitySpecificationBuilder.RoomSpecificationBuilder;

import com.example.web_nhom_5.service.LocationService;
import com.example.web_nhom_5.service.RoomService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RoomMapper roomMapper;
    private final EntityManager em;

    @Override
    public RoomResponse addRoom(RoomCreateRequest room) {

        RoomEntity roomEntity = roomMapper.roomCreateDtoToEntity(room);

        LocationEntity locationEntity = locationRepository.findById(room.getLocationCode())
                .orElseThrow(() -> new WebException(ErrorCode.LOCATION_NOT_FOUND));
        roomEntity.setLocation(locationEntity);

        locationEntity.getRooms().add(roomEntity); // Mặc dù bạn gọi getRooms() để lấy danh sách các phòng, nhưng khi bạn thêm một roomEntity vào danh sách này, danh sách rooms trong locationEntity đã thay đổi.
        // Vì Hibernate quản lý thực thể locationEntity, mọi thay đổi trong danh sách này sẽ được theo dõi và đồng bộ hóa.

        return roomMapper.roomEntityToRoomResponse(roomRepository.save(roomEntity));
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream().map(roomMapper::roomEntityToRoomResponse).toList();
    }

    @Override
    public RoomResponse updateRoom(RoomUpdateRequest room, Long roomId) {
        // Lấy RoomEntity hiện tại từ roomId
        RoomEntity roomEntity = getRoomById(roomId);

        // Lấy LocationEntity cũ và xóa phòng khỏi danh sách rooms trong location
        LocationEntity oldLocationEntity = locationRepository.findById(roomEntity.getLocation().getLocationCode())
                .orElseThrow(() -> new WebException(ErrorCode.LOCATION_NOT_FOUND));
        oldLocationEntity.getRooms().remove(roomEntity);

        // Cập nhật thông tin cho RoomEntity
        roomMapper.updateRoom(roomEntity, room);

        // Lấy LocationEntity mới
        LocationEntity newLocationEntity = locationRepository.findById(room.getLocationCode())
                .orElseThrow(() -> new WebException(ErrorCode.LOCATION_NOT_FOUND));
        roomEntity.setLocation(newLocationEntity);

        // Thêm phòng vào danh sách rooms của LocationEntity mới
        newLocationEntity.getRooms().add(roomEntity);

        // Lưu lại RoomEntity
        return roomMapper.roomEntityToRoomResponse(roomRepository.save(roomEntity));
    }


    @Override
    public RoomEntity getRoomById(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new WebException(ErrorCode.ROOM_NOT_FOUND));
    }

    @Override
    public void deleteRoom(Long roomId) {
        RoomEntity roomEntity = getRoomById(roomId);
        LocationEntity locationEntity = locationRepository.findById(roomEntity.getLocation().getLocationCode())
                .orElseThrow(() -> new WebException(ErrorCode.LOCATION_NOT_FOUND));
        locationEntity.getRooms().remove(roomEntity);
        roomRepository.deleteById(roomId);
    }

    @Override
    public List<RoomResponse> getAllRoomsByLocationCode(String locationCode) {
        List<RoomEntity> roomEntities = roomRepository.findAllByLocation_LocationCode(locationCode);
        return roomEntities.stream().map(roomMapper::roomEntityToRoomResponse).toList();
    }

    @Override
    public List<RoomResponse> getLimitedRooms() {
        List<RoomEntity> rooms = roomRepository.findAll(); // Lấy tất cả các phòng
        // Giới hạn chỉ lấy 4 phòng đầu tiên
        return rooms.stream().limit(4).map(roomMapper::roomEntityToRoomResponse).toList();
    }

    //search room
    @Override
    public List<RoomResponse> listAll(String keyword)
    {
        List<RoomEntity> roomEntities;
        if (keyword!=null)
        {
            roomEntities=roomRepository.listAll(keyword);

        } else
        {
            roomEntities=roomRepository.findAll();
        }
        return roomEntities.stream().map(roomMapper::roomEntityToRoomResponse).toList();
    }
    @Override
    public List<RoomResponse> findAllBySpecification(String search) {
        RoomSpecificationBuilder builder = new RoomSpecificationBuilder();
        String operationSetExper = Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern.compile("((\\w+?)([:<>!~])(\\w[\\w\\s]*))(\\p{Punct})?",Pattern.UNICODE_CHARACTER_CLASS);

        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {

            builder.with(
                    matcher.group(2),
                    matcher.group(3),
                    matcher.group(4),
                    matcher.group(1),
                    matcher.group(5));
        }
        Specification<RoomEntity> spec = builder.build();
        return roomRepository.findAll(spec).stream().map(roomMapper::roomEntityToRoomResponse).toList();
    }
    @Override
    public List<RoomResponse> filterBySpecificationAndAddress(String room,String address) {

        CriteriaBuilder builder=em.getCriteriaBuilder();
        CriteriaQuery<RoomEntity> query=builder.createQuery(RoomEntity.class);
        Root<RoomEntity> roomRoot=query.from(RoomEntity.class);
        Join<RoomEntity,LocationEntity> locationRoot=roomRoot.join("location");
        List<Predicate> roomPre=new ArrayList<>();
        List<Predicate> locationPre=new ArrayList<>();
        Pattern pattern = Pattern.compile("((\\w+?)([:<>!~])(\\w[\\w\\s]*))(\\p{Punct})?",Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher=pattern.matcher(room+',');
        while(matcher.find()) {
         SearchCriteria criteria=new SearchCriteria(matcher.group(2),matcher.group(3),matcher.group(4),matcher.group(1),matcher.group(5));
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            System.out.println(matcher.group(3));
            System.out.println(matcher.group(4));
            System.out.println(matcher.group(5));
         Predicate pre=toPredicate(roomRoot,builder,criteria);
         roomPre.add(pre);
        }
        Matcher matcher1=pattern.matcher(address+',');
        while (matcher1.find())
        {
            SearchCriteria criteria =new SearchCriteria(matcher1.group(2),matcher1.group(3),matcher1.group(4),matcher1.group(1),matcher1.group(5));
            Predicate pre=toPredicate(locationRoot,builder,criteria);
            locationPre.add(pre);
        }
        Predicate roomPreToArr=builder.or(roomPre.toArray(new Predicate[0]));
        Predicate locationPreToArr=builder.or(locationPre.toArray(new Predicate[0]));
        Predicate finalPre=builder.and(roomPreToArr,locationPreToArr);
        query.where(finalPre);
        return em.createQuery(query).getResultList().stream().map(roomMapper::roomEntityToRoomResponse).toList();
    }
    public Predicate toPredicate(@NotNull Root<RoomEntity> root,@NotNull CriteriaBuilder criteriaBuilder,SearchCriteria searchCriteria){
        return switch (searchCriteria.getOperation()) {
            case EQUALITY -> criteriaBuilder.equal(root.get(searchCriteria.getKeyword()), searchCriteria.getValue());
            case NEGATION -> criteriaBuilder.notEqual(root.get(searchCriteria.getKeyword()), searchCriteria.getValue());
            case GREATER_THAN ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString());
            case LESS_THAN ->
                    criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString());
            case LIKE ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString());
            case STARTS_WITH ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString() + "%");
            case ENDS_WITH ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), "%" + searchCriteria.getValue().toString());
            case CONTAINS ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), "%" + searchCriteria.getValue() + "%");
            default -> null;
        };
    }
    public Predicate toPredicate(@NotNull Join<RoomEntity,LocationEntity> root,@NotNull CriteriaBuilder criteriaBuilder,SearchCriteria searchCriteria){
        return switch (searchCriteria.getOperation()) {
            case EQUALITY -> criteriaBuilder.equal(root.get(searchCriteria.getKeyword()), searchCriteria.getValue());
            case NEGATION -> criteriaBuilder.notEqual(root.get(searchCriteria.getKeyword()), searchCriteria.getValue());
            case GREATER_THAN ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString());
            case LESS_THAN ->
                    criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString());
            case LIKE ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString());
            case STARTS_WITH ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), searchCriteria.getValue().toString() + "%");
            case ENDS_WITH ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), "%" + searchCriteria.getValue().toString());
            case CONTAINS ->
                    criteriaBuilder.like(root.get(searchCriteria.getKeyword()), "%" + searchCriteria.getValue() + "%");
            default -> null;
        };
    }
    }