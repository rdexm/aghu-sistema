package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.comissoes.vo.SolicitacoesUsoMedicamentoVO;
import br.gov.mec.aghu.comissoes.vo.VMpmItemPrcrMdtosVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.VMpmItemPrcrMdtos;
import br.gov.mec.aghu.core.utils.DateUtil;



public class VMpmItemPrcrMdtosDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMpmItemPrcrMdtos>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2651652696955599481L;

	
	/** Obtém criteria para consulta de SolicitacoesUsoMedicamento.
	 * #1291
	 * @param parametro {@link Object}
	 * @return {@link List<AfaMedicamento>} */
	public List<VMpmItemPrcrMdtosVO> pesquisarSolicitacoesUsoMedicamento(Integer arg0, Integer arg1,
			String arg2, boolean arg3, SolicitacoesUsoMedicamentoVO filtro) {
		DetachedCriteria criteria = processarCriteriaPesquisarSolicitacoesUsoMedicamento(filtro);
		return this.executeCriteria(criteria, arg0, arg1, VMpmItemPrcrMdtosVO.Fields.NOME_PACIENTE.toString(), true);
	}
	
	public Long pesquisarSolicitacoesUsoMedicamentoCount(SolicitacoesUsoMedicamentoVO filtro) {
		DetachedCriteria criteria = processarCriteriaPesquisarSolicitacoesUsoMedicamento(filtro);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria processarCriteriaPesquisarSolicitacoesUsoMedicamento(
			SolicitacoesUsoMedicamentoVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmItemPrcrMdtos.class, "VIPMDTOS");
		if(filtro.getCodProntuario() != null){
			criteria.add(Restrictions.eq("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.PRONTUARIO.toString(), filtro.getCodProntuario()));
		}
		
		
		
		DetachedCriteria subquery1 = DetachedCriteria.forClass(AfaMedicamento.class, "MED3");
		subquery1.createAlias("MED3."+AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), "TUM1", JoinType.INNER_JOIN);
		subquery1.add(Restrictions.eqProperty("MED3."+AfaMedicamento.Fields.MAT_CODIGO.toString(), "VIPMDTOS."+VMpmItemPrcrMdtos.Fields.MED_MAT_CODIGO.toString()));
		subquery1.add(Restrictions.eq("TUM1."+AfaTipoUsoMdto.Fields.IND_AVALIACAO.toString(), true));
		if(filtro.getIndAntimicrobianos() != null){
			subquery1.add(Restrictions.eq("TUM1."+AfaTipoUsoMdto.Fields.IND_ANTIMICROBIANO.toString(), filtro.getIndAntimicrobianos().isSim()));
		}
		subquery1.setProjection(Projections.property(AfaMedicamento.Fields.MAT_CODIGO.toString()));
		criteria.add(Subqueries.propertyIn(VMpmItemPrcrMdtos.Fields.MED_MAT_CODIGO.toString(), subquery1));
		
		
		DetachedCriteria subquery2 = DetachedCriteria.forClass(MpmPrescricaoMdto.class, "PMD1");
		subquery2.add(Restrictions.eqProperty("PMD1."+MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), "VIPMDTOS."+VMpmItemPrcrMdtos.Fields.PMD_ATD_SEQ.toString()));
		subquery2.add(Restrictions.not(Restrictions.in("PMD1."+MpmPrescricaoMdto.Fields.INDPENDENTE.toString(), Arrays.asList(DominioIndPendenteItemPrescricao.X,
				DominioIndPendenteItemPrescricao.P,
				DominioIndPendenteItemPrescricao.B))));
		subquery2.setProjection(Projections.property(MpmPrescricaoMdto.Fields.ATD_SEQ.toString()));
		criteria.add(Subqueries.propertyIn(VMpmItemPrcrMdtos.Fields.PMD_ATD_SEQ.toString(), subquery2));
		
		
		processarCriteriaPesquisarSolicitacoesUsoMedicamentoParteII(filtro, criteria);
		
		
		String projection =""; 
		if(isOracle()) {
			projection = "(select DISTINCT 'S' from agh.mci_notificacao_gmr gmr where gmr.pac_codigo = this_.pac_codigo and gmr.IND_NOTIFICACAO_ATIVA = 'S' and rownum = 1) as indGmr";
		} else {
			projection = "(select DISTINCT 'S' from agh.mci_notificacao_gmr gmr where gmr.pac_codigo = this_.pac_codigo and gmr.IND_NOTIFICACAO_ATIVA = 'S' limit 1) as indGmr";
		}
		
		String[] columnAliases = {"indGmr"};
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.PAC_CODIGO.toString()), VMpmItemPrcrMdtosVO.Fields.PAC_CODIGO.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.PRONTUARIO.toString()), VMpmItemPrcrMdtosVO.Fields.PRONTUARIO.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.MED_MAT_CODIGO.toString()), VMpmItemPrcrMdtosVO.Fields.MED_MAT_CODIGO.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.PMD_ATD_SEQ.toString()), VMpmItemPrcrMdtosVO.Fields.PMD_ATD_SEQ.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.JUM_SEQ.toString()), VMpmItemPrcrMdtosVO.Fields.JUM_SEQ.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.NOME_PACIENTE.toString()), VMpmItemPrcrMdtosVO.Fields.NOME_PACIENTE.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.DESCRICAO_MED.toString()), VMpmItemPrcrMdtosVO.Fields.DESCRICAO_MED.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.NOME_SOLICITANTE.toString()), VMpmItemPrcrMdtosVO.Fields.NOME_SOLICITANTE.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.LTO_LTO_ID.toString()), VMpmItemPrcrMdtosVO.Fields.LTO_LTO_ID.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.QRT_NUMERO.toString()), VMpmItemPrcrMdtosVO.Fields.QRT_NUMERO.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.UNF_SEQ.toString()), VMpmItemPrcrMdtosVO.Fields.UNF_SEQ.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.IND_SITUACAO.toString()), VMpmItemPrcrMdtosVO.Fields.IND_SITUACAO.toString());
		p.add(Projections.property("VIPMDTOS."+VMpmItemPrcrMdtos.Fields.CRIADO_EM.toString()), VMpmItemPrcrMdtosVO.Fields.CRIADO_EM.toString());
		p.add(Projections.sqlProjection(projection, columnAliases, new Type[]{StringType.INSTANCE}), VMpmItemPrcrMdtosVO.Fields.IND_GMR.toString());
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(VMpmItemPrcrMdtosVO.class));
//		this.executeCriteria(criteria);
		
		return criteria;
				
		
	}
	
	private  void processarCriteriaPesquisarSolicitacoesUsoMedicamentoParteII(SolicitacoesUsoMedicamentoVO filtro, DetachedCriteria criteria) {
		
		DetachedCriteria subquery3 = DetachedCriteria.forClass(MpmJustificativaUsoMdto.class, "JUM1");
		subquery3.createAlias("JUM1."+MpmJustificativaUsoMdto.Fields.GRUPO_USO_MEDICAMENTO.toString(), "GUP1", JoinType.INNER_JOIN);
		subquery3.add(Restrictions.eqProperty("JUM1."+MpmJustificativaUsoMdto.Fields.SEQ.toString(), "VIPMDTOS."+VMpmItemPrcrMdtos.Fields.JUM_SEQ.toString()));
		subquery3.add(Restrictions.eqProperty("JUM1."+MpmJustificativaUsoMdto.Fields.GUP_SEQ.toString(), "GUP1."+AfaGrupoUsoMedicamento.Fields.SEQ.toString()));
		if(filtro.getDataInicio() != null && filtro.getDataFim() != null){
			Date dataInicioTemp = DateUtil.truncaData(filtro.getDataInicio());
			Date dataFimTemp = DateUtil.truncaDataFim(filtro.getDataFim());
			subquery3.add(Restrictions.between("JUM1."+MpmJustificativaUsoMdto.Fields.CRIADO_EM.toString(), dataInicioTemp, dataFimTemp));
		}
		if(filtro.getSituacao() != null){
			subquery3.add(Restrictions.eq("JUM1."+MpmJustificativaUsoMdto.Fields.SITUACAO.toString(), filtro.getSituacao()));
		}
		if(filtro.getGrupoUsoMedicamento() != null){
			subquery3.add(Restrictions.eq("JUM1."+MpmJustificativaUsoMdto.Fields.GUP_SEQ.toString(), filtro.getGrupoUsoMedicamento().getSeq().shortValue()));
		}
		if(filtro.getvMedicoSolicitante() != null){
			subquery3.add(Restrictions.eq("JUM1."+MpmJustificativaUsoMdto.Fields.SERVIDOR_VALIDA_ID_MATRICULA.toString(), filtro.getvMedicoSolicitante().getMatricula()));
			subquery3.add(Restrictions.eq("JUM1."+MpmJustificativaUsoMdto.Fields.SERVIDOR_VALIDA_ID_VINCODIGO.toString(), filtro.getvMedicoSolicitante().getVinCodigo().shortValue()));
		}
		if(filtro.getAvaliador() != null){
			subquery3.add(Restrictions.eq("GUP1."+AfaGrupoUsoMedicamento.Fields.RESPONSAVEL_AVALIACAO.toString(), filtro.getAvaliador()));
		}
		subquery3.setProjection(Projections.property("JUM1."+MpmJustificativaUsoMdto.Fields.SEQ.toString()));
		criteria.add(Subqueries.propertyIn(VMpmItemPrcrMdtos.Fields.JUM_SEQ.toString(), subquery3));
		
		
		DetachedCriteria subquery4 = DetachedCriteria.forClass(AghAtendimentos.class, "ATD1");
		subquery4.add(Restrictions.eqProperty("ATD1."+AghAtendimentos.Fields.SEQ.toString(), "VIPMDTOS."+VMpmItemPrcrMdtos.Fields.PMD_ATD_SEQ.toString()));
		subquery4.add(Restrictions.eq("ATD1."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		if(filtro.getvAghUnidFuncional() != null){
			subquery4.add(Restrictions.eq("ATD1."+AghAtendimentos.Fields.UNF_SEQ.toString(), filtro.getvAghUnidFuncional().getSeq().shortValue()));
		}
		subquery4.setProjection(Projections.property(AghAtendimentos.Fields.SEQ.toString()));
		criteria.add(Subqueries.propertyIn(VMpmItemPrcrMdtos.Fields.PMD_ATD_SEQ.toString(), subquery4));
		
		
		DetachedCriteria subquery5 = DetachedCriteria.forClass(AfaMedicamento.class, "MED1");
		subquery5.add(Restrictions.eqProperty("MED1."+AfaMedicamento.Fields.MAT_CODIGO.toString(), "VIPMDTOS."+VMpmItemPrcrMdtos.Fields.MED_MAT_CODIGO.toString()));
		if(filtro.getMedicamento() != null){
			subquery5.add(Restrictions.eq("MED1."+AfaMedicamento.Fields.MAT_CODIGO.toString(), filtro.getMedicamento().getCodigo()));
		}
		if(filtro.getTipoUso() != null){
			subquery5.add(Restrictions.eq("MED1."+AfaMedicamento.Fields.TUM_SIGLA.toString(), filtro.getTipoUso().getSigla()));
		}
		subquery5.setProjection(Projections.property(AfaMedicamento.Fields.MAT_CODIGO.toString()));
		criteria.add(Subqueries.propertyIn(VMpmItemPrcrMdtos.Fields.MED_MAT_CODIGO.toString(), subquery5));
		
		possuiVMpmpProfInterna(filtro, criteria);
		
	}

	private void possuiVMpmpProfInterna(SolicitacoesUsoMedicamentoVO filtro, DetachedCriteria criteria) {
		if(filtro.getvMpmpProfInterna() != null){
			DetachedCriteria subquery6 = DetachedCriteria.forClass(AinInternacao.class, "AIN1");
			subquery6.createAlias("AIN1."+AinInternacao.Fields.ATENDIMENTO.toString(), "ATD1", JoinType.INNER_JOIN);
			subquery6.add(Restrictions.eqProperty("ATD1."+AghAtendimentos.Fields.SEQ.toString(), "VIPMDTOS."+VMpmItemPrcrMdtos.Fields.PMD_ATD_SEQ.toString()));
			subquery6.add(Restrictions.eq("ATD1."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
			subquery6.add(Restrictions.eq("AIN1."+AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString(), filtro.getvMpmpProfInterna().getMatricula()));
			subquery6.add(Restrictions.eq("AIN1."+AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString(), filtro.getvMpmpProfInterna().getVinCodigo().shortValue()));
			subquery6.setProjection(Projections.property("ATD1."+AghAtendimentos.Fields.SEQ.toString()));
			criteria.add(Subqueries.propertyIn(VMpmItemPrcrMdtos.Fields.PMD_ATD_SEQ.toString(), subquery6));
		}
	}

}
