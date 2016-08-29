package br.gov.mec.aghu.ambulatorio.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.MarcacaoConsultaRN.MarcacaoConsultaRNExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacRetornosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAtestadosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemReceituarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLogEmUsosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoRealizadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSituacaoAtendimentosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoAtestadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemEvolucaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.vo.PesquisarConsultasPendentesVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAtestadoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaractEspecialidadesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioAnamneseEvolucao;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.DominioEscopoAtendimento;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoControle;
import br.gov.mec.aghu.dominio.DominioTipoNotaAdicional;
import br.gov.mec.aghu.dominio.DominioTipoPerfilAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoRarPrograma;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.emergencia.dao.MamEmgServEspecialidadeDAO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamEmgServEspecialidade;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamLogEmUsos;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RarCandidatoPrograma;
import br.gov.mec.aghu.model.RarCandidatos;
import br.gov.mec.aghu.model.RarPrograma;
import br.gov.mec.aghu.model.RarProgramaDuracao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;

@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength",
		"PMD.CyclomaticComplexity", "PMD.CouplingBetweenObjects" })
@Stateless
public class AtendimentoPacientesAgendadosRN extends BaseBusiness {

	private static final String TITULO_MASCULINO = "tituloMasculino";

	private static final String TITULO_FEMININO = "tituloFeminino";

	private static final String _HIFEN_ = " - ";

	@EJB
	private AmbulatorioConsultaRN ambulatorioConsultaRN;
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	@EJB
	private MamLembreteRN mamLembreteRN;

