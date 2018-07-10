package rubix.mobile.rubix_mobile.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import rubix.mobile.rubix_mobile.R;

public class MenuHomeFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener{
    private OnClickMenu mMenu;
    Context mContext;
    private Button mbtnProfile;
    private Button mbtnPicking;
    private Button mbtnReceive;
    private Button mbtnTrasit;
    private Button mbtnChange;
    private Button mbtnCount;
    private NavigationView navigationView;
    public MenuHomeFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_home, container, false);
        //region Binding Data
        mbtnProfile = (Button) view.findViewById(R.id.btn_profile_main);
        mbtnReceive = (Button) view.findViewById(R.id.btn_receive_main);
        mbtnPicking = (Button) view.findViewById(R.id.btn_picking_main);
        mbtnTrasit = (Button) view.findViewById(R.id.btn_transit_main);
        mbtnCount = (Button) view.findViewById(R.id.btn_count_main);
        mbtnChange = (Button) view.findViewById(R.id.btn_change_main);
        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        mbtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.Update(1);
            }
        });
        mbtnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.Update(2);
            }
        });
        mbtnTrasit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.Update(3);
            }
        });
        mbtnPicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.Update(4);
            }
        });
        mbtnCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.Update(5);
            }
        });
        mbtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.Update(6);
            }
        });
        //endregion
        return view;
    }
    //Check nav menu follow menu
    public interface OnClickMenu {
        public void Update(int i);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mMenu = (OnClickMenu) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnClickMenu");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMenu = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navigationView = null;
    }
}
