package rs.ac.bg.fon.ai.json_menjacnica.main;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import rs.ac.bg.fon.ai.json_menjacnica.Transakcija;

public class Main2 {
	
	private static final String BASE_URL = "http://api.currencylayer.com";
	private static final String API_KEY = "2e4baadf5c5ae6ba436f53ae5558107f";
	private static final String SOURCE = "USD";
	private static final String[] CURRENCIES = {"EUR","CHF","CAD"};

	public static void main(String[] args) {
		
		
		Transakcija t = new Transakcija();
		t.setPocetniIznos(100);
		Date date = new GregorianCalendar(2020,Calendar.SEPTEMBER,4).getTime();	
		t.setDatumTransakcije(date);
		t.setIzvornaValuta(SOURCE);
		
		Format f = new SimpleDateFormat("yyyy-MM-dd");
		String dat = f.format(t.getDatumTransakcije());

		try (FileWriter file = new FileWriter("ostale_transakcije.json")){
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			for (String currency : CURRENCIES) {
				t.setKrajnjaValuta(currency);
				URL url = new URL(
						BASE_URL + "/historical?date="+dat+"&access_key=" + API_KEY + "&source=" + SOURCE + "&currencies=" + currency);
			
				
				HttpURLConnection con = (HttpURLConnection) url.openConnection();

				con.setRequestMethod("GET");

				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

				JsonObject rez = gson.fromJson(reader, JsonObject.class);

				if (rez.get("success").getAsBoolean()) {
					double kurs = rez.get("quotes").getAsJsonObject().get("USD"+currency).getAsDouble();
					t.setKonvertovaniIznos(t.getPocetniIznos()*kurs);
					System.out.println(kurs);
					System.out.println(t);
					gson.toJson(t, file);
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
