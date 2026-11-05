/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;
import java.util.Set;//تخزن البيانات بدوم تكرار
import java.util.HashSet;//نستخدمها لتنفيذSet  
import java.util.Collections;// كلاس جاهز فيه ميثود تساعد على تجميع البيانات

/**
 *
 * @author user
 */
public class UserManager {
    private Set<String> users;
    
    
    public UserManager() {
    users = Collections.synchronizedSet(new HashSet<>());//synchronizedSet :تساعدنا  غي التعامل مع  اكثر من عميل
}
    
    public synchronized boolean registerUser(String username) {

    if (username == null) {
        return false;
    }
    

    return users.add(username.trim());//وtrim:ميثود تشيل المساحات 
}
    
    public boolean userExists(String username) {

    if (username == null) {
        return false;
    }

    return users.contains(username.trim());//contains: ميثود موجوده في مكتبه Collections
}
    
    public String findUser(String username) {

    if (userExists(username)) {
        return username.trim();
    }

    return null;
}
    
    public Set<String> getAllUsers() {

    synchronized (users) {
        return new HashSet<>(users);
    }
}
    public boolean removeUser(String username) {

    if (username == null) {
        return false;
    }

    return users.remove(username.trim());
}
    
    
    
    
}
