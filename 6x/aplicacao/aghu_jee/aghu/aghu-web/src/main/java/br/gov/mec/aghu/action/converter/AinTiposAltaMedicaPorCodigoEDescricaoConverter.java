package br.gov.mec.aghu.action.converter;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.core.converter.AbstractConverter;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@FacesConverter(value = "ainTiposAltaMedicaPorCodigoEDescricaoConverter")
public class AinTiposAltaMedicaPorCodigoEDescricaoConverter extends AbstractConverter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1299012541911682220L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IParametroFacade parametroFacade;;
	
	@Override
	public Object getAsObject(String valor) {

		if (StringUtils.isBlank(valor)) {
			return null;
		}
		AghParametros param = null;
		try{
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CODIGOS_TIPO_ALTA_MEDICA);
		}catch(ApplicationBusinessException e){
			logError("Ocorreu um erro ao buscar o parâmetro P_AGHU_CODIGOS_TIPO_ALTA_MEDICA na tabela AGH_PARAMETROS.");
		}
		String [] idsFiltrados = param.getVlrTexto().split(",");
		List<AinTiposAltaMedica> result = cadastrosBasicosInternacaoFacade.pesquisarTipoAltaMedicaPorCodigoEDescricao(valor, idsFiltrados);

		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}

	}

	@Override
	public String getAsString(Object valor) {
		return ((AinTiposAltaMedica) valor).getDescricao();
	}

}
