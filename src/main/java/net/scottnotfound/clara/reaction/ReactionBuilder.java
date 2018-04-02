package net.scottnotfound.clara.reaction;

import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

import java.util.ArrayList;
import java.util.List;

public class ReactionBuilder {

    private static ReactionBuilder RB_INSTANCE;
    private static NameToStructure NTS_INSTANCE;
    private static SmilesParser SP_INSTANCE;

    public static ReactionBuilder getInstance() {
        if (RB_INSTANCE == null) {
            RB_INSTANCE = new ReactionBuilder();
        }
        return RB_INSTANCE;
    }

    private ReactionBuilder() {
        NTS_INSTANCE = NameToStructure.getInstance();
        SP_INSTANCE = new SmilesParser(SilentChemObjectBuilder.getInstance());

    }

    public IReaction build(List<String> reactants) {

        List<String> similes = new ArrayList<>();
        for (String reactant : reactants) {
            String simile = NTS_INSTANCE.parseToSmiles(reactant);
            similes.add(simile);
        }

        List<IAtomContainer> atomContainerList = new ArrayList<>();
        for (String simile : similes) {
            try {
                IAtomContainer atomContainer = SP_INSTANCE.parseSmiles(simile);
                atomContainerList.add(atomContainer);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        IReaction reaction = new Reaction();
        for (IAtomContainer atomContainer : atomContainerList) {
            reaction.addReactant(atomContainer);
        }

        return reaction;
    }
}
