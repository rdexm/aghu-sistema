package br.gov.mec.aghu.orcamento.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndicadorParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioLimiteValorPatrimonio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoFontesXVerbaGestao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoResultVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe DAO responsável pela persistência de grupos de natureza de despesas.
 * 
 * @author mlcruz
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
public class FsoParametrosOrcamentoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FsoParametrosOrcamento> {
	
	private static final long serialVersionUID = -5369373223906797075L;
	
	private static final Log LOG = LogFactory.getLog(FsoParametrosOrcamentoDAO.class);
	
	private static final String PO = "po", M = "m", GM = "gm", CC = "cc",
			GS = "gs", S = "s", JP = "jp", NTD = "ntd", GND = "gmd", VG = "vg", CCA = "CCA";

	/**
	 * Pesquisa por parâmetros de regras orçamentárias.
	 * 
	 * @param criteria
	 * @param first
	 * @param max
	 * @return VO's de parâmetros de regras orçamentárias.
	 */
	public List<FsoParametrosOrcamentoResultVO> pesquisarParametrosOrcamentoVO(
			FsoParametrosOrcamentoCriteriaVO criteria, int first, int max,
			String orderField, Boolean orderAsc) {
		DetachedCriteria detached = getVOCriteria(criteria);
		detached.setProjection(getProjection());

		detached.setResultTransformer(Transformers
				.aliasToBean(FsoParametrosOrcamentoResultVO.class));

		return executeCriteria(detached, first, max, orderField, orderAsc);
	}
	
	public FsoParametrosOrcamento pesquisarRegraOrcamentaria(
				FsoParametrosOrcamentoCriteriaVO criteria) {
			
		// Verifica regras a partir do Centro de Custo
		FsoParametrosOrcamentoCriteriaVO copiaCriteria = new FsoParametrosOrcamentoCriteriaVO();
		
		copiaCriteria = clonarCriteria(criteria);
		copiaCriteria.setGrupoMaterial(null);
		copiaCriteria.setMaterial(null);
		copiaCriteria.setLimite(null);
		DetachedCriteria detached = getRegraOrcamentariaCentroCusto(copiaCriteria);		
		List<FsoParametrosOrcamento> lista = executeCriteria(detached);
			
		if(lista == null || lista.isEmpty()){
			
			// Zero o centro de custo para desconsiderar para os proximos filtros 
			criteria.setCentroCusto(null);		
			
			// Verifica regras a partir do MATERIAL informado, ATÉ o valor total informado 
			copiaCriteria = clonarCriteria(criteria);
			copiaCriteria.setGrupoMaterial(null);			
			copiaCriteria.setLimite(DominioLimiteValorPatrimonio.A);
			detached = getRegraOrcamentaria(copiaCriteria);		
			lista = executeCriteria(detached);
			
			if(lista == null || lista.isEmpty()){
				
				// Verifica regras a partir do MATERIAL informado, MAIOR do que o valor total inforamdo
				copiaCriteria = clonarCriteria(criteria);
				copiaCriteria.setGrupoMaterial(null);			
				copiaCriteria.setLimite(DominioLimiteValorPatrimonio.M);
				detached = getRegraOrcamentaria(copiaCriteria);		
				lista = executeCriteria(detached);				
				
				if(lista == null || lista.isEmpty()){
					
					// Verifica regras a partir do MATERIA informado, SEM PARAMETRO PARA VALOR INFORMADO
					copiaCriteria = clonarCriteria(criteria);
					copiaCriteria.setGrupoMaterial(null);			
					copiaCriteria.setLimite(null);
					detached = getRegraOrcamentaria(copiaCriteria);		
					lista = executeCriteria(detached);
					
					if(lista == null || lista.isEmpty()){
						
						// Verifica regras a partir do GRUPO DE MATERIAL informado, ATÉ o valor total informado 
						copiaCriteria = clonarCriteria(criteria);
						copiaCriteria.setLimite(DominioLimiteValorPatrimonio.A);
						detached = getRegraOrcamentaria(copiaCriteria);		
						lista = executeCriteria(detached);
						if(lista == null || lista.isEmpty()){
						
							// Verifica regras a partir do GRUPO DE MATERIAL informado, MAIOR do que o valor total informado
							copiaCriteria = clonarCriteria(criteria);
							copiaCriteria.setLimite(DominioLimiteValorPatrimonio.M);
							detached = getRegraOrcamentaria(copiaCriteria);		
							lista = executeCriteria(detached);
							
							if(lista == null || lista.isEmpty()){
								
								// Verifica regras a partir do GRUPO DE MATERIAL informado, SEM PARAMETRO PARA VALOR INFORMADO
								copiaCriteria = clonarCriteria(criteria);
								copiaCriteria.setLimite(null);
								detached = getRegraOrcamentaria(copiaCriteria);		
								lista = executeCriteria(detached);
							
								if(lista == null || lista.isEmpty()){
									
									// Verifica se existe alguma REGRA GERAL cadastrada, filtrando somente pelo tipo de solicitação SS/SC
									copiaCriteria = clonarCriteria(criteria);
									copiaCriteria.setMaterial(null);
									copiaCriteria.setGrupoMaterial(null);
									detached = getRegraOrcamentaria(copiaCriteria);		
									lista = executeCriteria(detached);
								}
							}
						}
					}
				}				
			}	
		}
		
		return lista != null && !lista.isEmpty() ? (FsoParametrosOrcamento)lista.get(0) : null;
	}

	private FsoParametrosOrcamentoCriteriaVO clonarCriteria(FsoParametrosOrcamentoCriteriaVO criteria) {
		FsoParametrosOrcamentoCriteriaVO copiaCriteria = new FsoParametrosOrcamentoCriteriaVO();
		
		copiaCriteria.setAcoes(criteria.getAcoes());
		copiaCriteria.setAplicacao(criteria.getAplicacao());
		copiaCriteria.setCentroCusto(criteria.getCentroCusto());
		copiaCriteria.setCentroCustoAplicacao(criteria.getCentroCustoAplicacao());
		copiaCriteria.setCentroCustoAplicacaoShouldBeNull(criteria.getCentroCustoAplicacaoShouldBeNull());
		copiaCriteria.setCentroCustoSolicitacao(criteria.getCentroCustoSolicitante());
		copiaCriteria.setCentroCustoSolicitacaoShouldBeNull(criteria.getCentroCustoAplicacaoShouldBeNull());
		copiaCriteria.setDataReferencia(criteria.getDataReferencia());
		copiaCriteria.setFiltro(criteria.getFiltro());
		copiaCriteria.setGrupoMaterial(criteria.getGrupoMaterial());
		copiaCriteria.setGrupoNatureza(criteria.getGrupoNatureza());
		copiaCriteria.setGrupoNaturezaAlias(criteria.getGrupoNaturezaAlias());
		copiaCriteria.setGrupoNaturezaShouldBeNull(criteria.getGrupoNaturezaShouldBeNull());
		copiaCriteria.setGrupoServico(criteria.getGrupoServico());
		copiaCriteria.setIndicador(criteria.getIndicador());
		copiaCriteria.setLimite(criteria.getLimite());
		copiaCriteria.setMaterial(criteria.getMaterial());
		copiaCriteria.setMaxResults(criteria.getMaxResults());
		copiaCriteria.setNatureza(criteria.getNatureza());
		copiaCriteria.setNaturezaAlias(criteria.getNaturezaAlias());
		copiaCriteria.setNaturezaShouldBeNull(criteria.getCentroCustoAplicacaoShouldBeNull());
		copiaCriteria.setNivel(criteria.getNivel());
		copiaCriteria.setOrder(criteria.getOrder());
		copiaCriteria.setParametro(criteria.getParametro());
		copiaCriteria.setRegra(criteria.getRegra());
		copiaCriteria.setSeq(criteria.getSeq());
		copiaCriteria.setServico(criteria.getServico());
		copiaCriteria.setSituacao(criteria.getSituacao());
		copiaCriteria.setValor(criteria.getValor());
		copiaCriteria.setVerbaGestao(criteria.getVerbaGestao());
		copiaCriteria.setVerbaGestaoShouldBeNull(criteria.getVerbaGestaoShouldBeNull());
		
		return copiaCriteria;
	}

