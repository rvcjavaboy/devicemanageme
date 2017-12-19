package com.dev.mgm.dao;

import java.util.List;
import java.util.ListIterator;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dev.mgm.bean.Device;
import com.dev.mgm.bean.DeviceStatus;
import com.dev.mgm.bean.User;
import com.dev.mgm.db.MySessionFactory;
import com.dev.mgm.util.TimeValidation;

/**
 * @author ranjit
 *
 */
/**
 * @author ranjit
 *
 */

public class UserDAO {
	
	
	private static Logger log=LoggerFactory.getLogger(UserDAO.class);
	private long key;
	/**
	 * @param User object
	 * @return user object
	 */
	public User registerUser(User u) {
		
		//TODO user register to the db
		
		try {
			
			SessionFactory sf=MySessionFactory.getSessionFactory();
			Session session=sf.openSession();
			Transaction tx=session.beginTransaction();
			
			if(checkDeviceAssign(u.getDevice_id())) {
				return null;   //device  is assign
			}
			
			
			
			key=(long) session.save(u);          //save the object
		
			tx.commit();
			u=session.get(User.class, key);      //get The user details
			Device d=session.get(Device.class, u.getDevice_id());
			tx=session.beginTransaction();
			d.setStatus(DeviceStatus.REGISTER);  //change status of device
			session.save(d);
			tx.commit();
			
			
			
			session.close();
			log.info("user register complet successfully");
			
			return u;
			
			
		}catch(HibernateException hiber) {
			
			log.error("user register dao hibernate ",hiber);
		}
		return null;
		
		
	}
	
	/**
	 * @param Device id
	 * @return device availability of devices
	 */
	public boolean checkDeviceAvablity(long device_id) {
		/*
		 *TODO check the device is available or not based on device id 
		 *     
		 *
		 */
		try {
			log.info("check device");
			SessionFactory sf=MySessionFactory.getSessionFactory();
			Session session=sf.openSession();
			Device d=session.get(Device.class, device_id);
			
			
			if(d!=null) {  							
					//device found
					if(d.getStatus()==DeviceStatus.NEW||d.getStatus()==DeviceStatus.UNREGISTER) {
						//device status are new or unregister
						return true;
					}
					//device already used by other user
					return false;
				}else { 
				//device not found
				return false;
				}
			
		}catch(HibernateException e) {
			
		log.error("check device avalible exception ",e);
		}
		
		
		//device not found
		
		return false;
		
		
	}
	
	/**
	 * @param Device id
	 * @return device object
	 */
	public Device knowDeviceStatus(long device_id) {
	/*
	 * TODO check the device status 
	 * 
	 */		try {
			SessionFactory sf=MySessionFactory.getSessionFactory();
			Session session=sf.openSession();
			Device d=session.get(Device.class, device_id);
			if(d!=null) {                   		//check device is available or not             
					if(checkValidity(d)) { 
						d=changeStatus(d);        //update validate _till date
						log.info("Device Not Valid right now");	
					}
				
				log.info("Deivce status found");  
				return d;						//return device status
				
			}else {
				 log.info("Deivce status not found ID"+device_id);          //device not found
				 d=new Device(device_id,null,DeviceStatus.NOT_FOUND);
				return d;
			}
		}catch(HibernateException e) {
			
		log.error("check device avablity exception ",e);
		}
		return null;
	}
	/**
	 * @param Device id
	 * @return remove device status 
	 */
	public int deRegisterUser(long device_id) {
		/*
		 * TODO check the device status 
		 * 
		 */

		try {
			SessionFactory sf=MySessionFactory.getSessionFactory();
			Session session=sf.openSession();
			Device d=session.get(Device.class, device_id);
			Transaction tx=session.beginTransaction();
			
			
			if(d!=null) {
				log.info("Deivce found");
				if(d.getStatus()==DeviceStatus.UNREGISTER) {
					
					return 777; //device already unregister
				}
				d.setStatus(DeviceStatus.UNREGISTER);
				session.save(d);
				tx.commit();
				deleteUser(d.getDevice_id());      //delete user
				session.close();
				
				return 0;      //device deregister successfully
				
			}else {
				log.info("Deivce  not found ID"+device_id);
				 
				return -1;  //device not found
			}
		}catch(HibernateException e) {
			
		log.error("device deregister error ",e);
		}
		
		return -1;      //device not found
		
	}
	/**
	 * @param Device object
	 * @return check validity of devices
	 */
	public boolean checkValidity(Device d) {
		
		log.info("Checking Validity");
		return TimeValidation.compareValidDate(new java.sql.Timestamp(d.getValid_till().getTime()),new java.sql.Timestamp(new java.util.Date().getTime()));
		
	}
	
	/**
	 * @param Device object
	 * @return change device object
	 */
	public Device changeStatus(Device d) {
		try {
		SessionFactory sf=MySessionFactory.getSessionFactory();
		Session session=sf.openSession();
		Transaction tx=session.beginTransaction();
		d=session.get(Device.class, d.getDevice_id());
		d.setStatus(DeviceStatus.NOT_VALID);
		log.info("change status excecuting");
		session.update(d);           //update device status
		tx.commit();
		session.close();
		}catch(HibernateException e) {
		
		log.error("device change Status error ",e);
		
		}
		return d;
		
		
	}
	
	/**
	 * @param device id
	 * @return message bean  object which contain statust
	 */
	
	
	public boolean checkDeviceAssign(long device_id) {
		/*
		 * TODO check the device status 
		 * 
		 */

		try {
			SessionFactory sf=MySessionFactory.getSessionFactory();
			Session session=sf.openSession();
			Criteria cr=session.createCriteria(User.class);
			cr.add(Restrictions.eqOrIsNull("device_id", device_id));
			User d=(User) cr.uniqueResult();
			if(d!=null) {
				return true;  //device is assign to another user
			}
			return false;    //device is not assign;
			
		}catch(HibernateException e) {
			
			log.error("checkDeviceAssign  error ",e);
		}
		
		
		return false;
	}
	
	/**
	 * @param device id
	 * @return message bean  object which contain statust
	 */
	
	public static boolean deleteUser(long device_id) {
		/*
		 * TODO check the device status 
		 * 
		 */

		try {
			SessionFactory sf=MySessionFactory.getSessionFactory();
			Session session=sf.openSession();
			Criteria cr=session.createCriteria(User.class);
			cr.add(Restrictions.eq("device_id", device_id));
			User u=(User) cr.uniqueResult();
			if(u!=null) {
				log.info("user deleted successfully");
				Transaction tx=session.beginTransaction();
				session.delete(u);
				tx.commit();
				return true;
			}
			
		}catch(HibernateException e) {
			
			log.error("checkDeviceAssign  error ",e);
		}
		
		
		log.info("device not found");
		return false;	
	}
	
	
	
}
