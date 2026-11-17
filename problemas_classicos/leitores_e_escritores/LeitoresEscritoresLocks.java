package leitores_e_escritores;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LeitoresEscritoresLocks {

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
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public void ler(int id) {
        lock.readLock().lock();
        try {
            System.out.println("Leitor " + id + " está LENDO.");
            Thread.sleep((long) (Math.random() * 2000));
            System.out.println("Leitor " + id + " terminou de ler.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void escrever(int id) {
        lock.writeLock().lock();
        try {
            System.out.println("Escritor " + id + " está ESCREVENDO... -> [DADOS ATUALIZADOS]");
            Thread.sleep((long) (Math.random() * 3000));
            System.out.println("Escritor " + id + " terminou de escrever.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
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
        while (true) {
            banco.ler(id);
            try { Thread.sleep((long) (Math.random() * 3000)); } catch (InterruptedException e) {}
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
        while (true) {
            banco.escrever(id);
            try { Thread.sleep((long) (Math.random() * 5000)); } catch (InterruptedException e) {}
        }
    }
}