package customer.agrawal.anuj.customerf;


import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public class MyCart extends Fragment
{
    RecyclerView recyclerView;
    ArrayList<String> keys;
    ArrayList<FirebaseMap> map;
    MyAdapter adp;
    int sum=0;
    TextView tv2;
    ArrayList<Integer> number;
    String auth;
    ProgressDialog pd;

    public MyCart()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.my_cart_frag, container, false);
        Button btn1=v.findViewById(R.id.btn1);


        //Listener of PlaceOrder
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder al=new AlertDialog.Builder(getContext());
                al.setTitle("Place Order");
                al.setMessage("Are you Sure you want to place this order");
                al.setCancelable(false);


                al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        pd=new ProgressDialog(getContext());
                        pd.setMessage("Placing Your Order");
                        pd.setTitle("Place order");
                        pd.setCanceledOnTouchOutside(false);
                        pd.show();
                        placeOrder();
                    }
                });
                al.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                al.show();
            }
        });

        tv2=v.findViewById(R.id.tv2);

        keys=new ArrayList<>();
        map=new ArrayList<>();

        recyclerView=v.findViewById(R.id.cartactivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adp=new MyAdapter(getContext(),map);
        recyclerView.setAdapter(adp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)
        {
            startActivity(new Intent(getContext(),Login.class));
            Toast.makeText(getContext(),"Login to continue",Toast.LENGTH_SHORT).show();
        }
        else
        {
            auth=user.getUid();
            getKeys(auth);
        }
        return v;
    }
    private void getKeys(String auth)
    {
        final ProgressDialog pdd=new ProgressDialog(getContext());
        pdd.setTitle("Loading");
        pdd.setMessage("Please wait while we load your cart");
        pdd.setCanceledOnTouchOutside(false);
        pdd.show();
        FirebaseFirestore.getInstance().collection("Cart").document(auth).get().
        addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                //this means the cart is empty
                if(task.getResult()==null || task.getResult().getData()==null|| task.getResult().getData().isEmpty())
                {
                    pdd.dismiss();
                    MainActivity ref=(MainActivity)getActivity();
                    ref.emptyCart();
                }
                else
                {
                    pdd.dismiss();
                    Set ds = task.getResult().getData().keySet();
                    for (Object key : ds) {
                        keys.add(key.toString());
                    }
                    number = new ArrayList<>();
                    for (int i = 0; i < keys.size(); i++)
                        number.add(i, 1);
                    getData(keys);
                }
            }
        });
    }

    private void getData(final ArrayList<String> keys)
    {
        map.clear();
        for (int i = 0; i < keys.size(); i++)
        {
            FirebaseFirestore.getInstance().collection("ProductInfo").document(keys.get(i)).
            addSnapshotListener(new EventListener<DocumentSnapshot>()
            {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
                {
                    FirebaseMap f = documentSnapshot.toObject(FirebaseMap.class);
                    map.add(f);
                    sum=0;
                    adp.notifyDataSetChanged();
                }
            });
        }
    }

    class MyAdapter extends RecyclerView.Adapter<CartViewHolder>
    {
        Context c;
        ArrayList<FirebaseMap> map;
        MyAdapter(Context c, ArrayList<FirebaseMap> map)
        {
            this.c=c;
            this.map=map;
        }
        @NonNull

        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View v= LayoutInflater.from(c).inflate(R.layout.cart_items,parent,false);
            return new CartViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CartViewHolder holder, int position)
        {
            holder.setBrand(map.get(position).getBrand());
            holder.setDescription(map.get(position).getDescription());
            holder.setPrice(map.get(position).getPrice());
            String url=map.get(position).getDownloadURL();
            ImageView img=holder.getImage();
            Picasso.get().load(url).into(img);
            holder.setListener(position);
        }

        @Override
        public int getItemCount()
        {
            return map.size();
        }
    }

    class CartViewHolder extends RecyclerView.ViewHolder{
        View v;
        ImageView img2;//plus
        ImageView img3;//minus
        Button btn;//remove
        TextView tv3,tv4;//items count
        int count=0;
        int price=0;

        CartViewHolder(View itemView)
        {
            super(itemView);
            this.v = itemView;
            img2=v.findViewById(R.id.img2);//plus
            img3=v.findViewById(R.id.img3);//minus
            btn=v.findViewById(R.id.btn1);//remove
            tv3=v.findViewById(R.id.tv3);//items count
            tv4=v.findViewById(R.id.tv4);
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

        public void setPrice(String price)
        {
            this.price=Integer.parseInt(price);
            sum=sum+this.price;
            TextView tv4 = v.findViewById(R.id.tv4);
            tv4.setText(price);
            tv2.setText(Integer.toString(sum));
        }

        public ImageView getImage()
        {
            return (ImageView) v.findViewById(R.id.img1);
        }


        //Listener on plus and minus and remove
        public void setListener(final int position)
        {
            count=Integer.parseInt(tv3.getText().toString());
            img2.setOnClickListener(new View.OnClickListener()//plus
            {
                @Override
                public void onClick(View view)
                {
                    int per=price/count;
                    sum+=per;
                    price=price+per;
                    count+=1;

                    number.set(position,count);
                    tv3.setText(Integer.toString(count));
                    tv4.setText(Integer.toString(price));
                    tv2.setText(Integer.toString(sum));
                }
            });

            img3.setOnClickListener(new View.OnClickListener() {//minus
                @Override
                public void onClick(View view)
                {
                    if(count!=1)
                    {
                        int per=price/count;
                        sum=sum-per;
                        price=price-per;
                        count-=1;

                        number.set(position,count);
                        tv3.setText(Integer.toString(count));
                        tv4.setText(Integer.toString(price));
                        tv2.setText(Integer.toString(sum));


                        Log.e(number.size()+"","Size");
                        String s="";
                        for(int i:number)
                        {
                             s=s+i+" ";
                        }
                    }
                }
            });

            btn.setOnClickListener(new View.OnClickListener()//remove
            {
                @Override
                public void onClick(View view)
                {
                    number.remove(position);
                    String s=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DocumentReference ref=FirebaseFirestore.getInstance().
                            collection("Cart").document(s);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(keys.get(position), FieldValue.delete());
                    ref.update(updates);
                    keys.remove(position);
                    map.remove(position);

                    if(keys.isEmpty()|| map.isEmpty())
                    {
                        MainActivity main=(MainActivity)getActivity();
                        main.emptyCart();
                    }
                    getData(keys);
                }
            });
        }
    }


    //this will empty the cart and place the order
    public void placeOrder()
    {

        final String[] s = {""};
        FirebaseFirestore.getInstance().collection("PlacedOrders").whereEqualTo("userID",auth)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if(task.isSuccessful())
                {
                    //s will have the document id where userID==auth
                    if(task.getResult()!=null && task.getResult().getDocuments().size()!=0)
                    {
                        s[0] = s[0] +task.getResult().getDocumentChanges().get(0).getDocument().getId();
                    }
                    isIDExist(s[0]);
                }
            }
        });



