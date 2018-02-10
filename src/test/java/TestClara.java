import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

public class TestClara {

    public static void main(String[] args) {





    }

    public static void testSMILES() {



        try {
            SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
            IAtomContainer m = smilesParser.parseSmiles("c1ccccc1");
            Integer c1 = m.getAtom(1).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
            NameToStructure opsinParser = NameToStructure.getInstance();
            String smile = opsinParser.parseToSmiles("trinitrotoluene");
            System.out.println(smile);
            IAtomContainer tnt = smilesParser.parseSmiles(smile);
            for (IAtom atom : tnt.getConnectedAtomsList(tnt.getAtom(1))) {
                System.out.println(atom.getImplicitHydrogenCount());
            }
        } catch (InvalidSmilesException e) {
            System.err.println(e.getMessage());
        }

    }
}
