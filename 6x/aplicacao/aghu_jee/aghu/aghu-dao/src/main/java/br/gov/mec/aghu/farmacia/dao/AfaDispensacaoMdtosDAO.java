package br.gov.mec.aghu.farmacia.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.vo.AfaDispensacaoMdtoVO;
import br.gov.mec.aghu.farmacia.vo.ListaOcorrenciaVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoDispensadoPorBoxVO;
import br.gov.mec.aghu.farmacia.vo.TicketMdtoDispensadoVO;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaDispensacaoMdtosPai;
import br.gov.mec.aghu.model.AfaGrupoApresMdtos;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaItemGrupoApresMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalente;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VMpmMdtosDescr;

@SuppressWarnings({ "PMD.ExcessiveClassLength","PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class AfaDispensacaoMdtosDAO extends BaseDao<AfaDispensacaoMdtos> {

	
	
	private static final long serialVersionUID = -3818667573543893268L;

	private DetachedCriteria obterDetachedCriteriaAfaDispensacaoMdtos(){
		return DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
	}
	
	public AfaDispensacaoMdtos obterDispensacaoMdtosPorItemPrescricaoMdtoQtdSolicitada(MpmItemPrescricaoMdtoId itemPrescricaoMdtoId) {

		DetachedCriteria criteria = this.obterDetachedCriteriaAfaDispensacaoMdtos();
		
		criteria.createAlias(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO.toString(), AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO.toString());
		
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO.toString()+"."+MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ,
				itemPrescricaoMdtoId.getPmdAtdSeq()));
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO.toString()+"."+MpmItemPrescricaoMdto.Fields.PMD_SEQ,
				itemPrescricaoMdtoId.getPmdSeq()));
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO.toString()+"."+MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO,
				itemPrescricaoMdtoId.getMedMatCodigo()));
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO.toString()+"."+MpmItemPrescricaoMdto.Fields.SEQP,
				itemPrescricaoMdtoId.getSeqp()));
		criteria.add(Restrictions.isNotNull(AfaDispensacaoMdtos.Fields.QTDE_SOLICITADA.toString()));
		
		List<AfaDispensacaoMdtos> list = executeCriteria(criteria);
		return !list.isEmpty() ? list.get(0) : null;

	}
	

	/**
	 * Executa a consulta fazendo join de AfaDispensacaoMdtos com procedimentoHospInterno
	 */
	@SuppressWarnings("unchecked")
	public List<AfaDispensacaoMdtoVO> obterDispensacaoMdtosPorAtendimentoEDataCriacaoEntreDataIntEDataAlta(Integer atdSeq, Date dataInt, Date dataAlta){
		StringBuffer hql = new StringBuffer(750);
		
		hql.append(" SELECT AFA."+AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString()+" as ").append(AfaDispensacaoMdtoVO.Fields.MED_MAT_COD.toString()).append(", ")
		   .append("        PHI."+FatProcedHospInternos.Fields.SEQ.toString()+" as  ").append(AfaDispensacaoMdtoVO.Fields.PHI_SEQ.toString()).append(", ")
		   .append("        PHI."+FatProcedHospInternos.Fields.TIPO_OPER_CONVERSAO.toString()+" as ").append(AfaDispensacaoMdtoVO.Fields.TIPO_OPER_CONVERSAO.toString()).append(", ")
		   .append("        PHI."+FatProcedHospInternos.Fields.FATOR_CONVERSAO.toString()+" as  ").append(AfaDispensacaoMdtoVO.Fields.FATOR_CONVERSAO.toString()).append(", ")
		   .append("        SUM(AFA."+AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString()+") as  ").append(AfaDispensacaoMdtoVO.Fields.QUANTIDADE.toString()).append(' ')
		   
		   .append("   FROM ").append(FatProcedHospInternos.class.getSimpleName()).append(" AS PHI, ")
		   .append("        ").append(AfaDispensacaoMdtos.class.getSimpleName()).append(" AS AFA, ")
		   .append("        ").append(FatItensProcedHospitalar.class.getSimpleName()).append(" AS IPH, ")
		   .append("        ").append(VFatAssociacaoProcedimento.class.getSimpleName()).append(" AS V_FAT ")
		   
		   .append("   WHERE ")
		   .append("            IPH."+FatItensProcedHospitalar.Fields.SEQ.toString()+ " = V_FAT."+VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString())
		   .append("        AND IPH."+FatItensProcedHospitalar.Fields.PHO_SEQ.toString()+" = V_FAT."+VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString())
		   .append("        AND IPH."+FatItensProcedHospitalar.Fields.IND_PROC_ESPECIAL.toString()+" = :indProceEspecial" )
		   .append("        AND V_FAT."+VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString()+" = 1" )
		   .append("        AND V_FAT."+VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString()+" = 1" )
		   .append("        AND V_FAT."+VFatAssociacaoProcedimento.Fields.PHI_IND_SITUACAO.toString()+" = :indSituacao" )
		   .append("        AND V_FAT."+VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()+" = PHI."+FatProcedHospInternos.Fields.SEQ.toString())
		   .append("        AND V_FAT."+VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString()+" = 12" )
		   .append("        AND PHI."+FatProcedHospInternos.Fields.SITUACAO.toString()+ " = :situacao ")
		   .append("        AND PHI."+FatProcedHospInternos.Fields.MAT_CODIGO.toString()+" = AFA."+AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString())
		   .append("        AND AFA."+AfaDispensacaoMdtos.Fields.CRIADO_EM.toString()+" BETWEEN :dataInt AND :dataAlta ")
		
		   // Milena devido a situação triado que também não deve ser considerada
		   //.append("        AND decode(afa.IND_SITUACAO,'S','N','T','N','S') =  :indSituacaoDispensacao ")
		   .append("        AND ( CASE WHEN AFA."+AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString()+" = 'S' THEN 'N' ")
		   .append("               	   WHEN AFA."+AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString()+" = 'T' THEN 'N' ")
		   .append("                   ELSE 'S'")
		   .append(" 		       END ")
		   .append(" 		    ) = :indSituacaoDispensacao ")		   

		   .append("        AND AFA."+AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ.toString()+" = :atdSeq ")
		   
		   .append("  GROUP BY AFA."+AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString()+
				   		    ", PHI."+FatProcedHospInternos.Fields.SEQ.toString()+
				   		    ", PHI."+FatProcedHospInternos.Fields.TIPO_OPER_CONVERSAO.toString() +
				   		    ", PHI."+FatProcedHospInternos.Fields.FATOR_CONVERSAO.toString())
		   
		   .append("  HAVING SUM(AFA."+AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString()+") > 0 ");
		
		
		Query q = createHibernateQuery(hql.toString());
		q.setTimestamp("dataInt", dataInt);
		q.setTimestamp("dataAlta", dataAlta);
		q.setInteger("atdSeq", atdSeq);
		q.setString("situacao", DominioSituacao.A.toString());
		q.setString("indSituacao", DominioSituacao.A.toString());
		q.setString("indProceEspecial", DominioSimNao.S.toString());
		q.setString("indSituacaoDispensacao", DominioSituacaoDispensacaoMdto.S.toString());
		
		q.setResultTransformer(Transformers.aliasToBean(AfaDispensacaoMdtoVO.class));
		
		return q.list();
		

	}
	
	
	/**
	 * Obtem a descrição de um registro salvo no banco
	 * @param seq
	 * @return
	 */
	public Boolean verificarExisteDispensacaoPorOcorrencia(AfaTipoOcorDispensacao ocorrencia) {
		Boolean retorno = false;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString(),ocorrencia));

		List<AfaDispensacaoMdtos> listaDispMed = this.executeCriteria(criteria);
		
		if (listaDispMed.size() > 0){
			retorno = true;
		}

		return retorno;
	}

	/**
	 * Pesquisa pelos códigos dos medicamentos de dispensação
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param atdSeqPrescricao
	 * @param seqPrescricao
	 * @return List<AfaDispensacaoMdtos>
	 */
	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtosPorPrescricao(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			MpmPrescricaoMedica prescricaoMedica, Short unfSeq){
		
		DetachedCriteria criteria = obterCriteriaDispensacaoMdtosPorPrescricao(prescricaoMedica);
		criteria.add(Restrictions.ne(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), DominioSituacaoDispensacaoMdto.T));
		
		if(unfSeq != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.FARMACIA_SEQ.toString(), unfSeq));
		}
		
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtosseqPrescricaoNaoEletronica(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, Long seqPrescricaoNaoEletronica){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO_SEQ.toString(), seqPrescricaoNaoEletronica));
		criteria.add(Restrictions.ne(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), DominioSituacaoDispensacaoMdto.T));
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarDispensacaoMdtosseqPrescricaoNaoEletronicaCount(Long seqPrescricaoNaoEletronica){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO_SEQ.toString(), seqPrescricaoNaoEletronica));
		criteria.add(Restrictions.ne(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), DominioSituacaoDispensacaoMdto.T));
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Obtém o número de registros retornados
	 * @param atdSeqPrescricao
	 * @param seqPrescricao
	 * @return
	 */
	public Long pesquisarDispensacaoMdtosCount(MpmPrescricaoMedica prescricaoMedica, Short unfSeq){
		
		DetachedCriteria criteria = obterCriteriaDispensacaoMdtosPorPrescricao(prescricaoMedica);
		criteria.add(Restrictions.ne(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), DominioSituacaoDispensacaoMdto.T));
		
		if(unfSeq != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.FARMACIA_SEQ.toString(), unfSeq));
		}

		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaDispensacaoMdtosPorPrescricao(MpmPrescricaoMedica prescricaoMedica){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString(), prescricaoMedica));
				
		return criteria;
	}
	
	// Estória #5387
	public Long pesquisarItensDispensacaoMdtosCount(
			AghUnidadesFuncionais unidadeSolicitante, Integer prontuario,
			String nomePaciente, Date dtHrInclusaoItem,
			AfaMedicamento medicamento,
			DominioSituacaoDispensacaoMdto situacao,
			AghUnidadesFuncionais farmacia, AghAtendimentos aghAtendimentos,
			MpmPrescricaoMedica prescricaoMedica, String loteCodigo, Boolean indPmNaoEletronica) {
		
		DetachedCriteria criteria = obterCriteriaItensDispensacaoMdtosPorFiltro(
				unidadeSolicitante, prontuario, nomePaciente, dtHrInclusaoItem,
				medicamento, situacao, farmacia, aghAtendimentos,
				prescricaoMedica, loteCodigo, indPmNaoEletronica);
		
			return executeCriteriaCount(criteria);
	}
	
	// Estória #5387
	public List<AfaDispensacaoMdtos> pesquisarItensDispensadosPorFiltro(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeSolicitante,
			Integer prontuario, String nomePaciente, Date dtHrInclusaoItem,
			AfaMedicamento medicamento,
			DominioSituacaoDispensacaoMdto situacao,
			AghUnidadesFuncionais farmacia, AghAtendimentos aghAtendimentos,
			MpmPrescricaoMedica prescricaoMedica, String loteCodigo, Boolean indPmNaoEletronica) {
		
		DetachedCriteria criteria = obterCriteriaItensDispensacaoMdtosPorFiltro(
				unidadeSolicitante, prontuario, nomePaciente, dtHrInclusaoItem,
				medicamento, situacao, farmacia, aghAtendimentos,
				prescricaoMedica, loteCodigo, indPmNaoEletronica);
		criteria.addOrder(Order.asc(AfaDispensacaoMdtos.Fields.CRIADO_EM.toString()));
			
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		
	}
	
	// Estória #5387
	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria obterCriteriaItensDispensacaoMdtosPorFiltro(
			AghUnidadesFuncionais unidadeSolicitante, Integer prontuario,
			String nomePaciente, Date dtHrInclusaoItem,
			AfaMedicamento medicamento,
			DominioSituacaoDispensacaoMdto situacao,
			AghUnidadesFuncionais farmacia, AghAtendimentos aghAtendimentos,
			MpmPrescricaoMedica prescricaoMedica, String loteCodigo, Boolean indPmNaoEletronica) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString(), "unid_sol", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), "mdto", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.FARMACIA.toString(), "unf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mdto."+AfaMedicamento.Fields.TPR.toString(), "tpr", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString(), "tod", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.ATENDIMENTO.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
		
		if(aghAtendimentos != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ATENDIMENTO.toString(), aghAtendimentos));
		}
		
		if(unidadeSolicitante != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString(), unidadeSolicitante));
		}
		
		if(StringUtils.isNotBlank(loteCodigo)){
			criteria.createAlias(AfaDispensacaoMdtosPai.Fields.AFA_DISP_MDTO_CB_SPSS.toString(), "cb");
			criteria.createAlias("cb."+AfaDispMdtoCbSps.Fields.LOTE_DOC_IMPRESSAO.toString(), "lot");
			criteria.add(Restrictions.eq("lot."+SceLoteDocImpressao.Fields.LOTE_CODIGO.toString(), loteCodigo));
		}
		
		if (prontuario != null || StringUtils.isNotBlank(nomePaciente)) {
			if (prontuario != null) {
				criteria.add(Restrictions.eq("pac" + "." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
			}
			if (StringUtils.isNotBlank(nomePaciente)) {
				criteria.add(Restrictions.ilike("pac" + "." + AipPacientes.Fields.NOME.toString(), nomePaciente,MatchMode.ANYWHERE));
			}
		}

		if(dtHrInclusaoItem != null) {
			criteria.add(Restrictions.ge(AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString(), dtHrInclusaoItem));
		}
		
		if(medicamento != null) {			
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), medicamento));
		}
		
		if(situacao != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(farmacia != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.FARMACIA.toString(), farmacia));
		}
		
		if(prescricaoMedica != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString(), prescricaoMedica));
		}
		
		if(indPmNaoEletronica != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.IND_PM_NAO_ELETRONICA.toString(), indPmNaoEletronica));
		}
		
		//Defeito em Homologação #18269
		criteria.add(Restrictions.ne(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), DominioSituacaoDispensacaoMdto.T));

					
		return criteria;
	}
	
	//#5713
	/**
	 * Retorna Criteria Base para busca de Dispensacoes de Medicamento utilizada na estoria $5713 
	 */
	protected DetachedCriteria obterCriteriaBasePesquisarDispensacaoMdtosPorPaciente(AghUnidadesFuncionais unidadeSolicitante,
			AfaMedicamento medicamento, AfaGrupoUsoMedicamento grupo, AfaTipoUsoMdto tipoUsoMdto, Integer pacCodigo) {
		
		//***ALIASES***
		final String DISPENSACAO_MDTOS_ALIAS = "DSM";
		final String MEDICAMENTO_ALIAS = "MED";
		final String ATENDIMENTO_ALIAS = "ATD";
		final String PACIENTE_ALIAS = "PAC";
		final String UNID_MED_MEDICAS_ALIAS = "UMM";
		final String UNIDADE_SOLICITANTE_ALIAS = "UNF";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, DISPENSACAO_MDTOS_ALIAS);
		
		//***JOINS ALIASES***
		criteria.createAlias(DISPENSACAO_MDTOS_ALIAS + "." + AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), MEDICAMENTO_ALIAS);
		criteria.createAlias(DISPENSACAO_MDTOS_ALIAS + "." + AfaDispensacaoMdtos.Fields.ATENDIMENTO.toString(), ATENDIMENTO_ALIAS);
		criteria.createAlias(DISPENSACAO_MDTOS_ALIAS + "." + AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString(), UNIDADE_SOLICITANTE_ALIAS);
		criteria.createAlias(ATENDIMENTO_ALIAS + "." + AghAtendimentos.Fields.PACIENTE.toString(), PACIENTE_ALIAS);
		criteria.createAlias(MEDICAMENTO_ALIAS + "." + AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), UNID_MED_MEDICAS_ALIAS, JoinType.LEFT_OUTER_JOIN);
		
		//***PROJECTIONS***
		ProjectionList projListDisp = Projections.projectionList();
		
		//***GROUP BY***
		projListDisp.add(Projections.groupProperty(MEDICAMENTO_ALIAS + "." + AfaMedicamento.Fields.DESCRICAO.toString()),"mdtoDescricao");
		projListDisp.add(Projections.groupProperty(MEDICAMENTO_ALIAS + "." + AfaMedicamento.Fields.CONCENTRACAO.toString()),"mdtoConcentracao");
		projListDisp.add(Projections.groupProperty(MEDICAMENTO_ALIAS + "." + AfaMedicamento.Fields.TPR_SIGLA.toString()),"mdtoTprSigla");
		//Projections UnidadeMedidaMedica
		projListDisp.add(Projections.groupProperty(UNID_MED_MEDICAS_ALIAS + "." + MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()),"ummDescricao");
		//Projections UnidadeFuncional
		projListDisp.add(Projections.groupProperty(UNIDADE_SOLICITANTE_ALIAS + "." + AghUnidadesFuncionais.Fields.ANDAR.toString()),"unfAndar");
		projListDisp.add(Projections.groupProperty(UNIDADE_SOLICITANTE_ALIAS + "." + AghUnidadesFuncionais.Fields.ALA.toString()),"unfAla");
		projListDisp.add(Projections.groupProperty(UNIDADE_SOLICITANTE_ALIAS + "." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()),"unfDescricao");
		projListDisp.add(Projections.groupProperty(UNIDADE_SOLICITANTE_ALIAS + "." + AghUnidadesFuncionais.Fields.SIGLA.toString()),"unfSigla");
		//Projections Paciente
		projListDisp.add(Projections.groupProperty(PACIENTE_ALIAS + "." + AipPacientes.Fields.NOME.toString()),"pacNome");
		projListDisp.add(Projections.groupProperty(PACIENTE_ALIAS + "." + AipPacientes.Fields.PRONTUARIO.toString()),"pacProntuario");
		projListDisp.add(Projections.groupProperty(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString()));
		
		//SUM Qtdes
		projListDisp.add(Projections.sum(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString()));
		projListDisp.add(Projections.sum(AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString()));
		
		criteria.setProjection(projListDisp);
		
		//***RESTRICTIONS PADRAO***
		
		if(pacCodigo != null){
			criteria.add(Restrictions.eq(PACIENTE_ALIAS +"."+ AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		}
		if (unidadeSolicitante != null && unidadeSolicitante.getSeq() != null) {
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString(), unidadeSolicitante));
		}	
		if (medicamento != null && medicamento.getMatCodigo() != null) {
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), medicamento));
		}	
		if (tipoUsoMdto != null || grupo != null) {
			if (tipoUsoMdto != null && !"".equals(tipoUsoMdto.getSigla().trim())) {
				criteria.add(Restrictions.eq(MEDICAMENTO_ALIAS + "." + AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), tipoUsoMdto));
			}
			if (grupo != null && grupo.getSeq() != null) {
				criteria.createAlias(MEDICAMENTO_ALIAS + "." + AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), "TUM", JoinType.INNER_JOIN );
						criteria.add(Restrictions.eq("TUM" + "." + AfaTipoUsoMdto.Fields.GRUPO_USO.toString(), grupo));
			}
		}
		return criteria;
	}
	
	//#5713
	/**
	 * Busca informacoes de dispensacao de medicamentos filtrando por data e situacao da dispensacao, agrupando por paciente.
	 */
	public List<Object[]> pesquisarDispensacaoMdtosPacientePorPeriodo(Date dataInicio, Date dataFim, Boolean apenasItensDispensados,AghUnidadesFuncionais unidadeSolicitante,
			AfaMedicamento medicamento, AfaGrupoUsoMedicamento grupo, AfaTipoUsoMdto tipoUsoMdto, Integer pacCodigo) {
		
		List<Object[]> dispensacaoMdtoList = null;
		DetachedCriteria criteria = obterCriteriaBasePesquisarDispensacaoMdtosPorPaciente(unidadeSolicitante, medicamento, grupo, tipoUsoMdto, pacCodigo);
		//***RESTRICTIONS***
		// Soma um dia para pegar todo o intervalo da data fim
		criteria.add(Restrictions.between(AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString(), dataInicio, DateUtil.adicionaDias(dataFim, 1)));
		if (apenasItensDispensados) {
			DominioSituacaoDispensacaoMdto indSituacaoSolicitado = DominioSituacaoDispensacaoMdto.S;
			criteria.add(Restrictions.not(Restrictions.eq(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), indSituacaoSolicitado)));
		} 
		dispensacaoMdtoList = executeCriteria(criteria);
		
		return dispensacaoMdtoList;
	}
	
	//#5713
	/**
	 * Busca informacoes de dispensacao de medicamentos passados pela triagem filtrando por data e agrupando por paciente.
	 */
	public List<Object[]> pesquisarDispensacaoMdtosTriagemPacientePorPeriodo(Date dataInicio, Date dataFim, AghUnidadesFuncionais unidadeSolicitante,
			AfaMedicamento medicamento, AfaGrupoUsoMedicamento grupo, AfaTipoUsoMdto tipoUsoMdto, Integer pacCodigo) {
		
		List<Object[]> dispensacaoMdtoList = null;

		DetachedCriteria criteria = obterCriteriaBasePesquisarDispensacaoMdtosPorPaciente(unidadeSolicitante, medicamento, grupo, tipoUsoMdto, pacCodigo);

		//***RESTRICTIONS***
		criteria.add(Restrictions.between(AfaDispensacaoMdtos.Fields.DTHR_TRIADO.toString(), dataInicio, DateUtil.adicionaDias(dataFim, 1)));
		
		DominioSituacaoDispensacaoMdto indSituacaoSolicitado = DominioSituacaoDispensacaoMdto.T;
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), indSituacaoSolicitado));
		
		dispensacaoMdtoList = executeCriteria(criteria);
		
		return dispensacaoMdtoList;
	}
	
	/**
	 * 
	 * @param unidadeFuncional
	 * @param dataDeReferenciaInicio
	 * @param dataDeReferenciaFim
	 * @param medicamento
	 * @param itemDispensado
	 * @return List<AfaDispensacaoMdtos>
	 */
	public List<Object[]> pesquisarMedicamentoPorDataReferenciaUnidadeMedicamentoItemDispensado(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AghUnidadesFuncionais unidadeFuncional, AfaMedicamento medicamento,
			Boolean itemDispensado) {
		
			List<Object[]> lista = new ArrayList<Object[]>();

			lista.addAll(this.obterMedicamentosPrescritosPorUnidade(dataDeReferenciaInicio,dataDeReferenciaFim,unidadeFuncional,medicamento,itemDispensado, Boolean.TRUE));
			
			if(!itemDispensado){
				lista.addAll(this.obterMedicamentosPrescritosPorUnidade(dataDeReferenciaInicio,dataDeReferenciaFim,unidadeFuncional,medicamento,itemDispensado, Boolean.FALSE));
			}
    		
			return lista;
		}

	@SuppressWarnings("unchecked")
	private List<Object[]> obterMedicamentosPrescritosPorUnidade(Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AghUnidadesFuncionais unidadeFuncional, AfaMedicamento medicamento,
			Boolean itemDispensado, Boolean hqlDispensado) {

		StringBuffer hql = new StringBuffer(680);

		Date DtHrInicial = DateUtil.truncaData(dataDeReferenciaInicio);
		Date DtHrFinal = DateUtil.truncaData(DateUtil.adicionaDias(dataDeReferenciaFim, 1));

		String descMedicamento = AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString() + "."+ AfaMedicamento.Fields.DESCRICAO.toString();
		String concentracaoMedicamento = AfaMedicamento.Fields.CONCENTRACAO.toString();
		String unidadeMedidaMedicamento = MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString();
		String andar = AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString() + "." + AghUnidadesFuncionais.Fields.ANDAR.toString();
		String sigla = AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString() + "." + AghUnidadesFuncionais.Fields.SIGLA.toString();
		String descUnidadeFuncionalSolicitante = AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString() + "." + AghUnidadesFuncionais.Fields.DESCRICAO.toString();
		String siglaTipoApresentacaoMedicamento = AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString() + "." + AfaMedicamento.Fields.TPR_SIGLA.toString();
		String medMatCodigo = AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString();

		String mesAnoDispensacao = AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString();
		String qtdeDispensada = AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString();
		String qtdeEstornada = AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString();

		hql.append(" SELECT DSM." + descMedicamento + " \n" );
		hql.append(" , medicamento." + concentracaoMedicamento + " \n " );
		hql.append(" , unidadeMedidaMedica." + unidadeMedidaMedicamento + " \n " );
		hql.append(" , DSM." + andar + " \n " );
		hql.append(" , DSM." + sigla + " \n " );
		hql.append(" , ala." + AghAla.Fields.CODIGO.toString() + " \n " );
		hql.append(" , DSM." + descUnidadeFuncionalSolicitante + " \n " );
		hql.append(" , DSM." + siglaTipoApresentacaoMedicamento + " \n "  );
		hql.append(" , DSM." + medMatCodigo + " \n " );
		hql.append(" , to_date(to_char(DSM." + mesAnoDispensacao + ", 'mm/yyyy'),'mm/yyyy')" + " \n " );
		hql.append(" , SUM(DSM." + qtdeDispensada+")" + " \n " );
		hql.append(" , SUM(DSM." + qtdeEstornada+")" + " \n " );

		hql.append(" from AfaDispensacaoMdtos DSM \n");
		hql.append(" left outer join DSM.").append(AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString()).append(" unidSolicitante ");
		hql.append(" left outer join unidSolicitante.").append(AghUnidadesFuncionais.Fields.ALA.toString()).append(" ala ");
		hql.append(" left outer join DSM.").append(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString()).append(" medicamento ");
		hql.append(" left outer join medicamento.").append(AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString()).append(" unidadeMedidaMedica ");
		
		hql.append(" WHERE 1 = 1 \n");
			
		if (unidadeFuncional != null) {
			hql.append(" and DSM." + AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString() + " = :unidadeFuncional \n" );
		}

		if (medicamento != null) {
			hql.append(" and DSM." + AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString() + " = :medicamento \n" );
		}

		if(hqlDispensado){
			hql.append(" and DSM." + AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString()+ "  \n");
			hql.append(" BETWEEN :dtHrInicial AND :dtHrFinal \n ");
			hql.append(" and DSM." + AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString() + " <> '" + DominioSituacaoDispensacaoMdto.S +"' \n" );
			
		}else{//Triado
			hql.append(" and DSM." + AfaDispensacaoMdtos.Fields.DTHR_TRIADO.toString()+ " \n");
			hql.append(" BETWEEN :dtHrInicial AND :dtHrFinal \n ");
			
			hql.append(" and DSM." + AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString() + " = '" + DominioSituacaoDispensacaoMdto.T + "' \n");
		}

		hql.append(" GROUP BY DSM." + descMedicamento + " \n" );
		hql.append(" , medicamento." + concentracaoMedicamento + " \n " );
		hql.append(" , unidadeMedidaMedica." + unidadeMedidaMedicamento + " \n " );
		hql.append(" , DSM." + andar + " \n " );
		hql.append(" , DSM." + sigla + " \n " );
		//hql.append(" , DSM." + ala + " \n " );
		hql.append(" , ala." + AghAla.Fields.CODIGO.toString() + " \n " );
		hql.append(" , DSM." + descUnidadeFuncionalSolicitante + " \n " );
		hql.append(" , DSM." + siglaTipoApresentacaoMedicamento + " \n " );
		hql.append(" , DSM." + medMatCodigo + " \n " );
		hql.append(" , to_date(to_char(DSM." + mesAnoDispensacao + ", 'mm/yyyy'), 'mm/yyyy') " + " \n " );

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("dtHrInicial", DtHrInicial);
		query.setParameter("dtHrFinal", DtHrFinal);
		if (unidadeFuncional != null){ 
			query.setParameter("unidadeFuncional", unidadeFuncional);
		}
		if (medicamento != null){ 
			query.setParameter("medicamento", medicamento.getMatCodigo());
		}
		return query.list();

	}
	
	/**
	 * Pesquisa pelos códigos dos medicamentos de dispensação
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param atdSeqPrescricao
	 * @param seqPrescricao
	 * @return List<AfaDispensacaoMdtos>
	 */
