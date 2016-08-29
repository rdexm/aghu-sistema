package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemConfigExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;

public class AelItemConfigExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelItemConfigExame> {

	private static final long serialVersionUID = -2303930118134460294L;

	public Long obterQuantidadeItemConfigExame(final Integer lu2Seq, final String ufeEmaExaSigla, final Integer ufeEmaManSeq, final Short ufeUnfSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemConfigExame.class);
		criteria.createAlias(AelItemConfigExame.Fields.CONF_EX_LAUDO_UNICO.toString(), "CNF");
		
		criteria.add(Restrictions.eq(AelItemConfigExame.Fields.UFE_EMA_EXA_SILGA.toString(), ufeEmaExaSigla));
		criteria.add(Restrictions.eq(AelItemConfigExame.Fields.UFE_EMA_MAN_SEQ.toString(), ufeEmaManSeq));
		criteria.add(Restrictions.eq(AelItemConfigExame.Fields.UFE_UNF_SEQ.toString(), ufeUnfSeq));
		criteria.add(Restrictions.ne(AelItemConfigExame.Fields.LU2SEQ.toString(), lu2Seq));
		criteria.add(Restrictions.eq("CNF." + AelConfigExLaudoUnico.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteriaCount(criteria);
	}

	public List<AelItemConfigExame> listarItemConfigExames(final Integer firstResult, final Integer maxResult, final Integer lu2Seq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemConfigExame.class);
		criteria.add(Restrictions.eq(AelItemConfigExame.Fields.LU2SEQ.toString(), lu2Seq));
		criteria.createAlias(AelItemConfigExame.Fields.UNIDADE_EX_EXAME.toString(), "UEE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UEE."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "UEE_MA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UEE_MA."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "UEE_MA_EX", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UEE_MA."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "UEE_MA_MA", JoinType.LEFT_OUTER_JOIN);
		return executeCriteria(criteria, firstResult, maxResult, null, true);
	}

	public Long listarItemConfigExamesCount(final Integer lu2Seq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemConfigExame.class);
		criteria.add(Restrictions.eq(AelItemConfigExame.Fields.LU2SEQ.toString(), lu2Seq));
		
		return executeCriteriaCount(criteria);
	}

	/* AELK_ISE_RN.RN_ISEP_ATU_LAU_UNIC
	 CURSOR c_lu3 (c_ufe_ema_exa_sigla ael_item_config_exames.ufe_ema_exa_sigla%type,
              c_ufe_ema_man_seq   ael_item_config_exames.ufe_ema_man_seq%type,
              c_ufe_unf_seq       ael_item_config_exames.ufe_unf_seq%type)
  IS
   SELECT lu3.lu2_seq
     FROM ael_config_ex_laudo_unicos lu2,
          ael_item_config_exames     lu3
    WHERE lu3.ufe_ema_exa_sigla = c_ufe_ema_exa_sigla
      AND lu3.ufe_ema_man_seq   = c_ufe_ema_man_seq
      AND lu3.ufe_unf_seq       = c_ufe_unf_seq
      AND lu2.seq               = lu3.lu2_seq
      AND lu2.ind_situacao      = 'A';
	 */
	public AelItemConfigExame obterItemConfigExamePorConfigExLaudoUnico(final String ufeEmaExaSigla, final Integer ufeEmaManSeq, final Short ufeUnfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemConfigExame.class);
		criteria.createAlias(AelItemConfigExame.Fields.CONF_EX_LAUDO_UNICO.toString(), "CNF");
		
		criteria.add(Restrictions.eq(AelItemConfigExame.Fields.UFE_EMA_EXA_SILGA.toString(), ufeEmaExaSigla));
		criteria.add(Restrictions.eq(AelItemConfigExame.Fields.UFE_EMA_MAN_SEQ.toString(), ufeEmaManSeq));
		criteria.add(Restrictions.eq(AelItemConfigExame.Fields.UFE_UNF_SEQ.toString(), ufeUnfSeq));
		criteria.add(Restrictions.eq("CNF." + AelConfigExLaudoUnico.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return (AelItemConfigExame) executeCriteriaUniqueResult(criteria);
	}
	
	
}
