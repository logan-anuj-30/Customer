package customer.agrawal.anuj.customerf;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mtoggle;
    Toolbar toolbar;
    android.support.v4.app.FragmentManager fm;
    android.support.v4.app.FragmentTransaction ft;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    MainActivityAdapter adapter;
    RecyclerView rview;
    ArrayList<FirebaseMap> al;//hold firebaseMAp
    ArrayList<String> as;//hold keys
    SearchView search;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        al=new ArrayList<FirebaseMap>();
        as=new ArrayList<String>();

        SharedPreferences mpref=getPreferences(MODE_PRIVATE);
        if(mpref.getBoolean("skip",true))
        {
                // user skipped the login
                // true means user skipped
        }
        else if(mAuth.getCurrentUser()==null)//this will check is user login
        {
            startActivity(new Intent(MainActivity.this,Login.class));
            finish();
        }


        rview=findViewById(R.id.rview);
        rview.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        adapter=new MainActivityAdapter(al,as);
        rview.setAdapter(adapter);

        search=findViewById(R.id.sv1);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                search.onActionViewCollapsed();
                fireStoreSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                fireStoreSearch(newText);
                return false;
            }
        });

    FirebaseFirestore.getInstance().collection("ProductInfo").addSnapshotListener
    (new EventListener<QuerySnapshot>()
        {
      @Override
      public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
      {
          if (e != null)
          {
              Toast.makeText(MainActivity.this, "Error in Getting Data", Toast.LENGTH_SHORT).show();
          }
          else if(queryDocumentSnapshots!=null)
          {
              for (DocumentChange d : queryDocumentSnapshots.getDocumentChanges())
              {
                  if (d.getType() == DocumentChange.Type.ADDED)
                  {
                      FirebaseMap fm = d.getDocument().toObject(FirebaseMap.class);
                      al.add(fm);                       //hold firebase map object
                      as.add(d.getDocument().getId());  //hold set of keys for each document
                  }
                  else if(d.getType()==DocumentChange.Type.MODIFIED)
                  {
                      int index=as.indexOf(d.getDocument().getId());
                      al.set(index,d.getDocument().toObject(FirebaseMap.class));
                  }
                  else if(d.getType()==DocumentChange.Type.REMOVED)
                  {
                      int index=as.indexOf(d.getDocument().getId());
                      al.remove(index);
                      as.remove(index);
                  }
              }
              adapter.notifyDataSetChanged();
          }
      }

  });


        // Add this in manifest
        // <service android:name=".ServiceAndNetwork"
        //            android:exported="false"></service>

        Intent i=new Intent(MainActivity.this,MyService.class);
        startService(i);



        //-----------------------------------------------//
        // This statements will register the app for bradcast;
        //Whenever there is Change in network app will receive a broadbast
        MyBroadcast br=new MyBroadcast(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br,filter);
        //------------------------------------------------//



        //-------------Service----------------
        //startService(new Intent(MainActivity.this,MyService.class));


        drawerLayout=findViewById(R.id.drawer);
        mtoggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.open,R.string.close);//Button in Action bar
        drawerLayout.addDrawerListener(mtoggle);
        mtoggle.syncState();

        navigationView=findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);


        //-----------Apply click Listener on Navigation header(Profile)
        navigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                android.support.v4.app.FragmentManager fm=getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                drawerLayout.closeDrawer(navigationView);//hide the drawer when clicked
                ft.replace(R.id.linearLayout,new MyProfile(),"MyProfile").addToBackStack("MyProfile");
                ft.commit();
            }
        });

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("E-market");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//display hamburger icon
        getNavigationHeaderName();

    }

    private void fireStoreSearch(String s)
    {

        ArrayList<FirebaseMap> b;
        ArrayList<String> alternate;

        alternate=new ArrayList<>();
        b=new ArrayList<>();
        for(int i=0;i<al.size();i++)
        {
            if(al.get(i).getBrand().contains(s) || al.get(i).getDescription().contains(s))
            {
                b.add(al.get(i));
                alternate.add(as.get(i));
            }
        }
        rview.setAdapter(new MainActivityAdapter(b,alternate));

    }

    private void getNavigationHeaderName()
    {
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("Loading");
        pd.setMessage("Please Wait");
        pd.setCanceledOnTouchOutside(false);

        View header=navigationView.getHeaderView(0);
        final TextView tv1=header.findViewById(R.id.tv1);
        final TextView tv2=header.findViewById(R.id.tv2);

        if(user!=null)
        {
            pd.show();
            FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                    .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful() && task.getResult()!=null && task.getResult().getData()!=null)
                {
                    String name=task.getResult().getData().get("name")+"";
                    String email=task.getResult().getData().get("email")+"";
                    tv1.setText(name);
                    tv2.setText(email);
                    pd.dismiss();
                }
                else
                {
                    pd.hide();
                }
            }
        });
        }
    }


    //This method is called when you click on menu(hamburger)
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //When you successfully handle a menu item, return true. If you don't handle the menu item,
        // you should call the superclass implementation of onOptionsItemSelected()
        // (the default implementation returns false).

       if(mtoggle.onOptionsItemSelected(item))
           return true;
       return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        drawerLayout.closeDrawer(navigationView);//hide the drawer when clicked
        android.support.v4.app.FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();


        //if home is clicked
        if(item.getItemId()==R.id.home)
        {
            android.support.v4.app.FragmentManager gm=getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i)
            {
                gm.popBackStack();
            }
            rview.setAdapter(new MainActivityAdapter(al,as));//refresh the list when home clicked
        }

        //Dairy
       else if(item.getItemId()==R.id.itm1)
        {
            Bundle b=new Bundle();
            b.putCharSequence("query","1");
            ProductsFrag productsFrag=new ProductsFrag();
            productsFrag.setArguments(b);
            ft.replace(R.id.linearLayout,productsFrag,"dairy").addToBackStack("dairy");
            ft.commit();
            return true;
        }

        //pulses
        else if(item.getItemId()==R.id.itm2)
        {
            ProductsFrag productsFrag=new ProductsFrag();
            Bundle b=new Bundle();
            b.putCharSequence("query","2");
            productsFrag.setArguments(b);
            ft.replace(R.id.linearLayout,productsFrag,"pulses").addToBackStack("pulses");
            ft.commit();
            return true;
        }

        //oils
        else if(item.getItemId()==R.id.itm3)
        {
            Bundle b=new Bundle();
            b.putCharSequence("query","3");
            ProductsFrag productsFrag=new ProductsFrag();
            productsFrag.setArguments(b);
            ft.replace(R.id.linearLayout,productsFrag,"oil").addToBackStack("oil");
            ft.commit();
            return true;
        }


        //Home cleaning
        else if(item.getItemId()==R.id.itm4)
        {
            ProductsFrag productsFrag=new ProductsFrag();
            Bundle b=new Bundle();
            b.putCharSequence("query","4");
            productsFrag.setArguments(b);
            ft.replace(R.id.linearLayout,productsFrag,"home").addToBackStack("home");
            ft.commit();
            return true;
        }

        //dailty needs
        else if(item.getItemId()==R.id.itm5)
        {
            ProductsFrag productsFrag=new ProductsFrag();
            Bundle b=new Bundle();
            b.putCharSequence("query","5");
            productsFrag.setArguments(b);
            ft.replace(R.id.linearLayout,productsFrag,"daily").addToBackStack("daily");
            ft.commit();
            return true;
        }


        //----------------------------Notification--------
        else if(item.getItemId()==R.id.itm6)
        {
            Notification notification=new Notification();
            ft.replace(R.id.linearLayout,notification,"notification").addToBackStack("notification");
            ft.commit();
            return true;

        }

        //orders
        else if(item.getItemId()==R.id.itm7)
        {
            startActivity(new Intent(MainActivity.this,OrdersPlaced.class));
            Toast.makeText(MainActivity.this,"Orders",Toast.LENGTH_SHORT).show();
            return true;
        }
        //-------------Abut us and Contact-----------------------
        else if(item.getItemId()==R.id.itm8)
        {
            ft.replace(R.id.linearLayout,new AboutUs(),"AboutUs").addToBackStack("AboutUs");
            ft.commit();
            return true;
        }

        //contact
        else if(item.getItemId()==R.id.itm9)
        {
            ft.replace(R.id.linearLayout,new Contact(),"contact").addToBackStack("contact");
            ft.commit();
            return true;
        }

        return false;
    }



    public void newFragemntProductDetail(String key)
    {
        //this method is called from Products Frag
        //String s contains key of the prodeuct
        android.support.v4.app.FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();

        Bundle b=new Bundle();
        b.putCharSequence("key",key);
        ProductDetails prod=new ProductDetails();
        prod.setArguments(b);

        ft.replace(R.id.linearLayout,prod,"prod").addToBackStack("prod");
        ft.commit();
    }


    public void viewCart()
    {
        //startActivity(new Intent(MainActivity.this,CartActivity.class));
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivity(new Intent(MainActivity.this,Login.class));
            Toast.makeText(MainActivity.this,"Login to Continue",Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            MyCart cart = new MyCart();
            ft.replace(R.id.linearLayout, cart, "cart").addToBackStack("cart");
            ft.commit();
        }
    }

    public void emptyCart()
    {
        android.support.v4.app.FragmentManager fm=getSupportFragmentManager();
        fm.popBackStack();
        FragmentTransaction ft=fm.beginTransaction();
        EmptyCart emp=new EmptyCart();
        ft.replace(R.id.linearLayout,emp,"empty").addToBackStack("empty");
        ft.commit();
    }

    public void orderIsPlaced()
    {
        getSupportFragmentManager().popBackStackImmediate();
        startActivity(new Intent(MainActivity.this,OrdersPlaced.class));
    }



