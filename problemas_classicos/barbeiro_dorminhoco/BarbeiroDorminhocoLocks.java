package barbeiro_dorminhoco;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BarbeiroDorminhocoLocks {

    public static void main(String[] args) {
        Barbearia barbearia = new Barbearia(3);

        Thread barbeiro = new Thread(new Barbeiro(barbearia));
        barbeiro.start();

        for (int i = 1; i <= 10; i++) {
            Thread cliente = new Thread(new Cliente(barbearia, i));
            cliente.start();
            try {
                Thread.sleep((long) (Math.random() * 2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Barbearia {
    private final int totalCadeiras;
    private int clientesEsperando = 0;

    private final Lock lock = new ReentrantLock();
    
    private final Condition condBarbeiroDormindo = lock.newCondition();
    private final Condition condClienteEsperandoVez = lock.newCondition();
    private final Condition condAguardandoSentar = lock.newCondition();

    private boolean barbeiroPronto = false;
    private boolean clienteSentou = false;

    public Barbearia(int totalCadeiras) {
        this.totalCadeiras = totalCadeiras;
    }

    public boolean entrar(int idCliente) throws InterruptedException {
        lock.lock(); 
        try {
            if (clientesEsperando == totalCadeiras) {
                System.out.println("Cliente " + idCliente + ": Barbearia lotada. Foi embora.");
                return false;
            }

            clientesEsperando++;
            System.out.println("Cliente " + idCliente + ": Sentou na sala de espera. (Espera: " + clientesEsperando + ")");
            
            condBarbeiroDormindo.signal();

            while (!barbeiroPronto) {
                condClienteEsperandoVez.await(); // O cliente dorme nesta condição específica
            }

            clientesEsperando--;
            barbeiroPronto = false;
            clienteSentou = true;
            System.out.println("Cliente " + idCliente + ": Sentou na cadeira do barbeiro para o corte.");
            
            condAguardandoSentar.signal();

            return true;

        } finally {
            lock.unlock(); 
        }
    }

    public void chamarProximo() throws InterruptedException {
        lock.lock();
        try {
            while (clientesEsperando == 0) {
                System.out.println("Barbeiro: Zzzzz... (Dormindo na cadeira)");
                condBarbeiroDormindo.await();
            }

            barbeiroPronto = true;
            
            condClienteEsperandoVez.signal();

            while (!clienteSentou) {
                condAguardandoSentar.await();
            }

            clienteSentou = false;

        } finally {
            lock.unlock();
        }
    }
}

class Barbeiro implements Runnable {
    private final Barbearia barbearia;

    public Barbeiro(Barbearia barbearia) {
        this.barbearia = barbearia;
    }

    @Override
    public void run() {
        while (true) {
            try {
                barbearia.chamarProximo();
                
                System.out.println("Barbeiro: Cortando o cabelo...");
                Thread.sleep(3000); 
                System.out.println("Barbeiro: Terminou o corte!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Cliente implements Runnable {
    private final Barbearia barbearia;
    private final int id;

    public Cliente(Barbearia barbearia, int id) {
        this.barbearia = barbearia;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            System.out.println("Cliente " + id + ": Chegou na porta.");
            barbearia.entrar(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}