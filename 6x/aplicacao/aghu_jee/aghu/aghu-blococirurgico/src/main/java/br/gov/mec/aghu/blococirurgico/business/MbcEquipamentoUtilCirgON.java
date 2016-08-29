package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoUtilCirg;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcEquipamentoUtilCirgON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcEquipamentoUtilCirgON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 8727791009899759668L;
	
	protected enum MbcEquipamentoUtilCirgONExceptionCode implements BusinessExceptionCode {
		MBCEQUIPAMENTO_MENSAGEM_EQP_JA_CADASTRADO
	}

	public Boolean atualizarQuantidadeSeExistirMbcEquipamentoCirurgicoNaListaMbcEquipamentoutilCirg(
			MbcEquipamentoCirurgico mbcEquipamentoCirurgicoSelecionadoNaSuggestion,
			List<MbcEquipamentoUtilCirg> listaMbcEquipamentoUtilCirg,
			Short quantidade) throws ApplicationBusinessException {
		
		for(int i = 0; i < listaMbcEquipamentoUtilCirg.size(); i++){
			//significa que jah tem o quipamento na lista
			if(listaMbcEquipamentoUtilCirg.get(i).getMbcEquipamentoCirurgico().equals(mbcEquipamentoCirurgicoSelecionadoNaSuggestion)){
				throw new ApplicationBusinessException(MbcEquipamentoUtilCirgONExceptionCode.MBCEQUIPAMENTO_MENSAGEM_EQP_JA_CADASTRADO);
			}	
		}
		return Boolean.FALSE;
	}

}