class MainActivityAdapter extends RecyclerView.Adapter<MainActivityViewHolder> implements View.OnClickListener {
   ArrayList<FirebaseMap> mmap;//this hold fisebase data
   ArrayList<String> str;//this hold key associated with data
    MainActivityAdapter(ArrayList<FirebaseMap> al,ArrayList<String> str)
    {
        mmap=al;
        this.str=str;
    }

    @NonNull
    @Override
    public MainActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v=LayoutInflater.from(MainActivity.this).inflate(R.layout.itemrecycle,parent,false);
        v.setOnClickListener(this);
        return new MainActivityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityViewHolder holder, int position)
    {
        if(mmap.get(position).getAvailable().equals("1"))
        {
            holder.setVisibility(0);
        }
        else
        {
            holder.setVisibility(1);
        }


        holder.setBrand(mmap.get(position).getBrand());
        holder.setDescription(mmap.get(position).getDescription());
        holder.setQuantity(mmap.get(position).getQuantity());
        holder.setPrice(mmap.get(position).getPrice());
        String imageURL=mmap.get(position).getDownloadURL();
        Picasso.get().load(imageURL).into(holder.getImage());





    }

    @Override
    public int getItemCount()
    {
        return mmap.size();
    }

    //this method is called when you click any item in MainActivity
    public void onClick(View view)
    {
        String key=str.get(rview.getChildLayoutPosition(view));
        newFragemntProductDetail(key);
    }
}

class MainActivityViewHolder extends RecyclerView.ViewHolder
{
    View v;
    TextView out;
    MainActivityViewHolder(View v)
    {
        super(v);
        this.v=v;
    }


    public void setVisibility(int i)
    {
        out=v.findViewById(R.id.out);

        if(i==1)
        {
            out.setVisibility(View.INVISIBLE);
        }
        else
        {
            out.setVisibility(View.VISIBLE);
        }
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

}


