package net.expertsystem.lab.everest.collaborationspheresRO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthClientResponse;
import org.apache.oltu.oauth2.client.response.OAuthClientResponseFactory;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;


import net.expertsystem.spheres.oauth2.controller.Utils;
import net.expertsystem.spheres.oauth2.model.OAuthParams;

@Path("/jsonservices")
public class JSONServices {
	static final String clientId="q61M3Dj6czDXSdOmWI5btwI3pyka";
	static final String clientSecret="F6x2YxfzjGO8i9eTP0oR5APhinMa";
	private Logger logger = LogManager.getLogger(JSONServices.class);
	@POST
	@Consumes("application/json")
	public Response recommendation(final SpheresInput spheresInput) throws JSONException, SolrServerException, IOException {
		List <ResearchObject> ros_context = new ArrayList<ResearchObject>();
		List <Scientist> scientists_context = new ArrayList<Scientist>();
		String [] types = spheresInput.getTypes();
		String [] urls = spheresInput.getUrls();
		String iduser = spheresInput.getIduser();
		String json = "";
		if(types[0].equals("default")){
			json = "{ " +
					"\"name\": \"Showcase Spheres\"," +
					"\"type\": \"http://showcase.xlime.eu/sphere\"," +
					"\"uri\": \"http://showcase.xlime.eu/spheres\"," +
					"\"inner\": []," +
					"\"inter\": []," +
					"\"outer\": []}" ;
		}
		else{
			for (int i = 0; i < types.length; i++){
				if (types[i].equals("ResearchObject")){
					ResearchObject ro = new ResearchObject(urls[i]);
					ResultsGenerator.roSetter(ro);
					ros_context.add(ro);
				}
				if (types[i].equals("Scientist")){
					Scientist scientist = new Scientist(urls[i]);
					List<ResearchObject>list_of_ros_by_author = ListCreator.results_list_ros_by_author(urls[i]);
					scientist.setListRO(list_of_ros_by_author);
					String name = ResultsGenerator.scientistNamer(scientist);
					scientist.setName(name);
					scientists_context.add(scientist);
				}
			}
			String input = ResultsGenerator.queryGenerator(ros_context,scientists_context);
			List <ResearchObject> results = ResultsGenerator.results(input,ros_context,scientists_context, iduser);
			json = JSONcreator.creator(ros_context,scientists_context, results);
		}

		return Response.status(200).entity(json).build();
	}
	
	@Path("/myrolist")
	@POST
	@Consumes("application/json")
	public Response listado_filtrado(final CreatorInput creatorInput) throws SolrServerException, IOException{
		List <ResearchObject> list_of_ros = ListCreator.results_myros(creatorInput.getCreator());
		String json = "[";
		for (ResearchObject ro : list_of_ros){
			if ((ro.getTitle() != null && !ro.getTitle().matches("(?i:.*"+creatorInput.getSearch()+".*)"))||
				(ro.getTitle() == null && !ro.getId().matches("(?i:.*"+creatorInput.getSearch()+".*)"))){
				continue;
			}
			json=json + 
					"{\"@type\": \"ResearchObject\"," +
	                "\"url\": \"" + ro.getId() + "\"" +
	                JSONcreator.hay_title(ro) +
	                JSONcreator.hay_sketch(ro) +
	                JSONcreator.hay_description(ro) +
	                JSONcreator.hay_created(ro)+
	                JSONcreator.hay_author(ro) +
	                JSONcreator.hay_concepts(ro)+
	                JSONcreator.hay_domains(ro)+
	                "},";
		}
		if (!json.equals("[")){
			json=json.substring(0, json.length()-1);
		}
		json=json + "]";
		return Response.status(200).entity(json).build();
	}
	
