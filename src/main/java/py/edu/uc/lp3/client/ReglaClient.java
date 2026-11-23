package py.edu.uc.lp3.client;

public interface ReglaClient {

    String getNombre();

    ResultadoRegla validarDisparo(DisparoRequest req);

    ResultadoRegla validarImpacto(ImpactoRequest req);
}
