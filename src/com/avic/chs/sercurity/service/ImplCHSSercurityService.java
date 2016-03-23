package com.avic.chs.sercurity.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.avic.chs.cis.service.CapitalCISService;
import com.avic.chs.sercurity.util.CommonUtil;
import com.avic.chs.sercurity.util.JdbcUtils;

public class ImplCHSSercurityService implements CHSSercurityService{

	@Override
	public String relateToCHSAccount(String userName){
		
		String resultMsg = "";
		
		try {
			boolean isExist = checkUserExist(userName);
			if(isExist){
				resultMsg = "<result><success>true</success><message>账号"+userName+"已经关联！</message></result>";
			}else{
				relateToFirstAccount(userName);
				resultMsg = "<result><success>true</success><message>账号"+userName+"关联成功！</message></result>";
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = "<result><success>false</success><message>账号"+userName+"关联失败！错误信息:"+e.getMessage()+"</message></result>";
		}
		
		
		return resultMsg;
	}
	
	/**
	 * 将username与第一个没有使用的账号关联
	 * @return
	 * @throws Exception 
	 */
	private void relateToFirstAccount(String username) throws Exception{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			JdbcUtils.getCmClassName();
			conn = JdbcUtils.getConnection();
			conn.setAutoCommit(true);
			stmt = conn.createStatement();
			
			StringBuilder updateSql = new StringBuilder("update securityuseraccount t set t.fullname = '"+username+"' where t.fullname = (");
			updateSql.append("select min(tt.fullname) AS FULLNAME from securityuseraccount tt where tt.fullname like '%_user$%')");
			stmt.executeUpdate(updateSql.toString());
		} catch (Exception e) {
			throw e;
		}finally{
			JdbcUtils.close(rs, stmt, conn);
		}
	}
	
	/**
	 * 查看用户是否已经关联
	 * @param username
	 * @return
	 * @throws Exception
	 */
	private boolean checkUserExist(String username) throws Exception{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			JdbcUtils.getCmClassName();
			conn = JdbcUtils.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) AS USERNUM from securityuseraccount t where t.fullname = '"+username+"'");
			while (rs.next()) {
				if (rs.getInt("USERNUM") >0){
					return true;
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			JdbcUtils.close(rs, stmt, conn);
		}
		return false;
		
	}

