package testbot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;


public class Main {
	public static void main(String[] args) {
		System.out.println("START");
		TelegramBot bot = new TelegramBot("5733003100:AAGp_H1K0BsThG29LN2BsZLcQ2WGrnKctGI");
		    bot.setUpdatesListener(updates -> {
		    	for(Update update : updates) {
		    		var user_id = update.message().from().id() + "" ;
		    		var whitelist = readWhiteList("whitelist");
		    		var id = update.message().chat().id();
		        	var messageId = update.message().messageId();
		    		if(!whitelist.contains(user_id)) {
		    			SendMessage req = new SendMessage(id, "Access denied")
			           	        .parseMode(ParseMode.HTML)
			           	        .disableWebPagePreview(true)
			           	        .disableNotification(true)
			           	        .replyToMessageId(messageId);
			        	SendResponse sendResponse = bot.execute(req);
			        	continue;
		    		}
		        	
		        	var messageText = update.message().text().trim();
		        	SendMessage req = new SendMessage(id, test(messageText))
		           	        .parseMode(ParseMode.HTML)
		           	        .disableWebPagePreview(true)
		           	        .disableNotification(true)
		           	        .replyToMessageId(messageId);
		        	      
		        	SendResponse sendResponse = bot.execute(req);
		        	boolean ok = sendResponse.isOk();
		        	System.out.println(ok);
		        	
		        }
		        return UpdatesListener.CONFIRMED_UPDATES_ALL;
		    });
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
	public static String test(String messageText) {
		if(messageText.equals("/start")) {
   		 return "privet";
		}else if(messageText.startsWith("/find")) {
			var contacts = byBufferedReader("contactlist");
			var t = messageText.replace("/find", "").trim();
			var contact = contacts.get(t);
			System.out.println(contacts);
			if(contact == null) {
				return "not found";
			} else {
				return contact;
			}
	   	}else {
	   		return "text";
	       	    
	   	}
	
   
	}
}
