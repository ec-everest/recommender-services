package net.expertsystem.lab.everest.collaborationspheresRO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
//import javax.servlet.http.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import net.expertsystem.spheres.oauth2.controller.Utils;
import net.expertsystem.spheres.oauth2.model.OAuthParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Path("/")
public class AuthenticateServices {
	
	private Logger logger = LogManager.getLogger(AuthenticateServices.class);
	static final String scope="openid profile";
	static final String authzEndpoint="https://sso.everest.psnc.pl/oauth2/authorize";
	static final String tokenEndpoint="https://sso.everest.psnc.pl/oauth2/token";
	static final String clientId="q61M3Dj6czDXSdOmWI5btwI3pyka";
	static final String clientSecret="F6x2YxfzjGO8i9eTP0oR5APhinMa";
	static final String redirectUri="http://everest.expertsystemlab.com/EverEstSpheres/services/callback";
	
	
	
	@Path("/authorize")
	@GET
	@Produces("text/html")
	public Response authorize()
    throws ServletException, IOException
	{
		OAuthParams params=new OAuthParams();
		params.setScope("openid profile");
		params.setAuthzEndpoint("https://sso.everest.psnc.pl/oauth2/authorize");
		params.setTokenEndpoint("https://sso.everest.psnc.pl/oauth2/token");
		params.setClientId("q61M3Dj6czDXSdOmWI5btwI3pyka");
		params.setClientSecret("F6x2YxfzjGO8i9eTP0oR5APhinMa");
		params.setRedirectUri("http://everest.expertsystemlab.com/EverEstSpheres/services/callback");
	
						
		try {
	    logger.debug("start processing /authorize request");
         
        OAuthClientRequest request = OAuthClientRequest
            .authorizationLocation(params.getAuthzEndpoint())
            .setClientId(params.getClientId())
            .setRedirectURI(params.getRedirectUri())
            .setResponseType(ResponseType.CODE.toString())
            .setScope(params.getScope())
            .setState(params.getState())
            .buildQueryMessage();

            logger.debug("loc uri {}",request.getLocationUri());
            return Response.seeOther(URI.create(request.getLocationUri())).		
//            		 cookie(new NewCookie("clientId", params.getClientId(),"/","everest.expertsystemlab.com/spheres/index.html","",3600,false)).
//					 cookie(new NewCookie("clientId", params.getClientId(),"/","everest.expertsystemlab.com/spheres/index.html","",0,false)).        						 
//					 cookie(new NewCookie("clientSecret", params.getClientSecret(),"/","everest.expertsystemlab.com/spheres/index.html","",0,false)).
//					 cookie(new NewCookie("authzEndpoint", params.getAuthzEndpoint(),"/","everest.expertsystemlab.com/spheres/index.html","",0,false)).
//					 cookie(new NewCookie("tokenEndpoint", params.getTokenEndpoint(),"/","everest.expertsystemlab.com/spheres/index.html","",0,false)).
//					 cookie(new NewCookie("redirectUri", params.getRedirectUri(),"/","everest.expertsystemlab.com/spheres/index.html","",0,false)).
//					 cookie(new NewCookie("scope", params.getScope(),"/","everest.expertsystemlab.com/spheres/index.html","",0,false)).
//					 cookie(new NewCookie("state", params.getState(),"/","everest.expertsystemlab.com/spheres/index.html","",0,false)).
//					 cookie(new NewCookie("app", params.getApplication(),"/","everest.expertsystemlab.com/spheres/index.html","",0,false)).
					 build();
		} catch (OAuthSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.serverError().entity(e.getStackTrace()).build();
		}		
	}
	
	@Path("/callback")
	@GET
	@Produces("text/html")
	public Response callback(@Context HttpHeaders headers, @Context HttpServletRequest httpRequest)
    throws ServletException, IOException, OAuthSystemException{
		  logger.debug("creating OAuth authorization response wrapper (/callback)");
		  OAuthParams oauthParams = new OAuthParams();  		  
	      try {
	            // Get OAuth Info
//	            String clientId = Utils.findCookieValue(headers.getCookies(), "clientId");
//	            String clientSecret = Utils.findCookieValue(headers.getCookies(), "clientSecret");
//	            String authzEndpoint = Utils.findCookieValue(headers.getCookies(), "authzEndpoint");
//	            String tokenEndpoint = Utils.findCookieValue(headers.getCookies(), "tokenEndpoint");
//	            String redirectUri = Utils.findCookieValue(headers.getCookies(), "redirectUri");
//	            String scope = Utils.findCookieValue(headers.getCookies(), "scope");
//	            String state = Utils.findCookieValue(headers.getCookies(), "state");
	           
	            oauthParams.setClientId(clientId);
	            oauthParams.setClientSecret(clientSecret);
	            oauthParams.setAuthzEndpoint(authzEndpoint);
	            oauthParams.setTokenEndpoint(tokenEndpoint);
	            oauthParams.setRedirectUri(redirectUri);
	            oauthParams.setScope(Utils.isIssued(scope));
//	            oauthParams.setState(Utils.isIssued(state));		
	        
	            // Create the response wrapper
	            OAuthAuthzResponse oar = null;
	            oar = OAuthAuthzResponse.oauthCodeAuthzResponse(httpRequest);

	            // Get Authorization Code
	            String code = oar.getCode();
	            oauthParams.setAuthzCode(code);

//	            String app = Utils.findCookieValue(headers.getCookies(), "app");	            
//	            oauthParams.setApplication(app);
	            
	            return requestToken(oauthParams, httpRequest);
//	            return Response.seeOther(URI.create("/request_token")).
//        		 		cookie(new NewCookie("app", app)).
//        		 		build();	

	        } catch (OAuthProblemException e) {
	            logger.error("failed to create OAuth authorization response wrapper", e);
	            StringBuffer sb = new StringBuffer();
	            sb.append("<br />");
	            sb.append("Error code: ").append(e.getError()).append("<br />");
	            sb.append("Error description: ").append(e.getDescription()).append("<br />");
	            sb.append("Error uri: ").append(e.getUri()).append("<br />");
	            sb.append("State: ").append(e.getState()).append("<br />");
	            oauthParams.setErrorMessage(sb.toString());
	            return Response.seeOther(URI.create("authorize")).build();
	        }	                   	
	}
	

