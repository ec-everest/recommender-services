package net.expertsystem.lab.everest.collaborationspheresRO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class ResultsGenerator {
	private static String urlSolr = "http://172.16.32.89:8983/solr/EverEst";
	static final Logger logger = LogManager.getLogger(ResultsGenerator.class.getName());
	public static List <ResearchObject> results(String input, List <ResearchObject> ros_context, List <Scientist> scientists_context, String iduser)
			throws SolrServerException, IOException{
		logger.debug(input);
		List <ResearchObject> ros_input = new ArrayList<ResearchObject>();
		if (!ros_context.isEmpty()){
			for (ResearchObject ro: ros_context){
				ros_input.add(ro);
			}
		}

		List <ResearchObject> res = new ArrayList<ResearchObject>();
		

		SolrClient solr = new HttpSolrClient.Builder(urlSolr).build();
		SolrQuery query = new SolrQuery();

		query.setQuery(input);
		query.setFacet(true);
		query.setRows(700);
		query.add("group", "true");
		query.add("group.ngroups", "true");
		query.add("group.field","source_ro");
		query.add("group.sort", "created desc");
		query.add("fl","id, creator, created, title, author, description, domains, concepts, compound_terms, people, place, organization, sketch,"
				+ " score");

		QueryResponse rs = solr.query(query);
		List<Group> groupedData = rs.getGroupResponse().getValues().get(0).getValues();
		int cont = 0;
		for (Group gp : groupedData){
			SolrDocumentList results_list = gp.getResult();
			String maxScore = results_list.getMaxScore().toString();
			for (SolrDocument results : results_list){
				if (cont > 12){
					break;
				}
				boolean esContexto = false;
				boolean esIdUser = false;
				for(ResearchObject ro_input : ros_input){
					if(results.getFieldValue("id").equals(ro_input.getId())){
						esContexto = true;
					}
					if(results.getFieldValue("creator").equals(iduser)){
						esContexto = true;
					}
				}
				if (esContexto || esIdUser){
					continue;
				}
				ResearchObject ro = new ResearchObject(results.getFieldValue("id").toString());
				double calcScore = (100*Double.parseDouble(maxScore))/35;
				if (calcScore > 100){
					calcScore = 100.00;
				}
				ro.setScore(String.format("%.2f", calcScore)+ "%");
				
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
				cont++;
			}
		}
		return res;
	}
	public static String scientistNamer (Scientist scientist) throws SolrServerException, IOException{
		String input = "creator:\"" + scientist.getId() + "\"";
		String res = "";

		SolrClient solr = new HttpSolrClient.Builder(urlSolr).build();
		SolrQuery query = new SolrQuery();
		query.setQuery(input);
		query.setFacet(true);
		query.setRows(12);
		
		QueryResponse response = solr.query(query);
		for (SolrDocument results : response.getResults()){
			res = results.getFieldValue("author").toString();
			break;
		}
		return res;
	}
	public static String queryGenerator(List <ResearchObject> ros_context, List <Scientist> scientist_context){
		String input = "";
		List <ResearchObject> list_ROs_context = new ArrayList<ResearchObject>();
		if (!scientist_context.isEmpty()){
			for (Scientist scientist : scientist_context){
				if (!scientist.getListRO().isEmpty()){
					for (ResearchObject ro: scientist.getListRO()){
						list_ROs_context.add(ro);
					}
				}
			}		
		}
		if (!ros_context.isEmpty()){
			for (ResearchObject ro : ros_context){
				list_ROs_context.add(ro);
			}
		}
		Map<String, Integer> map_concepts = new HashMap<>();
		Map<String, Integer> map_places = new HashMap<>();
		if (!list_ROs_context.isEmpty()){
			for (ResearchObject ro: list_ROs_context){
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
		}
		int cont_limit_query = 0;
		if (!map_concepts.isEmpty()){
			Map<String, Integer> sortedMapConcepts = sortByValue(map_concepts);
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
			Map<String, Integer> sortedMapPlaces = sortByValue(map_places);
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
		logger.debug(input);
		return input;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ){
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>(){
        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
        	{
            	return (o2.getValue()).compareTo( o1.getValue() );
        	}
		} );

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list){
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
	
	public static ResearchObject roSetter(ResearchObject ro) throws SolrServerException, IOException{
		SolrClient solr = new HttpSolrClient.Builder(urlSolr).build();
		SolrQuery query = new SolrQuery();
		query.setQuery("id: \"" + ro.getId() + "\"");
		query.setFacet(true);
		query.setRows(1);
		QueryResponse response = solr.query(query);
		Iterator<SolrDocument> it = response.getResults().iterator();
		while (it.hasNext()){
			SolrDocument results = it.next();
			if(results.getFieldValue("title") != null){
				ro.setTitle(results.getFieldValue("title").toString());
			}
			if(results.getFieldValue("description") != null){
				ro.setDescription(results.getFieldValue("description").toString());
			}
			if(results.getFieldValue("created") != null){
				ro.setCreated(results.getFieldValue("created").toString());
			}
			if(results.getFieldValue("author") != null){
				ro.setAuthor(results.getFieldValue("author").toString());
			}
			if(results.getFieldValue("place") != null){
				Collection<Object> list_aux_places = results.getFieldValues("place");
				List <String> list_aux2_places = new ArrayList<String>();
				for(Object place : list_aux_places){
					 list_aux2_places.add(place.toString());
				}
				ro.setPlaces(list_aux2_places);
			}
			if(results.getFieldValue("concepts") != null){
				Collection<Object> list_aux_concepts = results.getFieldValues("concepts");
				List <String> list_aux2_concepts = new ArrayList<String>();
				for(Object concept : list_aux_concepts){
					 list_aux2_concepts.add(concept.toString());
				}
				ro.setConcepts(list_aux2_concepts);
			}
			if(results.getFieldValue("domains") != null){
				Collection<Object> list_aux_domains = results.getFieldValues("domains");
				List <String> list_aux2_domains = new ArrayList<String>();
				for(Object domain : list_aux_domains){
					 list_aux2_domains.add(domain.toString());
				}
				ro.setDomains(list_aux2_domains);
			}
			if(results.getFieldValue("sketch") != null){
				ro.setSketch(results.getFieldValue("sketch").toString());
			}
		}
		return ro;
		
	}
}
