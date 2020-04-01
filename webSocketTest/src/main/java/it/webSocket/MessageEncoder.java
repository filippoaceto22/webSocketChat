package it.webSocket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;


public class MessageEncoder implements Encoder.Text<Message> {
	 
    private static Gson gson = new Gson();
 
    @Override
    public String encode(Message message) throws EncodeException {
        return gson.toJson(message);
    }
 
   
 
    @Override
    public void destroy() {
        // Close resources
    }

	@Override
	public void init(EndpointConfig config) {
		// TODO Auto-generated method stub
		
	}
}