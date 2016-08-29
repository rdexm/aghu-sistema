package br.gov.mec.aghu.sig.custos.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSigTipoAlerta;
import br.gov.mec.aghu.dominio.DominioSigTipoAlertaDetalhado;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAlertasProcessamentoVO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoAlertasDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoAnalisesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class AlertaProcessamentoON extends BaseBusiness {

	private static final long serialVersionUID = 6679613926967678214L;
	private static final Log LOG = LogFactory.getLog(AlertaProcessamentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private SigProcessamentoAnalisesDAO sigProcessamentoAnalisesDAO;

	@Inject
	private SigProcessamentoAlertasDAO sigProcessamentoAlertasDAO;

	@EJB
	private ManterObjetosCustoON manterObjetosCustoON;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	/**
	 * {@link ICustosSigCadastrosBasicosFacade#buscaAlertasPorProcessamentoCentroCusto(Integer, Integer)}
	 */
	public List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCentroCusto(Integer seqProcessamento,
			Integer codigoCentroCusto) {
		List<VisualizarAlertasProcessamentoVO> alertas = new ArrayList<VisualizarAlertasProcessamentoVO>();
		alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCCQtAjustadaInsumos(seqProcessamento,
				codigoCentroCusto));
		alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCCQtAjustadaPessoas(seqProcessamento,
				codigoCentroCusto));
		alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCCSemObjCustoAtivo(seqProcessamento,
				codigoCentroCusto));
		alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCCSomenteRateio(seqProcessamento,
				codigoCentroCusto));
		return alertas;
	}

	/**
	 * {@link ICustosSigCadastrosBasicosFacade#buscaAlertasPorProcessamentoCentroCustoSemAnalise(Integer, Integer)}
	 */
	public List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCentroCustoSemAnalise(
			SigProcessamentoCusto sigProcessamentoCusto, FccCentroCustos fccCentroCustos, DominioSigTipoAlertaDetalhado tipoAlerta,
			Integer firstResult, Integer maxResult) {
		Integer codigoCentroCusto = null;
		if (fccCentroCustos != null) {
			codigoCentroCusto = fccCentroCustos.getCodigo();
		}
		List<VisualizarAlertasProcessamentoVO> alertas = new ArrayList<VisualizarAlertasProcessamentoVO>();
		if (tipoAlerta == null) {
			alertas.addAll(this.buscarAlertasPorProcessamentoCentroCusto(sigProcessamentoCusto.getSeq(), codigoCentroCusto));
			alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCCSemParecer(sigProcessamentoCusto.getSeq(),
					codigoCentroCusto));
		} else {
			switch (tipoAlerta) {
			case SA:
				alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCCSemParecer(
						sigProcessamentoCusto.getSeq(), codigoCentroCusto));
				break;
			case QA:
				alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCCQtAjustadaInsumos(
						sigProcessamentoCusto.getSeq(), codigoCentroCusto));
				alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCCQtAjustadaPessoas(
						sigProcessamentoCusto.getSeq(), codigoCentroCusto));
				break;
			case CR:
				alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCCSomenteRateio(
						sigProcessamentoCusto.getSeq(), codigoCentroCusto));
				break;
			case NC:
				alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCCSemObjCustoAtivo(
						sigProcessamentoCusto.getSeq(), codigoCentroCusto));
				break;
			case CP:
				alertas.addAll(this.getSigProcessamentoAlertasDAO().buscarAlertasPorProcessamentoCP(sigProcessamentoCusto.getSeq(),
						codigoCentroCusto));
				break;
			default:
				throw new UnsupportedOperationException("No suport for Enum type.");
			}
		}
		if (firstResult != null && maxResult != null) {
			if (firstResult + maxResult > alertas.size()) {
				return alertas.subList(firstResult, alertas.size());
			} else {
				return alertas.subList(firstResult, firstResult + maxResult);
			}
		}
		return alertas;
	}

	/**
	 * {@link ICustosSigCadastrosBasicosFacade#buscaTotaisParaCadaTipoAlerta(SigProcessamentoCusto)}
	 */
	public List<VisualizarAlertasProcessamentoVO> buscarTotaisParaCadaTipoAlerta(SigProcessamentoCusto processamentoCusto)
			throws ApplicationBusinessException {
		List<VisualizarAlertasProcessamentoVO> alertas = new ArrayList<VisualizarAlertasProcessamentoVO>();

		this.calcularTotal(DominioSigTipoAlertaDetalhado.QA, this.getSigProcessamentoAlertasDAO()
				.buscarTotalAlertasPorProcessamentoTipoAlerta(processamentoCusto, DominioSigTipoAlerta.QA), alertas);
		this.calcularTotal(DominioSigTipoAlertaDetalhado.CR, this.getSigProcessamentoAlertasDAO()
				.buscarTotalAlertasPorProcessamentoTipoAlerta(processamentoCusto, DominioSigTipoAlerta.CR), alertas);
		this.calcularTotal(DominioSigTipoAlertaDetalhado.NC, this.getSigProcessamentoAlertasDAO()
				.buscarTotalAlertasPorProcessamentoTipoAlerta(processamentoCusto, DominioSigTipoAlerta.NC), alertas);
		this.calcularTotal(DominioSigTipoAlertaDetalhado.SA,
				this.getSigProcessamentoAnalisesDAO().buscarTotalAnalisesSemParecer(processamentoCusto), alertas);
		this.calcularTotal(DominioSigTipoAlertaDetalhado.CP, this.getSigProcessamentoAlertasDAO()
				.buscarTotalAlertasPorProcessamentoTipoAlerta(processamentoCusto, DominioSigTipoAlerta.CP), alertas);

		double totalCC = 0D + this.getCentroCustoFacade().pesquisarCentroCustosPorCentroProdExcluindoGcc("", null, DominioSituacao.A)
				.size();
		for (VisualizarAlertasProcessamentoVO alerta : alertas) {
			if (alerta.getQuantidade() > 0) {
				alerta.setPercentual((alerta.getQuantidade() * 100) / totalCC);
			}
		}
		return alertas;
	}

	private void calcularTotal(DominioSigTipoAlertaDetalhado descricao, Long total, List<VisualizarAlertasProcessamentoVO> alertas) {
		if (total != null && total > 0) {
			VisualizarAlertasProcessamentoVO visualizarAlertasProcessamentoVO = new VisualizarAlertasProcessamentoVO();
			visualizarAlertasProcessamentoVO.setDominioSigTipoAlertaDetalhado(descricao);
			visualizarAlertasProcessamentoVO.setQuantidade(total.intValue());
			alertas.add(visualizarAlertasProcessamentoVO);
		}
	}

	// DAOs, ONs e FACADEs

	protected SigProcessamentoAlertasDAO getSigProcessamentoAlertasDAO() {
		return sigProcessamentoAlertasDAO;
	}

	protected SigProcessamentoAnalisesDAO getSigProcessamentoAnalisesDAO() {
		return sigProcessamentoAnalisesDAO;
	}

	protected ManterObjetosCustoON getManterObjetosCustoON() {
		return manterObjetosCustoON;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

}
