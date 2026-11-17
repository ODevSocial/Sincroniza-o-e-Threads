import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Filosofo extends Thread {
    private final int id;
    private final Lock garfoEsquerdo;
    private final Lock garfoDireito;

    public Filosofo(int id, Lock garfoEsquerdo, Lock garfoDireito) {
        this.id = id;
        this.garfoEsquerdo = garfoEsquerdo;
        this.garfoDireito = garfoDireito;
    }

    private void pensar() throws InterruptedException {
        System.out.println("Filósofo " + id + " está PENSANDO.");
        Thread.sleep((long) (Math.random() * 1000));
    }

    private void comer() throws InterruptedException {
        System.out.println("Filósofo " + id + " está COMENDO.");
        Thread.sleep((long) (Math.random() * 1000));
    }

    @Override
    public void run() {
        try {
            while (true) {
                pensar();

                while (true) {
                    boolean pegouEsquerda = garfoEsquerdo.tryLock();
                    boolean pegouDireita = false;

                    if (pegouEsquerda) {
                        pegouDireita = garfoDireito.tryLock();

                        if (pegouDireita) {
                            break; 
                        } else {
                            garfoEsquerdo.unlock();
                        }
                    }

                    Thread.sleep((long) (Math.random() * 50));
                }

                comer();

                garfoDireito.unlock();
                garfoEsquerdo.unlock();
            }
        } catch (InterruptedException e) {
            System.out.println("Filósofo " + id + " foi interrompido.");
            Thread.currentThread().interrupt();
        }
    }
}

public class JantarDosFilosofosLocks {
    public static void main(String[] args) {
        int NUM_FILOSOFOS = 5;
        Lock[] garfos = new ReentrantLock[NUM_FILOSOFOS];
        Filosofo[] filosofos = new Filosofo[NUM_FILOSOFOS];

        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            garfos[i] = new ReentrantLock();
        }

        for (int i = 0; i < NUM_FILOSOFOS; i++) {
            Lock garfoEsquerdo = garfos[i];
            Lock garfoDireito = garfos[(i + 1) % NUM_FILOSOFOS];

            filosofos[i] = new Filosofo(i, garfoEsquerdo, garfoDireito);
            filosofos[i].start();
        }
    }
}