package br.gov.mec.aghu.sig.custos.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.dao.SigCentroProducaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterCentroProducaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterCentroProducaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SigCentroProducaoDAO sigCentroProducaoDAO;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;

	private static final long serialVersionUID = 3856416685284906679L;

	public enum ManterCentroProducaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NAO_PERMITIDA_ALTERACAO_CENTRO_PRODUCAO, MENSAGEM_NAO_PERMITIDA_EXCLUSAO_CENTRO_PRODUCAO, MENSAGEM_NAO_PERMITIDA_INCLUSAO_CENTRO_PRODUCAO, MENSAGEM_NOME_CENTRO_PRODUCAO_JA_CADASTRADO
	}

	public void excluirSigCentroProducao(Integer seqCentroProducao) throws ApplicationBusinessException {
		SigCentroProducao centroProducao = getSigCentroProducaoDAO().obterPorChavePrimaria(seqCentroProducao);
		try {
			if (!getCentroCustoFacade().existeCentroCustoAssociado(centroProducao)) {
				getSigCentroProducaoDAO().remover(centroProducao);
				getSigCentroProducaoDAO().flush();
			} else {
				throw new ApplicationBusinessException(ManterCentroProducaoONExceptionCode.MENSAGEM_NAO_PERMITIDA_EXCLUSAO_CENTRO_PRODUCAO);
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(ManterCentroProducaoONExceptionCode.MENSAGEM_NAO_PERMITIDA_EXCLUSAO_CENTRO_PRODUCAO);
		}
	}

	public void alterarCentroProducao(SigCentroProducao centroProducao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.validarCentroProducaoNomeUnico(centroProducao);
		try {
			centroProducao.setServidor(servidorLogado);
			getSigCentroProducaoDAO().merge(centroProducao);
		} catch (Exception e) {
			throw new ApplicationBusinessException(ManterCentroProducaoONExceptionCode.MENSAGEM_NAO_PERMITIDA_ALTERACAO_CENTRO_PRODUCAO);
		}
	}

	public void incluirCentroProducao(SigCentroProducao centroProducao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		this.validarCentroProducaoNomeUnico(centroProducao);

		try {
			centroProducao.setCriadoEm(new Date());
			centroProducao.setServidor(servidorLogado);
			getSigCentroProducaoDAO().persistir(centroProducao);
			getSigCentroProducaoDAO().flush();
		} catch (Exception e) {
			throw new ApplicationBusinessException(ManterCentroProducaoONExceptionCode.MENSAGEM_NAO_PERMITIDA_INCLUSAO_CENTRO_PRODUCAO);
		}

	}

	public void validarCentroProducaoNomeUnico(SigCentroProducao centroProducao) throws ApplicationBusinessException {
		SigCentroProducao centroProducaoCadastrado = this.getSigCentroProducaoDAO().obterPorNome(centroProducao.getNome());
		if (centroProducaoCadastrado != null) {
			if (centroProducao.getSeq() == null || !centroProducao.getSeq().equals(centroProducaoCadastrado.getSeq())) {
				throw new ApplicationBusinessException(ManterCentroProducaoONExceptionCode.MENSAGEM_NOME_CENTRO_PRODUCAO_JA_CADASTRADO);
			}
		}
	}

	public List<FccCentroCustos> pesquisarCentroCustosHierarquia(FccCentroCustos principal) {
		List<FccCentroCustos> pesquisarCentroCustosHierarquiaRep = this.pesquisarCentroCustosHierarquiaRep(principal);
		Collections.sort(pesquisarCentroCustosHierarquiaRep, new Comparator<FccCentroCustos>() {
			@Override
			public int compare(FccCentroCustos o1, FccCentroCustos o2) {			
				return o1.getCodigo().compareTo(o2.getCodigo());
			}
		});
		return pesquisarCentroCustosHierarquiaRep;
	}
	
	private List<FccCentroCustos> pesquisarCentroCustosHierarquiaRep(FccCentroCustos principal) {
		if (principal == null) {
			return new ArrayList<FccCentroCustos>();
		}

		List<FccCentroCustos> pesquisaCentroCustoAtivo = this.getSigCentroProducaoDAO().pesquisaCentroCustoAtivoHierarquia(principal);
		List<FccCentroCustos> retorno = new ArrayList<FccCentroCustos>();
		retorno.addAll(pesquisaCentroCustoAtivo);
		for (FccCentroCustos fccCentroCustos : pesquisaCentroCustoAtivo) {
			retorno.addAll(this.pesquisarCentroCustosHierarquiaRep(fccCentroCustos));
		}
		return retorno;
	}

	// DAOs
	public SigCentroProducaoDAO getSigCentroProducaoDAO() {
		return sigCentroProducaoDAO;
	}

	public ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
