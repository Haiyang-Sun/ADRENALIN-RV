grammar RE;

initial: multiproc +;

multiproc : exp | MULTISWITCH ID '('initial')';

exp : item +;

item : unary (binary)*;

binary : binaryop unary;

unary : primary (unaryop)?;

primary : '(' initial ')' | ID;

binaryop : '|';

unaryop : '*' | '+' | '?';

ID  : CHARACTER+ | '?'CHARACTER+ ;


fragment CHARACTER : [a-zA-Z0-9_.];
MULTISWITCH : [$#];
DIGITS : [0-9]+;

WS  : (' ' | '\t' | '\r' | '\n')+ -> skip;