package br.gov.mec.aghu.action.converter;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.MbcpProcedimentoCirurgicoVO;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.converter.AbstractConverter;
import br.gov.mec.aghu.core.locator.ServiceLocator;

@FacesConverter(value = "mbcpProcedimentoCirurgicoVOConverter")
public class MbcpProcedimentoCirurgicoVOConverter extends AbstractConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8131739400200702115L;
	
		
	private IBlocoCirurgicoFacade blocoCirurgicoFacade = ServiceLocator.getBean(IBlocoCirurgicoFacade.class, "aghu-blococirurgico");
	
	@Override
	public Object getAsObject(String valor) {
				
		if (!StringUtils.isBlank(valor) && CoreUtil.isNumeroInteger(valor)) {
			final Integer seq = Integer.valueOf(valor);
			final MbcProcedimentoCirurgicos procedCirurgico = blocoCirurgicoFacade.obterMbcProcedimentoCirurgicosPorId(seq);
			
			if (procedCirurgico != null) {
				return new MbcpProcedimentoCirurgicoVO(procedCirurgico.getSeq(),procedCirurgico.getDescricao());
			}
		}
		return null;
		
	}

	@Override
	public String getAsString(Object valor) {
		if (valor==null) {
			return null;
		}
		return ((MbcpProcedimentoCirurgicoVO) valor).getSeq().toString();		
	}

}
