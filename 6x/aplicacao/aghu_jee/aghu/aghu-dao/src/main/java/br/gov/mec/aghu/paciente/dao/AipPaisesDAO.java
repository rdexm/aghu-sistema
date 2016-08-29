package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipPaises;

public class AipPaisesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPaises>{
	
	private static final long serialVersionUID = 1403873097485535812L;

	/**
	 * 
	 * Lista os Países pela descricao
	 * 
	 * @return
	 */
	public List<AipPaises> pesquisarPaisesPorDescricao(String strPesquisa) {
		DetachedCriteria criteria = createPesquisarPaisesPorDescricaoCriteria(strPesquisa);
		return executeCriteria(criteria, 0, 25,
				AipPaises.Fields.NOME.toString(), true);
	}
	
	public Long pesquisarPaisesPorDescricaoCount(String strPesquisa) {
		return executeCriteriaCount(createPesquisarPaisesPorDescricaoCriteria(strPesquisa));
	}
	
	private DetachedCriteria createPesquisarPaisesPorDescricaoCriteria(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPaises.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.or(Restrictions.ilike(
					AipPaises.Fields.SIGLA.toString(), strPesquisa,
					MatchMode.EXACT), Restrictions.ilike(AipPaises.Fields.NOME
					.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}

		return criteria;
	}
	
	public List<AipPaises> pesquisarPais(Integer firstResult, Integer maxResult, String sigla, String nome) {
		return executeCriteria(createCriteriaAipPaises(sigla, nome), firstResult, maxResult, "sigla", true);
	}
	
	public Long obterPaisCount(String sigla, String nome) {
		return executeCriteriaCount(createCriteriaAipPaises(sigla, nome));
	}
	
	private DetachedCriteria createCriteriaAipPaises(String sigla, String nome) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipPaises.class);
		
		if(sigla != null && !sigla.trim().equals("")) {
			cri.add(Restrictions.eq("sigla", sigla).ignoreCase());
		}
		
		if (nome != null && !nome.trim().equals("")) {
			cri.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));
		}
		
		return cri;
	}
	
	public AipPaises obterPaisPorNome(String nome) {
		AipPaises p = null;
		DetachedCriteria cri = DetachedCriteria.forClass(AipPaises.class);
		
		if (nome != null && !nome.trim().equals("")) {
			cri.add(Restrictions.ilike("nome", nome));
		}
		p = (AipPaises) executeCriteriaUniqueResult(cri);
		
		return p;
	}
	
	public String obterPaisPorSigla(String sigla) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipPaises.class);
		AipPaises pais = null;
		String retorno = "";

		if (sigla != null) {
			cri.add(Restrictions.eq(AipPaises.Fields.SIGLA.toString(), sigla));
		}

		pais = (AipPaises) executeCriteriaUniqueResult(cri);

		if (pais != null && pais.getNome() != null) {
			retorno = pais.getNome();
		}

		return retorno;
	}

}
