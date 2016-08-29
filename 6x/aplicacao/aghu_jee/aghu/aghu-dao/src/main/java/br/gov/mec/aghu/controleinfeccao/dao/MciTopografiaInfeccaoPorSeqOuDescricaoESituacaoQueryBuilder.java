package br.gov.mec.aghu.controleinfeccao.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.TopografiaInfeccaoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class MciTopografiaInfeccaoPorSeqOuDescricaoESituacaoQueryBuilder extends
		QueryBuilder<DetachedCriteria> {
	
	private static final long serialVersionUID = -2163829901907470577L;
	private static final String PONTO = ".";
	private static final String ALIAS_MTI = "MTI";
	private static final String ALIAS_RAP_SER = "SER";
	private static final String ALIAS_RAP_SER_MOVI = "SER_MOVI";

	private DetachedCriteria criteria;
	private Boolean isCount;
	private Short seq;
	private String strPesquisa;
	private DominioSituacao indSituacao;	
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria
				.forClass(MciTopografiaInfeccao.class, ALIAS_MTI);
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
		criteria.createAlias(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.SERVIDOR.toString(), ALIAS_RAP_SER, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.MOVIMENTADO_POR.toString(), ALIAS_RAP_SER_MOVI, JoinType.LEFT_OUTER_JOIN);
	}

	private void setFiltro() {
		
		if(seq != null ){
			criteria.add(Restrictions.eq(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.SEQ.toString(), seq));
		}
		
		if (indSituacao != null){
			criteria.add(Restrictions.eq(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		
		if(CoreUtil.isNumeroShort(strPesquisa)) {
			criteria.add(Restrictions.eq(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
		} else {
			if(!StringUtils.isEmpty(strPesquisa) && !StringUtils.isBlank(strPesquisa)){
				criteria.add(Restrictions.ilike(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
	}
	
	private void setProjecao() {
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.SEQ.toString()), TopografiaInfeccaoVO.Fields.SEQ.toString())
			.add(Projections.property(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.DESCRICAO.toString()), TopografiaInfeccaoVO.Fields.DESCRICAO.toString())
			.add(Projections.property(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.IND_SITUACAO.toString()), TopografiaInfeccaoVO.Fields.IND_SITUACAO.toString())
			.add(Projections.property(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.IND_SUPERVISAO.toString()), TopografiaInfeccaoVO.Fields.IND_SUPERVISAO.toString())
			.add(Projections.property(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.IND_PAC_INFECTADO.toString()), TopografiaInfeccaoVO.Fields.IND_PAC_INFECTADO.toString())
			.add(Projections.property(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.IND_CONTA_INFEC_MENSAL.toString()), TopografiaInfeccaoVO.Fields.IND_CONTA_INFEC_MENSAL.toString())
			.add(Projections.property(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.ALTERADO_EM.toString()), TopografiaInfeccaoVO.Fields.ALTERADO_EM.toString())
			.add(Projections.property(ALIAS_MTI + PONTO + MciTopografiaInfeccao.Fields.CRIADO_EM.toString()), TopografiaInfeccaoVO.Fields.CRIADO_EM.toString())
			.add(Projections.property(ALIAS_RAP_SER + PONTO + RapServidores.Fields.MATRICULA.toString()), TopografiaInfeccaoVO.Fields.MATRICULA.toString())
			.add(Projections.property(ALIAS_RAP_SER + PONTO + RapServidores.Fields.CODIGO_VINCULO.toString()), TopografiaInfeccaoVO.Fields.CODIGO_VINCULO.toString())
			.add(Projections.property(ALIAS_RAP_SER_MOVI + PONTO + RapServidores.Fields.MATRICULA.toString()), TopografiaInfeccaoVO.Fields.MATRICULA_MOVI.toString())
			.add(Projections.property(ALIAS_RAP_SER_MOVI + PONTO + RapServidores.Fields.CODIGO_VINCULO.toString()), TopografiaInfeccaoVO.Fields.CODIGO_VINCULO_MOVI.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(TopografiaInfeccaoVO.class));
	}
	
	public DetachedCriteria build(Boolean isCount, Short seq, String strPesquisa, DominioSituacao indSituacao) {
		this.isCount = isCount;
		this.seq = seq;
		this.strPesquisa = strPesquisa;
		this.indSituacao = indSituacao;
		
		return super.build();
	}

}
