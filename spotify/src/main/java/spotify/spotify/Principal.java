package spotify.spotify;


import java.io.IOException;

import java.net.URI;

import java.util.ArrayList;
import java.util.Date;

import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;

import com.wrapper.spotify.SpotifyHttpManager;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Recommendations;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import com.wrapper.spotify.requests.data.browse.GetRecommendationsRequest;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.PauseUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SetVolumeForUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;

import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;


public class Principal {

	private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8888/callback");
	final static String clientId = "bb958d9a732349ac882cb86eec2153a8";
	final static String clientSecret = "7f46cce8d89b4d119f07383046a4444b";
	static SpotifyApi spotifyApi = new SpotifyApi.Builder()
			.setClientId(clientId)
			.setClientSecret(clientSecret)
			//.setAccessToken("BQD3khL4ZfepNs0FvBSG74urJxSdtjeGC97L-aeJNsdnlftWSuaU1EZR-Rg5ogIGNSJ4mivApNoBuD3ywoHtid72n3h_D1hrVPVGdfD0fCd-icbN-FB_xRtZLDy0GCnPSjMiMltfqTiaIdVg21jU9-gtvXk")
			.setRedirectUri(redirectUri)
			.build();

	public static String respuestaASlack = "";
	public static ArrayList<String> idsCancionesEnviadas = new ArrayList <String>();
	public static int ultimaCancionEscuchada;
	public static String ultimaCancionBuscada;

	public static void main (String [] args) throws SpotifyWebApiException, IOException {
		
	}

