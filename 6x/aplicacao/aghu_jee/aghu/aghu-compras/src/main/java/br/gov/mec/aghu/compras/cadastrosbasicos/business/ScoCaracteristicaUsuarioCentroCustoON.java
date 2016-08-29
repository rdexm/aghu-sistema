package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoCaracteristicaDAO;
import br.gov.mec.aghu.compras.dao.ScoCaracteristicaUsuarioCentroCustoDAO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoCentroCusto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCaracteristica;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoCaracteristicaUsuarioCentroCustoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoCaracteristicaUsuarioCentroCustoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoCaracteristicaUsuarioCentroCustoDAO scoCaracteristicaUsuarioCentroCustoDAO;

@Inject
private ScoCaracteristicaDAO scoCaracteristicaDAO;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = -1066215596800961383L;

	public enum ManterCaracteristicaUserCCExceptionCode implements
			BusinessExceptionCode {

		CARACTERISTICA_CENTRO_CUSTO;
	}

	public void alterar(
			ScoCaracteristicaUsuarioCentroCusto scoCaracteristicaUsuarioCentroCusto)
			throws ApplicationBusinessException {

		if (scoCaracteristicaUsuarioCentroCusto == null) {
			throw new ApplicationBusinessException(
					ManterCaracteristicaUserCCExceptionCode.CARACTERISTICA_CENTRO_CUSTO);
		}

		this.getScoCaracteristicaUsuarioCentroCustoDAO().atualizar(
				scoCaracteristicaUsuarioCentroCusto);
		this.getScoCaracteristicaUsuarioCentroCustoDAO().flush();

	}

	public void inserir(
			ScoCaracteristicaUsuarioCentroCusto scoCaracteristicaUsuarioCentroCusto)
			throws ApplicationBusinessException {

		if (scoCaracteristicaUsuarioCentroCusto == null) {
			throw new ApplicationBusinessException(
					ManterCaracteristicaUserCCExceptionCode.CARACTERISTICA_CENTRO_CUSTO);
		}

		this.getScoCaracteristicaUsuarioCentroCustoDAO().persistir(
				scoCaracteristicaUsuarioCentroCusto);
		this.getScoCaracteristicaUsuarioCentroCustoDAO().flush();

	}
	
	public void excluir(final ScoCaracteristicaUsuarioCentroCusto scoCaracteristicaUsuarioCentroCusto) throws ApplicationBusinessException {
		
		if (scoCaracteristicaUsuarioCentroCusto == null) {
			throw new ApplicationBusinessException(
					ManterCaracteristicaUserCCExceptionCode.CARACTERISTICA_CENTRO_CUSTO);
		}
		
		ScoCaracteristicaUsuarioCentroCusto car = this.getScoCaracteristicaUsuarioCentroCustoDAO().obterPorChavePrimaria(scoCaracteristicaUsuarioCentroCusto.getSeq());
		
		if (car != null) {
			this.getScoCaracteristicaUsuarioCentroCustoDAO().remover(car);
			this.getScoCaracteristicaUsuarioCentroCustoDAO().flush();
		}
	} 

	public List<ScoCaracteristicaUsuarioCentroCusto> pesquisarCaracteristicaUserCC(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc,
			final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC) {

		return this.getScoCaracteristicaUsuarioCentroCustoDAO()
				.pesquisarCaracteristicaUserCC(firstResult, maxResult,
						orderProperty, asc, caracteristicaUserCC);
	}

	public Long pesquisarCaracteristicaUserCCCount(
			final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC) {

		return this.getScoCaracteristicaUsuarioCentroCustoDAO()
				.pesquisarCaracteristicaUserCCCount(caracteristicaUserCC);
	}

	public ScoCaracteristicaUsuarioCentroCusto obterCaracteristicaUserCC(
			final Integer seq) {
        RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		return this.getScoCaracteristicaUsuarioCentroCustoDAO()
				.obterCaracteristicaUserCC(servidor,seq);
	}

	public List<ScoCaracteristica> pesquisarCaracteristicasPorCodigoOuDescricao(
			Object objPesquisa) {
		return this.getScoCaracteristicaDAO()
				.pesquisarCaracteristicasPorCodigoOuDescricao(objPesquisa);
	}
	
	public Long pesquisarCaracteristicasPorCodigoOuDescricaoCount (
			Object objPesquisa) {
		return this.getScoCaracteristicaDAO()
				.pesquisarCaracteristicasPorCodigoOuDescricaoCount(objPesquisa);
	}

	public ScoCaracteristicaUsuarioCentroCusto montarScoCaracUsuario(DominioCaracteristicaCentroCusto carac) {
		
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		ScoCaracteristica scoCar = this.getScoCaracteristicaDAO().obterCaracteristicaPorNome(carac.getCodigo());
		ScoCaracteristicaUsuarioCentroCusto car = null;
		if (scoCar != null) {
			car = new ScoCaracteristicaUsuarioCentroCusto();
			car.setCentroCusto(servidor.getCentroCustoLotacao());
			car.setServidor(servidor);
			car.setHierarquiaCcusto(DominioSimNao.N);
			car.setCaracteristica(scoCar);
			
			if (carac.equals(DominioCaracteristicaCentroCusto.PREENCHER_CC_SOLIC_RM) || 
					carac.equals(DominioCaracteristicaCentroCusto.PREENCHER_CC_SOLIC_SC) || 
					carac.equals(DominioCaracteristicaCentroCusto.PREENCHER_CC_SOLIC_SS)) {
				car.setTipoCcusto(DominioTipoCentroCusto.S);	
			} else {
				car.setTipoCcusto(DominioTipoCentroCusto.A);
			}
		}
		
		return car;
	}
	// instâncias
	protected ScoCaracteristicaUsuarioCentroCustoDAO getScoCaracteristicaUsuarioCentroCustoDAO() {
		return scoCaracteristicaUsuarioCentroCustoDAO;
	}

	protected ScoCaracteristicaDAO getScoCaracteristicaDAO() {
		return scoCaracteristicaDAO;
	}

}
