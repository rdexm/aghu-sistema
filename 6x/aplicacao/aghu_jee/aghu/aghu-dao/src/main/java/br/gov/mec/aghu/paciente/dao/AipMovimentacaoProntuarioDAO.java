package br.gov.mec.aghu.paciente.dao;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.dominio.DominioTipoEnvioProntuario;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.paciente.vo.AipMovimentacaoProntuariosVO;

public class AipMovimentacaoProntuarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipMovimentacaoProntuarios> {
	
	private static final long serialVersionUID = -7074888885873543636L;

	/**
	 * Busca movimentação de prontuário (situação 'Requerido' e local contendo número da consulta) 
	 * pelo código do paciente e número da consulta
	 * 
 	 * @param pacCodigo
 	 * @param numConsulta
	 * @return
	 */
	public List<AipMovimentacaoProntuarios> pesquisarMovimentacaoPacienteProntRequerido(Integer pacCodigo, Integer numConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class);
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.SITUACAO.toString(), 
				DominioSituacaoMovimentoProntuario.Q));
		criteria.add(Restrictions.like(AipMovimentacaoProntuarios.Fields.LOCAL.toString(), Integer.toString(numConsulta), MatchMode.ANYWHERE));
		
		return this.executeCriteria(criteria);
	}
	
	public List<AipMovimentacaoProntuarios> pesquisarMovimentacaoPacienteVolumeRequerido(
			Integer pacCodigo, Short volumes, Integer seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class);
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.SITUACAO.toString(), DominioSituacaoMovimentoProntuario.Q));
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.VOLUMES.toString(), volumes));
		criteria.add(Restrictions.ne(AipMovimentacaoProntuarios.Fields.SEQ.toString(), seq));
		
		criteria.addOrder(Order.desc(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	public List<AipMovimentacaoProntuarios> pesquisarMovimentacaoPacienteVolumeRetirado(
			Integer pacCodigo, Short volumes, Integer seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class);
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.VOLUMES.toString(), volumes));
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.SITUACAO.toString(), DominioSituacaoMovimentoProntuario.R));
		criteria.add(Restrictions.ne(AipMovimentacaoProntuarios.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.ne(AipMovimentacaoProntuarios.Fields.TIPO_ENVIO.toString(), DominioTipoEnvioProntuario.P));
		
		criteria.addOrder(Order.desc(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString()));
		
		return this.executeCriteria(criteria);
	}
	

	/**
	 * 
	 * @param pacCodigo
	 * @param dtConsulta
	 * @return
	 */
	public List<AipMovimentacaoProntuarios> pesquisarMovimentacaoPorPacienteEDataConsulta(Integer pacCodigo, Date dtConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class);
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString(), pacCodigo));
		Date dataInicio = DateUtil.truncaData(dtConsulta);
		Date dataFim = DateUtil.truncaDataFim(dtConsulta);
		criteria.add(Restrictions.ge(AipMovimentacaoProntuarios.Fields.DATA_RETIRADA.toString(), 
				dataInicio));
		criteria.add(Restrictions.le(AipMovimentacaoProntuarios.Fields.DATA_RETIRADA.toString(), 
				dataFim));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param pacCodigo
	 * @param situacao
	 * @param tipoEnvio
	 * @param dataMovimento
	 * @return
	 */
	public AipMovimentacaoProntuarios obterMovimentacaoPorPacienteSituacaoTipoEnvioDtMovimento(
			Integer pacCodigo, DominioSituacaoMovimentoProntuario situacao,
			DominioTipoEnvioProntuario tipoEnvio, Date dataMovimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class);
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.SITUACAO.toString(), situacao));
		criteria.add(Restrictions.ne(AipMovimentacaoProntuarios.Fields.TIPO_ENVIO.toString(), tipoEnvio));
		criteria.add(Restrictions.lt(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString(), dataMovimento));
		
		return (AipMovimentacaoProntuarios)this.executeCriteriaUniqueResult(criteria);
	}

	/** Pesquisa movimentação de prontuário por codigo do paciente, 
	 *  volume e tipoEnvio diferente do passado por parâmetro
	 * 
	 * @param pacCodigo
	 * @param volume
	 * @param tipoEnvio
	 * @return
	 */
	public List<AipMovimentacaoProntuarios> pesquisarMovimentacaoPacienteProntuarioPorCodigoEVolume(Integer pacCodigo, Short volume, DominioTipoEnvioProntuario tipoEnvio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class);
		
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		if (volume != null){
			criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.VOLUMES.toString(), volume));
		}
		if (tipoEnvio != null){
			criteria.add(Restrictions.ne(AipMovimentacaoProntuarios.Fields.TIPO_ENVIO.toString(), tipoEnvio));
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaDesarquivamentoProntuario() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipMovimentacaoProntuarios.class);

		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.SITUACAO
				.toString(), DominioSituacaoMovimentoProntuario.Q));
		
		criteria.setFetchMode(AipMovimentacaoProntuarios.Fields.PACIENTE.toString(), FetchMode.JOIN);

		Calendar ontem = Calendar.getInstance();

		ontem.add(Calendar.DAY_OF_MONTH, -1);

		Calendar dezDias = Calendar.getInstance();

		dezDias.add(Calendar.DAY_OF_MONTH, 10);