	public static String retornoSpotify(String track){
		SearchTracksRequest searchTrackRequest = spotifyApi.searchTracks(track).build();
		ultimaCancionBuscada = track;
		respuestaASlack = "";
		idsCancionesEnviadas.clear();

		try {
			Paging <Track> trackPaging = searchTrackRequest.execute();
			Track[] tracks = trackPaging.getItems();
			int i=1;
			for (Track t : tracks) {
				if(i==4){
					break;
				}

				respuestaASlack += i+" - "+t.getName()+" - "+t.getAlbum().getArtists()[0].getName()+"\n";
				idsCancionesEnviadas.add(t.getId());
				i+=1;

			}
			
			while (idsCancionesEnviadas.size()<=6) {
				idsCancionesEnviadas.add(null);
			}

		} catch (SpotifyWebApiException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return respuestaASlack;
	}


	public static void reproducirCancion (int numeroRecibido) throws SpotifyWebApiException, IOException {
		ultimaCancionEscuchada = numeroRecibido;
		String idCancion = idsCancionesEnviadas.get(numeroRecibido-1);
		StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
				.startResumeUsersPlayback()
				.uris(new JsonParser().parse("[\"spotify:track:"+idCancion+"\"]").getAsJsonArray())
				.build();
		startResumeUsersPlaybackRequest.execute();
	}


	public static void pausarReproduccion () throws SpotifyWebApiException, IOException {
		PauseUsersPlaybackRequest pauseUserPlaybackRequest = spotifyApi.pauseUsersPlayback().build();
		pauseUserPlaybackRequest.execute();
	}


	public static SpotifyApi getSpotifyApi() {
		return spotifyApi;
	}


	public static void setSpotifyApi(SpotifyApi spotifyApi) {
		Principal.spotifyApi = spotifyApi;
	}
	public String getArtistTopTracks (String idArtista) {
		String respuesta = "";
		GetArtistsTopTracksRequest getArtistsTopTracksRequest = spotifyApi
		.getArtistsTopTracks(idArtista, CountryCode.UY)
		.build();
		try { 
		Track[] tracks = getArtistsTopTracksRequest.execute();
		int i=1;
		for (Track t : tracks) { 
		respuesta += i + " - " + t.getName() + " - " + t.getAlbum().getArtists()[0].getName() + "\n";
		i+=1;
		}
		System.out.println(respuesta);
		     
		} catch (IOException e) {
		     System.out.println("Error: " + e.getMessage());
		} catch (SpotifyWebApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return respuesta;
		}
	
	
		public static String getRecommendations () {
			
			SearchTracksRequest searchTrackRequest = spotifyApi.searchTracks(ultimaCancionBuscada).build();
			String idArtista = "";
			try {
				Paging <Track> trackPaging = searchTrackRequest.execute();
				Track[] tracks = trackPaging.getItems();
				int i=1;
				for (Track t : tracks) {
					if(i==4){
						break;
					}
					if (i==ultimaCancionEscuchada) {
						idArtista = t.getArtists()[0].getId();
					}
					i+=1;
				}
				

			} catch (SpotifyWebApiException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}			
		
			String respuesta = "";
			GetRecommendationsRequest getRecommendationsRequest = spotifyApi.getRecommendations()
			         .limit(3)
			         .market(CountryCode.UY)
			         .max_popularity(50)
			         .min_popularity(10)
			         .seed_artists(idArtista)
			         .target_popularity(20)
			         .build();
			try { 
			Recommendations recommendations = getRecommendationsRequest.execute();
			int i=0;
			respuesta = "Ey! Sabías que los que escuchan eso también escuchan esto?\n";
			for (TrackSimplified t : recommendations.getTracks()) {
				respuesta += i+4 + " - " + t.getName() + " - " + t.getArtists()[0].getName() + "\n";
				idsCancionesEnviadas.set(i+3, t.getId());
				i+=1;
			}
			
			System.out.println("idsCancionesEnviadas: "+idsCancionesEnviadas);
			     
			}catch (IOException e) {
			     System.out.println("Error: " + e.getMessage());
			} catch (SpotifyWebApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return respuesta;
		}

		public static void bajarVolumen() {
			Integer volumenActual;
			System.out.println("entre a bajarVolumen en spotify");
			
			final GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest =
			          spotifyApi.getInformationAboutUsersCurrentPlayback()
			                  .market(CountryCode.UY)
			                  .build();
			try {
				CurrentlyPlayingContext currentlyPlayingContext = getInformationAboutUsersCurrentPlaybackRequest.execute();
				volumenActual = currentlyPlayingContext.getDevice().getVolume_percent();
				System.out.println("volumen antes: "+volumenActual);
				if (volumenActual.compareTo(new Integer("20")) < 0){
					SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = spotifyApi
					          .setVolumeForUsersPlayback(new Integer("0"))
					          .build();
					setVolumeForUsersPlaybackRequest.execute();
				}else{
				SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = spotifyApi
				          .setVolumeForUsersPlayback(volumenActual-new Integer("20"))
				          .build();
				setVolumeForUsersPlaybackRequest.execute();
				}
				volumenActual = currentlyPlayingContext.getDevice().getVolume_percent();
				System.out.println("volumen despues :"+volumenActual);
				
			} catch (SpotifyWebApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		public static void subirVolumen() {
			Integer volumenActual;			
			System.out.println("entre a subirVolumen en spotify");
			final GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest =
			          spotifyApi.getInformationAboutUsersCurrentPlayback()
			                  .market(CountryCode.UY)
			                  .build();
			try {
				CurrentlyPlayingContext currentlyPlayingContext = getInformationAboutUsersCurrentPlaybackRequest.execute();
				volumenActual = currentlyPlayingContext.getDevice().getVolume_percent();
				System.out.println("volumen antes :"+volumenActual);
				if (volumenActual.compareTo(new Integer("80")) > 0){
					SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = spotifyApi
					          .setVolumeForUsersPlayback(new Integer("100"))
					          .build();
					setVolumeForUsersPlaybackRequest.execute();
				}else{
				SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = spotifyApi
				          .setVolumeForUsersPlayback(volumenActual+new Integer("20"))
				          .build();
				setVolumeForUsersPlaybackRequest.execute();
				}				
				volumenActual = currentlyPlayingContext.getDevice().getVolume_percent();
				System.out.println("volumen despues :"+volumenActual);
			} catch (SpotifyWebApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		}

}
