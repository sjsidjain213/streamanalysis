package bean;

import java.io.Serializable;

public class Log implements Serializable {
	private String ipaddress, timestamp, responsecode, responsetime, page, value;

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}

	public String getResponsetime() {
		return responsetime;
	}

	public void setResponsetime(String responsetime) {
		this.responsetime = responsetime;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public Log(String ipaddress, String page) {
		this.ipaddress = ipaddress;
		this.page = page;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Log [ipaddress=" + ipaddress + ", timestamp=" + timestamp + ", responsecode=" + responsecode
				+ ", responsetime=" + responsetime + ", page=" + page + ", value=" + value + "]";
	}

	public Log(String timestamp, String ipaddress, String page,String responsecode, String responsetime) {
		super();
		this.ipaddress = ipaddress;
		this.timestamp = timestamp;
		this.responsecode = responsecode;
		this.responsetime = responsetime;
		this.page = page;
	}
}
