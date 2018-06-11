package customer.agrawal.anuj.customerf;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Set;

import javax.annotation.Nullable;

public class CartActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    ArrayList<String> keys;
    ArrayList<FirebaseMap> map;
    CartAdapter adp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        keys=new ArrayList<>();
        map=new ArrayList<>();

        recyclerView=findViewById(R.id.cartactivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adp=new CartAdapter(getApplicationContext(),map);
        recyclerView.setAdapter(adp);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth == null)
            startActivity(new Intent(CartActivity.this, Login.class));

        //get keys from cart
        String auth= FirebaseAuth.getInstance().getCurrentUser().getUid();
        getKeys(auth);


    }

    private void getKeys(String auth)
    {
        FirebaseFirestore.getInstance().collection("Cart").document(auth).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        Set ds = task.getResult().getData().keySet();
                        for (Object key : ds)
                        {
                            keys.add(key.toString());
                            //Log.e(key.toString(), key.toString() + " Keys");
                        }
                        getData(keys);
                    }
                });
    }

    private void getData(final ArrayList<String> keys)
    {
        for (int i = 0; i < keys.size(); i++)
        {
            FirebaseFirestore.getInstance().collection("ProductInfo").document(keys.get(i)).
            addSnapshotListener(new EventListener<DocumentSnapshot>()
            {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
                {
                    //adp.notifyDataSetChanged();

                    //Log.e(documentSnapshot.getData()+"", documentSnapshot.getData()+" Data1");
                    FirebaseMap f = documentSnapshot.toObject(FirebaseMap.class);
                    //Log.e("Data1","Data2");
                    map.add(f);
                    adp=new CartAdapter(getApplicationContext(),map);
                    recyclerView.setAdapter(adp);

                }

            });

        }

    }

    private void setAdapter()
    {

    }

}

class CartAdapter extends RecyclerView.Adapter<MyCartViewHolder>
{
    Context c;
    ArrayList<FirebaseMap> map;
    CartAdapter(Context c, ArrayList<FirebaseMap> map)
    {
        this.c=c;
        this.map=map;
    }
    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(c).inflate(R.layout.cart_items,parent,false);
        return new MyCartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position)
    {
        holder.setBrand(map.get(position).getBrand());
        holder.setDescription(map.get(position).getDescription());
        holder.setPrice(map.get(position).getPrice());
        String url=map.get(position).getDownloadURL();
        ImageView img=holder.getImage();
        Picasso.get().load(url).into(img);
    }

    @Override
    public int getItemCount()
    {
        Log.e(map.size()+"",map.size()+"Size");
        return map.size();
    }
}


class MyCartViewHolder extends RecyclerView.ViewHolder
{
    View v;

     MyCartViewHolder(View itemView)
    {
        super(itemView);
        this.v = itemView;
    }

    public void setBrand(String s)
    {
        TextView tv2 = v.findViewById(R.id.tv2);
        tv2.setText(s);
    }

    public void setDescription(String s)
    {
        TextView tv1 = v.findViewById(R.id.tv1);
        tv1.setText(s);
    }

    public void setPrice(String s)
    {
        TextView tv4 = v.findViewById(R.id.tv4);
        tv4.setText(s);
    }

    public ImageView getImage()
    {
        return (ImageView) v.findViewById(R.id.img1);
    }
}
