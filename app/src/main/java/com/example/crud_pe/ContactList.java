package com.example.crud_pe;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactList extends ArrayAdapter<Contact> {
    private Activity context;
    List<Contact> contact;

    public ContactList( Activity context, List<Contact> contact) {
        super(context, R.layout.layout_contact_list,contact);
        this.context = context;
        this.contact = contact;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_contact_list, null, true);


        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewEmail = (TextView) listViewItem.findViewById(R.id.textViewEmail);
        TextView textViewCompany = (TextView) listViewItem.findViewById(R.id.textViewCompany);
        TextView textViewAddress = (TextView) listViewItem.findViewById(R.id.textViewAddress);

        ImageView imageView  =  listViewItem.findViewById(R.id.imageView);



        Contact contacts = contact.get(position);
        textViewName.setText(contacts.getName());
        textViewEmail.setText(contacts.getEmail());
        textViewCompany.setText(contacts.getCompany());
        textViewAddress.setText(contacts.getAddress());

        Picasso.with(context).load(contacts.getmImageUrl()).into(imageView);
        //imageView.set(contacts.getPhoto());


        return listViewItem;
    }
}
