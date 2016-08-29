package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciNotificacaoGmr;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptPrescricaoCiclo;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import br.gov.mec.aghu.core.utils.DateUtil;

public class GerarListaPacientesEmAtendimentoQueryBuilder extends QueryBuilder<String> {

	private static final String PARENTESE = ")";
	private static final String SELECT_MIN = " (SELECT MIN(CES.";
	private static final String ALIAS_PAC = ", PAC.";
	private static final long serialVersionUID = 7887694433254251380L;
	private static final String PTE = " = PTE.";
	private static final String SES = ", SES.";
	private static final String SELECT_COUNT = " (SELECT COUNT(*) ";
	private static final String FROM = "   FROM ";
	
	@Override
	protected String createProduct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doBuild(String arg0) {
		// TODO Auto-generated method stub
		
	}	
	
	/**
	 * Consulta principal, Lista de Pacientes Em Atendimento. (C1)
	 * @param dataInicio
	 * @param horario
	 * @param tipoSessao
	 * @param sala
	 * @param acomodacao
	 * @param tipoAcomodacao
	 * @param mpaProtocoloAssistencial
	 * @param valorNumerico
	 * @return List<ListaPacienteAguardandoAtendimentoVO>
	 */
	public String consultarListaPacientesEmAtendimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial, BigDecimal valorNumerico){
		
		Date dataFinal = new Date();
		Date dataInicial = DateUtils.addDays(dataFinal, -valorNumerico.intValue());
		
		StringBuffer primeiraconsulta = new StringBuffer(500);		
		primeiraconsulta.append(SELECT_COUNT)
		.append(FROM).append(MptHorarioSessao.class.getSimpleName()).append(" CHS, ")
		.append(MptAgendamentoSessao.class.getSimpleName()).append(" CAG WHERE ")		
		.append("   CAG."+MptAgendamentoSessao.Fields.SEQ.toString()+ " = CHS."+MptHorarioSessao.Fields.AGS_SEQ.toString())
		.append("   AND CAG.").append(MptAgendamentoSessao.Fields.PAC_CODIGO.toString()).append(" = PAC.")
		.append(AipPacientes.Fields.CODIGO.toString())
		.append("   AND CHS."+MptHorarioSessao.Fields.DATA_INICIO.toString() + "  BETWEEN  "+" '"+ DateUtil.obterDataFormatada(dataInicial, "dd/MM/yyyy") +"' "+" AND "+"'"+ DateUtil.obterDataFormatada(dataFinal, "dd/MM/yyyy") +"' "+") AS primeiraconsulta ");
		
		StringBuffer indgmr = new StringBuffer(500);		
		indgmr.append(SELECT_COUNT)
		.append(FROM).append(MciNotificacaoGmr.class.getSimpleName())
		.append(" CNG WHERE ")
		.append("   CNG."+MciNotificacaoGmr.Fields.PAC_CODIGO.toString()+ " = PAC."+AipPacientes.Fields.CODIGO.toString())
		.append("   AND CNG."+MciNotificacaoGmr.Fields.IND_NOTIFICACAO_ATIVA.toString()+ " = 'S')");
		
		StringBuffer triagem = new StringBuffer(500);		
		triagem.append(SELECT_MIN).append(MptExtratoSessao.Fields.CRIADO_EM.toString())
		.append(PARENTESE) 
		.append(FROM).append(MptExtratoSessao.class.getSimpleName())
		.append(" CES  WHERE ")
		.append("   CES."+MptExtratoSessao.Fields.MPTSESSAO_SEQ.toString()+ " = HOR."+MptHorarioSessao.Fields.SES_SEQ.toString())
		.append("   AND CES."+MptExtratoSessao.Fields.IND_SITUACAO.toString()+ " = '" + DominioSituacaoSessao.SAA + "' )");
		
		StringBuffer hql = new StringBuffer(750);		
		hql.append(" SELECT new br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO (HOR." + MptHorarioSessao.Fields.DATA_INICIO + ", HOR." + MptHorarioSessao.Fields.DATA_INICIO + SES + MptSessao.Fields.DTHR_CHEGADA + " , " +triagem.toString()+" , ")
		   .append("PAC." + AipPacientes.Fields.NOME + ALIAS_PAC + AipPacientes.Fields.SEXO + ALIAS_PAC + AipPacientes.Fields.DATA_NASCIMENTO + ALIAS_PAC + AipPacientes.Fields.PRONTUARIO + ", CLO." + MptPrescricaoCiclo.Fields.CICLO + ", ")
		   .append("CLO." + MptPrescricaoCiclo.Fields.SEQ + ", PES." + RapPessoasFisicas.Fields.NOME + SES + MptSessao.Fields.IND_SITUACAOSESSAO + SES + MptSessao.Fields.IND_SITUACAOADMINISTRACAO + SES + MptSessao.Fields.SEQ + ", ATD." + AghAtendimentos.Fields.SEQ + ", ")
		   .append("HOR." + MptHorarioSessao.Fields.DIA + ", PES_RES." + RapPessoasFisicas.Fields.NOME + SES + MptSessao.Fields.IND_MEDICAMENTO_DOMICILIAR + ", "+ primeiraconsulta.toString() + " , " + indgmr.toString()+ALIAS_PAC + AipPacientes.Fields.CODIGO + SES + MptSessao.Fields.IND_SITUACAOPRESCRICAOSESSAO + " ) ");
		
		createFrom(hql);
		
		createWhere(hql);

		filtros(horario, sala, acomodacao, tipoAcomodacao,	mpaProtocoloAssistencial, hql);
		
		return  hql.toString();
	}
	
	private void createFrom(StringBuffer hql) {
		hql.append(FROM).append(MptHorarioSessao.class.getSimpleName()).append(" HOR, ")
		   .append(MptAgendamentoSessao.class.getSimpleName()).append(" AGE, ")
		   .append(AipPacientes.class.getSimpleName()).append(" PAC, ")
		   .append(MptSalas.class.getSimpleName()).append(" SAL, ") 
		   .append(MptLocalAtendimento.class.getSimpleName()).append(" ACO, ") 
		   .append(MptTipoSessao.class.getSimpleName()).append(" TIP, ")
		   //.append(MptTurnoTipoSessao.class.getSimpleName()).append(" TUR, ")
		   .append(MptSessao.class.getSimpleName()).append(" SES, ")		   
		   .append(MptPrescricaoPaciente.class.getSimpleName()).append(" PTE, ")
		   .append(RapServidores.class.getSimpleName()).append(" SER, ")
		   .append(RapPessoasFisicas.class.getSimpleName()).append(" PES, ")
		   .append(MptPrescricaoCiclo.class.getSimpleName()).append(" CLO, ")
		   .append(AghAtendimentos.class.getSimpleName()).append(" ATD, ")		   
		   .append(MptPrescricaoCiclo.class.getSimpleName()).append(" PEC LEFT JOIN ")
		   .append("PEC." + MptPrescricaoCiclo.Fields.PROTOCOLO_CICLO.toString() + " PRC LEFT JOIN ")
		   .append("SES." + MptSessao.Fields.RESPONSAVEL.toString() + " RES LEFT JOIN ")
		   .append("RES." + RapServidores.Fields.PESSOA_FISICA.toString() + " PES_RES ");
		
	}
	
	private void createWhere(StringBuffer hql) {
		hql.append("   WHERE ")
		   .append("            HOR."+MptHorarioSessao.Fields.AGS_SEQ.toString()+ " = AGE."+MptAgendamentoSessao.Fields.SEQ.toString())
		   .append("        AND AGE."+MptAgendamentoSessao.Fields.PAC_CODIGO.toString()+" = PAC."+AipPacientes.Fields.CODIGO.toString())
		   .append("        AND AGE."+MptAgendamentoSessao.Fields.SAL_SEQ.toString()+" = SAL."+MptSalas.Fields.SEQ.toString())
		   .append("        AND HOR."+MptHorarioSessao.Fields.LOA_SEQ.toString()+" = ACO."+MptLocalAtendimento.Fields.SEQ.toString())
		   .append("        AND AGE."+MptAgendamentoSessao.Fields.TPS_SEQ.toString()+" = TIP."+MptTipoSessao.Fields.SEQ.toString())
		   //.append("        AND TIP."+MptTipoSessao.Fields.SEQ.toString()+" = TUR."+MptTurnoTipoSessao.Fields.TPS_SEQ.toString())
		   
		   //.append("        AND TO_CHAR(HOR."+MptHorarioSessao.Fields.DATA_INICIO.toString()+", 'HH24:MI') BETWEEN TO_CHAR(TUR."+MptTurnoTipoSessao.Fields.HR_INICIO.toString()+ ", 'HH24:MI') AND TO_CHAR(TUR."+MptTurnoTipoSessao.Fields.HR_FIM.toString()+ ", 'HH24:MI')")
		   
		   .append("        AND HOR."+MptHorarioSessao.Fields.SES_SEQ.toString()+" = SES."+MptSessao.Fields.SEQ.toString())
		   .append("        AND SES."+MptSessao.Fields.MPT_PRESCRICAO_PACIENTE_ATD_SEQ.toString()+PTE+MptPrescricaoPaciente.Fields.ATD_SEQ.toString())
		   .append("        AND SES."+MptSessao.Fields.MPT_PRESCRICAO_PACIENTE_SEQ.toString()+PTE+MptPrescricaoPaciente.Fields.SEQ.toString())
		   		   
		   .append("        AND ( (PTE."+MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()+" IS NULL AND ")
		   .append("         	  SER."+RapServidores.Fields.MATRICULA.toString()+PTE+MptPrescricaoPaciente.Fields.SER_MATRICULA.toString())
		   .append("              ) OR (PTE."+MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()+" IS NOT NULL AND SER."+RapServidores.Fields.MATRICULA.toString()+PTE+MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString())		   
		   .append("              )) ")

		   .append("        AND ( (PTE."+MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString()+" IS NULL AND ")
		   .append("         	  SER."+RapServidores.Fields.CODIGO_VINCULO.toString()+PTE+MptPrescricaoPaciente.Fields.SER_VIN_CODIGO.toString())
		   .append("              ) OR ( PTE."+MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString()+" IS NOT NULL AND SER."+RapServidores.Fields.CODIGO_VINCULO.toString()+PTE+MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString())		   
		   .append("              ) )")
		   
		   .append("        AND PES."+RapPessoasFisicas.Fields.CODIGO.toString()+ " = SER."+RapServidores.Fields.PES_CODIGO)		   
		   .append("        AND CLO."+MptPrescricaoCiclo.Fields.SEQ.toString()+ " = SES."+MptSessao.Fields.CLO_SEQ)		   
		   .append("        AND CLO."+MptPrescricaoCiclo.Fields.ATENDIMENTOS.toString()+" = ATD."+AghAtendimentos.Fields.SEQ.toString())
		   .append("        AND PEC."+MptPrescricaoCiclo.Fields.SEQ.toString()+ " = SES."+MptSessao.Fields.CLO_SEQ)		   
		   
		   .append("        AND SES."+MptSessao.Fields.IND_SITUACAOSESSAO.toString()+" = 'SAT' ") /* SITUACAO AGUARDANDO ATENDIMENTO */
		   
		   .append("        AND TO_CHAR(HOR."+MptHorarioSessao.Fields.DATA_INICIO.toString()+", 'DD/MM/YYYY') = :dataInicio ");
	}
	

	private void filtros(MptTurnoTipoSessao horario, Short sala, MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial, StringBuffer hql) {
		if (horario != null){
			//.append("        AND TUR."+MptTurnoTipoSessao.Fields.TURNO.toString()+" IN ('M', 'T', 'N') ")		   
			hql.append("        AND (TO_CHAR(HOR."+MptHorarioSessao.Fields.DATA_INICIO.toString()+", 'HH24:MI')) BETWEEN :horarioInicio AND :horarioFim ");			   
	    }
	   
		hql.append("        AND TIP."+MptTipoSessao.Fields.SEQ.toString()+" = :tipoSessao ");
		
	   if (sala != null){
		   hql.append("        AND SAL."+MptSalas.Fields.SEQ.toString()+" = :sala ");			   
	   }
	   
	   if (acomodacao != null && acomodacao.getSeq() != null){
		   hql.append("        AND ACO."+MptLocalAtendimento.Fields.SEQ.toString()+" = :acomodacao ");			   
	   }
	   if (tipoAcomodacao != null){
		   hql.append("        AND ACO."+MptLocalAtendimento.Fields.TIPO_LOCAL.toString()+" = :tipoAcomodacao ");			   
	   }
	   if(mpaProtocoloAssistencial != null){
		   hql.append("        AND PRC."+MptProtocoloCiclo.Fields.VPATPASEQP.toString()+" = :mpaProtocoloAssistencial ")
		   .append("        AND PRC."+MptProtocoloCiclo.Fields.VPASEQP.toString()+" IN (:tpaSeqList) ");			   
	   }
	}
}