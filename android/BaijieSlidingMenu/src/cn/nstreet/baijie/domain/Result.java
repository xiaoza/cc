package cn.nstreet.baijie.domain;

import java.io.Serializable;

/**
 * 与服务器交互返回数据的基类
 * @author clownfish
 *
 * @param <T>
 */
public class Result<T> implements Serializable{
	
	private Integer code;
	private String msg;
	private T data;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	public static enum CodeType {

        SUCCESS(200, "成功"),
        ERROR(500, "失败");

        private Integer id;
        private String name;

        CodeType(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public static CodeType valueOf(Integer id) {
            for (CodeType cityType : values()) {
                if (cityType.id == id.intValue()) {
                    return cityType;
                }
            }
            return null;
        }
    }
	
}
