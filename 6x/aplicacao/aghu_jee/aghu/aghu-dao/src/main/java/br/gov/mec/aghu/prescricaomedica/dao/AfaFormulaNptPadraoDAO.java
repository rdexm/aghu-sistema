package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.RapServidores;


public class AfaFormulaNptPadraoDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaFormulaNptPadrao>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -466145115373362480L;


	/**
	 * 
	 */
	private DetachedCriteria consultaPesquisaGenerica(AfaFormulaNptPadrao filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaFormulaNptPadrao.class, "fnp");
		
		criteria.createAlias("fnp."+ AfaFormulaNptPadrao.Fields.RAP_SERVIDORES_BY_AFA_FNP_SER_FK1.toString(), "rserv", JoinType.INNER_JOIN);
		criteria.createAlias("rserv."+ RapServidores.Fields.PESSOA_FISICA.toString(), "rservpf", JoinType.INNER_JOIN);
		
		criteria.setFetchMode("fnp."+ AfaFormulaNptPadrao.Fields.RAP_SERVIDORES_BY_AFA_FNP_SER_FK1.toString(), FetchMode.JOIN);
		criteria.setFetchMode("rserv."+ RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);
		
		if (filtro.getSeq() != null) {
			criteria.add(Restrictions.eq(AfaFormulaNptPadrao.Fields.SEQ.toString(), filtro.getSeq()));				
		}
		
		if (filtro.getDescricao() != StringUtils.EMPTY) {
			criteria.add(Restrictions.ilike(AfaFormulaNptPadrao.Fields.DESCRICAO.toString(), filtro.getDescricao(), MatchMode.ANYWHERE));				
		}

		if (filtro.getObservacao() != StringUtils.EMPTY) {
			criteria.add(Restrictions.ilike(AfaFormulaNptPadrao.Fields.OBSERVACAO.toString(), filtro.getObservacao(), MatchMode.ANYWHERE));
		}

		if (filtro.getVolumeTotalMlDia() != null) {
			criteria.add(Restrictions.eq(AfaFormulaNptPadrao.Fields.VOLUME_TOTAL_ML_DIA.toString(), filtro.getVolumeTotalMlDia()));
		}
		if (filtro.getIndFormulaPediatrica() != null) {
			boolean indPediatrica;
			if(filtro.getIndFormulaPediatrica().equalsIgnoreCase("Sim")){
				indPediatrica = true;
			}else{
				indPediatrica = false;
			}
			criteria.add(Restrictions.eq(AfaFormulaNptPadrao.Fields.IND_FORMULA_PEDIATRICA.toString(),DominioSimNao.getInstance(indPediatrica).toString()));
		}
		if (filtro.getIndPadrao() != null) {
			boolean indPadrao;
			if(filtro.getIndPadrao().equalsIgnoreCase("Sim")){
				indPadrao = true;
			}else{
				indPadrao = false;
			}
			criteria.add(Restrictions.eq(AfaFormulaNptPadrao.Fields.IND_PADRAO.toString(), DominioSimNao.getInstance(indPadrao).toString()));
		}
		if (filtro.getIndSituacao() != null) {
			boolean indSituacao;
			if(filtro.getIndSituacao().equalsIgnoreCase("Ativo")){
				indSituacao = true;
			}else{
				indSituacao = false;
			}
			criteria.add(Restrictions.eq(AfaFormulaNptPadrao.Fields.IND_SITUACAO.toString(),DominioSituacao.getInstance(indSituacao).toString()));
		}
		
		return criteria;
	}
	
	public List<AfaFormulaNptPadrao> montaPesquisa(AfaFormulaNptPadrao filtro){
		DetachedCriteria criteria = consultaPesquisaGenerica(filtro);
		return executeCriteria(criteria); 
		
	}	
		
	// #990 C2
	public String verificarFormulaLivreOuPadrao(Short seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaFormulaNptPadrao.class);
		criteria.add(Restrictions.eq(AfaFormulaNptPadrao.Fields.SEQ.toString(), seq));
		criteria.setProjection(Projections.property(AfaFormulaNptPadrao.Fields.IND_PADRAO.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	public List<AfaFormulaNptPadrao> listarFormulaNptPadrao(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaFormulaNptPadrao.class,"AFN");
		
		criteria.createAlias("AFN."+ AfaFormulaNptPadrao.Fields.RAP_SERVIDORES_BY_AFA_FNP_SER_FK1.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER."+ RapServidores.Fields.PESSOA_FISICA.toString(), "SPF", JoinType.INNER_JOIN);
		
		criteria.setFetchMode("AFN."+ AfaFormulaNptPadrao.Fields.RAP_SERVIDORES_BY_AFA_FNP_SER_FK1.toString(), FetchMode.JOIN);
		criteria.setFetchMode("SER."+ RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq("AFN."+AfaFormulaNptPadrao.Fields.IND_SITUACAO.toString(),DominioSituacao.A.toString()));
		return executeCriteria(criteria); 
		
	}
	/**
	 * #990 -
	 * @param atdSeq
	 * @return
	 */
	public AfaFormulaNptPadrao obterFormulaPediatrica(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaFormulaNptPadrao.class);
		criteria.add(Restrictions.eq(AfaFormulaNptPadrao.Fields.IND_FORMULA_PEDIATRICA.toString(), "S"));
		return (AfaFormulaNptPadrao) executeCriteriaUniqueResult(criteria);
	}
	

}