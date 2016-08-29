package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescricaoMedicamento;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencial;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptPrescricaoCuidado;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.vo.QtdSessoesTratamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioAgendamentoAmbulatorioVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacientesTratamentoSessaoVO;

public class MptPrescricaoPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptPrescricaoPaciente> {

	private static final long serialVersionUID = -3536388624983326503L;

	/**
	 * C1 da #18505 Sumário prescrição Quimio POL
	 * @param apaAtdSeq
	 * @return
	 */
	public List<MptPrescricaoPaciente> listarPeriodosSessoesQuimio(Integer apaAtdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoPaciente.class,"pte");
		criteria.add(Restrictions.eq("pte." + MptPrescricaoPaciente.Fields.ATENDIMENTO_SEQ.toString(), apaAtdSeq));
		
		ProjectionList plPrescricaoMedicamento = Projections.projectionList();
		plPrescricaoMedicamento.add(Projections.property("pmo.".concat(MptPrescricaoMedicamento.Fields.PTE_ATD_SEQ.toString())));
		plPrescricaoMedicamento.add(Projections.property("pmo.".concat(MptPrescricaoMedicamento.Fields.PTE_SEQ.toString())));
		
		DetachedCriteria subquery = DetachedCriteria.forClass(MptPrescricaoMedicamento.class, "pmo");
		subquery.setProjection(plPrescricaoMedicamento);
		subquery.add(
				Restrictions.eqProperty(
				"pmo.".concat(MptPrescricaoMedicamento.Fields.PTE_SEQ.toString()), 
				"pte.".concat(MptPrescricaoPaciente.Fields.SEQ.toString())));
		subquery.add(
				Restrictions.eqProperty(
				"pmo.".concat(MptPrescricaoMedicamento.Fields.PTE_ATD_SEQ.toString()), 
				"pte.".concat(MptPrescricaoPaciente.Fields.ATD_SEQ.toString())));
		subquery.add(Restrictions.in("pmo.".concat(MptPrescricaoMedicamento.Fields.SITUACAO_ITEM.toString()), 
				new DominioSituacaoItemPrescricaoMedicamento[] {
					DominioSituacaoItemPrescricaoMedicamento.V,DominioSituacaoItemPrescricaoMedicamento.A, 
					DominioSituacaoItemPrescricaoMedicamento.E}));
		
		ProjectionList plPrescricaoCuidado = Projections.projectionList();
		plPrescricaoCuidado.add(Projections.property("pco.".concat(MptPrescricaoCuidado.Fields.PTE_ATD_SEQ.toString())));
		plPrescricaoCuidado.add(Projections.property("pco.".concat(MptPrescricaoCuidado.Fields.PTE_SEQ.toString())));
		
		DetachedCriteria subquery1 = DetachedCriteria.forClass(MptPrescricaoCuidado.class, "pco");
		subquery1.setProjection(plPrescricaoCuidado);
		subquery1.add(Restrictions.eqProperty("pco.".concat(MptPrescricaoCuidado.Fields.PTE_ATD_SEQ.toString()), 
				"pte.".concat(MptPrescricaoPaciente.Fields.ATD_SEQ.toString())));
		subquery1.add(Restrictions.eqProperty("pco.".concat(MptPrescricaoCuidado.Fields.PTE_SEQ.toString()), 
				"pte.".concat(MptPrescricaoPaciente.Fields.SEQ.toString())));
		subquery1.add(Restrictions.in("pco.".concat(MptPrescricaoCuidado.Fields.SITUACAO_ITEM.toString()), 
				new DominioSituacaoItemPrescricaoMedicamento[] {
					DominioSituacaoItemPrescricaoMedicamento.V,DominioSituacaoItemPrescricaoMedicamento.A, 
					DominioSituacaoItemPrescricaoMedicamento.E}));
		
		criteria.add(Restrictions.or(
				Subqueries.exists(subquery1),
				Subqueries.exists(subquery)
				));
		
		criteria.add(Restrictions.isNotNull("pte."+ MptPrescricaoPaciente.Fields.SERVIDOR_VALIDO.toString()));
		
