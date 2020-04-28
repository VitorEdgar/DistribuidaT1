all:    RegistroRecurso.class RegistroCliente.class ClienteInterface.class ServidorInterface.class Ping.class Cliente.class Servidor.class  Main.class

Main.class: Main.java Servidor.class Cliente.class
			@javac Main.java

Servidor.class: Servidor.java
			@javac Servidor.java

ServidorInterface.class: ServidorInterface.java
			@javac ServidorInterface.java

Cliente.class:	Cliente.java ClienteInterface.java
			@javac Cliente.java

ClienteInterface.class: ClienteInterface.java
			@javac ClienteInterface.java

RegistroRecurso.class: RegistroRecurso.java
			@javac RegistroRecurso.java

RegistroCliente.class: RegistroCliente.java
			@javac RegistroCliente.java

Ping.class: Ping.java
			@javac Ping.java
clean:
			@rm -rf *.class *~
