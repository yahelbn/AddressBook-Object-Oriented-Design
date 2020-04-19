
public abstract class AdressBookAbstractDeco implements AdressBookInterface{
	protected AddressBookPane adressBookPaneDeco;

	   public AdressBookAbstractDeco(AddressBookPane adressBookPaneDeco){
	      this.adressBookPaneDeco = adressBookPaneDeco;
	   }

	   public void draw(){
		   adressBookPaneDeco.draw();
	   }


}
