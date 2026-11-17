import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JantarDosFilosofosMonitor {

    enum Estado {
        PENSANDO, FAMINTO, COMENDO
    }

    static class MonitorMesa {
        private final int NUM_FILOSOFOS = 5;
        private Estado[] estado = new Estado[NUM_FILOSOFOS];
        
        private Lock lock = new ReentrantLock();
        private Condition[] condicoes = new Condition[NUM_FILOSOFOS];

        public MonitorMesa() {
            for (int i = 0; i < NUM_FILOSOFOS; i++) {
                estado[i] = Estado.PENSANDO;
                condicoes[i] = lock.newCondition();
            }
        }

        private int esquerda(int i) { return (i + 4) % NUM_FILOSOFOS; }
        private int direita(int i) { return (i + 1) % NUM_FILOSOFOS; }

        private void testar(int i) {
            if (estado[i] == Estado.FAMINTO &&
                estado[esquerda(i)] != Estado.COMENDO &&
                estado[direita(i)] != Estado.COMENDO) {
                
                estado[i] = Estado.COMENDO;
                condicoes[i].signal();
            }
        }

        public void pegarGarfos(int i) throws InterruptedException {
            lock.lock();
            try {
                estado[i] = Estado.FAMINTO;
                testar(i);
                
                while (estado[i] != Estado.COMENDO) {
                    condicoes[i].await(); 
                }
            } finally {
                lock.unlock();
            }
        }

        public void largarGarfos(int i) {
            lock.lock();
            try {
                estado[i] = Estado.PENSANDO;
                testar(esquerda(i));
                testar(direita(i));
            } finally {
                lock.unlock();
            }
        }
    }

    static class Filosofo extends Thread {
        private int id;
        private MonitorMesa monitor;

        public Filosofo(int id, MonitorMesa monitor) {
            this.id = id;
            this.monitor = monitor;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("Filósofo " + id + " está PENSANDO.");
                    Thread.sleep((long) (Math.random() * 1000));

                    System.out.println("Filósofo " + id + " está com fome...");
                    monitor.pegarGarfos(id);

                    System.out.println("Filósofo " + id + " está COMENDO.");
                    Thread.sleep((long) (Math.random() * 1000));

                    monitor.largarGarfos(id);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        MonitorMesa monitor = new MonitorMesa();
        Filosofo[] filosofos = new Filosofo[5];

        for (int i = 0; i < 5; i++) {
            filosofos[i] = new Filosofo(i, monitor);
            filosofos[i].start();
        }
    }
}