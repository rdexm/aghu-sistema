package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.CursorCurTicketVO;
import br.gov.mec.aghu.ambulatorio.vo.MamSolicitacaoRetornoDocumentoImpressoVO;
import br.gov.mec.aghu.model.MamSolicitacaoRetorno;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class MamSolicitacaoRetornoDocumentoImpressoQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = -3630160309412856972L;

	private final static String ALIAS_SOR = "SOR";
	
	private DetachedCriteria criteria;
	private Integer conNumero;
	private boolean cursor;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(MamSolicitacaoRetorno.class, "SOR");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setProjecao();
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.CON_NUMERO.toString(), this.conNumero));
		criteria.add(Restrictions.isNull(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.in(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.IND_PENDENTE.toString(), new String[]{"V","P"}));
	}
	
	private void setProjecao() {
		ProjectionList listProj = Projections.projectionList();
		
		if(cursor){
			listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.SEQ.toString()), 
					CursorCurTicketVO.Fields.SEQ.toString());
			listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.IND_PENDENTE.toString()), 
					CursorCurTicketVO.Fields.IND_PENDENTE.toString());
			listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.IND_IMPRESSO.toString()), 
					CursorCurTicketVO.Fields.IND_IMPRESSO.toString());
			criteria.setProjection(listProj);
			criteria.setResultTransformer(Transformers.aliasToBean(CursorCurTicketVO.class));
		}else{
			listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.SEQ.toString()), 
					MamSolicitacaoRetornoDocumentoImpressoVO.Fields.SEQ.toString());
			listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.IND_PENDENTE.toString()), 
					MamSolicitacaoRetornoDocumentoImpressoVO.Fields.IND_PENDENTE.toString());
			criteria.setProjection(listProj);
			criteria.setResultTransformer(Transformers.aliasToBean(MamSolicitacaoRetornoDocumentoImpressoVO.class));
		}
	}

	public DetachedCriteria build(Integer conNumero, boolean cursor) {
		this.conNumero = conNumero;
		this.cursor = cursor;
		
		return super.build();
	}

	public DetachedCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetachedCriteria criteria) {
		this.criteria = criteria;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public boolean isCursor() {
		return cursor;
	}

	public void setCursor(boolean cursor) {
		this.cursor = cursor;
	}
}
