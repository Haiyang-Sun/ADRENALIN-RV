// Generated from RE.g4 by ANTLR 4.7
package ch.usi.dag.rv.re;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link REParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface REVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link REParser#initial}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitial(REParser.InitialContext ctx);
	/**
	 * Visit a parse tree produced by {@link REParser#multiproc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiproc(REParser.MultiprocContext ctx);
	/**
	 * Visit a parse tree produced by {@link REParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(REParser.ExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link REParser#item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitItem(REParser.ItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link REParser#binary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary(REParser.BinaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link REParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary(REParser.UnaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link REParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(REParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link REParser#binaryop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryop(REParser.BinaryopContext ctx);
	/**
	 * Visit a parse tree produced by {@link REParser#unaryop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryop(REParser.UnaryopContext ctx);
}