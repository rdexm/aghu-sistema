package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.ScoServicoCriteriaVO;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @modulo compras
 * @author cvagheti
 *
 */
public class ScoServicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoServico> {

	private static final long serialVersionUID = -1252366565424640129L;

	/**
	 * Pesquisar servico count.
	 * 
	 * @param codigo
	 *            the codigo
	 * @param grupoServico
	 *            the grupo servico
	 * @param nome
	 *            the nome
	 * @param situacao
	 *            the situacao
	 * @param descricao
	 *            the descricao
	 * @return the int
	 */
	public long pesquisarServicoCount(ScoServicoCriteriaVO criteria) {
		DetachedCriteria detached = this.pesquisarCriteria(criteria);

		return this.executeCriteriaCount(detached);
	}

	/**
	 * Pesquisar.
	 *
	 * @param firstResult the first result
	 * @param maxResult the max result
	 * @param orderProperty the order property
	 * @param asc the asc
	 * @param codigo the codigo
	 * @param grupoServico the grupo servico
	 * @param nome the nome
	 * @param situacao the situacao
	 * @param descricao the descricao
	 * @return the list
	 */
	public List<ScoServico> pesquisar(ScoServicoCriteriaVO criteria, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {

		DetachedCriteria detached = this.pesquisarCriteria(criteria);
		
		detached.createAlias(ScoServico.Fields.GRUPO_SERVICO.toString(), "gs", JoinType.LEFT_OUTER_JOIN );
		
		detached.createAlias(ScoServico.Fields.SERVIDOR.toString(), "serv", JoinType.LEFT_OUTER_JOIN );
		detached.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN );
		
		detached.createAlias(ScoServico.Fields.NATUREZA_DESPESA.toString(), "nat", JoinType.LEFT_OUTER_JOIN );
		detached.createAlias("nat."+FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "gru", JoinType.LEFT_OUTER_JOIN );
		
		detached.addOrder(Order.asc(ScoServico.Fields.CODIGO.toString()));

		return this.executeCriteria(detached, firstResult, maxResult,
				orderProperty, asc);
	}
	
	
	public List<ScoServico> listarServicosByNomeOrCodigoGrupoAtivo(Object param, ScoGrupoServico grupoSrv){
		List<ScoServico> lista = null;
		DetachedCriteria criteria = montarCriteriaScoServicoCodigoNome(param);	
		criteria.add(Restrictions.eq(ScoServico.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(grupoSrv!=null){
			criteria.add(Restrictions.eq(ScoServico.Fields.GRUPO_SERVICO.toString()+".codigo", grupoSrv.getCodigo()));
		}
		criteria.addOrder(Order.asc(ScoServico.Fields.CODIGO.toString()));
		lista = executeCriteria(criteria, 0, 100, null, true);
		return lista;
	}
	
	public Long listarServicosByNomeOrCodigoGrupoAtivoCount(Object param, ScoGrupoServico grupoSrv){
		DetachedCriteria criteria = montarCriteriaScoServicoCodigoNome(param);	
		criteria.add(Restrictions.eq(ScoServico.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(grupoSrv!=null){
			criteria.add(Restrictions.eq(ScoServico.Fields.GRUPO_SERVICO.toString()+".codigo", grupoSrv.getCodigo()));
		}
		
		return executeCriteriaCount(criteria);
		
	}

	
	
	/**
	 * Pesquisar criteria.
	 * 
	 * @param codigo
	 *            the codigo
	 * @param grupoServico
	 *            the grupo servico
	 * @param nome
	 *            the nome
	 * @param situacao
	 *            the situacao
	 * @param descricao
	 *            the descricao
	 * @return the detached criteria
	 */
	private DetachedCriteria pesquisarCriteria(ScoServicoCriteriaVO criteria) {
		DetachedCriteria detached = DetachedCriteria.forClass(ScoServico.class);

		if (criteria.getCodigo() != null) {
			detached.add(Restrictions.eq(ScoServico.Fields.CODIGO.toString(),criteria.getCodigo()));
		}

		if (StringUtils.isNotBlank(criteria.getNome())) {
			detached.add(Restrictions.ilike(ScoServico.Fields.NOME.toString(),	criteria.getNome(), MatchMode.ANYWHERE));
		}

		if (criteria.getSituacao() != null) {
			detached.add(Restrictions.eq(ScoServico.Fields.SITUACAO.toString(),	criteria.getSituacao()));
		}

		if (criteria.getGrupo() != null) {
			detached.add(Restrictions.eq(ScoServico.Fields.GRUPO_SERVICO.toString(),criteria.getGrupo()));
		}

		if(criteria.getGrupoNatureza() != null){
			detached.add(Restrictions.eq(ScoServico.Fields.GND_CODIGO.toString(), criteria.getGrupoNatureza().getCodigo()));
		} 
 
		if (criteria.getNatureza() != null) {
			detached.add(Restrictions.eq(ScoServico.Fields.NTD_CODIGO.toString(),criteria.getNatureza().getId().getCodigo()));
		}
		
		if (criteria.getCatSer() != null) {
            detached.add(Restrictions.eq(ScoServico.Fields.CATSER.toString(), criteria.getCatSer().getItCoServico()));
		}

		if (criteria.getContrato() != null) {
			detached.add(Restrictions.eq(ScoServico.Fields.IND_CONTRATO.toString(), criteria.getContrato().isSim()));
		}

		if (criteria.getDataCadastro() != null) {
			detached.add(Restrictions.eq(ScoServico.Fields.DT_DIGITACAO.toString(),	criteria.getDataCadastro()));
		}

		return detached;
	}
	
	/**
	 * Montar criteria sco servico nome ativa.
	 *
	 * @param objPesquisa the obj pesquisa
	 * @return the detached criteria
	 */
	private DetachedCriteria montarCriteriaScoServicoCodigoNome(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoServico.class);
		String strPesquisa = (String) objPesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(ScoServico.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(ScoServico.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}

	public List<ScoServico> listarServicosAtivos(Object pesquisa) {
		DetachedCriteria criteria = montarCriteriaScoServicoCodigoNome(pesquisa);
		criteria.add(Restrictions.eq(ScoServico.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(ScoServico.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
		
	}
	
	
	
	
	public List<ScoServico> listarServicos(Object pesquisa) {
		DetachedCriteria criteria = montarCriteriaScoServicoCodigoNome(pesquisa);
		criteria.addOrder(Order.asc(ScoServico.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
		
	}
	
	public List<ScoServico> listarServicosByCodigoGrupo(Integer gsvCod){
		List<ScoServico> lista = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoServico.class);	
		criteria.add(Restrictions.eq(ScoServico.Fields.GRUPO_SERVICO.toString()+".codigo", gsvCod));
		lista = executeCriteria(criteria);
		return lista;
	}
	
	/**
	 * Obtem lista de serviços para o perfil de engenharia
	 * @param codigo, se indEngenhia ou não
	 * @return
	 */
	public List<ScoServico> listarServicosEngenharia(Object pesquisa, String indEngenharia) {
		DetachedCriteria criteria = montarCriteriaScoServicoCodigoNome(pesquisa);
		criteria.createAlias(ScoServico.Fields.GRUPO_SERVICO.toString(), "GRU");
		
		if(indEngenharia!=null){
			if(indEngenharia.equalsIgnoreCase("S")){
				criteria.add(Restrictions.eq("GRU."+ScoGrupoServico.Fields.IND_ENGENHARIA.toString(),Boolean.TRUE));
			}else{
				criteria.add(Restrictions.eq("GRU."+ScoGrupoServico.Fields.IND_ENGENHARIA.toString(),Boolean.FALSE));
			}
		}
		
		criteria.addOrder(Order.asc(ScoServico.Fields.CODIGO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
		
	}
	

	/**
	 * Obtem lista de serviços para o perfil de engenharia
	 * @return
	 */
	public List<ScoServico> listarServicosEngenhariaPorGrupoEng() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoServico.class);
		criteria.createAlias(ScoServico.Fields.GRUPO_SERVICO.toString(), "GRU");
		criteria.add(Restrictions.eq("GRU."+ScoGrupoServico.Fields.IND_ENGENHARIA.toString(), Boolean.TRUE));
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Obtem lista de serviços Ativos para o perfil de engenharia
	 * @param codigo , se indEngenhia ou não
	 * @return
	 */
	public List<ScoServico> listarServicosEngenhariaAtivos(Object pesquisa, String indEngenharia) {
		DetachedCriteria criteria = montarCriteriaScoServicoCodigoNome(pesquisa);
		criteria.createAlias(ScoServico.Fields.GRUPO_SERVICO.toString(), "GRU");
		
		if(indEngenharia!=null && indEngenharia.equalsIgnoreCase("S")){
				criteria.add(Restrictions.eq("GRU."+ScoGrupoServico.Fields.IND_ENGENHARIA.toString(),Boolean.TRUE));
		}else if(indEngenharia!=null){
				criteria.add(Restrictions.eq("GRU."+ScoGrupoServico.Fields.IND_ENGENHARIA.toString(),Boolean.FALSE));
		}
		
		criteria.add(Restrictions.eq(ScoServico.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(ScoServico.Fields.CODIGO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
		
	}
	
	/**
	 * Obtem a quantidade da lista de serviços para o perfil de engenharia
	 * @param codigo
	 * @return
	 */
	public Long listarServicosEngenhariaCount(Object pesquisa, String indEngenharia) {
		DetachedCriteria criteria = montarCriteriaScoServicoCodigoNome(pesquisa);
		criteria.createAlias(ScoServico.Fields.GRUPO_SERVICO.toString(), "GRU");
		if(indEngenharia!=null && indEngenharia.equalsIgnoreCase("S")){
			criteria.add(Restrictions.eq("GRU."+ScoGrupoServico.Fields.IND_ENGENHARIA.toString(),Boolean.TRUE));
		}else if(indEngenharia!=null){
			criteria.add(Restrictions.eq("GRU."+ScoGrupoServico.Fields.IND_ENGENHARIA.toString(),Boolean.FALSE));
		}

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Obtem a quantidade da lista de serviços Ativos para o perfil de engenharia
	 * @param codigo
	 * @return
	 */
	public Long listarServicosEngenhariaAtivosCount(Object pesquisa, String indEngenharia) {
		DetachedCriteria criteria = montarCriteriaScoServicoCodigoNome(pesquisa);
		criteria.createAlias(ScoServico.Fields.GRUPO_SERVICO.toString(), "GRU");
		if(indEngenharia!=null && indEngenharia.equalsIgnoreCase("S")){
			criteria.add(Restrictions.eq("GRU."+ScoGrupoServico.Fields.IND_ENGENHARIA.toString(),Boolean.TRUE));
		}else if(indEngenharia!=null){
			criteria.add(Restrictions.eq("GRU."+ScoGrupoServico.Fields.IND_ENGENHARIA.toString(),Boolean.FALSE));
		}
		criteria.add(Restrictions.eq(ScoServico.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
	 
	/**
	 * Obtem um serviço por código
	 * @param codigo
	 * @return
	 */
	public ScoServico obterServicoPorId(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoServico.class);
		criteria.add(Restrictions.eq(ScoServico.Fields.CODIGO.toString(),codigo));
		return (ScoServico)executeCriteriaUniqueResult(criteria);
	}
	
	public Long listarServicosAtivosCount(Object pesquisa) {
		DetachedCriteria criteria = montarCriteriaScoServicoCodigoNome(pesquisa);

		criteria.add(Restrictions.eq(ScoServico.Fields.SITUACAO.toString(),	DominioSituacao.A));

		return this.executeCriteriaCount(criteria);
	}
	
	public Long listarServicosCount(Object pesquisa) {
		DetachedCriteria criteria = montarCriteriaScoServicoCodigoNome(pesquisa);
		return this.executeCriteriaCount(criteria);
	}
	
	public ScoServico buscarServico(Integer numeroPac, Short item) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoServico.class, "SRV");
		criteria.createAlias("SRV."+ScoServico.Fields.SOLICITACAO_SERVICO.toString(), "SLS");
		criteria.createAlias("SLS."+ScoSolicitacaoServico.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), numeroPac));
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString(), item));
		return (ScoServico) executeCriteriaUniqueResult(criteria);
	}
	
	public DetachedCriteria obterCriteriaSuggestionServico(Object pesquisa){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoServico.class, "SRV");
		criteria.createAlias("SRV."+ScoServico.Fields.GRUPO_SERVICO, "GRV");
		criteria.add(Restrictions.eq("GRV."+ScoGrupoServico.Fields.IND_GERA_TITULO_AVULSO, Boolean.FALSE));
		
		String strPesquisa = (String) pesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(ScoServico.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(ScoServico.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	public List<ScoServico> listarSuggestionServicos(Object pesquisa) {
		DetachedCriteria criteria = obterCriteriaSuggestionServico(pesquisa);		
		criteria.addOrder(Order.asc(ScoServico.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long listarSuggestionServicosCount(Object pesquisa) {
		DetachedCriteria criteria = obterCriteriaSuggestionServico(pesquisa);		
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * #46298 - Obtem Lista de Serviços Ativos por Codigo ou Nome
	 */
	public List<ScoServico> obterListaServicosAtivosPorCodigoOuNome(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoServico.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)){
				criteria.add(Restrictions.eq(ScoServico.Fields.CODIGO.toString(), Integer.valueOf(filter)));
			} else {
				criteria.add(Restrictions.ilike(ScoServico.Fields.NOME.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(ScoServico.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria, 0, 100, ScoServico.Fields.NOME.toString(), true);
	}
	
	/**
	 * #46298 - Obtem Count de Serviços Ativos por Codigo ou Nome
	 */
	public Long obterCountServicosAtivosPorCodigoOuNome(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoServico.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)){
				criteria.add(Restrictions.eq(ScoServico.Fields.CODIGO.toString(), Integer.valueOf(filter)));
			} else {
				criteria.add(Restrictions.ilike(ScoServico.Fields.NOME.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(ScoServico.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
}