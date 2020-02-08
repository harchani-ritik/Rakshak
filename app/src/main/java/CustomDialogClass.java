import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.bitsplease.rakshak.InNetwork;
import com.bitsplease.rakshak.R;

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button go;
    String TAG = "MyLOGS";
    public Spinner spinnerdialogue;


    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialogue);
        go=(Button)findViewById(R.id.BTNgo);
        spinnerdialogue=findViewById(R.id.spinner2);
        String[] spinnerEmergencylist = {"General Emergency", "Medical", "Fire"};
        ArrayAdapter<String> spinnerEmergencyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, spinnerEmergencylist);
        spinnerEmergencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdialogue.setAdapter(spinnerEmergencyAdapter);
        go.setOnClickListener(this);
        spinnerdialogue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("General Emergency")){
//                    Toast.makeText(InNetwork.this, "General Emergency selected", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(TAG, "onItemSelected: in else");
                    Toast.makeText(c, parent.getItemAtPosition(position).toString()+" selected.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: nothing selected");
                Toast.makeText(c, "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BTNgo:
                Toast.makeText(c, "Go clicked", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        dismiss();
    }
}