		criteria.addOrder(Order.asc(MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * C5 da #18505 Sumário prescrição Quimio POL
	 * @param trpSeq
	 * @return
	 */
	 public Integer obterPrescricaoPacienteporTrpSeq(Integer trpSeq){
		 DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoPaciente.class, "pte");
		 criteria.createAlias("pte." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "atd");
		 
		 ProjectionList pl = Projections.projectionList();
		 pl = Projections.projectionList().add(Projections.distinct(Projections
				 .property("atd.".concat(AghAtendimentos.Fields.SEQ.toString()))));
		 criteria.setProjection(pl);
		 
		 criteria.add(Restrictions.eq("atd."+ AghAtendimentos.Fields.TRP_SEQ.toString(), trpSeq));
		return (Integer) executeCriteriaUniqueResult(criteria);
	 }

	public List<MptPrescricaoPaciente> pesquisarPrescricoesPacientePorAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoPaciente.class,"pte");
		criteria.add(Restrictions.eq("pte." + MptPrescricaoPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.asc(MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * C4 da #18505 Sumário prescrição Quimio POL
	 * @param atdSeq
	 * @param dtInicio
	 * @param dtFim
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> listarTituloProtocoloAssistencial (Integer atdSeq, Date dtInicio, Date dtFim){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoPaciente.class, "pte");
		criteria.createAlias("pte." + MptPrescricaoPaciente.Fields.PROTOCOLO_ASSISTENCIAL.toString(), "pta");

		// Projections
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("pta." + MpaProtocoloAssistencial.Fields.TITULO.toString()))));

		criteria.add(Restrictions.eq("pte." + MptPrescricaoPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.between("pte." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString(), dtInicio, dtFim));
		criteria.add(Restrictions.isNotNull("pte." + MptPrescricaoPaciente.Fields.DTHR_VALIDA.toString()));
		
		return executeCriteria(criteria);
	}
	
