package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;

public class MpmItemPrescricaoDietaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmItemPrescricaoDieta> {



	private static final long serialVersionUID = -3644418179849882728L;

	/**
	 * Retorna instância desatachada do objeto com propriedades anteriores a
	 * alteração(old).<br>
	 * Não estão sendo carregadas todas as propriedades do objeto, verificar
	 * quando necessário.
	 * 
	 * @param item
	 * @return instância desatachada
	 */
	public MpmItemPrescricaoDieta obterOld(MpmItemPrescricaoDieta item) {
		if (item == null || item.getId() == null) {
			throw new IllegalArgumentException("Argumento obrigatório.");
		}
		MpmItemPrescricaoDietaId id = item.getId();
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmItemPrescricaoDieta.class);

		// projection
		ProjectionList projectionList = Projections
				.projectionList()
				.add(
						Projections
								.property(MpmItemPrescricaoDieta.Fields.TIPO_FREQUENCIA_APRAZAMENTO
										.toString()),
						MpmItemPrescricaoDieta.Fields.TIPO_FREQUENCIA_APRAZAMENTO
								.toString())
				.add(
						Projections
								.property(MpmItemPrescricaoDieta.Fields.FREQUENCIA
										.toString()),
						MpmItemPrescricaoDieta.Fields.FREQUENCIA.toString());
		criteria.setProjection(projectionList);

		// restrictions
		criteria.add(Restrictions.idEq(id));

		criteria.setResultTransformer(Transformers
				.aliasToBean(MpmItemPrescricaoDieta.class));

		MpmItemPrescricaoDieta result = (MpmItemPrescricaoDieta) executeCriteriaUniqueResult(criteria);

		return result;
	}
	
	
	public List<MpmItemPrescricaoDieta> pesquisarItensDietaOrdenadoPorTipo(MpmPrescricaoDieta prescricaoDieta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoDieta.class);
		criteria.createAlias(MpmItemPrescricaoDieta.Fields.TIPO_ITEM_DIETA.toString(),MpmItemPrescricaoDieta.Fields.TIPO_ITEM_DIETA.toString());
		
		criteria.add(Restrictions.eq(MpmItemPrescricaoDieta.Fields.PRESCRICAO_DIETA.toString(),prescricaoDieta));
		criteria.addOrder(Order
				.asc(MpmItemPrescricaoDieta.Fields.TIPO_ITEM_DIETA.toString()
						+ "." + AnuTipoItemDieta.Fields.DESCRICAO.toString()));

		List<MpmItemPrescricaoDieta> lista = executeCriteria(criteria);
		
		return lista;
	}

	/**
	 * Pesquisa itens de uma dieta
	 * 
	 * @param pdtAtdSeq
	 * @param pdtSeq
	 * @return
	 */
	public List<MpmItemPrescricaoDieta> pesquisarItensDieta(Integer pdtAtdSeq,
			Long pdtSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmItemPrescricaoDieta.class);

		criteria.add(Restrictions.eq(MpmItemPrescricaoDieta.Fields.PDT_ATD_SEQ
				.toString(), pdtAtdSeq));
		criteria.add(Restrictions.eq(MpmItemPrescricaoDieta.Fields.PDT_SEQ
				.toString(), pdtSeq));

		return executeCriteria(criteria);
	}

	/**
	 * Retornar o item da prescrição de dieta descartando as alterações em cache
	 * 
	 * @param item
	 * @return
	 */
	// public MpmItemPrescricaoDieta obterItemPrescricaoDietaOrigem(
	// MpmItemPrescricaoDieta item) {
	// this.desatachar(item);
	// return obterPorChavePrimaria(item.getId());
	// }

	public Set<MpmItemPrescricaoDieta> obterItensPrescricaoDieta(
			MpmPrescricaoDieta prescricaoDieta) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmItemPrescricaoDieta.class);
		criteria.setFetchMode(MpmItemPrescricaoDieta.Fields.TIPO_ITEM_DIETA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(MpmItemPrescricaoDieta.Fields.TIPO_ITEM_DIETA+"."+AnuTipoItemDieta.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(
				MpmItemPrescricaoDieta.Fields.PRESCRICAO_DIETA.toString(),
				prescricaoDieta));
		List<MpmItemPrescricaoDieta> lista = executeCriteria(criteria);
		Set<MpmItemPrescricaoDieta> setLista = new HashSet<MpmItemPrescricaoDieta>();
		setLista.addAll(lista);

		return setLista;
	}

	/**
	 * Verificar a prescrição de dieta tem pelo menos um item associado
	 * 
	 * @param pdtSeq
	 * @return
	 */
	public boolean existeItemPrescricao(MpmPrescricaoDieta prescricaoDieta) {

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmItemPrescricaoDieta.class);
		criteria.add(Restrictions.eq(
				MpmItemPrescricaoDieta.Fields.PRESCRICAO_DIETA.toString(),
				prescricaoDieta));

		return executeCriteriaCount(criteria) > 0;

	}

}
