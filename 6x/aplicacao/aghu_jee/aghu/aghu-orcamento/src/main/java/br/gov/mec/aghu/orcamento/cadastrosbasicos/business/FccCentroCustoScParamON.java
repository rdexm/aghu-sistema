package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * ON de Parâmetros Orçamentários de Centros de Custo de SC
 * 
 * @author mlcruz
 */
@Stateless
public class FccCentroCustoScParamON extends BaseBusiness {


@EJB
private FsoParametrosOrcamentoFilterON fsoParametrosOrcamentoFilterON;

@EJB
private FsoParametrosOrcamentoScFinderON fsoParametrosOrcamentoScFinderON;

@EJB
private FsoParametrosOrcamentoON fsoParametrosOrcamentoON;

private static final Log LOG = LogFactory.getLog(FccCentroCustoScParamON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;

@EJB
private ICentroCustoFacade centroCustoFacade;
	private static final long serialVersionUID = -6787903710324940617L;

	/**
	 * Obtem centro de custo aplicação parâmetro conforme SC.
	 * 
	 * @param material Material.
	 * @param centroCusto Centro de custo solicitante.
	 * @param valor Valor da SC.
	 * @return Centro de custo aplicação parâmetro.
	 */
	public FccCentroCustos getCentroCustoScParam(
			ScoMaterial material, FccCentroCustos centroCusto, BigDecimal valor) {
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();
		
		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.CENTRO_CUSTO);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		
		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);
		criteria.setMaxResults(2);
		
		FsoParametrosOrcamento param = getFsoParametrosOrcamentoScFinderON()
				.getResult(criteria, getFsoParametrosOrcamentoFilterON());