	@Path("/rolist")
	@GET
	@Produces("application/json")
	public Response listado() throws SolrServerException, IOException{
		List <ResearchObject> list = ListCreator.results();
		String json = "[";
		for (ResearchObject ro : list){
			json=json + 
					"{\"@type\": \"ResearchObject\"," +
	                "\"url\": \"" + ro.getId() + "\"" +
	                JSONcreator.hay_title(ro) +
	                JSONcreator.hay_sketch(ro) +
	                JSONcreator.hay_description(ro) +
	                JSONcreator.hay_created(ro)+
	                JSONcreator.hay_author(ro) +
	                JSONcreator.hay_concepts(ro)+
	                JSONcreator.hay_domains(ro)+
	                "},";
		}
		json=json.substring(0, json.length()-1);
		json=json + "]";
		return Response.status(200).entity(json).build();
	}
	
	@Path("/rolist/{search}")
	@GET
	@Produces("application/json")
	public Response listado_filtrado(@PathParam("search") String search) throws SolrServerException, IOException{
		List <ResearchObject> list_of_ros = ListCreator.results();
		String json = "[";
		for (ResearchObject ro : list_of_ros){
			if ((ro.getTitle() != null && !ro.getTitle().matches("(?i:.*"+search+".*)"))||
				(ro.getTitle() == null && !ro.getId().matches("(?i:.*"+search+".*)"))){
				continue;
			}
			json=json + 
					"{\"@type\": \"ResearchObject\"," +
	                "\"url\": \"" + ro.getId() + "\"" +
	                JSONcreator.hay_title(ro) +
	                JSONcreator.hay_sketch(ro) +
	                JSONcreator.hay_description(ro) +
	                JSONcreator.hay_created(ro)+
	                JSONcreator.hay_author(ro) +
	                JSONcreator.hay_concepts(ro)+
	                JSONcreator.hay_domains(ro)+
	                "},";
		}
		if (!json.equals("[")){
			json=json.substring(0, json.length()-1);
		}
		json=json + "]";
		return Response.status(200).entity(json).build();
	}
	
	@Path("/related")
	@POST
	@Consumes("application/json")
	public Response listado_related(final CreatorInput creatorInput) throws SolrServerException, IOException{
		String json = "[";
		List <ResearchObject> list_ros_by_author = ListCreator.results_list_ros_by_author(creatorInput.getCreator());
		Scientist scientist = new Scientist(creatorInput.getCreator());
		scientist.setListRO(list_ros_by_author);
		List <Scientist> list = ListCreator.related(scientist);
		for (Scientist scientist2 : list){
			if ((!scientist2.getName().matches("(?i:.*"+creatorInput.getSearch()+".*)"))||(scientist2.getId().equals(creatorInput.getCreator()))){
				continue;
			}
			json=json + 
					"{\"@type\": \"Scientist\"," +
					"\"url\": \"" + scientist2.getId() + "\"," +
	                "\"value\": \"" + scientist2.getName() + "\"" +
					JSONcreator.hay_ROs(scientist2) +
	                "},";
		}
		if (!json.equals("[")){
			json=json.substring(0, json.length()-1);
		}
		json=json + "]";
		return Response.status(200).entity(json).build();
	}
	
	@Path("/authorlist")
	@GET
	@Produces("application/json")
	public Response listado_scientist() throws SolrServerException, IOException{
		List <Scientist> list = ListCreator.results_authors();
		String json = "[";
		for (Scientist author : list){
			json=json + 
					"{\"@type\": \"Scientist\"," +
					"\"url\": \"" + author.getId() + "\"," +
	                "\"value\": \"" + author.getName() + "\"" +
	                JSONcreator.hay_ROs(author) +
	                "},";
		}
		json=json.substring(0, json.length()-1);
		json=json + "]";
		return Response.status(200).entity(json).build();
	}
	
