package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioFuncionario;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioRestricaoUsuario;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PesquisaExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao {

	private static final long serialVersionUID = 3359207443420534777L;

	public List<AelAgrpPesquisas> buscaAgrupamentosPesquisa(Object pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAgrpPesquisas.class);
		criteria.add(Restrictions.ilike(AelAgrpPesquisas.Fields.DESCRICAO.toString(), pesquisa.toString(), MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq(AelAgrpPesquisas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	/**
	 * Método responsável por buscar os exames de um certo paciente pela sua solicitação de exame
	 * o resultado dessa pesquisa será mostrado na tela de resultados do paciente
	 * @param seq_solicitacao
	 * @return List<PesquisaExamesPacientesVO>
	 */
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorSolicExa(Integer seq_solicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		criteria.createAlias(AipPacientes.Fields.ATENDIMENTOS.toString(), "atd", JoinType.INNER_JOIN);// join atendimentos
		criteria.createAlias("atd."+AghAtendimentos.Fields.CONSULTA.toString(), "cons", JoinType.LEFT_OUTER_JOIN);//itens solicitação exames
		criteria.createAlias("atd."+AghAtendimentos.Fields.SOLICITACAO_EXAMES.toString(), "soe", JoinType.INNER_JOIN); //join solicitação exames
		criteria.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.SEQ.toString(), seq_solicitacao));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipPacientes.Fields.CODIGO.toString()), "codigo")
				.add(Projections.property(AipPacientes.Fields.NOME.toString()), "nome")
				.add(Projections.property(AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property("cons."+AacConsultas.Fields.NUMERO.toString()), "consulta")
		);

		List<PesquisaExamesPacientesVO> listaPacientes = new ArrayList<PesquisaExamesPacientesVO>();
		
		List<Object[]> valores = executeCriteria(criteria);
		for (Object[] valor : valores) {
			PesquisaExamesPacientesVO paciente = new PesquisaExamesPacientesVO();
			
			paciente.setCodigo((Integer)valor[0]);
			paciente.setNomePaciente((String)valor[1]);
			paciente.setProntuario((Integer)valor[2]);
			paciente.setConsulta((Integer)valor[3]);
			
			listaPacientes.add(paciente);
		}
		
		return listaPacientes;
	}
	
	public List<VAelExamesAtdDiversosVO> buscarVAelExamesAtdDiversos(final VAelExamesAtdDiversosFiltroVO filtro) {
		if (filtro == null) {
			throw new IllegalArgumentException("Deve ser informado um filtro.");
		}
		final DetachedCriteria criteria = this.adicionarRestricions1(this.adicionarProjection(this.criarCriteriaBaseVAelExamesAtdDiversos(), filtro),
				filtro.getPjqSeq(), filtro.getLaeSeq(), filtro.getCcqSeq(),  filtro.getDdvSeq(), filtro.getCadSeq(), filtro.getDtInicio(), filtro.getDtFinal());
		
		criteria.add(Restrictions.sqlRestriction(" SUBSTR(eis8_.sit_codigo,1,2) = this_.sit_codigo "));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class, "EIS1");
		subCriteria.setProjection(Projections.max("EIS1." + AelExtratoItemSolicitacao.Fields.CRIADO_EM.toString()));
		subCriteria.add(Restrictions.eqProperty("EIS1." + AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), "EIS."
				+ AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("EIS1." + AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), "EIS."
				+ AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString()));
		
		criteria.add(Subqueries.propertyIn("EIS." + AelExtratoItemSolicitacao.Fields.CRIADO_EM.toString() , subCriteria));
		
		if (filtro.getSoeSeq() != null) {
			criteria.add(Restrictions.eq("SOE." + AelSolicitacaoExames.Fields.SEQ.toString(), filtro.getSoeSeq()));
		} else if (filtro.getPacCodigo() != null) {
			criteria.add(Restrictions.eq("ATV." + AelAtendimentoDiversos.Fields.PAC_CODIGO.toString(), filtro.getPacCodigo()));
		}

		if (filtro.getNumeroAp() != null && filtro.getLu2Seq() != null) {

			//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
			criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "LUL");
			criteria.createAlias("LUL." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "LUX");
			criteria.createAlias("LUX." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "LUM");

			criteria.add(Restrictions.eq("LUM." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), filtro.getNumeroAp().longValue()));
			criteria.add(Restrictions.eq("LUM." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), filtro.getLu2Seq()));
			
		}

		if (filtro.getSitCodigo() != null) {
			criteria.add(Restrictions.sqlRestriction(" SUBSTR(this_.sit_codigo, 1, 2) = ? ",
					filtro.getSitCodigo(), StringType.INSTANCE));
		}

		if (filtro.getIndImpressoLaudo() != null && filtro.getIndImpressoLaudo().length > 0) {
			criteria.add(Restrictions.in("ISE." + AelItemSolicitacaoExames.Fields.IND_IMPRESSO_LAUDO.toString(), filtro.getIndImpressoLaudo()));
		}

		if (filtro.getSitCodigoCancelado() != null) {
			criteria.add(Restrictions.sqlRestriction(" SUBSTR(this_.sit_codigo,1,2) != ? ",
					filtro.getSitCodigoCancelado(), StringType.INSTANCE));
		}

		if (filtro.getSitCodigoPendente() != null) {
			criteria.add(Restrictions.sqlRestriction(" SUBSTR(this_.sit_codigo,1,2) != ? ",
					filtro.getSitCodigoPendente(), StringType.INSTANCE));
		}

		criteria.setResultTransformer(Transformers.aliasToBean(VAelExamesAtdDiversosVO.class));
		return executeCriteria(criteria);
	}

	private DetachedCriteria adicionarRestricions1(DetachedCriteria criteria, Integer integer, Integer integer2, Integer integer3, Integer integer4, Integer integer5, Date date, Date date2) {
		if (integer != null) {
			criteria.add(Restrictions.eq("ATV." + AelAtendimentoDiversos.Fields.PJQ_SEQ.toString(), integer));
		}

		if (integer2 != null) {
			criteria.add(Restrictions.eq("ATV." + AelAtendimentoDiversos.Fields.LAE_SEQ.toString(), integer2));
		}

		if (integer3 != null) {
			criteria.add(Restrictions.eq("ATV." + AelAtendimentoDiversos.Fields.CCQ_SEQ.toString(), integer3));
		}

		if (integer4 != null) {
			criteria.add(Restrictions.eq("ATV." + AelAtendimentoDiversos.Fields.DDV_SEQ.toString(), integer4));
		}

		if (integer5 != null) {
			criteria.add(Restrictions.eq("ATV." + AelAtendimentoDiversos.Fields.CAD_SEQ.toString(), integer5));
		}
		
		if (date != null && date2 == null) {
			criteria.add(Restrictions.gt("EIS." + AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString(), DateUtil.obterDataComHoraInical(date)));
		} else if (date == null && date2 != null) {
			criteria.add(Restrictions.lt("EIS." + AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString(), DateUtil.obterDataComHoraFinal(date2)));
		} else if (date != null && date2 != null) {
			criteria.add(Restrictions.between("EIS." + AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString(), DateUtil.obterDataComHoraInical(date), DateUtil.obterDataComHoraFinal(date2)));
		}
		
		return criteria;
	}

	private DetachedCriteria adicionarProjection(final DetachedCriteria criteria, VAelExamesAtdDiversosFiltroVO filtro) {
		
		ProjectionList projectionList = Projections.projectionList();
		
		projectionList			
				.add(Projections.groupProperty("SOE." + AelSolicitacaoExames.Fields.SEQ.toString()), VAelExamesAtdDiversosVO.Fields.SOE_SEQ.toString())
				.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()), VAelExamesAtdDiversosVO.Fields.SEQP.toString())
				.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.ISE_SOE_SEQ.toString()), VAelExamesAtdDiversosVO.Fields.ISE_SOE_SEQ.toString())
				.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.ISE_SEQP.toString()), VAelExamesAtdDiversosVO.Fields.ISE_SEQP.toString())
				.add(Projections.groupProperty("SOE." + AelSolicitacaoExames.Fields.CRIADO_EM.toString()), VAelExamesAtdDiversosVO.Fields.CRIADO_EM.toString())
				.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString()), VAelExamesAtdDiversosVO.Fields.SIT_CODIGO.toString())
				.add(Projections.groupProperty("UFE." + AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString()), VAelExamesAtdDiversosVO.Fields.UFE_EMA_EXA_SIGLA.toString())
				.add(Projections.groupProperty("UFE." + AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString()), VAelExamesAtdDiversosVO.Fields.UFE_EMA_MAN_SEQ.toString())
				.add(Projections.groupProperty("UFE." + AelUnfExecutaExames.Fields.UNF_SEQ.toString()), VAelExamesAtdDiversosVO.Fields.UFE_UNF_SEQ.toString())
				.add(Projections.groupProperty("UFE." + AelUnfExecutaExames.Fields.IND_LIBERA_RESULT_AUTOM.toString()), VAelExamesAtdDiversosVO.Fields.IND_LIBERA_RESULT_AUTOM.toString())
				.add(Projections.groupProperty("EXA." + AelExames.Fields.DESCRICAO_USUAL.toString()), VAelExamesAtdDiversosVO.Fields.NOME_USUAL_EXAME.toString())
				.add(Projections.groupProperty("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()), VAelExamesAtdDiversosVO.Fields.DESCRICAO_MATERIAL.toString())
				.add(Projections.groupProperty("SOE." + AelSolicitacaoExames.Fields.SERVIDOR_MATRICULA.toString()), VAelExamesAtdDiversosVO.Fields.SER_MATRICULA.toString())
				.add(Projections.groupProperty("SOE." + AelSolicitacaoExames.Fields.SERVIDOR_VIN_CODIGO.toString()), VAelExamesAtdDiversosVO.Fields.SER_VIN_CODIGO.toString())
				.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.MOC_SEQ.toString()), VAelExamesAtdDiversosVO.Fields.MOC_SEQ.toString())
				.add(Projections.groupProperty("MOC." + AelMotivoCancelaExames.Fields.IND_PERMITE_INCLUIR_RESULTADO.toString()), VAelExamesAtdDiversosVO.Fields.IND_PERMITE_INCLUIR_RESULTADO.toString())
				.add(Projections.groupProperty("EIS." + AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()), VAelExamesAtdDiversosVO.Fields.DTHR_EVENTO.toString())
				.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString()), VAelExamesAtdDiversosVO.Fields.TIPO_COLETA.toString())
				.add(Projections.groupProperty("VEM." + VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()), VAelExamesAtdDiversosVO.Fields.NOME_EXAME_MATERIAL.toString())
				.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()), VAelExamesAtdDiversosVO.Fields.DTHR_PROGRAMADA.toString())
				.add(Projections.sqlGroupProjection("CASE WHEN man5_.ind_exige_desc_mat_anls = 'S' THEN this_.desc_material_analise ELSE man5_.descricao END as " + VAelExamesAtdDiversosVO.Fields.ISE_DESC_MAT_ANALISE.toString(),
						"CASE WHEN man5_.ind_exige_desc_mat_anls = 'S' THEN this_.desc_material_analise ELSE man5_.descricao END ",
						new String[] { VAelExamesAtdDiversosVO.Fields.ISE_DESC_MAT_ANALISE.toString() }, new Type[] { StringType.INSTANCE }))
				.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.EIN_TIPO.toString()), VAelExamesAtdDiversosVO.Fields.EIN_TIPO.toString())
				.add(Projections.groupProperty("VEM." + VAelExameMatAnalise.Fields.IND_CCI.toString()), VAelExamesAtdDiversosVO.Fields.IND_CCI.toString())
				.add(Projections.groupProperty("ATV." + AelAtendimentoDiversos.Fields.PJQ_SEQ.toString()), VAelExamesAtdDiversosVO.Fields.PJQ_SEQ.toString())
				.add(Projections.groupProperty("ATV." + AelAtendimentoDiversos.Fields.LAE_SEQ.toString()), VAelExamesAtdDiversosVO.Fields.LAE_SEQ.toString())
				.add(Projections.groupProperty("ATV." + AelAtendimentoDiversos.Fields.CCQ_SEQ.toString()), VAelExamesAtdDiversosVO.Fields.CCQ_SEQ.toString())
				.add(Projections.groupProperty("ATV." + AelAtendimentoDiversos.Fields.DDV_SEQ.toString()), VAelExamesAtdDiversosVO.Fields.DDV_SEQ.toString())
				.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.IND_IMPRESSO_LAUDO.toString()), VAelExamesAtdDiversosVO.Fields.IND_IMPRESSO_LAUDO.toString())
				.add(Projections.groupProperty("ATV." + AelAtendimentoDiversos.Fields.CAD_SEQ.toString()), VAelExamesAtdDiversosVO.Fields.CAD_SEQ.toString())
				.add(Projections.groupProperty("ATV." + AelAtendimentoDiversos.Fields.PAC_CODIGO.toString()), VAelExamesAtdDiversosVO.Fields.PAC_CODIGO2.toString())
				.add(Projections.groupProperty("ISE." + AelItemSolicitacaoExames.Fields.PAC_ORU_ACC_NUMBER.toString()), VAelExamesAtdDiversosVO.Fields.PAC_ORU_ACC_NUMBER.toString());

		if (filtro.getNumeroAp() != null && filtro.getLu2Seq() != null) {
			projectionList.add(Projections.groupProperty("LUM." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()), VAelExamesAtdDiversosVO.Fields.NUMERO_AP.toString());
		}
			
		criteria.setProjection(projectionList);
		
		return criteria;
	}

	private DetachedCriteria criarCriteriaBaseVAelExamesAtdDiversos() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UFE");
		criteria.createAlias("UFE." + AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA");
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "MAN");
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.EXAME.toString(), "EXA");
		criteria.createAlias("UFE." + AelUnfExecutaExames.Fields.V_AEL_EXAME_MAT_ANALISE.toString(), "VEM");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString(), "EIS");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_MOTIVO_CANCELA_EXAMES.toString(), "MOC", JoinType.LEFT_OUTER_JOIN).getAlias();
		
		return criteria;
	}
	
	/**
	 * Método responsável por buscar os exames de um certo paciente pelo numero AP
	 * o resultado dessa pesquisa será mostrado na tela de resultados do paciente
	 * @param configExame 
	 * @param seq_solicitacao
	 * @return List<PesquisaExamesPacientesVO>
	 */
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorNumeroAp(Long numeroAp, AelConfigExLaudoUnico configExame) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		criteria.createAlias(AipPacientes.Fields.ATENDIMENTOS.toString(), "atd");
		criteria.createAlias("atd."+AghAtendimentos.Fields.SOLICITACAO_EXAMES.toString(), "soe"); 
		criteria.createAlias("soe." + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ise"); 

		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");
		
		criteria.add(Restrictions.eq("lum." +  AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq("lum." +  AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), configExame.getSeq()));
		
		// Projeção
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipPacientes.Fields.CODIGO.toString()), "codigo")
				.add(Projections.property(AipPacientes.Fields.NOME.toString()), "nome")
				.add(Projections.property(AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property("soe." + AelSolicitacaoExames.Fields.SEQ.toString()), "solicitacao")
		);

		List<PesquisaExamesPacientesVO> listaPacientes = new ArrayList<PesquisaExamesPacientesVO>();
		
		List<Object[]> valores = executeCriteria(criteria);
		for (Object[] valor : valores) {
			PesquisaExamesPacientesVO paciente = new PesquisaExamesPacientesVO();
			
			paciente.setCodigo((Integer)valor[0]);
			paciente.setNomePaciente((String)valor[1]);
			paciente.setProntuario((Integer)valor[2]);
			paciente.setNumeroSolicitacaoInfo((Integer)valor[3]);
			
			listaPacientes.add(paciente);
		}
		
		return listaPacientes;
	}


	/**
	 * Método responsável por buscar os exames de um certo paciente pelo número da sua consulta
	 * o resultado dessa pesquisa será mostrado na tela de resultados do paciente
	 * @param seq_solicitacao
	 * @return List<PesquisaExamesPacientesVO>
	 */
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorConsulta(Integer seq_consulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		criteria.createAlias(AipPacientes.Fields.ATENDIMENTOS.toString(), "atd", JoinType.INNER_JOIN);// solicitação exames
		criteria.createAlias("atd."+AghAtendimentos.Fields.CONSULTA.toString(), "cons", JoinType.INNER_JOIN);//itens solicitação exames
		criteria.add(Restrictions.eq("cons."+AacConsultas.Fields.NUMERO.toString(), seq_consulta));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipPacientes.Fields.CODIGO.toString()), "codigo")
				.add(Projections.property(AipPacientes.Fields.NOME.toString()), "nome")
				.add(Projections.property(AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property("cons."+AacConsultas.Fields.NUMERO.toString()), "consulta")
		);

		List<PesquisaExamesPacientesVO> listaPacientes = new ArrayList<PesquisaExamesPacientesVO>();

		List<Object[]> valores = executeCriteria(criteria);
		for (Object[] valor : valores) {
			PesquisaExamesPacientesVO paciente = new PesquisaExamesPacientesVO();

			paciente.setCodigo((Integer)valor[0]);
			paciente.setNomePaciente((String)valor[1]);
			paciente.setProntuario((Integer)valor[2]);
			paciente.setConsulta((Integer)valor[3]);

			listaPacientes.add(paciente);
		}
		
		return listaPacientes;
	}

	/**
	 * Método responsável por buscar os exames de um grupo de pacientes atendidos pelo servidor pesquisado
	 * o resultado dessa pesquisa será mostrado na tela de resultados do paciente
	 * @param seq_solicitacao
	 * @return List<PesquisaExamesPacientesVO>
	 */
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorServidor(Integer matricula, Short vinCodigo) {
		/*faço nova pesquisa*/
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		criteria.createAlias(AipPacientes.Fields.ATENDIMENTOS.toString(), "atd");// solicitação exames
		criteria.createAlias("atd."+AghAtendimentos.Fields.CONSULTA.toString(), "cons");//itens solicitação exames
		
		//subquery
		//--------------------------------------------------------------------------------------------------------
		DetachedCriteria subQuery= DetachedCriteria.forClass(AacConsultas.class);
		subQuery.add(Restrictions.eq(AacConsultas.Fields.SERVIDOR_CONSULTADO_MATRICULA.toString(), matricula));
		subQuery.add(Restrictions.eq(AacConsultas.Fields.SERVIDOR_CONSULTADO_VIN_CODIGO.toString(), vinCodigo));
		subQuery.add(Restrictions.eq(AacConsultas.Fields.IND_FUNCIONARIO.toString(), DominioFuncionario.F));
		subQuery.setProjection(Projections.property(AacConsultas.Fields.NUMERO.toString()));
		//--------------------------------------------------------------------------------------------------------
		criteria.add(Property.forName("cons."+AacConsultas.Fields.NUMERO.toString()).in(subQuery));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(AipPacientes.Fields.CODIGO.toString())), "codigo")
				.add(Projections.property(AipPacientes.Fields.NOME.toString()), "nome")
				.add(Projections.property(AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				//.add(Projections.property("cons."+AacConsultas.Fields.NUMERO.toString()), "consulta")
		);

		List<PesquisaExamesPacientesVO> listaPacientes = new ArrayList<PesquisaExamesPacientesVO>();
		
		List<Object[]> valores = executeCriteria(criteria);
		for (Object[] valor : valores) {
			PesquisaExamesPacientesVO paciente = new PesquisaExamesPacientesVO();
			
			paciente.setCodigo((Integer)valor[0]);
			paciente.setNomePaciente((String)valor[1]);
			paciente.setProntuario((Integer)valor[2]);
			//paciente.setConsulta((Integer)valor[3]);
			
			listaPacientes.add(paciente);
		}
		
		return listaPacientes;
	}

	/**
	 * Método responsável por buscar pacientes conforme os parâmetros passados, também serão exibidos os resultados na tela de paciente
	 * @param prontuarioPac
	 * @param nomePaciente
	 * @param leitoPac
	 * @param unidadeFuncionalPac
	 * @return List<PesquisaExamesPacientesVO>
	 */
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorParametros(Integer prontuarioPac, String nomePaciente, AinLeitos leitoPac, AghUnidadesFuncionais unidadeFuncionalPac){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		 /** IF  NAME_IN('QMS$CTRL.UNF_SEQ') is not null and NAME_IN('QMS$CTRL.NUM_FIELD7') is  null then **/
		if(unidadeFuncionalPac != null){//&& consultaPac == null nunca ira acontecer aqui, se for informada cairá em outra consulta
			/*	pego os pacientes da unf	*/

			DetachedCriteria subCriteria = DetachedCriteria.forClass(AghAtendimentos.class);
			subCriteria.add(Restrictions.eq(AghAtendimentos.Fields.UNF_SEQ.toString(), unidadeFuncionalPac.getSeq() ));
			subCriteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

			subCriteria.setProjection(Projections.distinct(Projections.property(AghAtendimentos.Fields.PAC_CODIGO.toString())));

			criteria.add(Property.forName(AipPacientes.Fields.CODIGO.toString()).in(subCriteria));
		}

		/** IF  NAME_IN('QMS$CTRL.LTO_ID') is not null and NAME_IN('QMS$CTRL.NUM_FIELD7') is  null then **/
		if(leitoPac != null){//&& consultaPac == null nunca ira acontecer aqui, se for informada cairá em outra consulta
			/*	pego os pacientes do leito	*/
			DetachedCriteria subCriteria = DetachedCriteria.forClass(AghAtendimentos.class);
			subCriteria.add(Restrictions.eq(AghAtendimentos.Fields.LTO_LTO_ID.toString(), leitoPac.getLeitoID()));
			subCriteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

			subCriteria.setProjection(Projections.distinct(Projections.property(AghAtendimentos.Fields.PAC_CODIGO.toString())));

			criteria.add(Property.forName(AipPacientes.Fields.CODIGO.toString()).in(subCriteria));
		}

		if(prontuarioPac != null){
			criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuarioPac));
		}

		if(nomePaciente!=null && !nomePaciente.isEmpty()){
			criteria.add(Restrictions.eq(AipPacientes.Fields.NOME.toString(), nomePaciente));
		}

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipPacientes.Fields.CODIGO.toString()))
				.add(Projections.property(AipPacientes.Fields.NOME.toString()))
				.add(Projections.property(AipPacientes.Fields.PRONTUARIO.toString()))
		);

		criteria.addOrder(Order.asc(AipPacientes.Fields.NOME.toString()));

		List<PesquisaExamesPacientesVO> listaPacientes = new ArrayList<PesquisaExamesPacientesVO>();
		List<Object[]> valores = executeCriteria(criteria);
		for (Object[] valor : valores) {
			PesquisaExamesPacientesVO paciente = new PesquisaExamesPacientesVO();

			paciente.setCodigo((Integer)valor[0]);
			paciente.setNomePaciente((String)valor[1]);
			paciente.setProntuario((Integer)valor[2]);

			listaPacientes.add(paciente);
		}

		return listaPacientes;
	}
	
	/**
	 * Método que busca todas as informações dos paciente por solicitante
	 * @param filtro
	 * @return List<PesquisaExamesPacientesResultsVO>
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorSolicitante(PesquisaExamesFiltroVO filtro){
		List<PesquisaExamesPacientesResultsVO> exames = new ArrayList<PesquisaExamesPacientesResultsVO>();

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		criteria.createCriteria(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "une", JoinType.LEFT_OUTER_JOIN);

		criteria.createCriteria(AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString(), "eis", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "sis", JoinType.LEFT_OUTER_JOIN);

		criteria.createCriteria("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);
		criteria.createCriteria("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "aip", JoinType.LEFT_OUTER_JOIN);
		
		if (filtro.getNumeroAp() != null || filtro.getConfigExame() != null) {
			criteria.createCriteria(AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul", JoinType.INNER_JOIN);
			criteria.createCriteria("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux", JoinType.INNER_JOIN);
			criteria.createCriteria("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum", JoinType.INNER_JOIN);

			if (filtro.getNumeroAp() != null) {
				criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), filtro.getNumeroAp()));
			}

			if (filtro.getConfigExame() != null) {
				criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), filtro.getConfigExame().getSeq()));
			}
		}		

		//pegar os exames solicitados por um determinado servidor
		if(filtro.getServidorSolic() != null && filtro.getServidorSolic().getId() != null){
			criteria.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.SERVIDOR_MATRICULA.toString(), filtro.getServidorSolic().getId().getMatricula()));
			criteria.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.SERVIDOR_VIN_CODIGO.toString(), filtro.getServidorSolic().getId().getVinCodigo()));
		}

		/*	define_where_PAC;  Define a condição WHERE do Bloco PAC a partir do Prontuário
			def_where_solic := def_where_solic || 'Where soe_seq = :qms$ctrl.seq_solicitacao';	 */
		if(filtro.getNumeroSolicitacaoInfo()!=null && filtro.getNumeroSolicitacaoInfo() > 0){
			criteria.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.SEQ.toString(), filtro.getNumeroSolicitacaoInfo()));
		}

		//IF  NAME_IN('QMS$CTRL.ORIGEM') is not null then
		if(filtro!=null && filtro.getOrigemAtendimentoInfo()!=null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), filtro.getOrigemAtendimentoInfo())); 
		}

		//IF  NAME_IN('QMS$CTRL.UNF_EXECUTORA') is not null then
		if(filtro!=null && filtro.getAelUnfExecutoraInfo()!=null){
			criteria.add(Restrictions.eq("une."+AelUnfExecutaExames.Fields.UNF_SEQ.toString(), filtro.getAelUnfExecutoraInfo().getSeq()));
		}

		//IF  NAME_IN('QMS$CTRL.DSP_DESCRICAO_USUAL_EXAME') is not null then
		if(filtro!=null && filtro.getExameSolicitacaoInfo()!=null){
			criteria.add(Restrictions.eq("exa."+AelExames.Fields.SIGLA.toString(), filtro.getExameSolicitacaoInfo().getId().getSigla()));
		}

		//IF  NAME_IN('QMS$CTRL.CRITERIO_CONSULTA') is not null then
		if(filtro!=null && filtro.getRestricao()!=null && filtro.getServidorLogado()!=null){
			addRestricao(criteria, filtro.getRestricao(), filtro.getServidorLogado().getId().getMatricula(), filtro.getServidorLogado().getId().getVinCodigo());
		}

		//P_SITUACAO_CANCELADO
		if(filtro.getIndMostraCanceladosInfo() != null && filtro.getIndMostraCanceladosInfo().equals(DominioSimNao.N)){
			criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), filtro.getpSituacaoCancelado()));
		}

		//P_SITUACAO_PEDENTE
		if(filtro.getpSituacaoPendente() != null){
			criteria.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), filtro.getpSituacaoPendente()));
		}

		StringBuffer sqlDocs = new StringBuffer(250);
		sqlDocs.append("	(select 	count(*) as EXISTENTES ");
		sqlDocs.append("							from 	agh.ael_doc_resultado_exames doc ");
		sqlDocs.append("							where	doc.ise_soe_seq = soe2_.seq ");
		sqlDocs.append("							and	doc.ise_seqp = {alias}.seqp ");
		sqlDocs.append("							and	(doc.ind_anulacao_doc = 'N' or doc.dthr_anulacao_doc is null)) as EXISTENTES ");
		
		//campos do resultado 
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(" max({alias}.DTHR_PROGRAMADA) as DTHR_PROGRAMADA",
					 new String[] { "DTHR_PROGRAMADA" }, new Type[] { TimestampType.INSTANCE }))

				.add(Projections.sqlProjection(" max(DTHR_EVENTO) as DTHR_EVENTO",
					new String[] { "DTHR_EVENTO" }, new Type[] { TimestampType.INSTANCE }))

			.add(Projections.groupProperty(AelItemSolicitacaoExames.Fields.SEQP.toString()))
			.add(Projections.groupProperty("sis."+AelSitItemSolicitacoes.Fields.DESCRICAO.toString()))
			.add(Projections.groupProperty(AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString()))
			.add(Projections.groupProperty("exa."+AelExames.Fields.DESCRICAO_USUAL.toString()))
			.add(Projections.groupProperty("soe."+AelSolicitacaoExames.Fields.SEQ.toString()))
			.add(Projections.groupProperty("aip."+AipPacientes.Fields.NOME.toString()))
			.add(Projections.groupProperty("aip."+AipPacientes.Fields.PRONTUARIO.toString()))
			.add(Projections.groupProperty("atd."+AghAtendimentos.Fields.ORIGEM.toString()))
			.add(Projections.groupProperty(AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()))
			.add(Projections.sqlProjection(sqlDocs.toString(), new String[] { "EXISTENTES" }, new Type[] { IntegerType.INSTANCE }))
			.add(Projections.groupProperty("sis."+AelSitItemSolicitacoes.Fields.CODIGO.toString()))
		);

		//ordenação
		criteria.addOrder(Order.desc(AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()));
		criteria.addOrder(Order.asc(AelItemSolicitacaoExames.Fields.SEQP.toString()));

		List<Object[]> results = executeCriteria(criteria);
		for (Object[] record : results) {
			PesquisaExamesPacientesResultsVO exame = new PesquisaExamesPacientesResultsVO();

			exame.setDtHrProgramada((Date)record[0]);
			exame.setDtHrEvento((Date)record[1]);
			exame.setIseSeq((Short)record[2]);
			exame.setSituacaoItem((String)record[3]);
			exame.setTipoColeta(((DominioTipoColeta)record[4]).getDescricao());
			exame.setExame((String)record[5]);
			exame.setCodigoSoe((Integer)record[6]);
			exame.setPaciente((String)record[7]);
			exame.setProntuario((Integer)record[8]);
			exame.setOrigemAtendimento((DominioOrigemAtendimento)record[9]);
			exame.setExisteDocAnexado(((Integer)record[11])>0);
			exame.setSituacaoCodigo((String)record[12]);
			exames.add(exame);
		}

		return exames;
	}
	
	private void addRestricao(DetachedCriteria criteria, DominioRestricaoUsuario restricao, Integer matricula, Short vinCodigo){
		StringBuffer sql = new StringBuffer(180);
		sql.append("NOT EXISTS (SELECT 1 ");
		sql.append("			FROM 	agh.AEL_ITEM_SOLIC_CONSULTADOS ISC");
		sql.append("			WHERE 	ISC.ISE_SOE_SEQ = {alias}.SOE_SEQ ");
		sql.append("			AND		ISC.ISE_SEQP    = {alias}.SEQP ");

		if(DominioRestricaoUsuario.NC.equals(restricao)){
			/*	parametros para a subquery	*/
			Object[] values = {};
			Type[] types = {};
			sql.append(" ) ");
			criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));

		}else if(DominioRestricaoUsuario.NCU.equals(restricao)){
			sql.append("		AND		ISC.SER_MATRICULA	= ? ");
			sql.append("		AND		ISC.SER_VIN_CODIGO	= ? )");

			/*	parametros para a subquery	*/
			Object[] values = {matricula, vinCodigo};
			Type[] types = {IntegerType.INSTANCE, ShortType.INSTANCE};
			criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));
		}
	}

	public PesquisaExamesPacientesResultsVO buscaDadosSolicitacaoPorSoeSeq(Integer soeSeq) throws BaseException {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);

		criteria.createCriteria(AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "atv", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "aip", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("atv."+AelAtendimentoDiversos.Fields.PACIENTE.toString(), "aip2", JoinType.LEFT_OUTER_JOIN);

		//campos do resultado
		criteria.setProjection(Projections.projectionList()
			.add(Projections.distinct(Projections.property(AelSolicitacaoExames.Fields.SEQ.toString()))) //0
			.add(Projections.property("aip."+AipPacientes.Fields.NOME.toString())) //1
			.add(Projections.property("aip."+AipPacientes.Fields.PRONTUARIO.toString())) //2
			.add(Projections.property("atd."+AghAtendimentos.Fields.ORIGEM.toString())) //3
			.add(Projections.property(AelSolicitacaoExames.Fields.INFORMACOES_CLINICAS.toString())) //4
			.add(Projections.property("aip."+AipPacientes.Fields.CODIGO.toString())) //5
			
			.add(Projections.property("aip2."+AipPacientes.Fields.NOME.toString())) //6
			.add(Projections.property("aip2."+AipPacientes.Fields.PRONTUARIO.toString())) //7
			.add(Projections.property("aip2."+AipPacientes.Fields.CODIGO.toString())) //8
		);

		if(soeSeq != null && soeSeq > 0){
			criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.SEQ.toString(), soeSeq));
		}

		Object[] record = (Object[])executeCriteriaUniqueResult(criteria);

		PesquisaExamesPacientesResultsVO exame = new PesquisaExamesPacientesResultsVO();
		if(record != null){
			exame.setCodigoSoe((Integer)record[0]);

			if(record[1]!=null){
				exame.setPaciente((String)record[1]);
			}else if(record[6]!=null){
				exame.setPaciente((String)record[6]);
			}
			if(record[2]!=null){
				exame.setProntuario((Integer)record[2]);
			}else if(record[7]!=null){
				exame.setProntuario((Integer)record[7]);
			}
			if(record[3]!=null){
				exame.setOrigemAtendimento((DominioOrigemAtendimento)record[3]);
			}
			if(record[4]!=null){
				exame.setInfoClinica((String)record[4]);
			}
			if(record[5]!=null){
				exame.setCodPaciente((Integer)record[5]);
			}else if(record[8]!=null){
				exame.setCodPaciente((Integer)record[8]);
			}
		}
		return exame;
	}
	
	public List<PesquisaExamesPacientesResultsVO> buscaDadosItensSolicitacaoPorSoeSeq(Integer soeSeq, Short unfSeq) throws BaseException {
		List<PesquisaExamesPacientesResultsVO> exames = new ArrayList<PesquisaExamesPacientesResultsVO>();

		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);

		criteria.createCriteria(AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ise", JoinType.INNER_JOIN);
		criteria.createCriteria("ise."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "sit", JoinType.INNER_JOIN);
		criteria.createCriteria("ise."+AelItemSolicitacaoExames.Fields.AEL_MOTIVO_CANCELA_EXAMES.toString(), "moc", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("ise."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa", JoinType.INNER_JOIN);
		criteria.createCriteria("ise."+AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "man", JoinType.INNER_JOIN);
		if(unfSeq!=null){
			criteria.createCriteria("ise."+AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.LEFT_OUTER_JOIN);
		}
		criteria.add(Restrictions.eq(AelSolicitacaoExames.Fields.SEQ.toString(), soeSeq));
		
		/*SubQuery*/
		if(unfSeq!=null){
			DetachedCriteria subCri = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
	
			StringBuffer subSql = new StringBuffer(" {alias}.unf_seq in (	select  unf1.seq");                                    
			subSql.append("													from    agh.agh_unidades_funcionais unf1");
			subSql.append("													where   (unf1.unf_seq = ? or unf1.seq = ?)");
			subSql.append("													and     exists(	");
			subSql.append("																	select  1"); 
			subSql.append("																	from    agh.agh_caract_unid_funcionais");
			subSql.append("																	where   unf_seq = unf1.seq");
			subSql.append("																	and     caracteristica = ?");
			subSql.append("																  )");
			subSql.append("											    )");
	
			Object[] values = {unfSeq, unfSeq, ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS.getDescricao()};
			Type[] types = {ShortType.INSTANCE, ShortType.INSTANCE, StringType.INSTANCE};
	
			subCri.add(Restrictions.sqlRestriction(subSql.toString(), values, types));
	
			subCri.setProjection(Projections.projectionList()
					.add(Projections.distinct(Projections.property(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()))));
	
			criteria.add(Restrictions.or(Restrictions.eq("unf."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq), 
					Property.forName("unf."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).in(subCri)));
		}
		
		//campos do resultado
		criteria.setProjection(Projections.projectionList()
			.add(Projections.distinct(Projections.property("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString())))
			.add(Projections.property("sit."+AelSitItemSolicitacoes.Fields.DESCRICAO.toString()))
			.add(Projections.property("moc."+AelMotivoCancelaExames.Fields.DESCRICAO.toString()))
			.add(Projections.property("exa."+AelExames.Fields.DESCRICAO.toString()))
			.add(Projections.property("man."+AelExames.Fields.DESCRICAO.toString()))
			.add(Projections.property("sit."+AelSitItemSolicitacoes.Fields.CODIGO.toString()))
		);
		
		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString()));
		
		List<Object[]> results = executeCriteria(criteria);
		for (Object[] record : results) {
			PesquisaExamesPacientesResultsVO exame = new PesquisaExamesPacientesResultsVO();

			exame.setIseSeq((Short)record[0]);
			exame.setSituacaoItem((String)record[1]);
			exame.setMotivoCancelamentoItem((String)record[2]);
			exame.setExame((String)record[3]);
			exame.setMaterialAnalise((String)record[4]);
			exame.setSituacaoCodigo((String)record[5]);
			exames.add(exame);
		}

		return exames;
	}
	
	public List<Short> buscaExamesSolicitadosOrdenados(Integer solicitacao, List<Short> seqps) {
		List<Short> retornoSeqps = new ArrayList<Short>();

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);

		criteria.createCriteria(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "exa", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.INNER_JOIN);


		if(solicitacao!=null && solicitacao > 0){
			criteria.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.SEQ.toString(), solicitacao));
		}
		
		if(seqps!=null && !seqps.isEmpty()){
			criteria.add(Restrictions.in(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqps));
		}

		//campos do resultado 
		criteria.setProjection(Projections.projectionList()
			.add(Projections.groupProperty(AelItemSolicitacaoExames.Fields.SEQP.toString()))
			.add(Projections.groupProperty("unf."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()))
			.add(Projections.groupProperty("exa."+AelExames.Fields.DESCRICAO_USUAL.toString()))
		);

		//ordenação
		criteria.addOrder(Order.asc("unf."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("exa."+AelExames.Fields.DESCRICAO_USUAL.toString()));

		List<Object[]> results = executeCriteria(criteria);
		for (Object[] record : results) {
			retornoSeqps.add((Short)record[0]);
		}

		return retornoSeqps;
	}
	

	
	
	public List<Short> buscaExamesSolicitadosOrdenadosHist(Integer solicitacao, List<Short> seqps) {
		List<Short> retornoSeqps = new ArrayList<Short>();

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicExameHist.class);

		criteria.createCriteria(AelItemSolicExameHist.Fields.AEL_EXAMES.toString(), "exa", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.INNER_JOIN);
		criteria.createCriteria(AelItemSolicExameHist.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.INNER_JOIN);


		if(solicitacao!=null && solicitacao > 0){
			criteria.add(Restrictions.eq("soe."+AelSolicitacaoExamesHist.Fields.SEQ.toString(), solicitacao));
		}
		
		if(seqps!=null && !seqps.isEmpty()){
			criteria.add(Restrictions.in(AelItemSolicExameHist.Fields.SEQP.toString(), seqps));
		}

		//campos do resultado 
		criteria.setProjection(Projections.projectionList()
			.add(Projections.groupProperty(AelItemSolicExameHist.Fields.SEQP.toString()))
			.add(Projections.groupProperty("unf."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()))
			.add(Projections.groupProperty("exa."+AelExames.Fields.DESCRICAO_USUAL.toString()))
		);

		//ordenação
		criteria.addOrder(Order.asc("unf."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("exa."+AelExames.Fields.DESCRICAO_USUAL.toString()));

		List<Object[]> results = executeCriteria(criteria);
		for (Object[] record : results) {
			retornoSeqps.add((Short)record[0]);
		}

		return retornoSeqps;
	}
	
}