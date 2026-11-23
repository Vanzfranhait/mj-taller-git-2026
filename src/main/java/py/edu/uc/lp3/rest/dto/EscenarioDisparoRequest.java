package py.edu.uc.lp3.rest.dto;

public record EscenarioDisparoRequest(
        String modelo,
        Integer carga,
        Integer distancia,
        Integer vida,
        Integer blindaje
) {
}
