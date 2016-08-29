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
public class MbcEquipamentoCirurgicoBuscaON extends BaseBusiness {


@Inject
private MbcEquipamentoCirurgicoDAO mbcEquipamentoCirurgicoDAO;


private static final Log LOG = LogFactory.getLog(MbcEquipamentoCirurgicoBuscaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


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
