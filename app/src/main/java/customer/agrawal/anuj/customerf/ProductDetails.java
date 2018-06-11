package customer.agrawal.anuj.customerf;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class ProductDetails extends Fragment
{
    public ProductDetails()
    {
        // Required empty public constructor
    }
    TextView tv1,tv2,tv3,tv4,out;
    ImageView img1,img2;
    Button btn1;
    String key="";
    int stock;

    ProgressBar bar;
    ScrollView scroll;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.product_details_frag, container, false);
        key=getArguments().getCharSequence("key").toString();
        tv1=v.findViewById(R.id.tv1);//brand
        tv2=v.findViewById(R.id.tv2);//description
        tv3=v.findViewById(R.id.tv3);//quantity
        tv4=v.findViewById(R.id.tv4);//price
        btn1=v.findViewById(R.id.btn1);//add to cart
        img1=v.findViewById(R.id.img1);//item image
        img2=v.findViewById(R.id.img2);//cart

        out=v.findViewById(R.id.out);// out of stock

        FirebaseFirestore db= FirebaseFirestore.getInstance();
        bar=v.findViewById(R.id.bar);
        bar.setIndeterminate(true);
        scroll=v.findViewById(R.id.scroll);
        scroll.setVisibility(View.INVISIBLE);

        //Getting The Data associated with key
       DocumentReference ref=db.collection("ProductInfo").document(key);
       ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
       {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task)
           {
               if(task.isSuccessful())
               {
                  DocumentSnapshot ds=task.getResult();

                  String s=ds.get("available")+"";
                  if(s.equals("1"))
                      stock=0;
                  else
                      stock=1;

                  if(stock==1)
                      out.setVisibility(View.INVISIBLE);

                  tv1.setText(ds.get("brand")+"");
                  tv2.setText(ds.get("description")+"");
                  tv3.setText(ds.get("quantity")+"");
                  tv4.setText(ds.get("price")+"");
                  String imgLink=ds.get("downloadURL")+"";
                  Picasso.get().load(imgLink).into(img1);
                   addListenerOnButton(stock);
                   bar.setVisibility(View.GONE);
                   scroll.setVisibility(View.VISIBLE);

               }
           }
       });

        //this method is take us to ViewCart
       img2.setOnClickListener(new View.OnClickListener()
       {
           @Override
           public void onClick(View view)
           {
                MainActivity ma=(MainActivity)getActivity();
                ma.viewCart();
           }
       });
       return v;
    }

    public void addListenerOnButton(int stock)
    {
        //Add to cart Button
        if(stock==1)
        {
            btn1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String userID = "";
                    FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
                    if (mAuth == null)
                    {
                        Toast.makeText(getContext(), "Login to continue", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), Login.class));
                    }
                    else
                    {
                        userID = mAuth.getUid();
                        final ProgressDialog pd = new ProgressDialog(getContext());
                        pd.setTitle("Adding to Cart");
                        pd.setMessage("Please wait while we add it to cart");
                        pd.setCanceledOnTouchOutside(false);
                        pd.show();
                        Map<String, Integer> hs = new HashMap<>();
                        hs.put(key, 1);

                        FirebaseFirestore.getInstance().collection("Cart").document(userID).set(hs, SetOptions.merge()).
                        addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    pd.dismiss();
                                    Toast.makeText(getContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        else
        {
            FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
            if (mAuth == null)
            {
                Toast.makeText(getContext(), "Login to continue", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), Login.class));
            }
            else
            {
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Toast.makeText(getContext(),"Out of stock",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
