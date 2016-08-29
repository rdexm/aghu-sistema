package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;

@SuppressWarnings({"PMD.CyclomaticComplexity","PMD.NPathComplexity"})
public class AelSitItemSolicitacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSitItemSolicitacoes> {

	
	private static final long serialVersionUID = -2296926984406140321L;

	public List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesPorParametro(String parametro){
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.in(AelSitItemSolicitacoes.Fields.CODIGO.toString(), new String[]{"AX","AE","AG"}));
		
    	//Popula criteria com dados do elemento
    	if(parametro != null && !"".equals(parametro)) {
    		Criterion c1 = Restrictions.ilike(AelSitItemSolicitacoes.Fields.CODIGO.toString(), parametro, MatchMode.ANYWHERE);
    		Criterion c2 = Restrictions.ilike(AelSitItemSolicitacoes.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE);
			
    		criteria.add(Restrictions.or(c1, c2));
    		
    		return executeCriteria(criteria);
    	}
    	
    	return executeCriteria(criteria);
	}
	
	public List<AelSitItemSolicitacoes> pesquisarSitItemSolicitacoesPorCodigoOuDescricao(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSitItemSolicitacoes.class);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.or(Restrictions.ilike(AelSitItemSolicitacoes.Fields.DESCRICAO.toString(), strPesquisa,MatchMode.ANYWHERE), 
				Restrictions.eq(AelSitItemSolicitacoes.Fields.CODIGO.toString(), strPesquisa)));
		}
		
		criteria.addOrder(Order.asc(AelSitItemSolicitacoes.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesPorParametro(String parametro, String... codigosRestritivos ){
		DetachedCriteria criteria = obterCriteria();
		
		if (codigosRestritivos != null && codigosRestritivos.length > 0) {
			criteria.add(Restrictions.in(AelSitItemSolicitacoes.Fields.CODIGO.toString(), codigosRestritivos));
		}
		
    	//Popula criteria com dados do elemento
    	if(parametro != null && !"".equals(parametro)) {
    		Criterion c1 = Restrictions.ilike(AelSitItemSolicitacoes.Fields.CODIGO.toString(), parametro, MatchMode.ANYWHERE);
    		Criterion c2 = Restrictions.ilike(AelSitItemSolicitacoes.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE);
			
    		criteria.add(Restrictions.or(c1, c2));
    		
    		return executeCriteria(criteria);
    	}
    	
    	return executeCriteria(criteria);
	}
	
	public List<AacConsultas> listarSituacaoExames(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSitItemSolicitacoes.class);
				
		return executeCriteria(criteria);
	}
		
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSitItemSolicitacoes.class);
		return criteria;
    }
	
	public AelSitItemSolicitacoes obterPeloId(String codigo) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelSitItemSolicitacoes.Fields.CODIGO.toString(), codigo));
		return (AelSitItemSolicitacoes) executeCriteriaUniqueResult(criteria);
	}
	
	/*public AelSitItemSolicitacoes obterOriginal(String codigo) {
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AelSitItemSolicitacoes.Fields.DESCRICAO.toString());
		hql.append(", o.").append(AelSitItemSolicitacoes.Fields.SITUACAO.toString());
		hql.append(", o.").append(AelSitItemSolicitacoes.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelSitItemSolicitacoes.Fields.SERVIDOR.toString());
		hql.append(", o.").append(AelSitItemSolicitacoes.Fields.PERMITE_MANTER_RESULTADO.toString());
		hql.append(", o.").append(AelSitItemSolicitacoes.Fields.ALERTA_EXAME_JA_SOLIC.toString());
		hql.append(" from ").append(AelSitItemSolicitacoes.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(AelSitItemSolicitacoes.Fields.CODIGO.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", codigo);
		
		
		AelSitItemSolicitacoes retorno = null;
		Object[] campos = (Object[]) query.getSingleResult();
		 
		if(campos != null) {
			retorno = new AelSitItemSolicitacoes();

			retorno.setCodigo(codigo);
			retorno.setDescricao((String) campos[0]);
			retorno.setIndSituacao((DominioSituacao) campos[1]);
			retorno.setCriadoEm((Date) campos[2]);
			retorno.setServidor((RapServidores) campos[3]);
			retorno.setIndPermiteManterResultado((Boolean) campos[4]);
			retorno.setIndAlertaExameJaSolic((Boolean) campos[5]);
		}		
		
		return retorno;
	}*/
	
	private DetachedCriteria criarCriteria(AelSitItemSolicitacoes elemento) {
    	DetachedCriteria criteria = obterCriteria();
    	
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		
    		//Código
    		if(StringUtils.isNotBlank(elemento.getCodigo())) {
    			criteria.add(Restrictions.eq(AelSitItemSolicitacoes.Fields.CODIGO.toString(), elemento.getCodigo().toUpperCase()));
    		}
    		
    		//Descrição
    		if(StringUtils.isNotBlank(elemento.getDescricao())) {
				criteria.add(Restrictions.ilike(AelSitItemSolicitacoes.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
			}
			
			//Ativo
			if(elemento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(AelSitItemSolicitacoes.Fields.SITUACAO.toString(), elemento.getIndSituacao()));
			}
    		
    	}
    	
    	return criteria;
    }
	
	public List<AelSitItemSolicitacoes> listar(AelSitItemSolicitacoes elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		
		return executeCriteria(criteria);
	}
	
	public List<AelSitItemSolicitacoes> listarTodos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSitItemSolicitacoes.class);
		
		criteria.addOrder(Order.asc(AelSitItemSolicitacoes.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AelSitItemSolicitacoes> listarTodosPorSituacaoEMostrarSolicExames(DominioSituacao indSituacao, Boolean indMostraSolicitarExames) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSitItemSolicitacoes.class);

		criteria.add(Restrictions.eq(AelSitItemSolicitacoes.Fields.SITUACAO.toString(), indSituacao));
		criteria.add(Restrictions.eq(AelSitItemSolicitacoes.Fields.MOSTRA_SOLICITAR_EXAMES.toString(), indMostraSolicitarExames));

		criteria.addOrder(Order.asc(AelSitItemSolicitacoes.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}
	
	public List<AelSitItemSolicitacoes> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelSitItemSolicitacoes elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarCount(AelSitItemSolicitacoes elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}
	
	public List<AelSitItemSolicitacoes> listarSituacoesItensSolicitacaoAtivos() {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelSitItemSolicitacoes.class);

		criteria.add(Restrictions.eq(
				AelSitItemSolicitacoes.Fields.SITUACAO.toString(),
				DominioSituacao.A));

		return executeCriteria(criteria);
	}

	public List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesParaExamesPendentes(String parametro) {
		DetachedCriteria criteria = obterCriteriaParaExamesPendentes(parametro, true);
    	return executeCriteria(criteria);
	}

	public Long buscarListaAelSitItemSolicitacoesParaExamesPendentesCount(String parametro) {
		DetachedCriteria criteria = obterCriteriaParaExamesPendentes(parametro, false);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaParaExamesPendentes(String parametro, Boolean ordenar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSitItemSolicitacoes.class);
		
		criteria.add(Restrictions.not(Restrictions.in(AelSitItemSolicitacoes.Fields.CODIGO.toString(),
				new String[]{"CA", "EO", "AC","AG", "AX", "RE", "PE", "AE", "EX"})));
		
    	//Popula criteria com dados do elemento
    	if(parametro != null && !"".equals(parametro)) {
    		Criterion c1 = Restrictions.ilike(AelSitItemSolicitacoes.Fields.CODIGO.toString(), parametro, MatchMode.ANYWHERE);
    		Criterion c2 = Restrictions.ilike(AelSitItemSolicitacoes.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE);
			
    		criteria.add(Restrictions.or(c1, c2));
    	}
    	if (ordenar.equals(Boolean.TRUE)) {
    		criteria.addOrder(Order.asc(AelSitItemSolicitacoes.Fields.CODIGO.toString()));
    	}
    	
		return criteria;
	}
	
}
