import app.common.net.ActionRequest;
import app.common.net.LoginRequest;
import app.common.net.Message;

public class Main {

	public static void main(String[] args)
	{
		LoginRequest lm = new LoginRequest("Nicola", "Nicola");
		String json = lm.toJson();
		System.out.println(json);
		LoginRequest m = (LoginRequest)Message.fromJson(json);
		if(m.getMessageType() == ActionRequest.LOGIN) {
			LoginRequest lm1 = (LoginRequest)LoginRequest.fromJson(json, LoginRequest.class);
			System.out.println(lm1.getUsername() + " " + lm1.getPassword() + " " + lm1.getMessageType());
		}
	}

}
