/*
Java Workshop 2020
First Application
25/06/2020
Moshe Weizman 305343931
Aharon Packter 201530508
 */

package com.example.takemypackage.UI.MainActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.takemypackage.Entities.Member;
import com.example.takemypackage.MyBroadcastService;
import com.example.takemypackage.R;
import com.example.takemypackage.UI.MainActivity.FriendsParcelsFragment.FriendsParcelsFragment;
import com.example.takemypackage.UI.MainActivity.HistoryParcelsFragment.HistoryParcelsFragment;
import com.example.takemypackage.UI.MainActivity.ProfileEdit.ProfileEditFragment;
import com.example.takemypackage.UI.MainActivity.RegisteredParcelsFragment.RegisteredParcelsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import static com.example.takemypackage.UI.Login.LoginActivity.LoginActivity.MEMBER_KEY;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private FirebaseAuth auth;
    private Fragment fragment;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ImageView imageViewNav;
    private TextView textName;
    Member member;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //an authenticated user that is currently logged in
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        //setting intent for the fragments with the users details
        Intent myIntent = getIntent();
        member = (Member) myIntent.getSerializableExtra(MEMBER_KEY);
        getIntent().putExtra(MEMBER_KEY, member);

        //setting intent for the background service with the user's details
        serviceIntent = new Intent(this, MyBroadcastService.class);
        serviceIntent.putExtra(MEMBER_KEY, member);
        startService(serviceIntent);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        View header = nvDrawer.getHeaderView(0);
        imageViewNav = header.findViewById(R.id.imageViewNav);
        textName = header.findViewById(R.id.textName);
        Glide.with(getBaseContext()).load(member.getImageFirebaseUri()).centerCrop().override(150, 150).into(imageViewNav);
        textName.setText(member.getfName() +"  "+ member.getlName());
    }
//------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
//------------------------------------------------------------------------

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_registered_parcels_fragment:
                fragment = new RegisteredParcelsFragment();
                break;
            case R.id.nav_friend_parcels_fragment:
                fragment = new FriendsParcelsFragment();
                break;
            case R.id.nav_history_parcels_fragment:
                fragment = new HistoryParcelsFragment();
                break;
            case R.id.profile_update:
                fragment = new ProfileEditFragment();
                break;
            case R.id.sign_out:
                signOut();
                break;
        }

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
//------------------------------------------------------------------------

    private void signOut() {
        auth.signOut();
    }
}
