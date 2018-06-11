package customer.agrawal.anuj.customerf;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;

import static android.app.Notification.VISIBILITY_PUBLIC;

//The service can run in the background indefinitely, even if the component that started it
// is destroyed. As such, the service should stop itself when its job is complete by calling stopSelf(),
// or another component can stop it by calling stopService().


//By default a service will be called on the main thread

//A service is started when component (like activity) calls startService() method,
// now it runs in the background indefinitely. It is stopped by stopService() method.
// The service can stop itself by calling the stopSelf() method.

public class MyService extends Service
{
    class ServiceThread implements Runnable
    {
        int serviceId;
        ServiceThread(int serviceId)
        {
            this.serviceId = serviceId;
        }

        @Override
        public void run()
        {
            final String[] id = {""};
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null)
            {
                db.collection("Notification").document(user.getUid()).collection("navi").whereEqualTo("received","false").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot qds, @javax.annotation.Nullable FirebaseFirestoreException e)
                    {
                        if (e != null)
                        {
                            Toast.makeText(getApplicationContext(), "Some Data Retrival Error", Toast.LENGTH_SHORT).show();
                        }

                        else if (qds!=null && !qds.isEmpty())
                        {
                            Intent intent = new Intent(getApplicationContext(), Notification.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Unique_id");
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                            builder.setSmallIcon(R.drawable.notification);
                            builder.setContentTitle("E-market Message");

                            //fill as much as text you want

                            builder.setContentIntent(pendingIntent);
                            builder.setVisibility(VISIBILITY_PUBLIC);//show on Lock Screen
                            builder.setAutoCancel(true);//clear when user tap on It
                            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            builder.setGroupSummary(true);

                            ArrayList<String> al=new ArrayList<>();
                            for(DocumentSnapshot ds:qds.getDocuments())
                            {
                                if(ds.getData().get("received").equals("false"))
                                {
                                    al.add(ds.getData().get("message")+"");
                                    id[0] =ds.getId();
                                }
                            }

                            for(int i=0;i<al.size();i++)
                            {
//                                Toast.makeText(getApplicationContext(),al.get(i),Toast.LENGTH_SHORT).show();
                                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(al.get(i)));
                                builder.setGroup("Key");
                                // notificationId is a unique int for each notification that you must define
                                notificationManager.notify(serviceId + (int) (Math.random() * 200000), builder.build());
                            }
                            if(!id[0].isEmpty())
                            {
                                db.collection("Notification").document(user.getUid()).collection("navi").document(id[0]).update("received","true").
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                            }
                                        });
                            }
                        }
                            }
                    });
            }


        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        //this is called first
        super.onCreate();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //this is called Second
         Thread t = new Thread(new ServiceThread(startId));
            t.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}
