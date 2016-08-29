package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Query;

import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;

public class VAelExamesSolicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelExamesSolicitacao>{

		
	private static final long serialVersionUID = 8044298364953533785L;

	@SuppressWarnings("unchecked")
	public List<VAelExamesSolicitacao> pesquisaExameSolicitacao(String descricao, AghUnidadesFuncionais unidadeExecutora) {
		
		List<VAelExamesSolicitacao> result = null;
		
		if(unidadeExecutora != null) {
			
			StringBuilder hql = new StringBuilder("SELECT DISTINCT ");
					
			hql.append(" vxs")
				.append(" from ").append(VAelExamesSolicitacao.class.getName()).append(" vxs ");		
			hql.append(" WHERE ")
				.append(" vxs.")
				.append(VAelExamesSolicitacao.Fields.DESCRICAO_USUAL_EXAME)
				.append(" like :descricao ")
					.append(" and ( ")
						.append(" vxs.")
						.append(VAelExamesSolicitacao.Fields.SIGLA.toString()).append(" IN ")
							.append('(')
							.append(" SELECT ")
							.append("ufe.")
							.append(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString())
							.append(" FROM ").append(AelUnfExecutaExames.class.getName()).append(" ufe ")
							.append(" , ").append(AelTipoAmostraExame.class.getName()).append(" tae ")
							.append(" WHERE ")
							.append(" tae.").append(AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString())
							.append(" = 'C' ")
							.append(" and ")
							.append(" ufe.")
							.append(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA)
							.append(" = ")
							.append(" tae.")
							.append(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA)
							.append(" and ")
							.append(" ufe.")
							.append(AelUnfExecutaExames.Fields.EMA_MAN_SEQ)
							.append(" = ")
							.append(" tae.")
							.append(AelTipoAmostraExame.Fields.EMA_MAN_SEQ)
							.append(')')	
							.append(" OR  ")
							.append(" vxs.")
							.append(VAelExamesSolicitacao.Fields.UNF_SEQ.toString())
							.append(" = :unfSeq")
							.append(')');
			
			Query query = createHibernateQuery(hql.toString());
			query.setParameter("descricao", "%".concat(descricao.toUpperCase()).concat("%"));
			query.setParameter("unfSeq", unidadeExecutora.getSeq().intValue());
			
			result = query.list();
		}
		
		return result;
	}
	
}
