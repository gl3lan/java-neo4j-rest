package my.test.neo4j;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Request {
	private static final String SERVER_ROOT_URI = RestAPI.SERVER_ROOT_URI;

	public enum Method {
		PUT ("PUT"),
		GET ("GET"),
		DELETE ("DELETE"),
		POST ("POST");

		private String name = "";

		//Constructeur
		Method(String name){
			this.name = name;
		}

		public String toString(){
			return name;
		}
	}

	protected Method method;
	protected String URISuffix;
	protected String body;

	public Request(Method method, String uRISuffix, String body) {
		super();
		this.method = method;
		URISuffix = uRISuffix;
		this.body = body;
	}
	public String getURISuffix() {
		return URISuffix;
	}
	public void setURISuffix(String uRISuffix) {
		URISuffix = uRISuffix;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}


	public URI execute() {
		WebResource resource = Client.create()
				.resource( SERVER_ROOT_URI + this.URISuffix );
		ClientResponse response;
		switch (this.method)
		{
		case POST:
			// POST {} to the node entry point URI
			response = resource.accept( MediaType.APPLICATION_JSON )
			.type( MediaType.APPLICATION_JSON )
			.entity( this.body )
			.post( ClientResponse.class );
			final URI location = response.getLocation();
			System.out.println( String.format(
					"POST to [%s], status code [%d], location header [%s]",
					SERVER_ROOT_URI + this.URISuffix, response.getStatus(), location.toString() ) );
			response.close();
			return location;
		case PUT:
			response = resource.accept( MediaType.APPLICATION_JSON )
			.type( MediaType.APPLICATION_JSON )
			.entity( this.body )
			.put( ClientResponse.class );
			System.out.println( String.format( "PUT to [%s], status code [%d]",
					SERVER_ROOT_URI + this.URISuffix, response.getStatus() ) );
			response.close();
		default:
			return null;
		}


	}


}
