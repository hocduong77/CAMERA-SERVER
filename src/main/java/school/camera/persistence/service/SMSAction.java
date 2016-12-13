package school.camera.persistence.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class SMSAction {

	final String APIKey = "AB34AED6712E20576E2447E85DC019";
	final String SecretKey = "B557E42BC06F5C8B51600F749AF85D";
	private String message = "test";
	private String phone = "01699771699";

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String execute() {

		return "SUCCESS";

	}

	public static void main(String args[]) {
		SMSAction action = new SMSAction();
		try {
			action.sendGetJSON();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String sendGetJSON() throws IOException {

		String url = "http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?ApiKey="
				+ URLEncoder.encode(APIKey, "UTF-8") + "&SecretKey=" + URLEncoder.encode(SecretKey, "UTF-8")
				+ "&SmsType=8&Phone=" + URLEncoder.encode(phone, "UTF-8") + "&Content="
				+ URLEncoder.encode(message, "UTF-8");

		URL obj;
		try {
			obj = new URL(url);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// you need to encode ONLY the values of the parameters

			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			if (responseCode == 200) {
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			System.out.println("CodeResult=" + response.toString());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "SUCCESS";

	}

}
