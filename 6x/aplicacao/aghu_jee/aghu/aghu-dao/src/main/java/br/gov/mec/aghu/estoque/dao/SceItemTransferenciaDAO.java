package br.gov.mec.aghu.estoque.dao;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceItemTransferenciaId;
import br.gov.mec.aghu.model.ScoMaterial;


public class SceItemTransferenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemTransferencia> {
	
	private static final long serialVersionUID = 5036857595743998455L;

	@Override
	protected void obterValorSequencialId(SceItemTransferencia elemento) {
		
		if (elemento == null || elemento.getTransferencia() == null || elemento.getEstoqueAlmoxarifado() == null) {
			throw new IllegalArgumentException("Transferência ou Estoque Almoxarifado nao foi informado corretamente.");
		}
		
		SceItemTransferenciaId id = new SceItemTransferenciaId();
		id.setEalSeq(elemento.getEstoqueAlmoxarifado().getSeq());
		id.setTrfSeq(elemento.getTransferencia().getSeq());
		
		elemento.setId(id);
		
	}
	
	/**
	 * Retorna SceEstoqueAlmoxarifado original
	 * @param elementoModificado
	 * @return
	 */
	/*public SceItemTransferencia obterOriginal(SceItemTransferencia elementoModificado) {

		SceItemTransferenciaId id = elementoModificado.getId();

		StringBuilder hql = new StringBuilder();

		hql.append("select o.").append(SceItemTransferencia.Fields.ID.toString());

		hql.append(", o.").append(SceItemTransferencia.Fields.ESL_SEQ_CONSE.toString());
		hql.append(", o.").append(SceItemTransferencia.Fields.ESL_SEQ_CONSS.toString());
		hql.append(", o.").append(SceItemTransferencia.Fields.FATURADO_CONSS.toString());
		hql.append(", o.").append(SceItemTransferencia.Fields.MAT_BLOQUEADO.toString());
		hql.append(", o.").append(SceItemTransferencia.Fields.QUANTIDADE.toString());
		hql.append(", o.").append(SceItemTransferencia.Fields.UNIDADE_MEDIDA.toString());
		hql.append(", o.").append(SceItemTransferencia.Fields.QTDE_ENVIADA.toString());
		hql.append(", o.").append(SceItemTransferencia.Fields.TRANSFERENCIA.toString());
		hql.append(", o.").append(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO.toString());
		hql.append(", o.").append(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO_ORIGEM.toString());
		
		hql.append(" from ").append(SceItemTransferencia.class.getSimpleName()).append(" o ");

		hql.append(" left outer join o.").append(SceItemTransferencia.Fields.UNIDADE_MEDIDA.toString());
		hql.append(" left outer join o.").append(SceItemTransferencia.Fields.TRANSFERENCIA.toString());
		hql.append(" left outer join o.").append(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO_ORIGEM.toString());
		hql.append(" left outer join o.").append(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO.toString());
		
		
		hql.append(" where o.").append(SceItemTransferencia.Fields.ID.toString()).append(" = :entityId ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);

		SceItemTransferencia original = null;
		@SuppressWarnings("unchecked")
		List<Object[]> camposLst = (List<Object[]>) query.getResultList();

		if(camposLst != null && camposLst.size()>0) {

			Object[] campos = camposLst.get(0);

			original = new SceItemTransferencia();

			original.setId(id);
			original.setEslSeqConse((Integer)campos[1]);
			original.setEslSeqConss((Integer)campos[2]);
			original.setFaturadoConss((Boolean)campos[3]);
			original.setMatBloqueado((Boolean)campos[4]);
			original.setQuantidade((Integer)campos[5]);
			original.setUnidadeMedida((ScoUnidadeMedida)campos[6]);
			original.setQtdeEnviada((Integer)campos[7]);
			original.setTransferencia((SceTransferencia)campos[8]);
			original.setEstoqueAlmoxarifado((SceEstoqueAlmoxarifado)campos[9]);
			original.setEstoqueAlmoxarifadoOrigem((SceEstoqueAlmoxarifado)campos[10]);
			
		}

		return original;

	}*/

