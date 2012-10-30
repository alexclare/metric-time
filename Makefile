# Generic single-file ClojureScript makefile

target = metric-time
srcdir = ./src
prefix = ./out

CLJSC = $(CLOJURESCRIPT_HOME)/bin/cljsc

all:	$(prefix)/$(target).js

./out/$(target).js:	$(srcdir)/$(target).cljs
	$(CLJSC) $(srcdir)/$(target).cljs > $(prefix)/$(target).js

clean:
	rm -r $(prefix)/*
