package net.scottnotfound.clara.reaction;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

import java.util.ArrayList;
import java.util.List;

class ReactionBuilder {

    private static ReactionBuilder RB_INSTANCE;
    private static NameToStructure NTS_INSTANCE;
    private static SmilesParser SP_INSTANCE;

    static ReactionBuilder getInstance() {
        if (RB_INSTANCE == null) {
            RB_INSTANCE = new ReactionBuilder();
        }
        return RB_INSTANCE;
    }

    private ReactionBuilder() {
        NTS_INSTANCE = NameToStructure.getInstance();
        SP_INSTANCE = new SmilesParser(SilentChemObjectBuilder.getInstance());

    }

    IReaction build(List<String> reactants) {
        return this.build(reactants, new ArrayList<>());
    }

    IReaction build(List<String> reactantNames, List<String> agentNames) {

        List<String> reactantSmiles = namesToSmiles(reactantNames);
        List<String> agentSmiles = namesToSmiles(agentNames);

        IAtomContainerSet reactants = smilesToAtomContainerSet(reactantSmiles);
        IAtomContainerSet agents = smilesToAtomContainerSet(agentSmiles);

        IReaction reaction = new Reaction();
        reactants.atomContainers().forEach(reaction::addReactant);
        agents.atomContainers().forEach(reaction::addAgent);

        return reaction;
    }

    private List<String> namesToSmiles(List<String> names) {
        List<String> smiles = new ArrayList<>();
        for (String name : names) {
            String smile = NTS_INSTANCE.parseToSmiles(name);
            smiles.add(smile);
        }
        return smiles;
    }

    private IAtomContainerSet smilesToAtomContainerSet(List<String> smiles) {
        IAtomContainerSet atomContainerSet = new AtomContainerSet();
        for (String smile : smiles) {
            try {
                IAtomContainer atomContainer = SP_INSTANCE.parseSmiles(smile);
                atomContainerSet.addAtomContainer(atomContainer);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return atomContainerSet;
    }
}
