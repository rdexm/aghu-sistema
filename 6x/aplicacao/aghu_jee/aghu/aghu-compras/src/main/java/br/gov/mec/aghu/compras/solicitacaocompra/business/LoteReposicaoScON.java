package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.vo.CriterioReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.FiltroReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.ItemReposicaoMaterialVO;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoLoteReposicao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class LoteReposicaoScON extends BaseBMTBusiness {

	private static final long serialVersionUID = -5922834917785524834L;
	private static final Log LOG = LogFactory.getLog(LoteReposicaoScON.class);

	@EJB
	private ReposicaoScON reposicaoScON;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public ScoLoteReposicao criarLote(FiltroReposicaoMaterialVO filtro, CriterioReposicaoMaterialVO criterioGeracao,
			List<ItemReposicaoMaterialVO> listaAlteracoes, List<Integer> nroDesmarcados) throws ApplicationBusinessException {

		FccCentroCustos ccSolic = getCentroCustoFacade().obterFccCentroCustos(
				this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.CENTRO_CUSTO_SOLIC).getVlrNumerico().intValue());

		FccCentroCustos ccAplic = getCentroCustoFacade().obterFccCentroCustos(
				this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.CENTRO_CUSTO_APLIC).getVlrNumerico().intValue());

		ScoPontoParadaSolicitacao pontoParadaAnterior = getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(
				this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.PPS_GER_AUTO).getVlrNumerico().shortValue());
		ScoPontoParadaSolicitacao pontoParadaAtual = this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(
				DominioTipoPontoParada.PL);

		AghParametros parametroTipoMovimento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR);

		List<ItemReposicaoMaterialVO> listaEdicao = this.getReposicaoScON().pesquisarMaterialReposicao(filtro, criterioGeracao);

		ScoLoteReposicao lote = this.getReposicaoScON().criarCabecalhoLoteReposicao(filtro, criterioGeracao);

		StringBuilder nomeLote = new StringBuilder(20);
		nomeLote.append("Lote ").append(lote.getDescricao()).append(" gerado por ")
				.append(this.getServidorLogadoFacade().obterServidorLogadoSemCache().getPessoaFisica().getNome());

		for (ItemReposicaoMaterialVO item : listaEdicao) {
			// tem que ser feito assim (uma transacao por material) pois leva aproximadamente 3 segundos para cada material. Geralmente são manipulados 200,
			// 500, 1000 materiais por vez, fato que leva a um estouro do timeout da transacao, atualmente de 5 minutos. - amenegotto em 23/04/2014
			try {
				this.beginTransaction(60 * 60);

				this.getReposicaoScON().inserirItemLoteReposicao(item, nroDesmarcados, listaAlteracoes, lote, nomeLote.toString(), ccSolic,
						ccAplic, parametroTipoMovimento, pontoParadaAnterior, pontoParadaAtual);
				this.commitTransaction();
			} catch (ApplicationBusinessException e) {
				String msgErro = "Erro ao processar reposicao de material com ID " + item.getMatCodigo() + " : " + e.getMessage();
				LOG.error(msgErro);
				this.rollbackTransaction();
			} catch (Exception e) {
				String msgErro = "Erro ao processar reposicao de material com ID " + item.getMatCodigo() + " : "
						+ Arrays.toString(e.getStackTrace());
				LOG.error(msgErro);
				this.rollbackTransaction();
			}
		}

		return lote;
	}

	protected ReposicaoScON getReposicaoScON() {
		return this.reposicaoScON;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

	protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return this.scoPontoParadaSolicitacaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}