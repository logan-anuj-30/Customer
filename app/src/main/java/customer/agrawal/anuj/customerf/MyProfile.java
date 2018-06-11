package customer.agrawal.anuj.customerf;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfile extends Fragment {


    String pwdold,userId;

    public MyProfile()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.my_profile_frag, container, false);
        final TextView tv1=v.findViewById(R.id.tv1);//name
        final TextView tv2=v.findViewById(R.id.tv2);

        Button btn1=v.findViewById(R.id.btn1);//change pwd
        Button btn2=v.findViewById(R.id.btn2);// Loggout

       FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser==null)
        {
            Log.e("Hello1","World2");
            tv1.setText("Please Login");
            tv2.setText("Please Login");
            btn1.setVisibility(View.INVISIBLE);
            btn2.setVisibility(View.INVISIBLE);
        }

        else
        {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle("My Profile");
            pd.setMessage("Loading your Detail");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

            String user=firebaseUser.getUid();
            userId = user;
            FirebaseFirestore.getInstance().collection("Users").document(user).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        pd.dismiss();
                        String name=task.getResult().getData().get("name")+"";
                        String email=task.getResult().getData().get("email")+"";
                        tv1.setText(name);
                        tv2.setText(email);
                        pwdold=task.getResult().getData().get("password")+"";
                    }
                    else
                    {
                        pd.hide();
                    }
                }
            });
        }

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                FirebaseAuth.getInstance().signOut();
                Intent i=new Intent(getContext(),Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        return v;
    }




    private void resetPassword()
    {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Changing password");
        pd.setMessage("Please Wait");
        pd.setCanceledOnTouchOutside(false);

        LayoutInflater l = getLayoutInflater();
        final View v = l.inflate(R.layout.pwdresetdialog, null);

        AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
        ab.setCancelable(false);
        ab.setView(v);
        ab.setTitle("Reset Password");

        ab.setPositiveButton("Reset", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pd.show();

                EditText et1 = (EditText) v.findViewById(R.id.pw1);//old
                EditText et2 = (EditText) v.findViewById(R.id.pw2);//new
                EditText et3 = (EditText) v.findViewById(R.id.pw3);//re new

                String oldPWD = et1.getText().toString();
                final String newPWD = et2.getText().toString();
                String rePWD = et3.getText().toString();

                if (!newPWD.equals(rePWD))
                {
                    Toast.makeText(getContext(), "New Password and Re-password does'nt Match", Toast.LENGTH_SHORT).show();
                    pd.hide();

                }
                else if(!pwdold.equals(oldPWD))
                {
                    Toast.makeText(getContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                    pd.hide();
                }

                else
                {
                    FirebaseUser u=FirebaseAuth.getInstance().getCurrentUser();

                    u.updatePassword(newPWD).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Log.e("Success","Ful");
                            FirebaseFirestore.getInstance().collection("Users").document(userId).
                                    update("Password",newPWD).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    Intent i = new Intent(getContext(), Login.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    pd.cancel();
                                    startActivity(i);


                                }
                            });

                        }
                    });
                }

            }
        });

        ab.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ab.show();
    }






}
