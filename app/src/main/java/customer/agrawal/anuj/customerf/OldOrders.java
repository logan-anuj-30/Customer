package customer.agrawal.anuj.customerf;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;


public class OldOrders extends Fragment
{
    ArrayList<String> al;//hold keys
    RecyclerView rview;
    OldAdapter adp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.old_orders, container, false);
        rview=v.findViewById(R.id.rview);
        al=new ArrayList<>();
        rview.setLayoutManager(new LinearLayoutManager(getContext()));
        adp=new OldAdapter();
        rview.setAdapter(adp);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            FirebaseFirestore.getInstance().collection("ProcessedOrders").whereEqualTo("userID",user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot qds, FirebaseFirestoreException e)
                {
                    if (e != null)
                    {
                        Toast.makeText(getContext(), "Error in getting Data", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if (qds != null && !qds.getDocumentChanges().isEmpty())
                        {
                            for (DocumentSnapshot ds : qds.getDocuments())
                            {
                                al.add(ds.getId());
                                adp.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }

        return v;
    }


    class OldAdapter extends RecyclerView.Adapter<OldViewHolder>
    {

        @NonNull
        @Override
        public OldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View v= LayoutInflater.from(getContext()).inflate(R.layout.old_order_layout,parent,false);
            return new OldViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull OldViewHolder holder, int position)
        {
            holder.setText(position);
            holder.setOnClick(position);
        }

        @Override
        public int getItemCount() {
            return al.size();
        }
    }

    class OldViewHolder extends RecyclerView.ViewHolder
    {
        View v;
        public OldViewHolder(View v)
        {
            super(v);
            this.v=v;
        }

        public void setText(int position)
        {
            TextView tv1=v.findViewById(R.id.tv1);
            tv1.setText(position+"");
        }

       public void setOnClick(final int position)
       {
           v.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view)
               {
                   FragmentManager fm=getChildFragmentManager();
                   FragmentTransaction ft=fm.beginTransaction();
                   Bundle b=new Bundle();
                   b.putCharSequence("key",al.get(position));
                   OldOrdersDetails old=new OldOrdersDetails();
                   old.setArguments(b);
                   ft.replace(R.id.old,old,"Old").addToBackStack("Old");
                   ft.commit();
                   //Toast.makeText(getContext(),"Clicked "+position,Toast.LENGTH_SHORT).show();
               }
           });
       }
    }


}
