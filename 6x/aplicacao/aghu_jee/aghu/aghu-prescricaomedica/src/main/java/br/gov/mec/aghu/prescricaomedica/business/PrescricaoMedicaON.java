package br.gov.mec.aghu.prescricaomedica.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoPacientesDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AbsExameComponenteVisualPrescricao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipAlergiaPacientes;
import br.gov.mec.aghu.model.AipAlergiaPacientesId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MpmAlergiaUsual;
import br.gov.mec.aghu.model.MpmInformacaoPrescribente;
import br.gov.mec.aghu.model.MpmJustificativaNpt;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmTextoPadraoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudoConvenio;
import br.gov.mec.aghu.model.MpmTipoLaudoConvenioId;
import br.gov.mec.aghu.model.MpmTipoLaudoTextoPadrao;
import br.gov.mec.aghu.model.MpmTipoLaudoTextoPadraoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.paciente.dao.AipAlergiaPacientesDAO;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumStatusItem;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEstadoPacienteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmInformacaoPrescribenteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustificativaNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoConvenioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoTextoPadraoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.VMpmPrescrMdtosDAO;
import br.gov.mec.aghu.prescricaomedica.vo.CentralMensagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.ParecerPendenteVO;
import br.gov.mec.aghu.registrocolaborador.dao.VRapPessoaServidorDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class PrescricaoMedicaON extends BaseBusiness {
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private TipoLaudoRN tipoLaudoRN;
	
	@EJB
	private TipoLaudoTextoPadraoRN tipoLaudoTextoPadraoRN;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@Inject
	private VRapPessoaServidorDAO vRapPessoaServidorDAO;
	
	@EJB
	private TipoLaudoConvenioRN tipoLaudoConvenioRN;
	
	@EJB
	private TextoPadraoLaudoRN textoPadraoLaudoRN;
	
	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;
	
	@Inject
	private AghAtendimentoPacientesDAO aghAtendimentoPacientesDAO;

	private static final Log LOG = LogFactory.getLog(PrescricaoMedicaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
		
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@Inject
	private MpmInformacaoPrescribenteDAO mpmInformacaoPrescribenteDAO;
	
	@Inject
	private MpmTipoLaudoTextoPadraoDAO mpmTipoLaudoTextoPadraoDAO;
	
	@Inject	
	private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;
	
	@Inject
	private MpmAltaEstadoPacienteDAO mpmAltaEstadoPacienteDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmTipoLaudoConvenioDAO mpmTipoLaudoConvenioDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@Inject
	private MpmJustificativaNptDAO mpmJustificativaNptDAO;
		
	@Inject
	private VMpmPrescrMdtosDAO vmpmPrescrMdtosDAO;
	
	@Inject
	private AipAlergiaPacientesDAO aipAlergiaPacientesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5502875227111685598L;

	private enum PrescricaoMedicaONExceptionCode implements
			BusinessExceptionCode {
		DATA_MOVIMENTACAO_NULA, MENSAGEM_ERRO_CRIACAO_JA_EXISTE_TEXTO_PADRAO, MENSAGEM_ERRO_CRIACAO_JA_EXISTE_CONVENIO,
		MENSAGEM_CAMPO_OBRIGATORIO_JUSTIFICATIVAS_NPT
	}

	/**
	 * Retorna a prescrição médica pelo id.<br>
	 * Argumentos são a chave composta da prescricao medica.
	 * 
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public MpmPrescricaoMedica obterPrescricaoPorId(Integer atdSeq, Integer seq) {
		return this.getMpmPrescricaoMedicaDAO().obterPrescricaoComAtendimentoPaciente(atdSeq, seq);
	}

	/**
	 * Retorna true se a prescrição médica fornecida está vigente.<br>
	 * Está vigente quando a data/hora atual está entre o inicio e fim da
	 * prescrição.
	 * 
	 * @param prescricaoMedica
	 * @return retorna false em outros casos
	 */
	public boolean isPrescricaoVigente(MpmPrescricaoMedica prescricaoMedica) {
		if (prescricaoMedica == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}
		long hoje = Calendar.getInstance().getTimeInMillis();
		
		MpmPrescricaoMedica prescricaoMedicaLoaded =
				this.getMpmPrescricaoMedicaDAO().obterPorChavePrimaria(prescricaoMedica.getId());
		
		long inicio = prescricaoMedicaLoaded.getDthrInicio().getTime();
		long fim = prescricaoMedicaLoaded.getDthrFim().getTime();
		if (hoje >= inicio && hoje < fim) {
			return true;
		}
		return false;
	}

	public String buscaProcedimentoHospitalarInternoAgrupa(Integer phiSeq,
			Short cnvCodigo, Byte cspSeq, Short phoSeq)
			throws ApplicationBusinessException {
		return getPrescricaoMedicaRN()
				.buscaProcedimentoHospitalarInternoAgrupa(phiSeq, cnvCodigo,
						cspSeq, phoSeq);
	}
	
	public Long buscaDescricaoProcedimentoHospitalarInterno(Integer phiSeq,
			Short cnvCodigo, Byte cspSeq, Short phoSeq)
			throws ApplicationBusinessException {
		return getPrescricaoMedicaRN()
				.buscaDescricaoProcedimentoHospitalarInterno(phiSeq, cnvCodigo,
						cspSeq, phoSeq);
	}

	public String buscaJustificativaItemLaudo(Integer atdSeq, Integer phiSeq) {
		return getPrescricaoMedicaRN().buscaJustificativaItemLaudo(atdSeq,
				phiSeq);
	}

	public void verificaPrescricaoMedica(Integer seqAtendimento,
			Date dataHoraInicio, Date dataHoraFim,
			Date dataHoraMovimentoPendente,
			DominioIndPendenteItemPrescricao pendente, String operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		
		this.getPrescricaoMedicaRN().verificaPrescricaoMedica(seqAtendimento,
				dataHoraInicio, dataHoraFim, dataHoraMovimentoPendente,
				pendente, operacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	public void verificaPrescricaoMedicaUpdate(Integer seqAtendimento,
			Date dataHoraInicio, Date dataHoraFim,
			Date dataHoraMovimentoPendente,
			DominioIndPendenteItemPrescricao pendente, String operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		
		this.getPrescricaoMedicaRN().verificaPrescricaoMedicaUpdate(
				seqAtendimento, dataHoraInicio, dataHoraFim,
				dataHoraMovimentoPendente, pendente, operacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * ORADB MPMP_RESULT_EXAMES
	 * 
	 * Busca resultados de exames existentes para o componente sanguineo e para
	 * o procedimento hemoterápico.
	 * 
	 * Implementação da procedure MPMP_RESULT_EXAMES.
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public List<String> buscaResultadoExames(AipPacientes paciente,
			String codigoComponenteSanguineo,
			String codigoProcedimentoHemoterapico) throws ApplicationBusinessException {
		List<String> resultadosExames = new ArrayList<String>();

		if (StringUtils.isNotBlank(codigoComponenteSanguineo)
				|| StringUtils.isNotBlank(codigoProcedimentoHemoterapico)) {
			List<AbsExameComponenteVisualPrescricao> lista = getBancoDeSangueFacade()
					.buscarExamesComponenteVisualPrescricao(
							codigoComponenteSanguineo,
							codigoProcedimentoHemoterapico);

			if (lista != null && !lista.isEmpty()) {
				for (AbsExameComponenteVisualPrescricao exameComponenteVisualPrescricao : lista) {
					String nomeCampoLaudo = exameComponenteVisualPrescricao
							.getCampoLaudo() != null ? exameComponenteVisualPrescricao
							.getCampoLaudo().getNome()
							: null;
					Short tempo = exameComponenteVisualPrescricao.getTempo();
					String unidadeTempo = (exameComponenteVisualPrescricao.getUnidadeTempo() != null)
							? exameComponenteVisualPrescricao.getUnidadeTempo().toString()
							: null;
					
					Short idadeMinima = exameComponenteVisualPrescricao.getIdadeMinima();
					String unidadeTempoIdadeMinima = (exameComponenteVisualPrescricao.getUnidadeTempoIdadeMinima() != null) 
							? unidadeTempoIdadeMinima = exameComponenteVisualPrescricao.getUnidadeTempoIdadeMinima().toString()
							: null;
					
					Short idadeMaxima = exameComponenteVisualPrescricao.getIdadeMaxima();
					String unidadeTempoIdadeMaxima = (exameComponenteVisualPrescricao.getUnidadeTempoIdadeMaxima() != null) 
							? unidadeTempoIdadeMaxima = exameComponenteVisualPrescricao.getUnidadeTempoIdadeMaxima().toString()
							: null;
					
					if (exameComponenteVisualPrescricao.getTempo() == null) {
						unidadeTempo = "A";
						tempo = 30;
					}
					
					Integer numeroDias = this.acertaTempo(unidadeTempo, tempo);
					
					String resultado = getPrescricaoMedicaRN()
							.buscarResultadoExame(paciente.getCodigo(), new Date(),
									nomeCampoLaudo, numeroDias);
					
					if (idadeMinima == null) {
						unidadeTempoIdadeMinima = "D";
						idadeMinima = 1;
					}
					
					Integer numeroDiasIdadeMinima = this.acertaTempo(
							unidadeTempoIdadeMinima, idadeMinima);
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DAY_OF_MONTH, -numeroDiasIdadeMinima);
					Date dataValidadeMinima = cal.getTime();
					
					if (idadeMaxima == null) {
						unidadeTempoIdadeMaxima = "A";
						idadeMaxima = 100;
					}
					
					Integer numeroDiasMaximo = this.acertaTempo(unidadeTempoIdadeMaxima,
							idadeMaxima);
					cal = Calendar.getInstance();
					
					if (numeroDiasMaximo != null) {
						cal.add(Calendar.DAY_OF_MONTH, -numeroDiasMaximo);
					}
					
					Date dataValidadeMaxima = cal.getTime();
					
					boolean possuiDados = false;
					
					if (CoreUtil.isBetweenDatas(paciente.getDtNascimento(),
							dataValidadeMaxima, dataValidadeMinima)) {
						possuiDados = true;
					}
					
					String resultadoExame = null;
					if (possuiDados) {
						resultadoExame = this
								.formataNomeCampoLaudo(nomeCampoLaudo)
								+ " : " + (resultado != null ? resultado : "");
					} else {
						resultadoExame = this
								.formataNomeCampoLaudo(nomeCampoLaudo)
								+ " sem resultado";
					}
					
					resultadosExames.add(resultadoExame);
				}
			}
		}
		
		return resultadosExames;
	}

	private String formataNomeCampoLaudo(String nomeCampoLaudo) {
		if (nomeCampoLaudo != null && nomeCampoLaudo.length() > 15) {
			nomeCampoLaudo.substring(0, 15);
		}
		return nomeCampoLaudo;
	}
	
	/**
	 * ORADB MPMC_ACERTA_TEMPO
	 * 
	 * Transforma a unidade de tempo informada na Exame_visual em dias.
	 * 
	 * @param unidadeTempo
	 * @param tempo
	 * @return
	 */
	private Integer acertaTempo(String unidadeTempo, Short tempo) {
		Integer numeroDias = null;

		if ("D".equals(unidadeTempo)) {
			numeroDias = Integer.valueOf(tempo);
		} else if ("M".equals(unidadeTempo)) {
			numeroDias = tempo * 30;
		} else if ("A".equals(unidadeTempo)) {
			numeroDias = tempo * 365;
		} else if ("H".equals(unidadeTempo)) {
			numeroDias = tempo * 24;
		}

		return numeroDias;
	}

	protected MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	protected MpmAltaEstadoPacienteDAO getMpmAltaEstadoPacienteDAO() {
		return mpmAltaEstadoPacienteDAO;
	}

	protected PrescricaoMedicaRN getPrescricaoMedicaRN() {
		return prescricaoMedicaRN;
	}
	
	/**
	 * Obtém o número de vias de um relatório conforme a unidade funcional
	 * @param prescricaoMedica
	 * TODO: RETIRAR OS REFRESHS QUANDO AS EXCEPTIONS COM ROLLBACK DOS CRUDS DOS ÍTENS
	 * DA PRESCRIÇÃO TIVEREM SIDO ELIMINADAS
	 * @return
	 */
	public Byte obterNumeroDeViasRelatorio(MpmPrescricaoMedica prescricaoMedica){
		MpmPrescricaoMedicaId prescricaoId = prescricaoMedica.getId();
		prescricaoMedica = getMpmPrescricaoMedicaDAO().obterPorChavePrimaria(prescricaoId);
		
		Byte nroViasPme = null;
		// Local de internação
		if (prescricaoMedica.getAtendimento().getInternacao() != null) {
			AinInternacao internacao = prescricaoMedica.getAtendimento().getInternacao();
			if (internacao.getLeito() != null) {
				super.refresh(internacao.getLeito());
				nroViasPme = internacao.getLeito().getUnidadeFuncional().getNroViasPme();
			} else if (internacao.getQuarto() != null) {
				super.refresh(internacao.getQuarto());
				nroViasPme = internacao.getQuarto().getUnidadeFuncional().getNroViasPme();
			} else if (internacao.getUnidadesFuncionais() != null) {
				getAghuFacade().refreshUnidadesFuncionais(internacao.getUnidadesFuncionais());
				nroViasPme = internacao.getUnidadesFuncionais().getNroViasPme();
			}
		} else if (DominioOrigemAtendimento.N.equals(prescricaoMedica.getAtendimento().getOrigem())){
			nroViasPme = prescricaoMedica.getAtendimento().getUnidadeFuncional().getNroViasPme();
		}else if(prescricaoMedica.getAtendimento().getConsulta() != null){
			AacConsultas consulta = prescricaoMedica.getAtendimento().getConsulta();
			if (consulta.getGradeAgendamenConsulta() != null) {
				nroViasPme = consulta.getGradeAgendamenConsulta().getUnidadeFuncional().getNroViasPme();
			}
		}

		if (nroViasPme == null){
			nroViasPme = 1;
		}
		
		return nroViasPme;
	}
	
	/**
	 * Método que verifica se a impressão solicitada é a primeira da prescrição, situação em que
	 * os relatórios da confirmação da prescrição deverão ser
	 * impressos totalmente, ou se apenas as movimentações deverão ser exibidas. 
	 * Este método é chamado quando o botão clicado for o de "Impressão".
	 * @return
	 */
	public Boolean verificarPrimeiraImpressao(RapServidores servidorValida){
		if (servidorValida == null){
			//É primeira impressão
			return true;
		}
		else{
			//Não é a primeira impressão
			return false;
		}
	}
	
	/**
	 * Método que verifica qual a ação foi aplicada sobre um determinado item de prescrição
	 * @param item
	 * @param dthrMovimento 
	 * @return
	 *  
	 */
	public EnumStatusItem buscarStatusItem(ItemPrescricaoMedica item, Date dthrMovimento) throws ApplicationBusinessException{
		EnumStatusItem retorno = EnumStatusItem.NENHUMA;
		
		//A data de movimento da prescrição médica não pode ser nula
		if (dthrMovimento == null){
			throw new ApplicationBusinessException(PrescricaoMedicaONExceptionCode.DATA_MOVIMENTACAO_NULA);
		}
		if (item.possuiFilhos()){
			retorno = EnumStatusItem.VELHO;
		}
		else if (item.getCriadoEm().compareTo(dthrMovimento) >= 0
				|| (item.getAlteradoEm() != null && item.getAlteradoEm().compareTo(dthrMovimento) >= 0)) {
			
			if (item.getAlteradoEm() == null){
				if (item.getAnterior() == null){
					//INSERT
					retorno = EnumStatusItem.INCLUIDO;
				}
				else{
					//UPDATE
					retorno = EnumStatusItem.ALTERADO;
				}
			}
			else{
				//OPERAÇÃO DE DELETE
				retorno = EnumStatusItem.EXCLUIDO;
			}			
		}
		return retorno;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return this.bancoDeSangueFacade;
	}
	
	
	public void inserirMpmTipoLaudo(MpmTipoLaudo tipoLaudo, boolean flush) throws BaseException {
		this.getTipoLaudoRN().inserirMpmTipoLaudo(tipoLaudo, flush);
	}

	public void atualizarMpmTipoLaudo(MpmTipoLaudo tipoLaudo, boolean flush) throws BaseException {
		this.getTipoLaudoRN().atualizarMpmTipoLaudo(tipoLaudo, flush);
	}

	public void inserirMpmTextoPadraoLaudo(MpmTextoPadraoLaudo textoPadraoLaudo, boolean flush) throws BaseException {
		this.getTextoPadraoLaudoRN().inserirMpmTextoPadraoLaudo(textoPadraoLaudo, flush);
	}

	public void atualizarMpmTextoPadraoLaudo(MpmTextoPadraoLaudo textoPadraoLaudo, boolean flush) throws BaseException {
		this.getTextoPadraoLaudoRN().atualizarMpmTextoPadraoLaudo(textoPadraoLaudo, flush);
	}

	public void inserirMpmTipoLaudoConvenio(MpmTipoLaudoConvenio tipoLaudoConvenio, boolean flush) throws BaseException {
		MpmTipoLaudoConvenio auxTipoLaudoConvenio = this.obterMpmTipoLaudoConvenioPorChavePrimaria(tipoLaudoConvenio.getId());
		if (auxTipoLaudoConvenio != null) {
			throw new ApplicationBusinessException(PrescricaoMedicaONExceptionCode.MENSAGEM_ERRO_CRIACAO_JA_EXISTE_CONVENIO);
		}

		this.getTipoLaudoConvenioRN().inserirMpmTipoLaudoConvenio(tipoLaudoConvenio, flush);
	}

	public void removerMpmTipoLaudoConvenio(MpmTipoLaudoConvenio tipoLaudoConvenio, boolean flush) {
		
		MpmTipoLaudoConvenio mpmTipoLaudoConvenio = this.getMpmTipoLaudoConvenioDAO().obterPorChavePrimaria(tipoLaudoConvenio.getId());
		
		if (mpmTipoLaudoConvenio != null) {
			this.getTipoLaudoConvenioRN().removerMpmTipoLaudoConvenio(mpmTipoLaudoConvenio, flush);
		}
	}

	public void inserirMpmTipoLaudoTextoPadrao(MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao, boolean flush) throws BaseException {
		MpmTipoLaudoTextoPadrao auxTipoLaudoTextoPadrao = this.obterMpmTipoLaudoTextoPadraoPorChavePrimaria(tipoLaudoTextoPadrao
				.getId());
		if (auxTipoLaudoTextoPadrao != null) {
			throw new ApplicationBusinessException(PrescricaoMedicaONExceptionCode.MENSAGEM_ERRO_CRIACAO_JA_EXISTE_TEXTO_PADRAO);
		}

		this.getTipoLaudoTextoPadraoRN().inserirMpmTipoLaudoTextoPadrao(tipoLaudoTextoPadrao, flush);
	}

	private MpmTipoLaudoTextoPadrao obterMpmTipoLaudoTextoPadraoPorChavePrimaria(MpmTipoLaudoTextoPadraoId id) {
		return this.getMpmTipoLaudoTextoPadraoDAO().obterPorChavePrimaria(id);
	}

	private MpmTipoLaudoConvenio obterMpmTipoLaudoConvenioPorChavePrimaria(MpmTipoLaudoConvenioId id) {
		return this.getMpmTipoLaudoConvenioDAO().obterPorChavePrimaria(id);
	}

	public void removerMpmTipoLaudoTextoPadrao(MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao, boolean flush) {
		
		MpmTipoLaudoTextoPadrao mpmTipoLaudoTextoPadrao = this.getMpmTipoLaudoTextoPadraoDAO().obterPorChavePrimaria(tipoLaudoTextoPadrao.getId());
		
		if (mpmTipoLaudoTextoPadrao != null) {
			this.getTipoLaudoTextoPadraoRN().removerMpmTipoLaudoTextoPadrao(mpmTipoLaudoTextoPadrao, flush);
		}
	}
	
	protected TipoLaudoRN getTipoLaudoRN() {
		return tipoLaudoRN;
	}
	
	protected TextoPadraoLaudoRN getTextoPadraoLaudoRN() {
		return textoPadraoLaudoRN;
	}
	
	protected TipoLaudoTextoPadraoRN getTipoLaudoTextoPadraoRN() {
		return tipoLaudoTextoPadraoRN;
	}
	
	protected TipoLaudoConvenioRN getTipoLaudoConvenioRN() {
		return tipoLaudoConvenioRN;
	}

	protected MpmTipoLaudoTextoPadraoDAO getMpmTipoLaudoTextoPadraoDAO() {
		return mpmTipoLaudoTextoPadraoDAO;
	}
	
	protected MpmTipoLaudoConvenioDAO getMpmTipoLaudoConvenioDAO() {
		return mpmTipoLaudoConvenioDAO;
	}

	public MpmPrescricaoMedica obterPrescricaoComFatConvenioSaude(
			Integer atdSeqPrescricao, Integer seqPrescricao) {
		return mpmPrescricaoMedicaDAO.obterPrescricaoComFatConvenioSaude(atdSeqPrescricao, seqPrescricao);
	}
	
	
	public void persistirMpmJustificativasNPT(MpmJustificativaNpt justificativaNpt) throws BaseException {
		if (StringUtils.isEmpty(justificativaNpt.getDescricao())) {
				throw new ApplicationBusinessException(PrescricaoMedicaONExceptionCode.MENSAGEM_CAMPO_OBRIGATORIO_JUSTIFICATIVAS_NPT);
		}
		justificativaNpt.setDescricao(StringUtil.trim(
				justificativaNpt.getDescricao()).toUpperCase());
		if (justificativaNpt.getSeq() == null) {
			this.inserirJustificativaNPT(justificativaNpt);
		} else {
			this.atualizarJustificativaNPT(justificativaNpt);
		}
	}
	
	/**
	 * Lista conjunto de mensagens a serem exibidas na popup Central de Mensagens.
	 * 
	 * @param atdSeq - Código do Atendimento
	 * 
	 * @return Lista de mensagens
	 */
	public List<CentralMensagemVO> listarMensagensPendentes(Integer atdSeq) {

		List<CentralMensagemVO> retorno = new ArrayList<CentralMensagemVO>(); 

		List<MpmSolicitacaoConsultoria> consultorias = mpmSolicitacaoConsultoriaDAO.listarSolicitacaoConsultoriaPendentesPorCodigoAtendimento(atdSeq);

		for (MpmSolicitacaoConsultoria consultoria : consultorias) {
			CentralMensagemVO mensagem = new CentralMensagemVO();

			mensagem.setEntidade(consultoria);
			mensagem.setMensagem(getResourceBundleValue("CENTRAL_MENSAGENS_RETORNO_CONSULTORIA", consultoria.getTipo().getDescricao(),
					consultoria.getIndUrgencia().isSim() ? getResourceBundleValue("CENTRAL_MENSAGENS_LABEL_URGENTE") : "",
							consultoria.getEspecialidade().getNomeEspecialidade()));

			retorno.add(mensagem);
		}

		List<ParecerPendenteVO> pareceres = vmpmPrescrMdtosDAO.listarPareceresPendentesPorCodigoAtendimento(atdSeq);

		for (ParecerPendenteVO parecer : pareceres) {
			CentralMensagemVO mensagem = new CentralMensagemVO();

			mensagem.setEntidade(parecer);
			mensagem.setMensagem(getResourceBundleValue("CENTRAL_MENSAGENS_PARECER", parecer.getMedicamento(), parecer.getDose(),
					parecer.getAprazamento(), parecer.getViaAdministracao(),
					parecer.getDescricao()));

			retorno.add(mensagem);
		}

		List<MpmInformacaoPrescribente> prescribentes = mpmInformacaoPrescribenteDAO.listarPrescribentesPendentesPorCodigoAtendimentoPrescricao(atdSeq);

		for (MpmInformacaoPrescribente prescribente : prescribentes) {
			CentralMensagemVO mensagem = new CentralMensagemVO();

			SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

			mensagem.setEntidade(prescribente);
			mensagem.setMensagem(getResourceBundleValue("CENTRAL_MENSAGENS_PRESCRIBENTE",
					sdfData.format(prescribente.getPrescricaoMedica().getDtReferencia()),
					sdfHora.format(prescribente.getPrescricaoMedica().getDthrInicio()), sdfHora.format(prescribente.getPrescricaoMedica().getDthrFim()),
					prescribente.getServidorConselho().getNome(), prescribente.getServidorConselho().getSigla(),
					prescribente.getServidorConselho().getNroRegConselho()));

			retorno.add(mensagem);
		}

		return retorno;
	}

	
	/**
	 * @ORADB MPMC_CALC_IDADE_ANOS
	 * 
	 * Retornar a idade do paciente em anos baseado em sua data de nascimento.
	 * 
	 * @param codPaciente
	 * @return
	 */
	
	public Integer obterIdadePaciente(Integer codPaciente){
		Integer idadePaciente = null;
		Date dtNascimentoPaciente = aghAtendimentoPacientesDAO.obterDtNascimentoPaciente(codPaciente);
		if (dtNascimentoPaciente != null) {
			idadePaciente = DateUtil.getIdade(dtNascimentoPaciente);
		}
		return idadePaciente;
	}
	
	public List<AipAlergiaPacientes> atualizarListaAipAlergiaPacientes(List<AipAlergiaPacientes> listaAipAlergiaPacientes, AipAlergiaPacientes aipPacientesSelecionado,
			MpmAlergiaUsual mpmAlergiaUsualSelecionado, boolean motivoCancelamentoHabilitado, String descricaoNaoCadastrado, String motivoCancelamento, RapServidores servidorLogado) throws ApplicationBusinessException, CloneNotSupportedException{
		for (AipAlergiaPacientes aipAlergiaPacientes : listaAipAlergiaPacientes) {
			if(aipPacientesSelecionado.getId().getSeq().equals(aipAlergiaPacientes.getId().getSeq())){
				if((descricaoNaoCadastrado != null && !descricaoNaoCadastrado.trim().isEmpty()) && mpmAlergiaUsualSelecionado != null){
					throw new ApplicationBusinessException(ValidaAlteracaoInclusaoAipAlergiaPacientesExceptionCode.AIP_00184);
				}else{
					if(aipPacientesSelecionado.getId().getSeq() > 0){
						
						AipAlergiaPacientes clone = (AipAlergiaPacientes) aipAlergiaPacientes.clone();
						
						if(motivoCancelamentoHabilitado){
							clone.setIndSituacao("V");
						}else{
							clone.setIndSituacao("I");
						}
						
						clone.setMotivo(motivoCancelamento);
						
						if(mpmAlergiaUsualSelecionado != null){
							clone.setAusSeq(mpmAlergiaUsualSelecionado.getSeq());
						}
						
						if(descricaoNaoCadastrado != null && !descricaoNaoCadastrado.trim().isEmpty()){
							clone.setDescricao(descricaoNaoCadastrado);
						}
						
						this.alterarAipAlergiaPacientes(clone, servidorLogado);
						
						this.alterarPosValidacaoAipAlergiaPaciente(aipAlergiaPacientes, clone);
						
						break;
					}else{
						
						if(motivoCancelamentoHabilitado){
							aipAlergiaPacientes.setIndSituacao("V");
						}else{
							aipAlergiaPacientes.setIndSituacao("I");
						}
						
						aipAlergiaPacientes.setMotivo(motivoCancelamento);
						
						if(mpmAlergiaUsualSelecionado != null){
							aipAlergiaPacientes.setAusSeq(mpmAlergiaUsualSelecionado.getSeq());
							aipAlergiaPacientes.setMpmAlergiaUsual(mpmAlergiaUsualSelecionado);
						}
						
						if(descricaoNaoCadastrado != null && !descricaoNaoCadastrado.trim().isEmpty()){
							aipAlergiaPacientes.setDescricao(descricaoNaoCadastrado);
						}
						
						break;
					}
					
				}
			}
		}
		return listaAipAlergiaPacientes;
	}
	
	public void adicionarAipAlergiaPacientes(List<AipAlergiaPacientes> listaAipAlergiaPacientes, AipAlergiaPacientes aipPacientesSelecionado,
			MpmAlergiaUsual mpmAlergiaUsualSelecionado, boolean motivoCancelamentoHabilitado, String descricaoNaoCadastrado, String motivoCancelamento, int contador,
			AipPacientes paciente) throws ApplicationBusinessException{
		if((descricaoNaoCadastrado != null && !descricaoNaoCadastrado.trim().isEmpty()) &&
				mpmAlergiaUsualSelecionado != null){
			throw new ApplicationBusinessException(ValidaAlteracaoInclusaoAipAlergiaPacientesExceptionCode.AIP_00184);
		}else{
			AipAlergiaPacientes aipAlergiaPacientes = new AipAlergiaPacientes();
			aipAlergiaPacientes.setId(new AipAlergiaPacientesId());
			
			aipAlergiaPacientes.getId().setSeq(contador);
			aipAlergiaPacientes.getId().setPacCodigo(paciente.getCodigo());
			
			aipAlergiaPacientes.setDescricao(descricaoNaoCadastrado);
			
			if(mpmAlergiaUsualSelecionado != null){
				aipAlergiaPacientes.setAusSeq(mpmAlergiaUsualSelecionado.getSeq());
				aipAlergiaPacientes.setMpmAlergiaUsual(mpmAlergiaUsualSelecionado);
			}
			
			if(motivoCancelamentoHabilitado){
				aipAlergiaPacientes.setIndSituacao("V");
			}else{
				aipAlergiaPacientes.setIndSituacao("I");
			}
			
			if(motivoCancelamento != null && !motivoCancelamento.trim().isEmpty()){
				aipAlergiaPacientes.setMotivo(motivoCancelamento);		
			}
			listaAipAlergiaPacientes.add(aipAlergiaPacientes);
		}
	}
	
	public void alterarPosValidacaoAipAlergiaPaciente(AipAlergiaPacientes aipAlergiaPacientes, AipAlergiaPacientes clone){
		aipAlergiaPacientes.setIndSituacao(clone.getIndSituacao());
		aipAlergiaPacientes.setMotivo(clone.getMotivo());
		aipAlergiaPacientes.setAlteradoEm(clone.getAlteradoEm());
		aipAlergiaPacientes.setSerMatriculaInativada(clone.getSerMatriculaInativada());
		aipAlergiaPacientes.setSerVinCodigoInativada(clone.getSerVinCodigoInativada());
		aipAlergiaPacientes.setSerVinCodigo(clone.getSerVinCodigo());
		aipAlergiaPacientes.setSerMatricula(clone.getSerMatricula());
	}
	
	public void gravarAipAlergiaPacientes(List<AipAlergiaPacientes> listaAipAlergiaPacientes, RapServidores servidorLogado) throws ApplicationBusinessException{
		
		for (AipAlergiaPacientes aipAlergiaPacientes : listaAipAlergiaPacientes) {
			if(aipAlergiaPacientes.getId().getSeq() > 0){
				this.aipAlergiaPacientesDAO.merge(aipAlergiaPacientes);
			}else{
				persistirAipAlergiaPacientes(aipAlergiaPacientes, servidorLogado);
			}
		}
		
	}
	
	/**
	 * @ORADB aipt_alp_bri 
	 * @param aipAlergiaPacientes
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void alterarAipAlergiaPacientes(AipAlergiaPacientes aipAlergiaPacientes, RapServidores servidorLogado) throws ApplicationBusinessException{
		AipAlergiaPacientes aipAlergiaPacientesOld = this.aipAlergiaPacientesDAO.obterOriginal(aipAlergiaPacientes.getId());
		
		if(aipAlergiaPacientesOld.getIndSituacao().equals("I")){
			rnAlppVerReativa();
		}
		
		if(CoreUtil.modificados(aipAlergiaPacientesOld.getId().getPacCodigo(), aipAlergiaPacientes.getId().getPacCodigo())
			|| CoreUtil.modificados(aipAlergiaPacientesOld.getId().getSeq(), aipAlergiaPacientes.getId().getSeq())
			|| CoreUtil.modificados(DateUtil.obterDataFormatada(aipAlergiaPacientesOld.getCriadoEm(), "dd/MM/yyyy"),  
					DateUtil.obterDataFormatada(aipAlergiaPacientes.getCriadoEm(), "dd/MM/yyyy"))
			|| CoreUtil.modificados(aipAlergiaPacientesOld.getDescricao(), aipAlergiaPacientes.getDescricao())
			|| CoreUtil.modificados(aipAlergiaPacientesOld.getSerMatricula(), aipAlergiaPacientes.getSerMatricula())
			|| CoreUtil.modificados(aipAlergiaPacientesOld.getSerVinCodigo(), aipAlergiaPacientes.getSerVinCodigo())
			|| CoreUtil.modificados(aipAlergiaPacientesOld.getAusSeq(), aipAlergiaPacientes.getAusSeq())
			|| CoreUtil.modificados(aipAlergiaPacientesOld.getSerMatriculaInativada(), aipAlergiaPacientes.getSerMatriculaInativada())
			|| CoreUtil.modificados(aipAlergiaPacientesOld.getSerVinCodigoInativada(), aipAlergiaPacientes.getSerVinCodigoInativada())){
			rnAlppVerAltera();
		}
		
		if(aipAlergiaPacientes.getIndSituacao().equals("I")){
			if(servidorLogado != null && servidorLogado.getId() != null && servidorLogado.getId().getMatricula() != null){
				aipAlergiaPacientes.setSerMatriculaInativada(servidorLogado.getId().getMatricula());
				aipAlergiaPacientes.setSerVinCodigoInativada(servidorLogado.getId().getVinCodigo());
			}else{
				throw new ApplicationBusinessException(ValidaAlteracaoInclusaoAipAlergiaPacientesExceptionCode.AFA_00169);	
			}	
		}
		
		if(CoreUtil.modificados(aipAlergiaPacientesOld.getMotivo(), aipAlergiaPacientes.getMotivo())){
			if(servidorLogado != null && servidorLogado.getId() != null && servidorLogado.getId().getMatricula() != null){
				aipAlergiaPacientes.setSerMatricula(servidorLogado.getId().getMatricula());
				aipAlergiaPacientes.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
			}else{
				throw new ApplicationBusinessException(ValidaAlteracaoInclusaoAipAlergiaPacientesExceptionCode.AFA_00169);	
			}
		}
		aipAlergiaPacientes.setAlteradoEm(new Date());
	}
	
	/**
	 * @ORADB aipt_alp_bri 
	 * @param aipAlergiaPacientes
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	private void persistirAipAlergiaPacientes(AipAlergiaPacientes aipAlergiaPacientes, RapServidores servidorLogado) throws ApplicationBusinessException{
		aipAlergiaPacientes.getId().setSeq(this.aipAlergiaPacientesDAO.getNextVal(SequenceID.AIP_ALP_SQ1));
		aipAlergiaPacientes.setCriadoEm(new Date());
		
		if(servidorLogado != null && servidorLogado.getId() != null && servidorLogado.getId().getMatricula() != null){
			aipAlergiaPacientes.setSerMatricula(servidorLogado.getId().getMatricula());
			aipAlergiaPacientes.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		}else{
			throw new ApplicationBusinessException(ValidaAlteracaoInclusaoAipAlergiaPacientesExceptionCode.AFA_00169);
		}
		
		this.aipAlergiaPacientesDAO.persistir(aipAlergiaPacientes);
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB RN_ALPP_VER_REATIVA
	 */
	private void rnAlppVerReativa() throws ApplicationBusinessException{
		throw new ApplicationBusinessException(ValidaAlteracaoInclusaoAipAlergiaPacientesExceptionCode.AIP_00190);
	}
	
	/**
	 * @ORADB mpmc_get_nome_usual
	 * 
	 * Retornar o nome usual de uma pessoa.
	 * 
	 * @param matricula  
	 * @param vinCodigo  
	 * @return
	 */
	public String obterNomeUsualPacitente(Integer matricula, Short vinCodigo){
		String nomeUsual = vRapPessoaServidorDAO.obterNomeUsual(matricula, vinCodigo);
		if(nomeUsual != null){
			return nomeUsual;
		}else{
			return null;
		}
	}
	
	/**
	 * @ORADB MPMT_IFP_BRU
	 * #5795 PS2 
	 * @param novo
	 * @param old
	 * @throws ApplicationBusinessException 
	 */
	public void validarAlteracaoMpmInformacaoPrescribentes(MpmInformacaoPrescribente novo, MpmInformacaoPrescribente old) throws ApplicationBusinessException{
		if(CoreUtil.modificados(novo.getSeq(), old.getSeq()) ||
				CoreUtil.modificados(novo.getDescricao(), old.getDescricao()) ||
				CoreUtil.modificados(novo.getCriadoEm(), old.getCriadoEm()) ||
				CoreUtil.modificados(novo.getSerMatricula(), old.getSerMatricula()) ||
				CoreUtil.modificados(novo.getSerVinCodigo(), old.getSerVinCodigo()) ||
				CoreUtil.modificados(novo.getSerMatriculaVerificada(), old.getSerMatriculaVerificada()) ||
				CoreUtil.modificados(novo.getSerVinCodigoVerificada(), old.getSerVinCodigoVerificada()) ||
				CoreUtil.modificados(novo.getPmeAtdSeq(), old.getPmeAtdSeq()) ||
				CoreUtil.modificados(novo.getPmeSeq(), old.getPmeSeq()) ||
				CoreUtil.modificados(novo.getUnfSeq(), old.getUnfSeq()) ||
				CoreUtil.modificados(novo.getAtdSeq(), old.getAtdSeq()) ||
				CoreUtil.modificados(novo.getIndInfVerificada(), old.getIndInfVerificada()) ||
				CoreUtil.modificados(novo.getDthrInfVerificada(), old.getDthrInfVerificada())){
			
			/* chamada da procedure RN_IFPP_VER_ATU_VISTA, VER PC3  */ 
			verificarRnIfppAtuVista(old.getIndInfVerificada(), novo.getIndInfVerificada(), 
					novo.getDthrInfVerificada(), novo.getSerMatriculaVerificada(), novo.getSerVinCodigoVerificada(),
					novo);
			
			if(CoreUtil.modificados(old.getUnfSeq(), novo.getUnfSeq())){
				/* chamada da procedure RN_IFPP_VER_UNF, VER PC3  */  
				verificarRnIfppVerUnf(novo.getUnfSeq());
			}
			
			if(CoreUtil.modificados(old.getAtdSeq(), novo.getAtdSeq())){
			     /* chamada da procedure RN_IFPP_VER_ATEND, VER PC3 */
				verificarRnIfppVerAtend(novo.getAtdSeq());
			}
		}
	}
	
	/**
	 * @ORADB MPMT_IFP_BRI
	 * #5795 PS1 
	 * @param novo
	 * @throws ApplicationBusinessException 
	 */
	public void validarInsercaoMpmInformacaoPrescribentes(MpmInformacaoPrescribente novo) throws ApplicationBusinessException{
		novo.setCriadoEm(new Date());
		
		/* verifica a vigencia do atendimento, chamada da procedure RN_IFPP_VER_ATEND, VER PC3  */ 
		verificarRnIfppVerAtend(novo.getAtdSeq());
	
		/* chamada da procedure RN_IFPP_VER_UNF, VER PC3  */ 
		verificarRnIfppVerUnf(novo.getUnfSeq());
		
		String loginUsuarioLogado = obterLoginUsuarioLogado();
		RapServidores servidores = this.prescricaoMedicaFacade.obterServidorPorUsuario(loginUsuarioLogado); 
		novo.setSerMatricula(servidores.getId().getMatricula());
		novo.setSerVinCodigo(servidores.getId().getVinCodigo());
		novo.setServidor(servidores);
		
		if(novo.getSerMatricula() == null){
			throw new ApplicationBusinessException(ValidaProcedureRnIfppAtuVistaExceptionCode.RAP_00175);
		}
	}
	
	public void verificarRnIfppAtuVista(Boolean oldIndicator, Boolean newIndicator, Date newDthrInfVerificada,
			Integer newSerMatriculaVerificada, Short newSerVinCodigoVerificada, MpmInformacaoPrescribente novo) throws ApplicationBusinessException{

		if(oldIndicator){
			throw new ApplicationBusinessException(ValidaProcedureRnIfppAtuVistaExceptionCode.MPM_00767);
		}else{
			if(newIndicator){
				newDthrInfVerificada = new Date();
				
				String loginUsuarioLogado = obterLoginUsuarioLogado();
				RapServidores servidores = this.prescricaoMedicaFacade.obterServidorPorUsuario(loginUsuarioLogado); 
				newSerMatriculaVerificada = servidores.getId().getMatricula();
				newSerVinCodigoVerificada = servidores.getId().getVinCodigo();
				novo.setServidorVerificado(servidores);
				if(newSerMatriculaVerificada == null){
					throw new ApplicationBusinessException(ValidaProcedureRnIfppAtuVistaExceptionCode.RAP_00175);
				}
			}
		}
	}
	
	public void verificarRnIfppVerUnf(Short newUnfSeq) throws ApplicationBusinessException{
		Boolean vSit = Boolean.FALSE;
		Boolean vRetorno;
		
		AghUnidadesFuncionais unidadesFuncionais = aghUnidadesFuncionaisDAO.obterPorChavePrimaria(newUnfSeq);
		
		if(unidadesFuncionais != null && unidadesFuncionais.getIndSitUnidFunc() != null){
			vSit = unidadesFuncionais.getIndSitUnidFunc().isAtivo();
		}
		
		if(!vSit){
			throw new ApplicationBusinessException(ValidaProcedureRnIfppAtuVistaExceptionCode.MPM_00766);
		}
		
		/* verifica se permite enviar  informação prescribente, chamar procedure já implementada, procurar por ORADB Function AGHC_VER_CARACT_UNF. */ 
		vRetorno = examesLaudosFacade.verificarUnidadeFuncionalTemCaracteristica(newUnfSeq, ConstanteAghCaractUnidFuncionais.PERMITE_INF_PRESCRIBENTE); 
		
		if(!vRetorno){
			throw new ApplicationBusinessException(ValidaProcedureRnIfppAtuVistaExceptionCode.MPM_00765);
		}
		
	}
	
	public void verificarRnIfppVerAtend(Integer atdSeq) throws ApplicationBusinessException{
		AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
		this.prescricaoMedicaRN.verificaAtendimento(atendimento.getDthrInicio(), 
				atendimento.getDthrFim(), atdSeq, null, null, null);
	}
	
	private void inserirJustificativaNPT(MpmJustificativaNpt justificativaNpt)	throws BaseException {
		this.getPrescricaoMedicaRN().inserirJustificativaNPT(justificativaNpt);
	}
	
	private void atualizarJustificativaNPT(MpmJustificativaNpt justificativaNpt) throws BaseException  {
		this.getPrescricaoMedicaRN().atualizarJustificativaNPT(justificativaNpt);
	}
	

	public MpmJustificativaNptDAO getMpmJustificativaNptDAO() {
		return mpmJustificativaNptDAO;
	}

	public void setMpmJustificativaNptDAO(
			MpmJustificativaNptDAO mpmJustificativaNptDAO) {
		this.mpmJustificativaNptDAO = mpmJustificativaNptDAO;
	}
	
	private enum ValidaProcedureRnIfppAtuVistaExceptionCode implements BusinessExceptionCode {
		MPM_00767,
		RAP_00175,
		MPM_00766,
		MPM_00765
		;
	}
	
	private enum ValidaAlteracaoInclusaoAipAlergiaPacientesExceptionCode implements BusinessExceptionCode {
		AIP_00184,
		AFA_00169,
		AIP_00189,
		AIP_00190
		;
	}

	/**
	 * @throws ApplicationBusinessException 
	 * @RN_ALPP_VER_ALTERA
	 */
	private void rnAlppVerAltera() throws ApplicationBusinessException{
		throw new ApplicationBusinessException(ValidaAlteracaoInclusaoAipAlergiaPacientesExceptionCode.AIP_00189);
	}
}
