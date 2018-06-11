package customer.agrawal.anuj.customerf;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ProductsFrag extends Fragment
{

    ArrayList<FirebaseMap> al;
    RecyclerView rView;
    ArrayList<String> as;
    String query;
    View v1;
    public ProductsFrag()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        //The bundle obj is set in mainActivity
        if(v1 !=null)
            return v1;
        al=new ArrayList<FirebaseMap>();
        as=new ArrayList<String>();

        query=getArguments().getCharSequence("query").toString();
        View v1=inflater.inflate(R.layout.products_frag, container, false);
        rView=v1.findViewById(R.id.rView);
        rView.setLayoutManager(new GridLayoutManager(getContext(),2));
        final MyAdapter adapter=new MyAdapter(al,query);
        rView.setAdapter(adapter);

        //Listener on toolbar's backButton
        Toolbar toolbar=v1.findViewById(R.id.toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                getActivity().onBackPressed();
//            }
//        });

        FirebaseFirestore.getInstance().collection("ProductInfo").whereEqualTo("category",query).addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                if(e!=null)
                {
                    Toast.makeText(getContext(),"Error in Getting Data",Toast.LENGTH_SHORT).show();
                }
                else
                {
                   for(DocumentChange d:queryDocumentSnapshots.getDocumentChanges())
                   {
                        if(d.getType()==DocumentChange.Type.ADDED)
                        {
                            FirebaseMap fm=d.getDocument().toObject(FirebaseMap.class);
                            al.add(fm);
                            as.add(d.getDocument().getId());
                            adapter.notifyDataSetChanged();

                        }
//                        else if(d.getType()==DocumentChange.Type.REMOVED)
//                        {
//                            al.remove(d.getDocument().toObject(FirebaseMap.class));
//                        }
//                        else if(d.getType()==DocumentChange.Type.MODIFIED)
//                        {
//
//                        }
                   }



                }
            }
        });
        return v1;
    }


    class MyAdapter extends RecyclerView.Adapter<RecycleViewHolder> implements View.OnClickListener
    {
            ArrayList<FirebaseMap> al;
            String query;
            MyAdapter(ArrayList<FirebaseMap> al,String query)
            {
                this.al=al;
                this.query=query;
            }
            @NonNull
            @Override
            public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View v=LayoutInflater.from(getContext()).inflate(R.layout.itemrecycle,parent,false);
                v.setOnClickListener(this);
                return new RecycleViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position)
            {
                holder.setBrand(al.get(position).getBrand());
                holder.setDescription(al.get(position).getDescription());
                holder.setQuantity(al.get(position).getQuantity());
                holder.setPrice(al.get(position).getPrice());
                String imageURL=al.get(position).getDownloadURL();
                Picasso.get().load(imageURL).into(holder.getImage());
             }

            @Override
            public int getItemCount()
            {
                return al.size();
            }

        @Override
        public void onClick(View view)
        {
            int pos=rView.getChildLayoutPosition(view);//give the position which is clicked
            MainActivity m=(MainActivity)getActivity();
            m.newFragemntProductDetail(as.get(pos));//call MAin Activity method

        }

    }

}
class RecycleViewHolder extends RecyclerView.ViewHolder
{
    View v;
    public RecycleViewHolder(View v)
    {
        super(v);
        this.v=v;
    }

    public void setDownloadURL(String downloadURL)
    {
        TextView tv1=v.findViewById(R.id.tv1);
        tv1.setText(downloadURL);
    }
    public void setDescription(String description)
    {
        TextView tv1=v.findViewById(R.id.tv2);
        tv1.setText(description);
    }
    public void setBrand(String brand)
    {
        TextView tv1=v.findViewById(R.id.tv1);
        tv1.setText(brand);
    }
    public void setDiscount(String discount)
    {
        TextView tv1=v.findViewById(R.id.tv1);
        tv1.setText(discount);
    }
    public void setPrice(String price)
    {
        TextView tv1=v.findViewById(R.id.tv4);
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


