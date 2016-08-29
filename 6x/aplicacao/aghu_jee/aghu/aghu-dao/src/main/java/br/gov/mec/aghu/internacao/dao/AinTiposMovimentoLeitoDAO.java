package br.gov.mec.aghu.internacao.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AinTiposMovimentoLeitoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinTiposMovimentoLeito> {

	private static final long serialVersionUID = 7494522740377310338L;

	/**
	 * Obter descricao Tipo de Mvto por Código.
	 * 
	 * @param codBloq
	 * @return
	 */
	public String obterDescTipoMvtoPorCodigo(Short codBloq) {
		return (String) this.createQuery(
						"select descricao from AinTiposMovimentoLeito where codigo = " + codBloq)
				.getSingleResult();

	}
	
	
	/**
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param grupoMvtoLeito
	 * @param indNecessitaLimpeza
	 * @param indExigeJustificativa
	 * @param indBloqueioPaciente
	 * @param indExigeJustLiberacao
	 * @return
	 */
	public List<AinTiposMovimentoLeito> pesquisa(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Short codigo, String descricao,
			DominioMovimentoLeito grupoMvtoLeito,
			DominioSimNao indNecessitaLimpeza,
			DominioSimNao indExigeJustificativa,
			DominioSimNao indBloqueioPaciente,
			DominioSimNao indExigeJustLiberacao) {
		DetachedCriteria criteria = createPesquisaCriteria(codigo, descricao,
				grupoMvtoLeito, indNecessitaLimpeza, indExigeJustificativa,
				indBloqueioPaciente, indExigeJustLiberacao);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	/**
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @param descricao
	 * @param grupoMvtoLeito
	 * @param indNecessitaLimpeza
	 * @param indExigeJustificativa
	 * @param indBloqueioPaciente
	 * @param indExigeJustLiberacao
	 * @return
	 */
	public Long pesquisaCount(Short codigo, String descricao,
			DominioMovimentoLeito grupoMvtoLeito,
			DominioSimNao indNecessitaLimpeza,
			DominioSimNao indExigeJustificativa,
			DominioSimNao indBloqueioPaciente,
			DominioSimNao indExigeJustLiberacao) {
		return executeCriteriaCount(createPesquisaCriteria(codigo, descricao,
				grupoMvtoLeito, indNecessitaLimpeza, indExigeJustificativa,
				indBloqueioPaciente, indExigeJustLiberacao));
	}

	private DetachedCriteria createPesquisaCriteria(Short codigo,
			String descricao, DominioMovimentoLeito grupoMvtoLeito,
			DominioSimNao indNecessitaLimpeza,
			DominioSimNao indExigeJustificativa,
			DominioSimNao indBloqueioPaciente,
			DominioSimNao indExigeJustLiberacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposMovimentoLeito.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					AinTiposMovimentoLeito.Fields.CODIGO.toString(),
					codigo.shortValue()));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AinTiposMovimentoLeito.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		if (grupoMvtoLeito != null) {
			criteria.add(Restrictions.eq(
					AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO
							.toString(), grupoMvtoLeito));
		}

		if (indNecessitaLimpeza != null) {
			criteria.add(Restrictions.eq(
					AinTiposMovimentoLeito.Fields.NECESSITA_LIMPEZA.toString(),
					indNecessitaLimpeza));
		}

		if (indExigeJustificativa != null) {
			criteria.add(Restrictions.eq(
					AinTiposMovimentoLeito.Fields.EXIGE_JUSTIFICATIVA
							.toString(), indExigeJustificativa));
		}

		if (indBloqueioPaciente != null) {
			criteria.add(Restrictions.eq(
					AinTiposMovimentoLeito.Fields.BLOQUEIO_PACIENTE.toString(),
					indBloqueioPaciente));
		}

		if (indExigeJustLiberacao != null) {
			criteria.add(Restrictions.eq(
					AinTiposMovimentoLeito.Fields.EXIGE_JUSTIFICATIVA_LIBERACAO
							.toString(), indExigeJustLiberacao));
		}

		return criteria;
	}
	
	
	public List<AinTiposMovimentoLeito> getTiposSituacaoLeitoComMesmoCodigo(AinTiposMovimentoLeito tipoSituacaoLeito) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMovimentoLeito.class);

		criteria.add(Restrictions.eq(AinTiposMovimentoLeito.Fields.CODIGO.toString(), tipoSituacaoLeito.getCodigo()));

		return this.executeCriteria(criteria);
	}

	public List<AinTiposMovimentoLeito> getTiposSituacaoLeitoComMesmaDescricao(AinTiposMovimentoLeito tipoSituacaoLeito) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMovimentoLeito.class);

		criteria.add(Restrictions.ilike(AinTiposMovimentoLeito.Fields.DESCRICAO.toString(), tipoSituacaoLeito.getDescricao(), MatchMode.EXACT));

		// Garante que não será comparado a descrição do tipo de situação de
		// leito sendo editado
		if (tipoSituacaoLeito.getCodigo() != null) {
			criteria.add(Restrictions.ne(AinTiposMovimentoLeito.Fields.CODIGO.toString(),
					tipoSituacaoLeito.getCodigo()));
		}

		return this.executeCriteria(criteria);
	}

	/**
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	public AinTiposMovimentoLeito obterTipoSituacaoLeito(Short codigo) {
		AinTiposMovimentoLeito retorno = this.obterPorChavePrimaria(codigo);
		return retorno;
	}
	
	
	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com o codigo passado por parametro.<br>
	 * E grupoMovimentoLeito: L.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	public AinTiposMovimentoLeito pesquisarTipoSituacaoLeitoDesocupado(Short codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMovimentoLeito.class);

		criteria.add(Restrictions.eq(
				AinTiposMovimentoLeito.Fields.CODIGO.toString(), codigo));
		criteria.add(Restrictions.eq(
				AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
				DominioMovimentoLeito.L));

		return (AinTiposMovimentoLeito) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoLeito: L.<br>
	 * Ordenado por grupoMovimentoLeito.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoDesocupadosPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMovimentoLeito.class);

		if (descricao != null && StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AinTiposMovimentoLeito.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(
				AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
				DominioMovimentoLeito.L));

		criteria.addOrder(Order
				.asc(AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO
						.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna AinTiposMovimentoLeito para Tipos de Reserva.
	 * Filtros: codigo<br>
	 * grupoMovimentoLeito: L.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	public AinTiposMovimentoLeito pesquisarTipoSituacaoLeitoReservados(Short codigo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposMovimentoLeito.class);

		criteria.add(Restrictions.eq(
				AinTiposMovimentoLeito.Fields.CODIGO.toString(), codigo));

		criteria.add(Restrictions.eq(
				AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
				DominioMovimentoLeito.R));

		return (AinTiposMovimentoLeito) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Retorna AinTiposMovimentoLeito para Tipos de Reserva.<br>
	 * Filtros: Descricao,<br>
	 * grupoMovimentoLeito: R<br>
	 * Ordenado por Grupo movimento Leito.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoReservadosPorDescricao(String descricao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposMovimentoLeito.class);

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AinTiposMovimentoLeito.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq(
				AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
				DominioMovimentoLeito.R));

		criteria.addOrder(Order
				.asc(AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO
						.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com o codigo passado por parametros.<br>
	 * E grupoMovimentoSituacao: B, BI, BL, D, O, L, R.<br> 
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	public AinTiposMovimentoLeito pesquisarTipoSituacaoLeitoPorCodigo(Short codigo) {
		if (codigo == null) {
			throw new IllegalArgumentException("Parametro obrigatorio para a pesquisa nao informado!!!");
		}
		
		List<DominioMovimentoLeito> listaDominioMovimentoLeito = Arrays.asList(
				DominioMovimentoLeito.B,
				DominioMovimentoLeito.BI,
				DominioMovimentoLeito.BL,
				DominioMovimentoLeito.D,
				DominioMovimentoLeito.O,
				DominioMovimentoLeito.L,
				DominioMovimentoLeito.R
		);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMovimentoLeito.class);
		
		criteria.add(Restrictions.eq(AinTiposMovimentoLeito.Fields.CODIGO.toString(), codigo));
		
		criteria.add(Restrictions.in(AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), listaDominioMovimentoLeito));
		
		return (AinTiposMovimentoLeito) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoSituacao: B, BI, BL, D, O, L, R.<br>
	 * Ordenado por codigo.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMovimentoLeito.class);
		
		if (descricao != null) {
			if (CoreUtil.isNumeroShort(descricao)) {
				criteria.add(Restrictions.eq(AinTiposMovimentoLeito.Fields.CODIGO.toString(), Short.valueOf(descricao)));
			} else if (StringUtils.isNotBlank(descricao)) {
				criteria.add(Restrictions.ilike(AinTiposMovimentoLeito.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
			}
		}
		
		
		List<DominioMovimentoLeito> listaDominioMovimentoLeito = Arrays.asList(
				DominioMovimentoLeito.B,
				DominioMovimentoLeito.BI,
				DominioMovimentoLeito.BL,
				DominioMovimentoLeito.D,
				DominioMovimentoLeito.O,
				DominioMovimentoLeito.L,
				DominioMovimentoLeito.R
		);
		criteria.add(Restrictions.in(AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), listaDominioMovimentoLeito));
		
		criteria.addOrder(Order.asc(AinTiposMovimentoLeito.Fields.CODIGO.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com o codigo passado por parametro.<br>
	 * E grupoMovimentoLeito: B, BI, BL, D.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	public AinTiposMovimentoLeito pesquisarTipoSituacaoLeitoBloqueados(Short codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMovimentoLeito.class);

		ArrayList<DominioMovimentoLeito> lista = new ArrayList<DominioMovimentoLeito>();
		lista.add(DominioMovimentoLeito.B);
		lista.add(DominioMovimentoLeito.BI);
		lista.add(DominioMovimentoLeito.BL);
		lista.add(DominioMovimentoLeito.D);

		criteria.add(Restrictions.eq(
				AinTiposMovimentoLeito.Fields.CODIGO.toString(), codigo));
		criteria.add(Restrictions.in(
				AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
				lista));

		return (AinTiposMovimentoLeito) executeCriteriaUniqueResult(criteria);
	}

	

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoLeito: B, BI, BL, D.<br>
	 * Ordenado por grupoMovimentoLeito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoBloqueadosPorDescricaoOuCodigo(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMovimentoLeito.class);

		ArrayList<DominioMovimentoLeito> lista = new ArrayList<DominioMovimentoLeito>();
		lista.add(DominioMovimentoLeito.B);
		lista.add(DominioMovimentoLeito.BI);
		lista.add(DominioMovimentoLeito.BL);
		lista.add(DominioMovimentoLeito.D);
		
		if (descricao != null) {
			if (CoreUtil.isNumeroShort(descricao)) {
				criteria.add(Restrictions.eq(AinTiposMovimentoLeito.Fields.CODIGO.toString(), Short.valueOf(descricao)));
			} else if (StringUtils.isNotBlank(descricao)) {
				criteria.add(Restrictions.ilike(AinTiposMovimentoLeito.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.in(
				AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
				lista));

		criteria.addOrder(Order
				.asc(AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO
						.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * E grupoMovimentoLeito: B, BI, BL, D.<br>
	 * E código.<br>
	 * Ordenado por grupoMovimentoLeito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param d
	 * @return
	 */
	public List<AinTiposMovimentoLeito> pesquisarTipoSituacaoLeitoBloqueadosPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMovimentoLeito.class);

		ArrayList<DominioMovimentoLeito> lista = new ArrayList<DominioMovimentoLeito>();
		lista.add(DominioMovimentoLeito.B);
		lista.add(DominioMovimentoLeito.BI);
		lista.add(DominioMovimentoLeito.BL);
		lista.add(DominioMovimentoLeito.D);

		if (descricao != null && StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AinTiposMovimentoLeito.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.in(
				AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
				lista));

		criteria.addOrder(Order
				.asc(AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO
						.toString()));

		return executeCriteria(criteria);
	}

	

	/**
	 * Retorna AinTiposMovimentoLeito<br>
	 * com situação de bloqueio limpeza (BL)<br>
	 * de acordo com a descricao passado por parametro.<br>
	 * Ordernado por codigo.<br>
	 * 
	 * @dbtables AinTiposMovimentoLeito select
	 * 
	 * @param codigo
	 * @return
	 */
	public AinTiposMovimentoLeito pesquisarTipoSituacaoBloqueioLimpezaPorDescricao(
			String descricao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposMovimentoLeito.class);

		if (descricao != null && StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AinTiposMovimentoLeito.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(
				AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
				DominioMovimentoLeito.BL));

		criteria.addOrder(Order.asc(AinTiposMovimentoLeito.Fields.CODIGO
				.toString()));

		List<AinTiposMovimentoLeito> lista = executeCriteria(criteria);

		if (lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}
	

}