	private static final Log LOG = LogFactory
			.getLog(AtendimentoPacientesAgendadosRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;

	@Inject
	private RapServidoresDAO rapServidoresDAO;

	@Inject
	private MamTipoItemAnamnesesDAO mamTipoItemAnamnesesDAO;

	@Inject
	private MamItemEvolucoesDAO mamItemEvolucoesDAO;

	@Inject
	private MamEmgServEspecialidadeDAO mamEmgServEspecialidadeDAO;

	@EJB
	private IExamesFacade examesFacade;

	@Inject
	private MamReceituariosDAO mamReceituariosDAO;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@Inject
	private MamItemReceituarioDAO mamItemReceituarioDAO;

	@Inject
	private MamNotaAdicionalEvolucoesDAO mamNotaAdicionalEvolucoesDAO;

	@EJB
	private IAdministracaoFacade administracaoFacade;

	@Inject
	private MamLogEmUsosDAO mamLogEmUsosDAO;

	@Inject
	private MamControlesDAO mamControlesDAO;
	
	@Inject
	MamTipoAtestadoDAO mamTipoAtestadoDao;
	//MamTipoAtestadoDao ;
	
	@Inject
	MamAtestadosDAO mamAtestadosDAO;
	
	@Inject
	private AghCaractEspecialidadesDAO caractEspecialidadesDAO;

	@Inject
	private MamItemAnamnesesDAO mamItemAnamnesesDAO;

	@Inject
	private MamTipoItemEvolucaoDAO mamTipoItemEvolucaoDAO;

	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;

	@Inject
	private MamSituacaoAtendimentosDAO mamSituacaoAtendimentosDAO;

	@Inject
	private AacConsultasDAO aacConsultasDAO;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private MamProcedimentoRealizadoDAO mamProcedimentoRealizadoDAO;

	@Inject
	private MamNotaAdicionalAnamnesesDAO mamNotaAdicionalAnamnesesDAO;
	
	@Inject
	private AacRetornosDAO aacRetornosDAO;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;

	@Inject
	private AacGradeAgendamenConsultasDAO 	aacGradeAgendamenConsultasDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ConsultasON consultasON;

	@EJB
	private ManterSumarioAltaReceitasON manterSumarioAltaReceitasON;

	@EJB
	private ManterReceituarioRN manterReceituarioRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7586175778573067633L;
	private static final String NEWLINE = System.getProperty("line.separator");
	private static final String SEPARATIONLINE = "---------------------------------------------";
	private static final String TITULONOTASADICIONAIS = "Nota(s) adicional(is): ";
	
	public enum AtendimentoPacientesAgendadosRNExceptionCode implements
			BusinessExceptionCode {
		MAM_01889, MAM_01891, MAM_01816, MAM_04133, MAM_01893, MAM_01894, MAM_01895, CONSULTA_NAO_PERMITE_TECNICO, MAM_02672, MAM_02670, MAM_02671, MAM_02673, MAM_03803, MAM_01887,MAM_01888,MAM_01890,MAM_03802;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	private Boolean verificarMesmaEvolucao(AacConsultas consulta,
			MamEvolucoes evolucao) {
		Boolean retorno = false;
		MamEvolucoes evolucaoAtual = getMamEvolucoesDAO()
				.obterEvolucaoAtivaPorNumeroConsulta(consulta.getNumero());
		if (evolucaoAtual != null && evolucao != null
				&& evolucaoAtual.getSeq().equals(evolucao.getSeq())) {
			retorno = true;
		}
		return retorno;
	}

	private void removeEvolucaoItemEvolucao(MamEvolucoes evolucao)
			throws ApplicationBusinessException {
		for (MamItemEvolucoes itemEvolucao : getMamItemEvolucoesDAO()
				.pesquisarItemEvolucoesPorEvolucao(evolucao.getSeq())) {
			getMarcacaoConsultaRN().removerItemEvolucao(itemEvolucao);
		}
		getMarcacaoConsultaRN().removerEvolucao(evolucao);
	}

	private MamEvolucoes atualizaEvolucao(MamEvolucoes evolucao,
			RapServidores servidor) throws ApplicationBusinessException,
			ApplicationBusinessException {
		evolucao.setPendente(DominioIndPendenteAmbulatorio.E);
		evolucao.setDthrMvto(new Date());
		evolucao.setServidorMvto(servidor);
		return evolucao;
	}

	/**
	 * ORADB: PROCEDURE MAMP_EXCLUI_EVOLUCAO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void excluiEvolucao(AacConsultas consulta, MamEvolucoes evolucao) throws ApplicationBusinessException {

		consulta = getAacConsultasDAO().merge(consulta);
		evolucao = getMamEvolucoesDAO().merge(evolucao);
		
		
		RapServidores servidor = getServidorLogadoFacade().obterServidorLogado();
		

		// EXCLUI AS TEMPORÁRIAS DA EVOLUÇÃO USADAS NOS GRÁFICOS
		if (verificarMesmaEvolucao(consulta, evolucao)
				&& verificaPendenteRPV(evolucao.getPendente())) {
			if (verificaPendenteRP(evolucao.getPendente())) {
				if (evolucao.getEvolucao() != null) {
					MamEvolucoes evolucaoPai = evolucao.getEvolucao();
					evolucaoPai.setPendente(DominioIndPendenteAmbulatorio.E);
					evolucaoPai.setDthrMvto(new Date());
					evolucaoPai.setServidorMvto(servidor);
					getMamEvolucoesDAO().atualizar(evolucaoPai);
					getMamEvolucoesDAO().flush();
				}
				removeEvolucaoItemEvolucao(evolucao);
			} else {
				atualizaEvolucao(evolucao, servidor);
				getMamEvolucoesDAO().atualizar(evolucao);
				getMamEvolucoesDAO().flush();
			}
		}
		for (MamNotaAdicionalEvolucoes notaAdicional : consulta
				.getNotasAdicionaisEvolucoes()) {
			excluiNotaAdicionalEvolucao(notaAdicional);
		}
		// TODO NÃO SERÁ USADO POR ENQUANTO
		// for (MamRespostaEvolucoes respostaEvolucao :
		// evolucao.getRespostasEvolucoes()){
		// getMarcacaoConsultaRN().removerRespostaEvolucao(respostaEvolucao);
		// }
	}

	// Verifica se os dois objetos referenciam a mesma anamnese
	private Boolean verificarMesmaAnamnese(AacConsultas consulta,
			MamAnamneses anamnese) {
		Boolean retorno = false;
		MamAnamneses anamneseAtual = getMamAnamnesesDAO()
				.obterAnamneseAtivaPorNumeroConsulta(consulta.getNumero());
		if (anamneseAtual != null && anamnese != null
				&& anamneseAtual.getSeq().equals(anamnese.getSeq())) {
			retorno = true;
		}
		return retorno;
	}

	private void removeAnamneseItemAnamnese(MamAnamneses anamnese)
			throws ApplicationBusinessException {
		for (MamItemAnamneses itemAnamnese : getMamItemAnamnesesDAO()
				.pesquisarItemAnamnesesPorAnamneses(anamnese.getSeq())) {
			getMarcacaoConsultaRN().removerItemAnamnese(itemAnamnese);
		}
		getMarcacaoConsultaRN().removerAnamnese(anamnese);
	}

	private MamAnamneses atualizaAnamnese(MamAnamneses anamnese,
			RapServidores servidor) throws ApplicationBusinessException,
			ApplicationBusinessException {
		anamnese.setPendente(DominioIndPendenteAmbulatorio.E);
		anamnese.setDthrMvto(new Date());
		anamnese.setServidorMvto(servidor);
		return anamnese;
	}

	/**
	 * ORADB: PROCEDURE MAMP_EXCLUI_ANAMNESE
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void excluiAnamese(AacConsultas consulta, MamAnamneses anamnese) throws ApplicationBusinessException {
		consulta = getAacConsultasDAO().obterPorChavePrimaria(consulta.getNumero());
		anamnese = getMamAnamnesesDAO().merge(anamnese);
		
		RapServidores servidor = getServidorLogadoFacade().obterServidorLogado();
		

		// EXCLUI AS TEMPORÁRIAS DA ANAMNESE USADAS NOS GRÁFICOS
		if (verificarMesmaAnamnese(consulta, anamnese)
				&& verificaPendenteRPV(anamnese.getPendente())) {
			if (verificaPendenteRP(anamnese.getPendente())) {
				if (anamnese.getAnamnese() != null) {
					MamAnamneses anamnesePai = anamnese.getAnamnese();
					anamnesePai.setPendente(DominioIndPendenteAmbulatorio.E);
					anamnesePai.setDthrMvto(new Date());
					anamnesePai.setServidorMvto(servidor);
					getMamAnamnesesDAO().atualizar(anamnesePai);
					getMamAnamnesesDAO().flush();
				}
				removeAnamneseItemAnamnese(anamnese);
			} else {
				atualizaAnamnese(anamnese, servidor);
				getMamAnamnesesDAO().atualizar(anamnese);
				getMamAnamnesesDAO().flush();
			}
		}
		for (MamNotaAdicionalAnamneses notaAdicional : consulta
				.getNotasAdicionaisAnamneses()) {
			excluiNotaAdicionalAnamnese(notaAdicional);
		}
	}

	// verifica se o dominio pendente está entre os valores R ou P ou V
	protected Boolean verificaPendenteRPV(DominioIndPendenteAmbulatorio pendente) {
		if (pendente.equals(DominioIndPendenteAmbulatorio.R)
				|| pendente.equals(DominioIndPendenteAmbulatorio.P)
				|| pendente.equals(DominioIndPendenteAmbulatorio.V)) {
			return true;
		}
		return false;
	}

	// verifica se o dominio pendente está entre os valores R ou P
	protected Boolean verificaPendenteRP(DominioIndPendenteAmbulatorio pendente) {
		if (pendente.equals(DominioIndPendenteAmbulatorio.R)
				|| pendente.equals(DominioIndPendenteAmbulatorio.P)) {
			return true;
		}
		return false;
	}

	/**
	 * ORADB: MAMK_GENERICA.MAMC_BUSCA_CONS_ANT
	 * 
	 */
	public AacConsultas buscaConsultaAnterior(AacConsultas consulta)
			throws ApplicationBusinessException {
		AacConsultas consultaAnterior = null;
		List<AacConsultas> consultas = getAacConsultasDAO()
				.pesquisarConsultaAnterior(
						consulta,
						consulta.getGradeAgendamenConsulta().getEspecialidade()
								.getSeq(), false);
		if (consultas != null && !consultas.isEmpty()) {
			consultaAnterior = consultas.get(0);
		}
		// se não achar consulta na especialidade busca simplesmente a consulta
		// anterior
		if (consultaAnterior == null) {
			consultas = getAacConsultasDAO().pesquisarConsultaAnterior(
					consulta, null, false);

			if (consultas != null && !consultas.isEmpty()) {
				consultaAnterior = consultas.get(0);
			}
		}
		return consultaAnterior;
	}

	public String verificaPossuiAnaEvoRet(MamAnamneses anamnese,
			List<MamEvolucoes> evolucao, AacRetornos retorno) {
		String resposta = null;
		retorno = aacRetornosDAO.obterPorChavePrimaria(retorno.getSeq());
		if (anamnese == null && (evolucao == null || evolucao.isEmpty())) {
			if (retorno == null) {
				resposta = "Consulta não realizada.";
			} else if(retorno.getAbsenteismo() != null){
				if(!retorno.getAbsenteismo().equals(DominioIndAbsenteismo.R)){
					resposta = "Consulta não realizada.";
				}else{
					resposta = "Não foi registrada Anamnese ou Evolução desta consulta, consulte o prontuário.";
				}
			}else {
				resposta = "Não foi registrada Anamnese ou Evolução desta consulta, consulte o prontuário.";
			}
		}
		return resposta;
	}

	/**
	 * ORADB: mamk_visualizacao.mamc_get_dados_con
	 * 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public StringBuilder obterDadosBasicosConsulta(AacConsultas consulta) throws ApplicationBusinessException,
			ApplicationBusinessException {

		StringBuilder dadosConsulta = new StringBuilder();

		StringBuffer equipeNome = new StringBuffer();
		String agendaNome = null;

		Object[] objeto = getPrescricaoFacade().buscaConsProf(
				consulta.getGradeAgendamenConsulta().getEquipe()
						.getProfissionalResponsavel());

		if (objeto[1] != null) {
			equipeNome.append(WordUtils.capitalizeFully((String) objeto[1]));
			equipeNome.insert(0, "Equipe: ");
		}

		if (consulta.getGradeAgendamenConsulta().getProfEspecialidade() != null) {
			objeto = getPrescricaoFacade().buscaConsProf(
					consulta.getGradeAgendamenConsulta().getProfEspecialidade()
							.getRapServidor());
		}

		if (objeto[1] != null) {
			agendaNome = WordUtils.capitalizeFully((String) objeto[1]);
		}

		dadosConsulta.append(consulta.getNumero())
		.append(_HIFEN_)
		.append(DateUtil.dataToString(consulta.getDtConsulta(),
				"dd/MM/yyyy"))
		.append(_HIFEN_)
		.append(WordUtils.capitalizeFully(consulta
				.getGradeAgendamenConsulta().getEspecialidade()
				.getNomeEspecialidade()))
		.append(_HIFEN_)
		.append(equipeNome);
		if (agendaNome != null) {
			dadosConsulta.append(_HIFEN_);
			dadosConsulta.append(agendaNome);
		}

		List<MamEvolucoes> evolucaoAtual = getMamEvolucoesDAO()
				.pesquisarEvolucoesAtivaPorNumeroConsulta(consulta.getNumero());
		MamAnamneses anamneseAtual = getMamAnamnesesDAO()
				.obterAnamneseAtivaPorNumeroConsulta(consulta.getNumero());

		String naoPossui = verificaPossuiAnaEvoRet(anamneseAtual,
				evolucaoAtual, consulta.getRetorno());

		if (naoPossui != null) {
			dadosConsulta.append(NEWLINE);
			dadosConsulta.append(naoPossui);
		}
		return dadosConsulta;
	}

	public StringBuilder adicionaItemDescricao(String descricao, Boolean naoInf) {
		StringBuilder textoLivre = new StringBuilder();
		if (descricao != null && descricao.length() > 0) {
			textoLivre.append(NEWLINE);
			textoLivre.append(descricao);
		} else if (naoInf) {
			textoLivre.append(NEWLINE);
			textoLivre.append("Não informado.");
		}
		return textoLivre;
	}

	private DominioEscopoAtendimento obterEscopoAtendimento(MamTriagens trg,
			AghAtendimentos atd, MamRegistro rgt) {
		DominioEscopoAtendimento escopo = null;
		if (atd != null && rgt != null) {
			escopo = DominioEscopoAtendimento.N3;
		} else if (trg != null && rgt != null) {
			escopo = DominioEscopoAtendimento.N2;
		} else {
			escopo = DominioEscopoAtendimento.N1;
		}
		return escopo;
	}

	private Boolean verificarFoiAssinado(RapServidores servidorValida,
			Date dthrValida) {
		return servidorValida != null && dthrValida != null;
	}

	// retorna se o item foi assinado e validado pelo mesmo autor
	private Boolean verificarMesmoAutor(RapServidores servidor,
			RapServidores servidorValida, Boolean isAssinado) {
		return isAssinado && servidor.equals(servidorValida);
	}

	@SuppressWarnings("rawtypes")
	private Boolean verificaTipoAssinatura(RapServidores servidor,
			DominioTipoPerfilAtendimento tipo)
			throws ApplicationBusinessException {

		Boolean possuiMed01 = Boolean.FALSE;
		Boolean possuiEsp03 = Boolean.FALSE;
		Boolean possuiEstagio = Boolean.FALSE;

		Set perfis = new HashSet();
		try {
			if (StringUtils.isNotBlank(servidor.getUsuario())){
				perfis = cascaFacade.obterNomePerfisPorUsuario(servidor.getUsuario());
			}
		} catch (Exception e) {
			logError(e);
			return false;
		}
		String perfilUsuario = "";

		for (Object obj : perfis) {

			if (obj instanceof String) {
				perfilUsuario = (String) obj;
			}
			if (obj instanceof Perfil) {
				Perfil p = (Perfil) obj;
				if (p != null) {
					perfilUsuario = p.getNome();
				}
			}
			if ("MED01".equalsIgnoreCase(perfilUsuario)) {
				possuiMed01 = Boolean.TRUE;
			}
			if ("ESP03".equalsIgnoreCase(perfilUsuario)) {
				possuiEsp03 = Boolean.TRUE;
			}
			if ("MED02".equalsIgnoreCase(perfilUsuario)
					|| "ENF02".equalsIgnoreCase(perfilUsuario)) {
				possuiEstagio = Boolean.TRUE;
			}
		}
		return definePerfilCorreto(possuiMed01, possuiEsp03, possuiEstagio,
				tipo);
	}

	/**
	 * 
	 * @param possuiMed01
	 * @param possuiEsp03
	 * @param possuiEstagio
	 * @param tipo
	 * @return
	 */
	private boolean definePerfilCorreto(boolean possuiMed01,
			boolean possuiEsp03, boolean possuiEstagio,
			DominioTipoPerfilAtendimento tipo) {
		boolean perfilCorreto = Boolean.FALSE;
		if (possuiMed01 && possuiEsp03
				&& tipo.equals(DominioTipoPerfilAtendimento.N1)) {
			perfilCorreto = Boolean.TRUE;
		}
		if (possuiEstagio && tipo.equals(DominioTipoPerfilAtendimento.N3)) {
			perfilCorreto = Boolean.TRUE;
		}
		return perfilCorreto;
	}

	/**
	 * ORADB:MAMK_ASSINATURA.MAMC_ASS_GET_LOTACAO
	 * 
	 * Obtem a melhor descrição para o centro de custo
	 */
	private String obterDescCentroCusto(RapServidores servidor) {
		String retorno = null;
		if (servidor.getCentroCustoLotacao() != null) {
			retorno = servidor.getCentroCustoLotacao().getDescricao();
		} else if (servidor.getCentroCustoAtuacao() != null) {
			retorno = servidor.getCentroCustoAtuacao().getDescricao();
		}
		return retorno;
	}

	private Map<String, String> obterQualificacoesServidor(
			RapServidores servidor) {
		Map<String, String> map = new HashMap<String, String>();
		List<RapQualificacao> list = getRegistroColaboradorFacade()
				.pesquisarQualificacoes(servidor.getPessoaFisica());
		for (RapQualificacao obj : list) {
			if (obj.getTipoQualificacao().getConselhoProfissional() != null
					&& obj.getTipoQualificacao().getConselhoProfissional()
							.getIndSituacao().equals(DominioSituacao.A)) {
				if (obj.getTipoQualificacao().getConselhoProfissional()
						.getTituloFeminino() != null) {
					map.put(TITULO_FEMININO, obj.getTipoQualificacao()
							.getConselhoProfissional().getTituloFeminino());
				}
				if (obj.getTipoQualificacao().getConselhoProfissional()
						.getTituloMasculino() != null) {
					map.put(TITULO_MASCULINO, obj.getTipoQualificacao()
							.getConselhoProfissional().getTituloMasculino());
				}
				if (obj.getNroRegConselho() != null) {
					map.put("numeroRegConselho", obj.getNroRegConselho());
				}
				if (obj.getTipoQualificacao().getConselhoProfissional().getSigla() != null) {
					map.put("siglaConselho", obj.getTipoQualificacao().getConselhoProfissional().getSigla());
				}
			}
			if (map.containsKey(TITULO_FEMININO)
					&& map.containsKey(TITULO_MASCULINO)
					&& map.containsKey("numeroRegConselho")
					&& map.containsKey("siglaConselho")) {
				break;
			}
		}

		return map;
	}

	/**
	 * ORADB:MAMK_ASSINATURA.MAMC_ASS_TEXTO_USER
	 * 
	 * Monta o texto padrão para o usuário responsável
	 * 
	 * 
	 * 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private String obterTextoPadraoUsuario(DominioEscopoAtendimento escopo,
			RapServidores servidor, Date dthrCriacao, boolean isEvoluacao)
			throws ApplicationBusinessException {

		StringBuffer retorno = new StringBuffer(128);

		Map<String, String> qualificacao = obterQualificacoesServidor(servidor);

		String titulo = "";
		String siglaConselho = qualificacao.get("siglaConselho");
		String numeroRegConselho = qualificacao.get("numeroRegConselho");

		if (servidor.getPessoaFisica().getSexo().equals(DominioSexo.F)
				&& StringUtils.isNotEmpty(qualificacao.get(TITULO_FEMININO))) {
			titulo = qualificacao.get(TITULO_FEMININO);
		} else if (StringUtils.isNotEmpty(qualificacao.get(TITULO_MASCULINO))) {
			titulo = qualificacao.get(TITULO_MASCULINO);
		}

		TipoVinculo tipoVinculo = buscaTipoVinculo(servidor);
		
		if (tipoVinculo != null) {
			switch (tipoVinculo) {
			case PROFESSOR:
				if (servidor.getPessoaFisica().getSexo().equals(DominioSexo.F)) {
					retorno.append("Profª. ");
				} else {
					retorno.append("Prof. ");
				}
				break;
			case CONTRATADO:
				if (!verificarEnfermeiroTecnicoEnfermagem(servidor.getUsuario(), retorno)){
					if (escopo.equals(DominioEscopoAtendimento.N2)) {
						retorno.append("médico contratado ");
					}
					retorno.append(titulo).append(' ');					
				}
				break;
			case RESIDENTE:
				if (escopo.equals(DominioEscopoAtendimento.N2)) {
					retorno.append("médico residente ");
				}
				retorno.append(titulo).append(' ');
				break;
			case ACADEMICO:
				if (servidor.getPessoaFisica().getSexo().equals(DominioSexo.F)) {
					retorno.append("Acadêmica ");
				} else {
					retorno.append("Acadêmico ");
				}
				break;
			default:
				if (!verificarEnfermeiroTecnicoEnfermagem(servidor.getUsuario(), retorno)){
					retorno.append(titulo).append(' ');					
				}
				break;
			}
		}
		else{
			if (!verificarEnfermeiroTecnicoEnfermagem(servidor.getUsuario(), retorno)){
				retorno.append(titulo).append(' ');					
			}
		}
		retorno.append(WordUtils.capitalizeFully(servidor.getPessoaFisica()
				.getNome()));

		if (!TipoVinculo.ACADEMICO.equals(tipoVinculo) && siglaConselho != null && numeroRegConselho != null ){
			if (isEvoluacao){
				retorno.append(", \n ").append(siglaConselho).append(' ').append(numeroRegConselho);
			} else {
				retorno.append(", ").append(siglaConselho).append(' ').append(numeroRegConselho);	
			}
		}

		if (TipoVinculo.CONTRATADO.equals(tipoVinculo)){
			// registro feito no ambulatório ou na internação
			if (escopo.equals(DominioEscopoAtendimento.N1)
					|| escopo.equals(DominioEscopoAtendimento.N3)) {
				String descCentroCusto = obterDescCentroCusto(servidor);
				if (descCentroCusto != null) {
					retorno.append(", ").append(
							WordUtils.capitalizeFully(descCentroCusto));
				}
			} else if (escopo.equals(DominioEscopoAtendimento.N2)) {
				List<MamEmgServEspecialidade> listaServidorEspecialidade = getMamEmgServEspecialidadeDAO()
						.pesquisarServidorEspecialidadePorRapServidor(servidor);
				MamEmgServEspecialidade servidorEspecialidade = null;
				if(listaServidorEspecialidade!=null && listaServidorEspecialidade.size()>0){
					servidorEspecialidade = listaServidorEspecialidade.get(0);
				}
				if (servidorEspecialidade != null
						&& servidorEspecialidade.getMamEmgEspecialidades() != null
						&& servidorEspecialidade.getMamEmgEspecialidades()
								.getAghEspecialidade() != null) {
					retorno.append(", ").append(
							WordUtils.capitalizeFully(servidorEspecialidade
									.getMamEmgEspecialidades()
									.getAghEspecialidade()
									.getNomeEspecialidade()));
				}
			}
		} else if (TipoVinculo.RESIDENTE.equals(tipoVinculo)) {
			String programaResidencia = obterProgramaResidencia(servidor,
					dthrCriacao);
			if (StringUtils.isNotEmpty(programaResidencia)) {
				retorno.append(", ").append(
						WordUtils.capitalizeFully(programaResidencia));
			}
		}

		return retorno.toString();
	}
	
	private Boolean verificarEnfermeiroTecnicoEnfermagem(String loginServidor, StringBuffer retorno){
		Boolean ret = false;
		if (cascaFacade.usuarioTemPerfil(loginServidor, "ENF05") && !cascaFacade.usuarioTemPerfil(loginServidor, "ENF01")){
			retorno.append("Téc. Enf. ");
			ret = true;
		}
		else if (cascaFacade.usuarioTemPerfil(loginServidor, "ENF01")){
			retorno.append("Enf. ");
			ret = true;
		}
		return ret;
	}

	public enum TipoVinculo {
		PROFESSOR,
		CONTRATADO,
		RESIDENTE,
		ACADEMICO;
	}
	
	private TipoVinculo buscaTipoVinculo(RapServidores servidor) throws ApplicationBusinessException {
		
		AghParametros pCodigoProfessor = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_VINCULO_PROFESSOR);
		Short codigoProfessor = null;
		if (pCodigoProfessor != null && pCodigoProfessor.getVlrNumerico() != null){
			codigoProfessor = pCodigoProfessor.getVlrNumerico().shortValue();
		}
		AghParametros pCodigoAcademico = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_VINCULO_ACADEMICO);
		Short codigoAcademico = null;
		if (pCodigoAcademico != null && pCodigoAcademico.getVlrNumerico() != null) {
			codigoAcademico = pCodigoAcademico.getVlrNumerico().shortValue();
		}
		
		if (codigoProfessor != null){
			if (servidor.getId().getVinCodigo().equals(codigoProfessor)){
				return TipoVinculo.PROFESSOR;
			}
		} 
		if (codigoAcademico != null) {
			if (servidor.getId().getVinCodigo().equals(codigoAcademico)) {
				return TipoVinculo.ACADEMICO;
			} else if( verificaTipoAssinatura(servidor, DominioTipoPerfilAtendimento.N3)) {
				return TipoVinculo.ACADEMICO;
			}
		}
		if (verificaTipoAssinatura(servidor, DominioTipoPerfilAtendimento.N1)){
			return TipoVinculo.CONTRATADO;
		}
		if (verificaTipoAssinatura(servidor, DominioTipoPerfilAtendimento.N2)){
			return TipoVinculo.RESIDENTE;
		}
		return null;
	}

	/**
	 * @ORADB - MAMC_ASS_GET_PRG_RES
	 * @param servidor
	 * @param data
	 * @since 17/07/2012
	 * @author daniel.silva
	 * @return
	 */
	public String obterProgramaResidencia(RapServidores servidor, Date data) {
		StringBuilder sb = new StringBuilder();
		RarCandidatoPrograma cpm = getRegistroColaboradorFacade()
				.obterRarCandidatoProgramaPorServidorData(servidor, data);
		if (cpm != null) {
			sb.append(
					retornaAnoRarPrograma(cpm.getRarCandidatos().getSeq(), data))
					.append(' ')
					.append(cpm.getRarProgramasByPgaSeq().getResumo());
		}
		return sb.toString();
	}

	/**
	 * @ORADB - RARC_RETORNA_ANO
	 * @param cndSeq
	 * @param cData
	 * @since 18/07/2012
	 * @author daniel.silva
	 * @return
	 */
	public String retornaAnoRarPrograma(Integer cndSeq, Date vData) {
		String retorno = "";
		vData = DateUtil.truncaData(vData);
		RarCandidatos cnd = getRegistroColaboradorFacade()
				.obterRarCandidatoPorChavePrimaria(cndSeq);
		// c_pga
		RarCandidatoPrograma cpm = getRegistroColaboradorFacade()
				.obterRarCandidatoProgramaPorCandidatoData(cnd, vData);

		if (cpm != null) {
			if (isDatasIguaisFormatoDDMM(vData, cpm.getDtInicio())) {
				vData = DateUtil.adicionaDias(vData, 1);
			}
			retorno = obterAnoRarPrograma(vData, cnd, cpm);
		} else {
			// c_ult_pga
			cpm = getRegistroColaboradorFacade()
					.obterUltimoRarCandidatoProgramaPorCandidato(cnd);
			retorno = obterAnoRarPrograma(vData, cnd, cpm);
		}
		return retorno;
	}

	/**
	 * @param vData
	 * @param cnd
	 * @param cpm
	 * @return
	 * @since 17/07/2012
	 * @author daniel.silva
	 */
	private String obterAnoRarPrograma(Date vData, RarCandidatos cnd,
			RarCandidatoPrograma cpm) {
		StringBuilder sb = new StringBuilder();
		RarPrograma vPgaAtual = cpm.getRarProgramasByPgaSeq();
		Date vDtInicio = cpm.getDtInicio();
		DominioTipoRarPrograma vTipo = vPgaAtual.getTipo();
		sb.append(vTipo);

		if (vTipo == null || vTipo.equals(DominioTipoRarPrograma.PP)) {
			sb.append(DateUtil.obterQtdAnosEntreDuasDatas(vData, vDtInicio));
		} else {
			// c_cpm
			RarCandidatoPrograma vCpm = getRegistroColaboradorFacade()
					.obterRarCandidatoProgramaPorProgramaCandidato(cnd,
							vPgaAtual);
			if (vCpm != null) {
				sb.append(DateUtil.obterQtdAnosEntreDuasDatas(vCpm.getDtFim(),
						vCpm.getDtInicio())
						+ DateUtil.obterQtdAnosEntreDuasDatas(vData, vDtInicio));
			} else {
				// c_asp
				RarProgramaDuracao pdu = getRegistroColaboradorFacade()
						.obterRarProgramaDuracaoPorRarProgramaAtualDataInicio(
								vPgaAtual, DateUtil.adicionaDias(vData, -1),
								Boolean.FALSE);
				if (pdu != null) {
					vDtInicio = DateUtil.adicionaDias(vDtInicio,
							(pdu.getTempo() * 365));
					if (!pdu.getRarPrograma().getTipo()
							.equals(DominioTipoRarPrograma.PP)) {
						// c_asp_pp
						RarProgramaDuracao pduPP = getRegistroColaboradorFacade()
								.obterRarProgramaDuracaoPorRarProgramaAtualDataInicio(
										vPgaAtual, vData, Boolean.TRUE);
						vDtInicio = DateUtil.adicionaDias(vDtInicio,
								(pduPP.getTempo() * 365) * (-1));
					}
					sb.append(DateUtil.obterQtdAnosEntreDuasDatas(vData,
							vDtInicio));
				} else {
					sb.append(DateUtil.obterQtdAnosEntreDuasDatas(vData,
							vDtInicio));
				}
			}
		}
		return sb.toString();
	}

	private Boolean isDatasIguaisFormatoDDMM(Date data1, Date data2) {
		return DateUtil.obterDataFormatada(data1, "dd/MM").equals(
				DateUtil.obterDataFormatada(data2, "dd/MM"));
	}

	/**
	 * ORADB:MAMK_ASSINATURA.MAMC_ASS_TEXTO <br>
	 * Esta função retorna o padrão de visualização da assinatura do registro do
	 * atendimento do paciente.<br>
	 * Padrão adotado a partir de agosto de 2008 pela Comissão de Prontuários. <br>
	 * <br>
	 * Este é o novo padrão para a visualização da assinatura independente da
	 * origem do atendimento do paciente <br>
	 * - ambulatório <br>
	 * - emergência <br>
	 * - internação <br>
	 * - qualquer outra que por ventura surja <br>
	 * <br>
	 * 
	 * @param ana
	 * @param evo
	 * @param notaAna
	 * @param notaEvo
	 * @throws ApplicationBusinessException
	 * @return
	 */
	public String obterAssinaturaTexto(MamAnamneses ana, MamEvolucoes evo,
			MamNotaAdicionalAnamneses notaAna, MamNotaAdicionalEvolucoes notaEvo)
			throws ApplicationBusinessException {

		String texto = "";

		if (ana != null) {
			texto = obterAssinaturaTexto(ana.getServidor(),
					ana.getDthrCriacao(), ana.getDthrValida(),
					ana.getServidorValida(), ana.getConsulta(),
					ana.getMamTriagens(), ana.getRegistro(),
					ana.getAtendimento(), false);
		} else if (evo != null) {
			texto = obterAssinaturaTexto(evo.getServidor(),
					evo.getDthrCriacao(), evo.getDthrValida(),
					evo.getServidorValida(), evo.getConsulta(),
					evo.getMamTriagens(), evo.getRegistro(),
					evo.getAtendimento(), true);
		} else if (notaAna != null) {
			texto = obterAssinaturaTexto(notaAna.getServidor(),
					notaAna.getDthrCriacao(), notaAna.getDthrValida(),
					notaAna.getServidorValida(), notaAna.getConsulta(),
					notaAna.getTriagem(), notaAna.getRegistro(),
					notaAna.getAtendimento(), false);
		} else if (notaEvo != null) {
			texto = obterAssinaturaTexto(notaEvo.getServidor(),
					notaEvo.getDthrCriacao(), notaEvo.getDthrValida(),
					notaEvo.getServidorValida(), notaEvo.getConsulta(),
					notaEvo.getMamTriagens(), notaEvo.getRegistro(),
					notaEvo.getAtendimento(), false);
		}

		return texto;
	}

	private String obterAssinaturaTexto(RapServidores servidor,
			Date dtHrCriacao, Date dtHrValida, RapServidores servidorValida,
			AacConsultas con, MamTriagens trg, MamRegistro rgt,
			AghAtendimentos atd, boolean isEvolucao)
			throws ApplicationBusinessException {
		StringBuilder sb = new StringBuilder();

		DominioEscopoAtendimento escopo = obterEscopoAtendimento(trg, atd, rgt);

		Boolean foiAssinado = verificarFoiAssinado(servidorValida, dtHrValida);
		Boolean mesmoAutor = verificarMesmoAutor(servidor, servidorValida,
				foiAssinado);

		sb.append("Elaborado");
		if (!mesmoAutor) {
			sb.append(" por ");
			sb.append(obterTextoPadraoUsuario(escopo, servidor, dtHrCriacao,
					isEvolucao));
			sb.append(" em ").append(
					DateUtil.obterDataFormatada(dtHrCriacao,
							DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		}

		if (foiAssinado) {
			if (mesmoAutor) {
				sb.append(" e assinado por ");
			} else {
				sb.append(" - Assinado por ");
			}
			sb.append(obterTextoPadraoUsuario(escopo, servidorValida,
					dtHrValida, isEvolucao));
			sb.append(" em ").append(
					DateUtil.obterDataFormatada(dtHrValida,
							DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		}

		// se for um registro ambulatorial deve-se acrescentar quem
		// supervisionou o registro do atendimento do paciente
		if(con != null){
			con = aacConsultasDAO.obterPorChavePrimaria(con.getNumero());
			if (escopo.equals(DominioEscopoAtendimento.N1)
					&& con != null && con.getControle() != null
					&& con.getControle().getSupervisor() != null) {
				sb.append(" - Supervisionado por ")
						.append(WordUtils.capitalizeFully(con.getControle()
								.getSupervisor()));
			}
		}

		return StringUtils.substring(sb.toString(), 0, 1999);
	}

	private Boolean isUsaAssinaturaUnificada()
			throws ApplicationBusinessException {
		Boolean usaAssinaturaUnificada = Boolean.FALSE;

		String pUsaAssUnificada = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_USA_ASS_UNIFICADA).getVlrTexto();

		if (StringUtils.isNotEmpty(pUsaAssUnificada)) {
			usaAssinaturaUnificada = pUsaAssUnificada.equalsIgnoreCase("S");
		}

		return usaAssinaturaUnificada;
	}

	/**
	 * ORADB: mamk_visualizacao.MAMC_GET_IDENT_RESP
	 * 
	 * Retorna string com a identificação da consulta.
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	public String obterIdentificacaoResponsavel(MamAnamneses ana,
			MamEvolucoes evo)
			throws ApplicationBusinessException {

		StringBuffer texto = new StringBuffer();

		if (isUsaAssinaturaUnificada()) {
			if (ana != null) {
				texto.append(obterAssinaturaTexto(ana, null, null, null));
			} else if (evo != null) {
				texto.append(obterAssinaturaTexto(null, evo, null, null));
			}
		} else {
			if (ana != null) {
				texto.append(obterAssinaturaIdentificacaoResponsavel(
						ana.getServidor(), ana.getServidorValida(),
						ana.getDthrValida(), ana.getConsulta()));
			} else if (evo != null) {
				texto.append(obterAssinaturaIdentificacaoResponsavel(
						evo.getServidor(), evo.getServidorValida(),
						evo.getDthrValida(), evo.getConsulta()));
			}
		}

		return texto.toString();
	}

	private String obterAssinaturaIdentificacaoResponsavel(
			RapServidores servidor, RapServidores servidorValida,
			Date dthrValida, AacConsultas consulta)
			throws ApplicationBusinessException {
		StringBuilder sb = new StringBuilder();

		Object[] objeto = getPrescricaoFacade().buscaConsProf(servidor);
		if (objeto[1] != null) {
			sb.append("Elaborado por ").append(
					WordUtils.capitalizeFully((String) objeto[1]));
		}
		objeto = getPrescricaoFacade().buscaConsProf(servidorValida);
		if (objeto[1] != null) {
			sb.append(" - Assinado por ").append(
					WordUtils.capitalizeFully((String) objeto[1]));
			sb.append(" em: ").append(
					DateUtil.obterDataFormatada(dthrValida,
							DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		}
		if (consulta != null && consulta.getControle() != null
				&& consulta.getControle().getSupervisor() != null) {
			sb.append(" - Supervisionado por ").append(
					WordUtils.capitalizeFully(consulta.getControle()
							.getSupervisor()));
		}

		return sb.toString();
	}

	/**
	 * ORADB: mamk_visualizacao.MAMC_GET_RESP_NOTA
	 * 
	 * Retorna string com a identificação da consulta.
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	private String obterIdentificacaoResponsavelNota(
			MamNotaAdicionalAnamneses notaAnamnese,
			MamNotaAdicionalEvolucoes notaEvolucao)
			throws ApplicationBusinessException {

		String usaAssinaturaUnificada = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_USA_ASS_UNIFICADA)
				.getVlrTexto();

		StringBuffer texto = new StringBuffer();

		if ("S".equals(usaAssinaturaUnificada)) {
			if (notaAnamnese != null) {
				texto.append(obterAssinaturaTexto(null, null, notaAnamnese,
						null));
			} else if (notaEvolucao != null) {
				texto.append(obterAssinaturaTexto(null, null, null,
						notaEvolucao));
			}
		} else {
			RapServidores servidor = null;
			RapServidores servidorValida = null;
			Date dthrValida = null;
			AacConsultas consulta = null;

			if (notaAnamnese != null) {
				servidor = notaAnamnese.getServidor();
				servidorValida = notaAnamnese.getServidorValida();
				dthrValida = notaAnamnese.getDthrValida();
				consulta = notaAnamnese.getConsulta();
			} else if (notaEvolucao != null) {
				servidor = notaEvolucao.getServidor();
				servidorValida = notaEvolucao.getServidorValida();
				dthrValida = notaEvolucao.getDthrValida();
				consulta = notaEvolucao.getConsulta();
			}
			Object[] objeto = getPrescricaoFacade().buscaConsProf(servidor);
			if (objeto[1] != null) {
				texto.append("Elaborado por ").append(
						WordUtils.capitalizeFully((String) objeto[1]));
			}
			objeto = getPrescricaoFacade().buscaConsProf(servidorValida);
			if (objeto[1] != null) {
				texto.append(" - Assinado por ").append(
						WordUtils.capitalizeFully((String) objeto[1]));
				texto.append(" em ").append(
						DateUtil.obterDataFormatada(dthrValida,
								"dd/MM/yyyy HH:mm"));
			}
			if (consulta.getControle() != null
					&& consulta.getControle().getSupervisor() != null) {
				texto.append(" - Supervisionado por ").append(
						WordUtils.capitalizeFully(consulta.getControle()
								.getSupervisor()));
			}
		}
		return texto.toString();
	}

	/**
	 * mamk_visualizacao.mamc_get_notas_ana
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */

	public StringBuilder obtemDescricaoNotaAdicionalAna(AacConsultas consulta,
			DominioTipoNotaAdicional tipo, Boolean modoImpressao) throws ApplicationBusinessException,
			ApplicationBusinessException {
		StringBuilder texto = new StringBuilder(23);

		Boolean primeiraVez = true;

		List<MamNotaAdicionalAnamneses> notasAdicionaisAnamnese = getMamNotaAdicionalAnamnesesDAO()
				.pesquisarNotaAdicionalAnamneseParaDescricao(
						consulta.getNumero());
		
		
		if (notasAdicionaisAnamnese != null
				&& !notasAdicionaisAnamnese.isEmpty()
				&& tipo.equals(DominioTipoNotaAdicional.N2) && !modoImpressao) {
			texto.append(NEWLINE);
			texto.append(TITULONOTASADICIONAIS);
		}

		for (MamNotaAdicionalAnamneses notaAdicional : notasAdicionaisAnamnese) {
			
			final String DESCNTADC = notaAdicional.getDescricao();
			
			if (!tipo.equals(DominioTipoNotaAdicional.N2) && primeiraVez) {
				texto.append(NEWLINE);
				texto.append(NEWLINE);
				texto.append(DESCNTADC);
			} else {
				texto.append(NEWLINE);
				texto.append(SEPARATIONLINE);
				texto.append(NEWLINE);
				texto.append(DESCNTADC);
			}
			primeiraVez = false;

			if (tipo.equals(DominioTipoNotaAdicional.N3)) {
				texto.append(NEWLINE);
			} else {
				texto.append(NEWLINE);
				texto.append(NEWLINE);
				texto.append(obterIdentificacaoResponsavelNota(notaAdicional,
						null));
				texto.append(NEWLINE);
			}
		}
		return texto;
	}

	/**
	 * Procedure
	 * 
	 * ORADB AACP_ATU_DT_INI_ATEN
	 * 
	 * @param conNumero
	 * @param dataInicio
	 * @throws BaseException
	 */
	public void atualizarDataInicioAtendimento(Integer numeroConsulta,
			Date dataInicio, String nomeMicrocomputador,
			final Boolean substituirProntuario) throws BaseException {
		AacConsultas consultaAtual = this.getAacConsultasDAO().obterPorChavePrimaria(numeroConsulta);
		AacConsultas consultaAnterior = this.getConsultasON().clonarConsulta(consultaAtual);

		consultaAtual.setDthrInicio(dataInicio);
		getAmbulatorioConsultaRN().atualizarConsulta(consultaAnterior,
				consultaAtual, false, nomeMicrocomputador,
				new Date(), substituirProntuario, true);
	}

	/**
	 * mamk_visualizacao.mamc_get_notas_evo
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */

	public StringBuilder obtemDescricaoNotaAdicionalEvo(AacConsultas consulta,
			DominioTipoNotaAdicional tipo, Boolean modoImpresso) throws ApplicationBusinessException,
			ApplicationBusinessException {
		StringBuilder texto = new StringBuilder(23);

		Boolean primeiraVez = true;

		List<MamNotaAdicionalEvolucoes> notasAdicionaisEvolucao = getMamNotaAdicionalEvolucoesDAO()
				.pesquisarNotaAdicionalEevolucaoParaDescricao(
						consulta.getNumero());

		if (notasAdicionaisEvolucao != null
				&& !notasAdicionaisEvolucao.isEmpty()
				&& tipo.equals(DominioTipoNotaAdicional.N2) && !modoImpresso) {
			texto.append(NEWLINE);
			texto.append("TITULONOTASADICIONAIS");
		}

		for (MamNotaAdicionalEvolucoes notaAdicional : notasAdicionaisEvolucao) {
			if (!tipo.equals(DominioTipoNotaAdicional.N2) && primeiraVez) {
				texto.append(NEWLINE);
				texto.append(NEWLINE);
				texto.append(notaAdicional.getDescricao());
			} else {
				texto.append(NEWLINE);
				texto.append(SEPARATIONLINE);
				texto.append(NEWLINE);
				texto.append(notaAdicional.getDescricao());
			}
			primeiraVez = false;

			if (tipo.equals(DominioTipoNotaAdicional.N3)) {
				texto.append(NEWLINE);
			} else {
				texto.append(NEWLINE);
				texto.append(NEWLINE);
				texto.append(obterIdentificacaoResponsavelNota(null,
						notaAdicional));
				texto.append(NEWLINE);
			}
		}
		return texto;
	}
	
	/**
	 * ORADB: mamk_visualizacao.MAMP_GET_ANAMNESE
	 * 
	 * DIVERSOS CURSORES FORA DO ESCOPO DE AMBULATORIO V1 ANTES DE USAR ESTE
	 * METÓDO VERIFICAR SE ELE COBRE TODAS AS NECESSIDADES
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 * 
	 */
	public StringBuilder obtemDescricaoAnamnese(MamAnamneses anamnese,
			Boolean naoInf, Integer tinSeq, Boolean montaCabecalho,
			Boolean modoImpressao)
			throws ApplicationBusinessException {

		StringBuilder descricao = new StringBuilder(25);

		// inclui cabecalho com a identificação da consulta
		if (tinSeq == null && (montaCabecalho == null || montaCabecalho)) {
			descricao.append(obterDadosBasicosConsulta(anamnese.getConsulta()));
		}

		// Para cada tipo de item vai verificar se existe algum registro
		List<MamTipoItemAnamneses> listaTipoItemAnamnese = getMamTipoItemAnamnesesDAO()
				.buscaTipoItemAnamneseAtivoOrdenado(tinSeq);

		for (MamTipoItemAnamneses tipoItemAnamnese : listaTipoItemAnamnese) {
			StringBuilder textoLivre = new StringBuilder();
			List<MamItemAnamneses> itensAnamnese = new ArrayList<MamItemAnamneses>();
			if(anamnese != null){
				itensAnamnese = getMamItemAnamnesesDAO()
						.pesquisarItemAnamnesesPorAnamnesesTipoItem(
								anamnese.getSeq(), tipoItemAnamnese.getSeq());
			}
			

			for (MamItemAnamneses itemAnamnese : itensAnamnese) {
				MamTipoItemAnamneses tipoItem = getMamTipoItemAnamnesesDAO()
						.obterPorChavePrimaria(itemAnamnese.getId().getTinSeq());
				if (tipoItem.getPermiteLivre()) {
					textoLivre.append(adicionaItemDescricao(
							itemAnamnese.getDescricao(), naoInf));
				} else if (tipoItem.getIdentificacao()) {
					textoLivre.append(adicionaItemDescricao(
							itemAnamnese.getDescricao(), false));
				}
			}
			if (itensAnamnese != null && !itensAnamnese.isEmpty()) {
				descricao.append(NEWLINE);
				descricao.append(textoLivre);
			}
		}
		descricao = gravarAnamnese(tinSeq,descricao,anamnese,modoImpressao);
		return descricao;
	}
	
	public StringBuilder gravarAnamnese(Integer tinSeq,StringBuilder descricao,MamAnamneses anamnese,Boolean modoImpressao) throws ApplicationBusinessException{
		if (tinSeq == null) { // tela
			descricao.append(NEWLINE);
			descricao.append(NEWLINE);
			if(anamnese != null){
				descricao.append(obterIdentificacaoResponsavel(anamnese, null));
				descricao.append(NEWLINE);
				if (!modoImpressao) {
					StringBuilder notaAdicional = obtemDescricaoNotaAdicionalAna(
							anamnese.getConsulta(), DominioTipoNotaAdicional.N2,
							false);
					if (notaAdicional != null) {
						descricao.append(NEWLINE);
						descricao.append(notaAdicional);
					}
				}
			}
		}
		if(anamnese != null){
			if (anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.E)) {
				descricao.append(NEWLINE);
				descricao.append(NEWLINE);
				descricao.append("<<< ANAMNESE EXCLUÍDA >>>");
			}
		}
		return descricao;
	}

	/**
	 * ORADB: mamk_visualizacao.MAMP_GET_EVOLUCAO
	 * 
	 * DIVERSOS CURSORES FORA DO ESCOPO DE AMBULATORIO V1 ANTES DE USAR ESTE
	 * METÓDO VERIFICAR SE ELE COBRE TODAS AS NECESSIDADES
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 * 
	 */
	public StringBuilder obtemDescricaoEvolucao(MamEvolucoes evolucao,
			Boolean naoInf, Integer tinSeq, Boolean montaCabecalho,
			Boolean modoImpressao)
			throws ApplicationBusinessException {

		StringBuilder descricao = new StringBuilder(25);

		// inclui cabecalho com a identificação da consulta
		if (tinSeq == null && (montaCabecalho == null || montaCabecalho)) {
			if(evolucao != null){
				descricao.append(obterDadosBasicosConsulta(evolucao.getConsulta()));
			}
		}

		// Para cada tipo de item vai verificar se existe algum registro
		List<MamTipoItemEvolucao> listaTipoItemEvolucao = getMamTipoItemEvolucaoDAO()
				.buscaTipoItemEvolucaoAtivoOrdenado(tinSeq);

		for (MamTipoItemEvolucao tipoItemEvolucao : listaTipoItemEvolucao) {
			StringBuilder textoLivre = new StringBuilder();
			List<MamItemEvolucoes> itensEvolucao = new ArrayList<MamItemEvolucoes>();
			if(evolucao != null){
				itensEvolucao = getMamItemEvolucoesDAO()
						.pesquisarItemEvolucaoPorEvolucaoTipoItem(
								evolucao.getSeq(), tipoItemEvolucao.getSeq());
			}

			for (MamItemEvolucoes itemEvolucao : itensEvolucao) {
				MamTipoItemEvolucao tipoItem = getMamTipoItemEvolucaoDAO()
						.obterPorChavePrimaria(itemEvolucao.getId().getTieSeq());
				if (tipoItem.getPermiteLivre()) {
					textoLivre.append(adicionaItemDescricao(
							itemEvolucao.getDescricao(), naoInf));
				} else if (tipoItem.getIdentificacao()) {
					textoLivre.append(adicionaItemDescricao(
							itemEvolucao.getDescricao(), false));
				}
			}

			if (itensEvolucao != null && !itensEvolucao.isEmpty()) {
				descricao.append(NEWLINE);
				descricao.append(textoLivre);
			}
		}
		descricao = gravaLogEvo(tinSeq,descricao,evolucao,modoImpressao);
		return descricao;
	}

	
	public StringBuilder gravaLogEvo(Integer tinSeq,StringBuilder descricao,MamEvolucoes evolucao,Boolean modoImpressao) throws ApplicationBusinessException{
		if (tinSeq == null) { // tela
			descricao.append(NEWLINE);
			descricao.append(NEWLINE);
			if(evolucao != null){
				descricao.append(obterIdentificacaoResponsavel(null, evolucao));
				descricao.append(NEWLINE);
				if (!modoImpressao) {
					StringBuilder notaAdicional = obtemDescricaoNotaAdicionalEvo(
							evolucao.getConsulta(), DominioTipoNotaAdicional.N2,
							false);
					if (notaAdicional != null) {
						descricao.append(NEWLINE);
						descricao.append(notaAdicional);
					}
				}
			}
		}
		if(evolucao != null){
			if (evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.E)) {
				descricao.append(NEWLINE);
				descricao.append(NEWLINE);
				descricao.append("<<< EVOLUÇÃO EXCLUÍDA >>>");
			}
		}
		return descricao;
	}
	/**
	 * ORADB: mamk_visualizacao.MAMC_GET_RECEITA
	 * 
	 */
	private StringBuilder obtemDescricaoReceita(MamReceituarios receituario) {
		StringBuilder texto = new StringBuilder(29);
		List<MamItemReceituario> listaReceituarioInterno = new ArrayList<MamItemReceituario>();
		List<MamItemReceituario> listaReceituarioExterno = new ArrayList<MamItemReceituario>();

		List<MamItemReceituario> listaItemReceituario = getManterSumarioAltaReceitasON()
				.obterItensReceitaOrdenadoPorSeqp(receituario);
		for (MamItemReceituario itemReceituario : listaItemReceituario) {
			if (itemReceituario.getIndInterno().isSim()) {
				listaReceituarioInterno.add(itemReceituario);
			} else {
				listaReceituarioExterno.add(itemReceituario);
			}
		}

		if (listaReceituarioInterno.size() > 0) {
			texto.append("\t     Uso Interno");
			texto.append(NEWLINE);
			texto.append(obtemDescricaoReceitaFormatada(listaReceituarioInterno));
		}

		if (listaReceituarioExterno.size() > 0) {
			texto.append("\t     Uso Externo");
			texto.append(NEWLINE);
			texto.append(obtemDescricaoReceitaFormatada(listaReceituarioExterno));
		}

		if (receituario.getPendente().equals(DominioIndPendenteAmbulatorio.E)) {
			texto.append(NEWLINE);
			texto.append(" <<< RECEITUÁRIO EXCLUÍDO >>>");
		}

		return texto;
	}

	/**
	 * ORADB: mamk_visualizacao.MAMC_GET_RECEITA
	 * 
	 */
	private StringBuilder obtemDescricaoReceitaFormatada(
			List<MamItemReceituario> receituarios) {
		StringBuilder texto = new StringBuilder(19);
		for (MamItemReceituario itemReceituario : receituarios) {
			texto.append("\t\t\t").append(itemReceituario.getDescricao())
					.append(' ');

			if (itemReceituario.getQuantidade() != null) {
				texto.append(".................................... ")
				.append(itemReceituario.getQuantidade());
			}
			if (itemReceituario.getFormaUso() != null
					|| itemReceituario.getIndUsoContinuo().isSim()) {
				texto.append(NEWLINE);
				texto.append("\t\t\t\t");
				if (itemReceituario.getFormaUso() != null) {
					texto.append(itemReceituario.getFormaUso()).append(". ");
				}
				if (itemReceituario.getIndUsoContinuo().isSim()) {
					texto.append("Uso Contínuo");
				}
			}

			texto.append(NEWLINE);
		}
		return texto;
	}

	private String montaTextoCid(AghCid cid) {

		StringBuffer textoCid = new StringBuffer(", CID: ")
				.append(cid.getCodigo())
				.append(_HIFEN_)
				.append(cid.getDescricaoEditada() != null ? cid
						.getDescricaoEditada() : cid.getDescricao());

		return textoCid.toString();
	}

	private StringBuilder montaTextoDescricao(MamProcedimentoRealizado proc,
			String textoCid) {
		StringBuilder texto = new StringBuilder();

		if (proc.getQuantidade() == null || proc.getQuantidade().equals(0)) {
			texto.append(proc.getProcedimento().getDescricao());
		} else {
			texto.append(proc.getProcedimento().getDescricao())
					.append(", quantidade: ").append(proc.getQuantidade());
		}
		texto.append(textoCid)
		.append(NEWLINE);
		if (!proc.getPendente().equals(DominioIndPendenteAmbulatorio.V)
				&& proc.getSituacao().equals(DominioSituacao.I)) {
			texto.append("<<< EXCLUÍDO >>>");
		}

		return texto;
	}

	/**
	 * ORADB: MAMK_VISUALIZACAO.MAMC_GET_PROC_REAL
	 * 
	 */
	public StringBuilder obtemDescricaoProcedimentos(AacConsultas consulta)
			throws ApplicationBusinessException {
		StringBuilder texto = new StringBuilder();

		Set<MamProcedimentoRealizado> procedimentosRealizados = getMamProcedimentoRealizadoDAO()
				.pesquisarProcedimentoPorNumeroConsulta(consulta.getNumero());

		if (procedimentosRealizados != null
				&& !procedimentosRealizados.isEmpty()) {
			texto.append("Procedimentos Realizados:");
			texto.append(NEWLINE);
		}

		for (MamProcedimentoRealizado procedimento : procedimentosRealizados) {
			String textoCid = "";
			if (procedimento.getCid() != null) {
				textoCid = montaTextoCid(procedimento.getCid());
			}
			texto.append(montaTextoDescricao(procedimento, textoCid));
		}
		return texto;
	}

	/**
	 * ORADB: mamk_visualizacao.mamp_get_consulta
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public StringBuilder obtemDescricaoConsulta(AacConsultas consulta) throws ApplicationBusinessException,
			ApplicationBusinessException {

		StringBuilder dadosConsulta = obterDadosBasicosConsulta(consulta);
		StringBuilder descricaoAnamnese = new StringBuilder();
		StringBuilder descricaoEvolucao = new StringBuilder();
		StringBuilder descricaoReceituario = new StringBuilder();
		StringBuilder descricaoExames = new StringBuilder();

		// Anamnese
		MamAnamneses anamneseAtual = getMamAnamnesesDAO()
				.obterAnamneseAtivaPorNumeroConsulta(consulta.getNumero());
		if (anamneseAtual != null) {
			StringBuilder auxStringBuilder = obtemDescricaoAnamnese(
					anamneseAtual, false, null, false, false);
			if (auxStringBuilder != null) {
				descricaoAnamnese.append("Anamnese:");
				descricaoAnamnese.append(NEWLINE);
				descricaoAnamnese.append(NEWLINE);
				descricaoAnamnese.append(auxStringBuilder);
				descricaoAnamnese.append(NEWLINE);
				MamEvolucoes evolucaoExcluida = getMamEvolucoesDAO()
						.obterUltimaEvolucaoExcluidaPorConsulta(
								consulta.getNumero());
				if (evolucaoExcluida != null) {
					StringBuilder auxStringBuilder2 = obtemDescricaoEvolucao(
							evolucaoExcluida, false, null, false, false);
					if (auxStringBuilder2 != null
							&& anamneseAtual.getPendente().equals(
									DominioIndPendenteAmbulatorio.P)) {
						descricaoAnamnese.append(NEWLINE);
						descricaoAnamnese.append("Evolução:");
						descricaoAnamnese.append(NEWLINE);
						descricaoAnamnese.append(NEWLINE);
						descricaoAnamnese.append(auxStringBuilder2);
						descricaoAnamnese.append(NEWLINE);
					}
				}
			}
		}
		/*
		 * else{
		 * descricaoAnamnese.append(obtemDescricaoNotaAdicionalAna(consulta,
		 * DominioTipoNotaAdicional.N2, false)); }
		 */

		/**
		 * Ajuste para os métodos abaixo, referentes a evolução ficarem com comportamento igual<br>
		 * a PROCEDURE MAMP_GET_CONSULTA
		 */
		// Evolução
		List<MamEvolucoes> evolucoes = getMamEvolucoesDAO().pesquisarEvolucoesAtivaPorNumeroConsulta(consulta.getNumero());
		MamEvolucoes evolucao = null;
		
		if(!evolucoes.isEmpty()){
			evolucao = evolucoes.get(evolucoes.size() - 1);
		}
		if (evolucao != null) {
			StringBuilder auxStringBuilder = obtemDescricaoEvolucao(
					evolucao, false, null, false, false);
			if (auxStringBuilder != null) {
				descricaoEvolucao.append("Evolução:");
				descricaoEvolucao.append(NEWLINE);
				descricaoEvolucao.append(NEWLINE);
				descricaoEvolucao.append(auxStringBuilder);
				descricaoEvolucao.append(NEWLINE);
				MamAnamneses anamneseExcluida = getMamAnamnesesDAO()
						.obterUltimaAnamneseExcluidaPorConsulta(
								consulta.getNumero());
				if (anamneseExcluida != null
						&& evolucao.getPendente().equals(
								DominioIndPendenteAmbulatorio.P)) {
					StringBuilder auxStringBuilder2 = obtemDescricaoAnamnese(
							anamneseExcluida, false, null, false, false);
					if (auxStringBuilder2 != null) {
						descricaoEvolucao.append(NEWLINE);
						descricaoEvolucao.append("Anamnese:");
						descricaoEvolucao.append(NEWLINE);
						descricaoEvolucao.append(NEWLINE);
						descricaoEvolucao.append(auxStringBuilder2);
						descricaoEvolucao.append(NEWLINE);
					}
				}
			}
		}
		/*
		 * else{
		 * descricaoEvolucao.append(obtemDescricaoNotaAdicionalEvo(consulta,
		 * DominioTipoNotaAdicional.N2,false)); }
		 */

		// Receita medicamentos

		if (consulta.getReceituarios() != null) {
			List<MamReceituarios> receituarios = getMamReceituariosDAO()
					.pesquisarReceituarioPorConsultaOrdenaTipo(
							consulta.getNumero());

			if (receituarios != null && !receituarios.isEmpty()) {
				DominioTipoReceituario tipoAtual = null;
				descricaoReceituario.append("Receituário:");
				descricaoReceituario.append(NEWLINE);
				descricaoReceituario.append(NEWLINE);
				for (MamReceituarios receituario : receituarios) {

					StringBuilder auxStringBuilder = obtemDescricaoReceita(receituario);
					if (auxStringBuilder != null) {
						if (tipoAtual == null
								|| !receituario.getTipo().equals(tipoAtual)) {
							tipoAtual = receituario.getTipo();
							descricaoReceituario.append('\t').append(
									tipoAtual.getDescricao());
							descricaoReceituario.append(NEWLINE);
						}
						descricaoReceituario.append(auxStringBuilder);
						descricaoReceituario.append(NEWLINE);
					}
				}
			}
		}

		AghParametros pSituacaoCancelado = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
		String situacaoCancelado = pSituacaoCancelado.getVlrTexto();

		List<AelItemSolicitacaoExames> itensSolicitacoesExames = getExamesFacade()
				.obterSolicitacoesExamesPorConsultaESituacaoCancelado(
						consulta.getNumero(), situacaoCancelado);
		if (itensSolicitacoesExames != null
				&& !itensSolicitacoesExames.isEmpty()) {
			descricaoExames.append("Solicitação Exame(s):");
			descricaoExames.append(NEWLINE);
			for (AelItemSolicitacaoExames itemSoliciatacaoExame : itensSolicitacoesExames) {
				if (itemSoliciatacaoExame.getMaterialAnalise() != null
						&& itemSoliciatacaoExame.getExame() != null) {
					descricaoExames
							.append(itemSoliciatacaoExame.getExame()
									.getDescricao())
							.append('/')
							.append(itemSoliciatacaoExame.getMaterialAnalise()
									.getDescricao());
					descricaoExames.append(NEWLINE);
				}
			}
		}

		dadosConsulta.append(NEWLINE)
		.append(NEWLINE)
		.append(descricaoAnamnese)
		.append(NEWLINE)
		.append(descricaoEvolucao)
		.append(descricaoReceituario)
		.append(NEWLINE)
		.append(descricaoExames)
		.append(NEWLINE)
		.append(obtemDescricaoProcedimentos(consulta));

		return dadosConsulta;
	}

	/**
	 * ORADB: p_altera_auto_relacionado_naa
	 * 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void alteraCampoPendenteNotaAdicionalAnamneses(
			MamNotaAdicionalAnamneses notaAdicional,
			DominioIndPendenteAmbulatorio pendente)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		notaAdicional.setPendente(pendente);
		notaAdicional.setDthrMvto(new Date());
		notaAdicional.setServidorMvto(servidorLogado);
		getMamNotaAdicionalAnamnesesDAO().atualizar(notaAdicional);
		getMamNotaAdicionalAnamnesesDAO().flush();
	}
	
	public void atualizarIndImpressaoAtestado(MamAtestados atestado){
		atestado.setIndImpresso(true);
		getMamAtestadosDAO().atualizar(atestado);
		getMamAtestadosDAO().flush();
	}

	/**
	 * ORADB: p_altera_auto_relacionado_nev
	 * 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void alteraCampoPendenteNotaAdicionalEvolucao(
			MamNotaAdicionalEvolucoes notaAdicional,
			DominioIndPendenteAmbulatorio pendente)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		notaAdicional.setPendente(pendente);
		notaAdicional.setDthrMvto(new Date());
		notaAdicional.setServidorMvto(servidorLogado);
		getMamNotaAdicionalEvolucoesDAO().atualizar(notaAdicional);
		getMamNotaAdicionalEvolucoesDAO().flush();
	}

	public void excluiNotaAdicionalEvolucao(
			MamNotaAdicionalEvolucoes notaAdicional) throws ApplicationBusinessException,
			ApplicationBusinessException {
		notaAdicional = getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(notaAdicional.getSeq());
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (notaAdicional.getDthrValidaMvto() != null) {
			return;
		} else if (notaAdicional.getPendente().equals(
				DominioIndPendenteAmbulatorio.V)) {
			if (notaAdicional.getServidorValida().equals(servidorLogado)) {
				alteraCampoPendenteNotaAdicionalEvolucao(notaAdicional,
						DominioIndPendenteAmbulatorio.E);
			}
		} else if (notaAdicional.getPendente().equals(
				DominioIndPendenteAmbulatorio.R)
				|| notaAdicional.getPendente().equals(
						DominioIndPendenteAmbulatorio.P)) {

			if (notaAdicional.getNotaAdicionalEvolucao() != null
					&& DominioIndPendenteAmbulatorio.A.equals(notaAdicional
							.getNotaAdicionalEvolucao().getPendente())) {
				MamNotaAdicionalEvolucoes aux = notaAdicional
						.getNotaAdicionalEvolucao();
				alteraCampoPendenteNotaAdicionalEvolucao(aux,
						DominioIndPendenteAmbulatorio.E);
			}
			getMamNotaAdicionalEvolucoesDAO().remover(notaAdicional);
			getMamNotaAdicionalEvolucoesDAO().flush();
		}
	}

	public void excluiNotaAdicionalAnamnese(
			MamNotaAdicionalAnamneses notaAdicional) throws ApplicationBusinessException,
			ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		notaAdicional = getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(notaAdicional.getSeq());
		
		if (notaAdicional.getDthrValidaMvto() != null) {
			return;
		} else if (notaAdicional.getPendente().equals(
				DominioIndPendenteAmbulatorio.V)) {
			if (notaAdicional.getServidorValida().equals(servidorLogado)) {
				alteraCampoPendenteNotaAdicionalAnamneses(notaAdicional,
						DominioIndPendenteAmbulatorio.E);
			}
		} else if (notaAdicional.getPendente().equals(
				DominioIndPendenteAmbulatorio.R)
				|| notaAdicional.getPendente().equals(
						DominioIndPendenteAmbulatorio.P)) {

			if (notaAdicional.getNotaAdicionalAnamnese() != null
					&& DominioIndPendenteAmbulatorio.A.equals(notaAdicional
							.getNotaAdicionalAnamnese().getPendente())) {
				MamNotaAdicionalAnamneses aux = notaAdicional
						.getNotaAdicionalAnamnese();
				alteraCampoPendenteNotaAdicionalAnamneses(aux,
						DominioIndPendenteAmbulatorio.E);
			}
			getMamNotaAdicionalAnamnesesDAO().remover(notaAdicional);
			getMamNotaAdicionalAnamnesesDAO().flush();
		}
	}

	/**
	 * ORADB: MAMK_PERFIL.MAMC_VER_ELAB_ANA
	 * 
	 * Verifica se há condições de a anamnese ser elaborada pelo usuário
	 * 
	 */
	public Boolean verificaUsuarioElaboraAnamnese()
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return getRegistroColaboradorFacade()
				.validarPermissaoAmbulatorioItensPorServidor(servidorLogado.getUsuario(),
						DominioAnamneseEvolucao.A);
	}

	/**
	 * ORADB: MAMK_PERFIL.MAMC_VER_ELAB_EVO
	 * 
	 * Verifica se há condições de a evolução ser elaborada pelo usuário
	 * 
	 */

	public Boolean verificaUsuarioElaboraEvolucao()
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return getRegistroColaboradorFacade()
				.validarPermissaoAmbulatorioItensPorServidor(servidorLogado.getUsuario(),
						DominioAnamneseEvolucao.E);
	}