	public Response requestToken(OAuthParams oauthParams, HttpServletRequest httpRequest)
    throws ServletException, IOException, OAuthSystemException{
		  logger.debug("requesting token... ");		  	
		  try {	            
	            OAuthClientRequest request = OAuthClientRequest
	                .tokenLocation(oauthParams.getTokenEndpoint())
	                .setClientId(oauthParams.getClientId())
	                .setClientSecret(oauthParams.getClientSecret())
	                .setRedirectURI(oauthParams.getRedirectUri())
	                .setCode(oauthParams.getAuthzCode())
	                .setGrantType(GrantType.AUTHORIZATION_CODE)
	                .buildBodyMessage();

	            OAuthClient client = new OAuthClient(new URLConnectionClient());	            
	            Class<? extends OAuthAccessTokenResponse> cl = OAuthJSONAccessTokenResponse.class;	      
	            OAuthAccessTokenResponse oauthResponse = client.accessToken(request, cl);

	            oauthParams.setAccessToken(oauthResponse.getAccessToken());
	            oauthParams.setExpiresIn(oauthResponse.getExpiresIn());
	            oauthParams.setRefreshToken(Utils.isIssued(oauthResponse.getRefreshToken()));
	            
	            httpRequest.getSession().setAttribute("Logged in", true);

	            logger.debug("acces token gotten: {}",oauthParams.getAccessToken());
	            //GET rohub user-url
	            //http://everest.expertsystemlab.com
	            return Response.seeOther(URI.create("../../spheres/index.html")).
        		 		cookie(new NewCookie("accessToken",oauthParams.getAccessToken()/*,"/","everest","",3600,false*/)).
        		 		cookie(new NewCookie("refreshToken", oauthParams.getRefreshToken()/*,"/","everest","",3600,false*/)).
        		 		build();	
	     
	        } catch (OAuthProblemException e) {
	            logger.error("failed to acquire OAuth access token", e);
	            StringBuffer sb = new StringBuffer();
	            sb.append("<br />");
	            sb.append("Error code: ").append(e.getError()).append("<br />");
	            sb.append("Error description: ").append(e.getDescription()).append("<br />");
	            sb.append("Error uri: ").append(e.getUri()).append("<br />");
	            sb.append("State: ").append(e.getState()).append("<br />");
	            oauthParams.setErrorMessage(sb.toString());
	            return Response.seeOther(URI.create("../authorize")).build();
	        }
	}
	
	/*@Path("/getUserId")
	@GET
	@Produces("application/json")
	public Response getUserId(@Context HttpHeaders headers, @Context HttpServletRequest httpRequest)
			throws ServletException, IOException, OAuthSystemException, OAuthProblemException, URISyntaxException{
		logger.debug("getting user id");
	
		String json= "";
		String accessToken = Utils.findCookieValue(headers.getCookies(), "accessToken");
		if (accessToken != null){
			String uri_whoami = "http://sandbox.rohub.org/rodl/whoami/" ;
			HttpClient httpclient = HttpClients.createDefault();
			URIBuilder builder = new URIBuilder(uri_whoami);
			URI uri = builder.build();		
			HttpGet request = new HttpGet(uri);
			request.setHeader("accept", "text/turtle");
			request.setHeader("authorization", "Bearer " + accessToken);

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
						json = stmt.getSubject().getURI();
					}
				}
			}
		}
		logger.debug(json);
		return Response.status(200).entity(json).build();
	}*/
	