	public HorarioAgendamentoAmbulatorioVO obterConvenioPlanoPorPaciente(Integer pacCodigo, Integer prescricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoPaciente.class, "PTE");
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.TRATAMENTO_TERAPEUTICO.toString(), "TRP");
		criteria.createAlias("TRP." + MptTratamentoTerapeutico.Fields.FAT_CONVENIO_SAUDE.toString(), "FCS");
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("TRP." + MptTratamentoTerapeutico.Fields.CSP_CNV_CODIGO.toString())
						, HorarioAgendamentoAmbulatorioVO.Fields.CSP_CNV_CODIGO.toString())
				.add(Projections.property("TRP." + MptTratamentoTerapeutico.Fields.CSP_SEQ.toString())
						, HorarioAgendamentoAmbulatorioVO.Fields.CSP_SEQ.toString())
				.add(Projections.property("FCS." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString())
						, HorarioAgendamentoAmbulatorioVO.Fields.GRUPO_CONVENIO.toString())));
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		if (prescricao != null) {
			criteria.add(Restrictions.eq("PTE." + MptPrescricaoPaciente.Fields.SEQ.toString(), prescricao));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(HorarioAgendamentoAmbulatorioVO.class));
		
		return (HorarioAgendamentoAmbulatorioVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #43089 - C4
	 * @param atdSeq
	 * @param pteSeq
	 * @return MptPrescricaoPaciente. 
	 */
	public MptPrescricaoPaciente obterDataPrevisaoExecucaoPorNumeroAtendimento(Integer atdSeq, Integer pteSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoPaciente.class);
		
		criteria.add(Restrictions.eq(MptPrescricaoPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MptPrescricaoPaciente.Fields.SEQ.toString(), pteSeq));
		
		return (MptPrescricaoPaciente) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #43089 - C6
	 * @param atdSeq
	 * @param pteSeq
	 * @return QtdSessoesTratamentoVO
	 */
	public QtdSessoesTratamentoVO obterQuantidadeSessoesTratamento(Integer atdSeq, Integer pteSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoPaciente.class, "PTE");
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.ATD_SEQ.toString()), QtdSessoesTratamentoVO.Fields.ATD_SEQ_PRESCRICAO_PACIENTE.toString());
		projectionList.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SEQ.toString()), QtdSessoesTratamentoVO.Fields.SEQ_PRESCRICAO_PACIENTE.toString());
		projectionList.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.OBSERVACAO.toString()), QtdSessoesTratamentoVO.Fields.OBSERVACAO_PRESCRICAO_PACIENTE.toString());
		projectionList.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SESSOES_CONTINUAS.toString()), QtdSessoesTratamentoVO.Fields.IND_SESSOES_CONTINUAS.toString());
		projectionList.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.QUANTIDADE_SESSOES.toString()), QtdSessoesTratamentoVO.Fields.QTD_SESSOES.toString());
		projectionList.add(Projections.property("PHI." + FatProcedHospInternos.Fields.SEQ.toString()), QtdSessoesTratamentoVO.Fields.SEQ_PROCED_HOSP_INTERNO.toString());
		projectionList.add(Projections.property("PHI." + FatProcedHospInternos.Fields.DESCRICAO.toString()), QtdSessoesTratamentoVO.Fields.DESCRICAO_PROCED_HOSP_INTERNO.toString());
		
		criteria.setProjection(Projections.distinct(projectionList));		
		
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "PHI", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("PTE." + MptPrescricaoPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("PTE." + MptPrescricaoPaciente.Fields.SEQ.toString(), pteSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(QtdSessoesTratamentoVO.class));
		
		return (QtdSessoesTratamentoVO) executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * #43089 - C7
	 * Obtém prescrição por número de consulta.
	 * @param conNumero
	 * @return MptPrescricaoPaciente
	 */
	public MptPrescricaoPaciente obterPrescricaoPorNumeroConsulta(Integer conNumero) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoPaciente.class);
		
		criteria.add(Restrictions.eq(MptPrescricaoPaciente.Fields.CONNUMERO.toString(), conNumero));
		criteria.add(Restrictions.isNotNull(MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()));
		
		return (MptPrescricaoPaciente) executeCriteriaUniqueResult(criteria);
	}
	
//	consulta C1 da estória #45992
	private List<PacientesTratamentoSessaoVO> pesquisarPacientesTratamentoSessao(Integer firstResult, Integer maxResult, String orderProperty, 
			boolean asc, MptTipoSessao tipoSessao, Date periodoInicial, Date periodoFinal, Boolean checkAberto, Boolean checkFechado, 
			Boolean checkPrimeiro, Boolean checkSessao, Integer codigo, Integer tipoFiltroPersonalizado, Integer parametro){			
		
			DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
			
			//Projeções
			ProjectionList coluna = Projections.projectionList();
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.CICLO.toString()), "ciclo");
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SER_MATRICULA.toString()), "matricula");
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SER_VIN_CODIGO.toString()), "codigo");
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()), "matriculaValida");
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString()), "codigoValida");
			coluna.add(Projections.property("PES1." + RapPessoasFisicas.Fields.NOME.toString()), "responsavel1");
			coluna.add(Projections.property("PES2." + RapPessoasFisicas.Fields.NOME.toString()), "responsavel2");
			coluna.add(Projections.property("SES." + MptSessao.Fields.CICLO.toString()), "cloSeq");
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.LOTE.toString()), "lote");
			coluna.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), "paciente");
			coluna.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario");
			coluna.add(Projections.property("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), "especialidade");
			coluna.add(Projections.property("FCS." + FatConvenioSaude.Fields.DESCRICAO.toString()), "convenio");
			coluna.add(Projections.property("AAP." + AacAtendimentoApacs.Fields.NUMERO.toString()), "apac");
			coluna.add(Projections.property("ITE." + AinInternacao.Fields.QRT_NUMERO.toString()), "quarto");
			coluna.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), "unidade");
			coluna.add(Projections.property("SES." + MptSessao.Fields.DTHR_INICIO.toString()), "inicioCiclo");
