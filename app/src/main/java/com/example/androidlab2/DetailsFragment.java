package com.example.androidlab2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private Bundle dataFromActivity;
    private long id;
    private AppCompatActivity parentActivity;
    private boolean isTablet;

    public void setTablet(boolean tablet) { isTablet = tablet; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(ChatActivity.ITEM_ID );

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_details, container, false);

        //show the message
        TextView message = (TextView)result.findViewById(R.id.message);
        message.setText(dataFromActivity.getString(ChatActivity.ITEM_SELECTED));

        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.ID);
        idView.setText("ID=" + id);

        // get the delete button, and add a click listener:
        Button hideButton = (Button)result.findViewById(R.id.hideButton);
        hideButton.setOnClickListener( clk -> {
            DetailsFragment aFragment;

            if(isTablet){
                aFragment = new DetailsFragment( );
                int ft = getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, aFragment)
                        . commit();
            } else { startActivity ( this, EmptyActivity.class); }

        });
        return result;
    }

    private void startActivity(DetailsFragment detailsFragment, Class<EmptyActivity> emptyActivityClass) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }


}
