package kz.tengrilab.diplom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Space;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
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

        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    userList.add(dataSnapshot.getValue().toString());
                }
                String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                if (!userList.contains(user)){
                    databaseReference.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }


                String[] stringArray = userList.toArray(new String[0]);

                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.layout_users, R.id.textView_users,  stringArray);
                listUsers.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void MessageRemove(View view) {
        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("message");
        databaseReference.removeValue();
        Toast.makeText(getApplicationContext(), "Данные из чата удалены", Toast.LENGTH_SHORT).show();
    }

    public void Logout(View view){
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Toast.makeText(getApplicationContext(), "Выход выполнен", Toast.LENGTH_SHORT).show();

                        moveTaskToBack(true);
                        finish();
                        startActivity(new Intent(getApplicationContext(), SplashActivity.class));

                    }
                });
    }

}