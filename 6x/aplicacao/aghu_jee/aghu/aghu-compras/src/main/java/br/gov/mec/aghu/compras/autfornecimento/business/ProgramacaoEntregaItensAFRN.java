package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class ProgramacaoEntregaItensAFRN extends BaseBusiness {

	private static final long serialVersionUID = -5313183825424673564L;
	
	private static final Log LOG = LogFactory.getLog(ProgramacaoEntregaItensAFRN.class);
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	public enum ProgramacaoEntregaItensAFRNMessageCode implements BusinessExceptionCode {
		M1_PROGENTITEMAF,M2_PROGENTITEMAF,M3_PROGENTITEMAF;
	}

	public void verificaAtualizacaoRegistro(ProgramacaoEntregaItemAFVO programacaoEntregaItemAFVO, List<ProgramacaoEntregaItemAFVO> listaProgramacaoEntregaItemAFAlterados) {
		if(possuiAlteracao(programacaoEntregaItemAFVO)) {
			if(!listaProgramacaoEntregaItemAFAlterados.contains(programacaoEntregaItemAFVO)) {
				listaProgramacaoEntregaItemAFAlterados.add(programacaoEntregaItemAFVO);
			}
		}
	}

	public void gravarProgramacaoEntregaItemAF(List<ProgramacaoEntregaItemAFVO> listaProgramacaoEntregaItemAF) throws ApplicationBusinessException {
		Integer fornecedorPadrao = null;
		try {
			fornecedorPadrao = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		if(listaProgramacaoEntregaItemAF!=null && !listaProgramacaoEntregaItemAF.isEmpty()){

			for (ProgramacaoEntregaItemAFVO programacaoEntregaItemAFVO : listaProgramacaoEntregaItemAF) {
				if(possuiAlteracao(programacaoEntregaItemAFVO)) {
					Integer seqEstoqueAlmoxarifado = getEstoqueFacade().buscarEstoqueAlmoxarifadoPorAutFornNumeroItlNumeroFornPadrao(
							programacaoEntregaItemAFVO.getNumero(), 
							programacaoEntregaItemAFVO.getItem(), 
							fornecedorPadrao);

					if(seqEstoqueAlmoxarifado != null) {
						SceEstoqueAlmoxarifado estoqueAlmoxarifado = getEstoqueFacade().obterEstoqueAlmoxarifadoPorChavePrimaria(seqEstoqueAlmoxarifado);
						estoqueAlmoxarifado.setQtdePontoPedido(programacaoEntregaItemAFVO.getpPedido());
						estoqueAlmoxarifado.setTempoReposicao(programacaoEntregaItemAFVO.getTempRep());
						estoqueAlmoxarifado.setIndPontoPedidoCalc(programacaoEntregaItemAFVO.getPpCal());
						estoqueAlmoxarifado.setIndControleValidade(programacaoEntregaItemAFVO.getContrVal());
						sceEstoqueAlmoxarifadoDAO.persistir(estoqueAlmoxarifado);
						sceEstoqueAlmoxarifadoDAO.flush();
					}else{
						throw new ApplicationBusinessException(ProgramacaoEntregaItensAFRNMessageCode.M2_PROGENTITEMAF, Severity.ERROR); 						
					}
				}
			}
			
		}else{
			throw new ApplicationBusinessException(ProgramacaoEntregaItensAFRNMessageCode.M3_PROGENTITEMAF, Severity.ERROR);
		}


		listaProgramacaoEntregaItemAF.retainAll(listaProgramacaoEntregaItemAF);
	}

	public Boolean possuiAlteracao(ProgramacaoEntregaItemAFVO programacaoEntregaItemAFVO) {
		if(!programacaoEntregaItemAFVO.getTempRepOriginal().equals(programacaoEntregaItemAFVO.getTempRep()) ||
				!programacaoEntregaItemAFVO.getpPedidoOriginal().equals(programacaoEntregaItemAFVO.getpPedido()) ||
				!programacaoEntregaItemAFVO.getPpCalOriginal().equals(programacaoEntregaItemAFVO.getPpCal()) ||
				!programacaoEntregaItemAFVO.getContrValOriginal().equals(programacaoEntregaItemAFVO.getContrVal())) {
			return Boolean.TRUE;
		} 
		return Boolean.FALSE;
	}


	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
		
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
}
