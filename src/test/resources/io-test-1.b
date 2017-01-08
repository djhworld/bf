This is for testing i/o; give it a return followed by an EOF
It should give two lines of output; the two lines should be identical and
should be lined up one over the other If that doesn't happen ten (LF) is not
coming through as newline on output

The content of the lines tells how input is being processed; each line
should be two uppercase letters
Anything with O in it means newline is not coming through as ten on input
LK means newline input is working fine and EOF leaves the cell unchanged
(which I recommend)
LB means newline input is working fine and EOF translates as 0
LA means newline input is working fine and EOF translates as minus 1
Anything else is fairly unexpected

>,>+++++++++,>+++++++++++[<++++++<++++++<+>>>-]<<.>.<<-.>.>.<<.


Daniel B Cristofani (cristofdathevanetdotcom)
http://www(dot)hevanet(dot)com/cristofd/brainfuck/