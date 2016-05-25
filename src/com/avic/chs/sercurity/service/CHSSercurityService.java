package com.avic.chs.sercurity.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.RPC)
public interface CHSSercurityService {
	
	/**
	 * 账号(职工编号)与CHS内置账号的绑定
	 * @param userName 职工编号
	 * @return
	 */
	public String relateToCHSAccount(@WebParam(name = "userName")String userName);
	
	/**
	 * 获取CHS所有的角色及角色对应的权限信息
	 * @param roleName CHS的角色名称
	 * @return
	 */
	public String getCHSRoleInfo(@WebParam(name = "roleName")String roleName);
	
	/**
	 * 角色与账号的关联
	 * @param userName 职工编号
	 * @param roleName CHS的角色名称
	 * @return
	 */
	public String grantCHSRole(@WebParam(name = "userName")String userName,@WebParam(name = "roleName")String roleName);
	
	/**
	 * 删除账号对应的角色
	 * @param userName
	 * @param roleName
	 * @return
	 */
	public String deleteCHSUserRole(@WebParam(name = "userName")String userName,@WebParam(name = "roleName")String roleName);
	
	/**
	 * 获取有效用户信息,userName为空，获取所有账号信息；否则，获取指定账号信息
	 * @param userName 职工编号
	 * @return
	 */
	public String getAccount(@WebParam(name = "userName")String userName);
	
	/**
	 * 账号的启用与禁用
	 * @param isLock 
	 * @param userName
	 * @return
	 */
	public String changeUserLockStatus(@WebParam(name = "userName")String userName,@WebParam(name = "isLock")String isLock);
	
	/**
	 * 修改用户信息，只支持可见最高密级的修改
	 * @param userName
	 * @param secretLevel
	 * @return
	 */
	public String editUser(@WebParam(name = "userName")String userName,@WebParam(name = "secretLevel")String secretLevel);
	
	/**
	 * 获取用户数量
	 * countType	0 获取所有有效用户数量|1 获取启用的用户数量 |2 获取禁用的用户数量
	 * @param countType
	 * @return
	 */
	public String getUserCount(@WebParam(name = "countType")int countType);
	
}
