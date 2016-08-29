package br.gov.mec.aghu.blococirurgico.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;

public class MbcSolicHemoCirgAgendadaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcSolicHemoCirgAgendada> {
	
	private static final long serialVersionUID = 1719909380471469367L;

	public Long mbcSolicHemoCirgAgendadaCountPorCrgSeq(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicHemoCirgAgendada.class);
		criteria.add(Restrictions.eq(MbcSolicHemoCirgAgendada.Fields.ID_CRG_SEQ.toString(), crgSeq));
		
		return executeCriteriaCount(criteria);
	}	
	
	/**
	 * Pesquisa componentes sanguíneos na escala cirúrgica
	 * @param crgSeq
	 * @return
	 */
	public List<MbcSolicHemoCirgAgendada> pesquisarComponenteSanguineosEscalaCirurgica(Integer crgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicHemoCirgAgendada.class, "SHA");
		criteria.createAlias("SHA.".concat(MbcSolicHemoCirgAgendada.Fields.ABS_COMPONENTE_SANGUINEO.toString()), "CSA");
		criteria.createAlias("SHA.".concat(MbcSolicHemoCirgAgendada.Fields.MBC_CIRURGIAS.toString()), "CRG");

		criteria.add(Restrictions.eq("CRG.".concat(MbcCirurgias.Fields.SEQ.toString()), crgSeq));

		return executeCriteria(criteria);
	}

	public MbcSolicHemoCirgAgendada obterMbcSolicHemoCirgAgendadaById(Integer crgSeq, String csaCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicHemoCirgAgendada.class);
		criteria.createAlias(MbcSolicHemoCirgAgendada.Fields.ABS_COMPONENTE_SANGUINEO.toString(), "abscs");
		criteria.add(Restrictions.eq(MbcSolicHemoCirgAgendada.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcSolicHemoCirgAgendada.Fields.ID_CSA_CODIGO.toString(), csaCodigo));
		
		return  (MbcSolicHemoCirgAgendada) executeCriteriaUniqueResult(criteria);
	}
	
	public List<SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO> obterSangueUtilizadoPorCrgSeq(Integer crgSeq) {
		List<SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO> retorno = new ArrayList<SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO>();
		
		retorno.addAll(obterSangueUtilizadoPorCrgSeqUnion1(crgSeq));
		retorno.addAll(obterSangueUtilizadoPorCrgSeqUnion2(crgSeq));
		
		Collections.sort(retorno, new Comparator<SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO>() {
			@Override
			public int compare(SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO arg0, SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO arg1) {
				return arg0.getSangueUtilizado().compareTo(arg1.getSangueUtilizado());
			}
		});
		
		return retorno;
	}
	
	private List<SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO> obterSangueUtilizadoPorCrgSeqUnion1 (Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSolicHemoCirgAgendada.class, "SHA");
		criteria.createAlias("SHA.".concat(MbcSolicHemoCirgAgendada.Fields.ABS_COMPONENTE_SANGUINEO.toString()), "CSA");
		
		StringBuilder sqlProjection = new StringBuilder(250);
		sqlProjection.append("   RPAD(SUBSTR(csa1_.DESCRICAO,1,25),25,'.') || ':' ||   ")
		.append("   LPAD(TO_CHAR({alias}.QUANTIDADE, 'FM999'),3,' ') ||   ")
		.append("   CASE WHEN {alias}.QTDE_ML IS NULL THEN ' UN' ELSE ' ML' END  "
			).append( SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO.Fields.SANGUE_UTILIZADO.toString());
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlProjection.toString(),
						new String[]{SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO.Fields.SANGUE_UTILIZADO.toString()},
						new Type[] { StringType.INSTANCE })));
		
		criteria.add(Restrictions.eq("CSA." + AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("CSA." + AbsComponenteSanguineo.Fields.IND_PERMITE_PRESCRICAO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("SHA." + MbcSolicHemoCirgAgendada.Fields.ID_CRG_SEQ.toString(), crgSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO.class));
		
		return executeCriteria(criteria);
	}
	
	private List<SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO> obterSangueUtilizadoPorCrgSeqUnion2 (Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponenteSanguineo.class, "CSA");
		
		StringBuilder sqlProjection = new StringBuilder(300);
		sqlProjection.append("   CASE WHEN {alias}.CODIGO = 'SC' THEN                             ")
		.append("   RPAD(SUBSTR('SANGUE IRRADIADO',1,25),25,'.') || ':' || '___ UN'  ")
		.append("   ELSE RPAD(SUBSTR({alias}.DESCRICAO,1,25),25,'.') || ':' || '___ UN' END  "
				).append( SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO.Fields.SANGUE_UTILIZADO.toString());
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlProjection.toString(),
						new String[]{SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO.Fields.SANGUE_UTILIZADO.toString()},
						new Type[] { StringType.INSTANCE })));
		
		DetachedCriteria cp = DetachedCriteria.forClass(MbcSolicHemoCirgAgendada.class, "SHA");
		criteria.add(Subqueries.notExists(cp.setProjection(Property.forName("SHA." + MbcSolicHemoCirgAgendada.Fields.ID_CSA_CODIGO.toString()))
				.add(Restrictions.eqProperty("SHA." + MbcSolicHemoCirgAgendada.Fields.ID_CSA_CODIGO.toString(),
						"CSA." + AbsComponenteSanguineo.Fields.CODIGO.toString()))
				.add(Restrictions.eq("SHA." + MbcSolicHemoCirgAgendada.Fields.ID_CRG_SEQ.toString(), crgSeq))));
		
		criteria.add(Restrictions.eq("CSA." + AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		// Utilizado sqlRestriction pois a consulta estava com dados divergentes ao efetuar o count antes da consulta final.
		boolean isOracle = isOracle();
		if (isOracle) {
			criteria.add(Restrictions.eq("CSA." + AbsComponenteSanguineo.Fields.IND_PERMITE_PRESCRICAO.toString(), Boolean.TRUE));
			criteria.add(Restrictions.sqlRestriction(" ROWNUM < (SELECT 7 - COUNT(*) " +
					" FROM AGH.MBC_SOLIC_HEMO_CIRG_AGENDADAS SHY " +
					" WHERE SHY.CRG_SEQ = ?) ", crgSeq, IntegerType.INSTANCE));
		} else {
			criteria.add(Restrictions.sqlRestriction(" {alias}.IND_PERMITE_PRESCRICAO = 'S' " +
					" LIMIT (SELECT 6 - COUNT(*) " +
					" FROM AGH.MBC_SOLIC_HEMO_CIRG_AGENDADAS SHY " +
					" WHERE SHY.CRG_SEQ = ?) ", crgSeq, IntegerType.INSTANCE));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO.class));
		
		return executeCriteria(criteria);
	}
}