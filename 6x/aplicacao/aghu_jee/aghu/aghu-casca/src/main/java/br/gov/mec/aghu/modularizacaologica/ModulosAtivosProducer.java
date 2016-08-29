package br.gov.mec.aghu.modularizacaologica;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.moduleintegration.ModulosAtivosQualifier;

@ApplicationScoped
public class ModulosAtivosProducer {

	@EJB
	protected ICascaFacade cascaFacade;	
	
	private Set<ModuloEnum> modulosAtivos = null;
	
	@Produces @ModulosAtivosQualifier
	public Set<ModuloEnum> registrarModulosAtivos() {
		
		if (modulosAtivos == null) {
			modulosAtivos = new HashSet<ModuloEnum>();
		
			List<Modulo> cscModulosAtivos =  cascaFacade.listarModulosAtivos();
		
			for(Modulo modulo : cscModulosAtivos){
				ModuloEnum item = ModuloEnum.obterPorValorBanco(modulo.getNome());
				if (item != null) {
					modulosAtivos.add(item);
				}
			}
		}
		return modulosAtivos;
		
		
	}
	

}