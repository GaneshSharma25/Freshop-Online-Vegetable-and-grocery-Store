package com.freshop.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freshop.dto.SignUpDato;
import com.freshop.dto.UserRequestDto;
import com.freshop.dto.UserResponseDto;
import com.freshop.entity.User;
import com.freshop.exceptions.CustomException;
import com.freshop.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	public UserRepository userRepo;

	@Autowired 
	public ModelMapper mapper;
	
	public User getAllUsers(String email, String password) {
		
	    User user = userRepo.findByEmail(email).orElseThrow(()->new CustomException("User Not Found"));
	    String hashedPasswordFromDB = user.getPassword();
	    if(BCrypt.checkpw(password, hashedPasswordFromDB)) {
	    	return user;
	    }else {
	    	
	    	throw new CustomException("User Not Found");
	    }
	}

	@Override
	public boolean addUser(SignUpDato user) {
		boolean status=false;
		 String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
		User newUser = new User(user.getUserName(),user.getRole(),user.getEmail(),hashedPassword,user.getAddress(),user.getStatus(),user.getMobileNumber());
		User userdata = userRepo.save(newUser);
		if(userdata!=null) {
			status=true;
		}
		return status;
	}

	@Override
	public List<UserResponseDto> findByRole() {
		List<UserResponseDto> dto =new ArrayList<>();
		List<User> users= userRepo.findByRole("admin");
		users.forEach((v)->dto.add(mapper.map(v, UserResponseDto.class)));
		return dto;
	}

	@Override
	public boolean updateUser(UserRequestDto dto) {
		boolean status = false;
		String hashedPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(12));
		User user = new User(dto.getUserId(),dto.getUserName(), dto.getRole(), dto.getEmail(),hashedPassword,dto.getAddress(),dto.getStatus(),dto.getMobileNumber());
		System.out.println(user.getStatus());
		User newUser = userRepo.save(user);
		if(newUser!=null) {status = true;}
		System.out.println(status);
		return status;
	}
	
	

}