	public void atualizarIndImpressaoAnamnese(MamAnamneses anamnese) {
		anamnese.setImpresso(true);
		getMamAnamnesesDAO().atualizar(anamnese);
		getMamAnamnesesDAO().flush();
	}

	public void atualizarIndImpressaoEvolucao(MamEvolucoes evolucao) {
		evolucao.setImpresso(true);
		getMamEvolucoesDAO().atualizar(evolucao);
		getMamEvolucoesDAO().flush();
	}

	public void atualizarIndImpressaoReceituario(MamReceituarios receituario)
			throws ApplicationBusinessException {
		receituario.setIndImpresso(DominioSimNao.S);
		this.getManterReceituarioRN().atualizarReceituario(receituario);
	}

	/**
	 * ORADB MAMC_GET_AGENDADO
	 * 
	 * @param pConNumero
	 * @return
	 */
	private Boolean buscaAgendado(Integer pConNumero) {
		List<MamSituacaoAtendimentos> situacoes = getMamSituacaoAtendimentosDAO()
				.listarSituacoesAtendimentos(pConNumero);

		Boolean retorno = false;
		if (situacoes == null || situacoes.isEmpty()) {
			retorno = true;
		} else {
			retorno = situacoes.get(0).getAgendado();
		}

		return retorno;
	}

