import java.util.HashMap;
import java.util.List;

public class BiographyEdit extends Edit {
    Person person;
    private BiographyEdit(Person person,User publisher, String targetDetail, String newDetail) {
        super(publisher, targetDetail, newDetail);
        this.person = person;

    }
    public static void addBiographyEdit(Person person,User publisher, String targetDetail, String newDetail){
        BiographyEdit edit = new BiographyEdit(person,publisher,targetDetail,newDetail);
        DataBase.getBiographyEdits().add(edit);
        publisher.getEdits().add(edit);
    }
    @Override
    int confirmEdit(User admin){
        if(admin.getUserType()==UserType.ADMIN){
            //setting the status to true
            this.confirmationStatus = 1;
            //applying the change
            switch (this.targetDetail){
                case "name":
                    this.person.setName(this.newDetail);
                    break;
                case "lastname":
                    this.person.setLastname(this.newDetail);
                    break;
                case "age":
                    this.person.setAge(Integer.parseInt(this.newDetail));
                    break;
                case "nationalID":
                    this.person.setNationalID(this.newDetail);
                    break;
                case "gender":
                    switch (this.newDetail){
                        case "male":
                            this.person.setGender(Gender.MALE);
                            break;
                        case "female":
                            this.person.setGender(Gender.FEMALE);
                            break;
                        case "other":
                            this.person.setGender(Gender.OTHER);
                            break;
                    }
                    break;
            }
            return 1;
        }
        return 0;
    }

    @Override
    int rejectEdit(User admin) {
        if(admin.getUserType()==UserType.ADMIN){
            this.confirmationStatus = 2;
            return 1;
        }
        return 0;
    }
    public static HashMap<Integer,BiographyEdit> showBiographyEdits(){
        HashMap<Integer,BiographyEdit> biographyEditHashMap = new HashMap<>();
        List<BiographyEdit> biographyEdits  = DataBase.getBiographyEdits();
        int i = 1;
        if(biographyEdits.isEmpty())
            return null;
        for (BiographyEdit biographyEdit:biographyEdits) {
            System.out.printf("%d. detail to edit: %s\nnew offered detail: %s\n",i,biographyEdit.getTargetDetail(),biographyEdit.getNewDetail());
            biographyEditHashMap.put(i,biographyEdit);
            i++;
        }
        return biographyEditHashMap;
    }
}
