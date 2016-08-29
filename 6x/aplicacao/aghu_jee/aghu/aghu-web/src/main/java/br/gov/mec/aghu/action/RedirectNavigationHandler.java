package br.gov.mec.aghu.action;

import java.util.Map;
import java.util.Set;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

public class RedirectNavigationHandler extends ConfigurableNavigationHandler {

    private NavigationHandler parent;

    public RedirectNavigationHandler(NavigationHandler parent) {
        this.parent = parent;
    }

    @Override
    public void handleNavigation(FacesContext context, String from, String outcome) {
        if (validarOutcome(outcome)) {
            outcome += "?faces-redirect=true";
        }

        parent.handleNavigation(context, from, outcome);        
    }

	private boolean validarOutcome(String outcome) {
		return outcome != null 
				&&  !outcome.startsWith("casca") 
				&&  !outcome.startsWith("paciente") 
				&&  !outcome.startsWith("ambulatorio") 
				&&  !outcome.startsWith("bancodesangue") 
				&&  !outcome.startsWith("blococirurgico") 
				&&  !outcome.startsWith("controleinfeccao") 
				&&  !outcome.startsWith("certificacaodigital") 
				&&  !outcome.startsWith("controleinfeccao") 
				&&  !outcome.startsWith("controlepaciente") 
				&&  !outcome.startsWith("custos") 
				&&  !outcome.startsWith("estoque") 
				&&  !outcome.startsWith("exames")
				&&  !outcome.startsWith("compras")
				&&  !outcome.startsWith("farmacia") 
				&&  !outcome.startsWith("faturamento") 
				&&  !outcome.startsWith("internacao") 
				&&  !outcome.startsWith("paciente") 				
				&&  !outcome.startsWith("patrimonio")
				&&  !outcome.startsWith("pol") 
				&&  !outcome.startsWith("prescricaoenfermagem") 
				&&  !outcome.startsWith("prescricaomedica") 
				&&  !outcome.startsWith("sicon")
				&&  !outcome.startsWith("transplante")
				&&  !outcome.startsWith("suprimentos")
				&&  !outcome.startsWith("procedimentoterapeutico")
				&&  !outcome.startsWith("emergencia")				&&  !outcome.startsWith("perinatologia")
				&& !outcome.contains("faces-redirect=true");
	}
	
    @Override
    public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
        if (parent instanceof ConfigurableNavigationHandler) {
            return ((ConfigurableNavigationHandler) parent).getNavigationCase(context, fromAction, outcome);
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Set<NavigationCase>> getNavigationCases() {
        if (parent instanceof ConfigurableNavigationHandler) {
            return ((ConfigurableNavigationHandler) parent).getNavigationCases();
        } else {
            return null;
        }
    }

}