	/**
	 * ORADB MAMC_GET_AGUARDANDO
	 * 
	 * @param pConNumero
	 * @return
	 */
	private Boolean buscaAguardando(Integer pConNumero) {
		List<MamSituacaoAtendimentos> situacoes = getMamSituacaoAtendimentosDAO()
				.listarSituacoesAtendimentos(pConNumero);

		Boolean retorno = false;
		if (situacoes == null || situacoes.isEmpty()) {
			retorno = false;
		} else {
			retorno = situacoes.get(0).getAguardando();
		}

		return retorno;
	}

	/**
	 * ORADB MAMC_GET_ATEND_PEND
	 * 
	 * @param pConNumero
	 * @return
	 */
	private Boolean verificaAtendimentoPendente(Integer pConNumero) {
		List<MamSituacaoAtendimentos> situacoes = getMamSituacaoAtendimentosDAO()
				.listarSituacoesAtendimentos(pConNumero);

		Boolean retorno = false;
		if (situacoes == null || situacoes.isEmpty()) {
			retorno = false;
		} else {
			retorno = situacoes.get(0).getAtendPend();
		}

		return retorno;
	}

	/**
	 * ORADB AGHC_VER_CARAC_MICRO
	 * 
	 * @param pConNumero
	 * @return
	 */
	@SuppressWarnings("ucd")
	protected Boolean buscaCaracteristicaMicro(String nome,
			DominioCaracteristicaMicrocomputador caracteristica) {
		Boolean retorno = Boolean.FALSE;
		AghMicrocomputador microcomputador = getAdministracaoFacade()
				.obterAghMicroComputadorPorNomeOuIP(nome, caracteristica);
		if (microcomputador != null
				&& microcomputador.getCacteristicasMicrocomputador() != null
				&& !microcomputador.getCacteristicasMicrocomputador().isEmpty()) {
			retorno = Boolean.TRUE;
		}
		return retorno;
	}

