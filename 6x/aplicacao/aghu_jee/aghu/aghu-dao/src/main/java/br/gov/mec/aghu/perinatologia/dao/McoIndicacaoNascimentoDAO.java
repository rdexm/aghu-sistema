package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class McoIndicacaoNascimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoIndicacaoNascimento> {

	private static final long serialVersionUID = 3366340375130317955L;

	public List<String> obterDescricaoMcoIndicacaoNascimentoByFichaAnestesia(
			Integer gsoPacCodigo, Short gsoSeqp) {
		/**
		 * select distinct ina.descricao cesariana
		from mbc_ficha_anestesias fic,
		     mco_nasc_indicacoes ind,
		     mco_indicacao_nascimentos ina
		where fic.seq = $P{P_FIC_SEQ}
		  and fic.gso_pac_codigo =ind.csr_nas_gso_pac_codigo (+)
		  and fic.gso_seqp = ind.csr_nas_gso_seqp (+)   
		  and ind.ina_seq = ina.seq (+)
		 */
		
		StringBuffer hql = new StringBuffer(480);
		hql.append("SELECT DISTINCT ina.descricao FROM McoNascIndicacoes ind " +
				"LEFT JOIN ind.indicacaoNascimento ina ");
		
		hql.append(" where ind." 	+  McoNascIndicacoes.Fields.CESARIANA_CODIGO_PACIENTE + " = :gsoPacCodigo 		\n ");
		hql.append(" and ind." 		+  McoNascIndicacoes.Fields.CESARIANA_SEQP + " = :gsoSeqp 		\n ");
		
		Query q = createHibernateQuery(hql.toString());
		q.setInteger("gsoPacCodigo", gsoPacCodigo);
		q.setShort("gsoSeqp", gsoSeqp);
		
		return q.list();
	}
	
	public List<McoIndicacaoNascimento> pesquisarIndicacoesNascimento(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, Integer codigo, String descricao,
			DominioTipoIndicacaoNascimento tipoIndicacao, DominioSituacao situacao) {
		
		DetachedCriteria criteria = montarCriteriaPesquisarIndicacoesNascimento(
				codigo, descricao, tipoIndicacao, situacao);
		
		if(StringUtils.isBlank(orderProperty)){
			orderProperty = McoIndicacaoNascimento.Fields.SEQ.toString();
		}
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarIndicacoesNascimentoCount(Integer codigo, String descricao,
			DominioTipoIndicacaoNascimento tipoIndicacao, DominioSituacao situacao) {
		
		DetachedCriteria criteria = montarCriteriaPesquisarIndicacoesNascimento(
				codigo, descricao, tipoIndicacao, situacao);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaPesquisarIndicacoesNascimento(
			Integer codigo, String descricao,
			DominioTipoIndicacaoNascimento tipoIndicacao,
			DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIndicacaoNascimento.class);
		
		if (codigo != null) {
			criteria.add(Restrictions.eq(McoIndicacaoNascimento.Fields.SEQ.toString(), codigo));
		}
		if (descricao != null && !descricao.isEmpty()) {
			criteria.add(Restrictions.ilike(McoIndicacaoNascimento.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(McoIndicacaoNascimento.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(tipoIndicacao != null) {
			criteria.add(Restrictions.eq(McoIndicacaoNascimento.Fields.TIPO_INDICACAO.toString(), tipoIndicacao));
		}
		
		return criteria;
	}
	
	public Boolean pesquisarIndicacaoPorDescricaoExata(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIndicacaoNascimento.class);
		criteria.add(Restrictions.ilike(McoIndicacaoNascimento.Fields.DESCRICAO.toString(), descricao, MatchMode.EXACT));
		
		return (this.executeCriteriaCount(criteria) > 0);
	}

	public Long pesquisarIndicacoesNascimentoPorSeqNomeCount(String objPesquisa) {
		DetachedCriteria criteria = montarPesquisaIncicacaoNascimentoPorSeqNome(objPesquisa);
		return executeCriteriaCount(criteria);
	}

	public List<McoIndicacaoNascimento> pesquisarIndicacoesNascimentoPorSeqNome(String objPesquisa) {
		DetachedCriteria criteria = montarPesquisaIncicacaoNascimentoPorSeqNome(objPesquisa);
		criteria.addOrder(Order.asc(McoIndicacaoNascimento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarPesquisaIncicacaoNascimentoPorSeqNome(
			String objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIndicacaoNascimento.class);
		criteria.add(Restrictions.eq(McoIndicacaoNascimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(StringUtils.isNotBlank(objPesquisa)){
			
			Criterion restriction =  NumberUtils.isNumber(objPesquisa)? 
					Restrictions.eq(McoIndicacaoNascimento.Fields.SEQ.toString(), Integer.valueOf(objPesquisa))
					:Restrictions.ilike(McoIndicacaoNascimento.Fields.DESCRICAO.toString(), objPesquisa, MatchMode.ANYWHERE);
			
			criteria.add(restriction);
		}
	return criteria;
	}
	
	/**
	 * Pesquisa ativos por descri��o
	 * 
	 * C1 de 37857
	 * 
	 * @param descricao
	 * @param maxResults
	 * @return
	 */
	public DetachedCriteria montarCriteriaAtivosPorDescricaoOuSeq(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIndicacaoNascimento.class);
		criteria.add(Restrictions.eq(McoIndicacaoNascimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(McoIndicacaoNascimento.Fields.SEQ.toString(), Integer.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(McoIndicacaoNascimento.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	/**
	 * Pesquisa ativos por descri��o
	 * 
	 * C1 de 37857
	 * 
	 * @param descricao
	 * @param maxResults
	 * @return
	 */
	public List<McoIndicacaoNascimento> pesquisarAtivosPorDescricaoOuSeq(String descricao, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorDescricaoOuSeq(descricao);
		criteria.addOrder(Order.asc(McoIndicacaoNascimento.Fields.DESCRICAO.toString()));
		if (maxResults != null) {
			return super.executeCriteria(criteria, 0, maxResults, null, true);
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * Count ativos por descri��o
	 * 
	 * C1 de 37857
	 * 
	 * @param descricao
	 * @param maxResults
	 * @return
	 */
	public Long pesquisarAtivosPorDescricaoOuSeqCount(String descricao) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorDescricaoOuSeq(descricao);
		return super.executeCriteriaCount(criteria);
	}

}

