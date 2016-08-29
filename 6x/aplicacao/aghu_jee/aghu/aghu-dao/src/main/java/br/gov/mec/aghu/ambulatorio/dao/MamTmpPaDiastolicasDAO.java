package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamTmpPaDiastolicas;

public class MamTmpPaDiastolicasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTmpPaDiastolicas> {

	private static final long serialVersionUID = 4270656165809389946L;

	public List<MamTmpPaDiastolicas> listaTmpPaDiastolicasPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPaDiastolicas.class);

		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MamTmpPaDiastolicas> obterPorEvolucao(Long evoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPaDiastolicas.class);
		
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.CHAVE.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.TIPO.toString(), "E"));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.IND_RECUPERADO.toString(), "N"));

		return executeCriteria(criteria);
	}

	public List<MamTmpPaDiastolicas> obterPorAnamnese(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPaDiastolicas.class);
		
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.CHAVE.toString(), anaSeq));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.TIPO.toString(), "A"));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.IND_RECUPERADO.toString(), "N"));

		return executeCriteria(criteria);


	}
	//#51886
	public MamTmpPaDiastolicas obterPaDiastolicasPorPorChaveTipoSeq(Long cChave, Short cQusQutSeq, Short cQusSeqP, Short cSeqP, String cTipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPaDiastolicas.class);
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.CHAVE.toString(), cChave));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.QUS_QUT_SEQ.toString(), cQusQutSeq));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.QUS_SEQP.toString(), cQusSeqP));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.SEQP.toString(), cSeqP));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.TIPO.toString(), cTipo));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.IND_RECUPERADO.toString(), DominioSimNao.N.toString()));
		return (MamTmpPaDiastolicas) executeCriteria(criteria);
	}
	
	//#51886 cursor cur_tmp_resp
	public boolean obterPaDiastolicasExistentePorPorChaveTipoSeq(Long cChave, Short cQusQutSeq, Short cQusSeqP, Short cSeqP, String cTipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPaDiastolicas.class);
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.CHAVE.toString(), cChave));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.QUS_QUT_SEQ.toString(), cQusQutSeq));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.QUS_SEQP.toString(), cQusSeqP));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.SEQP.toString(), cSeqP));
		criteria.add(Restrictions.eq(MamTmpPaDiastolicas.Fields.TIPO.toString(), cTipo));
		return executeCriteriaExists(criteria);
	}

}
