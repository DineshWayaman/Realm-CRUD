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
            showAddDialog();
        }

        if(view.getId() == R.id.btnView){
            showAllData();
        }
    }

    private void showAllData() {
        List<Data> dataList = realm.where(Data.class).findAll();

        for(int i=0; i<dataList.size(); i++){
//            txtResult.setText("Test");
            txtResult.append("Id : "+dataList.get(i).getId()+" Name : "+dataList.get(i).getEmail()+" Age: "+dataList.get(i).getAge()+" Course : "+ dataList.get(i).getCourse()+" \n");
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
            alertDialog.dismiss();
            Data dataModel = new Data();

            Number current_id = realm.where(Data.class).max("id");
            long nextId;

            if(current_id==null){
                nextId=1;
            }else{
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
                        realm.copyToRealm(dataModel);
                        Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Log.e("TAG", e.toString(), e);
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });

    }



}