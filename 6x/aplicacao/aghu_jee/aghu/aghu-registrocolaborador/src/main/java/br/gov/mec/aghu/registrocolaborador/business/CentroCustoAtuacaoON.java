package br.gov.mec.aghu.registrocolaborador.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CentroCustoAtuacaoON extends BaseBusiness {


@EJB
private CentroCustoAtuacaoRN centroCustoAtuacaoRN;

@EJB
private ServidorRN servidorRN;

private static final Log LOG = LogFactory.getLog(CentroCustoAtuacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private RapServidoresDAO rapServidoresDAO;

@EJB
private ICentroCustoFacade centroCustoFacade;
	/**
	 * 
	 */
	private static final long serialVersionUID = 710793836893871731L;

	public enum ServidorONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CENTRO_CUSTO_NAO_ENCONTRADO, MENSAGEM_PARAMETRO_NAO_INFORMADO;
	}
	
	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}
	
	protected ServidorRN getServidorRN() {
		return servidorRN;
	}
	
	protected CentroCustoAtuacaoRN getCentroCustoAtuacaoRN() {
		return centroCustoAtuacaoRN;
	}

	public List<RapServidores> pesquisarServidores(Integer codigoCCLotacao,
			Integer codVinculo, Integer matricula, String nomeServidor,
			Integer codigoCCAtuacao, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) throws ApplicationBusinessException {
		
		return this.getRapServidoresDAO().pesquisarServidores(codigoCCLotacao, codVinculo, matricula, nomeServidor, codigoCCAtuacao, firstResult, maxResults, orderProperty, asc);
	}

	
	public void salvar(RapServidores rapServidor) throws ApplicationBusinessException {

		if (rapServidor == null || rapServidor.getId() == null) {
			throw new ApplicationBusinessException(ServidorONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		
		getCentroCustoAtuacaoRN().validarCCAtuacao(rapServidor);

		RapServidores old = rapServidoresDAO.obterOld(rapServidor);
		getServidorRN().atualizarJournal(old);

		rapServidoresDAO.merge(rapServidor);
	}

	public Long pesquisarServidoresCount(Integer codigoCCLotacao,
			Integer codVinculo, Integer matricula, String nomeServidor,
			Integer codigoCCAtuacao) throws ApplicationBusinessException {

		return this.getRapServidoresDAO().pesquisarServidorCount(codigoCCLotacao, codVinculo, matricula, nomeServidor, codigoCCAtuacao);
	}

	public List<FccCentroCustos> pesquisarCentroCustosAtivosOrdemDescricao(
			Object centroCusto) {
		return getCentroCustoFacade().pesquisarCentroCustosAtivosOrdemDescricao(centroCusto);
	}

	public FccCentroCustos obterCentroCusto(FccCentroCustos codigo) throws ApplicationBusinessException {
		if (codigo == null) {
			throw new ApplicationBusinessException(
					ServidorONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		FccCentroCustos centroCusto = getCentroCustoFacade().obterFccCentroCustos(codigo.getCodigo());
		if (centroCusto == null) {
			throw new ApplicationBusinessException(
					ServidorONExceptionCode.MENSAGEM_CENTRO_CUSTO_NAO_ENCONTRADO,
					String.valueOf(codigo));
		}
		return centroCusto;
	}
	
	protected RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}
	
}