//			coluna.add(Projections.sqlProjection(primeiraConsulta(parametro),new String[]{PacientesTratamentoSessaoVO.Fields.PRIMEIRA_CONSULTA.toString()},new Type[] { IntegerType.INSTANCE }));
			criteria.setProjection(Projections.distinct(coluna));
			
			//Joins da consulta
			criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE");			
			criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATE");
			criteria.createAlias("ATE." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
			criteria.createAlias("ATE." + AghAtendimentos.Fields.INTERNACAO.toString(), "ITE", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ATE." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ATE." + AghAtendimentos.Fields.ATENDIMENTOS_APAC.toString(), "AAP", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ATE." + AghAtendimentos.Fields.TRATAMENTO_TERAPEUTICO.toString(), "TRP", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("TRP." + MptTratamentoTerapeutico.Fields.FAT_CONVENIO_SAUDE.toString(), "FCS", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ATE." + AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.SERVIDOR_VALIDO.toString(), "SER1", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SER1." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES1", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.SERVIDOR.toString(), "SER2", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SER2." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES2", JoinType.LEFT_OUTER_JOIN);
			
			//Condições
			if(checkAberto && !checkFechado){
				criteria.add(Restrictions.isNull("TRP." + MptTratamentoTerapeutico.Fields.DTHR_FIM.toString()));				
			}
			if(checkFechado && !checkAberto){
				criteria.add(Restrictions.isNotNull("TRP." + MptTratamentoTerapeutico.Fields.DTHR_FIM.toString()));				
			}
			if(checkPrimeiro && !checkSessao){
				criteria.add(Restrictions.sqlRestriction(primeiraConsulta(parametro) + " = 0"));			
			}
			if(checkSessao && !checkPrimeiro){
				criteria.add(Restrictions.sqlRestriction(primeiraConsulta(parametro) + " > 0"));							
			}
			outrasCondicoesPacientesTratamento(periodoInicial, periodoFinal,
					codigo, tipoFiltroPersonalizado, criteria);
				
		
		//Ordenação
		criteria.addOrder(Order.asc("PAC." + AipPacientes.Fields.NOME.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PacientesTratamentoSessaoVO.class));		
		return executeCriteria(criteria);
	}
	
	private Long pesquisarPacientesTratamentoSessaoCount(MptTipoSessao tipoSessao, Date periodoInicial, Date periodoFinal, Boolean checkAberto, Boolean checkFechado, 
			Boolean checkPrimeiro, Boolean checkSessao, Integer codigo, Integer tipoFiltroPersonalizado, Integer parametro){			
		
			DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
			
			//Projeções
			ProjectionList coluna = Projections.projectionList();
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.CICLO.toString()), "ciclo");
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SER_MATRICULA.toString()), "matricula");
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SER_VIN_CODIGO.toString()), "codigo");
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()), "matriculaValida");
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString()), "codigoValida");
			coluna.add(Projections.property("PES1." + RapPessoasFisicas.Fields.NOME.toString()), "responsavel1");
			coluna.add(Projections.property("PES2." + RapPessoasFisicas.Fields.NOME.toString()), "responsavel2");
			coluna.add(Projections.property("SES." + MptSessao.Fields.CICLO.toString()), "cloSeq");
			coluna.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.LOTE.toString()), "lote");
			coluna.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), "paciente");
			coluna.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario");
			coluna.add(Projections.property("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), "especialidade");
			coluna.add(Projections.property("FCS." + FatConvenioSaude.Fields.DESCRICAO.toString()), "convenio");
			coluna.add(Projections.property("AAP." + AacAtendimentoApacs.Fields.NUMERO.toString()), "apac");
			coluna.add(Projections.property("ITE." + AinInternacao.Fields.QRT_NUMERO.toString()), "quarto");
			coluna.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), "unidade");
			coluna.add(Projections.property("SES." + MptSessao.Fields.DTHR_INICIO.toString()), "inicioCiclo");
