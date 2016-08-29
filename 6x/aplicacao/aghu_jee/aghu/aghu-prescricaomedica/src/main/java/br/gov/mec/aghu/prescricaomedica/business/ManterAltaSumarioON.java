package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOutrosFarmacos;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoExameFisicoRns;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoIddGestCapurros;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoSnappes;
import br.gov.mec.aghu.model.MpmAltaCirgRealizada;
import br.gov.mec.aghu.model.MpmAltaComplFarmaco;
import br.gov.mec.aghu.model.MpmAltaConsultoria;
import br.gov.mec.aghu.model.MpmAltaDiagMtvoInternacao;
import br.gov.mec.aghu.model.MpmAltaDiagPrincipal;
import br.gov.mec.aghu.model.MpmAltaDiagSecundario;
import br.gov.mec.aghu.model.MpmAltaDiagSecundarioId;
import br.gov.mec.aghu.model.MpmAltaEstadoPaciente;
import br.gov.mec.aghu.model.MpmAltaEvolucao;
import br.gov.mec.aghu.model.MpmAltaOtrProcedimento;
import br.gov.mec.aghu.model.MpmAltaPrincFarmaco;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaCirgRealizadaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaComplFarmacoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagMtvoInternacaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagPrincipalDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagSecundarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEstadoPacienteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEvolucaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOtrProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPrincFarmacoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaAntecedenteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaDiretaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtOutraCausaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.util.SumarioAltaDiagnosticosCidVOComparator;
import br.gov.mec.aghu.prescricaomedica.vo.AfaMedicamentoPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaEvolucaoEstadoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioInfoComplVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaPrescricaoProcedimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosConsultoriasVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosCrgVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosVO;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.view.VAfaDescrMdtoId;

/**
 * 
 * @author lalegre
 */
@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength",
		"PMD.CouplingBetweenObjects" })
