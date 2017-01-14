package ponny.org.telemed.negocio;

/**
 * Created by Daniel on 13/01/2017.
 */
public class Oximetria {
    private int spo2,pulse;
    private double pi;

    public Oximetria() {
    }

    public Oximetria(int spo2, int pulse, double pi) {
        this.spo2 = spo2;
        this.pulse = pulse;
        this.pi = pi;
    }

    public int getSpo2() {
        return spo2;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public double getPi() {
        return pi;
    }

    public void setPi(double pi) {
        this.pi = pi;
    }
}
