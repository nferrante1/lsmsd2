#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.69])
AC_INIT([lsmsd2-doc], [1.0.0], [https://github.com/nferrante1/lsmsd2])
AC_CONFIG_SRCDIR([design.tex])
AC_CONFIG_AUX_DIR([build-aux])
AC_CONFIG_MACRO_DIR([m4])

AM_INIT_AUTOMAKE([foreign -Wall -Werror])

# Checks for programs.

AC_PROG_LATEXMK
AC_PROG_PDFLATEX
AC_PROG_BIBTEX
AC_LATEX_CLASS_REPORT
AC_LATEX_PACKAGE_FONTENC
AC_LATEX_PACKAGE_AMSMATH
AC_LATEX_PACKAGES_REQUIRE([babel, inputenc, lmodern, csquotes,
	microtype, geometry, etoolbox, suffix, minibox, pdflscape,
	xcolor, graphicx, titlesec, caption, interval, array,
	makecell, enumitem, listings, nameref, varioref, hyperref,
	biblatex])

# Checks for libraries.

# Checks for header files.

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.

AC_CONFIG_FILES([Makefile])

AC_OUTPUT
