package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ProgramacaoEntregaItensAFON extends BaseBusiness {

	private static final long serialVersionUID = 3505214774764101580L;

	private static final Log LOG = LogFactory.getLog(ProgramacaoEntregaItensAFON.class);
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	public String buscarQuantidadeAFsProgramadas(Integer codigoMaterial, Integer afnNumero) {
		Integer quantidadeAFsProgramadas = scoItemAutorizacaoFornDAO.buscarQuantidadeAFsProgramadas(codigoMaterial, afnNumero);
		return "Saldo programado em outras AFs: " + quantidadeAFsProgramadas;
	}
	
	public List<ProgramacaoEntregaItemAFVO> listarProgramacaoEntregaItensAF(Integer numeroAF) {
		Integer numeroFornecedorPadrao = null;
		try {
			numeroFornecedorPadrao = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		} catch (ApplicationBusinessException e) {
			getLogger().error(e.getMessage());
		}
		
		List<ProgramacaoEntregaItemAFVO> listarProgramacaoEntregaItensAF = scoItemAutorizacaoFornDAO.listarProgramacaoEntregaItensAF(numeroAF, numeroFornecedorPadrao);
		
		atualizarCorFundo(listarProgramacaoEntregaItensAF);
		
		return listarProgramacaoEntregaItensAF;
	}
	
	private void atualizarCorFundo(List<ProgramacaoEntregaItemAFVO> listarProgramacaoEntregaItensAF) {
		for (ProgramacaoEntregaItemAFVO programacaoEntregaItemAFVO : listarProgramacaoEntregaItensAF) {
			programacaoEntregaItemAFVO.setCorFundoLinha("");
			
			if(programacaoEntregaItemAFVO.getpPedido().equals(0)) {
				programacaoEntregaItemAFVO.setCorFundoPPedido("yellow;");
			}
		}
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
}
