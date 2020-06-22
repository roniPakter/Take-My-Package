package com.example.takemypackage.UI.MainActivity.RegisteredParcelsFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takemypackage.Data.PendingParcelsFirebaseManager;
import com.example.takemypackage.Entities.Member;
import com.example.takemypackage.Entities.PendingParcel;
import com.example.takemypackage.R;
import static com.example.takemypackage.UI.Login.LoginActivity.LoginActivity.MEMBER_KEY;

import java.util.ArrayList;
import java.util.List;

public class RegisteredParcelsFragment extends Fragment {
   private RecyclerView _recyclerView;
   List<PendingParcel> registeredParcels;
   private Member member;

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.registered_parcels_fragment, container, false);
      final RecyclerView recyclerView = view.findViewById(R.id.registeredParcelsRecyclerView);
      recyclerView.setHasFixedSize(true);
      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
      recyclerView.setLayoutManager(layoutManager);

      Intent intent = this.getActivity().getIntent();
      member = (Member)intent.getSerializableExtra(MEMBER_KEY);

      PendingParcelsFirebaseManager.NotifyToParcelList(new PendingParcelsFirebaseManager.NotifyDataChange<List<PendingParcel>>() {
         @Override
         public void OnDataChanged(List<PendingParcel> obj) {
            if (recyclerView.getAdapter() == null){
               for (PendingParcel pendingParcel : obj) {
                  if (pendingParcel.getParcelDetails().getRecipientPhone().equals(member.getPhone())){
                     registeredParcels.add(pendingParcel);
                  }
               }
               recyclerView.setAdapter(new RegisteredParcelsRecyclerViewAdapter(registeredParcels, member, getContext()));
            }else recyclerView.getAdapter().notifyDataSetChanged();
         }

         @Override
         public void onFailure(Exception exception) {
            Toast.makeText(getContext(), "Failed to get your registered parcels data \n" + exception.toString(), Toast.LENGTH_LONG).show();
         }
      });
      return view;
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }
}
