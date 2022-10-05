package testbot;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name= "contacts")
@Entity
public class Contact {
	String Position;
	String Name;
	Integer id;
	String Phone_Number;
	String Telegram_UserName;
	
	@Column
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	@Id
	@Column
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column
	public String getPosition() {
		return Position;
	}
	public void setPosition(String position) {
		Position = position;
	}
	@Column
	public String getPhone_Number() {
		return Phone_Number;
	}
	public void setPhone_Number(String phone_Number) {
		Phone_Number = phone_Number;
	}
	@Column
	public String getTelegram_UserName() {
		return Telegram_UserName;
	}
	public void setTelegram_UserName(String telegram_UserName) {
		Telegram_UserName = telegram_UserName;
	}
	
}
