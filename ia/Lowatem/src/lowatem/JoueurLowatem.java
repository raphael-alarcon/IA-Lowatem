package lowatem;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Joueur implémentant les actions possibles à partir d'un plateau, pour un
 * niveau donné.
 */
public class JoueurLowatem implements IJoueurLowatem {

    /**
     * Cette méthode renvoie, pour un plateau donné et un joueur donné, toutes
     * les actions possibles pour ce joueur.
     *
     * @param plateau le plateau considéré
     * @param couleurJoueur couleur du joueur
     * @param niveau le niveau de la partie à jouer
     * @return l'ensemble des actions possibles
     */
    @Override
    public String[] actionsPossibles(Case[][] plateau, char couleurJoueur, int niveau) {
        // afficher l'heure de lancement
        //SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        //System.out.println("actionsPossibles : lancement le " + format.format(new Date()));
        // se préparer à stocker les actions possibles
        ActionsPossibles actions = new ActionsPossibles();
        // calculer les points de vie sur le plateau initial
        NbPointsDeVie nbPv = nbPointsDeVie(plateau);
        // déplacements possibles depuis (g,G)
        for (int i = 0; i < Coordonnees.NB_LIGNES; i++) {
            for (int j = 0; j < Coordonnees.NB_COLONNES; j++) {
                if (plateau[i][j].typeUnite != Case.CAR_VIDE) {
                    if (plateau[i][j].couleurUnite == couleurJoueur) {
                        ajoutDeplDepuis(new Coordonnees(i, j), actions, nbPv, couleurJoueur, plateau);
                    }
                }
            }
        }
        //System.out.println("actionsPossibles : fin");
        return actions.nettoyer();
    }

    /**
     * Nombre de points de vie de chaque joueur sur le plateau.
     *
     * @param plateau le plateau
     * @return le nombre de pions de cette couleur sur le plateau
     */
    static NbPointsDeVie nbPointsDeVie(Case[][] plateau) {
        NbPointsDeVie nbPv = new NbPointsDeVie(0, 0);
        for (int i = 0; i < Coordonnees.NB_LIGNES; i++) {
            for (int j = 0; j < Coordonnees.NB_COLONNES; j++) {
                if (plateau[i][j].typeUnite != Case.CAR_VIDE) {
                    if (plateau[i][j].couleurUnite == 'R') {
                        nbPv.nbPvRouge += plateau[i][j].pointsDeVie;
                    }
                    if (plateau[i][j].couleurUnite == 'N') {
                        nbPv.nbPvNoir += plateau[i][j].pointsDeVie;
                    }
                }
            }
        }
        return new NbPointsDeVie(nbPv.nbPvRouge, nbPv.nbPvNoir);
    }

    /**
     * Ajouter tous les déplacements depuis une case donnée.
     *
     * @param coord coordonnées de la case d'origine
     * @param actions ensemble des actions possibles, à compléter
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * @param plateau le plateau initial
     */
    void ajoutDeplDepuis(Coordonnees coord, ActionsPossibles actions, NbPointsDeVie nbPv, char couleurJoueur, Case[][] plateau) {
        // on part dans chacune des 4 directions
        for (Direction dir : Direction.toutes()) {
            ajoutDeplDansDirection(dir, coord, actions, nbPv, couleurJoueur, plateau);
        }
        // on ajoute le déplacement "sur place"
        ajoutDepl(coord, coord, actions, nbPv, couleurJoueur, plateau);
    }

    /**
     * Ajouter tous les déplacements depuis une case donnée, dans une direction
     * donnée.
     *
     * @param dir direction à suivre
     * @param src coordonnées de la case d'origine
     * @param actions ensemble des actions possibles, à compléter
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * @param couleurJoueur couleur du joueur initial
     */
    void ajoutDeplDansDirection(Direction dir, Coordonnees src,
            ActionsPossibles actions, NbPointsDeVie nbPv, char couleurJoueur, Case[][] plateau) {
        Coordonnees dst = src.suivantes(dir);
        while (dst.estDansPlateau()) {
            if (!plateau[dst.ligne][dst.colonne].unitePresente()) {
                ajoutDepl(src, dst, actions, nbPv, couleurJoueur, plateau);
            }
            dst = dst.suivantes(dir);
        }
    }

