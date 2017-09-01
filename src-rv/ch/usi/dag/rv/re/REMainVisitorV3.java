package ch.usi.dag.rv.re;

import java.util.HashMap;
import java.util.Map;

import ch.usi.dag.rv.nfa.NFA;

/**
 * Created by alexandernorth on 05.05.17.
 */
public class REMainVisitorV3 extends REBaseVisitor<Map<String, NFA>> {
    @Override
    public Map<String, NFA> visitInitial(REParser.InitialContext ctx) {
        Map<String, NFA> map = new HashMap<>();
        REParser.MultiprocContext parentCtx = (REParser.MultiprocContext) ctx.getParent();
        for (REParser.MultiprocContext multiprocContext : ctx.multiproc()) {

            multiprocContext.processName = ctx.processName;
            Map<String, NFA> initialResult = visitMultiproc(multiprocContext);

            NFA existingNFA = map.get(multiprocContext.processName);
            NFA resultNFA = initialResult.get(multiprocContext.processName);

            NFA concatNFA = null;

            if (existingNFA != null){
                existingNFA.or(resultNFA);

                concatNFA = map.get(ctx.processName);

                if (concatNFA != null) {
//                    This is saved into the map later
                    concatNFA.concatenate(NFA.recognizesBinderEvent(multiprocContext.processName, multiprocContext.binderType).concatenate(resultNFA));
                }

//                concatNFA = map.computeIfPresent(ctx.processName, (k, v) ->
//                        v.concatenate(NFA.recognizesBinderEvent(multiprocContext.processName, multiprocContext.binderType).concatenate(resultNFA))
//                );

            }else{
                existingNFA = resultNFA.copy();
            }

            map.putAll(initialResult);
            map.put(multiprocContext.processName, existingNFA);
            if (concatNFA != null)
                map.put(ctx.processName, concatNFA);

        }

        return map;
    }

    @Override
    public Map<String, NFA> visitMultiproc(REParser.MultiprocContext ctx) {
        REParser.ExpContext expContext = ctx.exp();
        if (expContext != null) {
            expContext.processName = ctx.processName;
            return visitExp(expContext);
        }else{
            String newProcName = ctx.ID().getText();
            REParser.InitialContext initialContext = ctx.initial();
            initialContext.processName = newProcName;

            Map<String, NFA> initialCtxMap = visitInitial(initialContext);

            NFA processNFA = initialCtxMap.get(newProcName).copy();

            if (ctx.MULTISWITCH().getText().equalsIgnoreCase("$")){
                processNFA = NFA.recognizesBinderEvent(newProcName, 2).concatenate(processNFA);
                ctx.binderType = 2;
            }else{
                processNFA = NFA.recognizesBinderEvent(newProcName, 5).concatenate(NFA.recognizesBinderEvent(newProcName, 2)).concatenate(processNFA);
                processNFA = processNFA.concatenate(NFA.recognizesBinderEvent(newProcName, 3));
                ctx.binderType = 5;
            }


            NFA thisProcNFA = initialCtxMap.get(ctx.processName);

            if (thisProcNFA != null){
//                TODO: Should this be CONCAT or OR - this is if the current process name appears in the subtree of the parse tree
//                #s_s.(#phone.(#s_s.(CP)))
                thisProcNFA.or(processNFA);
            }else{
                thisProcNFA = processNFA.copy();
            }


            initialCtxMap.put(ctx.processName, thisProcNFA);

            ctx.processName = newProcName;

            return initialCtxMap;
        }
    }

    @Override
    public Map<String, NFA> visitExp(REParser.ExpContext ctx) {
        NFA nfa = new NFA();
        for (REParser.ItemContext iCtx : ctx.item()) {
            iCtx.processName = ctx.processName;
            NFA temp = visitItem(iCtx).get(ctx.processName);
            nfa.concatenate(temp);

        }
        Map<String, NFA> expNFA = new HashMap<>();
        expNFA.put(ctx.processName, nfa);
        return expNFA;
    }

    @Override
    public Map<String, NFA> visitItem(REParser.ItemContext ctx) {
        REParser.UnaryContext unaryContext = ctx.unary();
        unaryContext.processName = ctx.processName;
        Map<String, NFA> unary = visitUnary(unaryContext);

        for (REParser.BinaryContext bCtx : ctx.binary()) {
            bCtx.processName = ctx.processName;
            if (bCtx.binaryop().getText().equalsIgnoreCase("|")){
                unary.get(ctx.processName).or(visitUnary(bCtx.unary()).get(ctx.processName));
            }
        }
        return unary;
    }

    @Override
    public Map<String, NFA> visitBinary(REParser.BinaryContext ctx) {
        REParser.UnaryContext unaryContext = ctx.unary();
        unaryContext.processName = ctx.processName;
        return visitUnary(unaryContext);
    }

    @Override
    public Map<String, NFA> visitUnary(REParser.UnaryContext ctx) {
        REParser.PrimaryContext primaryContext = ctx.primary();
        primaryContext.processName = ctx.processName;
        Map<String, NFA> primary = visitPrimary(primaryContext);

        REParser.UnaryopContext uOpCtx = ctx.unaryop();

        if (uOpCtx != null){
            if (uOpCtx.getText().equalsIgnoreCase("*")){
                primary.get(ctx.processName).star();
            } else if (uOpCtx.getText().equalsIgnoreCase("?")){
                primary.get(ctx.processName).questionMark();
            } else if (uOpCtx.getText().equalsIgnoreCase("+")){
                primary.get(ctx.processName).plus();
            }
        }
        return primary;
    }

    @Override
    public Map<String, NFA> visitPrimary(REParser.PrimaryContext ctx) {
        REParser.ExpContext expContext = ctx.exp();

        if (expContext != null) {
            expContext.processName = ctx.processName;
            return visitExp(expContext);
        } else {
            Map<String, NFA> map = new HashMap<>();
            map.put(ctx.processName, NFA.recognizesEventName(ctx.ID().getText(), ctx.processName));
            return map;
        }

    }
}
