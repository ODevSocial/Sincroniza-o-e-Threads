package barbeiro_dorminhoco;

import java.util.concurrent.Semaphore;

public class BarbeiroDorminhocoSemaforo {

    private static final int CADEIRAS_ESPERA = 3;
    
    private static int clientesEsperando = 0;

    private static final Semaphore mutex = new Semaphore(1);
    
    private static final Semaphore clientes = new Semaphore(0);
    
    private static final Semaphore barbeiro = new Semaphore(0);

    static class Barbeiro implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("Barbeiro: Zzzzz... (Dormindo ou aguardando clientes)");
                    
                    clientes.acquire();
                    
                    mutex.acquire();
                    clientesEsperando--;
                    
                    barbeiro.release();
                    mutex.release();
                    
                    System.out.println("Barbeiro: Cortando o cabelo de um cliente.");
                    Thread.sleep(3000);
                    System.out.println("Barbeiro: Terminou o corte.");
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Cliente implements Runnable {
        private int id;

        public Cliente(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("Cliente " + id + ": Chegou na barbearia.");
                
                mutex.acquire();
                
                if (clientesEsperando < CADEIRAS_ESPERA) {
                    clientesEsperando++;
                    System.out.println("Cliente " + id + ": Sentou na sala de espera. (Clientes esperando: " + clientesEsperando + ")");
                    
                    clientes.release();
                    mutex.release();
                    
                    barbeiro.acquire();
                    System.out.println("Cliente " + id + ": Sentou na cadeira do barbeiro e está cortando o cabelo.");
                } else {
                    mutex.release();
                    System.out.println("Cliente " + id + ": Barbearia lotada. Foi embora.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        public static void main(String[] args) {
        Thread threadBarbeiro = new Thread(new Barbeiro());
        threadBarbeiro.start();

        for (int i = 1; i <= 10; i++) {
            Thread threadCliente = new Thread(new Cliente(i));
            threadCliente.start();
            try {
                // Intervalo aleatório entre a chegada de cada cliente
                Thread.sleep((long) (Math.random() * 2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }

    }
}