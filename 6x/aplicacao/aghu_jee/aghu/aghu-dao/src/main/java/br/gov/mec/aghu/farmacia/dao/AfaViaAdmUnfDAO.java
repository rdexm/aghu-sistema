package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

/**
 * Classe de acesso a base de dados responsáveis pelas operações relativas a
 * tabela afa_via_adm_unfs.
 * 
 * @author gmneto
 * 
 */
public class AfaViaAdmUnfDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaViaAdmUnf> {

	private static final long serialVersionUID = 2315170710729114224L;

	/**
	 * Lista AfaViaAdmUnf ativas por unidade funcinal.
	 * 
	 * @param unidade
	 * @return
	 */
	public List<AfaViaAdmUnf> listarAfaViaAdmUnfAtivasPorUnidadeFuncional(
			AghUnidadesFuncionais unidade) {

		DetachedCriteria criteria = obterCriteriaAfaViaAdmUnfPorUnidade(unidade.getSeq(), DominioSituacao.A);

		return this.executeCriteria(criteria);
	}

	public List<AfaViaAdmUnf> listarAfaViaAdmUnfPorUnidadeFuncional(AghUnidadesFuncionais unidade) {
		DetachedCriteria criteria = obterCriteriaAfaViaAdmUnfPorUnidade(unidade.getSeq(), null);
		return executeCriteria(criteria);
	}

	/**
	 * Lista AfaViaAdmUnf ativas por unidade funcinal e via de administração
	 * 
	 * @param unidade
	 * @param viaAdministracao
	 * @return
	 */
	public List<AfaViaAdmUnf> listarAfaViaAdmUnfAtivasPorUnidadeFuncionalEViaAdministracao(
			AghUnidadesFuncionais unidade, AfaViaAdministracao viaAdministracao) {

		DetachedCriteria criteria = obterCriteriaAfaViaAdmUnfPorUnidade(unidade.getSeq(), DominioSituacao.A);

		criteria.add(Restrictions.eq(AfaViaAdmUnf.Fields.VIA_ADMINISTRACAO
				.toString(), viaAdministracao));

		return this.executeCriteria(criteria);
	}

	/**
	 * Obtem a criteria para consultas de AfaViaAdmUnf ativas por unidade
	 * funcional.
	 * 
	 * @param unidade
	 * @return
	 */
	private DetachedCriteria obterCriteriaAfaViaAdmUnfPorUnidade(
			Short unfSeq, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaViaAdmUnf.class, "UNF");
		criteria.createAlias("UNF." + AfaViaAdmUnf.Fields.VIA_ADMINISTRACAO.toString(), "VIA", JoinType.INNER_JOIN);

		if(situacao != null){
		criteria.add(Restrictions.eq(AfaViaAdmUnf.Fields.IND_SITUACAO
				.toString(), situacao));
		}

		if(unfSeq != null){
			criteria.add(Restrictions.eq(AfaViaAdmUnf.Fields.UNF_SEQ.toString(), unfSeq));
		}
		
		return criteria;
	}

//	public List<AfaViaAdmUnf> listarAfaViaAdmUnf(Short unidExecutora){
//		DetachedCriteria criteria = DetachedCriteria
//		.forClass(AfaViaAdmUnf.class);
//		
//		criteria.add(Restrictions.eq(AfaViaAdmUnf.Fields.UNF_SEQ.toString(),
//				unidExecutora));
//		criteria.add(Restrictions.eq(AfaViaAdmUnf.Fields.IND_SITUACAO
//				.toString(), DominioSituacao.A));
//		criteria.addOrder(Order.asc(AfaViaAdmUnf.Fields.VAD_SIGLA.toString()));
//		
//		return executeCriteria(criteria);
//	}
	
	
	public Boolean verificaChaveExistente(String sigla, Short seq){
		
		AfaViaAdmUnf result = null;
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(AfaViaAdmUnf.class);

		
		criteria.add(Restrictions.eq(AfaViaAdmUnf.Fields.UNF_SEQ.toString(),
				seq));
		criteria.add(Restrictions.eq(AfaViaAdmUnf.Fields.VAD_SIGLA.toString(),
				sigla));
		result = (AfaViaAdmUnf) executeCriteriaUniqueResult(criteria);
		
		if(result ==null){
			return false;
		}else{
			return true;
		}
			
		
	}
	
	public AfaViaAdmUnf recuperarAfaViaAdmUnfPorId(String sigla, Short seq){
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(AfaViaAdmUnf.class);
		
		criteria.add(Restrictions.eq(AfaViaAdmUnf.Fields.UNF_SEQ.toString(),
				seq));
		criteria.add(Restrictions.eq(AfaViaAdmUnf.Fields.VAD_SIGLA.toString(),
				sigla));
		return (AfaViaAdmUnf) executeCriteriaUniqueResult(criteria);
			
		
	}

	public List<AfaViaAdmUnf> listarAfaViaAdmUnf(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionaisVO unidFuncionais) {
		DetachedCriteria criteria = obterCriteriaAfaViaAdmUnfPorUnidade(unidFuncionais.getSeq(), null);
		
		criteria.addOrder(Order.asc(AfaViaAdmUnf.Fields.VAD_SIGLA.toString()));

		return this.executeCriteria(criteria,firstResult,maxResult,orderProperty,asc);
	}

	public Long listarViaAdmUnfCount(AghUnidadesFuncionaisVO unidFuncionais) {
		DetachedCriteria criteria = obterCriteriaAfaViaAdmUnfPorUnidade(unidFuncionais.getSeq(), null);
		
		return executeCriteriaCount(criteria);
	}
	
	
}