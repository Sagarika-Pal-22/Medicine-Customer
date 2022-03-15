package myrehabcare.in.Classes;

import java.io.Serializable;
import java.util.Comparator;

public class Doctors implements Comparator<Doctors>, Serializable {

    private String id;
    private String contact;
    private String name;
    private String date;
    private String status;
    private String type;
    private float rating;
    private String image;
    private String address;
    private String about_us;
    private String uploadPrescription;
    private String service_type;
    private String patient_serviceType;
    private String patient_category;
    private String patient_name;
    private String patient_age;
    private String patient_gender;
    private String patient_problem;
    private String patient_visitType;
    private String patient_scheduleDate;
    private String patient_scheduleTime;
    private String patient_fees;
    private String patient_discount;
    private String patient_address;
    private String patient_prescriptionImage;
    private String patient_tryAgain;

    public Doctors() {
    }

    public Doctors(String id, String contact, String name, String date, String status, String type, float rating, String image, String address, String about_us) {
        this.id = id;
        this.contact = contact;
        this.name = name;
        this.date = date;
        this.status = status;
        this.type = type;
        this.rating = rating;
        this.image = image;
        this.address = address;
        this.about_us = about_us;
    }

    public Doctors(String name, String type, float rating, String image, String address) {
        this.id = "id";
        this.contact = "contact";
        this.name = name;
        this.date = "date";
        this.status = status;
        this.type = type;
        this.rating = rating;
        this.image = image;
        this.address = address;
        this.about_us = "";
    }
    public Doctors(String name, String type, float rating, String image, String address,String uploadPrescription,String service_type) {
        this.id = "id";
        this.contact = "contact";
        this.name = name;
        this.date = "date";
        this.status = status;
        this.type = type;
        this.rating = rating;
        this.image = image;
        this.address = address;
        this.about_us = "";
        this.uploadPrescription = uploadPrescription;
        this.service_type = service_type;
    }

    public Doctors(String name, String type, float rating, String image, String address, String uploadPrescription, String service_type, String patient_serviceType, String patient_category, String patient_name, String patient_age, String patient_gender, String patient_problem, String patient_visitType, String patient_scheduleDate, String patient_scheduleTime, String patient_fees, String patient_discount, String patient_address, String patient_prescriptionImage,String tryAgain) {
        this.id = id;
        this.contact = contact;
        this.name = name;
        this.date = date;
        this.status = status;
        this.type = type;
        this.rating = rating;
        this.image = image;
        this.address = address;
        this.about_us = about_us;
        this.uploadPrescription = uploadPrescription;
        this.service_type = service_type;
        this.patient_serviceType = patient_serviceType;
        this.patient_category = patient_category;
        this.patient_name = patient_name;
        this.patient_age = patient_age;
        this.patient_gender = patient_gender;
        this.patient_problem = patient_problem;
        this.patient_visitType = patient_visitType;
        this.patient_scheduleDate = patient_scheduleDate;
        this.patient_scheduleTime = patient_scheduleTime;
        this.patient_fees = patient_fees;
        this.patient_discount = patient_discount;
        this.patient_address = patient_address;
        this.patient_prescriptionImage = patient_prescriptionImage;
        this.patient_tryAgain = tryAgain;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAbout_us() {
        return about_us;
    }

    public void setAbout_us(String about_us) {
        this.about_us = about_us;
    }

    public String getUploadPrescription() {
        return uploadPrescription;
    }

    public void setUploadPrescription(String uploadPrescription) {
        this.uploadPrescription = uploadPrescription;
    }

    public String getPatient_serviceType() {
        return patient_serviceType;
    }

    public void setPatient_serviceType(String patient_serviceType) {
        this.patient_serviceType = patient_serviceType;
    }

    public String getPatient_category() {
        return patient_category;
    }

    public void setPatient_category(String patient_category) {
        this.patient_category = patient_category;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getPatient_age() {
        return patient_age;
    }

    public void setPatient_age(String patient_age) {
        this.patient_age = patient_age;
    }

    public String getPatient_gender() {
        return patient_gender;
    }

    public void setPatient_gender(String patient_gender) {
        this.patient_gender = patient_gender;
    }

    public String getPatient_problem() {
        return patient_problem;
    }

    public void setPatient_problem(String patient_problem) {
        this.patient_problem = patient_problem;
    }

    public String getPatient_visitType() {
        return patient_visitType;
    }

    public void setPatient_visitType(String patient_visitType) {
        this.patient_visitType = patient_visitType;
    }

    public String getPatient_scheduleDate() {
        return patient_scheduleDate;
    }

    public void setPatient_scheduleDate(String patient_scheduleDate) {
        this.patient_scheduleDate = patient_scheduleDate;
    }

    public String getPatient_scheduleTime() {
        return patient_scheduleTime;
    }

    public void setPatient_scheduleTime(String patient_scheduleTime) {
        this.patient_scheduleTime = patient_scheduleTime;
    }

    public String getPatient_fees() {
        return patient_fees;
    }

    public void setPatient_fees(String patient_fees) {
        this.patient_fees = patient_fees;
    }

    public String getPatient_discount() {
        return patient_discount;
    }

    public void setPatient_discount(String patient_discount) {
        this.patient_discount = patient_discount;
    }

    public String getPatient_address() {
        return patient_address;
    }

    public void setPatient_address(String patient_address) {
        this.patient_address = patient_address;
    }

    public String getPatient_prescriptionImage() {
        return patient_prescriptionImage;
    }

    public void setPatient_prescriptionImage(String patient_prescriptionImage) {
        this.patient_prescriptionImage = patient_prescriptionImage;
    }

    public String getPatient_tryAgain() {
        return patient_tryAgain;
    }

    public void setPatient_tryAgain(String patient_tryAgain) {
        this.patient_tryAgain = patient_tryAgain;
    }

    @Override
    public int compare(Doctors o1, Doctors o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
