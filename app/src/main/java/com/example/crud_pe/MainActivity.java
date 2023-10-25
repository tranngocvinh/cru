package com.example.crud_pe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName;
    private EditText editEmail;
    private EditText editCompany;
    private EditText editAddress;
    private Button mButtonChooseImage;
    private ImageView mImageView;
    private Uri mImageUri;
    ListView listViewContacts;

    private ProgressBar mProgressBar;

    List<Contact> contacts ;
    private Button mUpload ;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private Button ButtonUpdateImage ;
    private ImageView UpdateImage ;
    private StorageTask mUploadTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = (EditText) findViewById(R.id.UpdateName);
        editEmail = (EditText) findViewById(R.id.editTextEmail);
        editCompany = (EditText) findViewById(R.id.editTextCompany);
        editAddress = (EditText) findViewById(R.id.editTextAddress);
        mButtonChooseImage = (Button) findViewById(R.id.button_choose_image);
        mImageView = findViewById(R.id.image_view);
        listViewContacts = findViewById(R.id.listViewContacts);
        mUpload = (Button) findViewById(R.id.buttonAdd);
        ButtonUpdateImage = (Button) findViewById(R.id.ButtonUpdateImage) ;
        UpdateImage = (ImageView) findViewById(R.id.UpdateImage) ;
        mStorageRef = FirebaseStorage.getInstance().getReference("contact");

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("contact");
        contacts = new ArrayList<>() ;

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }


        });





        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        listViewContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact contact = contacts.get(i);
                showUpdateDeleteDialog(contact.getId(), contact.getName());
                return true;
            }
        });
    }



    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void addContact() {
        String name = editTextName.getText().toString().trim() ;
        String email = editEmail.getText().toString().trim() ;
        String company = editCompany.getText().toString().trim() ;
        String address = editAddress.getText().toString().trim() ;



        //checking if the value is provided
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(company) && !TextUtils.isEmpty(address) && mImageUri != null) {

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            //getting a unique id using push().getKey() method
                            //it will create a unique id and we will use it as the Primary Key for our Artist
                            String id = mDatabaseRef.push().getKey();

                            //creating an Artist Object
                            Contact contact = new Contact(id, name, email,company,address,upload);

                            //Saving the Artist
                            mDatabaseRef.child(id).setValue(contact);

                            //setting edittext to blank again
                            editTextName.setText("");
                            editEmail.setText("");
                            editCompany.setText("");
                            editAddress.setText("");

                            //displaying a success toast
                        }

                        });
                    }else{
            Toast.makeText(this, "Please enter all ", Toast.LENGTH_SHORT).show();


        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        //attaching value event listener
        mDatabaseRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                contacts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Contact contact = postSnapshot.getValue(Contact.class);
                    //adding artist to the list
                    contacts.add(contact);
                }

                //creating adapter
                ContactList artistAdapter = new ContactList(MainActivity.this, contacts);
                //attaching adapter to the listview
                listViewContacts.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateDeleteDialog(final String id, String name) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update, null);
        dialogBuilder.setView(dialogView);

        final EditText updateName = (EditText) dialogView.findViewById(R.id.UpdateName);
        final EditText updateEmail = (EditText) dialogView.findViewById(R.id.UpdateEmail);
        final EditText updateCompany = (EditText) dialogView.findViewById(R.id.UpdateCompany);
        final EditText updateAddress = (EditText) dialogView.findViewById(R.id.UpdateAddress);
        final ImageView updatePhoto = (ImageView) dialogView.findViewById(R.id.UpdateImage);

        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdate);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDelete);



        dialogBuilder.setTitle(name);
        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = updateName.getText().toString().trim();
                String email = updateEmail.getText().toString().trim();
                String company = updateCompany.getText().toString().trim();
                String address = updateAddress.getText().toString().trim();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)
                        && !TextUtils.isEmpty(company) && !TextUtils.isEmpty(address) && mImageUri != null) {
                    StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                            + "." + getFileExtension(mImageUri));

                    mUploadTask = fileReference.putFile(mImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                    Upload upload = new Upload(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                                    //getting a unique id using push().getKey() method
                                    //it will create a unique id and we will use it as the Primary Key for our Artist
                                    updateContact(id, name, email,company,address,upload);
                                    //creating an Artist Object

                                    //Saving the Artist

                                    //setting edittext to blank again
                                    editTextName.setText("");
                                    editEmail.setText("");
                                    editCompany.setText("");
                                    editAddress.setText("");

                                    //displaying a success toast
                                }

                            });
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter all", Toast.LENGTH_LONG).show();

                }
            }
        });


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                 * we will code this method to delete the artist
                 * */

                deleteContact(id);
                b.dismiss();

            }
        });
    }

    private boolean updateContact(String id, String name, String email,String company,String address,Upload photo) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("contact").child(id);

        //updating artist
        Contact contact = new Contact(id, name, email,company,address,photo);
        dR.setValue(contact);
        Toast.makeText(getApplicationContext(), "Contact Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteContact(String id) {
        //getting the specified artist reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("contact").child(id);

        //removing artist
        dR.removeValue();



        //removing all tracks

        Toast.makeText(getApplicationContext(), "Contact Deleted", Toast.LENGTH_LONG).show();

        return true;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(mImageView);

        }
    }








}



