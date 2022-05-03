import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<shop> shop = readProductsFromXML("src/main/resources/shop.xml");
        menu(shop);
    }
}
