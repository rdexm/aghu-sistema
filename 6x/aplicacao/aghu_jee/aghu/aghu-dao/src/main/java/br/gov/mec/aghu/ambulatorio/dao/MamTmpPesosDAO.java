package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamTmpPesos;

public class MamTmpPesosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTmpPesos> {
	
	private static final long serialVersionUID = -1275948339067056756L;

	public List<MamTmpPesos> listaTmpPesosPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPesos.class);
		
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MamTmpPesos> obterPesosPorEvolucao(Long evoSeq, Date dthrMvto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPesos.class);
		
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.CHAVE.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.TIPO.toString(), "E"));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.IND_RECUPERADO.toString(), "N"));
		if (dthrMvto != null) {
			criteria.add(Restrictions.ge(MamTmpPesos.Fields.DATA_PESO.toString(), dthrMvto));
		}

		return executeCriteria(criteria);
	}
	
	//#51886 cursor cur_tmp_resp
	public boolean obterPesosExistentePorPorChaveTipoSeq(Long cChave, Short cQusQutSeq, Short cQusSeqP, Short cSeqP, String cTipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPesos.class);
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.CHAVE.toString(), cChave));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.QUS_QUT_SEQ.toString(), cQusQutSeq));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.QUS_SEQP.toString(), cQusSeqP));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.SEQP.toString(), cSeqP));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.TIPO.toString(), cTipo));
		return executeCriteriaExists(criteria);
	}
		
	//#51886 cursor cur_tmp_existe
	public boolean obterPesosExistentePorPorChaveTipoData(Long cChave, String cTipo, Date cCriadoEm){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPesos.class);
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.CHAVE.toString(), cChave));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.TIPO.toString(), cTipo));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.CRIADO_EM.toString(), cCriadoEm));
		return executeCriteriaExists(criteria);
	}
	
	//#51886
	public MamTmpPesos obterMamTmpPesosPorChave(Long chave, String tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPesos.class);
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.CHAVE.toString(), chave));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.TIPO.toString(), tipo));
		return (MamTmpPesos) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MamTmpPesos> obterPesosPorAnamnese(Long anaSeq, Date dthrMvto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPesos.class);
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.CHAVE.toString(), anaSeq));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.TIPO.toString(), "A"));
		criteria.add(Restrictions.eq(MamTmpPesos.Fields.IND_RECUPERADO.toString(), "N"));
		if (dthrMvto != null) {
			criteria.add(Restrictions.ge(MamTmpPesos.Fields.DATA_PESO.toString(), dthrMvto));
		}
		
		return executeCriteria(criteria);
	}
	
}
