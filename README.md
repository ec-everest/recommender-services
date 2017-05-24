# recommender-services

For a complete complete description and deployment of this tools and other research objects components visit http://everest.expertsystemlab.com

The recommender system suggests research objects that might be of interest according to user’s research interests. The recommender system follows a content-based approach in the sense that it compares the research object content with the user interest to draw the list of recommended items. 

The recommender system has a rest API and a web user interface called Collaboration Spheres. The collaboration Spheres eases the creation of the user interests, by enabling drag and drop of research objects and scientist to the user interests, and allows to visualize the suggested research object in a very intuitive way. 

## Installation
The recommender service is is fully implemented in Java 8 and uses maven 3 to manage the libraries dependencies and generate the jar and war files that are going to be deployed. The recommender service is implemented as a rest api using the framework Jersey 2 that can be deployed on any servlet container supporting Servlet 2.5 and higher, such as Tomcat 8.  

The recommender service strongly depends on the solr index populated with the semantic enrichment service. Therefore it is necessary to make sure that the solr index is correctly installed.

To install the recommender service first clone this repository using `git clone`

Then use `maven install` to compile the recommender service in the root folder. This command generates different jar files and war files. 

The file `EverEstSpheres.war`, that contains the recommender web service, in the target folder of the module `/everest-github-recommender-webservices` must be deployed in the servlet container in the path `/EverEstSpheres`. 

On the other hand, the Collaboration Spheres component is implemented in html and the java script library Polymer. Therefore the collaboration spheres could be potentially installed in any web server. Anyway, the recommendation is to install it in servlet container as in the previous cases. Before installation be sure to install the bower_components in the folder with the same name in `/everest-github-recommender-spheres`

To install the spheres the file `spheres.war`, that contains the collaboration spheres code, in the folder target of the module `/everest-github-recommender-spheres` must be deployed in the servlet container in the path `/spheres`.


## Web API
The recommender service rest api accepts post request of authenticated users and returns a json document with the list of research objects that make up the recommendation. The service is currently deployed in: `http://everest.expertsystemlab.com/EverEstSpheres/services/jsonservices/api`. The user requesting the recommendation is specified by means of the authorization header with value “bearer access-token” where access-token is the token returned by the EVER-EST authentication server. 

To include research objects or scientist in the recommendation context the service accepts a json document of the form `{“ros”:[“uri-1”,...], “scientists”:[“uri-2”,...]}` where the element “ros” is an array containing the list of uris corresponding to the research objects that will be added to the recommendation context and the element scientist is an array containing the list of uris corresponding to the users that will be added to the recommendation context. To be consistent with definition of context in the collaboration spheres a maximum of three uris, either research objects, users or a combination of both, can be added to the recommendation context.  Below an example of how the service can be call and the results that it provides is presented:

``` POST /EverEstSpheres/services/jsonservices/api HTTP/1.1
HOST: everest.expertsystemlab.com
authorization: Bearer a3a0224a-2f34-415a-b0a8-0969e2129a38
content-type: application/json
accept: application/json
message body:
{
	"ros": ["http://sandbox.rohub.org/rodl/ROs/cnr-bibliographic-resource-135/",
		"http://sandbox.rohub.org/rodl/ROs/HD_chromatin_analysis/"
	],
	"scientists": ["http://ffoglini.livejournal.com/"]
}
``` 

The result is a json document with the recommended research objects:
``` 
{
	"results": ["http://sandbox.rohub.org/rodl/ROs/bibliographictest01-1/",
		"http://sandbox.rohub.org/rodl/ROs/SeaMonitoring01/",
		"http://sandbox.rohub.org/rodl/ROs/SeaMonitoring02-snapshot/",
		"http://sandbox.rohub.org/rodl/ROs/SeaMonitoring_03-snapshot/",
		"http://sandbox.rohub.org/rodl/ROs/HD_chromatin_analysis-snapshot-1/"
	]
}
``` 
