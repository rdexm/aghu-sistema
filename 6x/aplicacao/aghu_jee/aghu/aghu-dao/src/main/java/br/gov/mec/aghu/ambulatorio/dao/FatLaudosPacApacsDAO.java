package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatEspecialidadeTratamento;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatLaudoPacApac;
import br.gov.mec.aghu.model.FatListaPacApac;
import br.gov.mec.aghu.model.FatPacienteTransplantes;
import br.gov.mec.aghu.model.FatTipoTratamentos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;

public class FatLaudosPacApacsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatLaudoPacApac> {


	private static final long serialVersionUID = 4465498610078050110L;
	/**
	 * C2
	 * #42013
	 * @param consulta
	 * @return
	 */
	public FatLaudoPacApac verificaPacienteRealizouTransplante(Integer consulta){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatLaudoPacApac.class, "lap");
		
		criteria.createAlias("lap."+FatLaudoPacApac.Fields.FAT_LISTA_PAC_APAC.toString(), "lpp");	
		criteria.createAlias("lpp."+FatListaPacApac.Fields.PACICENTE.toString(), "aip");	
		criteria.createAlias("aip."+AipPacientes.Fields.AAC_CONSULTAS.toString(), "con");
		criteria.createAlias("con."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grd"); 
		criteria.createAlias("aip."+AipPacientes.Fields.PAC_TRANSPLANTE.toString(), "p");
		criteria.createAlias("p."+FatPacienteTransplantes.Fields.TIPO_TRATAMENTO.toString(), "t");
		criteria.createAlias("t."+FatTipoTratamentos.Fields.ESPECIALIDADE_TRATAMENTO.toString(), "etr");
			
		criteria.add(Restrictions.eq("con."+AacConsultas.Fields.NUMERO.toString(), consulta));
		criteria.add(Restrictions.eqProperty("grd."+AacGradeAgendamenConsultas.Fields.SEQ.toString(), 
				"con."+AacConsultas.Fields.GRD_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("grd."+AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), 
				"etr."+FatEspecialidadeTratamento.Fields.ESP_SEQ.toString()));
		criteria.add(Restrictions.eq("etr."+FatEspecialidadeTratamento.Fields.IND_LISTA_CANDIDATO.toString(), "S"));
		criteria.add(Restrictions.eqProperty("lpp."+FatListaPacApac.Fields.PAC_CODIGO.toString(),
				"con."+AacConsultas.Fields.PAC_CODIGO.toString()));
		criteria.add(Restrictions.eqProperty("lpp."+FatListaPacApac.Fields.TPT_SEQ.toString(), 
				"etr."+FatEspecialidadeTratamento.Fields.TPT_SEQ.toString()));
		criteria.add(Restrictions.isNotNull("lpp."+FatListaPacApac.Fields.DT_CONFIRMADO.toString()));
		criteria.add(Restrictions.eqProperty("lap."+FatLaudoPacApac.Fields.LPP_SEQ.toString(), 
				"lpp."+FatListaPacApac.Fields.SEQ.toString()));
		criteria.add(Restrictions.eqProperty("lpp."+FatListaPacApac.Fields.TPT_SEQ.toString(), 
				"t."+FatTipoTratamentos.Fields.SEQ.toString()));
		criteria.add(Restrictions.eqProperty("p."+FatPacienteTransplantes.Fields.PAC_CODIGO.toString(),
				"lpp."+FatListaPacApac.Fields.PAC_CODIGO.toString()));
		criteria.add(Restrictions.eqProperty("p."+FatPacienteTransplantes.Fields.TTR_SEQ.toString(), 
				"t."+FatTipoTratamentos.Fields.SEQ.toString()));
		 
		return (FatLaudoPacApac) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * consulta c_pend_impr
	 * #40213 
	 * @param conNumero
	 * @return
	 */
	public boolean verificaLaudoListaApacEspecialidadeTratamentoConsultaGradeAgendamen(Integer conNumero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatLaudoPacApac.class, "lap");
		criteria.createAlias("lap."+FatLaudoPacApac.Fields.FAT_LISTA_PAC_APAC.toString(), "lpp");
		criteria.createAlias("lpp."+FatListaPacApac.Fields.PACICENTE.toString(), "aip");	
		criteria.createAlias("aip."+AipPacientes.Fields.AAC_CONSULTAS.toString(), "con");
		criteria.createAlias("con."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grd");
		
		criteria.createAlias("aip."+AipPacientes.Fields.PAC_TRANSPLANTE.toString(), "p");
		criteria.createAlias("p."+FatPacienteTransplantes.Fields.TIPO_TRATAMENTO.toString(), "t");
		criteria.createAlias("t."+FatTipoTratamentos.Fields.ESPECIALIDADE_TRATAMENTO.toString(), "etr");
		
		criteria.add(Restrictions.eq("con."+AacConsultas.Fields.NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eqProperty("grd."+AacGradeAgendamenConsultas.Fields.SEQ.toString(), 
				"con."+AacConsultas.Fields.GRD_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("grd."+AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), 
				"etr."+FatEspecialidadeTratamento.Fields.ESP_SEQ.toString()));
		criteria.add(Restrictions.eq("etr."+FatEspecialidadeTratamento.Fields.IND_LISTA_CANDIDATO.toString(), "S"));
		criteria.add(Restrictions.eqProperty("lpp."+FatListaPacApac.Fields.PAC_CODIGO.toString(), 
				"con."+AacConsultas.Fields.PAC_CODIGO.toString()));
		criteria.add(Restrictions.eqProperty("lpp."+FatListaPacApac.Fields.TPT_SEQ.toString(), 
				"etr."+FatEspecialidadeTratamento.Fields.TPT_SEQ.toString()));
		criteria.add(Restrictions.isNotNull("lpp."+FatListaPacApac.Fields.DT_CONFIRMADO.toString()));
		criteria.add(Restrictions.eqProperty("lap."+FatLaudoPacApac.Fields.LPP_SEQ.toString(), 
				"lpp."+FatListaPacApac.Fields.SEQ.toString()));
	
		criteria.add(Restrictions.eq("lpp."+FatListaPacApac.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		criteria.add(Restrictions.eq("lap."+FatLaudoPacApac.Fields.IND_COMPARECEU.toString(), "S"));
		
		if(isOracle()){
			criteria.add(Restrictions.sqlRestriction("TRUNC(this_.CRIADO_EM) = TRUNC(con3_.DT_CONSULTA)"));
		}else{
			criteria.add(Restrictions.sqlRestriction("date_trunc('DAY', this_.CRIADO_EM) "
												 + "= date_trunc('DAY', con3_.DT_CONSULTA)"));
		}
		
		return executeCriteriaExists(criteria);
	}

	/**
	 * 42803
	 * C1
	 */
	public FatLaudoPacApac obterLaudoRelacionadoConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatLaudoPacApac.class, "lap");
		criteria.createAlias("lap."+FatLaudoPacApac.Fields.FAT_LISTA_PAC_APAC.toString(), "lpp");
		criteria.createAlias("lpp."+FatListaPacApac.Fields.TIPO_TRATAMENTO.toString(), "tpt");	
		criteria.createAlias("tpt."+FatTipoTratamentos.Fields.ITEM_PROCED_HOSPITALAR.toString(), "iph");
		
		criteria.add(Restrictions.eqProperty("tpt."+FatTipoTratamentos.Fields.IPH_PHO_SEQ.toString(), "iph."+FatItensProcedHospitalar.Fields.PHO_SEQ.toString()));
		criteria.add(Restrictions.eq("lap."+FatLaudoPacApac.Fields.CON_NUMERO.toString(), numeroConsulta));
		
		criteria.addOrder(Order.desc("lap."+FatLaudoPacApac.Fields.CRIADO_EM.toString()));
		
		List<FatLaudoPacApac> list = executeCriteria(criteria);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}else{
			return null; 
		}
	}
	
	/**
	 * 42803
	 * C3
	 * C4
	 * C5
	 * C6
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterProcedimentoSolicitado(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatLaudoPacApac.class, "lap");
		criteria.createAlias("lap."+FatLaudoPacApac.Fields.FAT_LISTA_PAC_APAC.toString(), "lpp");
		criteria.createAlias("lpp."+FatListaPacApac.Fields.TIPO_TRATAMENTO.toString(), "tpt");
		criteria.createAlias("tpt."+FatTipoTratamentos.Fields.ITEM_PROCED_HOSPITALAR.toString(), "iph");
		criteria.createAlias("lap."+FatLaudoPacApac.Fields.SERVIDOR_ASSINA.toString(), "ser");
		criteria.createAlias("ser."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.createAlias("lap."+FatLaudoPacApac.Fields.CID.toString(), "cid");
		criteria.createAlias("lap."+FatLaudoPacApac.Fields.AAC_CONSULTAS.toString(), "con");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("iph." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.CODIGO_TABELA.toString()))
				.add(Projections.property("iph." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.DESCRICAO_IPH.toString()))
				.add(Projections.property("lap." + FatLaudoPacApac.Fields.APAC_NUMERO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.NUMERO_APAC.toString()))
				.add(Projections.property("cid." + AghCid.Fields.DESCRICAO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.DESCRICAO_CID.toString()))
				.add(Projections.property("cid." + AghCid.Fields.CODIGO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.CID_10_PRINCIPAL.toString()))
				.add(Projections.property("lap." + FatLaudoPacApac.Fields.OBSERVACAO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.OBSERVADOR_LAP.toString()))
				.add(Projections.property("lap." + FatLaudoPacApac.Fields.APAC_NUMERO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.NUMERO_AUTORIZACAO.toString()))
				.add(Projections.property("con." + AacConsultas.Fields.COD_CENTRAL.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.CMCE.toString()))
				.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.NOME_PROFISSIONAL_SOLICITANTE.toString()))
				.add(Projections.property("pes." + RapPessoasFisicas.Fields.CPF.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.PES_CPF.toString()))
				.add(Projections.property("lap." + FatLaudoPacApac.Fields.CRIADO_EM.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.DATA_SOLICITACAO.toString())));
		
		criteria.add(Restrictions.eq("lap."+FatLaudoPacApac.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eqProperty("tpt."+FatTipoTratamentos.Fields.IPH_PHO_SEQ.toString(), "iph." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(LaudoSolicitacaoAutorizacaoProcedAmbVO.class));
		return (LaudoSolicitacaoAutorizacaoProcedAmbVO) executeCriteriaUniqueResult(criteria);
	}

	public void persistir(FatListaPacApac listaPacApac) {
		this.persistir(listaPacApac);
	}
	
	/**
	 * 47668
	 * c18
	 */
	public Long obterApacNumero(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatLaudoPacApac.class, "lap");
		
		criteria.add(Restrictions.eq("lap."+FatLaudoPacApac.Fields.CON_NUMERO.toString(), numeroConsulta));
		
		List<FatLaudoPacApac> list = executeCriteria(criteria);
		if(list != null && !list.isEmpty()){
			return list.get(0).getApacNumero();
		}else{
			return null; 
		}
	}

}
