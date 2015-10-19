//klasa odpowiada za zapis danych do pliku xml

package xml;

import data.CompetitorModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class CompetitorToFile {


    public void toXML(CompetitorModel data, String filePath){

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("tournament");
            doc.appendChild(rootElement);

            Element competitor = doc.createElement("competitor");
            rootElement.appendChild(competitor);

            Attr attr = doc.createAttribute("id");
            attr.setValue(new Integer(data.getId()).toString());
            competitor.setAttributeNode(attr);

            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(data.getName()));
            competitor.appendChild(name);

            Element surname = doc.createElement("surname");
            surname.appendChild(doc.createTextNode(data.getSurname()));
            competitor.appendChild(surname);

            Element age = doc.createElement("age");
            age.appendChild(doc.createTextNode(Integer.toString(data.getAge())));
            competitor.appendChild(age);

            Element chessCategory = doc.createElement("chessCategory");
            chessCategory.appendChild(doc.createTextNode(Integer.toString(data.getChessCategory())));
            competitor.appendChild(chessCategory);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));

            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

}
