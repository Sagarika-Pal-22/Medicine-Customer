package myrehabcare.in.Callback;

import myrehabcare.in.Classes.User;

public interface EaSaGetUser {
    void Error(String error);
    void Success(User user,String message);
}
