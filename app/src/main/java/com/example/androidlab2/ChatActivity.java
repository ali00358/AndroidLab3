package com.example.androidlab2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
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
    //instantiate a private array for objects of Message class
    private ArrayList<Message> messageArr = new ArrayList<>();
    MyListAdapter myAdapter; //adapter to control items in the list view
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_linear);

        //loadDataFromDatabase and populate messageArr
        loadMessagesFromDatabase();

        //find view and set adapter
        ListView messages = (ListView) findViewById(R.id.messages);
        messages.setAdapter(myAdapter = new MyListAdapter());

        //set on item long click listener on list view
        messages.setOnItemLongClickListener( (p, b, pos, id) -> {
            deleteMessage(pos); //call delete message function on long click
            return true;
        });

        EditText editText = (EditText) findViewById(R.id.editText);

        //get send button
        Button sendBtn = (Button) findViewById(R.id.sendButton);
        //set listener to create a new outgoing message (outgoing is true by default)
        sendBtn.setOnClickListener( (click) -> {
            String text = editText.getText().toString();

            //insert into database and obtain new id
            ContentValues row = new ContentValues();
            row.put(DBOpener.COL_TEXT, text);
            row.put(DBOpener.COL_OUTGOING, 1); //1 to signify true

            //insert query
            long newID = db.insert(DBOpener.TABLE_NAME, null, row);

            //create new messge object and add to array
            Message newMessage = new Message(newID, text);
            editText.setText("");
            messageArr.add(newMessage);
            myAdapter.notifyDataSetChanged();
        });

        //get receive button
        Button receiveBtn = (Button) findViewById(R.id.receiveButton);
        //set listener to create a new incoming message (outgoing boolean set to false)
        receiveBtn.setOnClickListener( (click) -> {
            String text = editText.getText().toString();
            //insert into database and obtain new id
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

        //get writable database and assign to db
        db = opener.getWritableDatabase();

        //get all the messages that have been saved

        //query and return a cursor
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

        @Override
        public Message getItem(int position) {
            return messageArr.get(position);
        }

        @Override
        //modified to return the actual db ID
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            //get the message at the position
            Message message = messageArr.get(position);
            //get the inflater
            LayoutInflater inflater = getLayoutInflater();

            //if the message is outgoing, inflate the send_message_row layout
            //if the message is incoming, inflate the receive_message_row layout
            View newView = (message.isOutgoing())?
                    inflater.inflate(R.layout.send_message_row, parent, false) :
                    inflater.inflate(R.layout.receive_message_row, parent, false);

            //obtain the text view from the inflated view
            TextView tView = newView.findViewById(R.id.message);
            //set the message in the text view to be the message obtained from the array
            tView.setText(message.getMessage());

            //set on long click listener on the text view to display dialog
            tView.setOnLongClickListener(v -> {
                deleteMessage(position);
                return true;
            });

            //return the inflated view
            return newView;
        }
    }

    //create a delete contact method
    protected void deleteMessage(int position){
        Message selectedMessage = messageArr.get(position);

        //inflate the delete contact view
        View deleteContactView = getLayoutInflater().inflate(R.layout.delete_message_view, null);

        //get the text views
        TextView messageRow = deleteContactView.findViewById(R.id.selectedMessageRow);
        TextView messageID = deleteContactView.findViewById(R.id.selectedMessageID);
        TextView messageText = deleteContactView.findViewById(R.id.selectedMessageText);

        //set the text to the selected message
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

    //create a delete method from database
    protected void deleteFromDB(Message m){
        db.rawQuery("DELETE FROM " + DBOpener.TABLE_NAME + " WHERE _id = ?", new String[]{Long.toString(m.getId())});
    }


}
