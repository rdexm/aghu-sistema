package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.vo.CursorCurPreVO;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorCurPreVOQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	private static final long serialVersionUID = 2767818822605100203L;

	private final static String ALIAS_PES = "PES";
	private final static String ALIAS_SER = "SER";
	
	private DetachedCriteria criteria;
	private Integer matricula;
	private Short vinCodigo;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(RapServidores.class, ALIAS_SER);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setProjecao();
		setJoin();
	}
	
	private void setJoin(){
		criteria.createAlias(ALIAS_SER+"."+RapServidores.Fields.PESSOA_FISICA.toString(), ALIAS_PES, JoinType.INNER_JOIN);
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_SER+"."+RapServidores.Fields.MATRICULA.toString(), this.matricula));
		criteria.add(Restrictions.eq(ALIAS_SER+"."+RapServidores.Fields.VIN_CODIGO.toString(), this.vinCodigo));
	}
	
	private void setProjecao() {
		criteria.setProjection(Projections.projectionList().add(Projections.property(ALIAS_PES+"."+RapPessoasFisicas.Fields.NOME.toString()), CursorCurPreVO.Fields.NOME.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CursorCurPreVO.class));
	}

	public DetachedCriteria build(Integer matricula, Short vinCodigo) {
		this.matricula = matricula;
		this.vinCodigo = vinCodigo;
		
		return super.build();
	}

	public DetachedCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetachedCriteria criteria) {
		this.criteria = criteria;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
}
