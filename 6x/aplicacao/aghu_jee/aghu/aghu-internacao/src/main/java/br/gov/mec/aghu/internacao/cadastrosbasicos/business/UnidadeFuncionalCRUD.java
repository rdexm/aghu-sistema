package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisJn;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionaisJn;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de Unidade Funcional.
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class UnidadeFuncionalCRUD extends BaseBusiness {


	private static final String MSG_AGH_00215 = "AGH-00215 Uma Unidade não pode ser ao mesmo tempo Neonatologia, UTIP, Farmácia, CTI, Obstétrica, Hospital Dia e Emergência";
	
	private static final String FORMAT_ANDAR_STRING = "%s";

	@EJB
	private ImpressoraPadraoON impressoraPadraoON;
	
	private static final Log LOG = LogFactory.getLog(UnidadeFuncionalCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AinQuartosDAO ainQuartosDAO;
	
	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7844184708579904018L;

	private enum UnidadeFuncionalCRUDExceptionCode implements
			BusinessExceptionCode {
		UNIDADE_FUNCIONAL_OBRIGATORIA, 
		ERRO_PERSISTIR_UNIDADE_FUNCIONAL,
		ERRO_ATUALIZAR_UNIDADE_FUNCIONAL, 
		ERRO_VALIDACAO_MODELO_UNIDADE_FUNCIONAL, 
		ERRO_UNIDADE_FUNCIONAL_JA_EXISTENTE, 
		ERRO_REMOVER_UNIDADE_FUNCIONAL, 
		ERRO_ANDAR_UNIDADE_FUNCIONAL, 
		ERRO_CENTRO_CUSTO_UNIDADE_PAI, 
		ERRO_DESATIVAR_UNIDADE, 
		ERRO_ATIVAR_UNIDADE_PAI, 
		ERRO_ATIVAR_UNIDADE_PAI_INATIVA, 
		ERRO_CARACTERISTICA_PRESCRICAO_INFORMATIZADA,
		ERRO_DESATIVAR_UNID_ATENDIMENTOS_HOSP_DIA, 
		ERRO_ANDAR_UNIDADE_NULO, 
		ERRO_DESATIVAR_EXISTEM_UNIDADES_ATIVAS_VINCULADAS, 
		AGH_UNF_CK10, 
		AGH_UNF_CK13,
		AGH_UNF_CK14, 
		AGH_UNF_CK9, 
		AGH_UNF_CK18, 
		AGH_UNF_CK22, 
		AGH_UNF_CK15, 
		AGH_UNF_CK21, 
		AGH_UNF_CK20, 
		AGH_UNF_CK19, 
		ERRO_DESATIVAR_UNIDADE_EXISTEM_ATENDIMENTOS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_SOLICITACAO_EXAMES
		,ERRO_REMOVER_ACOMODACAO_CONSTRAINT_AFA_DISPENSACAO_MDTOS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_DISPENSACAO_MDTOS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_PROF_ATUA_UNID_CIRGS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MAM_PARAM_PREFERIDOS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_V_MPM_CUIDADO_UNFS, 
		ERRO_REMOVER_ACOMODACAO_CONSTRAINT_AEL_UNF_EXECUTA_EXAMES, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_UNF_EXECUTA_EXAMES, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_LOCAL_DISPENSACAO_MDTOS_JN,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPT_AGENDA_PRESCRICOES, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_ITEM_SOLICITACAO_EXAMES, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_V_ABS_MOVIMENTOS_COMPONENTES, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_ALTA_PEDIDO_EXAMES, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPT_CONTROLE_DISPENSACOES, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_IMPRESSORA_PADRAO_UNIDS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_SERVIDOR_UNID_FUNCIONAIS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_EQUIPAMENTO_CIRG_POR_UNIDS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_FAT_ESPELHO_PROCED_SISCOLOS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_V_AEL_ARCO_SOLICITACAO, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_PROC_POR_EQUIPES,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_UNID_FUNC_GRP_ESTATISTICAS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_ALTA_SUMARIOS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPT_TRATAMENTO_TERAPEUTICOS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_CONFIG_MAPAS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_FAT_DADOS_CONTA_SEM_INT,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_UNIDADES_FUNCIONAIS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_GRADE_AGENDA_EXAMES,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_TIPOS_AMOSTRA_EXAMES,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_UNIDADE_NOTA_SALAS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_V_AGH_UNID_FUNCIONAL,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_ATENDIMENTOS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AAC_ATENDIMENTO_APACS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_FICHAS_APACHE,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_TRANSP_MED_OSSEAS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPT_AREA_REALIZACAO_SESSOES, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_QUESTIONARIOS_CONV_UNID,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_HORARIO_ROTINA_COLETAS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_ATENDIMENTOS_PAC_EXTERN, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_FAT_PROCED_AMB_REALIZADOS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_LOCAL_DISPENSACAO_MDTOS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_TRF_DESTINOS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_V_MPT_PRESC_AGENDA,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_FAT_PENDENCIAS_CONTA_HOSP, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_V_AIN_INT_UNID_FUNC,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_CID_UNID_FUNCIONAIS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_INFORMACAO_PRESCRIBENTES, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_ATENDIMENTO_PACIENTES,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_SOLIC_CIRG_POS_ESCALAS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_CUIDADO_USUAL_UNFS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AIN_LEITOS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_CONTROL_IMPRES_MDTOS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_CIRURGIAS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MCI_UNIDADE_AREA_PORTAIS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_NECESSIDADE_CIRURGICAS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AIP_PACIENTES_HIST, 
		ERRO_REMOVER_UNIDADE_ANU_PRESCRICAO_DIETA_IG,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_CARACT_UNID_FUNCIONAIS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_ANU_PRESCRICAO_DIETA_IG, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_V_FAT_TOTAIS_UNIDADE_AMB,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_MOTIVO_INGRESSO_CTIS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AAC_UNID_FUNCIONAL_SALAS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_SALA_CIRURGICAS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_UNID_EXAME_SIGNIFICATIVOS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AIP_PACIENTES, 
		ERRO_REMOVER_UNIDADE_FAT_ITENS_CONTA_HOSPITALAR, 
		ERRO_REMOVER_UNIDADE_MBC_CONTROLE_ESCALA_CIRURGICAS, 
		ERRO_REMOVER_UNIDADE_FAT_ACERTO_AIHS, 
		ERRO_REMOVER_UNIDADE_AGH_MICROCOMPUTADORES, 
		ERRO_REMOVER_UNIDADE_AEL_UNF_EXECUTA_EXAMES, 
		ERRO_REMOVER_UNIDADE_FAT_ACERTO_AMBULATORIOS, 
		ERRO_REMOVER_UNIDADE_AEL_CAD_GUICHES, 
		ERRO_REMOVER_UNIDADE_AIN_QUARTOS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MCI_TMP_CARGA_BIS, 
		ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_HORARIO_INIC_APRAZAMENTOS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_PERMISSAO_UNID_SOLICS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_EXAME_FORA_AGHS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_ESCALA_PRODUCAO_FARMACIAS,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_SOLICITACAO_EXAMES,
		ERRO_REMOVER_UNIDADE_CONSTRAINT_UNID_FUNC_GRP_ESTATISTICAS,
		ERRO_CARACTERISTICA_PME_INFORMATIZADA_CONSECUTIVA, 
		ERRO_CARACTERISTICA_PME_INFORMATIZADA_NUM_DIAS_ADIANTAS, 
		ERRO_CARACTERISTICA_PME_INFORMATIZADA_FARMACIA, 
		ERRO_CARACTERISTICA_INTERNACAO_FARMACIA,
		ERRO_CARACTERISTICA_UNIDADE_NAO_PODE_SER_VARIAS_UNIDADES, 
		ERRO_CARACTERISTICA_UNIDADE_PME_INFORMATIZADA_HORARIO_VALIDADE, 
		ERRO_CARACTERISTICA_UNIDADE_INATIVA, 
		ERRO_CARACTERISTICA_IMPRESSORAS,
		ERRO_CARACTERISTICA_UNIDADE_PME_INFORMATIZADA_HORARIO_VALIDADE_NULO,
		ERRO_REGISTRO_ALTERACOES,
		ERRO_CARAC_UNID_FUNC_DESATIVAR_PME_INFORMATIZADA, 
		ERRO_CARAC_UNID_FUNC_DESATIVAR_FARMACIA,
		ERRO_CARAC_UNID_FUNC_DESATIVAR_PME_INFORMATIZADA_CONSECUTIVA,
		ERRO_REGISTRO_ALTERACOES_CARACTERISTICAS,
		ERRO_DESATIVAR_EXISTEM_INTERNACOES_PENDENTES,
		ERRO_DESATIVAR_EXISTEM_ATENDIMENTOS_URGENCIA,
		ERRO_NIVEL_HIERARQUIA_MAIOR_PERMITIDO,
		ERRO_DE_CONSTRAINT, 
		ERRO_DADOS_PME_NULO,
		ERRO_DADOS_PEN_NULO, 
		ERRO_CARACTERISTICA_UNIDADE_FARMACIA_HORARIO_VALIDADE, 
		ERRO_SIGLA_EXISTENTE,
		ERRO_EXIGE_UNIDADE_FUNCIONAL,
		CENTRO_CUSTO_NAO_INFORMADO;
	}
		
	/**
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final String strPesquisa) {

		// Tenta buscar pelo ID único da unidade funcional e caso encontre 1 elemento
		// válido, então retorna apenas ele.
		if (StringUtils.isNotBlank(strPesquisa) && StringUtils.isNumeric(strPesquisa)) {
			AghUnidadesFuncionais uf = getAghuFacade().obterUnidadeFuncional(Short.valueOf(strPesquisa));
			if (uf != null) {
				List<AghUnidadesFuncionais> lista = new ArrayList<AghUnidadesFuncionais>(1);
				lista.add(uf);
				return lista;
			}
		}
		// Caso não ache 1 elemento pelo ID, tenta buscar por outras informações
		return getAghuFacade().pesquisarUnidadeFuncional(strPesquisa);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalInternacaoAtiva(String strPesquisa){
		return getAghuFacade().pesquisarUnidadeFuncionalInternacaoAtiva(strPesquisa, AghUnidadesFuncionaisDAO.ORDERNAR_POR_ANDAR, true,
				new Object[] { ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO});
	}
	
	private static final Comparator<AghUnidadesFuncionaisVO> COMPARATOR_AGHUNIDADESFUNCIONAISVO = new Comparator<AghUnidadesFuncionaisVO>() {
		@Override
		public int compare(final AghUnidadesFuncionaisVO o1,
				final AghUnidadesFuncionaisVO o2) {
			return o1.getAndarAlaDescricao().toUpperCase().compareTo(
					o2.getAndarAlaDescricao().toUpperCase());
		}
	};

	/**
	 * Método para retornar todas Unidades Funcionais cadastradas que estão
	 * ativas.
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @return Lista com todas Unidades Funcionais
	 */
	public List<AghUnidadesFuncionaisVO> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(
			final Object objPesquisa) {
		final String strPesquisa = (String) objPesquisa;

		final List<AghUnidadesFuncionais> unidadeList = this.getAghuFacade().pesquisarUnidadeFuncionalVOPorCodigoEDescricao(
				strPesquisa);

		// Popula lista de VOs
		final List<AghUnidadesFuncionaisVO> aghUnidadesFuncionaisVOLIST = new ArrayList<AghUnidadesFuncionaisVO>();
		for (final AghUnidadesFuncionais unidadeFuncional : unidadeList) {
			final AghUnidadesFuncionaisVO aghUnidadesFuncionaisVO = popularUnidadeFuncionalVO(unidadeFuncional);

			aghUnidadesFuncionaisVOLIST.add(aghUnidadesFuncionaisVO);
		}

		Collections.sort(aghUnidadesFuncionaisVOLIST,
				COMPARATOR_AGHUNIDADESFUNCIONAISVO);
		return aghUnidadesFuncionaisVOLIST;
	}
	
	public AghUnidadesFuncionaisVO popularUnidadeFuncionalVO(final AghUnidadesFuncionais unidadeFuncional) {
		final AghUnidadesFuncionaisVO aghUnidadesFuncionaisVO = new AghUnidadesFuncionaisVO();
		aghUnidadesFuncionaisVO.setSeq(unidadeFuncional.getSeq());
		aghUnidadesFuncionaisVO.setDescricao(unidadeFuncional
				.getDescricao());
		aghUnidadesFuncionaisVO.setAndar(unidadeFuncional.getAndar());
		aghUnidadesFuncionaisVO.setCapacInternacao(unidadeFuncional
				.getCapacInternacao());
		aghUnidadesFuncionaisVO.setIndAla(unidadeFuncional.getIndAla());
		aghUnidadesFuncionaisVO.setHrioValidadePme(unidadeFuncional
				.getHrioValidadePme());
		aghUnidadesFuncionaisVO.setHrioValidadePen(unidadeFuncional.getHrioValidadePen());
		aghUnidadesFuncionaisVO			
				.setAndarAlaDescricao((unidadeFuncional.getAndar() != null ? String
						.format(FORMAT_ANDAR_STRING, unidadeFuncional.getAndar())
						: "")
						+ " "
						+ (unidadeFuncional.getIndAla() != null ? unidadeFuncional
								.getIndAla().toString()
								: " ")
						+ " - "
						+ (unidadeFuncional.getDescricao() != null ? unidadeFuncional
								.getDescricao()
								: ""));
		aghUnidadesFuncionaisVO.setIndSitUnidFunc(unidadeFuncional.getIndSitUnidFunc());
		aghUnidadesFuncionaisVO.setIndPermPacienteExtra(unidadeFuncional.getIndPermPacienteExtra());
		aghUnidadesFuncionaisVO.setDthrConfCenso(unidadeFuncional.getDthrConfCenso());
		aghUnidadesFuncionaisVO.setIndVerfEscalaProfInt(unidadeFuncional.getIndVerfEscalaProfInt());
		aghUnidadesFuncionaisVO.setIndBloqLtoIsolamento(unidadeFuncional.getIndBloqLtoIsolamento());
		aghUnidadesFuncionaisVO.setIndUnidEmergencia(unidadeFuncional.getIndUnidEmergencia());
		aghUnidadesFuncionaisVO.setCapacInternacao(unidadeFuncional.getCapacInternacao());
		aghUnidadesFuncionaisVO.setClinicas(unidadeFuncional.getClinica());
		aghUnidadesFuncionaisVO.setTiposUnidadeFuncional(unidadeFuncional.getTiposUnidadeFuncional());
		aghUnidadesFuncionaisVO.setIndConsClin(unidadeFuncional.getIndConsClin());
		aghUnidadesFuncionaisVO.setIndUnidHospDia(unidadeFuncional.getIndUnidHospDia());
		aghUnidadesFuncionaisVO.setIndUnidCti(unidadeFuncional.getIndUnidCti());
		aghUnidadesFuncionaisVO.setIndUnidInternacao(unidadeFuncional.getIndUnidInternacao());
		aghUnidadesFuncionaisVO.setNroViasPme(unidadeFuncional.getNroViasPme());
		aghUnidadesFuncionaisVO.setNroViasPen(unidadeFuncional.getNroViasPen());
		
		return aghUnidadesFuncionaisVO;
	}
	
	/**
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param codigo
	 * @return
	 */
	public AghUnidadesFuncionaisVO obterUnidadeFuncionalVO(final Short codigo) {
		final AghUnidadesFuncionais aghUnidadesFuncionais = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(codigo);

		AghUnidadesFuncionaisVO aghUnidadesFuncionaisVO = null;
		aghUnidadesFuncionaisVO = new AghUnidadesFuncionaisVO();
		aghUnidadesFuncionaisVO.setSeq(aghUnidadesFuncionais.getSeq());
		aghUnidadesFuncionaisVO.setDescricao(aghUnidadesFuncionais
				.getDescricao());
		aghUnidadesFuncionaisVO.setAndar(aghUnidadesFuncionais.getAndar());
		aghUnidadesFuncionaisVO.setCapacInternacao(aghUnidadesFuncionais
				.getCapacInternacao());
		aghUnidadesFuncionaisVO.setIndAla(aghUnidadesFuncionais.getIndAla());
		aghUnidadesFuncionaisVO.setHrioValidadePme(aghUnidadesFuncionais
				.getHrioValidadePme());
		aghUnidadesFuncionaisVO
				.setAndarAlaDescricao((aghUnidadesFuncionais.getAndar() != null ? 
						 aghUnidadesFuncionais.getAndar()
						: "")
						+ " "
						+ (aghUnidadesFuncionais.getIndAla() != null ? aghUnidadesFuncionais
								.getIndAla().toString()
								: " ")
						+ " - "
						+ (aghUnidadesFuncionais.getDescricao() != null ? aghUnidadesFuncionais
								.getDescricao()
								: ""));
		aghUnidadesFuncionaisVO.setIndSitUnidFunc(aghUnidadesFuncionais
				.getIndSitUnidFunc());
		aghUnidadesFuncionaisVO.setIndPermPacienteExtra(aghUnidadesFuncionais
				.getIndPermPacienteExtra());
		aghUnidadesFuncionaisVO.setDthrConfCenso(aghUnidadesFuncionais
				.getDthrConfCenso());
		aghUnidadesFuncionaisVO.setIndVerfEscalaProfInt(aghUnidadesFuncionais
				.getIndVerfEscalaProfInt());
		aghUnidadesFuncionaisVO.setIndBloqLtoIsolamento(aghUnidadesFuncionais
				.getIndBloqLtoIsolamento());
		aghUnidadesFuncionaisVO.setIndUnidEmergencia(aghUnidadesFuncionais
				.getIndUnidEmergencia());
		aghUnidadesFuncionaisVO.setCapacInternacao(aghUnidadesFuncionais
				.getCapacInternacao());
		aghUnidadesFuncionaisVO.setClinicas(aghUnidadesFuncionais.getClinica());
		aghUnidadesFuncionaisVO.setTiposUnidadeFuncional(aghUnidadesFuncionais
				.getTiposUnidadeFuncional());
		aghUnidadesFuncionaisVO.setIndConsClin(aghUnidadesFuncionais
				.getIndConsClin());
		aghUnidadesFuncionaisVO.setIndUnidHospDia(aghUnidadesFuncionais
				.getIndUnidHospDia());
		aghUnidadesFuncionaisVO.setIndUnidCti(aghUnidadesFuncionais
				.getIndUnidCti());
		aghUnidadesFuncionaisVO.setIndUnidInternacao(aghUnidadesFuncionais
				.getIndUnidInternacao());
		aghUnidadesFuncionaisVO.setNroViasPme(aghUnidadesFuncionais
				.getNroViasPme());
		aghUnidadesFuncionaisVO.setNroViasPen(aghUnidadesFuncionais
				.getNroViasPen());
		return aghUnidadesFuncionaisVO;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(
			final Object objPesquisa) {
		return pesquisarUnidadeFuncionalPorCodigoEDescricao(objPesquisa, false,
				true, false, false);
	}

	/**
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricao(
			final Object objPesquisa) {
		return pesquisarUnidadeFuncionalPorCodigoEDescricao(objPesquisa, true,
				true, false, false);
	}

	/**
	 * @dbtables AghUnidadesFuncionais select
	 * @param objPesquisa
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoAtivasInativas(
			final Object objPesquisa) {
		return pesquisarUnidadeFuncionalPorCodigoEDescricao(objPesquisa, true,
				false, false, false);
	}

	/**
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoCaracteristicasInternacaoOuEmergencia(
			final Object objPesquisa) {
		return pesquisarUnidadeFuncionalPorCodigoEDescricao(objPesquisa, true,
				true, true, false);
	}
	
	/**
	 * @dbtables AghUnidadesFuncionais select
	 * @param objPesquisa
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoCaractInternacaoOuEmergenciaAtivasInativas(
			final Object objPesquisa) {
		return pesquisarUnidadeFuncionalPorCodigoEDescricao(objPesquisa, true, false, true, false);
	}
	

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExames(
			final String param) {
		return pesquisarUnidadeFuncionalPorCodigoEDescricao(param, true, true, false, true);
	}
	
	public Long pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesCount(
			String param) {
		return pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(param).longValue();
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesOrdenadaDescricao(final String param) {
		return pesquisarUnidadeFuncionalPorCodigoEDescricao(param, false, true, false, true);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesAtivasOrdenadaDescricao(final String param) {
		return pesquisarUnidadeFuncionalPorCodigoEDescricao(param, false, true, false, false);
	}
	
	/**
	 * Método para retornar todas Unidades Funcionais cadastradas que estão
	 * ativas.
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @return Lista com todas Unidades Funcionais
	 */
	private List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final Object objPesquisa,
			final Boolean ordernarPorCodigoAlaDescricao, final boolean apenasAtivos, final boolean caracteristicasInternacaoOuEmergencia,
			final boolean caracteristicaUnidadeExecutora) {
		final String strPesquisa = (String) objPesquisa;

		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa)) {
			final List<AghUnidadesFuncionais> list = this.getAghuFacade().pesquisarUnidadeFuncionalPorCodigoEDescricao(
					Short.valueOf(strPesquisa), ordernarPorCodigoAlaDescricao, apenasAtivos, caracteristicasInternacaoOuEmergencia,
					caracteristicaUnidadeExecutora);

			if (list.size() > 0) {
				return list;
			}
		}

		return this.getAghuFacade().pesquisarUnidadeFuncionalPorCodigoEDescricao(strPesquisa,
				ordernarPorCodigoAlaDescricao, apenasAtivos, caracteristicasInternacaoOuEmergencia, caracteristicaUnidadeExecutora);
	}

	
	public Integer pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(final String param) {
		List<AghUnidadesFuncionais> result = null;
		
		result = pesquisarUnidadeFuncionalPorCodigoEDescricao(param, false, true, false, true);
		
		if(result.isEmpty()) {
			return Integer.valueOf(0);
		}
		
		return result.size();
	}
	
	/**
	 * Pesquisa ordenada de unidade funcional
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalOrdenado(
			final String strPesquisa) {
		// FIXME Arrumar esta consulta para realizar o filtro na consulta
		final List<AghUnidadesFuncionais> lista = this.getAghuFacade().pesquisarUnidadesFuncionais();
		List<AghUnidadesFuncionais> listaFiltrada = null;
		
		if (strPesquisa != null || StringUtils.isNotBlank(strPesquisa)) {
			listaFiltrada = new ArrayList<AghUnidadesFuncionais>();
			for (final AghUnidadesFuncionais unf : lista) {
				if (unf.getLPADAndarAlaDescricao().contains(strPesquisa.toUpperCase())) {
					listaFiltrada.add(unf);
				}
			}
			Collections.sort(listaFiltrada, new Comparator<AghUnidadesFuncionais>() {
				@Override
				public int compare(final AghUnidadesFuncionais o1, final AghUnidadesFuncionais o2) {
					return o1.getLPADAndarAlaDescricao().compareTo(o2.getLPADAndarAlaDescricao());
				}
			});
			return listaFiltrada;	
		}
		
		Collections.sort(lista, new Comparator<AghUnidadesFuncionais>() {
			@Override
			public int compare(final AghUnidadesFuncionais o1, final AghUnidadesFuncionais o2) {
				return o1.getLPADAndarAlaDescricao().compareTo(o2.getLPADAndarAlaDescricao());
			}
		});

		return lista;
	}
	
	/**
	 * 
	 * @dbtables AghCaractUnidFuncionais select
	 * @dbtables AinAtendimentosUrgencia select
	 * @dbtables AinInternacao select
	 * @dbtables AghUnidadesFuncionais select,update
	 * 
	 * @param unidadeFuncional
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void atualizarUnidadeFuncionalidade(AghUnidadesFuncionais unidadeFuncional, List<ConstanteAghCaractUnidFuncionais> caracteristicas) throws ApplicationBusinessException {
		try {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			unidadeFuncional.setRapServidor(servidorLogado);
			unidadeFuncional.setPreSerMatricula(servidorLogado.getId().getMatricula());
			unidadeFuncional.setPreSerVinCodigo(servidorLogado.getVinculo().getCodigo());
			
			validarDadosUnidadesFuncionais(unidadeFuncional, caracteristicas);

			final AghUnidadesFuncionais unidFuncOriginal = iAghuFacade.obterAghUnidadesFuncionaisOriginal(unidadeFuncional);
			final List<ConstanteAghCaractUnidFuncionais> caracteristicasOld = obterListaCaracteristicasEnum(unidFuncOriginal);

			verificaExigeAlmoxarifado(unidadeFuncional);
			validarDadosAlteracaoUnidade(unidFuncOriginal, unidadeFuncional, caracteristicas, caracteristicasOld);
			
			validarDadosAlteracaoCaracUnidade(unidFuncOriginal, unidadeFuncional, caracteristicas, caracteristicasOld);
			//Setando Valores Default caso não tenham sido marcado pelo usuário 			
			
			atualizarIndicadoresDasCaracteristicas(unidadeFuncional, caracteristicas);
			
			final Short capacMin = 0;
			
			//Em toda inclusão deve setar o Ind Visualiza IG para S
			if(unidadeFuncional.getIndVisualizaIg()== null) {
				unidadeFuncional.setIndVisualizaIg(DominioSimNao.S);
			}		
			
			//Setando Valores Default caso não tenham sido marcado pelo usuário
			if(unidadeFuncional.getIndVisualizaIap()== null) {
				unidadeFuncional.setIndVisualizaIap(DominioSimNao.N);
			}
			
			if(unidadeFuncional.getIndBloqLtoIsolamento()== null) {
				unidadeFuncional.setIndBloqLtoIsolamento(DominioSimNao.N);
			}
			
			if(unidadeFuncional.getIndPermPacienteExtra()== null) {
				unidadeFuncional.setIndPermPacienteExtra(DominioSimNao.N);
			}	
			
			if(unidadeFuncional.getIndConsClin() == null) {
				unidadeFuncional.setIndConsClin(DominioSimNao.N);
			}
			
			if(unidadeFuncional.getIndAnexaDocAutomatico()==null) {
				unidadeFuncional.setIndAnexaDocAutomatico(DominioSimNao.N);
			}
			
			if(unidadeFuncional.getIndUnidHospDia()== null) {
				unidadeFuncional.setIndUnidHospDia(DominioSimNao.N);
			}
			
			if(unidadeFuncional.getIndUnidEmergencia() == null) {
				unidadeFuncional.setIndUnidEmergencia(DominioSimNao.N);
			}
			
			if(unidadeFuncional.getIndUnidCti()==null) {
				unidadeFuncional.setIndUnidCti(DominioSimNao.N);
			}
			
			if(unidadeFuncional.getIndVerfEscalaProfInt()==null) {
				unidadeFuncional.setIndVerfEscalaProfInt(DominioSimNao.N);
			}
			
			if(unidadeFuncional.getIndSitUnidFunc()==null) {
				unidadeFuncional.setIndSitUnidFunc(DominioSituacao.A);
			}
			
			if(unidadeFuncional.getIndUnidInternacao()==null) {
				unidadeFuncional.setIndUnidInternacao(DominioSimNao.N);
			}
			
			if(unidadeFuncional.getCapacInternacao()==null) {
				unidadeFuncional.setCapacInternacao(capacMin);
			}
			
			if(unidadeFuncional.getCentroCusto() != null) {
			
				if(unidadeFuncional.getHrioInicioAtendimento()!= null && unidadeFuncional.getHrioFimAtendimento()!= null){
					
					final Calendar cal = Calendar.getInstance();
					final Calendar cal2 = Calendar.getInstance();
					
					final SimpleDateFormat ano = new SimpleDateFormat(DateConstants.DATE_PATTERN_YYYY);
					final SimpleDateFormat mes = new SimpleDateFormat(DateConstants.DATE_PATTERN_MM);
					cal.setTime(unidadeFuncional.getHrioInicioAtendimento());
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.YEAR, Integer.valueOf(ano.format(cal2.getTime())));
					cal.set(Calendar.MONTH, Integer.valueOf(mes.format(cal2.getTime()))-1);
					unidadeFuncional.setHrioInicioAtendimento(cal.getTime());
					
					final Calendar cal4 = Calendar.getInstance();
					final Calendar cal3 = Calendar.getInstance();
					
					final SimpleDateFormat ano2 = new SimpleDateFormat(DateConstants.DATE_PATTERN_YYYY);
					final SimpleDateFormat mes2 = new SimpleDateFormat(DateConstants.DATE_PATTERN_MM);
					cal4.setTime(unidadeFuncional.getHrioFimAtendimento());
					cal4.set(Calendar.DAY_OF_MONTH, 1);
					cal4.set(Calendar.YEAR, Integer.valueOf(ano2.format(cal3.getTime())));
					cal4.set(Calendar.MONTH, Integer.valueOf(mes2.format(cal3.getTime()))-1);
					unidadeFuncional.setHrioFimAtendimento(cal4.getTime());
				
				
				}
	
				if(unidadeFuncional.getHrioValidadePme()!= null){
					
					final Calendar cal6 = Calendar.getInstance();
					final Calendar cal5 = Calendar.getInstance();
					final SimpleDateFormat ano3 = new SimpleDateFormat(DateConstants.DATE_PATTERN_YYYY);
					final SimpleDateFormat mes3 = new SimpleDateFormat(DateConstants.DATE_PATTERN_MM);
					cal6.setTime(unidadeFuncional.getHrioValidadePme());
					cal6.set(Calendar.DAY_OF_MONTH, 1);
					cal6.set(Calendar.YEAR, Integer.valueOf(ano3.format(cal5.getTime())));
					cal6.set(Calendar.MONTH, Integer.valueOf(mes3.format(cal5.getTime()))-1);
					unidadeFuncional.setHrioValidadePme(cal6.getTime());
				}
	
				if(unidadeFuncional.getHrioValidadePen() != null){
					final Calendar cal7 = Calendar.getInstance();
					final Calendar cal8 = Calendar.getInstance();
					final SimpleDateFormat ano4 = new SimpleDateFormat(DateConstants.DATE_PATTERN_YYYY);
					final SimpleDateFormat mes4 = new SimpleDateFormat(DateConstants.DATE_PATTERN_MM);
					cal7.setTime(unidadeFuncional.getHrioValidadePen());
					cal7.set(Calendar.DAY_OF_MONTH, 1);
					cal7.set(Calendar.YEAR, Integer.valueOf(ano4.format(cal8.getTime())));
					cal7.set(Calendar.MONTH, Integer.valueOf(mes4.format(cal8.getTime()))-1);
					unidadeFuncional.setHrioValidadePen(cal7.getTime());
					
				}
				
				//#15348 - Refatorar cadastro de unidades funcionais para se adequar a mudanças com relação a ala.
				if (!Objects.equals(unidFuncOriginal.getIndAla(), unidadeFuncional.getIndAla())) {
					final Short unfSeq = unidadeFuncional.getSeq();
					final List<AinQuartos> quartosList = this.getAinQuartosDAO().listarQuartosPorUnf(null, unfSeq, false);
					for (final AinQuartos quarto : quartosList) {
						if (unidadeFuncional.getIndAla() == null) {
							quarto.setAla(null);
						} else {
							final AghAla ala = this.getAghuFacade().obterAghAlaPorChavePrimaria(unidadeFuncional.getIndAla().getCodigo());
							quarto.setAla(ala);						
						}
						getAinQuartosDAO().atualizar(quarto);
					}
				}
				
				unidadeFuncional = iAghuFacade.atualizarAghUnidadesFuncionaisSemException(unidadeFuncional);
				
				boolean mudouCarcteristicas = false;
				if(caracteristicas.size() != caracteristicasOld.size()){
					mudouCarcteristicas = true;
				} else {
					for (ConstanteAghCaractUnidFuncionais caractOld : caracteristicasOld) {
						if(!caracteristicas.contains(caractOld)){
							mudouCarcteristicas = true;
							break;
						}
					}
				}
				
				//Setando dados no Journal de Unidades Funcionais 
				journalUpdate(unidFuncOriginal,unidadeFuncional, mudouCarcteristicas);
				
				journalUpdateCaractUnidFunc(unidFuncOriginal,unidadeFuncional, caracteristicas, caracteristicasOld);
				journalExclusaoCaracteristicas(unidFuncOriginal,unidadeFuncional, caracteristicas, caracteristicasOld);
				
				super.flush();
				super.clear();
				
				persistirCaracteristicas(unidadeFuncional, caracteristicas, servidorLogado, caracteristicasOld);
			
		  } else {
			  throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.CENTRO_CUSTO_NAO_INFORMADO);
		  }
		}catch (final BaseRuntimeException em) {			
			throw new ApplicationBusinessException(em.getCode());
			
		} catch (final PersistenceException e) {
			LOG.error("Erro ao atualizar", e);
			lancaErroConstraint(e);
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_ATUALIZAR_UNIDADE_FUNCIONAL);

		} catch (Exception e) {
			if(e instanceof ApplicationBusinessException){
				throw e;
				
			} else if(e.getCause() instanceof BaseRuntimeException){
				throw new ApplicationBusinessException(e.getCause().getMessage(), null);
				
			} else {
				throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_PERSISTIR_UNIDADE_FUNCIONAL);
			}
		}
	}

	private List<ConstanteAghCaractUnidFuncionais> obterListaCaracteristicasEnum(final AghUnidadesFuncionais unidFuncOriginal) {
		final List<AghCaractUnidFuncionais> caracteristicasUnidFuncionais = iAghuFacade.listarCaracteristicasUnidadesFuncionais(unidFuncOriginal.getSeq(), null, null, null);
		final List<ConstanteAghCaractUnidFuncionais> caracteristicas = new ArrayList<>(caracteristicasUnidFuncionais.size());
		
		for (AghCaractUnidFuncionais aghCaractUnidFuncionais : caracteristicasUnidFuncionais) {
			if(aghCaractUnidFuncionais != null){
				caracteristicas.add(aghCaractUnidFuncionais.getId().getCaracteristica());
			}
		}
		return caracteristicas;
	}

	private void persistirCaracteristicas(
			final AghUnidadesFuncionais unidadeFuncional,
			final List<ConstanteAghCaractUnidFuncionais> caracteristicas,
			final RapServidores servidorLogado,
			final List<ConstanteAghCaractUnidFuncionais> caracteristicasOld) {
		
		AghCaractUnidFuncionaisId id = new AghCaractUnidFuncionaisId(unidadeFuncional.getSeq(), null);
		
		if(caracteristicasOld != null){
			
			for (ConstanteAghCaractUnidFuncionais caractOld : caracteristicasOld) {
				// Remove apenas as que foram desassociadas
				if(!caracteristicas.contains(caractOld)){
					id = new AghCaractUnidFuncionaisId(unidadeFuncional.getSeq(), caractOld);
					iAghuFacade.desassociarAghCaractUnidFuncionais(id);					
				}
			}
			
			iAghuFacade.atualizarAghUnidadesFuncionaisSemException(unidadeFuncional);
			
			for (ConstanteAghCaractUnidFuncionais caract : caracteristicas) {
				// adiciona apenas caracteristicas novas
				if(!caracteristicasOld.contains(caract)){
					id = new AghCaractUnidFuncionaisId(unidadeFuncional.getSeq(), caract);
					AghCaractUnidFuncionais  acuf = new AghCaractUnidFuncionais(id, unidadeFuncional, servidorLogado);
					iAghuFacade.associarAghCaractUnidFuncionais(acuf);
					flush();
				}
			}
			
		} else {
			// entra quando de uma inserção
			for (ConstanteAghCaractUnidFuncionais caract : caracteristicas) {
				id = new AghCaractUnidFuncionaisId(unidadeFuncional.getSeq(), caract);
				AghCaractUnidFuncionais  acuf = new AghCaractUnidFuncionais(id, unidadeFuncional, servidorLogado);
				iAghuFacade.associarAghCaractUnidFuncionais(acuf);
				flush();
			}
		}
		
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	/*
	 *  ORADB: Triggers AGHT_CUN_ARI e AGHT_CUN_ARD
	 *  ORADB: Procedure AGHK_CUN_RN.RN_CUNP_ATU_UND_FUNC
		toda vez que for inserida ou deletada as características: permite paciente
		extra, verifica escala prof int, unidade emergencia, consiste clínica,
		unidade CTI, unidade hospital dia, unidade internação e bloqueia leito
		isolamento deve-se atualizar os respectivos indicadores na
		AGH_UNIDADES_FUNCIONAIS.
		Na inserção atualizar os indicadores para "S" e na deleção para "N".
 	 */
	private void atualizarIndicadoresDasCaracteristicas(final AghUnidadesFuncionais unidadeFuncional, 
				final List<ConstanteAghCaractUnidFuncionais> caracteristicas) {
		
		unidadeFuncional.setIndPermPacienteExtra( validaCaracteristicaParaDominioSimNao(caracteristicas, ConstanteAghCaractUnidFuncionais.PERMITE_PACIENTE_EXTRA));		
		unidadeFuncional.setIndUnidHospDia(       validaCaracteristicaParaDominioSimNao(caracteristicas, ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA));
		unidadeFuncional.setIndUnidInternacao(    validaCaracteristicaParaDominioSimNao(caracteristicas, ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO));
		unidadeFuncional.setIndUnidEmergencia(    validaCaracteristicaParaDominioSimNao(caracteristicas, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA));
		unidadeFuncional.setIndVerfEscalaProfInt( validaCaracteristicaParaDominioSimNao(caracteristicas, ConstanteAghCaractUnidFuncionais.VERF_ESCALA_PROF_INT));
		unidadeFuncional.setIndUnidCti(           validaCaracteristicaParaDominioSimNao(caracteristicas, ConstanteAghCaractUnidFuncionais.UNID_CTI));
		unidadeFuncional.setIndBloqLtoIsolamento( validaCaracteristicaParaDominioSimNao(caracteristicas, ConstanteAghCaractUnidFuncionais.BLOQ_LTO_ISOLAMENTO));
		unidadeFuncional.setIndConsClin(          validaCaracteristicaParaDominioSimNao(caracteristicas, ConstanteAghCaractUnidFuncionais.CONS_CLIN));
	}
	
	private DominioSimNao validaCaracteristicaParaDominioSimNao(final List<ConstanteAghCaractUnidFuncionais> caracteristicas, ConstanteAghCaractUnidFuncionais caracteristica){
		if(caracteristicas.contains(caracteristica)){
			return DominioSimNao.S;
			
		} else {
			return DominioSimNao.N;
		}
	}
	
	/* Método reponsável para validar os dados das Unidades */
	@SuppressWarnings("PMD.NPathComplexity")
	private void validarDadosUnidadesFuncionais(final AghUnidadesFuncionais unidade, List<ConstanteAghCaractUnidFuncionais> caracteristicas) throws ApplicationBusinessException {
		
		//Verifica se sigla já existe
		final AghUnidadesFuncionais unidadeExistente = getAghuFacade().obterUnidadeFuncionalPorSigla(unidade.getSigla());
		
		if (unidadeExistente != null && !unidadeExistente.getSeq().equals(unidade.getSeq())){
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_SIGLA_EXISTENTE);
		}

		// (AGH-00550) Centro de Custo deve ser preenchido quando Unid Pai estiver informada
		if (unidade.getUnfSeq() != null && unidade.getCentroCusto() == null) {
			LOG.debug("(AGH-00550) Centro de Custo deve ser preenchido quando Unid Pai estiver informada ");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CENTRO_CUSTO_UNIDADE_PAI);
		}
		
		
		if((unidade.getHrioValidadePme()!= null && unidade.getIndUnidTempoPmeAdiantada()!=null && unidade.getNroUnidTempoPmeAdiantadas()!=null && unidade.getNroViasPme() == null) ||
				(unidade.getHrioValidadePme()== null && unidade.getIndUnidTempoPmeAdiantada()!=null && unidade.getNroUnidTempoPmeAdiantadas()!=null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePme()!= null && unidade.getIndUnidTempoPmeAdiantada()==null && unidade.getNroUnidTempoPmeAdiantadas()!=null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePme()!= null && unidade.getIndUnidTempoPmeAdiantada()!=null && unidade.getNroUnidTempoPmeAdiantadas()==null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePme()== null && unidade.getIndUnidTempoPmeAdiantada()==null && unidade.getNroUnidTempoPmeAdiantadas()==null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePme()== null && unidade.getIndUnidTempoPmeAdiantada()==null && unidade.getNroUnidTempoPmeAdiantadas()!=null && unidade.getNroViasPme() == null)
		){
			LOG.debug("(AGH-00286) Infomre o horário de validade PME, o Tempo de Adiantada,a unidade de tempo o nº de vias ou nenhum dos campos ");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_DADOS_PME_NULO);
		}
		
		if((unidade.getHrioValidadePme()!= null && unidade.getIndUnidTempoPmeAdiantada()!=null && unidade.getNroUnidTempoPmeAdiantadas()==null && unidade.getNroViasPme() == null) ||
				(unidade.getHrioValidadePme()== null && unidade.getIndUnidTempoPmeAdiantada() ==null && unidade.getNroUnidTempoPmeAdiantadas()!=null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePme()!= null && unidade.getIndUnidTempoPmeAdiantada()==null && unidade.getNroUnidTempoPmeAdiantadas()!=null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePme()== null && unidade.getIndUnidTempoPmeAdiantada()!=null && unidade.getNroUnidTempoPmeAdiantadas()==null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePme()== null && unidade.getIndUnidTempoPmeAdiantada()!=null && unidade.getNroUnidTempoPmeAdiantadas()==null && unidade.getNroViasPme() == null)
		){
			LOG.debug("(AGH-00286) Infomre o horário de validade PME, o Tempo de Adiantada,a unidade de tempo o nº de vias ou nenhum dos campos ");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_DADOS_PME_NULO);
		}
		
		if((unidade.getHrioValidadePen()!= null && unidade.getIndUnidTempoPenAdiantada()!=null && unidade.getNroUnidTempoPenAdiantadas()!=null && unidade.getNroViasPme() == null) ||
				(unidade.getHrioValidadePen()== null && unidade.getIndUnidTempoPenAdiantada()!=null && unidade.getNroUnidTempoPenAdiantadas()!=null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePen()!= null && unidade.getIndUnidTempoPenAdiantada()==null && unidade.getNroUnidTempoPenAdiantadas()!=null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePen()!= null && unidade.getIndUnidTempoPenAdiantada()!=null && unidade.getNroUnidTempoPenAdiantadas()==null && unidade.getNroViasPme() != null)
		){
			LOG.debug("(AGH-00287) Infomre o horário de validade PEN, o Tempo de Adiantada,a unidade de tempo o nº de vias ou nenhum dos campos ");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_DADOS_PEN_NULO);
		}
		
		if((unidade.getHrioValidadePen()!= null && unidade.getIndUnidTempoPenAdiantada()!=null && unidade.getNroUnidTempoPenAdiantadas()==null && unidade.getNroViasPme() == null) ||
				(unidade.getHrioValidadePen()== null && unidade.getIndUnidTempoPenAdiantada() ==null && unidade.getNroUnidTempoPenAdiantadas()!=null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePen()!= null && unidade.getIndUnidTempoPenAdiantada()==null && unidade.getNroUnidTempoPenAdiantadas()==null && unidade.getNroViasPme() != null)
				|| (unidade.getHrioValidadePen()== null && unidade.getIndUnidTempoPenAdiantada()!=null && unidade.getNroUnidTempoPenAdiantadas()==null && unidade.getNroViasPme() != null)
		){
			LOG.debug("(AGH-00287) Infomre o horário de validade PEN, o Tempo de Adiantada,a unidade de tempo o nº de vias ou nenhum dos campos ");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_DADOS_PEN_NULO);
		}
		
		// Verifica se unidade pai já possui 3 níveis de hierarquia
		if (unidade.getUnfSeq() != null) {
			final int count = recuperarNiveisHierarquia(unidade.getUnfSeq().getSeq());
			if (count >= 3) {
				throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_NIVEL_HIERARQUIA_MAIOR_PERMITIDO);
			}
		}
		
		this.validarDadosCaractUnidade(unidade, caracteristicas);
		
	}

	// AGHT_UNF_BRU
	/**
	 * @dbtables AinAtendimentosUrgencia select
	 * @dbtables AinInternacao select
	 * @dbtables AghCaractUnidFuncionais select 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param unidadeOriginal
	 * @param unidadeNova
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void validarDadosAlteracaoUnidade(final AghUnidadesFuncionais unidadeOriginal, final AghUnidadesFuncionais unidadeNova, 
											  final List<ConstanteAghCaractUnidFuncionais> caracteristicas, 
											  final List<ConstanteAghCaractUnidFuncionais> caracteristicasOld) 
											throws ApplicationBusinessException {
		
		if (unidadeOriginal.isAtivo() && !unidadeNova.isAtivo()
				&& DominioSimNao.S.equals(unidadeOriginal.getIndUnidEmergencia())) { // Verifica se no original esta
																					 // ativo e no novo inativo
			// FIXME: Aqui deveria validar se existem dados nas tabelas 
			// AIN_ATENDIMENTOS_URGENCIA, AIN_LEITOS, AIN_QUARTOS, conforme procedure AGHP_UNF_VER_ATU_UNF
			LOG.debug("Não pode desativar unidade funcional se existem atendimentos de urgência na unidade.");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_DESATIVAR_UNIDADE_EXISTEM_ATENDIMENTOS);
		}

			
		// Não pode ativar unidade funcional se a unidade pai estiver inativa. (AIN-00748)
		if (unidadeNova.isAtivo() && unidadeNova.getUnfSeq() != null) {
			if(unidadeOriginal.getUnfSeq()!= null && !unidadeOriginal.getUnfSeq().isAtivo()){
				LOG.debug("Não pode ativar unidade funcional se a unidade pai estiver inativa. (AIN-00748)");
				throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_ATIVAR_UNIDADE_PAI_INATIVA);
				
			}else if(unidadeNova.getUnfSeq()!= null && !unidadeNova.getUnfSeq().isAtivo()){
				LOG.debug("Não pode ativar unidade funcional se a unidade pai estiver inativa. (AIN-00748)");
				throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_ATIVAR_UNIDADE_PAI_INATIVA);	
			}
			
		}
		
		//	Não pode desativar unidade funcional se existem unidades funcionais ativas vinculadas (AIN-00747)
		if(unidadeOriginal.isAtivo()&& !unidadeNova.isAtivo()){
			
			List<AghUnidadesFuncionais> listaUnidadesFilhas = iAghuFacade.pesquisaUnidadesFilhasVinculadas(unidadeOriginal.getSeq(),DominioSituacao.A);
			if(listaUnidadesFilhas!=null) {
				for(final AghUnidadesFuncionais unid : listaUnidadesFilhas){
					if(unid.isAtivo()){
						LOG.debug("AIN-00747 - Esta unidade está vinculada a outra inativa. Você deve ativá-la anteriormente");
						throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_DESATIVAR_EXISTEM_UNIDADES_ATIVAS_VINCULADAS);					
					}
				}	
			}
		}
		
		// AGHT_UNF_BRU
		/*
		 * Ao tornar o horário de validade da prescrição nulo não pode haver a
		 * característica de prescrição médica informatizada
		 */
		verificarHorarioValidadePreecricao(unidadeOriginal, unidadeNova, caracteristicasOld);

		// Não pode desativar unidade funcional se existem atendimentos em
		// hospital dia para ela.

		if (DominioSimNao.S.equals(unidadeOriginal.getIndUnidHospDia()) && !unidadeNova.isAtivo()) {
			LOG.debug("Não pode desativar unidade funcional se existem atendimentos em hospital dia para ela.");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_DESATIVAR_UNID_ATENDIMENTOS_HOSP_DIA);
		}
		
		//Verifica pacientes internados  para unidade funcional a ser desativada
		if(unidadeOriginal.isAtivo()&& !unidadeNova.isAtivo() && possuiAtendimentosInternados(unidadeOriginal)){
			LOG.debug("AIN-00744 Existem internações pendentes nesta unidade. Não é possível desativá-la");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_DESATIVAR_EXISTEM_INTERNACOES_PENDENTES);
		}
		
		//Verifica pacientes em atendimento de urgência  para unidade funcional a ser desativada.
		
		if(unidadeOriginal.isAtivo() && !unidadeNova.isAtivo() && possuiPacientesAtendimentoUrgencia(unidadeOriginal)){
			//AIN-00746 Existem atendimentos de urgência pendentes nesta unidade.Não é possível desativá-la
			LOG.debug("AIN-00746 Existem atendimentos de urgência pendentes nesta unidade.Não é possível desativá-la");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_DESATIVAR_EXISTEM_ATENDIMENTOS_URGENCIA);
		}
		
		// Só pode inserir uma característica para uma unidade funcional ativa
		if(!unidadeNova.isAtivo() && caracteristicas != null && caracteristicas.size() > 0){
			for(final ConstanteAghCaractUnidFuncionais acuf : caracteristicas){
				if(!caracteristicasOld.contains(acuf)){
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_INATIVA);
				}
			}
		}
	}
	
		
	/**
	 * @dbtables AinInternacao select
	 * 
	 * @param unidade
	 * @return
	 */
	private boolean possuiAtendimentosInternados(final AghUnidadesFuncionais unidade){
		final List<AghUnidadesFuncionais> unidades = this.getAghuFacade().pesquisaPacienteInternado(0, 200, null, true,
				unidade.getSeq(), true);
		return !unidades.isEmpty();
	}
	
	
	/**
	 * @dbtables AinAtendimentosUrgencia select
	 * 
	 * @param unidade
	 * @return
	 */
	private boolean possuiPacientesAtendimentoUrgencia(final AghUnidadesFuncionais unidade){
		final List<AghUnidadesFuncionais> unidades = this.getAghuFacade().pesquisaAtendimentoUrgencia(0, 200, null,
				true, unidade.getSeq(), true);
		return !unidades.isEmpty();
	}
	
	
	private void validarDadosAlteracaoCaracUnidade(final AghUnidadesFuncionais unidadeOriginal,final AghUnidadesFuncionais unidadeNova, 
												   final List<ConstanteAghCaractUnidFuncionais> caracteristicas, 
												   final List<ConstanteAghCaractUnidFuncionais> caracteristicasOld)
														   throws ApplicationBusinessException {
		
		//VALIDAR DESATIVACAO DA CARACTERISTICA
		if(caracteristicasOld.contains(ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA) && 
				!caracteristicas.contains(ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA) &&
				unidadeNova.getNroUnidTempoPmeAdiantadas()!= null
		){
			LOG.debug("AGH-00241 Quando a unidade não tiver prescrição médica informatizada não pode ser informado o número de unidades de tempo de prescrição adiantada");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARAC_UNID_FUNC_DESATIVAR_PME_INFORMATIZADA);
		}
		
		if(caracteristicasOld.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA) && 
				!caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA) &&
				unidadeNova.getHrioInicioAtendimento()!= null && unidadeNova.getHrioFimAtendimento()!= null
		){
			LOG.debug("AGH-00236 O horário de início e de fim do atendimento da unidade não deve ser informado quando esta não for mais farmácia");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARAC_UNID_FUNC_DESATIVAR_FARMACIA);
		}
		
		if(caracteristicasOld.contains(ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA) && 
				!caracteristicas.contains(ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA) &&
				caracteristicas.contains(ConstanteAghCaractUnidFuncionais.PME_CONSECUTIVA)
		){
			LOG.debug("AGH-00243 Ao deletar a caracterísitica de prescrição médica informatizada não pode haver a característica de prescrição médica consecutiva");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARAC_UNID_FUNC_DESATIVAR_PME_INFORMATIZADA_CONSECUTIVA);
		}
		
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void validarDadosCaractUnidade(final AghUnidadesFuncionais unidadeNova, List<ConstanteAghCaractUnidFuncionais> caracteristicas) throws ApplicationBusinessException {
		
		//Validar características
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.PME_CONSECUTIVA) && 
				!caracteristicas.contains(ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA)){
			LOG.debug("AGH-00242 Ao inserir a característica de prescrição médica consecutiva deve haver a característica de prescrição médica consecutiva");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_PME_INFORMATIZADA_CONSECUTIVA);
		}
		
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA) && unidadeNova.getNroUnidTempoPmeAdiantadas() == null){
			LOG.debug("AGH-00240 Quando a unidade tiver prescrição médica informatizada deve ser informado o número de unidades de tempo de prescrição adiantada");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_PME_INFORMATIZADA_NUM_DIAS_ADIANTAS);
		}
		
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA) && 
				caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA)){
			LOG.debug("AGH-00239 Uma unidade não pode ser farmácia e ao mesmo tempo ter prescrição médica informatizada");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_PME_INFORMATIZADA_FARMACIA);
		}
		
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO) && 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA)){
			LOG.debug("AGH-00212 Uma Unidade não pode ser ao mesmo tempo Farmácia e Internação");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_INTERNACAO_FARMACIA);
		}
		
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA) &&
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_UTIP) &&
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_CTI) &&
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA) &&
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_OBSTETRICA)&&
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA) &&
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)
		){
			LOG.debug(MSG_AGH_00215);
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_NAO_PODE_SER_VARIAS_UNIDADES);
		}
		
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_UTIP)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_CTI)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_OBSTETRICA)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)){
			
			LOG.debug(MSG_AGH_00215);
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_NAO_PODE_SER_VARIAS_UNIDADES);
		}
		
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_UTIP)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_CTI)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_UTIP)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA)||				
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_UTIP)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_OBSTETRICA)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_UTIP)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_UTIP)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)						
		){
			LOG.debug(MSG_AGH_00215);
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_NAO_PODE_SER_VARIAS_UNIDADES);
		}
		
		if(	caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_CTI)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA)||				
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_CTI)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_OBSTETRICA)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_CTI)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_CTI)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)						
		){
			LOG.debug(MSG_AGH_00215);
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_NAO_PODE_SER_VARIAS_UNIDADES);
		}
		
		if(	caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_OBSTETRICA)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)						
		){
			LOG.debug(MSG_AGH_00215);
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_NAO_PODE_SER_VARIAS_UNIDADES);
		}
		
		if( caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_OBSTETRICA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA)||
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_OBSTETRICA)&& 
			caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)						
		){
			LOG.debug(MSG_AGH_00215);
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_NAO_PODE_SER_VARIAS_UNIDADES);
		}
		
		if(	caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA)&& 
				caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA) ){
			
			LOG.debug(MSG_AGH_00215);
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_NAO_PODE_SER_VARIAS_UNIDADES);
		}
		
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA)
			&& unidadeNova.getHrioValidadePme() == null){
			LOG.debug("AGH-00234 O horário de validade da prescrição médica deve ser informado quando a unidade tem a característica de prescrição médica informatizada");
			//AGH-00233 Para inserir a característica de Prescrição Médica informatizada é necessário preencher o horário de validade da Prescrição Médica
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_PME_INFORMATIZADA_HORARIO_VALIDADE);
		}
		
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA)
			&& unidadeNova.getHrioInicioAtendimento() == null && unidadeNova.getHrioFimAtendimento() == null
		){
			LOG.debug("AGH-00235 O horário de início e de fim do atendimento da unidade deve ser informado quando esta for farmácia");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_FARMACIA_HORARIO_VALIDADE);
		}
		
		//AGH-00234 AGH-00234 O horário de validade da prescrição médica deve ser informado quando a unidade tem a característica de prescrição médica informatizada
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA) && unidadeNova.getHrioValidadePme() == null){
			LOG.debug("AGH-00234 AGH-00234 O horário de validade da prescrição médica deve ser informado quando a unidade tem a característica de prescrição médica informatizada");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_PME_INFORMATIZADA_HORARIO_VALIDADE_NULO);
		}
		
		//AGH-00416 A característica #1 não pode ser informada, pois a unidade #2 possui mais que uma impressora para o mesmo tipo de impressão.
		if(caracteristicas.contains(ConstanteAghCaractUnidFuncionais.POSSUI_IMPRESSORA_PADRAO)){
			final Long numImp = this.getImpressoraPadraoON().pesquisaImpressorasCount(unidadeNova.getSeq());
			if(numImp > 1){
				LOG.debug("AGH-00416 A característica 'Possui Impressora Padrao' não pode ser informada, pois a unidade possui mais que uma impressora para o mesmo tipo de impressão");
				throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_IMPRESSORAS);
			}
		}
	}
	
	private void validarInclusaoAtivas(final AghUnidadesFuncionais unidadeNova)throws ApplicationBusinessException {
		if (!unidadeNova.isAtivo() && unidadeNova.getCaracteristicas() != null && !unidadeNova.getCaracteristicas().isEmpty()) {
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_UNIDADE_INATIVA);
		}
	}
	
	
	// AGHT_UNF_BRU
	/*
	 * Ao tornar o horário de validade da prescrição nulo não pode haver a
	 * característica de prescrição médica informatizada
	 */
	/**
	 * 
	 * @dbtables AghCaractUnidFuncionais select
	 * 
	 * @param unidadeOriginal
	 * @param unidadeNova
	 * @throws ApplicationBusinessException
	 */
	private void verificarHorarioValidadePreecricao(final AghUnidadesFuncionais unidadeOriginal, final AghUnidadesFuncionais unidadeNova, final List<ConstanteAghCaractUnidFuncionais> caracteristicasOld) throws ApplicationBusinessException {
		/*
		 * ao tornar o horário de validade da prescrição nulo não pode haver
		 * a característica de prescrição médica informatizada
		 */
		if (unidadeOriginal.getHrioValidadePme() != null && unidadeNova.getHrioValidadePme() == null
				&& unidadeNova.getCaracteristicas() != null
				&& caracteristicasOld.contains(ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA)) {
			LOG.debug("ao tornar o horário de validade da prescrição nulo não pode haver a característica de prescrição médica informatizada.");
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_CARACTERISTICA_PRESCRICAO_INFORMATIZADA);
		}
	}

	
	
	private void verificaExigeAlmoxarifado(final AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException {
		if (unidadeFuncional.getControleEstoque() && (unidadeFuncional.getAlmoxarifado() == null || 
				unidadeFuncional.getAlmoxarifado().getSeq() == null)) {
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_EXIGE_UNIDADE_FUNCIONAL);
		}
	}
	
	/**
	 * 
	 * @dbtables AghCaractUnidFuncionais select
	 * @dbtables AinAtendimentosUrgencia select
	 * @dbtables AinInternacao select
	 * @dbtables AghUnidadesFuncionais select,insert
	 * 
	 * @param unidade
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void incluirUnidadeFuncional(final AghUnidadesFuncionais unidade, List<ConstanteAghCaractUnidFuncionais> caracteristicas) throws ApplicationBusinessException {
		try {
			final RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			verificaExigeAlmoxarifado(unidade);
			
			final Short capacMin = 0;

			//Setando Valores Default
			//Em toda inclusão deve setar o Ind Visualiza IG para S
			unidade.setIndVisualizaIg(DominioSimNao.S);
			
			unidade.setRapServidor(servidorLogado);
			unidade.setPreSerMatricula(servidorLogado.getId().getMatricula());
			unidade.setPreSerVinCodigo(servidorLogado.getVinculo().getCodigo());
			
			//Setando Valores Default caso não tenham sido marcado pelo usuário
			if(unidade.getIndVisualizaIap()== null) {
				unidade.setIndVisualizaIap(DominioSimNao.N);
			}
			
			if(unidade.getIndBloqLtoIsolamento()== null) {
				unidade.setIndBloqLtoIsolamento(DominioSimNao.N);
			}
			
			if(unidade.getIndPermPacienteExtra()== null) {
				unidade.setIndPermPacienteExtra(DominioSimNao.N);
			}
			
			if(unidade.getIndConsClin() == null) {
				unidade.setIndConsClin(DominioSimNao.N);
			}
			
			if(unidade.getIndAnexaDocAutomatico()==null) {
				unidade.setIndAnexaDocAutomatico(DominioSimNao.N);
			}
			
			if(unidade.getIndUnidHospDia()== null) {
				unidade.setIndUnidHospDia(DominioSimNao.N);
			}
			
			if(unidade.getIndUnidEmergencia() == null) {
				unidade.setIndUnidEmergencia(DominioSimNao.N);
			}
			
			if(unidade.getIndUnidCti()==null) {
				unidade.setIndUnidCti(DominioSimNao.N);
			}
			
			if(unidade.getIndVerfEscalaProfInt()==null) {
				unidade.setIndVerfEscalaProfInt(DominioSimNao.N);
			}
			
			if(unidade.getIndSitUnidFunc()==null) {
				unidade.setIndSitUnidFunc(DominioSituacao.A);
			}
			
			if(unidade.getIndUnidInternacao()==null) {
				unidade.setIndUnidInternacao(DominioSimNao.N);
			}
			
			if(unidade.getCapacInternacao()==null) {
				unidade.setCapacInternacao(capacMin);
			}
			
			validarInclusaoAtivas(unidade);
			validarDadosUnidadesFuncionais(unidade, caracteristicas);
			atualizarIndicadoresDasCaracteristicas(unidade, caracteristicas);
			
			if(unidade.getCentroCusto() != null) {
			
				if(unidade.getHrioInicioAtendimento() != null && unidade.getHrioFimAtendimento() != null){
					final Calendar cal = Calendar.getInstance();
					final Calendar cal2 = Calendar.getInstance();
					
					final SimpleDateFormat ano = new SimpleDateFormat(DateConstants.DATE_PATTERN_YYYY);
					final SimpleDateFormat mes = new SimpleDateFormat(DateConstants.DATE_PATTERN_MM);
					cal.setTime(unidade.getHrioInicioAtendimento());
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.YEAR, Integer.valueOf(ano.format(cal2.getTime())));
					cal.set(Calendar.MONTH, Integer.valueOf(mes.format(cal2.getTime()))-1);
					unidade.setHrioInicioAtendimento(cal.getTime());
					
					final Calendar cal5 = Calendar.getInstance();
					final Calendar cal8 = Calendar.getInstance();
					
					final SimpleDateFormat ano2 = new SimpleDateFormat(DateConstants.DATE_PATTERN_YYYY);
					final SimpleDateFormat mes2 = new SimpleDateFormat(DateConstants.DATE_PATTERN_MM);
					cal5.setTime(unidade.getHrioFimAtendimento());
					cal5.set(Calendar.DAY_OF_MONTH, 1);
					cal5.set(Calendar.YEAR, Integer.valueOf(ano2.format(cal8.getTime())));
					cal5.set(Calendar.MONTH, Integer.valueOf(mes2.format(cal8.getTime()))-1);
					unidade.setHrioFimAtendimento(cal5.getTime());
	
				}
				
				if(unidade.getHrioValidadePme()!= null){
					final Calendar cal3 = Calendar.getInstance();
					final Calendar cal7 = Calendar.getInstance();
					
					final SimpleDateFormat ano3 = new SimpleDateFormat(DateConstants.DATE_PATTERN_YYYY);
					final SimpleDateFormat mes3 = new SimpleDateFormat(DateConstants.DATE_PATTERN_MM);
					cal3.setTime(unidade.getHrioValidadePme());
					cal3.set(Calendar.DAY_OF_MONTH, 1);
					cal3.set(Calendar.YEAR, Integer.valueOf(ano3.format(cal7.getTime())));
					cal3.set(Calendar.MONTH, Integer.valueOf(mes3.format(cal7.getTime()))-1);
					unidade.setHrioValidadePme(cal3.getTime());
				}
				
				if(unidade.getHrioValidadePen()!= null){
			
					final Calendar cal4 = Calendar.getInstance();
					final Calendar cal6 = Calendar.getInstance();
					
					final SimpleDateFormat ano4 = new SimpleDateFormat(DateConstants.DATE_PATTERN_YYYY);
					final SimpleDateFormat mes4 = new SimpleDateFormat(DateConstants.DATE_PATTERN_MM);
					cal4.setTime(unidade.getHrioValidadePen());
					cal4.set(Calendar.DAY_OF_MONTH, 1);
					cal4.set(Calendar.YEAR, Integer.valueOf(ano4.format(cal6.getTime())));
					cal4.set(Calendar.MONTH, Integer.valueOf(mes4.format(cal6.getTime()))-1);
					unidade.setHrioValidadePen(cal4.getTime());
				}
				
				iAghuFacade.persistirAghUnidadesFuncionais(unidade);
				
				journalUpdateCaractUnidFunc(null,unidade, caracteristicas, null);
				
				persistirCaracteristicas(unidade, caracteristicas, servidorLogado, null);
			
		  } else {
			  throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.CENTRO_CUSTO_NAO_INFORMADO);
		  }
		} catch (final BaseRuntimeException em){	
			 unidade.setSeq(null);
			throw new ApplicationBusinessException(em.getCode());
			
		} catch (final PersistenceException e) {
			 unidade.setSeq(null);
			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause().getClass())
					&& StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "AGH_UNF_UK1")) {
				throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_SIGLA_EXISTENTE);
			}
			
			lancaErroConstraint(e);
			LOG.error("Erro ao incluir a unidade funcional.", e);			
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_PERSISTIR_UNIDADE_FUNCIONAL);
			
		} catch (Exception e) {
			unidade.setSeq(null);

			if(e instanceof ApplicationBusinessException){
				throw e;
				
			}else if(e.getCause() instanceof BaseRuntimeException){
				throw new ApplicationBusinessException(e.getCause().getMessage(), null);
				
			} else {
				throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_PERSISTIR_UNIDADE_FUNCIONAL);
			}
		}
	}
	
	private void lancaErroConstraint(final PersistenceException pe) throws ApplicationBusinessException {
		final Throwable cause = pe.getCause();
		if (cause instanceof ConstraintViolationException) {
			final ConstraintViolationException cve = (ConstraintViolationException) cause;
			try {
				if (cause instanceof ConstraintViolationException) {
					throw new IllegalArgumentException();
				}
				final String constraint_name = cve.getConstraintName().substring(cve.getConstraintName().indexOf(".") + 1);
				final Constraint constraint = Constraint.valueOf(constraint_name); 
				throw new ApplicationBusinessException(constraint.getExceptionCode());
			} catch (final IllegalArgumentException iaex) {
				LOG.error("ConstraintViolationException", pe);
				throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_DE_CONSTRAINT,cve.getConstraintName() );
			}
		}
	}

	// AGH_UNF_PK
	// Associa constraints que podem ser lançadas pelo banco aos erros de
	// negócio
	private enum Constraint {
		AGH_UNF_PK(
				UnidadeFuncionalCRUDExceptionCode.ERRO_UNIDADE_FUNCIONAL_JA_EXISTENTE);

		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private final UnidadeFuncionalCRUDExceptionCode exception_code;

		Constraint(final UnidadeFuncionalCRUDExceptionCode exception_code) {
			this.exception_code = exception_code;
		}

		public UnidadeFuncionalCRUDExceptionCode getExceptionCode() {
			return this.exception_code;
		}
	}

//	Rotina de Journaling executado durante, a inclusão e atualização de unidades.
//	 ORADB: Triggers AGHT_UNF_ARD e AGHT_UNF_ARU
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void journalUpdate(final AghUnidadesFuncionais velho,final AghUnidadesFuncionais novo, boolean mudouCaracteristicas) throws ApplicationBusinessException {
		if (!Objects.equals(velho.getAndar(), novo.getAndar())
				|| !Objects.equals(velho.getDescricao(), novo.getDescricao())
				|| !Objects.equals(velho.getSigla(), novo.getSigla())
				|| !Objects.equals(velho.getIndAla(), novo.getIndAla())
				|| !Objects.equals(velho.getCapacInternacao(), novo.getCapacInternacao())
				|| !Objects.equals(velho.getCentroCusto(), novo.getCentroCusto())
				|| !Objects.equals(velho.getIndAla(), novo.getIndAla())
				|| !Objects.equals(velho.getDescricaoSituacao(), novo.getDescricaoSituacao())
				|| !Objects.equals(velho.getClinica(), novo.getClinica())
				|| !Objects.equals(velho.getIndPermPacienteExtra(), novo.getIndPermPacienteExtra())
				|| !Objects.equals(velho.getDthrConfCenso(), novo.getDthrConfCenso())
				|| !Objects.equals(velho.getRotinaFuncionamento(), novo.getRotinaFuncionamento())
				|| !Objects.equals(velho.getIndSitUnidFunc(), novo.getIndSitUnidFunc())
				|| !Objects.equals(velho.getHrioInicioAtendimento(), novo.getHrioInicioAtendimento())
				|| !Objects.equals(velho.getHrioFimAtendimento(), novo.getHrioFimAtendimento())
				|| !Objects.equals(velho.getNroUltimoProtocolo(), novo.getNroUltimoProtocolo())
				|| !Objects.equals(velho.getHrioValidadePen(), novo.getHrioValidadePen())
				|| !Objects.equals(velho.getHrioValidadePme(), novo.getHrioValidadePme())
				|| !Objects.equals(velho.getIndAnexaDocAutomatico(), novo.getIndAnexaDocAutomatico())
				|| !Objects.equals(velho.getSeq(), novo.getSeq())
				|| !Objects.equals(velho.getIndVerfEscalaProfInt(), novo.getIndVerfEscalaProfInt())
				|| !Objects.equals(velho.getIndConsClin(), novo.getIndConsClin())
				|| !Objects.equals(velho.getIndUnidHospDia(), novo.getIndUnidHospDia())
				|| !Objects.equals(velho.getIndUnidInternacao(), novo.getIndUnidInternacao())
				|| !Objects.equals(velho.getNroUnidTempoPenAdiantadas(), novo.getNroUnidTempoPenAdiantadas())
				|| !Objects.equals(velho.getNroUnidTempoPmeAdiantadas(), novo.getNroUnidTempoPmeAdiantadas())
				|| !Objects.equals(velho.getIndUnidTempoPenAdiantada(), novo.getIndUnidTempoPenAdiantada())
				||	!Objects.equals(velho.getSerMatricula(), novo.getSerMatricula())
				|| !Objects.equals(velho.getSerVinCodigo(), novo.getSerVinCodigo())
				|| !Objects.equals(velho.getNroViasPen(), novo.getNroViasPen())
				|| !Objects.equals(velho.getNroViasPme(), novo.getNroViasPme())
				|| !Objects.equals(velho.getIndTipoTratamento(), novo.getIndTipoTratamento())
				|| !Objects.equals(velho.getPreSerMatricula(), novo.getPreSerMatricula())
				|| !Objects.equals(velho.getPreSerVinCodigo(), novo.getPreSerVinCodigo())
				|| !Objects.equals(velho.getPreEspSeq(), novo.getPreEspSeq())
				|| !Objects.equals(velho.getQtdDiasLimiteCirg(), novo.getQtdDiasLimiteCirg())
				|| !Objects.equals(velho.getTempoMaximoCirurgia(), novo.getTempoMaximoCirurgia())
				|| !Objects.equals(velho.getTempoMinimoCirurgia(), novo.getTempoMinimoCirurgia())
				|| !Objects.equals(velho.getIndVisualizaIap(), novo.getIndVisualizaIap())
				|| !Objects.equals(velho.getIndVisualizaIg(), novo.getIndVisualizaIg())
				|| !Objects.equals(velho.getIntervaloEscalaCirurgia(), novo.getIntervaloEscalaCirurgia())
				|| !Objects.equals(velho.getIntervaloEscalaProced(), novo.getIntervaloEscalaProced())
				|| !Objects.equals(velho.getLocalDocAnexo(), novo.getLocalDocAnexo())
				|| !Objects.equals(velho.getTiposUnidadeFuncional(), novo.getTiposUnidadeFuncional())
				|| !Objects.equals(velho.getRapServidor(), novo.getRapServidor())
				|| !Objects.equals(velho.getRapServidorChefia(), novo.getRapServidorChefia())
				|| !Objects.equals(velho.getUnfSeq(), novo.getUnfSeq())
				|| !Objects.equals(velho.getIndUnidTempoPmeAdiantada(),novo.getIndUnidTempoPmeAdiantada())
				|| !Objects.equals(velho.getVersion(), novo.getVersion())
				|| !Objects.equals(velho.getTiposUnidadeFuncional(), novo.getTiposUnidadeFuncional())
				|| !Objects.equals(velho.getQtdDiasLimiteCirgConvenio(), novo.getQtdDiasLimiteCirgConvenio())
				|| mudouCaracteristicas) {
			
			final RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			final AghUnidadesFuncionaisJn unidadeJn  = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AghUnidadesFuncionaisJn.class, servidorLogado.getUsuario());
			
			if(velho.getSeq() != null) {
				unidadeJn.setSeq(velho.getSeq());
			}
			
			if(velho.getDescricao() != null) {
				unidadeJn.setDescricao(velho.getDescricao());
			}
			
			if(velho.getAndar()!= null) {
				unidadeJn.setAndar(velho.getAndar());
			}
			
			if (velho.getCapacInternacao() != null) {
				unidadeJn.setCapacInternacao(velho.getCapacInternacao());
			}
			
			if (velho.getDthrConfCenso() != null) {
				unidadeJn.setDthrConfCenso(velho.getDthrConfCenso());
			}
			
			if(velho.getIndSitUnidFunc()!= null) {
				unidadeJn.setIndSitUnidFunc(velho.getIndSitUnidFunc());
			}			
			
			if (velho.getIndAla() != null) {
				unidadeJn.setIndAla(velho.getIndAla().toString());
			}
			
			if (velho.getUnfSeq() != null) {
				unidadeJn.setUnfSeq(velho.getUnfSeq().getSeq());
			}
			
			if (velho.getRotinaFuncionamento() != null) {
				unidadeJn.setRotinaFuncionamento(velho.getRotinaFuncionamento());
			}
			
			if (velho.getHrioInicioAtendimento() != null) {
				unidadeJn.setHrioInicioAtendimento(velho.getHrioInicioAtendimento());
			}
			
			if (velho.getHrioFimAtendimento() != null) {
				unidadeJn.setHrioFimAtendimento(velho.getHrioFimAtendimento());
			}
			
			if (velho.getNroUnidTempoPenAdiantadas() != null) {
				unidadeJn.setNroUnidTempoPenAdiantadas(velho.getNroUnidTempoPenAdiantadas());
			}
			
			if (velho.getNroUnidTempoPmeAdiantadas() != null) {
				unidadeJn.setNroUnidTempoPmeAdiantadas(velho.getNroUnidTempoPmeAdiantadas());
			}
			
			if (velho.getIndUnidTempoPenAdiantada() != null) {
				unidadeJn.setIndUnidTempoPenAdiantada(velho.getIndUnidTempoPenAdiantada());
			}
			
			if (velho.getIndUnidTempoPmeAdiantada() != null) {
				unidadeJn.setIndUnidTempoPmeAdiantada(velho.getIndUnidTempoPmeAdiantada());
			}
			
			if (velho.getHrioValidadePen() != null) {
				unidadeJn.setHrioValidadePen(velho.getHrioValidadePen());
			}
			
			if (velho.getHrioValidadePme() != null) {
				unidadeJn.setHrioValidadePme(velho.getHrioValidadePme());
			}
			
			if (velho.getSerMatricula() != null) {
				unidadeJn.setSerMatricula(velho.getSerMatricula().intValue());
			}
			
			if (velho.getSerVinCodigo() != null) {
				unidadeJn.setSerVinCodigo(velho.getSerVinCodigo().shortValue());
			}
			
			if (velho.getIndVisualizaIg() != null) {
				unidadeJn.setIndVisualizaIg(velho.getIndVisualizaIg());
			}
			
			if (velho.getIndVisualizaIap() != null) {
				unidadeJn.setIndVisualizaIap(velho.getIndVisualizaIap());
			}
			
			if (velho.getIntervaloEscalaCirurgia() != null) {
				unidadeJn.setIntervaloEscalaCirurgia(velho.getIntervaloEscalaCirurgia());
			}
			
			if (velho.getIntervaloEscalaProced() != null) {
				unidadeJn.setIntervaloEscalaProced(velho.getIntervaloEscalaProced());
			}
			
			if (velho.getIndAnexaDocAutomatico() != null) {
				unidadeJn.setIndAnexaDocAutomatico(velho.getIndAnexaDocAutomatico());
			}
			
			if (velho.getLocalDocAnexo() != null) {
				unidadeJn.setLocalDocAnexo(velho.getLocalDocAnexo());
			}
			
			if (velho.getCentroCusto() != null) {
				unidadeJn.setCctCodigo(velho.getCentroCusto().getCodigo());
			}
			if (velho.getClinica() != null) {
				unidadeJn.setClcCodigo(velho.getClinica().getCodigo().byteValue());
			}
			
			if (velho.getTiposUnidadeFuncional() != null) {
				unidadeJn.setTufSeq(velho.getTiposUnidadeFuncional().getCodigo().byteValue());
			}
			
			if (velho.getQtdDiasLimiteCirgConvenio() != null) {
				unidadeJn.setQtdDiasLimiteCirgConvenio(velho.getQtdDiasLimiteCirgConvenio());
			}
			
			this.getAghuFacade().persistirAghUnidadesFuncionaisJn(unidadeJn);
		}
	}
	
	//Rotina de Journaling executado durante a inclusão e remoção de características de unidades.
	private void journalUpdateCaractUnidFunc(final AghUnidadesFuncionais velho, final AghUnidadesFuncionais novo, 
			  final List<ConstanteAghCaractUnidFuncionais> caracteristicas, 
			  final List<ConstanteAghCaractUnidFuncionais> caracteristicasOld ) throws ApplicationBusinessException {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		//Se velho == null então é uma inserção de nova unidade funcional
		if (velho == null && caracteristicas != null && caracteristicas.size() > 0) {
			for(final ConstanteAghCaractUnidFuncionais caracteristica : caracteristicas){
				final AghCaractUnidFuncionaisJn unidadeJn  = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.INS, AghCaractUnidFuncionaisJn.class, servidorLogado.getUsuario());

				if (novo.getSerMatricula() != null) {
					unidadeJn.setSerMatricula(novo.getSerMatricula());
				}
				
				if (novo.getSerVinCodigo() != null) {
					unidadeJn.setSerVinCodigo(novo.getSerVinCodigo());
				}
				
				if (novo.getSeq() != null) {
					unidadeJn.setUnfSeq(novo.getSeq().intValue());
				}	
				
				unidadeJn.setCaracteristica(caracteristica);
			
				iAghuFacade.persistirAghCaractUnidFuncionaisJn(unidadeJn);
			}
			
		} else if (!Objects.equals(caracteristicasOld, caracteristicas) && caracteristicas != null) {
			for(final ConstanteAghCaractUnidFuncionais caract : caracteristicas){
				if(!caracteristicasOld.contains(caract)){					
					final AghCaractUnidFuncionaisJn unidadeJn  = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.INS, AghCaractUnidFuncionaisJn.class, servidorLogado.getUsuario());
					
					if (novo.getSerMatricula() != null) {
						unidadeJn.setSerMatricula(novo.getSerMatricula());
					}
					
					if (novo.getSerVinCodigo() != null) {
						unidadeJn.setSerVinCodigo(novo.getSerVinCodigo());
					}
					
					if (novo.getSeq() != null) {
						unidadeJn.setUnfSeq(novo.getSeq().intValue());
					}		
					
					unidadeJn.setCaracteristica(caract);
					
					iAghuFacade.persistirAghCaractUnidFuncionaisJn(unidadeJn);
				}
			}
		}		
	}
	

	/**
	 * @dbtables AghUnidadesFuncionais delete
	 * 
	 * @param unidade
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount"})
	public void excluirUnidade(final Short seq) throws ApplicationBusinessException {
		try {
			
			
			final AghUnidadesFuncionais unidade = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seq);	

			final AghUnidadesFuncionais unidFuncOriginal = iAghuFacade.obterAghUnidadesFuncionaisOriginal(unidade);
			final List<ConstanteAghCaractUnidFuncionais> caracteristicasOld = obterListaCaracteristicasEnum(unidFuncOriginal);
			
			this.getAghuFacade().excluirAghUnidadesFuncionais(unidade);

			//CHAMAR JOURNAL CARACTERISTICAS
			//Setando dados no Journal de Unidades Funcionais 
			this.journalExclusao(unidade);
			
			journalExclusaoCaracteristicas(unidade, null, null, caracteristicasOld);
			
			//Flush deve ser aqui pq se violar FK será tratado!
			this.flush();
		} catch (final PersistenceException e) {
			LOG.error("Erro ao remover a unidade funcional.", e);
			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause().getClass())) {
				
				//possui dependencia em AEL_SOLICITACAO_EXAMES
				if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_SOE_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_SOLICITACAO_EXAMES);
					
				//possui dependencia em AFA_DISPENSACAO_MDTOS
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_DSM_UNF_FK2")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_DISPENSACAO_MDTOS);
					
				//possui dependencia em MBC_PROF_ATUA_UNID_CIRGS
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_PUC_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_PROF_ATUA_UNID_CIRGS);
					
				//possui dependencia em MAM_PARAM_PREFERIDOS
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MAM_PPF_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MAM_PARAM_PREFERIDOS);
					
				//possui dependencia em V_MPM_CUIDADO_UNFS
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_VCU_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_V_MPM_CUIDADO_UNFS);
					
				//possui dependencia em AEL_UNF_EXECUTA_EXAMES
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_UFE_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_UNF_EXECUTA_EXAMES);
					
				//possui dependencia em AFA_LOCAL_DISPENSACAO_MDTOS_JN
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_LDMJ_UNF_FK2")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_LOCAL_DISPENSACAO_MDTOS_JN);
					
				// possui dependencia em MPT_AGENDA_PRESCRICOES
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPT_AGP_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPT_AGENDA_PRESCRICOES);
					
				//possui dependencia em AEL_ITEM_SOLICITACAO_EXAMES
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_ISE_UNF_FK2")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_ITEM_SOLICITACAO_EXAMES);
					
				//possui dependencia em V_ABS_MOVIMENTOS_COMPONENTES
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"ABS_VMCO_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_V_ABS_MOVIMENTOS_COMPONENTES);
					
				//possui dependencia em MPM_ALTA_PEDIDO_EXAMES
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_AEX_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_ALTA_PEDIDO_EXAMES);
					
				//possui dependencia em MPT_CONTROLE_DISPENSACOES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPT_COD_UNF_FK2")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPT_CONTROLE_DISPENSACOES);
					
				//possui dependencia em AGH_IMPRESSORA_PADRAO_UNIDS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AGH_IPU_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_IMPRESSORA_PADRAO_UNIDS);
					
				//possui dependencia em MPM_SERVIDOR_UNID_FUNCIONAIS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_SUF_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_SERVIDOR_UNID_FUNCIONAIS);
					
				//possui dependencia em MBC_EQUIPAMENTO_CIRG_POR_UNIDS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_ECU_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_EQUIPAMENTO_CIRG_POR_UNIDS);
					
				//ppossui dependencia em MPM_SERVIDOR_UNID_FUNCIONAIS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_SOE_UNF_FK2")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_SERVIDOR_UNID_FUNCIONAIS);
					
				//possui dependencia em FAT_ESPELHO_PROCED_SISCOLOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"FAT_EPS_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_FAT_ESPELHO_PROCED_SISCOLOS);
					
				//possui dependencia em V_AEL_ARCO_SOLICITACAO
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_VAA_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_V_AEL_ARCO_SOLICITACAO);
					
				//se possui dependencia em MBC_PROC_POR_EQUIPES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_PXQ_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_PROC_POR_EQUIPES);
					
				//se possui dependencia em AFA_LOCAL_DISPENSACAO_MDTOS_JN
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_LDMJ_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_LOCAL_DISPENSACAO_MDTOS_JN);
					
				//possui dependencia em MPM_ALTA_PEDIDO_EXAMES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_AEX_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_ALTA_PEDIDO_EXAMES);
					
				//Verifica se possui dependencia em AFA_LOCAL_DISPENSACAO_MDTOS_JN
				//possui dependencia em AEL_UNID_FUNC_GRP_ESTATISTICAS
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_UGE_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_UNID_FUNC_GRP_ESTATISTICAS);
					
				//Verifica se possui dependencia em MPM_ALTA_SUMARIOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_ASU_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_ALTA_SUMARIOS);
					
				//Verifica  se possui dependencia em MPT_TRATAMENTO_TERAPEUTICOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPT_TRP_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPT_TRATAMENTO_TERAPEUTICOS);
					
				//Verifica  se possui dependencia em AEL_CONFIG_MAPAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_CGM_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_CONFIG_MAPAS);
					
				//se possui dependencia em FAT_DADOS_CONTA_SEM_INT
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"FAT_DCS_UNF_FK")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_FAT_DADOS_CONTA_SEM_INT);
					
				//se possui dependencia em AGH_UNIDADES_FUNCIONAIS
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "AGH_UNF_UNF_FK")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_UNIDADES_FUNCIONAIS);
					
				//se possui dependencia em AGH_UNIDADES_FUNCIONAIS
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AGH_UNF_UNF_FK")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_UNIDADES_FUNCIONAIS);
					
				//se possui dependencia em AFA_DISPENSACAO_MDTOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_DSM_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_DISPENSACAO_MDTOS);
					
				//se possui dependencia em AEL_GRADE_AGENDA_EXAMES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_GAE_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_GRADE_AGENDA_EXAMES);
					
				//se possui dependencia em AEL_TIPOS_AMOSTRA_EXAMES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_TAE_UNF_FK11")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_TIPOS_AMOSTRA_EXAMES);
					
				//se possui dependencia em MBC_UNIDADE_NOTA_SALAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_NOA_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_UNIDADE_NOTA_SALAS);
					
				//se possui dependencia em V_AGH_UNID_FUNCIONAL
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AGH_VUF_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_V_AGH_UNID_FUNCIONAL);
					
				//se possui dependencia em AGH_ATENDIMENTOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AGH_ATD_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_ATENDIMENTOS);
					
				//se possui dependencia em AAC_ATENDIMENTO_APACS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AAC_ATM_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AAC_ATENDIMENTO_APACS);
				
				//se possui dependencia em MPM_FICHAS_APACHE
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_FIAJ_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_FICHAS_APACHE);
					
				//se possui dependencia em MBC_TRANSP_MED_OSSEAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_MTO_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_TRANSP_MED_OSSEAS);
					
				//se possui dependencia em MPT_AREA_REALIZACAO_SESSOES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPT_ARS_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPT_AREA_REALIZACAO_SESSOES);
					
				//se possui dependencia em AEL_QUESTIONARIOS_CONV_UNID
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_QCU_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_QUESTIONARIOS_CONV_UNID);
					
				//se possui dependencia em AGH_ATENDIMENTOS_PAC_EXTERN
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AGH_APE_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_ATENDIMENTOS_PAC_EXTERN);
					
				//se possui dependencia em FAT_PROCED_AMB_REALIZADOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"FAT_PMR_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_FAT_PROCED_AMB_REALIZADOS);
					
				//se possui dependencia em AFA_LOCAL_DISPENSACAO_MDTOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_LDM_UNF_FK5")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_LOCAL_DISPENSACAO_MDTOS);
					
				//se possui dependencia em MPM_TRF_DESTINOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_TDE_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_TRF_DESTINOS);
					
				//se possui dependencia em V_MPT_PRESC_AGENDA
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPT_VP_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_V_MPT_PRESC_AGENDA);
					
				//se possui dependencia em FAT_PENDENCIAS_CONTA_HOSP
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"FAT_FPC_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_FAT_PENDENCIAS_CONTA_HOSP);
					
				//se possui dependencia em V_AIN_INT_UNID_FUNC
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AIN_VIU_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_V_AIN_INT_UNID_FUNC);
					
				//se possui dependencia em MPM_CID_UNID_FUNCIONAIS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_CUF_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_CID_UNID_FUNCIONAIS);
					
				//se possui dependencia em MPM_INFORMACAO_PRESCRIBENTES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_IFP_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_INFORMACAO_PRESCRIBENTES);
					
				//se possui dependencia em AGH_ATENDIMENTO_PACIENTES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AGH_APA_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_ATENDIMENTO_PACIENTES);
					
				//se possui dependencia em MBC_SOLIC_CIRG_POS_ESCALAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_SPE_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_SOLIC_CIRG_POS_ESCALAS);
					
				//se possui dependencia em MPM_CUIDADO_USUAL_UNFS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_CUU_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_CUIDADO_USUAL_UNFS);
					
				//se possui dependencia em AIN_LEITOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AIN_LTO_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AIN_LEITOS);
					
				//se possui dependencia em AFA_LOCAL_DISPENSACAO_MDTOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_LDM_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_LOCAL_DISPENSACAO_MDTOS);
					
				//se possui dependencia em MPM_CONTROL_IMPRES_MDTOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_CIM_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_CONTROL_IMPRES_MDTOS);
					
				//se possui dependencia em AFA_LOCAL_DISPENSACAO_MDTOS_JN
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_LDMJ_UNF_FK3")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_LOCAL_DISPENSACAO_MDTOS_JN);
					
				//se possui dependencia em MBC_CIRURGIAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_CRG_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_CIRURGIAS);
					
				//se possui dependencia em MBC_CIRURGIAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MCI_UAP_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MCI_UNIDADE_AREA_PORTAIS);
					
				//se possui dependencia em AFA_LOCAL_DISPENSACAO_MDTOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_LDM_UNF_FK2")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_LOCAL_DISPENSACAO_MDTOS);
					
				//se possui dependencia em MBC_NECESSIDADE_CIRURGICAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_NCI_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_NECESSIDADE_CIRURGICAS);
					
				//se possui dependencia em AIP_PACIENTES_HIST
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AIP_PAC_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AIP_PACIENTES_HIST);
					
				//se possui dependencia em ANU_PRESCRICAO_DIETA_IG
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"ANU_PIG_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_ANU_PRESCRICAO_DIETA_IG);
					
				//se possui dependencia em ANU_PRESCRICAO_DIETA_IG
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AGH_CUN_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_CARACT_UNID_FUNCIONAIS);
					
				//se possui dependencia em V_FAT_TOTAIS_UNIDADE_AMB
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"FAT_VFTUA_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_V_FAT_TOTAIS_UNIDADE_AMB);
					
				//se possui dependencia em MPM_MOTIVO_INGRESSO_CTIS	
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_MIG_UNF_FK1_I")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_MOTIVO_INGRESSO_CTIS);
					
				//se possui dependencia em AFA_LOCAL_DISPENSACAO_MDTOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_LDM_UNF_FK3")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_LOCAL_DISPENSACAO_MDTOS);
					
				//se possui dependencia em AAC_UNID_FUNCIONAL_SALAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AAC_USL_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AAC_UNID_FUNCIONAL_SALAS);
					
				//se possui dependencia em MBC_SALA_CIRURGICAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_HTC_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_SALA_CIRURGICAS);
					
				//se possui dependencia em AEL_UNID_EXAME_SIGNIFICATIVOS
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_UES_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_UNID_EXAME_SIGNIFICATIVOS);
					
				//se possui dependencia em AIP_PACIENTES
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AIP_PAC_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AIP_PACIENTES);
					
				//se possui dependencia em FAT_ITENS_CONTA_HOSPITALAR
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"FAT_ICH_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_FAT_ITENS_CONTA_HOSPITALAR);
					
				//se possui dependencia em MBC_CONTROLE_ESCALA_CIRURGICAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_CEC_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_MBC_CONTROLE_ESCALA_CIRURGICAS);
					
				//se possui dependencia em MFAT_ACERTO_AIHS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"FAT_FAA_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_FAT_ACERTO_AIHS);
					
				//se possui dependencia em AGH_MICROCOMPUTADORES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AGH_MIC_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_AGH_MICROCOMPUTADORES);
					
				//se possui dependencia em AEL_UNF_EXECUTA_EXAMES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_UFE_UNF_FK2")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_AEL_UNF_EXECUTA_EXAMES);
					
				//se possui dependencia em FAT_ACERTO_AMBULATORIOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"FAT_FAB_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_FAT_ACERTO_AMBULATORIOS);
					
				//se possui dependencia em AEL_CAD_GUICHES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_CGU_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_AEL_CAD_GUICHES);
					
				//se possui dependencia em AIN_QUARTOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AIN_QRT_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_AIN_QUARTOS);
					
				//se possui dependencia em MCI_TMP_CARGA_BIS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MCI_TCB_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MCI_TMP_CARGA_BIS);
					
				//se possui dependencia em MPT_CONTROLE_DISPENSACOES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPT_COD_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPT_CONTROLE_DISPENSACOES);
					
				//se possui dependencia em MPM_HORARIO_INIC_APRAZAMENTOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_HIA_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MPM_HORARIO_INIC_APRAZAMENTOS);
					
				//se possui dependencia em AEL_PERMISSAO_UNID_SOLICS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_PUS_UNF_FK2")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_PERMISSAO_UNID_SOLICS);
					
				//se possui dependencia em AEL_EXAME_FORA_AGHS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_EFA_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AEL_EXAME_FORA_AGHS);
					
				//se possui dependencia em AFA_ESCALA_PRODUCAO_FARMACIAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_EPF_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_ESCALA_PRODUCAO_FARMACIAS);
					
				//se possui dependencia em AFA_LOCAL_DISPENSACAO_MDTOS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AFA_LDM_UNF_FK4")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AFA_LOCAL_DISPENSACAO_MDTOS);
					
				//se possui dependencia em MBC_SALA_CIRURGICAS
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MBC_SCI_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_MBC_SALA_CIRURGICAS);
					
				//se possui dependencia em SOLICITACAO_EXAMES
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"ABS_MCO_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_SOLICITACAO_EXAMES);
					
				//se possui dependencia em UNID_FUNC_GRP_ESTATISTICAS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AEL_PUS_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_UNID_FUNC_GRP_ESTATISTICAS);
					
				//se possui dependencia em AGH_CARACT_UNID_FUNCIONAIS
				}else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"AIN_MVI_UNF_FK1")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_AGH_CARACT_UNID_FUNCIONAIS);
				
				}else if(StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "AEL_HRC_UNF_FK2")) {
					throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_CONSTRAINT_HORARIO_ROTINA_COLETAS);
				} else{
					lancaErroConstraint(e);
				}
			}
			throw new ApplicationBusinessException(UnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_UNIDADE_FUNCIONAL);
		}
	}	
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void journalExclusao(final AghUnidadesFuncionais unid) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AghUnidadesFuncionaisJn unidadeJn  = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AghUnidadesFuncionaisJn.class, servidorLogado.getUsuario());
		
		if(unid.getSeq() != null) {
			unidadeJn.setSeq(unid.getSeq());
		}
		if(unid.getDescricao() != null) {
			unidadeJn.setDescricao(unid.getDescricao());
		}
		if(unid.getAndar()!= null) {
			unidadeJn.setAndar(unid.getAndar());
		}
		if (unid.getCapacInternacao() != null) {
			unidadeJn.setCapacInternacao(unid.getCapacInternacao());
		}
		if (unid.getDthrConfCenso() != null) {
			unidadeJn.setDthrConfCenso(unid.getDthrConfCenso());
		}
		
		if(unid.getIndSitUnidFunc()!= null) {
			unidadeJn.setIndSitUnidFunc(unid.getIndSitUnidFunc());
		}			
		if (unid.getIndAla() != null) {
			unidadeJn.setIndAla(unid.getIndAla().toString());
		}
		if (unid.getUnfSeq() != null) {
			unidadeJn.setUnfSeq(unid.getUnfSeq().getSeq());
		}
		
		if (unid.getRotinaFuncionamento() != null) {
			unidadeJn.setRotinaFuncionamento(unid.getRotinaFuncionamento());
		}
		if (unid.getHrioInicioAtendimento() != null) {
			unidadeJn.setHrioInicioAtendimento(unid
						.getHrioInicioAtendimento());
		}
		if (unid.getHrioFimAtendimento() != null) {
			unidadeJn.setHrioFimAtendimento(unid.getHrioFimAtendimento());
		}
		if (unid.getNroUnidTempoPenAdiantadas() != null) {
			unidadeJn.setNroUnidTempoPenAdiantadas(unid
						.getNroUnidTempoPenAdiantadas());
		}
		if (unid.getNroUnidTempoPmeAdiantadas() != null) {
			unidadeJn.setNroUnidTempoPmeAdiantadas(unid
						.getNroUnidTempoPmeAdiantadas());
		}
		if (unid.getIndUnidTempoPenAdiantada() != null) {
			unidadeJn.setIndUnidTempoPenAdiantada(unid
						.getIndUnidTempoPenAdiantada());
		}
		if (unid.getIndUnidTempoPmeAdiantada() != null) {
			unidadeJn.setIndUnidTempoPmeAdiantada(unid
						.getIndUnidTempoPmeAdiantada());
		}
		if (unid.getHrioValidadePen() != null) {
			unidadeJn.setHrioValidadePen(unid.getHrioValidadePen());
		}
		if (unid.getHrioValidadePme() != null) {
			unidadeJn.setHrioValidadePme(unid.getHrioValidadePme());
		}
		if (unid.getSerMatricula() != null) {
			unidadeJn.setSerMatricula(unid.getSerMatricula().intValue());
		}
		if (unid.getSerVinCodigo() != null) {
			unidadeJn.setSerVinCodigo(unid.getSerVinCodigo().shortValue());
		}
		if (unid.getIndVisualizaIg() != null) {
			unidadeJn.setIndVisualizaIg(unid.getIndVisualizaIg()
						);
		}
		if (unid.getIndVisualizaIap() != null) {
			unidadeJn.setIndVisualizaIap(unid.getIndVisualizaIap()
						);
		}
		if (unid.getIntervaloEscalaCirurgia() != null) {
			unidadeJn.setIntervaloEscalaCirurgia(unid
						.getIntervaloEscalaCirurgia());
		}
		if (unid.getIntervaloEscalaProced() != null) {
			unidadeJn.setIntervaloEscalaProced(unid
						.getIntervaloEscalaProced());
		}
		if (unid.getIndAnexaDocAutomatico() != null) {
			unidadeJn.setIndAnexaDocAutomatico(unid
						.getIndAnexaDocAutomatico());
		}
		
		if (unid.getLocalDocAnexo() != null) {
			unidadeJn.setLocalDocAnexo(unid.getLocalDocAnexo());
		}
		if (unid.getCentroCusto() != null) {
			unidadeJn.setCctCodigo(unid.getCentroCusto().getCodigo());
		}
		if (unid.getClinica() != null) {
			unidadeJn.setClcCodigo(unid.getClinica().getCodigo().byteValue());
		}
		if (unid.getTiposUnidadeFuncional() != null) {
			unidadeJn.setTufSeq(unid.getTiposUnidadeFuncional().getCodigo().byteValue());
		}
		
		this.getAghuFacade().persistirAghUnidadesFuncionaisJn(unidadeJn);
	}

	private void journalExclusaoCaracteristicas(final AghUnidadesFuncionais velha, final AghUnidadesFuncionais unid, 
			  final List<ConstanteAghCaractUnidFuncionais> caracteristicas, 
			  final List<ConstanteAghCaractUnidFuncionais> caracteristicasOld) throws ApplicationBusinessException {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (unid == null) {
			//			está removendo a unidade inteira
			for(final ConstanteAghCaractUnidFuncionais caract : caracteristicasOld){				
				final AghCaractUnidFuncionaisJn unidadeJn  = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AghCaractUnidFuncionaisJn.class, servidorLogado.getUsuario());

				if (velha.getSerMatricula() != null) {
					unidadeJn.setSerMatricula(velha.getSerMatricula());
				}
				
				if (velha.getSerVinCodigo() != null) {
					unidadeJn.setSerVinCodigo(velha.getSerVinCodigo());
				}
				
				if (velha.getSeq() != null) {
					unidadeJn.setUnfSeq(velha.getSeq().intValue());
				}
				
				unidadeJn.setCaracteristica(caract);
				iAghuFacade.persistirAghCaractUnidFuncionaisJn(unidadeJn);
			}
			
		} else if(velha.getCaracteristicas()!= null){
			for(final ConstanteAghCaractUnidFuncionais caract : caracteristicasOld){
				
				if(!caracteristicas.contains(caract)){
					
					final AghCaractUnidFuncionaisJn unidadeJn  = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AghCaractUnidFuncionaisJn.class, servidorLogado.getUsuario());
					
					if (velha.getSerMatricula() != null) {
						unidadeJn.setSerMatricula(velha.getSerMatricula());
					}
					
					if (velha.getSerVinCodigo() != null) {
						unidadeJn.setSerVinCodigo(velha.getSerVinCodigo());
					}
					
					if (velha.getSeq() != null) {
						unidadeJn.setUnfSeq(velha.getSeq().intValue());
					}
					
					unidadeJn.setCaracteristica(caract);
					iAghuFacade.persistirAghCaractUnidFuncionaisJn(unidadeJn);
				}
			}
		}
	}
	
	/**
	 * Retorna o número de niveis de hierarquia da unidade funcional
	 * @param unfSeq
	 * @return
	 */
	private int recuperarNiveisHierarquia(final Short unfSeq) {
		
		int niveis = 1;
		boolean pesquisa = true;
		Short seq = unfSeq;
		
		final IAghuFacade aghuFacade = this.getAghuFacade();
		
		while (pesquisa) {
			final AghUnidadesFuncionais unidade = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(seq);
			
			if (unidade != null && unidade.getUnfSeq() != null) {
				if (unidade.getIndVisualizaIg() == DominioSimNao.S) {
					niveis = niveis + 1;
					if(niveis >= 3){
						pesquisa = false;
					}
				}
				seq = unidade.getUnfSeq().getSeq();
			} else {
				pesquisa = false;
			}
			
		}
		return niveis;
	}

	public Short pesquisarUnidadeFuncionalTriagemRecepcao(
			List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador) {
		List<Short> listaRetorno = getAghuFacade()
				.pesquisarUnidadeFuncionalTriagemRecepcao(listaUnfSeqTriagemRecepcao, unfSeqMicroComputador);
		
		if (listaRetorno != null && !listaRetorno.isEmpty()) {
			return listaRetorno.get(0);
		}
		return null;
	}
	
	protected ImpressoraPadraoON getImpressoraPadraoON() {
		return impressoraPadraoON;
	}
	
	protected AinQuartosDAO getAinQuartosDAO() {
		return ainQuartosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public Long pesquisaCount(Short codigo, String descricao, String sigla,
			AghClinicas clinica, FccCentroCustos centroCusto, AghUnidadesFuncionais unidadeFuncionalPai, DominioSituacao situacao, String andar, 
			AghAla ala) {
		return this.getAghuFacade().pesquisaAghUnidadesFuncionaisCount(codigo, descricao, sigla, clinica, centroCusto, unidadeFuncionalPai,
				situacao, andar, ala);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(Integer firstResult,
			Integer maxResults, String orderProperty, Boolean asc,
			Short codigo, String descricao,String sigla, AghClinicas clinica,
			FccCentroCustos centroCusto,
			AghUnidadesFuncionais unidadeFuncionalPai, DominioSituacao situacao, String andar, AghAla ala) {
		return this.getAghuFacade().pesquisaAghUnidadesFuncionais(firstResult, maxResults, orderProperty, asc, codigo, descricao, sigla,
				clinica, centroCusto, unidadeFuncionalPai, situacao, andar, ala);
	}
}