	/**
	 * ORADB: MAMC_GET_ATEND_CONC
	 * 
	 * @param pConNumero
	 */
	private Boolean verificaAtendimentoConcluido(Integer pConNumero) {
		List<MamSituacaoAtendimentos> situacoes = getMamSituacaoAtendimentosDAO()
				.listarSituacoesAtendimentos(pConNumero);

		Boolean retorno = false;
		if (situacoes == null || situacoes.isEmpty()) {
			retorno = false;
		} else {
			retorno = situacoes.get(0).getAtendConcluido();
		}

		return retorno;
	}

	public void inserirLogEmUso(MamLogEmUsos logEmUso) throws ApplicationBusinessException,
			ApplicationBusinessException {
		logEmUso = this.preInserirLogEmUso(logEmUso);
		getMamLogsEmUsoDAO().persistir(logEmUso);
		getMamLogsEmUsoDAO().flush();
	}

	/**
	 * ORADB Trigger MAMT_LES_BRI
	 * 
	 * @param controle
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private MamLogEmUsos preInserirLogEmUso(MamLogEmUsos logEmUso) throws ApplicationBusinessException,
			ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		logEmUso.setCriadoEm(new Date());

		// Obtém Servidor
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(
					MarcacaoConsultaRNExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		}
		logEmUso.setServidor(servidorLogado);

		if (logEmUso.getPaciente() == null && logEmUso.getConsulta() != null) {
			Integer pacCodigo = getMarcacaoConsultaRN()
					.obterCodigoPacienteOrigem(1,
							logEmUso.getConsulta().getNumero());
			if (pacCodigo != null && pacCodigo > 0) {
				AipPacientes paciente = this.getPacienteFacade()
						.obterAipPacientesPorChavePrimaria(pacCodigo);
				logEmUso.setPaciente(paciente);
			}
		}
		return logEmUso;
	}
	
	
	/**
	 * Método que consiste se a grade é de procedimentos (caso o usuário seja técnico de enfermagem)
	 * @param consulta
	 * @throws ApplicationBusinessException
	 */
	private void verificarPerfilTecnicoEnfermagem(AacConsultas consulta) throws ApplicationBusinessException{
		if (cascaFacade.usuarioTemPerfil(this.obterLoginUsuarioLogado(), "ENF05.1")){
			AacGradeAgendamenConsultas grade = consulta.getGradeAgendamenConsulta();
			if (!grade.getProcedimento()){
				AtendimentoPacientesAgendadosRNExceptionCode.CONSULTA_NAO_PERMITE_TECNICO.throwException();
			}
		}

	}

