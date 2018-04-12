package net.scottnotfound.clara.reaction;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.atomic.AtomHybridizationVSEPRDescriptor;

class ReactionEngine {

    private static ReactionEngine RE_INSTANCE;


    static ReactionEngine getInstance() {
        if (RE_INSTANCE == null) {
            RE_INSTANCE = new ReactionEngine();
        }
        return RE_INSTANCE;
    }

    private ReactionEngine() {

    }

    IReaction solveReaction(IReaction unsolvedReaction) {

        return unsolvedReaction;
    }

    IReaction solveSimpleSN2Reaction(IReaction unsolvedReaction) {
        if (!checkSimpleSN2(unsolvedReaction)) {
            System.out.println("Reaction is not a valid single halogen alkane SN2 reaction with alkali halogen salt.");
        }

        return unsolvedReaction;
    }

    private boolean isValidReaction(IReaction reaction) {
        return reaction.getReactantCount() != 1;
    }

    /**
     * Checks the reaction to see of the reaction is between a haloalkane and a halogen alkali salt.
     * The halogen must not be on a tertiary carbon and there may be only one halogen in the reacting molecule.
     * The reacting molecule must also be under 30 atoms in size.
     *
     * @param reaction reaction to check
     * @return true if the reaction is valid, false otherwise
     */
    private boolean checkSimpleSN2(IReaction reaction) {

        if (!isValidReaction(reaction)) {
            return false;
        }

        if (reaction.getReactantCount() != 2) {
            return false;
        }

        Iterable<IAtomContainer> reactants = reaction.getReactants().atomContainers();
        for (IAtomContainer reactant : reactants) {

            if (reactant.getAtomCount() > 30) {
                return false;
            }
            if (reactant.getAtomCount() == 2) {
                boolean nuc = false;
                for (IAtom iAtom : reactant.atoms()) {
                    int an = iAtom.getAtomicNumber();
                    nuc = nuc || an == 9 || an == 17 || an == 35 || an == 53;
                }
                if (nuc) {
                    continue;
                }
            }

            boolean flag1 = false;
            Iterable<IAtom> atoms = reactant.atoms();
            for (IAtom atom : atoms) {

                int atomicNumber = atom.getAtomicNumber();
                if (atomicNumber != 6 && atomicNumber != 1) { // carbon or hydrogen

                    if (atomicNumber == 9 || atomicNumber == 17 || atomicNumber == 35 || atomicNumber == 53) {

                        if (flag1) {
                            return false;
                        } else {
                            flag1 = true;
                        }

                        for (IAtom atom1 : reactant.getConnectedAtomsList(atom)) {

                            DescriptorValue descriptorValue = new AtomHybridizationVSEPRDescriptor().calculate(atom1, reactant);
                            Integer hybridizationValue = Integer.parseInt(descriptorValue.getValue().toString());
                            if (hybridizationValue != 3) {
                                // needs sp3 hybridization
                                return false;
                            }
                            if (atom1.getImplicitHydrogenCount() == 0) {
                                // tertiary carbon
                                return false;
                            }

                        }

                    } else {
                        return false;
                    }

                }

            }
        }

        return true;
    }

}
