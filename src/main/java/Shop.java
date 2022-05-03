public class Shop
{
    private String Name;
    private int Price;
    private int Quantity;

    // set and get//
    public void setName(String name){
        this.Name = name;
    }
    public String getName(){
        return Name;
    }
    public void setPrice(int price){
        this.Price = price;
    }
    public Integer getPrice(){
        return Price;
    }
    public void setQuantity(int quantity){
        this.Quantity = quantity;
    }
    public Integer getQuantity(){
        return Quantity;
    }
    public Shop(String Name, int Price, int Quantity) {
        this.Name = Name;
        this.Price = Price;
        this.Quantity = Quantity;
    }
}
