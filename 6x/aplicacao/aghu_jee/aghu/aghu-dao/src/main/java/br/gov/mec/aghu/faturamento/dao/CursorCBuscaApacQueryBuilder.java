package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoContaApac;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaApacVO;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.FatAtendimentoApacProcHosp;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorCBuscaApacQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = 4820718179149394448L;

	private static final String ALIAS_AAP = "AAP"; 
	private static final String ALIAS_ATM = "ATM"; 
	private static final String ALIAS_CAP = "CAP"; 
	private static final String ALIAS_CPE = "CPE"; 
	
	private DetachedCriteria criteria;
	private Integer cPacCodigo;
	private Byte indTipoTratamento;	
	
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
		List<DominioModuloCompetencia> listaDominioModulo = new ArrayList<DominioModuloCompetencia>();
		listaDominioModulo.add(DominioModuloCompetencia.APAC);
		listaDominioModulo.add(DominioModuloCompetencia.APAR);
		
		criteria.add(Restrictions.eq(ALIAS_CAP+"."+FatContaApac.Fields.COD_PACIENTE.toString(), this.cPacCodigo));
		criteria.add(Restrictions.eq(ALIAS_CAP+"."+FatContaApac.Fields.IND_SITUACAO.toString(), DominioSituacaoContaApac.A));
		criteria.add(Restrictions.eq(ALIAS_ATM+"."+AacAtendimentoApacs.Fields.IND_TIPO_TRATAMENTO.toString(), this.indTipoTratamento));
		criteria.add(Restrictions.eq(ALIAS_AAP+"."+FatAtendimentoApacProcHosp.Fields.IND_PRIORIDADE.toString(), DominioPrioridadeCid.P));
		criteria.add(Restrictions.in(ALIAS_CPE+"."+FatCompetencia.Fields.MODULO.toString(), listaDominioModulo));
		criteria.add(Restrictions.eq(ALIAS_CPE+"."+FatCompetencia.Fields.IND_SITUACAO.toString(), DominioSituacaoCompetencia.A));
	}
	
	private void setProjecao() {
		criteria.setProjection((Projections.projectionList()
				.add(Projections.property(ALIAS_CAP+"."+FatContaApac.Fields.SEQP.toString()), CursorBuscaApacVO.Fields.SEQP.toString())
				.add(Projections.property(ALIAS_CAP+"."+FatContaApac.Fields.DT_INICIO_VALIDADE.toString()), CursorBuscaApacVO.Fields.DT_INICIO_VALIDADE.toString())
				.add(Projections.property(ALIAS_CAP+"."+FatContaApac.Fields.CAP_TYPE.toString()), CursorBuscaApacVO.Fields.CAP_TYPE.toString())
				.add(Projections.property(ALIAS_ATM+"."+AacAtendimentoApacs.Fields.IND_AUTORIZADO_SMS.toString()), CursorBuscaApacVO.Fields.IND_AUTORIZADO_SMS.toString())
				.add(Projections.property(ALIAS_ATM+"."+AacAtendimentoApacs.Fields.NUMERO.toString()), CursorBuscaApacVO.Fields.NUMERO.toString())
				.add(Projections.property(ALIAS_ATM+"."+AacAtendimentoApacs.Fields.DT_INICIO.toString()), CursorBuscaApacVO.Fields.DT_INICIO_VALIDADE.toString())
				.add(Projections.property(ALIAS_AAP+"."+FatAtendimentoApacProcHosp.Fields.PHI_SEQ.toString()), CursorBuscaApacVO.Fields.PHI_SEQ.toString())
				));
		criteria.setResultTransformer(Transformers.aliasToBean(CursorBuscaApacVO.class));
	}
	
	private void setJoin(){
		criteria.createAlias(ALIAS_AAP+"."+FatAtendimentoApacProcHosp.Fields.ATENDIMENTO_APAC.toString(), ALIAS_ATM, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_ATM+"."+AacAtendimentoApacs.Fields.FAT_CONTA_APAC.toString(), ALIAS_CAP, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_CAP+"."+FatContaApac.Fields.FAT_COMPETENCIA.toString(), ALIAS_CPE, JoinType.INNER_JOIN);
	}
	
	private void setOrder(){
		criteria.addOrder(Order.desc(ALIAS_ATM+"."+AacAtendimentoApacs.Fields.CRIADO_EM.toString()));
	}
	
	public DetachedCriteria build(Integer cPacCodigo, Byte indTipoTratamento) {
		this.cPacCodigo = cPacCodigo;
		this.indTipoTratamento = indTipoTratamento;
		return super.build();
	}

	public DetachedCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetachedCriteria criteria) {
		this.criteria = criteria;
	}

	public Integer getcPacCodigo() {
		return cPacCodigo;
	}

	public void setcPacCodigo(Integer cPacCodigo) {
		this.cPacCodigo = cPacCodigo;
	}

	public Byte getIndTipoTratamento() {
		return indTipoTratamento;
	}

	public void setIndTipoTratamento(Byte indTipoTratamento) {
		this.indTipoTratamento = indTipoTratamento;
	}
}