//			coluna.add(Projections.sqlProjection(primeiraConsulta(parametro),new String[]{PacientesTratamentoSessaoVO.Fields.PRIMEIRA_CONSULTA.toString()},new Type[] { IntegerType.INSTANCE }));
			criteria.setProjection(Projections.distinct(coluna));
			
			//Joins da consulta
			criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE");			
			criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATE");
			criteria.createAlias("ATE." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
			criteria.createAlias("ATE." + AghAtendimentos.Fields.INTERNACAO.toString(), "ITE", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ATE." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ATE." + AghAtendimentos.Fields.ATENDIMENTOS_APAC.toString(), "AAP", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ATE." + AghAtendimentos.Fields.TRATAMENTO_TERAPEUTICO.toString(), "TRP", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("TRP." + MptTratamentoTerapeutico.Fields.FAT_CONVENIO_SAUDE.toString(), "FCS", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ATE." + AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.SERVIDOR_VALIDO.toString(), "SER1", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SER1." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES1", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.SERVIDOR.toString(), "SER2", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SER2." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES2", JoinType.LEFT_OUTER_JOIN);
			
			//Condições
			if(checkAberto && !checkFechado){
				criteria.add(Restrictions.isNull("TRP." + MptTratamentoTerapeutico.Fields.DTHR_FIM.toString()));				
			}
			if(checkFechado && !checkAberto){
				criteria.add(Restrictions.isNotNull("TRP." + MptTratamentoTerapeutico.Fields.DTHR_FIM.toString()));				
			}
			if(checkPrimeiro && !checkSessao){
				criteria.add(Restrictions.sqlRestriction(primeiraConsulta(parametro) + " = 0"));			
			}
			if(checkSessao && !checkPrimeiro){
				criteria.add(Restrictions.sqlRestriction(primeiraConsulta(parametro) + " > 0"));							
			}
			
			outrasCondicoesPacientesTratamento(periodoInicial, periodoFinal, codigo, tipoFiltroPersonalizado, criteria);		
		
			return Long.valueOf(new ArrayList<Object>(executeCriteria(criteria)).size());		
	}

	private void outrasCondicoesPacientesTratamento(Date periodoInicial,
			Date periodoFinal, Integer codigo, Integer tipoFiltroPersonalizado,
			DetachedCriteria criteria) {
		if(tipoFiltroPersonalizado == 2){
			criteria.add(Restrictions.eq("SES." + MptSessao.Fields.TIPO_ATENDIMENTO.toString(), DominioSituacao.I));				
		}
		if(tipoFiltroPersonalizado == 3){
			criteria.add(Restrictions.eq("SES." + MptSessao.Fields.TIPO_ATENDIMENTO.toString(), DominioSituacao.A));				
		}
		if(codigo != 0){
			criteria.add(Restrictions.eq("ATE." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codigo));				
		}
		if(periodoInicial != null && periodoFinal != null){
			criteria.add(Restrictions.between("PTE." + MptPrescricaoPaciente.Fields.CRIADO_EM.toString(), periodoInicial, periodoFinal));				
		}
	}
	
	public List<PacientesTratamentoSessaoVO> unionListas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MptTipoSessao tipoSessao,
			Date periodoInicial, Date periodoFinal, Boolean checkAberto, Boolean checkFechado, Boolean checkPrimeiro, Boolean checkSessao, Integer codigo,
			Integer tipoFiltroPersonalizado, Integer parametro){
		
		List<PacientesTratamentoSessaoVO> lista = new ArrayList<PacientesTratamentoSessaoVO>();		
		if(tipoFiltroPersonalizado != 4){
			lista.addAll(pesquisarPacientesTratamentoSessao(firstResult, maxResult, orderProperty, asc, tipoSessao, periodoInicial, periodoFinal, checkAberto, checkFechado, 
					checkPrimeiro, checkSessao, codigo, tipoFiltroPersonalizado, parametro));
		}		
		if(tipoFiltroPersonalizado == 1 || tipoFiltroPersonalizado == 4){
			lista.addAll(pesquisarHorarioSessao(firstResult, maxResult, orderProperty, asc, periodoFinal, codigo, tipoSessao));			
		}
		
		return lista;
	}
	
	//Consulta C3 da estória #45992
	public List<PacientesTratamentoSessaoVO> pesquisarHorarioSessao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Date periodoFinal, Integer codigo, MptTipoSessao tipoSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		ProjectionList coluna2 = Projections.projectionList();
		coluna2.add(Projections.property("HRS." + MptHorarioSessao.Fields.CICLO.toString()), "horasCiclo");
		coluna2.add(Projections.property("PTA." + MpaProtocoloAssistencial.Fields.TITULO.toString()), "tituloProtocolo");
		coluna2.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), "paciente");
		coluna2.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario");
		criteria.setProjection(Projections.distinct(coluna2));
		
