package net.scottnotfound.clara.reaction;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.InvalidSmilesException;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

class ReactionProfileBuilder {

    private static final NameToStructure NTS_INSTANCE;
    private static final SmilesParser SP_INSTANCE;

    static {
        NTS_INSTANCE = NameToStructure.getInstance();
        SP_INSTANCE = new SmilesParser(SilentChemObjectBuilder.getInstance());
    }

    /** The profile that will be built. */
    private ReactionProfile reactionProfile;


    public ReactionProfileBuilder() {
        this.reactionProfile = new ReactionProfile();
    }

    public ReactionProfileBuilder(String flags) {
        this.reactionProfile = new ReactionProfile(flags);
    }

    public ReactionProfileBuilder(IReaction reaction) {
        this.reactionProfile = new ReactionProfile(reaction);
    }

    public ReactionProfileBuilder(IReaction reaction, String flags) {
        this.reactionProfile = new ReactionProfile(reaction, flags);
    }


    /**
     * Resets the builder to a blank slate with a new reaction profile.
     */
    public ReactionProfileBuilder reset() {
        this.reactionProfile = new ReactionProfile();
        return this;
    }

    public ReactionProfileBuilder setFlag(char c) {
        this.reactionProfile.setFlag(c);
        return this;
    }

    public ReactionProfileBuilder setFlag(char c, boolean b) {
        this.reactionProfile.setFlag(c, b);
        return this;
    }

    public ReactionProfileBuilder setFlags(char... cs) {
        this.reactionProfile.setFlags(cs);
        return this;
    }

    public ReactionProfileBuilder removeFlag(char c) {
        this.reactionProfile.removeFlag(c);
        return this;
    }

    public ReactionProfileBuilder toggleFlag(char c) {
        this.reactionProfile.toggleFlag(c);
        return this;
    }

    public ReactionProfileBuilder addReactant(String reactant) {
        this.reactionProfile.getReaction().addReactant(smileToMol(nameToSmile(reactant)));
        return this;
    }

    public ReactionProfileBuilder addAgent(String agent) {
        this.reactionProfile.getReaction().addAgent(smileToMol(nameToSmile(agent)));
        return this;
    }

    public ReactionProfileBuilder addProduct(String product) {
        this.reactionProfile.getReaction().addProduct(smileToMol(nameToSmile(product)));
        return this;
    }

    public ReactionProfileBuilder addReactants(String... reactants) {
        return this.addReactants(Arrays.asList(reactants));
    }

    public ReactionProfileBuilder addAgents(String... agents) {
        return this.addAgents(Arrays.asList(agents));
    }

    public ReactionProfileBuilder addProducts(String... products) {
        return this.addProducts(Arrays.asList(products));
    }

    public ReactionProfileBuilder addReactants(Collection<String> reactants) {
        this.reactionProfile.getReaction().getReactants().add(smilesToMols(namesToSmiles(reactants)));
        return this;
    }

    public ReactionProfileBuilder addAgents(Collection<String> agents) {
        this.reactionProfile.getReaction().getAgents().add(smilesToMols(namesToSmiles(agents)));
        return this;
    }

    public ReactionProfileBuilder addProducts(Collection<String> products) {
        this.reactionProfile.getReaction().getProducts().add(smilesToMols(namesToSmiles(products)));
        return this;
    }

    /**
     * Builds the reaction profile based off of what is currently set.
     * @return A new IReactionProfile
     */
    public IReactionProfile buildProfile() {
        IReactionProfile reactionProfile = this.reactionProfile.clone();
        for (char c : reactionProfile.getFlags().toCharArray()) {
            checkFlag(c);
        }
        return reactionProfile;
    }

    /**
     * Builds a reaction from the list of reactants and agents. The built reaction will
     * contain no products.
     *
     * @param reactantNames names of reactants
     * @return built reaction
     */
    private IReaction buildReaction(Collection<String> reactantNames) {
        return this.buildReaction(reactantNames, new ArrayList<>());
    }

    /**
     * Builds a reaction from the list of reactants and agents. The built reaction will
     * contain no products.
     *
     * @param reactantNames names of reactants
     * @param agentNames names of agents
     * @return built reaction
     */
    private IReaction buildReaction(Collection<String> reactantNames, Collection<String> agentNames) {

        Collection<String> reactantSmiles = namesToSmiles(reactantNames);
        Collection<String> agentSmiles = namesToSmiles(agentNames);

        IAtomContainerSet reactants = smilesToMols(reactantSmiles);
        IAtomContainerSet agents = smilesToMols(agentSmiles);

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
    private Collection<String> namesToSmiles(Collection<String> names) {

        return names.stream().map(NTS_INSTANCE::parseToSmiles).collect(Collectors.toList());
    }

    /**
     * Constructs a set of molecules from a list of smiles.
     *
     * @param smiles list of smiles
     * @return set of molecules
     */
    private IAtomContainerSet smilesToMols(Collection<String> smiles) {

        return smiles.stream().collect(
                AtomContainerSet::new,
                (set, s) -> {
                    try {
                        set.addAtomContainer(SP_INSTANCE.parseSmiles(s));
                    } catch (InvalidSmilesException e) {
                        e.printStackTrace();
                    }
                },
                AtomContainerSet::add);
    }

    private String nameToSmile(String name) {
        return NTS_INSTANCE.parseToSmiles(name);
    }

    private IAtomContainer smileToMol(String smile) {
        try {
            return SP_INSTANCE.parseSmiles(smile);
        } catch (InvalidSmilesException e) {
            System.err.println(e.getMessage());
            return null;
        }
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

    /**
     * Checks if the reaction is valid. Reaction is not valid if it has at most one reactant.
     */
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

    private void checkFlag(char c) {

        switch (c) {

            case 'p' : {
                System.out.println(reactionProfile.getReaction().toString());
                break;
            }

            case 's' : {
                if (!checkSimpleSN2(reactionProfile.getReaction())) {
                    System.out.println("Reaction is not a valid single halogen alkane SN2 reaction with alkali halogen salt.");
                }
                break;
            }


        }
    }

}
