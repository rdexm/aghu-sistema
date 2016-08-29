package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelServidoresExameUnid;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;

public class AelServidoresExameUnidDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelServidoresExameUnid> {

	private static final long serialVersionUID = -2979224154382555115L;


	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelServidoresExameUnid.class);
		return criteria;
    }

	public List<AelServidoresExameUnid> buscaListaAelServidoresExameUnidPorEmaExaSiglaEmaManSeqUnfSeq(
			String emaExaSigla, Integer emaManSeq, Short unfSeq) {
		
		DetachedCriteria dc = obterCriteria();
	
		dc.createAlias(AelServidoresExameUnid.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias(AelServidoresExameUnid.Fields.UNF_EXEC_EXAMES.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);		
		dc.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES", JoinType.LEFT_OUTER_JOIN);
		
		dc.add(Restrictions.eq(AelServidoresExameUnid.Fields.UFE_EMA_EXA_SIGLA.toString(), emaExaSigla));
		dc.add(Restrictions.eq(AelServidoresExameUnid.Fields.UFE_EMA_MAN_SEQ.toString(), emaManSeq));
		dc.add(Restrictions.eq(AelServidoresExameUnid.Fields.UFE_UNF_SEQ.toString(), unfSeq));
		
		return executeCriteria(dc);
	}
	
	public List<AelServidoresExameUnid> buscaListaAelServidoresExameUnid(AelExames exame, AelMateriaisAnalises materialAnalise, AghUnidadesFuncionais unidadeFuncional) {
		if (exame == null || exame.getSigla() == null 
				|| materialAnalise == null || materialAnalise.getSeq() == null
				|| unidadeFuncional == null || unidadeFuncional.getSeq() == null) {
			throw new IllegalArgumentException("Parametro invalido");
		}
		
		return this.buscaListaAelServidoresExameUnidPorEmaExaSiglaEmaManSeqUnfSeq(exame.getSigla(), materialAnalise.getSeq(), unidadeFuncional.getSeq());
	}
	

	public AelServidoresExameUnid buscarAelServidoresExameUnidPorId(
			String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq,
			Integer serMatricula, Short serVinCodigo) {
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelServidoresExameUnid.Fields.UFE_EMA_EXA_SIGLA.toString(), ufeEmaExaSigla));
		dc.add(Restrictions.eq(AelServidoresExameUnid.Fields.UFE_EMA_MAN_SEQ.toString(), ufeEmaManSeq));
		dc.add(Restrictions.eq(AelServidoresExameUnid.Fields.UFE_UNF_SEQ.toString(), ufeUnfSeq));
		dc.add(Restrictions.eq(AelServidoresExameUnid.Fields.SER_MATRICULA.toString(), serMatricula));
		dc.add(Restrictions.eq(AelServidoresExameUnid.Fields.SER_VIN_CODIGO.toString(), serVinCodigo));
		
		return (AelServidoresExameUnid)executeCriteriaUniqueResult(dc);
	}
	
}
