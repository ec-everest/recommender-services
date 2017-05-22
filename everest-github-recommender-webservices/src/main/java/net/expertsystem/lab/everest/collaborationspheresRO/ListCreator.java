package net.expertsystem.lab.everest.collaborationspheresRO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class ListCreator {
	private static Logger logger = LogManager.getLogger(JSONServices.class);
	private static String urlSolr = "http://172.16.32.89:8983/solr/EverEst";
	public static List <ResearchObject> results() throws SolrServerException, IOException{
		String input = "*";

		SolrClient solr = new HttpSolrClient.Builder(urlSolr).build();
		SolrQuery query = new SolrQuery();

		List <ResearchObject> res = new ArrayList<ResearchObject>();

		query.setQuery(input);
		query.setFacet(true);
		query.setRows(700);
		query.add("group", "true");
		query.add("group.ngroups", "true");
		query.add("group.field","source_ro");
		query.addSort("title", ORDER.asc);

		QueryResponse rs = solr.query(query);
		List<Group> groupedData = rs.getGroupResponse().getValues().get(0).getValues();
		for (Group gp : groupedData){
			SolrDocumentList results_list = gp.getResult();
			for (SolrDocument results : results_list){
				ResearchObject ro = new ResearchObject(results.getFieldValue("id").toString());
				if(results.getFieldValue("title") != null){
					ro.setTitle(results.getFieldValue("title").toString());
				}
				if(results.getFieldValue("created") != null){
					ro.setCreated(results.getFieldValue("created").toString());
				}
				if(results.getFieldValue("author") != null){
					ro.setAuthor(results.getFieldValue("author").toString());
				}
				if(results.getFieldValue("description") != null){
					ro.setDescription(results.getFieldValue("description").toString());
				}
				if(results.getFieldValue("domains") != null){
					Collection<Object> list_aux_domains = results.getFieldValues("domains");
					List <String> list_aux2_domains = new ArrayList<String>();
					for(Object domain : list_aux_domains){
						list_aux2_domains.add(domain.toString());
					}
					ro.setDomains(list_aux2_domains);
				}
				if(results.getFieldValue("concepts") != null){
					Collection<Object> list_aux_concepts = results.getFieldValues("concepts");
					List <String> list_aux2_concepts = new ArrayList<String>();
					for(Object concept : list_aux_concepts){
						list_aux2_concepts.add(concept.toString());
					}
					ro.setConcepts(list_aux2_concepts);
				}
				if(results.getFieldValue("compound_terms") != null){
					Collection<Object> list_aux_expressions = results.getFieldValues("compound_terms");
					List <String> list_aux2_expressions = new ArrayList<String>();
					for(Object expression : list_aux_expressions){
						list_aux2_expressions.add(expression.toString());
					}
					ro.setExpressions(list_aux2_expressions);
				}
				if(results.getFieldValue("people") != null){
					Collection<Object> list_aux_people = results.getFieldValues("people");
					List <String> list_aux2_people = new ArrayList<String>();
					for(Object person : list_aux_people){
						list_aux2_people.add(person.toString());
					}
					ro.setPeople(list_aux2_people);
				}
				if(results.getFieldValue("place") != null){
					Collection<Object> list_aux_place = results.getFieldValues("place");
					List <String> list_aux2_place = new ArrayList<String>();
					for(Object place : list_aux_place){
						list_aux2_place.add(place.toString());
					}
					ro.setPlaces(list_aux2_place);
				}
				if(results.getFieldValue("organization") != null){
					Collection<Object> list_aux_org = results.getFieldValues("organization");
					List <String> list_aux2_org = new ArrayList<String>();
					for(Object org : list_aux_org){
						list_aux2_org.add(org.toString());
					}
					ro.setOrg(list_aux2_org);
				}
				if(results.getFieldValue("sketch") != null){
					ro.setSketch(results.getFieldValue("sketch").toString());
				}
				res.add(ro);
			}
		}
		return res;
	}
	public static List <Scientist> results_authors() throws SolrServerException, IOException{
		String input = "*";
		SolrClient solr = new HttpSolrClient.Builder(urlSolr).build();
		SolrQuery query = new SolrQuery();

		List <Scientist> res = new ArrayList<Scientist>();

		query.setQuery(input);
		query.setFacet(true);
		query.setRows(700);
		query.add("group", "true");
		query.add("group.ngroups", "true");
		query.add("group.field","source_ro");
		query.addSort("author", ORDER.desc);

		QueryResponse rs = solr.query(query);
		List<Group> groupedData = rs.getGroupResponse().getValues().get(0).getValues();
		for (Group gp : groupedData){
			SolrDocumentList results_list = gp.getResult();
			for (SolrDocument results : results_list){
				if(results.getFieldValue("author") != null){
					boolean incluido = false;
					for (Scientist ya_metidos:res){
						if(results.getFieldValue("creator").toString().equals(ya_metidos.getId())){
							incluido = true;
						}
					}
					if(!incluido){
						Scientist scientist = new Scientist(results.getFieldValue("creator").toString());
						scientist.setName(results.getFieldValue("author").toString());
						scientist.setListRO(ListCreator.results_list_ros_by_author(scientist.getId()));
						res.add(scientist);
					}
				}
			}
		}
		return res;
	}
	public static List <ResearchObject> results_myros(String creator) throws SolrServerException, IOException{
		String input = "creator:\"" + creator + "\"";
		SolrClient solr = new HttpSolrClient.Builder(urlSolr).build();
		SolrQuery query = new SolrQuery();

		List <ResearchObject> res = new ArrayList<ResearchObject>();

		query.setQuery(input);
		query.setFacet(true);
		query.setRows(700);
		query.add("group", "true");
		query.add("group.ngroups", "true");
		query.add("group.field","source_ro");
		query.addSort("title", ORDER.asc);

		QueryResponse rs = solr.query(query);
		List<Group> groupedData = rs.getGroupResponse().getValues().get(0).getValues();
		for (Group gp : groupedData){
			SolrDocumentList results_list = gp.getResult();
			for (SolrDocument results : results_list){
				ResearchObject ro = new ResearchObject(results.getFieldValue("id").toString());
				if(results.getFieldValue("title") != null){
					ro.setTitle(results.getFieldValue("title").toString());
				}
				if(results.getFieldValue("created") != null){
					ro.setCreated(results.getFieldValue("created").toString());
				}
				if(results.getFieldValue("author") != null){
					ro.setAuthor(results.getFieldValue("author").toString());
				}
				if(results.getFieldValue("description") != null){
					ro.setDescription(results.getFieldValue("description").toString());
				}
				if(results.getFieldValue("domains") != null){
					Collection<Object> list_aux_domains = results.getFieldValues("domains");
					List <String> list_aux2_domains = new ArrayList<String>();
					for(Object domain : list_aux_domains){
						list_aux2_domains.add(domain.toString());
					}
					ro.setDomains(list_aux2_domains);
				}
				if(results.getFieldValue("concepts") != null){
					Collection<Object> list_aux_concepts = results.getFieldValues("concepts");
					List <String> list_aux2_concepts = new ArrayList<String>();
					for(Object concept : list_aux_concepts){
						list_aux2_concepts.add(concept.toString());
					}
					ro.setConcepts(list_aux2_concepts);
				}
				if(results.getFieldValue("compound_terms") != null){
					Collection<Object> list_aux_expressions = results.getFieldValues("compound_terms");
					List <String> list_aux2_expressions = new ArrayList<String>();
					for(Object expression : list_aux_expressions){
						list_aux2_expressions.add(expression.toString());
					}
					ro.setExpressions(list_aux2_expressions);
				}
				if(results.getFieldValue("people") != null){
					Collection<Object> list_aux_people = results.getFieldValues("people");
					List <String> list_aux2_people = new ArrayList<String>();
					for(Object person : list_aux_people){
						list_aux2_people.add(person.toString());
					}
					ro.setPeople(list_aux2_people);
				}
				if(results.getFieldValue("place") != null){
					Collection<Object> list_aux_place = results.getFieldValues("place");
					List <String> list_aux2_place = new ArrayList<String>();
					for(Object place : list_aux_place){
						list_aux2_place.add(place.toString());
					}
					ro.setPlaces(list_aux2_place);
				}
				if(results.getFieldValue("organization") != null){
					Collection<Object> list_aux_org = results.getFieldValues("organization");
					List <String> list_aux2_org = new ArrayList<String>();
					for(Object org : list_aux_org){
						list_aux2_org.add(org.toString());
					}
					ro.setOrg(list_aux2_org);
				}
				if(results.getFieldValue("sketch") != null){
					ro.setSketch(results.getFieldValue("sketch").toString());
				}
				res.add(ro);
			}
		}
		return res;
	}
	public static List <ResearchObject> results_list_ros_by_author(String creator) throws SolrServerException, IOException{
		String input = "creator:\"" + creator + "\"";
		SolrClient solr = new HttpSolrClient.Builder(urlSolr).build();
		SolrQuery query = new SolrQuery();

		List <ResearchObject> res = new ArrayList<ResearchObject>();

		query.setQuery(input);
		query.setFacet(true);
		query.setRows(700);
//		query.add("group", "true");
//		query.add("group.ngroups", "true");
//		query.add("group.field","source_ro");
//		query.add("group.sort", "created desc");

//		QueryResponse rs = solr.query(query);
//		List<Group> groupedData = rs.getGroupResponse().getValues().get(0).getValues();
//		for (Group gp : groupedData){
//			SolrDocumentList results_list = gp.getResult();
		QueryResponse response = solr.query(query);
		for (SolrDocument results : response.getResults()){
//			for (SolrDocument results : results_list){
				ResearchObject ro = new ResearchObject(results.getFieldValue("id").toString());
				if(results.getFieldValue("title") != null){
					ro.setTitle(results.getFieldValue("title").toString());
				}
				if(results.getFieldValue("created") != null){
					ro.setCreated(results.getFieldValue("created").toString());
				}
				if(results.getFieldValue("author") != null){
					ro.setAuthor(results.getFieldValue("author").toString());
				}
				if(results.getFieldValue("description") != null){
					ro.setDescription(results.getFieldValue("description").toString());
				}
				if(results.getFieldValue("domains") != null){
					Collection<Object> list_aux_domains = results.getFieldValues("domains");
					List <String> list_aux2_domains = new ArrayList<String>();
					for(Object domain : list_aux_domains){
						list_aux2_domains.add(domain.toString());
					}
					ro.setDomains(list_aux2_domains);
				}
				if(results.getFieldValue("concepts") != null){
					Collection<Object> list_aux_concepts = results.getFieldValues("concepts");
					List <String> list_aux2_concepts = new ArrayList<String>();
					for(Object concept : list_aux_concepts){
						list_aux2_concepts.add(concept.toString());
					}
					ro.setConcepts(list_aux2_concepts);
				}
				if(results.getFieldValue("compound_terms") != null){
					Collection<Object> list_aux_expressions = results.getFieldValues("compound_terms");
					List <String> list_aux2_expressions = new ArrayList<String>();
					for(Object expression : list_aux_expressions){
						list_aux2_expressions.add(expression.toString());
					}
					ro.setExpressions(list_aux2_expressions);
				}
				if(results.getFieldValue("people") != null){
					Collection<Object> list_aux_people = results.getFieldValues("people");
					List <String> list_aux2_people = new ArrayList<String>();
					for(Object person : list_aux_people){
						list_aux2_people.add(person.toString());
					}
					ro.setPeople(list_aux2_people);
				}
				if(results.getFieldValue("place") != null){
					Collection<Object> list_aux_place = results.getFieldValues("place");
					List <String> list_aux2_place = new ArrayList<String>();
					for(Object place : list_aux_place){
						list_aux2_place.add(place.toString());
					}
					ro.setPlaces(list_aux2_place);
				}
				if(results.getFieldValue("organization") != null){
					Collection<Object> list_aux_org = results.getFieldValues("organization");
					List <String> list_aux2_org = new ArrayList<String>();
					for(Object org : list_aux_org){
						list_aux2_org.add(org.toString());
					}
					ro.setOrg(list_aux2_org);
				}
				if(results.getFieldValue("sketch") != null){
					ro.setSketch(results.getFieldValue("sketch").toString());
				}
				res.add(ro);
			}
//		}
		return res;
	}
	public static List <Scientist> related(Scientist scientist) throws SolrServerException, IOException{
		String input = "";
		Map<String, Integer> map_concepts = new HashMap<>();
		Map<String, Integer> map_places = new HashMap<>();
		for(ResearchObject ro:scientist.getListRO()){
			if(ro.getConcepts() != null && !ro.getConcepts().isEmpty()){
				List <String> list_concepts = ro.getConcepts();
				Iterator<String> it = list_concepts.iterator();
				int cont = list_concepts.size();
				while(it.hasNext() && cont >= 1){
					String concept = it.next();
					map_concepts.put(concept, map_concepts.containsKey(concept)? map_concepts.get(concept)+cont : cont);
					cont--;
				}
			}
			if(ro.getPlaces() != null && !ro.getPlaces().isEmpty()){
				List <String> list_places = ro.getPlaces();
				Iterator<String> it = list_places.iterator();
				int cont = list_places.size();
				while(it.hasNext() && cont >= 1){
					String place = it.next();
					map_places.put(place, map_places.containsKey(place)? map_places.get(place)+cont : cont);
					cont = cont--;
				}
			}
		}
		int cont_limit_query = 0;
		if (!map_concepts.isEmpty()){
			Map<String, Integer> sortedMapConcepts = ResultsGenerator.sortByValue(map_concepts);
			int cont = 100;
			int div = 100/sortedMapConcepts.size();
			for (String concept : sortedMapConcepts.keySet() ) {
				if (cont_limit_query > 225){
					break;
				}
				cont_limit_query++;
				input = input + "concepts:" + concept + "^" + (double)cont/100 + " & ";
				if (cont > 1){
					cont=cont-div;
				}
			}
		}
		if (!map_places.isEmpty()){
			Map<String, Integer> sortedMapPlaces = ResultsGenerator.sortByValue(map_places);
			int cont = 100;
			int div = 100/sortedMapPlaces.size();
			for (String place : sortedMapPlaces.keySet() ) {
				if (cont_limit_query > 250){
					break;
				}
				cont_limit_query++;
				input = input + "place:" + place + "^" + (double)cont/100 + " & ";
				if (cont > 1){
					cont=cont-div;
				}
			}
		}
		SolrClient solr = new HttpSolrClient.Builder(urlSolr).build();
		SolrQuery query = new SolrQuery();

		List <Scientist> res = new ArrayList<Scientist>();
		int cont = 0;

		query.setQuery(input);
		query.setFacet(true);
		query.setRows(700);
		query.add("group", "true");
		query.add("group.ngroups", "true");
		query.add("group.field","source_ro");

		QueryResponse rs = solr.query(query);
		List<Group> groupedData = rs.getGroupResponse().getValues().get(0).getValues();
		for (Group gp : groupedData){
			SolrDocumentList results_list = gp.getResult();
			for (SolrDocument results : results_list){
				if(results.getFieldValue("author") != null){
					boolean incluido = false;
					for (Scientist ya_metidos:res){
						if(results.getFieldValue("creator").toString().equals(ya_metidos.getId())){
							incluido = true;
						}
					}
					if(!incluido){
						if (cont > 8){
							break;
						}
						Scientist scientist2 = new Scientist(results.getFieldValue("creator").toString());
						scientist2.setName(results.getFieldValue("author").toString());
						scientist2.setListRO(ListCreator.results_list_ros_by_author(scientist2.getId()));
						res.add(scientist2);
						cont ++;
					}
				}
			}
		}
		return res;
	}
}
