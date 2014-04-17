/*
 *	MySQL Mode for CodeMirror 2 by MySQL-Tools
 *	@author James Thorne (partydroid)
 *	@link 	http://github.com/partydroid/MySQL-Tools
 * 	@link 	http://mysqltools.org
 *	@version 02/Jan/2012
 */
CodeMirror.defineMode("manchestersyntax", function (config) {

    var indentUnit = config.indentUnit;

    var curPunc;

    function wordRegexp(words) {
       return new RegExp("^(?:" + words.join("|") + ")$", "i");
    }

    var quantifierKeywords = wordRegexp([
        ('some'), ('only'), ('min'), ('max'), ('exactly'), ('value')
    ]);

    var booleanConnectiveKeywords = wordRegexp([
            ('or'), ('and'), ('not')
    ]);
    
    //added by @gs
    var prefix = wordRegexp([
       ('dcterms'), ('dc'), ('owl'), ('rdf'), ('rdfs'), ('lom') //, ('dspace'), ('-ont')
    ]);
    
    //added by @gs
   // var dspace = "null";   // successful match http://www.regular-expressions.info/javascriptexample.html
   // var ont = "null";
    var prevtoken = "null";
    var dspacetoken = /^(dspace)$/i; 
    var onttoken = /^(-ont)$/i;
    
    //added by @gs
    var number = new RegExp("^([0-9])*$", "i");

   // var operatorChars = /[*+\-<>=&|]/;

    function tokenBase(stream, state) {
        var ch = stream.next();
        curPunc = null;
        stream.eatWhile(/[_\w\d]/);
        
        var word = stream.current(), type;
      
        if (quantifierKeywords.test(word)) {
            //return "quantifier"; //gs: alternatively use "keyword"
            prevtoken = "restriction";
            return "ms-restriction";
        }
        else if(booleanConnectiveKeywords.test(word)) {
           // return "boolean-connective"  //gs: alternatively use "def"
             prevtoken = "boolean";
             return "ms-boolean"
        }
        //added by @gs for dspace specific entities
        else if(prefix.test(word)) {       //gs: alternatively use "builtin"
            prevtoken = "prefix";
            return "ms-keyword"
        }
        //added by @gs for dspace specific entities
        else if(dspacetoken.test(word))  {
              prevtoken = "dspace";
              return "ms-keyword" 
        }
        else if(onttoken.test(word))  {
               if (prevtoken=="dspace"){
                  prevtoken="null";
                  return "ms-keyword"
               }
              return "operator"
         }                     
        else if(number.test(word)) {
            prevtoken = "number";
            return "number"
        }
        else
            prevtoken = "null";
            return "operator"; //black
               
    }
    

//    function tokenLiteral(quote) {
//        return function (stream, state) {
//            var escaped = false, ch;
//            while ((ch = stream.next()) != null) {
//                if (ch == quote && !escaped) {
//                    state.tokenize = tokenBase;
//                    break;
//                }
//                escaped = !escaped && ch == "\\";
//            }
//            return "string";
//        };
//    }
//
//    function tokenOpLiteral(quote) {
//        return function (stream, state) {
//            var escaped = false, ch;
//            while ((ch = stream.next()) != null) {
//                if (ch == quote && !escaped) {
//                    state.tokenize = tokenBase;
//                    break;
//                }
//                escaped = !escaped && ch == "\\";
//            }
//            return "variable-2";
//        };
//    }


    function pushContext(state, type, col) {
        state.context = {prev:state.context, indent:state.indent, col:col, type:type};
    }

    function popContext(state) {
        state.indent = state.context.indent;
        state.context = state.context.prev;
    }

    return {
        startState:function (base) {
            return {tokenize:tokenBase,
                context:null,
                indent:0,
                col:0};
        },

        token:function (stream, state) {
            if (stream.sol()) {
                if (state.context && state.context.align == null) state.context.align = false;
                state.indent = stream.indentation();
            }
            if (stream.eatSpace()) return null;
            var style = state.tokenize(stream, state);

            if (style != "comment" && state.context && state.context.align == null && state.context.type != "pattern") {
                state.context.align = true;
            }

            if (curPunc == "(") pushContext(state, ")", stream.column()); else if (curPunc == "[") pushContext(state, "]", stream.column()); else if (curPunc == "{") pushContext(state, "}", stream.column()); else if (/[\]\}\)]/.test(curPunc)) {
                while (state.context && state.context.type == "pattern") popContext(state);
                if (state.context && curPunc == state.context.type) popContext(state);
            } else if (curPunc == "." && state.context && state.context.type == "pattern") popContext(state); else if (/atom|string|variable/.test(style) && state.context) {
                if (/[\}\]]/.test(state.context.type))
                    pushContext(state, "pattern", stream.column()); else if (state.context.type == "pattern" && !state.context.align) {
                    state.context.align = true;
                    state.context.col = stream.column();
                }
            }

            return style;
        },

        indent:function (state, textAfter) {
            var firstChar = textAfter && textAfter.charAt(0);
            var context = state.context;
            if (/[\]\}]/.test(firstChar))
                while (context && context.type == "pattern") context = context.prev;

            var closing = context && firstChar == context.type;
            if (!context)
                return 0; else if (context.type == "pattern")
                return context.col; else if (context.align)
                return context.col + (closing ? 0 : 1);
            else
                return context.indent + (closing ? 0 : indentUnit);
        }
    };
});

//CodeMirror.defineMIME("text/x-mysql", "mysql");
