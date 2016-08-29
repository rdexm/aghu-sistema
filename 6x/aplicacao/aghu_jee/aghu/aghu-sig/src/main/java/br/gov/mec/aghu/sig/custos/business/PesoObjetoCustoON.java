package br.gov.mec.aghu.sig.custos.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioTipoRateio;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigParamCentroCusto;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoPesosDAO;
import br.gov.mec.aghu.sig.dao.SigParamCentroCustoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PesoObjetoCustoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PesoObjetoCustoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SigParamCentroCustoDAO sigParamCentroCustoDAO;
	
	@Inject
	private SigObjetoCustoPesosDAO sigObjetoCustoPesosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = -23112384315215L;

	public enum PesoObjetoCustoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_INFORMAR_PESOS_TABELA_SUS, MENSAGEM_ERRO_INFORMAR_TODOS_PESOS,
	}

	public Map<Integer, Double> pesquisarPesoTabelaUnificadaSUS(Integer codigoCentroCusto) {

		try {
			AghParametros pConvenioSus = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
			AghParametros pTabelaFaturPadrao = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			AghParametros pSusPlanoInternacao = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
			AghParametros pSusPlanoAmbulatorio = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);

			return this.getSigObjetoCustoPesosDAO().pesquisarPesoTabelaUnificadaSUS(codigoCentroCusto, pConvenioSus.getVlrNumerico().intValue(),
					pTabelaFaturPadrao.getVlrNumerico().intValue(), pSusPlanoInternacao.getVlrNumerico().intValue(),
					pSusPlanoAmbulatorio.getVlrNumerico().intValue());
		}
		// Captura as exceptios que podem ser geradas ao obter os parâmetros
		catch (ApplicationBusinessException e) {
			// E retorna um mapeamento vazio
			return new HashMap<Integer, Double>();
		}
	}

	public void persistirPesosObjetoCusto(List<SigObjetoCustoCcts> listaObjetoCustoCentroCusto, FccCentroCustos centroCusto, DominioTipoRateio tipoRateio,
			SigDirecionadores direcionador, Boolean estaUtilizandoTabelaSus, Map<Integer, Double> mapeamentoSus) throws ApplicationBusinessException {

		if (tipoRateio == DominioTipoRateio.P) {
			// Valida se todos os valores foram informados
			this.validarPreencimentoPesoObjetoCusto(listaObjetoCustoCentroCusto, mapeamentoSus, estaUtilizandoTabelaSus);
		}

		// Persiste os dados na tabela SIG_PARAM_CENTRO_CUSTOS
		this.persistirSigParamObjetoCusto(centroCusto, tipoRateio, direcionador);

		if (tipoRateio == DominioTipoRateio.P) {
			// Persiste cada um dos pesos dos objetos de custo
			if (listaObjetoCustoCentroCusto != null) {
				for (SigObjetoCustoCcts objetoCustoCcts : listaObjetoCustoCentroCusto) {
					this.persistirSigObjetoCustoPesos(objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos(), estaUtilizandoTabelaSus, mapeamentoSus);
				}
			}
		} else {
			// Deletar os pesos de custo
			this.excluirTodosPesosObjetoCusto(listaObjetoCustoCentroCusto);
		}
	}

	protected void persistirSigParamObjetoCusto(FccCentroCustos centroCusto, DominioTipoRateio tipoRateio, SigDirecionadores direcionador) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (centroCusto.getSigParamCentroCusto() == null) {
			centroCusto.setSigParamCentroCusto(new SigParamCentroCusto());
		}

		// Atualiza os campos que devem ser sempre alterados
		centroCusto.getSigParamCentroCusto().setTipoRateio(tipoRateio);
		centroCusto.getSigParamCentroCusto().setSigDirecionadores(direcionador);
		centroCusto.getSigParamCentroCusto().setServidor(servidorLogado);

		// Verifica se tem que fazer um insert, ou um update do registro
		if (centroCusto.getSigParamCentroCusto().getSeq() == null) {

			// Atualiza os dados que são definidos na criação
			centroCusto.getSigParamCentroCusto().setFccCentroCustos(centroCusto);
			centroCusto.getSigParamCentroCusto().setCriadoEm(new Date());

			this.getSigParamCentroCustoDAO().persistir(centroCusto.getSigParamCentroCusto());
		} else {
			this.getSigParamCentroCustoDAO().merge(centroCusto.getSigParamCentroCusto());
		}
	}

	protected void persistirSigObjetoCustoPesos(SigObjetoCustos objetoCusto, Boolean estaUtilizandoTabelaSus, Map<Integer, Double> mapeamentoSus)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		objetoCusto.getSigObjetoCustoPesos().setRapServidores(servidorLogado);

		// Quando estiver utilizando a tabela SUS
		if (estaUtilizandoTabelaSus) {

			// E o valor ajustado não for informado
			if (objetoCusto.getSigObjetoCustoPesos().getValor() == null) {
				// Então utiliza o valor padrão na coluna de valor ajustado
				objetoCusto.getSigObjetoCustoPesos().setValor(BigDecimal.valueOf(mapeamentoSus.get(objetoCusto.getSeq())));
			}
		}
		// Caso esteja utilizando outro direcionador
		else {
			// Apaga o valor base
			objetoCusto.getSigObjetoCustoPesos().setValor(null);
		}

		// Verifica se tem que fazer um insert, ou um update do registro
		if (objetoCusto.getSigObjetoCustoPesos().getSeq() == null) {
			objetoCusto.getSigObjetoCustoPesos().setCriadoEm(new Date());
			objetoCusto.getSigObjetoCustoPesos().setSigObjetoCustos(objetoCusto);
			this.getSigObjetoCustoPesosDAO().persistir(objetoCusto.getSigObjetoCustoPesos());
		} else {
			this.getSigObjetoCustoPesosDAO().atualizar(objetoCusto.getSigObjetoCustoPesos());
		}

	}

	protected void excluirTodosPesosObjetoCusto(List<SigObjetoCustoCcts> listaObjetoCustoCentroCusto) throws ApplicationBusinessException {

		if (listaObjetoCustoCentroCusto != null) {
			for (SigObjetoCustoCcts objetoCustoCcts : listaObjetoCustoCentroCusto) {
				if (objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSigObjetoCustoPesos().getSeq() != null) {
					this.getSigObjetoCustoPesosDAO().remover(objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSigObjetoCustoPesos());
					objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().setSigObjetoCustoPesos(null);
				}
			}
		}
	}

	protected void validarPreencimentoPesoObjetoCusto(List<SigObjetoCustoCcts> listaObjetoCustoCentroCusto, Map<Integer, Double> mapeamentoSus, Boolean estaUtilizandoTabelaSus)
			throws ApplicationBusinessException {

		if (listaObjetoCustoCentroCusto != null) {

			Double valorTabelaSus = null;
			BigDecimal valor = null;

			for (SigObjetoCustoCcts objetoCustoCcts : listaObjetoCustoCentroCusto) {

				valorTabelaSus = mapeamentoSus.get(objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSeq());
				if (valorTabelaSus != null && valorTabelaSus.intValue() == 0) {
					valorTabelaSus = null;
				}

				valor = objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSigObjetoCustoPesos().getValor();
				if (valor != null && valor.intValue() == 0) {
					valor = null;
					objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSigObjetoCustoPesos().setValor(null);
				}
				
				if (estaUtilizandoTabelaSus) {

					if (valor == null && valorTabelaSus == null) {
						throw new ApplicationBusinessException(PesoObjetoCustoONExceptionCode.MENSAGEM_ERRO_INFORMAR_PESOS_TABELA_SUS);
					}
				} else {
					if (valor == null) {
						throw new ApplicationBusinessException(PesoObjetoCustoONExceptionCode.MENSAGEM_ERRO_INFORMAR_TODOS_PESOS);
					}
				}
			}
		}
	}

	protected SigParamCentroCustoDAO getSigParamCentroCustoDAO() {
		return sigParamCentroCustoDAO;
	}

	protected SigObjetoCustoPesosDAO getSigObjetoCustoPesosDAO() {
		return sigObjetoCustoPesosDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
