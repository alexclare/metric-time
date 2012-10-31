# Generic ClojureScript makefile

target = metric-time
srcdir = src
prefix = out

CLJSC = $(CLOJURESCRIPT_HOME)/bin/cljsc

dev:	OPTS = :optimizations :simple :pretty-print true
dev:	compile

release:	OPTS = :optimizations :advanced
release:	compile

compile:	$(prefix)/$(target).js

$(prefix)/$(target).js: $(srcdir)/*.cljs
	$(CLJSC) $(srcdir) '{$(OPTS) :output-dir "$(@D)" :output-to "out/$(target).js"}'

clean:
	rm -r $(prefix)/*
