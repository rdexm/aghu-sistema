package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSituacaoContaApac;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaHistoricoVO;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.FatAtendimentoApacProcHosp;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorCBuscaOutraQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = -303667807356346011L;
	
	private static final String ALIAS_AAP = "AAP"; 
	private static final String ALIAS_ATM = "ATM"; 
	private static final String ALIAS_CAP = "CAP"; 
	
	private DetachedCriteria criteria;
	private Long cAtmNumero;
	private Date cDtInicioValidade;
	private String cCapType;
	private Integer cPacCodigo;
	private Integer cPhiSeq;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(FatAtendimentoApacProcHosp.class, "AAP");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFiltro();
		setProjecao();
		setOrder();
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_CAP+"."+FatContaApac.Fields.COD_PACIENTE.toString(), this.cPacCodigo));
		criteria.add(Restrictions.eq(ALIAS_CAP+"."+FatContaApac.Fields.CAP_TYPE.toString(), this.cCapType));
		criteria.add(Restrictions.eq(ALIAS_CAP+"."+FatContaApac.Fields.IND_SITUACAO.toString(), DominioSituacaoContaApac.E));
		criteria.add(Restrictions.ne(ALIAS_CAP+"."+FatContaApac.Fields.ATM_CODIGO.toString(), this.cAtmNumero));
		criteria.add(Restrictions.lt(ALIAS_CAP+"."+FatContaApac.Fields.DT_INICIO_VALIDADE.toString(), this.cDtInicioValidade));
		criteria.add(Restrictions.eq(ALIAS_ATM+"."+AacAtendimentoApacs.Fields.IND_AUTORIZADO_SMS.toString(), "S"));
		criteria.add(Restrictions.eq(ALIAS_AAP+"."+FatAtendimentoApacProcHosp.Fields.PHI_SEQ.toString(), this.cPhiSeq));
		criteria.add(Restrictions.eq(ALIAS_AAP+"."+FatAtendimentoApacProcHosp.Fields.IND_PRIORIDADE.toString(), DominioPrioridadeCid.P));
	}
	
	private void setProjecao() {
		criteria.setProjection((Projections.projectionList()
				.add(Projections.property(ALIAS_AAP+"."+FatAtendimentoApacProcHosp.Fields.PHI_SEQ.toString()), CursorBuscaHistoricoVO.Fields.PHI_SEQ.toString())));
		criteria.setResultTransformer(Transformers.aliasToBean(CursorBuscaHistoricoVO.class));
	}
	
	private void setJoin(){
		criteria.createAlias(ALIAS_AAP+"."+FatAtendimentoApacProcHosp.Fields.ATENDIMENTO_APAC.toString(), ALIAS_ATM, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_ATM+"."+AacAtendimentoApacs.Fields.FAT_CONTA_APAC.toString(), ALIAS_CAP, JoinType.INNER_JOIN);
	}
	
	private void setOrder(){
		criteria.addOrder(Order.desc(ALIAS_CAP+"."+FatContaApac.Fields.DT_INICIO_VALIDADE.toString()));
	}
	
	public DetachedCriteria build(Long cAtmNumero, Date cDtInicioValidade, 
			String cCapType, Integer cPacCodigo, Integer cPhiSeq) {
		this.cAtmNumero = cAtmNumero;
		this.cDtInicioValidade = cDtInicioValidade;
		this.cCapType = cCapType;
		this.cPacCodigo = cPacCodigo;
		this.cPhiSeq = cPhiSeq;
		
		return super.build();
	}

	public DetachedCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetachedCriteria criteria) {
		this.criteria = criteria;
	}

	public Long getcAtmNumero() {
		return cAtmNumero;
	}

	public void setcAtmNumero(Long cAtmNumero) {
		this.cAtmNumero = cAtmNumero;
	}

	public Date getcDtInicioValidade() {
		return cDtInicioValidade;
	}

	public void setcDtInicioValidade(Date cDtInicioValidade) {
		this.cDtInicioValidade = cDtInicioValidade;
	}

	public String getcCapType() {
		return cCapType;
	}

	public void setcCapType(String cCapType) {
		this.cCapType = cCapType;
	}

	public Integer getcPacCodigo() {
		return cPacCodigo;
	}

	public void setcPacCodigo(Integer cPacCodigo) {
		this.cPacCodigo = cPacCodigo;
	}

	public Integer getcPhiSeq() {
		return cPhiSeq;
	}

	public void setcPhiSeq(Integer cPhiSeq) {
		this.cPhiSeq = cPhiSeq;
	}
}