	/**
	 * ORADB: MAMK_GENERICA.MAMC_VALIDA_REGRAS
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void validaRegrasAtendimento(AacConsultas consulta,
			Boolean supervisionar, Boolean atender, String nomeMicrocomputador) throws ApplicationBusinessException,
			ApplicationBusinessException {
		consulta = getAacConsultasDAO().obterPorChavePrimaria(consulta.getNumero());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		Long qtdCaracteristicasEspecialidade = 0l;
		Integer diasReabrirConcluido = 0;
		Integer diasReabrirPendente = 0;
		
		//#51661 - consiste se a grade é de procedimento para usuário técnico de enfermagem
		verificarPerfilTecnicoEnfermagem(consulta);

		Calendar dataInicial = DateUtils.truncate(Calendar.getInstance(),
				Calendar.DATE);
		Calendar dataFinal = DateUtils.truncate(Calendar.getInstance(),
				Calendar.DATE);

		Boolean supervisionaConsulta = getICascaFacade().usuarioTemPermissao(
				servidorLogado != null ? servidorLogado.getUsuario() : null,
				"supervisionarConsulta", "supervisionar");

		AghMicrocomputador micro = getAdministracaoFacade()
				.obterAghMicroComputadorPorNomeOuIP(nomeMicrocomputador, null);

		if (nomeMicrocomputador == null || micro == null) {
			AtendimentoPacientesAgendadosRNExceptionCode.MAM_01889
					.throwException(nomeMicrocomputador);
		}

		if (buscaAgendado(consulta.getNumero())) {
			AtendimentoPacientesAgendadosRNExceptionCode.MAM_01891
					.throwException();
		}

		if (consulta.getControle() == null) {
			AtendimentoPacientesAgendadosRNExceptionCode.MAM_01816
					.throwException();
		}

		if (!(buscaAgendado(consulta.getNumero())
				&& buscaAguardando(consulta.getNumero()) && servidorLogado.equals(consulta.getControle().getServidorReponsavel()))) {

			// Boolean supervisionaConsulta =
			// getPacienteFacade().verificarAcaoQualificacaoMatricula("SUPERVISIONAR CONSULTA",servidor.getId().getVinCodigo(),
			// servidor.getId().getMatricula()).isSim();
			if (verificaAtendimentoPendente(consulta.getNumero()) && supervisionaConsulta) {
				qtdCaracteristicasEspecialidade = getAghuFacade().listarCaracteristicasEspecialidadesCount(
								consulta.getGradeAgendamenConsulta().getEspecialidade().getSeq(),
								DominioCaracEspecialidade.PERMITE_SUPERVISAO_FORA_ZONA);
			}

			micro = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIP(
							nomeMicrocomputador, DominioCaracteristicaMicrocomputador.PERMITE_REGISTRO_ATENDIMENTO_AMBULATORIAL);
			
			if (qtdCaracteristicasEspecialidade.equals(0l) && micro == null) {
				AtendimentoPacientesAgendadosRNExceptionCode.MAM_04133.throwException();
			}
		}

		if (verificaAtendimentoPendente(consulta.getNumero())) {
			dataFinal.add(Calendar.DATE, 1);
		}

		try {
			// recupera parametro de sistema que contém o respectivo número de
			// dias
			AghParametros pReabrirConcluido = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_DIAS_REABRIR_CONCLUIDO);
			diasReabrirConcluido = pReabrirConcluido.getVlrNumerico().intValue();
		} catch (ApplicationBusinessException e) {
			diasReabrirConcluido = 1;
		}

		diasReabrirPendente = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_DIAS_REABRIR_PENDENTE).getVlrNumerico().intValue();
		if (verificaAtendimentoConcluido(consulta.getNumero())) {
			dataInicial.add(Calendar.DATE, -diasReabrirConcluido);
		} else {
			dataInicial.add(Calendar.DATE, -diasReabrirPendente);
		}

		if (DateUtil.truncaData(consulta.getDtConsulta()).before(
				dataInicial.getTime())
				|| DateUtil.truncaData(consulta.getDtConsulta()).after(
						dataFinal.getTime())) {
			AtendimentoPacientesAgendadosRNExceptionCode.MAM_01893
					.throwException();
		}

		// criticas de perfil
		if (supervisionar && supervisionaConsulta) {
			AtendimentoPacientesAgendadosRNExceptionCode.MAM_01894
					.throwException();
		}

		Boolean atendeConsulta = getICascaFacade().usuarioTemPermissao(
				servidorLogado != null ? servidorLogado.getUsuario() : null,
				"atenderConsulta", "atender");
		if (atender && !atendeConsulta) {
			AtendimentoPacientesAgendadosRNExceptionCode.MAM_01895
					.throwException();
		}
	}

	public List<MamItemReceituario> atualizarReceituarioGeral(
			MamReceituarios receitaGeral, AacConsultas consultaSelecionada,
			Integer viasGeral, List<MamItemReceituario> itemReceitaGeralList) throws BaseException,
			CloneNotSupportedException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		consultaSelecionada = getAacConsultasDAO().obterPorChavePrimaria(consultaSelecionada.getNumero());
		
		List<MamItemReceituario> novoItemReceitaGeralList = new ArrayList<MamItemReceituario>();
		if (receitaGeral.getSeq() == null) {
			receitaGeral.setTipo(DominioTipoReceituario.G);
			receitaGeral.setConsulta(consultaSelecionada);
			receitaGeral.setNroVias(viasGeral.byteValue());
			getManterSumarioAltaReceitasON().inserir(receitaGeral);
			if (consultaSelecionada.getReceituarios() == null) {
				consultaSelecionada
						.setReceituarios(new HashSet<MamReceituarios>());
			}
			consultaSelecionada.getReceituarios().add(receitaGeral);
		} else {
			if (receitaGeral.getPendente().equals(
					DominioIndPendenteAmbulatorio.P)) {
				receitaGeral.setNroVias(viasGeral.byteValue());
				getManterReceituarioRN().atualizarReceituario(receitaGeral);
			} else if (receitaGeral.getPendente().equals(
					DominioIndPendenteAmbulatorio.V)) {
				receitaGeral.setPendente(DominioIndPendenteAmbulatorio.A);
				receitaGeral.setDthrMvto(new Date());
				receitaGeral.setServidorMovimento(servidorLogado);
				getManterReceituarioRN().atualizarReceituario(receitaGeral);
				// Insere novo receituario como pendente
				MamReceituarios novaReceita = new MamReceituarios();
				novaReceita.setTipo(DominioTipoReceituario.G);
				novaReceita.setConsulta(consultaSelecionada);
				novaReceita.setNroVias(viasGeral.byteValue());
				novaReceita.setReceituario(receitaGeral);
				getManterSumarioAltaReceitasON().inserir(novaReceita);
				// Copia os itens do receituario que foi alterado para o
				// receituario pendente
				for (MamItemReceituario mamItemReceituario : itemReceitaGeralList) {
					MamItemReceituario itemReceituarioPendente = (MamItemReceituario) mamItemReceituario
							.clone();
					itemReceituarioPendente.setReceituario(novaReceita);
					itemReceituarioPendente.setVersion(0);
					novoItemReceitaGeralList.add(itemReceituarioPendente);
					getManterSumarioAltaReceitasON().inserirItem(novaReceita,
							itemReceituarioPendente);
				}
				receitaGeral = novaReceita;
			}
		}
		return novoItemReceitaGeralList;
	}

	public List<MamItemReceituario> atualizarReceituarioEspecial(
			MamReceituarios receitaEspecial, AacConsultas consultaSelecionada,
			Integer viasEspecial,
			List<MamItemReceituario> itemReceitaEspecialList) throws BaseException,
			CloneNotSupportedException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		consultaSelecionada = getAacConsultasDAO().obterPorChavePrimaria(consultaSelecionada.getNumero());
				
		List<MamItemReceituario> novoItemReceitaEspecialList = new ArrayList<MamItemReceituario>();
		if (receitaEspecial.getSeq() == null) {
			receitaEspecial.setTipo(DominioTipoReceituario.E);
			receitaEspecial.setConsulta(consultaSelecionada);
			receitaEspecial.setNroVias(viasEspecial.byteValue());
			getManterSumarioAltaReceitasON().inserir(receitaEspecial);
			if (consultaSelecionada.getReceituarios() == null) {
				consultaSelecionada
						.setReceituarios(new HashSet<MamReceituarios>());
			}
			consultaSelecionada.getReceituarios().add(receitaEspecial);
		} else {
			if (receitaEspecial.getPendente().equals(
					DominioIndPendenteAmbulatorio.P)) {
				receitaEspecial.setNroVias(viasEspecial.byteValue());
				getManterReceituarioRN().atualizarReceituario(receitaEspecial);
			} else if (receitaEspecial.getPendente().equals(
					DominioIndPendenteAmbulatorio.V)) {
				receitaEspecial.setPendente(DominioIndPendenteAmbulatorio.A);
				receitaEspecial.setDthrMvto(new Date());
				receitaEspecial.setServidorMovimento(servidorLogado);
				getManterReceituarioRN().atualizarReceituario(receitaEspecial);
				// Insere novo receituario como pendente
				MamReceituarios novaReceita = new MamReceituarios();
				novaReceita.setTipo(DominioTipoReceituario.E);
				novaReceita.setConsulta(consultaSelecionada);
				novaReceita.setNroVias(viasEspecial.byteValue());
				novaReceita.setReceituario(receitaEspecial);
				getManterSumarioAltaReceitasON().inserir(novaReceita);
				// Copia os itens do receituario que foi alterado para o
				// receituario pendente
				for (MamItemReceituario mamItemReceituario : itemReceitaEspecialList) {
					MamItemReceituario itemReceituarioPendente = (MamItemReceituario) mamItemReceituario
							.clone();
					itemReceituarioPendente.setReceituario(novaReceita);
					itemReceituarioPendente.setVersion(0);
					novoItemReceitaEspecialList.add(itemReceituarioPendente);
					getManterSumarioAltaReceitasON().inserirItem(novaReceita,
							itemReceituarioPendente);
				}
				receitaEspecial = novaReceita;
			}
		}
		return novoItemReceitaEspecialList;
	}

	public void excluirReceituarioEspecial(MamReceituarios receituarioEspecial,
			AacConsultas consultaSelecionada)
			throws BaseException {
		receituarioEspecial = getMamReceituariosDAO().obterPorChavePrimaria(receituarioEspecial.getSeq());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (receituarioEspecial.getPendente().equals(
				DominioIndPendenteAmbulatorio.V)) {
			receituarioEspecial.setPendente(DominioIndPendenteAmbulatorio.E);
			receituarioEspecial.setDthrMvto(new Date());
			receituarioEspecial.setServidorMovimento(servidorLogado);
			getManterReceituarioRN().atualizarReceituario(receituarioEspecial);
		} else if (receituarioEspecial.getPendente().equals(
				DominioIndPendenteAmbulatorio.R)
				|| receituarioEspecial.getPendente().equals(
						DominioIndPendenteAmbulatorio.P)) {
			MamReceituarios receituarioEspecialAlterado = receituarioEspecial
					.getReceituario();
			// Caso seja um receituario que passou o pendente de 'V' para 'A'
			if (receituarioEspecialAlterado != null) {
				// O receituario com pendente = 'A' passa para 'E'
				receituarioEspecialAlterado
						.setPendente(DominioIndPendenteAmbulatorio.E);
				getManterReceituarioRN().atualizarReceituario(
						receituarioEspecialAlterado);
			}
			getManterSumarioAltaReceitasON().excluirReceituario(
					receituarioEspecial);
			consultaSelecionada.getReceituarios().remove(receituarioEspecial);
			getAacConsultasDAO().refresh(consultaSelecionada);
		}
	}

	public void excluirReceituarioGeral(MamReceituarios receituarioGeral,
			AacConsultas consultaSelecionada)
			throws BaseException {
		receituarioGeral = getMamReceituariosDAO().obterPorChavePrimaria(receituarioGeral.getSeq());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (DominioIndPendenteAmbulatorio.V.equals(receituarioGeral
				.getPendente())) {
			receituarioGeral.setPendente(DominioIndPendenteAmbulatorio.E);
			receituarioGeral.setDthrMvto(new Date());
			receituarioGeral.setServidorMovimento(servidorLogado);
			getManterReceituarioRN().atualizarReceituario(receituarioGeral);
		} else if (DominioIndPendenteAmbulatorio.R.equals(receituarioGeral
				.getPendente())
				|| DominioIndPendenteAmbulatorio.P.equals(receituarioGeral
						.getPendente())) {
			MamReceituarios receituarioGeralAlterado = receituarioGeral
					.getReceituario();
			// Caso seja um receituario que passou o pendente de 'V' para 'A'
			if (receituarioGeralAlterado != null) {
				// O receituario com pendente = 'A' passa para 'E'
				receituarioGeralAlterado
						.setPendente(DominioIndPendenteAmbulatorio.E);
				getManterReceituarioRN().atualizarReceituario(
						receituarioGeralAlterado);
			}
			getManterSumarioAltaReceitasON().excluirReceituario(
					receituarioGeral);
			consultaSelecionada.getReceituarios().remove(receituarioGeral);
			getAacConsultasDAO().refresh(consultaSelecionada);
		}
	}

	public void excluirReceitaGeral(MamReceituarios receitaGeral,
			MamItemReceituario item, AacConsultas consultaSelecionada,
			Integer viasGeral, List<MamItemReceituario> itemReceitaGeralList) throws BaseException,
			CloneNotSupportedException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		receitaGeral = getMamReceituariosDAO().obterPorChavePrimaria(receitaGeral.getSeq());
		item = getMamItemReceituarioDAO().obterPorChavePrimaria(item.getId());
		
		if (item.getReceituario().getPendente()
				.equals(DominioIndPendenteAmbulatorio.V)) {
			receitaGeral.setPendente(DominioIndPendenteAmbulatorio.A);
			receitaGeral.setDthrMvto(new Date());
			receitaGeral.setServidorMovimento(servidorLogado);
			// Insere novo receituario como pendente
			MamReceituarios novaReceitaGeralPendente = new MamReceituarios();
			novaReceitaGeralPendente.setTipo(DominioTipoReceituario.G);
			novaReceitaGeralPendente.setConsulta(consultaSelecionada);
			novaReceitaGeralPendente.setNroVias(viasGeral.byteValue());
			novaReceitaGeralPendente.setReceituario(receitaGeral);
			getManterSumarioAltaReceitasON().inserir(novaReceitaGeralPendente);
			// Copia os itens do receituario que foi alterado para o receituario
			// pendente
			List<MamItemReceituario> novoItemReceitaGeralList = new ArrayList<MamItemReceituario>();
			for (MamItemReceituario mamItemReceituario : itemReceitaGeralList) {
				MamItemReceituario itemReceituarioPendente = (MamItemReceituario) mamItemReceituario
						.clone();
				itemReceituarioPendente
						.setReceituario(novaReceitaGeralPendente);
				// Não adiciona o item de receita excluido
				if (!item.getId().getSeqp()
						.equals(mamItemReceituario.getId().getSeqp())) {
					novoItemReceitaGeralList.add(itemReceituarioPendente);
					getManterSumarioAltaReceitasON().inserirItem(
							novaReceitaGeralPendente, itemReceituarioPendente);
				}
			}
			itemReceitaGeralList = novoItemReceitaGeralList;
			receitaGeral = new MamReceituarios();
		} else if (item.getReceituario().getPendente()
				.equals(DominioIndPendenteAmbulatorio.R)
				|| item.getReceituario().getPendente()
						.equals(DominioIndPendenteAmbulatorio.P)) {
			getManterSumarioAltaReceitasON().excluir(item);
			if (getManterSumarioAltaReceitasON().buscarItensReceita(
					receitaGeral).isEmpty()) {
				getManterSumarioAltaReceitasON().excluirReceituario(
						receitaGeral);
			}
		}
	}

	public void excluirReceitaEspecial(MamReceituarios receitaEspecial,
			MamItemReceituario item, AacConsultas consultaSelecionada,
			Integer viasEspecial,
			List<MamItemReceituario> itemReceitaEspecialList) throws BaseException,
			CloneNotSupportedException {
		receitaEspecial = getMamReceituariosDAO().obterPorChavePrimaria(receitaEspecial.getSeq());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		item = getMamItemReceituarioDAO().obterPorChavePrimaria(item.getId());
		if (item.getReceituario().getPendente()
				.equals(DominioIndPendenteAmbulatorio.V)) {
			receitaEspecial.setPendente(DominioIndPendenteAmbulatorio.A);
			receitaEspecial.setDthrMvto(new Date());
			receitaEspecial.setServidorMovimento(servidorLogado);
			// Insere novo receituario como pendente
			MamReceituarios novaReceitaEspecialPendente = new MamReceituarios();
			novaReceitaEspecialPendente.setTipo(DominioTipoReceituario.E);
			novaReceitaEspecialPendente.setConsulta(consultaSelecionada);
			novaReceitaEspecialPendente.setNroVias(viasEspecial.byteValue());
			novaReceitaEspecialPendente.setReceituario(receitaEspecial);
			getManterSumarioAltaReceitasON().inserir(
					novaReceitaEspecialPendente);
			// Copia os itens do receituario que foi alterado para o receituario
			// pendente
			List<MamItemReceituario> novoItemReceitaEspecialList = new ArrayList<MamItemReceituario>();
			for (MamItemReceituario mamItemReceituario : itemReceitaEspecialList) {
				MamItemReceituario itemReceituarioPendente = (MamItemReceituario) mamItemReceituario
						.clone();
				itemReceituarioPendente
						.setReceituario(novaReceitaEspecialPendente);
				// Não adiciona o item de receita excluido
				if (!item.getId().getSeqp()
						.equals(mamItemReceituario.getId().getSeqp())) {
					novoItemReceitaEspecialList.add(itemReceituarioPendente);
					getManterSumarioAltaReceitasON().inserirItem(
							novaReceitaEspecialPendente,
							itemReceituarioPendente);
				}
			}
			itemReceitaEspecialList = novoItemReceitaEspecialList;
			receitaEspecial = new MamReceituarios();
		} else if (item.getReceituario().getPendente()
				.equals(DominioIndPendenteAmbulatorio.R)
				|| item.getReceituario().getPendente()
						.equals(DominioIndPendenteAmbulatorio.P)) {
			getManterSumarioAltaReceitasON().excluir(item);
			if (getManterSumarioAltaReceitasON().buscarItensReceita(
					receitaEspecial).isEmpty()) {
				getManterSumarioAltaReceitasON().excluirReceituario(
						receitaEspecial);
			}
		}
	}
	
	/**
	 * @ORADB: VISUALIZA_CONSULTA_ATUAL
	 * @param conNumero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String visualizarConsultaAtual(Integer conNumero) throws ApplicationBusinessException {
		
		StringBuilder retorno = new StringBuilder();
		AacConsultas consulta = this.aacConsultasDAO.obterPorChavePrimaria(conNumero);
		
		// Anamnese
		StringBuilder vAnamnese = this.visualizarConsultaAtualAnamnese(consulta);
		
		// Evolução
		StringBuilder vEvolucao = this.visualizarConsultaAtualEvolucao(consulta);
		
		// Dados de triagem
		StringBuilder vTriagem = new StringBuilder();
		List<MamTrgEncInterno> mamTrgEncInternos = this.mamTrgEncInternoDAO.obterPorConsulta(consulta);		
		if (mamTrgEncInternos != null && !mamTrgEncInternos.isEmpty()) {
			StringBuilder triagem = this.ambulatorioFacade
					.obterEmergenciaVisTriagemCon(mamTrgEncInternos.get(0).getId().getTrgSeq(), "CS");
			if (StringUtils.isNotBlank(triagem)) {
				vTriagem.append(triagem);
				vEvolucao.append(NEWLINE);
			}
		}
		
		StringBuilder vCons = this.obterDadosBasicosConsulta(consulta);
		
		StringBuilder vLembretes = this.mamLembreteRN.obterLembretePorNumeroConsulta(conNumero);
		
		if (StringUtils.isNotBlank(vTriagem)) {
			retorno.append(vTriagem)
				.append(NEWLINE)
				.append(NEWLINE);
		}
		
		retorno.append(vCons)
			.append(NEWLINE)
			.append(vLembretes);
		
		if (StringUtils.isNotBlank(vAnamnese)) {
			retorno.append(NEWLINE)
				.append(vAnamnese);
		}
		
		if (StringUtils.isNotBlank(vEvolucao)) {
			retorno.append(NEWLINE)
				.append(vEvolucao);
		}
		
		return retorno.toString();
	}
	
	private StringBuilder visualizarConsultaAtualAnamnese(AacConsultas consulta) throws ApplicationBusinessException {
		StringBuilder vAnamnese = new StringBuilder();
		List<MamAnamneses> mamAnamneses = this.mamAnamnesesDAO.obterAnamnesesPorConsultaIndPendenteSemMvto(consulta.getNumero());
		for (MamAnamneses mamAnamnese : mamAnamneses) {
			StringBuilder anamnese = this.obtemDescricaoAnamnese(mamAnamnese,false, null, false, false);
			if (StringUtils.isNotBlank(anamnese)) {
				vAnamnese.append(anamnese);
				vAnamnese.append(NEWLINE);
			}
		}
		// Notas adicionais anamnese
		if (StringUtils.isBlank(vAnamnese)) {
			StringBuilder vNotasAdicionaisAna = this
					.obtemDescricaoNotaAdicionalAna(consulta, DominioTipoNotaAdicional.N2, false);
			if (StringUtils.isNotBlank(vNotasAdicionaisAna)) {
				vAnamnese.append(vNotasAdicionaisAna);
				vAnamnese.append(NEWLINE);
			}
		}
		return vAnamnese;
	}
	
	private StringBuilder visualizarConsultaAtualEvolucao(AacConsultas consulta) throws ApplicationBusinessException {
		StringBuilder vEvolucao = new StringBuilder();
		List<MamEvolucoes> mamEvolucoes = this.mamEvolucoesDAO.obterEvolucoesPorConsultaIndPendenteSemMvto(consulta.getNumero());
		for (MamEvolucoes mamEvolucao : mamEvolucoes) {
			StringBuilder evolucao = this.obtemDescricaoEvolucao(mamEvolucao,false, null, false, false);
			if (StringUtils.isNotBlank(evolucao)) {
				vEvolucao.append(evolucao);
				vEvolucao.append(NEWLINE);
			}
		}

		// Notas adicionais evolução
		if (StringUtils.isBlank(vEvolucao)) {
			StringBuilder vNotasAdicionaisEvo = this
					.obtemDescricaoNotaAdicionalEvo(consulta, DominioTipoNotaAdicional.N2, false);
			if (StringUtils.isNotBlank(vNotasAdicionaisEvo)) {
				vEvolucao.append(vNotasAdicionaisEvo);
				vEvolucao.append(NEWLINE);
			}
		}
		return vEvolucao;
	}
	
	
	/**
	 * mamc_get_aguard_aten
	 * @param conNumero
	 * @return
	 */
	public Boolean buscaAguardandoAten(Integer conNumero){
		
		List<MamSituacaoAtendimentos> controle = mamSituacaoAtendimentosDAO.pesquisarSituacoesAtendimentosPorConsulta(conNumero);
			if(controle!=null && (controle.get(0).getAgendado()||controle.get(0).getAguardando())){
				return Boolean.TRUE;
			}else{
				controle = mamSituacaoAtendimentosDAO.pesquisarSituacoesAtendimentosPorConsultaCur2(conNumero);
				if(controle!=null && (controle.get(0).getAgendado()||controle.get(0).getAguardando())){
					return Boolean.TRUE;
				}
			}
		return Boolean.FALSE;
	}
	
