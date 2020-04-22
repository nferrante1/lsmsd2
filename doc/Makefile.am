LATEXMKOPTS = -pdf -cd -use-make -pdflatex="pdflatex -interaction=nonstopmode %O %S"

EXTRA_DIST = $(srcdir)/*.tex $(srcdir)/*.sty $(srcdir)/**/*.tex \
	$(srcdir)/**/**/*.tex $(srcdir)/**/*.eps $(srcdir)/**/*.pdf \
	$(srcdir)/listings/

CLEANFILES = chapters/*.aux report.bbl report.run.xml **/*-converted-to.*

doc_DATA = design.pdf documentation.pdf

.PHONY: FORCE_MAKE

design.pdf: $(srcdir)/design.tex FORCE_MAKE
	$(LATEXMK) $(LATEXMKOPTS) $<

documentation.pdf: $(srcdir)/documentation.tex FORCE_MAKE
	$(LATEXMK) $(LATEXMKOPTS) $<

clean-local:
	$(LATEXMK) -C
