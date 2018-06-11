package customer.agrawal.anuj.customerf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        new Thread()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(1200);
                    Intent i=new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
                catch (Exception e)
                {

                }
            }
        }.start();
    }
}
