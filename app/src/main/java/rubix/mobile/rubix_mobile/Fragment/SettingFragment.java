package rubix.mobile.rubix_mobile.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.R;

public class SettingFragment extends Fragment {
    private NumberPicker mTypePick;
    private NumberPicker mTimePick;
    private Button mBtnCancel;
    private Button mBtnSave;
    private String Unit;
    private int Time;
    public SettingFragment(){
    }
    public static SettingFragment newInstance (int Time ,String Unit) {
        SettingFragment st = new SettingFragment();
        Bundle bd = new Bundle();
        bd.putString("Unit",Unit);
        bd.putInt("Time",Time);
        st.setArguments(bd);
        return st;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Unit = getArguments().getString("Unit");
        Time =  getArguments().getInt("Time");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.FragmentName = "Setting Page";
        //region Binding data
        mTypePick = (NumberPicker) view.findViewById(R.id.type_picker);
        mTimePick = (NumberPicker) view.findViewById(R.id.second_number);
        mBtnCancel = (Button) view.findViewById(R.id.setting_cancel);
        mBtnSave = (Button) view.findViewById(R.id.setting_save);
        mTypePick.setMaxValue(1);
        mTypePick.setMinValue(0);
        mTypePick.setDisplayedValues( new String[]{"Hour(s)", "Minute(s)"});
        if(Unit.equals("Minute")) {
            mTypePick.setValue(1);
            mTimePick.setMinValue(1);
            mTimePick.setMaxValue(59);
        }
        else{
            mTimePick.setValue(0);
            mTimePick.setMinValue(1);
            mTimePick.setMaxValue(6);
        }
        mTimePick.setValue(Time);
        //endregion
        //region Set Type pick
        mTypePick.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(mTypePick.getValue() == 0) {
                    mTimePick.setMaxValue(6);
                    mTimePick.setMinValue(1);
                } else if (mTypePick.getValue() == 1) {
                    mTimePick.setMaxValue(59);
                    mTimePick.setMinValue(1);
                }
            }
        });
        //endregion
        //region Button Cancel
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().onBackPressed();
            }
        });
        //endregion
        //region Button Save
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String xmlFile = "setting.xml";
                try {
                    FileOutputStream fileos = getActivity().openFileOutput(xmlFile, Context.MODE_PRIVATE);
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileos);
                    XmlSerializer xmlSerializer = Xml.newSerializer();
                    StringWriter writer = new StringWriter();
                    xmlSerializer.setOutput(writer);
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.startTag(null, "Unit");
                    if(mTypePick.getValue()==1) xmlSerializer.text("Minute");
                    else xmlSerializer.text("Hour");
                    xmlSerializer.endTag(null, "Unit");
                    xmlSerializer.startTag(null,"Time");
                    if(mTypePick.getValue()==1) xmlSerializer.text(String.valueOf(mTimePick.getValue()));
                    else xmlSerializer.text(String.valueOf(mTimePick.getValue()));
                    xmlSerializer.endTag(null, "Time");
                    xmlSerializer.endDocument();
                    xmlSerializer.flush();
                    outputWriter.write(writer.toString());
                    outputWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ((MainActivity) getActivity()).SetTime();
                getActivity().onBackPressed();
                Toast.makeText(getActivity(),"Setting Complete",Toast.LENGTH_LONG).show();

            }
        });
        //endregion
    }
}
