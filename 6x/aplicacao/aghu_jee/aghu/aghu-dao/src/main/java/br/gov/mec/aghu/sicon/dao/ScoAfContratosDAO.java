package br.gov.mec.aghu.sicon.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;

/**
 * @modulo sicon
 * @author cvagheti
 *
 */
public class ScoAfContratosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAfContrato> {

	private static final long serialVersionUID = 8514315864654081200L;

	public List<ScoAfContrato> obterAfByContrato(ScoContrato input) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAfContrato.class);
		
		criteria.createCriteria(ScoAfContrato.Fields.AUT_FORN.toString(), "afn", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("afn." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "prpst_frncdr", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("prpst_frncdr." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "frncdr", JoinType.LEFT_OUTER_JOIN);		
		criteria.createCriteria("afn." + ScoAutorizacaoForn.Fields.CVF_CODIGO.toString(), "cvf", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("prpst_frncdr." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "licitacao", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createCriteria("afn." + ScoAutorizacaoForn.Fields.NATUREZA_DESPESA.toString(), "ntd", JoinType.LEFT_OUTER_JOIN);		
		criteria.createCriteria("ntd." + FsoNaturezaDespesa.Fields.RELACIONA_NATUREZAS.toString(), "rnt", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ScoAfContrato.Fields.NR_CONTRATO.toString(), input.getSeq()));
		
		return this.executeCriteria(criteria);

	}

	public ScoAfContrato obterAfContratoByAfContrato(ScoContrato input, ScoAutorizacaoForn autorizacaoForn) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAfContrato.class);
		criteria.add(Restrictions.eq(ScoAfContrato.Fields.NR_CONTRATO.toString(), input.getSeq()));
		criteria.add(Restrictions.eq(ScoAfContrato.Fields.NUMERO_AUT_FORN.toString(), autorizacaoForn.getNumero()));

		List<ScoAfContrato> list = this.executeCriteria(criteria);

		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Descobrir se a AF pertence a um contrato deve ser executada a seguinte consulta.
	 * 
	 * @author rmalvezzi
	 * @param iafAfnNumero				Número da Autorização.
	 * @return							O contrato se exisitr.
	 */
	public ScoAfContrato buscaItemContratoAF(Integer iafAfnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAfContrato.class);
		criteria.add(Restrictions.eq(ScoAfContrato.Fields.NUMERO_AUT_FORN.toString(), iafAfnNumero));
		return (ScoAfContrato) executeCriteriaUniqueResult(criteria);
	}

	public ScoAfContrato obterAfContratosById(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAfContrato.class);
		criteria.createAlias(ScoAfContrato.Fields.CONTRATO.toString(), "CONT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CONT."+ScoContrato.Fields.FORNECEDOR, "FORN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAfContrato.Fields.AUT_FORN.toString(), "AUT_FORNECIMENTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AUT_FORNECIMENTO."+ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR, "PROPOSTA", JoinType.LEFT_OUTER_JOIN);
		
		
		criteria.add(Restrictions.eq(ScoAfContrato.Fields.SEQ.toString(), seq));
		List<ScoAfContrato> list = this.executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}
