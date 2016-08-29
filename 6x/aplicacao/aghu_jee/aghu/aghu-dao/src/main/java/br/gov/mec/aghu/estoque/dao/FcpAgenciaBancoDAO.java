package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.FcpBanco;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FcpAgenciaBancoDAO extends BaseDao<FcpAgenciaBanco> {

	private static final long serialVersionUID = 8078848395499456068L;
	private static final String ALIAS_FCP_AGENCIA_BANCOS="AGB";
	private static final String ALIAS_FCP_BANCOS ="BCO";
	private static final String PONTO = ".";

	public List<FcpAgenciaBanco> pesquisarAgenciaBanco(Object param) {
		DetachedCriteria criteria = buildCriteriaForSuggestion(param);
		return executeCriteria(criteria);
	}

	public Long pesquisarAgenciaBancoCount(Object param) {
		DetachedCriteria criteria = buildCriteriaForSuggestion(param);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria buildCriteriaForSuggestion(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpAgenciaBanco.class, "AGB");
		criteria.createAlias("AGB." + FcpAgenciaBanco.Fields.BANCO.toString(), "BCO");
		String textoPesquisa = param.toString();
		if (CoreUtil.isNumeroShort(textoPesquisa)) {
			criteria.add(Restrictions.eq("BCO." + FcpBanco.Fields.CODIGO.toString(), Short.valueOf(textoPesquisa)));
		} else {
			criteria.add(Restrictions.ilike("BCO." + FcpBanco.Fields.NOME.toString(), textoPesquisa, MatchMode.ANYWHERE));
		}
		return criteria;
	}
	
	
	private DetachedCriteria obterCriteriaAgenciaBancoPorCodBancoCodAgencia(Short codBanco, Integer codigoAgencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpAgenciaBanco.class, ALIAS_FCP_AGENCIA_BANCOS);
		
		criteria.createAlias(ALIAS_FCP_AGENCIA_BANCOS + PONTO + FcpAgenciaBanco.Fields.BANCO.toString(), ALIAS_FCP_BANCOS);
		
		if(codBanco != null) {
			criteria.add(Restrictions.eq(ALIAS_FCP_AGENCIA_BANCOS + PONTO + FcpAgenciaBanco.Fields.ID_BCO_CODIGO.toString(), codBanco));
		}

		if(codigoAgencia != null) {
			criteria.add(Restrictions.eq(ALIAS_FCP_AGENCIA_BANCOS + PONTO + FcpAgenciaBanco.Fields.ID_CODIGO.toString(), codigoAgencia));
		}

		return criteria;
	}

	public Long countAgenciaBancoPorCodBancoCodAgencia(Short codBanco, Integer codigoAgencia) {
		return executeCriteriaCount(obterCriteriaAgenciaBancoPorCodBancoCodAgencia(codBanco, codigoAgencia));
	}
	
	public List<FcpAgenciaBanco> listarAgenciaBancoPorCodBancoCodAgencia(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short codBanco, Integer codigoAgencia) {
		
		return executeCriteria(
				obterCriteriaAgenciaBancoPorCodBancoCodAgencia(
						codBanco,codigoAgencia), firstResult, maxResult, orderProperty, asc);
	}

}
