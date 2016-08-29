package br.gov.mec.aghu.action.converter;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.core.converter.AbstractConverter;

@FacesConverter(value = "aghEquipesAtivasConverter")
public class AghEquipesAtivasConverter extends AbstractConverter {

	private static final long serialVersionUID = -3841705681637558096L;

	@EJB
	private IAghuFacade aghuFacade;

	@Override
	public Object getAsObject(String valor) {
		List<AghEquipes> result = aghuFacade.pesquisarEquipesAtivasPorNomeOuCodigo(valor, 25);

		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

	@Override
	public String getAsString(Object valor) {
		return valor == null ? "" : ((AghEquipes) valor).getSeq().toString();
	}

}
