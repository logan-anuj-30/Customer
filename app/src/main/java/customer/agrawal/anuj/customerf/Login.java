package customer.agrawal.anuj.customerf;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity
{

    FirebaseAuth mAuth;
    EditText et1,et2;
    Button btn1;
    ProgressDialog pd;
    TextView tv1,tv2;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth=FirebaseAuth.getInstance();


        pd=new ProgressDialog(Login.this);
        pd.setTitle("Login");
        pd.setMessage("Please wait while we login you");
        pd.setCanceledOnTouchOutside(false);


        pref=getPreferences(MODE_PRIVATE);
        editor=pref.edit();

        et1=findViewById(R.id.et1);     //Email
        et2=findViewById(R.id.et2);     //Password
        tv1=findViewById(R.id.tv1);     //Skip


        //if user clicked skip option
        tv1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //if skip true that means user did'nt login and use skip button
                editor.putBoolean("skip",true);
                editor.apply();
                Intent i=new Intent(Login.this,MainActivity.class);
                startActivity(i);
            }
        });



        btn1=findViewById(R.id.btn1);   //Login
        btn1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String username=et1.getText().toString();
                String password=et2.getText().toString();
                if(TextUtils.isEmpty(username)|| TextUtils.isEmpty(password))
                {
                    Toast.makeText(Login.this,"Fill all entries",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pd.show();
                    signin(username.trim(),password.trim());
                }
            }
        });
    }

    private void signin(String username, String password)
    {
        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    editor.putBoolean("skip",false);
                    editor.commit();
                    pd.dismiss();
                    startActivity(new Intent(Login.this,MainActivity.class));
                    finish();
                }
                else
                {
                    pd.hide();
                    Toast.makeText(Login.this,"Invalid Email or Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
