package kz.tengrilab.diplom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<Message> adapter;
    LinearLayout activity_main;
    ImageButton send;
    EditText input;
    ListView listMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        activity_main = (LinearLayout) findViewById(R.id.activity_main);
        listMessages = (ListView) findViewById(R.id.listView);
        send = (ImageButton) findViewById(R.id.send_btn);
        input = (EditText) findViewById(R.id.input);
//
//        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("users");
//        databaseReference.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //databaseReference.removeValue();
//        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference();
//        databaseReference.removeValue();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), SIGN_IN_REQUEST_CODE);
        } else {
            displayChat();
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = input.getText().toString();
                if (message.equals("")) {
                    Toast.makeText(getApplicationContext(), "Input empty", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference("message").push().setValue(new Message(message, FirebaseAuth.getInstance().getCurrentUser().getEmail()));

                    input.setText("");
                }
            }
        });

        addEmail();

    }


    public void addEmail(){

        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> users = new ArrayList<String>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    users.add(dataSnapshot.getValue().toString());
                }
                String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                Log.d("User", user);
                if (users.contains(user)){
                    Log.d("Main2", "SSsasa");
                }
                else {
                    databaseReference.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }
                Log.d("MAin", users.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        databaseReference.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

    private DatabaseReference myRef;

    @SuppressLint("ResourceAsColor")
    private void displayChat() {
        myRef = FirebaseDatabase.getInstance().getReference("message");
        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setQuery(myRef, Message.class)
                .setLayout(R.layout.item_message)
                .build();

        adapter = new FirebaseListAdapter<Message>(options) {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateView(View v, Message model, int position) {

                TextView textMessage, author, timeMessage, tvCipher;
                textMessage = (TextView) v.findViewById(R.id.tvMessage);
                author = (TextView) v.findViewById(R.id.tvUser);
                timeMessage = (TextView) v.findViewById(R.id.tvTime);

                textMessage.setText(model.getTextMessage());
                author.setText(model.getAutor());
                timeMessage.setText(DateFormat.format("HH:mm:ss", model.getTimeMessage()));

            }
        };
        adapter.startListening();
        listMessages.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_main, "Вход выполнен", Snackbar.LENGTH_SHORT).show();
                displayChat();
            } else {
                Snackbar.make(activity_main, "Вход не выполнен", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.info){
            startActivity(new Intent(getApplicationContext(), InfoActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}