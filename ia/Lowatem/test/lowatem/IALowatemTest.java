/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lowatem;

import java.util.Random;
import static lowatem.IALowatem.mettreAJour;
import org.junit.Test;

/**
 *
 * @author ralarcon
 */
public class IALowatemTest {

    final int DEPTH = 2;

    @Test
    public void testIA() {
        JoueurLowatem joueur = new JoueurLowatem();
        // un plateau sur lequel on veut tester actionsPossibles()
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU6);
        // on choisit la couleur du joueur
        char couleur = 'R';
        // on choisit le niveau
        int niveau = 6;
        // on lance actionsPossibles
        String[] actionsPossiblesDepuisPlateau
                = joueur.actionsPossibles(plateau, couleur, niveau);
        ActionsPossibles actionsPossibles
                = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        int tours = 0;
        
        while(JoueurLowatem.nbPointsDeVie(plateau).nbPvNoir != 0 && JoueurLowatem.nbPointsDeVie(plateau).nbPvRouge != 0 ) { //&& tours <=40
            long start = System.currentTimeMillis();
            
            int v = negamax(plateau, DEPTH, (couleur == 'R') ? 1:-1, joueur);
            
            long finish = System.currentTimeMillis();
            System.out.println("negamax took: " + (finish - start) + "ms\n");
            tours++;
            
            if (JoueurLowatem.nbPointsDeVie(plateau).nbPvNoir != 0 && JoueurLowatem.nbPointsDeVie(plateau).nbPvRouge != 0 ) { //&& tours <= 40
                String[] actionsPossiblesEnn = ActionsPossibles.nettoyerTableau(
                joueur.actionsPossibles(plateau, couleur, 6));
                Random r = new Random();
                int indiceAleatoire = r.nextInt(actionsPossiblesEnn.length);
                String actionJouee = ActionsPossibles.enleverPointsDeVie(
                    actionsPossiblesEnn[indiceAleatoire]);
                // jouer l'action
                printHp(plateau);
                System.out.println("Tacheron joue : " + actionsPossiblesEnn[indiceAleatoire]);
                IALowatem.mettreAJour(plateau, actionJouee);
//                JoueurLowatem.displayPlateau(plateau);
                tours++;
            }
        }
    }
    
    static public void printHp(Case[][] board) {
        System.out.print("("+ JoueurLowatem.nbPointsDeVie(board).nbPvRouge + ":" + JoueurLowatem.nbPointsDeVie(board).nbPvNoir + ") ");
    }
    
    
    public int negamax(Case[][] plateau, int depth, int couleur, JoueurLowatem joueurLowatem) {
        NbPointsDeVie nbPv = JoueurLowatem.nbPointsDeVie(plateau);
        if (depth == 0 || partieFinie(nbPv)) {
            return couleur * heuristicValue(plateau, nbPv);
        }
        int bestScore = -Integer.MIN_VALUE;
        String bestAction = "";
        String[] actionsPossibles = ActionsPossibles.nettoyerTableau(
                joueurLowatem.actionsPossibles(plateau, (couleur == -1) ? 'R':'N', 6));
        for (String action : actionsPossibles) {
            Case[][] copiePlateau = deepCopy(plateau);
            mettreAJour(copiePlateau, action);
            int scoreAction = -negamax(copiePlateau, depth - 1, -couleur, joueurLowatem);
            if (scoreAction > bestScore) {
                bestScore = scoreAction;
                bestAction = action;
            }
        }
        if (depth == DEPTH) {
            System.out.println("On joue : " + bestAction);
            mettreAJour(plateau, bestAction);
        }
        return bestScore;
    }
    
    public Case[][] deepCopy(Case[][] plateau) {
        Case[][] plateauCopie = new Case[plateau.length][plateau[0].length];
        for (int i = 0; i < plateau.length; i++) {
            for (int j = 0; j < plateau[0].length; j++) {
                plateauCopie[i][j] = new Case(plateau[i][j].typeUnite, plateau[i][j].couleurUnite, plateau[i][j].pointsDeVie, plateau[i][j].altitude, plateau[i][j].nature);
            }
        }
        return plateauCopie;
    }
                
    public int heuristicValue(Case[][] plateau, NbPointsDeVie nbPv) {
        return nbPv.nbPvRouge - nbPv.nbPvNoir;
    }
    
    public boolean partieFinie(NbPointsDeVie nbPv) {
        return (nbPv.nbPvRouge <= 0 || nbPv.nbPvRouge <= 0);
    }
    
    final String PLATEAU_NIVEAU6
            = "   A   B   C   D   E   F   G   H   I   J   K   L   M   N  \n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "a|   |   |   |   |   |   |   |SR8|   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "b|   |SN4|   |   |   |SN7|   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "c|SN9|SR9|SR4|   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "d|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "e|   |   |   |   |   |   |   |SR7|   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "f|   |   |   |   |   |   |   |   |   |   |   |SR9|SN4|   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "g|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "h|   |   |SN9|   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "i|   |   |   |   |   |   |   |SR6|   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "j|   |   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "k|   |   |   |   |   |   |   |   |   |   |SR5|   |   |SN5|\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "l|   |   |   |   |   |   |   |   |   |   |   |SN6|SR9|   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "m|SN8|   |   |   |   |   |   |   |   |   |   |   |   |   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n"
            + "n|   |   |   |   |SN9|   |   |   |   |   |   |   |SR4|   |\n"
            + " +---+---+---+---+---+---+---+---+---+---+---+---+---+---+\n";
}
