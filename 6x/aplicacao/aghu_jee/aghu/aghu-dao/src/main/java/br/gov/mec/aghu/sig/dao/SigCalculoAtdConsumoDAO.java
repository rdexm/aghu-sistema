package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaFatura;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaDispensacaoMdtosPai;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.CntaConv;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.model.MptItemPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptPrescricaoProcedimento;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoDetalheConsumo;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.ConsultoriaMedicaEspecialidadeVO;
import br.gov.mec.aghu.sig.custos.vo.ConsumoPacienteConsumidoVO;
import br.gov.mec.aghu.sig.custos.vo.OrtesesProtesesesInternacaoVO;
import br.gov.mec.aghu.sig.custos.vo.ProcedimentoEspecialContagemVO;
import br.gov.mec.aghu.sig.custos.vo.SigAtendimentoDialiseVO;
import br.gov.mec.aghu.sig.custos.vo.SigConsumosInternacoesVO;
import br.gov.mec.aghu.sig.custos.vo.SigProcedimentoMaterialPrescricaoDialiseVO;
import br.gov.mec.aghu.sig.custos.vo.SolicitacaoHemoterapiaNaInternacaoVO;
import br.gov.mec.aghu.sig.custos.vo.SumarioProducaoAssistencialPacienteVO;

public class SigCalculoAtdConsumoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtdConsumo> {

	private static final long serialVersionUID = -394757986163901383L;

	
	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(112);
		sql.append(" DELETE ").append(SigCalculoAtdConsumo.class.getSimpleName().toString()).append(" caa ");
		sql.append(" WHERE caa.").append(SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString()).append('.').append(SigCalculoAtdPermanencia.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT ca.").append(SigCalculoAtdPermanencia.Fields.SEQ.toString());
			sql.append(" FROM ").append(SigCalculoAtdPermanencia.class.getSimpleName().toString()).append(" ca ");
			sql.append(" WHERE ca.").append(SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString()).append('.').append(SigCalculoAtdPaciente.Fields.SEQ.toString());
			sql.append(" IN ( ");
				sql.append(" SELECT c.").append(SigCalculoAtdPaciente.Fields.SEQ.toString());
				sql.append(" FROM ").append( SigCalculoAtdPaciente.class.getSimpleName()).append(" c ");
				sql.append(" WHERE c.").append(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
			sql.append(" ) ");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
	
	/**
	 * Busca um item de consumo de acordo com os parâmetros informados
	 * 
	 * @author rogeriovieira
	 * @param calculoAtdPermanencia 	Permanência que pode representar a unidade de internação, a especialidade e a equipe.
	 * @param objetoCustoVersao 		Versão do objeto de custo, que pode ser nulo se for informado o phi.
	 * @param phi 						Phi que pode ser nulo se for informada a versã do objeto de custo.
	 * @return 							Objeto encontrado, ou nulo se não encontrou.
	 */
	public SigCalculoAtdConsumo buscarItemConsumo(SigCalculoAtdPermanencia calculoAtdPermanencia, SigObjetoCustoVersoes objetoCustoVersao, FatProcedHospInternos phi) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class);
		criteria.add(Restrictions.eq(SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString(), calculoAtdPermanencia));
		if (objetoCustoVersao != null) {
			criteria.add(Restrictions.eq(SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO.toString(), objetoCustoVersao));
		} else {
			criteria.add(Restrictions.eq(SigCalculoAtdConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), phi));
		}

