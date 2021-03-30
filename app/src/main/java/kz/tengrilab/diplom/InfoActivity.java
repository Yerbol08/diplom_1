package kz.tengrilab.diplom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {



    ListView listUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        listUsers = (ListView) findViewById(R.id.ListView);
        ArrayList<String> userList = new ArrayList<String>();

      //  displayChat();
        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    userList.add(dataSnapshot.getValue().toString());
                }
                String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                Log.d("User", userList.toString());
                if (userList.contains(user)){
                    Log.d("Main2", "SSsasa");
                }
                else {
                    databaseReference.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }
                Log.d("MAin", userList.toString());

                String[] stringArray = userList.toArray(new String[0]);
                //String[] stringArray ={ "Бразилия", "Аргентина", "Колумбия", "Чили", "Уругвай"};
//                int len = removeDuplicateElements(stringArray, users.size());
                for (int i = 0; i < stringArray.length; i++){
                    Log.d("List", stringArray[i]);
                }
//
                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.layout_users, R.id.textView_users,  stringArray);
                listUsers.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public static int removeDuplicateElements(String arr[], int n){
        if (n==0 || n==1){
            return n;
        }
        String[] temp = new String[n];
        int j = 0;
        for (int i=0; i<n-1; i++){
            if (arr[i] != arr[i+1]){
                temp[j++] = arr[i];
            }
        }
        temp[j++] = arr[n-1];
        for (int i=0; i<j; i++){
            arr[i] = temp[i];
        }
        return j;
    }

    private DatabaseReference myRef;


    public void MessageRemove(View view) {
        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("message");
        databaseReference.removeValue();
    }

    public void Logout(View view){
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(getApplicationContext(), "Выход выполнен", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });
    }

    private FirebaseListAdapter<Users> adapter;
    @SuppressLint("ResourceAsColor")
    private void displayChat() {
        myRef = FirebaseDatabase.getInstance().getReference("users");
        FirebaseListOptions<Users> options = new FirebaseListOptions.Builder<Users>()
                .setQuery(myRef, Users.class)
                .setLayout(R.layout.layout_users)
                .build();

        adapter = new FirebaseListAdapter<Users>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Users model, int position) {
                TextView textUsers;
                textUsers = (TextView) v.findViewById(R.id.textUser);
                textUsers.setText(model.getUsername());
            }

        };
        adapter.startListening();
        listUsers.setAdapter(adapter);
    }
}