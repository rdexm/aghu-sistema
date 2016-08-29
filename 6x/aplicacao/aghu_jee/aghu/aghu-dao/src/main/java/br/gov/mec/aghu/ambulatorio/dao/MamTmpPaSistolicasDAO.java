package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamTmpPaSistolicas;


public class MamTmpPaSistolicasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTmpPaSistolicas> {

	private static final long serialVersionUID = -7861527903134579032L;

	public List<MamTmpPaSistolicas> listaTmpPaSistolicasPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPaSistolicas.class);

		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MamTmpPaSistolicas> obterPorEvolucao(Long evoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPaSistolicas.class);
		
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.CHAVE.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.TIPO.toString(), "E"));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.IND_RECUPERADO.toString(), "N"));

		return executeCriteria(criteria);
	}

	//#51886
	public MamTmpPaSistolicas obterPaDiastolicasPorPorChaveTipoSeq(Long cChave, Integer cQusQutSeq, Short cQusSeqP, Short cSeqP, String cTipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPaSistolicas.class);
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.CHAVE.toString(), cChave));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.QUS_QUT_SEQ.toString(), cQusQutSeq));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.QUS_SEQP.toString(), cQusSeqP));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.SEQP.toString(), cSeqP));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.TIPO.toString(), cTipo));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.IND_RECUPERADO.toString(), DominioSimNao.N.toString()));
		return (MamTmpPaSistolicas) executeCriteria(criteria);
	}
	
	//#51886 cursor cur_tmp_resp
	public boolean obterPaSistolicasExistentePorPorChaveTipoSeq(Long cChave, Integer cQusQutSeq, Short cQusSeqP, Short cSeqP, String cTipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPaSistolicas.class);
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.CHAVE.toString(), cChave));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.QUS_QUT_SEQ.toString(), cQusQutSeq));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.QUS_SEQP.toString(), cQusSeqP));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.SEQP.toString(), cSeqP));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.TIPO.toString(), cTipo));
		return executeCriteriaExists(criteria);
	}

	public List<MamTmpPaSistolicas> obterPorAnamnese(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPaSistolicas.class);
		
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.CHAVE.toString(), anaSeq));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.TIPO.toString(), "A"));
		criteria.add(Restrictions.eq(MamTmpPaSistolicas.Fields.IND_RECUPERADO.toString(), "N"));

		return executeCriteria(criteria);


	}
}