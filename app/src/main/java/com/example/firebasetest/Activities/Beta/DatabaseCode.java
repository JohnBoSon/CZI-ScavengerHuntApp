/*package com.example.firebasetest.Activities.Beta;

import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.Activities.Classes.User;

public class DatabaseCode {
    private DatabaseReference mDatabase;
    mDatabase = FirebaseDatabase.getInstance().getReference("SH");

    boolean onGoing = true;
    String id =  "";
    String ownerId = "";
    String title = "";
    String description = "";

    SH artMusemSH =  SH(id, ownerId, title, description);

    mDatabase.child("SH").child(userId).setValue(artMusemSH);


}


private void write(String userId, String name, String email) {
    User user = new User(name, email);
    mDatabase.child("users").child(userId).setValue(user);
    mDatabase.child("users").child(userId).child("username").setValue(name);
    String key = mDatabase.child("posts").push().getKey();
    Map<String, SH> childUpdates = new HashMap<>();
    childUpdates.put("/SH/" + key, postValues);
    childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

    mDatabase.updateChildren(childUpdates);
}





ref.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Post post = dataSnapshot.getValue(Post.class);
        System.out.println(post);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        System.out.println("The read failed: " + databaseError.getCode());
    }
});*/