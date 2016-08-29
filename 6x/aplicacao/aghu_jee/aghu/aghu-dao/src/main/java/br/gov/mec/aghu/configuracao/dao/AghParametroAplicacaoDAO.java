package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghCaixaPostalAplicacaoId;
import br.gov.mec.aghu.model.AghParametroAplicacao;
import br.gov.mec.aghu.model.AghParametroAplicacaoId;


public class AghParametroAplicacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghParametroAplicacao> {
	
	private static final long serialVersionUID = -8809019855134658247L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametroAplicacao.class);
		return criteria;
    }

	@Override
	public void obterValorSequencialId(AghParametroAplicacao elemento) {
		
		if (elemento.getAghCaixaPostalAplicacao() == null ) {
			
			throw new IllegalArgumentException("Parâmetro da Aplicação não está associado corretamente a nenhuma Caixa Postal Aplicação.");
			
		}
		
		AghParametroAplicacaoId id = new AghParametroAplicacaoId();
		
		String aplicCodigo = elemento.getAghCaixaPostalAplicacao().getId().getAplicacaoCodigo();
		Long cxtSeq = elemento.getAghCaixaPostalAplicacao().getId().getCxtSeq();
		
		DetachedCriteria criteria = obterCriteria() ;
		criteria.add(Restrictions.eq(AghParametroAplicacao.Fields.CXT_SEQ.toString(), cxtSeq));
		criteria.add(Restrictions.eq(AghParametroAplicacao.Fields.APLICACAO_CODIGO.toString(), aplicCodigo));
		criteria.setProjection(Projections.max(AghParametroAplicacao.Fields.SEQP.toString()));

		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		
		id.setCxaAplicacaoCodigo(aplicCodigo);
		id.setCxaCxtSeq(cxtSeq);
		id.setSeqp(++seqp);
		
		elemento.setId(id);
	}	
	
	/**
	 * 
	 * @param aghCaixaPostalAplicacao
	 * @return
	 */
	public List<AghParametroAplicacao> pesquisarParametroAplicacaoPorCaixaPostalAplicacao(AghCaixaPostalAplicacaoId id) {

		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AghParametroAplicacao.Fields.CXT_SEQ.toString(), id.getCxtSeq()));
		dc.add(Restrictions.eq(AghParametroAplicacao.Fields.APLICACAO_CODIGO.toString(), id.getAplicacaoCodigo()));
		dc.addOrder(Order.asc(AghParametroAplicacao.Fields.ORDEM.toString()));
		
		
		return executeCriteria(dc);
	}
	
	
	/**
	 * 
	 * @param cxt_seq
	 * Estoria: 40229; Consulta c12
	 */
	public void removerAghParamtroAplicaoPorCaixaPostalServidoresSeq(Long cxt_seq){
		DetachedCriteria criteria=obterCriteria();
		
		criteria.add(Restrictions.eq(AghParametroAplicacao.Fields.CXT_SEQ.toString(), cxt_seq));
		
		List<AghParametroAplicacao> aghParametroAplicacaos=executeCriteria(criteria);
		
		for(AghParametroAplicacao aghParametroAplicacao:aghParametroAplicacaos){
			this.remover(aghParametroAplicacao);
		}
	}	
	
}
