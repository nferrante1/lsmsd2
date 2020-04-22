#
# AC_PROG_LATEXMK
#
# Test for latexmk
# and set $latexmk to the correct value
#
dnl @synopsis AC_PROG_LATEXMK
dnl
dnl This macro test if latexmk is installed. If latexmk
dnl is installed, it set $LATEXMK to the right value
dnl
dnl @version 1.0
dnl @author Niccolò Scatena speedjack95@gmail.com
dnl
AC_DEFUN([AC_PROG_LATEXMK],[
AC_CHECK_PROGS(LATEXMK,[latexmk],no)
export LATEXMK;
if test $LATEXMK = "no" ;
then
	AC_MSG_ERROR([Unable to find LaTeXmk application]);
fi
AC_SUBST(LATEXMK)
])