@Stateless
public class ManterAltaSumarioON extends BaseBusiness {


private static final String PARAMETRO_OBRIGATORIO_NAO_INFORMADO = "Parametro obrigatorio nao informado!!!";

private static final String QUEBRA_LINHA = "\n";

private static final String _DE_M = " De ";

private static final String _DA_M = " Da ";

private static final String _DA_ = " da ";

private static final String _DE_ = " de ";

private static final String _DO_ = " do ";

private static final String _DO_M = " Do ";

@EJB
private ManterAltaComplFarmacoRN manterAltaComplFarmacoRN;

@EJB
private ManterAltaEvolucaoON manterAltaEvolucaoON;

@EJB
private ManterAltaCirgRealizadaON manterAltaCirgRealizadaON;

@EJB
private ManterAltaDiagSecundarioON manterAltaDiagSecundarioON;

@EJB
private ManterObtCausaAntecedenteON manterObtCausaAntecedenteON;

@EJB
private ManterAltaReinternacaoON manterAltaReinternacaoON;

@EJB
private ManterAltaMotivoON manterAltaMotivoON;

@EJB
private ManterAltaEstadoPacienteRN manterAltaEstadoPacienteRN;

@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

@EJB
private ManterAltaDiagPrincipalRN manterAltaDiagPrincipalRN;

@EJB
private ManterObtCausaDiretaON manterObtCausaDiretaON;

@EJB
private ManterAltaPrincFarmacoRN manterAltaPrincFarmacoRN;

@EJB
private ManterAltaDiagMtvoInternacaoON manterAltaDiagMtvoInternacaoON;

@EJB
private ManterAltaOutraEquipeSumrON manterAltaOutraEquipeSumrON;

@EJB
private ManterAltaPedidoExameON manterAltaPedidoExameON;

@EJB
private ManterObitoNecropsiaON manterObitoNecropsiaON;

@EJB
private ManterAltaEvolucaoRN manterAltaEvolucaoRN;

@EJB
private ManterAltaConsultoriaON manterAltaConsultoriaON;

@EJB
private ManterAltaItemPedidoExameON manterAltaItemPedidoExameON;

@EJB
private ManterAltaDiagMtvoInternacaoRN manterAltaDiagMtvoInternacaoRN;

@EJB
private ManterAltaDiagSecundarioRN manterAltaDiagSecundarioRN;

@EJB
private ManterAltaOtrProcedimentoON manterAltaOtrProcedimentoON;

@EJB
private ManterAltaComplFarmacoON manterAltaComplFarmacoON;

@EJB
private ManterObtGravidezAnteriorON manterObtGravidezAnteriorON;

@EJB
private ManterAltaPrincFarmacoON manterAltaPrincFarmacoON;

@EJB
private ManterAltaEstadoPacienteON manterAltaEstadoPacienteON;

@EJB
private ManterAltaRecomendacaoON manterAltaRecomendacaoON;

@EJB
private ManterAltaPlanoON manterAltaPlanoON;

@EJB
private ManterObtOutraCausaON manterObtOutraCausaON;

@EJB
private ManterAltaDiagPrincipalON manterAltaDiagPrincipalON;

private static final Log LOG = LogFactory.getLog(ManterAltaSumarioON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;

@Inject
private MpmAltaComplFarmacoDAO mpmAltaComplFarmacoDAO;

@EJB
private IInternacaoFacade internacaoFacade;

@Inject
private MpmObtOutraCausaDAO mpmObtOutraCausaDAO;

@Inject
private MpmAltaDiagMtvoInternacaoDAO mpmAltaDiagMtvoInternacaoDAO;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@Inject
private MpmAltaEvolucaoDAO mpmAltaEvolucaoDAO;

@Inject
private MpmObtCausaAntecedenteDAO mpmObtCausaAntecedenteDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private MpmObtCausaDiretaDAO mpmObtCausaDiretaDAO;

@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

@Inject
private MpmAltaConsultoriaDAO mpmAltaConsultoriaDAO;

@Inject
private MpmAltaPrincFarmacoDAO mpmAltaPrincFarmacoDAO;

@Inject
private MpmAltaEstadoPacienteDAO mpmAltaEstadoPacienteDAO;

@EJB
private IPerinatologiaFacade perinatologiaFacade;

@Inject
private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;

@Inject
private MpmAltaDiagPrincipalDAO mpmAltaDiagPrincipalDAO;

@Inject
private MpmAltaOtrProcedimentoDAO mpmAltaOtrProcedimentoDAO;

@EJB
private IPacienteFacade pacienteFacade;

@Inject
private MpmAltaDiagSecundarioDAO mpmAltaDiagSecundarioDAO;

@Inject
private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;

@Inject
private MpmAltaCirgRealizadaDAO mpmAltaCirgRealizadaDAO;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;

@Inject
private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;

@Inject
private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 202855865726890707L;

	private final static String alta = "ALTA";

	private final static String anteciparSumario = "ANTECIPAR SUMARIO";

	private final static String obito = "OBITO";

	public enum ManterAltaSumarioONExceptionCode implements BusinessExceptionCode {
		ATENDIMENTO_NAO_ENCONTRADO, //
		PACIENTE_NAO_ENCONTRADO, //
		UNIDADE_FUNCIONAL_NAO_ENCONTRADO, //
		CENTRO_CUSTO_NAO_ENCONTRADO,
		ERRO_REMOVER_ALTA_DIAG_SECUNDARIO, //
		DIAG_SEC_JA_INFORMADO,
		MPM_02724, //
		MPM_02723;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	public AltaSumarioVO populaAltaSumarioVO(MpmAltaSumario altaSumario, String origem) throws ApplicationBusinessException {
		AltaSumarioVO altaSumarioVO = null;

		if (altaSumario != null) {

			altaSumarioVO = new AltaSumarioVO();
			altaSumarioVO.setId(altaSumario.getId());
			altaSumarioVO.setNome(altaSumario.getNome());
			altaSumarioVO.setSexo(altaSumario.getSexo());
			altaSumarioVO.setProntuario(altaSumario.getProntuario());

			if (!origem.equals("OBITO")) {

				altaSumarioVO.setDataAlta(altaSumario.getDthrAlta());

			}

			altaSumarioVO.setDataNascimento(altaSumario.getDtNascimento());
			altaSumarioVO.setDataInternacao(this.getManterAltaSumarioRN().obterDataInternacao2(altaSumario.getId().getApaAtdSeq()));
			altaSumarioVO.setLocal(this.getManterAltaSumarioRN().obterLocalPaciente(altaSumario.getId().getApaAtdSeq()));
			altaSumarioVO.setDescConvenio(altaSumario.getDescConvenio());

			if (altaSumario.getDescEquipeResponsavel() == null) {

				altaSumarioVO.setDescEquipeResponsavel("");

			} else {

				altaSumarioVO.setDescEquipeResponsavel(altaSumario.getDescEquipeResponsavel() + QUEBRA_LINHA + altaSumario.getDescServicoResponsavel());

			}

			altaSumarioVO.setDescPlanoConvenio(altaSumario.getDescPlanoConvenio());
			altaSumarioVO.setDescServico(this.getPrescricaoMedicaFacade().obterDescricaoServicoOutrasEquipes(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp()));
			altaSumarioVO.setDataAtendimento(altaSumario.getDthrAtendimento());
			this.calcularIdade(altaSumarioVO);
			this.calcularDiasPermanencia(altaSumarioVO);

		}

		return altaSumarioVO;
	}

	/**
	 * Calcula idade em anos, meses, dias e permanência
	 * @param altaSumarioVO
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void calcularIdade(AltaSumarioVO altaSumarioVO) throws ApplicationBusinessException {

		Integer anos = DateUtil.obterQtdAnosEntreDuasDatas(altaSumarioVO.getDataNascimento(), altaSumarioVO.getDataAlta());
		Integer meses = DateUtil.obterQtdMesesEntreDuasDatas(altaSumarioVO.getDataNascimento(), altaSumarioVO.getDataAlta())%12;
		Integer dias = 0;
		String idadeFormatado = null;

		Calendar dataAlta = Calendar.getInstance();
		Calendar dataNascimento = Calendar.getInstance();

		if (altaSumarioVO.getDataAlta() != null) {
			dataAlta.setTime(altaSumarioVO.getDataAlta());
		} else {
			dataAlta.setTime(new Date());
		}
		dataNascimento.setTime(altaSumarioVO.getDataNascimento());

		if (dataAlta.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
			int lastDayMonth = dataNascimento.getActualMaximum(Calendar.DAY_OF_MONTH);
			dias = lastDayMonth - dataNascimento.get(Calendar.DAY_OF_MONTH)
			+ dataAlta.get(Calendar.DAY_OF_MONTH);
		} else {
			dias = dataAlta.get(Calendar.DAY_OF_MONTH) - dataNascimento.get(Calendar.DAY_OF_MONTH);
		}

		if (anos == 0) {
			idadeFormatado = getLabelMes(meses) + getLabelDias(dias);
		} else if (anos > 1) {
			idadeFormatado = anos.toString() + " anos " + getLabelMes(meses);
		} else if (anos == 1) {
			idadeFormatado = anos.toString() + " ano " + getLabelMes(meses);
		}
		altaSumarioVO.setIdadeAnos(anos.shortValue());
		altaSumarioVO.setIdadeMeses(meses);
		altaSumarioVO.setIdadeDias(dias);
		altaSumarioVO.setIdadeFormatado(idadeFormatado);
	}

	private String getLabelMes(Integer meses) {
		String labelMes = "";
		if (meses != null) {
			if (meses > 1) {
				labelMes = meses.toString() + " meses ";
			} else if (meses == 1) {
				labelMes = meses.toString() + " mês ";
			}
		}
		return labelMes;
	}

	private String getLabelDias(Integer dias) {
		String labelDias = "";
		if (dias != null) {
			if (dias > 1) {
				labelDias = dias.toString() + " dias ";
			} else if (dias == 1) {
				labelDias = dias.toString() + " dia ";
			}
		}
		return labelDias;
	}


	/**
	 * Calcula dias permancência
	 * @param altaSumarioVO
	 * @return
	 */
	public AltaSumarioVO calcularDiasPermanencia(AltaSumarioVO altaSumarioVO) {

		Date dataAlta = new Date();

		if (altaSumarioVO.getDataAlta() != null) {

			dataAlta = altaSumarioVO.getDataAlta();

		}

		Integer dias = DateUtil.obterQtdDiasEntreDuasDatasTruncadas(altaSumarioVO.getDataAtendimento(), dataAlta);

		if (dias <= 1) {

			altaSumarioVO.setDiasPermanenciaFormatado(dias.toString() + " dia");

		} else {

			altaSumarioVO.setDiasPermanenciaFormatado(dias.toString() + " dias");

		}

		altaSumarioVO.setDiasPermanencia(dias.shortValue());
		return altaSumarioVO;
	}

	/**
	 * Atualiza os campos informados na alta sumário especificada pelo parâmetro id.
	 *
	 * @param id identificador da alta sumário.
	 * @param diasPermanencia
	 * @param idadeDias
	 * @param idadeMeses
	 * @param idadeAnos
	 * @param dataAlta
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarAltaSumario(MpmAltaSumarioId id,
			Short diasPermanencia, Integer idadeDias, Integer idadeMeses,
			Short idadeAnos, Date dataAlta, String nomeMicrocomputador) throws ApplicationBusinessException {
		MpmAltaSumarioDAO altaSumarioDAO = this.getMpmAltaSumarioDAO();

		MpmAltaSumario altaSumario = altaSumarioDAO.obterPorChavePrimaria(id);
		altaSumario.setDiasPermanencia(diasPermanencia.intValue());
		altaSumario.setIdadeDias(idadeDias);
		altaSumario.setIdadeMeses(idadeMeses);
		altaSumario.setIdadeAnos(idadeAnos);
		altaSumario.setDthrAltaAux(dataAlta);

		ManterAltaSumarioRN altaSumarioRN = this.getManterAltaSumarioRN();
		altaSumarioRN.atualizarAltaSumario(altaSumario, nomeMicrocomputador);
	}

	public void atualizarAltaSumario(MpmAltaSumario altaSumario, String nomeMicrocomputador) throws BaseException {
		ManterAltaSumarioRN altaSumarioRN = this.getManterAltaSumarioRN();
		altaSumarioRN.atualizarAltaSumario(altaSumario, nomeMicrocomputador);
	}

	/**
	 * Recupera seqp de sumario alta
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @return
	 */
	public MpmAltaSumario recuperarSumarioAlta(Integer altanAtdSeq, Integer altanApaSeq) throws ApplicationBusinessException {
		return this.getMpmAltaSumarioDAO().recuperarSumarioAlta(altanAtdSeq, altanApaSeq);
	}

	/**
	 * Gera a nova versão de sumário de alta
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanListaOrigem
	 * @return
	 */
	public MpmAltaSumario gerarAltaSumario(Integer altanAtdSeq, Integer altanApaSeq, String altanListaOrigem) throws BaseException {
		MpmAltaSumario altaSumario = new MpmAltaSumario();
		altanApaSeq = this.getPacienteFacade().verificarAtendimentoPaciente(altanAtdSeq, altanApaSeq, altaSumario);
		this.popularAltaSumario(altaSumario, altanListaOrigem);
		this.popularAltaSumarioPorAtendimento(altaSumario, altanAtdSeq, altanListaOrigem);
		this.popularAltaSumarioPorPaciente(altaSumario);
		this.popularAltaSumarioPorUnidade(altaSumario);
		this.popularDescricaoServicoResponsavel(altaSumario);
		this.popularConvenio(altaSumario);
		this.calcularDiasPermanencia(altaSumario, altanListaOrigem);
		this.getManterAltaSumarioRN().inserirAltaSumario(altaSumario);
		return altaSumario;
	}

	/**
	 * Atualiza sumário de alta
	 * @param altaSumario
	 * @param altanListaOrigem
	 * @throws BaseException
	 */
	public MpmAltaSumario versionarAltaSumario(MpmAltaSumario altaSumario, String altanListaOrigem, String nomeMicrocomputador) throws BaseException {
		//this.getMpmAltaSumarioDAO().limparEntityManager();

		Integer antigoAtdSeq = altaSumario.getId().getApaAtdSeq();
		Integer antigoApaSeq = altaSumario.getId().getApaSeq();
		Short antigoAsuSeqp = altaSumario.getId().getSeqp();
		MpmAltaSumario novoAltaSumario = new MpmAltaSumario();

		//Popula novo sumário de alta
		novoAltaSumario.setAtendimentoPaciente(this.getAghuFacade().obterAtendimentoPaciente(antigoAtdSeq, antigoApaSeq));

		this.popularAltaSumario(novoAltaSumario, altanListaOrigem);
		this.popularAltaSumarioPorAtendimento(novoAltaSumario, antigoAtdSeq, altanListaOrigem);
		this.popularAltaSumarioPorPaciente(novoAltaSumario);
		this.popularAltaSumarioPorUnidade(novoAltaSumario);
		this.popularDescricaoServicoResponsavel(novoAltaSumario);
		this.popularConvenio(novoAltaSumario);
		this.calcularDiasPermanencia(novoAltaSumario, altanListaOrigem);

		this.getManterAltaSumarioRN().inserirAltaSumario(novoAltaSumario);
		this.versionarFilhosAltaSumario(novoAltaSumario, antigoAsuSeqp, altanListaOrigem);

		//Inativa o sumário alta antigo
		this.inativarAltaSumario(antigoAtdSeq, antigoApaSeq, antigoAsuSeqp, nomeMicrocomputador);

		return novoAltaSumario;
	}

	/**
	 * Atualiza tabela filhos de alta sumário
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @param altanListaOrigem
	 * @throws BaseException
	 */
	private void versionarFilhosAltaSumario(MpmAltaSumario altaSumario, Short antigoAsuSeqp, String altanListaOrigem) throws BaseException {

		this.getManterAltaOutraEquipeSumrON().versionarAltaOutraEquipeSumr(altaSumario, antigoAsuSeqp);

		this.getManterAltaDiagMtvoInternacaoON().versionarAltaDiagMtvoInternacao(altaSumario, antigoAsuSeqp);

		this.getManterAltaDiagPrincipalON().versionarAltaDiagPrincipal(altaSumario, antigoAsuSeqp);

		this.getManterAltaDiagSecundarioON().versionarMpmAltaDiagSecundario(altaSumario, antigoAsuSeqp);

		this.getManterAltaCirgRealizadaON().versionarAltaCirgRealizada(altaSumario, antigoAsuSeqp);

		this.getManterAltaOtrProcedimentoON().versionarAltaOtrProcedimento(altaSumario, antigoAsuSeqp);

		this.getManterAltaConsultoriaON().versionarAltaConsultoria(altaSumario, antigoAsuSeqp);

		this.getManterAltaPrincFarmacoON().versionarAltaPrincFarmaco(altaSumario, antigoAsuSeqp);

		this.getManterAltaComplFarmacoON().versionarAltaComplFarmaco(altaSumario, antigoAsuSeqp);

		this.getManterAltaEvolucaoON().versionarAltaEvolucao(altaSumario, antigoAsuSeqp);

		if (altanListaOrigem.equals(alta) || altanListaOrigem.equals(anteciparSumario)) {

			this.getManterAltaPlanoON().versionarAltaPlano(altaSumario, antigoAsuSeqp);

			this.getManterAltaPedidoExameON().versionarAltaPedidoExame(altaSumario, antigoAsuSeqp);

			this.getManterAltaItemPedidoExameON().versionarAltaItemPedidoExame(altaSumario, antigoAsuSeqp);

			this.getManterAltaReinternacaoON().versionarAltaReinternacao(altaSumario, antigoAsuSeqp);

			this.getManterAltaRecomendacaoON().versionarAltaRecomendacao(altaSumario, antigoAsuSeqp);

		}

		if (altanListaOrigem.equals(alta)) {

			this.getManterAltaMotivoON().versionarAltaMotivo(altaSumario, antigoAsuSeqp);

			this.getManterAltaEstadoPacienteON().versionarAltaEstadoPaciente(altaSumario, antigoAsuSeqp);

			this.getAmbulatorioFacade().versionarAltaReceituario(altaSumario, antigoAsuSeqp);

		}

		if (altanListaOrigem.equals(obito)) {

			try {
				this.getManterObtCausaDiretaON().versionarObtCausaDireta(altaSumario, antigoAsuSeqp);
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getClass().getName(),e);
			}

			this.getManterObtCausaAntecedenteON().versionarObtCausaAntecedente(altaSumario, antigoAsuSeqp);

			this.getManterObtOutraCausaON().versionarObtOutraCausa(altaSumario, antigoAsuSeqp);

			this.getManterObitoNecropsiaON().versionarObitoNecropsia(altaSumario, antigoAsuSeqp);

			this.getManterObtGravidezAnteriorON().versionarObtGravidezAnterior(altaSumario, antigoAsuSeqp);

		}

	}


	/**
	 * Popula os dados em MPM_ALTA_SUMARIOS
	 * @param altaSumario
	 * @param altanApaSeq
	 * @param altanListaOrigem
	 * @throws ApplicationBusinessException
	 */
	private void popularAltaSumario(MpmAltaSumario altaSumario, String altanListaOrigem) throws ApplicationBusinessException {

		if (altanListaOrigem.equals(anteciparSumario)) {

			altaSumario.setTipo(DominioIndTipoAltaSumarios.ANT);

		} else if (altanListaOrigem.equals(obito)){

			altaSumario.setTipo(DominioIndTipoAltaSumarios.OBT);
			altaSumario.setEstorno(false);

		} else {

			altaSumario.setTipo(DominioIndTipoAltaSumarios.ALT);

		}

		altaSumario.setImpresso(false);
		altaSumario.setSituacao(DominioSituacao.A);
		altaSumario.setConcluido(DominioIndConcluido.N);
		altaSumario.setDthrElaboracaoAlta(new Date());
		altaSumario.setDthrInicio(new Date());

	}

	/**
	 * Popula os dados em MPM_ALTA_SUMARIOS através de AGH_ATENDIMENTOS
	 * @param altaSumario
	 * @param altanAtdSeq
	 * @param altanListaOrigem
	 * @throws ApplicationBusinessException
	 */
	private void popularAltaSumarioPorAtendimento(MpmAltaSumario altaSumario, Integer altanAtdSeq, String altanListaOrigem) throws ApplicationBusinessException {
		AghAtendimentos atendimento = this.getAghuFacade().obterAtendimentoPeloSeq(altanAtdSeq);

		if (atendimento != null) {
			//Associacao bi-direcional.
			//Garante que está pegando o atendimento atualizado. 
			super.refresh(atendimento);

			//Associacao bi-direcional.
			altaSumario.setAtendimento(atendimento);
			atendimento.getAltasSumario().add(altaSumario);
			//---
			altaSumario.setLeito(atendimento.getLeito());
			altaSumario.setQuarto(atendimento.getQuarto());
			altaSumario.setEspecialidade(atendimento.getEspecialidade());
			altaSumario.setServidorEquipe(atendimento.getServidor());

			if (atendimento.getServidor() != null) {
				altaSumario.setDescEquipeResponsavel(this.getManterAltaSumarioRN().obtemNomeServidorEditado(atendimento.getServidor().getId().getVinCodigo(), atendimento.getServidor().getId().getMatricula()));
			}

			altaSumario.setPaciente(atendimento.getPaciente());
			altaSumario.setUnidadeFuncional(atendimento.getUnidadeFuncional());
			altaSumario.setProntuario(atendimento.getProntuario());

			if (altanListaOrigem.equals(alta)) {
				if (atendimento.getIndPacAtendimento().equals(DominioPacAtendimento.S)) {
					altaSumario.setDthrAlta(new Date());
				} else {
					altaSumario.setDthrAlta(atendimento.getDthrFim());
				}
			} else {
				altaSumario.setDthrAlta(null);
			}

			if (atendimento.getAtendimentoUrgencia() != null) {
				altaSumario.setDthrAtendimento(this.getInternacaoFacade().obterDataAtendimento(atendimento.getAtendimentoUrgencia().getSeq()));
			} else if (atendimento.getInternacao() != null) {
				altaSumario.setDthrAtendimento(this.getInternacaoFacade().obterDataInternacao(atendimento.getInternacao().getSeq()));
			} else {
				altaSumario.setDthrAtendimento(atendimento.getDthrInicio());
			}

		} else {
			throw new ApplicationBusinessException (
					ManterAltaSumarioONExceptionCode.ATENDIMENTO_NAO_ENCONTRADO);
		}

	}

	/**
	 * Popula os dados em MPM_ALTA_SUMARIOS através de AIP_PACIENTES
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	private void popularAltaSumarioPorPaciente(MpmAltaSumario altaSumario) throws ApplicationBusinessException {

		if (altaSumario.getPaciente() != null) {

			if(altaSumario.getPaciente().getNomeSocial() != null && !altaSumario.getPaciente().getNomeSocial().isEmpty()){
				altaSumario.setNome(WordUtils.capitalizeFully(altaSumario.getPaciente().getNomeSocial())
						.replaceAll(" Da ", " da ").replaceAll(" De ", " de ")
						.replaceAll(" Do ", " do "));
			}else{
				altaSumario.setNome(WordUtils.capitalizeFully(altaSumario.getPaciente().getNome())
						.replaceAll(" Da ", " da ").replaceAll(" De ", " de ")
						.replaceAll(" Do ", " do "));
			}
			altaSumario.setDtNascimento(altaSumario.getPaciente().getDtNascimento());
			altaSumario.setSexo(altaSumario.getPaciente().getSexo());
			this.calcularIdade(altaSumario);

		} else {

			throw new ApplicationBusinessException (
					ManterAltaSumarioONExceptionCode.PACIENTE_NAO_ENCONTRADO);

		}
	}

	/**
	 * Popula os dados em MPM_ALTA_SUMARIOS através de AGH_UNIDADE_FUNCIONAL
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	private void popularAltaSumarioPorUnidade(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		String local = null;

		if (altaSumario.getUnidadeFuncional() != null) {

			local = altaSumario.getUnidadeFuncional().getLPADAndarAlaDescricao();
            String descricaoUnidade = WordUtils.capitalizeFully(local).replaceAll(_DA_M, _DA_).replaceAll(_DE_M, _DE_).replaceAll(_DO_M, _DO_);
            if(descricaoUnidade.length() > 60) {
                descricaoUnidade = descricaoUnidade.substring(0, 60);
            }
			altaSumario.setDescUnidade(descricaoUnidade);

		} else {

			throw new ApplicationBusinessException (
					ManterAltaSumarioONExceptionCode.UNIDADE_FUNCIONAL_NAO_ENCONTRADO);

		}
	}

	/**
	 * Popula as informações de convênio e plano
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	private void popularConvenio(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		AghAtendimentos atendimento = altaSumario.getAtendimento();

		if (atendimento.getInternacao() != null) {

			altaSumario.setConvenioSaudePlano(atendimento.getInternacao().getConvenioSaudePlano());

		} else if (atendimento.getAtendimentoUrgencia() != null) {

			altaSumario.setConvenioSaudePlano(atendimento.getAtendimentoUrgencia().getConvenioSaudePlano());

		} else {

			this.popularConvenioRecemNascido(altaSumario);

		}

		if (altaSumario != null &&
				altaSumario.getConvenioSaudePlano() != null &&
				altaSumario.getConvenioSaudePlano().getConvenioSaude() != null &&
				altaSumario.getConvenioSaudePlano().getConvenioSaude().getDescricao() != null) {
			String descConv = WordUtils.capitalizeFully(altaSumario.getConvenioSaudePlano().getConvenioSaude().getDescricao()).replaceAll(_DA_M, _DA_);
			descConv = descConv.replaceAll(_DE_M, _DE_).replaceAll(_DO_M, _DO_);
			altaSumario.setDescConvenio(descConv);
		}

		if (altaSumario != null &&
				altaSumario.getConvenioSaudePlano() != null &&
				altaSumario.getConvenioSaudePlano().getDescricao() != null) {
			String descPlanoConv = WordUtils.capitalizeFully(altaSumario.getConvenioSaudePlano().getDescricao());
			descPlanoConv = descPlanoConv.replaceAll(_DA_M, _DA_).replaceAll(_DE_M, _DE_).replaceAll(_DO_M, _DO_);
			altaSumario.setDescPlanoConvenio(descPlanoConv);
		}

	}

	/**
	 * Busca o convênio quando o paciente é RN (Recém Nascido).
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	private void popularConvenioRecemNascido(MpmAltaSumario altaSumario) throws ApplicationBusinessException {

		McoRecemNascidos rn = this.getPerinatologiaFacade().obterMcoRecemNascidos(altaSumario.getPaciente().getCodigo());

		if (rn != null) {

			AghAtendimentos atendimento = this.getAghuFacade().obterAtendimentoRecemNascido(rn.getId().getGsoPacCodigo(), rn.getId().getSeqp());

			if (atendimento != null) {

				if ((atendimento.getInternacao() != null) && (atendimento.getInternacao().getConvenioSaudePlano() != null)) {

					altaSumario.setConvenioSaudePlano(atendimento.getInternacao().getConvenioSaudePlano());

				} else if ((atendimento.getAtendimentoUrgencia() != null) && (atendimento.getAtendimentoUrgencia().getConvenioSaudePlano() != null)) {

					altaSumario.setConvenioSaudePlano(atendimento.getAtendimentoUrgencia().getConvenioSaudePlano());

				}

			} else {

				atendimento = this.getAghuFacade().obterAtendimentoRecemNascido(rn.getId().getGsoPacCodigo());

				if (atendimento != null) {

					if ((atendimento.getInternacao() != null) && (atendimento.getInternacao().getConvenioSaudePlano() != null)) {

						altaSumario.setConvenioSaudePlano(atendimento.getInternacao().getConvenioSaudePlano());

					} else if ((atendimento.getAtendimentoUrgencia() != null) && (atendimento.getAtendimentoUrgencia().getConvenioSaudePlano() != null)) {

						altaSumario.setConvenioSaudePlano(atendimento.getAtendimentoUrgencia().getConvenioSaudePlano());

					}
				}
			}

		} else if (altaSumario.getAtendimento().getOrigem().equals(DominioOrigemAtendimento.N)) {

			AghAtendimentos atendimento = altaSumario.getAtendimento().getAtendimentoMae();

			if (atendimento != null && atendimento.getInternacao() != null) {

				altaSumario.setConvenioSaudePlano(atendimento.getInternacao().getConvenioSaudePlano());

			}
		}

	}

	/**
	 * Calcula idade do paciente
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	private void calcularIdade(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		Integer anos = DateUtil.obterQtdAnosEntreDuasDatas(altaSumario.getDtNascimento(), altaSumario.getDthrAlta());
		Integer meses = DateUtil.obterQtdMesesEntreDuasDatas(altaSumario.getDtNascimento(), altaSumario.getDthrAlta())%12;
		Integer dias = 0;

		Calendar dataNascimento = Calendar.getInstance();
		dataNascimento.setTime(altaSumario.getDtNascimento());
		Calendar dataAlta = Calendar.getInstance();

		if (altaSumario.getDthrAlta() != null) {

			dataAlta.setTime(altaSumario.getDthrAlta());

		} else {

			dataAlta.setTime(new Date());

		}

		if (dataAlta.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {

			int lastDayMonth = dataNascimento.getActualMaximum(Calendar.DAY_OF_MONTH);
			dias = lastDayMonth - dataNascimento.get(Calendar.DAY_OF_MONTH)
			+ dataAlta.get(Calendar.DAY_OF_MONTH);
		} else {

			dias = dataAlta.get(Calendar.DAY_OF_MONTH) - dataNascimento.get(Calendar.DAY_OF_MONTH);

		}

		altaSumario.setIdadeAnos(anos.shortValue());
		altaSumario.setIdadeMeses(meses);

		if ((anos == 0) && (meses == 0) && (dias == 0)) {

			altaSumario.setIdadeDias(1);

		} else {

			altaSumario.setIdadeDias(dias);

		}
	}

	/**
	 * Popula coluna desc_servico_responsavel
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	private void popularDescricaoServicoResponsavel(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		FccCentroCustos centroCustos = null;
		String descricaoEspecialidade = null;
		String descricaoCentroCustos = null;

		if (altaSumario.getEspecialidade() != null) {

			if (altaSumario.getEspecialidade().getCentroCusto() != null) {

				centroCustos = altaSumario.getEspecialidade().getCentroCusto();
				descricaoCentroCustos = centroCustos.getDescricao();
				descricaoEspecialidade = altaSumario.getEspecialidade().getNomeEspecialidade();

				if (altaSumario.getEspecialidade().getIndImpSoServico().equals(DominioSimNao.N)) {

					altaSumario.setDescServicoResponsavel(WordUtils.capitalizeFully(descricaoCentroCustos).replaceAll(_DA_M, _DA_).replaceAll(_DE_M, _DE_).replaceAll(_DO_M, _DO_));

				} else {

					altaSumario.setDescServicoResponsavel(WordUtils.capitalizeFully(descricaoCentroCustos + " - " + descricaoEspecialidade).replaceAll(_DE_M, _DE_).replaceAll(_DO_M, _DO_));

				}
			} else {

				throw new ApplicationBusinessException (
						ManterAltaSumarioONExceptionCode.CENTRO_CUSTO_NAO_ENCONTRADO);

			}
		}
	}

	/**
	 * Calcula dias permanência
	 * @param altaSumario
	 * @param altanListaOrigem
	 */
	private void calcularDiasPermanencia(MpmAltaSumario altaSumario, String altanListaOrigem) {
		Date dataAlta = null;
		Integer dias = 0;

		if (!altanListaOrigem.equals(alta)) {

			dataAlta = new Date();

		} else {

			dataAlta = altaSumario.getDthrAlta();

		}

		dias = DateUtil.obterQtdDiasEntreDuasDatasTruncadas(altaSumario.getDthrAtendimento(), dataAlta);
		altaSumario.setDiasPermanencia(dias);
	}

	/**
	 * VERIFICA SE O SUMÁRIO É DA EMERGENCIA
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param seqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean verificarEmergencia(Integer altanAtdSeq, Integer altanApaSeq, Short seqp) throws ApplicationBusinessException {
		return this.getMpmAltaSumarioDAO().verificarEmergencia(altanAtdSeq, altanApaSeq, seqp);
	}

	/**
	 * Pre-insert
	 * @param altaSumario
	 * @param sumarioAltaDiagnosticosCidVO
	 * @throws ApplicationBusinessException
	 */
	public void preinserirAltaDiagMtvoInternacao(SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO) throws ApplicationBusinessException{

		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(sumarioAltaDiagnosticosCidVO.getId().getApaAtdSeq(), sumarioAltaDiagnosticosCidVO.getId().getApaSeq(), sumarioAltaDiagnosticosCidVO.getId().getSeqp());

		if (altaSumario != null) {

			MpmAltaDiagMtvoInternacao mpmAltaDiagMtvoInternacaoNovo = new MpmAltaDiagMtvoInternacao();
			mpmAltaDiagMtvoInternacaoNovo.setAltaSumario(altaSumario);

			mpmAltaDiagMtvoInternacaoNovo.setIndSituacao(DominioSituacao.A);

			if (sumarioAltaDiagnosticosCidVO.getOrigemListaCombo()) {

				mpmAltaDiagMtvoInternacaoNovo.setIndCarga(true);

			} else {

				mpmAltaDiagMtvoInternacaoNovo.setIndCarga(false);

			}

			mpmAltaDiagMtvoInternacaoNovo.setDescCid(montaDescricaoCIDComPrimeiraMaiuscula(sumarioAltaDiagnosticosCidVO.getCid().getDescricao(),sumarioAltaDiagnosticosCidVO.getCid().getCodigo()));
			mpmAltaDiagMtvoInternacaoNovo.setCid(sumarioAltaDiagnosticosCidVO.getCid());
			mpmAltaDiagMtvoInternacaoNovo.setComplCid(StringUtil.trim(sumarioAltaDiagnosticosCidVO.getComplementoEditado()));

			if (sumarioAltaDiagnosticosCidVO.getCiaSeq()!= null) {

				MpmCidAtendimento cidAtendimento = this.getMpmCidAtendimentoDAO().obterCidAtendimentoPeloId(sumarioAltaDiagnosticosCidVO.getCiaSeq()) ;

				if(cidAtendimento != null){

					mpmAltaDiagMtvoInternacaoNovo.setCidAtendimento(cidAtendimento);

				}

			}

			if (sumarioAltaDiagnosticosCidVO.getDiaSeq() != null) {

				MamDiagnostico mamDiagnostico = this.getAmbulatorioFacade().obterDiagnosticoPorChavePrimaria(sumarioAltaDiagnosticosCidVO.getDiaSeq());

				if (mamDiagnostico != null) {

					mpmAltaDiagMtvoInternacaoNovo.setDiagnostico(mamDiagnostico);

				}

			}

			this.getManterAltaDiagMtvoInternacaoRN().inserirAltaDiagMtvoInternacao(mpmAltaDiagMtvoInternacaoNovo);

		}

	}


	/**
	 * Insere ou atualizar a Alta Diagnostico Princal.
	 * So pode existir uma para cada Alta Sumario;.
	 * Retorna true caso seja uma inserção, false caso seja uma atualização.
	 *
	 * @param altaDiagnosticosCidVO <code>SumarioAltaDiagnosticosCidVO</code>
	 * @throws ApplicationBusinessException
	 */
	public boolean inserirAltaDiagPrincipal(SumarioAltaDiagnosticosCidVO altaDiagnosticosCidVO) throws BaseException {
		boolean insert = true;
		
		if ((altaDiagnosticosCidVO == null) || (altaDiagnosticosCidVO.getCid() == null) || (altaDiagnosticosCidVO.getId() == null)) {
			throw new IllegalArgumentException("Parametros obrigatorios nao informados!!!");
		}
		
		MpmAltaDiagPrincipal altaDiagPrincipal = this.getMpmAltaDiagPrincipalDAO().obterAltaDiagPrincipal(altaDiagnosticosCidVO.getId().getApaAtdSeq(), altaDiagnosticosCidVO.getId().getApaSeq(), altaDiagnosticosCidVO.getId().getSeqp());

		if (altaDiagPrincipal != null) {
			this.getManterAltaDiagPrincipalRN().removerAltaDiagPrincipal(altaDiagPrincipal);
			insert = false;
		}

		// Insert
		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(altaDiagnosticosCidVO.getId().getApaAtdSeq(), altaDiagnosticosCidVO.getId().getApaSeq(), altaDiagnosticosCidVO.getId().getSeqp());
		MpmAltaDiagPrincipal altaDiagPrincipalNovo = new MpmAltaDiagPrincipal();
		altaDiagPrincipalNovo.setAltaSumario(altaSumario);

		this.populaAltaDiagPrincipal(altaDiagnosticosCidVO, altaDiagPrincipalNovo);
		this.getManterAltaDiagPrincipalRN().inserirAltaDiagPrincipal(altaDiagPrincipalNovo);

		return insert;
	}

	private void populaAltaDiagPrincipal(SumarioAltaDiagnosticosCidVO altaDiagnosticosCidVO, MpmAltaDiagPrincipal altaDiagPrincipal) {
		altaDiagPrincipal.setCid(altaDiagnosticosCidVO.getCid());
		altaDiagPrincipal.setDescCid(montaDescricaoCIDComPrimeiraMaiuscula(altaDiagnosticosCidVO.getCid().getDescricao(),altaDiagnosticosCidVO.getCid().getCodigo()));
		altaDiagPrincipal.setIndSituacao(DominioSituacao.A);
		altaDiagPrincipal.setIndCarga(altaDiagnosticosCidVO.getOrigemListaCombo());
		altaDiagPrincipal.setComplCid(StringUtil.trim(altaDiagnosticosCidVO.getComplementoEditado()));
		if (altaDiagnosticosCidVO.getCiaSeq() != null) {
			MpmCidAtendimento umMpmCidAtendimento =
				this.getMpmCidAtendimentoDAO().obterPorChavePrimaria(altaDiagnosticosCidVO.getCiaSeq());
			altaDiagPrincipal.setCidAtendimento(umMpmCidAtendimento);
		}
		if (altaDiagnosticosCidVO.getDiaSeq() != null) {
			MamDiagnostico umMamDiagnostico =
				this.getAmbulatorioFacade().obterDiagnosticoPorChavePrimaria(altaDiagnosticosCidVO.getDiaSeq());
			altaDiagPrincipal.setDiagnostico(umMamDiagnostico);
		}
		//altaDiagPrincipal.setId();
		//altaDiagPrincipal.setVersion();
	}

	private ManterAltaDiagPrincipalRN getManterAltaDiagPrincipalRN() {
		return manterAltaDiagPrincipalRN;
	}

	/**
	 * Remove a Alta Diagnostico Principal associado ao Sumario de Alta
	 *
	 * @param altaDiagnosticosCidVO
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaDiagPrincipal(SumarioAltaDiagnosticosCidVO altaDiagnosticosCidVO) throws ApplicationBusinessException {

		if ((altaDiagnosticosCidVO == null) || (altaDiagnosticosCidVO.getId() == null)) {
			throw new IllegalArgumentException("Parametros obrigatorios nao informados!!!");
		}

		MpmAltaDiagPrincipal altaDiagPrincipal = this.getMpmAltaDiagPrincipalDAO().obterAltaDiagPrincipal(altaDiagnosticosCidVO.getId().getApaAtdSeq(), altaDiagnosticosCidVO.getId().getApaSeq(), altaDiagnosticosCidVO.getId().getSeqp());

		this.getManterAltaDiagPrincipalRN().removerAltaDiagPrincipal(altaDiagPrincipal);

	}

	public void preatualizarMpmAltaDiagMtvoInternacao(SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO) throws ApplicationBusinessException{

		if (sumarioAltaDiagnosticosCidVO.getId() != null) {

			MpmAltaDiagMtvoInternacao mpmAltaDiagMtvoInternacaoModificado = this.getMpmAltaDiagMtvoInternacaoDAO().obterAltaDiagMtvoInternacao(sumarioAltaDiagnosticosCidVO.getId().getApaAtdSeq(), sumarioAltaDiagnosticosCidVO.getId().getApaSeq(), sumarioAltaDiagnosticosCidVO.getId().getSeqp(), sumarioAltaDiagnosticosCidVO.getSeqp().byteValue());

			mpmAltaDiagMtvoInternacaoModificado.setComplCid(sumarioAltaDiagnosticosCidVO.getComplementoEditado() == null ? "" : StringUtil.trim(sumarioAltaDiagnosticosCidVO.getComplementoEditado()));
			mpmAltaDiagMtvoInternacaoModificado.setCid(sumarioAltaDiagnosticosCidVO.getCid());
			mpmAltaDiagMtvoInternacaoModificado.setDescCid(montaDescricaoCIDComPrimeiraMaiuscula(sumarioAltaDiagnosticosCidVO.getCid().getDescricao(), sumarioAltaDiagnosticosCidVO.getCid().getCodigo()));

//			if (sumarioAltaDiagnosticosCidVO.getOrigemListaCombo()) {
//
//				mpmAltaDiagMtvoInternacaoModificado.setIndCarga(true);
//			} else {
//
//				mpmAltaDiagMtvoInternacaoModificado.setIndCarga(false);
//
//			}

			if (sumarioAltaDiagnosticosCidVO.getCiaSeq()!= null) {

				MpmCidAtendimento cidAtendimento = this.getMpmCidAtendimentoDAO().obterCidAtendimentoPeloId(sumarioAltaDiagnosticosCidVO.getCiaSeq()) ;

				if (cidAtendimento != null) {

					mpmAltaDiagMtvoInternacaoModificado.setCidAtendimento(cidAtendimento);

				}

			}

			if (sumarioAltaDiagnosticosCidVO.getDiaSeq() != null) {

				MamDiagnostico mamDiagnostico = this.getAmbulatorioFacade().obterDiagnosticoPorChavePrimaria(sumarioAltaDiagnosticosCidVO.getDiaSeq());

				if (mamDiagnostico != null) {

					mpmAltaDiagMtvoInternacaoModificado.setDiagnostico(mamDiagnostico);

				}

			}

			this.getManterAltaDiagMtvoInternacaoRN().atualizarAltaDiagMtvoInternacao(mpmAltaDiagMtvoInternacaoModificado);

		}

	}

	public List<SumarioAltaDiagnosticosCidVO> pesquisarMotivosInternacaoCombo(MpmAltaSumarioId id) {
		// MpmAltaSumarioId id = altaSumario.getId();
		Integer apaAtdSeq = id.getApaAtdSeq();
		Integer apaSeq = id.getApaSeq();
		Short seqp = id.getSeqp();

		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(apaAtdSeq, apaSeq, seqp);

		Set<SumarioAltaDiagnosticosCidVO> setCidVO = new HashSet<SumarioAltaDiagnosticosCidVO>();
		List<SumarioAltaDiagnosticosCidVO> lista = null;

		// Pesquisa em MpmCidAtendimento
		lista = this.getMpmCidAtendimentoDAO().pesquisarSumarioAltaDiagnosticosCidVO(apaAtdSeq);
		setCidVO.addAll(lista);
		// Pesquisa em MpmAltaDiagMtvoInternacao
		lista = this.getMpmAltaDiagMtvoInternacaoDAO().pesquisarSumarioAltaDiagnosticosCidVO(apaAtdSeq, apaSeq);
		setCidVO.addAll(lista);
		// Pesquisa em MpmAltaDiagPrincipal
		lista = this.getMpmAltaDiagPrincipalDAO().pesquisarSumarioAltaDiagnosticosCidVO(apaAtdSeq, apaSeq);
		setCidVO.addAll(lista);
		// Pesquisa em MpmAltaDiagSecundario
		lista = this.getMpmAltaDiagSecundarioDAO().pesquisarSumarioAltaDiagnosticosCidVO(apaAtdSeq, apaSeq);
		setCidVO.addAll(lista);
		// Pesquisa em MpmObtCausaDireta
		lista = this.getMpmObtCausaDiretaDAO().pesquisarSumarioAltaDiagnosticosCidVO(apaAtdSeq, apaSeq);
		setCidVO.addAll(lista);
		// Pesquisa em MpmObtCausaAntecedente
		lista = this.getMpmObtCausaAntecedenteDAO().pesquisarSumarioAltaDiagnosticosCidVO(apaAtdSeq, apaSeq);
		setCidVO.addAll(lista);
		// Pesquisa em MpmObtOutraCausa
		lista = this.getMpmObtOutraCausaDAO().pesquisarSumarioAltaDiagnosticosCidVO(apaAtdSeq, apaSeq);
		setCidVO.addAll(lista);
		// Não pesquisar por MPM_TRF_DIAGNOSTICOS conforme descrito no documento de análise.
		// Pesquisa em MamDiagnostico
		if (altaSumario.getAtendimento() != null) {

			Integer pacCodigo = altaSumario.getAtendimento().getPaciente().getCodigo();
			lista = this.getAmbulatorioFacade().pesquisarSumarioAltaDiagnosticosCidVO(pacCodigo);
			setCidVO.addAll(lista);

		}

		for (SumarioAltaDiagnosticosCidVO sadCidVO : setCidVO) {
			AghCid cid = this.aghuFacade.obterCid(sadCidVO.getCid().getSeq());
			sadCidVO.setCid(cid);
			sadCidVO.setId(id);
			sadCidVO.setOrigemListaCombo(Boolean.TRUE);
		}

		lista = new ArrayList<SumarioAltaDiagnosticosCidVO>(setCidVO);
		Collections.sort(lista, new SumarioAltaDiagnosticosCidVOComparator());

		return lista;
	}

	public List<SumarioAltaProcedimentosCrgVO> pesquisarCirurgiasRelizadasCombo(MpmAltaSumarioId id) {
		List<SumarioAltaProcedimentosCrgVO> listaVO = new ArrayList<SumarioAltaProcedimentosCrgVO>();

		IBlocoCirurgicoFacade blocoCirurgicoFacade = getBlocoCirurgicoFacade();
		List<Object[]> listaCirurgias = blocoCirurgicoFacade.pesquisarRealizadasPorProcedimentoEspecial(id.getApaAtdSeq());
		for (Object[] colunas : listaCirurgias) {
			Integer crgSeq = (Integer) colunas[0];
			Integer eprPciSeq = (Integer) colunas[1];
			Short eprEspSeq = (Short) colunas[2];
			DominioIndRespProc indRespProc = (DominioIndRespProc) colunas[3];
			Date data = (Date) colunas[4];
			String descricao = (String) colunas[5];

			MbcCirurgias cirurgia = this.getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(crgSeq);
			MbcProcEspPorCirurgiasId procEspPorCirurgiasId = null;
			procEspPorCirurgiasId = new MbcProcEspPorCirurgiasId(cirurgia.getSeq(), eprPciSeq, eprEspSeq, indRespProc);

			SumarioAltaProcedimentosCrgVO vo = new SumarioAltaProcedimentosCrgVO(id);
			vo.setOrigemListaCombo(Boolean.TRUE);
			vo.setData(data);
			vo.setDescricao(descricao);
			vo.setSeqProcedimentoCirurgico(eprPciSeq);
			vo.setProcEspPorCirurgiasId(procEspPorCirurgiasId);

			listaVO.add(vo);
		}

		return listaVO;
	}

	public List<SumarioAltaProcedimentosCrgVO> pesquisarCirurgiasRelizadasGrid(MpmAltaSumarioId id) throws ApplicationBusinessException {
		List<SumarioAltaProcedimentosCrgVO> listaVO = new ArrayList<SumarioAltaProcedimentosCrgVO>();

		MpmAltaCirgRealizadaDAO dao = this.getMpmAltaCirgRealizadaDAO();
		List<MpmAltaCirgRealizada> listaCirurgiasRealizadas = dao.obterMpmAltaCirgRealizada(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());

		for (MpmAltaCirgRealizada cirgRealizada : listaCirurgiasRealizadas) {
			SumarioAltaProcedimentosCrgVO vo = new SumarioAltaProcedimentosCrgVO(id);
			vo.setSeqp(cirgRealizada.getId().getSeqp());
			vo.setData(cirgRealizada.getDthrCirurgia());
			vo.setDescricao(cirgRealizada.getDescCirurgia());
			vo.setSeqProcedimentoCirurgico(cirgRealizada.getProcedimentoCirurgico().getSeq());
			if (cirgRealizada.getProcedimentoEspCirurgia() != null) {
				vo.setProcEspPorCirurgiasId(cirgRealizada.getProcedimentoEspCirurgia().getId());
				
			}
			vo.setOrigemListaCombo(cirgRealizada.getIndCarga());

			listaVO.add(vo);
		}

		return listaVO;
	}

	/**
	 * Função retorna lista de medicamentos já subtraída.
	 * 
	 * @param {MpmAltaSumarioId} id
	 * @throws ApplicationBusinessException
	 * @return List<AfaMedicamentoPrescricaoVO>
	 */
	public List<AfaMedicamentoPrescricaoVO> prepararListasMedicamento(MpmAltaSumarioId id) throws ApplicationBusinessException {

		List<AfaMedicamentoPrescricaoVO> listaVO = new ArrayList<AfaMedicamentoPrescricaoVO>();

		if(id.getApaAtdSeq() != null) {

			MpmPrescricaoMdtoDAO dao = this.getPrescricaoMdtoDAO();
			List<Object[]> lista = dao.pesquisarPrescricaoMdtoMaterialPorAtendimento(id.getApaAtdSeq());

			for (Object[] object : lista) {

				// Pega dados do object
				Integer medMatCodigo = (Integer) object[1];
				AfaMedicamento med = (AfaMedicamento) object[3];

				// Seta dados no VO.
				AfaMedicamentoPrescricaoVO vo = new AfaMedicamentoPrescricaoVO();
				vo.setAsuApaAtdSeq(id.getApaAtdSeq());
				vo.setAsuApaSeq(id.getApaSeq());
				vo.setAsuSeqp(id.getSeqp());
				vo.setMedMatCodigo(medMatCodigo);
				vo.setDescricao(med.getDescricaoEditada());

				listaVO.add(vo);

			}

			//Monta lista para realização do MINUS
			List<AfaMedicamentoPrescricaoVO> listaMinusVO = new ArrayList<AfaMedicamentoPrescricaoVO>();

			MpmAltaSumarioDAO altaDAO = this.getMpmAltaSumarioDAO();
			List<Object[]> listaMinus = altaDAO.pesquisarAltaSumarioMaterialPorAtendimento(id.getApaAtdSeq());

			for (Object[] object : listaMinus) {

				// Pega dados do object
				Integer medMatCodigo = (Integer) object[1];
				String descricao = (String) object[2];

				// Seta dados no VO.
				AfaMedicamentoPrescricaoVO vo = new AfaMedicamentoPrescricaoVO();
				vo.setAsuApaAtdSeq(id.getApaAtdSeq());
				vo.setAsuApaSeq(id.getApaSeq());
				vo.setAsuSeqp(id.getSeqp());
				vo.setMedMatCodigo(medMatCodigo);
				vo.setDescricao(descricao);

				listaMinusVO.add(vo);

			}

			//Realiza MINUS
			listaVO.removeAll(listaMinusVO);

		}

		return listaVO;

	}

	/**
	 * Monta primeira parte dos itens da combo de procedimentos da tela
	 * de sumário de alta.
	 *
	 * @param {MpmAltaSumarioId} id
	 * @return
	 */
	public List<SumarioAltaPrescricaoProcedimentoVO> pesquisarPrescricaoProcedimentoCombo(MpmAltaSumarioId id) {
		List<SumarioAltaPrescricaoProcedimentoVO> listaVO = new ArrayList<SumarioAltaPrescricaoProcedimentoVO>();

		MpmPrescricaoProcedimentoDAO dao = this.getPrescricaoProcedimentoDAO();
		List<Object[]> lista = dao.pesquisarPrescricaoProcedimentoConfirmadoPorAtendimento(id.getApaAtdSeq());

		for (Object[] object : lista) {

			// Pega dados do object
			Integer matCodigo = (Integer) object[0];
			Integer pciSeq = (Integer) object[1];
			Short pedSeq = (Short) object[2];
			String pciDescricao = (String) object[3];
			String pedDescricao = (String) object[4];
			String matNome = (String) object[5];

			// Seta dados no VO.
			SumarioAltaPrescricaoProcedimentoVO vo = new SumarioAltaPrescricaoProcedimentoVO(id);
			vo.setMatCodigo(matCodigo);
			vo.setPciSeq(pciSeq);
			vo.setPedSeq(pedSeq);
			vo.setDescProcedCirurgico(pciDescricao);
			vo.setDescProcedEspecial(pedDescricao);
			vo.setMatNome(matNome);
			vo.setOrigemListaCombo(Boolean.TRUE);

			listaVO.add(vo);

		}

		MpmPrescricaoNptDAO nptDao = this.getPrescricaoNptDAO();
		List<Object[]> lista2 = nptDao.pesquisarPrescricaoNptConfirmadoPorAtendimento(id.getApaAtdSeq());
		for (Object[] object : lista2) {

			Short pedSeq = (Short) object[0];
			if (pedSeq != null) {

				String pedDesc = (String) object[1];
				Date data = nptDao.pesquisarMenorDataInicoNptConfirmadoPorAtendimentoEEspecialDiverso(id.getApaAtdSeq(), pedSeq);

				// Seta dados no VO.
				SumarioAltaPrescricaoProcedimentoVO vo = new SumarioAltaPrescricaoProcedimentoVO(id);
				vo.setPedSeq(pedSeq);
				vo.setDescProcedEspecial(pedDesc);
				vo.setDescricao(pedDesc);
				vo.setData(data);
				vo.setOrigemListaCombo(Boolean.TRUE);

				listaVO.add(vo);
			}

		}

		return listaVO;

	}

	/**
	 * Pesquisa procedimentos do slider Procedimentos;
	 *
	 * @param {MpmAltaSumarioId} id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SumarioAltaPrescricaoProcedimentoVO> pesquisarPrescricaoProcedimentoGrid(MpmAltaSumarioId id) throws ApplicationBusinessException {
		List<SumarioAltaPrescricaoProcedimentoVO> listaVO = new ArrayList<SumarioAltaPrescricaoProcedimentoVO>();

		MpmAltaOtrProcedimentoDAO dao = this.getAltaOtrProcedimentoDAO();
		List<MpmAltaOtrProcedimento> lista = dao.obterMpmAltaOtrProcedimentoSliderProcedimentos(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());

		for (MpmAltaOtrProcedimento altaOtrProcedimento : lista) {

			Integer matCodigo = null;
			String matNome = null;
			Integer pciSeq = null;
			String pciDescricao = null;
			Short pedSeq = null;
			String pedDescricao = null;

			// Pega dados do objeto
			if (altaOtrProcedimento.getMatCodigo() != null) {

				matCodigo = altaOtrProcedimento.getMatCodigo().getCodigo();
				matNome = altaOtrProcedimento.getMatCodigo().getNome();

			}

			if (altaOtrProcedimento.getMbcProcedimentoCirurgicos() != null) {

				pciSeq = altaOtrProcedimento.getMbcProcedimentoCirurgicos().getSeq();
				pciDescricao = altaOtrProcedimento.getMbcProcedimentoCirurgicos().getDescricao();

			}

			if (altaOtrProcedimento.getMpmProcedEspecialDiversos() != null) {

				pedSeq = altaOtrProcedimento.getMpmProcedEspecialDiversos().getSeq();
				pedDescricao = altaOtrProcedimento.getMpmProcedEspecialDiversos().getDescricao();

			}

			SumarioAltaPrescricaoProcedimentoVO vo = new SumarioAltaPrescricaoProcedimentoVO(id);
			vo.setSeqp(altaOtrProcedimento.getId().getSeqp());
			vo.setMatCodigo(matCodigo);
			vo.setPciSeq(pciSeq);
			vo.setPedSeq(pedSeq);
			vo.setDescProcedCirurgico(pciDescricao);
			vo.setDescProcedEspecial(pedDescricao);
			vo.setMatNome(matNome);
			vo.setData(altaOtrProcedimento.getDthrOutProcedimento());
			vo.setDescricao(altaOtrProcedimento.getDescOutProcedimento());

			vo.setOrigemListaCombo(altaOtrProcedimento.getIndCarga());

			listaVO.add(vo);
		}

		return listaVO;
	}

	/**
	 * Pesquisa procedimentos do slider Outros Procedimentos;
	 *
	 * @param {MpmAltaSumarioId} id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SumarioAltaProcedimentosVO> pesquisarPrescricaoOutrosProcedimentoGrid(MpmAltaSumarioId id) throws ApplicationBusinessException {
		List<SumarioAltaProcedimentosVO> listaVO = new ArrayList<SumarioAltaProcedimentosVO>();

		MpmAltaOtrProcedimentoDAO dao = this.getAltaOtrProcedimentoDAO();
		List<MpmAltaOtrProcedimento> lista = dao.obterMpmAltaOtrProcedimentoSliderOutrosProcedimentos(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());

		for (MpmAltaOtrProcedimento altaOtrProcedimento : lista) {
			SumarioAltaProcedimentosVO vo = new SumarioAltaProcedimentosVO(id);
			vo.setSeqp(altaOtrProcedimento.getId().getSeqp());
			vo.setData(altaOtrProcedimento.getDthrOutProcedimento());
			vo.setDescricao(altaOtrProcedimento.getComplOutProcedimento());

			vo.setOrigemListaCombo(altaOtrProcedimento.getIndCarga());

			listaVO.add(vo);
		}

		return listaVO;
	}

	/**
	 * Popula o que resta da lista de acordo com PLL MPMP_LISTA_OUT_PROCEDIMENTOS.
	 *
	 * FORM: MPMF_SUMARIO_ALTA
	 * PLL: MPMF_SUMARIO_ALTA
	 * FUNCTION: MPMP_LISTA_OUT_PROCEDIMENTOS
	 *
	 * @param {List<SumarioAltaPrescricaoProcedimentoVO>} listaComboPrescricaoProcedimentos
	 * @param {MpmAltaSumarioId} id
	 */
	public void populaListaComboPrescricaoProcedimento(
			List<SumarioAltaPrescricaoProcedimentoVO> listaComboPrescricaoProcedimentos, MpmAltaSumarioId id) {

		MpmPrescricaoProcedimentoDAO dao = this.getPrescricaoProcedimentoDAO();

		for (SumarioAltaPrescricaoProcedimentoVO vo : listaComboPrescricaoProcedimentos) {

			if (vo.getPciSeq() != null) {

				//Busca dados
				Date data = dao.pesquisarMenorDataInicoPrescricaoProcedimentoConfirmadoPorAtendimentoEProcedimentosCirurgicos(id.getApaAtdSeq(), vo.getPciSeq());

				//Seta dados
				if (data != null) {
					vo.setData(data);
				}	
				vo.setDescricao(vo.getDescProcedCirurgico());

			}

			if (vo.getPedSeq() != null) {

				//Busca dados
				Date data = dao.pesquisarMenorDataInicoPrescricaoProcedimentoConfirmadoPorAtendimentoEProcedimentosDiversos(id.getApaAtdSeq(), vo.getPedSeq());

				//Seta dados
				if (data != null) { 
					vo.setData(data);
				}
				vo.setDescricao(vo.getDescProcedEspecial());

			}

			if (vo.getMatCodigo() != null) {

				//Busca dados
				Date data = dao.pesquisarMenorDataInicoPrescricaoProcedimentoConfirmadoPorAtendimentoEMaterial(id.getApaAtdSeq(), vo.getMatCodigo());

				//Seta dados
				if (data != null) {
					vo.setData(data);
				}	
				vo.setDescricao(vo.getMatNome());

			}

		}

	}

	/**
	 * Busca parâmetro P_AGHU_MODULO_RESPOSTA_CONSULTORIA_IMPLANTADO;
	 * @return {Boolean} moduloConsultoriasImplantado
	 */
	private boolean buscaParametroConsultoriasImplantado() {

		//Verifica o parâmetro "P_AGHU_MODULO_RESPOSTA_CONSULTORIA_IMPLANTADO" para filtrar ou não os resultados
		boolean moduloConsultoriasImplantado = false;

		AghParametros parametro = null;
		try {
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_MODULO_RESPOSTA_CONSULTORIA_IMPLANTADO);
		} catch(ApplicationBusinessException e) {
			logError(e.getClass().getName(), e);
		}
		if(parametro != null && parametro.getVlrTexto().equalsIgnoreCase("true")) {
			moduloConsultoriasImplantado = true;
		}

		return moduloConsultoriasImplantado;

	}

