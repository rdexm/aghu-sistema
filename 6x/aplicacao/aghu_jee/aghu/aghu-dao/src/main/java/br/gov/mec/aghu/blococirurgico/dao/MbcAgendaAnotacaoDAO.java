package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcAgendaAnotacao;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MbcAgendaAnotacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAgendaAnotacao> {

	private static final long serialVersionUID = -2072061942620967849L;
	
	public List<MbcAgendaAnotacao> pesquisarAgendaAnotacaoByPeriodoAndMbcProfAtua(Date dtIni, Date dtFim, MbcProfAtuaUnidCirgsId profAtuaUnidCirgsId) {
		DetachedCriteria criteria = getCriteriaPesquisarAgendaAnotacaoByPeriodoAndMbcProfAtua(
				dtIni, dtFim, profAtuaUnidCirgsId);
	
		return executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarAgendaAnotacaoByPeriodoAndMbcProfAtua(
			Date dtIni, Date dtFim, MbcProfAtuaUnidCirgsId profAtuaUnidCirgsId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaAnotacao.class, "agn");		

		// Where
		if(dtIni != null && dtFim != null){ 
			Date dtIni2 = DateUtil.truncaData(dtIni);
			Date dtFim2= DateUtil.truncaDataFim(dtFim);
			criteria.add(Restrictions.between("agn."+MbcAgendaAnotacao.Fields.DATA.toString(), dtIni2, dtFim2));
		}		
		if(profAtuaUnidCirgsId != null){ 
			criteria.add(Restrictions.eq("agn."+MbcAgendaAnotacao.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID.toString(),profAtuaUnidCirgsId));
		}
		return criteria;
	}
	
	public Long pesquisarAgendaAnotacaoByPeriodoAndMbcProfAtuaCount(Date dtIni, Date dtFim, MbcProfAtuaUnidCirgsId profAtuaUnidCirgsId) {
		DetachedCriteria criteria = getCriteriaPesquisarAgendaAnotacaoByPeriodoAndMbcProfAtua(
				dtIni, dtFim, profAtuaUnidCirgsId);
	
		return executeCriteriaCount(criteria);
	}
}