	@Override
	public String getCHSRoleInfo(String roleName) {
		Document resultDoc = DocumentHelper.createDocument();
		Element resultRoot = resultDoc.addElement("result");
		try {
			boolean hasRole = false;
			
			CapitalCISService cisService = new CapitalCISService();
			Document doc = cisService.getCHSSecurityData();
			Element root = doc.getRootElement();
			Element rolesMgr = root.element("RolesMgr");
			
			if(CommonUtil.isBlank(roleName)){
				resultRoot.addElement("success").addText("true");
				
				Element message = resultRoot.addElement("message");
				for(Object role : rolesMgr.elements("role")){
					Element roleElement = (Element) role;
					message.add(roleElement.detach());
				}
			}else{
				List<Element> roles = rolesMgr.elements();
				for(Element elment : roles){
					String name = elment.attributeValue("rolename");
					if(roleName.equals(name)){
						resultRoot.addElement("success").addText("true");
						resultRoot.addElement("message").add(elment.detach());
						hasRole = true;
						break;
					}
				}
				if(!hasRole){
					resultRoot.addElement("success").addText("false");
					resultRoot.addElement("message").addText("系统没有"+roleName+"角色!");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			resultRoot.addElement("success").addText("false");
			resultRoot.addElement("message").addText("获取角色信息出错，错误信息："+e.getMessage()+"!");
		}
		
		return resultDoc.asXML();
	}

	@Override
	public String grantCHSRole(String userName, String roleName) {
		Document resultDoc = DocumentHelper.createDocument();
		Element resultRoot = resultDoc.addElement("result");
		
		try {
			String userId = getCapitalUserAccountId(userName);
			if(CommonUtil.isBlank(userId)){
				resultRoot.addElement("success").addText("false");
				resultRoot.addElement("message").addText("系统没有用户"+userName+"!");
			}else{
				String roleId = getCapitalRoleId(roleName);
				if(CommonUtil.isBlank(roleId)){
					resultRoot.addElement("success").addText("false");
					resultRoot.addElement("message").addText("系统没有"+roleName+"角色!");
				}else{
					addRoleToUser(roleId, userId);
					addPermissionToUser(roleId, userId);
					resultRoot.addElement("success").addText("true");
					resultRoot.addElement("message").addText("成功将用户"+userName+"设置为"+roleName+"!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultRoot.addElement("success").addText("false");
			resultRoot.addElement("message").addText("将用户"+userName+"设置为"+roleName+"失败，错误信息:"+e.getMessage()+"!");
		}
		
		return resultDoc.asXML();
	}
	
	/**
	 * 为用户在Capital中添加角色
	 * @param role_id
	 * @param useraccount_id
	 * @throws Exception 
	 */
	private void addRoleToUser(String role_id,String useraccount_id) throws Exception{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			JdbcUtils.getCmClassName();
			conn = JdbcUtils.getConnection();
			stmt = conn.createStatement();
			stmt.execute("insert into securityroletouseraccount(role_id,useraccount_id) values('" + role_id + "','" + useraccount_id + "')");
		} catch (Exception e) {
			throw e;
		} finally{
			JdbcUtils.close(rs, stmt, conn);
		}
	}
	
	/**
	 * 为用户在Capital中添加权限
	 * @param role_id
	 * @param useraccount_id
	 * @throws Exception 
	 */
	private void addPermissionToUser(String roleId,String userId) throws Exception{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			JdbcUtils.getCmClassName();
			conn = JdbcUtils.getConnection();
			stmt = conn.createStatement();
			stmt.execute("insert into securitypermtouseraccount select r.permission_id,'"
							+ userId
							+ "' from securitypermissiontorole r where r.role_id='"
							+ roleId + "'");
			stmt.execute("insert into funcpermtouseraccount select t.funcpermission_id,'"
							+ userId
							+ "' from funcpermtorole t where t.role_id='"
							+ roleId + "'");
		} catch (Exception e) {
			throw e;
		} finally{
			JdbcUtils.close(rs, stmt, conn);
		}
		
	}
	
	/**
	 * 根据用户名获取用户在Capital中的useraccount_id
	 * @param userName
	 * @return
	 * @throws Exception 
	 */
	private String getCapitalUserAccountId(String userName) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String useraccount_id = "";
		try {
			JdbcUtils.getCmClassName();
			conn = JdbcUtils.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select t.useraccount_id AS useraccount_id from securityuseraccount t where t.fullname='" + userName + "'");
			while (rs.next()) {
				if (null != rs.getString("useraccount_id") && !"".equals(rs.getString("useraccount_id"))){
					useraccount_id = rs.getString("useraccount_id");
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			JdbcUtils.close(rs, stmt, conn);
		}
		return useraccount_id;
	}
	
	private String getCapitalRoleId(String roleName) throws Exception{
		String roleid = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			JdbcUtils.getCmClassName();
			conn = JdbcUtils.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select r.role_id AS ROLEID from securityrole r where r.rolename='"+roleName+"'");
			while (rs.next()) {
				if (null != rs.getString("ROLEID") && !"".equals(rs.getString("ROLEID"))){
					roleid = rs.getString("ROLEID");
					break;
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			JdbcUtils.close(rs, stmt, conn);
		}
		return roleid;
	}
	
	/**
	 * 检查roleName是否存在
	 * @param roleName
	 * @return
	 * @throws Exception
	 */
	private boolean checkRoleExist(String roleName) throws Exception{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			JdbcUtils.getCmClassName();
			conn = JdbcUtils.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) AS ROLENUM from securityrole t where t.rolename = '"+roleName+"'");
			while (rs.next()) {
				if (rs.getInt("ROLENUM") >0){
					return true;
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			JdbcUtils.close(rs, stmt, conn);
		}
		return false;
		
	}
	
	@Override
	public String deleteCHSUserRole(String userName, String roleName) {
		Document resultDoc = DocumentHelper.createDocument();
		Element resultRoot = resultDoc.addElement("result");
		
		try {
			String userId = getCapitalUserAccountId(userName);
			if(CommonUtil.isBlank(userId)){
				resultRoot.addElement("success").addText("false");
				resultRoot.addElement("message").addText("系统没有用户"+userName+"!");
			}else{
				String roleId = getCapitalRoleId(roleName);
				if(CommonUtil.isBlank(roleId)){
					resultRoot.addElement("success").addText("false");
					resultRoot.addElement("message").addText("系统没有"+roleName+"角色!");
				}else{
					deleteRoleToUser(roleId, userId);
					deletePermissionToUser(roleId, userId);
					resultRoot.addElement("success").addText("true");
					resultRoot.addElement("message").addText("成功删除用户"+userName+"的"+roleName+"角色!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultRoot.addElement("success").addText("false");
			resultRoot.addElement("message").addText("删除用户"+userName+"的"+roleName+"角色失败，错误信息:"+e.getMessage()+"!");
		}
		
		return resultDoc.asXML();
	}
	
	private void deleteRoleToUser(String roleId,String userId) throws Exception{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			JdbcUtils.getCmClassName();
			conn = JdbcUtils.getConnection();
			stmt = conn.createStatement();
			stmt.execute("delete from securityroletouseraccount t where t.role_id = '"+roleId+"' and t.useraccount_id='" + userId + "'");
		} catch (Exception e) {
			throw e;
		} finally{
			JdbcUtils.close(rs, stmt, conn);
		}
	
	}
	
	/**
	 * 删除为用户在Capital中权限
	 * @param role_id
	 * @param useraccount_id
	 * @throws Exception 
	 */
	private void deletePermissionToUser(String roleId,String userId) throws Exception{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			JdbcUtils.getCmClassName();
			System.out.println("Start connect Capital DB!!!To do deletePermissionToUser");
			conn = JdbcUtils.getConnection();
			stmt = conn.createStatement();
			
			stmt.execute("delete from securitypermtouseraccount t where t.useraccount_id ='"+userId+"' and t.permission_id in ("
					+ "select spr.permission_id from securitypermissiontorole spr where spr.role_id = '"+roleId+"')");
			stmt.execute("delete from funcpermtouseraccount t where t.useraccount_id ='"+userId+"' and t.funcpermission_id in ("
					+ "select fpr.funcpermission_id from funcpermtorole fpr where fpr.role_id = '"+roleId+"')");
		} catch (Exception e) {
			throw e;
		} finally{
			JdbcUtils.close(rs, stmt, conn);
		}
		
	}
	
	@Override
	public String getAllAccount(String userName) {

		Document resultDoc = DocumentHelper.createDocument();
		Element resultRoot = resultDoc.addElement("result");
		try {
			boolean hasUser = false;
			
			CapitalCISService cisService = new CapitalCISService();
			
			Document doc = cisService.getCHSSecurityData();
			Element root = doc.getRootElement();
			Element userMgr = root.element("UserMgr");
			
			if(CommonUtil.isBlank(userName)){
				resultRoot.addElement("success").addText("true");
				
				Element message = resultRoot.addElement("message");
				for(Object user : userMgr.elements("useraccount")){
					Element userElement = (Element) user;
					if(!userElement.attributeValue("fullname").startsWith("_user$") || 
							!userElement.attributeValue("fullname").equals(userElement.attributeValue("username"))){
						userElement.remove(userElement.attribute("dept"));
						userElement.remove(userElement.attribute("lockstatus"));
						userElement.remove(userElement.attribute("noofinvalidpasswordattempts"));
						userElement.remove(userElement.attribute("password"));
						userElement.remove(userElement.attribute("passwordchangefrequency"));
						userElement.remove(userElement.attribute("passwordexpirydate"));
						userElement.remove(userElement.attribute("sitenumber"));
						userElement.remove(userElement.attribute("uiconfiguration"));
						message.add(userElement.detach());
					}
				}
			}else{
				List<Element> users = userMgr.elements();
				for(Element elment : users){
					String name = elment.attributeValue("fullname");
					if(userName.equals(name)){
						resultRoot.addElement("success").addText("true");
						elment.remove(elment.attribute("dept"));
						elment.remove(elment.attribute("lockstatus"));
						elment.remove(elment.attribute("noofinvalidpasswordattempts"));
						elment.remove(elment.attribute("password"));
						elment.remove(elment.attribute("passwordchangefrequency"));
						elment.remove(elment.attribute("passwordexpirydate"));
						elment.remove(elment.attribute("sitenumber"));
						elment.remove(elment.attribute("uiconfiguration"));
						resultRoot.addElement("message").add(elment.detach());
						hasUser = true;
						break;
					}
				}
				
				if(!hasUser){
					resultRoot.addElement("success").addText("false");
					resultRoot.addElement("message").addText("系统没有"+userName+"用户!");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			resultRoot.addElement("success").addText("false");
			resultRoot.addElement("message").addText("获取用户信息出错，错误信息："+e.getMessage()+"!");
		}
		
		return resultDoc.asXML();
	
	}
}
