package utp.esirem.vincent.realtimegraph.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import utp.esirem.vincent.realtimegraph.R;

public class ScoreDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_name,txt_score, txt_score_plus ;
    public ScoreDetailViewHolder(View itemView) {
        super(itemView);

        txt_name = (TextView)itemView.findViewById(R.id.txt_name);
        txt_score = (TextView)itemView.findViewById(R.id.txt_score);
        txt_score_plus = (TextView)itemView.findViewById(R.id.txt_score_plus);
    }
}