	/**
	 * F1 43174
	 * @param consultaPendenteVO
	 * @param pHostname
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean reabrirConsulta(PesquisarConsultasPendentesVO consultaPendenteVO,String pHostname) throws ApplicationBusinessException{
				
		//Boolean vAgendaLiberada = Boolean.FALSE;
		Boolean vRetorno = Boolean.FALSE;
		Boolean vAguardando= Boolean.FALSE;
		Boolean vAguardAten = Boolean.FALSE;
		Boolean  vAgendado = Boolean.FALSE;
		
		if(consultaPendenteVO.getPrioridade()==1){
			
			vAguardando  = buscaAguardando(consultaPendenteVO.getNumero()); 
			vAguardAten = buscaAguardandoAten(consultaPendenteVO.getNumero());
			vAgendado = buscaAgendado(consultaPendenteVO.getNumero());
			if(!(!vAgendado && vAguardAten && vAguardando)){
				//atender=false;
				AtendimentoPacientesAgendadosRNExceptionCode.MAM_02672
				.throwException();
				return false;
			}
		}
		vRetorno = Boolean.FALSE;
		if(consultaPendenteVO.getPrioridade()==2){
			vRetorno = verificaAtendimentoPendente(consultaPendenteVO.getNumero());
			if(!vRetorno){
				//atender=false;
				AtendimentoPacientesAgendadosRNExceptionCode.MAM_02670
				.throwException();
				return false;
			}
		}
		if(consultaPendenteVO.getPrioridade()==3){
			vRetorno = verificaAtendimentoPendente(consultaPendenteVO.getNumero());
			if(!vRetorno){
				//atender=false;
				AtendimentoPacientesAgendadosRNExceptionCode.MAM_02671
				.throwException();
				return false;
			}
		}
		if(consultaPendenteVO.getPrioridade()==4){
			vAguardando  = buscaAguardando(consultaPendenteVO.getNumero());
			vAgendado = buscaAgendado(consultaPendenteVO.getNumero());
			vAguardAten = buscaAguardandoAten(consultaPendenteVO.getNumero());
			if(!(!vAgendado && !vAguardando && vAguardAten)){
				//atender=false;
				AtendimentoPacientesAgendadosRNExceptionCode.MAM_02673
				.throwException();
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * @ORADB p_chama_portal
	 * @param conNumero
	 * @throws BaseException 
	 */
	public void chamaPortal(PesquisarConsultasPendentesVO consultaPendenteVO,String pHostname) throws BaseException{
		
		//AghAtendimentos aghatendimento = getAghAtendimentoDAO().obterAtendimentoPacientePorNumeroConsulta(consultaPendenteVO.getNumero());
		AacConsultas aacConsultasGraAacConsultas = aacConsultasDAO.obterAacConsultasJoinGradeEEspecialidadeUnidadeFuncional(consultaPendenteVO.getNumero());
		Date cData = new Date();
		if(aacConsultasGraAacConsultas.getControle()!=null){
			if (aacConsultasGraAacConsultas.getDthrInicio()==null){// RF01 estória 43174
				ambulatorioFacade.atualizarDataInicioAtendimento(consultaPendenteVO.getNumero(), new Date(), pHostname);
			}
			if(!aacConsultasGraAacConsultas.getControle().getSituacao().equals(DominioSituacaoControle.U)){
				ambulatorioFacade.atualizaControleAguardandoLivre(consultaPendenteVO.getNumero(),cData,pHostname);
			}
			else{
				ambulatorioFacade.atualizaControleAguardandoUso(consultaPendenteVO.getNumero(),cData);
			}	
		}
	}
	
// Metodos para  Atestado

