package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.ParamReportGrupoVO;
import br.gov.mec.aghu.model.MciExportacaoDado;
import br.gov.mec.aghu.model.MciParamReportGrupo;
import br.gov.mec.aghu.model.MciParamReportUsuario;

public class MciParamReportGrupoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciParamReportGrupo> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7767649429410883437L;
	
	public List<ParamReportGrupoVO> pesquisarParamReportGrupoPorSeqGrupo(Short seqGrupo) {
		DetachedCriteria criteria = montarParamReportGrupoPorSeqGrupo(seqGrupo);
		criteria.addOrder(Order.asc("PRU."+MciParamReportUsuario.Fields.NOME_PARAM_PERMANENTE.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarParamReportGrupoPorSeqGrupoCount(Short seqGrupo) {
		DetachedCriteria criteria = montarParamReportGrupoPorSeqGrupo(seqGrupo);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarParamReportGrupoPorSeqGrupo(Short seqGrupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciParamReportGrupo.class, "PRG");
		criteria.createAlias(MciParamReportGrupo.Fields.MCI_PARAM_REPORT_USUARIOS.toString(), "PRU", JoinType.INNER_JOIN);
		criteria.createAlias(MciParamReportGrupo.Fields.MCI_EXPORTACAO_DADOS.toString(), "EDA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PRG."+MciParamReportGrupo.Fields.PRU_SEQ.toString()), ParamReportGrupoVO.Fields.PRU_SEQ.toString())
				.add(Projections.property("PRG."+MciParamReportGrupo.Fields.GRR_SEQ.toString()), ParamReportGrupoVO.Fields.GRR_SEQ.toString())
				.add(Projections.property("PRG."+MciParamReportGrupo.Fields.ORDEM_EMISSAO.toString()), ParamReportGrupoVO.Fields.ORDEM_EMISSAO.toString())
				.add(Projections.property("PRG."+MciParamReportGrupo.Fields.IND_IMPRESSAO.toString()), ParamReportGrupoVO.Fields.IMPRESSAO.toString())
				.add(Projections.property("PRG."+MciParamReportGrupo.Fields.NRO_COPIAS.toString()), ParamReportGrupoVO.Fields.NRO_COPIAS.toString())
				.add(Projections.property("PRU."+MciParamReportUsuario.Fields.NOME_PARAM_PERMANENTE.toString()), ParamReportGrupoVO.Fields.NOME_PARAM_PERMANENTE.toString())
				.add(Projections.property("PRU."+MciParamReportUsuario.Fields.NRO_COPIAS.toString()), ParamReportGrupoVO.Fields.NRO_COPIAS_DEFAULT.toString())
				.add(Projections.property("EDA."+MciExportacaoDado.Fields.SEQ.toString()), ParamReportGrupoVO.Fields.EDA_SEQ.toString())
				.add(Projections.property("EDA."+MciExportacaoDado.Fields.DESCRICAO.toString()), ParamReportGrupoVO.Fields.EDA_DESCRICAO.toString())
				);
		if (seqGrupo != null) {
			criteria.add(Restrictions.eq("PRG."+MciParamReportGrupo.Fields.MCI_GRUPO_REPORT_ROTINA_CCIS_SEQ.toString(), seqGrupo));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(ParamReportGrupoVO.class));
		return criteria;
	}
}
