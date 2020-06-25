package com.example.takemypackage.Data;

import androidx.annotation.NonNull;
import com.example.takemypackage.Entities.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class PendingParcelsFirebaseManager {
    private static List<PendingParcel> pendingParcelList = new ArrayList<PendingParcel>();

    public interface Action<T> {
        void onSuccess(T obj);

        void onFailure(Exception exception);
    }

    public interface NotifyDataChange<T> {
        void OnDataChanged(T obj);

        void onFailure(Exception exception);
    }

    /**
     * Static reference to the parcels database
     */
    public static DatabaseReference parcelRef;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        parcelRef = database.getReference("PendingParcel");
    }

    public static ChildEventListener parcelRefChildEventListener;
    public static void NotifyToParcelList(final NotifyDataChange<List<PendingParcel>> notifyDataChange) {
        if (notifyDataChange != null) {
            if (parcelRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify parcel list"));
                return;
            }
            pendingParcelList.clear();
            parcelRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        PendingParcel pendingParcel = new PendingParcel();
                        pendingParcel = child.getValue(PendingParcel.class);
                        pendingParcel.getParcelDetails().set_parcelID(child.getKey());
                        pendingParcelList.add(pendingParcel);
                    }
                    notifyDataChange.OnDataChanged(pendingParcelList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    PendingParcel pendingParcel = new PendingParcel();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        pendingParcel = child.getValue(PendingParcel.class);
                        pendingParcel.getParcelDetails().set_parcelID(child.child(child.getKey()).getKey());
                        for (int i = 0; i < pendingParcelList.size(); i++) {
                            if (pendingParcelList.get(i).getParcelDetails().getParcelID().equals(pendingParcel.getParcelDetails().getParcelID())) {
                                pendingParcelList.set(i, pendingParcel);
                                break;
                            }
                        }
                    }
                    notifyDataChange.OnDataChanged(pendingParcelList);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    PendingParcel pendingParcel = new PendingParcel();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        pendingParcel = child.getValue(PendingParcel.class);
                        pendingParcel.getParcelDetails().set_parcelID(child.child(child.getKey()).getKey());
                    }
                    for (int i = 0; i < pendingParcelList.size(); i++) {
                        if (pendingParcelList.get(i).getParcelDetails().getParcelID().equals(pendingParcel.getParcelDetails().getParcelID())) {
                            pendingParcelList.remove(i);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(pendingParcelList);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            parcelRef.addChildEventListener(parcelRefChildEventListener);
        }
    }

    public static void stopNotifyToPendingList() {
        if (parcelRefChildEventListener != null) {
            parcelRef.removeEventListener(parcelRefChildEventListener);
            parcelRefChildEventListener = null;
        }
    }

    public static void addOrUpdateMemberToOptionalDeliveries(PendingParcel pendingParcel, DeliveryPerson deliveryPerson, final Action<String> action) {
        DatabaseReference DeliveryPersonRef = parcelRef.child(pendingParcel.getParcelDetails().getRecipientPhone()).child(pendingParcel.getParcelDetails().getParcelID()).child("optionalDeliveries");

        DeliveryPersonRef.child(deliveryPerson.getPhone())
                .setValue(deliveryPerson).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess("Registration was successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
            }
        });

    }

    public static void deletePendingParcel(PendingParcel pendingParcel, final Action<String> action) {
        Parcel parcel = pendingParcel.getParcelDetails();
        parcelRef.child(parcel.getRecipientPhone()).child(parcel.getParcelID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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


    public static void deleteAllPedingsParcelsOfMember(String phone, final Action<String> action) {
        parcelRef.child(phone).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
