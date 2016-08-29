package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamTmpPerimCefalicos;

public class MamTmpPerimCefalicosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTmpPerimCefalicos> {

	private static final long serialVersionUID = 5920439362300000310L;

	public List<MamTmpPerimCefalicos> listaTmpPerimCefalicosPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPerimCefalicos.class);
		
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MamTmpPerimCefalicos> obterPorEvolucao(Long evoSeq, Date dthrMvto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPerimCefalicos.class);
		
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.CHAVE.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.TIPO.toString(), "E"));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.IND_RECUPERADO.toString(), "N"));
		if (dthrMvto != null) {
			criteria.add(Restrictions.ge(MamTmpPerimCefalicos.Fields.DATA_PC.toString(), dthrMvto));
		}

		return executeCriteria(criteria);
	}

	//#51886
	public MamTmpPerimCefalicos obterMamTmpPerimCefalicosPorChaveSeqTipo(Long chave, Integer pQusQutSeq, Short pQusSeqP, String tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPerimCefalicos.class);
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.CHAVE.toString(), chave));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.QUS_QUT_SEQ.toString(), pQusQutSeq));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.QUS_SEQP.toString(), pQusSeqP));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.TIPO.toString(), tipo));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.IND_RECUPERADO.toString(), DominioSimNao.N.toString()));
		return (MamTmpPerimCefalicos) executeCriteriaUniqueResult(criteria);
	}
	
	//#51886 cursor cur_tmp_resp
	public boolean obterPerimCefalicosExistentePorPorChaveTipoSeq(Long cChave, Integer cQusQutSeq, Short cQusSeqP, Short cSeqP, String cTipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPerimCefalicos.class);
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.CHAVE.toString(), cChave));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.QUS_QUT_SEQ.toString(), cQusQutSeq));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.QUS_SEQP.toString(), cQusSeqP));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.SEQP.toString(), cSeqP));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.TIPO.toString(), cTipo));
		return executeCriteriaExists(criteria);
	}
	
	//#51886 cursor cur_tmp_existe
	public boolean obterPerimCefalicosExistentePorPorChaveTipoData(Long cChave, String cTipo, Date cCriadoEm){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPerimCefalicos.class);
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.CHAVE.toString(), cChave));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.TIPO.toString(), cTipo));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.CRIADO_EM.toString(), cCriadoEm));
		return executeCriteriaExists(criteria);
	}

	public List<MamTmpPerimCefalicos> obterPorAnamnese(Long anaSeq, Date dthrMvto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpPerimCefalicos.class);
		
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.CHAVE.toString(), anaSeq));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.TIPO.toString(), "A"));
		criteria.add(Restrictions.eq(MamTmpPerimCefalicos.Fields.IND_RECUPERADO.toString(), "N"));
		if (dthrMvto != null) {
			criteria.add(Restrictions.ge(MamTmpPerimCefalicos.Fields.DATA_PC.toString(), dthrMvto));

		}


		return executeCriteria(criteria);
	}
}
