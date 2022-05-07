import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        ArrayList<Shop> Shop=readProductsFromXML("src/main/resources/shop.xml");
        menu(Shop);
    }

    private static void menu(ArrayList<Shop> shop) {
        int choice = 0;
        while (choice != 5) {
            System.out.println("1 - List items\r\n2 - Add new item\r\n3 - Modify item\r\n4 - Delete item\r\n5 - Exit");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Not valid.");
            }
            scanner.nextLine();
            switch (choice) {
                case 1 -> listItems(shop);
                case 2 -> addNewItem(shop);
                case 3 -> modifyItem(shop);
                case 4 -> deleteItem(shop);
            }
            saveShopToXML(shop, "src/main/resources/shop.xml");
        }
    }

    private static void deleteItem(ArrayList<Shop> shop) {
        System.out.println("Enter the name of the product you want to delete:");
        try{
            shop.remove(findProduct(shop, scanner.nextLine()));
        }
        catch(IllegalArgumentException error2){
            System.out.println(error2.getMessage());
        }
    }

    private static void modifyItem(ArrayList<Shop> shop) {
        System.out.println("Enter the product you want to modify:");
        try{
            Shop shop1 = findProduct(shop, scanner.nextLine());
            shop.set(shop.indexOf(shop1), new Shop(shop1.getName(), inputPrice(), inputQuantity()));
        }
        catch(IllegalArgumentException error){
            System.out.println(error.getMessage());
        }
    }

    private static Shop findProduct(ArrayList<Shop> shop, String lookinfor) throws IllegalArgumentException{
        for(Shop shops : shop){
            if(shops.getName().equals(lookinfor)){
                return shops;
            }
        }
        throw new IllegalArgumentException("No such item found.");
    }

    private static void addNewItem(ArrayList<Shop> shop) {
        shop.add(new Shop(inputName(), inputPrice(), inputQuantity()));
    }

    private static int inputQuantity() {
        int qnt;
        System.out.println("Enter the quantity of the new product:\r\n(Must be greater than 0)");
        qnt = scanner.nextInt();
        while(qnt <= 0) {
            try {
                return qnt;
            } catch (InputMismatchException e) {
                e.printStackTrace();
            }
        }
        return qnt;
    }

    private static int inputPrice() {
        int prz;
        System.out.println("Enter the prize of the new product:\r\n(Must be greater than 0)");
        prz = scanner.nextInt();
        while(prz <= 0) {
            try {
                return prz;
            } catch (InputMismatchException e) {
                e.printStackTrace();
            }
        }
        return prz;
    }

    private static String inputName() {
        System.out.println("Enter the name of the new Product:");
        return scanner.nextLine();
    }


    private static void listItems(ArrayList<Shop> shop) {
        System.out.println(shop);
    }

    private static void saveShopToXML(ArrayList<Shop> shop, String filepath) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("Shop");
            document.appendChild(root);
            for (Shop shops : shop) {
                Element gradeElement = document.createElement("Product");
                root.appendChild(gradeElement);
                childElement(document, gradeElement, "Name", shops.getName());
                childElement(document, gradeElement, "Price", String.valueOf(shops.getPrice()));
                childElement(document, gradeElement, "Quantity", String.valueOf(shops.getQuantity()));
            }
            TransformerFactory transformerFactory=TransformerFactory.newInstance();
            Transformer transformer=transformerFactory.newTransformer();
            DOMSource source=new DOMSource(document);
            StreamResult result= new StreamResult(new FileOutputStream(filepath));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void childElement(Document document, Element smth, String tag, String txt){
        Element element=document.createElement(tag);
        element.setTextContent(txt);
        smth.appendChild(element);
    }

    // XML reader //

    private static ArrayList<Shop> readProductsFromXML(String filepath){
        ArrayList<Shop> shop=new ArrayList<>();
        try{
            DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder=documentBuilderFactory.newDocumentBuilder();
            Document document=documentBuilder.parse(filepath);

            Element rootElement=document.getDocumentElement();
            NodeList childNodeList=rootElement.getChildNodes();
            Node node;

            for(int i=0; i<childNodeList.getLength(); i++){
                node=childNodeList.item(i);

                if(node.getNodeType()==Node.ELEMENT_NODE){
                    NodeList childNodesOfShop=node.getChildNodes();
                    String name="", price="", quantity="";
                    for(int j=0; j<childNodesOfShop.getLength(); j++){
                        Node childNodeOfShops=childNodesOfShop.item(j);

                        if(childNodeOfShops.getNodeType()==Node.ELEMENT_NODE){
                            switch (childNodeOfShops.getNodeName()){
                                case "Name" -> name=childNodeOfShops.getTextContent();
                                case "Price" -> price=childNodeOfShops.getTextContent();
                                case "Quantity" -> quantity=childNodeOfShops.getTextContent();
                            }
                        }
                    }
                    shop.add(new Shop(name, Integer.parseInt(price), Integer.parseInt(quantity)));
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return shop;
    }
}
