package model.db.internal.async;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageButton;

import com.greenwav.greenwav.R;

import java.util.Iterator;

import model.Line;
import model.Route;
import model.Stop;
import model.db.internal.JamboDAO;

/**
 * Created by sauray on 01/03/15.
 */
public class RemoveLocalSchedules extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Line line;
    private ImageButton favorite;

    public RemoveLocalSchedules(Context context, Line line, ImageButton favorite){
        this.context = context;
        this.line = line;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JamboDAO dao = new JamboDAO(context);
        dao.open();
        Iterator<Route> it = dao.findRoutes(line.getIdBdd()).iterator();
        while(it.hasNext()){
            Iterator<Stop> it2 = dao.findAssociateArrets(it.next(), "ASC").iterator();
            while(it2.hasNext()){
                dao.removeHoraireByAppartient(it2.next().getIdAppartient());
            }

        }
        dao.setLigneNotFavoris(line.getIdBdd());
        dao.close();
        return null;
    }

    protected void onPostExecute(Void result){
        favorite.setImageResource(R.drawable.ic_action_not_important);
    }
}
