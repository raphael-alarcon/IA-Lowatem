package lowatem;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Joueur implémentant les actions possibles à partir d'un plateau, pour un
 * niveau donné.
 */
public class JoueurLowatem implements IJoueurLowatem {

    SimpleDateFormat format;
    Case[][] plateau;
    int niveau = 0;
    char couleur;
    public final static char CAR_EAU = 'E';

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
        this.plateau = plateau;
        this.niveau = niveau;
        this.couleur = couleurJoueur;

        // afficher l'heure de lancement
        format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        System.out.println("actionsPossibles : lancement le " + format.format(new Date()));
        // se préparer à stocker les actions possibles
        ActionsPossibles actions = new ActionsPossibles();
        // calculer les points de vie sur le plateau initial
        NbPointsDeVie nbPv = nbPointsDeVie(this.plateau);
        // déplacements possibles depuis chaque unite
        for (int i=0; i<plateau.length; i++) {
            Case[] cl = plateau[i];
            for (int j=0; j<cl.length; j++) {
                Case c=cl[j];
                if (c.unitePresente() && c.couleurUnite == couleurJoueur) {
//                    System.out.println(c.couleurUnite +" "+ (new Coordonnees(i, j)).carLigne() +","+ (new Coordonnees(i, j)).carColonne());
                    ajoutDeplDepuis(new Coordonnees(i, j), actions, nbPv);
                }
            }
        }

        if (niveau >= 11) {
            // HANDLE FLEAU
            for (Direction d : Direction.toutes()) {
                ajoutFleauDepuis(d, actions, nbPv);
            }
        }

        if (niveau >= 14) {
            if (checkBlast()) {
                NbPointsDeVie newNbPv = new NbPointsDeVie(nbPv);
                int s=0;
                for (int i=0; i<plateau.length; i++) {
                    Case[] cl = plateau[i];
                    for (int j=0; j<cl.length; j++) {

                        Case c=cl[j];
                        if (c.unitePresente() && c.couleurUnite != couleurJoueur) {
//                            Coordonnees coord = new Coordonnees(i, j);
                            int newHp = (int)(c.pointsDeVie/2);
                            s +=(c.pointsDeVie - newHp);

                        }
                    }
                }
                if (couleurJoueur == 'R') {
                    newNbPv.nbPvNoir -= s;

                } else {
                    newNbPv.nbPvRouge -= s;
                }
                actions.ajouterAction("B,"+newNbPv.nbPvRouge+","+newNbPv.nbPvNoir); //,"+newNbPv.nbPvRouge+","+newNbPv.nbPvNoir
            }
        }

