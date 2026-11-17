package leitores_e_escritores;
import java.util.concurrent.Semaphore;


public class LeitoresEscritoresSemaforo {

    private static int leitoresAtivos = 0;

    private static final Semaphore mutex = new Semaphore(1);

    private static final Semaphore recursoCompartilhado = new Semaphore(1);

    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            new Thread(new Leitor(i)).start();
        }

        for (int i = 1; i <= 2; i++) {
            new Thread(new Escritor(i)).start();
        }
    }

    static class Leitor implements Runnable {
        private int id;

        public Leitor(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    mutex.acquire();
                    leitoresAtivos++;
                    
                    if (leitoresAtivos == 1) {
                        recursoCompartilhado.acquire();
                    }
                    mutex.release();

                    System.out.println("Leitor " + id + " está LENDO. (Leitores ativos: " + leitoresAtivos + ")");
                    Thread.sleep((long) (Math.random() * 2000)); // Tempo de leitura
                    System.out.println("Leitor " + id + " terminou de ler.");

                    mutex.acquire();
                    leitoresAtivos--;
                    
                    if (leitoresAtivos == 0) {
                        recursoCompartilhado.release();
                    }
                    mutex.release();

                    Thread.sleep((long) (Math.random() * 3000));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Escritor implements Runnable {
        private int id;

        public Escritor(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("Escritor " + id + " quer escrever e está aguardando.");
                    
                    recursoCompartilhado.acquire();

                    System.out.println("Escritor " + id + " está ESCREVENDO... -> [DADOS ATUALIZADOS]");
                    Thread.sleep((long) (Math.random() * 3000));
                    System.out.println("Escritor " + id + " terminou de escrever.");

                    recursoCompartilhado.release();

                    Thread.sleep((long) (Math.random() * 5000));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}