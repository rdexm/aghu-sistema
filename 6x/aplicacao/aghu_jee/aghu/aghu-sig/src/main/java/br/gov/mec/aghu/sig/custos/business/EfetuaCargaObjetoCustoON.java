package br.gov.mec.aghu.sig.custos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCustoCcts;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoCctsDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoPhisDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoVersoesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/*
 * Classe que efetua carga de Objetos de custo provenientes de outros modulos.
 */
@Stateless
public class EfetuaCargaObjetoCustoON extends BaseBusiness {

	@EJB
	private IntegracaoCentralPendenciasON integracaoCentralPendenciasON;
	
	private static final Log LOG = LogFactory.getLog(EfetuaCargaObjetoCustoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private SigObjetoCustoCctsDAO sigObjetoCustoCctsDAO;
	
	@Inject
	private SigObjetoCustosDAO sigObjetoCustosDAO;
	
	@Inject
	private SigObjetoCustoVersoesDAO sigObjetoCustoVersoesDAO;
	
	@Inject
	private SigObjetoCustoPhisDAO sigObjetoCustoPhisDAO;

	private static final long serialVersionUID = 7510914930789292160L;
	
	public enum EfetuaCargaObjetoCustoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_UNIDADE_FUNCIONAL_SEM_CC
	}

	/*
	 * Estoria do usuario - #14915 - Carga de exames como objetos de custo
	 * 
	 * Método chamado após um exame ser cadastrado no modulo de exames, para a criação de um objeto de custo.
	 */
	public void efetuaCargaExames(String descricaoUsual, FccCentroCustos centroCustoCCTS, String siglaExameEdicao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		SigObjetoCustoPhisDAO sigObjetoCustoPhisDAO = getSigObjetoCustoPhisDAO();

		if (centroCustoCCTS == null) {
			throw new ApplicationBusinessException(EfetuaCargaObjetoCustoONExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_SEM_CC);
		}

		SigObjetoCustos objetoCusto = new SigObjetoCustos();
		objetoCusto.setCriadoEm(new Date());
		objetoCusto.setRapServidores(servidorLogado);
		objetoCusto.setIndCompartilha(false);
		objetoCusto.setNome(descricaoUsual);
		objetoCusto.setIndTipo(DominioTipoObjetoCusto.AS);
		getSigObjetoCustosDAO().persistir(objetoCusto);

		//defaults para a versao
		final Integer nroVersao = 1;
		DominioSituacaoVersoesCustos situacaoPadraoVersao = DominioSituacaoVersoesCustos.E;

		SigObjetoCustoVersoes objetoCustoVersao = new SigObjetoCustoVersoes();
		objetoCustoVersao.setCriadoEm(new Date());
		objetoCustoVersao.setDataInicio(new Date());
		objetoCustoVersao.setIndSituacao(situacaoPadraoVersao);
		objetoCustoVersao.setRapServidores(servidorLogado);
		objetoCustoVersao.setSigObjetoCustos(objetoCusto);
		objetoCustoVersao.setNroVersao(nroVersao);
		this.getSigObjetoCustoVersoesDAO().persistir(objetoCustoVersao);

		//default para o CCTS
		DominioSituacao situacaoCCTS = DominioSituacao.A;
		SigObjetoCustoCcts objetoCustoCcts = new SigObjetoCustoCcts();
		objetoCustoCcts.setCriadoEm(new Date());
		objetoCustoCcts.setRapServidores(servidorLogado);
		objetoCustoCcts.setIndSituacao(situacaoCCTS);
		objetoCustoCcts.setSigObjetoCustoVersoes(objetoCustoVersao);
		objetoCustoCcts.setFccCentroCustos(centroCustoCCTS);
		objetoCustoCcts.setIndTipo(DominioTipoObjetoCustoCcts.P);
		this.getSigObjetoCustoCctsDAO().persistir(objetoCustoCcts);

		//default para os PHI 
		DominioSituacao situacaoPHI = DominioSituacao.A;
		List<FatProcedHospInternos> listaProcedimentos = this.getFaturamentoFacade().listaPhiAtivosExame(situacaoPHI, siglaExameEdicao);
		for (FatProcedHospInternos fatProcedHospInternos : listaProcedimentos) {
			SigObjetoCustoPhis objetoCustoPHI = new SigObjetoCustoPhis();
			objetoCustoPHI.setSigObjetoCustoVersoes(objetoCustoVersao);
			objetoCustoPHI.setDominioSituacao(situacaoPHI);
			objetoCustoPHI.setRapServidores(servidorLogado);
			objetoCustoPHI.setCriadoEm(new Date());
			objetoCustoPHI.setFatProcedHospInternos(fatProcedHospInternos);
			sigObjetoCustoPhisDAO.persistir(objetoCustoPHI);
		}

		sigObjetoCustoPhisDAO.flush();

		//Cria uma pendência informando que o objeto de custo deve ainda ser atualizado
		this.getIntegracaoCentralPendenciasON().adicionarPendenciaCadastroNovoExame(objetoCusto.getNome(), objetoCustoVersao.getSeq(),
				centroCustoCCTS.getCodigo());
	}

	protected IntegracaoCentralPendenciasON getIntegracaoCentralPendenciasON() {
		return integracaoCentralPendenciasON;
	}

	protected SigObjetoCustoVersoesDAO getSigObjetoCustoVersoesDAO() {
		return sigObjetoCustoVersoesDAO;
	}

	protected SigObjetoCustosDAO getSigObjetoCustosDAO() {
		return sigObjetoCustosDAO;
	}

	protected SigObjetoCustoCctsDAO getSigObjetoCustoCctsDAO() {
		return sigObjetoCustoCctsDAO;
	}

	protected SigObjetoCustoPhisDAO getSigObjetoCustoPhisDAO() {
		return sigObjetoCustoPhisDAO;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
