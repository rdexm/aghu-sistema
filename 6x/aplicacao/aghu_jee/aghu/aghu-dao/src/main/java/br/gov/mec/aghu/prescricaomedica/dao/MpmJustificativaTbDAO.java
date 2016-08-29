package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MpmJustificativaTb;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoNotificacaoTbVO;

public class MpmJustificativaTbDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmJustificativaTb> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5949970198343682100L;

	public List<PrescricaoNotificacaoTbVO> listarJustificativasTbPorAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaTb.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MpmJustificativaTb.Fields.PME_SEQ.toString()), PrescricaoNotificacaoTbVO.Fields.PME_SEQ.toString()));

		criteria.add(Restrictions.eq(MpmJustificativaTb.Fields.PME_ATD_SEQ.toString(), atdSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(PrescricaoNotificacaoTbVO.class));
		return executeCriteria(criteria);
	}
	
	
	public List<MpmJustificativaTb> obterJustificativaParaConfirmacao(MpmPrescricaoMedica prescricao,Date data) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaTb.class);

		criteria.add(Restrictions.eq(MpmJustificativaTb.Fields.PME_ATD_SEQ.toString(),prescricao.getId().getAtdSeq()));
		criteria.add(Restrictions.ge(MpmJustificativaTb.Fields.CRIADO_EM.toString(), data));
		criteria.add(Restrictions.eq(MpmJustificativaTb.Fields.PME_SEQ.toString(),prescricao.getId().getSeq()));
		

		return executeCriteria(criteria);
	}
}
