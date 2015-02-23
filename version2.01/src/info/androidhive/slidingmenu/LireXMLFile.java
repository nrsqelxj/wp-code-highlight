package info.androidhive.slidingmenu;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LireXMLFile {
	public static int [][] tableau;
	public void Parse(){
       
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            	
        try {
          
            final DocumentBuilder builder = factory.newDocumentBuilder();
         
	    final Document document= builder.parse(("employees.xml"));
		
	    final Element racine = document.getDocumentElement();
		
	    final NodeList racineNoeuds = racine.getChildNodes();
	    final int nbRacineNoeuds = racineNoeuds.getLength();
	    tableau=new int[8][8];
	    int f = 0;
	    int l = 0; 
	    for (int i = 0; i<nbRacineNoeuds; i++) {
	        if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
	            final Element pc = (Element) racineNoeuds.item(i);
				
		 
			f = f + 1;
			l = f + 1;
		    final NodeList matrice =  pc.getElementsByTagName("matrice");
		    for(int k =0; k<matrice.getLength();k++){
		    String s = matrice.item(k).getTextContent(); 
		    
		    float g= Float.valueOf (s);
		
		    	tableau [f][l]=(int) Float.parseFloat(matrice.item(k).getTextContent());
		    	tableau [l][f]=(int) Float.parseFloat(matrice.item(k).getTextContent());
		    	l = l+1;
	        }
		    
		   
	        }				
	    }


        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (final SAXException e) {
            e.printStackTrace();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }		
    }
}