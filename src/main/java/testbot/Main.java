package testbot;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.postgresql.util.PSQLException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

public class Main {
	public static void main(String[] args) throws URISyntaxException, SQLException {

        Connection connection = getConnection();
        
        Statement stmt = connection.createStatement();
		//EntityManagerFactory emf = null;
//		emf = Persistence.createEntityManagerFactory("jbd-pu");
//		final EntityManager entityManager =  emf.createEntityManager();
//		EntityTransaction transaction = null;
//		transaction = entityManager.getTransaction();
//		transaction.begin();
		System.out.println("START");
		TelegramBot bot = new TelegramBot("5733003100:AAGp_H1K0BsThG29LN2BsZLcQ2WGrnKctGI");
		bot.setUpdatesListener(updates -> {
			ResultSet q;
			try {
				q = stmt.executeQuery("select * from contacts");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return UpdatesListener.CONFIRMED_UPDATES_NONE;
			}
			
			for (Update update : updates) {
				var user_id = update.message().from().id() + "";
				var whitelist = readWhiteList("whitelist");
				var id = update.message().chat().id();
				var messageId = update.message().messageId();
				if (!whitelist.contains(user_id)) {
					SendMessage req = new SendMessage(id, "Access denied").parseMode(ParseMode.HTML)
							.disableWebPagePreview(true).disableNotification(true).replyToMessageId(messageId);
					SendResponse sendResponse = bot.execute(req);
					continue;
				}
				List<Contact> contacts = new ArrayList<>();
				try{
				while (q.next()) {
					Contact c  = new Contact();
					c.setName(q.getString("Name"));
					c.setPhone_Number(q.getString("Phone_Number"));
					c.setTelegram_UserName(q.getString("Telegram_UserName"));
					contacts.add(c);
					
		        }
				}catch(Exception e) {
					e.printStackTrace();
					continue;
				}
				var messageText = update.message().text().trim();
				SendMessage req = new SendMessage(id, test(messageText, contacts)).parseMode(ParseMode.HTML)
						.disableWebPagePreview(true).disableNotification(true).replyToMessageId(messageId);

				SendResponse sendResponse = bot.execute(req);
				boolean ok = sendResponse.isOk();
				if(!ok) {
					System.out.println("Something went wrong");
				}
			}
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}
	
	private static Connection getConnection() throws URISyntaxException, SQLException {
		String dbUrl = "jdbc:postgresql://ec2-99-80-170-190.eu-west-1.compute.amazonaws.com:5432/dcf6a77hkvnd97?sslmode=require&user=zotkvhnmjheghz&password=5b00730e1c6f7483c360ecf3665b9b1ca198794b80c2c2cabbd7b5b3f670faa9";
	    return DriverManager.getConnection(dbUrl);
	}
	public static Map<String, String> byBufferedReader(String filePath) {
		HashMap<String, String> map = new HashMap<>();
		String line;
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			while ((line = reader.readLine()) != null) {
				String[] keyValuePair = line.split(":", 2);
				if (keyValuePair.length > 1) {
					String key = keyValuePair[0];
					String value = keyValuePair[1];
					map.put(key, value);
				} else {
					System.out.println("No Key:Value found in line, ignoring: " + line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static List<String> readWhiteList(String filePath) {
		ArrayList<String> list = new ArrayList<>();
		String line;
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String test(String messageText, List<Contact> contacts) {
		if (messageText.equals("/start")) {
			return "privet";
		} else if (messageText.startsWith("/find")) {
			if(contacts.size()==0) {
				return "Database is empty";
			}
			StringBuilder text = new StringBuilder();
			for(var contact : contacts) {
				text.append(contact.getName() + " - " + contact.getPhone_Number() + " - " + contact.getTelegram_UserName() + "\n");
			}
			return text.toString();
		} else {
			return "text";
		}

	}
}
