package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaDispensacaoMdtosPai;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.EceKit;
import br.gov.mec.aghu.model.EceKitComponente;
import br.gov.mec.aghu.model.EceViaDoKit;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MptItemPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigCalculoAtdCIDS;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoDetalheConsumo;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.CalculoAtendimentoPacienteVO;
import br.gov.mec.aghu.sig.custos.vo.ContagemOrtesesProtesesVO;
import br.gov.mec.aghu.sig.custos.vo.CuidadosPrescricaoDialiseVO;
import br.gov.mec.aghu.sig.custos.vo.MedicamentosDialiseVO;
import br.gov.mec.aghu.sig.custos.vo.SigAtendimentoVO;
import br.gov.mec.aghu.sig.custos.vo.SigCalculoAtendimentoPacienteVO;

public class SigCalculoAtdPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtdPaciente> {

	private static final long serialVersionUID = -8251997420595031262L;
	
	public static final int C1 = 2;
	public static final int C3 = 3;
	public static final int C4 = 4;
	public static final int C7 = 7;
	public static final int C9 = 9;
	public static final int C10 = 10;
	
	
	public void removerPorProcessamento(Integer idProcessamentoCusto){
		StringBuilder sql = new StringBuilder(50);
		sql.append(" DELETE ").append(SigCalculoAtdPaciente.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		javax.persistence.Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();
	}

	/**
	 * Busca todas altas de atendimentos que já estão no calculo mas não possuem CDIS.
	 * 
	 * @author rmalvezzi
	 * @param processamentoCusto	Porcessamento Atual.
	 * @return 						Lista de {@link SigCalculoAtdPaciente} que satisfaz os filtros
	 */
	public List<SigCalculoAtdPaciente> buscarAltasAtendimentoInternacao(SigProcessamentoCusto processamentoCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createCriteria("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));
		criteria.add(Restrictions.isNotNull("atd." + AghAtendimentos.Fields.DTHR_FIM.toString()));
		DetachedCriteria criteriaIn = DetachedCriteria.forClass(SigCalculoAtdCIDS.class, "cid");
		criteriaIn.setProjection(Projections.property(SigCalculoAtdCIDS.Fields.CALCULO_ATD_PACIENTE.toString()));
		criteria.add(Subqueries.propertyNotIn("cac." + SigCalculoAtdPaciente.Fields.SEQ.toString(), criteriaIn));
		return executeCriteria(criteria);
	}

	/**
	 * Método que cria um criteria que contem todas as  internações de pacientes. 
	 * 
	 * @author rmalvezzi
	 * @param processamento 		Porcessamento Atual.
	 * @return						{@link DetachedCriteria} contendo todas as internações.
	 */	
	private DetachedCriteria criarCriteriaParaTodasInternacoes(SigProcessamentoCusto processamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias(SigCalculoAtdPaciente.Fields.INTERNACAO.toString(), SigCalculoAtdPaciente.Fields.INTERNACAO.toString(), JoinType.LEFT_OUTER_JOIN);
		if (processamento != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), processamento));
		}
		DetachedCriteria criteriaIn = DetachedCriteria.forClass(SigCalculoAtdPermanencia.class, "ccp");
		criteriaIn.setProjection(Projections.property(SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString()));
		criteria.add(Subqueries.propertyNotIn("cac." + SigCalculoAtdPaciente.Fields.SEQ.toString(), criteriaIn));
		return criteria;
	}

	/**
	 * Método que executa o criteria e retorna lista que contem todas as  internações de pacientes. 
	 * 
	 * @author rmalvezzi
	 * @param processamento 		Porcessamento Atual.
	 * @return						List<{@link SigCalculoAtdPaciente}> contendo todas as internações.
	 */
	public List<SigCalculoAtdPaciente> buscarTodasInternacoes(SigProcessamentoCusto processamento) {
		return executeCriteria(this.criarCriteriaParaTodasInternacoes(processamento));
	}

	/**
	 * Método que executa a busca de pacientes que tiveram alta.
	 * 
	 * @author rmalvezzi
	 * @param processamento			Porcessamento Atual.
	 * @return						List<{@link SigCalculoAtdPaciente}> com alta.
	 */
	public List<SigCalculoAtdPaciente> buscarAltasInternacao(SigProcessamentoCusto processamento) {
		DetachedCriteria criteria = this.criarCriteriaParaTodasInternacoes(processamento);
		criteria.createCriteria("cac." + SigCalculoAtdPaciente.Fields.LISTA_CALCULOS_CIDS.toString(), "cci", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("cci." + SigCalculoAtdCIDS.Fields.PRINCIPAL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.in("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), Arrays.asList(DominioSituacaoCalculoPaciente.IA, DominioSituacaoCalculoPaciente.A)));
		return executeCriteria(criteria);

	}

	/**
	 * Método que busca os kits de medicamentos 
	 * @author rmalvezzi
	 * @param procedHospInternos
	 * @param pediatrico
	 * @param viaAdministracao
	 * @return
	 */
	public List<EceKitComponente> buscarKitsMedicamentos(Integer phiSeq, Boolean pediatrico, String siglaViaAdministracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EceKitComponente.class, "kco");
		criteria.createCriteria("kco." + EceKitComponente.Fields.ECE_KITS.toString(), "kts", JoinType.INNER_JOIN);
		criteria.createCriteria("kco." + EceKitComponente.Fields.FAT_PROCED_HOSP_INTERNOS.toString(), "phi", JoinType.INNER_JOIN);
		criteria.createCriteria("kco." + EceKitComponente.Fields.ECE_VIA_DO_KITS.toString(), "vdk", JoinType.INNER_JOIN);
		criteria.createCriteria("phi." + FatProcedHospInternos.Fields.MATERIAIS.toString(), "mat", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("kts." + EceKit.Fields.SITUACAO.toString(), "A"));
		criteria.add(Restrictions.eq("kts." + EceKit.Fields.FAT_PROCED_HOSP_INTERNOS_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq("kts." + EceKit.Fields.PEDIATRICO.toString(), (pediatrico ? "S" : "N")));
		if(siglaViaAdministracao != null){
			criteria.add(Restrictions.eq("vdk." + EceViaDoKit.Fields.AFA_VIA_ADMINISTRACAO_SIGLA.toString(), siglaViaAdministracao));
		}
		return executeCriteria(criteria);
	}

	/**
	 * Consulta Busca cálculo atendimento do paciente do documento, utilziada para identificar 
	 * se o atendimento do paciente já está incluso na tabela SIG_CALCULO_ATD_PACIENTES.
	 * 
	 * @author rmalvezzi
	 * 
	 * @param atdSeq					Atendimento do Paciente.
	 * @param processamentoCusto		Processamento Atual.
	 * @param internacao				Internação do Paciente.
	 * @return							Lista de SigCalculoAtdPaciente que satisfaz os filtros.
	 */
	public List<SigCalculoAtdPaciente> buscarCalculoAtendimentoPaciente(Integer atdSeq, SigProcessamentoCusto processamentoCusto, AinInternacao internacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class);
		criteria.add(Restrictions.eq(SigCalculoAtdPaciente.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));
		if(internacao != null){
			criteria.add(Restrictions.eq(SigCalculoAtdPaciente.Fields.INTERNACAO.toString(), internacao));
		}
		else{
			criteria.add(Restrictions.isNull(SigCalculoAtdPaciente.Fields.INTERNACAO.toString()));
		}
		return this.executeCriteria(criteria);
	}
		
	public List<EceViaDoKit> buscaKitMedicamento(Integer phiSeq, String vadSigla, String indPacPediatrico){
		DetachedCriteria criteria = DetachedCriteria.forClass(EceViaDoKit.class, "vdk");
		criteria.createAlias("vdk."+EceViaDoKit.Fields.ECE_KIT_COMPONENTEES.toString(), "kco");
		criteria.createAlias("kco."+EceKitComponente.Fields.ECE_KITS.toString(), "kts");
		criteria.createCriteria("kco." + EceKitComponente.Fields.FAT_PROCED_HOSP_INTERNOS.toString(), "phi");
		criteria.createCriteria("phi." + FatProcedHospInternos.Fields.MATERIAIS.toString(), "mat");
		criteria.add(Restrictions.eq("kts."+EceKit.Fields.SITUACAO.toString(), "A"));
		criteria.add(Restrictions.eq("kts." + EceKit.Fields.FAT_PROCED_HOSP_INTERNOS_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq("vdk."+EceViaDoKit.Fields.AFA_VIA_ADMINISTRACAO_SIGLA, vadSigla));
		criteria.add(Restrictions.eq("kts." + EceKit.Fields.PEDIATRICO.toString(), indPacPediatrico));
		return this.executeCriteria(criteria);
	}
	/*
	 * Utiliza SQL Nativo devida a uma impossibilidade de executar a query com HQL ou Criteria quando houver LEFT JOINS
	 * entre entidades sem ligação direta (mapeada) pelo hibernate;
	 * Vide MbcBloqSalaCirurgicaDAO.getSqlPesquisarBloqSalaCirurgica();
	 */
	public List<CuidadosPrescricaoDialiseVO> buscarCuidadosPrescricaoDialise(Date dtInicio, Date dtFim){
		SQLQuery q = this.createSQLQuery(getQueryCuidadosPrescricaoDialise());
		q.setDate("dtInicioProcessamento", dtInicio);
		q.setDate("dtFimProcessamento", dtFim);
		@SuppressWarnings("unchecked")
		List<CuidadosPrescricaoDialiseVO> listaVO = q
				.addScalar("atdPaciente",  IntegerType.INSTANCE)
				.addScalar("codPaciente",IntegerType.INSTANCE)
				.addScalar("tratamentosTerapeuticosSeq",IntegerType.INSTANCE)
				.addScalar("unidadeAtendimentoSeq",IntegerType.INSTANCE)
				.addScalar("codCentroCusto",IntegerType.INSTANCE)
				.addScalar("phiSeq",IntegerType.INSTANCE)
				.addScalar("ocvSeq",IntegerType.INSTANCE)
				.addScalar("dataPrevisaoExecucao",DateType.INSTANCE)
				.addScalar("cuidadoSeq",IntegerType.INSTANCE)
				.addScalar("frequenciaAprazamentoSeq",ShortType.INSTANCE)
				.addScalar("frequencia",ShortType.INSTANCE)
				.addScalar("quantidadeUnidade",IntegerType.INSTANCE)
				.addScalar("unidadeMedidaSeq",IntegerType.INSTANCE)
				.addScalar("quantidadeCuidados",IntegerType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(CuidadosPrescricaoDialiseVO.class)).list();
		return listaVO;
	}
	
	private String getQueryCuidadosPrescricaoDialise(){
		StringBuilder sql = new StringBuilder(1700);
		sql.append("SELECT ")
		.append("ATD.SEQ AS atdPaciente, ")
		.append("ATD.PAC_CODIGO AS codPaciente, ")
		.append("ATD.TRP_SEQ AS tratamentosTerapeuticosSeq, ")
	    .append("ATD.UNF_SEQ AS unidadeAtendimentoSeq, ")   
	    .append("AGP.UNF_SEQ AS unidadeAgendaSeq, ")   
	    .append("UNF.CCT_CODIGO AS codCentroCusto, ")   
	    .append("PHI.SEQ AS phiSeq, ")   
	    .append("OCV.SEQ AS ocvSeq, ")   
	    .append("MPT.DT_PREV_EXECUCAO AS dataPrevisaoExecucao, ")
	    .append("PCO.CDU_SEQ AS cuidadoSeq, ")
	    .append("PCO.TFQ_SEQ AS frequenciaAprazamentoSeq, ")
	    .append("PCO.FREQUENCIA AS frequencia, ")
	    .append("PCO.QUANTIDADE_UNIDADE AS quantidadeUnidade, ")
	    .append("PCO.UMM_SEQ AS unidadeMedidaSeq, ")
	    .append("COUNT(*) AS quantidadeCuidados ")
	    .append("FROM ")
	    .append("AGH.AGH_ATENDIMENTOS ATD, ")
	    .append("AGH.MPT_PRESCRICAO_PACIENTES MPT, ")
	    .append("AGH.MPT_PRESCRICAO_CUIDADOS PCO, ")
	    .append("AGH.MPT_AGENDA_PRESCRICOES AGP, ")
	    .append("AGH.AGH_UNIDADES_FUNCIONAIS UNF, ")
	    .append("AGH.FAT_PROCED_HOSP_INTERNOS PHI ")
	    .append("LEFT JOIN AGH.SIG_OBJETO_CUSTO_PHIS OCP ON PHI.SEQ = OCP.PHI_SEQ ")
	    .append("LEFT JOIN AGH.SIG_OBJETO_CUSTO_VERSOES OCV ON OCP.OCV_SEQ = OCV.SEQ ")
	    .append("WHERE  ")
	    .append("(ATD.IND_TIPO_TRATAMENTO = 27 OR ATD.TPT_SEQ = 19) ")
	    .append("AND ATD.TRP_SEQ IS NOT NULL ")
	    .append("AND ATD.SEQ = MPT.ATD_SEQ ")
	    .append("AND MPT.DT_PREV_EXECUCAO BETWEEN :dtInicioProcessamento AND :dtFimProcessamento ")
	    .append("AND MPT.ATD_SEQ = AGP.PTE_ATD_SEQ ")
	    .append("AND MPT.SEQ = AGP.PTE_SEQ ")
	    .append("AND AGP.IND_SITUACAO =  'A' ")
	    .append("AND AGP.UNF_SEQ = UNF.SEQ ")
	    .append("AND MPT.ATD_SEQ = PCO.PTE_ATD_SEQ ")
	    .append("AND MPT.SEQ = PCO.PTE_SEQ ")
	    .append("AND PCO.IND_SITUACAO_ITEM IN ('V','A','E') ")
	    .append("AND PCO.CDU_SEQ = PHI.CDU_SEQ ")
		.append("GROUP BY ")
	    .append("ATD.PAC_CODIGO, ATD.SEQ, ATD.TRP_SEQ, ATD.UNF_SEQ, AGP.UNF_SEQ, UNF.CCT_CODIGO,")
	    .append("PHI.SEQ, OCV.SEQ, MPT.DT_PREV_EXECUCAO, PCO.CDU_SEQ, PCO.TFQ_SEQ, PCO.FREQUENCIA,")
		.append("PCO.QUANTIDADE_UNIDADE, PCO.UMM_SEQ ")
		.append("ORDER BY ")
		.append("MPT.DT_PREV_EXECUCAO ASC, ATD.PAC_CODIGO, ATD.SEQ, ATD.TRP_SEQ, ATD.UNF_SEQ,")
		.append("AGP.UNF_SEQ, UNF.CCT_CODIGO, PHI.SEQ, OCV.SEQ, PCO.CDU_SEQ, PCO.TFQ_SEQ,")  
	    .append("PCO.FREQUENCIA, PCO.QUANTIDADE_UNIDADE, PCO.UMM_SEQ");   
		return sql.toString();
	}
	
	/**
	 * Retorna todos os insumos associados à npt, bolsas, seringas e dispensações de quimioterapias consumidas por pacientes no mês de processamento.
	 */
	public List<SigCalculoAtendimentoPacienteVO> buscarSigCalculoAtendimentoPacienteVOPorProcessamentoECodigoCentroCusto(Integer pmuSeq, 
			Integer centroCustoCodigo, int tipoConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac."+SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cpp."+SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cca."+SigCalculoAtdConsumo.Fields.CALCULO_DETALHE_CONSUMO, "cdc", JoinType.LEFT_OUTER_JOIN);
		//WHERE
		criteria.add(Restrictions.eq("cdc."+SigCalculoDetalheConsumo.Fields.IND_EXTERNO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO_SEQ.toString(), pmuSeq));
		criteria.add(Restrictions.eq("cca."+SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString(), centroCustoCodigo));
		ProjectionList projection;
		if(C3 == tipoConsulta){
			projection = Projections
			.projectionList()
			.add(Projections.sum("cdc."+SigCalculoDetalheConsumo.Fields.VALOR_PREVISTO.toString()).as(SigCalculoAtendimentoPacienteVO.Fields.VALOR_PREVISTO.toString()))
			.add(Projections.groupProperty("cdc."+SigCalculoDetalheConsumo.Fields.CALCULO_ATIVIDADE_CONSUMO_SEQ.toString()).as(SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_ATD_CONSUMO_SEQ.toString()));
		} else if(C4 == tipoConsulta){
			projection = Projections
			.projectionList()
			.add(Projections.sum("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_PREVISTO.toString()).as(SigCalculoAtendimentoPacienteVO.Fields.QUANTIDADE_PREVISTO_TOTAL.toString()))
			.add(Projections.groupProperty("cdc."+SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()).as(SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_DETALHE_CONSUMO_SCO_MATERIAL_SEQ.toString()));
		} else if(C7 == tipoConsulta){
			criteria.add(Restrictions.or(Restrictions.eq("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString(), BigDecimal.ZERO), Restrictions.eq("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString(), null)));
			projection = Projections
			.projectionList()
			.add(Projections.sum("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_PREVISTO.toString()).as(SigCalculoAtendimentoPacienteVO.Fields.QUANTIDADE_PREVISTO_TOTAL.toString()))
			.add(Projections.groupProperty("cdc."+SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()).as(SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_DETALHE_CONSUMO_SCO_MATERIAL_SEQ.toString()));
		} else if(C9 == tipoConsulta){
			criteria.add(Restrictions.or(Restrictions.eq("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString(), BigDecimal.ZERO), Restrictions.eq("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString(), null)));
			projection = Projections
			.projectionList()
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_DETALHE_CONSUMO_SCO_MATERIAL_SEQ.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_PREVISTO.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_DETALHE_CONSUMO_QUANTIDADE_PREVISTO.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.VALOR_PREVISTO.toString()), SigCalculoAtendimentoPacienteVO.Fields.VALOR_PREVISTO.toString());
		} else if(C10 == tipoConsulta){
			projection = Projections
			.projectionList()
			.add(Projections.sum("cdc."+SigCalculoDetalheConsumo.Fields.VALOR_CONSUMIDO.toString()).as(SigCalculoAtendimentoPacienteVO.Fields.VALOR_CONSUMIDO.toString()))
			.add(Projections.groupProperty("cdc."+SigCalculoDetalheConsumo.Fields.CALCULO_ATIVIDADE_CONSUMO_SEQ.toString()).as(SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_ATD_CONSUMO_SEQ.toString()));
		} else {
			projection = Projections
			.projectionList()
			.add(Projections.property("cca."+SigCalculoAtdConsumo.Fields.SEQ.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_ATD_CONSUMO_SEQ.toString())
			.add(Projections.property("cca."+SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA_SEQ.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_ATD_CONSUMO_SIG_CALCULO_ATD_PERMANENCIAS_SEQ.toString())
			.add(Projections.property("cca."+SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_ATD_CONSUMO_QUANTIDADE.toString())
			.add(Projections.property("cca."+SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_ATD_CONSUMO_FCC_CENTRO_CUSTRO_SEQ.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_DETALHE_CONSUMO_SCO_MATERIAL_SEQ.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_PREVISTO.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_DETALHE_CONSUMO_QUANTIDADE_PREVISTO.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_DEBITADO.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_DETALHE_CONSUMO_QUANTIDADE_DEBITADO.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_DETALHE_CONSUMO_QUANTIDADE_DEBITADO.toString());
		}
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(SigCalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca Utilizações de Insumo para Ajuste de Valores
	 */
	public List<SigCalculoAtendimentoPacienteVO> buscarValoresSigCalculoDetalhePaciente(Integer pmuSeq, 
			Integer centroCustoCodigo, Integer materialCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac."+SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cpp."+SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cca."+SigCalculoAtdConsumo.Fields.CALCULO_DETALHE_CONSUMO, "cdc", JoinType.LEFT_OUTER_JOIN);
		//WHERE
		criteria.add(Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO_SEQ.toString(), pmuSeq));
		criteria.add(Restrictions.eq("cca."+SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString(), centroCustoCodigo));
		criteria.add(Restrictions.eq("cdc."+SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString(), materialCodigo));
		ProjectionList projection = Projections
			.projectionList()
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.SEQ.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_DETALHE_CONSUMO_SEQ.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_PREVISTO.toString()), SigCalculoAtendimentoPacienteVO.Fields.QUANTIDADE_PREVISTO.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_DEBITADO.toString()), SigCalculoAtendimentoPacienteVO.Fields.QUANTIDADE_DEBITADO.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.VALOR_PREVISTO.toString()), SigCalculoAtendimentoPacienteVO.Fields.VALOR_PREVISTO.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.VALOR_DEBITADO.toString()), SigCalculoAtendimentoPacienteVO.Fields.VALOR_DEBITADO.toString());
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(SigCalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca Utilizações de Insumo para Ajuste de Valores
	 */
	public List<SigCalculoAtendimentoPacienteVO> buscarValoresSigCalculoDetalhePaciente(Integer pmuSeq, 
			Integer centroCustoCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac."+SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cpp."+SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cca."+SigCalculoAtdConsumo.Fields.CALCULO_DETALHE_CONSUMO, "cdc", JoinType.LEFT_OUTER_JOIN);
		//WHERE
		criteria.add(Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO_SEQ.toString(), pmuSeq));
		criteria.add(Restrictions.eq("cca."+SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString(), centroCustoCodigo));
		criteria.add(Restrictions.ge("cdc."+SigCalculoDetalheConsumo.Fields.VALOR_CONSUMIDO.toString(), BigDecimal.ZERO));
		ProjectionList projection = Projections
			.projectionList()
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()), SigCalculoAtendimentoPacienteVO.Fields.SIG_CALCULO_DETALHE_CONSUMO_SCO_MATERIAL_SEQ.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.VALOR_CONSUMIDO.toString()), SigCalculoAtendimentoPacienteVO.Fields.VALOR_CONSUMIDO.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString()), SigCalculoAtendimentoPacienteVO.Fields.QUANTIDADE_CONSUMIDO.toString());
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(SigCalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}

	public List<ContagemOrtesesProtesesVO> buscarOrtesesEProtesesUtilizadas(Integer pmuSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac."+SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cpp."+SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cca."+SigCalculoAtdConsumo.Fields.CALCULO_DETALHE_CONSUMO.toString(), "cdc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cca."+SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc", JoinType.LEFT_OUTER_JOIN);
		//WHERE
		criteria.add(Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO_SEQ.toString(), pmuSeq));
		criteria.add(Restrictions.eq("ctc."+SigCategoriaConsumos.Fields.IND_CONTAGEM.toString(), DominioIndContagem.OP));
		ProjectionList projection = Projections
			.projectionList()
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.SEQ.toString()), ContagemOrtesesProtesesVO.Fields.CDC_SEQ.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.CALCULO_ATIVIDADE_CONSUMO_SEQ.toString()), ContagemOrtesesProtesesVO.Fields.CCA_SEQ.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_SEQ.toString()), ContagemOrtesesProtesesVO.Fields.PHI_SEQ.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()), ContagemOrtesesProtesesVO.Fields.MAT_CODIGO.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.IDENTIFICADOR.toString()), ContagemOrtesesProtesesVO.Fields.RMP_SEQ.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_PREVISTO.toString()), ContagemOrtesesProtesesVO.Fields.QTED_PREVISTO.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_DEBITADO.toString()), ContagemOrtesesProtesesVO.Fields.QTDE_DEBITADO.toString())
			.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString()), ContagemOrtesesProtesesVO.Fields.QTDE_CONSUMIDO.toString())
			.add(Projections.property("cca."+SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString()), ContagemOrtesesProtesesVO.Fields.CCT_CODIGO.toString());
			
		
		criteria.setProjection(projection);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ContagemOrtesesProtesesVO.class));
		
		return executeCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<SigAtendimentoVO> buscarMedicamentosDispensadosSemPreparo(Integer tipoTratamento,
			Date dataInicio, Date dataFim) {
		StringBuffer sql = new StringBuffer(" select ")
		   .append(" 	atd."+AghAtendimentos.Fields.SEQ.toString()+" as atdPaciente,  ")
	       .append(" 	atd."+AghAtendimentos.Fields.PAC_CODIGO.toString()+" as pacCodigo, ")
	       .append(" 	atd."+AghAtendimentos.Fields.TRP_SEQ.toString()+" as trpSeq, ")
	       .append(" 	mpt." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString() + " as dtPrevExecucao, ")
	       .append(" 	mdt."+AfaDispensacaoMdtosPai.Fields.UNID_SOLICITANTE_SEQ.toString()+" as unfSeqSolicitante, ")
	       .append(" 	unf." + AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString() + " as centroCusto, ")
	       .append(" 	mdt."+AfaDispensacaoMdtosPai.Fields.MED_MAT_CODIGO.toString()+" as medicamento, ")
	       .append(" 	phi."+FatProcedHospInternosPai.Fields.SEQ.toString()+" as phiSeq, ")
	       .append(" 	sum(mdt."+AfaDispensacaoMdtosPai.Fields.QTDE_SOLICITADA.toString()+") as totalSolic, ")     
	       .append(" 	sum(mdt." + AfaDispensacaoMdtosPai.Fields.QTDE_DISPENSADA.toString() + ") as totalDisp ")
	       .append(" from ")
	       .append("  	").append(AghAtendimentos.class.getSimpleName()).append("  atd, ")
	       .append("  	").append(AfaDispensacaoMdtos.class.getSimpleName()).append("  mdt, ")
	       .append("  	").append(ScoMaterial.class.getSimpleName()).append("  mat, ")
	       .append("  	").append(FatProcedHospInternos.class.getSimpleName()).append("  phi, ")
	       .append("  	").append(AghUnidadesFuncionais.class.getSimpleName()).append("  unf, ")
	       .append("  	").append(MptPrescricaoPaciente.class.getSimpleName()).append("  mpt, ")
	       .append("  	").append(MptPrescricaoMedicamento.class.getSimpleName()).append("  pmo, ")
	       .append("  	").append(MptItemPrescricaoMedicamento.class.getSimpleName()).append("  ipmo ")
	       //.append(" inner join mpt.").append(MptPrescricaoPaciente.Fields.MPT_PRESCRICAO_MEDICAMENTOS.toString()).append(" pmo ")
	       //.append(" inner join pmo.").append(MptPrescricaoMedicamento.Fields.ITENS_PRESCRICOES_MEDICAMENTOS.toString()).append(" ipmo ")
	       .append(" where ")
	       .append(" 	ipmo.").append(MptItemPrescricaoMedicamento.Fields.PMO_PTE_ATD_SEQ.toString()).append(" = ").append("mdt.").append(AfaDispensacaoMdtosPai.Fields.IMO_PMO_PTE_ATD_SEQ.toString())
	       .append(" 	and ipmo.").append(MptItemPrescricaoMedicamento.Fields.PMO_PTE_SEQ.toString()).append(" = ").append("mdt.").append(AfaDispensacaoMdtosPai.Fields.IMO_PMO_PTE_SEQ.toString())
	       .append(" 	and ipmo.").append(MptItemPrescricaoMedicamento.Fields.PMO_SEQ.toString()).append(" = ").append("mdt.").append(AfaDispensacaoMdtosPai.Fields.IMO_PMO_SEQ.toString())
	       .append(" 	and ipmo.").append(MptItemPrescricaoMedicamento.Fields.SEQP.toString()).append(" = ").append("mdt.").append(AfaDispensacaoMdtosPai.Fields.IMO_SEQP.toString())
	       .append("	and mpt.").append(MptPrescricaoPaciente.Fields.ATD_SEQ.toString()).append(" = ").append("atd.").append(AghAtendimentos.Fields.SEQ)
	       .append("	and mpt.").append(MptPrescricaoPaciente.Fields.ATD_SEQ.toString()).append(" = ").append("pmo.").append(MptPrescricaoMedicamento.Fields.PTE_ATD_SEQ.toString())
	       .append("	and mpt.").append(MptPrescricaoPaciente.Fields.SEQ.toString()).append(" = ").append("pmo.").append(MptPrescricaoMedicamento.Fields.PTE_SEQ.toString())
	       .append("	and pmo.").append(MptPrescricaoMedicamento.Fields.PTE_ATD_SEQ.toString()).append(" = ").append(" ipmo.").append(MptItemPrescricaoMedicamento.Fields.PMO_PTE_ATD_SEQ.toString())
	       .append("	and pmo.").append(MptPrescricaoMedicamento.Fields.PTE_SEQ.toString()).append(" = ").append(" ipmo.").append(MptItemPrescricaoMedicamento.Fields.PMO_PTE_SEQ.toString())
	       .append("	and pmo.").append(MptPrescricaoMedicamento.Fields.SEQ.toString()).append(" = ").append(" ipmo.").append(MptItemPrescricaoMedicamento.Fields.PMO_SEQ.toString())
	       .append("	and (mdt.").append(AfaDispensacaoMdtosPai.Fields.CPO_ITO_PTO_SEQ.toString()).append(" is null ")
		   .append("	and mdt.").append(AfaDispensacaoMdtosPai.Fields.CPO_ITO_SEQP.toString()).append(" is null ")
	       .append("	and mdt.").append(AfaDispensacaoMdtosPai.Fields.CPO_SEQP.toString()).append(" is null) ")
	       .append("  	and mdt.").append(AfaDispensacaoMdtosPai.Fields.IND_SITUACAO.toString()).append(" in ").append("( 'D', 'C', 'E' )")
	       .append("  	and mdt.").append(AfaDispensacaoMdtosPai.Fields.QTDE_DISPENSADA.toString()).append(" > 0 ")
	       .append("  	and pmo.").append(MptPrescricaoMedicamento.Fields.SITUACAO_ITEM.toString()).append(" = ").append(" 'V' ")
	       .append("	and mat.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = ").append("mdt.").append(AfaDispensacaoMdtosPai.Fields.MED_MAT_CODIGO.toString())
	       .append("	and mat.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = ").append("phi.").append(FatProcedHospInternos.Fields.MAT_CODIGO.toString())
	       .append("	and mdt.").append(AfaDispensacaoMdtosPai.Fields.UNID_SOLICITANTE_SEQ.toString()).append(" = ").append("unf.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL)
	       .append("  	and (atd.").append(AghAtendimentos.Fields.IND_TIPO_TRATAMENTO.toString()).append(" = ").append("29")
	       .append("  	or atd.").append(AghAtendimentos.Fields.TRP_SEQ.toString()).append(" = ").append(":tipoTratamento").append(')')
	       .append("	and atd.").append(AghAtendimentos.Fields.TRP_SEQ.toString()).append(" is not null")
	       .append("	and mpt.").append(MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString()).append(" between ").append(":dataInicio").append(" and ").append(":dataFim")
	       .append(" group by ")
	       .append(" 	atd.").append(AghAtendimentos.Fields.SEQ.toString())
	       .append(" 	,atd.").append(AghAtendimentos.Fields.PAC_CODIGO.toString())
	       .append("  	,atd.").append(AghAtendimentos.Fields.TRP_SEQ.toString())
	       .append("    ,mpt.").append(MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString())
	       .append("    ,mdt.").append(AfaDispensacaoMdtosPai.Fields.UNID_SOLICITANTE_SEQ.toString())	       
	       .append("  	,unf.").append(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString())
	       .append("  	,mdt.").append(AfaDispensacaoMdtosPai.Fields.MED_MAT_CODIGO.toString())
	       .append("  	,phi.").append(FatProcedHospInternosPai.Fields.SEQ.toString())
	       .append("  order by ")
	       .append("    mpt.").append(MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString()).append(" asc ")
	       .append(" 	,atd.").append(AghAtendimentos.Fields.SEQ.toString())
	       .append(" 	,atd.").append(AghAtendimentos.Fields.PAC_CODIGO.toString())
	       .append("  	,atd.").append(AghAtendimentos.Fields.TRP_SEQ.toString())
	       .append("    ,mdt.").append(AfaDispensacaoMdtosPai.Fields.UNID_SOLICITANTE_SEQ.toString())
	       .append("  	,unf.").append(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString())
	       .append("  	,mdt.").append(AfaDispensacaoMdtosPai.Fields.MED_MAT_CODIGO.toString())
	       .append("  	,phi.").append(FatProcedHospInternosPai.Fields.SEQ.toString());
		final Query query = this.createHibernateQuery(sql.toString());
		query.setParameter("tipoTratamento", tipoTratamento);
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("dataFim", dataFim);
		query.setResultTransformer(Transformers.aliasToBean(SigAtendimentoVO.class));
		return query.list();
			
	}
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeral(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), "ctcSeq")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), "agrupador")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), "descricao")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()), "ordemVisualizacao")
				.add(Projections.sum("cca." + SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()).as("quantidade"))				
				.add(Projections.sqlProjection("sum(coalesce(VLR_TOT_INSUMOS,0) + coalesce(VLR_TOT_PESSOAS,0) + coalesce(VLR_TOT_EQUIPAMENTOS,0) + coalesce(VLR_TOT_SERVICOS,0)) custoTotal",
                        new String[]{"custoTotal"}, new Type[]{BigDecimalType.INSTANCE}))
                .add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()));
		criteria.setProjection(projectionList);
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		criteria.addOrder(Order.asc("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeralCentroCusto(Integer prontuario, Integer pmuSeq, Integer codCentroCusto, Short espSeq, Integer atdSeq, Boolean isEspecialidade, List<Integer> seqCategorias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO.toString(), "cct");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "phi", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.LEFT_OUTER_JOIN);
		ProjectionList projectionList;
		if(isEspecialidade) {
			criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), "esp");	
			projectionList = Projections.projectionList()
					.add(Projections.sqlProjection("sum(coalesce(VLR_TOT_INSUMOS,0) + coalesce(VLR_TOT_PESSOAS,0) + coalesce(VLR_TOT_EQUIPAMENTOS,0) + coalesce(VLR_TOT_SERVICOS,0)) custoTotal",
							new String[]{"custoTotal"}, new Type[] {BigDecimalType.INSTANCE}))
							.add(Projections.groupProperty("esp." + AghEspecialidades.Fields.SEQ.toString()), "espSeq")
							.add(Projections.groupProperty("esp." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()), "nomeReduzido")
							.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), "agrupador")
							.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), "descricao")
							.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), "ctcSeq")
					.add(Projections.sum("cca." + SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()).as("quantidade"));
		} else {
			projectionList = Projections.projectionList()
					.add(Projections.property("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()), "phiDescricao")
					.add(Projections.property("obj." + SigObjetoCustos.Fields.NOME.toString()), "objNome")
					.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), "agrupador")
					.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), "descricao")
					.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()), "ordemVisualizacao")
					.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), "ctcSeq")
					.add(Projections.sum("cca." + SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()).as("quantidade"))				
					.add(Projections.sqlProjection("sum(coalesce(VLR_TOT_INSUMOS,0) + coalesce(VLR_TOT_PESSOAS,0) + coalesce(VLR_TOT_EQUIPAMENTOS,0) + coalesce(VLR_TOT_SERVICOS,0)) custoTotal",
	                        new String[]{"custoTotal"}, new Type[]{BigDecimalType.INSTANCE}))
					.add(Projections.groupProperty("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()))
					.add(Projections.groupProperty("obj." + SigObjetoCustos.Fields.NOME.toString()))
					.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()))
					.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()))
					.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()))
					.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()));
		}
		criteria.setProjection(projectionList);
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(codCentroCusto != null) {
			criteria.add(Restrictions.eq("cct." + FccCentroCustos.Fields.CODIGO.toString(), codCentroCusto));
		}
		if(espSeq != null) {
			criteria.add(Restrictions.eq("esp." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
			criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.SM));
		} else {
			criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		}
		if(seqCategorias != null && !seqCategorias.isEmpty()) {
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), seqCategorias));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	public List<CalculoAtendimentoPacienteVO> buscarCentrosCustoVisaoGeral(Integer prontuario, Integer pmuSeq, Integer codCentroCusto, List<Integer> categorias, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO.toString(), "cct");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "prc");
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.property("cct." + FccCentroCustos.Fields.CODIGO.toString()), "codCentroCusto")
				.add(Projections.property("cct." + FccCentroCustos.Fields.DESCRICAO.toString()), "descricaoCentroCusto");
		criteria.setProjection(Projections.distinct(projectionList));
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("prc." + SigProcessamentoCusto.Fields.SEQ.toString(), pmuSeq));
		}
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(codCentroCusto != null) {
			criteria.add(Restrictions.eq("cct." + FccCentroCustos.Fields.CODIGO.toString(), codCentroCusto));
		}
		if(categorias != null && !categorias.isEmpty()) {
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), categorias));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	public List<CalculoAtendimentoPacienteVO> buscarEspecialidades(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> seqCategorias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO.toString(), "cct");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), "esp");
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), "especialidade")
				.add(Projections.property("esp." + AghEspecialidades.Fields.SEQ.toString()), "espSeq");
		criteria.setProjection(Projections.distinct(projectionList));
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.SM));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}		
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(seqCategorias != null && !seqCategorias.isEmpty()) {
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), seqCategorias));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	public List<CalculoAtendimentoPacienteVO> buscarEquipesMedicas(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> seqCategorias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), "ser");
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.property("cpp." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL_MATRICULA.toString()), "matriculaRespEquipe")
				.add(Projections.property("cpp." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL_VIN_CODIGO.toString()), "vinCodigoRespEquipe");
		criteria.setProjection(Projections.distinct(projectionList));
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.EQ));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(seqCategorias != null && !seqCategorias.isEmpty()) {
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), seqCategorias));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteEquipeMedica(Integer prontuario, Integer pmuSeq, Integer matriculaResp, Short vinCodigoResp, Integer atdSeq, List<Integer> seqCategorias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), "ser");
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.property("ser." + RapServidores.Fields.VIN_CODIGO.toString()), "vinCodigoRespEquipe")
				.add(Projections.property("ser." + RapServidores.Fields.MATRICULA.toString()), "matriculaRespEquipe")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), "agrupador")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), "descricao")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), "ctcSeq")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()), "ordemVisualizacao")
				.add(Projections.sum("cca." + SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()).as("quantidade"))
				.add(Projections.sqlProjection("sum(coalesce(VLR_TOT_INSUMOS,0) + coalesce(VLR_TOT_PESSOAS,0) + coalesce(VLR_TOT_EQUIPAMENTOS,0) + coalesce(VLR_TOT_SERVICOS,0)) custoTotal",
							new String[]{"custoTotal"}, new Type[] {BigDecimalType.INSTANCE}))
				.add(Projections.groupProperty("ser." + RapServidores.Fields.VIN_CODIGO.toString()))
				.add(Projections.groupProperty("ser." + RapServidores.Fields.MATRICULA.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()));
		criteria.setProjection(projectionList);
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.EQ));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(matriculaResp != null && vinCodigoResp != null) {
			criteria.add(Restrictions.eq("ser." + RapServidores.Fields.MATRICULA.toString(), matriculaResp));
			criteria.add(Restrictions.eq("ser." + RapServidores.Fields.VIN_CODIGO.toString(), vinCodigoResp));
		}
		if(seqCategorias != null && !seqCategorias.isEmpty()) {
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), seqCategorias));
		}
		criteria.addOrder(Order.asc("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()));
		criteria.addOrder(Order.asc("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeralCentroCustoCategoria(Integer prontuario, Integer pmuSeq, Integer codCentroCusto, List<Integer> categorias, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO.toString(), "cct");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "phi", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.LEFT_OUTER_JOIN);
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.property("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()), "phiDescricao")
				.add(Projections.property("obj." + SigObjetoCustos.Fields.NOME.toString()), "objNome")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), "agrupador")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), "descricao")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()), "ordemVisualizacao")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), "ctcSeq")
				.add(Projections.sum("cca." + SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()).as("quantidade"))				
				.add(Projections.sqlProjection("sum(coalesce(VLR_TOT_INSUMOS,0) + coalesce(VLR_TOT_PESSOAS,0) + coalesce(VLR_TOT_EQUIPAMENTOS,0) + coalesce(VLR_TOT_SERVICOS,0)) custoTotal",
                        new String[]{"custoTotal"}, new Type[]{BigDecimalType.INSTANCE}))
				.add(Projections.groupProperty("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()))
				.add(Projections.groupProperty("obj." + SigObjetoCustos.Fields.NOME.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()));
		criteria.setProjection(projectionList);
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(codCentroCusto != null) {
			criteria.add(Restrictions.eq("cct." + FccCentroCustos.Fields.CODIGO.toString(), codCentroCusto));
		}
		if(categorias != null &&  !categorias.isEmpty()) {
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), categorias));
		}
		criteria.addOrder(Order.asc("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("obj." + SigObjetoCustos.Fields.NOME.toString()));		
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteVisaoGeral(Integer prontuario, Integer pmuSeq, List<Integer> categorias, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO.toString(), "cct");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "phi", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.LEFT_OUTER_JOIN);
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.property("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()), "phiDescricao")
				.add(Projections.property("obj." + SigObjetoCustos.Fields.NOME.toString()), "objNome")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), "agrupador")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), "descricao")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()), "ordemVisualizacao")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), "ctcSeq")
				.add(Projections.sum("cca." + SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()).as("quantidade"))				
				.add(Projections.sqlProjection("sum(coalesce(VLR_TOT_INSUMOS,0) + coalesce(VLR_TOT_PESSOAS,0) + coalesce(VLR_TOT_EQUIPAMENTOS,0) + coalesce(VLR_TOT_SERVICOS,0)) custoTotal",
                        new String[]{"custoTotal"}, new Type[]{BigDecimalType.INSTANCE}))
				.add(Projections.groupProperty("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()))
				.add(Projections.groupProperty("obj." + SigObjetoCustos.Fields.NOME.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()));
		criteria.setProjection(projectionList);
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(categorias != null &&  !categorias.isEmpty()) {
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), categorias));
		}
		criteria.addOrder(Order.asc("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("obj." + SigObjetoCustos.Fields.NOME.toString()));		
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	public List<CalculoAtendimentoPacienteVO> buscarCustosPacienteInternacao(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.LISTA_CALCULOS_CIDS.toString(), "cci");
		criteria.createAlias("cci." + SigCalculoAtdCIDS.Fields.CID.toString(), "cid");
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.property("cid." + AghCid.Fields.DESCRICAO.toString()), "diagnostico")
				.add(Projections.property("cci." + SigCalculoAtdCIDS.Fields.PRINCIPAL.toString()), "principal")
				.add(Projections.property("atd." + AghAtendimentos.Fields.DTHR_INICIO.toString()), "dataInicio")
				.add(Projections.property("atd." + AghAtendimentos.Fields.DTHR_FIM.toString()), "dataFim");
		criteria.setProjection(Projections.distinct(projectionList));
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		criteria.addOrder(Order.desc("cci." + SigCalculoAtdCIDS.Fields.PRINCIPAL.toString()));
		criteria.addOrder(Order.desc("atd." + AghAtendimentos.Fields.DTHR_INICIO.toString()));
		criteria.addOrder(Order.asc("cid." + AghCid.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	public BigDecimal buscarCustoTotal(Integer prontuario, Integer pmuSeq, boolean isEspecialidade, boolean isEquipeMedica, Integer atdSeq, List<Integer> seqCategorias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.sqlProjection("sum(coalesce(VLR_TOT_INSUMOS,0) + coalesce(VLR_TOT_PESSOAS,0) + coalesce(VLR_TOT_EQUIPAMENTOS,0) + coalesce(VLR_TOT_SERVICOS,0)) custoTotal",
							new String[]{"custoTotal"}, new Type[] {BigDecimalType.INSTANCE}));
		criteria.setProjection(projectionList);
		criteria.add(Restrictions.or(
                Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
                Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(prontuario!=null){
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));	
		}
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(isEspecialidade && !isEquipeMedica) {
			criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO.toString(), "cct");
			criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), "esp");
			criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.SM));
		} else if(isEquipeMedica && !isEspecialidade) {
			criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), "ser");
			criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.EQ));
		} else {
			criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		}
		if(seqCategorias != null && !seqCategorias.isEmpty()) {
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), seqCategorias));
		}
		criteria.add(Restrictions.or(Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.P),
				Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.F)));
		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}
	// #32268 C1 Parte 1 do union all
    public List<MedicamentosDialiseVO> buscarMedicamentosDialise(Integer pmuSeq, Boolean comMaterial, DominioIndContagem ... contagens) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp", JoinType.INNER_JOIN);
        criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.INNER_JOIN);
        criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CALCULO_DETALHE_CONSUMO.toString(), "cdc", JoinType.INNER_JOIN);
        criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc", JoinType.INNER_JOIN);
        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.SEQ.toString()), "cdcSeq")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.CALCULO_ATIVIDADE_CONSUMO_SEQ.toString()), "ccaSeq")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_SEQ.toString()), "phiSeq")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()), "matCodigo")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.QUANTIDADE_PREVISTO.toString()), "qtdePrevisto")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.QUANTIDADE_DEBITADO.toString()), "qtdeDebitado")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString()), "qtdeConsumido")
                .add(Projections.property("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString()), "cctCodigo");
        criteria.setProjection(projectionList);
        criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.IND_CONTAGEM.toString(), contagens));
        criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
        if(comMaterial) {
            criteria.add(Restrictions.isNotNull("cdc." + SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()));
        }
        criteria.setResultTransformer(Transformers.aliasToBean(MedicamentosDialiseVO.class));
        return executeCriteria(criteria);
    }
    // #32268 C1 Parte 2 do union all
    public List<MedicamentosDialiseVO> buscarMedicamentosDialisePhi(Integer pmuSeq, DominioIndContagem...contagens) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp", JoinType.INNER_JOIN);
        criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.INNER_JOIN);
        criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CALCULO_DETALHE_CONSUMO.toString(), "cdc", JoinType.INNER_JOIN);
        criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc", JoinType.INNER_JOIN);
        criteria.createAlias("cdc." + SigCalculoDetalheConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "phi", JoinType.INNER_JOIN);
        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.SEQ.toString()), "cdcSeq")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.CALCULO_ATIVIDADE_CONSUMO_SEQ.toString()), "ccaSeq")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_SEQ.toString()), "phiSeq")
                .add(Projections.property("phi." + FatProcedHospInternos.Fields.MAT_CODIGO.toString()), "matCodigo")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.QUANTIDADE_PREVISTO.toString()), "qtdePrevisto")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.QUANTIDADE_DEBITADO.toString()), "qtdeDebitado")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString()), "qtdeConsumido")
                .add(Projections.property("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString()), "cctCodigo");
        criteria.setProjection(projectionList);
        criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.IND_CONTAGEM.toString(), contagens));
        criteria.add(Restrictions.isNull("cdc." + SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()));
        criteria.add(Restrictions.isNotNull("phi." + FatProcedHospInternos.Fields.MAT_CODIGO.toString()));
        criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
        criteria.setResultTransformer(Transformers.aliasToBean(MedicamentosDialiseVO.class));
        return executeCriteria(criteria);
    }
    // #32236 - C1
    public List<MedicamentosDialiseVO> buscarMedicamentos(Integer pmuSeq, DominioIndContagem contagem) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp", JoinType.INNER_JOIN);
        criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.INNER_JOIN);
        criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CALCULO_DETALHE_CONSUMO.toString(), "cdc", JoinType.INNER_JOIN);
        criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc", JoinType.INNER_JOIN);
        criteria.createAlias("cdc." + SigCalculoDetalheConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "phi", JoinType.INNER_JOIN);
        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.SEQ.toString()), "cdcSeq")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.CALCULO_ATIVIDADE_CONSUMO_SEQ.toString()), "ccaSeq")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_SEQ.toString()), "phiSeq")
                .add(Projections.property("phi." + FatProcedHospInternos.Fields.MAT_CODIGO.toString()), "matCodigo")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.QUANTIDADE_PREVISTO.toString()), "qtdePrevisto")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.QUANTIDADE_DEBITADO.toString()), "qtdeDebitado")
                .add(Projections.property("cdc." + SigCalculoDetalheConsumo.Fields.QUANTIDADE_CONSUMIDO.toString()), "qtdeConsumido")
                .add(Projections.property("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString()), "cctCodigo");
        criteria.setProjection(projectionList);
        criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_CONTAGEM.toString(), contagem));
        criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
        criteria.setResultTransformer(Transformers.aliasToBean(MedicamentosDialiseVO.class));
        return executeCriteria(criteria);
    }
}
