package br.gov.mec.aghu.action.converter;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.converter.AbstractConverter;
import br.gov.mec.aghu.core.locator.ServiceLocator;

@FacesConverter(value = "mbcSalaCirurgiaConverter")
public class MbcSalaCirurgiaConverter extends AbstractConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8131739400200702222L;
	
		
	private IBlocoCirurgicoFacade blocoCirurgicoFacade = ServiceLocator.getBean(IBlocoCirurgicoFacade.class, "aghu-blococirurgico");
	
	@Override
	public Object getAsObject(String valor) {
				
		if (!StringUtils.isBlank(valor)) {
			String [] ids = valor.split(",");
			
			if (CoreUtil.isNumeroShort(ids[0]) &&
		    	CoreUtil.isNumeroShort(ids[1])){
		    	
		        Short unfSeq = Short.valueOf(ids[0]);
		        Short seq = Short.valueOf(ids[1]);
		        
		        MbcSalaCirurgicaId idSala = new MbcSalaCirurgicaId(unfSeq, seq);
		        return blocoCirurgicoFacade.obterSalaCirurgicaPorId(idSala);
			}
			
			return null;
		}
		return null;
		
	}

	@Override
	public String getAsString(Object valor) {
		if (valor==null) {
			return null;
		}
		return ((MbcSalaCirurgica) valor).getId().getUnfSeq().toString() + "," + ((MbcSalaCirurgica) valor).getId().getSeqp().toString();		
	}

}
