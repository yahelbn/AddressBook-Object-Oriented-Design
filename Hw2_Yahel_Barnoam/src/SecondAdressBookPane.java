import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.FlowPane;

public class SecondAdressBookPane extends AdressBookAbstractDeco {
	private AddButton jbtAdd;
	private UndoButton jbUndo;
	private RedoButton jbRedo;
	private static Originator originator = new Originator();
	private static CareTaker careTaker = new CareTaker();
	private boolean IfIdidUndo = false;

	public SecondAdressBookPane(AddressBookPane adressBookPaneDeco) {
		super(adressBookPaneDeco);
		setSecondAdress(adressBookPaneDeco);
	}

	@Override
	public void draw() {
		adressBookPaneDeco.draw();
		setSecondAdress(adressBookPaneDeco);
	}

	private void setSecondAdress(AddressBookPane adressBookPaneDeco) {
		System.out.println("Second adressbook pane created");
		jbUndo = new UndoButton(adressBookPaneDeco, adressBookPaneDeco.getRaf());
		jbRedo = new RedoButton(adressBookPaneDeco, adressBookPaneDeco.getRaf());
		jbtAdd = new AddButton(adressBookPaneDeco, adressBookPaneDeco.getRaf());
		jbtAdd.setOnAction(e -> jbtAdd.Execute());
		jbUndo.setOnAction(e -> jbUndo.Execute());
		jbRedo.setOnAction(e -> jbRedo.Execute());

		adressBookPaneDeco.getJpButton().getChildren().addAll(jbUndo, jbRedo, jbtAdd);

	}

	public AddressBookPane getPane() {
		return (AddressBookPane) this.adressBookPaneDeco;
	}

	public void setAddressBookPane(AddressBookPane adressBookPaneDeco) {
		this.adressBookPaneDeco = adressBookPaneDeco;
	}

	class AddButton extends CommandButton {
		public AddButton(AddressBookPane pane, RandomAccessFile r) {
			super(pane, r);
			this.setText("Add");
		}

		@Override
		public void Execute() {
			writeAddress();
		}
	}

	class UndoButton extends CommandButton {
		public UndoButton(AddressBookPane pane, RandomAccessFile r) {
			super(pane, r);
			this.setText("Undo");

		}

		@Override
		public void Execute() {

			try {
				if (raf.length() == 0) {
					return;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				if (raf.length() > 0) {

					raf.seek(raf.length() - RECORD_SIZE * 2);
					String name = FixedLengthStringIO.readFixedLengthString(NAME_SIZE, raf);
					String Street = FixedLengthStringIO.readFixedLengthString(STREET_SIZE, raf);
					String city = FixedLengthStringIO.readFixedLengthString(CITY_SIZE, raf);
					String state = FixedLengthStringIO.readFixedLengthString(STATE_SIZE, raf);
					String zip = FixedLengthStringIO.readFixedLengthString(ZIP_SIZE, raf);
					originator.setState(zip, name, Street, city, state);
					Memento meme = originator.saveStateToMemento();
					System.out.println(meme.toString());
					careTaker.add(meme);

					raf.setLength(raf.length() - RECORD_SIZE * 2);
					IfIdidUndo = true;
					if (raf.length() == 0) {
						IfEmpty();
					}
					AddressBookPane.jbtFirst.Execute();
				}
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

	class RedoButton extends CommandButton {
		public RedoButton(AddressBookPane pane, RandomAccessFile r) {
			super(pane, r);
			this.setText("Redo");
		}

		@Override
		public void Execute() {

			try {
				if ((raf.length() == 0 && careTaker.getIndex()==-1 )||IfIdidUndo==false) {
					
					return;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int indexDelete = careTaker.getIndex();
			if (careTaker.getIndex() > -1) {
				Memento m = careTaker.getPrev();
				System.out.println(m.toString());
				try {
					raf.seek(raf.length());

					FixedLengthStringIO.writeFixedLengthString(m.getName(), NAME_SIZE, raf);
					FixedLengthStringIO.writeFixedLengthString(m.getStreet(), STREET_SIZE, raf);
					FixedLengthStringIO.writeFixedLengthString(m.getCity(), CITY_SIZE, raf);
					FixedLengthStringIO.writeFixedLengthString(m.getState(), STATE_SIZE, raf);
					FixedLengthStringIO.writeFixedLengthString(m.getZip(), ZIP_SIZE, raf);
					careTaker.mementoList.remove(indexDelete);

					AddressBookPane.jbtLast.fire();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class Memento {
	private String zip;
	private String name;
	private String Street;
	private String city;
	private String state;

	public Memento(String zip, String name, String Street, String city, String state) {
		this.zip = zip;
		this.name = name;
		this.Street = Street;
		this.city = city;
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return Street;
	}

	public void setStreet(String street) {
		Street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "Memento [zip=" + zip + ", name=" + name + ", Street=" + Street + ", city=" + city + ", state=" + state
				+ "]";
	}

}

class CareTaker {
	static List<Memento> mementoList = new ArrayList<Memento>();
	static int index;

	public static void setMementoListSize(List<Memento> mementoList) {
		CareTaker.mementoList = mementoList;
	}

	public static int getSize() {
		return mementoList.size() - 1;
	}

	public static int getIndex() {
		return index;
	}

	public static void setIndex(int index) {
		CareTaker.index = mementoList.size();
	}

	public CareTaker() {
		index = mementoList.size();
	}

	public void add(Memento state) {
		if (state != null) {
			mementoList.add(state);
			index = mementoList.size() - 1;
		}
	}

	public Memento getPrev() {
		if (mementoList.isEmpty() || index < 0) {
			return null;
		}

		return mementoList.get(index--);
	}

	public Memento getNext() {
		if (mementoList.isEmpty() || index >= mementoList.size() - 1) {
			return null;
		}
		return mementoList.get(++index);
	}

}

// ***** Originator Class *****///
class Originator {
	private String zip;
	private String name;
	private String Street;
	private String city;
	private String state;

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return Street;
	}

	public void setStreet(String street) {
		Street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setState(String zip, String name, String Street, String city, String state) {
		this.zip = zip;
		this.name = name;
		this.Street = Street;
		this.city = city;
		this.state = state;
	}

	public Memento saveStateToMemento() {
		return new Memento(zip, name, Street, city, state);
	}

	public void getStateFromMemento(Memento memento) {
		if (memento != null) {
			this.zip = memento.getZip();
			this.name = memento.getName();
			this.Street = memento.getStreet();
			this.city = memento.getCity();
			this.state = memento.getState();
		}
	}
}
