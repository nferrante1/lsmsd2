package app.client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import app.client.config.Configuration;

public class Protocol implements AutoCloseable
{
	private Socket socket = null;
	private DataInputStream inputStream = null;
	private DataOutputStream outputStream = null;
	private static Protocol instance = null;

	private Protocol() throws IOException
	{
		Configuration config = Configuration.getConfig();
		this.socket = new Socket(config.getServerIp(), config.getServerPort());
		inputStream = new DataInputStream(socket.getInputStream());
		outputStream = new DataOutputStream(socket.getOutputStream());
		Logger.getLogger(Protocol.class.getName()).info("Connected to " + config.getServerIp() + ":" + config.getServerPort() + ".");
	}

	public static Protocol getInstance()
	{
		if(instance == null)
			try {
				instance = new Protocol();
			} catch (IOException ex) {
				Logger.getLogger(Protocol.class.getName()).severe("Unhable to connect to server: " + ex.getMessage());
				System.exit(1);
			}
		return instance;
	}


	public ResponseMessage performLogin(User user)
	{
		return sendRequest(ActionRequest.LOGIN, user);
	}

	public ResponseMessage performLogout()
	{
		return sendRequest(ActionRequest.LOGOUT);
	}

	public ResponseMessage registerUser(Customer customer)
	{
		ResponseMessage resMsg = sendRequest(ActionRequest.REGISTER, customer);
		if (resMsg.isSuccess() && !(resMsg.getEntity() instanceof Customer))
			return getProtocolErrorMessage();
		return resMsg;
	}

	public ResponseMessage registerUser(Owner owner, Restaurant restaurant)
	{
		ResponseMessage resMsg = sendRequest(ActionRequest.REGISTER, owner, restaurant);
		if (resMsg.isSuccess() && !(resMsg.getEntity() instanceof Owner))
			return getProtocolErrorMessage();
		return resMsg;
	}

	public ResponseMessage getOwnRestaurant()
	{
		return sendRequest(ActionRequest.GET_OWN_RESTAURANT);
	}

	public ResponseMessage editRestaurant(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.EDIT_RESTAURANT, restaurant);
	}

	public ResponseMessage deleteRestaurant(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.DELETE_RESTAURANT, restaurant);
	}

	public ResponseMessage getOwnActiveReservations()
	{
		return sendRequest(ActionRequest.LIST_OWN_RESERVATIONS);
	}

	public ResponseMessage editReservation(Reservation reservation)
	{
		return sendRequest(ActionRequest.EDIT_RESERVATION, reservation);
	}

	public ResponseMessage getRestaurants()
	{
		return sendRequest(ActionRequest.LIST_RESTAURANTS);
	}

	public ResponseMessage getRestaurants(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.LIST_RESTAURANTS, restaurant);
	}

	public ResponseMessage reserve(Reservation reservation, Restaurant restaurant)
	{
		return sendRequest(ActionRequest.RESERVE, reservation, restaurant);
	}

	public ResponseMessage deleteReservation(Reservation reservation)
	{
		return sendRequest(ActionRequest.DELETE_RESERVATION, reservation);
	}

	public ResponseMessage getReservations(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.LIST_RESERVATIONS, restaurant);
	}

	public ResponseMessage checkSeats(Reservation reservation, Restaurant restaurant)
	{
		return sendRequest(ActionRequest.CHECK_SEATS, reservation, restaurant);
	}

	public ResponseMessage checkSeats(Reservation reservation)
	{
		return sendRequest(ActionRequest.CHECK_SEATS, reservation);
	}

	private ResponseMessage sendRequest(ActionRequest actionRequest, Entity... entities)
	{
		Logger.getLogger(Protocol.class.getName()).entering(Protocol.class.getName(), "sendRequest", entities);
		new RequestMessage(actionRequest, entities).send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		Logger.getLogger(Protocol.class.getName()).exiting(Protocol.class.getName(), "sendRequest", entities);
		return resMsg != null && resMsg.isValid(actionRequest) ? resMsg : getProtocolErrorMessage();
	}

	private ResponseMessage getProtocolErrorMessage()
	{
		Logger.getLogger(Protocol.class.getName()).warning("Received an invalid response from server.");
		return new ResponseMessage("Invalid response from server.");
	}

	public void close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}