	@Path("/authorlist/{search}")
	@GET
	@Produces("application/json")
	public Response listado_scientific_filtrado(@PathParam("search") String search) throws SolrServerException, IOException{
		List <Scientist> list = ListCreator.results_authors();
		String json = "[";
		for (Scientist author : list){
			if (!author.getName().matches("(?i:.*"+search+".*)")){
				continue;
			}
			json=json + 
					"{\"@type\": \"Scientist\"," +
					"\"url\": \"" + author.getId() + "\"," +
	                "\"value\": \"" + author.getName() + "\"" +
	                JSONcreator.hay_ROs(author) +
	                "},";
		}
		if (!json.equals("[")){
			json=json.substring(0, json.length()-1);
		}
		json=json + "]";
		return Response.status(200).entity(json).build();
	}
	@Path("/api")
	@POST
	@Consumes("application/json")
	public Response api(final APIInput apiInput, @Context HttpHeaders headers) throws SolrServerException, IOException, URISyntaxException{
		String accessToken = headers.getHeaderString("Authorization");
		String[] ros = apiInput.getRos();
		String[] scientists = apiInput.getScientists();
		int tam_ros = 0;
		int tam_scientists = 0;
		if (ros != null){tam_ros = ros.length;}
		if (scientists != null){tam_scientists = scientists.length;}
		String iduser = "";
		
		if (tam_ros + tam_scientists == 0){
			return Response.status(400).entity("ERROR: there's no objects in the context of the recommendation").build();
		}
	
		if (tam_ros + tam_scientists > 3){
			return Response.status(400).entity("ERROR: too much objects in the context of the recommendation").build();
		}
		
		String uri_whoami = "http://sandbox.rohub.org/rodl/whoami/" ;
		HttpClient httpclient = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder(uri_whoami);
		URI uri = builder.build();		
		HttpGet request = new HttpGet(uri);
		request.setHeader("accept", "text/turtle");
		request.setHeader("authorization", accessToken);
		HttpResponse response = httpclient.execute(request);

		HttpEntity entity = response.getEntity();
		String modelText = EntityUtils.toString(entity);
		if(response.getStatusLine().getStatusCode() != 401){
			Model model = ModelFactory.createDefaultModel();
			model.read(new ByteArrayInputStream(modelText.getBytes()),null,"TTL");

			StmtIterator it = model.listStatements();
			while (it.hasNext()) {
				Statement stmt      = it.nextStatement();  // get next statement
				RDFNode object    = stmt.getObject();      // get the object

				if (object.toString().equals("http://xmlns.com/foaf/0.1/Agent")){
					iduser = stmt.getSubject().getURI();
				}
			}
		}
		if (iduser.isEmpty()){
			return Response.status(400).entity("ERROR: invalid access token or user").build();
		}
		//if research object no existe
		//if scientist no existe
		
		
		List <ResearchObject> ros_context = new ArrayList<ResearchObject>();
		List <Scientist> scientists_context = new ArrayList<Scientist>();
		String json = "";
		if (ros != null){
			for (int i = 0; i < ros.length; i++){
				ResearchObject ro = new ResearchObject(ros[i]);
				ResultsGenerator.roSetter(ro);
				if (ro.getCreated() == null){
					return Response.status(400).entity("ERROR: one or more Research Objects are invalid").build();
				}
				ros_context.add(ro);
			}
		}
		if (scientists != null){
			for (int i = 0; i < scientists.length; i++){
				Scientist scientist = new Scientist(scientists[i]);
				List<ResearchObject>list_of_ros_by_author = ListCreator.results_list_ros_by_author(scientists[i]);
				scientist.setListRO(list_of_ros_by_author);
				String name = ResultsGenerator.scientistNamer(scientist);
				scientist.setName(name);
				if (scientist.getName().isEmpty()){
					return Response.status(400).entity("ERROR: one or more Scientists are invalid").build();
				}
				scientists_context.add(scientist);
			}
		}
		String input = ResultsGenerator.queryGenerator(ros_context,scientists_context);		
		List <ResearchObject> results = ResultsGenerator.results(input,ros_context,scientists_context, iduser);
		for (ResearchObject result : results){
			json = json + result.getId() + "\n";
		}
		return Response.status(200).entity(json).build();
	}
	
}