	/**
	 * Conta parâmetros de regras orçamentárias.
	 * 
	 * @param criteria
	 *            Critério (VO's).
	 * @return Número de parâmetros.
	 */
	public Long contarParametrosOrcamentoVO(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		DetachedCriteria detached = getVOCriteria(criteria);
		//detached.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		return executeCriteriaCount(detached);
	}
	
	/**
	 * Conta parâmetros de regras orçamentárias.
	 * 
	 * @param criteria
	 * @return Número de parâmetros.
	 */
	public Long contarParametrosOrcamento(FsoParametrosOrcamentoCriteriaVO criteria) {
		return executeCriteriaCount(getCriteria(criteria));
	}

	/**
	 * Obtem parâmetro orçamentário.
	 * 
	 * @param criteria Critério.
	 * @return Parâmetro orçamentário.
	 */
	public List<FsoParametrosOrcamento> pesquisarParametrosOrcamento(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		return executeCriteria(getCriteria(criteria), 0,
				criteria.getMaxResults(), criteria.getOrder(), true);
	}
	
	/**
	 * Obtem centros de custo parametrizados.
	 * 
	 * @param criteria Critério base.
	 * @return Centros de custo parametrizados.
	 */
	public List<FccCentroCustos> pesquisarCentroCustos(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		return executeCriteria(getCentroCustoCriteria(criteria), 0,
				criteria.getMaxResults(), criteria.getOrder(), true);
	}
	
	/**
	 * Obtem centros de custo parametrizados.
	 * 
	 * @param criteria Critério base.
	 * @return Centros de custo parametrizados.
	 */
	public List<FsoVerbaGestao> pesquisarVerbasGestao(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		return executeCriteria(getVerbaGestaoCriteria(criteria), 0,
				criteria.getMaxResults(), criteria.getOrder(), true);
	}
	
	/**
	 * Contabiliza centros de custo parametrizados.
	 * 
	 * @param criteria Critério base.
	 * @return Centros de custo parametrizados.
	 */
	public Long contarCentroCustos(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		return executeCriteriaCount(getCentroCustoCriteria(criteria));
	}
	
	/**
	 * Contabiliza verbas de gestão parametrizadas.
	 * 
	 * @param criteria Critério base.
	 * @return Verbas de gestão parametrizadas.
	 */
	public Long contarVerbasGestao(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		return executeCriteriaCount(getVerbaGestaoCriteria(criteria));
	}