//		Joins da consulta
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), "VPA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VPA." + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTA", JoinType.LEFT_OUTER_JOIN);
		
//		Condições
		criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), tipoSessao));		
		criteria.add(Restrictions.eq("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.R));
		if(periodoFinal != null){
			criteria.add(Restrictions.ge("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString(), periodoFinal));			
			criteria.add(Restrictions.ge("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString(), periodoFinal));
		}
		if(codigo != 0){
			criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.PAC_CODIGO.toString(), codigo));			
		}
		
		criteria.addOrder(Order.asc("PAC." + AipPacientes.Fields.NOME.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PacientesTratamentoSessaoVO.class));
		
//		return executeCriteria(criteria, firstResult, maxResult,orderProperty, asc);
		return executeCriteria(criteria);
	}
	
	public boolean verificarExistePrescricaoPacientePorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoPaciente.class);
		criteria.add(Restrictions.eq(MptPrescricaoPaciente.Fields.SEQ.toString(), seq));
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * #43089 Query responsável por obter o vínculo e matrícula do médico.
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public MptPrescricaoPaciente obterVinculoMatriculaResponsavel(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = montarCriteriaObterVinculoMatriculaResponsavel(atdSeq, seq);
		List<MptPrescricaoPaciente> listResponsavel = executeCriteria(criteria);
		if (listResponsavel != null && !listResponsavel.isEmpty()) {
			return listResponsavel.get(0);
		}
		return null;
	}

	private DetachedCriteria montarCriteriaObterVinculoMatriculaResponsavel(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoPaciente.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString()),MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString())
				.add(Projections.property(MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()),MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()));
		if (atdSeq != null) {
			criteria.add(Restrictions.eq(MptPrescricaoPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if (seq != null) {
			criteria.add(Restrictions.eq(MptPrescricaoPaciente.Fields.SEQ.toString(), seq));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptPrescricaoPaciente.class));
		return criteria;
	}
	
	public Long pesquisarHorarioSessaoCount(Date periodoFinal, Integer codigo, MptTipoSessao tipoSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		ProjectionList coluna2 = Projections.projectionList();
		coluna2.add(Projections.property("HRS." + MptHorarioSessao.Fields.CICLO.toString()), "horasCiclo");
		coluna2.add(Projections.property("PTA." + MpaProtocoloAssistencial.Fields.TITULO.toString()), "tituloProtocolo");
		coluna2.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), "paciente");
		coluna2.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario");
		criteria.setProjection(Projections.distinct(coluna2));
		
//		Joins da consulta
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), "VPA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VPA." + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTA", JoinType.LEFT_OUTER_JOIN);
		
