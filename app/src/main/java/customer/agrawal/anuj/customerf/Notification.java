package customer.agrawal.anuj.customerf;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notification extends Fragment
{

    RecyclerView rview;
    ArrayList<String> msg;
    NoteAdapter adapter;
    TextView tv1;
    public Notification()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.notification_frag, container, false);
        rview=v.findViewById(R.id.rview);
        rview.setLayoutManager(new LinearLayoutManager(getContext()));
        msg=new ArrayList<>();
        adapter=new NoteAdapter();
        rview.setAdapter(adapter);
        tv1=v.findViewById(R.id.tv1);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            tv1.setVisibility(View.INVISIBLE);
            FirebaseFirestore.getInstance().collection("Notification").document(user.getUid())
            .collection("navi").orderBy("time", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot qds, @Nullable FirebaseFirestoreException e)
                {
                    msg=new ArrayList<>();
                    if(e!=null)
                    {
                        Toast.makeText(getContext(),"Error in receiving notification",Toast.LENGTH_SHORT).show();
                    }
                    else if(!qds.isEmpty() && !qds.getDocuments().isEmpty())
                    {
                        for(DocumentSnapshot ds:qds.getDocuments())
                        {
                            msg.add(ds.getData().get("message")+"");
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });

        }
        else
        {
            rview.setVisibility(View.INVISIBLE);
        }

        return v;
    }


    class NoteAdapter extends RecyclerView.Adapter<NoteHolder>
    {


        @NonNull
        @Override
        public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View v=LayoutInflater.from(getContext()).inflate(R.layout.notification_layout,parent,false);
            return new NoteHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteHolder holder, int position)
        {
            holder.setMessage(msg.get(position));
        }

        @Override
        public int getItemCount()
        {
            return msg.size();
        }
    }

    class NoteHolder extends RecyclerView.ViewHolder
    {
        View v;
        public NoteHolder(View v)
        {
            super(v);
            this.v=v;
        }
        public void setMessage(String s)
        {
            TextView tv1=v.findViewById(R.id.tv1);
            tv1.setText(s);
        }
    }



}
