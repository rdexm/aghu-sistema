package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MpmProcedEspecialDiversoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmProcedEspecialDiversos> {

	private static final long serialVersionUID = -7947599132477763529L;

	public List<MpmProcedEspecialDiversos> buscaProcedEspecialDiversos(Object objPesquisa) {
		// Tarefa 882 - Consulta para ser usada em SB.
		/*
		 * select PED.IND_SITUACAO DSP_IND_SITUACAO3 ,PED.IND_PERM_PRESCRICAO
		 * DSP_IND_PERM_PRESCRICAO ,PED.SEQ PED_SEQ,PED.DESCRICAO DSP_DESCRICAO
		 * from MPM_PROCED_ESPECIAL_DIVERSOS PED where (( (:SYSTEM.MODE =
		 * 'ENTER-QUERY') ) OR ((:SYSTEM.MODE = 'NORMAL') and PED.IND_SITUACAO =
		 * 'A' and PED.IND_PERM_PRESCRICAO = 'S')) order by PED.DESCRICAO asc
		 */
		String srtPesquisa = (String) objPesquisa;
		List<MpmProcedEspecialDiversos> list;

		DetachedCriteria criteria = obterCriteriaProcedESpecialDiversos();
		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroShort(srtPesquisa)) {
				criteria.add(Restrictions.eq(MpmProcedEspecialDiversos.Fields.SEQ.toString(), Short.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(MpmProcedEspecialDiversos.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(MpmProcedEspecialDiversos.Fields.PERMITE_PRESCRICAO.toString(), true));
		criteria.add(Restrictions.eq(MpmProcedEspecialDiversos.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.addOrder(Order.asc(MpmProcedEspecialDiversos.Fields.DESCRICAO.toString()));

		list = super.executeCriteria(criteria);

		return list;
	}

	private DetachedCriteria obterCriteriaProcedESpecialDiversos() {
		// Metódo criado prevendo reuso.
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmProcedEspecialDiversos.class);
		return criteria;
	}

	private DetachedCriteria criarCriteria(MpmProcedEspecialDiversos elemento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmProcedEspecialDiversos.class);
		
		criteria.createAlias(MpmProcedEspecialDiversos.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);

		// Popula criteria com dados do elemento
		if (elemento != null) {

			// Código
			if (elemento.getSeq() != null) {
				criteria.add(Restrictions.eq(MpmProcedEspecialDiversos.Fields.SEQ.toString(), elemento.getSeq()));
			}

			// Descrição
			if (StringUtils.isNotBlank(elemento.getDescricao())) {
				criteria.add(Restrictions.ilike(MpmProcedEspecialDiversos.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
			}

			// Ativo
			if (elemento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(MpmProcedEspecialDiversos.Fields.SITUACAO.toString(), elemento.getIndSituacao()));
			}

		}

		return criteria;
	}

	/**
	 * Busca na base uma lista de MpmProcedEspecialDiversos pelo filtro
	 */
	public List<MpmProcedEspecialDiversos> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MpmProcedEspecialDiversos elemento) {
		
		DetachedCriteria criteria = criarCriteria(elemento);
		criteria.createAlias(
				MpmProcedEspecialDiversos.Fields.MODOS_PROCEDIMENTOS_PRESCRICAO_DE_PROCEDIMENTOS
						.toString(), "MMU", JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Busca na base o número de elementos da lista de MpmProcedEspecialDiversos
	 * pelo filtro
	 */
	public Long pesquisarCount(MpmProcedEspecialDiversos elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}

	public MpmProcedEspecialDiversos obterPeloId(Short codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmProcedEspecialDiversos.class);
		criteria.add(Restrictions.eq(MpmProcedEspecialDiversos.Fields.SEQ.toString(), codigo));
		return (MpmProcedEspecialDiversos) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Metodo que monta uma criteria para pesquisar MpmProcedEspecialDiversos
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaMpmProcedEspecialDiversos(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmProcedEspecialDiversos.class);
		String strPesquisa = (String) objPesquisa;

		if (CoreUtil.isNumeroShort(strPesquisa)) {
			criteria.add(Restrictions.eq(MpmProcedEspecialDiversos.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(MpmProcedEspecialDiversos.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por
	 * MpmProcedEspecialDiversos, filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<MpmProcedEspecialDiversos>
	 */
	public List<MpmProcedEspecialDiversos> listarMpmProcedEspecialDiversos(Object objPesquisa) {
		List<MpmProcedEspecialDiversos> lista = null;
		DetachedCriteria criteria = montarCriteriaMpmProcedEspecialDiversos(objPesquisa);

		criteria.addOrder(Order.asc(MpmProcedEspecialDiversos.Fields.SEQ.toString()));

		lista = executeCriteria(criteria, 0, 100, null, true);

		return lista;
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por
	 * MpmProcedEspecialDiversos, filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarMpmProcedEspecialDiversosCount(Object objPesquisa) {
		DetachedCriteria criteria = montarCriteriaMpmProcedEspecialDiversos(objPesquisa);

		return executeCriteriaCount(criteria);
	}

	public List<MpmProcedEspecialDiversos> listarProcedimentosEspeciaisAtivosComProcedimentoIntAtivoPorPhiSeq(Integer phiSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmProcedEspecialDiversos.class);

		criteria.createAlias(MpmProcedEspecialDiversos.Fields.PROCED_HOSP_INTERNO.toString(), MpmProcedEspecialDiversos.Fields.PROCED_HOSP_INTERNO.toString());

		criteria.add(Restrictions.eq(MpmProcedEspecialDiversos.Fields.SEQ_PROCEDIMENTO_INTERNO.toString(), phiSeq));
		criteria.add(Restrictions.eq(MpmProcedEspecialDiversos.Fields.SITUACAO_PROCEDIMENTO_INTERNO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MpmProcedEspecialDiversos.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria);
	}

}