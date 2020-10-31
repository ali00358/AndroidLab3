package com.example.androidlab2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";

    private ArrayList<Message> messageArr = new ArrayList<>();
    MyListAdapter myAdapter; //adapter to control items in the list view
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_linear);

        loadMessagesFromDatabase();

        boolean isTablet = findViewById(R.id.frameLayout) != null;
        ListView messages = findViewById(R.id.messages);
        messages.setAdapter(myAdapter = new MyListAdapter());

        messages.setOnItemClickListener((list, item, position, id) -> {

            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SELECTED, String.valueOf(item));
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            if(isTablet){
                DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment.
            } else {
                {
                    Intent nextActivity = new Intent(ChatActivity.this, EmptyActivity.class);
                    nextActivity.putExtras(dataToPass); //send data to next activity
                    startActivity(nextActivity); //make the transition
                }
            }

        });

        messages.setOnItemLongClickListener( (p, b, pos, id) -> {
            deleteMessage(pos);
            return true;
        });

        EditText editText = findViewById(R.id.editText);

        Button sendBtn = findViewById(R.id.sendButton);
        sendBtn.setOnClickListener( (click) -> {
            String text = editText.getText().toString();

            ContentValues row = new ContentValues();
            row.put(DBOpener.COL_TEXT, text);
            row.put(DBOpener.COL_OUTGOING, 1);

            long newID = db.insert(DBOpener.TABLE_NAME, null, row);

            Message newMessage = new Message(newID, text);
            editText.setText("");
            messageArr.add(newMessage);
            myAdapter.notifyDataSetChanged();
        });

        Button receiveBtn = findViewById(R.id.receiveButton);
        receiveBtn.setOnClickListener( (click) -> {
            String text = editText.getText().toString();
            ContentValues row = new ContentValues();
            row.put(DBOpener.COL_TEXT, text);
            row.put(DBOpener.COL_OUTGOING, 0); //0 to signify false

            //insert query
            long newID = db.insert(DBOpener.TABLE_NAME, null, row);

            //create new messge object and add to array
            Message newMessage = new Message(newID, text, 0); //create incoming message object
            editText.setText("");
            messageArr.add(newMessage);
            myAdapter.notifyDataSetChanged();
        });
    }

    private void loadMessagesFromDatabase(){
        DBOpener opener = new DBOpener(this);

        db = opener.getWritableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM " + DBOpener.TABLE_NAME, null);
        DBOpener.printCursor(result, db.getVersion());

        int idIndex = result.getColumnIndex(DBOpener.COL_ID);
        int textIndex = result.getColumnIndex(DBOpener.COL_TEXT);
        int outgoingIndex = result.getColumnIndex(DBOpener.COL_OUTGOING);


        while(result.moveToNext()){
            long id = result.getLong(idIndex);
            if (textIndex != -1) {
                String text = result.getString(textIndex);
                int outgoing = result.getInt(outgoingIndex);

                //create a new message object
                Message loadedMessage = new Message(id, text, outgoing);

                //add to message array
                messageArr.add(loadedMessage);
            }
        }
    }

    //private adapter class that extends BaseAdapter
    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return messageArr.size();
        }

        public Message getItem(int position) {
            return messageArr.get(position);
        }

        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {

            Message message = messageArr.get(position);
            LayoutInflater inflater = getLayoutInflater();

            View newView = (message.isOutgoing())?
                    inflater.inflate(R.layout.send_message_row, parent, false) :
                    inflater.inflate(R.layout.receive_message_row, parent, false);

            TextView tView = newView.findViewById(R.id.message);
            tView.setText(message.getMessage());

            tView.setOnLongClickListener(v -> {
                deleteMessage(position);
                return true;
            });

            return newView;
        }
    }

    @SuppressLint("SetTextI18n")
    protected void deleteMessage(int position){
        Message selectedMessage = messageArr.get(position);

        @SuppressLint("InflateParams") View deleteContactView = getLayoutInflater().inflate(R.layout.delete_message_view, null);

        TextView messageRow = deleteContactView.findViewById(R.id.selectedMessageRow);
        TextView messageID = deleteContactView.findViewById(R.id.selectedMessageID);
        TextView messageText = deleteContactView.findViewById(R.id.selectedMessageText);

        messageRow.setText("Position: " + position);
        messageID.setText("Id: " + selectedMessage.getId());
        messageText.setText(selectedMessage.getMessage());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatActivity.this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.alertDialogTitle))
                .setMessage(getResources().getString(R.string.alertMessageOne))
                .setView(deleteContactView)
                .setPositiveButton(R.string.positiveButton, (click, arg) -> {
                    deleteFromDB(selectedMessage);
                    messageArr.remove(position);
                    myAdapter.notifyDataSetChanged();
                    Toast.makeText(ChatActivity.this,
                            getResources().getString(R.string.toastDeleteMessage), Toast.LENGTH_LONG).show();
                })
                .setNegativeButton(getResources().getString(R.string.negativeButton), (click, arg) -> {})
                .create().show();

    }

    @SuppressLint("Recycle")
    protected void deleteFromDB(Message m){
        db.rawQuery("DELETE FROM " + DBOpener.TABLE_NAME + " WHERE _id = ?", new String[]{Long.toString(m.getId())});
    }


}