	@Path("/cleanCookies")
	@GET
	@Produces("application/json")
	public Response cleanCookies(@Context HttpHeaders headers, @Context HttpServletRequest httpRequest)
			throws ServletException, IOException, OAuthSystemException, OAuthProblemException, URISyntaxException{
		
		String uri_logout="https://sso.everest.psnc.pl/commonauth?commonAuthLogout=true&type=oidc&commonAuthCallerPath=http://everest.expertsystemlab.com/EverEstSpheres/services/callback&relyingParty=Collaboration_Spheres";
		return Response.seeOther(URI.create(uri_logout)).cookie(new NewCookie("accessToken", null)).build();
	}
	@Path("/getUser")
	@GET
	public Response getUser(@Context HttpHeaders headers) throws SolrServerException, IOException, URISyntaxException, JAXBException, OAuthSystemException{
		String json ="";
		logger.debug("Getting the label and the id of the user...");
		String accessToken = Utils.findCookieValue(headers.getCookies(), "accessToken");
		HttpClient httpclient = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder("https://sso.everest.psnc.pl/oauth2/userinfo");
		URI uri = builder.build();		
		HttpGet request = new HttpGet(uri);
		request.setHeader("content-type", "application/json");
		request.setHeader("authorization", "Bearer " + accessToken);
		HttpResponse response = httpclient.execute(request);
		logger.debug(response.getStatusLine().getStatusCode());
		logger.debug(accessToken);
		if(response.getStatusLine().getStatusCode() == 401){   //token caducado
			logger.debug("token caducado");
			String refreshToken = Utils.findCookieValue(headers.getCookies(), "refreshToken");
			RefreshToken rt = refreshing(refreshToken);
			logger.debug(rt.toString());
			return Response.seeOther(URI.create("http://everest.expertsystemlab.com/EverEstSpheres/services/getUser")).
					cookie(new NewCookie("accessToken", rt.getAccess_token())).
					cookie(new NewCookie("refreshToken", rt.getRefresh_token())).build();
		}
		else{
			HttpEntity entity = response.getEntity();
			InputStream input = entity.getContent();

			System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
			
			JAXBContext jc = JAXBContext.newInstance(UserProfile.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			unmarshaller.setProperty("eclipselink.media-type", "application/json");
			unmarshaller.setProperty("eclipselink.json.include-root", false);
			JAXBElement <UserProfile> element = unmarshaller.unmarshal(new StreamSource(input),UserProfile.class);
			UserProfile up = element.getValue();

			json = up.getGiven_name() + " " + up.getFamily_name();
			byte[] textBytes = json.getBytes("Windows-1252");
			json = "{\"label\":\"" + new String(textBytes , "UTF-8") + "\", ";
		
			String uri_whoami = "http://sandbox.rohub.org/rodl/whoami/" ;
			HttpClient httpclient2 = HttpClients.createDefault();
			URIBuilder builder2 = new URIBuilder(uri_whoami);
			URI uri2 = builder2.build();		
			HttpGet request2 = new HttpGet(uri2);
			request2.setHeader("accept", "text/turtle");
			request2.setHeader("authorization", "Bearer " + accessToken);

			HttpResponse response2 = httpclient2.execute(request2);

			HttpEntity entity2 = response2.getEntity();
			String modelText = EntityUtils.toString(entity2);

			if(response2.getStatusLine().getStatusCode() != 401){
				Model model = ModelFactory.createDefaultModel();
				model.read(new ByteArrayInputStream(modelText.getBytes()),null,"TTL");

				StmtIterator it = model.listStatements();
				while (it.hasNext()) {
					Statement stmt      = it.nextStatement();  // get next statement
					RDFNode object    = stmt.getObject();      // get the object

					if (object.toString().equals("http://xmlns.com/foaf/0.1/Agent")){
						json = json + "\"id\":\"" + stmt.getSubject().getURI() + "\"}";
					}
				}
			}
		}
		return Response.status(200).entity(json).build();
		//}
	}
	RefreshToken refreshing(String refreshToken) throws OAuthSystemException, URISyntaxException, ClientProtocolException, IOException, JAXBException{
		logger.debug(refreshToken);


		OAuthClientRequest requestAux = OAuthClientRequest
				.tokenLocation("https://sso.everest.psnc.pl/oauth2/token")
				.setClientId(clientId)
				.setClientSecret(clientSecret)
				.setGrantType(GrantType.REFRESH_TOKEN)
				.setRefreshToken(refreshToken)
				.buildBodyMessage();


		HttpClient httpclientRefresh = HttpClients.createDefault();
		URIBuilder builderRefresh = new URIBuilder(requestAux.getLocationUri() + "?" + requestAux.getBody());
		logger.debug(requestAux.getLocationUri() + "?" + requestAux.getBody());
		URI uriRefresh = builderRefresh.build();
		HttpPost requestRefresh = new HttpPost(uriRefresh);
		requestRefresh.setHeader("content-type", "application/x-www-form-urlencoded");
		requestRefresh.setHeader("charset", "UTF-8");
		HttpResponse responseRefresh = httpclientRefresh.execute(requestRefresh);

		HttpEntity entity = responseRefresh.getEntity();
		InputStream input1 = entity.getContent();

		System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
		JAXBContext jc = JAXBContext.newInstance(RefreshToken.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");
		unmarshaller.setProperty("eclipselink.json.include-root", false);
		JAXBElement <RefreshToken> element = unmarshaller.unmarshal(new StreamSource(input1),RefreshToken.class);
		RefreshToken rt = element.getValue();
		return rt;
	}
}
