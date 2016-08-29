package br.gov.mec.aghu.farmacia.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPreparo;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.model.AfaComposicaoItemPreparo;
import br.gov.mec.aghu.model.AfaItemPreparoMdto;
import br.gov.mec.aghu.model.AfaPreparoMdto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.sig.custos.vo.SigPreparoMdtoPrescricaoMedicaVO;
import br.gov.mec.aghu.sig.custos.vo.SigPreparoMdtoVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class AfaPreparoMdtosDAO extends BaseDao<AfaPreparoMdto>{

	private static final long serialVersionUID = -3310749349207790614L;

	public List<AfaPreparoMdto> buscarBolsasSeringasDinpensacoesCriteria(Date dataInicioProcessamento, 
			Date dataFimProcessamento, 
			Integer tipoTratamento/*"P_TIPO_TRAT_QUIMIO"*/) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaPreparoMdto.class, "PMT");
		criteria.createAlias(AfaPreparoMdto.Fields.AFA_ITEM_PREPARO_MDTOES.toString(), "IPM", JoinType.INNER_JOIN);		
		criteria.createAlias(AfaPreparoMdto.Fields.MPT_PRESCRICAO_MEDICAMENTO.toString(), "PMO", JoinType.INNER_JOIN);		
		criteria.createAlias("PMO."+MptPrescricaoMedicamento.Fields.PRESCRICAO_PACIENTE.toString(), "MPT", JoinType.INNER_JOIN);
		criteria.createAlias("MPT."+MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);		
		criteria.createAlias("MPT."+MptPrescricaoPaciente.Fields.AGENDA_PRESCRICAO.toString(), "AGP", JoinType.INNER_JOIN);
		criteria.createAlias("AGP."+MptAgendaPrescricao.Fields.UNF_SEQ.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias("IPM."+AfaItemPreparoMdto.Fields.AFA_COMPOSICAO_ITEM_PREPAROES.toString(), "CIP", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.IND_TIPO_TRATAMENTO.toString(), DominioTipoTratamentoAtendimento.VALOR_29))
				.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.TIPO_TRATAMENTO.toString(), tipoTratamento))
		);
		
		criteria.add(Restrictions.isNotNull("ATD."+AghAtendimentos.Fields.TRP_SEQ.toString()));
		criteria.add(Restrictions.in("IPM."+AfaItemPreparoMdto.Fields.IND_SITUACAO.toString(), 
				new Object[]{
								DominioSituacaoItemPreparo.P, 
								DominioSituacaoItemPreparo.E, 
								DominioSituacaoItemPreparo.I}));
		
		criteria.add(Restrictions.eq("AGP."+MptAgendaPrescricao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.add(Restrictions.between("MPT."+MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString(), dataInicioProcessamento, dataFimProcessamento));		
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("ATD."+AghAtendimentos.Fields.PAC_CODIGO.toString()))
				.add(Projections.groupProperty("ATD."+AghAtendimentos.Fields.SEQ.toString()))
				.add(Projections.groupProperty("ATD."+AghAtendimentos.Fields.TRP_SEQ.toString()))
				.add(Projections.groupProperty("MPT."+MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString()))
				.add(Projections.groupProperty("AGP."+MptAgendaPrescricao.Fields.UNF_SEQ.toString()))
				.add(Projections.groupProperty("UNF."+AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString()))
				.add(Projections.groupProperty("PMT."+AfaPreparoMdto.Fields.TIPO_ETIQUETA.toString()))
				.add(Projections.groupProperty("IPM."+AfaItemPreparoMdto.Fields.NRO_PREPARO.toString()))
				.add(Projections.groupProperty("CIP."+AfaComposicaoItemPreparo.Fields.ITO_PTO_SEQ.toString()))
				.add(Projections.groupProperty("CIP."+AfaComposicaoItemPreparo.Fields.ITO_SEQP.toString()))
				
		);
		
		criteria.addOrder(Order.asc("MPT."+MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString()));
		criteria.addOrder(Order.asc("CIP."+AfaComposicaoItemPreparo.Fields.ITO_PTO_SEQ.toString()));
		criteria.addOrder(Order.asc("CIP."+AfaComposicaoItemPreparo.Fields.ITO_SEQP.toString()));
		criteria.addOrder(Order.asc("IPM."+AfaItemPreparoMdto.Fields.NRO_PREPARO.toString()));
		criteria.addOrder(Order.asc("ATD."+AghAtendimentos.Fields.TRP_SEQ.toString()));
		criteria.addOrder(Order.asc("AGP."+MptAgendaPrescricao.Fields.UNF_SEQ.toString()));
		criteria.addOrder(Order.asc("UNF."+AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString()));
		criteria.addOrder(Order.asc("PMT."+AfaPreparoMdto.Fields.TIPO_ETIQUETA.toString()));
		
		return executeCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<SigPreparoMdtoVO> buscarBolsasSeringasDinpensacoes(Date dataInicioProcessamento, 
			Date dataFimProcessamento, 
			Integer tipoTratamento/*"P_TIPO_TRAT_QUIMIO"*/){
		StringBuffer query = new StringBuffer(" ")
		
	   .append(" select atd.seq as atdPaciente, ") 
	   .append(" 	atd.pac_codigo as pacCodigo, ")
	   .append(" 	atd.trp_seq as trpSeq, ")
	   .append(" 	mpt.dt_prev_execucao as dtPrevExecucao, ")
	   .append(" 	agp.unf_seq as unfSeq, ")
	   .append(" 	unf.cct_codigo as centroCusto, ")
	   .append(" 	pmt.tipo_etiqueta as tipoEtiqueta, ")
	   .append(" 	ipm.nro_preparo as nroPreparo, ")
	   .append(" 	cip.ito_pto_seq as itoPtoSeq, ")
	   .append(" 	cip.ito_seqp as itoSeqp ")
	   .append(" from agh.afa_preparo_mdtos pmt, ")
	   .append(" 	agh.agh_atendimentos atd, ") 
	   .append(" 	agh.mpt_prescricao_pacientes mpt, ")  
	   .append(" 	agh.mpt_prescricao_mdtos pmo, ")
	   .append(" 	agh.afa_item_preparo_mdtos ipm, ")
	   .append(" 	agh.afa_compos_item_preparos cip, ")
	   .append(" 	agh.mpt_agenda_prescricoes agp, ")
	   .append(" 	agh.agh_unidades_funcionais unf ")      
	   .append(" where (atd.ind_tipo_tratamento = 29 ")
	   .append(" 	or atd.tpt_seq = :tipoTratamento ) ")
	   .append(" 	and atd.trp_seq is not null ") 
	   .append(" 	and atd.seq = mpt.atd_seq ")
	   .append(" 	and mpt.dt_prev_execucao between :dataInicioProcessamento ") 
	   .append(" 	and :dataFimProcessamento ")
	   .append(" 	and mpt.pte_atd_seq = agp.pte_atd_seq ")
	   .append(" 	and mpt.pte_seq = agp.pte_seq ")
	   .append(" 	and agp.ind_situacao =  'A' ")
	   .append(" 	and agp.unf_seq = unf.seq ")                     
	   .append(" 	and mpt.atd_seq = pmo.pte_atd_seq ")
	   .append(" 	and mpt.seq = pmo.pte_seq ") 
	   .append(" 	and pmo.pte_atd_seq = pmt.pmo_pte_atd_seq ")
	   .append(" 	and pmo.pte_seq = pmt.pmo_pte_seq ")
	   .append(" 	and pmo.seq = pmt.pmo_seq ")
	   .append(" 	and pmt.seq = ipm.pto_seq ")
	   .append(" 	and ipm.pto_seq = cip.ito_pto_seq ")
	   .append(" 	and ipm.seqp = cip.ito_seqp ")
	   .append(" 	and ipm.ind_situacao in ('P','E','I') ")
	   .append(" group by atd.pac_codigo, atd.seq, atd.trp_seq, mpt.dt_prev_execucao, agp.unf_seq, ") 
       .append(" 	unf.cct_codigo, pmt.tipo_etiqueta, ipm.nro_preparo, ") 
       .append(" 	cip.ito_pto_seq, cip.ito_seqp ")
       .append(" order by mpt.dt_prev_execucao asc, cip.ito_pto_seq, cip.ito_seqp, ") 
       .append(" 	ipm.nro_preparo, atd.trp_seq, agp.unf_seq, unf.cct_codigo, ")
       .append(" 	pmt.tipo_etiqueta ");
		
		SQLQuery sqlQuery = createSQLQuery(query.toString());
		sqlQuery.setDate("dataInicioProcessamento", dataInicioProcessamento);
		sqlQuery.setDate("dataFimProcessamento", dataFimProcessamento);
		sqlQuery.setInteger("tipoTratamento", tipoTratamento);
		
		List<SigPreparoMdtoVO> listaVO = sqlQuery.addScalar("atdPaciente", IntegerType.INSTANCE)
		.addScalar("trpSeq", IntegerType.INSTANCE)
		.addScalar("dtPrevExecucao",DateType.INSTANCE)
		.addScalar("unfSeq", ShortType.INSTANCE)
		.addScalar("centroCusto", IntegerType.INSTANCE)
		.addScalar("tipoEtiqueta", StringType.INSTANCE)
		.addScalar("nroPreparo", IntegerType.INSTANCE)
		.addScalar("itoPtoSeq", IntegerType.INSTANCE)
		.addScalar("itoSeqp", ShortType.INSTANCE)
		.setResultTransformer(Transformers.aliasToBean(SigPreparoMdtoVO.class)).list();
		
		return listaVO;
	}
	
	public List<SigPreparoMdtoPrescricaoMedicaVO> buscarBolsasSeringasDinpensacoesPrescricaoMedica(Date dataInicio, Date dataFim){
		
		StringBuffer sql = new StringBuffer()
		
		.append(" select atd.seq as atdPaciente, ")
	    .append(" atd.int_seq as atdInternacao, ")
	    .append(" atd.pac_codigo as pacCodigo, ")
	    .append(" atd.dthr_fim as fimAtendimento, ")
	    .append(" ipm.dthr_preparo as dtPreparo, ")
	    .append(" pmt.tipo_etiqueta as tipoEtiqueta, ")
	    .append(" ipm.nro_preparo as nroPreparo, ")
	    .append(" cip.ito_pto_seq as itoPtoSeq, ")
	    .append(" cip.ito_seqp as itoSeqp ")
	    
	    .append(" 	from agh.afa_preparo_mdtos pmt, ")
	    .append(" agh.agh_atendimentos atd, ") 
	    .append(" agh.mpm_prescricao_medicas pme, ") 
	    .append(" agh.mpm_prescricao_mdtos pmd, ")
	    .append(" agh.afa_item_preparo_mdtos ipm, ")
	    .append(" agh.afa_compos_item_preparos cip ")
	    
	    .append(" where atd.seq = pme.atd_seq ")
	    .append(" and ipm.dthr_preparo between :dataInicio  ")
	    .append(" and :dataFim ")
	    .append(" and pme.atd_seq = pmd.pme_atd_seq ")
	    .append(" and pme.seq = pmd.pme_seq  ")
	    
//	    .append(" and pmd.pme_atd_seq = pmt.pme_atd_seq ")
	    
	    .append(" and pmd.pme_seq = pmt.pme_seq  ")
	    .append(" and pmd.atd_seq = pmt.pmd_atd_seq ")
	    .append(" and pmd.seq = pmt.pmd_seq  ")
	    .append(" and pmt.seq = ipm.pto_seq  ")
	    .append(" and ipm.pto_seq = cip.ito_pto_seq ")
	    .append(" and ipm.seqp = cip.ito_seqp  ")
	    .append(" and ipm.ind_situacao in ('P','E','I') ")
	    
	    .append(" group by atd.pac_codigo, atd.seq, atd.int_seq, ipm.dthr_preparo, pmt.tipo_etiqueta, ")
	    .append(" ipm.nro_preparo, cip.ito_pto_seq, cip.ito_seqp, atd.dthr_fim ")
	    .append(" order by ipm.dthr_preparo asc, cip.ito_pto_seq, cip.ito_seqp, ") 
	    .append(" ipm.nro_preparo, pmt.tipo_etiqueta ");
		
		SQLQuery query = createSQLQuery(sql.toString());
		query.setDate("dataInicio", dataInicio);
		query.setDate("dataFim", dataFim);
		
		@SuppressWarnings("unchecked")
		List<SigPreparoMdtoPrescricaoMedicaVO> listaVO = 
			query.addScalar("atdPaciente", IntegerType.INSTANCE)
		.addScalar("atdInternacao", IntegerType.INSTANCE)
		.addScalar("pacCodigo", IntegerType.INSTANCE)
		.addScalar("fimAtendimento", DateType.INSTANCE)
		.addScalar("dtPreparo", DateType.INSTANCE)
		.addScalar("tipoEtiqueta", StringType.INSTANCE)
		.addScalar("nroPreparo", IntegerType.INSTANCE)
		.addScalar("itoPtoSeq", IntegerType.INSTANCE)
		.addScalar("itoSeqp", ShortType.INSTANCE)
		
		.setResultTransformer(Transformers.aliasToBean(SigPreparoMdtoPrescricaoMedicaVO.class)).list();
		
		return listaVO;
	}
	
//	public List<AfaPreparoMdto> buscarBolsasSeringasDinpensacoesPrescricaoMedicaTeste(Date dataInicio, Date dataFim) {
//		DetachedCriteria criteria = DetachedCriteria.forClass(AfaPreparoMdto.class, "PMT");
//		criteria.createAlias("PMT."+AfaPreparoMdto.Fields.MPM_PRESCRICAO_MDTO.toString(), "PMD");
//		criteria.createAlias("PMT."+AfaPreparoMdto.Fields.AFA_ITEM_PREPARO_MDTOES.toString(), "IPM");
//		criteria.createAlias("PMD."+MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA.toString(), "PME");
//		criteria.createAlias("PME."+MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "ATD");
//		criteria.createAlias("IPM."+AfaItemPreparoMdto.Fields.AFA_COMPOSICAO_ITEM_PREPAROES.toString(), "CIP");
//		
//		return this.executeCriteria(criteria);
//	}
}