	public List<SumarioAltaProcedimentosConsultoriasVO> pesquisarConsultoriasCombo(MpmAltaSumarioId id) {
		List<SumarioAltaProcedimentosConsultoriasVO> listaVO = new ArrayList<SumarioAltaProcedimentosConsultoriasVO>();

		boolean moduloConsultoriasImplantado = this.buscaParametroConsultoriasImplantado();

		// TODO ver MPMP_LISTA_CONSULTORIA
		MpmSolicitacaoConsultoriaDAO dao = this.getMpmSolicitacaoConsultoriaDAO();
		List<MpmSolicitacaoConsultoria> listaSolicitacaoConsultoria;
		if(moduloConsultoriasImplantado) {
			listaSolicitacaoConsultoria = dao.obterListaConsultoriasRespondidas(id.getApaAtdSeq());
		} else {
			listaSolicitacaoConsultoria = dao.obterListaConsultorias(id.getApaAtdSeq());
		}

		for (MpmSolicitacaoConsultoria solicitacaoConsultoria : listaSolicitacaoConsultoria) {
			AghEspecialidades especialidade = solicitacaoConsultoria.getEspecialidade();

			SumarioAltaProcedimentosConsultoriasVO vo = new SumarioAltaProcedimentosConsultoriasVO(id);

			// Dados de tela
			vo.setDescricao(especialidade.getNomeEspecialidade());

			// Dados para persistência, controle e evitar duplicação com dados da grid
			// Ver SumarioAltaProcedimentosConsultoriasVO.equals
			vo.setSolicitacaoConsultoriaId(solicitacaoConsultoria.getId());
			vo.setDthrResposta(solicitacaoConsultoria.getDthrResposta());
			if(moduloConsultoriasImplantado) {
				vo.setData(solicitacaoConsultoria.getDthrResposta());
			} else {
				vo.setData(solicitacaoConsultoria.getDthrSolicitada());
			}

			vo.setSeqEspecialidade(especialidade.getSeq());
			vo.setNomeEspecialidade(especialidade.getNomeEspecialidade());

			vo.setOrigemListaCombo(Boolean.TRUE);

			listaVO.add(vo);
		}

		return listaVO;
	}

