package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.CursorCTicketRetornoVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamSolicitacaoRetorno;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorCTicketRetornoVOQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	private static final long serialVersionUID = 4759956926993985558L;
	private final static String ALIAS_SOR = "SOR";
	private final static String ALIAS_PAC = "PAC";
	
	private DetachedCriteria criteria;
	private Long cSorSeq;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(MamSolicitacaoRetorno.class, "SOR");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setProjecao();
		setJoin();
	}
	
	private void setJoin(){
		criteria.createAlias(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.AIP_PACIENTES.toString(), ALIAS_PAC, JoinType.INNER_JOIN);
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.SEQ.toString(), this.cSorSeq));
	}
	
	private void setProjecao() {
		ProjectionList listProj = Projections.projectionList();
		
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.CON_NUMERO_RETORNO.toString()), 
				CursorCTicketRetornoVO.Fields.CON_NUMERO_RETORNO.toString());
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.IND_QTDE_MESES.toString()), 
				CursorCTicketRetornoVO.Fields.IND_QTDE_MESES.toString());
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.QTDE_MESES.toString()), 
				CursorCTicketRetornoVO.Fields.QTDE_MESES.toString());
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.IND_APOS_EXAMES.toString()), 
				CursorCTicketRetornoVO.Fields.IND_APOS_EXAMES.toString());
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.IND_DATA.toString()), 
				CursorCTicketRetornoVO.Fields.IND_DATA.toString());
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.DATA_RETORNO.toString()), 
				CursorCTicketRetornoVO.Fields.DATA_RETORNO.toString());
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.IND_SE_NECESSARIO.toString()), 
				CursorCTicketRetornoVO.Fields.IND_SE_NECESSARIO.toString());
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.IND_DIA_SEMANA.toString()), 
				CursorCTicketRetornoVO.Fields.IND_DIA_SEMANA.toString());
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.DIA_SEMANA.toString()), 
				CursorCTicketRetornoVO.Fields.DIA_SEMANA.toString());
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.EQP_SEQ.toString()), 
				CursorCTicketRetornoVO.Fields.EQP_SEQ.toString());
		listProj.add(Projections.property(ALIAS_SOR+"."+MamSolicitacaoRetorno.Fields.ESP_SEQ.toString()), 
				CursorCTicketRetornoVO.Fields.ESP_SEQ.toString());
		listProj.add(Projections.property(ALIAS_PAC+"."+AipPacientes.Fields.CODIGO.toString()), 
				CursorCTicketRetornoVO.Fields.CODIGO.toString());
		listProj.add(Projections.property(ALIAS_PAC+"."+AipPacientes.Fields.PRONTUARIO.toString()), 
				CursorCTicketRetornoVO.Fields.PRONTUARIO.toString());
		listProj.add(Projections.property(ALIAS_PAC+"."+AipPacientes.Fields.NOME.toString()), 
				CursorCTicketRetornoVO.Fields.NOME.toString());
		
		criteria.setProjection(listProj);
		criteria.setResultTransformer(Transformers.aliasToBean(CursorCTicketRetornoVO.class));
	}

	public DetachedCriteria build(Long cSorSeq) {
		this.cSorSeq = cSorSeq;
		
		return super.build();
	}

	public DetachedCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetachedCriteria criteria) {
		this.criteria = criteria;
	}

	public Long getcSorSeq() {
		return cSorSeq;
	}

	public void setcSorSeq(Long cSorSeq) {
		this.cSorSeq = cSorSeq;
	}
}
