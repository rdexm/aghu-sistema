package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirurgicoDAO;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoUtilCirg;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class MbcEquipamentoCirurgicoON2 extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcEquipamentoCirurgicoON2.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcEquipamentoCirurgicoDAO mbcEquipamentoCirurgicoDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 4092588775954285567L;

	private MbcEquipamentoCirurgicoDAO getMbcEquipamentoCirurgicoDAO(){
		return mbcEquipamentoCirurgicoDAO;
	}
	
	public MbcEquipamentoUtilCirg getMbcEquipamentosUtilCirgsPorMbcEquipamentoCirurgico(MbcEquipamentoCirurgico mbcEquipamentoCirurgicoSelecionadoNaSuggestion, Short quantidade) {
		MbcEquipamentoCirurgico original = this.getMbcEquipamentoCirurgicoDAO().obterOriginal(mbcEquipamentoCirurgicoSelecionadoNaSuggestion);
		MbcEquipamentoUtilCirg retorno = new MbcEquipamentoUtilCirg();
		retorno.setMbcEquipamentoCirurgico(original);
		retorno.setQuantidade(quantidade);
		return retorno;
	}
}
