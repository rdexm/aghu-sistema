package br.gov.mec.aghu.faturamento.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.faturamento.vo.CursorCTrpVO;
import br.gov.mec.aghu.model.FatTipoTratamentos;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorCTrpQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = -303667807356346011L;

	private static final String ALIAS_TPT = "TPT"; 
	private static final String ALIAS_TRP = "TRP"; 
	
	private DetachedCriteria criteria;
	private Integer cPacCodigo;
	private Byte indTipoTratamento;		
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(FatTipoTratamentos.class, "TPT");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFiltro();
		setProjecao();
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_TRP+"."+MptTratamentoTerapeutico.Fields.PAC_CODIGO.toString(), this.cPacCodigo));
		criteria.add(Restrictions.eq(ALIAS_TPT+"."+FatTipoTratamentos.Fields.CODIGO.toString(), Integer.valueOf(this.indTipoTratamento)));
		criteria.add(Restrictions.isNull(ALIAS_TRP+"."+MptTratamentoTerapeutico.Fields.DTHR_FIM.toString()));
	}
	
	private void setProjecao() {
		criteria.setProjection((Projections.projectionList()
				.add(Projections.property(ALIAS_TRP+"."+MptTratamentoTerapeutico.Fields.DATA_INICIO_AGENDA_SESSAO.toString()), 
						CursorCTrpVO.Fields.DATA_INICIO_AGENDA_SESSAO.toString())
				.add(Projections.property(ALIAS_TRP+"."+MptTratamentoTerapeutico.Fields.NRO_AGENDA_SESSAO.toString()), 
						CursorCTrpVO.Fields.NRO_AGENDA_SESSAO.toString())
				));
		criteria.setResultTransformer(Transformers.aliasToBean(CursorCTrpVO.class));
	}
	
	private void setJoin(){
		criteria.createAlias(ALIAS_TPT+"."+FatTipoTratamentos.Fields.FAT_TIPO_TRATAMENTO.toString(), ALIAS_TRP, JoinType.INNER_JOIN);
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
