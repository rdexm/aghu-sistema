package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.LockOptions;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoMedicamento;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmTipoEsquemaTb;
import br.gov.mec.aghu.model.MpmTrocaEsquemaTb;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.MpmJustificativaUsoMdtoVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MpmJustificativaUsoMdtoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmJustificativaUsoMdto> {

	private static final long serialVersionUID = -7323753262847212426L;

	private static final String INTER = "INTER";
	private static final String INTER_DOT = "INTER.";
	private static final String TET = "TET";
	private static final String TET_DOT = "TET.";
	private static final String CLC = "CLC";
	private static final String CLC_DOT = "CLC.";
	private static final String TEB = "TEB";
	private static final String TEB_DOT = "TEB.";
	private static final String ESP = "ESP";
	private static final String ESP_DOT = "ESP.";
	private static final String ATD = "ATD";
	private static final String ATD_DOT = "ATD.";
	private static final String PAC = "PAC";
	private static final String PAC_DOT = "PAC.";
	private static final String JUM = "JUM";
	private static final String JUM_DOT = "JUM.";
	private static final String GUP = "GUP";
	private static final String GUP_DOT = "GUP.";
	private static final String SER = "SER";
	private static final String SER_DOT = "SER.";
	private static final String PES = "PES";
	private static final String UNF = "UNF";
	private static final String LTO = "LTO";
	private static final String QRT = "QRT";
	private static final String SRV = "SRV";

	public MpmJustificativaUsoMdto obterJustificativaUsoMdtos(Integer seq) {
		return this.obterPorChavePrimaria(seq);
	}

	public List<MpmJustificativaUsoMdto> obterJustificativaParaConfirmacao(MpmPrescricaoMedica prescricao,Date data) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaUsoMdto.class, JUM);
		criteria.createAlias(MpmJustificativaUsoMdto.Fields.ATENDIMENTO.toString(), ATD);

		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SITUACAO.toString(),DominioSituacaoSolicitacaoMedicamento.P));
		criteria.add(Restrictions.ge(JUM_DOT + MpmJustificativaUsoMdto.Fields.CRIADO_EM.toString(), data));
		criteria.add(Restrictions.eq(ATD_DOT + AghAtendimentos.Fields.SEQ.toString(),prescricao.getId().getAtdSeq()));
		

		return executeCriteria(criteria);
	}

	/**
	 * #45269 - Carrega array de objetos do Relatorio de Avaliação de Medicamentos
	 * @param solicitacao Seq da Justificativa
	 * @return Lista de Objetos
	 */
	public Object[] obterAvaliacaoMedicamento(Integer solicitacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaUsoMdto.class, JUM);
		
		criteria.createAlias(JUM_DOT + MpmJustificativaUsoMdto.Fields.GRUPO_USO_MEDICAMENTO.toString(), GUP, JoinType.INNER_JOIN);
		criteria.createAlias(JUM_DOT + MpmJustificativaUsoMdto.Fields.ATENDIMENTO.toString(), ATD, JoinType.INNER_JOIN);
		criteria.createAlias(ATD_DOT + AghAtendimentos.Fields.PACIENTE.toString(), PAC, JoinType.INNER_JOIN);
		criteria.createAlias(ATD_DOT + AghAtendimentos.Fields.INTERNACAO.toString(), INTER, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ATD_DOT + AghAtendimentos.Fields.ESPECIALIDADE.toString(), ESP, JoinType.INNER_JOIN);
		criteria.createAlias(ESP_DOT + AghEspecialidades.Fields.CLINICA.toString(), CLC, JoinType.INNER_JOIN);
		criteria.createAlias(JUM_DOT + MpmJustificativaUsoMdto.Fields.TROCA_ESQUEMA_TB.toString(), TEB, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(TEB_DOT + MpmTrocaEsquemaTb.Fields.MPM_TIPO_ESQUEMA_TBS.toString(), TET, JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString()), MpmJustificativaUsoMdto.Fields.SEQ.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.GUP_SEQ.toString()), MpmJustificativaUsoMdto.Fields.GUP_SEQ.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.GESTANTE.toString()), MpmJustificativaUsoMdto.Fields.GESTANTE.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.FUNC_RENAL_COMPROMETIDA.toString()), MpmJustificativaUsoMdto.Fields.FUNC_RENAL_COMPROMETIDA.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.PAC_IMUNODEPRIMIDO.toString()), MpmJustificativaUsoMdto.Fields.PAC_IMUNODEPRIMIDO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.INDICACAO.toString()), MpmJustificativaUsoMdto.Fields.INDICACAO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.INFECCAO_TRATAR.toString()), MpmJustificativaUsoMdto.Fields.INFECCAO_TRATAR.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.TRAT_ANTIMICROB_ANT.toString()), MpmJustificativaUsoMdto.Fields.TRAT_ANTIMICROB_ANT.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.INTERNACAO_PREVIA.toString()), MpmJustificativaUsoMdto.Fields.INTERNACAO_PREVIA.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.INICIO_INFECCAO.toString()), MpmJustificativaUsoMdto.Fields.INICIO_INFECCAO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.INFEC_REL_PROCED_INVASIVO.toString()), MpmJustificativaUsoMdto.Fields.INFEC_REL_PROCED_INVASIVO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.SONDA_VESICAL_DEMORA.toString()), MpmJustificativaUsoMdto.Fields.SONDA_VESICAL_DEMORA.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.PESO_ESTIMADO.toString()), MpmJustificativaUsoMdto.Fields.PESO_ESTIMADO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.CONDUTA_BASE_PROT_ASSIST.toString()), MpmJustificativaUsoMdto.Fields.CONDUTA_BASE_PROT_ASSIST.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.INSUF_HEPATICA.toString()), MpmJustificativaUsoMdto.Fields.INSUF_HEPATICA.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.VANTAGEM_NS_PADRONIZACAO.toString()), MpmJustificativaUsoMdto.Fields.VANTAGEM_NS_PADRONIZACAO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.USO_CRONICO_PREV_INT.toString()), MpmJustificativaUsoMdto.Fields.USO_CRONICO_PREV_INT.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.CUSTO_DIARIO_EST_REAL.toString()), MpmJustificativaUsoMdto.Fields.CUSTO_DIARIO_EST_REAL.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.REF_BIBLIOGRAFICAS.toString()), MpmJustificativaUsoMdto.Fields.REF_BIBLIOGRAFICAS.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.DIAGNOSTICO.toString()), MpmJustificativaUsoMdto.Fields.DIAGNOSTICO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.ECOG.toString()), MpmJustificativaUsoMdto.Fields.ECOG.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.INTENCAO_TRAT.toString()), MpmJustificativaUsoMdto.Fields.INTENCAO_TRAT.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.LINHA_TRAT.toString()), MpmJustificativaUsoMdto.Fields.LINHA_TRAT.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.TRAT_ANT_CIRURGIA.toString()), MpmJustificativaUsoMdto.Fields.TRAT_ANT_CIRURGIA.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.TRAT_ANT_RADIO.toString()), MpmJustificativaUsoMdto.Fields.TRAT_ANT_RADIO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.TRAT_ANT_QUIMIO.toString()), MpmJustificativaUsoMdto.Fields.TRAT_ANT_QUIMIO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.MES_ANO_ULT_CICLO.toString()), MpmJustificativaUsoMdto.Fields.MES_ANO_ULT_CICLO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.TRAT_ANT_HORMONIO.toString()), MpmJustificativaUsoMdto.Fields.TRAT_ANT_HORMONIO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.TRAT_ANT_OUTROS.toString()), MpmJustificativaUsoMdto.Fields.TRAT_ANT_OUTROS.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.CRIADO_EM.toString()), MpmJustificativaUsoMdto.Fields.CRIADO_EM.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.TIPO_INFECCAO.toString()), MpmJustificativaUsoMdto.Fields.TIPO_INFECCAO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.SIT_ANTIBIOGRAMA.toString()), MpmJustificativaUsoMdto.Fields.SIT_ANTIBIOGRAMA.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.NOME_GERME.toString()), MpmJustificativaUsoMdto.Fields.NOME_GERME.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.SENSIBILIDADE_ANTIBIOTICO.toString()), MpmJustificativaUsoMdto.Fields.SENSIBILIDADE_ANTIBIOTICO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.ORIENTACAO_AVALIADOR.toString()), MpmJustificativaUsoMdto.Fields.ORIENTACAO_AVALIADOR.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.JUSTIFICATIVA.toString()), MpmJustificativaUsoMdto.Fields.JUSTIFICATIVA.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.SITUACAO.toString()), MpmJustificativaUsoMdto.Fields.SITUACAO.toString());
		projection.add(Projections.property(PAC_DOT + AipPacientes.Fields.PRONTUARIO.toString()), AipPacientes.Fields.PRONTUARIO.toString());
		projection.add(Projections.property(PAC_DOT + AipPacientes.Fields.NOME.toString()), AipPacientes.Fields.NOME.toString());
		projection.add(Projections.property(PAC_DOT + AipPacientes.Fields.SEXO.toString()), AipPacientes.Fields.SEXO.toString());
		projection.add(Projections.property(PAC_DOT + AipPacientes.Fields.DATA_NASCIMENTO.toString()), AipPacientes.Fields.DATA_NASCIMENTO.toString());
		projection.add(Projections.property(ATD_DOT + AghAtendimentos.Fields.LTO_LTO_ID.toString()), AghAtendimentos.Fields.LTO_LTO_ID.toString());
		projection.add(Projections.property(ATD_DOT + AghAtendimentos.Fields.QRT_NUMERO.toString()), AghAtendimentos.Fields.QRT_NUMERO.toString());
		projection.add(Projections.property(ATD_DOT + AghAtendimentos.Fields.UNF_SEQ.toString()), AghAtendimentos.Fields.UNF_SEQ.toString());
		projection.add(Projections.property(ATD_DOT + AghAtendimentos.Fields.INT_SEQ.toString()), AghAtendimentos.Fields.INT_SEQ.toString());
		projection.add(Projections.property(ATD_DOT + AghAtendimentos.Fields.ATU_SEQ.toString()), AghAtendimentos.Fields.ATU_SEQ.toString());
		projection.add(Projections.property(ATD_DOT + AghAtendimentos.Fields.HOD_SEQ.toString()), AghAtendimentos.Fields.HOD_SEQ.toString());
		projection.add(Projections.property(ATD_DOT + AghAtendimentos.Fields.SEQ.toString()), AghAtendimentos.Fields.SEQ.toString());
		projection.add(Projections.property(ATD_DOT + AghAtendimentos.Fields.SER_MATRICULA.toString()), AghAtendimentos.Fields.SER_MATRICULA.toString());
		projection.add(Projections.property(ATD_DOT + AghAtendimentos.Fields.SER_VIN_CODIGO.toString()), AghAtendimentos.Fields.SER_VIN_CODIGO.toString());
		projection.add(Projections.property(CLC_DOT + AghClinicas.Fields.DESCRICAO.toString()), AghClinicas.Fields.DESCRICAO.toString());
		projection.add(Projections.property(TET_DOT + MpmTipoEsquemaTb.Fields.DESCRICAO.toString()), MpmTipoEsquemaTb.Fields.DESCRICAO.toString());
		projection.add(Projections.property(TEB_DOT + MpmTrocaEsquemaTb.Fields.DESCRICAO.toString()), MpmTrocaEsquemaTb.Fields.DESCRICAO.toString());
		projection.add(Projections.property(INTER_DOT + AinInternacao.Fields.DATA_PREV_ALTA.toString()), AinInternacao.Fields.DATA_PREV_ALTA.toString());
		projection.add(Projections.property(GUP_DOT + AfaGrupoUsoMedicamento.Fields.RESPONSAVEL_AVALIACAO.toString()), AfaGrupoUsoMedicamento.Fields.RESPONSAVEL_AVALIACAO.toString());

		criteria.setProjection(projection);
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString(), solicitacao));
		
		return (Object[]) executeCriteriaUniqueResult(criteria);
}

	/**
	 * Obtém as informações da justificativa de uso de medicamento, conforme código de justificativa informado.
	 * 
	 * @param jumSeq - Código da justificativa
	 * @return Dados da Justificativa solicitada
	 */
	public MpmJustificativaUsoMdto obterDadosJustificativaUsoMedicamento(Integer jumSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaUsoMdto.class, JUM);

		criteria.createAlias(JUM_DOT + MpmJustificativaUsoMdto.Fields.ATENDIMENTO.toString(), ATD, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ATD_DOT + AghAtendimentos.Fields.ESPECIALIDADE.toString(), ESP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ESP_DOT + AghEspecialidades.Fields.CLINICA.toString(), CLC, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ATD_DOT + AghAtendimentos.Fields.SERVIDOR.toString(), SER, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SER_DOT + RapServidores.Fields.PESSOA_FISICA.toString(), PES, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ATD_DOT + AghAtendimentos.Fields.PACIENTE.toString(), PAC, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ATD_DOT + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), UNF, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ATD_DOT + AghAtendimentos.Fields.LEITO.toString(), LTO, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ATD_DOT + AghAtendimentos.Fields.QUARTO.toString(), QRT, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(JUM_DOT + MpmJustificativaUsoMdto.Fields.SERVIDOR_VALIDA.toString(), SRV, JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString(), jumSeq));

		List<MpmJustificativaUsoMdto> retorno = executeCriteria(criteria);

		if (retorno.isEmpty()) {
			return null;
		}

		return retorno.get(0);
	}

	public List<MpmJustificativaUsoMdto> obterJustificativaUsoMedicamento(Integer seqSolicitacao, Date dataInicio, Date dataFim, 
			DominioIndRespAvaliacao indResponsavelAvaliacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaUsoMdto.class, JUM);
		criteria.createAlias(JUM_DOT + MpmJustificativaUsoMdto.Fields.GRUPO_USO_MEDICAMENTO.toString(), GUP, JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SITUACAO.toString(), DominioSituacaoSolicitacaoMedicamento.P));

		if(seqSolicitacao != null){
			criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString(), seqSolicitacao));
		}else{
			criteria.add(Restrictions.between(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString(), 1, 999999999));
		}

		if(dataInicio != null){
			criteria.add(Restrictions.ge(JUM_DOT + MpmJustificativaUsoMdto.Fields.CRIADO_EM.toString(), dataInicio));
		}else{
			criteria.add(Restrictions.ge(JUM_DOT + MpmJustificativaUsoMdto.Fields.CRIADO_EM.toString(), subtrairDiasData()));
		}

		if(dataFim != null){
			criteria.add(Restrictions.lt(JUM_DOT + MpmJustificativaUsoMdto.Fields.CRIADO_EM.toString(), DateUtil.adicionaDias(dataFim, 1)));
		}else{
			criteria.add(Restrictions.lt(JUM_DOT + MpmJustificativaUsoMdto.Fields.CRIADO_EM.toString(), new Date()));
		}

		if(indResponsavelAvaliacao != null){
			criteria.add(Restrictions.eq(GUP_DOT + AfaGrupoUsoMedicamento.Fields.RESPONSAVEL_AVALIACAO.toString(), indResponsavelAvaliacao));
		}

		return executeCriteria(criteria);
	}
	
	private Date subtrairDiasData(){
		Date dataAtual = new Date();
		
		Calendar calendarData = Calendar.getInstance();
		calendarData.setTime(dataAtual);
		
		calendarData.add(Calendar.DATE, -60);
		
		return calendarData.getTime();
	}
	
	public void obterJustificativaUsoMdto(Integer seq) {
		this.getAndLock(seq, LockOptions.UPGRADE);
	}
	
	/**
	 * Obtem {@link MpmJustificativaUsoMdto} com {@link AghAtendimentos} por Id e Situacao
	 */
	public MpmJustificativaUsoMdto obterPorIdSituacao(Integer seq, DominioSituacaoSolicitacaoMedicamento situacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaUsoMdto.class);
		criteria.createAlias(MpmJustificativaUsoMdto.Fields.ATENDIMENTO.toString(), ATD, JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(MpmJustificativaUsoMdto.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MpmJustificativaUsoMdto.Fields.SITUACAO.toString(), situacao));
		
		return (MpmJustificativaUsoMdto) executeCriteriaUniqueResult(criteria);
	}

	public MpmJustificativaUsoMdtoVO obterJustificativaUsoMdtoVO(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaUsoMdto.class);

		criteria.setProjection(Projections.projectionList()
						.add(Projections.property(MpmJustificativaUsoMdto.Fields.SEQ.toString()), MpmJustificativaUsoMdtoVO.Fields.SEQ.toString())
						.add(Projections.property(MpmJustificativaUsoMdto.Fields.ATD_SEQ.toString()), MpmJustificativaUsoMdtoVO.Fields.ATD_SEQ.toString())
						.add(Projections.property(MpmJustificativaUsoMdto.Fields.GUP_SEQ.toString()), MpmJustificativaUsoMdtoVO.Fields.GUP_SEQ.toString())
						.add(Projections.property(MpmJustificativaUsoMdto.Fields.SITUACAO.toString()), MpmJustificativaUsoMdtoVO.Fields.SITUACAO.toString())
						.add(Projections.property(MpmJustificativaUsoMdto.Fields.CAND_APROV_LOTE.toString()), MpmJustificativaUsoMdtoVO.Fields.CAND_APROV_LOTE.toString())
						.add(Projections.property(MpmJustificativaUsoMdto.Fields.GESTANTE.toString()), MpmJustificativaUsoMdtoVO.Fields.GESTANTE.toString())
						.add(Projections.property(MpmJustificativaUsoMdto.Fields.CRIADO_EM.toString()), MpmJustificativaUsoMdtoVO.Fields.CRIADO_EM.toString())
						.add(Projections.property(MpmJustificativaUsoMdto.Fields.ALTERADO_EM.toString()), MpmJustificativaUsoMdtoVO.Fields.ALTERADO_EM.toString())
						.add(Projections.property(MpmJustificativaUsoMdto.Fields.SER_MATRICULA_CONHECIMENTO.toString()), MpmJustificativaUsoMdtoVO.Fields.SER_MATRICULA_CONHECIMENTO.toString())
						.add(Projections.property(MpmJustificativaUsoMdto.Fields.SER_VIN_CODIGO_CONHECIMENTO.toString()), MpmJustificativaUsoMdtoVO.Fields.SER_VIN_CODIGO_CONHECIMENTO.toString())
					);
		
		criteria.add(Restrictions.eq(MpmJustificativaUsoMdto.Fields.SEQ.toString(), seq));

		criteria.setResultTransformer(Transformers.aliasToBean(MpmJustificativaUsoMdtoVO.class));
		
		return (MpmJustificativaUsoMdtoVO) executeCriteriaUniqueResult(criteria);
	}
}
