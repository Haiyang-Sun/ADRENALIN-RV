// Generated from RE.g4 by ANTLR 4.7
package ch.usi.dag.rv.re;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RELexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, ID=7, MULTISWITCH=8, DIGITS=9, 
		WS=10;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "ID", "CHARACTER", "MULTISWITCH", 
		"DIGITS", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "'|'", "'*'", "'+'", "'?'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, "ID", "MULTISWITCH", "DIGITS", 
		"WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public RELexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "RE.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\fB\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\6\b\'"+
		"\n\b\r\b\16\b(\3\b\3\b\6\b-\n\b\r\b\16\b.\5\b\61\n\b\3\t\3\t\3\n\3\n\3"+
		"\13\6\138\n\13\r\13\16\139\3\f\6\f=\n\f\r\f\16\f>\3\f\3\f\2\2\r\3\3\5"+
		"\4\7\5\t\6\13\7\r\b\17\t\21\2\23\n\25\13\27\f\3\2\6\7\2\60\60\62;C\\a"+
		"ac|\3\2%&\3\2\62;\5\2\13\f\17\17\"\"\2E\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
		"\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\3\31\3\2\2\2\5\33\3\2\2\2\7\35\3\2\2\2\t\37"+
		"\3\2\2\2\13!\3\2\2\2\r#\3\2\2\2\17\60\3\2\2\2\21\62\3\2\2\2\23\64\3\2"+
		"\2\2\25\67\3\2\2\2\27<\3\2\2\2\31\32\7*\2\2\32\4\3\2\2\2\33\34\7+\2\2"+
		"\34\6\3\2\2\2\35\36\7~\2\2\36\b\3\2\2\2\37 \7,\2\2 \n\3\2\2\2!\"\7-\2"+
		"\2\"\f\3\2\2\2#$\7A\2\2$\16\3\2\2\2%\'\5\21\t\2&%\3\2\2\2\'(\3\2\2\2("+
		"&\3\2\2\2()\3\2\2\2)\61\3\2\2\2*,\7A\2\2+-\5\21\t\2,+\3\2\2\2-.\3\2\2"+
		"\2.,\3\2\2\2./\3\2\2\2/\61\3\2\2\2\60&\3\2\2\2\60*\3\2\2\2\61\20\3\2\2"+
		"\2\62\63\t\2\2\2\63\22\3\2\2\2\64\65\t\3\2\2\65\24\3\2\2\2\668\t\4\2\2"+
		"\67\66\3\2\2\289\3\2\2\29\67\3\2\2\29:\3\2\2\2:\26\3\2\2\2;=\t\5\2\2<"+
		";\3\2\2\2=>\3\2\2\2><\3\2\2\2>?\3\2\2\2?@\3\2\2\2@A\b\f\2\2A\30\3\2\2"+
		"\2\b\2(.\609>\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}