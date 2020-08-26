package in.co.tsmith.androidso;

import java.io.Serializable;

//Crested by Pavithra on 22-08-2020
public class CustomerPL implements Serializable {

    public CustomerPL() {
        Code = "";
        HUID = "";
        Name = "";
        Phone = "";
        AddressLine1 = "";
        AddressLine2 = "";
    }

    public int Id ;
    public String Code ;
    public String HUID ;
    public String Name ;
    public String Phone ;
    public String AddressLine1 ;
    public String AddressLine2 ;
    public int DoctorId;
    public int ErrorStatus ;
    public String Message ;
}
