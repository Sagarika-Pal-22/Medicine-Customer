package myrehabcare.in.Classes;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Appointments implements Comparator<Appointments>,Serializable {

    private Doctors doctors;
    private Date appointmentsDate;
    private String status;

    public Appointments(Doctors doctors, Date appointmentsDate, String status) {
        this.doctors = doctors;
        this.appointmentsDate = appointmentsDate;
        this.status = status;
    }

    public Appointments() {
    }

    public Doctors getDoctors() {
        return doctors;
    }

    public void setDoctors(Doctors doctors) {
        this.doctors = doctors;
    }

    public Date getAppointmentsDate() {
        return appointmentsDate;
    }

    public void setAppointmentsDate(Date appointmentsDate) {
        this.appointmentsDate = appointmentsDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compare(Appointments o1, Appointments o2) {
        return o1.getAppointmentsDate().compareTo(o2.getAppointmentsDate());
    }
}
