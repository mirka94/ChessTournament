//klasa która odpowiada za czytanie pliku xml

package xml;

import data.CompetitorModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class CompetitorReadFile {

    private CompetitorModel cm;

    public CompetitorModel competitor(String filePath){

        cm = new CompetitorModel();

        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("competitor");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    cm.setId(new Integer(eElement.getAttribute("id")).parseInt(eElement.getAttribute("id")));
                    cm.setName(eElement.getElementsByTagName("name").item(0).getTextContent());
                    cm.setSurname(eElement.getElementsByTagName("surname").item(0).getTextContent());
                    cm.setAge(Integer.parseInt(eElement.getElementsByTagName("age").item(0).getTextContent()));
                    cm.setChessCategory(Integer.parseInt(eElement.getElementsByTagName("chessCategory").item(0).getTextContent()));

                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return cm;

    }

}