//		criteria.add(Restrictions.between(
//				AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString(),
//				ontem.getTime(), dezDias.getTime()));

		if (isOracle()) {
			criteria.add(
					Restrictions.sqlRestriction("TRUNC("+AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.name()+") BETWEEN TO_DATE('"+DateUtil.dataToString(DateUtil.truncaData(ontem.getTime()), "dd/MM/yyyy")+"','DD/MM/YYYY') AND TO_DATE('"+DateUtil.dataToString(DateUtil.truncaData(dezDias.getTime()), "dd/MM/yyyy")+"','DD/MM/YYYY')"));
		} else {
			criteria.add(
					Restrictions.sqlRestriction("DATE("+AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.name()+")BETWEEN DATE('"+DateUtil.dataToString(DateUtil.truncaData(ontem.getTime()), "dd/MM/yyyy")+"') AND DATE('"+DateUtil.dataToString(DateUtil.truncaData(dezDias.getTime()), "dd/MM/yyyy") +"')"));
		}

		
	//	criteria.add(Restrictions.sqlRestriction(sql))
//		//join com pacientes para possível ordenação apenas
//		criteria.createAlias(AipMovimentacaoProntuarios.Fields.PACIENTE
//				.toString(), AipMovimentacaoProntuarios.Fields.PACIENTE
//				.toString());

		return criteria;
	}
	
	public Long obterCountPesquisaDesarquivamentoProntuarios() {
		DetachedCriteria criteria = obterCriteriaPesquisaDesarquivamentoProntuario();
		return  this.executeCriteriaCount(criteria);
	}

	public List<AipMovimentacaoProntuarios> pesquisarDesarquivamentoProntuarios(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		DetachedCriteria criteria = obterCriteriaPesquisaDesarquivamentoProntuario();
		if (orderProperty == null) {
			criteria.addOrder(Order.asc(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString()));
		}

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria createPesquisaCriteria(Integer slpCodigo,
			Date dataMovimento) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipMovimentacaoProntuarios.class);

		criteria.createAlias(AipMovimentacaoProntuarios.Fields.PACIENTE
				.toString(), AipMovimentacaoProntuarios.Fields.PACIENTE
				.toString());
		criteria.createAlias(AipMovimentacaoProntuarios.Fields.SOLICITACAO
				.toString(), AipMovimentacaoProntuarios.Fields.SOLICITACAO
				.toString());

		if (slpCodigo != null) {
			criteria.add(Restrictions.eq(
					AipMovimentacaoProntuarios.Fields.SLP_CODIGO.toString(),
					slpCodigo));
		}
		if (dataMovimento != null) {
			// DATA_MOVIMENTO >= dataMovimento AND DATA_MOVIMENTO <
			// (dataMovimento + 2)
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataMovimento);
			cal.add(Calendar.DATE, 2);

			criteria.add(Restrictions
					.isNotNull(AipMovimentacaoProntuarios.Fields.SLP_CODIGO
							.toString()));
			criteria.add(Restrictions
					.ge(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO
							.toString(), dataMovimento));
			criteria.add(Restrictions
					.le(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO
							.toString(), cal.getTime()));
		}

		criteria
				.setProjection(Projections
						.projectionList()
						.add(
								Projections
										.property(AipMovimentacaoProntuarios.Fields.SLP_CODIGO
												.toString()))
						.add(
								Projections
										.property(AipMovimentacaoProntuarios.Fields.PAC_PRONTUARIO
												.toString()))
						.add(
								Projections
										.property(AipMovimentacaoProntuarios.Fields.PAC_NOME
												.toString()))
						.add(
								Projections
										.property(AipMovimentacaoProntuarios.Fields.LOCAL
												.toString()))
						.add(
								Projections
										.property(AipMovimentacaoProntuarios.Fields.OBSERVACOES
												.toString()))
						.add(
								Projections
										.property(AipMovimentacaoProntuarios.Fields.VOLUMES
												.toString()))
						.add(
								Projections
										.property(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO
												.toString())));

		criteria.addOrder(Order
				.asc(AipMovimentacaoProntuarios.Fields.SLP_CODIGO.toString()));
		criteria.addOrder(Order
				.asc(AipMovimentacaoProntuarios.Fields.PAC_PRONTUARIO
						.toString()));

		return criteria;
	}

	public List<Object[]> listaInformacoesMovimentacoesProntuario(Integer slpCodigo, Date dataMovimento) {
		DetachedCriteria criteria = createPesquisaCriteria(slpCodigo, dataMovimento);

		return executeCriteria(criteria);
	}

	public List<AipMovimentacaoProntuarios> listarMovimentacoesProntuariosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class);

		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<AipMovimentacaoProntuarios> listarMovimentacoesProntuarios(AipPacientes paciente, Date dthrInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class);
		criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.PACIENTE.toString(), paciente));
		criteria.add(Restrictions.ge(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString(), dthrInicio));
		
		return executeCriteria(criteria);
	}
	
	public List<AipMovimentacaoProntuarios> listarMovimentacoesProntuariosPorCodigoPacienteESituacao(Integer pacCodigo,
			Integer codEvento, Integer origemEmergencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class, "MVP");
		criteria.createAlias("MVP." + AipMovimentacaoProntuarios.Fields.SOLICITANTE.toString() , "SOP");
		
		criteria.add(Restrictions.eq("MVP." + AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("MVP." + AipMovimentacaoProntuarios.Fields.SITUACAO.toString(), DominioSituacaoMovimentoProntuario.R));
		
		List<Short> origemEventos = Arrays.asList(codEvento.shortValue(), origemEmergencia.shortValue());
		criteria.add(Restrictions.in("SOP." + AipSolicitantesProntuario.Fields.ORIGEM_EVENTOS_SEQ.toString(), origemEventos));
		
		return executeCriteria(criteria);
	}
	
	public Long pesquisaCount(Integer codigoPacientePesquisa, Integer prontuario, String nomePesquisaPaciente) {
		DetachedCriteria criteria = createPesquisaCriteriaAipPacientes(codigoPacientePesquisa, prontuario, nomePesquisaPaciente);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria createPesquisaCriteriaAipPacientes(
			Integer codigoPacientePesquisa, Integer prontuario, String nomePesquisaPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		if (codigoPacientePesquisa != null ) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigoPacientePesquisa));
		}
		if (prontuario != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}
		if (nomePesquisaPaciente != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.NOME.toString(), nomePesquisaPaciente));
		}
		return criteria;
	}
	
	public List<AipPacientes> pesquisa(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer codigoPaciente, Integer prontuario, String nomePesquisaPaciente) {
		DetachedCriteria criteria = createPesquisaCriteriaAipPacientes(codigoPaciente, prontuario, nomePesquisaPaciente);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public List<AipMovimentacaoProntuariosVO> pesquisaMovimentacoesDeProntuarios(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AipPacientes paciente,
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, DominioSituacaoMovimentoProntuario situacao, 
			Date dataMovimentacaoConsulta) {
		DetachedCriteria criteria = createPesquisaCriteriaAipMovimentacaoProntuario(paciente, 
				origemProntuariosPesquisa, unidadeSolicitantePesquisa, situacao, dataMovimentacaoConsulta);
		if(firstResult != null){
			return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		} else{
			return executeCriteria(criteria);
		}
		
	}
	
	public List<AipMovimentacaoProntuariosVO> pesquisaMovimentacoesDeProntuariosPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigoPesquisaPaciente,
			AipPacientes paciente, AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, 
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta) {
		DetachedCriteria criteria = createPesquisaCriteriaMovimentacaoProntuarioPaciente(paciente,  
				origemProntuariosPesquisa, unidadeSolicitantePesquisa, situacao, dataMovimentacaoConsulta);
		if(firstResult != null){
			return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		} else{
			return executeCriteria(criteria);
		}
		
	}
	
	
	public Long pesquisaMovimentacoesDeProntuariosCount(
			AipPacientes paciente,
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, 
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta) {
		DetachedCriteria criteria = createPesquisaCriteriaAipMovimentacaoProntuario(paciente,  
				origemProntuariosPesquisa, unidadeSolicitantePesquisa,  situacao, dataMovimentacaoConsulta);
		return executeCriteriaCount(criteria);
	}

	public List<AipMovimentacaoProntuariosVO> pesquisarTodasMovimentacoesParaSelecionarTodas(
			AipPacientes paciente, 
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, DominioSituacaoMovimentoProntuario situacao, 
			Date dataReferencia) {
		DetachedCriteria criteria = createPesquisaCriteriaAipMovimentacaoProntuario(paciente, 
				origemProntuariosPesquisa, unidadeSolicitantePesquisa, situacao, dataReferencia);
		return executeCriteria(criteria);
	}	
	
	public Long pesquisaMovimentacoesDeProntuariosPacienteCount(Integer codigoPesquisaPaciente,
	AipPacientes paciente, AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, 
	String nomePesquisaPaciente, DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta) {
		DetachedCriteria criteria = createPesquisaCriteriaMovimentacaoProntuarioPaciente(paciente,  
				origemProntuariosPesquisa, unidadeSolicitantePesquisa, situacao, dataMovimentacaoConsulta);
		return executeCriteriaCount(criteria);
	}	
	
	private DetachedCriteria createPesquisaCriteriaMovimentacaoProntuarioPaciente(
			AipPacientes paciente,  AghSamis origemProntuariosPesquisa,
			AghUnidadesFuncionais unidadeSolicitantePesquisa, DominioSituacaoMovimentoProntuario situacao,
			Date dataMovimentacaoConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class, "aipmov");
		verificaDadosPaciente(paciente, criteria);
		createProjectionMovimentacaoProntuario(criteria);
		criteria.setResultTransformer(Transformers.aliasToBean(AipMovimentacaoProntuariosVO.class));
		return criteria;
	}			
	
	
	private DetachedCriteria createPesquisaCriteriaAipMovimentacaoProntuario(AipPacientes paciente,
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, 
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class, "aipmov");
		criteria.createAlias("aipmov."+AipMovimentacaoProntuarios.Fields.PACIENTE.toString(), "paciente", JoinType.INNER_JOIN);
		criteria.createAlias("aipmov."+AipMovimentacaoProntuarios.Fields.SAMIS.toString(), "samis", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("aipmov."+AipMovimentacaoProntuarios.Fields.SOLICITANTE.toString(), "sol", JoinType.LEFT_OUTER_JOIN);
		verificaDadosPaciente(paciente,  criteria);
		if (origemProntuariosPesquisa != null) {
			criteria.add(Restrictions.eq("samis."+AghSamis.Fields.CODIGO.toString(), origemProntuariosPesquisa.getCodigo()));
		}
		if(unidadeSolicitantePesquisa != null && !StringUtils.isBlank(unidadeSolicitantePesquisa.getDescricao())) {
			criteria.add(Restrictions.ilike("aipmov."+AipMovimentacaoProntuarios.Fields.LOCAL.toString(), unidadeSolicitantePesquisa.getSigla(), MatchMode.ANYWHERE));
		}
		if(situacao != null) {
			criteria.add(Restrictions.eq("aipmov."+AipMovimentacaoProntuarios.Fields.SITUACAO.toString(), situacao));
		}
		if(dataMovimentacaoConsulta != null){
			Date dtMovimentacaoHrInicio = DateUtil.truncaData(dataMovimentacaoConsulta);
			Date dtMovimentacaoHrFim = DateUtil.obterDataComHoraFinal(dtMovimentacaoHrInicio);
			criteria.add(Restrictions.between("aipmov."+AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString(), dtMovimentacaoHrInicio, dtMovimentacaoHrFim));
		}
		createProjectionMovimentacaoProntuario(criteria);
		criteria.setResultTransformer(Transformers.aliasToBean(AipMovimentacaoProntuariosVO.class));
		return criteria;
	}

	private void verificaDadosPaciente(AipPacientes paciente,
			DetachedCriteria criteria) {
		if (paciente != null){
			if (paciente.getCodigo() != null ) {
				criteria.add(Restrictions.eq("paciente."+AipPacientes.Fields.CODIGO.toString(), paciente.getCodigo()));
			}
			if (paciente.getProntuario() != null) {
				criteria.add(Restrictions.eq("paciente."+AipPacientes.Fields.PRONTUARIO.toString(), paciente.getProntuario()));
			}
			if (paciente.getNome() != null) {
				criteria.add(Restrictions.eq("paciente."+AipPacientes.Fields.NOME.toString(), paciente.getNome()));
			}
		}
	}	
	
	
	private void createProjectionMovimentacaoProntuario(DetachedCriteria criteria) {
		criteria.setProjection(
		Projections.projectionList()
		.add(
		Projections.property("paciente" + "." + AipPacientes.Fields.CODIGO.toString()), "pacCodigo")
		.add(
		Projections.property("paciente" + "." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
		.add(
		Projections.property("paciente" + "." + AipPacientes.Fields.NOME.toString()), "nomePaciente")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString()), "dataMovimentacao")
		.add(
		Projections.property("samis" + "." + AghSamis.Fields.DESCRICAO), "origemProntuario")
		.add(
		Projections.property("samis" + "." + AghSamis.Fields.CODIGO), "seqOrigemProntuario")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.LOCAL.toString()), "localPrimeiraMovimentacao")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.LOCAL_ATUAL.toString()), "localAtual")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.SITUACAO.toString()), "situacao")
		.add(
		Projections.property("sol." + AipSolicitantesProntuario.Fields.DESCRICAO.toString() ), "solicitanteDescricao")
		.add(
		Projections.property("sol." + AipSolicitantesProntuario.Fields.SEQ.toString() ), "codigoSolicitante")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.TIPO_ENVIO.toString()), "tipoEnvio")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.VOLUMES.toString()), "volumes")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.DATA_RETIRADA.toString()), "dataRetirada")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.DATA_DEVOLUCAO.toString()), "dataDevolucao")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.OBSERVACOES.toString()), "observacoes")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.SEQ.toString()), "seq")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.SERVIDOR_MATRICULA.toString()), "servidorMatricula")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.SERVIDOR_RETIRADO_MATRICULA.toString()), "serMatriculaRetirado")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.SERVIDOR_RETIRADO_VINCULO.toString()), "serVinCodigoRetirado")
		.add(
		Projections.property("aipmov."+AipMovimentacaoProntuarios.Fields.SLP_CODIGO.toString()), "slpCodigo")
		);		
	}
}