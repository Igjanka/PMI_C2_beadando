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


/**Shop inventory program.
 * You can add new products to the shop,
 * modify the alredy existing ones,
 * or delete them if you are out of stock.
 * */



public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    /**Main, where XML is read first, then it's given to the menu for the rest of the program.
     * @param args
     */

    public static void main(String[] args) {
        ArrayList<Shop> Shop = readProductsFromXML("src/main/resources/shop.xml");
        menu(Shop);
    }

    /**Menu part of the program, where the user can choose to call a part of the program,
     * ie. CRUD (Create, Read, Update, Delete)
     * @param shop
     */

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
            //Switch operation to call different parts
            switch (choice) {
                case 1 -> listItems(shop);
                case 2 -> addNewItem(shop);
                case 3 -> modifyItem(shop);
                case 4 -> deleteItem(shop);
            }
            saveShopToXML(shop, "src/main/resources/shop.xml");
        }
    }

    /**
     * Part of CRUD,the Delete part, uses findProduct to locate the product based on it's name.
     * @param shop
     */

    private static void deleteItem(ArrayList<Shop> shop) {
        System.out.println("Enter the name of the product you want to delete:");
        try{
            shop.remove(findProduct(shop, scanner.nextLine()));
        }
        catch(IllegalArgumentException error2){
            System.out.println(error2.getMessage());
        }
    }
    /**
     * Part of CRUD,the Update part, uses findProduct to locate the product based on it's name.
     * Also uses the input parts for the price and quantity, since name is already given.
     * @param shop
     */

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

    /**
     * Forementioned findProduct program, return the item with the given name.
     * @param shop
     * @param lookinfor
     * @return
     * @throws IllegalArgumentException
     */
    private static Shop findProduct(ArrayList<Shop> shop, String lookinfor) throws IllegalArgumentException{
        //foreach
        for(Shop shops : shop){
            if(shops.getName().equals(lookinfor)){
                return shops;
            }
        }
        throw new IllegalArgumentException("No such item found.");
    }

    /**
     * Quote on quote main, but for the CRUD's create. Uses three input programs to achieve its goal.
     * @param shop
     */
    private static void addNewItem(ArrayList<Shop> shop) {
        shop.add(new Shop(inputName(), inputPrice(), inputQuantity()));
    }

    /**
     * Input for Quantity. Makes sure it's an integer and greater than 0.
     * Returns the new product's quantity.
     * @return
     */
    private static int inputQuantity() throws InputMismatchException{
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
        throw new InputMismatchException("Not a number.");
    }

    /**
     * Input for the Prize. Makes sure it's an integer and greater than 0.
     * Returns the new product's prize.
     * @return
     */
    private static int inputPrice() throws InputMismatchException{
        int prz;
        System.out.println("Enter the price of the new product:\r\n(Must be greater than 0)");
        prz = scanner.nextInt();
        while(prz <= 0){
            try{
                return prz;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        throw new InputMismatchException("Not a number");
    }

    /**
     * Input for Name of the new product.
     * Returns the new product's name.
     * @return
     */
    private static String inputName() {
        System.out.println("Enter the name of the new Product:");
        return scanner.nextLine();
    }

    /**
     * Print's the content of current shop.xml.
     * @param shop
     */
    private static void listItems(ArrayList<Shop> shop) {
        System.out.println(shop);
    }

    /**
     * This is the part of the program, which saves the updates to shop.xml.
     * It's used after every menu interaction.
     * @param shop
     * @param filepath
     */
    private static void saveShopToXML(ArrayList<Shop> shop, String filepath) {
        try {
            //documentuilder to build the xml
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("Shop");
            document.appendChild(root);
            //foreach
            for (Shop shops : shop) {
                Element gradeElement = document.createElement("Product");
                root.appendChild(gradeElement);
                childElement(document, gradeElement, "Name", shops.getName());
                childElement(document, gradeElement, "Price", String.valueOf(shops.getPrice()));
                childElement(document, gradeElement, "Quantity", String.valueOf(shops.getQuantity()));
            }
            //transformer
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

    /**
     * Creates child elements for saveShopToXML.
     * @param document
     * @param smth
     * @param tag
     * @param txt
     */
    public static void childElement(Document document, Element smth, String tag, String txt){
        Element element=document.createElement(tag);
        element.setTextContent(txt);
        smth.appendChild(element);
    }

    /**
     * Part of the program, which reads the xml file.
     * Returns an arraylist.
     * @param filepath
     * @return
     */

    private static ArrayList<Shop> readProductsFromXML(String filepath){
        ArrayList<Shop> shop=new ArrayList<>();
        try{

            //documentbuilder and documentbuilderfactory in multi line configuration

            DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder=documentBuilderFactory.newDocumentBuilder();
            Document document=documentBuilder.parse(filepath);

            //creating root element

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

                            //switch case for the different xml handles.

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
