package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoParamProgEntgAfDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.vo.ExcluirProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.model.ScoParamProgEntgAf;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ExcluirProgramacaoEntregaItemAFRN extends BaseBusiness {

	private static final long serialVersionUID = -5313183825424673564L;
	private static final Log LOG = LogFactory.getLog(ExcluirProgramacaoEntregaItemAFRN.class);

	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoParamProgEntgAfDAO scoParamProgEntgAfDAO;

	@Inject
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;

	public enum ExcluirProgramacaoEntregaItemAFRNExceptionCode implements BusinessExceptionCode {
		ALERTA_ITEM_SEM_PROGRAMACAO;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	//C1
	public List<ExcluirProgramacaoEntregaItemAFVO> pesquisarListaProgrEntregaItensAfExclusao(Integer numeroAF) {
		return getScoItemAutorizacaoFornDAO().buscarProgramacaoItensAFExclusao(numeroAF);
	}

	//RN01
	public void excluirProgrEntregaItensAf(Integer numeroAF) throws ApplicationBusinessException {
		List<ScoProgEntregaItemAutorizacaoFornecimento> parcelasNaoAssinadasNaoCanceladas = buscaParcelasNaoAssinadasNaoCanceladas(
				numeroAF, null);
		List<ScoSolicitacaoProgramacaoEntrega> listaSolics = getScoSolicitacaoProgramacaoEntregaDAO().listarSolicitacaoByItemAFId (numeroAF, null, null, null);

		if (parcelasNaoAssinadasNaoCanceladas == null || parcelasNaoAssinadasNaoCanceladas.isEmpty()) {
			throw new ApplicationBusinessException(ExcluirProgramacaoEntregaItemAFRNExceptionCode.ALERTA_ITEM_SEM_PROGRAMACAO);
		} else {
			removerParcelas(parcelasNaoAssinadasNaoCanceladas, listaSolics);
			if (!existemParcelasAssinadas(numeroAF)) {
				List<ScoParamProgEntgAf> vinculosAfSolicitacaoCompras = this.getScoParamProgEntgAfDAO()
						.buscaVinculosAfSolicitacaoCompras(numeroAF, null);
				removerVinculosAFSolicitacaoCompras(vinculosAfSolicitacaoCompras);
			}
		}
	}

	//RN02
	public void excluirListaProgrEntregaItensAfExclusao(Integer numeroAF, List<ExcluirProgramacaoEntregaItemAFVO> listaItensProgramacao) {
		Integer numeroItemAf;
		for (ExcluirProgramacaoEntregaItemAFVO itemAF : listaItensProgramacao) {
			numeroItemAf = itemAF.getNumeroItem();

			List<ScoProgEntregaItemAutorizacaoFornecimento> parcelasNaoAssinadasNaoCanceladas = buscaParcelasNaoAssinadasNaoCanceladas(
					numeroAF, numeroItemAf);
			List<ScoSolicitacaoProgramacaoEntrega> listaSolicProgEntregas = getScoSolicitacaoProgramacaoEntregaDAO().listarSolicitacaoByItemAFId(numeroAF, numeroItemAf, null, null);

			removerParcelas(parcelasNaoAssinadasNaoCanceladas, listaSolicProgEntregas);

			List<ScoParamProgEntgAf> vinculosAfSolicitacaoCompras = this.getScoParamProgEntgAfDAO().buscaVinculosAfSolicitacaoCompras(numeroAF,
					numeroItemAf);

			removerVinculosAFSolicitacaoCompras(vinculosAfSolicitacaoCompras);
		}
	}

	public List<ScoProgEntregaItemAutorizacaoFornecimento> buscaParcelasNaoAssinadasNaoCanceladas(Integer numeroAF, Integer numeroItem) {
		return getScoProgEntregaItemAutorizacaoFornecimentoDAO().buscaParcelasNaoAssinadasNaoCanceladas(numeroAF, numeroItem);
	}

	private void removerParcelas(List<ScoProgEntregaItemAutorizacaoFornecimento> parcelas, List<ScoSolicitacaoProgramacaoEntrega> solics) {

		for (ScoSolicitacaoProgramacaoEntrega solic : solics) {
			getScoSolicitacaoProgramacaoEntregaDAO().remover(solic);
//			getScoSolicitacaoProgramacaoEntregaDAO().flush();
		}

		for (ScoProgEntregaItemAutorizacaoFornecimento parcela : parcelas) {
			getScoProgEntregaItemAutorizacaoFornecimentoDAO().remover(parcela);
//			getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
		}
	}

	private Boolean existemParcelasAssinadas(Integer numeroAF) {
		return getScoProgEntregaItemAutorizacaoFornecimentoDAO().verificaParcelaAssinadas(numeroAF);
	}

	private void removerVinculosAFSolicitacaoCompras(List<ScoParamProgEntgAf> vinculos) {
		for (ScoParamProgEntgAf vinculo : vinculos) {
			this.getScoParamProgEntgAfDAO().remover(vinculo);
		}
	}

	private ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	private ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	private ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO() {return scoSolicitacaoProgramacaoEntregaDAO;}
	
	public ScoParamProgEntgAfDAO getScoParamProgEntgAfDAO() {
		return scoParamProgEntgAfDAO;
	}
}