	private DetachedCriteria getCentroCustoCriteria(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		final String CCT = "cct";
		DetachedCriteria detached = DetachedCriteria.forClass(
				FccCentroCustos.class, CCT);
		
		FsoParametrosOrcamentoCriteriaVO clone = null;
		
		try {
			clone = criteria.cloneBasico();
		} catch (CloneNotSupportedException e) {
			LOG.error(e.getMessage());
		}
		
		DetachedCriteria subquery = getCriteria(clone);
		subquery.setProjection(Projections.id())
				.add(Restrictions.eqProperty(
						JP + "." + FccCentroCustos.Fields.CODIGO.toString(),
						detached.getAlias() + "."
								+ FccCentroCustos.Fields.CODIGO.toString()));
		
		detached.add(Subqueries.notExists(subquery));

		filter(detached, criteria.getFiltro(),
				FccCentroCustos.Fields.CODIGO.toString(),
				FccCentroCustos.Fields.DESCRICAO.toString());
		
		if (criteria.getCentroCustoAplicacao() != null) {
			detached.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO
					.toString(), criteria.getCentroCustoAplicacao().getCodigo()));
		}
		
		if (Boolean.TRUE.equals(criteria.getCentroCustoAplicacaoShouldBeNull())) {
			detached.add(Restrictions.isNull(FsoVerbaGestao.Fields.SEQ.toString()));
		}
		
		return detached;
	}

	private DetachedCriteria getGrupoNaturezaCriteria(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		final String GND = "gnd";
		DetachedCriteria detached = DetachedCriteria.forClass(
				FsoGrupoNaturezaDespesa.class, GND);

		FsoParametrosOrcamentoCriteriaVO clone = null;

		try {
			clone = criteria.cloneBasico();
		} catch (CloneNotSupportedException e) {
			LOG.error(e.getMessage());
		}

		clone.setGrupoNaturezaAlias(detached.getAlias());
		DetachedCriteria subquery = getCriteria(clone);
		subquery.setProjection(Projections.id());
		detached.add(Subqueries.notExists(subquery));

		filter(detached, criteria.getFiltro(),
				FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(),
				FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString());

		if (criteria.getGrupoNatureza() != null) {
			detached.add(Restrictions.eq(FsoGrupoNaturezaDespesa.Fields.CODIGO
					.toString(), criteria.getGrupoNatureza().getCodigo()));
		}

		if (Boolean.TRUE.equals(criteria.getGrupoNaturezaShouldBeNull())) {
			detached.add(Restrictions
					.isNull(FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()));
		}

		detached.add(Restrictions.eq(
				FsoGrupoNaturezaDespesa.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		return detached;
	}
	
	private DetachedCriteria getVerbaGestaoCriteria(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		final String VBG = "vbg";
		DetachedCriteria detached = DetachedCriteria.forClass(
				FsoVerbaGestao.class, VBG);
		
		FsoParametrosOrcamentoCriteriaVO clone = null;
		
		try {
			clone = criteria.cloneBasico();
		} catch (CloneNotSupportedException e) {
			LOG.error(e.getMessage());
		}
		
		DetachedCriteria subquery = getCriteria(clone);
		subquery.setProjection(Projections.id())
				.add(Restrictions.eqProperty(
						JP + "." + FsoVerbaGestao.Fields.SEQ.toString(),
						detached.getAlias() + "."
								+ FsoVerbaGestao.Fields.SEQ.toString()));
		
		detached.add(Subqueries.notExists(subquery));

		filter(detached, criteria.getFiltro(),
				FsoVerbaGestao.Fields.SEQ.toString(),
				FsoVerbaGestao.Fields.DESCRICAO.toString());
		
		if (criteria.getVerbaGestao() != null) {
			detached.add(Restrictions.eq(FsoVerbaGestao.Fields.SEQ.toString(),
					criteria.getVerbaGestao().getSeq()));
		}
		
		if (Boolean.TRUE.equals(criteria.getVerbaGestaoShouldBeNull())) {
			detached.add(Restrictions.isNull(FsoVerbaGestao.Fields.SEQ.toString()));
		}
		
		if (criteria.getDataReferencia() != null) {
			detached.add(Restrictions.eq(VBG + "."
					+ FsoVerbaGestao.Fields.SITUACAO.toString(),
					DominioSituacao.A));

			detached.add(getVerbaGestaoRestriction(detached, criteria,
					FsoVerbaGestao.Fields.SEQ));
		}
		
		return detached;
	}

	/**
	 * Obtem critério de busca de parâmetros orçamentários.
	 * 
	 * @param criteria Critério base.
	 * @return Critério.
	 */
	private DetachedCriteria getCriteria(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		DetachedCriteria detached = DetachedCriteria.forClass(
				FsoParametrosOrcamento.class, PO);
	
		//detached.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		// Tipo de parâmetro (SC ou SS).
		detached.add(Restrictions.eq(
				FsoParametrosOrcamento.Fields.TP_PROCESSO.toString(),
				criteria.getAplicacao()));
		
		// Parâmetro a ser obtido.
		joinParam(detached, criteria);
		
		// NÃ­vel de busca (por material, grupo, etc)
		switch (criteria.getNivel()) {
		case MATERIAL:
			detached.add(Restrictions.eq(FsoParametrosOrcamento.Fields.MATERIAL
					.toString(), criteria.getMaterial()));
			break;

		case SERVICO:
			detached.add(Restrictions.eq(FsoParametrosOrcamento.Fields.SERVICO
					.toString(), criteria.getServico()));
			break;
			
		case GRUPO_MATERIAL:
			detached.add(Subqueries.exists(DetachedCriteria
					.forClass(ScoMaterial.class, M)
					.setProjection(Projections.id())
					.createAlias(ScoMaterial.Fields.GRUPO_MATERIAL.toString(),
							GM, Criteria.INNER_JOIN)
					.add(Restrictions.eq(
							M + "." + ScoMaterial.Fields.CODIGO.toString(),
							criteria.getMaterial().getCodigo()))
					.add(Restrictions.eqProperty(GM + "."
							+ ScoGrupoMaterial.Fields.CODIGO.toString(), detached.getAlias()
							+ "."
							+ FsoParametrosOrcamento.Fields.GRUPO_MATERIAL.toString()))));
			break;
			
		case GRUPO_SERVICO:
			detached.add(Subqueries.exists(DetachedCriteria
					.forClass(ScoServico.class, S)
					.setProjection(Projections.id())
					.createAlias(ScoServico.Fields.GRUPO_SERVICO.toString(),
							GS, Criteria.INNER_JOIN)
					.add(Restrictions.eq(
							S + "." + ScoServico.Fields.CODIGO.toString(),
							criteria.getServico().getCodigo()))
					.add(Restrictions.eqProperty(GS + "."
							+ ScoGrupoServico.Fields.CODIGO.toString(), detached.getAlias()
							+ "."
							+ FsoParametrosOrcamento.Fields.GRUPO_SERVICO.toString()))));
			break;
			
		case INDICADOR:
			restrictByIndicador(detached, criteria);
			break;
			
		case GERAL:
			detached.add(Restrictions.isNull(FsoParametrosOrcamento.Fields.MATERIAL
					.toString()));
			detached.add(Restrictions.isNull(FsoParametrosOrcamento.Fields.GRUPO_MATERIAL
					.toString()));
			detached.add(Restrictions.isNull(FsoParametrosOrcamento.Fields.SERVICO
					.toString()));
			detached.add(Restrictions.isNull(FsoParametrosOrcamento.Fields.GRUPO_SERVICO
					.toString()));
			detached.add(Restrictions.isNull(FsoParametrosOrcamento.Fields.IND_GRUPO
					.toString()));
			break;
		}
		
		// Valor
		if (criteria.getValor() != null && criteria.getLimite() != null) {
			detached.add(Restrictions.eq(
					FsoParametrosOrcamento.Fields.TP_LIMITE.toString(),
					criteria.getLimite()));

			switch (criteria.getLimite()) {
			case A:
				detached.add(Restrictions.ge(
						FsoParametrosOrcamento.Fields.VLR_LIMITE_PATRIMONIO
								.toString(), criteria.getValor()));
				break;

			case M:
				detached.add(Restrictions.lt(
						FsoParametrosOrcamento.Fields.VLR_LIMITE_PATRIMONIO
								.toString(), criteria.getValor()));
				break;
			}
		} else {
			detached.add(Restrictions
					.isNull(FsoParametrosOrcamento.Fields.VLR_LIMITE_PATRIMONIO
							.toString()));
		}
		
		// Centro de custo solicitante
		if (criteria.getCentroCusto() != null) {
			detached.add(Restrictions.eq(
					FsoParametrosOrcamento.Fields.CENTRO_CUSTO.toString(),
					criteria.getCentroCusto()));
		} else {
			detached.add(Restrictions
					.isNull(FsoParametrosOrcamento.Fields.CENTRO_CUSTO
							.toString()));
		}
		
		// Situação (ex.: ativos).
		detached.add(Restrictions.eq(
				FsoParametrosOrcamento.Fields.IND_SITUACAO.toString(),
				criteria.getSituacao()));

		return detached;
	}
	
	/**
	 * Obtem objeto alvo dos parâmetros orçamentário.
	 * 
	 * @param detached Critério.
	 * @param criteria Critério base.
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void joinParam(DetachedCriteria detached, FsoParametrosOrcamentoCriteriaVO criteria) {
		switch (criteria.getParametro()) {
		case GRUPO_NATUREZA:
			joinGrupoNatureza(detached, criteria);
			break;

		case NATUREZA:
			joinNatureza(detached, criteria);
			break;

		case VERBA_GESTAO:
			detached.createAlias(
					FsoParametrosOrcamento.Fields.VERBA_GESTAO.toString(), JP,
					Criteria.LEFT_JOIN);
			detached.add(Restrictions.in(
					FsoParametrosOrcamento.Fields.ACAO_VBG.toString(),
					criteria.getAcoes()));
			
			if (criteria.getVerbaGestao() != null) {
				detached.add(Restrictions.eq(
						FsoParametrosOrcamento.Fields.VERBA_GESTAO.toString(),
						criteria.getVerbaGestao()));
			}

			if (criteria.getDataReferencia() != null) {
				Criterion isAtiva = Restrictions.eq(JP + "."
						+ FsoVerbaGestao.Fields.SITUACAO.toString(),
						DominioSituacao.A);

				Criterion comFonteVigente = getVerbaGestaoRestriction(detached,
						criteria, FsoParametrosOrcamento.Fields.VERBA_GESTAO);

				Criterion isAtivaComFonteVigente = Restrictions.and(isAtiva,
						comFonteVigente);
				
				detached.add(Restrictions.or(Restrictions
						.isNull(FsoParametrosOrcamento.Fields.VERBA_GESTAO
								.toString()), isAtivaComFonteVigente));
			}
			
			if (Boolean.TRUE.equals(criteria.getVerbaGestaoShouldBeNull())) {
				detached.add(Restrictions.isNull(detached.getAlias() + "."
						+ FsoParametrosOrcamento.Fields.VERBA_GESTAO.toString()));
			}
			
			filter(detached, criteria.getFiltro(), JP + "."
					+ FsoVerbaGestao.Fields.SEQ.toString(), JP + "."
					+ FsoVerbaGestao.Fields.DESCRICAO.toString());
			break;

		case CENTRO_CUSTO:
			detached.createAlias(
					FsoParametrosOrcamento.Fields.CENTRO_CUSTO_REFERENCIA
							.toString(), JP, Criteria.LEFT_JOIN);
			detached.add(Restrictions.in(
					FsoParametrosOrcamento.Fields.ACAO_CCT.toString(),
					criteria.getAcoes()));
			
			if (criteria.getCentroCustoAplicacao() != null) {
				detached.add(Restrictions.eq(
						FsoParametrosOrcamento.Fields.CENTRO_CUSTO_REFERENCIA
								.toString(), criteria.getCentroCustoAplicacao()));
			}
			
			if (Boolean.TRUE.equals(criteria.getCentroCustoAplicacaoShouldBeNull())) {
				detached.add(Restrictions.isNull(detached.getAlias() + "."
						+ FsoParametrosOrcamento.Fields.CENTRO_CUSTO_REFERENCIA.toString()));
			}
			
			filter(detached, criteria.getFiltro(), JP + "."
					+ FccCentroCustos.Fields.CODIGO.toString(), JP + "."
					+ FccCentroCustos.Fields.DESCRICAO.toString());
			break;
		}
	}
	
	/**
	 * Obtem parâmetro grupo de natureza de despesa.
	 * 
	 * @param detached Critério
	 * @param criteria Critério Base
	 */
	private void joinGrupoNatureza(DetachedCriteria detached, FsoParametrosOrcamentoCriteriaVO criteria) {
		// Grupo de natureza cadastrado no parâmetro. 
		detached.createAlias(FsoParametrosOrcamento.Fields.GRUPO_NATUREZA_DESPESA.toString(), JP, Criteria.LEFT_JOIN);		
		detached.add(Restrictions.in(FsoParametrosOrcamento.Fields.ACAO_GND.toString(), criteria.getAcoes()));
		String inGrpFld = detached.getAlias() + '.' + FsoParametrosOrcamento.Fields.IS_CADASTRADA_GRUPO.toString();
		
		Criterion gndInParam = Restrictions.and(
				Restrictions.or(Restrictions.eq(inGrpFld, false), Restrictions.isNull(inGrpFld)),
				getGrupoNaturezaRestriction(criteria, JP));
		
		Junction junction = Restrictions.disjunction().add(gndInParam);
		
		switch (criteria.getAplicacao()) {
		case SC:
			Criterion ntdInGrpMat = Restrictions.and(
					Restrictions.eq(inGrpFld, true),
					Subqueries.exists(getNaturezaInGrupoMaterialCriteria(criteria)));
			
			junction.add(ntdInGrpMat);
			break;
			
		case SS:			
			Criterion gndInSrv = Restrictions.and(
					Restrictions.eq(inGrpFld, true),
					Subqueries.exists(getGrupoNaturezaInServicoCriteria(criteria)));
			
			junction.add(gndInSrv);
			break;
		}
		
		detached.add(junction);
	}
	
	/**
	 * Obtem criteria de busca por grupo de natureza cadastrado no serviço.
	 *  
	 * @param criteria Critério Base
	 * @return Criteria
	 */
	private DetachedCriteria getGrupoNaturezaInServicoCriteria(FsoParametrosOrcamentoCriteriaVO criteria) {
		final String PARAM_SRV = "PARAM_SRV", PARAM_SRV_NTD = "PARAM_SRV_NTD", PARAM_SRV_NTD_GND = "PARAM_SRV_NTD_GND";
		
		DetachedCriteria detached = DetachedCriteria.forClass(ScoServico.class, PARAM_SRV)
			.setProjection(Projections.id())
			.createAlias(PARAM_SRV + '.' + ScoServico.Fields.NATUREZA_DESPESA, PARAM_SRV_NTD, Criteria.LEFT_JOIN)
			.createAlias(PARAM_SRV_NTD + '.' + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA, PARAM_SRV_NTD_GND, Criteria.LEFT_JOIN)
			.add(getGrupoNaturezaRestriction(criteria, PARAM_SRV_NTD_GND))
			.add(getNaturezaRestriction(criteria, PARAM_SRV_NTD))
			.add(Restrictions.eq(PARAM_SRV + '.' + ScoServico.Fields.CODIGO.toString(), criteria.getServico().getCodigo()));
		
		return detached;
	}
	
	/**
	 * Obtem restrição por grupo de natureza de despesa.
	 * 
	 * @param criteria
	 *            Critério Base
	 * @param gndField
	 *            Campo Grupo Natureza de Despesa
	 * @return Restrição
	 */
	private Criterion getGrupoNaturezaRestriction(FsoParametrosOrcamentoCriteriaVO criteria, String gndField) {
		Conjunction con = Restrictions.conjunction();
		
		if (Boolean.TRUE.equals(criteria.getGrupoNaturezaShouldBeNull())) {
			con.add(Restrictions.isNull(gndField + '.' + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()));
		} else {
			SimpleExpression isAtivo = Restrictions.eq(gndField + '.'
					+ FsoGrupoNaturezaDespesa.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A);
			
			if (criteria.getGrupoNatureza() != null) {
				con.add(Restrictions.eq(gndField + '.' + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), 
								criteria.getGrupoNatureza().getCodigo()));
				
				con.add(isAtivo);
			} else if (criteria.getGrupoNaturezaAlias() != null) {
				con.add(Restrictions.eqProperty(gndField + '.' + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), 
								criteria.getGrupoNaturezaAlias() + '.' + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()));
				
				con.add(isAtivo);
			} else {
				con.add(Restrictions.or(
						Restrictions.isNull(gndField + '.' + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()), 
						isAtivo));
			}
		}
		
		filter(con, criteria.getFiltro(), 
				gndField + '.' + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), 
				gndField + '.' + FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString());
		
		return con;
	}

	/**
	 * Filtra Criteria por ID e/ou descrição.
	 * 
	 * @param criteria
	 * @param filter
	 * @param idField
	 * @param descField
	 */
	private void filter(DetachedCriteria criteria, Object filter,
			String idField, String descField) {
		Criterion restriction = getFilterRestriction(filter, idField, descField);

		if (restriction != null) {
			criteria.add(restriction);
		}
	}
	
	/**
	 * Filtra Conjunction por ID e/ou descrição.
	 * 
	 * @param con
	 * @param filter
	 * @param idField
	 * @param descField
	 */
	private void filter(Conjunction con, Object filter,
			String idField, String descField) {
		Criterion restriction = getFilterRestriction(filter, idField, descField);

		if (restriction != null) {
			con.add(restriction);
		}
	}
	
	/**
	 * Obtem restrição de filtro por ID e/ou descrição.
	 * 
	 * @param filter Filtro
	 * @param idField Campo ID
	 * @param descField Campo Descrição
	 * @return Restrição
	 */
	private Criterion getFilterRestriction(Object filter, String idField, String descField) {
		if (filter == null) {
			return null;
		}

		String str = (String) filter;

		if (StringUtils.isNotBlank(str)) {
			Criterion restriction = Restrictions.ilike(descField, str,
					MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroInteger(filter)) {
				restriction = Restrictions.or(restriction,
						Restrictions.eq(idField, Integer.valueOf(str)));
			}

			return restriction;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void joinNatureza(DetachedCriteria detached,
			FsoParametrosOrcamentoCriteriaVO criteria) {
		// Natureza cadastrado no parâmetro. 
		detached.createAlias(FsoParametrosOrcamento.Fields.NATUREZA_DESPESA.toString(), JP, Criteria.LEFT_JOIN);		
		detached.add(Restrictions.in(FsoParametrosOrcamento.Fields.ACAO_NTD.toString(), criteria.getAcoes()));
		
		String inGrpFld = detached.getAlias() + '.' + FsoParametrosOrcamento.Fields.IS_CADASTRADA_GRUPO.toString();
		
		Junction ntdInParam = Restrictions.conjunction()
				.add(Restrictions.or(Restrictions.eq(inGrpFld, false), Restrictions.isNull(inGrpFld)))
				.add(getNaturezaRestriction(criteria, JP));
		
		if (criteria.getGrupoNatureza() != null) {
			ntdInParam.add(Restrictions.eq(
					detached.getAlias() + '.' + FsoParametrosOrcamento.Fields.GRUPO_NATUREZA_DESPESA.toString(),
					criteria.getGrupoNatureza()));
		}
		
		Junction junction = Restrictions.disjunction().add(ntdInParam);
		
		switch (criteria.getAplicacao()) {
		case SC:
			Criterion ntdInGrpMat = Restrictions.and(
					Restrictions.eq(detached.getAlias() + '.' + FsoParametrosOrcamento.Fields.IS_CADASTRADA_GRUPO.toString(), true),
					Subqueries.exists(getNaturezaInGrupoMaterialCriteria(criteria)));
			
			junction.add(ntdInGrpMat);
			break;
			
		case SS:			
			Criterion gndInSrv = Restrictions.and(
					Restrictions.eq(detached.getAlias() + '.' + FsoParametrosOrcamento.Fields.IS_CADASTRADA_GRUPO.toString(), true),
					Subqueries.exists(getGrupoNaturezaInServicoCriteria(criteria)));
			
			junction.add(gndInSrv);
			break;
		}
		
		detached.add(junction);
	}
	
	/**
	 * Obtem criteria de busca por grupo de natureza cadastrado no serviço.
	 *  
	 * @param criteria Critério Base
	 * @return Criteria
	 */
	private DetachedCriteria getNaturezaInGrupoMaterialCriteria(FsoParametrosOrcamentoCriteriaVO criteria) {
		final String PARAM_NTD = "PARAM_NTD", PARAM_MAT = "PARAM_MAT", PARAM_MAT_GRP = "PARAM_MAT_GRP";
		
		DetachedCriteria ntdInGrpMat = DetachedCriteria.forClass(ScoMaterial.class, PARAM_MAT)
				.setProjection(Projections.id())
				.createAlias(PARAM_MAT + '.' + ScoMaterial.Fields.GRUPO_MATERIAL, PARAM_MAT_GRP)
				.add(Restrictions.eq(
						PARAM_MAT + '.' + ScoMaterial.Fields.CODIGO.toString(),
						criteria.getMaterial().getCodigo()))
				.add(Restrictions.eqProperty(
						PARAM_MAT_GRP + '.' + ScoGrupoMaterial.Fields.NTD_CODIGO.toString(), 
						PARAM_NTD + '.' + FsoNaturezaDespesa.Fields.CODIGO.toString()));
		
		DetachedCriteria detached = DetachedCriteria.forClass(FsoNaturezaDespesa.class, PARAM_NTD)
				.setProjection(Projections.property(FsoNaturezaDespesa.Fields.CODIGO.toString()))
				.add(getNaturezaRestriction(criteria, PARAM_NTD))
				.add(Subqueries.exists(ntdInGrpMat));
		
		if (criteria.getNatureza() != null) {
			detached.add(Restrictions.eq(
					PARAM_NTD + '.' + FsoNaturezaDespesa.Fields.CODIGO.toString(),
					criteria.getNatureza().getId().getCodigo()));
		}
		
		return detached;
	}
	
	/**
	 * Obtem restrição por grupo de natureza de despesa.
	 * 
	 * @param criteria
	 *            Critério Base
	 * @param ntdField
	 *            Campo Grupo Natureza de Despesa
	 * @return Restrição
	 */
	private Criterion getNaturezaRestriction(FsoParametrosOrcamentoCriteriaVO criteria, String ntdField) {
		Conjunction con = Restrictions.conjunction();
		
		if (Boolean.TRUE.equals(criteria.getNaturezaShouldBeNull())) {
			con.add(Restrictions.isNull(ntdField + '.' + FsoNaturezaDespesa.Fields.CODIGO.toString()));
		} else {
			SimpleExpression isAtivo = Restrictions.eq(ntdField + '.'
					+ FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A);
			
			if (criteria.getNatureza() != null) {
				con.add(Restrictions.eq(ntdField + '.' + FsoNaturezaDespesa.Fields.CODIGO.toString(), 
								criteria.getNatureza().getId().getCodigo()));
				
				con.add(isAtivo);
			} else if (criteria.getNaturezaAlias() != null) {
				con.add(Restrictions.eqProperty(ntdField + '.' + FsoNaturezaDespesa.Fields.CODIGO.toString(), 
								criteria.getNaturezaAlias() + '.' + FsoNaturezaDespesa.Fields.CODIGO.toString()));
				
				con.add(isAtivo);
			} else {
				con.add(Restrictions.or(
						Restrictions.isNull(ntdField + '.' + FsoNaturezaDespesa.Fields.CODIGO.toString()), 
						isAtivo));
			}
		}
		
		filter(con, criteria.getFiltro(), 
				ntdField + '.' + FsoNaturezaDespesa.Fields.CODIGO.toString(), 
				ntdField + '.' + FsoNaturezaDespesa.Fields.DESCRICAO.toString());
		
		return con;
	}
	
	/**
	 * Restringe verba de gestão.
	 * 
	 * @param detached Critério.
	 * @param criteria Critério base.
	 */
	private Criterion getVerbaGestaoRestriction(DetachedCriteria detached,
			FsoParametrosOrcamentoCriteriaVO criteria, Enum<?> keyField) {
		final String FVB = "fvb";		
		Date dataReferencia = DateUtil.obterDataComHoraInical(criteria
				.getDataReferencia());
		
		Criterion subquery = Subqueries.exists(DetachedCriteria
				.forClass(FsoFontesXVerbaGestao.class, FVB)
				.setProjection(Projections.id())
				.add(Restrictions.eqProperty(
						FVB + "."
								+ FsoFontesXVerbaGestao.Fields.VERBA.toString(),
						detached.getAlias() + "." + keyField.toString()))
				.add(Restrictions.le(FVB + "."
						+ FsoFontesXVerbaGestao.Fields.DT_VIG_INI.toString(),
						dataReferencia))
				.add(Restrictions.or(Restrictions.ge(FVB + "."
						+ FsoFontesXVerbaGestao.Fields.DT_VIG_FIM.toString(),
						dataReferencia), Restrictions.isNull(FVB
						+ "."
						+ FsoFontesXVerbaGestao.Fields.DT_VIG_FIM.toString()))));
		
		return subquery;
	}

	/**
	 * Restringe parâmetros por indicador do grupo de material.
	 * 
	 * @param detached Critério.
	 * @param criteria Critério base.
	 */
	private void restrictByIndicador(DetachedCriteria detached,
			FsoParametrosOrcamentoCriteriaVO criteria) {
		Criterion indP = Restrictions.and(Restrictions.eq(detached.getAlias() + "."
				+ FsoParametrosOrcamento.Fields.IND_GRUPO.toString(),
				DominioIndicadorParametrosOrcamento.P), Restrictions.eq(GM
				+ "." + ScoGrupoMaterial.Fields.IND_PATRIMONIO.toString(),
				true));
		
		Criterion indE = Restrictions.and(Restrictions.eq(detached.getAlias() + "."
				+ FsoParametrosOrcamento.Fields.IND_GRUPO.toString(),
				DominioIndicadorParametrosOrcamento.E), Restrictions.eq(GM
				+ "." + ScoGrupoMaterial.Fields.IND_ENGENHARI.toString(),
				true));

		Criterion indN = Restrictions.and(Restrictions.eq(detached.getAlias() + "."
				+ FsoParametrosOrcamento.Fields.IND_GRUPO.toString(),
				DominioIndicadorParametrosOrcamento.N), Restrictions.eq(GM
				+ "." + ScoGrupoMaterial.Fields.IND_NUTRICAO.toString(),
				true));
		
		detached.add(Subqueries.exists(DetachedCriteria
				.forClass(ScoMaterial.class, M)
				.setProjection(Projections.id())
				.createAlias(ScoMaterial.Fields.GRUPO_MATERIAL.toString(),
						GM, Criteria.INNER_JOIN)
				.add(Restrictions.eq(
						M + "." + ScoMaterial.Fields.CODIGO.toString(),
						criteria.getMaterial().getCodigo()))
				.add(Restrictions.or(indP, Restrictions.or(indE, indN)))));
	}

	/**
	 * Obtem detached criteria para VO's.
	 * 
	 * @param criteria
	 *            Critérios.
	 * @return Detached criteria.
	 */
	private DetachedCriteria getVOCriteria(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		DetachedCriteria detached = DetachedCriteria.forClass(FsoParametrosOrcamento.class, PO);

		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.GRUPO_MATERIAL.toString(), GM, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.MATERIAL.toString(), M, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.GRUPO_SERVICO.toString(),	GS, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.SERVICO.toString(), S, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.CENTRO_CUSTO.toString(), CC, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.CENTRO_CUSTO_REFERENCIA.toString(), CCA, Criteria.LEFT_JOIN);		
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.GRUPO_NATUREZA_DESPESA.toString(),	GND, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.NATUREZA_DESPESA.toString(), NTD, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.VERBA_GESTAO.toString(), VG, Criteria.LEFT_JOIN);
				
		if (criteria.getSeq() != null) {
			detached.add(Restrictions.eq(PO + "." + FsoParametrosOrcamento.Fields.SEQ.toString(),criteria.getSeq()));
		}		

		if (criteria.getAplicacao() != null) {
			detached.add(Restrictions.eq(PO + "." +	FsoParametrosOrcamento.Fields.TP_PROCESSO.toString(),criteria.getAplicacao()));
		}
		
		if (criteria.getIndicador() != null) {
			detached.add(Restrictions.eq(PO + "." +	FsoParametrosOrcamento.Fields.IND_GRUPO.toString(), criteria.getIndicador()));
		}

		if (criteria.getGrupoMaterial() != null) { 
			detached.add(Restrictions.eq(GM + "." + ScoGrupoMaterial.Fields.CODIGO.toString(), criteria.getGrupoMaterial().getCodigo()));
		}

		if (criteria.getMaterial() != null) {
			detached.add(Restrictions.eq(M + "." + ScoMaterial.Fields.CODIGO.toString(), criteria.getMaterial().getCodigo()));
		}

		if (criteria.getGrupoServico() != null) {
			detached.add(Restrictions.eq(GS + "." + ScoGrupoServico.Fields.CODIGO.toString(), criteria.getGrupoServico().getCodigo()));
		}

		if (criteria.getServico() != null) {
			detached.add(Restrictions.eq(S + "." + ScoServico.Fields.CODIGO.toString(), criteria.getServico().getCodigo()));
		}

		if (criteria.getCentroCusto() != null) {
			detached.add(Restrictions.eq(CC + "." + FccCentroCustos.Fields.CODIGO.toString(), criteria.getCentroCusto().getCodigo()));
		}

		if (criteria.getSituacao() != null) {
			detached.add(Restrictions.eq(PO + "." + FsoParametrosOrcamento.Fields.IND_SITUACAO.toString(), criteria.getSituacao()));
		}
				
		getVOCriteriaOutrosFiltros(criteria, detached);

		return detached;
	}
	
	
    private DetachedCriteria getRegraOrcamentariaCentroCusto(FsoParametrosOrcamentoCriteriaVO criteria) {
		DetachedCriteria detached = DetachedCriteria.forClass(FsoParametrosOrcamento.class, PO);

		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.GRUPO_MATERIAL.toString(), GM, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.MATERIAL.toString(), M, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.GRUPO_SERVICO.toString(), GS, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.SERVICO.toString(), S, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.CENTRO_CUSTO.toString(), CC, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.CENTRO_CUSTO_REFERENCIA.toString(), CCA, Criteria.LEFT_JOIN);		
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.GRUPO_NATUREZA_DESPESA.toString(), GND, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.NATUREZA_DESPESA.toString(), NTD, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.VERBA_GESTAO.toString(), VG, Criteria.LEFT_JOIN);
		
		
		if (criteria.getSeq() != null) {
			detached.add(Restrictions.eq(PO + "." + FsoParametrosOrcamento.Fields.SEQ.toString(), 
					criteria.getSeq()));
		}		

		if (criteria.getAplicacao() != null) {
			detached.add(Restrictions.eq(PO + "." +FsoParametrosOrcamento.Fields.TP_PROCESSO.toString(), criteria.getAplicacao()));
		}
		
		if (criteria.getIndicador() != null) {
			detached.add(Restrictions.eq(PO + "." +FsoParametrosOrcamento.Fields.IND_GRUPO.toString(), criteria.getIndicador()));
		}

		if (criteria.getGrupoServico() != null) {
			detached.add(Restrictions.eq(GS + "." + ScoGrupoServico.Fields.CODIGO.toString(), criteria.getGrupoServico().getCodigo()));
		}

		if (criteria.getServico() != null) {
			detached.add(Restrictions.eq(S + "." + ScoServico.Fields.CODIGO.toString(), criteria.getServico().getCodigo()));
		}
		
		if (criteria.getCentroCusto() != null) {
			detached.add(Restrictions.eq(CC + "." + FccCentroCustos.Fields.CODIGO.toString(), criteria.getCentroCusto().getCodigo()));
		}
		
		if (criteria.getSituacao() != null) {
			detached.add(Restrictions.eq(PO + "." + FsoParametrosOrcamento.Fields.IND_SITUACAO.toString(), criteria.getSituacao()));
		}
		
		return detached;
	}
	
	/**
	 * Obtem detached criteria para VO's.
	 * 
	 * @param criteria
	 *            Critérios.
	 * @return Detached criteria.
	 */
	private DetachedCriteria getRegraOrcamentaria(FsoParametrosOrcamentoCriteriaVO criteria) {
		DetachedCriteria detached = DetachedCriteria.forClass(FsoParametrosOrcamento.class, PO);

		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.GRUPO_MATERIAL.toString(), GM, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.MATERIAL.toString(), M, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.GRUPO_SERVICO.toString(), GS, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.SERVICO.toString(), S, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.CENTRO_CUSTO.toString(), CC, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.CENTRO_CUSTO_REFERENCIA.toString(), CCA, Criteria.LEFT_JOIN);		
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.GRUPO_NATUREZA_DESPESA.toString(), GND, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.NATUREZA_DESPESA.toString(), NTD, Criteria.LEFT_JOIN);
		detached.createAlias( PO + "." + FsoParametrosOrcamento.Fields.VERBA_GESTAO.toString(), VG, Criteria.LEFT_JOIN);			  
		
		if (criteria.getSeq() != null) {
			detached.add(Restrictions.eq(PO + "." + FsoParametrosOrcamento.Fields.SEQ.toString(), 
					criteria.getSeq()));
		}		

		if (criteria.getAplicacao() != null) {
			detached.add(Restrictions.eq(PO + "." +FsoParametrosOrcamento.Fields.TP_PROCESSO.toString(), criteria.getAplicacao()));
		}
		
		if (criteria.getIndicador() != null) {
			detached.add(Restrictions.eq(PO + "." +FsoParametrosOrcamento.Fields.IND_GRUPO.toString(), criteria.getIndicador()));
		}

		if (criteria.getGrupoMaterial() != null && criteria.getMaterial() != null) {
		
			Criterion cond1 = Restrictions.eq(GM + "." + ScoGrupoMaterial.Fields.CODIGO.toString(), criteria.getGrupoMaterial().getCodigo());
			
			Criterion cond2 = Restrictions.eq(M + "."+ ScoMaterial.Fields.CODIGO.toString(), criteria.getMaterial().getCodigo());
			
			detached.add(Restrictions.or(cond1, cond2));
			
		}else{

			if (criteria.getMaterial() != null) {
				detached.add(Restrictions.eq(M + "." + ScoMaterial.Fields.CODIGO.toString(), criteria.getMaterial().getCodigo()));
			} else {
				detached.add(Restrictions.isNull(M + "." + ScoMaterial.Fields.CODIGO.toString()));
			}
			
			if (criteria.getGrupoMaterial() != null) {
				detached.add(Restrictions.eq(GM + "." + ScoGrupoMaterial.Fields.CODIGO.toString(), criteria.getGrupoMaterial().getCodigo()));
			} else {
				detached.add(Restrictions.isNull(GM + "." + ScoGrupoMaterial.Fields.CODIGO.toString()));
			}
		}

		if (criteria.getGrupoServico() != null) {
			detached.add(Restrictions.eq(GS + "." + ScoGrupoServico.Fields.CODIGO.toString(), criteria.getGrupoServico().getCodigo()));
		}

		if (criteria.getServico() != null) {
			detached.add(Restrictions.eq(S + "." + ScoServico.Fields.CODIGO.toString(), criteria.getServico().getCodigo()));
		}
		
		if (criteria.getCentroCusto() != null) {
			detached.add(Restrictions.eq(CC + "." + FccCentroCustos.Fields.CODIGO.toString(), criteria.getCentroCusto().getCodigo()));
		} else {
			detached.add(Restrictions.isNull(CC + "." + FccCentroCustos.Fields.CODIGO.toString()));
		}
		
		if (criteria.getSituacao() != null) {
			detached.add(Restrictions.eq(PO + "." + FsoParametrosOrcamento.Fields.IND_SITUACAO.toString(), criteria.getSituacao()));
		}
		
		getRegraOutrosFiltros(criteria, detached);
		
		return detached;
	}
	
	private void getRegraOutrosFiltros(FsoParametrosOrcamentoCriteriaVO criteria, DetachedCriteria detached) {
		
		if (StringUtils.isNotBlank(criteria.getRegra())) {
			detached.add(Restrictions.ilike(PO + "." + FsoParametrosOrcamento.Fields.REGRA.toString(), criteria.getRegra(), MatchMode.ANYWHERE));
		}
		
		if (criteria.getCentroCustoAplicacao() != null) {
			detached.add(Restrictions.eq(CCA + "." + FccCentroCustos.Fields.CODIGO.toString(), criteria.getCentroCustoAplicacao().getCodigo()));
		}
		
		if (criteria.getGrupoNatureza() != null) {
			detached.add(Restrictions.eq(GND + "." + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), criteria.getGrupoNatureza().getCodigo()));
		}
		
		if (criteria.getNatureza() != null) {
			detached.add(Restrictions.eq(NTD + "." + FsoNaturezaDespesa.Fields.ID.toString(), criteria.getNatureza().getId()));
		}
		
		if (criteria.getVerbaGestao() != null) {
			detached.add(Restrictions.eq(VG + "." + FsoVerbaGestao.Fields.SEQ.toString(), criteria.getVerbaGestao().getSeq()));
		}
		
		if (criteria.getLimite() != null) {
			detached.add(Restrictions.eq(PO + "." + FsoParametrosOrcamento.Fields.TP_LIMITE.toString(), criteria.getLimite()));
			
			if (criteria.getLimite().equals(DominioLimiteValorPatrimonio.A)) {
				detached.add(Restrictions.ge(FsoParametrosOrcamento.Fields.VLR_LIMITE_PATRIMONIO.toString(), criteria.getValor()));
			} else if (criteria.getLimite().equals(DominioLimiteValorPatrimonio.M)) {
				detached.add(Restrictions.le(FsoParametrosOrcamento.Fields.VLR_LIMITE_PATRIMONIO.toString(), criteria.getValor()));
			}
		}
		
	}

	public void getVOCriteriaOutrosFiltros(FsoParametrosOrcamentoCriteriaVO criteria, DetachedCriteria detached){
		
		if (StringUtils.isNotBlank(criteria.getRegra())) {
			detached.add(Restrictions.ilike(PO + "." + FsoParametrosOrcamento.Fields.REGRA.toString(), criteria.getRegra(), MatchMode.ANYWHERE));
		}
		
		if (criteria.getCentroCustoAplicacao() != null) {
			detached.add(Restrictions.eq(CCA + "." + FccCentroCustos.Fields.CODIGO.toString(), criteria.getCentroCustoAplicacao().getCodigo()));
		}
		
		if (criteria.getGrupoNatureza() != null) {
			detached.add(Restrictions.eq(GND + "." + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), criteria.getGrupoNatureza().getCodigo()));
		}
		
		if (criteria.getNatureza() != null) {
			detached.add(Restrictions.eq(NTD + "." + FsoNaturezaDespesa.Fields.ID.toString(), criteria.getNatureza().getId()));
		}
		
		if (criteria.getVerbaGestao() != null) {
			detached.add(Restrictions.eq(VG + "." + FsoVerbaGestao.Fields.SEQ.toString(), criteria.getVerbaGestao().getSeq()));
		}
		
		if (criteria.getLimite() != null) {
			detached.add(Restrictions.eq(PO + "." + FsoParametrosOrcamento.Fields.TP_LIMITE.toString(), criteria.getLimite()));
			
			if (criteria.getLimite().equals(DominioLimiteValorPatrimonio.A)) {				
				detached.add(Restrictions.le(FsoParametrosOrcamento.Fields.VLR_LIMITE_PATRIMONIO.toString(), criteria.getValor()));
			} else if (criteria.getLimite().equals(DominioLimiteValorPatrimonio.M)) {
				detached.add(Restrictions.ge(FsoParametrosOrcamento.Fields.VLR_LIMITE_PATRIMONIO.toString(), criteria.getValor()));
			}			
		}
	}

	/**
	 * Obtem projeção para o retorno da consulta.
	 * 
	 * @return Projection.
	 */
	private Projection getProjection() {
		return Projections
				.projectionList()
				// Regra
				.add(Projections.property(FsoParametrosOrcamento.Fields.SEQ.toString()),
						FsoParametrosOrcamentoResultVO.Fields.SEQ.toString())
						
				// Nome Regra
				.add(Projections.property(FsoParametrosOrcamento.Fields.REGRA.toString()),
						FsoParametrosOrcamentoResultVO.Fields.REGRA.toString())
						
				// Descricao Regra
				.add(Projections.property(FsoParametrosOrcamento.Fields.DESCRICAO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.DESCRICAO_REGRA.toString())		

				// Indicador
				.add(Projections.property(FsoParametrosOrcamento.Fields.IND_GRUPO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.IND_GRUPO.toString())

				// Grupo de material
				.add(Projections.property(GM + "." + ScoGrupoMaterial.Fields.CODIGO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.GRUPO_MATERIAL_ID.toString())
						
				.add(Projections.property(GM + "." + ScoGrupoMaterial.Fields.DESCRICAO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.GRUPO_MATERIAL_DESC.toString())

				// Material
				.add(Projections.property(M + "." + ScoMaterial.Fields.CODIGO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.MATERIAL_ID.toString())
						
				.add(Projections.property(M + "." + ScoMaterial.Fields.NOME.toString()),
						FsoParametrosOrcamentoResultVO.Fields.MATERIAL_DESC.toString())
								
				// Grupo de Serviço
				.add(Projections.property(GS + "." + ScoGrupoServico.Fields.CODIGO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.GRUPO_SERVICO_ID.toString())
						
				.add(Projections.property(GS + "." + ScoGrupoServico.Fields.DESCRICAO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.GRUPO_SERVICO_DESC.toString())
								
				// Serviço
				.add(Projections.property(S + "." + ScoServico.Fields.CODIGO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.SERVICO_ID.toString())
				
				.add(Projections.property(S + "." + ScoServico.Fields.NOME.toString()),
						FsoParametrosOrcamentoResultVO.Fields.SERVICO_DESC.toString())

				// Limite valor patrimônio
				.add(Projections.property(FsoParametrosOrcamento.Fields.TP_LIMITE.toString()),
						FsoParametrosOrcamentoResultVO.Fields.TP_LIMITE.toString())
						
				.add(Projections.property(FsoParametrosOrcamento.Fields.VLR_LIMITE_PATRIMONIO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.VLR_LIMITE_PATRIMONIO.toString())

				// CC Aplicação
				.add(Projections.property(CC + "." + FccCentroCustos.Fields.CODIGO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.CENTRO_CUSTO_ID.toString())
				.add(Projections.property(CC + "." + FccCentroCustos.Fields.DESCRICAO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.CENTRO_CUSTO_DESC.toString())

				// Situação
				.add(Projections.property(FsoParametrosOrcamento.Fields.IND_SITUACAO.toString()),
						FsoParametrosOrcamentoResultVO.Fields.IND_SITUACAO.toString());
	}

	/**
	 * Pesquisa parâmetros com regras de mesmo foco.
	 * 
	 * @param param
	 * @return
	 */
	public List<FsoParametrosOrcamento> pesquisarParametrosOrcamento(
			FsoParametrosOrcamento param) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FsoParametrosOrcamento.class);

		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		if (param.getSeq() != null) {
			criteria.add(Restrictions.ne(
					FsoParametrosOrcamento.Fields.SEQ.toString(),
					param.getSeq()));
		}
		
		criteria.add(Restrictions.eq(
				FsoParametrosOrcamento.Fields.TP_PROCESSO.toString(),
				param.getTpProcesso()));
		
		restrictMaterial(param, criteria);
		restrictServico(param, criteria);

		if (param.getIndGrupo() != null) {
			criteria.add(Restrictions.eq(
					FsoParametrosOrcamento.Fields.IND_GRUPO.toString(),
					param.getIndGrupo()));
		} else {
			criteria.add(Restrictions
					.isNull(FsoParametrosOrcamento.Fields.IND_GRUPO.toString()));
		}

		if (param.getTpLimite() != null) {
			SimpleExpression tpLimiteExpr = Restrictions.eq(
					FsoParametrosOrcamento.Fields.TP_LIMITE.toString(),
					param.getTpLimite()), vlrExpr = null;

			switch (param.getTpLimite()) {
			case A:
				vlrExpr = Restrictions.lt(
						FsoParametrosOrcamento.Fields.VLR_LIMITE_PATRIMONIO
								.toString(), param.getVlrLimitePatrimonio());

				break;

			case M:
				vlrExpr = Restrictions.gt(
						FsoParametrosOrcamento.Fields.VLR_LIMITE_PATRIMONIO
								.toString(), param.getVlrLimitePatrimonio());

				break;
			}

			criteria.add(Restrictions.or(tpLimiteExpr, vlrExpr));
		}

		if (param.getCentroCusto() != null) {
			criteria.add(Restrictions.eq(
					FsoParametrosOrcamento.Fields.CENTRO_CUSTO.toString(),
					param.getCentroCusto()));
		} else {
			criteria.add(Restrictions
					.isNull(FsoParametrosOrcamento.Fields.CENTRO_CUSTO
							.toString()));
		}

		criteria.add(Restrictions.eq(
				FsoParametrosOrcamento.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		return executeCriteria(criteria);
	}

	public List<FsoGrupoNaturezaDespesa> pesquisarGruposNatureza(
			FsoParametrosOrcamentoCriteriaVO criteria) {		
		return executeCriteria(getGrupoNaturezaCriteria(criteria), 0,
				criteria.getMaxResults(), criteria.getOrder(), true);
	}

	/**
	 * Contabiliza naturezas parametrizados.
	 * 
	 * @param criteria Critério base.
	 * @return Grupos de natureza parametrizados.
	 */
	public Long contarNaturezas(FsoParametrosOrcamentoCriteriaVO criteria) {
		return executeCriteriaCount(getNaturezaCriteria(criteria));
	}

	public List<FsoNaturezaDespesa> pesquisarNaturezas(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		return executeCriteria(getNaturezaCriteria(criteria), 0,
				criteria.getMaxResults(), criteria.getOrder(), true);
	}
	
	private DetachedCriteria getNaturezaCriteria(FsoParametrosOrcamentoCriteriaVO criteria) {
		DetachedCriteria detached = DetachedCriteria.forClass(
				FsoNaturezaDespesa.class, NTD);
		
		FsoParametrosOrcamentoCriteriaVO clone = null;
		
		try {
			clone = criteria.cloneBasico();
		} catch (CloneNotSupportedException e) {
			LOG.error(e.getMessage());
		}
		
		clone.setNaturezaAlias(detached.getAlias());
		detached.add(Subqueries.notExists(getCriteria(clone).setProjection(
				Projections.id())));

		Object filtro = criteria.getFiltro();
		String filtroStr = (String) filtro;

		if (StringUtils.isNotBlank(filtroStr)) {
			Criterion restriction = Restrictions.ilike(
					FsoNaturezaDespesa.Fields.DESCRICAO.toString(), filtroStr,
					MatchMode.ANYWHERE);
			
			if (CoreUtil.isNumeroByte(filtro)) {
				restriction = Restrictions.or(restriction, Restrictions.eq(
						FsoNaturezaDespesa.Fields.CODIGO.toString(),
						Byte.valueOf(filtroStr)));
			}

			detached.add(restriction);
		}

		if (criteria.getGrupoNatureza() != null) {
			detached.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GRUPO_NATUREZA
					.toString(), criteria.getGrupoNatureza()));
		}

		if (criteria.getNatureza() != null) {
			detached.add(Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO
					.toString(), criteria.getNatureza().getId().getCodigo()));
		}
		
		if (Boolean.TRUE.equals(criteria.getNaturezaShouldBeNull())) {
			detached.add(Restrictions.isNull(FsoNaturezaDespesa.Fields.CODIGO
					.toString()));
		}

		detached.add(Restrictions.eq(
				FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		return detached;
	}

	/**
	 * Restringe por grupo e serviço.
	 * 
	 * @param param
	 * @param criteria
	 */
	private void restrictServico(FsoParametrosOrcamento param,
			DetachedCriteria criteria) {
		if (param.getServico() != null) {
			criteria.add(Restrictions.eq(
					FsoParametrosOrcamento.Fields.SERVICO.toString(),
					param.getServico()));
		} else {
			criteria.add(Restrictions
					.isNull(FsoParametrosOrcamento.Fields.SERVICO.toString()));
		}

		if (param.getGrupoServico() != null) {
			criteria.add(Restrictions.eq(
					FsoParametrosOrcamento.Fields.GRUPO_SERVICO.toString(),
					param.getGrupoServico()));
		} else {
			criteria.add(Restrictions
					.isNull(FsoParametrosOrcamento.Fields.GRUPO_SERVICO
							.toString()));
		}
	}

	/**
	 * Restringe por grupo e material.
	 * 
	 * @param param
	 * @param criteria
	 */
	private void restrictMaterial(FsoParametrosOrcamento param,
			DetachedCriteria criteria) {
		if (param.getGrupoMaterial() != null) {
			criteria.add(Restrictions.eq(
					FsoParametrosOrcamento.Fields.GRUPO_MATERIAL.toString(),
					param.getGrupoMaterial()));
		} else {
			criteria.add(Restrictions
					.isNull(FsoParametrosOrcamento.Fields.GRUPO_MATERIAL
							.toString()));
		}
		
		if (param.getMaterial() != null) {
			criteria.add(Restrictions.eq(
					FsoParametrosOrcamento.Fields.MATERIAL.toString(),
					param.getMaterial()));
		} else {
			criteria.add(Restrictions
					.isNull(FsoParametrosOrcamento.Fields.MATERIAL.toString()));
		}
	}
	
	public List<FsoParametrosOrcamento> pesquisarVerbaGestaoAssociadaParametroOrcamento(FsoVerbaGestao verbaGestao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FsoParametrosOrcamento.class);

	//	criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
	//	criteria.getExecutableCriteria(getSession()).setMaxResults(10);
		criteria.add(Restrictions.eq(FsoParametrosOrcamento.Fields.VERBA_GESTAO.toString(), verbaGestao));
		
		criteria.addOrder(Order.asc(FsoParametrosOrcamento.Fields.SEQ.toString()));
		
		return executeCriteria(criteria,0,10,null);
	}

	public FsoNaturezaDespesa pesquisarNaturezaScGrupoMaterial(
			ScoMaterial material,BigDecimal paramVlrNumerico) {
			
		StringBuilder hql = new StringBuilder(400);
		hql.append(" select ntd from FsoNaturezaDespesa ntd, ");
		hql.append("                 FsoGrupoNaturezaDespesa gntd ");
		hql.append("           where ntd.id.codigo in ( select gm.ntdCodigo "); 
		hql.append("                                      from ScoGrupoMaterial gm ");
		hql.append("                                     where gm.codigo = :gm_codigo ) ");
		hql.append("             and ntd.grupoNaturezaDespesa.codigo = gntd.codigo ");
		hql.append("             and gntd.codigo = :gntd_codigo");
		
		Query consulta = this.createQuery(hql.toString());
		
		consulta.setParameter("gm_codigo", material.getGrupoMaterial().getCodigo());
		consulta.setParameter("gntd_codigo", Integer.valueOf(paramVlrNumerico.toString()));
		
		List<FsoNaturezaDespesa> lista = consulta.getResultList();
		
		return lista != null && !lista.isEmpty() ? (FsoNaturezaDespesa)lista.get(0) : null; 
		
	}
}