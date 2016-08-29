package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class SigCategoriaConsumosDAO extends BaseDao<SigCategoriaConsumos> {

	private static final long serialVersionUID = 6861878683792306737L;

	public DetachedCriteria obterDetachedCriteria(){
		return DetachedCriteria.forClass(SigCategoriaConsumos.class);
	}

	public List<SigCategoriaConsumos> buscaCategoriasDeConsumo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String descricao, DominioIndContagem indContagem, DominioSituacao situacao) {
		DetachedCriteria criteria = obterDetachedCriteria();
		if(descricao != null && !descricao.isEmpty()){
			criteria.add(Restrictions.like(SigCategoriaConsumos.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(indContagem != null){
			criteria.add(Restrictions.eq(SigCategoriaConsumos.Fields.IND_CONTAGEM.toString(), indContagem));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), situacao));
		}
		criteria.addOrder(Order.asc(SigCategoriaConsumos.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long buscaCategoriasDeConsumoCount(String descricao, DominioIndContagem indContagem, DominioSituacao situacao) {
		DetachedCriteria criteria = obterDetachedCriteria();
		if(descricao != null && !descricao.isEmpty()){
			criteria.add(Restrictions.like(SigCategoriaConsumos.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(indContagem != null){
			criteria.add(Restrictions.eq(SigCategoriaConsumos.Fields.IND_CONTAGEM.toString(), indContagem));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), situacao));
		}
		return this.executeCriteriaCount(criteria);
	}

	public boolean verificaCategoriaConsumoCadastrada(SigCategoriaConsumos categoriaConsumo) {
		DetachedCriteria criteria = obterDetachedCriteria();
		criteria.add(Restrictions.like(SigCategoriaConsumos.Fields.DESCRICAO.toString(), categoriaConsumo.getDescricao()));
		return this.executeCriteriaExists(criteria);
	}

	public boolean verificaContagemVinculadaCategoria(SigCategoriaConsumos categoriaConsumo) {
		DetachedCriteria criteria = obterDetachedCriteria();
		criteria.add(Restrictions.like(SigCategoriaConsumos.Fields.IND_CONTAGEM.toString(), categoriaConsumo.getIndContagem()));
		return this.executeCriteriaExists(criteria);
	}

	public boolean verificaCategoriaConsumoCadastradaAlteracao(	SigCategoriaConsumos categoriaConsumo) {
		DetachedCriteria criteria = obterDetachedCriteria();
		criteria.add(Restrictions.ne(SigCategoriaConsumos.Fields.SEQ.toString(), categoriaConsumo.getSeq()));
		criteria.add(Restrictions.like(SigCategoriaConsumos.Fields.DESCRICAO.toString(), categoriaConsumo.getDescricao()));
		return this.executeCriteriaExists(criteria);
	}
	
	public SigCategoriaConsumos obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem dominio){
		DetachedCriteria criteria = obterDetachedCriteria();
		criteria.add(Restrictions.eq(SigCategoriaConsumos.Fields.IND_CONTAGEM.toString(), dominio));
		return (SigCategoriaConsumos)this.executeCriteriaUniqueResult(criteria);
	}
	
	public boolean validaOrdemVisualizacaoCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) {
		DetachedCriteria criteria = obterDetachedCriteria();
		if(categoriaConsumo.getSeq() != null) {
			criteria.add(Restrictions.ne(SigCategoriaConsumos.Fields.SEQ.toString(), categoriaConsumo.getSeq()));
		}
		criteria.add(Restrictions.eq(SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString(), categoriaConsumo.getOrdemVisualizacao()));
		criteria.add(Restrictions.eq(SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteriaExists(criteria);
	}
}
