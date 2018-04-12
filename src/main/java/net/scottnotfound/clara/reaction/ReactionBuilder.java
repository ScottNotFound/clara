package net.scottnotfound.clara.reaction;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.AtomHybridizationVSEPRDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.CovalentRadiusDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.BondPartialPiChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.BondPartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.BondPartialTChargeDescriptor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

import java.io.IOException;
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

    IReactionProfile buildProfile(List<String> reactants) {
        return new ReactionProfile(buildReaction(reactants));
    }

    IReactionProfile buildProfile(List<String> reactants, String flagSequence) {
        return new ReactionProfile(buildReaction(reactants), flagSequence);
    }

    IReactionProfile buildProfile(List<String> reactants, List<String> agents) {
        return new ReactionProfile(buildReaction(reactants, agents));
    }

    IReactionProfile buildProfile(List<String> reactants, List<String> agents, String flagSequence) {
        return new ReactionProfile(buildReaction(reactants, agents), flagSequence);
    }

    IReactionProfile buildProfile(IReaction reaction) {
        return new ReactionProfile(reaction);
    }

    IReactionProfile buildProfile(IReaction reaction, String flagSequence) {
        return new ReactionProfile(reaction, flagSequence);
    }

    private IReaction buildReaction(List<String> reactants) {
        return this.buildReaction(reactants, new ArrayList<>());
    }

    /**
     * Builds a reaction from the list of reactants and agents. The built reaction will
     * contain no products.
     *
     * @param reactantNames names of reactants
     * @param agentNames names of agents
     * @return built reaction
     */
    private IReaction buildReaction(List<String> reactantNames, List<String> agentNames) {

        List<String> reactantSmiles = namesToSmiles(reactantNames);
        List<String> agentSmiles = namesToSmiles(agentNames);

        IAtomContainerSet reactants = smilesToAtomContainerSet(reactantSmiles);
        IAtomContainerSet agents = smilesToAtomContainerSet(agentSmiles);

        IReaction reaction = new Reaction();
        reactants.atomContainers().forEach(reaction::addReactant);
        agents.atomContainers().forEach(reaction::addAgent);

        return reaction;
    }

    /**
     * Constructs a list of names from a list of smiles.
     *
     * @param names list of names
     * @return list of smiles
     */
    private List<String> namesToSmiles(List<String> names) {
        List<String> smiles = new ArrayList<>();
        for (String name : names) {
            String smile = NTS_INSTANCE.parseToSmiles(name);
            smiles.add(smile);
        }
        return smiles;
    }

    /**
     * Constructs a set of molecules from a list of smiles.
     *
     * @param smiles list of smiles
     * @return set of molecules
     */
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

    /**
     * Computes properties of the molecules in a reaction.
     *
     * @param reaction reaction to compute properties of
     * @return the same reaction with computed properties
     */
    private IReaction computeProperties(IReaction reaction) {

        reaction = computePartialCharge(reaction);
        reaction = computeHybridization(reaction);
        reaction = computeCovalentRadius(reaction);

        return reaction;
    }

    /**
     * Computes hybridization of the atoms of the molecules in a reaction.
     *
     * @param reaction reaction to compute properties of
     * @return the same reaction with computed properties
     */
    private IReaction computeHybridization(IReaction reaction) {

        IAtomicDescriptor hybridizationVSEPRDescriptor = new AtomHybridizationVSEPRDescriptor();

        for (IAtomContainer mol : reaction.getReactants().atomContainers()) {

            for (IAtom atom : mol.atoms()) {

                IAtomType.Hybridization hybridization = null;

                DescriptorValue descriptorValue = hybridizationVSEPRDescriptor.calculate(atom, mol);
                Integer value = Integer.parseInt(descriptorValue.getValue().toString());

                switch (value) {
                    case 1: hybridization = IAtomType.Hybridization.SP1;    break;
                    case 2: hybridization = IAtomType.Hybridization.SP2;    break;
                    case 3: hybridization = IAtomType.Hybridization.SP3;    break;
                    case 4: hybridization = IAtomType.Hybridization.SP3D1;  break;
                    case 5: hybridization = IAtomType.Hybridization.SP3D2;  break;
                    case 6: hybridization = IAtomType.Hybridization.SP3D3;  break;
                    case 7: hybridization = IAtomType.Hybridization.SP3D4;  break;
                    case 8: hybridization = IAtomType.Hybridization.SP3D5;  break;
                }

                atom.setHybridization(hybridization);

            }
        }

        return reaction;
    }

    /**
     * Computes covalent radii of the atoms of the molecules in a reaction.
     *
     * @param reaction reaction to compute properties of
     * @return the same reaction with computed properties
     */
    private IReaction computeCovalentRadius(IReaction reaction) {

        IAtomicDescriptor covalentRadiusDescriptor = null;

        try {
            covalentRadiusDescriptor = new CovalentRadiusDescriptor();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (IAtomContainer mol : reaction.getReactants().atomContainers()) {

            for (IAtom atom : mol.atoms()) {

                DescriptorValue descriptorValue = covalentRadiusDescriptor.calculate(atom, mol);
                Double radius = Double.parseDouble(descriptorValue.getValue().toString());

                atom.setCovalentRadius(radius);

            }

        }

        return reaction;
    }

    /**
     * Computes partial charges of the molecules in a reaction.
     *
     * @param reaction reaction to compute properties of
     * @return the same reaction with computed properties
     */
    private IReaction computePartialCharge(IReaction reaction) {

        IBondDescriptor partialPiChargeDescriptor       = new BondPartialPiChargeDescriptor();
        IBondDescriptor partialSigmaChargeDescriptor    = new BondPartialSigmaChargeDescriptor();
        IBondDescriptor partialTChargeDescriptor        = new BondPartialTChargeDescriptor();

        for (IAtomContainer mol : reaction.getReactants().atomContainers()) {

            for (IBond bond : mol.bonds()) {

                Object partialPiCharge      = partialPiChargeDescriptor.calculate(bond, mol).getValue().toString();
                Object partialSigmaCharge   = partialSigmaChargeDescriptor.calculate(bond, mol).getValue().toString();
                Object partialTCharge       = partialTChargeDescriptor.calculate(bond, mol).getValue().toString();

                bond.setProperty("partialPiCharge", partialPiCharge);
                bond.setProperty("partialSigmaCharge", partialSigmaCharge);
                bond.setProperty("partialTCharge", partialTCharge);

            }
        }

        return reaction;
    }

}
