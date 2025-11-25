package gitrepositoryanalysis;

public class Infra {
    public boolean k8s;
    public boolean helm;

    public Infra() {}
    public Infra(boolean k8s, boolean helm) {
        this.k8s = k8s;
        this.helm = helm;
    }
}
