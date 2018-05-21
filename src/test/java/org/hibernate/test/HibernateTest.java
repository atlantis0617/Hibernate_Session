package org.hibernate.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.model.Student;
import org.junit.Test;

public class HibernateTest {
	
	@Test
	public void testOpenSession() {
		Configuration config = new Configuration().configure();// 获取配置对象
		SessionFactory sessionFactory = config.buildSessionFactory();// 获取SessionFactory对象
		Session session1 = sessionFactory.openSession();// 获取Session对象
		Session session2 = sessionFactory.openSession();// 获取Session对象
		boolean result = session1 == session2;
		if (result) {
			System.out.println("openSession使用现有的session对象");
		} else {
			System.out.println("openSession每次都创建新的session对象");
		}
	}
	/**
	 * 1. openSession

　　2. getCurrentSession

　　　　- 如果使用getCurrentSession需要在hibernate.cfg.xml中进行如下配置：

　　　　如果是本地事务（jdbc事务）

<property name="current_session_context_class">thread</property>
　　　　如果是全局事务（jta事务）

<property name="current_session_context_class">jta</property>
　　　　- 全局事务和本地事务

　　　　本地事务适合对一个数据库进行操作，全局事务适合对多个数据库进行操作；

　　　　当存在多个数据库是，也就存在多个session，这样本地事务就无法对多个session进行统一管理，因此可以使用全局事务。

　　3. openSession和getCurrentSession区别

　　　　- getCurrentSession在事务提交或者回滚之后会自动关闭session，而openSession需要你手动关闭session。如果使用openSession而没有手动关闭，多次之后会导致连接池溢出

　　　　- openSession每次创建新的session对象，getCurrentSession使用现有的session对象
	 * 
	 * */
	
	
	@Test
	public void testGetCurrentSession() {
		Configuration config = new Configuration().configure();// 获取配置对象
		SessionFactory sessionFactory = config.buildSessionFactory();// 获取SessionFactory对象
		Session session1 = sessionFactory.getCurrentSession();// 获取Session对象
		Session session2 = sessionFactory.getCurrentSession();// 获取Session对象
		boolean result = session1 == session2;
		if (result) {
			System.out.println("getCurrentSession使用现有的session对象");
		} else {
			System.out.println("getCurrentSession每次都创建新的session对象");
		}
	}
	
	@Test
	public void testSaveStudentWithOpenSession() {
	
		Configuration config = new Configuration().configure();// 获取配置对象
		SessionFactory sessionFactory = config.buildSessionFactory();// 获取SessionFactory对象
		
		Session session1 = sessionFactory.openSession();// 获取Session对象
		Transaction transaction = session1.beginTransaction();// 开启事务
		Student student = new Student(1, "张三", "男", new Date(), "北京");// 创建Student对象
		session1.doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				
				System.out.println("connection hashCode: " + connection.hashCode());
				
			}
		});
		session1.save(student);// 保存对象
		transaction.commit();
		System.out.println(session1);

		Session session2 = sessionFactory.openSession();// 获取Session对象
		transaction = session2.beginTransaction();// 开启事务
		student = new Student(2, "李四", "男", new Date(), "上海");// 创建Student对象
		session2.doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				
				System.out.println("connection hashCode: " + connection.hashCode());
				
			}
		});
		session2.save(student);// 保存对象
		transaction.commit();
		System.out.println(session2);
	}
	
	
	@Test
	public void testSaveStudentWithGetCurrentSession() {
		Configuration config = new Configuration().configure();// 获取配置对象
		SessionFactory sessionFactory = config.buildSessionFactory();// 获取SessionFactory对象
		Session session1 = sessionFactory.getCurrentSession();// 获取Session对象
		
		Transaction transaction = session1.beginTransaction();// 开启事务
		Student student = new Student(1, "张三", "男", new Date(), "北京");// 创建Student对象
		session1.doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				
				System.out.println("connection hashcode: " + connection.hashCode());
				
			}
		});
		session1.save(student);// 保存对象
		transaction.commit();// 提交事务
		System.out.println(session1);
		
		Session session2 = sessionFactory.getCurrentSession();// 获取Session对象
		transaction = session2.beginTransaction();// 开启事务
		student = new Student(2, "李四", "男", new Date(), "上海");// 创建Student对象
		session2.doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				
				System.out.println("connection hashcode: " + connection.hashCode());
				
			}
		});
		session2.save(student);// 保存对象
		transaction.commit();// 提交事务
		System.out.println(session2);
	}
	
}
