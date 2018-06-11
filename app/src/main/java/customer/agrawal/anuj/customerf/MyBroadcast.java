package customer.agrawal.anuj.customerf;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

public class MyBroadcast extends BroadcastReceiver
{
    AlertDialog.Builder al;
    AlertDialog a;

    public MyBroadcast(final Context c)
    {
        al=new AlertDialog.Builder(c);
        al.setTitle("No Internet Connection");
        al.setMessage("Please connect to internet to continue");
        al.setCancelable(false);
        al.setPositiveButton("Connect", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                c.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));

            }
        });
        al.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        a=al.create();
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
       if(isConnected )
        {
            Log.e("Connected", "connected");
            a.dismiss();
        }

        else
        {
                Log.e("Not Connected", "Not connected");
                a.show();
        }

    }
}