    void voisineHostile(Coordonnees src, Coordonnees dst,
            ActionsPossibles actions, NbPointsDeVie nbPv, char couleurJoueur, Case[][] plateau) {
        for (Direction dir : Direction.toutes()) {
            Coordonnees target = dst.suivantes(dir);
            if (target.estDansPlateau()) {
                if ((plateau[target.ligne][target.colonne].couleurUnite != couleurJoueur)
                        && (plateau[target.ligne][target.colonne].unitePresente())) {
                    NbPointsDeVie newNbPv = new NbPointsDeVie(nbPv);
                    int oldPvAttaquant = plateau[src.ligne][src.colonne].pointsDeVie;
                    int oldPvAttaque = plateau[target.ligne][target.colonne].pointsDeVie;
                    int degatsSurAttaquant = 2 + ((oldPvAttaque - 5) / 2);
                    int degatsSurAttaque = 4 + ((oldPvAttaquant - 5) / 2);
                    if(degatsSurAttaque >= oldPvAttaque){
                        degatsSurAttaque = oldPvAttaque;
                    }
                    if(degatsSurAttaquant >= oldPvAttaquant){
                        degatsSurAttaquant = oldPvAttaquant;
                    }
                    if (couleurJoueur == 'R') {
                        newNbPv.nbPvRouge -= degatsSurAttaquant;
                        newNbPv.nbPvNoir -= degatsSurAttaque;
                    }
                    if (couleurJoueur == 'N') {
                        newNbPv.nbPvNoir -= degatsSurAttaquant;
                        newNbPv.nbPvRouge -= degatsSurAttaque;
                    }
                    ajoutAttaque(src, dst, target, actions, newNbPv);
                }
            }
        }
    }

    /**
     * Ajout d'une action de déplacement dans l'ensemble des actions possibles.
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param actions l'ensemble des actions possibles (en construction)
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * @param couleurJoueur couleur du joueur qui joue actuellement
     * initial
     */
    void ajoutDepl(Coordonnees src, Coordonnees dst, ActionsPossibles actions,
            NbPointsDeVie nbPv, char couleurJoueur, Case[][] plateau) {
        voisineHostile(src, dst, actions, nbPv, couleurJoueur, plateau);
        actions.ajouterAction(chaineActionDepl(src, dst, nbPv));
    }

    /**
     * Ajout d'une action d'attaque dans l'ensemble des actions possibles.
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param actions l'ensemble des actions possibles (en construction)
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * initial
     */
    void ajoutAttaque(Coordonnees src, Coordonnees dst, Coordonnees target, ActionsPossibles actions,
            NbPointsDeVie nbPv) {
        actions.ajouterAction(chaineActionAttaque(src, dst, target, nbPv));
    }

    /**
     * Chaîne de caractères correspondant à une action-mesure de déplacement.
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param nbPv nombre de points de vie de chaque joueur après l'action
     * @return la chaîne codant cette action-mesure
     */
    static String chaineActionDepl(Coordonnees src, Coordonnees dst, NbPointsDeVie nbPv) {
        return "" + src.carLigne() + src.carColonne()
                + "D" + dst.carLigne() + dst.carColonne()
                + "," + nbPv.nbPvRouge + "," + nbPv.nbPvNoir;
    }

    static String chaineActionAttaque(Coordonnees src, Coordonnees dst, Coordonnees target, NbPointsDeVie nbPv) {
        return "" + src.carLigne() + src.carColonne()
                + "D" + dst.carLigne() + dst.carColonne()
                + "A" + target.carLigne() + target.carColonne()
                + "," + nbPv.nbPvRouge + "," + nbPv.nbPvNoir;
    }
}
