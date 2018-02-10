import net.scottnotfound.clara.interpret.Expression;
import net.scottnotfound.clara.interpret.Lexer;
import net.scottnotfound.clara.interpret.Parser;
import net.scottnotfound.clara.interpret.PrintAST;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

public class TestClara {

    public static void main(String[] args) {


        Lexer lexer = new Lexer("4 + 4");
        lexer.lex();
        Parser parser = new Parser(lexer.lex());
        Expression expression = parser.parse();
        System.out.println(new PrintAST().print(expression));

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
