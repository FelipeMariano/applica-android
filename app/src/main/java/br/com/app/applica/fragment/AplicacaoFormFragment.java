package br.com.app.applica.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.entitity.Aplicacao;

/**
 * A simple {@link Fragment} subclass.
 */
public class AplicacaoFormFragment extends Fragment {
    MainNavActivity navActivity;
    public static String CURRENT_APLICACAO_ID = null;
    public static Aplicacao CURRENT_APLICACAO;
    public String AUTH_TOKEN;
    int year_x, month_x, day_x;


    public AplicacaoFormFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View formAplicacaoView = inflater.inflate(R.layout.fragment_aplicacao_form, container, false);

        navActivity = (MainNavActivity) getActivity();

        navActivity.toggleFab("HIDE", null);

        if(CURRENT_APLICACAO_ID != null)
            System.out.println("VAI FAZER LOAD PRA EDITAR!");
        else
            CURRENT_APLICACAO = new Aplicacao();

        showDatePickerDialog(formAplicacaoView);

        return formAplicacaoView;
    }

    private void showDatePickerDialog(final View view){

        final Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);

        Button btnDataAplic = (Button) view.findViewById(R.id.aplicacao_data);

        btnDataAplic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogFragment newFragment = new SelectedDateFragment();
                newFragment.show(navActivity.getFragmentManager(), "DatePicker");
            }
        });
    }

    public class SelectedDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();

            year_x = calendar.get(Calendar.YEAR);
            month_x = calendar.get(Calendar.MONTH);
            day_x = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year_x, month_x, day_x);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Button btn = (Button) navActivity.findViewById(R.id.aplicacao_data);
            btn.setText(day_x + "/" + month_x + "/" + year_x);

            String bd_format_date = year_x + "-" + month_x + "-" + day_x;

            CURRENT_APLICACAO.setData(bd_format_date);
        }
    }

}
