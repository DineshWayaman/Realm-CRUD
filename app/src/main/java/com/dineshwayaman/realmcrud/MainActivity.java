package com.dineshwayaman.realmcrud;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dineshwayaman.realmcrud.Models.Data;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnAdd, btnRead, btnUpdate, btnDelete;
    TextView txtResult;
    private Realm realm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfiguration);
//        realm = Realm.getDefaultInstance();

        initializeWidgets();

        btnAdd.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);


//        if data available in table display
        showAllData();

    }



    private void initializeWidgets() {
            btnAdd = findViewById(R.id.btnAdd);
            btnRead = findViewById(R.id.btnView);
            btnUpdate = findViewById(R.id.btnUpdate);
            btnDelete = findViewById(R.id.btnDelete);
            txtResult = findViewById(R.id.txtShow);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnAdd){
            Log.d("Insert", "Insert");
//            show dialog to insert data
            showAddDialog();
        }

        if(view.getId() == R.id.btnView){
            showAllData();
        }
        
        if(view.getId() == R.id.btnUpdate){
            updateDialog();
        }

        if(view.getId() == R.id.btnDelete){
            deleteDialod();
        }
    }

    private void deleteDialod() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.update_delete_layout,null);
        alertBuilder.setView(view);

        Button btnDelete = view.findViewById(R.id.btnSubmit);
        final EditText edtID = view.findViewById(R.id.edtID);

        btnDelete.setText("Delete");

        final AlertDialog deleteDialog = alertBuilder.show();

        btnDelete.setOnClickListener(v -> {

            if (edtID.getText().length() == 0){
                Toast.makeText(this, "ID is required", Toast.LENGTH_SHORT).show();
            }else{

                long id = Long.parseLong(edtID.getText().toString());
                final Data dataModel = realm.where(Data.class).equalTo("id", id).findFirst();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        deleteDialog.dismiss();
                        try {
                            dataModel.deleteFromRealm();
                            Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            recreate();
                        } catch(Exception e){
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }




        });
    }

    private void updateDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.update_delete_layout,null);
        alertBuilder.setView(view);

        Button btnUpdate = view.findViewById(R.id.btnSubmit);
        final EditText edtID = view.findViewById(R.id.edtID);

        btnUpdate.setText("Update");

        final AlertDialog dialog = alertBuilder.show();



        btnUpdate.setOnClickListener(v -> {
            if (edtID.getText().length() == 0){
                Toast.makeText(this, "ID is required", Toast.LENGTH_SHORT).show();
            }else{
                dialog.dismiss();
                long id = Long.parseLong(edtID.getText().toString());
                final Data dataModel = realm.where(Data.class).equalTo("id", id).findFirst();
                showUpdateDialog(dataModel);

            }



        });

    }

    private void showUpdateDialog(Data dataModel) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.input_data_dialog, null);
        alertDialogBuilder.setView(view);

        final EditText name = view.findViewById(R.id.edtName);
        final EditText email = view.findViewById(R.id.edtEmail);
        final EditText age = view.findViewById(R.id.edtAge);
        Spinner courses = view.findViewById(R.id.spnCourses);
        Button btnSave = view.findViewById(R.id.btnSave);

        btnSave.setText("Update");

        final AlertDialog  alertDialog = alertDialogBuilder.show();

        name.setText(dataModel.getName());
        email.setText(dataModel.getEmail());
        age.setText(String.valueOf(dataModel.getAge()));

        if(dataModel.getCourse().equalsIgnoreCase("Software Engineering")){
            courses.setSelection(0);
        }

        else if (dataModel.getCourse().equalsIgnoreCase("Computer Science")){
            courses.setSelection(1);
        }else{
            courses.setSelection(2);
        }


        btnSave.setOnClickListener(v -> {
            alertDialog.dismiss();


            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        dataModel.setName(name.getText().toString());
                        dataModel.setEmail(email.getText().toString());
                        dataModel.setAge(Integer.parseInt(age.getText().toString()));
                        dataModel.setCourse(courses.getSelectedItem().toString());
                        realm.copyToRealmOrUpdate(dataModel);
                        Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        recreate();
                    }catch(Exception e){
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        });


    }

    private void showAllData() {
//        get all data from table
        List<Data> dataList = realm.where(Data.class).findAll();
        txtResult.setText("");

//        check data availability
        if(dataList.size() > 0){
            for(int i=0; i<dataList.size(); i++){
//            txtResult.setText("Test");
                txtResult.append("Id : "+dataList.get(i).getId()+" Name : "+dataList.get(i).getName()+" Email :"+dataList.get(i).getEmail()+" Age: "+dataList.get(i).getAge()+" Course : "+ dataList.get(i).getCourse()+" \n");
            }
        }else{
            txtResult.setText("No Data Available");
        }



    }

    private void showAddDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.input_data_dialog, null);
        alertDialogBuilder.setView(view);

        final EditText name = view.findViewById(R.id.edtName);
        final EditText email = view.findViewById(R.id.edtEmail);
        final EditText age = view.findViewById(R.id.edtAge);
        Spinner courses = view.findViewById(R.id.spnCourses);
        Button btnSave = view.findViewById(R.id.btnSave);

        final AlertDialog  alertDialog = alertDialogBuilder.show();

        btnSave.setOnClickListener(v -> {

            if (email.length() == 0 || name.length() == 0 || age.length() == 0){
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            }else{

                alertDialog.dismiss();
                Data dataModel = new Data();


//            get last entered id
                Number current_id = realm.where(Data.class).max("id");
                long nextId;

                if(current_id==null){
//                if data not available id should be 1
                    nextId=1;
                }else{
//                last id value increment by 1 for next row id
                    nextId = current_id.intValue()+1;
                }

                dataModel.setId(nextId);
                dataModel.setName(name.getText().toString());
                dataModel.setEmail(email.getText().toString());
                dataModel.setAge(Integer.parseInt(age.getText().toString()));
                dataModel.setCourse(courses.getSelectedItem().toString());

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
//                        copy data from data model to database
                            realm.copyToRealm(dataModel);
                            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            recreate();
                        }catch (Exception e){
                            Log.e("TAG", e.toString(), e);
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }


        });

    }



}