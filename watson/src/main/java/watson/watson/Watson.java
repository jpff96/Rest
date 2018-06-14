package watson.watson;

import org.json.JSONObject;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.riversun.wcs.WcsClient;
import org.riversun.xternal.simpleslackapi.SlackUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

public class Watson {
	
    private static String string_res = "";
    private static String ultimo_intent="";
    private static String entity="";
    private static String entity_value="";
    
    public JSONObject response(String input, String userId, WcsClient watson){
    	
    	String wcsClientId = userId;
        String botOutputText = watson.sendMessageForText(wcsClientId, input);
        MessageResponse botOutput = watson.sendMessage(wcsClientId, input);
        ObjectMapper map = new ObjectMapper(); //Libreria utilizada para convertir el message response en string y luego a json
        JSONObject json = null;
    	
        try {
            string_res = map.writeValueAsString(botOutput);
            json = new JSONObject(string_res);
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

}
