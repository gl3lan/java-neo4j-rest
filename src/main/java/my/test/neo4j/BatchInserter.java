package my.test.neo4j;

import java.util.LinkedList;
import java.util.List;

import my.test.neo4j.Request.Method;

public class BatchInserter {

	private String operationsSet;
	private StringBuilder builder;
	private List<Request> requestList;
	
	public BatchInserter() {
		builder = new StringBuilder(operationsSet);
		requestList = new LinkedList<Request>();
	}
	
	public void addRequest(Request aRequest) {
		requestList.add(aRequest);
	}
	
	public void execute()
	{
		for (Request request : requestList)
		{
			
		}
		Request batchRequest = new Request(Method.POST, "batch", operationsSet);
		batchRequest.execute();
	}
}
