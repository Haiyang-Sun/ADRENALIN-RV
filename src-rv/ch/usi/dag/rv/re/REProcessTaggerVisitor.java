package ch.usi.dag.rv.re;

/**
 * Created by alexandernorth on 31.03.17.
 */
public class REProcessTaggerVisitor extends REBaseVisitor<Void> {
    @Override
    public Void visitInitial(REParser.InitialContext ctx) {
        for (REParser.MultiprocContext multiprocCtx : ctx.multiproc()) {
            multiprocCtx.processName = ctx.processName;
            visitMultiproc(multiprocCtx);
        }
        return null;
    }

    @Override
    public Void visitMultiproc(REParser.MultiprocContext ctx) {
        REParser.ExpContext expContext = ctx.exp();
        if (expContext != null){
            expContext.processName = ctx.processName;
            visitExp(expContext);
        }else{
            REParser.InitialContext initialContext = ctx.initial();
            initialContext.processName = ctx.ID().getText();
            visitInitial(initialContext);
        }
        return null;
    }

    @Override
    public Void visitExp(REParser.ExpContext ctx) {
        for (REParser.ItemContext itemContext : ctx.item()) {
            itemContext.processName = ctx.processName;
            visitItem(itemContext);
        }
        return null;
    }

    @Override
    public Void visitItem(REParser.ItemContext ctx) {
        REParser.UnaryContext unaryContext = ctx.unary();
        unaryContext.processName = ctx.processName;
        visitUnary(unaryContext);

        for (REParser.BinaryContext bContext : ctx.binary()) {
            bContext.processName = ctx.processName;
            visitBinary(bContext);
        }
        return null;
    }

    @Override
    public Void visitBinary(REParser.BinaryContext ctx) {
        REParser.UnaryContext unaryContext = ctx.unary();
        unaryContext.processName = ctx.processName;
        visitUnary(unaryContext);
        return null;
    }

    @Override
    public Void visitUnary(REParser.UnaryContext ctx) {
        REParser.PrimaryContext primaryContext = ctx.primary();
        primaryContext.processName = ctx.processName;
        visitPrimary(primaryContext);
        return null;
    }

    @Override
    public Void visitPrimary(REParser.PrimaryContext ctx) {
        REParser.ExpContext expContext = ctx.exp();
        if (expContext != null){
            expContext.processName = ctx.processName;
            visitExp(expContext);
        }
        return null;
    }
}
