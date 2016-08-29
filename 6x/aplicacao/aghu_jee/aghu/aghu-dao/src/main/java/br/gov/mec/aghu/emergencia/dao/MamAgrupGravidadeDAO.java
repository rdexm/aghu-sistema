package br.gov.mec.aghu.emergencia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamAgrupGravidade;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;


public class MamAgrupGravidadeDAO extends BaseDao<MamAgrupGravidade> {
	
	private static final long serialVersionUID = 1751087196974423910L;

	/***
	 * C17
	 * Consulta utilizada para obtenção da previsão de atendimento para o
	 * acolhimento SELECT agr.ind_prev_atend FROM mam_agrup_gravidades agr WHERE
	 * agr.seq = <SEQ em MAM_AGRP_GRAVIDADES obtido na consulta C15> AND
	 * agr.ind_situacao = 'A';
	 */
    public MamAgrupGravidade obterPrevAtendAcolhimento(Short seqAgrupamentoGravidade){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamAgrupGravidade.class, "MamAgrupGravidade");
		
		criteria.add(Restrictions.eq("MamAgrupGravidade." + MamAgrupGravidade.Fields.SEQ.toString(), seqAgrupamentoGravidade));
		criteria.add(Restrictions.eq("MamAgrupGravidade." + MamAgrupGravidade.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		
		return (MamAgrupGravidade) this.executeCriteriaUniqueResult(criteria);
	}

}