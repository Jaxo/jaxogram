/*
dump() displays the contents of a variable like var_dump() does in PHP.
Parameters:
  v:              The variable
  indent:         A string of spaces for indentation
Return Value:
  The stringized variable
*/
function dump(v, howDisplay, indent, depth) {
   depth = (typeof depth !== 'number')? 0 : depth;
   indent = (typeof indent !== "string")? "   " : indent;
   var vType = typeof v;
   var out = "(" + vType;
   switch (vType) {
   case "number":
   case "boolean":
      out += ") " + v;
      break;
   case "string":
      out += "," + v.length + ') "' + v + '"';
      break;
   case "object":
      if (v === null) {
         out = "null";
      }else if (Object.prototype.toString.call(v) === '[object Array]') {
         out = 'array[' + v.length + '] {\n';
         ++depth;
         for (var i=0; i < v.length; ++i) {
            out += (
               repeatString(indent, depth) + "[" + i + "]: " +
               dump(v[i], "none", indent, depth) + "\n"
            );
         }
         --depth;
         out += repeatString(indent, depth) + "}";
      }else { // object
         sContents = "{\n";
         cnt = 0;
         ++depth;
         for (var member in v) {
            // No way to know the original data type of member, since JS
            // always converts it to a string and no other way to parse objects.
            sContents += (
               repeatString(indent, depth) +
               member + ": " +
               dump(v[member], "none", indent, depth) + "\n"
            );
            ++cnt;
         }
         --depth;
         sContents += repeatString(indent, depth) + "}";
         out += "," + cnt + ") " + sContents;
      }
      break;
   }
   return out;
}

/* repeatString() returns a string which has been repeated a set number of times */
function repeatString(str, num) {
   out = '';
   for (var i=0; i < num; ++i) out += str;
   return out;
}

