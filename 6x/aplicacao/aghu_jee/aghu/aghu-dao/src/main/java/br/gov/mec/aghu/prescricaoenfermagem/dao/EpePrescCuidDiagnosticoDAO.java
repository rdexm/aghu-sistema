package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ShortType;

import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpePrescCuidDiagnostico;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;

public class EpePrescCuidDiagnosticoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpePrescCuidDiagnostico> {

	private static final long serialVersionUID = -7161376224745954176L;
	
	public List<EpePrescCuidDiagnostico> listarPrescCuidDiagnosticoPorPrescricaoCuidado(EpePrescricoesCuidados cuidado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescCuidDiagnostico.class);
		
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.PRESCRICAO_CUIDADO.toString(), cuidado));
		
		return executeCriteria(criteria);
	}
	
	
	private DetachedCriteria montarCriteriaListarPrescCuidDiagnosticoPorAtdSeqDataInicioDataFim(
			String aliasPrescricaoEnfermagem, String aliasPrescricaoCuidado, String aliasFatRelacionado, 
			String aliasDiagnostico, String separador, Integer atdSeq, Date dthrInicio, Date dthrFim, Date dthrMovimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescCuidDiagnostico.class);

		criteria.createAlias(EpePrescCuidDiagnostico.Fields.PRESCRICAO_CUIDADO.toString(), aliasPrescricaoCuidado);
		criteria.createAlias(aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.PRESCRICAO_ENFERMAGEM.toString(), aliasPrescricaoEnfermagem);
		criteria.createAlias(EpePrescCuidDiagnostico.Fields.FAT_RELACIONADO.toString(), aliasFatRelacionado);
		criteria.createAlias(EpePrescCuidDiagnostico.Fields.DIAGNOSTICO.toString(), aliasDiagnostico);
		
		criteria.add(Restrictions.eq(aliasPrescricaoEnfermagem + separador + EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		
		criteria.add(
			Restrictions.or(
				Restrictions.isNull(aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.DTHR_FIM.toString()),
				Restrictions.ltProperty(aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.DTHR_INICIO.toString(),
								aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.DTHR_FIM.toString())));
		
		criteria.add(Restrictions.lt(aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.DTHR_INICIO.toString(), dthrFim));
		
		criteria.add(Restrictions.or(
						Restrictions.isNull(aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.DTHR_FIM.toString()),
						Restrictions.or(
							Restrictions.and(Restrictions.gt(aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), new Date()),
											Restrictions.gt(aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), dthrInicio)),
							Restrictions.and(Restrictions.isNotNull(aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.DTHR_FIM.toString()),
											Restrictions.ge(aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.CRIADO_EM.toString(), dthrMovimento)))));
		
		return criteria;
	}
	
	public List<DiagnosticoEtiologiaVO> listarPrescCuidDiagnosticoPorAtdSeqDataInicioDataFim(
			Integer atdSeq, Date dthrInicio, Date dthrFim, Date dthrMovimento) {
		
		String aliasPrescricaoEnfermagem = "pen";
		String aliasPrescricaoCuidado = "prc";
		String aliasFatRelacionado = "fre";
		String aliasDiagnostico = "dgn"; 
		String separador = ".";
				
		DetachedCriteria criteria = montarCriteriaListarPrescCuidDiagnosticoPorAtdSeqDataInicioDataFim(
				aliasPrescricaoEnfermagem, aliasPrescricaoCuidado, aliasFatRelacionado, aliasDiagnostico, separador,
				atdSeq, dthrInicio, dthrFim, dthrMovimento);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(aliasPrescricaoEnfermagem + separador + EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString()))
				.add(Projections.property(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SNB_GNB_SEQ.toString()))
				.add(Projections.property(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SNB_SEQUENCIA.toString()))
				.add(Projections.property(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SEQUENCIA.toString()))
				.add(Projections.property(EpePrescCuidDiagnostico.Fields.CDG_FDG_FRE_SEQ.toString()))
				.add(Projections.property(aliasDiagnostico + separador + EpeDiagnostico.Fields.DESCRICAO.toString()))
				.add(Projections.property(aliasFatRelacionado + separador + EpeFatRelacionado.Fields.DESCRICAO.toString()))));

		List<Object[]> listaArrayObject = executeCriteria(criteria); 
		
		List<DiagnosticoEtiologiaVO> listaDiagnosticoEtiologiaVO = new ArrayList<DiagnosticoEtiologiaVO>();
		
		for (Object[] diagArrayObject : listaArrayObject) {
			DiagnosticoEtiologiaVO diagEtiologiaVO = new DiagnosticoEtiologiaVO();
			diagEtiologiaVO.setPrcAtdSeq((Integer) diagArrayObject[0]);
			diagEtiologiaVO.setCdgFdgDgnSnbGnbSeq((Short) diagArrayObject[1]);
			diagEtiologiaVO.setCdgFdgDgnSnbSequencia((Short) diagArrayObject[2]);
			diagEtiologiaVO.setCdgFdgDgnSequencia((Short) diagArrayObject[3]);
			diagEtiologiaVO.setCdgFdgFreSeq((Short) diagArrayObject[4]);
			diagEtiologiaVO.setDescricaoDiagnostico((String) diagArrayObject[5]);
			diagEtiologiaVO.setDescricaoEtiologia((String) diagArrayObject[6]);
			listaDiagnosticoEtiologiaVO.add(diagEtiologiaVO);
		}
		
		return listaDiagnosticoEtiologiaVO;
	}
	
	public List<EpePrescCuidDiagnostico> listarPrescCuidDiagnosticoPorAtdSeqEDiagnostico(Integer atdSeq, Short cdgFdgDgnSnbGnbSeq, 
			Short cdgFdgDgnSnbSequencia, Short cdgFdgDgnSequencia, Short cdgFdgFreSeq, Integer penAtdSeq, Integer penSeq) {
		
		String aliasPrescricaoCuidado = "prc";
		String aliasPrescricaoEnfermagem = "pen";
		String separador = ".";

		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescCuidDiagnostico.class);
		
		criteria.createAlias(EpePrescCuidDiagnostico.Fields.PRESCRICAO_CUIDADO.toString(), aliasPrescricaoCuidado);
		criteria.createAlias(aliasPrescricaoCuidado + separador + EpePrescricoesCuidados.Fields.PRESCRICAO_ENFERMAGEM, aliasPrescricaoEnfermagem);
		
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.PRC_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SNB_GNB_SEQ.toString(), cdgFdgDgnSnbGnbSeq));
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SNB_SEQUENCIA.toString(), cdgFdgDgnSnbSequencia));
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SEQUENCIA.toString(), cdgFdgDgnSequencia));
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.CDG_FDG_FRE_SEQ.toString(), cdgFdgFreSeq));
		criteria.add(Restrictions.eq(aliasPrescricaoEnfermagem + separador + EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), penAtdSeq));
		criteria.add(Restrictions.eq(aliasPrescricaoEnfermagem + separador + EpePrescricaoEnfermagem.Fields.SEQ.toString(), penSeq));
				
		return executeCriteria(criteria); 
	}
	
	// #4960 - Manter diagnósticos x cuidados
	// C8
	public Boolean verificarCuidadoRelacionadoPrescricao(Short cuiSeq, Short freSeq, Short dgnSequencia, Short dgnSnbSequencia, Short dgnSnbGnbSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescCuidDiagnostico.class);
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.CDG_CUI_SEQ.toString(), cuiSeq));
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.CDG_FDG_FRE_SEQ.toString(), freSeq));
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SEQUENCIA.toString(), dgnSequencia));
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SNB_SEQUENCIA.toString(), dgnSnbSequencia));
		criteria.add(Restrictions.eq(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SNB_GNB_SEQ.toString(), dgnSnbGnbSeq));
		return executeCriteriaExists(criteria);
	}
	
	public List<DiagnosticoEtiologiaVO> listarGerarMamDignosticos(Integer atdSeq){

		StringBuffer sql = new StringBuffer(1200);


		sql.append(" select atd.pac_codigo , pcd.cdg_fdg_dgn_snb_gnb_seq as cdgFdgDgnSnbGnbSeq  , pcd.cdg_fdg_dgn_snb_sequencia as cdgFdgDgnSnbSequencia, ");
		sql.append(" pcd.cdg_fdg_dgn_sequencia as cdgFdgDgnSequencia , pcd.cdg_fdg_fre_seq as cdgFdgFreSeq ");
		sql.append(" from agh.epe_presc_cuid_diagnosticos pcd , agh.agh_atendimentos atd ");
		sql.append(" where atd.seq = " ).append(atdSeq);
		sql.append(" and pcd.prc_atd_seq  = atd.seq");
		sql.append(" and (pcd.prc_atd_seq, pcd.prc_seq) in");
		sql.append(" (select prc.atd_seq, prc.seq");
		sql.append(" from agh.epe_prescricoes_cuidados prc ");
		sql.append(" where prc.atd_seq = pcd.prc_atd_seq ");
		sql.append(" and prc.seq = pcd.prc_seq " );
		sql.append(" and ((prc.dthr_fim is null) or (prc.dthr_fim is not null and prc.dthr_fim > ");
		if(isOracle()){
			sql.append(" SYSDATE))) MINUS ");
		}else{
			sql.append(" now()))) EXCEPT ");
		}
		sql.append(" select atd.pac_codigo , dia.FDG_DGN_SNB_GNB_SEQ as cdgFdgDgnSnbGnbSeq, dia.FDG_DGN_SNB_SEQUENCIA as cdgFdgDgnSnbSequencia, ");
		sql.append(" dia.FDG_DGN_SEQUENCIA as cdgFdgDgnSequencia, dia.FDG_FRE_SEQ as cdgFdgFreSeq"); 
		sql.append(" from agh.mam_diagnosticos dia , agh.agh_atendimentos atd ");
		sql.append(" where atd.seq = ").append(atdSeq);
		sql.append(" and dia.pac_codigo = atd.pac_codigo ");
		sql.append(" AND dia.data_fim IS NULL");
		sql.append(" AND dia.ind_situacao = 'A'");
		sql.append(" AND dia.ind_pendente = 'V'");
		sql.append(" AND dia.dthr_valida_mvto IS NULL");
		sql.append(" AND dia.FDG_DGN_SNB_GNB_SEQ IS NOT NULL");
		sql.append(" AND dia.FDG_DGN_SNB_SEQUENCIA IS NOT NULL");
		sql.append(" AND dia.FDG_DGN_SEQUENCIA IS NOT NULL");
		sql.append(" AND dia.FDG_FRE_SEQ IS NOT NULL");

	
		SQLQuery query = createSQLQuery(sql.toString());
	

		query.addScalar("cdgFdgDgnSnbGnbSeq", ShortType.INSTANCE);
		query.addScalar("cdgFdgDgnSnbSequencia", ShortType.INSTANCE);
		query.addScalar("cdgFdgDgnSequencia", ShortType.INSTANCE);
		query.addScalar("cdgFdgFreSeq", ShortType.INSTANCE);
		
		// Transforma e seta aliases do resultado no VO

		query.setResultTransformer(Transformers.aliasToBean(DiagnosticoEtiologiaVO.class));

		// Retorna Lista
		return query.list();

	}

}