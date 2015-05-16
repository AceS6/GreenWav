package model.utility;

/**
 * Created by asauray on 2/18/15.
 */
public class MathsUtils {

    public static int pgcd (int a, int b) { // début de pgcd ()

        if(a<b) // on veut le premier argument plus grand
            return (pgcd(b,a));
        else if(b==0) // condition d'arrêt
            return (a);
        else // on poursuit l'algorithme d'Euclide
            return (pgcd(b,a%b));

    } // fin de pgcd ()

}
