all:    RegistroRecurso.class ClienteInterface.class ServidorInterface.class Cliente.class Servidor.class  Main.class

Main.class: Main.java Servidor.class Cliente.class
			@javac Main.java

Servidor.class: Servidor.java
			@javac Servidor.java

ServidorInterface.class: ServidorInterface.java
			@javac ServidorInterface.java

Cliente.class:	Cliente.java
			@javac Cliente.java

ClienteInterface.class: ClienteInterface.java
			@javac ClienteInterface.java

RegistroRecurso.class: RegistroRecurso.java
			@javac RegistroRecurso.java

clean:
			@rm -rf *.class *~
