package net.scottnotfound.clara.reaction;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.*;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.atomic.AtomHybridizationVSEPRDescriptor;

import java.util.BitSet;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    private BitSet generateFoldedFP(IReaction reaction, int FPclass, int dim) {

        IBitFingerprint bitSetFingerprint   = new BitSetFingerprint(dim);
        IFingerprinter fingerprinter        = new CircularFingerprinter(FPclass, dim);

        IAtomContainerSet atomContainerSet = new AtomContainerSet();
        atomContainerSet.add(reaction.getReactants());
        atomContainerSet.add(reaction.getAgents());

        Spliterator<IAtomContainer> atomContainerSpliterator = atomContainerSet.atomContainers().spliterator();
        Stream<IAtomContainer> atomContainerStream = StreamSupport.stream(atomContainerSpliterator, true);

        atomContainerStream.forEach(mol ->
                                    {
                                        try {
                                            bitSetFingerprint.or(fingerprinter.getBitFingerprint(mol));
                                        } catch (CDKException e) {
                                            e.printStackTrace();
                                        }
                                    });

        return bitSetFingerprint.asBitSet();
    }

    private BitSet foldCFP(ICountFingerprint countFingerprint, int dim) {

        final BitSet bitSet = new BitSet(dim);

        for (int k = 0; k < countFingerprint.numOfPopulatedbins(); k++) {
            int i = countFingerprint.getHash(k);
            long b = i >= 0 ? i : ((i & 0x7FFFFFFF) | (1L << 31));
            bitSet.set((int) (b % dim));
        }

        return bitSet;
    }

}
