package com.orquestador_1.ws.rest.service;

import spotify.spotify.Principal;

public class Usuario {
	
    private static final Principal SPOTIFY = new Principal();
    private String chat_id;
    private static String ultimo_intent="";
    private Integer numeroCancionReproducida;
    
    public Integer getNumeroCancionReproducida() {
		return numeroCancionReproducida;
	}
	public void setNumeroCancionReproducida(Integer numeroCancionReproducida) {
		this.numeroCancionReproducida = numeroCancionReproducida;
	}
	public Usuario (String chat_id) {
    	this.chat_id = chat_id;    	
    }
	public String getChat_id() {
		return chat_id;
	}

	public void setChat_id(String chat_id) {
		this.chat_id = chat_id;
	}

	public static String getUltimo_intent() {
		return ultimo_intent;
	}

	public static void setUltimo_intent(String ultimo_intent) {
		Usuario.ultimo_intent = ultimo_intent;
	}

	public static Principal getSpotify() {
		return SPOTIFY;
	}
	
	
	
	

}