//        FirebaseFirestore.getInstance().collection("PlacedOrders").add(items).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference)
//            {
//                pd.dismiss();
//                MainActivity m=(MainActivity)getActivity();
//                m.orderIsPlaced();
//            }
//        });
//
//        FirebaseFirestore.getInstance().collection("Cart").document(auth).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid)
//            {
//
//            }
//        });
    }

    private void isIDExist(String s)
    {
        Map<String,Object> items=new HashMap<>();
        items.put("userID",auth);
        items.put("time",FieldValue.serverTimestamp()+"");
        items.put("received","false");
        for(int i=0;i<number.size();i++)
        {
            items.put(keys.get(i),number.get(i)+"");
        }

        if(s.length()==0)//means ID not exist
        {
            FirebaseFirestore.getInstance().collection("PlacedOrders").add(items)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
            {
                @Override
                public void onSuccess(DocumentReference documentReference)
                {
                    Toast.makeText(getContext(),"Successfully Placed(Not Exist)",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            FirebaseFirestore.getInstance().collection("PlacedOrders").document(s).update(items).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Toast.makeText(getContext(),"Successfully Placed(Exist)",Toast.LENGTH_SHORT).show();

                }
            });
        }

        FirebaseFirestore.getInstance().collection("Cart").document(auth).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                pd.dismiss();
                MainActivity m=(MainActivity)getActivity();
                m.orderIsPlaced();
            }
        });
    }
}