public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtosPorPrescricaoESituacao(MpmPrescricaoMedica prescricaoMedica, DominioSituacaoDispensacaoMdto ... situacoesDispensacoes){
		
		DetachedCriteria criteria = obterCriteriaDispensacaoMdtosPorPrescricao(prescricaoMedica);
		criteria.add(Restrictions.in(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), situacoesDispensacoes));
		criteria.createAlias(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), "MED");
		criteria.createAlias(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO.toString(), "ITEM");
		
		criteria.createAlias(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString(), "tipoOcorrenciaDispensacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MED."+AfaMedicamento.Fields.TPR.toString(), "tipoApresentacaoMedicamento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.SERVIDOR_CONFERIDA.toString(), "SERCONF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.SERVIDOR_ENTREGUE.toString(), "SERENT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.SERVIDOR_ESTORNADO.toString(), "SEREST", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.SERVIDOR_TRIADO_POR.toString(), "SERTRI", JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.asc("ITEM." + MpmItemPrescricaoMdto.Fields.PMD_SEQ));
		criteria.addOrder(Order.asc("MED." + AfaMedicamento.Fields.IND_DILUENTE.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	// 5465
	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtosPorProntuarioDataMedicamento(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AfaMedicamento medicamento, AipPacientes paciente,
			Date dthrLimiteEstorno, List<AghAtendimentos> atendimentos,//Integer seqAtendimento,
			Long seqAfaDispMdto,
			DominioSituacaoDispensacaoMdto ... situacoesDispensacoes){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
		

		criteria.createAlias(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), "medicamento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO_ESTORNADO.toString(), "tipoOcorrenciaDispensacaoEstornado", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString(), "tipoOcorrenciaDispensacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString(), "prescricaoMedica", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("medicamento."+AfaMedicamento.Fields.TPR.toString(), "tipoApresentacaoMedicamento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.AFA_DISP_MDTO_CB_SPS.toString(), "afaDispMdtoCbSpss", JoinType.LEFT_OUTER_JOIN);
		
		//Estoque
		criteria.createAlias(AfaDispensacaoMdtos.Fields.FARMACIA.toString(), "unidadeFuncional", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("prescricaoMedica."+MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "atendimento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncionalAtendimento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("unidadeFuncionalAtendimento."+AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(), "centroCusto", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.in(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), situacoesDispensacoes));
		
		criteria.add(Restrictions.gt(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString(), BigDecimal.ZERO));
		
		//criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ.toString(), seqAtendimento)); 
		
		if(atendimentos != null && !atendimentos.isEmpty()){
			criteria.add(Restrictions.in(AfaDispensacaoMdtos.Fields.ATENDIMENTO.toString(), atendimentos));
		}
		
		if(medicamento != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString(), medicamento.getMatCodigo()));
		}

		if(seqAfaDispMdto == null){
			criteria.add(Restrictions.ge(AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString(), dthrLimiteEstorno));
		}
		
		if (dataDeReferenciaInicio != null){
			criteria.add(Restrictions.between(AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString(), dataDeReferenciaInicio, dataDeReferenciaFim));
		}
		
		if(seqAfaDispMdto != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.SEQ.toString(), seqAfaDispMdto));
		}
		
		criteria.addOrder(Order.desc(AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	
	//Fim 5465
	
	public List<Object[]> pesquisarMedicamentoEstornadoPorMotivo(
			Date dataDeReferenciaInicio, 
			Date dataDeReferenciaFim, 
			AghUnidadesFuncionais unidadeFuncional, 
			AfaTipoOcorDispensacao tipoOcorDispensacao, 
			AfaMedicamento medicamento){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class); //corresponde ao FROM do select
		
		String p = ".";
		
		criteria.createAlias(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), "MED");
		criteria.createAlias("MED"+p+AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "UMM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString(), "UNF");
		criteria.createAlias("UNF"+p+AghUnidadesFuncionais.Fields.ALA.toString(), "ALA", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO_ESTORNADO.toString(),"TOD");
		
		ProjectionList projection = Projections.projectionList()
												.add(Projections.groupProperty("MED"+p+AfaMedicamento.Fields.DESCRICAO.toString()))
												.add(Projections.groupProperty("MED"+p+AfaMedicamento.Fields.CONCENTRACAO.toString()))
												.add(Projections.groupProperty("UMM"+p+MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()))
												.add(Projections.groupProperty("UNF"+p+AghUnidadesFuncionais.Fields.ANDAR.toString()))
												.add(Projections.groupProperty("UNF"+p+AghUnidadesFuncionais.Fields.SIGLA.toString()))
												.add(Projections.groupProperty("ALA"+p+AghAla.Fields.CODIGO.toString()))
												.add(Projections.groupProperty("UNF"+p+AghUnidadesFuncionais.Fields.DESCRICAO.toString()))
												.add(Projections.sum(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString()))
												.add(Projections.sum(AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString()))
												.add(Projections.groupProperty("MED"+p+AfaMedicamento.Fields.TPR_SIGLA.toString()))
												.add(Projections.groupProperty("MED"+p+AfaMedicamento.Fields.MAT_CODIGO.toString()))
												.add(Projections.groupProperty("TOD"+p+AfaTipoOcorDispensacao.Fields.DESCRICAO.toString()));
		
		criteria.setProjection(projection);
		// Cláusula where
		Date dataDeReferenciaInicioFormatada = DateUtil.truncaData(dataDeReferenciaInicio);
		Date dataDeReferenciaFimFormatada = DateUtil.truncaData(DateUtil.adicionaDias(dataDeReferenciaFim, 1));
		
		criteria.add(Restrictions.between(AfaDispensacaoMdtos.Fields.DTHR_ESTORNO.toString(), dataDeReferenciaInicioFormatada, dataDeReferenciaFimFormatada));
		
		if(unidadeFuncional != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString() + p + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeFuncional.getSeq()));
		}
		
		if(tipoOcorDispensacao != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO_ESTORNADO.toString() + p + AfaTipoOcorDispensacao.Fields.SEQ.toString(), tipoOcorDispensacao.getSeq()));
		}
		
		if(medicamento != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString(), medicamento.getMatCodigo()));
		}
		
		criteria.add(Restrictions.gt(AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString(), BigDecimal.ZERO));

		return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa Dispensação de Medicamentos por Unidade Funcional, Data, Ocorrência, Prontuário e Leito
	 * 
	 * @param unidadeFuncional
	 * @param data
	 * @param tipoOcorDispensacao
	 * @param prontuário
	 * @param leito	
	 * @param d 
	 * @param t 
	 * @return List<AfaDispensacaoMdtos>
	 */	
	
	public List<AfaDispensacaoMdtos> pesquisarOcorrenciasMdtosDispensados(AghUnidadesFuncionais unidadeFuncional, Date data,
			AfaTipoOcorDispensacao tipoOcorDispensacao, Integer prontuario,  AinLeitos leito, AghUnidadesFuncionais farmacia, BigDecimal qtdeDispensada, DominioSituacaoItemPrescritoDispensacaoMdto situacaoItem,
			DominioSituacaoDispensacaoMdto ... situacoesDispensacao) {
		
			DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);  
			criteria.createAlias(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), "mdto", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(AfaDispensacaoMdtos.Fields.FARMACIA.toString(), "farmacia", JoinType.LEFT_OUTER_JOIN);
            criteria.createAlias("farmacia."+AghUnidadesFuncionais.Fields.ALMOXARIFADO, "almoxarifado", JoinType.LEFT_OUTER_JOIN);
            criteria.createAlias("farmacia."+AghUnidadesFuncionais.Fields.CENTRO_CUSTO, "centroCustoFarmacia", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("mdto."+AfaMedicamento.Fields.TPR.toString(), "tpr", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString(), "pm", JoinType.LEFT_OUTER_JOIN);			
			criteria.createAlias("pm."+MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
			
			//Joins com tabelas AghAtendimentos e AipPacientes
			criteria.createAlias(AfaDispensacaoMdtos.Fields.ATENDIMENTO.toString(), "atendimento");
			criteria.createAlias("atendimento" + "." + AghAtendimentos.Fields.PACIENTE.toString(), "paciente");	
			
			//Estoque
			criteria.createAlias("atd."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncionalAtendimento", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("unidadeFuncionalAtendimento."+AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(), "centroCusto", JoinType.LEFT_OUTER_JOIN);
			
			//Condição obrigatória. Pesquisa para a Data informada 
			Date dataInicioFormatada = DateUtil.truncaData(data);
			Date dataFimFormatada = DateUtil.truncaData(DateUtil.adicionaDias(data, 1));			
			criteria.add(Restrictions.between(AfaDispensacaoMdtos.Fields.DTHR_TRIADO.toString(), dataInicioFormatada, dataFimFormatada));
			
			//Condição obrigatória. Pesquisa para a Situacão de Dispensação do Mdto: Triado ou Dispensado 
			criteria.add(Restrictions.in(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), situacoesDispensacao));			
			
			//Condição obrigatória. Pesquisa para a Situacão do Item Prescrito na Dispensação: diferente de EG (excluído pós geração)
			criteria.add(Restrictions.ne(AfaDispensacaoMdtos.Fields.IND_SIT_ITEM_PRESCRITO.toString(), situacaoItem));			
			
			//Condição obrigatória. Pesquisa para a Quantidade Dispensada do Item > 0
			criteria.add(Restrictions.gt(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString(), qtdeDispensada));
			
			if(unidadeFuncional != null){
				criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString(), unidadeFuncional));
			}
			
			if(farmacia != null){
				criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.FARMACIA.toString(), farmacia));
			}
			
			if(tipoOcorDispensacao != null){
				criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString(), tipoOcorDispensacao));
			}else{
				criteria.add(Restrictions.isNotNull(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString()));
			}
			
			if(prontuario != null){
				criteria.add(Restrictions.eq("paciente" + "." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
			}
			
			if(leito != null){
				criteria.add(Restrictions.eq("atendimento" + "." + AghAtendimentos.Fields.LTO_LTO_ID.toString(), leito.getLeitoID()));
			}			 
			
			criteria.addOrder(Order.asc(AfaDispensacaoMdtos.Fields.SEQ.toString()));

			return executeCriteria(criteria);
		}
	
	public Long recuperarListaOcorrenciaCount(Short seqUnidadeFuncional, Date data,
			Short seqOcorrencia, BigDecimal qtdeDispensada, Short seqFarmacia, DominioSituacaoItemPrescritoDispensacaoMdto situacaoItem,
			DominioSituacaoDispensacaoMdto triado, DominioSituacaoDispensacaoMdto solicitado) {

		DetachedCriteria criteria = getCriteriaRecuperarListaOcorrencia(
				seqUnidadeFuncional, data, seqOcorrencia, qtdeDispensada, seqFarmacia,
				situacaoItem, triado, solicitado);

		return executeCriteriaCount(criteria);
	}
	
	public List<ListaOcorrenciaVO> recuperarListaOcorrencia(Short seqUnidadeFuncional, Date data,
			Short seqOcorrencia, BigDecimal qtdeDispensada, Short seqFarmacia, DominioSituacaoItemPrescritoDispensacaoMdto situacaoItem,
			DominioSituacaoDispensacaoMdto triado, DominioSituacaoDispensacaoMdto solicitado) {
		
		DetachedCriteria criteria = getCriteriaRecuperarListaOcorrencia(
				seqUnidadeFuncional, data, seqOcorrencia, qtdeDispensada, seqFarmacia,
				situacaoItem, triado, solicitado);    
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.groupProperty("tipoOcorrenciaDispensacao"+"."+AfaTipoOcorDispensacao.Fields.DESCRICAO.toString()), ListaOcorrenciaVO.Fields.DESCRICAO_OCORRENCIA.toString())
//		.add(Projections.groupProperty(AfaDispensacaoMdtos.Fields.DTHR_TRIADO.toString()), ListaOcorrenciaVO.Fields.DT_HR_TRIADO.toString())
		.add(Projections.groupProperty("medicamento"+"."+AfaMedicamento.Fields.DESCRICAO.toString()), ListaOcorrenciaVO.Fields.DESCRICAO_MEDICAMENTO.toString())
		.add(Projections.groupProperty("medicamento"+"."+AfaMedicamento.Fields.CONCENTRACAO.toString()), ListaOcorrenciaVO.Fields.CONCENTRACAO.toString())
		.add(Projections.groupProperty("mpmUnidadeMedidaMedicas"+"."+MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()), ListaOcorrenciaVO.Fields.DESCRICAO_UNID_MED_MEDICAS.toString())
		.add(Projections.groupProperty("unidadeFuncionalSolicitante"+"."+AghUnidadesFuncionais.Fields.ANDAR.toString()), ListaOcorrenciaVO.Fields.ANDAR.toString())		
		.add(Projections.groupProperty("indAla"+"."+AghAla.Fields.CODIGO.toString()), ListaOcorrenciaVO.Fields.IND_ALA.toString())
		.add(Projections.groupProperty("unidadeFuncionalSolicitante"+"."+AghUnidadesFuncionais.Fields.SIGLA.toString()), ListaOcorrenciaVO.Fields.SIGLA_UNID_SOLICITANTE.toString())
		.add(Projections.groupProperty("unidadeFuncionalSolicitante"+"."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()), ListaOcorrenciaVO.Fields.DESCRICAO_UNID_SOLICITANTE.toString())
		.add(Projections.groupProperty("paciente"+"."+AipPacientes.Fields.NOME.toString()), ListaOcorrenciaVO.Fields.NOME.toString())
		.add(Projections.groupProperty("paciente"+"."+AipPacientes.Fields.PRONTUARIO.toString()), ListaOcorrenciaVO.Fields.PRONTUARIO.toString())
		.add(Projections.groupProperty("tipoApresentacaoMedicamento"+"."+AfaTipoApresentacaoMedicamento.Fields.SIGLA.toString()), ListaOcorrenciaVO.Fields.SIGLA_APRES_MED.toString())
		.add(Projections.groupProperty("leito"+"."+AinLeitos.Fields.LTO_ID.toString()), ListaOcorrenciaVO.Fields.LEITO_ID.toString())
		.add(Projections.groupProperty("quarto"+"."+AinQuartos.Fields.NUMERO.toString()), ListaOcorrenciaVO.Fields.NUMERO_QUARTO.toString())		
		.add(Projections.groupProperty("unidadeFuncionalSolicitante"+"."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), ListaOcorrenciaVO.Fields.SEQ_UF.toString())
		.add(Projections.groupProperty("atendimento"+"."+AghAtendimentos.Fields.SEQ.toString()), ListaOcorrenciaVO.Fields.SEQ_ATD.toString())
				
		.add(Projections.sum(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString()),ListaOcorrenciaVO.Fields.QTDE_DISPENSADA1.toString())
		.add(Projections.sum(AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString()),ListaOcorrenciaVO.Fields.QTDE_ESTORNADA.toString());		

		criteria.setProjection(projection);		
		
		criteria.setResultTransformer(Transformers.aliasToBean(ListaOcorrenciaVO.class));
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria getCriteriaRecuperarListaOcorrencia(
			Short seqUnidadeFuncional, Date data, Short seqOcorrencia,
			BigDecimal qtdeDispensada, Short seqFarmacia,
			DominioSituacaoItemPrescritoDispensacaoMdto situacaoItem,
			DominioSituacaoDispensacaoMdto triado,
			DominioSituacaoDispensacaoMdto solicitado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "adm");
		
		//Joins com tabelas  
		criteria.createAlias(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString(), "tipoOcorrenciaDispensacao");
		criteria.createAlias(AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE.toString(), "unidadeFuncionalSolicitante");
		criteria.createAlias("unidadeFuncionalSolicitante" + "." + AghUnidadesFuncionais.Fields.ALA.toString(), "indAla");
		
		criteria.createAlias(AfaDispensacaoMdtos.Fields.FARMACIA.toString(), "unidadeFuncional");
		
		criteria.createAlias(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), "medicamento");
		criteria.createAlias("medicamento" + "." + AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "mpmUnidadeMedidaMedicas", JoinType.LEFT_OUTER_JOIN);	
		criteria.createAlias("medicamento" + "." + AfaMedicamento.Fields.TPR.toString(), "tipoApresentacaoMedicamento");	
			
		criteria.createAlias(AfaDispensacaoMdtos.Fields.ATENDIMENTO.toString(), "atendimento");
		criteria.createAlias("atendimento" + "." + AghAtendimentos.Fields.PACIENTE.toString(), "paciente");	
		criteria.createAlias("atendimento" + "." + AghAtendimentos.Fields.LEITO.toString(), "leito", JoinType.LEFT_OUTER_JOIN);	
		criteria.createAlias("atendimento" + "." + AghAtendimentos.Fields.QUARTO.toString(), "quarto", JoinType.LEFT_OUTER_JOIN);	
