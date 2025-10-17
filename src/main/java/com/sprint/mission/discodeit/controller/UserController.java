package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
          encoding = @Encoding(name = "userCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
  ))
  @RequestMapping(
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
      method = RequestMethod.POST
  )
  public ResponseEntity<UserDto> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdUser);
  }

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            encoding = @Encoding(name = "userUpdateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
    ))
  @RequestMapping(
      path = "/{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
      method = RequestMethod.PATCH
  )
  public ResponseEntity<UserDto> update(
      @PathVariable("userId") UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUser);
  }

  @RequestMapping(path = "/{userId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  @RequestMapping(path = "/{userId}/userStatus", method = RequestMethod.PATCH)
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(@PathVariable("userId") UUID userId,
                                                                @RequestBody UserStatusUpdateRequest request) {
    UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUserStatus);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profileFile.getOriginalFilename(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
