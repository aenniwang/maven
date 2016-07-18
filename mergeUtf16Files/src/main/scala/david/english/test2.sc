import java.io.File
import io.Source._

val ff="C:\\Working-Intel\\working\\maven\\mergeUtf16Files\\EnglishWords_0.txt"
var c=fromFile(ff,"utf-16").getLines().toList
println(c(0))
println(c(1))