//		criteria.createAlias("atendimento" + "." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional");	
//		criteria.createAlias("unidadeFuncional" + "." + AghUnidadesFuncionais.Fields.ALA.toString(), "indAla");	
		
		
		//Condição obrigatória. Pesquisa para a Data informada 
		Date dataInicioFormatada = DateUtil.truncaData(data);
		Date dataFimFormatada = DateUtil.truncaData(DateUtil.adicionaDias(data, 1));			
		criteria.add(Restrictions.between("adm." + AfaDispensacaoMdtos.Fields.DTHR_TRIADO.toString(), dataInicioFormatada, dataFimFormatada));
		
		//Condição obrigatória. Pesquisa para a Situacão de Dispensação do Mdto: Triado   
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), triado));		
		
		//Condição obrigatória. Pesquisa para a Situacão de Dispensação do Mdto: diferente de Solicitado   
		criteria.add(Restrictions.not(Restrictions.eq(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), solicitado)));	
		
		//Condição obrigatória. Pesquisa para a Situacão do Item Prescrito na Dispensação: diferente de EG (excluído pós geração)
		criteria.add(Restrictions.ne(AfaDispensacaoMdtos.Fields.IND_SIT_ITEM_PRESCRITO.toString(), situacaoItem));			
		
		//Condição obrigatória. Pesquisa para a Quantidade Dispensada do Item > 0
		criteria.add(Restrictions.gt(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString(), qtdeDispensada));
		
		if(seqUnidadeFuncional != null){
			criteria.add(Restrictions.eq("unidadeFuncionalSolicitante" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seqUnidadeFuncional));
		}
		
		if(seqOcorrencia != null){
			criteria.add(Restrictions.eq("tipoOcorrenciaDispensacao" + "." + AfaTipoOcorDispensacao.Fields.SEQ.toString(), seqOcorrencia));
		}
		
		if(seqFarmacia != null){
			criteria.add(Restrictions.eq("unidadeFuncional" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seqFarmacia));
		}
		
		return criteria;
	}

	public DominioSituacaoDispensacaoMdto pesquisarSituacaoDispensacaoMdto(Long seq) {
			
			DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.SEQ.toString(), seq));

			criteria.setProjection(Projections.projectionList().add(
					Projections.property(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString())));
			
			DominioSituacaoDispensacaoMdto situacaoDispensacaoMdto = (DominioSituacaoDispensacaoMdto) this.executeCriteriaUniqueResult(criteria);

			return situacaoDispensacaoMdto;
			
	}
	
	
	public Integer pesquisarQtdeDispensadaMdto(Long seq) {		

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);

		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.SEQ.toString(), seq));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString())));
		
		BigDecimal qtdeDisp = (BigDecimal) executeCriteriaUniqueResult(criteria);
		
		return qtdeDisp.intValue();
	}
	
	//#5388
	/**
	 * Recupera lista de Medicamentos selecionados para dispensacao
	 * apos a triagem filtrando pelo item prescrito de uma prescricao medica 
	 * @param itemPrescrito
	 * @return lista contendo o(s) medicamento(s) selecionados para dispensacao
	 */
	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtoPorItemPrescrito(MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica) {
		
		List<AfaDispensacaoMdtos> lista = null;
		//Criteria Principal
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "DSM");
		criteria.createAlias(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString(), "tod", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.FARMACIA.toString(), "far", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.conjunction()
			.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO.toString(), itemPrescrito))
			.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString(), prescricaoMedica)));
		
		//--------------- Fim Criterion
		//Caracteristicas
		DetachedCriteria criteriaCaractUnf = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "UNF_CARACT");
		criteriaCaractUnf.setProjection(Projections.projectionList().add(Projections.property(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString())));
		criteriaCaractUnf.add(
				Restrictions.eq(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), 
						ConstanteAghCaractUnidFuncionais.UNID_FARMACIA));
		
		//UnidadeFuncional
		DetachedCriteria criteriaUnf = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "UNF");
		criteriaUnf.setProjection(Projections.projectionList().add(Projections.property(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())));
		criteriaUnf.add(
				Restrictions.eqProperty(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), 
						"DSM" + "." + AfaDispensacaoMdtos.Fields.FARMACIA.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteriaUnf.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteriaUnf.add(Subqueries.propertyIn(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), criteriaCaractUnf));
		
		Criterion c2 = Subqueries.exists(criteriaUnf); 
		//--------------- Fim Criterion
		//MedicamentoEquivalente
		DetachedCriteria criteriaMedEq = DetachedCriteria.forClass(AfaMedicamentoEquivalente.class, "MEQ");
		criteriaMedEq.setProjection(Projections.projectionList().add(Projections.property(AfaMedicamentoEquivalente.Fields.MED_MAT_CODIGO_EQUIVALENTE.toString())));
		criteriaMedEq.add(Restrictions.eq(AfaMedicamentoEquivalente.Fields.MED_MAT_CODIGO.toString(), itemPrescrito.getMedicamento().getMatCodigo()));
		
		//AfaDispensacaoMdtos
		DetachedCriteria criteriaDispMed = DetachedCriteria.forClass(AfaDispensacaoMdtos.class , "DSM1");
		criteriaDispMed.setProjection(Projections.projectionList().add(Projections.property(AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString())));
		criteriaDispMed.add(Restrictions.conjunction()
				.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO.toString(), itemPrescrito))
				.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString(), prescricaoMedica)));
		
		//View VMpmMdtosDescr
		DetachedCriteria criteriaViewMdtoDescr = DetachedCriteria.forClass(VMpmMdtosDescr.class, "VDE");
		criteriaViewMdtoDescr.setProjection(Projections.projectionList().add(Projections.property(VMpmMdtosDescr.Fields.MAT_CODIGO.toString())));
		criteriaViewMdtoDescr.add(Restrictions.eqProperty(VMpmMdtosDescr.Fields.MAT_CODIGO.toString(), "DSM" + "." + AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString()));
		Criterion criterionVMpmMdtosDescr1 = Subqueries.propertyIn(VMpmMdtosDescr.Fields.MAT_CODIGO.toString(), criteriaMedEq);
		Criterion criterionVMpmMdtosDescr2 = Subqueries.propertyIn(VMpmMdtosDescr.Fields.MAT_CODIGO.toString(), criteriaDispMed);
		Criterion criterionVMpmMdtosDescr3 = Restrictions.eq(VMpmMdtosDescr.Fields.MAT_CODIGO.toString(), itemPrescrito.getMedicamento().getMatCodigo());
		
		criteriaViewMdtoDescr
			.add(Restrictions.disjunction()
					.add(Restrictions.disjunction().add(criterionVMpmMdtosDescr1).add(criterionVMpmMdtosDescr2))
					.add(criterionVMpmMdtosDescr3));
		
		Criterion c3 = Subqueries.exists(criteriaViewMdtoDescr);
		//--------------- Fim Criterion
		
		//Regra Principal
		criteria.add(c2);
		criteria.add(c3);
		
		criteria.addOrder(Order.asc(AfaDispensacaoMdtos.Fields.CRIADO_EM.toString()));
		
	
		lista = executeCriteria(criteria);
		
		return lista;
	}
	
	//#5465
	public Long obterQtdeDispMdtosEstornadosBySeq(Long seqDsm) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.SEQ.toString(), seqDsm));
		criteria.setProjection(Projections.property(AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString()));
		
		//return executeCriteriaCountDistinct(criteria, AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString(), Boolean.FALSE);
		BigDecimal qtdeEst = (BigDecimal) executeCriteriaUniqueResult(criteria);
		return qtdeEst == null ? 0 : qtdeEst.longValue();
	}
	
	public List<AfaDispensacaoMdtos> pesquisarAfaDispensacaoMdto(Integer pmeAtdSeq,
			Integer pmeSeq, Long pmdSeqAnt, DominioSituacaoItemPrescritoDispensacaoMdto ... itensPrescritos) {
		
		DetachedCriteria cri = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "adm");
		addPrescricaoMedica(cri, pmeAtdSeq, pmeSeq);
		cri.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO_PMD_ATD_SEQ.toString(), pmeAtdSeq));
		cri.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO_PMD_SEQ.toString(), pmdSeqAnt));
		
		cri.add(Restrictions.not(Restrictions.in(AfaDispensacaoMdtos.Fields.IND_SIT_ITEM_PRESCRITO.toString(), itensPrescritos)));
		
		DetachedCriteria criNotExistsAfaDispMdto = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "adm1");
		criNotExistsAfaDispMdto.add(Restrictions.eqProperty(AfaDispensacaoMdtos.Fields.DSM_SEQ.toString(), "adm."+AfaDispensacaoMdtos.Fields.SEQ.toString()));
		criNotExistsAfaDispMdto.setProjection(Projections.property(AfaDispensacaoMdtos.Fields.SEQ.toString()));
		cri.add(Subqueries.notExists(criNotExistsAfaDispMdto));
		
		return executeCriteria(cri);
	}
	
	public List<AfaDispensacaoMdtos> pesquisarAfaDispensacaoMdto(Integer pmeAtdSeq, Integer pmeSeq, 
			Integer imePmdAtdSeq, Long imePmdSeq, Integer imeMedMatCodigo,Short imeSeqP) {
		
		DetachedCriteria cri = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "adm");
		addPrescricaoMedica(cri, pmeAtdSeq, pmeSeq);
		addItemPrescricaoMedicamento(cri, imePmdAtdSeq, imePmdSeq, imeMedMatCodigo, imeSeqP);
		cri.add(Restrictions.or(
				Restrictions.isNull(AfaDispensacaoMdtos.Fields.IND_SIT_ITEM_PRESCRITO.toString()), 
				Restrictions.ne(AfaDispensacaoMdtos.Fields.IND_SIT_ITEM_PRESCRITO.toString(), DominioSituacaoItemPrescritoDispensacaoMdto.IF)
				));
		cri.addOrder(Order.desc(AfaDispensacaoMdtos.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(cri);
	}
	
	private void addItemPrescricaoMedicamento(DetachedCriteria cri,
			Integer imePmdAtdSeq, Long imePmdSeq, Integer imeMedMatCodigo,
			Short imeSeqP) {
		cri.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO_PMD_ATD_SEQ.toString(), imePmdAtdSeq));
		cri.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO_PMD_SEQ.toString(), imePmdSeq));
		cri.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO_MED_MAT_CODIGO.toString(), imeMedMatCodigo));
		cri.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO_SEQP.toString(), imeSeqP));
	}

	/**
	 * Adiciona filtro de Prescricaomedica na criteria passada via paramêtro
	 */
	private void addPrescricaoMedica(DetachedCriteria criteria, Integer pmeAtdSeq,Integer pmeSeq){
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ.toString(), pmeAtdSeq));
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ.toString(), pmeSeq));
	}

	public List<AfaDispensacaoMdtos> pesquisarAfaDispensacaoMdto(Integer pmeAtdSeq, Integer pmeSeq, 
			Integer imePmdAtdSeq, Long imePmdSeq, Integer imeMedMatCodigo,Short imeSeqP, Long seq) {
		
		DetachedCriteria cri = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "adm");
		addPrescricaoMedica(cri, pmeAtdSeq, pmeSeq);
		addItemPrescricaoMedicamento(cri, imePmdAtdSeq, imePmdSeq, imeMedMatCodigo, imeSeqP);
		if(seq != null){
			cri.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.SEQ.toString(), seq));
		}

		return executeCriteria(cri);
	}
	
	public Long getQtdeItensDispensadosComPrescricaoMedicaByDataEmissao(
			Date dataEmissao) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
		
		cri.setProjection(Projections.rowCount());
		
		cri.createAlias(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString(), AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString());
		
		cri.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_DT_REFERENCIA.toString(), dataEmissao));
		cri.add(Restrictions.isNotNull(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString() + "." + MpmPrescricaoMedica.Fields.SERVIDOR_VALIDA_MATRICULA.toString()));
		cri.add(Restrictions.in(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), Arrays.asList(DominioSituacaoDispensacaoMdto.D, DominioSituacaoDispensacaoMdto.T)));
		
		return (Long) executeCriteriaUniqueResult(cri);
	}
	
	public Long getQtdeItensDispensadosComPrescricaoNaoEletronicaByDataEmissao(Date dataEmissao) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
		
		cri.setProjection(Projections.rowCount());
		
		cri.createAlias(AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO.toString(), AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO.toString());
		
		cri.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO.toString() + "." + AfaPrescricaoMedicamento.Fields.DT_REFERENCIA, dataEmissao));
		cri.add(Restrictions.in(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), Arrays.asList(DominioSituacaoDispensacaoMdto.D, DominioSituacaoDispensacaoMdto.T)));
		
		return (Long) executeCriteriaUniqueResult(cri);
	}
	
	public List<AfaDispensacaoMdtos> pesquisarListaDispMdtoDispensarPelaPrescricao(Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro) { 
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "adm");
		
		criteria.createAlias(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), "medicamento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaDispensacaoMdtos.Fields.FARMACIA.toString(), "unidadeFuncional", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("unidadeFuncional."+AghUnidadesFuncionais.Fields.ALMOXARIFADO, "almoxarifado", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("unidadeFuncional."+AghUnidadesFuncionais.Fields.CENTRO_CUSTO, "centroCustoUnf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("medicamento."+AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "mpmUnidadeMedidaMedicas", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("medicamento."+AfaMedicamento.Fields.TPR.toString(), "tipoApresentacaoMedicamento", JoinType.LEFT_OUTER_JOIN);
		
		//Estoque
		criteria.createAlias(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA.toString(), "prescricaoMedica", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("prescricaoMedica."+MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "atendimento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atendimento."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncionalAtendimento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("unidadeFuncionalAtendimento."+AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(), "centroCusto", JoinType.LEFT_OUTER_JOIN);
		
		addPrescricaoMedica(criteria, pmeAtdSeq, pmeSeq);
		if (unidadeFuncionalMicro != null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.FARMACIA.toString(),unidadeFuncionalMicro));
		}
		criteria.add(Restrictions.gt(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString(), BigDecimal.ZERO));
		criteria.add(Restrictions.isNull(AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString()));
		criteria.add(Restrictions.or(Restrictions.eq(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), DominioSituacaoDispensacaoMdto.T)
				, Restrictions.eq(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), DominioSituacaoDispensacaoMdto.D)));
		return executeCriteria(criteria);
	}
	
	public List<Long> pesquisarSeqsDispMdtoDispensarPelaPrescricao(Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro) { 
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "dispensacao");
		addPrescricaoMedica(criteria, pmeAtdSeq, pmeSeq);
		addUnfSeqSituacaoDispensacaoMdto(unidadeFuncionalMicro, criteria, DominioSituacaoDispensacaoMdto.T);
		
		criteria.setProjection(Projections.projectionList().add(Projections.property(AfaDispensacaoMdtos.Fields.SEQ.toString())));
		
		return executeCriteria(criteria);
	}
	
	public Object[] pesquisarMaxDataHrTicket(Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro, Long pmmSeq) { 
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "dispensacao");
		if(pmeAtdSeq != null && pmeSeq != null){
			addPrescricaoMedica(criteria, pmeAtdSeq, pmeSeq);
		}else{
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO_SEQ.toString(), pmmSeq));
		}
		addUnfSeqSituacaoDispensacaoMdto(unidadeFuncionalMicro, criteria, DominioSituacaoDispensacaoMdto.D);
		criteria.add(Restrictions.isNotNull(AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString()));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.max(AfaDispensacaoMdtos.Fields.DTHR_TICKET.toString()))
				.add(Projections.min(AfaDispensacaoMdtos.Fields.CRIADO_EM.toString())));
		
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}

	private void addUnfSeqSituacaoDispensacaoMdto(AghUnidadesFuncionais unidadeFuncionalMicro, DetachedCriteria criteria, DominioSituacaoDispensacaoMdto dominioSituacaoDispensacaoMdto) {
		criteria.add(Restrictions.gt("dispensacao."+AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString(), BigDecimal.ZERO));
		criteria.add(Restrictions.isNull("dispensacao."+AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString()));
		criteria.add(Restrictions.eq("dispensacao."+AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), dominioSituacaoDispensacaoMdto));
		
		if (unidadeFuncionalMicro != null){
			criteria.add(Restrictions.eq("dispensacao."+AfaDispensacaoMdtos.Fields.FARMACIA.toString(),unidadeFuncionalMicro));
		}
	}
	
	
	public List<AfaDispensacaoMdtos> processaProximaPrescricaoTriagemMedicamentoByProntuario(
			Integer atdSeqPrescricao, Integer seqPrescricao, Boolean proximoReg, DominioSituacaoDispensacaoMdto ... situacoes) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);
		
		criteria.add(Restrictions.in(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), situacoes));
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ.toString(), atdSeqPrescricao));

		if(proximoReg){
			criteria.add(Restrictions.gt(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ.toString(), seqPrescricao));
			criteria.addOrder(Order.asc(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ.toString()));
		}else{
			criteria.add(Restrictions.lt(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ.toString(), seqPrescricao));
			criteria.addOrder(Order.desc(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ.toString()));
		}
		
		//Somente um registro é necessário
		return executeCriteria(criteria, 0, 1, "", proximoReg);
	}
	
	//Estoria #5714
	public List<MedicamentoDispensadoPorBoxVO> pesquisarMedicamentosDispensadosPorBox(
			Date dataEmissaoInicio, 
			Date dataEmissaoFim,
			AghMicrocomputador aghMicrocomputador,
			AfaMedicamento medicamento,
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento,
			AfaGrupoApresMdtos afaGrupoApresMdtos,
			AghParametros parametroGrupoUsoMdto, Integer pacCodigo) {
		
		String dsm = "DSM";
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, dsm);
		
		String p = ".";
		String med = "MED"; //AfaMedicamento
		String umm = "UMM";//MpmUnidadesMedidasMedicas
		String tum = "TUM";//AfaTipoUsoMedicamento
		String atd = "ATD";//AgHAtendimentos
		
		//Alias		
		criteria.createAlias(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), med);
		criteria.createAlias(med+p+AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), tum);
		criteria.createAlias(med+p+AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), umm, JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AfaDispensacaoMdtos.Fields.ATENDIMENTO.toString(), atd);
		
		//Projections
		ProjectionList projection = Projections.projectionList()
				.add(Projections.groupProperty(med+p+AfaMedicamento.Fields.MAT_CODIGO.toString()),MedicamentoDispensadoPorBoxVO.Fields.CODIGO_MEDICAMENTO.toString())
				.add(Projections.groupProperty(med+p+AfaMedicamento.Fields.DESCRICAO.toString()),MedicamentoDispensadoPorBoxVO.Fields.DESCRICAO_MEDICAMENTO.toString())
				.add(Projections.groupProperty(med+p+AfaMedicamento.Fields.CONCENTRACAO.toString()),MedicamentoDispensadoPorBoxVO.Fields.CONCENTRACAO.toString())
				.add(Projections.groupProperty(med+p+AfaMedicamento.Fields.TPR_SIGLA.toString()),MedicamentoDispensadoPorBoxVO.Fields.APRESENTACAO.toString())
				.add(Projections.groupProperty(med+p+AfaMedicamento.Fields.TUM_SIGLA.toString()),MedicamentoDispensadoPorBoxVO.Fields.TIPO_USO_MEDICAMENTO.toString())
				.add(Projections.groupProperty(AfaDispensacaoMdtos.Fields.NOME_ESTACAO_DISP.toString()),MedicamentoDispensadoPorBoxVO.Fields.ESTACAO_DISPENSADORA.toString())
				.add(Projections.sum(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString()),MedicamentoDispensadoPorBoxVO.Fields.QUANTIDADE.toString())
				.add(Projections.groupProperty(umm+p+MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()),MedicamentoDispensadoPorBoxVO.Fields.UNIDADE_MEDIDA_MEDICA.toString());				

		criteria.setProjection(projection);
		
		// Cláusula where
		Date dataDeEmissaoInicioFormatada = DateUtil.truncaData(dataEmissaoInicio);
		Date dataDeEmissaoFimFormatada = DateUtil.truncaData(DateUtil.adicionaDias(dataEmissaoFim, 1));

		if(dataDeEmissaoInicioFormatada != null){
			criteria.add(Restrictions.between(AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString(), dataDeEmissaoInicioFormatada, dataDeEmissaoFimFormatada));
		}
	
		
		if(medicamento !=null){
			criteria.add(Restrictions.eq(dsm+p+AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), medicamento));
		}
		if(aghMicrocomputador != null){
			criteria.add(Restrictions.ilike(AfaDispensacaoMdtos.Fields.NOME_ESTACAO_DISP.toString(), aghMicrocomputador.getNome(), MatchMode.ANYWHERE));
		}
		
		if(tipoApresentacaoMedicamento !=null){
			criteria.add(Restrictions.eq(med+p+AfaMedicamento.Fields.TPR_SIGLA.toString(), tipoApresentacaoMedicamento.getSigla()));
		}
		
		//#36658
		//criteria.add(Restrictions.eq(med + p + AfaMedicamento.Fields.IND_PADRONIZACAO.toString(), Boolean.TRUE));

		if(afaGrupoApresMdtos != null){
			criteria.add(Subqueries.exists(getCriteriaAfaItemGrupoApresMdto(afaGrupoApresMdtos, med)));
		}
		
		if(pacCodigo !=null){
			criteria.add(Restrictions.eq(atd+p+AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		
		//#36658
		//criteria.add(Restrictions.ne(tum + p + AfaTipoUsoMdto.Fields.GUP_SEQ.toString(), parametroGrupoUsoMdto.getVlrNumerico().intValue()));
		
		//#36658
		//criteria.add(Restrictions.eq(tum + p + AfaTipoUsoMdto.Fields.IND_CONTROLADO.toString(),Boolean.FALSE));
				
		
		
		criteria.add(Restrictions.in(dsm + p + AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), 
				Arrays.asList(
						DominioSituacaoDispensacaoMdto.C,
						DominioSituacaoDispensacaoMdto.E,
						DominioSituacaoDispensacaoMdto.D)));
		
		criteria.add(Restrictions.gt(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString(), BigDecimal.ZERO));
		
		criteria.addOrder(Order.asc(MedicamentoDispensadoPorBoxVO.Fields.ESTACAO_DISPENSADORA.toString()));
		criteria.addOrder(Order.asc(MedicamentoDispensadoPorBoxVO.Fields.DESCRICAO_MEDICAMENTO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MedicamentoDispensadoPorBoxVO.class));
		
		return executeCriteria(criteria);
	}
	
	//#5714
	private DetachedCriteria getCriteriaAfaItemGrupoApresMdto(
			AfaGrupoApresMdtos afaGrupoApresMdtos, String med) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaItemGrupoApresMdtos.class);
		cri.setProjection(Projections.property(AfaItemGrupoApresMdtos.Fields.CRIADO_EM.toString()));
		cri.add(Restrictions.eq(AfaItemGrupoApresMdtos.Fields.GRUPO_APRES_MDTOS.toString(), afaGrupoApresMdtos));
		cri.add(Restrictions.eqProperty(AfaItemGrupoApresMdtos.Fields.SIGLA.toString(), med.concat(".").concat(AfaMedicamento.Fields.TPR_SIGLA.toString())));
		
		return cri;
	}


	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(
			Long seqAfaPrescricaoMedicamento, Integer matCodigo, Boolean comEtiquetas, Boolean comQtdeDispensadaMaiorQueEstornada) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "dispensacao");
		
		cri.createAlias(AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), "medicamento");
		cri.createAlias(AfaDispensacaoMdtos.Fields.FARMACIA.toString(), "farmacia");
		//cri.createAlias("medicamento."+AfaMedicamento.Fields.TPR.toString(), "tipoApresentacao");
		cri.createAlias(AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO.toString(), "afaPrescricaoMdto");
		
		
		//cri.add(Restrictions.eq("tipoApresentacao."+AfaTipoApresentacaoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		cri.add(Restrictions.eq("afaPrescricaoMdto.seq"/*+AfaPrescricaoMedicamento.Fields.SEQ.toString()*/, seqAfaPrescricaoMedicamento));
		
		if(matCodigo != null){
			cri.add(Restrictions.eq("medicamento."+AfaMedicamento.Fields.MAT_CODIGO.toString(), matCodigo));
		}
		
		if(Boolean.TRUE.equals(comEtiquetas)){//Se for null não aplica
			cri.add(Subqueries.exists(getCriteriaBuscaEtiquetasByDispensacaoMdto()));
		}else{
			if(Boolean.FALSE.equals(comEtiquetas)){
				cri.add(Subqueries.notExists(getCriteriaBuscaEtiquetasByDispensacaoMdto()));
			}
		}
		
		if(Boolean.TRUE.equals(comQtdeDispensadaMaiorQueEstornada)){
			cri.add(
					Restrictions.or(
						Restrictions.isNull(AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString())
						,Restrictions.gtProperty(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString(), AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString())
					));
		}
		
		cri.addOrder(Order.asc("medicamento."+AfaMedicamento.Fields.DESCRICAO.toString()));
		
		return executeCriteria(cri);
	}

	private DetachedCriteria getCriteriaBuscaEtiquetasByDispensacaoMdto() {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaDispMdtoCbSps.class, "etiqueta");
		cri.setProjection(Projections.property("etiqueta."+AfaDispMdtoCbSps.Fields.SEQ.toString()));
		
		cri.add(Restrictions.eqProperty("etiqueta."+AfaDispMdtoCbSps.Fields.DISPENSACAO_SEQ.toString(), "dispensacao."+AfaDispensacaoMdtos.Fields.SEQ.toString()));
		return cri;
	}
	
	public List<TicketMdtoDispensadoVO> pesquisarDispensacaoMdtoSemCB(Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro, Long pmmSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "dispensacao");
		criteria.createAlias("dispensacao."+AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString(), "mdto");
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property("dispensacao"+"."+AfaDispensacaoMdtos.Fields.SEQ.toString()), TicketMdtoDispensadoVO.Fields.DISPENSACAO_MDTO_SEQ.toString())
		.add(Projections.property("mdto"+"."+AfaMedicamento.Fields.IND_CONTROLADO.toString()), TicketMdtoDispensadoVO.Fields.MDTO_CONTROLADO.toString())
		.add(Projections.property("mdto"+"."+AfaMedicamento.Fields.DESCRICAO.toString()), TicketMdtoDispensadoVO.Fields.MDTO_DESCRICAO.toString())
		.add(Projections.property("mdto"+"."+AfaMedicamento.Fields.TPR_SIGLA.toString()), TicketMdtoDispensadoVO.Fields.MDTO_SIGLA.toString())
		.add(Projections.property("mdto"+"."+AfaMedicamento.Fields.MAT_CODIGO.toString()), TicketMdtoDispensadoVO.Fields.MDTO_CODIGO.toString())
		.add(Projections.property("dispensacao"+"."+AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString()), TicketMdtoDispensadoVO.Fields.QTD_DISPENSADA.toString())
		.add(Projections.property("dispensacao"+"."+AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString()), TicketMdtoDispensadoVO.Fields.IND_SITUACAO.toString())
		.add(Projections.property("dispensacao"+"."+AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString()), TicketMdtoDispensadoVO.Fields.QTD_ESTORNADA.toString())
		.add(Projections.property("dispensacao"+"."+AfaDispensacaoMdtos.Fields.DTHR_TICKET.toString()), TicketMdtoDispensadoVO.Fields.DTHR_TICKET.toString())
		.add(Projections.property("dispensacao"+"."+AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString()), TicketMdtoDispensadoVO.Fields.DTHR_DISPENSACAO.toString());
 
		criteria.setProjection(projection);	
		criteria.setResultTransformer(Transformers.aliasToBean(TicketMdtoDispensadoVO.class));
		
		if(pmeAtdSeq != null && pmeSeq != null){
			addPrescricaoMedica(criteria, pmeAtdSeq, pmeSeq);
		}else{
			criteria.add(Restrictions.eq("dispensacao."+AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO_SEQ.toString(), pmmSeq));
		}
		criteria.add(Restrictions.isNotNull("dispensacao."+AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString()));
		criteria.add(Restrictions.or(
				Restrictions.isNull("dispensacao."+AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString())
				,Restrictions.gtProperty("dispensacao."+AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString(), "dispensacao."+AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString())));
		addUnfSeqSituacaoDispensacaoMdto(unidadeFuncionalMicro, criteria, DominioSituacaoDispensacaoMdto.D);
		criteria.add(Subqueries.notExists(getCriteriaBuscaEtiquetasByDispensacaoMdto()));
		return executeCriteria(criteria);
	}
	
	public List<TicketMdtoDispensadoVO> pesquisarDispensacaoMdtoComCB(Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro, Long pmmSeq, Date dthrUltimoTicket){
		//Utilizado HQL devido a existência de um parâmetro no left join	(with dispMdtoCb.indExcluido = 'I')
		Boolean cbNaoImpresso = false;
		return criarHqlDispensacaomdto(pmeAtdSeq, pmeSeq, unidadeFuncionalMicro, pmmSeq, dthrUltimoTicket, cbNaoImpresso);
	}
	
	public List<TicketMdtoDispensadoVO> pesquisarDispensacaoMdtoComCBNaoImpresso(Integer pmeAtdSeq, Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro, Long pmmSeq, Date dthrUltimoTicket){
		//Utilizado HQL devido a existência de um parâmetro no left join	(with dispMdtoCb.indExcluido = 'I')
		Boolean cbNaoImpresso = true;
		return criarHqlDispensacaomdto(pmeAtdSeq, pmeSeq, unidadeFuncionalMicro, pmmSeq, dthrUltimoTicket, cbNaoImpresso);
	}

	private List<TicketMdtoDispensadoVO> criarHqlDispensacaomdto(Integer pmeAtdSeq, Integer pmeSeq,
			AghUnidadesFuncionais unidadeFuncionalMicro, Long pmmSeq, Date dthrUltimoTicket, Boolean cbNaoImpresso) {
		StringBuffer hql = new StringBuffer(1018);
		hql.append("select  ");
		hql.append("dispMdto."+AfaDispensacaoMdtos.Fields.SEQ.toString()+" as "+ TicketMdtoDispensadoVO.Fields.DISPENSACAO_MDTO_SEQ.toString() +", ");
		hql.append("mdto."+AfaMedicamento.Fields.IND_CONTROLADO.toString()+" as "+ TicketMdtoDispensadoVO.Fields.MDTO_CONTROLADO.toString() +", ");
		//hql.append("mdto."+AfaMedicamento.Fields.DESCRICAO.toString()+" as "+ TicketMdtoDispensadoVO.Fields.MDTO_DESCRICAO.toString() +", ");
		hql.append("mdto."+AfaMedicamento.Fields.TPR_SIGLA.toString()+" as "+ TicketMdtoDispensadoVO.Fields.MDTO_SIGLA.toString() +", ");
		hql.append("mdto."+AfaMedicamento.Fields.MAT_CODIGO.toString()+" as "+ TicketMdtoDispensadoVO.Fields.MDTO_CODIGO.toString() +", ");
		hql.append("dispMdto."+AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString()+" as "+ TicketMdtoDispensadoVO.Fields.QTD_DISPENSADA.toString() +", ");
		hql.append("dispMdto."+AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString()+" as "+ TicketMdtoDispensadoVO.Fields.IND_SITUACAO.toString() +", ");
		hql.append("dispMdto."+AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString()+" as "+ TicketMdtoDispensadoVO.Fields.QTD_ESTORNADA.toString() +", ");
		if(!cbNaoImpresso){
			hql.append("dispMdto."+AfaDispensacaoMdtos.Fields.DTHR_TICKET.toString()+" as "+ TicketMdtoDispensadoVO.Fields.DTHR_TICKET.toString() +", ");
		}
		hql.append("max(dispMdtoCb."+AfaDispMdtoCbSps.Fields.CRIADO_EM.toString()+") as "+ TicketMdtoDispensadoVO.Fields.DTHR_DISPENSACAO.toString() +", ");
		hql.append("count(dispMdto."+AfaDispensacaoMdtos.Fields.SEQ.toString()+") as "+ TicketMdtoDispensadoVO.Fields.QTD_UTILIZADA_LONG.toString() +" ");
		hql.append("from "+AfaDispensacaoMdtos.class.getSimpleName()+" dispMdto ");
		hql.append("inner join dispMdto."+AfaDispensacaoMdtos.Fields.MEDICAMENTO.toString()+" mdto ");
		hql.append("inner join dispMdto."+AfaDispensacaoMdtos.Fields.AFA_DISP_MDTO_CB_SPS.toString()+" dispMdtoCb with dispMdtoCb."+AfaDispMdtoCbSps.Fields.IND_EXCLUIDO.toString()+" = 'I' ");
		hql.append("where 1 = 1 ");
		
		if(pmmSeq != null){
			hql.append("and dispMdto."+AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO_SEQ.toString()+" = :pmmSeq ");
		}
		else{
			hql.append("and dispMdto."+AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ.toString()+" = :pmeAtdSeq ");
			hql.append("and dispMdto."+AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ.toString()+" = :pmeSeq ");
		}
		if(cbNaoImpresso){
			hql.append("and dispMdtoCb."+AfaDispMdtoCbSps.Fields.CRIADO_EM.toString()+" > :dthrUltimoTicket ");
		}else{
			hql.append("and dispMdtoCb."+AfaDispMdtoCbSps.Fields.CRIADO_EM.toString()+" < :dthrUltimoTicket ");
		}
		
		hql.append("and dispMdto."+AfaDispensacaoMdtos.Fields.FARMACIA_SEQ.toString()+" = :unfSeq ");
		hql.append("and dispMdto."+AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString()+" is not null ");
		hql.append("and dispMdto."+AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString()+" > 0 ");
		hql.append("and dispMdto."+AfaDispensacaoMdtos.Fields.TIPO_OCORRENCIA_DISPENSACAO.toString()+" is null ");
		hql.append("and dispMdto."+AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString()+" = 'D' ");
		hql.append("group by  ");
		hql.append("dispMdto."+AfaDispensacaoMdtos.Fields.SEQ.toString()+", ");
		hql.append("mdto."+AfaMedicamento.Fields.IND_CONTROLADO.toString()+", ");
		//hql.append("mdto."+AfaMedicamento.Fields.DESCRICAO.toString()+", ");
		hql.append("mdto."+AfaMedicamento.Fields.TPR_SIGLA.toString()+", ");
		hql.append("mdto."+AfaMedicamento.Fields.MAT_CODIGO.toString()+", ");
		hql.append("dispMdto."+AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString()+", ");
		hql.append("dispMdto."+AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString()+", ");
		hql.append("dispMdto."+AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString()+", ");
		hql.append("dispMdto."+AfaDispensacaoMdtos.Fields.DTHR_TICKET.toString()+" ");
				
		Query q = createHibernateQuery(hql.toString());
		if(pmmSeq != null){
			q.setLong("pmmSeq", pmmSeq);
		}else{
			q.setInteger("pmeAtdSeq", pmeAtdSeq);
			q.setInteger("pmeSeq", pmeSeq);
		}
		q.setShort("unfSeq", unidadeFuncionalMicro.getSeq());
		q.setTimestamp("dthrUltimoTicket", dthrUltimoTicket);
		
		q.setResultTransformer(Transformers.aliasToBean(TicketMdtoDispensadoVO.class));
		
		return q.list();
	}

	public Boolean existeDispensacaoAnteriorPacienteUTI(Integer atdSeq, Short unfSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class);

		final Object[] values = {DateUtil.truncaData(DateUtil.adicionaDias(new Date(), -1))};	final Type[] types = {DateType.INSTANCE};
		
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.UNID_SOLICITANTE_SEQ.toString(), unfSeq));
        if(isOracle()) {
            criteria.add(Restrictions.sqlRestriction("TRUNC({alias}." + AfaDispensacaoMdtos.Fields.CRIADO_EM.name()+") <= ?", values, types));
        } else {
            criteria.add(Restrictions.sqlRestriction("DATE_TRUNC('DAY', {alias}." + AfaDispensacaoMdtos.Fields.CRIADO_EM.name()+") <= ?", values, types));
        }


		return executeCriteriaCount(criteria) > 0 ? true : false;
	}

}
