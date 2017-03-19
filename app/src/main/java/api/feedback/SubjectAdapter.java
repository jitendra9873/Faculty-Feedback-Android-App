package api.feedback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder>{

    private List<Subject> subjectList;
    private SharedPreferences preferences;
    private Context context;

    SubjectAdapter(Context context, List<Subject> subjects){
        this.subjectList = subjects;
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public SubjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubjectAdapter.ViewHolder holder, int position) {
        final Subject s = subjectList.get(position);
        holder.subject.setText(s.subjectName);
        holder.teacher.setText(s.teacherName);
        if(s.isPracs){
            holder.type1.setVisibility(View.GONE);
        }
        else {
            holder.type2.setVisibility(View.GONE);
        }
        if(!preferences.getBoolean(s.subjectName + s.teacherName, true)){
            holder.button.setVisibility(View.GONE);
        }
        else{
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FeedBackForm.class);
                    intent.putExtra("teacher", s.teacherName);
                    intent.putExtra("subject", s.subjectName);
                    intent.putExtra("division", s.division);
                    intent.putExtra("isPracs", s.isPracs);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView subject;
        TextView teacher;
        ImageView type1, type2;
        ImageButton button;


        ViewHolder(View itemView) {
            super(itemView);
            subject = (TextView) itemView.findViewById(R.id.subject);
            teacher = (TextView) itemView.findViewById(R.id.teacher_name);
            type1 = (ImageView) itemView.findViewById(R.id.label_image1);
            type2 = (ImageView) itemView.findViewById(R.id.label_image2);
            button = (ImageButton) itemView.findViewById(R.id.ImageButton);
        }
    }
}
