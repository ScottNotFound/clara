import net.scottnotfound.clara.lang.Lang;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

import java.io.IOException;

public class TestClara {

    public static void main(String[] args) throws IOException {

        runBasicScriptTests();

    }

    private static void runBasicScriptTests() throws IOException {

        // iterative test
        Lang.main(new String[]{"resources/scripts/basic/iterative/sum100"});

        // recursive test
        Lang.main(new String[]{"resources/scripts/basic/recursive/sum100"});

    }

    public static void testSMILES() {



        try {
            SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
            IAtomContainer m = smilesParser.parseSmiles("c1ccccc1");
            Integer c1 = m.getAtom(1).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
            NameToStructure opsinParser = NameToStructure.getInstance();
            String smile = opsinParser.parseToSmiles("water");
            System.out.println(smile);
            IAtomContainer tnt = smilesParser.parseSmiles(smile);
        } catch (InvalidSmilesException e) {
            System.err.println(e.getMessage());
        }

    }
}
