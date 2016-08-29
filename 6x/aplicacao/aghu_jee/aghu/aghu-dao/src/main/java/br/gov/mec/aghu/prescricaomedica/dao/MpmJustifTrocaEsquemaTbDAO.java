package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmJustifTrocaEsquemaTb;
import br.gov.mec.aghu.model.MpmTipoJustifTrocaEsqTb;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MpmJustifTrocaEsquemaTbDAO extends BaseDao<MpmJustifTrocaEsquemaTb> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8275094356674390135L;

	private static final String JTE = "JTE";
	private static final String TTE = "TTE";
	private static final String JTE_DOT = "JTE.";
	private static final String TTE_DOT = "TTE.";
	
	/**
	 * #45269 - C2
	 * @param tebJumSeq 
	 * @return
	 */
	public Object[] obterDescricaoAvaliacaoMedicamento(Integer tebJumSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustifTrocaEsquemaTb.class, JTE);
		criteria.createAlias(JTE_DOT + MpmJustifTrocaEsquemaTb.Fields.TIPO_JUSTIF_TROCA_ESQ_TB.toString(), TTE);
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property(JTE_DOT + MpmJustifTrocaEsquemaTb.Fields.TEB_JUM_SEQ.toString()), MpmJustifTrocaEsquemaTb.Fields.TEB_JUM_SEQ.toString())
				.add(Projections.property(TTE_DOT + MpmTipoJustifTrocaEsqTb.Fields.DESCRICAO.toString()), MpmTipoJustifTrocaEsqTb.Fields.DESCRICAO.toString())
				.add(Projections.property(JTE_DOT + MpmJustifTrocaEsquemaTb.Fields.DESCRICAO.toString()), MpmJustifTrocaEsquemaTb.Fields.DESCRICAO.toString());
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq(JTE_DOT + MpmJustifTrocaEsquemaTb.Fields.TEB_JUM_SEQ.toString(), tebJumSeq));
		
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}

	public List<MpmJustifTrocaEsquemaTb> listarJustificativasTrocaEsquemaPorCodigoJustificativaUso(Integer jumSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustifTrocaEsquemaTb.class, JTE);

		criteria.createAlias(JTE_DOT + MpmJustifTrocaEsquemaTb.Fields.TIPO_JUSTIF_TROCA_ESQ_TB.toString(), TTE);

		criteria.add(Restrictions.eq(JTE_DOT + MpmJustifTrocaEsquemaTb.Fields.TEB_JUM_SEQ.toString(), jumSeq));

		return executeCriteria(criteria);
	}
}
