package Strategies;

/**
 * Enumeration de conjectures qu'on peut vérifier sur une strategie
 */
public enum Conjecture {

    CORRECTION,
    PSEUDO_CORRECTION,
    AGM_CORRECTION,

    COMPLETNESS,
    UNDER_COMPLETNESS,

    NONAME,
    AGM_CONSISTENCY,
    AGM_NONAME,

    AGM_PHI_INDEPENDENCE,
    AGM_PSI_INDEPENDENCE,

    AGM_ASSOCIATIVTY_1,
    AGM_ASSOCIATIVITY_2

}
