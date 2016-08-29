package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SigAtividadePessoaRestricoes;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigCategoriaRecurso;

public class SigAtividadePessoaRestricoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigAtividadePessoaRestricoes> {

	private static final long serialVersionUID = -8329692329836044640L;
	
	
	public List<SigAtividadePessoaRestricoes> buscarRestricoesAtividade(Integer tvdSeq, Integer gocSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividadePessoaRestricoes.class, "avpr");
		
		criteria.createAlias("avpr." + SigAtividadePessoaRestricoes.Fields.CATEGORIA_RECURSO.toString(), "catr", JoinType.INNER_JOIN);
		criteria.createAlias("avpr." + SigAtividadePessoaRestricoes.Fields.ATIVIDADE_PESSOA.toString(), "avp", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.ilike("catr." + SigCategoriaRecurso.Fields.DESCRICAO.toString(), "Pessoa", MatchMode.EXACT));
		criteria.add(Restrictions.eq("avp." + SigAtividadePessoas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("avp." + SigAtividadePessoas.Fields.ATIVIDADE_SEQ.toString(), tvdSeq));
		criteria.add(Restrictions.eq("avp." + SigAtividadePessoas.Fields.GRUPO_OCUPACAO_SEQ.toString(), gocSeq));		
		
		return executeCriteria(criteria);
	}


	public List<SigAtividadePessoaRestricoes> listarAtividadePessoaRestricoes(SigAtividadePessoas sigAtividadePessoas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividadePessoaRestricoes.class);
		criteria.createAlias(SigAtividadePessoaRestricoes.Fields.ATIVIDADE_PESSOA.toString(), "avp", JoinType.INNER_JOIN);
		criteria.createAlias(SigAtividadePessoaRestricoes.Fields.PAGADOR.toString(), "pgd", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(SigAtividadePessoaRestricoes.Fields.ATIVIDADE_PESSOA.toString(), sigAtividadePessoas));
		return executeCriteria(criteria);
	}
}