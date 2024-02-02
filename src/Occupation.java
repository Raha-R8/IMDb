import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.io.Serializable;
public class Occupation implements Serializable{
    String occupation;
    //****************************************************
    //getters and setters

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    private Occupation(String occupation) {
        this.occupation = occupation;
        DataBase.getOccupationHashMap().put(occupation, this);
    }
    public static Occupation addOccupation(String occupationStr){
        Occupation occupation = new Occupation(occupationStr);
        DataBase.getOccupationHashMap().put(occupationStr, occupation);
        return occupation;
    }
    public static HashMap<Integer,Occupation> showOccupations(){
        HashMap<Integer,Occupation> occupationHashMap = new HashMap<>();
        int i = 1;
        Collection<Occupation> occupations = DataBase.getOccupationHashMap().values();
        if(occupations.isEmpty()){
            System.out.println("This list is empty");
            return null;
        }
        for(Occupation occupation: occupations) {
            System.out.printf("%d. %s",i,occupation);
            occupationHashMap.put(i,occupation);
            i++;
        }
        return occupationHashMap;
    }
}
