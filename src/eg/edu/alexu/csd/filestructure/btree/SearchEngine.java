package eg.edu.alexu.csd.filestructure.btree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SearchEngine implements ISearchEngine{
	private String id;
	private IBTree<String,List<ISearchResult>> bTree;
	SearchEngine(int minimumDegree){
		 bTree = new BTree( minimumDegree);
	}
	
	@Override
	public void indexWebPage(String filePath) {
		if (filePath==null||filePath.equals("")) {
			throw new RuntimeErrorException (null); 
		}
		
		File file = new File(filePath);
		if(file.exists()) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder =factory.newDocumentBuilder();
			Document document = builder.parse(file);
			NodeList docs = document.getElementsByTagName("doc");
			for (int i =0;i<docs.getLength();i++) {
				Node d = docs.item(i);
				if (d.getNodeType()==Node.ELEMENT_NODE) {
					Element doc = (Element) d;
					id = doc.getAttribute("id");
					String text = doc.getTextContent();
					split(text);
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}else {
			System.out.println("soso");
		}
	}
	private void split(String text) {
		text = text.toLowerCase();
		text = text.trim();
		text = text.replace("\n"," ");
		String[] splited = text.split("\\s+");
		Map< String,Integer> wordsMap = getRanks(splited);
		insetIntoBtree(wordsMap);
	}
	private Map< String,Integer> getRanks(String splited[]){
		 Map< String,Integer> ranks = new HashMap< String,Integer>(); ;
		 for (int i =0;i<splited.length;i++) {
			 if (!ranks.containsKey(splited[i])) {
				 ranks.put(splited[i], 1);
			 }else {
				 ranks.put(splited[i],  ranks.get(splited[i])+1);
			 }
		 }
		return ranks;
	}
	private void insetIntoBtree(Map< String,Integer> wordsMap) {
	for(Map.Entry< String,Integer> map: wordsMap.entrySet()) {
			ISearchResult result = new SearchResult(id, map.getValue());
			List <ISearchResult>valuesList = new ArrayList();
			valuesList.add(result);
			if (bTree.search(map.getKey())==null) {
				bTree.insert(map.getKey(), valuesList);
			}else {
				bTree.search(map.getKey()).add(result);
			}
		}
	}
	@Override
	public void indexDirectory(String directoryPath) {
		if (directoryPath==null||directoryPath.equals("")) {
			throw new RuntimeErrorException (null); 
		}
		File file = new File(directoryPath);
		
		if(file.exists()) {
				for (File f :file.listFiles()) {
					if (f.isDirectory()) {
						indexDirectory(f.toString());
					}else {
					indexWebPage(f.toString());
				}
			}
		}
	}

	@Override
	public void deleteWebPage(String filePath) {
		if (filePath==null||filePath.equals("")) {
			throw new RuntimeErrorException (null); 
		}
		File f = new File (filePath);
		if (f.exists()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder =factory.newDocumentBuilder();
				Document document = builder.parse(f);
				NodeList docs = document.getElementsByTagName("doc");
				for (int i =0;i<docs.getLength();i++) {
					Node d = docs.item(i);
					if (d.getNodeType()==Node.ELEMENT_NODE) {
						Element doc = (Element) d;
						id = doc.getAttribute("id");
						String text = doc.getTextContent();
						deleteWrods(text);
						
					}
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void deleteWrods(String text) {
		text = text.toLowerCase();
		text = text.trim();
		text = text.replace("\n"," ");
		String[] splited = text.split("\\s+");
		for (int i=0;i<splited.length;i++) {
			if (bTree.search(splited[i])!=null) {
				List <ISearchResult>help=bTree.search(splited[i]);
				for (int j =0 ;j<help.size();j++) {
					if (help.get(j).getId().contentEquals(id)) {
						bTree.search(splited[i]).remove(help.get(j));
						break;
					}
				}
				if (bTree.search(splited[i]).size()==0) {
					bTree.delete(splited[i]);
				}
			}
		}
	}
	@Override
	public List<ISearchResult> searchByWordWithRanking(String word) {
		if (word==null) {
			throw new RuntimeErrorException (null);   
		}else if (word.contentEquals("")) {
			return new ArrayList<>();
		}
		word= word.trim();
		word = word.toLowerCase();
		List<ISearchResult>  help = new ArrayList();
		help =bTree.search(word);
		if (help==null) {
			return  new ArrayList();
		}else {
			return help;
		}
	}

	@Override
	public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
		List<ISearchResult> mulWordList = new ArrayList <ISearchResult>();
		if (sentence==null) {
			throw new RuntimeErrorException (null);   
		}else if (sentence.contentEquals("")) {
			return new ArrayList<>();
		}
		sentence = sentence.trim();
		String[] splited = sentence.split("\\s+");
		for (int i =0;i<splited.length;i++ ) {
			mulWordList.addAll(searchByWordWithRanking(splited[i]));
		}
		return mulWordList;
	}

}
