package com.akashraje.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akashraje.chatapp.DataModel.TextMessage;
import com.akashraje.chatapp.Utils.Constants;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;
import java.util.Locale;

import static com.akashraje.chatapp.R.id.time;

public class MainActivity extends AppCompatActivity {

    EditText txtInput;
    private FirebaseListAdapter<TextMessage> adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtInput = (EditText) findViewById(R.id.txtInput);
        listView = (ListView) findViewById(R.id.listChatMsgs);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),
                    Constants.SIGN_IN_REQUEST_CODE);
        } else {
            showChatMessages();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSend);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txtInput.getText().toString();
                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                if (!msg.equalsIgnoreCase("")) {


                    TextMessage txtMessage = new TextMessage();
                    txtMessage.setText(msg);
                    txtMessage.setUserName(userName);
                    txtMessage.setSentTime(System.currentTimeMillis());


                    FirebaseDatabase.getInstance().getReference()
                            .push()
                            .setValue(txtMessage, userName);

                    listView.smoothScrollToPosition(adapter.getCount() - 1);

                    txtInput.setText("");

                } else {
                    txtInput.setError(getString(R.string.txt_empty));
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        getString(R.string.welcome) +" "+ FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getDisplayName(),
                        Toast.LENGTH_LONG)
                        .show();
                //We can show a list of conversations as well
                showChatMessages();

            } else {
                Toast.makeText(this,
                        getString(R.string.sign_out_success),
                        Toast.LENGTH_LONG)
                        .show();
            }

        }
    }

    private void showChatMessages() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Query query = dbRef.orderByChild("sentTime");

        adapter = new FirebaseListAdapter<TextMessage>(this, TextMessage.class,
                R.layout.text_message, query) {
            @Override
            protected void populateView(View v, TextMessage model, int position) {

                TextView txtMsg = (TextView) v.findViewById(R.id.txtMsg);
                TextView txtUserName = (TextView) v.findViewById(R.id.txtUserName);
                TextView txtSentTime = (TextView) v.findViewById(R.id.txtSentTime);

                txtMsg.setText(model.getText());
                txtUserName.setText(model.getUserName());

                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(model.getSentTime());
                String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();

                txtSentTime.setText(date);

                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                if (currentUser.equalsIgnoreCase(model.getUserName())) {
                    v.setBackgroundColor(Color.parseColor("#ADDFA7"));
                    txtMsg.setGravity(Gravity.RIGHT);
                } else {
                    v.setBackgroundColor(Color.TRANSPARENT);
                    txtMsg.setGravity(Gravity.LEFT);
                }
            }
        };

        listView.setAdapter(adapter);

    }

    private void showToast(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_signOut) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    showToast(getString(R.string.sign_out_success));
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),
                            Constants.SIGN_IN_REQUEST_CODE);
                }
            });
        }
        return true;
    }
}
