package br.gov.mec.aghu.action.converter;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.converter.AbstractConverter;

@FacesConverter(value = "aghUnidadesFuncionaisPorAndarAlaDescricaoCaracteristicasInternacaoOuEmergenciaConverter")
public class AghUnidadesFuncionaisPorAndarAlaDescricaoCaracteristicasInternacaoOuEmergenciaConverter extends AbstractConverter {

	private static final long serialVersionUID = 3108142242511165939L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Override
	public Object getAsObject(String valor) {
		List<AghUnidadesFuncionais> list = cadastrosBasicosInternacaoFacade
				.pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoCaracteristicasInternacaoOuEmergencia(valor);
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public String getAsString(Object valor) {
		return ((AghUnidadesFuncionais) valor).getSeq().toString();
	}

}
