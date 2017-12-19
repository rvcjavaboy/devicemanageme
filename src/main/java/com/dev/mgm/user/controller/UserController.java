package com.dev.mgm.user.controller;

/**
 * @author ranjit
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.dev.mgm.bean.Device;
import com.dev.mgm.bean.DeviceStatus;
import com.dev.mgm.bean.MessageBean;
import com.dev.mgm.bean.User;
import com.dev.mgm.dao.UserDAO;

/**
 * @author ranjit
 *
 */
public class UserController {
	private static Logger log=LoggerFactory.getLogger(UserController.class);
	
	/**
	 * @param user  object
	 * @return message bean  object which contain status
	 */
	public static  MessageBean registerUser(User u) {
		/*
		 * TODO register the user 
		 * 
		 * 
		 */
		MessageBean m=new MessageBean();
		UserDAO udao=new UserDAO();
		
		if(udao.checkDeviceAvablity(u.getDevice_id())) {
		
			u=udao.registerUser(u);
			if(u!=null) {
				
				//user register successfully
			m.setDesc("User Registered successfully");
			m.setMessage("Successfully");
			Device d=checkDeviceStatusReg(u.getDevice_id());  //device status changed
			//log.info("device status changed");
			log.info("User Register Successfully",u.getUser_id(),u.getName(),u.getDevice_id(),d.getStatus());	//maintaing db logs
			}else {
				//device already assign to other user

				m.setDesc("device already assign to other user");
				m.setMessage("failed");
				log.info("User Register failed","","","","");
			}
			
			
		}else {
			//device not register
			m.setDesc("device not found");
			m.setMessage("error");
		}
		
		return m;
	}
	
	/**
	 * @param device id
	 * @return message bean  object which contain status
	 */
	
	public static Device checkDeviceStatus(long device_id) {
		/*
		 * TODO know the device status
		 * 
		 * 
		 */
		Device d=new UserDAO().knowDeviceStatus(device_id);
		log.info("Device Status ","","",d.getDevice_id(),d.getStatus());	//maintaing db logs
		return d;
		
	}
	
	/**
	 * @param device id
	 * @return message bean  object which contain statust
	 */
	
	
	public static MessageBean deRegisterUser(long device_id) {
		/*
		 * TODO deregister the device 
		 * 
		 * 
		 */
		UserDAO user=new UserDAO();
		int status=user.deRegisterUser(device_id);     //call deregister dao
		MessageBean m=new MessageBean();
		if(status>-1) {
			if(status==777) {
				m.setMessage("Un-register");
				m.setDesc("device already unregister");	
				log.info("Device already unregister","","",device_id,DeviceStatus.UNREGISTER);                   //maintaing db logs
				
			}else {
				user.deRegisterUser(device_id);
				log.info("Device Deregister","","",device_id,DeviceStatus.UNREGISTER);                   //maintaing db logs
				m.setMessage("Successfully");
				m.setDesc("Device De registered completed.");
			}
			return m;
		}
			//deregister failed
			m.setMessage("Failed");
			m.setDesc("Device not found");
		return m;
		
	}
	public static Device checkDeviceStatusReg(long device_id) {
		/*
		 * TODO know the device status
		 * 
		 * 
		 */
		Device d=new UserDAO().knowDeviceStatus(device_id);
		
		return d;
		
	}
	
	
	
	
	
}