	/**
	 * Pesquisa itens de transferências através da id da transferência
	 * Obs. Ordenada pelo nome do material
	 * @param seqTransferencia
	 * @return
	 */
	public List<SceItemTransferencia> pesquisarListaItensTransferenciaPorTransferencia(Integer seqTransferencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemTransferencia.class);

		criteria.createAlias(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FNC", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), "UM", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(SceItemTransferencia.Fields.TRF_SEQ.toString(), seqTransferencia));
				
		return executeCriteria(criteria);
		
	}
	
	
	/**
	 * Pesquisa itens de transferências através da id da transferência
	 * Obs. Ordenada pelo nome do material
	 * @param seqTransferencia
	 * @return
	 */
	public List<SceItemTransferencia> pesquisarListaItensTransferenciaEventual(Integer seqTransferencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemTransferencia.class);
		
		criteria.createAlias(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL", JoinType.INNER_JOIN);
		criteria.createAlias(SceItemTransferencia.Fields.UNIDADE_MEDIDA.toString(), "UNM", JoinType.INNER_JOIN);	
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO_ORIGEM.toString(), "EALO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(SceItemTransferencia.Fields.TRF_SEQ.toString(), seqTransferencia));
				
		return executeCriteria(criteria);
		
	}
	
	
	/**
	 * 
	 * @param item
	 * @return
	 */
	public SceItemTransferencia obterItemTransferencia(SceItemTransferencia elemento) {
		
		if (elemento == null || elemento.getTransferencia() == null || elemento.getEstoqueAlmoxarifado() == null) {
			
			throw new IllegalArgumentException("Transferência ou Estoque Almoxarifado nao foi informado corretamente.");
		
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemTransferencia.class);
		criteria.add(Restrictions.eq(SceItemTransferencia.Fields.TRF_SEQ.toString(), elemento.getTransferencia().getSeq()));
		criteria.add(Restrictions.eq(SceItemTransferencia.Fields.EAL_SEQ.toString(), elemento.getEstoqueAlmoxarifado().getSeq()));
		
		return (SceItemTransferencia) executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 */
	public SceItemTransferencia obterItemTransferenciaPorEstoqueAlmoxarifado(Integer trfSeq, Integer matCodigo, Short almSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemTransferencia.class);
		criteria.createAlias(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(SceItemTransferencia.Fields.TRF_SEQ.toString(), trfSeq));
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq("ALM." + SceAlmoxarifado.Fields.SEQ.toString(), almSeq));
		
		List<SceItemTransferencia> itens = executeCriteria(criteria);
		
		if (!itens.isEmpty()) {
			
			return itens.get(0);
			
		} else {
			
			return null;
			
		}
		
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 */
	public SceItemTransferencia obterItemTransferenciaPorChave(Integer ealSeq, Integer trfSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemTransferencia.class);
		criteria.add(Restrictions.eq(SceItemTransferencia.Fields.TRF_SEQ.toString(), trfSeq));
		criteria.add(Restrictions.eq(SceItemTransferencia.Fields.EAL_SEQ.toString(), ealSeq));
		
		criteria.createAlias(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO_ORIGEM.toString(), "EAO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceItemTransferencia.Fields.TRANSFERENCIA.toString(), "TRF", JoinType.INNER_JOIN);
		criteria.createAlias(SceItemTransferencia.Fields.UNIDADE_MEDIDA.toString(), "UN", JoinType.INNER_JOIN);
		
		criteria.createAlias(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EA", JoinType.INNER_JOIN);
		criteria.createAlias("EA."+SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		
		return (SceItemTransferencia) executeCriteriaUniqueResult(criteria);
		
	}
	
	public String obterNomeMaterialItemTransferencia(Integer ealSeq, Integer trfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceItemTransferencia.class);
		
		criteria.add(Restrictions.eq(SceItemTransferencia.Fields.TRF_SEQ.toString(), trfSeq));
		criteria.add(Restrictions.eq(SceItemTransferencia.Fields.EAL_SEQ.toString(), ealSeq));
		
		criteria.createAlias(SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EA", JoinType.INNER_JOIN);
		criteria.createAlias("EA."+SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.property("MAT."+ScoMaterial.Fields.NOME.toString()));
		
		List<String> result = executeCriteria(criteria);
		
		if(!result.isEmpty()){
			return result.get(0);
		}
		
		return null;
	}
	
}
