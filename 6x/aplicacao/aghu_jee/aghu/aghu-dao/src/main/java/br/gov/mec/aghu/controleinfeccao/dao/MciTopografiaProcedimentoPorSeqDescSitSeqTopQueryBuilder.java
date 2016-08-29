package br.gov.mec.aghu.controleinfeccao.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
import br.gov.mec.aghu.model.MciTopografiaProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class MciTopografiaProcedimentoPorSeqDescSitSeqTopQueryBuilder extends
		QueryBuilder<DetachedCriteria> {
	
	private static final long serialVersionUID = -2163829901907470577L;
	private static final String PONTO = ".";
	private static final String ALIAS_TOP = "TOP";
	private static final String ALIAS_TOI = "TOI";
	private static final String ALIAS_RAP_SER = "SER";
	private static final String ALIAS_RAP_SER_MOVI = "SER_MOVI";
	

	private DetachedCriteria criteria;
	private Boolean isCount;
	private Short seq;
	private String strPesquisa;
	private DominioSituacao indSituacao;
	private Short toiSeq;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(MciTopografiaProcedimento.class, ALIAS_TOP);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFiltro();
		if(!isCount){
			setProjecao();
		}
	}

	private void setJoin() {
		criteria.createAlias(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.TOPOGRAFIA_INFECCAO.toString(), ALIAS_TOI, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_TOP + PONTO + MciTopografiaInfeccao.Fields.SERVIDOR.toString(), ALIAS_RAP_SER, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_TOP + PONTO + MciTopografiaInfeccao.Fields.MOVIMENTADO_POR.toString(), ALIAS_RAP_SER_MOVI, JoinType.LEFT_OUTER_JOIN);
	}

	private void setFiltro() {
		
		if(seq != null ){
			criteria.add(Restrictions.eq(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.SEQ.toString(), seq));
		}
		
		if (indSituacao != null){
			criteria.add(Restrictions.eq(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		
		if (toiSeq != null){
			criteria.add(Restrictions.eq(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.TOI_SEQ.toString(), toiSeq));
		}
		
		if (!StringUtils.isEmpty(strPesquisa) && !StringUtils.isBlank(strPesquisa)){
			if(CoreUtil.isNumeroShort(strPesquisa)){
				criteria.add(Restrictions.eq(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
	}
	
	private void setProjecao() {
		StringBuilder projectionCase = new StringBuilder(80);
		projectionCase.append(" (CASE WHEN (UPPER({alias}.DESCRICAO) like '%CIRÚRGICA%') ")
			.append(" THEN 1 ELSE 0 END) ").append(TopografiaProcedimentoVO.Fields.PROCEDIMENTO_CIRURGICO.toString());
		
		criteria.setProjection(Projections.projectionList()
			.add(Projections.sqlProjection(projectionCase.toString(), new String[]{TopografiaProcedimentoVO.Fields.PROCEDIMENTO_CIRURGICO.toString()}, new Type[]{IntegerType.INSTANCE}))
			.add(Projections.property(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.SEQ.toString()), TopografiaProcedimentoVO.Fields.SEQ.toString())
			.add(Projections.property(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.DESCRICAO.toString()), TopografiaProcedimentoVO.Fields.DESCRICAO.toString())
			.add(Projections.property(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.IND_SITUACAO.toString()), TopografiaProcedimentoVO.Fields.IND_SITUACAO.toString())
			.add(Projections.property(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.IND_PERM_SOBREPOSICAO.toString()), TopografiaProcedimentoVO.Fields.IND_PERM_SOBREPOSICAO.toString())
			.add(Projections.property(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.TOI_SEQ.toString()), TopografiaProcedimentoVO.Fields.SEQ_TOPOGRAFIA_INFECCAO.toString())
			.add(Projections.property(ALIAS_TOI + PONTO + MciTopografiaInfeccao.Fields.DESCRICAO.toString()), TopografiaProcedimentoVO.Fields.DESCRICAO_TOPOGRA_FIAINFECCAO.toString())
			.add(Projections.property(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.CRIADO_EM.toString()), TopografiaProcedimentoVO.Fields.CRIADO_EM.toString())
			.add(Projections.property(ALIAS_RAP_SER + PONTO + RapServidores.Fields.MATRICULA.toString()), TopografiaProcedimentoVO.Fields.SERVIDOR_MATRICULA.toString())
			.add(Projections.property(ALIAS_RAP_SER + PONTO + RapServidores.Fields.CODIGO_VINCULO.toString()), TopografiaProcedimentoVO.Fields.SERVIDOR_VIN_CODIGO.toString())
			.add(Projections.property(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.ALTERADO_EM.toString()), TopografiaProcedimentoVO.Fields.ALTERADO_EM.toString())
			.add(Projections.property(ALIAS_RAP_SER_MOVI + PONTO + RapServidores.Fields.MATRICULA.toString()), TopografiaProcedimentoVO.Fields.SER_MATRICULA_MOVI.toString())
			.add(Projections.property(ALIAS_RAP_SER_MOVI + PONTO + RapServidores.Fields.CODIGO_VINCULO.toString()), TopografiaProcedimentoVO.Fields.SER_VINCULO_CODIGO_MOVI.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(TopografiaProcedimentoVO.class));
	}

	public DetachedCriteria build(Boolean isCount, Short seq, String strPesquisa, DominioSituacao indSituacao, Short toiSeq) {
		this.isCount = isCount;
		this.seq = seq;
		this.strPesquisa = strPesquisa;
		this.indSituacao = indSituacao;
		this.toiSeq = toiSeq;
		
		return super.build();
	}

}
