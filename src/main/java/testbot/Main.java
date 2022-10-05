package testbot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

public class Main {
	public static void main(String[] args) {
		
		EntityManagerFactory emf = null;
		emf = Persistence.createEntityManagerFactory("jbd-pu");
		final EntityManager entityManager =  emf.createEntityManager();
		EntityTransaction transaction = null;
		transaction = entityManager.getTransaction();
		transaction.begin();
		System.out.println("START");
		TelegramBot bot = new TelegramBot("5733003100:AAGp_H1K0BsThG29LN2BsZLcQ2WGrnKctGI");
		bot.setUpdatesListener(updates -> {
			TypedQuery<Contact> q = (TypedQuery<Contact>) entityManager.createNativeQuery("select * from contacts", Contact.class);
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
			    List<Contact> resultList = q.getResultList();
				var messageText = update.message().text().trim();
				SendMessage req = new SendMessage(id, test(messageText, resultList)).parseMode(ParseMode.HTML)
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
	    String dbUrl = System.getenv("Contacts_Base");
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