		return (SigCalculoAtdConsumo) this.executeCriteriaUniqueResult(criteria, true);
	}
	
	/**
	 * Buscar o consumo de acordo com a permanencia, a versão do objeto de custo e o centro de custo
	 * 
	 * @author rogeriovieira
	 * @param calculoAtdPermanencia 	Permanência que pode representar a unidade de internação, a especialidade e a equipe.
	 * @param objetoCustoVersao 		Versão do objeto de custo.
	 * @param centroCusto 				Centro do custo.
	 * @return 							Objeto encontrado, ou nulo se não encontrou.
	 */
	public SigCalculoAtdConsumo buscarCalculoAtendimentoConsumo(SigCalculoAtdPermanencia calculoAtdPermanencia, SigObjetoCustoVersoes objetoCustoVersao, FccCentroCustos centroCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class, "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString(), "cpp", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString(), calculoAtdPermanencia));
		criteria.add(Restrictions.eq(SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO.toString(), objetoCustoVersao));
		criteria.add(Restrictions.eq(SigCalculoAtdConsumo.Fields.CENTRO_CUSTO.toString(), centroCusto));
		return (SigCalculoAtdConsumo) this.executeCriteriaUniqueResult(criteria, true);
	}
	

	public List<ConsumoPacienteConsumidoVO> buscarConsumoPacienteConsumidos(SigProcessamentoCusto processamentoCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class, "cca");

		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString(), "cpp", JoinType.INNER_JOIN);
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac", JoinType.INNER_JOIN);
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.INNER_JOIN);
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.CALCULO_OBJETO_CUSTO.toString(), "cbj", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));
		
		criteria.add(Restrictions.eqProperty("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(),  "cbj."+ SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString()));
		
		criteria.add(Restrictions.eqProperty("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO.toString(),  "cbj."+ SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()));

		//nvl(cbj.PGD_SEQ,cac.PGD_SEQ) = cac.PGD_SEQ
		criteria.add(Restrictions.or(
				Restrictions.eqProperty("cbj." + SigCalculoObjetoCusto.Fields.PAGADOR.toString(), "cac."+ SigCalculoAtdPaciente.Fields.PAGADOR.toString()),
				Restrictions.isNull("cbj." + SigCalculoObjetoCusto.Fields.PAGADOR.toString())
		));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("cca." + SigCalculoAtdConsumo.Fields.SEQ.toString()), "ccaSeq")
				.add(Projections.property("ocv." + SigObjetoCustoVersoes.Fields.SEQ.toString()), "ocvSeq")
				.add(Projections.property("cca." + SigCalculoAtdConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString() + "."
						+ FatProcedHospInternosPai.Fields.SEQ.toString()), "phiSeq")
				.add(Projections.property("cpp." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString() + "." + FccCentroCustos.Fields.CODIGO.toString()),
						"cctCodigo")
				.add(Projections.property("cca." + SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()), "qtdeConsumida")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.QUANTIDADE_PRODUZIDA.toString()), "qtdeProduzida")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_ATV_INSUMOS.toString()), "valorAtvInsumo")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_ATV_PESSOAL.toString()), "valorAtvPessoal")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_ATV_EQUIPAMENTO.toString()), "valorAtvEquipamento")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_ATV_SERVICO.toString()), "valorAtvServico")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_RATEIO_INSUMOS.toString()), "valorRateioInsumo")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_RATEIO_PESSOAS.toString()), "valorRateioPessoal")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_RATEIO_EQUIPAMENTOS.toString()), "valorRateioEquipamento")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_RATEIO_SERVICO.toString()), "valorRateioServico")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_IND_INSUMOS.toString()), "valorIndiretoInsumo")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_IND_PESSOAS.toString()), "valorIndiretoPessoal")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_IND_EQUIPAMENTOS.toString()), "valorIndiretoEquipamento")
				.add(Projections.property("cbj." + SigCalculoObjetoCusto.Fields.VALOR_IND_SERVICOS.toString()), "valorIndiretoServico")
		);
		
		criteria.addOrder(Order.desc("cbj." + SigCalculoObjetoCusto.Fields.TIPO_CALCULO));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ConsumoPacienteConsumidoVO.class));
		return executeCriteria(criteria);
	}
	
	public List<SumarioProducaoAssistencialPacienteVO> buscarProducaoPaciente(SigProcessamentoCusto processamentoCusto) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class, "cca");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("cca." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO), SumarioProducaoAssistencialPacienteVO.Fields.OBJETO_CUSTO_VERSAO.toString())
				.add(Projections.groupProperty("cca." + SigCalculoAtdConsumo.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO), SumarioProducaoAssistencialPacienteVO.Fields.PHI.toString())
				.add(Projections.groupProperty("cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO), SumarioProducaoAssistencialPacienteVO.Fields.CENTRO_CUSTO.toString())
				.add(Projections.groupProperty("cac." + SigCalculoAtdPaciente.Fields.PAGADOR), SumarioProducaoAssistencialPacienteVO.Fields.PAGADOR.toString())
				.add(Projections.sum("cca." + SigCalculoAtdConsumo.Fields.QUANTIDADE), SumarioProducaoAssistencialPacienteVO.Fields.QUANTIDADE.toString())
		);
		
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA, "cpp", JoinType.INNER_JOIN);
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE, "cac", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO, processamentoCusto));
		criteria.add(Restrictions.isNotNull("cpp." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO));

		//Não existe produção para objeto de custo
		criteria.add(Subqueries.notExists( DetachedCriteria.forClass(SigDetalheProducao.class, "dhp")
				.setProjection(Projections.projectionList().add(Projections.property("dhp." + SigDetalheProducao.Fields.SEQ)))
				.add(Restrictions.eqProperty("dhp." + SigDetalheProducao.Fields.OBJETO_CUSTO_VERSAO, "cca."+ SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO))
				.add(Restrictions.eqProperty("dhp." + SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO, "cac."+ SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO))
				.add(Restrictions.eqProperty("dhp." + SigDetalheProducao.Fields.CENTRO_CUSTO,"cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO))
				.add(Restrictions.or(
						Restrictions.eqProperty("dhp." + SigDetalheProducao.Fields.PAGADOR,"cac." + SigCalculoAtdPaciente.Fields.PAGADOR),
						Restrictions.and( 
								Restrictions.isNull("dhp." + SigDetalheProducao.Fields.PAGADOR),  
								Restrictions.isNull("cac." + SigCalculoAtdPaciente.Fields.PAGADOR)
						)
				)
		)));
		
		//Não existe produção para phi com pagador nulo
		criteria.add(Subqueries.notExists( DetachedCriteria.forClass(SigDetalheProducao.class, "dhp")
				.setProjection(Projections.projectionList().add(Projections.property("dhp." + SigDetalheProducao.Fields.SEQ)))
				.add(Restrictions.eqProperty("dhp." + SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO, "cac."+ SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO))
				.add(Restrictions.eqProperty("dhp." + SigDetalheProducao.Fields.CENTRO_CUSTO,"cca." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO))
				.add(Restrictions.eq("dhp." + SigDetalheProducao.Fields.GRUPO, DominioGrupoDetalheProducao.PHI))
				.add(Restrictions.isNull("dhp." + SigDetalheProducao.Fields.OBJETO_CUSTO_VERSAO))
				.add(Restrictions.isNull("dhp." + SigDetalheProducao.Fields.PAGADOR))
		));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SumarioProducaoAssistencialPacienteVO.class));
		
		return this.executeCriteria(criteria);
	}
	
	
	/**
	 * Busca de consultorias médica de internação
	 * 
	 * @author rogeriovieira
	 * @param atdSeq 					Identificador do atendimento de internação
	 * @param dtInicioProcessamento 	Data de início do processamento
	 * @param dtFimProcessamento 		Data de fim do processamento
	 * @return Todas as consultorias respondidas no atendimento de internação
	 */
	public List<ConsultoriaMedicaEspecialidadeVO> buscarConsultoriasMedicasPorAtendimento(Integer atdSeq, Date dtInicioProcessamento, Date dtFimProcessamento){	
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class, "scn");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE),ConsultoriaMedicaEspecialidadeVO.Fields.NOME_ESPECIALIDADE_CONSULTORIA.toString())
				.add(Projections.groupProperty("esp." + AghEspecialidades.Fields.CENTRO_CUSTO_CODIGO),ConsultoriaMedicaEspecialidadeVO.Fields.CODIGO_CENTRO_CUSTO.toString())
				.add(Projections.groupProperty("scn." + MpmSolicitacaoConsultoria.Fields.SEQ),ConsultoriaMedicaEspecialidadeVO.Fields.SEQ_CONSULTORIA.toString())
				.add(Projections.groupProperty("scn." + MpmSolicitacaoConsultoria.Fields.CRIADO_EM),ConsultoriaMedicaEspecialidadeVO.Fields.DATA_SOLICITACAO.toString())
				.add(Projections.groupProperty("scn." + MpmSolicitacaoConsultoria.Fields.DTHRRESPOSTA),ConsultoriaMedicaEspecialidadeVO.Fields.DATA_RESPOSTA.toString())
				.add(Projections.count("scn." + MpmSolicitacaoConsultoria.Fields.SEQ), ConsultoriaMedicaEspecialidadeVO.Fields.QTDE_CONSULTORIAS.toString()));
		criteria.createAlias("scn." + MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE, "esp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("scn." + MpmSolicitacaoConsultoria.Fields.PRESCRICAO_MEDICA, "prm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("prm." + MpmPrescricaoMedica.Fields.ATENDIMENTO, "atd", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("scn." + MpmSolicitacaoConsultoria.Fields.PME_ATD_SEQ, atdSeq));
		criteria.add(Restrictions.between("scn." + MpmSolicitacaoConsultoria.Fields.CRIADO_EM, dtInicioProcessamento, dtFimProcessamento));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.ORIGEM, DominioOrigemAtendimento.I));
		criteria.add(Restrictions.in("scn." + MpmSolicitacaoConsultoria.Fields.PENDENCIA, new DominioIndPendenteItemPrescricao[] {
				DominioIndPendenteItemPrescricao.N, DominioIndPendenteItemPrescricao.A, DominioIndPendenteItemPrescricao.E 
		}));
		criteria.add(Restrictions.isNull("scn." + MpmSolicitacaoConsultoria.Fields.DTHR_FIM));
		criteria.add(Restrictions.isNotNull("scn." + MpmSolicitacaoConsultoria.Fields.DTHRRESPOSTA));
		criteria.addOrder(Order.asc("scn." + MpmSolicitacaoConsultoria.Fields.CRIADO_EM));
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultoriaMedicaEspecialidadeVO.class));
		
		return this.executeCriteria(criteria);
	}
	
	
	/**
	 * Busca Órteses e Próteses utilizadas na internação
	 * 
	 * @author rogeriovieira
	 * @param seqInternacao identificador da internação
	 * @param dtInicioProcessamento data de início da competência do processamento
	 * @param dtFimProcessamento data de fim da competência do processamento
	 * @return  todas as órteses e próteses utilizadas na internação
	 */
	public List<OrtesesProtesesesInternacaoVO> buscarOrtesesProtesesPorInternacao(Integer seqInternacao, Date dtInicioProcessamento, Date dtFimProcessamento){
		 
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRmps.class, "ips");
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("rmp." + SceRmrPaciente.Fields.DT_UTILIZACAO), OrtesesProtesesesInternacaoVO.Fields.DATA_UTILIZACAO.toString())
				.add(Projections.property("rmp." + SceRmrPaciente.Fields.CENTRO_CUSTO + "." + FccCentroCustos.Fields.CODIGO),
						OrtesesProtesesesInternacaoVO.Fields.CODIGO_CENTRO_CUSTO.toString())
				.add(Projections.property("phi." + FatProcedHospInternos.Fields.SEQ), OrtesesProtesesesInternacaoVO.Fields.SEQ_PHI.toString())
				.add(Projections.property("rmp." + SceRmrPaciente.Fields.SEQ), OrtesesProtesesesInternacaoVO.Fields.RMP_SEQ.toString())
				.add(Projections.property("mat." + ScoMaterial.Fields.CODIGO), OrtesesProtesesesInternacaoVO.Fields.MAT_CODIGO.toString()));
		criteria.createCriteria("ips." + SceItemRmps.Fields.SCE_RMR_PACIENTE, "rmp", JoinType.INNER_JOIN);
		criteria.setFetchMode("ips." + SceItemRmps.Fields.SCE_RMR_PACIENTE, FetchMode.JOIN);
		criteria.createCriteria("ips." + SceItemRmps.Fields.SCE_ESTQ_ALMOX, "eal", JoinType.LEFT_OUTER_JOIN);
		criteria.setFetchMode("ips." + SceItemRmps.Fields.SCE_ESTQ_ALMOX, FetchMode.JOIN);
		criteria.createCriteria("eal." + SceEstoqueAlmoxarifado.Fields.MATERIAL, "mat", JoinType.LEFT_OUTER_JOIN);
		criteria.setFetchMode("eal." + SceEstoqueAlmoxarifado.Fields.MATERIAL, FetchMode.JOIN);
		criteria.createCriteria("mat." + ScoMaterial.Fields.PROCED_HOSP_INTERNO, "phi", JoinType.LEFT_OUTER_JOIN);
		criteria.setFetchMode("mat." + ScoMaterial.Fields.PROCED_HOSP_INTERNO, FetchMode.JOIN);
		criteria.add(Restrictions.between("rmp." + SceRmrPaciente.Fields.DT_UTILIZACAO, dtInicioProcessamento, dtFimProcessamento));
		criteria.add(Restrictions.eq("rmp." + SceRmrPaciente.Fields.INT_SEQ, seqInternacao));
		criteria.add(Restrictions.or(Restrictions.isNull("rmp." + SceRmrPaciente.Fields.IND_SITUACAO),
				Restrictions.ne("rmp." + SceRmrPaciente.Fields.IND_SITUACAO, DominioSituacaFatura.X)));
		criteria.add(Restrictions.isNull("rmp." + SceRmrPaciente.Fields.CIRURGIA));
		criteria.addOrder(Order.asc("rmp." + SceRmrPaciente.Fields.SEQ));
		criteria.setResultTransformer(Transformers.aliasToBean(OrtesesProtesesesInternacaoVO.class));
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Busca procedimentos especiais diversos
	 * 
	 * @author rogeriovieira
	 * @param atdSeq identificador do atendimento de internação
	 * @param dtInicioProcessamento data de início do processamento
	 * @param dtFimProcessamento data de fim do processamento
	 * @return todos os procedimentos especiais diversos prescritos no atendimento de internação
	 */
	public List<ProcedimentoEspecialContagemVO> buscarProcedimentosEspeciaisDiversos(Integer atdSeq, Date dtInicioProcessamento, Date dtFimProcessamento){
		return this.buscarProcedimentosEspeciais(atdSeq, dtInicioProcessamento, dtFimProcessamento, true);
	}
	
	/**
	 * Busca procedimentos especiais cirúrgicos
	 * 
	 * @author rogeriovieira
	 * @param atdSeq identificador do atendimento de internação
	 * @param dtInicioProcessamento data de início do processamento
	 * @param dtFimProcessamento data de fim do processamento
	 * @return todos os procedimentos especiais cirúrgicos prescritos no atendimento de internação
	 */
	public List<ProcedimentoEspecialContagemVO> buscarProcedimentosEspeciaisCirurgicos(Integer atdSeq, Date dtInicioProcessamento, Date dtFimProcessamento){
		return this.buscarProcedimentosEspeciais(atdSeq, dtInicioProcessamento, dtFimProcessamento, false);
	}
	
	/**
	 * Busca procedimentoa especiais de um determinado tipo
	 * 
	 * @author rogeriovieira
	 * @param atdSeq identificador do atendimento de internação
	 * @param dtInicioProcessamento data de início do processamento
	 * @param dtFimProcessamento  data de fim do processamento
	 * @param somenteProcedimentosDiversos determina se utilizará a tabela de procedimento diversos, ou a de procedimento cirurgicas
	 * @return todos os procedimentos especiais de um determinado tipo prescritos no atendimento de internação
	 */
	private List<ProcedimentoEspecialContagemVO> buscarProcedimentosEspeciais(Integer atdSeq, Date dtInicioProcessamento, Date dtFimProcessamento, boolean somenteProcedimentosDiversos){
		
		String nomeTabela = null;
		String nomeAliasTabela = null;
		String nomeJuncaoPhi = null;
		
		String nomePropriedadeConsultaProjection = null;
		String nomePropriedadeVO = null;
		
		if(somenteProcedimentosDiversos){
			nomePropriedadeConsultaProjection = MpmProcedEspecialDiversos.Fields.SEQ.toString();
			nomePropriedadeVO = ProcedimentoEspecialContagemVO.Fields.PED_SEQ.toString();
			
			nomeTabela = MpmPrescricaoProcedimento.Fields.PED_SEQ.toString();
			nomeAliasTabela = "ped";
			nomeJuncaoPhi = MpmProcedEspecialDiversos.Fields.PROCED_HOSP_INTERNO.toString();
		}
		else{
			nomePropriedadeConsultaProjection = MbcProcedimentoCirurgicos.Fields.SEQ.toString();
			nomePropriedadeVO = ProcedimentoEspecialContagemVO.Fields.MPC_SEQ.toString();
			
			nomeTabela =  MpmPrescricaoProcedimento.Fields.PCI_SEQ.toString();
			nomeAliasTabela = "mpc";
			nomeJuncaoPhi = MbcProcedimentoCirurgicos.Fields.PROCEDIMENTOS_HOSPITALARES_INTERNOS.toString();
		}
		
		//join mpm_prescricao_procedimentos mpp
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoProcedimento.class, "mpp");

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.groupProperty("pme." + MpmPrescricaoMedica.Fields.ATD_SEQ), ProcedimentoEspecialContagemVO.Fields.ATD_SEQ.toString())
				.add(Projections.groupProperty("pme." + MpmPrescricaoMedica.Fields.SEQ), ProcedimentoEspecialContagemVO.Fields.PME_SEQ.toString())
				.add(Projections.groupProperty(nomeAliasTabela + "." + nomePropriedadeConsultaProjection), nomePropriedadeVO)
				.add(Projections.groupProperty("phi." + FatProcedHospInternos.Fields.SEQ), ProcedimentoEspecialContagemVO.Fields.PHI_SEQ.toString())
				.add(Projections.groupProperty("ocv." + SigObjetoCustoVersoes.Fields.SEQ), ProcedimentoEspecialContagemVO.Fields.OCV_SEQ.toString())
				.add(Projections.groupProperty("pme." + MpmPrescricaoMedica.Fields.DTHR_INICIO), ProcedimentoEspecialContagemVO.Fields.PME_DTHR_INICIO.toString()));
		
		criteria.createCriteria("mpp."+MpmPrescricaoProcedimento.Fields.PRESCRICAOMEDICA, "pme", JoinType.INNER_JOIN);
		criteria.createCriteria("mpp."+nomeTabela, nomeAliasTabela, JoinType.INNER_JOIN);
		criteria.createCriteria(nomeAliasTabela+"."+nomeJuncaoPhi, "phi", JoinType.INNER_JOIN);
		criteria.createCriteria("phi."+FatProcedHospInternos.Fields.OBJETO_CUSTO_PHI, "ocp", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("ocp."+SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO, "ocv", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("pme."+MpmPrescricaoMedica.Fields.ATD_SEQ, atdSeq));
		criteria.add(Restrictions.between("pme."+MpmPrescricaoMedica.Fields.DT_REFERENCIA, dtInicioProcessamento, dtFimProcessamento));
		criteria.add(Restrictions.isNotNull("mpp."+MpmPrescricaoProcedimento.Fields.SERVIDOR_VALIDACAO));
		criteria.addOrder(Order.asc("pme." + MpmPrescricaoMedica.Fields.DTHR_INICIO));
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentoEspecialContagemVO.class));
		return this.executeCriteria(criteria);		
	}
	
	/**
	 * Busca as solicitações de hemoterapias e seus itens(procedimentos hemoterápicos e/ou componentes sanguineos) realizadas 
	 * em uma internação.
	 * 
	 * @param atendimento              Atendimento	
	 * @param dataInicioProcessamento  Data do inicio do processamento
	 * @param dataFimProcessamento     Data do fim do processamento
	 * @return Lista com as solicitacoes hemoterapicas realizadas na internacao e no processamento informado nas datas 
	 * @author rhrosa
	 */
	public List<SolicitacaoHemoterapiaNaInternacaoVO> buscarSolicitacoesHemoterapicasRealizadasNaInternacao(Integer atdSeq, Date dataInicioProcessamento,
			Date dataFimProcessamento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesHemoterapicas.class, "she");
		
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("she." + AbsSolicitacoesHemoterapicas.Fields.SEQ), SolicitacaoHemoterapiaNaInternacaoVO.Fields.SHE_SEQ.toString())
				.add(Projections.property("she." + AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ), SolicitacaoHemoterapiaNaInternacaoVO.Fields.SHE_ATD_SEQ.toString())
				.add(Projections.property("she." + AbsSolicitacoesHemoterapicas.Fields.CRIADO_EM), SolicitacaoHemoterapiaNaInternacaoVO.Fields.SHE_CRIADO_EM.toString())

				.add(Projections.property("ishe." + AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS_CODIGO), SolicitacaoHemoterapiaNaInternacaoVO.Fields.ITEM_SHE_CSA_CODIGO.toString())
				.add(Projections.property("ishe." + AbsItensSolHemoterapicas.Fields.ABS_PROCED_HEMOTERAPICOS_CODIGO), SolicitacaoHemoterapiaNaInternacaoVO.Fields.ITEM_SHE_PHE_CODIGO.toString())
				.add(Projections.property("ishe." + AbsItensSolHemoterapicas.Fields.QTDE_APLICACOES), SolicitacaoHemoterapiaNaInternacaoVO.Fields.ITEM_SHE_QTDE_APLICACOES.toString())
				.add(Projections.property("ishe." + AbsItensSolHemoterapicas.Fields.QTDE_UNIDADES), SolicitacaoHemoterapiaNaInternacaoVO.Fields.ITEM_SHE_QTDE_UNIDADES.toString())
				.add(Projections.property("ishe." + AbsItensSolHemoterapicas.Fields.QTDE_ML), SolicitacaoHemoterapiaNaInternacaoVO.Fields.ITEM_SHE_QTDE_ML.toString())

				.add(Projections.property("phi." + FatProcedHospInternos.Fields.PHI_SEQ), SolicitacaoHemoterapiaNaInternacaoVO.Fields.PHI_SEQ.toString())
				.add(Projections.property("ocv." + SigObjetoCustoVersoes.Fields.SEQ), SolicitacaoHemoterapiaNaInternacaoVO.Fields.OCV_SEQ.toString()));

		criteria.createAlias("she." + AbsSolicitacoesHemoterapicas.Fields.ITENS_SOLICITACOES_HEMOTERAPICAS, "ishe", JoinType.INNER_JOIN);		
		
		criteria.createAlias("ishe." + AbsItensSolHemoterapicas.Fields.PROCED_HEMOTERAPICOS, "phe", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("phe." + AbsProcedHemoterapico.Fields.FAT_PROCED_HOSP_INTERNOS, "phi", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("phi." + FatProcedHospInternos.Fields.OBJETO_CUSTO_PHI, "ocp", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("ocp." + SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO, "ocv", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("ishe." + AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS, "csa", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("csa." + AbsComponenteSanguineo.Fields.FAT_PROCED_HOSP_INTERNOS , "phi2", JoinType.LEFT_OUTER_JOIN);
						
		criteria.createAlias("phi2." + FatProcedHospInternos.Fields.OBJETO_CUSTO_PHI, "ocp2", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("ocp2." + SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO, "ocv2", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.isNull("she."+AbsSolicitacoesHemoterapicas.Fields.DTHR_FIM));
		criteria.add(Restrictions.eq("she."+AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE, DominioSimNao.N));
		criteria.add(Restrictions.eq("she."+AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ, atdSeq));
		criteria.add(Restrictions.between("she."+AbsSolicitacoesHemoterapicas.Fields.CRIADO_EM , dataInicioProcessamento, dataFimProcessamento));
		
		criteria.addOrder(Order.asc("she." + AbsSolicitacoesHemoterapicas.Fields.CRIADO_EM));
		criteria.setResultTransformer(Transformers.aliasToBean(SolicitacaoHemoterapiaNaInternacaoVO.class));
		return this.executeCriteria(criteria);
		
	}
	
	public SigCalculoAtdConsumo buscarItemConsumoPorPermanenciaEVersao(final Integer cppSeq, final Integer ocvSeq)  {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class);
		criteria.add(Restrictions.eq(SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA_SEQ.toString(), cppSeq));
		criteria.add(Restrictions.eq(SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString(), ocvSeq));
		return (SigCalculoAtdConsumo) executeCriteriaUniqueResult(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<SigAtendimentoDialiseVO> buscarMedicamentosPrescricaoDialise(Date dataInicio,
			Date dataFim) {

		StringBuffer sql = new StringBuffer(" select ")
		.append("  atd." + AghAtendimentos.Fields.SEQ.toString() + " as atdPaciente ")
		.append("  , atd." + AghAtendimentos.Fields.PAC_CODIGO.toString() + " as pacCodigo ")
		.append("  , atd." + AghAtendimentos.Fields.TRP_SEQ.toString() + " as trpSeq ")
		.append("  , atd." + AghAtendimentos.Fields.IND_PAC_PEDIATRICO.toString() + " as indPacPediatrico ")
		.append("  , mpt." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString() + " as dtPrevExecucao ")
		.append("  , dmd." + AfaDispensacaoMdtosPai.Fields.DTHR_DISPENSACAO.toString() + " as dtDispensacao ")
		.append("  , pmo." + MptPrescricaoMedicamento.Fields.FREQUENCIA.toString() + " as frequencia ")
		.append("  , pmo." + MptPrescricaoMedicamento.Fields.TIPO_FREQ_APRAZAMENTO_SEQ.toString() + " as tfqSeq ")
		.append("  , pmo." + MptPrescricaoMedicamento.Fields.VIA_ADMINISTRACAO_SIGLA.toString() + " as vadSigla ")
		.append("  , ipmo." + MptItemPrescricaoMedicamento.Fields.MEDICAMENTO_MAT_CODIGO.toString() + " as medMat ")
		.append("  , phi." + FatProcedHospInternosPai.Fields.SEQ.toString() + " as phiSeq ")
		.append("  , unf." + AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString() + " as centroCusto ")
		.append("   , sum(coalesce(dmd." + AfaDispensacaoMdtosPai.Fields.QTDE_SOLICITADA.toString() + ", 0)) as quantidadeSolicitada ")
		.append("  , sum(coalesce(dmd." + AfaDispensacaoMdtosPai.Fields.QTDE_DISPENSADA.toString() + ", 0)) as quantidadeDispensada ")
		.append(" from ")
		.append("  ")
		.append(AghAtendimentos.class.getSimpleName())
		.append(" atd, ")
		.append("  " + MptPrescricaoPaciente.class.getSimpleName() + " mpt, ")
		.append("  " + MptPrescricaoMedicamento.class.getSimpleName() + " pmo, ")
		.append("  " + MptItemPrescricaoMedicamento.class.getSimpleName() + " ipmo, ")
		.append("  " + AfaDispensacaoMdtos.class.getSimpleName() + " dmd, ")
		.append("  " + ScoMaterial.class.getSimpleName() + " mat, ")
		.append("  " + FatProcedHospInternos.class.getSimpleName() + " phi, ")
		.append("  " + AghUnidadesFuncionais.class.getSimpleName() + " unf ")
		.append(" where ")
		.append("  mat." + ScoMaterial.Fields.CODIGO.toString() + " = dmd." + AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString())
		.append("  and mat." + ScoMaterial.Fields.CODIGO.toString() + " = phi." + FatProcedHospInternos.Fields.MAT_CODIGO.toString())
		.append("  and (atd." + AghAtendimentos.Fields.IND_TIPO_TRATAMENTO.toString() + " 	= 27 ")
		.append("       or atd." + AghAtendimentos.Fields.TIPO_TRATAMENTO.toString() + " 	= 19) ")
		.append("  and atd." + AghAtendimentos.Fields.TRP_SEQ.toString() + " is not null ")
		.append("  and atd." + AghAtendimentos.Fields.SEQ.toString() + " = mpt." + MptPrescricaoPaciente.Fields.ATENDIMENTO_SEQ.toString())
		.append("  and mpt." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString() + " between :inicioProcessamento and :fimProcessamento")
		.append("  and mpt." + MptPrescricaoPaciente.Fields.ATD_SEQ.toString() + " = pmo." + MptPrescricaoMedicamento.Fields.PTE_ATD_SEQ.toString())
		.append("  and mpt." + MptPrescricaoPaciente.Fields.SEQ.toString() + " = pmo." + MptPrescricaoMedicamento.Fields.PTE_SEQ.toString())
		.append("  and pmo." + MptPrescricaoMedicamento.Fields.PTE_ATD_SEQ.toString() + " = ipmo."
				+ MptItemPrescricaoMedicamento.Fields.PMO_PTE_ATD_SEQ)
		.append("  and pmo." + MptPrescricaoMedicamento.Fields.PTE_SEQ.toString() + " = ipmo." + MptItemPrescricaoMedicamento.Fields.PMO_PTE_SEQ)
		.append("  and pmo." + MptPrescricaoMedicamento.Fields.SEQ.toString() + " = ipmo." + MptItemPrescricaoMedicamento.Fields.PMO_SEQ)
		.append("  and ipmo." + MptItemPrescricaoMedicamento.Fields.PMO_PTE_ATD_SEQ.toString() + " = dmd."
				+ AfaDispensacaoMdtos.Fields.IMO_PMO_PTE_ATD_SEQ.toString())
		.append("  and ipmo." + MptItemPrescricaoMedicamento.Fields.PMO_PTE_SEQ.toString() + " = dmd."
				+ AfaDispensacaoMdtos.Fields.IMO_PMO_PTE_SEQ.toString())
		.append("  and ipmo." + MptItemPrescricaoMedicamento.Fields.PMO_SEQ.toString() + " = dmd."
				+ AfaDispensacaoMdtosPai.Fields.IMO_PMO_SEQ.toString())
		.append("  and ipmo." + MptItemPrescricaoMedicamento.Fields.SEQP.toString() + " = dmd." + AfaDispensacaoMdtosPai.Fields.IMO_SEQP.toString())
		.append("  and dmd." + AfaDispensacaoMdtosPai.Fields.IND_SITUACAO.toString() + " in ('D','E','C')")
		.append("  and dmd." + AfaDispensacaoMdtosPai.Fields.QTDE_DISPENSADA.toString() + " > 0 ")
		.append("  and dmd." + AfaDispensacaoMdtosPai.Fields.FARMACIA_SEQ.toString() + " = unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL)
		.append("  group by ").append("    atd." + AghAtendimentos.Fields.SEQ.toString())
		.append("  , atd." + AghAtendimentos.Fields.PAC_CODIGO.toString()).append("  , atd." + AghAtendimentos.Fields.TRP_SEQ.toString())
		.append("  , atd." + AghAtendimentos.Fields.IND_PAC_PEDIATRICO.toString())
		.append("  , mpt." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString())
		.append("  , pmo." + MptPrescricaoMedicamento.Fields.FREQUENCIA.toString())
		.append("  , pmo." + MptPrescricaoMedicamento.Fields.TIPO_FREQ_APRAZAMENTO_SEQ.toString())
		.append("  , pmo." + MptPrescricaoMedicamento.Fields.VIA_ADMINISTRACAO_SIGLA.toString())
		.append("  , ipmo." + MptItemPrescricaoMedicamento.Fields.MEDICAMENTO_MAT_CODIGO.toString())
		.append("  , phi." + FatProcedHospInternosPai.Fields.SEQ.toString())
		.append("  , unf." + AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString())
		.append("  , dmd." + AfaDispensacaoMdtosPai.Fields.DTHR_DISPENSACAO.toString()).append("  order by ")
		.append("    atd." + AghAtendimentos.Fields.SEQ.toString()).append("  , atd." + AghAtendimentos.Fields.PAC_CODIGO.toString())
		.append("  , atd." + AghAtendimentos.Fields.TRP_SEQ.toString()).append("  , atd." + AghAtendimentos.Fields.IND_PAC_PEDIATRICO.toString())
		.append("  , mpt." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString())
		.append("  , pmo." + MptPrescricaoMedicamento.Fields.FREQUENCIA.toString())
		.append("  , pmo." + MptPrescricaoMedicamento.Fields.TIPO_FREQ_APRAZAMENTO_SEQ.toString())
		.append("  , pmo." + MptPrescricaoMedicamento.Fields.VIA_ADMINISTRACAO_SIGLA.toString())
		.append("  , ipmo." + MptItemPrescricaoMedicamento.Fields.MEDICAMENTO_MAT_CODIGO.toString())
		.append("  , phi." + FatProcedHospInternosPai.Fields.SEQ.toString())
		.append("  , unf." + AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString())
		.append("  , dmd." + AfaDispensacaoMdtosPai.Fields.DTHR_DISPENSACAO.toString());

		final org.hibernate.Query sqlQuery = createHibernateQuery(sql.toString());
		sqlQuery.setParameter("inicioProcessamento", dataInicio);
		sqlQuery.setParameter("fimProcessamento", dataFim);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SigAtendimentoDialiseVO.class));

		return sqlQuery.list();	
	}
	
	/**
	 * Busca procedimentos e materiais da prescrição de diálise” para obter todos os 
	 * procedimentos e materiais prescritos durante o tratamento de diálise em um atendimento de 
	 * internação ou ambulatorial no período de processamento. 
	 * 
	 * @param dataInicio - inicio do processamento
	 * @param dataFim - fim do processamento
	 */
	@SuppressWarnings("unchecked")
	public List<SigProcedimentoMaterialPrescricaoDialiseVO> buscarProcedimentosEMateriaisDaPrescricaoDeDialise(Date dataInicio, Date dataFim){

		StringBuffer sql = new StringBuffer(" select ")
		.append(" atd.").append(AghAtendimentos.Fields.SEQ.toString()).append(" as atdPaciente ")
		.append(" , atd.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()).append(" as pacCodigo ")
		.append(" , atd.").append(AghAtendimentos.Fields.TRP_SEQ.toString()).append(" as trpSeq ")
		.append(" , mpt.").append(MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString()).append(" as dtPrevExecucao ")
		.append(" , mpp.").append(MptPrescricaoProcedimento.Fields.TIPO.toString()).append(" as tipo")
		.append(" , sum(mpp.").append(MptPrescricaoProcedimento.Fields.QUANTIDADE.toString()).append(") as quantidade ")
		.append(" , mpp.").append(MptPrescricaoProcedimento.Fields.SCO_MATERIAL_CODIGO.toString()).append(" as matCodigo")
		.append(" , phi.").append(FatProcedHospInternosPai.Fields.SEQ.toString()).append(" as phiSeq ")
		.append(" , atd.").append(AghAtendimentos.Fields.UNF_SEQ.toString()).append(" as undAtend ")
		.append(" , unfp.").append(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString()).append(" as cProducao ")
		.append(" , agp.").append(MptAgendaPrescricao.Fields.UNF_SEQ_SEQ.toString()).append(" as undAgenda")
		.append(" , unf.").append(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString()).append(" as centroCusto ")
		.append(" from ")
		.append(AghAtendimentos.class.getSimpleName()).append(" atd ")
		.append(" ,").append(MptPrescricaoPaciente.class.getSimpleName()).append(" mpt ")
		.append(" ,").append(MptAgendaPrescricao.class.getSimpleName()).append(" agp ")
		.append(" ,").append(AghUnidadesFuncionais.class.getSimpleName()).append(" unf ")
		.append(" ,").append(AghUnidadesFuncionais.class.getSimpleName()).append(" unfp ")
		.append(" ,").append(MptPrescricaoProcedimento.class.getSimpleName()).append(" mpp ")
		.append(" ,").append(ScoMaterial.class.getSimpleName()).append(" mat ")
		.append(" ,").append(FatProcedHospInternos.class.getSimpleName()).append(" phi ")
		.append(" where ")
		.append(' ').append(ScoMaterial.Fields.CODIGO.toString()).append(" = mpp.").append(MptPrescricaoProcedimento.Fields.SCO_MATERIAL_CODIGO.toString())
		.append("  and phi.").append(FatProcedHospInternosPai.Fields.MAT_CODIGO.toString()).append(" = ").append(ScoMaterial.Fields.CODIGO.toString())
		.append("  and (atd." + AghAtendimentos.Fields.IND_TIPO_TRATAMENTO.toString()).append(" = 27")
		.append("       or atd." + AghAtendimentos.Fields.TIPO_TRATAMENTO.toString()).append(" = 19)")
		.append("  and atd." + AghAtendimentos.Fields.TRP_SEQ.toString() + " is not null ")
		.append("  and atd." + AghAtendimentos.Fields.SEQ.toString() + " = mpt." + MptPrescricaoPaciente.Fields.ATENDIMENTO_SEQ.toString())
		.append("  and mpt." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString() + " between :dataInicio and :dataFim")
		.append("  and mpt.").append(MptPrescricaoPaciente.Fields.ATENDIMENTO_SEQ.toString()).append(" = agp.").append(MptAgendaPrescricao.Fields.ATD_SEQ.toString())
		.append("  and mpt.").append(MptPrescricaoPaciente.Fields.SEQ.toString()).append(" = agp.").append(MptAgendaPrescricao.Fields.PTE_SEQ.toString())
		.append("  and agp.").append(MptAgendaPrescricao.Fields.IND_SITUACAO.toString()).append(" = '").append(DominioSituacao.A.toString()).append('\'')
		.append("  and agp.").append(MptAgendaPrescricao.Fields.UNF_SEQ_SEQ.toString()).append(" = unf.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())
		.append("  and atd.").append(AghAtendimentos.Fields.UNF_SEQ.toString()).append(" = unfp.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())
		.append("  and mpt.").append(MptPrescricaoPaciente.Fields.ATD_SEQ.toString()).append(" = mpp.").append(MptPrescricaoProcedimento.Fields.MPT_PRESCRICAO_PACIENTE_PTE_ATD_SEQ.toString())
		.append("  and mpt.").append(MptPrescricaoPaciente.Fields.SEQ.toString()).append(" = mpp.").append(MptPrescricaoProcedimento.Fields.MPT_PRESCRICAO_PACIENTE_PTE_SEQ.toString())
		.append("  and mpp.").append(MptPrescricaoProcedimento.Fields.IND_SITUACAO_ITEM.toString()).append(" in ").append("('V','A','E')")
		.append("  group by ")
		.append(" atd.").append(AghAtendimentos.Fields.SEQ.toString())
		.append(" , atd.").append(AghAtendimentos.Fields.PAC_CODIGO.toString())
		.append(" , atd.").append(AghAtendimentos.Fields.TRP_SEQ.toString())
		.append(" , mpt.").append(MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString())
		.append(" , mpp.").append(MptPrescricaoProcedimento.Fields.TIPO.toString()).append(" , mpp.").append(MptPrescricaoProcedimento.Fields.QUANTIDADE.toString())
		.append(" , mpp.").append(MptPrescricaoProcedimento.Fields.SCO_MATERIAL_CODIGO.toString())
		.append(" , phi.").append(FatProcedHospInternosPai.Fields.SEQ.toString())
		.append(" , atd.").append(AghAtendimentos.Fields.UNF_SEQ.toString())
		.append(" , unfp.").append(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString())
		.append(" , agp.").append(MptAgendaPrescricao.Fields.UNF_SEQ_SEQ.toString())
		.append(" , unf.").append(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString())	
		.append(" order by ")
		.append(" atd.").append(AghAtendimentos.Fields.SEQ.toString())
		.append(" , atd.").append(AghAtendimentos.Fields.PAC_CODIGO.toString())
		.append(" , atd.").append(AghAtendimentos.Fields.TRP_SEQ.toString())
		.append(" , mpt.").append(MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString())
		.append(" , mpp.").append(MptPrescricaoProcedimento.Fields.TIPO.toString())
		.append(" , mpp.").append(MptPrescricaoProcedimento.Fields.QUANTIDADE.toString())
		.append(" , mpp.").append(MptPrescricaoProcedimento.Fields.SCO_MATERIAL_CODIGO.toString())
		.append(" , phi.").append(FatProcedHospInternosPai.Fields.SEQ.toString())
		.append(" , atd.").append(AghAtendimentos.Fields.UNF_SEQ.toString())
		.append(" , unfp.").append(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString())
		.append(" , agp.").append(MptAgendaPrescricao.Fields.UNF_SEQ_SEQ.toString())
		.append(" , unf.").append(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString());
		
		final org.hibernate.Query sqlQuery = createHibernateQuery(sql.toString());
		sqlQuery.setParameter("dataInicio", dataInicio);
		sqlQuery.setParameter("dataFim", dataFim);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SigProcedimentoMaterialPrescricaoDialiseVO.class));

		return sqlQuery.list();
	}
	
	public Boolean verificaConsumosContabilizados(SigCategoriaConsumos categoriaConsumo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class);
		criteria.add(Restrictions.eq(SigCalculoAtdConsumo.Fields.CTC_SEQ.toString(), categoriaConsumo.getSeq()));
		return this.executeCriteriaExists(criteria);
	}
	
	
	private void adicionarRestricoesAltaNoMesPendenciaFaturamento(DetachedCriteria criteria, Integer pmuSeq, Date dataInicioProcessamentoAtual, Short pConvenioSus){
		
		//Paciente com alta e estar entre as datas da competência
		Criterion restricaoAltaNoMes = Restrictions.and( 
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA),
				Restrictions.geProperty("atd." + AghAtendimentos.Fields.DTHR_FIM, "pmu."+SigProcessamentoCusto.Fields.DATA_INICIO),
				Restrictions.leProperty("atd." + AghAtendimentos.Fields.DTHR_FIM, "pmu."+SigProcessamentoCusto.Fields.DATA_FIM)
		);
		
		//Faturamento pendente em contas do sus
		DetachedCriteria subCriteriaPendenciaContaSus = DetachedCriteria.forClass(FatContasInternacao.class,"coi")
		.setProjection(Projections.property("coi."+FatContasInternacao.Fields.SEQ))
		.createAlias("coi." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), "cth")
		.add(Restrictions.eqProperty("coi." + FatContasInternacao.Fields.INT_SEQ.toString(), "cac."+SigCalculoAtdPaciente.Fields.INTERNACAO_SEQ))
		.add(Restrictions.eq("cth." + FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus))
		.add(Restrictions.ne("cth." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.O));
		
		//Faturamento pendente em contas de convênios
		DetachedCriteria subCriteriaPendenciaContaConvenio = DetachedCriteria.forClass(CntaConv.class,"cta")
		.setProjection(Projections.property("cta."+CntaConv.Fields.NRO))
		.add(Restrictions.eqProperty("cta." + CntaConv.Fields.ATENDIMENTO_SEQ.toString(), "cac."+SigCalculoAtdPaciente.Fields.ATD_SEQ))
		.add(Restrictions.ne("cta." + CntaConv.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus))
		.add(Restrictions.isNull("cta." + CntaConv.Fields.DATA_COMP.toString()));
		
		criteria.add(
			Restrictions.or(	
				Restrictions.and( 
						Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SEQ.toString(), pmuSeq), 				//Na competência atual
						restricaoAltaNoMes,																			//Alta no mês
						Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.IND_FAT_PENDENTE.toString(), false)	// Não pendente
				),
				Restrictions.and( 
						Restrictions.lt("pmu." + SigProcessamentoCusto.Fields.DATA_FIM.toString(), dataInicioProcessamentoAtual), 	//Competência anterior
						restricaoAltaNoMes,																							//Alta no mês 
						Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.IND_FAT_PENDENTE.toString(), true),					// Ainda marcado como pendente
						Subqueries.notExists(subCriteriaPendenciaContaSus),															// Mas sem conta sus pendente
						Subqueries.notExists(subCriteriaPendenciaContaConvenio)														// e sem conta convênio pendente
				)
			)
		);
	}
	
	// #32240 - C1
	public Map<Integer, List<SigConsumosInternacoesVO> > buscarConsumosRelacionadoInternacoesDeDetermindadoProcessamento(Integer pmuSeq, Date dataInicioProcessamentoAtual, Short pConvenioSus, Boolean isSus) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class, "ccs");
		
		criteria.createAlias("ccs." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString(), "cpp", JoinType.INNER_JOIN);
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac", JoinType.INNER_JOIN);
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO.toString(), "ain", JoinType.INNER_JOIN);
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO_SEQ.toString()), SigConsumosInternacoesVO.Fields.INT_SEQ.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.PHI_SEQ.toString()),SigConsumosInternacoesVO.Fields.PHI_SEQ.toString())
				.add(Projections.property("ccs." + SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()), SigConsumosInternacoesVO.Fields.QTDE.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA_SEQ.toString()),SigConsumosInternacoesVO.Fields.CPP_SEQ.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO_SEQ.toString()),SigConsumosInternacoesVO.Fields.CTC_SEQ.toString())
		        .add(Projections.property("cpp." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO_CODIGO.toString()), SigConsumosInternacoesVO.Fields.CCT_CODIGO.toString())
                .add(Projections.property("ccs." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString()), SigConsumosInternacoesVO.Fields.CCS_CCT_CODIGO.toString())
                .add(Projections.property("cac." + SigCalculoAtdPaciente.Fields.SEQ.toString()), SigConsumosInternacoesVO.Fields.CAC_SEQ.toString())
                .add(Projections.property("cac." + SigCalculoAtdPaciente.Fields.IND_FAT_PENDENTE.toString()), SigConsumosInternacoesVO.Fields.IND_FAT_PENDENTE.toString())
		));
		
		//Demora mais para comparar no banco do filtrar na aplicação
		//criteria.add(Restrictions.isNotNull("ccs." + SigCalculoAtdConsumo.Fields.PHI_SEQ.toString()));
	

		//Não importa a categoria de consumo depois vai agrupar
		//criteria.add(Restrictions.eq("ccs." + SigCalculoAtdConsumo.Fields.CTC_SEQ.toString(), ctcSeq));
		
		this.adicionarRestricoesAltaNoMesPendenciaFaturamento(criteria, pmuSeq, dataInicioProcessamentoAtual, pConvenioSus);
		
		if(isSus) {
			criteria.add(Restrictions.eq("ain." + AinInternacao.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));
		} else {
			criteria.add(Restrictions.ne("ain." + AinInternacao.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));
		}
		
		criteria.addOrder(Order.asc("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO_SEQ.toString()));
		criteria.addOrder(Order.asc("ccs." + SigCalculoAtdConsumo.Fields.PHI_SEQ.toString()));
		criteria.addOrder(Order.asc("ccs." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA_SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SigConsumosInternacoesVO.class));
		
		List<SigConsumosInternacoesVO> lista = this.executeCriteria(criteria);
		
		return this.agruparConsumosInternacoesPorCategoriaConsumo(lista, true);
	}	
	
	// #47286 - C1
	public Map<Integer, List<SigConsumosInternacoesVO>> buscarConsumosRelacionadoOcv(Integer pmuSeq, Date dataInicioProcessamentoAtual, Short pConvenioSus, Boolean isSus) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class, "ccs");
		criteria.createAlias("ccs." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString(), "cpp", JoinType.INNER_JOIN);
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac", JoinType.INNER_JOIN);
		criteria.createAlias("ccs." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.INNER_JOIN);
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.PHI.toString(), "phc", JoinType.INNER_JOIN);
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO.toString(), "ain", JoinType.INNER_JOIN);
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO_SEQ.toString()), SigConsumosInternacoesVO.Fields.INT_SEQ.toString())
				.add(Projections.property("phc."+SigObjetoCustoPhis.Fields.FAT_PHI_SEQ.toString()),SigConsumosInternacoesVO.Fields.PHI_SEQ.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString()),SigConsumosInternacoesVO.Fields.OCV_SEQ.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()),SigConsumosInternacoesVO.Fields.QTDE.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA_SEQ.toString()),SigConsumosInternacoesVO.Fields.CPP_SEQ.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO_SEQ.toString()),SigConsumosInternacoesVO.Fields.CTC_SEQ.toString())
		        .add(Projections.property("cpp." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO_CODIGO.toString()), SigConsumosInternacoesVO.Fields.CCT_CODIGO.toString())
                .add(Projections.property("ccs." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString()), SigConsumosInternacoesVO.Fields.CCS_CCT_CODIGO.toString())
                .add(Projections.property("cac." + SigCalculoAtdPaciente.Fields.SEQ.toString()), SigConsumosInternacoesVO.Fields.CAC_SEQ.toString())
                .add(Projections.property("cac." + SigCalculoAtdPaciente.Fields.IND_FAT_PENDENTE.toString()), SigConsumosInternacoesVO.Fields.IND_FAT_PENDENTE.toString())
		));		
		
		//Não importa a categoria de consumo depois vai agrupar
		//criteria.add(Restrictions.eq("ccs." + SigCalculoAtdConsumo.Fields.CTC_SEQ.toString(), ctcSeq));
		
		//Não precisa verificar se é nulo, já que o join faz isso
		//criteria.add(Restrictions.isNotNull("ccs." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString()));
		
		criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.A));
		
		this.adicionarRestricoesAltaNoMesPendenciaFaturamento(criteria, pmuSeq, dataInicioProcessamentoAtual, pConvenioSus);
		
		if(isSus) {
			criteria.add(Restrictions.eq("ain." + AinInternacao.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));
		} else {
			criteria.add(Restrictions.ne("ain." + AinInternacao.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));
		}
		
		criteria.addOrder(Order.asc("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO_SEQ.toString()));
		criteria.addOrder(Order.asc("phc." + SigObjetoCustoPhis.Fields.FAT_PHI_SEQ.toString()));
		criteria.addOrder(Order.asc("ccs." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString()));
		criteria.addOrder(Order.asc("ccs." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA_SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SigConsumosInternacoesVO.class));
		
		List<SigConsumosInternacoesVO> lista = this.executeCriteria(criteria);
		
		return this.agruparConsumosInternacoesPorCategoriaConsumo(lista, false);
	}	
	
	// #47286 - C2
	public Map<Integer, List<SigConsumosInternacoesVO>> buscarConsumosContendoMaterial(Integer pmuSeq, Date dataInicioProcessamentoAtual, Short pConvenioSus) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class, "ccs");
		criteria.createAlias("ccs." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString(), "cpp", JoinType.INNER_JOIN);
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac", JoinType.INNER_JOIN);
		criteria.createAlias("ccs." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.INNER_JOIN);
		criteria.createAlias("ccs." + SigCalculoAtdConsumo.Fields.CALCULO_DETALHE_CONSUMO.toString(), "cdc", JoinType.INNER_JOIN);
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO.toString(), "ain", JoinType.INNER_JOIN);
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);
        criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu", JoinType.INNER_JOIN);

		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("cac."+SigCalculoAtdPaciente.Fields.INTERNACAO_SEQ.toString()),SigConsumosInternacoesVO.Fields.INT_SEQ.toString())
				.add(Projections.property("cdc."+SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()),SigConsumosInternacoesVO.Fields.MAT_CODIGO.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString()),SigConsumosInternacoesVO.Fields.OCV_SEQ.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.QUANTIDADE.toString()),SigConsumosInternacoesVO.Fields.QTDE.toString())
				.add(Projections.property("cac."+SigCalculoAtdPaciente.Fields.PMU_SEQ.toString()),SigConsumosInternacoesVO.Fields.PMU_SEQ.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA_SEQ.toString()),SigConsumosInternacoesVO.Fields.CPP_SEQ.toString())
				.add(Projections.property("ccs."+SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO_SEQ.toString()),SigConsumosInternacoesVO.Fields.CTC_SEQ.toString())
		        .add(Projections.property("cpp."+SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO_CODIGO.toString()),SigConsumosInternacoesVO.Fields.CCT_CODIGO.toString())
                .add(Projections.property("ccs." + SigCalculoAtdConsumo.Fields.CENTRO_CUSTO_CODIGO.toString()), SigConsumosInternacoesVO.Fields.CCS_CCT_CODIGO.toString())));
		
		criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.A));
		criteria.add(Restrictions.isNotNull("ccs." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString()));
		criteria.add(Restrictions.isNotNull("cdc." + SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()));
		
		//Não importa a categoria de consumo depois vai agrupar
		//criteria.add(Restrictions.eq("ccs." + SigCalculoAtdConsumo.Fields.CTC_SEQ.toString(), ctcSeq));
		
		this.adicionarRestricoesAltaNoMesPendenciaFaturamento(criteria, pmuSeq, dataInicioProcessamentoAtual, pConvenioSus);
		
        criteria.add(Restrictions.ne("ain." + AinInternacao.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));

		criteria.addOrder(Order.asc("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO_SEQ.toString()));
		criteria.addOrder(Order.asc("cdc." + SigCalculoDetalheConsumo.Fields.MATERIAL_CODIGO.toString()));
		criteria.addOrder(Order.asc("ccs." + SigCalculoAtdConsumo.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString()));
		criteria.addOrder(Order.asc("ccs." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA_SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SigConsumosInternacoesVO.class));
		
		List<SigConsumosInternacoesVO> lista = this.executeCriteria(criteria);
		
		return this.agruparConsumosInternacoesPorCategoriaConsumo(lista, false);
	}	
	
	/**
	 * Metódo que recebe a lista de consumos das internações e agrupa esses registros pelas categorias de consumo
	 * @param lista
	 * @param verificarPhiNotNull
	 * @return
	 */
	private Map<Integer, List<SigConsumosInternacoesVO>> agruparConsumosInternacoesPorCategoriaConsumo(List<SigConsumosInternacoesVO> lista, boolean verificarPhiNotNull){
		
		Map<Integer, List<SigConsumosInternacoesVO>> map = new HashMap<Integer, List<SigConsumosInternacoesVO>>();
		
		for (SigConsumosInternacoesVO vo : lista) {
			
			if( !verificarPhiNotNull || (verificarPhiNotNull && vo.getPhiSeq() != null) ){
				
				if(!map.containsKey(vo.getCtcSeq())){
					map.put(vo.getCtcSeq(), new ArrayList<SigConsumosInternacoesVO>());
				}
				
				map.get(vo.getCtcSeq()).add(vo);
			}
		}	
		return map;
	}
	
	public List<SigCalculoAtdConsumo> buscarConsumos(Integer pmuSeq, Integer ctcSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class, "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString(), "cpp", JoinType.INNER_JOIN);
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		criteria.add(Restrictions.eq("cca." + SigCalculoAtdConsumo.Fields.CTC_SEQ.toString(), ctcSeq));
		return this.executeCriteria(criteria);
	}
	
	public Integer obterPhiPorPciSeq(Integer pciSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospInternos.class, "phi");
		criteria.setProjection(Projections.max(FatProcedHospInternos.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq("phi."+FatProcedHospInternos.Fields.PCI_SEQ.toString(), pciSeq));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
}
