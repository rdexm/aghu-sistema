package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamTmpAlturas;


public class MamTmpAlturasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTmpAlturas> {
	
	private static final long serialVersionUID = -6626422700649538672L;

	public List<MamTmpAlturas> listaTmpAlturasPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpAlturas.class);
		
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MamTmpAlturas> obterAlturaPorEvolucao(Long evoSeq, Date dthrMvto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpAlturas.class);
		
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.CHAVE.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.TIPO.toString(), "E"));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.IND_RECUPERADO.toString(), "N"));
		if (dthrMvto != null) {
			criteria.add(Restrictions.ge(MamTmpAlturas.Fields.DATA_ALTURA.toString(), dthrMvto));
		}

		return executeCriteria(criteria);
	}

	public List<MamTmpAlturas> obterAlturaPorAnamnese(Long anaSeq, Date dthrMvto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpAlturas.class);
		
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.CHAVE.toString(), anaSeq));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.TIPO.toString(), "A"));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.IND_RECUPERADO.toString(), "N"));
		if (dthrMvto != null) {
			criteria.add(Restrictions.ge(MamTmpAlturas.Fields.DATA_ALTURA.toString(), dthrMvto));

		}


		return executeCriteria(criteria);
	}

	//#51886
	public MamTmpAlturas obterMamTmpAlturaPorChaveSeqTipo(Long chave, Integer pQusQutSeq, Short pQusSeqP, String tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpAlturas.class);
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.CHAVE.toString(), chave));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.QUS_QUT_SEQ.toString(), chave));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.QUS_SEQP.toString(), chave));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.TIPO.toString(), tipo));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.IND_RECUPERADO.toString(), DominioSimNao.N.toString()));
		return (MamTmpAlturas) executeCriteriaUniqueResult(criteria);
	}
	
	//#51886 cursor cur_tmp_existe
	public boolean obterMamTmpAlturaPorChaveSeqTipoData(Long cChave, String cTipo, Date cCriadoEm){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpAlturas.class);
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.CHAVE.toString(), cChave));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.TIPO.toString(), cTipo));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.CRIADO_EM.toString(), cCriadoEm));
		return executeCriteriaExists(criteria);
	}
	
	//#51886 cursor cur_tmp_resp
	public boolean obterMamTmpAlturaPorChaveSeqTipoResp(Long cChave, Integer cQusQutSeq, Short cQusSeqP, Short cSeqP, String cTipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTmpAlturas.class);
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.CHAVE.toString(), cChave));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.QUS_QUT_SEQ.toString(), cQusQutSeq));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.QUS_SEQP.toString(), cQusSeqP));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.SEQP.toString(), cSeqP));
		criteria.add(Restrictions.eq(MamTmpAlturas.Fields.TIPO.toString(), cTipo));
		return executeCriteriaExists(criteria);
	}
	
}
