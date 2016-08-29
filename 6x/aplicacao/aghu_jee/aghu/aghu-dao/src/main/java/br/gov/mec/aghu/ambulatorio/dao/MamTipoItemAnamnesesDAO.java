package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.AnamneseItemTipoItemVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamQuestionario;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;

public class MamTipoItemAnamnesesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTipoItemAnamneses> {
	
	private static final long serialVersionUID = -7715465163716444469L;

	public List<MamTipoItemAnamneses> buscaTipoItemAnamneseAtivoOrdenado(Integer seq){

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemAnamneses.class);
		criteria.add(Restrictions.eq(MamTipoItemAnamneses.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if (seq != null){
			criteria.add(Restrictions.eq(MamTipoItemAnamneses.Fields.SEQ.toString(), seq));	
		}
		
		criteria.addOrder(Order.asc(MamTipoItemAnamneses.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(MamTipoItemAnamneses.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MamTipoItemAnamneses> buscaTipoItemAnamnesePorCategoriaOrdenado(Integer seqCategoriaProfissional) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemAnamneses.class);
		criteria.add(Restrictions.eq(MamTipoItemAnamneses.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.ne(MamTipoItemAnamneses.Fields.SIGLA.toString(), "NA"));
		if (seqCategoriaProfissional != null) {
			criteria.add(Restrictions.eq(MamTipoItemAnamneses.Fields.CATEGORIA_PROFISSIONAL.toString() + ".seq", seqCategoriaProfissional));
		}

		criteria.addOrder(Order.asc(MamTipoItemAnamneses.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(MamTipoItemAnamneses.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}	
	
	public Long buscaTipoItemAnamnesePorCategoriaCount(Integer seqCategoriaProfissional) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemAnamneses.class);
		criteria = obterCriteriaPesquisaMamTipoItemAnamneses(seqCategoriaProfissional, criteria);
		return executeCriteriaCount(criteria);
	}
	
	public Long retornaQtdQuestionariosComTipoItemAnamnese(MamTipoItemAnamneses itemAnamnese) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestionario.class);
		criteria.add(Restrictions.eq(MamQuestionario.Fields.MAM_TIPO_ITEM_ANAMNESES.toString() + ".seq", itemAnamnese.getSeq()));
		return executeCriteriaCount(criteria);
	}

	protected DetachedCriteria obterCriteriaPesquisaMamTipoItemAnamneses(Integer seqCategoriaProfissional,
			DetachedCriteria criteria) {

		if (seqCategoriaProfissional != null) {
			criteria.add(Restrictions.eq(MamTipoItemAnamneses.Fields.CATEGORIA_PROFISSIONAL.toString() + ".seq", seqCategoriaProfissional));
		}
		return criteria;
	}

	public List<MamTipoItemAnamneses> pesquisarMamTipoItemAnamneses(Integer filtroSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemAnamneses.class);
		criteria = this.obterCriteriaPesquisaMamTipoItemAnamneses(filtroSeq, criteria);
		criteria.addOrder(Order.asc(MamTipoItemAnamneses.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(MamTipoItemAnamneses.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MamTipoItemAnamneses> pesquisarTipoItemAnamneseOrdenado() {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemAnamneses.class);
		criteria.addOrder(Order.asc(MamTipoItemAnamneses.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(MamTipoItemAnamneses.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * #50743 - C2 - Consulta que retorna os botões que serão apresentados para o perfil do usuário logado. 
	 * 				 Cada botão corresponde a um item de anamnese.
	 */
	public List<MamTipoItemAnamneses> pesquisarTipoItemAnamneseBotoes(Integer cagSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemAnamneses.class);
		criteria.add(Restrictions.eq(MamTipoItemAnamneses.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MamTipoItemAnamneses.Fields.CAG_SEQ.toString(), cagSeq));
		criteria.add(Restrictions.ne(MamTipoItemAnamneses.Fields.SIGLA.toString(), "NA"));
		criteria.add(Restrictions.ne(MamTipoItemAnamneses.Fields.SIGLA.toString(), "ID"));
		criteria.addOrder(Order.asc(MamTipoItemAnamneses.Fields.ORDEM.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * #50937
	 * Cursor: cur_ian
	 * @param anaSeq
	 * @param tinSeq
	 * @return
	 */
	public List<AnamneseItemTipoItemVO> obterTextoLivresPorAnaSeqTinSeq(Long anaSeq, Integer tinSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemAnamneses.class, "TIN");
		
		criteria.createAlias("TIN." + MamTipoItemAnamneses.Fields.ITENS_ANAMNESES.toString(), "IAN", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("IAN." + MamItemAnamneses.Fields.DESCRICAO.toString()), AnamneseItemTipoItemVO.Fields.DESCRICAO.toString())
				.add(Projections.property("TIN." + MamTipoItemAnamneses.Fields.PERMITE_LIVRE.toString()), AnamneseItemTipoItemVO.Fields.PERMITE_LIVRE.toString())
				.add(Projections.property("TIN." + MamTipoItemAnamneses.Fields.PERMITE_QUEST.toString()), AnamneseItemTipoItemVO.Fields.PERMITE_QUEST.toString())
				.add(Projections.property("TIN." + MamTipoItemAnamneses.Fields.PERMITE_FIGURA.toString()), AnamneseItemTipoItemVO.Fields.PERMITE_FIGURA.toString())
				.add(Projections.property("TIN." + MamTipoItemAnamneses.Fields.IDENTIFICACAO.toString()), AnamneseItemTipoItemVO.Fields.IDENTIFICACAO.toString())
		);
		
		criteria.add(Restrictions.eq("IAN." + MamItemAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		criteria.add(Restrictions.eq("IAN." + MamItemAnamneses.Fields.TIN_SEQ.toString(), tinSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AnamneseItemTipoItemVO.class));	

		return executeCriteria(criteria);
	}
	
}
