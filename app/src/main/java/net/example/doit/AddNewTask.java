package net.example.doit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


import net.example.doit.Model.ToDoModel;
import net.example.doit.Receiver.ReminderBroadcast;
import net.example.doit.Utils.DatabaseHandler;


import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    Spinner Hourspin;
    Spinner Minutespin;
    Spinner Secondspin;
    private DatabaseHandler db;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = Objects.requireNonNull(getView()).findViewById(R.id.newTaskText);
        Hourspin = (Spinner) getView().findViewById(R.id.Hourspin);
        Minutespin = (Spinner) getView().findViewById(R.id.Minutespin);
        Secondspin = (Spinner) getView().findViewById(R.id.Secondspin);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            assert task != null;
            if(task.length()>0)
                newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
        }

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                long milliHour = Long.parseLong(Hourspin.getSelectedItem().toString()) * 1000 * 3600;
                long milliMin = Long.parseLong(Minutespin.getSelectedItem().toString()) * 1000 * 60;
                long milliSec = Long.parseLong(Secondspin.getSelectedItem().toString()) * 1000;
                if(finalIsUpdate){
                    db.updateTask(bundle.getInt("id"), text);

                    Intent intent = new Intent(AddNewTask.this.getActivity(), ReminderBroadcast.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0,intent,0);

                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);

                    long timeAtButtonClick = System.currentTimeMillis();

                    long tenSecondsInMillis = milliHour + milliMin + milliSec;

                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + tenSecondsInMillis,pendingIntent);
                }
                else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    db.insertTask(task);

                    Intent intent = new Intent(AddNewTask.this.getActivity(),ReminderBroadcast.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0,intent,0);

                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);

                    long timeAtButtonClick = System.currentTimeMillis();

                    long tenSecondsInMillis = milliHour + milliMin + milliSec;

                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + tenSecondsInMillis,pendingIntent);
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }
}
