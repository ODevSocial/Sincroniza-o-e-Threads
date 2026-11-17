package leitores_e_escritores;

public class LeitoresEscritoresMonitor {

    public static void main(String[] args) {
        BancoDeDados banco = new BancoDeDados();

        for (int i = 1; i <= 5; i++) {
            new Thread(new Leitor(banco, i)).start();
        }

        for (int i = 1; i <= 2; i++) {
            new Thread(new Escritor(banco, i)).start();
        }
    }
}

class BancoDeDados {
    private int leitoresAtivos = 0;
    private boolean escritorAtivo = false;
    
    private int escritoresEsperando = 0; 

    
    public synchronized void iniciarLeitura(int id) throws InterruptedException {
        while (escritorAtivo || escritoresEsperando > 0) {
            wait();
        }
        
        leitoresAtivos++;
        System.out.println("Leitor " + id + " entrou. (Leitores lendo: " + leitoresAtivos + ")");
    }

    public synchronized void finalizarLeitura(int id) {
        leitoresAtivos--;
        System.out.println("Leitor " + id + " saiu.");
        
        if (leitoresAtivos == 0) {
            notifyAll(); 
        }
    }


    public synchronized void iniciarEscrita(int id) throws InterruptedException {
        escritoresEsperando++; // Entrou na fila de espera
        
        while (leitoresAtivos > 0 || escritorAtivo) {
            wait();
        }
        
        escritoresEsperando--;
        escritorAtivo = true;
        System.out.println("Escritor " + id + " entrou e BLOQUEOU o banco para escrita.");
    }

    public synchronized void finalizarEscrita(int id) {
        escritorAtivo = false;
        System.out.println("Escritor " + id + " saiu e LIBEROU o banco.");
        
        notifyAll(); 
    }
}

class Leitor implements Runnable {
    private final BancoDeDados banco;
    private final int id;

    public Leitor(BancoDeDados banco, int id) {
        this.banco = banco;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                banco.iniciarLeitura(id);
                
                Thread.sleep((long) (Math.random() * 2000)); 
                
                banco.finalizarLeitura(id);
                
                Thread.sleep((long) (Math.random() * 3000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Escritor implements Runnable {
    private final BancoDeDados banco;
    private final int id;

    public Escritor(BancoDeDados banco, int id) {
        this.banco = banco;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                banco.iniciarEscrita(id);
                
                Thread.sleep((long) (Math.random() * 3000)); 
                
                banco.finalizarEscrita(id);
                
                Thread.sleep((long) (Math.random() * 5000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}