package org.cn.o2o.dto;
/**
 * 封装Json对象，所有返回结果都是用它
 * @author Administrator
 *
 */
public class Result<T> {
	private boolean success;//是否成功标志
	private T data;//成功时返回的数据
	private String errorMsg;//错误信息
	private int errorCode;
	
	public Result() {	
	}
	public Result(boolean success,T data) {
		this.success=success;
		this.data=data;
	}
	public Result(boolean success,int errorCode,String errorMsg) {
		this.success=success;
		this.errorMsg=errorMsg;
		this.errorCode=errorCode;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}
