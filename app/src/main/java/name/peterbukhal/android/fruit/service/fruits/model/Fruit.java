package name.peterbukhal.android.fruit.service.fruits.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Peter Bukhal petr.bukhal <at> doconcall.ru
 */
public final class Fruit implements Parcelable {

    private int id;
    private String name;

    public Fruit(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Fruit(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }

    public static final Creator<Fruit> CREATOR = new Creator<Fruit>() {
        @Override
        public Fruit createFromParcel(Parcel in) {
            return new Fruit(in);
        }

        @Override
        public Fruit[] newArray(int size) {
            return new Fruit[size];
        }
    };

    @Override
    public String toString() {
        return "Fruit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
