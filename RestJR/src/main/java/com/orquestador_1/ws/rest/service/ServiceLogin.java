/*package com.orquestador_1.ws.rest.service;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.riversun.wcs.WcsClient;
import java.util.Base64;

import com.sun.research.ws.wadl.Request;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import spotify.spotify.Principal;
import watson.watson.Watson;

@Path("/services")
public class ServiceLogin {
		
    private static final String WATSON_CONVERSATION_USERNAME = "daae0606-fbe6-42b0-90e6-1ed71fcab264";
    private static final String WATSON_CONVERSATION_PASSWORD = "Z1VBWZ5iuMpy";
    private static final String WATSON_CONVERSATION_WORKSPACE_ID = "b3eb3e30-07ea-46dd-9a94-8da5a859405b";
    private static final Principal SPOTIFY = new Principal();
	private static final Watson conversacion = new Watson();
    private static String string_res = "";
    private static String ultimo_intent="";
    private static String entity="";
    private static String entity_value="";
    private static String token_spotify = "";
    private static int numeroCancionReproducida;
       
    
    private static HttpClient client = HttpClientBuilder.create().build();
    final WcsClient watson = new WcsClient(
    		WATSON_CONVERSATION_USERNAME,
            WATSON_CONVERSATION_PASSWORD,
            WATSON_CONVERSATION_WORKSPACE_ID);
	@GET
	@Path("/response/{userId}/{userInputText}")
	@Produces("application/json")
	public String validaUsuario(@PathParam("userId") String userId, @PathParam("userInputText") String userInputText) {
		String wcsClientId = userId;
		System.out.println("entrada"+userInputText);
        String botOutputText = "";
        JSONObject jsonRespuesta = conversacion.response(userInputText, wcsClientId, watson);
        System.out.println(jsonRespuesta);
		String ultimoIntent = "";
		botOutputText = jsonRespuesta.getJSONObject("output").getJSONArray("text").getString(0);
		if(jsonRespuesta.getJSONArray("entities").length() != 0) {
			if(jsonRespuesta.getJSONArray("entities").getJSONObject(0).getString("entity").toString().equals("Numero")) {
					try {
						SPOTIFY.reproducirCancion(new Integer(jsonRespuesta.getJSONObject("output").getJSONArray("text").getString(0)));
						System.out.println("VER ACA: " +new Integer(jsonRespuesta.getJSONObject("output").getJSONArray("text").getString(0)));
						numeroCancionReproducida = new Integer(jsonRespuesta.getJSONObject("output").getJSONArray("text").getString(0));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SpotifyWebApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					botOutputText = "Disfruta tu canción!"; 
			}
		}
		if(ultimo_intent.equals(new String("Buscar"))){
		    botOutputText = "Estos son los temas que encontré para "+ userInputText+":\n"+ SPOTIFY.retornoSpotify(userInputText)+"\n "
		    		+ "Para reproducir alguna de estas canciones indica el número que querés";
		    ultimo_intent="";
		}
		
		if(jsonRespuesta.getJSONArray("intents").getJSONObject(0).getString("intent").equals(new String("Buscar"))){
		    ultimo_intent = jsonRespuesta.getJSONArray("intents").getJSONObject(0).getString("intent").toString();
		}
		return botOutputText;
		
		

    }
	@GET
	@Path("/callback")
	@Produces("application/json")
	public void callback(@Context HttpServletRequest request) {
	    final String clientId = "bb958d9a732349ac882cb86eec2153a8";
		final String clientSecret = "7f46cce8d89b4d119f07383046a4444b";
		String httpsURL = "https://accounts.spotify.com/api/token";
		String query;
		try {
			query = "code="+URLEncoder.encode(request.getParameter("code"),"UTF-8");
			query+="&";
			query+="grant_type="+URLEncoder.encode("authorization_code","UTF-8");
			query+="&";
			query+="redirect_uri="+URLEncoder.encode("https://restjr.mybluemix.net/rest/services/callback","UTF-8");
			
			URL urlSpotify = new URL(httpsURL);
			HttpsURLConnection con = (HttpsURLConnection) urlSpotify.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
			String userCredentials = (clientId+":"+clientSecret);
			String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
			con.setRequestProperty("Authorization",basicAuth);
			con.setDoOutput(true);
			con.setDoInput(true);
			DataOutputStream output = new DataOutputStream(con.getOutputStream());
			output.writeBytes(query);
			output.flush();
			output.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject jsonResponse = new JSONObject(response.toString());
			System.out.println(jsonResponse.getString("access_token"));
			SPOTIFY.getSpotifyApi().setAccessToken(jsonResponse.getString("access_token"));
			SPOTIFY.getSpotifyApi().setRefreshToken(jsonResponse.getString("refresh_token"));
			} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	@GET
	@Path("/login")
	@Produces("application/json")
	public String login() {
		System.out.println("Llego");
		System.setProperty("webdriver.gecko.driver", "/home/juan/Downloads/geckodriver");
		String url = "https://accounts.spotify.com/authorize";
		String charset = "UTF-8";
		String response_type = "code";
		String client_id = "bb958d9a732349ac882cb86eec2153a8";
		String scope = "user-read-private user-read-email user-modify-playback-state user-read-playback-state user-read-currently-playing";
		String redirect_uri = "https://restjr.mybluemix.net/rest/services/callback";
		String query;
	    URLConnection urlConnection = null;
			try {
				query = String.format("response_type=%s&client_id=%s&scope=%s&redirect_uri=%s",
						URLEncoder.encode(response_type, charset),
						URLEncoder.encode(client_id, charset),
						URLEncoder.encode(scope, charset),
						URLEncoder.encode(redirect_uri, charset));
			urlConnection = new URL (url+"?"+query).openConnection();
			urlConnection.setRequestProperty("Accept-Charset", charset);

			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return urlConnection.getURL().toString();
	}
	
	@GET
	@Path("/pausar")
	@Produces("application/json") 
	public String pausar(){
		try {
			SPOTIFY.pausarReproduccion();
		} catch (SpotifyWebApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Se paró la música";
	}
	
	@GET
	@Path("/recomendar")
	@Produces("application/json") 
	public String recomendar(){
		return SPOTIFY.getRecommendations();
	}	
	
	@GET
	@Path("/bajarVolumen")
	@Produces("application/json") 
	public String bajarVolumen(){
		SPOTIFY.bajarVolumen();
		return ("Bajaste el volumen");
	}	
	
	@GET
	@Path("/subirVolumen")
	@Produces("application/json") 
	public String subirVolumen(){
		SPOTIFY.subirVolumen();
		return ("Subiste el volumen");
	}
	
}*/