	public List<SumarioAltaProcedimentosConsultoriasVO> pesquisarConsultoriasGrid(MpmAltaSumarioId id) throws ApplicationBusinessException {
		List<SumarioAltaProcedimentosConsultoriasVO> listaVO = new ArrayList<SumarioAltaProcedimentosConsultoriasVO>();

		boolean moduloConsultoriasImplantado = this.buscaParametroConsultoriasImplantado();
		MpmAltaConsultoriaDAO dao = this.getMpmAltaConsultoriaDAO();
		List<MpmAltaConsultoria> listaConsultorias = dao.obterMpmAltaConsultoria(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());

		for (MpmAltaConsultoria altaConsultoria : listaConsultorias) {
			AghEspecialidades especialidade = altaConsultoria.getAghEspecialidade();
			MpmSolicitacaoConsultoria solicitacaoConsultoria = altaConsultoria.getSolicitacaoConsultoria();

			SumarioAltaProcedimentosConsultoriasVO vo = new SumarioAltaProcedimentosConsultoriasVO(id);
			vo.setSeqp(altaConsultoria.getId().getSeqp());

			// Dados de tela
			vo.setData(altaConsultoria.getDthrConsultoria());
			vo.setDescricao(altaConsultoria.getDescConsultoria());

			// Dados para persistência, controle e evitar duplicação com dados da combo
			// Ver SumarioAltaProcedimentosConsultoriasVO.equals
			if (solicitacaoConsultoria != null) {

				vo.setSolicitacaoConsultoriaId(solicitacaoConsultoria.getId());
				vo.setDthrResposta(solicitacaoConsultoria.getDthrResposta());

				if(!moduloConsultoriasImplantado) {
					vo.setData(solicitacaoConsultoria.getDthrSolicitada());
				}

			}

			vo.setSeqEspecialidade(especialidade.getSeq());
			vo.setNomeEspecialidade(especialidade.getNomeEspecialidade());
			vo.setOrigemListaCombo(altaConsultoria.getIndCarga());

			listaVO.add(vo);
		}

		return listaVO;
	}