		return param != null ? param.getCentroCustoReferencia() : null;
	}

	/**
	 * Indica se centro de custo possui parâmetro obrigatório Ãºnico.
	 * 
	 * @param material Material.
	 * @param centroCusto Centro de custo solicitante.
	 * @param valor Valor da SC.
	 * @return Indicador.
	 */
	public Boolean hasUniqueRequiredCentroCustoScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();
		
		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.CENTRO_CUSTO);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		
		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);
		
		criteria.setMaxResults(2);
		
		FsoParametrosOrcamentoFilterStrategy<Boolean> strategy = 
				new FsoParametrosOrcamentoFilterStrategy<Boolean>() {			
			@Override
			public Boolean find(FsoParametrosOrcamentoCriteriaVO criteria) {
				Boolean isUnique = null;
				Long count = getFsoParametrosOrcamentoDAO()
						.contarParametrosOrcamento(criteria);
				
				if (count > 0) {
					if (count.equals(1)) {
						isUnique = getFsoParametrosOrcamentoDAO()
								.pesquisarParametrosOrcamento(criteria).get(0)
								.getAcaoCct()
								.equals(DominioAcaoParametrosOrcamento.O);
					} else {
						isUnique = false;
					}
				}
				
				return isUnique;
			}
		};
		
		Boolean flag = getFsoParametrosOrcamentoScFinderON().getResult(criteria, strategy);
		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Indica se o centro de custo aplicação é válido.
	 * 
	 * @param material Material.
	 * @param ccSolicitante Centro de custo solicitante.
	 * @param valor Valor da SC.
	 * @param ccAplicacao Centro de custo aplicação.
	 * @return Indicador.
	 */
	public Boolean isCentroCustoValidScParam(
			ScoMaterial material, FccCentroCustos ccSolicitante,
			BigDecimal valor, FccCentroCustos ccAplicacao) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();
		
		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.CENTRO_CUSTO);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(ccSolicitante);
		criteria.setCentroCustoAplicacao(ccAplicacao);
		criteria.setCentroCustoAplicacaoShouldBeNull(ccAplicacao == null);
		criteria.setMaxResults(1);
		
		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON().
				getAcaoScParam(material, ccSolicitante, valor, 
						FsoParametrosOrcamentoCriteriaVO.Parametro.CENTRO_CUSTO);
		
		if (acaoParam != null) {
			// Se existe apenas um parâmetro de sugestão, então qualquer valor é
			// válido.
			if (DominioAcaoParametrosOrcamento.S.equals(acaoParam.getAcaoCct())) {
				return true;
			}

			Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
			acoes.add(acaoParam.getAcaoCct());
			criteria.setAcoes(acoes);
		}
		
		// Strategy
		FsoParametrosOrcamentoFilterStrategy<Boolean> strategy = 
				new FsoParametrosOrcamentoFilterStrategy<Boolean>() {			
			@Override
			public Boolean find(FsoParametrosOrcamentoCriteriaVO criteria) {
				Boolean isValid = null;
				
				if (criteria.getAcao() == null) {
					isValid = true;
				} else {
					FsoParametrosOrcamentoCriteriaVO clone = null;

					try {
						clone = criteria.cloneBasico();
					} catch (CloneNotSupportedException e) {
						logError(e.getMessage());
					}

					if (getFsoParametrosOrcamentoDAO()
							.contarParametrosOrcamento(clone) > 0) {
						if (DominioAcaoParametrosOrcamento.R.equals(criteria
								.getAcao())) {
							if (criteria.getCentroCustoAplicacao() != null) {
								isValid = !getFsoParametrosOrcamentoDAO()
										.contarCentroCustos(criteria).equals(0);
							} else {
								isValid = true;
							}
						} else {
							isValid = !getFsoParametrosOrcamentoDAO()
									.contarParametrosOrcamento(criteria)
									.equals(0);
						}
					}
				}
				
				return isValid;
			}
		};
		
		Boolean flag = getFsoParametrosOrcamentoScFinderON().getResult(criteria, strategy);
		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Obtem centros de custo parametrizados.
	 * 
	 * @param material Material.
	 * @param centroCusto Centro de custo solicitante.
	 * @param valorTotal Valor da SC.
	 * @param filter Filtro do centro de custo por ID ou descrição.
	 * @return Centros de custo parametrizados.
	 */
	public List<FccCentroCustos> listarCentroCustosScParams(
			ScoMaterial material, FccCentroCustos centroCusto,
			BigDecimal valor, Object filter) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();
		
		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.CENTRO_CUSTO);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		criteria.setFiltro(filter);
		criteria.setMaxResults(100);
		
		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoScParam(material, centroCusto, valor, 
						FsoParametrosOrcamentoCriteriaVO.Parametro.CENTRO_CUSTO);
		
		if (acaoParam != null) {
			Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
			acoes.add(acaoParam.getAcaoCct());
			criteria.setAcoes(acoes);
		}

		criteria.setOrder(DominioAcaoParametrosOrcamento.O.equals(criteria
				.getAcao()) ? FsoParametrosOrcamento.Fields.CENTRO_CUSTO.toString()
				: FccCentroCustos.Fields.CODIGO.toString());

		// Strategy
		FsoParametrosOrcamentoFilterStrategy<List<FccCentroCustos>> strategy = 
				new FsoParametrosOrcamentoFilterStrategy<List<FccCentroCustos>>() {			
			@Override
			public List<FccCentroCustos> find(FsoParametrosOrcamentoCriteriaVO criteria) {
				List<FccCentroCustos> result = null;
				
				if (criteria.getAcao() == null
						|| DominioAcaoParametrosOrcamento.S.equals(criteria
								.getAcao())) {
					logInfo("Não foram encontrados centros de custo parametrizados.");
					
					result = getCentroCustoFacade()
							.pesquisarCentroCustosAtivosOrdemDescricao(criteria.getFiltro());
				} else {
					FsoParametrosOrcamentoCriteriaVO clone = null;

					try {
						clone = criteria.cloneBasico();
					} catch (CloneNotSupportedException e) {
						logError(e.getMessage());
					}

					if (getFsoParametrosOrcamentoDAO()
							.contarParametrosOrcamento(clone) > 0) {
						if (DominioAcaoParametrosOrcamento.O.equals(criteria
								.getAcao())) {
							result = new ArrayList<FccCentroCustos>();

							for (FsoParametrosOrcamento cct : getFsoParametrosOrcamentoDAO()
									.pesquisarParametrosOrcamento(criteria)) {
								logInfo(String
										.format("Encontrado centro de custo a partir do parâmetro %d.",
												cct.getSeq()));
								
								result.add(cct.getCentroCustoReferencia());
							}
						} else {
							logInfo("Obtendo centros de custo não restringidos.");
							
							result = getFsoParametrosOrcamentoDAO()
									.pesquisarCentroCustos(criteria);
						}
					}
				}
				
				return result != null && !result.isEmpty() ? result : null;
			}
		};
		
		return getFsoParametrosOrcamentoScFinderON().getResult(criteria, strategy);
	}

	protected FsoParametrosOrcamentoDAO getFsoParametrosOrcamentoDAO() {
		return fsoParametrosOrcamentoDAO;
	}
	
	protected FsoParametrosOrcamentoON getFsoParametrosOrcamentoON() {
		return fsoParametrosOrcamentoON;
	}
	
	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}
	
	protected FsoParametrosOrcamentoScFinderON getFsoParametrosOrcamentoScFinderON() {
		return fsoParametrosOrcamentoScFinderON;
	}

	protected FsoParametrosOrcamentoFilterON getFsoParametrosOrcamentoFilterON() {
		return fsoParametrosOrcamentoFilterON;
	}
}