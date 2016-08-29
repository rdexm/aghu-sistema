package br.gov.mec.aghu.action.converter;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.MotivoCancelamentoVO;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.converter.AbstractConverter;
import br.gov.mec.aghu.core.locator.ServiceLocator;

@FacesConverter(value = "motivoCancelamentoVOConverter")
public class MotivoCancelamentoVOConverter extends AbstractConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8131739400200702115L;
	
		
	private IBlocoCirurgicoFacade blocoCirurgicoFacade = ServiceLocator.getBean(IBlocoCirurgicoFacade.class, "aghu-blococirurgico");
	
	@Override
	public Object getAsObject(String valor)	{
		if (!StringUtils.isBlank(valor) && CoreUtil.isNumeroShort(valor)) {
			Short seq = Short.valueOf(valor);
			MbcMotivoCancelamento motivoCancelamento = blocoCirurgicoFacade.obterMotivoCancelamentoPorChavePrimaria(seq);
			if (motivoCancelamento != null) {
				return new MotivoCancelamentoVO(motivoCancelamento.getDescricao(), motivoCancelamento.getSeq());
			}
		}
		
		return null;
	}	

	@Override
	public String getAsString(Object valor) {
		if (valor==null) {
			return null;
		}
		return ((MotivoCancelamentoVO) valor).getSeq().toString();	
	}

}
