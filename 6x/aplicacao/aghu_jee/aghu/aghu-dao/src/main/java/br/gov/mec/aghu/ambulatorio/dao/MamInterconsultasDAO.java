package br.gov.mec.aghu.ambulatorio.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.ambulatorio.vo.RelatorioPacientesInterconsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatoriosInterconsultasVO;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioAvaliacaoInterconsulta;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOrdenacaoInterconsultas;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultas;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultasPesquisa;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;

public class MamInterconsultasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamInterconsultas> {

//	private static final String ALIAS_CON = "CON.";
	private static final long serialVersionUID = -6191071072396371321L;
	private static final String IEO_PONTO = "IEO.";
	private static final String ESP_PONTO = "ESP.";
	private static final String PAC_PONTO = "PAC.";
	private static final String CCT_PONTO = "CCT.";
	private static final String CON_PONTO = "CON.";
	private static final String GRD_PONTO = "GRD.";
	private static final String UNF_PONTO = "UNF.";

	public List<MamInterconsultas> listarInterconsultasPorNumero(
			Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamInterconsultas.class, "MAM");
		
		criteria.add(Restrictions.eq("MAM."+MamInterconsultas.Fields.SITUACAO.toString(), DominioSituacaoInterconsultasPesquisa.P ));
		criteria.add(Restrictions.eq("MAM."+MamInterconsultas.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V ));
		criteria.add(Restrictions.isNull("MAM."+MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
		
		
		DetachedCriteria criteriaSubquery = DetachedCriteria
				.forClass(AacConsultas.class, "CON");
		criteriaSubquery.createAlias("CON."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GAC");
		criteriaSubquery.add(Restrictions.eq("CON."+AacConsultas.Fields.NUMERO.toString(), consultaNumero ));
		criteriaSubquery.add(Restrictions.eqProperty("MAM."+MamInterconsultas.Fields.ESP_SEQ_ADM.toString(), "GAC."+AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()));
		criteriaSubquery.add(Restrictions.eqProperty("MAM."+MamInterconsultas.Fields.PACIENTE_CODIGO.toString(),"CON."+AacConsultas.Fields.PAC_CODIGO.toString()));
		
		criteriaSubquery.setProjection(Projections.projectionList().add(Projections.property(AacConsultas.Fields.NUMERO.toString()), "numero"));
		
		criteria.add(Subqueries.exists(criteriaSubquery));
		
		
		/*PROCEDURE AACK_CON_3_RN.RN_CONP_ATU_MAM_EN
		 (P_CON_NUMERO IN NUMBER
		 )
		 IS
		CURSOR c_busca_mam (c_con_numero aac_consultas.numero%type) IS
		SELECT int.seq
		FROM	mam_interconsultas int,
			aac_grade_agendamen_consultas grd,
			aac_consultas con
		WHERE con.numero = c_con_numero
		AND grd.seq = con.grd_seq
		AND int.esp_seq_ADM = grd.esp_seq
		AND int.ind_pendente = 'V'
		AND SUBSTR(int.situacao,1,1) = 'P'
		AND int.dthr_valida_mvto IS NULL  -- Milena 01/07/2009
		AND int.pac_codigo = con.pac_codigo;
		r_busca_mam  c_busca_mam%rowtype;
		--
		BEGIN
			FOR r_mam IN c_busca_mam(p_con_numero)
			LOOP
				UPDATE mam_interconsultas
		        	SET con_numero_marcada = p_con_numero,
				    situacao = 'M'
		     		WHERE  seq  = r_mam.seq;
			END LOOP;

		END;*/

		return  this.executeCriteria(criteria);
	}
	
	public MamInterconsultas obterInterconsultaPorEspecialidadePaciente(
			Short espSeq, Integer pacCodigo, Boolean validaData) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamInterconsultas.class);
		
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.ESPECIALIDADE_ADM
				.toString()+"."+AghEspecialidades.Fields.SEQ.toString(), espSeq ));
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.SITUACAO
				.toString(), DominioSituacaoInterconsultas.P ));
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.PENDENTE
				.toString(), DominioIndPendenteAmbulatorio.V ));	
		
		if (validaData){
			criteria.add(Restrictions.isNull(MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
			criteria.add(Restrictions.eq(
					MamInterconsultas.Fields.PACIENTE.toString() + "."
							+ AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		}
		else{
			criteria.add(Restrictions.ne(
					MamInterconsultas.Fields.PACIENTE.toString() + "."
							+ AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		}
		
		List <MamInterconsultas> lista = executeCriteria(criteria);
		if (!lista.isEmpty()){
			return lista.get(0);
		}

		return  null;
	}
	
	public List<MamInterconsultas> obterInterconsultaPorConsultaSituacao(
			AacConsultas consulta) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamInterconsultas.class);
		
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.CONSULTA.toString(), consulta ));
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V ));
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.IND_DIGITADO.toString(), false ));
		criteria.add(Restrictions.isNull(MamInterconsultas.Fields.DTHR_VALIDA.toString()));
		criteria.add(Restrictions.in(MamInterconsultas.Fields.SITUACAO.toString(), new Object[]{DominioSituacaoInterconsultas.M, DominioSituacaoInterconsultas.A}));

		return this.executeCriteria(criteria);
	}

	/**
	 * Realiza uma busca por Interconsultas a partir da Consulta marcada informada.
	 * 
	 * @param numeroConsulta - Número da Consulta marcada
	 * @return Lista de Interconsultas relacionadas
	 */
	public List<MamInterconsultas> obterInterconsultaPorConsultaMarcada(Integer numeroConsulta) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class, "MIN");

		criteria.createAlias("MIN." + MamInterconsultas.Fields.CONSULTA_MARCADA.toString(), "CON");
		
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		
		return this.executeCriteria(criteria);
	}

	public List<MamInterconsultas> obterInterconsultasPeloNumeroConsultaPendente(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamInterconsultas.class);
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.isNull(MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
		
		criteria.add(Restrictions.in(MamInterconsultas.Fields.PENDENTE.toString(),new Object[]{DominioIndPendenteAmbulatorio.V, DominioIndPendenteAmbulatorio.P}));
		
		return executeCriteria(criteria);
	}

	/**
	 * Realiza uma busca por Interconsultas a partir do Paciente informado.
	 * 
	 * @param codigoPaciente - Código do Paciente
	 * @return Lista de Interconsultas relacionadas
	 */
	public List<MamInterconsultas> pesquisaInterconsultaPendentePorPaciente(Integer codigoPaciente) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class, "MIN");
		
		criteria.createAlias("MIN." + MamInterconsultas.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq("MIN." + MamInterconsultas.Fields.SITUACAO.toString(), DominioSituacaoInterconsultas.P));
		
		return this.executeCriteria(criteria);
	}

	public void atualizarInterconsultas(final Integer pacCodigo,
			final Character tipo, final String usuarioLogado) {
		
		if (!this.isOracle()) {
			return;
		}
		
		
		AghWork work = new AghWork(usuarioLogado) {
			public void executeAghProcedure(Connection connection) throws SQLException {
				
				CallableStatement cs = null;
				try {
					StringBuilder sbCall = new StringBuilder("{call ");
					sbCall.append(EsquemasOracleEnum.AGH.toString());
					sbCall.append('.');
					sbCall.append(ObjetosBancoOracleEnum.MAMP_ATU_IEO_OBITO
							.toString());
					sbCall.append("(?,?)}");

					cs = connection.prepareCall(sbCall.toString());

					cs.setString(1, pacCodigo.toString());
					cs.setString(2, tipo.toString());

					cs.execute();
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};
		
		
		this.doWork(work);
		
		if (work.getException() != null){
			throw new HibernateException(work.getException());
		}
	}
	
	public List<MamInterconsultas> pesquisarConsultoriasAmbulatoriais(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class); 
		
		criteria.createAlias(MamInterconsultas.Fields.ESPECIALIDADE_AGENDA.toString(), "espagenda", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MamInterconsultas.Fields.ESPECIALIDADE.toString(), "espec", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MamInterconsultas.Fields.SERVIDOR_VALIDA.toString(), "SV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente)); 
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V)); 
		criteria.add(Restrictions.isNull(MamInterconsultas.Fields.DTHR_MOVIMENTO.toString())); 
		criteria.addOrder(Order.desc(MamInterconsultas.Fields.DTHR_CRIACAO.toString())); 
		return executeCriteria(criteria);
	}
	
	public MamInterconsultas obterDadosRelConsultoriasAmbulatoriais(Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class); 
	
		criteria.createAlias(MamInterconsultas.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias(MamInterconsultas.Fields.ESPECIALIDADE.toString(), "ESP1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MamInterconsultas.Fields.ESPECIALIDADE_AGENDA.toString(), "ESP2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MamInterconsultas.Fields.VMAM_PESSOAS_SERVIDORES.toString(), "SV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MamInterconsultas.Fields.EQUIPE.toString(), "EQP", JoinType.LEFT_OUTER_JOIN);		
	
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.SEQ.toString(), seq));
		return (MamInterconsultas) executeCriteriaUniqueResult(criteria);
	}	
	
	
	public List<MamInterconsultas> listaInterconsultas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MamInterconsultas mamInterconsultas,
			Date dataInicial, Date dataFinal, boolean consultoria, boolean excluidos) {
		DetachedCriteria criteria = listaInterconsultas(mamInterconsultas, orderProperty, true, dataInicial, dataFinal, consultoria, excluidos);
		
		return executeCriteria(criteria, firstResult, maxResult, null, asc);
	}
	
	public Long listaInterconsultasCount(MamInterconsultas mamInterconsultas, Date dataInicial, Date dataFinal, boolean consultoria, boolean excluidos) {
		DetachedCriteria criteria = listaInterconsultas(mamInterconsultas, "", false, dataInicial, dataFinal, consultoria, excluidos);
		return executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria listaInterconsultas(MamInterconsultas mamInterconsultas, String orderProperty, boolean ordenar,
			Date dataInicial, Date dataFinal, boolean consultoria, boolean excluidos) {
						
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class, "INT");
			

		criteria.createAlias("INT." + MamInterconsultas.Fields.PACIENTE.toString(), "APAC", JoinType.INNER_JOIN);
		criteria.createAlias("INT." + MamInterconsultas.Fields.ESPECIALIDADE_ADM.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias("INT." + MamInterconsultas.Fields.EQUIPE.toString(), "EQU", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT." + MamInterconsultas.Fields.SERVIDOR_RESPONSAVEL.toString(), "SER_RES", JoinType.LEFT_OUTER_JOIN); 
		criteria.createAlias("SER_RES." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES_RES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT." + MamInterconsultas.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT." + MamInterconsultas.Fields.SERVIDOR_MARCADA.toString(), "SER_MAR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_MAR." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES_MAR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT." + MamInterconsultas.Fields.SERVIDOR_AVISADA.toString(), "SER_AVI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_AVI." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES_AVI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT." + MamInterconsultas.Fields.CONSULTA_MARCADA.toString(), "CON_MAR", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("CON_MAR." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRA_MAR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRA_MAR." + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNI_MAR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRA_MAR." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQU_MAR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRA_MAR." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE, "ESP_MAR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRA_MAR." + AacGradeAgendamenConsultas.Fields.PROF_SERVIDOR, "SER_MAR_GRA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_MAR_GRA." + RapServidores.Fields.PESSOA_FISICA, "PES_MAR_GRA", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("INT." + MamInterconsultas.Fields.CONSULTA_RETORNO.toString(), "CON_RET", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("CON_RET." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRA_RET", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRA_RET." + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNI_RET", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRA_RET." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQU_RET", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRA_RET." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP_RET", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRA_RET." + AacGradeAgendamenConsultas.Fields.PROF_SERVIDOR.toString(), "SER_RET", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_RET." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES_RET", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.or(Restrictions.isNull("SER_RES." + RapServidores.Fields.DATA_FIM_VINCULO), Restrictions.gt("SER_RES." + RapServidores.Fields.DATA_FIM_VINCULO, new Date())));
		criteria.add(Restrictions.or(Restrictions.isNull("CON_MAR." + AacConsultas.Fields.PACIENTE + "." + AipPacientes.Fields.CODIGO.toString()), 
				Restrictions.eqProperty("CON_MAR." + AacConsultas.Fields.PACIENTE + "." + AipPacientes.Fields.CODIGO.toString(), 
						"APAC." + AipPacientes.Fields.CODIGO)));
		criteria.add(Restrictions.or(Restrictions.isNull("CON_RET." + AacConsultas.Fields.PACIENTE + "." + AipPacientes.Fields.CODIGO.toString()), 
				Restrictions.eqProperty("CON_RET." + AacConsultas.Fields.PACIENTE + "." + AipPacientes.Fields.CODIGO.toString(), 
						"APAC." + AipPacientes.Fields.CODIGO)));

		
		 /* SITUAÇÃO ----------------------------------------------------------------------------*/					
		montarFiltroSituacao(mamInterconsultas, criteria);
		
		
		/* AVISO CONSULTORIA ----------------------------------------------------------------------------*/
		montarFiltroConsultoria(consultoria, criteria);
		
		
		 /* EXCLUÍDOS ----------------------------------------------------------------------------*/ 
 		montarFiltroExcluido(excluidos, criteria);
		
		
		/* DATA INICIAL x DATA FINAL ----------------------------------------------------------------------------*/ 
		montarFiltroDatas(mamInterconsultas, dataInicial, dataFinal, criteria);
		

		/*FILTROS LIVRES */
		montarFiltroLivre(mamInterconsultas, criteria);
		
		
		 /* ORDENAÇÃO ----------------------------------------------------------------------------*/
		montarFiltroOrdenacao(orderProperty, ordenar, criteria);
		
		/* ORDENAÇÃO FIM----------------------------------------------------------------------------*/
		
		return criteria;
	}

	private void montarFiltroOrdenacao(String orderProperty, boolean ordenar,
			DetachedCriteria criteria) {
		if (ordenar){			
			if (orderProperty.equals(DominioOrdenacaoInterconsultas.D.toString())) {
				criteria.addOrder(Order.asc(MamInterconsultas.Fields.DTHR_CRIACAO.toString()));
				criteria.addOrder(Order.asc(MamInterconsultas.Fields.ESPECIALIDADE.toString()));
			}
			
			if (orderProperty.equals(DominioOrdenacaoInterconsultas.P.toString())) {
				criteria.addOrder(Order.asc("APAC." + AipPacientes.Fields.NOME));
			}
			
			if (orderProperty.equals(DominioOrdenacaoInterconsultas.T.toString())) {
				criteria.addOrder(Order.asc("APAC." + AipPacientes.Fields.PRONTUARIO));
			}
				
			if (orderProperty.equals(DominioOrdenacaoInterconsultas.E.toString())) {
				
				criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.SIGLA));
			}
			
			if (orderProperty.equals(DominioOrdenacaoInterconsultas.C.toString())) {
				criteria.addOrder(Order.asc(MamInterconsultas.Fields.CONSULTA_MARCADA_NUMERO.toString()));
			}
			
			if (orderProperty.equals("S")) {
				criteria.addOrder(Order.asc("UNI_MAR." + AghUnidadesFuncionais.Fields.SIGLA));
			}
		}
	}

	private void montarFiltroLivre(MamInterconsultas mamInterconsultas,
			DetachedCriteria criteria) {
		
		//-- Caso informado valor diferente de "Todas" no campo "Prioridade"
		if (mamInterconsultas.getPrioridadeAprovada()  != null) {
			criteria.add(Restrictions.eq("INT." + MamInterconsultas.Fields.PRIORIDADE_APROVADA.toString(), mamInterconsultas.getPrioridadeAprovada()));			
		}
		
		// -- Caso informado valor no campo "Agenda"
		if (mamInterconsultas.getEspecialidade() != null && mamInterconsultas.getEspecialidade().getSigla() != null){
			criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.SIGLA.toString(), mamInterconsultas.getEspecialidade().getSigla()));			
		}
		
		//-- Caso informado valor no campo "Prontuário" 
		if (mamInterconsultas.getPaciente() != null && mamInterconsultas.getPaciente().getProntuario() != null){
			criteria.add(Restrictions.eq("APAC." + AipPacientes.Fields.PRONTUARIO.toString(), mamInterconsultas.getPaciente().getProntuario()));			
		}
		
		// -- Caso informado valor no campo "Interconsulta"
		if (mamInterconsultas.getConsultaMarcada() != null && mamInterconsultas.getConsultaMarcada().getNumero() != null){
			criteria.add(Restrictions.eq("INT." + MamInterconsultas.Fields.CONSULTA_MARCADA_NUMERO.toString(), mamInterconsultas.getConsultaMarcada().getNumero()));			
		}
		
		//-- Caso informado valor no campo "Consulta Retorno" 
		if (mamInterconsultas.getConsultaRetorno() != null && mamInterconsultas.getConsultaRetorno().getNumero() != null){
			criteria.add(Restrictions.eq("INT." + MamInterconsultas.Fields.CONSULTA_RETORNO_NUMERO.toString(), mamInterconsultas.getConsultaRetorno().getNumero()));			
		}
		
		//-- Caso informado valor no campo "Setor"
		if ((mamInterconsultas.getConsulta().getGradeAgendamenConsulta().getUnidadeFuncional() != null 
				&& mamInterconsultas.getConsulta().getGradeAgendamenConsulta().getUnidadeFuncional().getSigla() != null)) {
			criteria.add(Restrictions.or(Restrictions.ilike("UNI_MAR." + AghUnidadesFuncionais.Fields.SIGLA.toString(), mamInterconsultas.getConsulta().getGradeAgendamenConsulta().getUnidadeFuncional().getSigla(), MatchMode.ANYWHERE), 
					Restrictions.ilike("UNI_RET." + AghUnidadesFuncionais.Fields.SIGLA.toString(), mamInterconsultas.getConsulta().getGradeAgendamenConsulta().getUnidadeFuncional().getSigla(), MatchMode.ANYWHERE)));
		}
	}

	private void montarFiltroDatas(MamInterconsultas mamInterconsultas,
			Date dataInicial, Date dataFinal, DetachedCriteria criteria) {
		
		//-- Caso NÃO informado 'M' no campo "Situação" e informado ambos os campos: "Data Inicial" e "Data Final"
		if ((mamInterconsultas.getSituacao() == null || (mamInterconsultas.getSituacao() != null && 
				!mamInterconsultas.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.M))) 
				&& dataInicial != null && dataFinal != null){
			criteria.add(Restrictions.between("INT." + MamInterconsultas.Fields.DTHR_CRIACAO.toString(), dataInicial, dataFinal));			
		}
		
		//-- Caso NÃO informado 'M' no campo "Situação" e informado apenas o campo: "Data Inicial"
		if ((mamInterconsultas.getSituacao() == null || (mamInterconsultas.getSituacao() != null &&
				!mamInterconsultas.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.M)))
				&& dataInicial != null && dataFinal == null){
			criteria.add(Restrictions.between("INT." + MamInterconsultas.Fields.DTHR_CRIACAO.toString(), dataInicial, new Date()));			
		}		
		
	}

	private void montarFiltroExcluido(boolean excluidos,
			DetachedCriteria criteria) {

		// -- Caso selecionado o campo "Visualizar Apenas Excluídos" 
		if (excluidos){
			criteria.add(Restrictions.eq("INT." + MamInterconsultas.Fields.PENDENTE.toString(), DominioSituacaoInterconsultas.C));			
		}
		
		//-- Caso NÃO selecionado o campo "Visualizar Apenas Excluídos"
		if (!excluidos){
			criteria.add(Restrictions.eq("INT." + MamInterconsultas.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
			criteria.add(Restrictions.isNull("INT." + MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));			
		}
	}

	private void montarFiltroConsultoria(boolean consultoria,
			DetachedCriteria criteria) {
		//-- Caso selecionado o campo "Aviso Consultoria"
		if (consultoria){
			
			LogicalExpression primeiro = Restrictions.and(Restrictions.gt("INT." + MamInterconsultas.Fields.CONSULTA_RETORNO_NUMERO.toString(), 0), 
					Restrictions.isNull("INT." + MamInterconsultas.Fields.DTHR_AVISA_RETORNO.toString()));
			
			Criterion segundo = Restrictions.and(Restrictions.eq("INT." + MamInterconsultas.Fields.SITUACAO.toString(), DominioSituacaoInterconsultas.M),
					Restrictions.eq("INT." + MamInterconsultas.Fields.AVALIACAO.toString(), DominioAvaliacaoInterconsulta.M));  
			
			LogicalExpression ordenarConsultoria = Restrictions.or(primeiro,segundo);
			
			criteria.add(ordenarConsultoria);			
		}
	}

	private void montarFiltroSituacao(MamInterconsultas mamInterconsultas,
			DetachedCriteria criteria) {
		// -- Caso selecionado "Pendente" (P) no campo "Situação" 
		if (mamInterconsultas.getSituacao() != null && mamInterconsultas.getSituacao() == DominioSituacaoInterconsultasPesquisa.P){
			criteria.add(Restrictions.eq("INT." + MamInterconsultas.Fields.SITUACAO.toString(), DominioSituacaoInterconsultasPesquisa.P));
			criteria.add(Restrictions.eq("INT." + MamInterconsultas.Fields.AVALIACAO.toString(), DominioAvaliacaoInterconsulta.L));			
		}
		
		//  -- Caso selecionado "Marcada" (M) no campo "Situação"
		if (mamInterconsultas.getSituacao() != null && mamInterconsultas.getSituacao() == DominioSituacaoInterconsultasPesquisa.M){
			DominioSituacaoInterconsultasPesquisa [] marcada = {DominioSituacaoInterconsultasPesquisa.M, DominioSituacaoInterconsultasPesquisa.O}; 
			criteria.add(Restrictions.in("INT." + MamInterconsultas.Fields.SITUACAO.toString(), marcada));
			criteria.add(Restrictions.isNull("INT." + MamInterconsultas.Fields.DTHR_AVISA_RETORNO.toString()));			
		}
		
		//  -- Caso selecionado "Avisada" (A) no campo "Situação"
		if (mamInterconsultas.getSituacao() != null && mamInterconsultas.getSituacao() == DominioSituacaoInterconsultasPesquisa.A){
			criteria.add(Restrictions.or(Restrictions.eq("INT." + MamInterconsultas.Fields.SITUACAO.toString(), DominioSituacaoInterconsultasPesquisa.A),
					Restrictions.isNotNull("INT." + MamInterconsultas.Fields.DTHR_AVISA_RETORNO.toString())));			
		}
		
		//  -- Caso selecionado "Não Avaliada" (N) no campo "Situação" 
		if (mamInterconsultas.getSituacao() != null && mamInterconsultas.getSituacao() == DominioSituacaoInterconsultasPesquisa.N){
			criteria.add(Restrictions.eq("INT." + MamInterconsultas.Fields.AVALIACAO.toString(), DominioAvaliacaoInterconsulta.N));			
		}
	}		
	
	
	//Querys da estória 40299------------------------------------------------------------------------------------------
	
	/**
	 * 
	 Estoria:40299 Procedure: c1
	 */
	public boolean verificarExistenciaInterconsultaPorNumeroEspecialidadeSequencia(Integer conNumero,Short espSeq,Long seq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class);
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.CONSULTA.toString()+"."+AacConsultas.Fields.NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));		
		criteria.add(Restrictions.ne(MamInterconsultas.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.C ));
		
		criteria.add(Restrictions.ne(MamInterconsultas.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.isNull(MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
		
		if(!executeCriteria(criteria).isEmpty()){
			return true;
		}
		
		return false;
		
	}
	
	
	
	/**
	 * 
	 * @param pac_codigo
	 * @param esp_seq_adm
	 * @param seq
	 * @param numero_marcada
	 * @return
	 * Estoria: 40229 consulta C4
	 */
	public boolean verificaExistenciaInterconsultaPorPacienteEspecialidadeNumeroValido(
			Integer pac_codigo,Short esp_seq_adm,Long seq,Integer numero_marcada){
		
			Date current_date = new Date();//receber como parâmetro
		
		//subQuery
		DetachedCriteria subQuery = DetachedCriteria.forClass(AacConsultas.class, "CON");		
		subQuery.add(Restrictions.gt("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(),current_date));
		subQuery.setProjection(Projections.property("CON." + AacConsultas.Fields.NUMERO.toString()));
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class, "IEO");

		criteria.add(Restrictions.eq(MamInterconsultas.Fields.PACIENTE_CODIGO.toString(), pac_codigo));
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.ESPECIALIDADE_ADM_CODIGO.toString(), esp_seq_adm.intValue()));
		criteria.add(Restrictions.ne(MamInterconsultas.Fields.SEQ.toString(),seq));
		criteria.add(Restrictions.ne(MamInterconsultas.Fields.PENDENTE.
				toString(),DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.ne(MamInterconsultas.Fields.SITUACAO
				.toString(), DominioSituacaoInterconsultas.O ));
		
		criteria.add(Restrictions.isNull(MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
				
		subQuery.add(Restrictions.eqProperty("CON." + AacConsultas.Fields.NUMERO.toString(), "IEO." + MamInterconsultas.Fields.CONSULTA_MARCADA_NUMERO.toString()));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MamInterconsultas.Fields.CONSULTA_MARCADA_NUMERO.toString()), Subqueries.exists(subQuery)));
	
		if(!executeCriteria(criteria).isEmpty()){
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * 
	 * @param con_numero
	 * @param esp_seq_adm
	 * @param seq
	 * @return
	 * /**
	 * 
	 Estoria:40299 Procedure: c3
	 */
	 
	public boolean verificaExistenciaInterconsultaPorNumeroEspecialidadeAdmSeq(Integer con_numero,Short esp_seq_adm,Long seq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class);
		
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.CONSULTA.toString()+"."+AacConsultas.Fields.NUMERO,con_numero));
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.ESP_SEQ_ADM.toString(), esp_seq_adm));
		criteria.add(Restrictions.ne(MamInterconsultas.Fields.SEQ.toString(),seq));
		
		//havia erro cofigido que foi corrigido
		criteria.add(Restrictions.ne(MamInterconsultas.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.C ));
		
		criteria.add(Restrictions.isNull(MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
		
		if(!executeCriteria(criteria).isEmpty()){
			return true;		
		}		
		return false;	
	}		
	public List<MamInterconsultas> pesquisarDocumentoImpressoInterconsultaSituacao(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class);
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property(MamInterconsultas.Fields.SEQ.toString()))
					.add(Projections.property(MamInterconsultas.Fields.PENDENTE.toString()))
					.add(Projections.property(MamInterconsultas.Fields.IND_IMPRESSO.toString()))
					);
			criteria.add(Restrictions.eq(MamInterconsultas.Fields.CON_NUMERO.toString(), numeroConsulta));
			criteria.add(Restrictions.isNull(MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
			criteria.add(Restrictions.in(MamInterconsultas.Fields.PENDENTE.toString(), new DominioIndPendenteAmbulatorio[] {DominioIndPendenteAmbulatorio.V,DominioIndPendenteAmbulatorio.P}));
			criteria.setResultTransformer(Transformers.aliasToBean(MamInterconsultas.class));	
		
		return  executeCriteria(criteria);
}
	@Override
	public MamInterconsultas obterPorChavePrimaria(Object pk) {
		MamInterconsultas mamInterconsultas = super.obterPorChavePrimaria(pk);
		return mamInterconsultas;
	}
	
	/** 40312 Consulta Serviços	 */
	public List<RelatoriosInterconsultasVO> carregarRelatorioPacientesInterconsultas(Date dataInicial, Date dataFinal, AghEspecialidades agenda) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class, "IEO");
		
		criteria.createAlias(IEO_PONTO + MamInterconsultas.Fields.ESPECIALIDADE_ADM.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias(IEO_PONTO + MamInterconsultas.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias(ESP_PONTO + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "CCT", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(CCT_PONTO + FccCentroCustos.Fields.CODIGO.toString()), RelatoriosInterconsultasVO.Fields.CCT_CODIGO.toString());
        projList.add(Projections.property(CCT_PONTO + FccCentroCustos.Fields.DESCRICAO.toString()), RelatoriosInterconsultasVO.Fields.CCT_DESCRICAO.toString());
        projList.add(Projections.property(ESP_PONTO + AghEspecialidades.Fields.SIGLA.toString()), RelatoriosInterconsultasVO.Fields.ESP_SIGLA.toString());
        projList.add(Projections.property(ESP_PONTO + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), RelatoriosInterconsultasVO.Fields.ESP_NOME_ESPECIALIDADE.toString());
        criteria.setProjection(Projections.distinct(projList));
        
        if (dataInicial != null && dataFinal != null) {
        	criteria.add(Restrictions.between(IEO_PONTO + MamInterconsultas.Fields.DTHR_VALIDA.toString(), dataInicial, dataFinal));
        }
        else{
        	if (dataInicial != null && dataFinal == null) {
        		criteria.add(Restrictions.between(IEO_PONTO + MamInterconsultas.Fields.DTHR_VALIDA.toString(), dataInicial, new Date()));
        	}
		}
        if (agenda != null && agenda.getSigla() != null) {
        	criteria.add(Restrictions.eq(ESP_PONTO + AghEspecialidades.Fields.SIGLA.toString(), agenda.getSigla()));
        }
        
        criteria.add(Restrictions.eq(IEO_PONTO + MamInterconsultas.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
        criteria.add(Restrictions.isNull(IEO_PONTO + MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
        criteria.add(Restrictions.eq(IEO_PONTO + MamInterconsultas.Fields.SITUACAO.toString(), DominioSituacaoInterconsultasPesquisa.P));
        criteria.add(Restrictions.isNotNull(IEO_PONTO + MamInterconsultas.Fields.ESPECIALIDADE_ADM_CODIGO.toString()));
        criteria.add(Restrictions.isNull(IEO_PONTO + MamInterconsultas.Fields.CONSULTA_MARCADA_NUMERO.toString()));
        
        criteria.addOrder(Order.asc(CCT_PONTO + FccCentroCustos.Fields.DESCRICAO.toString()));
        criteria.addOrder(Order.asc(ESP_PONTO + AghEspecialidades.Fields.SIGLA.toString()));
        criteria.addOrder(Order.asc(ESP_PONTO + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
        
        criteria.setResultTransformer(Transformers.aliasToBean(RelatoriosInterconsultasVO.class));
        List<RelatoriosInterconsultasVO> listaServico = executeCriteria(criteria);
        
        if (listaServico != null && !listaServico.isEmpty()) {
        	for (RelatoriosInterconsultasVO servico : listaServico) {
            	if (servico != null && servico.getCctcodigo() != null) {
            		servico.setListapacientes(consultarRelatorioPacientesInterconsultas(dataInicial, dataFinal, agenda, servico.getCctcodigo()));
    			}
    		}
		}
		return listaServico;
	}
	
	/** 40132 Consulta Pacientes com interconsultas */
	public List<RelatorioPacientesInterconsultasVO> consultarRelatorioPacientesInterconsultas(Date dataInicial, Date dataFinal, AghEspecialidades agenda, Integer codigoCentroCusto){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class, "IEO");
		
		criteria.createAlias(IEO_PONTO + MamInterconsultas.Fields.ESPECIALIDADE_ADM.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias(IEO_PONTO + MamInterconsultas.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias(ESP_PONTO + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "CCT", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.sqlProjection("trunc(this_.DTHR_CRIACAO) ieodthrcricao", new String [] {RelatoriosInterconsultasVO.Fields.IEO_DTHRCRICAO.toString()}, new Type[] {new DateType()}));
        projList.add(Projections.property(PAC_PONTO + AipPacientes.Fields.NOME.toString()), RelatorioPacientesInterconsultasVO.Fields.PAC_NOME.toString());
        projList.add(Projections.property(PAC_PONTO + AipPacientes.Fields.PRONTUARIO.toString()), RelatorioPacientesInterconsultasVO.Fields.PAC_PRONTUARIO.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.OBSERVACAO.toString()), RelatorioPacientesInterconsultasVO.Fields.IEO_OBSERVACAO.toString());
        criteria.setProjection(Projections.distinct(projList));
        
        if (dataInicial != null && dataFinal != null) {
        	criteria.add(Restrictions.between(IEO_PONTO + MamInterconsultas.Fields.DTHR_VALIDA.toString(), dataInicial, dataFinal));
		}
        else{
        	if (dataInicial != null && dataFinal == null) {
        		criteria.add(Restrictions.between(IEO_PONTO + MamInterconsultas.Fields.DTHR_VALIDA.toString(), dataInicial, new Date()));
        	}
		}
        if (agenda != null && agenda.getSigla() != null) {
        	criteria.add(Restrictions.eq(ESP_PONTO + AghEspecialidades.Fields.SIGLA.toString(), agenda.getSigla()));
        }
        criteria.add(Restrictions.eq(CCT_PONTO + FccCentroCustos.Fields.CODIGO.toString(), codigoCentroCusto));
        criteria.add(Restrictions.eq(IEO_PONTO + MamInterconsultas.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
        criteria.add(Restrictions.isNull(IEO_PONTO + MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
        criteria.add(Restrictions.eq(IEO_PONTO + MamInterconsultas.Fields.SITUACAO.toString(), DominioSituacaoInterconsultasPesquisa.P));
        criteria.add(Restrictions.isNotNull(IEO_PONTO + MamInterconsultas.Fields.ESPECIALIDADE_ADM_CODIGO.toString()));
        criteria.add(Restrictions.isNull(IEO_PONTO + MamInterconsultas.Fields.CONSULTA_MARCADA_NUMERO.toString()));
        
        criteria.addOrder(OrderBySql.sql(isOracle() ? "trunc(this_.DTHR_CRIACAO) asc" : "date_trunc('day', this_.DTHR_CRIACAO) asc"));
        criteria.addOrder(Order.asc(PAC_PONTO + AipPacientes.Fields.NOME.toString()));
        
        criteria.setResultTransformer(Transformers.aliasToBean(RelatorioPacientesInterconsultasVO.class));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @param situacaoFiltro
	 * @param ordenar
	 * @param agenda
	 * @param tipo
	 * @return
	 */
	public List<RelatoriosInterconsultasVO> carregarRelatorioInterconsultas(Date dataInicial, Date dataFinal, DominioSituacaoInterconsultasPesquisa situacaoFiltro, String ordenar, AghEspecialidades agenda) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class, "IEO");
		
		criteria.createAlias(IEO_PONTO + MamInterconsultas.Fields.ESPECIALIDADE_ADM.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias(IEO_PONTO + MamInterconsultas.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias(IEO_PONTO + MamInterconsultas.Fields.CONSULTA.toString(), "CON", JoinType.INNER_JOIN);
		criteria.createAlias(CON_PONTO + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD", JoinType.INNER_JOIN);
		criteria.createAlias(GRD_PONTO + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias(GRD_PONTO + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP", JoinType.INNER_JOIN);
		
        criteria.setProjection(projecaoRelatorioInterconsultas());
        
        if (dataInicial != null && dataFinal != null) {
        	criteria.add(Restrictions.between(IEO_PONTO + MamInterconsultas.Fields.DTHR_CRIACAO.toString(), dataInicial, dataFinal));
		}
        else{
        	if (dataInicial != null && dataFinal == null) {
        		criteria.add(Restrictions.between(IEO_PONTO + MamInterconsultas.Fields.DTHR_CRIACAO.toString(), dataInicial, new Date()));
        	}
		}
        
        if (situacaoFiltro != null && !situacaoFiltro.toString().equals("T")) {
        	criteria.add(Restrictions.eq(IEO_PONTO + MamInterconsultas.Fields.SITUACAO.toString(), situacaoFiltro));
		}
        
        if (agenda != null && agenda.getSigla() != null) {
        	criteria.add(Restrictions.eq(ESP_PONTO + AghEspecialidades.Fields.SIGLA.toString(), agenda.getSigla()));
        }
        criteria.add(Restrictions.ne(IEO_PONTO + MamInterconsultas.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.C));
        criteria.add(Restrictions.isNull(IEO_PONTO + MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
        
        ordenarRelatorioInterconsultas(ordenar, criteria);
        criteria.setResultTransformer(Transformers.aliasToBean(RelatoriosInterconsultasVO.class));
		return executeCriteria(criteria);
	}

	private void ordenarRelatorioInterconsultas(String ordenar,	final DetachedCriteria criteria) {
		if (ordenar != null) {
        	
        	if (ordenar.equals(DominioOrdenacaoInterconsultas.D.toString())) {
				criteria.addOrder(Order.asc(IEO_PONTO + MamInterconsultas.Fields.DTHR_CRIACAO.toString()));
				criteria.addOrder(Order.asc(IEO_PONTO + MamInterconsultas.Fields.ESPECIALIDADE.toString()));
			}
			if (ordenar.equals(DominioOrdenacaoInterconsultas.P.toString())) {
				criteria.addOrder(Order.asc(PAC_PONTO + AipPacientes.Fields.NOME));
			}
			if (ordenar.equals(DominioOrdenacaoInterconsultas.T.toString())) {
				criteria.addOrder(Order.asc(PAC_PONTO + AipPacientes.Fields.PRONTUARIO));
			}
			if (ordenar.equals(DominioOrdenacaoInterconsultas.E.toString())) {
				criteria.addOrder(Order.asc(ESP_PONTO + AghEspecialidades.Fields.SIGLA));
			}
			if (ordenar.equals(DominioOrdenacaoInterconsultas.C.toString())) {
				criteria.addOrder(Order.asc(IEO_PONTO + MamInterconsultas.Fields.CONSULTA_MARCADA_NUMERO.toString()));
			}
			if (ordenar.equals("S")) {
				criteria.addOrder(Order.asc(UNF_PONTO + AghUnidadesFuncionais.Fields.SIGLA));
			}
		}
	}
	
	/**
	 * 40132 Criar projeção da consulta de Relatorio Interconsultas
	 * @return
	 */
	private ProjectionList projecaoRelatorioInterconsultas(){
		
		String nomeEspecialidade = " (SELECT esp2.nome_especialidade FROM AGH.AGH_ESPECIALIDADES ESP2, AGH.V_MAM_CONSULTA_GRADES VCG WHERE VCG.ESP_SEQ = ESP2.SEQ and VCG.CON_NUMERO = this_.CON_NUMERO) as espnomeespecialidade ";
		StringBuilder marcacao = new StringBuilder(500);
		marcacao.append("(SELECT TO_CHAR(VCG.CON_DT_CONSULTA,'DD/MM/YYYY HH24:MI')||'  Cons='||VCG.CON_NUMERO||', Esp='||ESP2.SIGLA||', Zona='||VCG.ZONA||', Sala='||VCG.USL_SALA ");
		marcacao.append("FROM AGH.AGH_ESPECIALIDADES  ESP2, AGH.V_MAM_CONSULTA_GRADES VCG WHERE VCG.ESP_SEQ  = ESP2.SEQ and VCG.CON_NUMERO = this_.CON_NUMERO_MARCADA) as marcacao ");
		
		ProjectionList projList = Projections.projectionList();
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.SEQ.toString()), RelatoriosInterconsultasVO.Fields.IEO_SEQ.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.DTHR_CRIACAO.toString()), RelatoriosInterconsultasVO.Fields.IEO_DTHRCRICAO.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.PENDENTE.toString()), RelatoriosInterconsultasVO.Fields.IEO_PENDENTE.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.PACIENTE_CODIGO.toString()), RelatoriosInterconsultasVO.Fields.IEO_PACCODIGO.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.CON_NUMERO.toString()), RelatoriosInterconsultasVO.Fields.IEO_CONNUMERO.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.CONSULTA_MARCADA_NUMERO.toString()), RelatoriosInterconsultasVO.Fields.IEO_CONNUMEROMARCADA.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.ESPECIALIDADE_SEQ.toString()), RelatoriosInterconsultasVO.Fields.IEO_ESPSEQ.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.EQUIPE_SEQ.toString()), RelatoriosInterconsultasVO.Fields.IEO_EQPSEQ.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.SERVIDOR_RESPONSAVEL_MATRICULA.toString()), RelatoriosInterconsultasVO.Fields.IEO_SERMATRICULARESP.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.SERVIDOR_RESPONSAVEL_VINCODIGO.toString()), RelatoriosInterconsultasVO.Fields.IEO_VINCODIGORESP.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.SITUACAO.toString()), RelatoriosInterconsultasVO.Fields.IEO_SITUCAO.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.IND_URGENTE.toString()), RelatoriosInterconsultasVO.Fields.IEO_INDURGENTE.toString());
        projList.add(Projections.property(IEO_PONTO + MamInterconsultas.Fields.OBSERVACAO_ADICIONAL.toString()), RelatoriosInterconsultasVO.Fields.IEO_OBSERVACAO_ADICIONAL.toString());
        projList.add(Projections.property(PAC_PONTO + AipPacientes.Fields.CODIGO.toString()), RelatoriosInterconsultasVO.Fields.PAC_CODIGO.toString());
        projList.add(Projections.property(PAC_PONTO + AipPacientes.Fields.NOME.toString()), RelatoriosInterconsultasVO.Fields.PAC_NOME.toString());
        projList.add(Projections.property(PAC_PONTO + AipPacientes.Fields.PRONTUARIO.toString()), RelatoriosInterconsultasVO.Fields.PAC_PRONTUARIO.toString());
        projList.add(Projections.property(PAC_PONTO + AipPacientes.Fields.FONE_RESIDENCIAL.toString()), RelatoriosInterconsultasVO.Fields.PAC_FONERESIDENCIAL.toString());
        projList.add(Projections.property(PAC_PONTO + AipPacientes.Fields.DDD_FONE_RESIDENCIAL.toString()), RelatoriosInterconsultasVO.Fields.PAC_DDDFONERESIDENCIAL.toString());
        projList.add(Projections.property(ESP_PONTO + AghEspecialidades.Fields.SEQ.toString()), RelatoriosInterconsultasVO.Fields.ESP_SEQ.toString());
        projList.add(Projections.property(ESP_PONTO + AghEspecialidades.Fields.SIGLA.toString()), RelatoriosInterconsultasVO.Fields.ESP_SIGLA.toString());
        projList.add(Projections.sqlProjection(nomeEspecialidade, new String [] {RelatoriosInterconsultasVO.Fields.ESP_NOME_ESPECIALIDADE.toString()}, new Type[] {new StringType()}));
        projList.add(Projections.sqlProjection(marcacao.toString(), new String [] {RelatoriosInterconsultasVO.Fields.MARCACAO.toString()}, new Type[] {new StringType()}));
        return projList;
		
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamInterconsultas buscarInterconsultasPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class);

		criteria.add(Restrictions.eq(MamInterconsultas.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamInterconsultas> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public Long obterQtdInterconsulta(Short espSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class);
		criteria.createAlias(MamInterconsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("ESP."+ AghEspecialidades.Fields.SEQ, espSeq));
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.SITUACAO.toString(), DominioSituacaoInterconsultasPesquisa.P));
		criteria.add(Restrictions.eq(MamInterconsultas.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.isNull(MamInterconsultas.Fields.DTHR_MOVIMENTO.toString()));
		
		return executeCriteriaCount(criteria);
}
	

	public List<MamInterconsultas> obterInterconsulta(Short espSeq, Integer codPaciente){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamInterconsultas.class, "I");
		criteria.createAlias("I."+ MamInterconsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("I."+ MamInterconsultas.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("I."+ MamInterconsultas.Fields.CONSULTA.toString(), "CON", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("ESP."+ AghEspecialidades.Fields.SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq("PAC."+ AipPacientes.Fields.CODIGO.toString(), codPaciente));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("I."+ MamInterconsultas.Fields.SEQ.toString()).as(MamInterconsultas.Fields.SEQ.toString())));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MamInterconsultas.class));
		
		return executeCriteria(criteria);
	}

}
