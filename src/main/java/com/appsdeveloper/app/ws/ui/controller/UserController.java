package com.appsdeveloper.app.ws.ui.controller;

import com.appsdeveloper.app.ws.exceptions.UserServiceException;
import com.appsdeveloper.app.ws.service.AddressesService;
import com.appsdeveloper.app.ws.service.HtmlMail;
import com.appsdeveloper.app.ws.service.TaskService;
import com.appsdeveloper.app.ws.service.UserService;
import com.appsdeveloper.app.ws.shared.dto.AddressDto;
import com.appsdeveloper.app.ws.shared.dto.TaskDto;
import com.appsdeveloper.app.ws.shared.dto.UserDto;
import com.appsdeveloper.app.ws.ui.model.request.TaskRequestModel;
import com.appsdeveloper.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloper.app.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("users") //http://localhost:8080/users
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressesService addressesService;

    @Autowired
    HtmlMail htmlMail;

    @Autowired
    TaskService taskService;

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUsers(@PathVariable String id) {

        UserDto userDto = userService.getUserByUserId(id);


        ModelMapper modelMapper = new ModelMapper();
        UserRest returnValue = modelMapper.map(userDto, UserRest.class);

        return returnValue;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        UserRest returnValue = new UserRest();
        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        //UserDto userDto = new UserDto();
        //BeanUtils.copyProperties(userDetails,userDto);
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createUser = userService.createUser(userDto);
        //BeanUtils.copyProperties(createUser,returnValue);
        returnValue = modelMapper.map(createUser, UserRest.class);


        htmlMail.sendMail("anhphu19051996@gmail.com",createUser.getEmail(),"Complete Registration!", createUser.getEmailVerificationToken());

        return returnValue;
    }

    @PutMapping(path = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updateUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updateUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUser(id);
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "2") int limit) {
        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);
        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }
        return returnValue;
    }

    @GetMapping(path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public CollectionModel<AddressRest> getUsersAddresses(@PathVariable String id) {
        List<AddressRest> returnValue = new ArrayList<>();

        List<AddressDto> addressDtos = addressesService.getAddresses(id);

        if (addressDtos != null && !addressDtos.isEmpty()) {
            Type listType = new TypeToken<List<AddressRest>>() {
            }.getType();
            returnValue = new ModelMapper().map(addressDtos, listType);
            for (AddressRest addressRest : returnValue) {
                Link selfLink = WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUsersAddress(id, addressRest.getAddressId()))
                        .withSelfRel();
                addressRest.add(selfLink);
            }
        }
        // oder
//        ModelMapper modelMapper = new ModelMapper();
//        for (AddressDto addressDto : addressDtos){
//            returnValue.add(modelMapper.map(addressDto,AddressRest.class));
//        }

        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(id)
                .withRel("user");
        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUsersAddresses(id))
                .withSelfRel();

        return CollectionModel.of(returnValue, userLink, selfLink);
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EntityModel<AddressRest> getUsersAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressDto addressDtos = addressesService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressRest returnValue = modelMapper.map(addressDtos, AddressRest.class);
        // oder
//        ModelMapper modelMapper = new ModelMapper();
//        for (AddressDto addressDto : addressDtos){
//            returnValue.add(modelMapper.map(addressDto,AddressRest.class));
//        }

        //http://localhost:8080/users/<userId>
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(userId)
                .withRel("user");
        Link addressLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUsersAddresses(userId))
//                .slash(userId)
//                .slash("adresses")
                .withRel("adresses");
        Link seftLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUsersAddress(userId, addressId))
                .withSelfRel();
//        returnValue.add(userLink);
//        returnValue.add(addressLink);
//        returnValue.add(seftLink);

        //EntityModel.of(returnValue, Arrays.asList(userLink,addressLink,seftLink));

        return EntityModel.of(returnValue, Arrays.asList(userLink, addressLink, seftLink));
    }

    @GetMapping(path = "/email-verification",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);
        if (isVerified){
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }
    @PutMapping(path = "/{userId}/addTask",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TaskRest createTask(@PathVariable String userId, @RequestBody TaskRequestModel taskRequestModel) throws Exception {
        ModelMapper modelMapper = new ModelMapper();
        TaskDto taskDto = modelMapper.map(taskRequestModel,TaskDto.class);
        TaskDto addedTask = taskService.addTask(userId,taskDto.getTaskContent());
        TaskRest returnValue = modelMapper.map(addedTask,TaskRest.class);
        return returnValue;
    }

    @GetMapping(path = "/{userId}/task",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<TaskRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "2") int limit, @PathVariable String userId) {
        List<TaskRest> returnValue = new ArrayList<>();
        List<TaskDto> tasks = taskService.getTasks(userId, page, limit);
        for (TaskDto taskDto : tasks) {
            TaskRest taskRest = new TaskRest();
            BeanUtils.copyProperties(taskDto, taskRest);
            returnValue.add(taskRest);
        }
        return returnValue;
    }
}