        System.out.println("actionsPossibles : fin " + format.format(new Date()));
        return actions.nettoyer();
    }

    /**
     * Vérifie si un blast est possible et l'ajoute si possible
     */
    boolean checkBlast() {
        boolean blastCheck;
        char[] possibleColors = {'R', 'N'};
        for (char c1 : possibleColors) {
            for (char c2 : possibleColors) {
                for (char c3 : possibleColors) {
                    for (char c4 : possibleColors) {
                        for (char c5 : possibleColors) {
                            for (char c6 : possibleColors) {
                                for (char c7 : possibleColors) {
                                    for (char c8 : possibleColors) {
                                        for (char c9 : possibleColors) {
                                            for (char c10 : possibleColors) {
                                                for (char c11 : possibleColors) {
                                                    for (char c12 : possibleColors) {
                                                        for (char c13 : possibleColors) {
                                                            for (char c14 : possibleColors) {
                                                                blastCheck = true;
                                                                Character[] coloration = {c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14};
                                                                Boolean[] bools = new Boolean[14];

                                                                for (int j=0; j< plateau[0].length; j++) {
                                                                    int nbUnits = 0;
                                                                    int i=0;
                                                                    while (i<plateau.length && bools[j] == null) {
                                                                        if (plateau[i][j].unitePresente()) {
                                                                            nbUnits++;
                                                                            if (plateau[i][j].couleurUnite == coloration[i]) {
                                                                                bools[j] = true;
                                                                            }
                                                                        }
                                                                        i++;
                                                                    }
                                                                    if (nbUnits != 0 && bools[j]==null) {
                                                                        bools[j] = false;
                                                                    }
                                                                }

                                                                for (Boolean b: bools) {
                                                                    if (b != null && !b) {
                                                                        blastCheck = false;
                                                                        break;
                                                                    }
                                                                }

                                                                if (blastCheck) {
                                                                    return true;
                                                                }

                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Nombre de points de vie de chaque joueur sur le plateau.
     *
     * @param plateau le plateau
     * @return le nombre de points de vie de chaque joueur
     */
    static NbPointsDeVie nbPointsDeVie(Case[][] plateau) {
        int ptVieN = 0;
        int ptVieR = 0;
        for (int i=0; i<plateau.length; i++) {
            Case[] cl = plateau[i];
            for (int j=0; j<cl.length; j++) {
                Case c=cl[j];
                if (c.unitePresente()) {
                    if (c.couleurUnite == 'N') {
                        ptVieN+=c.pointsDeVie;
                    } else {
                        ptVieR+=c.pointsDeVie;
                    }
                }

            }
        }
        return new NbPointsDeVie(ptVieR, ptVieN);
    }

    /**
     * Ajouter le fléau depuis une dir donnée si possible
     *
     * @param dir direction du fleau
     * @param actions ensemble des actions possibles
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * initial
     */
    void ajoutFleauDepuis(Direction dir, ActionsPossibles actions, NbPointsDeVie nbPv) {
        Coordonnees[] hits = new Coordonnees[plateau.length]; //on considère que le tableau est carré
        Case[] hitsCases = new Case[plateau.length]; //on considère que le tableau est carré

        int[] hitDmg = new int[plateau.length];
        for (int i=0; i<hitDmg.length; i++) {
            hitDmg[i] = -1;
        }
        //boolean allFound = false;
        int dmg;
        Case c;
        int dst = 0;
        switch (dir) {
            case NORD:
                for (int ligne = 0; ligne<plateau.length; ligne++) {
                    for (int colonne=0; colonne<plateau[0].length; colonne++) {
                        c = getCase(new Coordonnees(ligne, colonne));
                        if (c.unitePresente() && hits[colonne] == null) {
                            hits[colonne] = new Coordonnees(ligne, colonne);
                            dmg = Math.max((9 - dst), 0);
                            hitDmg[colonne] = ((c.pointsDeVie-dmg) > 0) ? dmg: c.pointsDeVie;
                            hitsCases[colonne] = c;
                        }
                    }
                    dst++;
                }
                break;
            case SUD:
                for (int ligne = plateau.length-1; ligne>0; ligne--) {
                    for (int colonne=0; colonne<plateau[0].length; colonne++) {
                        c = getCase(new Coordonnees(ligne, colonne));
                        if (c.unitePresente() && hits[colonne] == null) {
                            hits[colonne] = new Coordonnees(ligne, colonne);
                            dmg = Math.max((9 - dst), 0);
                            hitDmg[colonne] = ((c.pointsDeVie-dmg) > 0) ? dmg: c.pointsDeVie;
                            hitsCases[colonne] = c;
                        }
                    }
                    dst++;
                }
                break;
            case OUEST:
                for (int ligne = 0; ligne<plateau.length; ligne++) {
                    dst=0;
                    for (int colonne=0; colonne<plateau[0].length; colonne++) {
                        c = getCase(new Coordonnees(ligne, colonne));
                        if (c.unitePresente() && hits[ligne] == null) {
                            hits[ligne] = new Coordonnees(ligne, colonne);
                            dmg = Math.max((9 - dst), 0);
                            hitDmg[ligne] = ((c.pointsDeVie-dmg) > 0) ? dmg: c.pointsDeVie;
                            hitsCases[ligne] = c;
                        }
                        dst++;
                    }
                }
                break;
            case EST:
                for (int ligne = 0; ligne< plateau.length; ligne++) {
                    dst=0;
                    for (int colonne=plateau[0].length-1; colonne>0; colonne--) {
                        c = getCase(new Coordonnees(ligne, colonne));
                        if (c.unitePresente() && hits[ligne] == null) {
                            hits[ligne] = new Coordonnees(ligne, colonne);
                            dmg = Math.max((9 - dst), 0);
                            hitDmg[ligne] = ((c.pointsDeVie-dmg) > 0) ? dmg: c.pointsDeVie;
                            hitsCases[ligne] = c;
                        }
                        dst++;
                    }
                }
                break;
        }
        NbPointsDeVie newPv = new NbPointsDeVie(nbPv);
        for (int i=0; i<hits.length; i++) {
            if (hitDmg[i] != -1 && hits[i] != null) {
                if (getCase(hits[i]).couleurUnite == 'R') {
                    newPv.nbPvRouge -= hitDmg[i];
                } else {
                    newPv.nbPvNoir -= hitDmg[i];
                }
            }
        }
        ajoutFleau(dir, actions, newPv);
    }

    /**
     * Ajouter tous les déplacements depuis une case donnée.
     *
     * @param coord coordonnées de la case d'origine
     * @param actions ensemble des actions possibles
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * initial
     */
    void ajoutDeplDepuis(Coordonnees coord, ActionsPossibles actions, NbPointsDeVie nbPv) {
        for (Direction dir : Direction.toutes()) {
            ajoutDeplDansDirection(dir, coord, actions, nbPv);
        }

        // on ajoute le déplacement "sur place" et les attaques si nécessaire
        ajoutDeplAndAtts(coord, coord, actions, nbPv);
    }

    /**
     * (implémentation questionnable de DFS)
     * Ajouter récursivement les coûts minimaux de déplacement sur toutes les cases dans la portée de src au plateau de coûts
     *
     * @param src coordonnées de la case à l'origine du déplacement (à chaque iter)
     * @param costs plateau de coûts de déplacement
     * @param moveCost coût de déplacement de l'unité se déplaçant
     * @param baseHp hp de base de l'unité se déplaçant
     * @param depth profondeur de la récursion (aka distance parcourue ici)
     */
    void recurseBagarre(Integer[][] costs, Coordonnees src, float moveCost, int baseHp, int depth) {
        if (costs[src.ligne][src.colonne] != null) {
            costs[src.ligne][src.colonne] = Math.min(costs[src.ligne][src.colonne], (int) (depth * moveCost));
        } else if (!getCase(src).unitePresente()) {
            costs[src.ligne][src.colonne] = (int) (depth * moveCost);
        }

        Coordonnees c;
        for (Direction d : Direction.toutes()) {
            c = src.suivantes(d);
            if (c.estDansPlateau()
                    && getCase(c).altitude == getCase(src).altitude
                    && getCase(c).nature == 'T'
                    && (int) ((depth+1) * moveCost) < baseHp
                    && (costs[c.ligne][c.colonne] == null || (int) ((depth+1) * moveCost) <= costs[c.ligne][c.colonne])
            ) { //
                recurseBagarre(costs, c, moveCost, baseHp, depth+1);
            }
        }
    }

    /**
     * Récupérer toutes les coordonnées attaquables depuis une coordonnée
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param r portée de l'attaque (radius)
     */
    Coordonnees[] getRadius(Coordonnees src, int r) {
        Coordonnees[] cs = new Coordonnees[2*(r*r) + 2*r]; // 4 (d(d-1)/2 + d) car 4 * Somme de 0 à d de k + 4 * d

        int i = 0;
        for (int dy = -r; dy <= r; dy++) { // de -radius à +radius (figure symétrique)
            int y = (r - Math.abs(dy));
            for (int dx = -y; dx <= y; dx++) { //carré à 45° donc chaque point est symétrique par rapport à l'axe des abcisses
                Coordonnees dst = new Coordonnees(src.ligne + dy, src.colonne + dx);
                cs[i] = dst;
                if (!dst.equals(src) && dst.estDansPlateau() && getCase(dst).unitePresente() && getCase(dst).couleurUnite != couleur) {
                    i++;
                }
            }
        }
        return Arrays.copyOf(cs, i);
    }

    /**
     * Ajouter toutes les attaques possibles à partir d'une coordonée
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param actions ensemble des actions possibles
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     */
    void addAttAdj(Coordonnees src, Coordonnees dst, ActionsPossibles actions, NbPointsDeVie nbPv) {
        for (Coordonnees adjCoords: getRadius(dst, 1)) { // for each pos in within reach
            if (niveau >= 6) { // for unit tests to work with old vers
                int default_atk = 4; //8
                int default_recoil = 2; //3

                int oldPvAttaquant = getCase(src).pointsDeVie; // 4
                int oldPvAttaque = getCase(adjCoords).pointsDeVie; //4

                int deltaAtk = default_recoil + (int)((oldPvAttaque-5)/2); // 3 + -1/2
                int deltaAtque = default_atk + (int)((oldPvAttaquant-5)/2); // 8 +

                if (oldPvAttaquant - deltaAtk <= 0) {
                    deltaAtk = oldPvAttaquant;
                }
                if (oldPvAttaque - deltaAtque <= 0) {
                    deltaAtque = oldPvAttaque;
                }

                NbPointsDeVie newPv = (couleur == 'R') ? new NbPointsDeVie(nbPv.nbPvRouge - deltaAtk, nbPv.nbPvNoir - deltaAtque)
                        : new NbPointsDeVie(nbPv.nbPvRouge - deltaAtque, nbPv.nbPvNoir  - deltaAtk);
                ajoutDeplAtt(src, dst, adjCoords, actions, newPv); //add attack move with new hp
            } else {
                ajoutDeplAtt(src, dst, adjCoords, actions, nbPv); //add attack move
            }
        }
    }

    /**
     * Ajouter tous les déplacements depuis une case donnée, dans une direction
     * donnée.
     *
     * @param dir direction à suivre
     * @param src coordonnées de la case d'origine
     * @param actions ensemble des actions possibles
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * initial
     */
    void ajoutDeplDansDirection(Direction dir, Coordonnees src, ActionsPossibles actions, NbPointsDeVie nbPv) {
        Coordonnees dst = src.suivantes(dir);
        while (dst.estDansPlateau()) {

            if (!getCase(dst).unitePresente()) {
                ajoutDeplAndAtts(src, dst, actions, nbPv);
            }
            dst = dst.suivantes(dir);
        }
    }

    /**
     * Récuperer la case correspondante aux coordonées
     *
     * @param coord coordonnées de la case à récupérer
     */
    Case getCase(Coordonnees coord) {
        return this.plateau[coord.ligne][coord.colonne];
    }

    /**
     * Calcul de la distance de manhattan parcourue lors d'un déplacement
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     */
    static int mDist(Coordonnees src, Coordonnees dst) {
        return (Math.abs(dst.colonne - src.colonne) + Math.abs(dst.ligne - src.ligne));
    }


    /**
     * Calcul des attaques possibles après un déplacemnt
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param actions l'ensemble des actions possibles
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau initial
     */
    void ajoutDeplAndAtts(Coordonnees src, Coordonnees dst, ActionsPossibles actions, NbPointsDeVie nbPv) {
        NbPointsDeVie newNbPv = new NbPointsDeVie(nbPv.nbPvRouge, nbPv.nbPvNoir);
        if (getCase(src).pointsDeVie > 0) {
            ajoutDepl(src, dst, actions, newNbPv);

            if (niveau >=5) {
                addAttAdj(src, dst, actions, newNbPv);
            }
        }
    }


    /**
     * Ajout d'une action de déplacement dans l'ensemble des actions possibles.
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param actions l'ensemble des actions possibles
     * @param nbPv nombre de points de vie de chaque joueur sur le plateauhome
     * initial
     */
    void ajoutDepl(Coordonnees src, Coordonnees dst, ActionsPossibles actions, NbPointsDeVie nbPv) {
        actions.ajouterAction(chaineActionDepl(src, dst, nbPv));
    }

    /**
     * Ajout d'une action de Fléau
     *
     * @param dir direction du fléau
     * @param actions l'ensemble des actions possibles
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * initial
     */
    void ajoutFleau(Direction dir, ActionsPossibles actions, NbPointsDeVie nbPv) {
        actions.ajouterAction(chaineActionFleau(dir, nbPv));
    }

    /**
     * Ajout d'une action de déplacement+attaque dans l'ensemble des actions possibles.
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param enn coordonnées de la case du pion a attaquer
     * @param actions l'ensemble des actions possibles
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * initial
     */
    void ajoutDeplAtt(Coordonnees src, Coordonnees dst, Coordonnees enn, ActionsPossibles actions, NbPointsDeVie nbPv) {
        actions.ajouterAction(chaineActionDeplAtt(src, dst, enn, nbPv));
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

    /**
     * Chaîne de caractères correspondant à une action-mesure de Fléau
     *
     * @param dir direction du fléau
     * @param nbPv nombre de points de vie de chaque joueur après l'action
     * @return la chaîne codant cette action-mesure
     */
    static String chaineActionFleau(Direction dir, NbPointsDeVie nbPv) {
        return "F" + dir.toString().charAt(0)
                + "," + nbPv.nbPvRouge + "," + nbPv.nbPvNoir;
    }

    /**
     * Chaîne de caractères correspondant à une action-mesure de déplacement + attaque.
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param enn coordonnées de la case du à attaquer
     * @param nbPv nombre de points de vie de chaque joueur après l'action
     * @return la chaîne codant cette action-mesure
     */
    static String chaineActionDeplAtt(Coordonnees src, Coordonnees dst, Coordonnees enn, NbPointsDeVie nbPv) {
        return "" + src.carLigne() + src.carColonne()
                + "D" + dst.carLigne() + dst.carColonne()
                + "A" + enn.carLigne() + enn.carColonne()
                + "," + nbPv.nbPvRouge + "," + nbPv.nbPvNoir;
    }
}