package br.gov.mec.aghu.sig.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoControleVidaUtil;
import br.gov.mec.aghu.model.SigControleVidaUtil;
import br.gov.mec.aghu.model.SigProcessamentoCusto;

public class SigControleVidaUtilDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigControleVidaUtil>{

	private static final long serialVersionUID = 2007786717943719800L;


	/**
	 * Remove os controle de vida util vinculados ao processamento
	 * 
	 * @author rogeriovieira
	 * @param seqProcessamentoCusto processamento ao qual o controle de vida util está associado
	 */
	public void removerPorProcessamento(Integer seqProcessamentoCusto) {
		
		StringBuilder sql = new StringBuilder(50);
		
		sql.append(" DELETE ").append(SigControleVidaUtil.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigControleVidaUtil.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", seqProcessamentoCusto);
		query.executeUpdate();
	}

	
	
	/**
	 * Busca débitos de controle de vida útil de acordo com a situação e tipo de controle
	 * 
	 * @author rogeriovieira
	 * @param situacao situação 
	 * @param tipoControle tipo de controle
	 * @return lista de controles de vida útil
	 */
	public List<SigControleVidaUtil> buscarDebitosControleVidaUtilParaProcessamentoMensal(DominioSituacao situacao, DominioTipoControleVidaUtil tipoControle){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigControleVidaUtil.class);
		criteria.add(Restrictions.eq(SigControleVidaUtil.Fields.SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq(SigControleVidaUtil.Fields.TIPO.toString(), tipoControle));
		return this.executeCriteria(criteria);
	}
	
}