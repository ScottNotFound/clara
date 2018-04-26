package net.scottnotfound.clara.reaction;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.*;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;

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

    /**
     * Generates a bitset of the given dimension to act as the input layer of a neural network.
     * The fingerprints present are used as indices of the bitset vector and mapped to an address
     * space of dim.
     *
     * @param reaction Reaction from which the bitset is generated.
     * @param FPclass Class of the fingerprint.
     * @param dim Dimension or size of the bitset.
     * @return Bitset of the modulo folded fingerprints.
     */
    private BitSet generateFoldedFP(IReaction reaction, int FPclass, int dim) {

        IBitFingerprint bitSetFingerprint   = new BitSetFingerprint(dim);
        IFingerprinter fingerprinter        = new CircularFingerprinter(FPclass, dim);

        IAtomContainerSet atomContainerSet = new AtomContainerSet();
        atomContainerSet.add(reaction.getReactants());
        atomContainerSet.add(reaction.getAgents());
        atomContainerSet.add(reaction.getProducts());

        Spliterator<IAtomContainer> atomContainerSpliterator = atomContainerSet.atomContainers().spliterator();
        Stream<IAtomContainer> atomContainerStream = StreamSupport.stream(atomContainerSpliterator, true);

        // OR together all the bitsets of all the fingerprints
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

    /** Folds the fingerprint counts into a bitset. */
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
