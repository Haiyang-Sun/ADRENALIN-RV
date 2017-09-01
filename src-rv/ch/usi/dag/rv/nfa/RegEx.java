package ch.usi.dag.rv.nfa;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import ch.usi.dag.rv.re.RELexer;
import ch.usi.dag.rv.re.REMainVisitorV3;
import ch.usi.dag.rv.re.REParser;

/**
 * Created by alexandernorth on 27.03.17.
 */
public class RegEx{
	String regExp;
	Map<String, NFA> processName2NFA;
	boolean involveMultipleProcess;
	private RegEx(String regExp){
		this.regExp = regExp;
	    RELexer lexer = new RELexer(CharStreams.fromString(regExp));
	    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
	    REParser parser = new REParser(tokenStream);
	    REMainVisitorV3 mainVisitorV3 = new REMainVisitorV3();
	    processName2NFA = mainVisitorV3.visit(parser.initial());
	    involveMultipleProcess = processName2NFA.size() > 1;
	    //prepending the starting binder event of type 2
	    //appending the ending binder event of type 3
	    for(Entry<String, NFA> entry : processName2NFA.entrySet()) {
	    	String key = entry.getKey();
	        if (!key.equalsIgnoreCase("?")){
	            NFA theNFA = processName2NFA.get(key);
	            NFA updated = NFA.recognizesBinderEvent(key, 2);
	            updated.concatenate(theNFA);
	            updated.concatenate(NFA.recognizesBinderEvent(key, 3));
	            processName2NFA.put(key, updated);
	        }
	    }
	    
	    cacheReg2NFA.put(regExp, this);
	}
	
	public static RegEx getRegEx(String exp){
		if(!cacheReg2NFA.containsKey(exp))
			cacheReg2NFA.put(exp, new RegEx(exp));
		return cacheReg2NFA.get(exp);
	}
	
	//regular expression => (processId => NFAs)
    private static Map<String, RegEx> cacheReg2NFA = new HashMap<>();

    //? => the outmost NFA only
    //others => the outmost + entry
    public ArrayList<NFA> getNFA(String processName) {
    	ArrayList<NFA> res = new ArrayList<NFA>();
    	NFA outmost = processName2NFA.get("?");
    	outmost.setOutMost(true);
    	res.add(outmost);
    	
    	if(involveMultipleProcess && !processName.equals("?")){
	        NFA retNFA = processName2NFA.get(processName);
	        if (retNFA != null) {
	            res.add(retNFA);
	        }
	        retNFA = processName2NFA.get("_");
	        if (retNFA != null) {
	            res.add(retNFA);
	        }
    	}
		return res;
    }
}


