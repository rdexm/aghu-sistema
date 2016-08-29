package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatFormaOrganizacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatFormaOrganizacao> {

	private static final long serialVersionUID = 1174116883848352353L;

	private DetachedCriteria obterDetachedCrieriaFatFormaOrganizacao(){
		return  DetachedCriteria.forClass(FatFormaOrganizacao.class);
	}

	private DetachedCriteria montarCriteriaFormaOrganizacaoPorGrupoESubGrupo(final Short grpSeq, final Byte subGrupo){
		final DetachedCriteria criteria = this.obterDetachedCrieriaFatFormaOrganizacao();
		criteria.add(Restrictions.eq(FatFormaOrganizacao.Fields.ID_SGR_GRP_SEQ.toString(), grpSeq));
		criteria.add(Restrictions.eq(FatFormaOrganizacao.Fields.ID_SGR_SUB_GRUPO.toString(), subGrupo));

		return criteria;
	}

	/**
	 * Metodo para montar uma criteria para pesquisar por Formas Organizacao,
	 * filtrando pela descricao ou pelo codigo, em um subconjunto de Formas Organizacao pertencente a um determinado grupo e subgrupo.
	 * @param objPesquisa
	 * @return DetachedCriteria
	 */
	private DetachedCriteria montarCriteriaListarSubGruposPorSeqOuDescricao(final Object objPesquisa, final Short grpSeq, final Byte subGrupo){
		final DetachedCriteria criteria = this.montarCriteriaFormaOrganizacaoPorGrupoESubGrupo(grpSeq, subGrupo);
		final String strPesquisa = (String) objPesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(FatFormaOrganizacao.Fields.ID_CODIGO.toString(), Byte.valueOf(strPesquisa)));

		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(FatFormaOrganizacao.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.START));
		}
		return criteria;
	}

	/**
	 * Monta pesquisa por registros ativos. 
	 * @param objPesquisa
	 * @param grpSeq
	 * @param subGrupo
	 * @return
	 */
	private DetachedCriteria montarCriteriaListarSubGruposAtivosPorSeqOuDescricao(final Object objPesquisa, final Short grpSeq, final Byte subGrupo){
		final DetachedCriteria criteria = this.montarCriteriaFormaOrganizacaoPorGrupoESubGrupo(grpSeq, subGrupo);
		final String strPesquisa = (String) objPesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(FatFormaOrganizacao.Fields.ID_CODIGO.toString(), Byte.valueOf(strPesquisa)));

		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(FatFormaOrganizacao.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(FatFormaOrganizacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
	/**
	 * Metodo listar formas organizacao,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return
	 */
	public List<FatFormaOrganizacao> listarFormasOrganizacaoPorCodigoOuDescricao(final Object objPesquisa, final Short grpSeq, final Byte subGrupo){
		final DetachedCriteria criteria = this.montarCriteriaListarSubGruposPorSeqOuDescricao(objPesquisa, grpSeq, subGrupo);

		criteria.addOrder(Order.asc(FatFormaOrganizacao.Fields.ID_CODIGO.toString()));
		criteria.addOrder(Order.asc(FatFormaOrganizacao.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);

	}

	/**
	 * 
	 * @param objPesquisa
	 * @param grpSeq
	 * @param subGrupo
	 * @return
	 */
	public List<FatFormaOrganizacao> listarFormasOrganizacaoAtivosPorCodigoOuDescricao(final Object objPesquisa, final Short grpSeq, final Byte subGrupo){
		final DetachedCriteria criteria = this.montarCriteriaListarSubGruposAtivosPorSeqOuDescricao(objPesquisa, grpSeq, subGrupo);

		criteria.addOrder(Order.asc(FatFormaOrganizacao.Fields.ID_CODIGO.toString()));
		criteria.addOrder(Order.asc(FatFormaOrganizacao.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);

	}
	
	/**
	 * Metodo que retorna o count de formas organizacao,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return
	 */
	public Long listarFormasOrganizacaoPorCodigoOuDescricaoCount(final Object objPesquisa,final Short grpSeq, final Byte subGrupo){

		final DetachedCriteria criteria = this.montarCriteriaListarSubGruposPorSeqOuDescricao(objPesquisa, grpSeq, subGrupo);

		return executeCriteriaCount(criteria);

	}

	public List<FatFormaOrganizacao> listarTodosFatModalidadeAtendimento() {
		return executeCriteria(DetachedCriteria.forClass(FatFormaOrganizacao.class));
	}

}
