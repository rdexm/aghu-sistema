package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.vo.ConsultarParcelasEntregaMateriaisVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ParcelaEntregaMaterialON extends BaseBusiness {

	/** 
	 * 
	 */
	private static final long serialVersionUID = -7372424576476899828L;
	
	private static final Log LOG = LogFactory.getLog(ParcelaEntregaMaterialON.class);
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	@EJB
	private ParcelaEntregaMaterialRN parcelaEntregaMaterialRN;

	public List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPorItem() throws ApplicationBusinessException {
		Date dataEntrega = getParcelaEntregaMaterialRN().buscarDataEntrega();
		return getScoProgEntregaItemAutorizacaoFornecimentoDAO().consultarLiberacaoEntragasPorItem(dataEntrega);
	}
	
	public List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPorItemNumeroAfNumeroComplemento(Integer numeroAf, Short numeroComplemento) throws ApplicationBusinessException {
		Date dataEntrega = getParcelaEntregaMaterialRN().buscarDataEntrega();
		return getScoProgEntregaItemAutorizacaoFornecimentoDAO().consultarParcelasLiberar(dataEntrega, numeroAf, numeroComplemento);
	}
	
	public List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPorItemNumeroLctNumeroComplemento(Integer numeroAf, Short numeroComplemento) {
		return getScoProgEntregaItemAutorizacaoFornecimentoDAO().consultarAlterarProgramacaoProgPendente(numeroAf, numeroComplemento, true);
	}
	
	public List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPendentesMaterial(Integer numeroAf, Short numeroComplemento) {
		return getScoProgEntregaItemAutorizacaoFornecimentoDAO().consultarAlterarProgramacaoProgPendente(numeroAf, numeroComplemento, false);
	}

	protected ParcelaEntregaMaterialRN getParcelaEntregaMaterialRN() {
		return parcelaEntregaMaterialRN;
	}
	
	
	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	protected void setScoProgEntregaItemAutorizacaoFornecimentoDAO(ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO) {
		this.scoProgEntregaItemAutorizacaoFornecimentoDAO = scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}
