package br.gov.mec.aghu.protocolos.dao;

import java.util.List;

import javax.persistence.Query;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoPopVersao;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpaPopAgendas;
import br.gov.mec.aghu.model.MpaPopExame;
import br.gov.mec.aghu.model.MpaPopVersoes;
import br.gov.mec.aghu.model.MpaPops;

public class MpaPopsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpaPops> {
	
	
	private static final long serialVersionUID = 304935062749359013L;

	public List<MpaPops> pesquisarProtocoloLiberado(AghEspecialidades especialidade, AelExames exame) {
		if (especialidade == null || exame == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		StringBuilder hql = new StringBuilder(200);
		hql.append(" select pop ");
		hql.append(" from ");
		hql.append(MpaPopExame.class.getSimpleName()).append(" o ");
		hql.append("inner join o.").append(MpaPopExame.Fields.POP_VERSAO).append(" versao ");
		hql.append("inner join versao.").append(MpaPopVersoes.Fields.MPA_POP).append(" pop ");
		hql.append("inner join versao.").append(MpaPopVersoes.Fields.MPA_POP_AGENDASES).append(" agendas ");
		hql.append("where versao.").append(MpaPopVersoes.Fields.IND_SITUACAO).append(" = :indSituacao");
		hql.append(" and pop.").append(MpaPops.Fields.TIPO).append(" = :popTipo");
		
		hql.append(" and agendas.").append(MpaPopAgendas.Fields.ESPECIALIDADE).append(" = :especialidade");
		hql.append(" and o.").append(MpaPopExame.Fields.EXAME).append(" = :exame");
		
		Query query = this.createQuery(hql.toString());
		
		query.setParameter("indSituacao", DominioSituacaoPopVersao.L);
		query.setParameter("popTipo", DominioSituacao.A);
		
		query.setParameter("especialidade", especialidade);
		query.setParameter("exame", exame);
		
		@SuppressWarnings("unchecked")
		List<MpaPops> list = query.getResultList();
		
		return list;
	}

}
