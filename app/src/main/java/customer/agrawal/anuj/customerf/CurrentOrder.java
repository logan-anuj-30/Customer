package customer.agrawal.anuj.customerf;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import io.grpc.Server;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentOrder extends Fragment
{

    Map<String,Object> map;
    List<String> count;
    List<FirebaseMap> firebase;
    RecyclerView rView;
    CurrentAdapter adapter;
    public CurrentOrder()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.current_order, container, false);
        TextView tv1=v.findViewById(R.id.tv1);
        rView=v.findViewById(R.id.currentview);
        count=new ArrayList<>();
        firebase=new ArrayList<>();

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null)
        {
            tv1.setVisibility(View.INVISIBLE);
            rView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter=new CurrentAdapter();
            rView.setAdapter(adapter);
            getKeys(user.getUid());

        }
        else
        {
            rView.setVisibility(View.INVISIBLE);
        }
        return v;
    }


    private void getKeys(String auth)
    {
        FirebaseFirestore.getInstance().collection("PlacedOrders").whereEqualTo
                ("userID",auth).addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots,FirebaseFirestoreException e)
            {
                if(!queryDocumentSnapshots.isEmpty())
                {
                    count=new ArrayList<>();
                    firebase=new ArrayList<>();
                    map=new HashMap<>();
                    for(DocumentSnapshot ds:queryDocumentSnapshots.getDocuments())
                    {
                        map=ds.getData();
                    }
                    getData(map);
                }
            }
        });
    }
   public void getData(final Map<String,Object> map)
   {
       Set<String> key=map.keySet();

       for( final String s:key)
       {
           if(s.equals("userID"))
               continue;
           FirebaseFirestore.getInstance().collection("ProductInfo").document(s)
                   .addSnapshotListener(new EventListener<DocumentSnapshot>()
           {
       @Override
       public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
       {
           FirebaseMap m=documentSnapshot.toObject(FirebaseMap.class);
           if(m!=null)
           {
               firebase.add(m);
               count.add(map.get(s) + "");
               adapter.notifyDataSetChanged();
           }
       }
   });
       }

   }

   class CurrentAdapter extends RecyclerView.Adapter<CurrentViewHolder>
   {

       @NonNull
       @Override
       public CurrentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
       {
           View v1=LayoutInflater.from(getContext()).inflate(R.layout.placed_layout,parent,false);
           return new CurrentViewHolder(v1);
       }

       @Override
       public void onBindViewHolder(@NonNull CurrentViewHolder holder, int position)
       {
           holder.setBrand(firebase.get(position).getBrand());
           holder.setDescription(firebase.get(position).getDescription());
           holder.setQuantity(firebase.get(position).getQuantity());

           String price=firebase.get(position).getPrice();
           String num=count.get(position);

           holder.setPrice("Rs "+price);
           holder.setItems("items "+num);

           holder.setItemTotal(price,num);

           String imageURL=firebase.get(position).getDownloadURL();
           Picasso.get().load(imageURL).into(holder.getImage());
       }

       @Override
       public int getItemCount()
       {
           return firebase.size();
       }
   }

   class CurrentViewHolder extends RecycleViewHolder
   {

       public CurrentViewHolder(View v)
       {
           super(v);
       }
       public void setDescription(String description)
       {
           TextView tv1=v.findViewById(R.id.tv2);
           tv1.setText(description);
       }

       public void setItemTotal(String price ,String num)
       {
           TextView t=v.findViewById(R.id.tv6);
           int sum=Integer.parseInt(price)*Integer.parseInt(num);
           t.setText(sum+"");
       }

       public void setBrand(String brand)
       {
           TextView tv1=v.findViewById(R.id.tv1);
           tv1.setText(brand);
       }
       public void setItems(String items)
       {
           TextView tv1=v.findViewById(R.id.tv4);
           tv1.setText(items);
       }
       public void setPrice(String price)
       {
           TextView tv1=v.findViewById(R.id.tv5);
           tv1.setText(price);
       }
       public void setQuantity(String quantity)
       {
           TextView tv1=v.findViewById(R.id.tv3);
           tv1.setText(quantity);
       }
       public ImageView getImage()
       {
           return (ImageView)v.findViewById(R.id.img1);
       }

   }


}
