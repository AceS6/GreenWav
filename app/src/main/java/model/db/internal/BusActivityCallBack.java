package model.db.internal;

import model.Line;
import model.Stop;

/**
 * Created by root on 6/14/15.
 */
public interface BusActivityCallBack {
    public void lineSelected(Line l);
    public void stopSelected(Stop s);
}
