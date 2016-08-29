package br.gov.mec.aghu.configuracao.dao;

import br.gov.mec.aghu.model.AghAtendimentos;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class AtendimentoParaSolicitacaoExameCountQueryBuilder extends AtendimentoParaSolicitacaoExameQueryBuilder {
	
	@Override
	protected StringBuilder makeQueryBasic() {
		final StringBuilder hql = new StringBuilder(30);

		hql.append(" select count(*) ");
		hql.append(" from ");
		hql.append(AghAtendimentos.class.getSimpleName()).append(" o ");
		//hql.append(" inner join o.").append(AghAtendimentos.Fields.PACIENTE.toString()).append(" pac ");
		//hql.append(" left outer join o.").append(AghAtendimentos.Fields.ESPECIALIDADE.toString()).append(" esp ");
		//hql.append(" left outer join o.").append(AghAtendimentos.Fields.QUARTO.toString()).append(" qrt ");
		
		return hql;
	}


}
