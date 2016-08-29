package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencial;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptPrescricaoCiclo;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PacientesAtendimentoConcluidoQueryBuilder extends QueryBuilder<DetachedCriteria>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1372458518281483118L;
	
	private static final String SES = "SES.";
	private static final String PAC = "PAC.";
	private static final String CLO = "CLO.";
	private static final String HOR = "HOR.";
    private static final String AGE = "AGE.";
	
	private Date dataInicio; 
	private MptTurnoTipoSessao horario; 
	private Short tipoSessao;
	private Short sala; 
	private MptLocalAtendimento acomodacao; 
	private DominioTipoAcomodacao tipoAcomodacao; 
	private MpaProtocoloAssistencial mpaProtocoloAssistencial; 
	private Date dataInicial;
	private Date dataFinal;

    @Override
    protected DetachedCriteria createProduct() {
        return DetachedCriteria.forClass(MptHorarioSessao.class, "HOR");
    }
    
    public DetachedCriteria build(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial,
			Date dataInicial, Date dataFinal) {

    	this.dataInicio = dataInicio; 
    	this.horario = horario; 
    	this.tipoSessao= tipoSessao;
    	this.sala = sala; 
    	this.acomodacao = acomodacao; 
    	this.tipoAcomodacao = tipoAcomodacao; 
    	this.mpaProtocoloAssistencial = mpaProtocoloAssistencial; 
    	this.dataInicial = dataInicial;
    	this.dataFinal = dataFinal;
    	
        return super.build();
    }

    @Override
    protected void doBuild(DetachedCriteria aProduct) {
    	criarFrom(aProduct);

		criarProjecao(aProduct);
		
		criarWhere(aProduct);
		
		aProduct.setResultTransformer(Transformers.aliasToBean(PacienteConcluidoVO.class));
		
		aProduct.addOrder(Order.asc(HOR + MptHorarioSessao.Fields.DATA_INICIO.toString()));
    }

	private void criarWhere(DetachedCriteria aProduct) {
		aProduct.add(Restrictions.in(SES+MptSessao.Fields.IND_SITUACAOSESSAO.toString(), 
				new Object[]{ DominioSituacaoSessao.SAC, DominioSituacaoSessao.SSU, DominioSituacaoSessao.SFA, DominioSituacaoSessao.SIT, DominioSituacaoSessao.SNC}));
		
		if (dataInicio  != null) {			 
			 String formataData = " (TO_CHAR(THIS_.DATA_INICIO, 'DD/MM/YYYY') = ?) ";			
			 aProduct.add(Restrictions.sqlRestriction(formataData, DateUtil.obterDataFormatada(dataInicio, "dd/MM/YYYY") , StandardBasicTypes.STRING));			
		}

		if (horario  != null) {
			String horaFormatada = " (TO_CHAR(THIS_.DATA_INICIO, 'HH24:MI')) ";
			aProduct.add(Restrictions.sqlRestriction(horaFormatada + " BETWEEN '" + DateUtil.obterDataFormatada(horario.getHoraInicio(), "HH:mm") + "' AND '" + DateUtil.obterDataFormatada(horario.getHoraFim(), "HH:mm") + "'"));			
		}

		if (tipoSessao  != null) {
			aProduct.add(Restrictions.eq("TIP." + MptTipoSessao.Fields.SEQ.toString(), tipoSessao));			
		}

		if (sala  != null) {
			aProduct.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), sala));			
		}

		if (acomodacao  != null && acomodacao.getSeq() != null) {
			aProduct.add(Restrictions.eq("ACO." + MptLocalAtendimento.Fields.SEQ.toString(), acomodacao.getSeq()));			
		}

		if (tipoAcomodacao  != null) {
			aProduct.add(Restrictions.eq("ACO." + MptLocalAtendimento.Fields.TIPO_LOCAL.toString(), tipoAcomodacao));			
		}
		
		if(mpaProtocoloAssistencial != null){
			aProduct.add(Restrictions.eq("PRC." + MptProtocoloCiclo.Fields.VPATPASEQP.toString(), mpaProtocoloAssistencial.getSeq()));
			List<Short> tpaSeqList = new ArrayList<Short>();
			Hibernate.initialize(mpaProtocoloAssistencial.getMpaVersaoProtAssistenciales());
			for (MpaVersaoProtAssistencial item : mpaProtocoloAssistencial.getMpaVersaoProtAssistenciales()) {
				tpaSeqList.add(item.getId().getSeqp());
			}
			if (!tpaSeqList.isEmpty()) {
				aProduct.add(Restrictions.in("PRC." + MptProtocoloCiclo.Fields.VPASEQP.toString(), tpaSeqList));
			}
		}
	}

	private void criarProjecao(DetachedCriteria aProduct) {
		String countPrimeiraConsulta = " (SELECT COUNT(*) FROM AGH.MPT_HORARIO_SESSAO CHS "
				+"    INNER JOIN AGH.MPT_AGENDAMENTO_SESSAO CAG ON CAG.SEQ = CHS.AGS_SEQ WHERE CAG.PAC_CODIGO = PAC2_.CODIGO "
				+"    AND TO_CHAR(CHS.DATA_INICIO, 'YYYYMMDD') BETWEEN '"+ DateUtil.obterDataFormatada(dataInicial, "YYYYMMdd") +"' AND '"+ DateUtil.obterDataFormatada(dataFinal, "YYYYMMdd") +"') AS primeiraconsulta ";
		
		String countSituacaoHorario = " (SELECT COUNT(*) FROM AGH.MCI_NOTIFICACAO_GMR WHERE PAC_CODIGO = PAC2_.CODIGO AND IND_NOTIFICACAO_ATIVA = 'S') AS indgmr ";
		String selectDataTriagem = " (SELECT MIN(CRIADO_EM) FROM AGH.MPT_EXTRATO_SESSAO EXT WHERE EXT.SES_SEQ = ses6_.SEQ AND IND_SITUACAO = 'SEA') AS dataTriagem ";
		String selectDataAtendimento = " (SELECT MIN(CRIADO_EM) FROM AGH.MPT_EXTRATO_SESSAO EXT WHERE EXT.SES_SEQ = ses6_.SEQ AND IND_SITUACAO = 'SAT') AS dataAtendimento ";

		aProduct.setProjection(Projections.distinct((Projections.projectionList()				
		.add(Projections.property(HOR + MptHorarioSessao.Fields.SESSAO_CODIGO.toString()), PacienteConcluidoVO.Fields.CODIGO.toString())
		.add(Projections.property(HOR + MptHorarioSessao.Fields.DATA_INICIO.toString()), PacienteConcluidoVO.Fields.DATA_INICIO.toString())
		.add(Projections.property(SES + MptSessao.Fields.DTHR_CHEGADA.toString()), PacienteConcluidoVO.Fields.DATA_CHEGADA.toString())
		.add(Projections.property(PAC + AipPacientes.Fields.NOME.toString()), PacienteConcluidoVO.Fields.PACIENTE.toString())
		.add(Projections.property(PAC + AipPacientes.Fields.SEXO.toString()), PacienteConcluidoVO.Fields.SEXO.toString())
		.add(Projections.property(PAC + AipPacientes.Fields.CODIGO.toString()), PacienteConcluidoVO.Fields.CODIGO_PACIENTE.toString())
		.add(Projections.property(PAC + AipPacientes.Fields.DATA_NASCIMENTO.toString()), PacienteConcluidoVO.Fields.DATANASCIMENTO.toString())
		.add(Projections.property(PAC + AipPacientes.Fields.PRONTUARIO.toString()), PacienteConcluidoVO.Fields.PRONTUARIO.toString())
		.add(Projections.property(CLO + MptPrescricaoCiclo.Fields.CICLO.toString()), PacienteConcluidoVO.Fields.NUMERO_CICLO.toString())
		.add(Projections.property(CLO + MptPrescricaoCiclo.Fields.SEQ.toString()), PacienteConcluidoVO.Fields.CODIGO_CICLO.toString())
		.add(Projections.property(SES + MptSessao.Fields.IND_SITUACAOSESSAO.toString()), PacienteConcluidoVO.Fields.SITUACAO_SESSAO.toString())
		.add(Projections.property(SES + MptSessao.Fields.IND_MEDICAMENTO_DOMICILIAR.toString()), PacienteConcluidoVO.Fields.MEDICAMENTO_DOMICILIAR.toString())
		.add(Projections.property(SES + MptSessao.Fields.IND_IMPRESSAO_PULSEIRA.toString()), PacienteConcluidoVO.Fields.IMPRESSAO_PULSEIRA.toString())
		.add(Projections.property(SES + MptSessao.Fields.IND_SITUACAOADMINISTRACAO.toString()), PacienteConcluidoVO.Fields.SITUACAO_ADMINISTRACAO.toString())
		.add(Projections.property(SES + MptSessao.Fields.IND_SITUACAOPRESCRICAOSESSAO.toString()), PacienteConcluidoVO.Fields.SITUACAO_LM.toString())
		.add(Projections.property("ATD." + AghAtendimentos.Fields.SEQ.toString()), PacienteConcluidoVO.Fields.CODIGO_ATENDIMENTO.toString())
		.add(Projections.property(HOR + MptHorarioSessao.Fields.DIA.toString()), PacienteConcluidoVO.Fields.DIA.toString())
		.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), PacienteConcluidoVO.Fields.RESPONSAVEL.toString())
		.add(Projections.property(AGE + MptAgendamentoSessao.Fields.SEQ.toString()), PacienteConcluidoVO.Fields.SEQ_AGENDAMENTO.toString())
		.add(Projections.sqlProjection(countPrimeiraConsulta, new String [] {PacienteConcluidoVO.Fields.PRIMEIRA_CONSULTA.toString()}, new Type[] {new IntegerType()}))
		.add(Projections.sqlProjection(countSituacaoHorario, new String [] {PacienteConcluidoVO.Fields.INDGMR.toString()}, new Type[] {new IntegerType()}))
		.add(Projections.sqlProjection(selectDataTriagem, new String [] {PacienteConcluidoVO.Fields.DATA_TRIAGEM.toString()}, new Type[] {new TimestampType()}))
		.add(Projections.sqlProjection(selectDataAtendimento, new String [] {PacienteConcluidoVO.Fields.DATA_ATENDIMENTO.toString()}, new Type[] {new TimestampType()}))
		.add(Projections.property(SES + MptSessao.Fields.SEQ.toString()), PacienteConcluidoVO.Fields.SEQ_SESSAO.toString()))));
	}

	private void criarFrom(DetachedCriteria aProduct) {
		aProduct.createAlias(HOR+MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGE", JoinType.INNER_JOIN);
		aProduct.createAlias(AGE+MptAgendamentoSessao.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		aProduct.createAlias(AGE+MptAgendamentoSessao.Fields.SALA.toString(), "SAL", JoinType.INNER_JOIN);
		aProduct.createAlias(HOR+MptHorarioSessao.Fields.LOCAL_ATENDIMENTO.toString(), "ACO", JoinType.INNER_JOIN);
		aProduct.createAlias(AGE+MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TIP", JoinType.INNER_JOIN);
		aProduct.createAlias(HOR+MptHorarioSessao.Fields.SESSAO.toString(), "SES", JoinType.INNER_JOIN);
		aProduct.createAlias(SES+MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
		aProduct.createAlias(SES+MptSessao.Fields.MPT_PRESCRICAO_CICLO.toString(), "CLO", JoinType.INNER_JOIN);
		aProduct.createAlias(CLO+MptPrescricaoCiclo.Fields.ATENDIMENTOS.toString(), "ATD", JoinType.INNER_JOIN);
		aProduct.createAlias(CLO+MptPrescricaoCiclo.Fields.PROTOCOLO_CICLO.toString(), "PRC", JoinType.LEFT_OUTER_JOIN);
		aProduct.createAlias(SES+MptSessao.Fields.RESPONSAVEL.toString(), "RESP", JoinType.LEFT_OUTER_JOIN);
		aProduct.createAlias("RESP."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
	}
}
