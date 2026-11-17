package barbeiro_dorminhoco;

class Barbearia {
    private final int totalCadeiras;
    private int clientesEsperando = 0;
    
    private boolean barbeiroPronto = false;
    private boolean clientePronto = false;

    public Barbearia(int totalCadeiras) {
        this.totalCadeiras = totalCadeiras;
    }

    public synchronized boolean entrar(int idCliente) throws InterruptedException {
        if (clientesEsperando == totalCadeiras) {
            System.out.println("Cliente " + idCliente + ": Barbearia lotada. Foi embora.");
            return false; 
        }

        clientesEsperando++;
        System.out.println("Cliente " + idCliente + ": Sentou na sala de espera. (Espera: " + clientesEsperando + ")");
        
        notifyAll();

        while (!barbeiroPronto) {
            wait();
        }

        clientesEsperando--;
        barbeiroPronto = false;
        clientePronto = true;
        
        System.out.println("Cliente " + idCliente + ": Sentou na cadeira do barbeiro para o corte.");
        notifyAll();

        return true;
    }

    public synchronized void chamarProximo() throws InterruptedException {
        while (clientesEsperando == 0) {
            System.out.println("Barbeiro: Zzzzz... (Dormindo na cadeira)");
            wait();
        }

        barbeiroPronto = true;
        notifyAll();

        while (!clientePronto) {
            wait();
        }

        clientePronto = false;
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
                
                System.out.println("Barbeiro: Cortando o cabelo do cliente...");
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

public class BarbeiroDorminhocoMonitor {

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