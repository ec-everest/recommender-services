package net.expertsystem.lab.everest.collaborationspheresRO;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JSONcreator {
	private static Logger logger = LogManager.getLogger(JSONcreator.class);
	public static String hay_title(ResearchObject ro){
		String res = "";
		if(ro.getTitle() != null){
			res = ",\"value\": \""+ ro.getTitle() + "\"";
		}
		else{
			res = ",\"value\": \""+ ro.getId() + "\"";
		}
		return res;
	}
	public static String hay_sketch(ResearchObject ro){
		String res = "";
		if(ro.getSketch() != null){
			res = ",\"relatedImage\": \""+ ro.getSketch() + "\"";
		}
		return res;
	}
	public static String hay_description(ResearchObject ro){
		String res = "";
		if(ro.getDescription() != null){
			String aux = ro.getDescription().replace("\n", "").replace("\r", "").replace("\t", "").replaceAll("\"","").replace("\\", "");
			res = ",\"description\": \""+ aux.substring(0, Math.min(aux.length(), 650));
			if (aux.length() > 650){
				res = res + "...\"";
			}
			else{
				res = res + "\"";
			}
		}
		return res;
	}
	public static String hay_created(ResearchObject ro){
		String res = "";
		if(ro.getCreated() != null){
			res = ",\"created\": \""+ ro.getCreated() + "\"";
		}
		return res;
	}
	public static String hay_concepts(ResearchObject ro){
		String res = "";
		List <String> list = ro.getConcepts();
		if(list != null && !list.isEmpty()){
			
			res = ",\"concepts\": \"";
			int cont = 0;
			for (String concept : list){
				if(cont == 3){
					break;
				}
				if (cont == 2 || cont == list.size()-1){
					res = res + concept + "";
				}
				else{
					res = res + concept + ", ";
				}
				cont ++;
			}
			res = res + "\"";
		}
		return res;
	}
	public static String hay_author(ResearchObject ro){
		String res = "";
		if(ro.getAuthor() != null){
			res = ",\"author\": \""+ ro.getAuthor() + "\"";
		}
		return res;
	}
	public static String hay_score(ResearchObject ro){
		String res = "";
		if(ro.getScore() != null){
			res = ",\"score\": \""+ ro.getScore() + "\"";
		}
		return res;
	}
	public static String hay_domains(ResearchObject ro){
		String res = "";
		List <String> list = ro.getDomains();
		if(list != null && !list.isEmpty()){
			
			res = ",\"domains\": \"";
			int cont = 0;
			for (String domain : list){
				if(cont == 3){
					break;
				}
				if (cont == 2 || cont == list.size()-1){
					res = res + domain + "";
				}
				else{
					res = res + domain + ", ";
				}
				cont ++;
			}
			res = res + "\"";
		}
		return res;
	}
	public static String hay_ROs(Scientist scientist){
		String res = "";
		if(!scientist.getListRO().isEmpty()){
			res = res + ",\"ROs\": [";
			int cont = 1;
			for (ResearchObject ro: scientist.getListRO()){
				res = res + "{\"@type\": \"ResearchObject\"," +
						"\"url\": \"" + ro.getId() + "\"" +
						hay_title(ro) +
						hay_sketch(ro) +
						hay_description(ro) +
						hay_created(ro)+
						hay_author(ro) +
						hay_concepts(ro)+
						hay_domains(ro) +
						"}";
				if (cont != scientist.getListRO().size()){
					res = res + ",";
				}
				cont++;
			}
			res = res + "]";
		}
		return res;
	}
	public static String creator(List <ResearchObject> ros_context, List <Scientist> scientists_context, List <ResearchObject> list){
		String inner =
				"{ " +
						"\"name\": \"Showcase Spheres\"," +
						"\"type\": \"http://everest.expertsystemlab.com/spheres\"," +
						"\"uri\": \"http://everest.expertsystemlab.com/EverEstSpheres/services/jsonservices/\",";
		inner = inner +
				"\"inner\": [";
		if (!scientists_context.isEmpty()){
			int cont_contextos = 1;
			for (Scientist scientist : scientists_context){
				inner = inner +
						"{" +
						"\"recommended\": {" +
						"\"@type\": \"Scientist\"," +
						"\"url\": \"" + scientist.getId() + "\"," +
		                "\"value\": \"" + scientist.getName() + "\"" +
						JSONcreator.hay_ROs(scientist) +
		                "}," +
								"\"confidence\": 1" +
								"}";
				if (ros_context.isEmpty()){
					if (cont_contextos != scientists_context.size()){
						inner = inner + ",";
					}
					cont_contextos++;
				}
				else{
					inner = inner + ",";
				}

			}
		}
		if (!ros_context.isEmpty()){
			int cont_contextos = 1;
			for (ResearchObject ro_context : ros_context){
				inner = inner +  
						"{" +
						"\"recommended\": {" +
						"\"@type\": \"ResearchObject\"," +
						"\"url\": \"" + ro_context.getId() + "\"" +
						hay_title(ro_context) +
						hay_sketch(ro_context) +
						hay_description(ro_context) +
						hay_created(ro_context) +
						hay_author(ro_context) +
						hay_concepts(ro_context)+
						hay_domains(ro_context)+
						"}," +
								"\"confidence\": 1" +
								"}";
				if (cont_contextos != ros_context.size()){
					inner = inner + ",";
				}
				cont_contextos++;
			}
		}
		inner = inner + "],";
		String inter = "\"inter\": [";
		String outer = "\"outer\": [";
		int cont = 1;
		for (ResearchObject recommended : list){
			if(cont < 5 && cont != list.size()){
				inter = inter + "{" +
						"\"recommended\": {" +
						"\"@type\": \"ResearchObject\"," +
						"\"url\": \"" + recommended.getId() + "\"" +
						hay_title(recommended) +
						hay_sketch(recommended) +
						hay_description(recommended)+
						hay_created(recommended) +
						hay_author(recommended) +
						hay_concepts(recommended) +
						hay_domains(recommended)+
						hay_score(recommended)+
						"}," +
						"\"confidence\": 1" +
						"},";
			}
			if(cont == 5 || (cont <= 5 && cont == list.size())){
				inter = inter + "{" +
					"\"recommended\": {" +
					"\"@type\": \"ResearchObject\"," +
					"\"url\": \"" + recommended.getId() + "\"" +
					hay_title(recommended) +
					hay_sketch(recommended) +
					hay_description(recommended)+
					hay_created(recommended) +
					hay_author(recommended) +
					hay_concepts(recommended) +
					hay_domains(recommended)+
					hay_score(recommended)+
					"}," +
					"\"confidence\": 1" +
					"}" +
				    "],";
			}
			if(cont > 5 && cont == list.size()){
				outer = outer + "{" +
						"\"recommended\": {" +
						"\"@type\": \"ResearchObject\"," +
						"\"url\": \"" + recommended.getId() + "\"" +
						hay_title(recommended) +
						hay_sketch(recommended) +
						hay_description(recommended) +
						hay_created(recommended) +
						hay_author(recommended) +
						hay_concepts(recommended) +
						hay_domains(recommended)+
						hay_score(recommended)+
						"}," +
						"\"confidence\": 1" +
						"}"+
					    "]}";
			}
			if(cont > 5 && cont != list.size()){
				outer = outer + "{" +
						"\"recommended\": {" +
						"\"@type\": \"ResearchObject\"," +
						"\"url\": \"" + recommended.getId() + "\"" +
						hay_title(recommended) +
						hay_sketch(recommended) +
						hay_description(recommended) +
						hay_created(recommended) +
						hay_author(recommended) +
						hay_concepts(recommended) +
						hay_domains(recommended)+
						hay_score(recommended)+
						"}," +
						"\"confidence\": 1" +
						"},";
			}
			cont++;
		}
		if (cont <= 1){
			inter = inter + "],";
		}
		if (cont <= 6){
			outer = outer + "]}";
		}
		return inner + inter + outer;
	}
}