	public List<MamTipoAtestado> listarTodos() {
		return getMamTipoAtestadoDao().listarTodos();
	}

	public void inserirAtestadoAmbulatorio(MamAtestados atestado) {
		if (atestado.getSeq() == null) {
			this.getMamAtestadosDAO().persistir(atestado);
		} else {
			this.getMamAtestadosDAO().merge(atestado);
		}
		this.getMamAtestadosDAO().flush();
	}

	public List<RelatorioAtestadoVO> recuperarInformacoesConsultaAtestados(
			List<MamAtestados> consultas) throws ApplicationBusinessException {

		List<RelatorioAtestadoVO> listaVO = new ArrayList<RelatorioAtestadoVO>();

		for (MamAtestados dadoAtestado : consultas) {
			dadoAtestado = getMamAtestadosDAO().obterPorChavePrimaria(dadoAtestado.getSeq());
			RelatorioAtestadoVO voAtestados = new RelatorioAtestadoVO();

			recuperarDadosAtestadoPaciente(dadoAtestado, voAtestados);
			recuperarConselhoRegionalAtestado(dadoAtestado, voAtestados);
			recuperarDadosEspecificoAtestado(dadoAtestado, voAtestados);
			recuperarDatasInicialFinal(dadoAtestado, voAtestados);
			recuperarHorarioInicialFinal(dadoAtestado, voAtestados);
			recuperaTurnoAtestado(dadoAtestado, voAtestados);

			listaVO.add(voAtestados);
		}

		return listaVO;
	}
	
	public void recuperarConselhoRegionalAtestado(MamAtestados atestado, RelatorioAtestadoVO relatorioAtestado){
		RapQualificacao qualificacao ;
		RapServidores servidor = atestado.getServidor();
		qualificacao = getRegistroColaboradorFacade().obterRapQualificacaoPorServidor(servidor);
		relatorioAtestado.setConselhoRegional(qualificacao.getNroRegConselho() == null ? "" : qualificacao.getNroRegConselho());
		relatorioAtestado.setSiglaConselhoRegional(siglaConselhoRegionalIgualNull(qualificacao) ? "" : qualificacao.getTipoQualificacao().getConselhoProfissional().getSigla());
	}

	private boolean siglaConselhoRegionalIgualNull(RapQualificacao qualificacao) {
		return qualificacao.getTipoQualificacao() == null
				|| qualificacao.getTipoQualificacao().getConselhoProfissional() == null
				|| qualificacao.getTipoQualificacao().getConselhoProfissional().getSigla() == null;
	}

	public void recuperarDadosAtestadoPaciente(MamAtestados atestado, RelatorioAtestadoVO relatorioAtestado) {
		relatorioAtestado.setNomePaciente(nomePacienteIgualNull(atestado) ? ""	: atestado.getConsulta().getPaciente().getNome());
		relatorioAtestado.setNomeAcompanhante(atestado.getNomeAcompanhante() == null ? "" : atestado.getNomeAcompanhante());
		
	}

	private boolean nomePacienteIgualNull(MamAtestados atestado) {
		return atestado.getConsulta() == null || atestado.getConsulta().getPaciente() == null
				|| atestado.getConsulta().getPaciente().getNome() == null;
	}

	public void recuperarDadosEspecificoAtestado(MamAtestados atestado, RelatorioAtestadoVO relatorioAtestado) {
		relatorioAtestado.setTipoAtestado(atestado.getMamTipoAtestado() == null || atestado.getMamTipoAtestado().getDescricao() == null ? "" : atestado.getMamTipoAtestado().getDescricao());
		relatorioAtestado.setNomeMedico(nomeMedicoIgualNull(atestado) ? "" : atestado.getServidor().getPessoaFisica().getNome());
		if (atestado.getDiagnosticoImpresso()) {
			relatorioAtestado.setCid(atestado.getAghCid() == null || atestado.getAghCid().getCodigo() == null ? "" : atestado.getAghCid().getCodigo());
		}
		relatorioAtestado.setObservacao(atestado.getObservacao() == null ? "" : atestado.getObservacao());
		relatorioAtestado.setImprimiu(atestado.getIndImpresso());
	
	}

	private boolean nomeMedicoIgualNull(MamAtestados atestado) {
		return atestado.getServidor() == null || atestado.getServidor().getPessoaFisica().getNome() == null;
	}

	public void recuperarDatasInicialFinal(MamAtestados atestado,
			RelatorioAtestadoVO relatorioAtestado) {
		if (atestado.getDataInicial() != null) {
			String dataInicial = new SimpleDateFormat(MamAtestados.Fields.DATA.toString()).format(atestado.getDataInicial());
			relatorioAtestado.setDataInicial(dataInicial);
		}
		if (atestado.getDataFinal() != null) {
			String dataFinal = new SimpleDateFormat(MamAtestados.Fields.DATA.toString()).format(atestado.getDataFinal());
			relatorioAtestado.setDataFinal(dataFinal);
		}
		if (atestado.getDthrCriacao() != null) {
			String dataCriacao = new SimpleDateFormat(MamAtestados.Fields.DATA.toString()).format(atestado.getDthrCriacao());
			relatorioAtestado.setDataCriacao(dataCriacao);
		}
	}

	private void recuperarHorarioInicialFinal(MamAtestados atestado, RelatorioAtestadoVO relatorioAtestado) {
		if (atestado.getDataInicial() != null) {
			String horaInicial = new SimpleDateFormat(MamAtestados.Fields.HORAS.toString()).format(atestado.getDataInicial());
			relatorioAtestado.setHorarioInicial(horaInicial);
		}
		if (atestado.getDataFinal() != null) {
			String horaFinal = new SimpleDateFormat(MamAtestados.Fields.HORAS.toString()).format(atestado.getDataFinal());
			relatorioAtestado.setHorarioFinal(horaFinal);
		}
	}

	public void recuperaTurnoAtestado(MamAtestados atestado, RelatorioAtestadoVO relatorioAtestado) {

		if (atestado.getTurnoConsulta() != null	&& atestado.getTurnoConsulta().toString().equalsIgnoreCase("M")) {
						relatorioAtestado.setTurno(MamAtestados.Fields.MANHA.toString());
					} else {
						if (atestado.getTurnoConsulta() != null	&& atestado.getTurnoConsulta().toString().equalsIgnoreCase("T")) {
							relatorioAtestado.setTurno(MamAtestados.Fields.TARDE.toString());
						} else {
							relatorioAtestado.setTurno(MamAtestados.Fields.NOITE.toString());
						}
					}
		calculaNumeroDiasExatos(atestado, relatorioAtestado);
	}
	
	private void calculaNumeroDiasExatos(MamAtestados atestado,	RelatorioAtestadoVO relatorioAtestado) {
		Integer numeroDias = DateUtil.calcularDiasEntreDatas(atestado.getDataInicial(), atestado.getDataFinal());
		if (numeroDias == 0) {
			numeroDias = 1;
		} else {
			numeroDias = numeroDias + 1;
		}
		relatorioAtestado.setNumeroDiasAtestado(numeroDias.toString());
	}

	protected MamAtestadosDAO getMamAtestadosDAO() {
		return mamAtestadosDAO;
	}
	
	
		
	public MamTipoAtestadoDAO getMamTipoAtestadoDao() {
		return mamTipoAtestadoDao;
	}

	public Boolean verificarAtendimentoHCPA() {
		return this.isHCPA();
	}

	protected MamLogEmUsosDAO getMamLogsEmUsoDAO() {
		return mamLogEmUsosDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected MamItemAnamnesesDAO getMamItemAnamnesesDAO() {
		return mamItemAnamnesesDAO;
	}

	protected MamItemEvolucoesDAO getMamItemEvolucoesDAO() {
		return mamItemEvolucoesDAO;
	}

	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	protected MamTipoItemAnamnesesDAO getMamTipoItemAnamnesesDAO() {
		return mamTipoItemAnamnesesDAO;
	}

	protected MamTipoItemEvolucaoDAO getMamTipoItemEvolucaoDAO() {
		return mamTipoItemEvolucaoDAO;
	}

	protected MarcacaoConsultaRN getMarcacaoConsultaRN() {
		return marcacaoConsultaRN;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ICascaFacade getICascaFacade() {
		return this.cascaFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoFacade() {
		return prescricaoMedicaFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected IAdministracaoFacade getAdministracaoFacade() {
		return administracaoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected MamNotaAdicionalAnamnesesDAO getMamNotaAdicionalAnamnesesDAO() {
		return mamNotaAdicionalAnamnesesDAO;
	}

	protected MamNotaAdicionalEvolucoesDAO getMamNotaAdicionalEvolucoesDAO() {
		return mamNotaAdicionalEvolucoesDAO;
	}

	protected MamAnamnesesDAO getMamAnamnesesDAO() {
		return mamAnamnesesDAO;
	}

	protected MamEvolucoesDAO getMamEvolucoesDAO() {
		return mamEvolucoesDAO;
	}

	protected MamSituacaoAtendimentosDAO getMamSituacaoAtendimentosDAO() {
		return mamSituacaoAtendimentosDAO;
	}

	protected AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
		return ambulatorioConsultaRN;
	}

	protected MamProcedimentoRealizadoDAO getMamProcedimentoRealizadoDAO() {
		return mamProcedimentoRealizadoDAO;
	}

	protected MamEmgServEspecialidadeDAO getMamEmgServEspecialidadeDAO() {
		return mamEmgServEspecialidadeDAO;
	}

	protected MamReceituariosDAO getMamReceituariosDAO() {
		return mamReceituariosDAO;
	}
	
	protected MamItemReceituarioDAO getMamItemReceituarioDAO() {
		return mamItemReceituarioDAO;
	}
	

	protected ConsultasON getConsultasON() {
		return consultasON;
	}

	protected ManterSumarioAltaReceitasON getManterSumarioAltaReceitasON() {
		return manterSumarioAltaReceitasON;
	}

	protected ManterReceituarioRN getManterReceituarioRN() {
		return manterReceituarioRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}

	public AacGradeAgendamenConsultasDAO getAacGradeAgendamenConsultasDAO() {
		return aacGradeAgendamenConsultasDAO;
	}

	public void setAacGradeAgendamenConsultasDAO(
			AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO) {
		this.aacGradeAgendamenConsultasDAO = aacGradeAgendamenConsultasDAO;
	}

	public MamControlesDAO getMamControlesDAO() {
		return mamControlesDAO;
	}

	public void setMamControlesDAO(MamControlesDAO mamControlesDAO) {
		this.mamControlesDAO = mamControlesDAO;
	}

	public AghCaractEspecialidadesDAO getCaractEspecialidadesDAO() {
		return caractEspecialidadesDAO;
	}

	public void setCaractEspecialidadesDAO(
			AghCaractEspecialidadesDAO caractEspecialidadesDAO) {
		this.caractEspecialidadesDAO = caractEspecialidadesDAO;
	}

	public AghAtendimentoDAO getAghAtendimentoDAO() {
		return aghAtendimentoDAO;
	}

	public void setAghAtendimentoDAO(AghAtendimentoDAO aghAtendimentoDAO) {
		this.aghAtendimentoDAO = aghAtendimentoDAO;
	}

	public RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}

	public void setRapServidoresDAO(RapServidoresDAO rapServidoresDAO) {
		this.rapServidoresDAO = rapServidoresDAO;
	}

	
	public void setMamAtestadosDAO(MamAtestadosDAO mamAtestadosDAO) {
		this.mamAtestadosDAO = mamAtestadosDAO;
	}
	
	
}