	/**
	 * Método responsável por executar a lógica de gravar as alteracoes de
	 * Sumario Alta - Evolucao e Estado do Paciente
	 *
	 * @param altaEvolucaoEstadoVo
	 * @return uma <code>AltaEvolucaoEstadoPacienteVO</code> com as alteracoes
	 * @throws ApplicationBusinessException
	 */
	public AltaEvolucaoEstadoPacienteVO gravarAltaSumarioEvolucaoEstado(AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoVo, String origem) throws ApplicationBusinessException {
		// Validacao de contrato:
		// - este metodo nao pode ser chamado com parametro nulo;
		// - nem sem estar associado a uma AltaSumario
		if ((altaEvolucaoEstadoVo == null) || (altaEvolucaoEstadoVo.getId() == null)) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO_NAO_INFORMADO);
		}
		// Validacao de negocio, campos obrigatorios:
		// - MPM_02724=A evolução deve ser preenchida na alta.
		// - MPM_02723=Preencha o estado do paciente.
		if (altaEvolucaoEstadoVo.getEstadoPaciente() == null && origem.equals(alta)) {
			throw new ApplicationBusinessException(ManterAltaSumarioONExceptionCode.MPM_02723);
		}
		if (StringUtils.isBlank(altaEvolucaoEstadoVo.getDescricao())) {
			throw new ApplicationBusinessException(ManterAltaSumarioONExceptionCode.MPM_02724);
		}

		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(altaEvolucaoEstadoVo.getId().getApaAtdSeq(), altaEvolucaoEstadoVo.getId().getApaSeq(), altaEvolucaoEstadoVo.getId().getSeqp());
		MpmAltaEvolucao altaEvolucao = this.getAltaEvolucaoDAO().obterMpmAltaEvolucao(altaEvolucaoEstadoVo.getId().getApaAtdSeq(), altaEvolucaoEstadoVo.getId().getApaSeq(), altaEvolucaoEstadoVo.getId().getSeqp());
		MpmAltaEstadoPaciente altaEstadoPaciente = this.getAltaEstadoPacienteDAO().obterMpmAltaEstadoPaciente(altaEvolucaoEstadoVo.getId().getApaAtdSeq(), altaEvolucaoEstadoVo.getId().getApaSeq(), altaEvolucaoEstadoVo.getId().getSeqp());

		if (altaEvolucao == null) {
			altaEvolucao = altaEvolucaoEstadoVo.getModelAltaEvolucao();
			altaEvolucao.setAltaSumario(altaSumario);
			this.getManterAltaEvolucaoRN().inserirAltaEvolucao(altaEvolucao);
			this.getManterAltaRecomendacaoON().montarAltaNaoCadastradaRN(altaSumario);
		} else {
			altaEvolucao.setDescricao(StringUtil.trim(altaEvolucaoEstadoVo.getDescricao()));
			this.getManterAltaEvolucaoRN().atualizarAltaEvolucao(altaEvolucao);
		}

		if (altaEstadoPaciente == null) {
			altaEstadoPaciente = altaEvolucaoEstadoVo.getModelAltaEstadoPaciente();
			altaEstadoPaciente.setDescricaoSituacaoSaida(this.getAltaEstadoPacienteDAO().obterDescricaoFormatadaCodigoSUS(altaEstadoPaciente));
			altaEstadoPaciente.setAltaSumario(altaSumario);
			this.getManterAltaEstadoPacienteRN().inserirAltaEstadoPaciente(altaEstadoPaciente);
		} else {
			altaEstadoPaciente.setEstadoPaciente(altaEvolucaoEstadoVo.getEstadoPaciente());
			altaEstadoPaciente.setDescricaoSituacaoSaida(this.getAltaEstadoPacienteDAO().obterDescricaoFormatadaCodigoSUS(altaEstadoPaciente));
			this.getManterAltaEstadoPacienteRN().atualizarAltaEstadoPaciente(altaEstadoPaciente);
		}


		//altaEvolucaoEstadoVo = this.buscaAltaSumarioEvolucaoEstado(altaSumario);

		return altaEvolucaoEstadoVo;
	}

	public AltaEvolucaoEstadoPacienteVO gravarAltaSumarioEvolucao(AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoVo) throws ApplicationBusinessException {
		// Validacao de contrato:
		// - este metodo nao pode ser chamado com parametro nulo;
		// - nem sem estar associado a uma AltaSumario
		if ((altaEvolucaoEstadoVo == null) || (altaEvolucaoEstadoVo.getId() == null)) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO_NAO_INFORMADO);
		}
		// Validacao de negocio, campos obrigatorios:
		// - MPM_02724=A evolução deve ser preenchida na alta.
		if (StringUtils.isBlank(altaEvolucaoEstadoVo.getDescricao())) {
			throw new ApplicationBusinessException(ManterAltaSumarioONExceptionCode.MPM_02724);
		}

		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(altaEvolucaoEstadoVo.getId().getApaAtdSeq(), altaEvolucaoEstadoVo.getId().getApaSeq(), altaEvolucaoEstadoVo.getId().getSeqp());
		MpmAltaEvolucao altaEvolucao = this.getAltaEvolucaoDAO().obterMpmAltaEvolucao(altaEvolucaoEstadoVo.getId().getApaAtdSeq(), altaEvolucaoEstadoVo.getId().getApaSeq(), altaEvolucaoEstadoVo.getId().getSeqp());

		if (altaEvolucao == null) {
			altaEvolucao = altaEvolucaoEstadoVo.getModelAltaEvolucao();
			altaEvolucao.setAltaSumario(altaSumario);
			this.getManterAltaEvolucaoRN().inserirAltaEvolucao(altaEvolucao);
			this.getManterAltaRecomendacaoON().montarAltaNaoCadastradaRN(altaSumario);
		} else {
			altaEvolucao.setDescricao(StringUtil.trim(altaEvolucaoEstadoVo.getDescricao()));
			this.getManterAltaEvolucaoRN().atualizarAltaEvolucao(altaEvolucao);
		}

		//altaEvolucaoEstadoVo = this.buscaAltaSumarioEvolucaoEstado(altaSumario);

		return altaEvolucaoEstadoVo;
	}
	
	public AltaEvolucaoEstadoPacienteVO gravarAltaSumarioEstado(AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoVo, String origem) throws ApplicationBusinessException {
		// Validacao de contrato:
		// - este metodo nao pode ser chamado com parametro nulo;
		// - nem sem estar associado a uma AltaSumario
		if ((altaEvolucaoEstadoVo == null) || (altaEvolucaoEstadoVo.getId() == null)) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO_NAO_INFORMADO);
		}
		// Validacao de negocio, campos obrigatorios:
		// - MPM_02723=Preencha o estado do paciente.
		if (altaEvolucaoEstadoVo.getEstadoPaciente() == null && origem.equals(alta)) {
			throw new ApplicationBusinessException(ManterAltaSumarioONExceptionCode.MPM_02723);
		}
		
		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(altaEvolucaoEstadoVo.getId().getApaAtdSeq(), altaEvolucaoEstadoVo.getId().getApaSeq(), altaEvolucaoEstadoVo.getId().getSeqp());
		MpmAltaEstadoPaciente altaEstadoPaciente = this.getAltaEstadoPacienteDAO().obterMpmAltaEstadoPaciente(altaEvolucaoEstadoVo.getId().getApaAtdSeq(), altaEvolucaoEstadoVo.getId().getApaSeq(), altaEvolucaoEstadoVo.getId().getSeqp());

		if (altaEstadoPaciente == null) {
			altaEstadoPaciente = altaEvolucaoEstadoVo.getModelAltaEstadoPaciente();
			altaEstadoPaciente.setDescricaoSituacaoSaida(this.getAltaEstadoPacienteDAO().obterDescricaoFormatadaCodigoSUS(altaEstadoPaciente));
			altaEstadoPaciente.setAltaSumario(altaSumario);
			this.getManterAltaEstadoPacienteRN().inserirAltaEstadoPaciente(altaEstadoPaciente);
		} else {
			altaEstadoPaciente.setEstadoPaciente(altaEvolucaoEstadoVo.getEstadoPaciente());
			altaEstadoPaciente.setDescricaoSituacaoSaida(this.getAltaEstadoPacienteDAO().obterDescricaoFormatadaCodigoSUS(altaEstadoPaciente));
			this.getManterAltaEstadoPacienteRN().atualizarAltaEstadoPaciente(altaEstadoPaciente);
		}

		//altaEvolucaoEstadoVo = this.buscaAltaSumarioEvolucaoEstado(altaSumario);

		return altaEvolucaoEstadoVo;
	}
	
	@SuppressWarnings("PMD")
	public AltaEvolucaoEstadoPacienteVO buscaAltaSumarioEvolucaoEstado(MpmAltaSumario pAltaSumario) throws ApplicationBusinessException {
		// Validacao de contrato:
		// - este metodo nao pode ser chamado com parametro nulo;
		// - nem sem estar associado a uma AltaSumario
		if ((pAltaSumario == null) || (pAltaSumario.getId() == null)) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO_NAO_INFORMADO);
		}

        MpmAltaEvolucao altaEvolucao = this.getAltaEvolucaoDAO().obterMpmAltaEvolucao(pAltaSumario.getId().getApaAtdSeq(), pAltaSumario.getId().getApaSeq(), pAltaSumario.getId().getSeqp());
        MpmAltaEstadoPaciente altaEstadoPaciente = this.getAltaEstadoPacienteDAO().obterMpmAltaEstadoPaciente(pAltaSumario.getId().getApaAtdSeq(), pAltaSumario.getId().getApaSeq(), pAltaSumario.getId().getSeqp());
        AltaEvolucaoEstadoPacienteVO vo = new AltaEvolucaoEstadoPacienteVO();
        vo.setId(pAltaSumario.getId());

        if (altaEvolucao != null) {
            vo.setIdEvolucao(altaEvolucao.getId());
            vo.setDescricao(altaEvolucao.getDescricao());
        }else{
            // Texto para a mae
            StringBuffer texto = new StringBuffer();
            Date dthrAtendimento = pAltaSumario.getDthrAtendimento();
            //Busca Alta Sem Problema de Proxy
            pAltaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(
            						pAltaSumario.getId().getApaAtdSeq(), 
            						pAltaSumario.getId().getApaSeq(), 
            						pAltaSumario.getId().getSeqp());
            
            Integer pacCodigo = pAltaSumario.getPaciente().getCodigo();
            AghParametros pClinicaObstetrica =  getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CLINICA_OBSTETRICA);
            if(pAltaSumario.getUnidadeFuncional().getClinica() != null && pAltaSumario.getUnidadeFuncional().getClinica().getCodigo() == pClinicaObstetrica.getVlrNumerico().intValue()
            		&& this.getPerinatologiaFacade().verificarExisteAtendimentoGestacaoUnidadeFuncional(pAltaSumario.getId().getApaAtdSeq(), pAltaSumario.getUnidadeFuncional().getSeq())) {
                texto.append(this.montarAltaGE(pacCodigo, pAltaSumario.getAtendimento()));
            }

            // Texto para a crianca
            boolean ehUTINNeonatologia;
            ehUTINNeonatologia = this.getAghuFacade().unidadeFuncionalPossuiCaracteristica(pAltaSumario.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_UTIN, ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA);
            if((ehUTINNeonatologia && pAltaSumario.getPaciente().getMaePaciente() != null
            		&& pAltaSumario.getPaciente().getMaePaciente().getCodigo() != null)){
                montarAltaRNCompleta(texto, dthrAtendimento, pacCodigo);
            }else if (DominioOrigemAtendimento.N.equals(pAltaSumario.getAtendimento().getOrigem())) {
				if (pAltaSumario.getPaciente().getMaePaciente() != null && pAltaSumario.getPaciente().getMaePaciente().getCodigo() != null) {
					pacCodigo = pAltaSumario.getPaciente().getMaePaciente().getCodigo();
					montarAltaRNCompleta(texto, dthrAtendimento, pacCodigo);
				}
            }
            vo.setDescricao(texto.toString());
        }

        if (altaEstadoPaciente != null) {
            vo.setIdEstado(altaEstadoPaciente.getId());
            vo.setEstadoPaciente(altaEstadoPaciente.getEstadoPaciente());
        }

        
        if (StringUtils.isNotBlank(vo.getDescricao())){
        	gravarAltaSumarioEvolucao(vo);
        }
		return vo;
	}

	private void montarAltaRNCompleta(StringBuffer texto, Date dthrAtendimento,
			Integer pacCodigo) {
		texto.append(this.montarAltaRN(pacCodigo, dthrAtendimento));
		texto.append(QUEBRA_LINHA);
		texto.append(this.montarAltaRNNormal(pacCodigo));
	}

    @SuppressWarnings("PMD")
    private String montarAltaGE(Integer pacCodigo, AghAtendimentos aghAtendimentos) {

        StringBuffer texto = new StringBuffer();
        McoGestacoes mcoGestacoes = this.getPerinatologiaFacade().obterMcoGestacoes(new McoGestacoesId(aghAtendimentos.getGsoPacCodigo(), aghAtendimentos.getGsoSeqp()));
        Byte ultimoSeqpRegSanguineo = this.getPacienteFacade().buscaMaxSeqpAipRegSanguineos(pacCodigo);
        AipRegSanguineos aipRegSanguineos = this.getPacienteFacade().obterRegSanguineosPorCodigoPaciente(pacCodigo, ultimoSeqpRegSanguineo);
        Short gsoSeqp;
        List<McoNascimentos> listaNascimentos;
        McoRecemNascidos recemNascido;
        AipPesoPacientes pesoPaciente;

        if(mcoGestacoes != null){
            gsoSeqp = mcoGestacoes.getId().getSeqp();
            mcoGestacoes.setDthrSumarioAltaMae(new Date());
            this.getPerinatologiaFacade().atualizarMcoGestacoes(mcoGestacoes, true);
            texto.append(this.montaDadosGestacao(mcoGestacoes));

            if(aipRegSanguineos != null && !"".equals(aipRegSanguineos.getGrupoSanguineo()) && "-".equals(aipRegSanguineos.getFatorRh())){
                texto.append(", Tipo Sangue Mãe " + aipRegSanguineos.getGrupoSanguineo() + "-");
            }

            if(mcoGestacoes.getIgAtualSemanas() != null){
                texto.append(", IG de " + Byte.toString(mcoGestacoes.getIgAtualSemanas()) + " sem")  ;
            }

            if(mcoGestacoes.getIgAtualDias() != null){
                texto.append(" " + Byte.toString(mcoGestacoes.getIgAtualDias()) + " dia(s)")  ;
            }

            listaNascimentos = this.getPerinatologiaFacade().listarNascimentos(pacCodigo, gsoSeqp);
            for (McoNascimentos bebe : listaNascimentos) {
                texto.append(", RN" + bebe.getId().getSeqp() + " " + bebe.getTipo().getDescricao());
                if(bebe.getModo() != null){
                    texto.append(" " + bebe.getModo().getDescricao());
                }
                recemNascido = this.getPerinatologiaFacade().obterRecemNascidoGestacoesPaciente(pacCodigo,gsoSeqp,bebe.getId().getSeqp());
                if (bebe.getDthrNascimento() != null){
                    texto.append(" em " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(bebe.getDthrNascimento()) + " hs");
                }else if(recemNascido.getDthrNascimento() != null){
                	texto.append(" em " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(recemNascido.getDthrNascimento()) + " hs");
                }
                if(recemNascido.getApgar1() != null){
                    texto.append(", APGAR " + recemNascido.getApgar1());
                }
                if(recemNascido.getApgar5() != null){
                    texto.append("/" + recemNascido.getApgar5());
                }
                if(recemNascido.getPaciente().getSexo() != null){
                    texto.append(", " + recemNascido.getPaciente().getSexo().getDescricao());
                }
                pesoPaciente = this.getPacienteFacade().obterPesoPaciente(recemNascido.getPaciente().getCodigo(), DominioMomento.N);
                if(pesoPaciente != null && pesoPaciente.getPeso() != null){
                    texto.append(", " + pesoPaciente.getPeso().multiply(new BigDecimal(1000)).setScale(0, BigDecimal.ROUND_HALF_UP) + " G");
                }
            }
        }
        return texto.toString();
    }

    @SuppressWarnings("PMD")
    private String montarAltaRN(Integer pacCodigo,Date dthrAtendimento) {
        StringBuffer texto = new StringBuffer();
        texto.append("\nReflexo Vermelho(Teste do Olhinho):<<INFORME AQUI O RESULTADO DO TESTE DO OLHINHO>>\n");
        List<McoSnappes> mcoSnappesList =  this.getPerinatologiaFacade().listarSnappesPorCodigoPacienteDthrInternacao(pacCodigo, dthrAtendimento);
        for (McoSnappes mcoSnappes : mcoSnappesList){
            texto.append("ESCORE SNAPPEII EM " + new SimpleDateFormat("dd/MM/yyyy").format(mcoSnappes.getCriadoEm()) + ": " + mcoSnappes.getEscoreSnappeii() + " ,");
            mcoSnappes.setDthrSumarioAlta(new Date());
            this.getPerinatologiaFacade().atualizarMcoSnappes(mcoSnappes,true);
        }
        return texto.substring(0,texto.length()-1);
    }

    @SuppressWarnings("PMD")
    private String montarAltaRNNormal(Integer pacCodigo) {
        StringBuffer texto = new StringBuffer();
        AipPesoPacientes pesoPaciente;
        McoRecemNascidos recemNascido;
        List<McoNascimentos> listaNascimentos;
        List<McoExameFisicoRns> examesFisicosRealizados;
        AipAlturaPacientes alturaPaciente;
        Byte ultimoSeqpRegSanguineoBebe;
        AipRegSanguineos aipRegSanguineosBebe;
        Short ultimoSeqpGestacao = this.getPerinatologiaFacade().obterMaxSeqMcoGestacoes(pacCodigo);
        McoGestacoes mcoGestacoes = this.getPerinatologiaFacade().obterMcoGestacoes(new McoGestacoesId(pacCodigo,ultimoSeqpGestacao));
        Byte ultimoSeqpRegSanguineo = this.getPacienteFacade().buscaMaxSeqpAipRegSanguineos(pacCodigo);
        AipRegSanguineos aipRegSanguineos = this.getPacienteFacade().obterRegSanguineosPorCodigoPaciente(pacCodigo, ultimoSeqpRegSanguineo);
        Short gsoSeqp;

        if(mcoGestacoes != null) {
            texto.append("MÃE C/ " + mcoGestacoes.getPaciente().getIdade() + " anos; ");
            gsoSeqp = mcoGestacoes.getId().getSeqp();
            texto.append(this.montaDadosGestacao(mcoGestacoes));
            if(aipRegSanguineos != null && aipRegSanguineos.getGrupoSanguineo() != null && aipRegSanguineos.getFatorRh() != null){
                texto.append(", Tipo Sangue Mãe " + aipRegSanguineos.getGrupoSanguineo() + aipRegSanguineos.getFatorRh());
            }else{
                texto.append(", Tipo Sangue Mãe ______________");
            }
            if(mcoGestacoes.getNumConsPrn() != null && mcoGestacoes.getNumConsPrn() != 0){
                texto.append(",    PRÉ-NATAL COM " + mcoGestacoes.getNumConsPrn() + " CONSULTAS.\n");
            }else{
                texto.append(",    PRÉ-NATAL COM ___________ CONSULTAS.\n");
            }
            texto.append("EXAMES:   VDRL(____),   HBSAG(____),   HIV(____),  TOXO IgM(____),  IgG(____);\n");
            listaNascimentos = this.getPerinatologiaFacade().listarNascimentos(pacCodigo, gsoSeqp);
            for (McoNascimentos bebe : listaNascimentos) {
                recemNascido = this.getPerinatologiaFacade().obterRecemNascidoGestacoesPaciente(pacCodigo,gsoSeqp,bebe.getId().getSeqp());
                StringBuffer dataNascimento = new StringBuffer("   às ");
                if(bebe.getDthrNascimento()!=null){
                    dataNascimento.append(new SimpleDateFormat("HH:mm").format(bebe.getDthrNascimento()));
                    dataNascimento.append(" do dia " + new SimpleDateFormat("dd/MM/yyyy").format(bebe.getDthrNascimento()));
                }else if(recemNascido.getDthrNascimento() !=null){
                    dataNascimento.append(new SimpleDateFormat("HH:mm").format(recemNascido.getDthrNascimento()));
                    dataNascimento.append(" do dia " + new SimpleDateFormat("dd/MM/yyyy").format(recemNascido.getDthrNascimento()));
                }
                if(bebe.getApresentacao() != null){
                    texto.append("Apresentação " + bebe.getApresentacao().getDescricao());
                }else{
                    texto.append("Apresentação __________");
                }
                texto.append(QUEBRA_LINHA);
                examesFisicosRealizados = this.getPerinatologiaFacade().listarExamesFisicosRns(pacCodigo, gsoSeqp);
                for(McoExameFisicoRns exameFisico : examesFisicosRealizados){
                    if(exameFisico.getReflexoVermelho() !=null){
                        texto.append(exameFisico.getReflexoVermelho().getDescricao() + QUEBRA_LINHA);
                    }
                }
                McoBolsaRotas bolsaRotas = this.getPerinatologiaFacade().obterMcoBolsaRotasProId(new McoGestacoesId(pacCodigo,ultimoSeqpGestacao));
                if(bolsaRotas != null) {
                    if (bolsaRotas.getLiquidoAmniotico() != null) {
                        texto.append("Líquido Amniótico " + bolsaRotas.getLiquidoAmniotico().getDescricao());
                    } else {
                        texto.append("Líquido Amniótico __________");
                    }
                    if (recemNascido.getDiasRuptBolsa() != null && recemNascido.getHrsRuptBolsa() != null && recemNascido.getMinRuptBolsa() != null) {
                        texto.append(" Bolsa Rota há " + recemNascido.getDiasRuptBolsa() + " dias " + recemNascido.getHrsRuptBolsa() + "hs " + recemNascido.getMinRuptBolsa() + " min\n");
                    } else {
                        texto.append(" Bolsa Rota há ___________\n");
                    }
                }else{
                    texto.append("Líquido Amniótico __________");
                    texto.append(" Bolsa Rota há ___________\n");
                }
                texto.append("Evoluiu para " + bebe.getTipo().getDescricao() + dataNascimento.toString() + QUEBRA_LINHA);
                texto.append("RN do sexo:");
                if(recemNascido.getPaciente().getSexo() != null){
                    texto.append(recemNascido.getPaciente().getSexo().getDescricao());
                }else{
                    texto.append("_________");
                }
                texto.append("    Cor: ");
                if(recemNascido.getPaciente().getCor() != null){
                    texto.append(recemNascido.getPaciente().getCor().getDescricao());
                }else{
                    texto.append("_________");
                }
                texto.append(", pesando ");
                pesoPaciente = this.getPacienteFacade().obterPesoPaciente(recemNascido.getPaciente().getCodigo(), DominioMomento.N);
                if(pesoPaciente != null && pesoPaciente.getPeso() != null){
                    texto.append(pesoPaciente.getPeso().multiply(new BigDecimal(1000)).setScale(0, BigDecimal.ROUND_HALF_UP) + " G");
                }else{
                    texto.append("________ G");
                }
                if(recemNascido.getApgar1() != null){
                    texto.append("  , APGAR " + recemNascido.getApgar1());
                }
                if(recemNascido.getApgar5() != null){
                    texto.append("/" + recemNascido.getApgar5());
                }
                texto.append("\nComprimento: ");
                alturaPaciente = this.getPacienteFacade().obterAlturasPaciente(recemNascido.getPaciente().getCodigo(), DominioMomento.N);
                if(alturaPaciente != null && alturaPaciente.getAltura() != null){
                    texto.append(alturaPaciente.getAltura().setScale(0, BigDecimal.ROUND_HALF_UP));
                }else{
                    texto.append("________ ");
                }
                texto.append(" cm, ");
                if (examesFisicosRealizados == null || examesFisicosRealizados.isEmpty()){
                    texto.append("Perímetro Cefálico: ______ cm, ");
                    texto.append("Perímetro Torácico: ______ cm, \n");
                }else{
	                for(McoExameFisicoRns exameFisico : examesFisicosRealizados){
	                    if(exameFisico.getPerCefalico() !=null){
	                        texto.append("Perímetro Cefálico: " +  exameFisico.getPerCefalico().setScale(0, BigDecimal.ROUND_HALF_UP) + " cm, ");
	                    }else{
	                        texto.append("Perímetro Cefálico: ______ cm, ");
	                    }
	                    if(exameFisico.getPerToracico() !=null){
	                        texto.append("Perímetro Torácico: " +  exameFisico.getPerToracico().setScale(0, BigDecimal.ROUND_HALF_UP) + " cm, \n");
	                    }else{
	                        texto.append("Perímetro Torácico: ______ cm, \n");
	                    }
	                }
                }
                texto.append(" TS: ");
                //Obter tipo os dados de sangue do bebe
                ultimoSeqpRegSanguineoBebe = this.getPacienteFacade().buscaMaxSeqpAipRegSanguineos(recemNascido.getPaciente().getCodigo());
                aipRegSanguineosBebe = this.getPacienteFacade().obterRegSanguineosPorCodigoPaciente(recemNascido.getPaciente().getCodigo(), ultimoSeqpRegSanguineoBebe);
                if(aipRegSanguineosBebe != null && aipRegSanguineosBebe.getGrupoSanguineo()  != null){
                    texto.append(aipRegSanguineosBebe.getGrupoSanguineo()+aipRegSanguineosBebe.getFatorRh());
                }else{
                    texto.append("______");
                }
                texto.append(", CD: ");
                //Obter tipo os dados de sangue do bebe
                if(aipRegSanguineosBebe != null && aipRegSanguineosBebe.getCoombs() != null){
                    texto.append(aipRegSanguineosBebe.getCoombs().getDescricao());
                }else{
                    texto.append("______");
                }
                texto.append(" CAPURRO: ");
                List<McoIddGestCapurros> capurros =  this.getPerinatologiaFacade().listarIddGestCapurrosPorCodigoPaciente(recemNascido.getPaciente().getCodigo());
                if(capurros == null || capurros.isEmpty()){
                    texto.append("______");
                }else{
                    for (McoIddGestCapurros capurro : capurros){
                        texto.append(capurro.getIgSemanas() + " sem " + capurro.getIgDias() + " dias.");
                    }
                }
                if (examesFisicosRealizados == null || examesFisicosRealizados.isEmpty()){
                    texto.append(" _______ IG Peso na Alta:_______g");
                }else{
	                for(McoExameFisicoRns exameFisico : examesFisicosRealizados){
	                    if(exameFisico.getIgFinal() !=null){
	                        texto.append(" " +  Byte.toString(exameFisico.getIgFinal()) + " IG Peso na Alta:_______g ");
	                    }else{
	                        texto.append(" _______ IG Peso na Alta:_______g");
	                    }
	                }
                }
            }
        }else{
            texto.append("Apresentação __________  Líquido Amniótico __________   Bolsa Rota há ___________\n");
            texto.append("Evoluiu para ____________    às _________  do dia ______________________________\n");
            texto.append("RN do sexo:  ___________    Cor: ___________ ,  pesando ________ G ,  APGAR: ___/____\n");
            texto.append("Comprimento: _______ cm,  Perímetro Cefálico: _____ cm,Perímetro Torácico: _____ cm\n");
            texto.append("TS: ______  CD: ______ CAPURRO: ___________   _________ IG Peso na Alta:________g");
        }
        return texto.toString();
    }

    @SuppressWarnings("PMD")
    private String montaDadosGestacao(McoGestacoes mcoGestacoes){
        StringBuffer texto = new StringBuffer();
        if(mcoGestacoes.getGesta() != null && mcoGestacoes.getGesta() != 0){
            texto.append("G"+ mcoGestacoes.getGesta());
        }
        if(mcoGestacoes.getPara() != null && mcoGestacoes.getPara() != 0){
            texto.append("P" + Byte.toString(mcoGestacoes.getPara()));
        }
        if(mcoGestacoes.getCesarea() != null && mcoGestacoes.getCesarea() != 0){
            texto.append("C" + Byte.toString(mcoGestacoes.getCesarea()));
        }
        if(mcoGestacoes.getAborto() != null && mcoGestacoes.getAborto() != 0){
            texto.append("A" + Byte.toString(mcoGestacoes.getAborto()));
        }
        return texto.toString();
    }

	/**
	 * Inativa o sumário alta
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inativarAltaSumario(Integer atdSeq, Integer apaSeq, Short seqp, String nomeMicrocomputador) throws ApplicationBusinessException {

		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(atdSeq, apaSeq, seqp);

		if (altaSumario != null) {

			if (!altaSumario.getConcluido().equals(DominioIndConcluido.R)) {

				altaSumario.setConcluido(DominioIndConcluido.N);

			}

			altaSumario.setSituacao(DominioSituacao.I);
			altaSumario.setDthrFim(new Date());
			this.getManterAltaSumarioRN().atualizarAltaSumario(altaSumario, nomeMicrocomputador);

		}

	}

	public void removerAltaDiagMtvoInternacao(SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO) throws ApplicationBusinessException {
		MpmAltaDiagMtvoInternacaoDAO dao = this.getMpmAltaDiagMtvoInternacaoDAO();

		MpmAltaDiagMtvoInternacao diagMtvoInternacao = dao.obterAltaDiagMtvoInternacao(
				sumarioAltaDiagnosticosCidVO.getId().getApaAtdSeq()
				, sumarioAltaDiagnosticosCidVO.getId().getApaSeq()
				, sumarioAltaDiagnosticosCidVO.getId().getSeqp()
				, sumarioAltaDiagnosticosCidVO.getSeqp().byteValue()
		);
		diagMtvoInternacao.setIndSituacao(DominioSituacao.I);

		this.getManterAltaDiagMtvoInternacaoRN().atualizarAltaDiagMtvoInternacao(diagMtvoInternacao);

		//dao.desatachar(diagMtvoInternacao);
	}

	public void removerAltaDiagSecundario(SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO) throws ApplicationBusinessException{
		try {
			MpmAltaSumarioId asuId = sumarioAltaDiagnosticosCidVO.getId();

			Integer asuApaAtdSeq = asuId.getApaAtdSeq();
			Integer asuApaSeq = asuId.getApaSeq();
			Short asuSeqp = asuId.getSeqp();
			Short seqp = sumarioAltaDiagnosticosCidVO.getSeqp();

			MpmAltaDiagSecundarioId id = new MpmAltaDiagSecundarioId(asuApaAtdSeq, asuApaSeq, asuSeqp, seqp);
			MpmAltaDiagSecundarioDAO dao = this.getMpmAltaDiagSecundarioDAO();
			MpmAltaDiagSecundario diagSecundario = dao.obterPorChavePrimaria(id);
			diagSecundario.setIndSituacao(DominioSituacao.I);

			this.getManterAltaDiagSecundarioRN().atualizarAltaDiagSecundario(diagSecundario);
		} catch (ApplicationBusinessException e) {
			ManterAltaSumarioONExceptionCode.ERRO_REMOVER_ALTA_DIAG_SECUNDARIO.throwException();
		}
	}


	public void preinserirAltaDiagSecundario(SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO) throws ApplicationBusinessException{
		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(sumarioAltaDiagnosticosCidVO.getId().getApaAtdSeq(),sumarioAltaDiagnosticosCidVO.getId().getApaSeq(),sumarioAltaDiagnosticosCidVO.getId().getSeqp());
		if(altaSumario != null){
			MpmAltaDiagSecundario MpmAltaDiagSecundarioNovo = new MpmAltaDiagSecundario();
			MpmAltaDiagSecundarioId id = new MpmAltaDiagSecundarioId();
			id.setAsuApaAtdSeq(altaSumario.getId().getApaAtdSeq());
			id.setAsuApaSeq(altaSumario.getId().getApaSeq());
			id.setAsuSeqp(altaSumario.getId().getSeqp());

			MpmAltaDiagSecundarioNovo.setMpmAltaSumarios(altaSumario);

			this.getMpmAltaDiagSecundarioDAO().obterValorSequencialId(MpmAltaDiagSecundarioNovo);

			MpmAltaDiagSecundarioNovo.setId(id);

			MpmAltaDiagSecundarioNovo.setIndSituacao(DominioSituacao.A);

			if(sumarioAltaDiagnosticosCidVO.getOrigemListaCombo()){
				MpmAltaDiagSecundarioNovo.setIndCarga(true);
			}else{
				MpmAltaDiagSecundarioNovo.setIndCarga(false);
			}
			MpmAltaDiagSecundarioNovo.setDescCid(montaDescricaoCIDComPrimeiraMaiuscula(sumarioAltaDiagnosticosCidVO.getCid().getDescricao(),sumarioAltaDiagnosticosCidVO.getCid().getCodigo()));
			MpmAltaDiagSecundarioNovo.setCidSeq(sumarioAltaDiagnosticosCidVO.getCid());
			MpmAltaDiagSecundarioNovo.setComplCid(sumarioAltaDiagnosticosCidVO.getComplementoEditado() == null ? "" : StringUtil.trim(sumarioAltaDiagnosticosCidVO.getComplementoEditado()));
			if(sumarioAltaDiagnosticosCidVO.getCiaSeq()!= null){
				MpmCidAtendimento cidAtendimento = this.getMpmCidAtendimentoDAO().obterCidAtendimentoPeloId(sumarioAltaDiagnosticosCidVO.getCiaSeq()) ;
				if(cidAtendimento != null){
					MpmAltaDiagSecundarioNovo.setMpmCidAtendimentos(cidAtendimento);
				}
			}
			if(sumarioAltaDiagnosticosCidVO.getDiaSeq() != null){
				MamDiagnostico mamDiagnostico = this.getAmbulatorioFacade().obterDiagnosticoPorChavePrimaria(sumarioAltaDiagnosticosCidVO.getDiaSeq());
				if(mamDiagnostico != null){

					MpmAltaDiagSecundarioNovo.setDiaSeq(mamDiagnostico);
				}
			}

			verificarDiagnosticoSecundarioDuplicado(MpmAltaDiagSecundarioNovo, altaSumario);

			this.getManterAltaDiagSecundarioRN().inserirAltaDiagSecundario(MpmAltaDiagSecundarioNovo);
		}
	}

	/**
	 * Não permite inserir diagnóstico secundário já informado anteriormente
	 * http://redmine.mec.gov.br/issues/6900
	 * @param mpmAltaDiagSecundarioNovo
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	private void verificarDiagnosticoSecundarioDuplicado(MpmAltaDiagSecundario mpmAltaDiagSecundarioNovo, MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		List<MpmAltaDiagSecundario> listaDiagSecundarios = null;
		listaDiagSecundarios = getPrescricaoMedicaFacade().obterAltaDiagSecundario(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		if (listaDiagSecundarios != null) {
			for (MpmAltaDiagSecundario diagSec : listaDiagSecundarios) {
				if (mpmAltaDiagSecundarioNovo.getCidSeq().equals(diagSec.getCidSeq())) {
					throw new ApplicationBusinessException (ManterAltaSumarioONExceptionCode.DIAG_SEC_JA_INFORMADO);
				}
			}
		}
	}

	public void preAtualizarAltaDiagSecundario(SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO) throws ApplicationBusinessException{

		if( sumarioAltaDiagnosticosCidVO.getId() != null) {

			MpmAltaDiagSecundarioDAO dao = this.getMpmAltaDiagSecundarioDAO();

			MpmAltaSumarioId idAsu = sumarioAltaDiagnosticosCidVO.getId();
			Integer asuApaAtdSeq = idAsu.getApaAtdSeq();
			Integer asuApaSeq = idAsu.getApaSeq();
			Short asuSeqp = idAsu.getSeqp();
			Short seqp = sumarioAltaDiagnosticosCidVO.getSeqp();

			MpmAltaDiagSecundarioId idDSE = new MpmAltaDiagSecundarioId(asuApaAtdSeq, asuApaSeq, asuSeqp, seqp);

			MpmAltaDiagSecundario mpmAltaDiagSecundarioModificado = null;
			mpmAltaDiagSecundarioModificado = dao.obterPorChavePrimaria(idDSE);

			mpmAltaDiagSecundarioModificado.setComplCid(sumarioAltaDiagnosticosCidVO.getComplementoEditado() == null ? "" : StringUtil.trim(sumarioAltaDiagnosticosCidVO.getComplementoEditado()));
			mpmAltaDiagSecundarioModificado.setCidSeq(sumarioAltaDiagnosticosCidVO.getCid());
			mpmAltaDiagSecundarioModificado.setDescCid(montaDescricaoCIDComPrimeiraMaiuscula(sumarioAltaDiagnosticosCidVO.getCid().getDescricao(),sumarioAltaDiagnosticosCidVO.getCid().getCodigo()));

//			if (sumarioAltaDiagnosticosCidVO.getOrigemListaCombo()) { #6904
//				mpmAltaDiagSecundarioModificado.setIndCarga(true);
//			} else {
//				mpmAltaDiagSecundarioModificado.setIndCarga(false);
//			}
			if(sumarioAltaDiagnosticosCidVO.getCiaSeq()!= null){
				MpmCidAtendimento cidAtendimento = this.getMpmCidAtendimentoDAO().obterCidAtendimentoPeloId(sumarioAltaDiagnosticosCidVO.getCiaSeq()) ;
				if(cidAtendimento != null){
					mpmAltaDiagSecundarioModificado.setMpmCidAtendimentos(cidAtendimento);
				}
			}
			if(sumarioAltaDiagnosticosCidVO.getDiaSeq() != null){
				MamDiagnostico mamDiagnostico = this.getAmbulatorioFacade().obterDiagnosticoPorChavePrimaria(sumarioAltaDiagnosticosCidVO.getDiaSeq());
				if(mamDiagnostico != null){
					mpmAltaDiagSecundarioModificado.setDiaSeq(mamDiagnostico);
				}
			}
			this.getManterAltaDiagSecundarioRN().atualizarAltaDiagSecundario(mpmAltaDiagSecundarioModificado);
		}

	}

	private MpmAltaEstadoPacienteDAO getAltaEstadoPacienteDAO() {
		return mpmAltaEstadoPacienteDAO;
	}

	private MpmCidAtendimentoDAO getMpmCidAtendimentoDAO() {
		return mpmCidAtendimentoDAO;
	}

	private MpmObtCausaDiretaDAO getMpmObtCausaDiretaDAO() {
		return mpmObtCausaDiretaDAO;
	}

	private MpmObtCausaAntecedenteDAO getMpmObtCausaAntecedenteDAO() {
		return mpmObtCausaAntecedenteDAO;
	}

	private MpmObtOutraCausaDAO getMpmObtOutraCausaDAO() {
		return mpmObtOutraCausaDAO;
	}

	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected ManterAltaSumarioRN getManterAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected MpmAltaDiagPrincipalDAO getMpmAltaDiagPrincipalDAO(){
		return mpmAltaDiagPrincipalDAO;
	}

	protected MpmAltaDiagSecundarioDAO getMpmAltaDiagSecundarioDAO(){
		return mpmAltaDiagSecundarioDAO;
	}

	protected MpmAltaDiagMtvoInternacaoDAO getMpmAltaDiagMtvoInternacaoDAO(){
		return mpmAltaDiagMtvoInternacaoDAO;
	}

	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO(){
		return mpmAltaSumarioDAO;
	}

	protected ManterAltaOutraEquipeSumrON getManterAltaOutraEquipeSumrON() {
		return manterAltaOutraEquipeSumrON;
	}

	protected ManterAltaDiagMtvoInternacaoON getManterAltaDiagMtvoInternacaoON() {
		return manterAltaDiagMtvoInternacaoON;
	}

	protected ManterAltaDiagPrincipalON getManterAltaDiagPrincipalON() {
		return manterAltaDiagPrincipalON;
	}

	protected ManterAltaDiagSecundarioON getManterAltaDiagSecundarioON() {
		return manterAltaDiagSecundarioON;
	}

	protected ManterAltaCirgRealizadaON getManterAltaCirgRealizadaON() {
		return manterAltaCirgRealizadaON;
	}

	protected ManterAltaOtrProcedimentoON getManterAltaOtrProcedimentoON() {
		return manterAltaOtrProcedimentoON;
	}

	protected ManterAltaConsultoriaON getManterAltaConsultoriaON() {
		return manterAltaConsultoriaON;
	}

	protected ManterAltaPrincFarmacoON getManterAltaPrincFarmacoON() {
		return manterAltaPrincFarmacoON;
	}

	protected ManterAltaMotivoON getManterAltaMotivoON() {
		return manterAltaMotivoON;
	}

	protected ManterAltaEvolucaoON getManterAltaEvolucaoON() {
		return manterAltaEvolucaoON;
	}

	private ManterAltaEstadoPacienteRN getManterAltaEstadoPacienteRN() {
		return manterAltaEstadoPacienteRN;
	}

	private ManterAltaEvolucaoRN getManterAltaEvolucaoRN() {
		return manterAltaEvolucaoRN;
	}

	protected ManterAltaPlanoON getManterAltaPlanoON() {
		return manterAltaPlanoON;
	}

	protected ManterAltaPedidoExameON getManterAltaPedidoExameON() {
		return manterAltaPedidoExameON;
	}

	protected ManterAltaItemPedidoExameON getManterAltaItemPedidoExameON() {
		return manterAltaItemPedidoExameON;
	}

	protected ManterAltaReinternacaoON getManterAltaReinternacaoON() {
		return manterAltaReinternacaoON;
	}

	protected ManterAltaEstadoPacienteON getManterAltaEstadoPacienteON() {
		return manterAltaEstadoPacienteON;
	}

	protected ManterAltaRecomendacaoON getManterAltaRecomendacaoON() {
		return manterAltaRecomendacaoON;
	}

	protected ManterAltaComplFarmacoON getManterAltaComplFarmacoON() {
		return manterAltaComplFarmacoON;
	}

	//ManterAltaDiagPrincipalRN

	protected ManterAltaDiagMtvoInternacaoRN getManterAltaDiagMtvoInternacaoRN() {
		return manterAltaDiagMtvoInternacaoRN;
	}

	private ManterAltaDiagSecundarioRN getManterAltaDiagSecundarioRN() {
		return manterAltaDiagSecundarioRN;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	private MpmAltaCirgRealizadaDAO getMpmAltaCirgRealizadaDAO() {
		return mpmAltaCirgRealizadaDAO;
	}

	protected MpmPrescricaoProcedimentoDAO getPrescricaoProcedimentoDAO() {
		return mpmPrescricaoProcedimentoDAO;
	}

	protected MpmAltaOtrProcedimentoDAO getAltaOtrProcedimentoDAO() {
		return mpmAltaOtrProcedimentoDAO;
	}

	protected MpmPrescricaoNptDAO getPrescricaoNptDAO() {
		return mpmPrescricaoNptDAO;
	}


	private MpmAltaConsultoriaDAO getMpmAltaConsultoriaDAO() {
		return mpmAltaConsultoriaDAO;
	}

	private MpmSolicitacaoConsultoriaDAO getMpmSolicitacaoConsultoriaDAO() {
		return mpmSolicitacaoConsultoriaDAO;
	}

	private ManterAltaComplFarmacoRN getManterAltaComplFarmacoRN() {
		return manterAltaComplFarmacoRN;
	}

	private ManterAltaPrincFarmacoRN getManterAltaPrincFarmacoRN() {
		return manterAltaPrincFarmacoRN;
	}

	private ManterObtCausaDiretaON getManterObtCausaDiretaON() {
		return manterObtCausaDiretaON;
	}

	private ManterObtCausaAntecedenteON getManterObtCausaAntecedenteON() {
		return manterObtCausaAntecedenteON;
	}

	private ManterObtOutraCausaON getManterObtOutraCausaON() {
		return manterObtOutraCausaON;
	}

	private ManterObitoNecropsiaON getManterObitoNecropsiaON() {
		return manterObitoNecropsiaON;
	}

	private ManterObtGravidezAnteriorON getManterObtGravidezAnteriorON() {
		return manterObtGravidezAnteriorON;
	}


	public MpmAltaComplFarmaco getMpmAltaComplFarmaco(MpmAltaSumarioId id) throws ApplicationBusinessException {
		if(id == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}

		MpmAltaComplFarmacoDAO daoComplFarmaco = getMpmAltaComplFarmacoDAO();

		return daoComplFarmaco.obterMpmAltaComplFarmaco(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}

	/**
	 * Retorna uma lista de informações 
	 * complementares.
	 * 
	 * @author gfmenezes
	 * 
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<AltaSumarioInfoComplVO> findListaInformacaoComplementar(MpmAltaSumarioId id) throws ApplicationBusinessException {
		if (id == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		MpmAltaSumario sumarioAlta = this.getMpmAltaSumarioDAO().obterPorChavePrimaria(id);

		List<AltaSumarioInfoComplVO> list = new LinkedList<AltaSumarioInfoComplVO>();
		MpmAltaPrincFarmacoDAO daoFarmaco = this.getMpmAltaPrincFarmacoDAO();

		MpmAltaComplFarmacoDAO daoComplFarmaco = this.getMpmAltaComplFarmacoDAO();
		MpmAltaComplFarmaco altaComplFarmaco = daoComplFarmaco.obterAltaComplempentoFarmacoPorSumarioAlta(sumarioAlta);

		List<MpmAltaPrincFarmaco> listFarmacos = daoFarmaco.obterListaAltaPrincFarmacoPeloAltaSumarioId(id);
		for (MpmAltaPrincFarmaco altaPrincFarmaco : listFarmacos) {

			if (!altaPrincFarmaco.getIndCarga()) {

				AltaSumarioInfoComplVO vo = new AltaSumarioInfoComplVO();

				vo.setIdAltaSumario(id);
				vo.setIdAltaPrincFarmaco(altaPrincFarmaco.getId());

				if (altaComplFarmaco != null) {
					vo.setIdAltaComplFarmaco(altaComplFarmaco.getId());
					vo.setComplemento(altaComplFarmaco.getDescricao());
				}
				vo.setDescricaoMedicamento(altaPrincFarmaco.getDescMedicamento());

				if (altaPrincFarmaco.getMedicamento() != null) {
					vo.setMedicamento(altaPrincFarmaco.getMedicamento());

					VAfaDescrMdtoId idVafaDescrMdto = new VAfaDescrMdtoId();
					idVafaDescrMdto.setMatCodigo(altaPrincFarmaco.getMedicamento().getMatCodigo());
					idVafaDescrMdto.setDescricaoMat(altaPrincFarmaco.getMedicamento().getDescricaoEditada());

					VAfaDescrMdto viewAfaMdto = new VAfaDescrMdto(idVafaDescrMdto);
					viewAfaMdto.setMedicamento(altaPrincFarmaco.getMedicamento());

					vo.setDescricaoMedicamento(altaPrincFarmaco.getMedicamento().getDescricaoEditada());

					vo.setvAfaDescrMdto(viewAfaMdto);
				}

				vo.setRadioInformacoesComplementares((altaPrincFarmaco.getMedicamento() != null) ? DominioOutrosFarmacos.CADASTRADO : DominioOutrosFarmacos.NAO_CADASTRADO);

				list.add(vo);

			}

		}

		return list;
	}

	private MpmAltaEvolucaoDAO getAltaEvolucaoDAO() {
		return mpmAltaEvolucaoDAO;
	}

	private MpmAltaComplFarmacoDAO getMpmAltaComplFarmacoDAO() {
		return mpmAltaComplFarmacoDAO;
	}

	private MpmAltaPrincFarmacoDAO getMpmAltaPrincFarmacoDAO() {
		return mpmAltaPrincFarmacoDAO;
	}

	private MpmPrescricaoMdtoDAO getPrescricaoMdtoDAO() {
		return mpmPrescricaoMdtoDAO;
	}	

	/**
	 * Grava ou altera uma informação complementar.
	 * 
	 * @author gfmenezes
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	public void gravarAltaSumarioInformacaoComplementar(String complemento, AltaSumarioInfoComplVO vo) throws ApplicationBusinessException {
		verificarGravacaoAltaInformacaoComplementar(vo);

		if (StringUtils.isNotBlank(complemento)) {
			gravarAltaComplementoFarmaco(complemento, vo);
		}

		if (StringUtils.isNotBlank(vo.getDescricaoMedicamento()) || vo.getvAfaDescrMdto() != null) {
			if(vo.getIdAltaPrincFarmaco() == null) {
				insertInformacaoComplementar(vo);
			} else {
				alterarInformacaoComplementar(vo);
			}
		}
	}

	/**
	 * Insere ou atualiza registros na tabela
	 * MPM_ALTA_COMPL_FARMACOS.
	 * 
	 * @author gfmenezes
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	private void gravarAltaComplementoFarmaco(String complemento, AltaSumarioInfoComplVO vo) throws ApplicationBusinessException {

		MpmAltaComplFarmaco mpmAltaComplFarmaco = getMpmAltaComplFarmacoDAO().obterPorChavePrimaria(vo.getIdAltaSumario());

		if(mpmAltaComplFarmaco == null) {
			MpmAltaSumario sumarioAlta = this.getMpmAltaSumarioDAO().obterPorChavePrimaria(vo.getIdAltaSumario());
			mpmAltaComplFarmaco = new MpmAltaComplFarmaco();
			mpmAltaComplFarmaco.setAltaSumario(sumarioAlta);
			mpmAltaComplFarmaco.setDescricao(complemento);
			this.getManterAltaComplFarmacoRN().inserirAltaComplFarmaco(mpmAltaComplFarmaco);
		} else {
			mpmAltaComplFarmaco.setDescricao(complemento);
			this.getManterAltaComplFarmacoRN().atualizarAltaComplFarmaco(mpmAltaComplFarmaco);
		}

	}

	/**
	 * Verifica se o alta sumário
	 * informado é válido.
	 * 
	 * @author gfmenezes
	 * 
	 * @param vo
	 */
	private void verificarGravacaoAltaInformacaoComplementar(AltaSumarioInfoComplVO vo) {
		if(vo.getIdAltaSumario() == null) {
			throw new IllegalArgumentException("Id Alta Sumario eh Invalido!");
		}
	}

	/**
	 * Atualiza um registro na tabela MPM_ALTA_PRINC_FARMACOS
	 * 
	 * @author gfmenezes
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	private void alterarInformacaoComplementar(AltaSumarioInfoComplVO vo) throws ApplicationBusinessException {
		MpmAltaPrincFarmaco mpmPrincFarmaco = this.getMpmAltaPrincFarmacoDAO().obterPorChavePrimaria(vo.getIdAltaPrincFarmaco());

		mpmPrincFarmaco.setDescMedicamento(CoreUtil.capitalizaTextoFormatoAghu(vo.getDescricaoMedicamento()));

		if(vo.getvAfaDescrMdto() != null && vo.getvAfaDescrMdto().getMedicamento() != null){
			mpmPrincFarmaco.setMedicamento(vo.getvAfaDescrMdto().getMedicamento());
			mpmPrincFarmaco.setDescMedicamento(colocarMaiusculoComecoEMinusculoResto(vo.getvAfaDescrMdto().getMedicamento().getDescricaoEditada()));
		}

		this.getManterAltaPrincFarmacoRN().atualizarAltaPrincFarmaco(mpmPrincFarmaco);
	}

	private String colocarMaiusculoComecoEMinusculoResto(
			String descricao) {
		String retorno = "";
		if(descricao != null && StringUtils.isNotBlank(descricao)) {
			retorno = descricao.substring(0,1).toUpperCase().concat(descricao.substring(1, descricao.length()).toLowerCase());
		}
		return retorno;
	}

	/**
	 * Insere um novo registro na tabela MPM_ALTA_PRINC_FARMACOS.
	 * 
	 * @author gfmenezes
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	private void insertInformacaoComplementar(AltaSumarioInfoComplVO vo) throws ApplicationBusinessException {
		MpmAltaSumario sumarioAlta = this.getMpmAltaSumarioDAO().obterPorChavePrimaria(vo.getIdAltaSumario());

		MpmAltaPrincFarmaco altaPrincFarmaco = vo.getModelMpmAltaPrincFarmaco();
		altaPrincFarmaco.setDescMedicamento(CoreUtil.capitalizaTextoFormatoAghu(altaPrincFarmaco.getDescMedicamento()));
		altaPrincFarmaco.setAltaSumario(sumarioAlta);

		vo.setDescricaoMedicamento(colocarMaiusculoComecoEMinusculoResto(vo.getDescricaoMedicamento()));
		altaPrincFarmaco.setDescMedicamento(colocarMaiusculoComecoEMinusculoResto(altaPrincFarmaco.getDescMedicamento()));

		this.getManterAltaPrincFarmacoRN().inserirAltaPrincFarmaco(altaPrincFarmaco);
	}

	/**
	 * Remove um registro na tabela MPM_ALTA_PRINC_FARMACOS.
	 * obs: Faz uma remoção lógica setando o indSituacao para
	 * inativo.
	 * 
	 * @author gfmenezes
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaSumarioInformacaoComplementar(AltaSumarioInfoComplVO vo) throws ApplicationBusinessException {
		MpmAltaPrincFarmaco mpmPrincFarmaco = this.getMpmAltaPrincFarmacoDAO().obterPorChavePrimaria(vo.getIdAltaPrincFarmaco());
		mpmPrincFarmaco.setIndSituacao(DominioSituacao.I);

		this.getManterAltaPrincFarmacoRN().atualizarAltaPrincFarmaco(mpmPrincFarmaco);
	}

	/**
	 * Método utilitário para as seguintes operações sobre à descrição de uma CID:
	 * Capitular a descrição de um CID
	 * Acrescentar o código da CID no final da descrição capitulada
	 * @param descricao
	 * @param codigo
	 * @return
	 */
	public static String montaDescricaoCIDComPrimeiraMaiuscula(String descricao, String codigo){
		if(StringUtils.isNotBlank(descricao)){
			descricao = descricao.substring(0,1).toUpperCase().concat(descricao.substring(1, descricao.length()).toLowerCase());
			if(StringUtils.isNotBlank(codigo)){
				descricao += " (" + codigo.toUpperCase() + ")";
			}
			return descricao;
		}
		return null;
	}

	/**
	 * ORADB PROCEDURE MPMP_CANCELA_SUMARIO
	 * Tem por finalidade excluir os filhos e o mpm_alta_sumario
	 * @param altaSumario
	 * @throws BaseException
	 */
	public void cancelarSumario(MpmAltaSumario altaSumario, String nomeMicrocomputador) throws BaseException {

		getManterAltaSumarioRN().cancelarSumario(altaSumario, nomeMicrocomputador);

	}

	protected IPacienteFacade getPacienteFacade() {
		return (IPacienteFacade) pacienteFacade; 
	}
	
	protected IParametroFacade getParametroFacade() {
		return (IParametroFacade) parametroFacade; 
	}

	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}


}