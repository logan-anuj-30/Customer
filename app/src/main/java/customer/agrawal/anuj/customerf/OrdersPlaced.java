package customer.agrawal.anuj.customerf;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class OrdersPlaced extends AppCompatActivity {
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_placed);

        pager = findViewById(R.id.pager);
        pager.setAdapter(new Adapter(getSupportFragmentManager()));

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTap(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void changeTap(int position)
    {
        TextView tv1 = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tv2);
        if (position == 0)
        {
            tv1.setTextSize(18);
            tv2.setTextSize(12);
        }
        else if (position == 1)
        {
            tv1.setTextSize(12);
            tv2.setTextSize(18);

        }
    }


    class Adapter extends FragmentPagerAdapter
    {
        FragmentManager fm;
        public Adapter(FragmentManager fm)
        {
            super(fm);
            this.fm=fm;
        }

        @Override
        public Fragment getItem(int position)
        {
            if (position == 0)
            {
                return  new CurrentOrder();
            }
            else if (position == 1)
            {
                return new OldOrders();
            }
            else {
                return null;
            }

        }

        @Override
        public int getCount()
        {
            return 2;//totla 2 items
        }
    }
}