package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;

public class MpmItemModeloBasicoDietaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmItemModeloBasicoDieta> {

	private static final long serialVersionUID = -7571946185349659390L;

	@Override
	protected void obterValorSequencialId(MpmItemModeloBasicoDieta itemDieta) {
		
		if (itemDieta == null || itemDieta.getModeloBasicoDieta() == null || itemDieta.getTipoItemDieta() == null) {
			
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!");
		
		}
		
		MpmItemModeloBasicoDietaId id = new MpmItemModeloBasicoDietaId();
		id.setModeloBasicoDietaSeq(itemDieta.getModeloBasicoDieta().getId().getSeq());
		id.setModeloBasicoPrescricaoSeq(itemDieta.getModeloBasicoDieta().getModeloBasicoPrescricao().getSeq());
		id.setTipoItemDietaSeq(itemDieta.getTipoItemDieta().getSeq());
		
		itemDieta.setId(id);
		
	}
	
	public MpmItemModeloBasicoDieta obterItemModeloBasicoDieta(MpmItemModeloBasicoDietaId id) {
		
		DetachedCriteria criteria = obterCriteriaCompleto();
		
		criteria.add(Restrictions.eq(MpmItemModeloBasicoDieta.Fields.MODELO_BASICO_PRESCRICAO_SEQ.toString(), id.getModeloBasicoPrescricaoSeq()));
		criteria.add(Restrictions.eq(MpmItemModeloBasicoDieta.Fields.MODELO_BASICO_DIETA_SEQ.toString(), id.getModeloBasicoDietaSeq()));
		criteria.add(Restrictions.eq(MpmItemModeloBasicoDieta.Fields.TIPO_ITEM_DIETA_SEQ.toString(), id.getTipoItemDietaSeq()));
		
		return (MpmItemModeloBasicoDieta) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MpmItemModeloBasicoDieta> pesquisar(Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmItemModeloBasicoDieta.class);

		criteria.add(Restrictions.eq(MpmItemModeloBasicoDieta.Fields.MODELO_BASICO_PRESCRICAO_SEQ.toString(), modeloBasicoPrescricaoSeq));
		criteria.add(Restrictions.eq(MpmItemModeloBasicoDieta.Fields.MODELO_BASICO_DIETA_SEQ.toString(), modeloBasicoDietaSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<MpmItemModeloBasicoDieta> pesquisarParaVO(Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq) {

		DetachedCriteria criteria = obterCriteriaCompleto();

		criteria.add(Restrictions.eq(MpmItemModeloBasicoDieta.Fields.MODELO_BASICO_PRESCRICAO_SEQ.toString(), modeloBasicoPrescricaoSeq));
		criteria.add(Restrictions.eq(MpmItemModeloBasicoDieta.Fields.MODELO_BASICO_DIETA_SEQ.toString(), modeloBasicoDietaSeq));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaCompleto() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemModeloBasicoDieta.class, "IBD");
		criteria.createAlias("IBD." + MpmItemModeloBasicoDieta.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("IBD." + MpmItemModeloBasicoDieta.Fields.MODELO_BASICO_DIETA.toString(), "MBD", JoinType.INNER_JOIN);
		criteria.createAlias("MBD." + MpmModeloBasicoDieta.Fields.MODELO_BASICO_PRESCRICAO.toString(), "MBP", JoinType.INNER_JOIN);
		criteria.createAlias("IBD." + MpmItemModeloBasicoDieta.Fields.TIPO_FREQ_APRAZAMENTO.toString(), "TFA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IBD." + MpmItemModeloBasicoDieta.Fields.TIPO_ITEM_DIETA.toString(), "TID", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TID." + AnuTipoItemDieta.Fields.TIPO_FREQ_APRAZAMENTOS.toString(), "TFX", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TID." + AnuTipoItemDieta.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "UND", JoinType.LEFT_OUTER_JOIN);
		return criteria;
	}
	
	public Long countItemModeloBasicoDieta(Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmItemModeloBasicoDieta.class);

		criteria.add(Restrictions.eq(MpmItemModeloBasicoDieta.Fields.MODELO_BASICO_PRESCRICAO_SEQ.toString(), modeloBasicoPrescricaoSeq));
		criteria.add(Restrictions.eq(MpmItemModeloBasicoDieta.Fields.MODELO_BASICO_DIETA_SEQ.toString(), modeloBasicoDietaSeq));
		
		return executeCriteriaCount(criteria);
	}
}
