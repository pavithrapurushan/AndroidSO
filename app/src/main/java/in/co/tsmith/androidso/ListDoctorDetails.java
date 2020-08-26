package in.co.tsmith.androidso;

import java.util.ArrayList;

public class ListDoctorDetails extends CommonPL {

    public ListDoctorDetails()
    {
        lst = new ArrayList<>();
        filter = "";
    }

    public ArrayList<DoctorDetailsPL> lst ;
    public String filter ;
}