//		Condições
		criteria.add(Restrictions.eq("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.R));
		criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), tipoSessao));
		if(periodoFinal != null){
			criteria.add(Restrictions.ge("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString(), periodoFinal));			
			criteria.add(Restrictions.ge("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString(), periodoFinal));
		}
		if(codigo != 0){
			criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.PAC_CODIGO.toString(), codigo));			
		}
		
//		criteria.setResultTransformer(Transformers.aliasToBean(PacientesTratamentoSessaoVO.class));
		
		return Long.valueOf(new ArrayList<Object>(executeCriteria(criteria)).size());
	}

	public Long pesquisarAgendamento(Integer lote, Integer consulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
		criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE");
		if(consulta == 6){
			criteria.add(Restrictions.eq("SES." + MptSessao.Fields.IND_SITUACAOSESSAO.toString(), DominioSituacaoSessao.SAG));			
		}
		if(consulta == 7){
			criteria.add(Restrictions.eq("SES." + MptSessao.Fields.IND_SITUACAOSESSAO.toString(), DominioSituacaoSessao.SAC));			
		}
		if(consulta == 8){
			criteria.add(Restrictions.eq("SES." + MptSessao.Fields.IND_SITUACAOSESSAO.toString(), DominioSituacaoSessao.SSU));			
		}
		if(consulta == 9){
			criteria.add(Restrictions.eq("SES." + MptSessao.Fields.IND_SITUACAOSESSAO.toString(), DominioSituacaoSessao.SFA));			
		}
		criteria.add(Restrictions.eq("PTE." + MptPrescricaoPaciente.Fields.LOTE.toString(), lote));
		return executeCriteriaCount(criteria);
	}

	public Long unionListasCount(MptTipoSessao tipoSessao, Date periodoInicial,
			Date periodoFinal, Boolean checkAberto, Boolean checkFechado,
			Boolean checkPrimeiro, Boolean checkSessao, Integer codigoPaciente,
			Integer tipoFiltroPersonalizado, Integer parametro) {
		
		Long contagem = (long) 0;
		if(tipoFiltroPersonalizado != 4){
			contagem = pesquisarPacientesTratamentoSessaoCount(tipoSessao, periodoInicial, periodoFinal, checkAberto, checkFechado, checkPrimeiro, checkSessao, codigoPaciente, tipoFiltroPersonalizado, parametro);			
		}
		if(tipoFiltroPersonalizado == 1 || tipoFiltroPersonalizado == 4){
			contagem += pesquisarHorarioSessaoCount(periodoFinal, codigoPaciente, tipoSessao);			
		}
		 
		return contagem;
	}
	
	private String primeiraConsulta(Integer diasPrimeiraConsulta){
		
		Date dataFinal = new Date();
		Date dataInicial = DateUtils.addDays(dataFinal, -diasPrimeiraConsulta.intValue());

        StringBuilder countPrimeiraConsulta = new StringBuilder(500);
        countPrimeiraConsulta.append(" (SELECT COUNT(*) FROM AGH.MPT_SESSAO CSE ");
        countPrimeiraConsulta.append("    INNER JOIN AGH.MPT_HORARIO_SESSAO CHS ON CHS.SES_SEQ = CSE.SEQ ");
        countPrimeiraConsulta.append("    INNER JOIN AGH.MPT_AGENDAMENTO_SESSAO CAG ON CAG.SEQ = CHS.AGS_SEQ ");
        countPrimeiraConsulta.append("    WHERE CAG.PAC_CODIGO = PAC3_.CODIGO ");

        countPrimeiraConsulta.append("    AND TO_CHAR(CHS.DATA_INICIO, 'YYYYMMDD') BETWEEN "+"'"+ br.gov.mec.aghu.core.utils.DateUtil.obterDataFormatada(dataInicial, "YYYYMMdd") +"'"+" AND  "+"'"+ br.gov.mec.aghu.core.utils.DateUtil.obterDataFormatada(dataFinal, "YYYYMMdd") +"'"+") ");

		return countPrimeiraConsulta.toString();
	}

	public Long removerItensRN07(Integer lote, Integer cloSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSessao.class, "SES");
		criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE");
		criteria.createAlias("SES." + MptSessao.Fields.HORARIO_SESSAO.toString(), "HRS", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.or(Restrictions.eq("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.C), 
				Restrictions.isNull("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString())));
		criteria.add(Restrictions.eq("PTE." + MptPrescricaoPaciente.Fields.LOTE.toString(), lote));
		criteria.add(Restrictions.eq("SES." + MptSessao.Fields.CICLO.toString(), cloSeq));
		return executeCriteriaCount(criteria);
	}
}
