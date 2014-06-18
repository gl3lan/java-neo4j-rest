package my.test.neo4j;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import my.test.neo4j.Request.Method;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestAPI
{
    public static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";

    public static void main( String[] args ) throws URISyntaxException
    {
        checkDatabaseIsRunning();

        // START SNIPPET: nodesAndProps
        URI firstNode = createNode();
        addProperty( firstNode, "name", "Joe Strummer" );
        URI secondNode = createNode();
        addProperty( secondNode, "band", "The Clash" );
        // END SNIPPET: nodesAndProps

        // START SNIPPET: addRel
        URI relationshipUri = addRelationship( firstNode, secondNode, "singer",
                "{ \"from\" : \"1976\", \"until\" : \"1986\" }" );
        // END SNIPPET: addRel

        // START SNIPPET: addMetaToRel
//        addMetadataToProperty( relationshipUri, "stars", "5" );
        // END SNIPPET: addMetaToRel

        // START SNIPPET: queryForSingers
//        findSingersInBands( firstNode );
        // END SNIPPET: queryForSingers

        sendTransactionalCypherQuery( "MATCH (n) WHERE has(n.name) RETURN n.name AS name" );
    }

    private static void sendTransactionalCypherQuery(String query) {
        // START SNIPPET: queryAllNodes
        final String txUri = SERVER_ROOT_URI + "transaction/commit";
        WebResource resource = Client.create().resource( txUri );

        String payload = "{\"statements\" : [ {\"statement\" : \"" +query + "\"} ]}";
        ClientResponse response = resource
                .accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( payload )
                .post( ClientResponse.class );
        
        System.out.println( String.format(
                "POST [%s] to [%s], status code [%d], returned data: "
                        + System.getProperty( "line.separator" ) + "%s",
                payload, txUri, response.getStatus(),
                response.getEntity( String.class ) ) );
        
        response.close();
        // END SNIPPET: queryAllNodes
    }

//    private static void findSingersInBands( URI startNode )
//            throws URISyntaxException
//    {
//        // START SNIPPET: traversalDesc
//        // TraversalDefinition turns into JSON to send to the Server
//        TraversalDefinition t = new TraversalDefinition();
//        t.setOrder( TraversalDefinition.DEPTH_FIRST );
//        t.setUniqueness( TraversalDefinition.NODE );
//        t.setMaxDepth( 10 );
//        t.setReturnFilter( TraversalDefinition.ALL );
//        t.setRelationships( new Relation( "singer", Relation.OUT ) );
//        // END SNIPPET: traversalDesc
//
//        // START SNIPPET: traverse
//        URI traverserUri = new URI( startNode.toString() + "/traverse/node" );
//        WebResource resource = Client.create()
//                .resource( traverserUri );
//        String jsonTraverserPayload = t.toJson();
//        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
//                .type( MediaType.APPLICATION_JSON )
//                .entity( jsonTraverserPayload )
//                .post( ClientResponse.class );
//
//        System.out.println( String.format(
//                "POST [%s] to [%s], status code [%d], returned data: "
//                        + System.getProperty( "line.separator" ) + "%s",
//                jsonTraverserPayload, traverserUri, response.getStatus(),
//                response.getEntity( String.class ) ) );
//        response.close();
//        // END SNIPPET: traverse
//    }

    // START SNIPPET: insideAddMetaToProp
    private static void addMetadataToProperty( URI relationshipUri,
            String name, String value ) throws URISyntaxException
    {
        URI propertyUri = new URI( relationshipUri.toString() + "/properties" );
        String entity = toJsonNameValuePairCollection( name, value );
        WebResource resource = Client.create()
                .resource( propertyUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( entity )
                .put( ClientResponse.class );

        System.out.println( String.format(
                "PUT [%s] to [%s], status code [%d]", entity, propertyUri,
                response.getStatus() ) );
        response.close();
    }

    // END SNIPPET: insideAddMetaToProp

    private static String toJsonNameValuePairCollection( String name,
            String value )
    {
        return String.format( "{ \"%s\" : \"%s\" }", name, value );
    }

    private static URI createNode()
    {
        Request request = new Request(Method.POST, "node", "{}");
        return request.execute();
    }

    // START SNIPPET: insideAddRel
    private static URI addRelationship( URI startNode, URI endNode,
            String relationshipType, String jsonAttributes )
            throws URISyntaxException
    {
        String fromUri = startNode.toString().replace(SERVER_ROOT_URI, "") + "/relationships" ;
        String relationshipJson = generateJsonRelationship( endNode,
                relationshipType, jsonAttributes );
        
        Request request = new Request(Method.POST, fromUri, relationshipJson);
        return request.execute();
    }
    // END SNIPPET: insideAddRel

    private static String generateJsonRelationship( URI endNode,
            String relationshipType, String... jsonAttributes )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "{ \"to\" : \"" );
        sb.append( endNode.toString() );
        sb.append( "\", " );

        sb.append( "\"type\" : \"" );
        sb.append( relationshipType );
        if ( jsonAttributes == null || jsonAttributes.length < 1 )
        {
            sb.append( "\"" );
        }
        else
        {
            sb.append( "\", \"data\" : " );
            for ( int i = 0; i < jsonAttributes.length; i++ )
            {
                sb.append( jsonAttributes[i] );
                if ( i < jsonAttributes.length - 1 )
                { // Miss off the final comma
                    sb.append( ", " );
                }
            }
        }

        sb.append( " }" );
        return sb.toString();
    }

    private static void addProperty( URI nodeUri, String propertyName,
            String propertyValue )
    {
        // START SNIPPET: addProp
        String propertyUri = nodeUri.toString().replace(SERVER_ROOT_URI, "") + "/properties/" + propertyName;
        // http://localhost:7474/db/data/node/{node_id}/properties/{property_name}
        Request request = new Request(Method.PUT, propertyUri,  "\"" + propertyValue + "\"");
        request.execute();
        // END SNIPPET: addProp
    }

    private static void checkDatabaseIsRunning()
    {
        // START SNIPPET: checkServer
        WebResource resource = Client.create()
                .resource( SERVER_ROOT_URI );
        ClientResponse response = resource.get( ClientResponse.class );

        System.out.println( String.format( "GET on [%s], status code [%d]",
                SERVER_ROOT_URI, response.getStatus() ) );
        response.close();
        // END SNIPPET: checkServer
    }
}