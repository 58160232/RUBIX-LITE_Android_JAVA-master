package rubix.mobile.rubix_mobile.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rubix.mobile.rubix_mobile.Appconfig;
import rubix.mobile.rubix_mobile.AsyncTaskAdapter;
import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.R;

public class MenuProfileFragment extends Fragment{

    private TextView mName;
    private TextView mSurename;
    private TextView mUsername;
    private TextView mUserCode;
    private TextView mPhone;
    private TextView mEmail;
    private TextView mRole;
    private de.hdodenhof.circleimageview.CircleImageView mPic;
    Appconfig appconfig;
    private SwipeRefreshLayout swipeRefresh;
    public MenuProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (appconfig == null)
            appconfig = (Appconfig) getArguments().getSerializable("Appconfig");
        if (!appconfig.checkstate())
            ((MainActivity) getActivity()).restartApp();

        return inflater.inflate(R.layout.fragment_menu_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.FragmentName = "Profile";
        //region Binding Data
        mName = (TextView) view.findViewById(R.id.txt_name);
        mSurename = (TextView) view.findViewById(R.id.txt_surename);
        mUsername = (TextView) view.findViewById(R.id.txt_info_username);
        mUserCode = (TextView) view.findViewById(R.id.txt_info_usercode);
        mPhone = (TextView) view.findViewById(R.id.txt_info_phone);
        mEmail = (TextView) view.findViewById(R.id.txt_info_email);
        mRole = (TextView) view.findViewById(R.id.txt_info_role);
        mPic = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.profile_image);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshProfile);
        //endregion
        //region Refresh when pull down
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadprofile();
            }
        });
        //endregion
    }
    private void loadprofile(){
        swipeRefresh.setRefreshing(true);
        if (((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
            AsyncTaskAdapter SR = new AsyncTaskAdapter(new Gson().toJson(appconfig.getUser().toString()), appconfig);
            SR.execute("api/MobileUserProfile/LoadUserProfile");

            try {
                String data = SR.get(10000, TimeUnit.MILLISECONDS).toString();
                JSONObject UserData = new JSONObject(data.substring(2, data.length() - 1));
                String img_src = UserData.getString("ImageUrl");
                String img_url = appconfig.getURL() + img_src.substring(2, img_src.length() - 1);

                mName.setText(UserData.getString("FirstName"));
                mSurename.setText(UserData.getString("LastName"));
                mUsername.setText(UserData.getString("Username"));
                mUserCode.setText(UserData.getString("UserCode"));
                mPhone.setText(UserData.getString("Tel"));
                mEmail.setText(UserData.getString("Email"));
                mRole.setText(UserData.getString("Role"));
                Picasso.with(getContext())
                        .load(img_url)
                        .placeholder(R.drawable.default_user)
                        .error(R.drawable.default_user)
                        .into(mPic);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getActivity(),"Please check internet",Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(getActivity(),"Please connect internet",Toast.LENGTH_SHORT).show();
        }
        swipeRefresh.setRefreshing(false);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadprofile();
    }
}
