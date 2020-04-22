AC_DEFUN([AC_LATEX_PACKAGE_REQUIRE],[
AC_LATEX_PACKAGE([$1],[report],[$1],[],[AC_MSG_ERROR([Unable to find $1 package])])
])
