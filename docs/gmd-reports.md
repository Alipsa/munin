# GMD reports
GMD or Groovy Markdown is essentially markdown with possibilities to add groovy code to dynamically generate
content on the fly. It is quite similar to the MDR format but the code is Groovy instead of R.
See [The gmd project](https://github.com/perNyfelt/gmd) for more information on syntax etc. but basically

1. code blocks are defined using \`\`\`{groovy} or \`\`\`{groovy echo=false}.
  - Inside code blocks, you can use out.println to output markdown. For convenience Matrix tables can be printed directly i.e. `out.println(myMatrix)`
2. Variables can be inlined using \`=varName\`