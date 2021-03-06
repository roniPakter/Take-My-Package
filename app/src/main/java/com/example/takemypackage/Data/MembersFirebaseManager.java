/*
Java Workshop 2020
First Application
25/06/2020
Moshe Weizman 305343931
Aharon Packter 201530508
 */

package com.example.takemypackage.Data;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.takemypackage.Entities.Member;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MembersFirebaseManager {

    /**
     * Interface for listeners who want to add or update
     * @param <T>
     */
    public interface Action<T> {
        void onSuccess(T obj);

        void onFailure(Exception exception);
    }
    //Reference to firebase on  members
    public static DatabaseReference memberRef;
    //Reference to storage in firebase
    public static StorageReference imagesRef;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        memberRef = database.getReference("Members");
        imagesRef = FirebaseStorage.getInstance().getReference();
    }

    /**
     * add member to firebase and listner if the action is successful
     * @param member
     * @param action
     */
    public static void addMemberToFirebase(final Member member, final Action<String> action) {
        memberRef.child(member.getPhone()).setValue(member).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(member.getPhone());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
    }

    /**
     * Update User Profile and listner if the action is successful
     * @param phoneOldMember
     * @param newMember
     * @param action
     */
    public static void UpdateUserProfile(final String phoneOldMember, final Member newMember, final Action<String> action) {

        memberRef.child(phoneOldMember).setValue(newMember).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess("Update successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    /**
     * delete Member from firebase
     * @param phoneMember
     * @param action
     */
    public static void deleteMember(final String phoneMember, final Action<String> action) {
        memberRef.child(phoneMember).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess("Deletion was successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });
    }
//-----------------------------------------------------------------------------
    /**
     * add Image Member to the storage of firebase
     * @param member
     * @param action
     */
    public static void addImageMember(final Member member, final Action<String> action) {
        if (member.getImageLocalUri() != null) {
            // upload image
            imagesRef = imagesRef.child("images").child(System.currentTimeMillis() + ".jpg");
            //final StorageReference finalImagesRef = imagesRef;
            imagesRef.putFile(member.getImageLocalUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get a URL to the uploaded content
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                    while (!downloadUrl.isComplete()) ;
                    Uri url = downloadUrl.getResult();
                    member.setImageFirebaseUri(url.toString());
                    addMemberToFirebase(member, action);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    action.onFailure(exception);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
        } else action.onFailure(new Exception("select image first ..."));
    }
}
