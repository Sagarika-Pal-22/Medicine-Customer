package myrehabcare.in.JSB;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

public class JsbValidations {
    Activity activity;

    public JsbValidations(Activity activity){
        this.activity = activity;
    }


    public static class Email {

        String email;
        boolean validEmail = false;
        String error = "";

        public Email(String email){
            this.email = email;
        }
        public Email(EditText emailEt){
            this.email = emailEt.getText().toString();
        }

        public String getEmail() {
            return email;
        }

        public String getError() {
            return error;
        }

        public boolean isValidEmail(){
            if (email.isEmpty()){
                validEmail = false;
                error = "Please Enter Email";
            }else validEmail = true;

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                validEmail = false;
                error = "Invalid Email";
            }


            return validEmail;
        }
    }


    public static class Password {

        String password;
        boolean validPassword = false;
        String error = "";

        public Password(String password){
            this.password = password;
        }
        public Password(EditText passwordEt){
            this.password = passwordEt.getText().toString();
        }

        public String getPassword() {
            return password;
        }

        public String getError() {
            return error;
        }

        public boolean isValidPassword(){
            if (password.isEmpty()){
                validPassword = false;
                error = "Please Enter Password";
            }else validPassword = true;

            if (password.length() < 6){
                validPassword = false;
                error = "Password must be 6-16 characters and letters";
            }


            return validPassword;
        }
    }

    public static class Phone {

        String phone;
        boolean validPhone = false;
        String error = "";

        public Phone(String phone){
            this.phone = phone;
        }
        public Phone(EditText phoneEt){
            this.phone = phoneEt.getText().toString();
        }

        public String getPhone() {
            return phone;
        }

        public String getError() {
            return error;
        }

        public boolean isValidPhone(){
            if (phone.length() != 10){
                validPhone = false;
                error = "Invalid Phone Number";
            }else validPhone = true;

            if (phone.isEmpty()){
                error = "Please Enter Phone";
            }
            return validPhone;
        }
    }

    public static class Name {

        String name;
        boolean validName = false;
        String error = "";

        public Name(String name){
            this.name = name;
        }
        public Name(EditText nameEt){
            this.name = nameEt.getText().toString();
        }

        public String getName() {
            return name;
        }

        public String getError() {
            return error;
        }

        public boolean isValidName(){
            if (name.isEmpty()){
                validName = false;
                error = "Please Enter Name";
            }else validName = true;

            if (name.length() < 2){
                validName = false;
                error = "Name must be 2-40 characters and letters";
            }


            return validName;
        }
    }
    public static class CnfPassword {
        String password,cnfPassword;
        boolean validName = false;
        String error = "";

        public CnfPassword(EditText password,EditText cnfPassword){
            this.password = password.getText().toString();
            this.cnfPassword = cnfPassword.getText().toString();
        }

        public String getPassword() {
            return password;
        }

        public String getError() {
            return error;
        }

        public boolean isCheckPassword() {
            if (!password.equals("") && !password.equals(cnfPassword)) {
                validName = false;
                error = "Incorrect Password.";
            }else{
                validName = true;
            }
            return validName;
        }
    }
}
