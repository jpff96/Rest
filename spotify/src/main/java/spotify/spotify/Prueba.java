package spotify.spotify;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Prueba {
    public static void main(String[] args) {
        HttpClient client = HttpClientBuilder.create().build();
        System.setProperty("webdriver.gecko.driver", "/home/juan/Downloads/geckodriver");

       // WebDriver driver = new FirefoxDriver();
    	
       // driver.get("https://accounts.spotify.com/es-UY/login?continue=https:%2F%2Fwww.spotify.com%2Fuy%2Faccount%2Foverview%2F");

		String url = "https://accounts.spotify.com/authorize";
		String charset = "UTF-8";
		String response_type = "code";
		String client_id = "bb958d9a732349ac882cb86eec2153a8";
		String scope = "user-read-private user-read-email";
		String redirect_uri = "http://localhost:8888/callback";
		String query;
		try {
			query = String.format("response_type=%s&client_id=%s&scope=%s&redirect_uri=%s",
					URLEncoder.encode(response_type, charset),
					URLEncoder.encode(client_id, charset),
					URLEncoder.encode(scope, charset),
					URLEncoder.encode(redirect_uri, charset));

			URLConnection urlConnection = new URL (url+"?"+query).openConnection();
			urlConnection.setRequestProperty("Accept-Charset", charset);
	        /*HttpGet request = new HttpGet(urlConnection.getURL().toString());
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            // Read the contents of an entity and return it as a String.
            String content = EntityUtils.toString(entity);*/
           // driver.get(urlConnection.getURL().toString());
			Desktop.getDesktop().browse(new URI(urlConnection.getURL().toString()));
			System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (URISyntaxException e) {
			// TODO Auto-generated catch blockx
			e.printStackTrace();
		}
    }
}