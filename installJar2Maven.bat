@echo off
call mvn install:install-file -Dfile=lib/antlr-runtime-3.5.jar -DgroupId=com.googlecode -DartifactId=antlr-runtime -Dversion=3.5 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/asm-debug-all-4.1.jar -DgroupId=com.googlecode -DartifactId=asm-debug-all -Dversion=4.1 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/d2j-base-cmd-2.0.jar -DgroupId=com.googlecode -DartifactId=d2j-base-cmd -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/d2j-jasmin-2.0.jar -DgroupId=com.googlecode -DartifactId=d2j-jasmin -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/d2j-smali-2.0.jar -DgroupId=com.googlecode -DartifactId=d2j-smali -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/dex-ir-2.0.jar -DgroupId=com.googlecode -DartifactId=dex-ir -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/dex-reader-2.0.jar -DgroupId=com.googlecode -DartifactId=dex-reader -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/dex-reader-api-2.0.jar -DgroupId=com.googlecode -DartifactId=dex-reader-api -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/dex-tools-2.0.jar -DgroupId=com.googlecode -DartifactId=dex-tools -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/dex-translator-2.0.jar -DgroupId=com.googlecode -DartifactId=dex-translator -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/dex-writer-2.0.jar -DgroupId=com.googlecode -DartifactId=dex-writer -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=lib/dx-1.7.jar -DgroupId=com.googlecode -DartifactId=dx -Dversion=1.7 -Dpackaging=jar
