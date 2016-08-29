package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaCodigoProcedimentoSusVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProfissionalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatPacienteTransplantes;
import br.gov.mec.aghu.model.FatPacienteTransplantesId;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras de FORMS MBCF_ATU_CIRURGIA_NS de #24941: Registro de cirurgia realizada e nota de consumo
 * @author aghu
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength" })
@Stateless
public class RegistroCirurgiaRealizadaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RegistroCirurgiaRealizadaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcExtratoCirurgiaDAO mbcExtratoCirurgiaDAO;


	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private AgendaProcedimentosON agendaProcedimentosON;

	@EJB
	private ValorCadastralProcedimentoSusRN valorCadastralProcedimentoSusRN;

	@EJB
	private PopulaCodigoSsmRN populaCodigoSsmRN;

	@EJB
	private VerificaTipoAnestesiaRN verificaTipoAnestesiaRN;

	@EJB
	private MbcProcEspPorCirurgiasRN mbcProcEspPorCirurgiasRN;

	@EJB
	private MbcfEscolheFatRN mbcfEscolheFatRN;

	@EJB
	private VerificaEspecialidadeNotaRN verificaEspecialidadeNotaRN;

	@EJB
	private MbcCirurgiasRN mbcCirurgiasRN;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade iBlocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private PopulaProcedimentoHospitalarInternoRN populaProcedimentoHospitalarInternoRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private MbcCirurgiasVerificacoes2RN mbcCirurgiasVerificacoes2RN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;
	
	@EJB
	private DataCompetenciaRegistroCirurgiaRealizadoRN dataCompetenciaRegistroCirurgiaRealizadoRN;

	@EJB
	private MbcCirurgiasVerificacoesRN mbcCirurgiasVerificacoesRN;

	@EJB
	private MbcProfCirurgiasRN mbcProfCirurgiasRN;
	
	@Inject
	private MbcCirurgiasON mbcCirurgiasON;

	private static final long serialVersionUID = 3661498116130725271L;

	private static final int VALOR_48HORAS_EM_MINUTOS = 2880;
	
	private static final String SITUACAO_ATIVA = "A", TRANSPLANTE_CORNEA="CORNEA", STRING_ESCOLHER ="*ESCOLHER*";

	public enum RegistroCirurgiaRealizadaONExceptionCode implements BusinessExceptionCode {
		MBC_00537,MBC_00536, MBC_00511, MBC_00478, MBC_00340, MBC_00477, MBC_01829, MBC_01830, MBC_00553, 
		MBC_01641, MBC_01642, MBC_00487, MBC_01048, MBC_01077, MBC_01075, MBC_01078, MBC_01076, MBC_01080, 
		MBC_01338, MBC_01339, MBC_01079, MBC_00501, MBC_00502, MBC_00483, MBC_01336, MBC_00509, MBC_00506, 
		MBC_00507, FAT_00564, MENSAGEM_CONVENIO_NAO_ENCONTRADO, MSG_ERRO_ATUALIZAR_AGH_ATENDIMENTOS, MSG_ERRO_PROCED_HOSP_DATA_COMPET;
	}

	private final static String MSG_TEMPO_UTILIZACAO_O2_OZOT = " Tempo de anestesia maior que o tempo máximo de cirurgia permitido pela unidade. Confirma?";

	/**
	 * Registra cirurgia realizada e nota de consumo
	 * 
	 * @param emEdicao
	 * @param vo
	 * @param nomeMicrocomputador
	 * @param servidorLogado
	 * @return
	 * @throws BaseException
	 */
	public AlertaModalVO registrarCirurgiaRealizadaNotaConsumo(final boolean emEdicao, final boolean confirmaDigitacaoNS
			, CirurgiaTelaVO vo, final String nomeMicrocomputador) throws BaseException {
		
		final AlertaModalVO alertaVO = new AlertaModalVO();
		MbcCirurgias cirurgiaOld = getMbcCirurgiasDAO().obterOriginal(vo.getCirurgia().getSeq());
		MbcCirurgias cirurgiaSalva = mbcCirurgiasON.prepararCirurgia(vo, DominioSituacaoCirurgia.AGND);

		RapServidores servidorLogado = iRegistroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
		
		boolean alterouProntuario= false;
		if (!cirurgiaOld.getPaciente().equals(cirurgiaSalva.getPaciente())) {
			alterouProntuario = true;
		}
		
		alertaVO.setTitulo("Confirmar operação");

		// Chamada para 7.3. EVT_WHEN_LIST_CHANGED (Executada ao gravar)
		this.validarListaModificada(cirurgiaSalva);

		// Chamada para 7.8. EVT_PRE_UPDATE (Executada ao gravar a cirurgia)   cirurgiaSalva.getVersion()    cirurgiaOld.getVersion()
		cirurgiaSalva = this.validarDatas(cirurgiaSalva, vo);

		// Chamada para 7.11. EVT_PRE_COMMIT (Executada ao gravar)
		this.validarPartoCesarea(cirurgiaSalva, vo);
		
		final Boolean valorDigtNs = cirurgiaSalva.getDigitaNotaSala();
		
		// PERSISTE CIRURGIA
		cirurgiaSalva = this.getMbcCirurgiasRN().persistirCirurgia(cirurgiaSalva, nomeMicrocomputador, servidorLogado.getDtFimVinculo());
		
		if (alterouProntuario && cirurgiaSalva.getAtendimento()!=null) {
			AghAtendimentos atendimento = cirurgiaSalva.getAtendimento();
			atendimento.setProntuario(cirurgiaSalva.getPaciente().getProntuario());
			atendimento.setPaciente(cirurgiaSalva.getPaciente());
			try {
				this.getPacienteFacade().persistirAtendimento(atendimento, cirurgiaSalva.getAtendimento(), nomeMicrocomputador, servidorLogado.getDtFimVinculo());
			} catch (final Exception e) { //essa não seria a forma ideal de tratar a exceção, contudo não tenho como propagar a exceção no momento, pois causou mais que 50 erros após colocar a chamada adequada as triggers
				logError("Exceção capturada: ", e);
				throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MSG_ERRO_ATUALIZAR_AGH_ATENDIMENTOS);
			}
			
		}
		
		// Chamada para 7.9. EVT_POST_UPDATE (Executada após gravar a cirurgia)
		this.validarTempoUtilizacaoO2Ozot(cirurgiaSalva, alertaVO, vo);

		// EVT_POST_FORMS_COMMIT é simulado aqui!
		// Se foi modificada a LISTA DE PROCEDIMENTOS gravar em MBC_PROC_ESP_POR_CIRURGIAS e executar as RNs
		this.verificarAlteracoesListaProcedimentos(cirurgiaSalva, vo, valorDigtNs, servidorLogado);
		
		
		
		
		// Se foi modificada a LISTA DE PROFISSIONAIS gravar em MBC_PROF_CIRURGIAS e executar as RNs
		cirurgiaSalva = this.getAgendaProcedimentosON().verificarAlteracoesListaProfissionais(cirurgiaSalva, vo);

		// Se foi modificada a LISTA DE ANESTESIAS gravar em MBC_ANESTESIA_CIRURGIAS e executar as RNs
		this.getAgendaProcedimentosON().verificarAlteracoesListaAnestesias(cirurgiaSalva, vo);
		
		// Chamada para 7.4. EVT_POST_FORMS_COMMIT (Executada ao gravar)
		this.validarCirurgia(cirurgiaSalva, vo);
		
		return alertaVO;
	}

	protected void verificarAlteracoesListaProcedimentos(MbcCirurgias cirurgia, CirurgiaTelaVO vo, Boolean valorDigtNs, RapServidores servidorLogado) throws BaseException {
		for (MbcProcEspPorCirurgiasId idRemover : vo.getListaProcedimentosRemovidos()) { // REMOVE
			// RN91, RN92 (MBCP_ENFORCE_PPC_RULES)
			MbcProcEspPorCirurgias originalRemovido = this.getMbcProcEspPorCirurgiasDAO().obterPorChavePrimaria(idRemover);
			if (originalRemovido != null) {
				this.getMbcProcEspPorCirurgiasRN().removerMbcProcEspPorCirurgias(originalRemovido);
			}
		}
		for (CirurgiaTelaProcedimentoVO procedimentoVO : vo.getListaProcedimentosVO()) { // INSERE OU ALTERA			
			// Chamada para 7.7. EVT_PRE_INSERT (Executada ao gravar um procedimento)
			this.popularInformacoesSusValorCadastralPermCidInterface(procedimentoVO, cirurgia);
			
			// Chamada para 7.1. EVT_WHEN_VALIDATE_RECORD (Executada ao gravar um procedimento)   
			this.validarRegistroGravarProcedimento(cirurgia, procedimentoVO);

			boolean atualizacao = getMbcProcEspPorCirurgiasDAO().obterOriginal(procedimentoVO.getId()) != null;			

			// RN77, RN89, RN90, RN92 (MBCP_ENFORCE_PPC_RULES)
			MbcProcEspPorCirurgias procEspPorCirurgia = this.getAgendaProcedimentosON().prepararProcedimentoPersistencia(cirurgia, procedimentoVO);
			
			this.getMbcProcEspPorCirurgiasRN().persistirProcEspPorCirurgias(procEspPorCirurgia);
			if (atualizacao) { // Chamada PROCEDURE EVT_ON_UPDATE
				this.verificarAtualizacaoProcedimento(cirurgia, procedimentoVO, valorDigtNs, servidorLogado); 
			}
			this.getMbcProcEspPorCirurgiasDAO().flush();
			
			AghParametros parametro = this.iParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PROCED_FAT_TRANSP_CORNEA);
			Integer vlrParam = 0;

			if (parametro.getVlrNumerico() != null) {
				vlrParam = parametro.getVlrNumerico().intValue();
			}
			if(procedimentoVO.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getSeq().equals(vlrParam)){
				this.inserirPacienteTransplante(cirurgia, vo, procedimentoVO);	
			}
			
		}
	}
	
	/**
	 * Implementacao Melhoria em Produção #52300
	 * @param cirurgia
	 * @param vo
	 * @param procedimentoVO
	 */
	private void inserirPacienteTransplante(MbcCirurgias cirurgia, CirurgiaTelaVO vo, CirurgiaTelaProcedimentoVO procedimentoVO){
		FatPacienteTransplantes pacienteTransplante = new FatPacienteTransplantes();
		FatPacienteTransplantesId id = new FatPacienteTransplantesId();
		id.setPacCodigo(cirurgia.getPaciente().getCodigo());
		id.setTtrCodigo(TRANSPLANTE_CORNEA);
		id.setDtInscricaoTransplante(cirurgia.getData());
		pacienteTransplante.setId(id);
		pacienteTransplante.setDtTransplante(cirurgia.getData());
		pacienteTransplante.setIndSituacao(SITUACAO_ATIVA);
		
		Short cnvCodigo = vo.getCirurgia().getConvenioSaudePlano().getId().getCnvCodigo();
		Byte cspSeq = 	vo.getCirurgia().getConvenioSaudePlano().getId().getSeq();
		Integer phiSeq = procedimentoVO.getPhiSeq();
		Long codTabela = null;
		if(procedimentoVO.getCgnbtEprPciseq() != null && procedimentoVO.getCgnbtEprPciseq().equalsIgnoreCase(STRING_ESCOLHER)){
			try{
				codTabela = Long.valueOf(procedimentoVO.getCgnbtEprPciseq());
			}catch(NumberFormatException e){
				codTabela = null;
			}
		}
		
		List<VFatAssociacaoProcedimento> lista = this.iFaturamentoFacade.pesquisarAssociacaoProcedimentos(phiSeq, cnvCodigo, cspSeq, codTabela);
		if(lista!=null && lista.size()>0){
			VFatAssociacaoProcedimento item = lista.get(0);
			pacienteTransplante.setIphPhoSeq(item.getId().getIphPhoSeq());
			pacienteTransplante.setIphSeq(item.getId().getIphSeq());
		}
		FatPacienteTransplantes pacienteTransplanteOld = this.iFaturamentoFacade.obterFatPacienteTransplante(id);
		if(pacienteTransplanteOld==null){
			this.iFaturamentoFacade.persistirFatPacienteTransplantes(pacienteTransplante);	
		}
	}

	/**
	 * ORADB PROCEDURE EVT_WHEN_VALIDATE_RECORD
	 * <p>
	 * RN7.1 Executada ao gravar um procedimento
	 * <p>
	 * 
	 * @param digitaNotaSala
	 * @param procedimentoVO
	 * @throws BaseException
	 */
	public void validarRegistroGravarProcedimento(MbcCirurgias cirurgia, CirurgiaTelaProcedimentoVO procedimentoVO) throws BaseException {
		if (procedimentoVO.getId() == null) {
			throw new IllegalArgumentException();
		}
		AipPacientes paciente = cirurgia.getPaciente();
		final boolean isProntuarioCadastrado = paciente != null && paciente.getProntuario() != null ? true : false;
		if (isProntuarioCadastrado && Boolean.TRUE.equals(cirurgia.getDigitaNotaSala())) {
			procedimentoVO.getId().setIndRespProc(DominioIndRespProc.NOTA);
		}
	}

	/**
	 * ORADB PROCEDURE EVT_WHEN_NEW_RECORD_INSTANCE
	 * <p>
	 * RN7.2 Executada ao entrar na tela
	 * <p>
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	public String validarProcedimentoFaturado(CirurgiaTelaVO vo) {
		AipPacientes paciente = vo.getCirurgia().getPaciente();
		final boolean isProntuarioCadastrado = paciente != null && paciente.getProntuario() != null ? true : false;
		// Chamada para FUNCTION MBCC_VERIF_CIRG_FATD
		Boolean isExisteProcedimentoFaturado = this.getFaturamentoFacade().verificarCirurgiaFaturada(vo.getCirurgia().getSeq(), vo.getCirurgia().getOrigemPacienteCirurgia());
		if (isProntuarioCadastrado && Boolean.TRUE.equals(vo.getCirurgia().getDigitaNotaSala()) && Boolean.TRUE.equals(isExisteProcedimentoFaturado)) {
			// Já existem Procedimentos Cirúrgicos faturados para esta Cirurgia. Não podem ser alteradas as seguintes informações: origem da cirurgia, convênio e procedimentos.
			return RegistroCirurgiaRealizadaONExceptionCode.MBC_00511.toString();
		}
		return null;
	}

	/**
	 * ORADB PROCEDURE EVT_WHEN_LIST_CHANGED
	 * <p>
	 * RN7.3 Executada ao gravar
	 * <p>
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	protected void validarListaModificada(MbcCirurgias cirurgia) throws BaseException {
		if (Boolean.TRUE.equals(cirurgia.getDigitaNotaSala())) {
			final Date dataInicioCirurgia = cirurgia.getDataInicioCirurgia();
			final Date dataEntradaSala = cirurgia.getDataEntradaSala();
			if (dataInicioCirurgia != null && dataEntradaSala == null) {
				// 1) Se dthr_inicio_cirg for diferente de nulo e dthr_entrada_sala for nulo então seta dthr_entrada_sala com o valor de dthr_inicio_cirg.
				cirurgia.setDataEntradaSala(dataInicioCirurgia);
			}
			final Date dataFimCirurgia = cirurgia.getDataFimCirurgia();
			final Date dataSaidaSala = cirurgia.getDataSaidaSala();
			if (dataFimCirurgia != null && dataSaidaSala == null) {
				// 2) Se dthr_fim_cirg for diferente de nulo e dthr_saida_sala for nulo então seta dthr_saida_sala com o valor de dthr_fim_cirg.
				cirurgia.setDataSaidaSala(dataFimCirurgia);
			}
		}
	}

	/**
	 * ORADB PROCEDURE EVT_POST_FORMS_COMMIT
	 * <p>
	 * RN7.4 PARTE 1 Executada ao gravar
	 * <p>
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	protected void validarCirurgia(MbcCirurgias cirurgia, CirurgiaTelaVO vo) throws BaseException {
		Boolean valorIndNsSemPront = null;
		// RN1
		if (!DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao())) {

			// RN1.1 Garante que convenio da cirurgia seja igual ao do internação
			if (cirurgia.getAtendimento() != null && cirurgia.getAtendimento().getInternacao() != null
					&& cirurgia.getAtendimento().getInternacao().getConvenioSaudePlano() != null) {
				FatConvenioSaudePlano convenioInternacao = cirurgia.getAtendimento().getInternacao().getConvenioSaudePlano();
				FatConvenioSaudePlano convenioCirurgia = cirurgia.getConvenioSaudePlano();
				if (!CoreUtil.igual(convenioInternacao, convenioCirurgia)) {
					// Convênio da Cirurgia é diferente do Convênio da Internação.
					throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_00478);
				}
			}

			// RN1.2 Pesquisa procedimentos especialidade por cirurgia do tipo NOTA
			List<MbcProcEspPorCirurgias> listaNota = this.getMbcProcEspPorCirurgiasDAO().pesquisarProcedimentosAtivosPorCirurgiaResposta(cirurgia.getSeq(),
					DominioIndRespProc.NOTA);
			List<MbcProcEspPorCirurgias> listaAgenda = this.getMbcProcEspPorCirurgiasDAO().pesquisarProcedimentosAtivosPorCirurgiaResposta(cirurgia.getSeq(),
					DominioIndRespProc.AGND);
			if (listaNota.isEmpty()) {
				if (listaAgenda.isEmpty()) {
					// Convênio da Cirurgia é diferente do Convênio da Internação.
					throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_00340);
				} else {
					if(listaAgenda.get(0).getProcedimentoCirurgico() != null){
						valorIndNsSemPront = listaAgenda.get(0).getProcedimentoCirurgico().getIndNsSemPront();
					}
				}
			} else {
				if(listaNota.get(0).getProcedimentoCirurgico() != null){
					valorIndNsSemPront = listaNota.get(0).getProcedimentoCirurgico().getIndNsSemPront();	
				}
				
			}

			// RN1.3
			if (cirurgia.getPaciente().getProntuario() == null
					&& (Boolean.FALSE.equals(valorIndNsSemPront) || DominioOrigemPacienteCirurgia.I.equals(cirurgia.getOrigemPacienteCirurgia()))) {
				// Paciente deve ter prontuário cadastrado quando da digitação da nota de sala
				throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_00477);
			}

			// RN1.4
			if (listaNota!=null && listaNota.size()>0) {
				// Chamada para PROCEDURE RN_CRGP_VER_PROC_ESP
				this.getMbcProcEspPorCirurgiasRN().verificarProcedimentoEspecialidade(cirurgia.getSeq(), DominioIndRespProc.NOTA);
			} else if (listaAgenda!=null && listaAgenda.size()>0){
				// Chamada para PROCEDURE RN_CRGP_VER_PROC_ESP
				this.getMbcProcEspPorCirurgiasRN().verificarProcedimentoEspecialidade(cirurgia.getSeq(), DominioIndRespProc.AGND);
			}

		}

		// Chamada para PARTE 2 (EVITAR VIOLAÇÕES DE PMD: NPATH COMPLEXITY)
		this.validarCirurgiaParte2(cirurgia, vo);

	}

	/**
	 * CONTINUAÇÃO DA PROCEDURE PROCEDURE EVT_POST_FORMS_COMMIT PARA EVITAR VIOLAÇÕES DE PMD (NPATH COMPLEXITY)
	 * 
	 * <p>
	 * RN7.4 PARTE 2 Executada ao gravar
	 * <p>
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	private void validarCirurgiaParte2(MbcCirurgias cirurgia, CirurgiaTelaVO vo) throws BaseException {

		// CONTINUAÇÃO DA RN1
		if (!DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao())) {

			// RN1.5
			if (Boolean.TRUE.equals(cirurgia.getDigitaNotaSala())) {

				// RN 1.5.1 Garante que só pode ter um responsável pela cirurgia e que este seja medico professor ou contratado.
				this.getMbcProfCirurgiasRN().verificarResponsavel(cirurgia.getSeq());

				// RN 1.5.2 Garante que deve haver pelo menos um e somente um responsável pela realização da cirurgia e que este seja medico professor,contratado ou auxiliar.
				this.getMbcCirurgiasVerificacoes2RN().verificarProfissionalExecucaoCirugia(vo.getListaProfissionaisVO());

				// RN 1.5.3 Garante que deve haver pelo menos um profissional da enfermagem vinculado a cirurgia
				final Boolean isUnidadeFuncionalBloco = this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(cirurgia.getUnidadeFuncional().getSeq(),
						ConstanteAghCaractUnidFuncionais.BLOCO);
				if (isUnidadeFuncionalBloco) {
					this.getMbcCirurgiasVerificacoes2RN().verificarProfissionalEnfermagem(cirurgia.getSeq());
				}

				// RN 1.5.4 Garante que tenha sido informado pelo menos um tipo de anestesia
				this.getVerificaTipoAnestesiaRN().verificarTipoAnestesia(cirurgia.getSeq());

				// RN 1.5.5 Verifica a necessidade de profissional anestesista associado a cirurgia
				if (isUnidadeFuncionalBloco || vo.getDeveTerAnestesista()) {
					this.getMbcCirurgiasVerificacoes2RN().verificarProfissionalAnestesista(cirurgia.getSeq());
				}

				// RN 1.5.6 Verificar se os procedimentos da cirurgia exigem informação do CERIH
				// Chamada para FUNCTION MBCK_CRG_RN.RN_CRGC_VER_CERIH
				final Boolean isExigeCerih = this.getMbcCirurgiasVerificacoes2RN().verificarExigenciaCerih(cirurgia);
				if (!isExigeCerih) {
					if (Boolean.TRUE.equals(cirurgia.getDigitaNotaSala())) {
						// Inclusão de item não permitida, pois há procedimento realizado que exige informação do CERIH.
						throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01829);
					} else {
						// Confirmação da nota não permitida, pois há procedimento realizado que exige informação do CERIH.
						throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01830);
					}
				}
			}

		}

		// RN1.6 Chamada da PROCEDURE RN_CRGP_VER_ESP_NOTA: Garante que especialidade do procedimento principal e profissional será a mesma da cirurgia
		this.getVerificaEspecialidadeNotaRN().verificarEspecialidadeNota(cirurgia.getDigitaNotaSala(), cirurgia.getSeq(), cirurgia.getSituacao(),
				cirurgia.getEspecialidade().getSeq());

		// RN1.7 Garante que se a cirurgia for SUS e o procedimento tiver mais de 1 PHI, este PHI tenha sido informado
		if (Boolean.TRUE.equals(cirurgia.getDigitaNotaSala())) {

			FatConvenioSaude convenioSaude = this.getFaturamentoFacade().obterFatConvenioSaudePorId(cirurgia.getConvenioSaude().getCodigo());

			validaConvenioSaude(convenioSaude);

			final DominioGrupoConvenio grupoConvenio = convenioSaude.getGrupoConvenio();

			if (DominioGrupoConvenio.S.equals(grupoConvenio)) { // Verifica se é SUS

				// CURSOR CUR_PHI
				if(validaListaProceds(vo.getListaProcedimentosVO())){
					for (CirurgiaTelaProcedimentoVO procedimento : vo.getListaProcedimentosVO()) {
						List<CirurgiaCodigoProcedimentoSusVO> procedsSus = this
								.getBlocoCirurgicoFacade().listarCodigoProcedimentosSUS(
										procedimento.getProcedimentoCirurgico().getSeq(), 
										procedimento.getMbcEspecialidadeProcCirgs().getId().getEspSeq(),
										cirurgia.getOrigemPacienteCirurgia());
						if(procedimento.getSituacao() == DominioSituacao.A) {
							validaListaPhi(procedsSus);
						}
					}
				}

				/* #45148 Cirurgias/ Nota de Consumo - CID informado não compatível.
				 * Com o novo layout de procedimentos não é mais necessário essa validação */ 
			}
		}
	}

	private Boolean validaListaProceds(List<CirurgiaTelaProcedimentoVO> listaProcedimentosVO) {
		if(!listaProcedimentosVO.isEmpty()){
			if("*ESCOLHER*".equals(listaProcedimentosVO.get(0).getCgnbtEprPciseq())){
				return Boolean.TRUE;
			}
		}	
		return Boolean.FALSE;
	}
	
	private void validaListaPhi(List<CirurgiaCodigoProcedimentoSusVO> procedsSus) throws ApplicationBusinessException {
		if (!procedsSus.isEmpty() && procedsSus.size() > 1) {
			// Favor, escolher o código para faturamento SUS do procedimento.
			throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_00553);
		}
	}

	private void validaConvenioSaude(FatConvenioSaude convenioSaude)
			throws ApplicationBusinessException {
		if (convenioSaude == null) {
			throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MENSAGEM_CONVENIO_NAO_ENCONTRADO);
		}
	}

	/**
	 * ORADB PROCEDURE EVT_POST_QUERY
	 * <p>
	 * RN7.5 Executada ao entrar na tela
	 * <p>
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	public String validarFaturamentoPacienteTransplantado(CirurgiaTelaVO vo) {
		Date dataUltimoTransplante = this.getFaturamentoFacade().obterDataUltimoTransplante(vo.getCirurgia().getPaciente().getCodigo());
		if (dataUltimoTransplante != null) {
			// ATENCÃO. Paciente é transplantado!
			return RegistroCirurgiaRealizadaONExceptionCode.MBC_00537.toString();
		}
		return null;
	}

	/*
	 * ATENÇÃO: A RN7.6 EVT_POST_FORMS_COMMIT foi descartada
	 */

	/**
	 * ORADB PROCEDURE EVT_PRE_INSERT
	 * <p>
	 * RN7.7 Executada ao gravar um procedimento
	 * <p>
	 * 
	 * @param cirurgia
	 * @throws BaseException
	 */
	public void popularInformacoesSusValorCadastralPermCidInterface(CirurgiaTelaProcedimentoVO procedimentoVO, MbcCirurgias cirurgia) throws BaseException {
		// Seta as informações de Cadastros SUS, Valor Cadastral, Perm e CID na interface
		this.getPopulaProcedimentoHospitalarInternoRN().popularProcedimentoHospitalarInterno(procedimentoVO, cirurgia);
	}

	/**
	 * ORADB PROCEDURE MBCP_CODIGO_SSM
	 * <p>
	 * Conforme a estória #28181: Executada ao entrar na tela
	 * <p>
	 * 
	 * @param listaProcedimentos
	 * @throws BaseException
	 */
	public void popularCodigoSsm(List<CirurgiaTelaProcedimentoVO> listaProcedimentos, MbcCirurgias cirurgia) throws BaseException {
		for (CirurgiaTelaProcedimentoVO procedimentoVO : listaProcedimentos) {
			this.getPopulaCodigoSsmRN().popularCodigoSsm(procedimentoVO, cirurgia);
		}
	}

	/**
	 * ORADB PROCEDURE EVT_PRE_UPDATE
	 * <p>
	 * RN7.8 PARTE 1 Executada ao gravar a cirurgia
	 * <p>
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	public MbcCirurgias validarDatas(MbcCirurgias cirurgia, CirurgiaTelaVO vo) throws BaseException {
		// Obtém os valores no BANCO (DATABASE_VALUE)
		final MbcCirurgias originalBancoDados = this.getMbcCirurgiasDAO().obterOriginal(cirurgia.getSeq());

		// Salva atendimento anterior
		if (cirurgia.getAtendimento() != null) {
			vo.setAtcnsAtendimentoAnterior(cirurgia.getAtendimento().getSeq());
		}

		// RN1 Atualiza a data hora da digitação da nota de sala caso esta ainda esteja nula originalBancoDados.getVersion()
		if (cirurgia.getDataDigitacaoNotaSala() == null && Boolean.TRUE.equals(cirurgia.getDigitaNotaSala())) {
			Date dataAtual = new Date();
			cirurgia.setDataDigitacaoNotaSala(dataAtual);
			cirurgia.setDataUltimaAtualizacaoNotaSala(dataAtual);
		}

		final Short valorMtcSeq = originalBancoDados.getMotivoCancelamento() != null ? originalBancoDados.getMotivoCancelamento().getSeq() : null;
		final Boolean valorDigtNs = originalBancoDados.getDigitaNotaSala();

		vo.setIndDigtNotaSala(valorDigtNs); // Vide: copy(v_digt_ns,'GLOBAL.MBC$IND_DIGT_NOTA_SALA');

		if (valorMtcSeq == null && cirurgia.getMotivoCancelamento() != null) {
			// Só pode incluir/alterar motivo de cancelamento quando cirurgia já cancelada
			throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_00483);
		}

		// Rotina para buscar valor da tabela de parâmetros que define o procedimento como parto ou cesarea
		AghParametros pHrsMaxOcupSala = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HRS_MAX_OCUP_SALA);

		// RN2 Tempo de OCUPAÇÃO DA SALA
		final Date dataEntradaSala = cirurgia.getDataEntradaSala();
		final Date dataSaidaSala = cirurgia.getDataSaidaSala();
		if (dataEntradaSala != null && dataSaidaSala != null) {
			// Obtém a diferença em minutos entre a entrada e saída da sala
			int diferencaMinutos = Minutes.minutesBetween(new DateTime(dataEntradaSala), new DateTime(dataSaidaSala)).getMinutes();

			// Obtém o tempo máximo de ocupação da sala em minutos. Vide 24 * 60 = 1440
			int valorMaximoOcupacaoSala = pHrsMaxOcupSala.getVlrNumerico().intValue() * 60;

			if (CoreUtil.maior(diferencaMinutos, valorMaximoOcupacaoSala)) {
				// Tempo de ocupação da sala (dthr saida - dthr entrada) é maior que o permitido em horas.
				throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01336, pHrsMaxOcupSala.getVlrNumerico().intValue());
			}
		}
		// Chamada para PARTE 2 (EVITAR VIOLAÇÕES DE PMD: NPATH COMPLEXITY)
		return this.validarDatasParte2(cirurgia, originalBancoDados, vo);
	}

	/**
	 * CONTINUAÇÃO DA PROCEDURE PROCEDURE EVT_PRE_UPDATE PARA EVITAR VIOLAÇÕES DE PMD (NPATH COMPLEXITY)
	 * <p>
	 * RN7.8 PARTE 2 Executada ao gravar um procedimento
	 * <p>
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	private MbcCirurgias validarDatasParte2(MbcCirurgias cirurgia, final MbcCirurgias originalBancoDados, CirurgiaTelaVO vo) throws BaseException {
		// RN3 Tempo de ANESTESIA
		final Date dataInicioAnestesia = cirurgia.getDataInicioAnestesia();
		final Date dataFimAnestesia = cirurgia.getDataFimAnestesia();
		if (dataFimAnestesia != null && dataInicioAnestesia != null) {
			// Obtém a diferença em minutos entre a entrada e saída da sala
			final int diferencaMinutos = Minutes.minutesBetween(new DateTime(dataInicioAnestesia), new DateTime(dataFimAnestesia)).getMinutes();

			if (CoreUtil.maior(diferencaMinutos, VALOR_48HORAS_EM_MINUTOS)) {
				// Tempo de anestesia ultrapassa 48 horas. Verifique!
				throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_00509);
			}
		}

		// Chamada da PROCEDURE MBCK_CRG_RN.RN_CRGP_VER_DIGT_NOT
		this.getMbcCirurgiasVerificacoes2RN().verificarDigitoNotaSala(cirurgia);

		if (!CoreUtil.igual(cirurgia.getOrigemPacienteCirurgia(), originalBancoDados.getOrigemPacienteCirurgia())) {
			// Chamada da PROCEDURE MBCK_CRG_RN.RN_CRGP_ATU_ORIGEM
			cirurgia.setAtendimento(this.getMbcCirurgiasVerificacoesRN().atualizarOrigem(cirurgia.getOrigemPacienteCirurgia(), cirurgia.getDataDigitacaoNotaSala(),
					cirurgia.getAtendimento(), cirurgia.getNaturezaAgenda(), cirurgia.getUnidadeFuncional(), cirurgia.getData(),
					cirurgia.getPaciente(), cirurgia.getDataInicioCirurgia(), cirurgia.getEspecialidade()));
		}

		// Garante que cirurgia com origem internaçãoo terá associado um convenio com plano internação
		// o e uma ambulatório terá associado um convenio com plano ambulatório
		if (vo.getTipoPlano() != null) {
			if (DominioTipoPlano.A.equals(vo.getTipoPlano()) && !DominioOrigemPacienteCirurgia.A.equals(cirurgia.getOrigemPacienteCirurgia())) {
				// Para cirurgia com Convênio/Plano de Ambulatório a origem deve ser ambulatorial.
				throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_00506);
			} else if (DominioTipoPlano.I.equals(vo.getTipoPlano()) && !DominioOrigemPacienteCirurgia.I.equals(cirurgia.getOrigemPacienteCirurgia())) {
				// Para cirurgia com Convênio/Plano de Internação a origem deve ser de internação.
				throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_00507);
			}
		}

		// Quando for CCA,o profissional que realizou a cirurgia será o mesmo que está indicado como o responsável por ela. Isto evita de ter que entrar no bloco PCG
		final Boolean isUnidadeFuncionalCca = this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(cirurgia.getUnidadeFuncional().getSeq(),
				ConstanteAghCaractUnidFuncionais.CCA);

		if (isUnidadeFuncionalCca) {
			MbcProfCirurgias profCirurgias = this.getMbcProfCirurgiasDAO().retornaResponsavelCirurgia(cirurgia);
			profCirurgias.setIndRealizou(true);
			profCirurgias.setCirurgia(cirurgia);
			cirurgia = this.getMbcProfCirurgiasRN().persistirProfissionalCirurgias(profCirurgias).getCirurgia();
		}
		return cirurgia;
	}

	/**
	 * ORADB PROCEDURE EVT_ON_UPDATE
	 * 
	 * @param vo
	 * @param procedimentoVO
	 * @param valorDigtNs
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void verificarAtualizacaoProcedimento(MbcCirurgias cirurgia, final CirurgiaTelaProcedimentoVO procedimentoVO, final Boolean valorDigtNs, RapServidores servidorLogado) throws BaseException {

		if (Boolean.TRUE.equals(cirurgia.getDigitaNotaSala()) && Boolean.FALSE.equals(valorDigtNs)) {

			if (DominioSituacao.A.equals(procedimentoVO.getSituacao())) {

				// Chama a PROCEDURE MBCP_POPULA_PHI
				this.getPopulaProcedimentoHospitalarInternoRN().popularProcedimentoHospitalarInterno(procedimentoVO, cirurgia);

				MbcProcEspPorCirurgias novaNota = new MbcProcEspPorCirurgias();

				MbcProcEspPorCirurgiasId id = new MbcProcEspPorCirurgiasId();
				id.setCrgSeq(cirurgia.getSeq());
				id.setEprEspSeq(procedimentoVO.getMbcEspecialidadeProcCirgs().getId().getEspSeq());
				id.setEprPciSeq(procedimentoVO.getProcedimentoCirurgico().getSeq());
				id.setIndRespProc(DominioIndRespProc.NOTA);
				novaNota.setId(id);
				novaNota.setCirurgia(cirurgia);
				
				novaNota.setServidor(servidorLogado);
				novaNota.setSituacao(procedimentoVO.getSituacao());
				novaNota.setCriadoEm(new Date());
				novaNota.setQtd(procedimentoVO.getQtd());
				novaNota.setIndPrincipal(procedimentoVO.getIndPrincipal());
				if(procedimentoVO.getPhiSeq() != null){
					novaNota.setProcedHospInterno(getFaturamentoFacade().obterFatProcedHospInternosPorChavePrimaria(procedimentoVO.getPhiSeq()));
				}
				novaNota.setCid(procedimentoVO.getCid());

				getMbcProcEspPorCirurgiasRN().persistirProcEspPorCirurgias(novaNota);

			}
		}
	}

	/**
	 * ORADB PROCEDURE EVT_POST_UPDATE
	 * <p>
	 * RN7.9 Executada após gravar a cirurgia
	 * <p>
	 * 
	 * @param vo
	 * @param alertaVO
	 * @throws BaseException
	 */
	protected void validarTempoUtilizacaoO2Ozot(MbcCirurgias cirurgia, final AlertaModalVO alertaVO, CirurgiaTelaVO vo) throws BaseException {
		final MbcCirurgias original = this.getMbcCirurgiasDAO().obterOriginal(cirurgia.getSeq());
		
		if (original != null) {
			//cirurgia.setDigitaNotaSala(original.getDigitaNotaSala());
			//cirurgia.setDataDigitacaoNotaSala(original.getDataDigitacaoNotaSala());
			//cirurgia.setDataUltimaAtualizacaoNotaSala(original.getDataUltimaAtualizacaoNotaSala());
			
			// Comentado pois ao executar o método getAgendaProcedimentosON().verificarAlteracoesListaProfissionais() a cirurgia é persistida novamente.
			/*if (original.getAtendimento() != null) {
				AghAtendimentos atendimentoOriginal = getAghuFacade().obterAtendimentoPeloSeq(original.getAtendimento().getSeq());			
				cirurgia.setAtendimento(atendimentoOriginal);
			}*/
			
			// #35865 - resgatar o tempo Máximo de cirurgia 
			if (vo.getTempoMaximoCirurgia()==null) {
				List<AghUnidadesFuncionais> listUniFuncionais = this.getAghuFacade().pesquisarUnidadesFuncionaisExecutoraCirurgias(
															cirurgia.getUnidadeFuncional().getSeq().toString(), null, null
				);
				if (listUniFuncionais!=null && listUniFuncionais.size()> 0)	{
					for (AghUnidadesFuncionais uniFuncional: listUniFuncionais){
						vo.setTempoMaximoCirurgia(uniFuncional.getTempoMaximoCirurgia());
						break;
					}
				}
			}
			
			// #35882 - atualizar apenas se a utulização de O2 foi indicada
			if (cirurgia.getUtilizaO2()!=null && cirurgia.getUtilizaO2()){
//				if (vo.getAtcnsAtendimentoAnterior() == null) {
//					cirurgia.setAtendimento(null);
//				}
				
			    if (CoreUtil.maiorOuIgual(original.getTempoUtilizacaoO2(), vo.getTempoMaximoCirurgia())) {
					alertaVO.setAlerta(MSG_TEMPO_UTILIZACAO_O2_OZOT); // SHOW_ALERT aqui!
					alertaVO.setCancelarAlertaContinuaProcesso(true);
					alertaVO.setTempoUtlzO2(true);
				}
			
			// #35882 - atualizar apenas se a utulização de pro Axot foi indicada
			} else if (vo.getCirurgia().getUtilizaProAzot()!= null && vo.getCirurgia().getUtilizaProAzot()!= null) {
//				if (vo.getAtcnsAtendimentoAnterior() == null) {
//					cirurgia.setAtendimento(null);
//				}
				
					// CRG.DSP_TEMPO_MAXIMO_CIRURGIA
				if (CoreUtil.maiorOuIgual(original.getTempoUtilizacaoProAzot(), vo.getTempoMaximoCirurgia())) {
					alertaVO.setAlerta(MSG_TEMPO_UTILIZACAO_O2_OZOT); // SHOW_ALERT aqui!
					alertaVO.setCancelarAlertaContinuaProcesso(true);
					alertaVO.setTempoUtlzProAzot(true);
				}
			}	
		}
	}

	/**
	 * Parte da RN7.8 que foi extraída da procedure principal para ser executada através da modal de confirmação no XHTML
	 * 
	 * @param vo
	 * @param alertaVO
	 * @param isPressionouBotaoSimModal
	 * @throws BaseException
	 */
	public void validarModalTempoUtilizacaoO2Ozot(CirurgiaTelaVO vo, final AlertaModalVO alertaVO, final boolean isPressionouBotaoSimModal) throws BaseException {
		if (Boolean.FALSE.equals(isPressionouBotaoSimModal)) { // NÃO
			RegistroCirurgiaRealizadaONExceptionCode exceptionCode = null;
			if (alertaVO.isTempoUtlzO2()) {
				// Até o desligamento do sist. de faturamento do BULL não é permitido anestesia c/ duração > 23h59m. Acerte as datas de início e fim da anestesia ou desmarque a
				// utilização de O2 e/ou Pro Azot.
				exceptionCode = RegistroCirurgiaRealizadaONExceptionCode.MBC_00501;
			} else if (alertaVO.isTempoUtlzProAzot()) {
				// Acerte as datas de inicio e fim da anestesia ou desmarque a utilização de O2 e/ou Pro Azot
				exceptionCode = RegistroCirurgiaRealizadaONExceptionCode.MBC_00502;
			}
			throw new ApplicationBusinessException(exceptionCode);
		}
		vo.setAtcnsAtendimentoAnterior(null); // Vide: erase('global.mbc$atcnr_atendimento_anterior');
	}

	/**
	 * ORADB PROCEDURE EVT_POST_BLOCK
	 * <p>
	 * RN7.10 Executada ao entrar na tela
	 * <p>
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	public List<String> validarProntuario(CirurgiaTelaVO vo , Integer pacCodigoFonetica) {
		List<String> retorno = new ArrayList<String>();
		Boolean valorIndNsSemPront = null;
		List<MbcProcEspPorCirurgias> listaProfissionalPorCirurgiaAgenda = this.getMbcProcEspPorCirurgiasDAO().pesquisarProcedimentosAtivosPorCirurgiaResposta(
				vo.getCirurgia().getSeq(), DominioIndRespProc.AGND);
		if (listaProfissionalPorCirurgiaAgenda.isEmpty()) {
			List<MbcProcEspPorCirurgias> listaProfissionalPorCirurgiaNota = this.getMbcProcEspPorCirurgiasDAO().pesquisarProcedimentosAtivosPorCirurgiaResposta(
					vo.getCirurgia().getSeq(), DominioIndRespProc.NOTA);
			if (listaProfissionalPorCirurgiaNota.isEmpty()) {
				retorno.add(RegistroCirurgiaRealizadaONExceptionCode.MBC_00340.toString());
			} else {
				valorIndNsSemPront = listaProfissionalPorCirurgiaNota.get(0).getProcedimentoCirurgico().getIndNsSemPront();
			}
		} else {
			valorIndNsSemPront = listaProfissionalPorCirurgiaAgenda.get(0).getProcedimentoCirurgico().getIndNsSemPront();
		}
		boolean isProntuarioNulo = vo.getCirurgia().getPaciente() != null && vo.getCirurgia().getPaciente().getProntuario() == null;
		if (pacCodigoFonetica==null &&  isProntuarioNulo && (Boolean.FALSE.equals(valorIndNsSemPront) || DominioOrigemPacienteCirurgia.I.equals(vo.getCirurgia().getOrigemPacienteCirurgia()))) {
			if (vo.getCirurgia().getPaciente() != null && vo.getCirurgia().getPaciente().getCodigo() != null) {
				// Paciente não possui prontuário. Cadastre antes de digitar a Nota de Sala.
				retorno.add(RegistroCirurgiaRealizadaONExceptionCode.MBC_00487.toString());
			}
		}
		return retorno;
	}

	/**
	 * ORADB PROCEDURE EVT_PRE_COMMIT
	 * <p>
	 * RN7.11 Executada ao gravar
	 * <p>
	 * 
	 * @param vo
	 * @throws BaseException
	 */
	public void validarPartoCesarea(MbcCirurgias cirurgia, CirurgiaTelaVO vo) throws BaseException {
		Date valorDthrEntSlPrep = null;
		Date valorDthrEntSala = null;
		Date valorDthrSaiSala = null;
		Date valorDthrIniCirg = null;
		Date valorDthrFimCirg = null;
		Date valorDthrIniAnst = null;
		Date valorDthrFimAnst = null;

		// Rotina para buscar valor da tabela de parâmetros que define o procedimento como parto ou cesarea
		AghParametros pAnalgesiaParto = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ANALGESIA_PARTO);
		final Integer valorParto = pAnalgesiaParto.getVlrNumerico().intValue();

		AghParametros pCesareana = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CESAREANA);
		final Integer valorCesareana = pCesareana.getVlrNumerico().intValue();

		// RN1
		if (Boolean.TRUE.equals(cirurgia.getDigitaNotaSala())) {

			// Equivalente ao ANC.TAN_SEQ
			final boolean exiteTanseq = vo.getListaAnestesiasVO() != null && !vo.getListaAnestesiasVO().isEmpty();

			// RN1.1 Valida tipo anestesia necessitar de anestesista. Atenção ao GLOBAL.DEVE_TER_ANESTESISTA
			this.validarNecessidadeAnestesia(cirurgia.getDataInicioAnestesia(), cirurgia.getDataFimAnestesia(), vo.getDeveTerAnestesista(), exiteTanseq);

			// RN1.2
			if (cirurgia.getDataEntradaSala() != null && cirurgia.getDataSaidaSala() != null) {
				valorDthrEntSala = cirurgia.getDataEntradaSala();
				valorDthrSaiSala = cirurgia.getDataSaidaSala();
				if (DateUtil.validaDataMaior(valorDthrEntSala, valorDthrSaiSala)) {
					throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01077);
				}
			} else {
				throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01075);
			}

			// RN1.3
			if (cirurgia.getDataInicioCirurgia() != null && cirurgia.getDataFimCirurgia() != null) {
				valorDthrIniCirg = cirurgia.getDataInicioCirurgia();
				valorDthrFimCirg = cirurgia.getDataFimCirurgia();
				if (DateUtil.validaDataMaior(valorDthrIniCirg, valorDthrFimCirg)) {
					throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01078);
				}
			} else {
				throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01076);
			}

			if (cirurgia.getDataInicioAnestesia() != null && cirurgia.getDataFimAnestesia() != null) {

				valorDthrIniAnst = cirurgia.getDataInicioAnestesia();
				valorDthrFimAnst = cirurgia.getDataFimAnestesia();

				// Valida data do fim e início anestesia
				this.validarDatasAnestesia(valorDthrFimAnst, valorDthrIniAnst);

				// CURSOR C_SLPREP
				List<MbcExtratoCirurgia> listaSolucaoPreparada = this.getMbcExtratoCirurgiaDAO().pesquisarMbcExtratoCirurgiaPorCirurgiaSituacao(cirurgia.getSeq(),
						DominioSituacaoCirurgia.PREP);

				// Cálculo dos minutos anteriores a entrada na sala que é permitido anestesia
				valorDthrEntSlPrep = this.getValorDthrEntSlPrep(cirurgia.getDataEntradaSala(), listaSolucaoPreparada);

				// CURSOR CUR_PPC_PARTO_CESAREA
				List<MbcProcEspPorCirurgias> listaPartoCesarea = this.getMbcProcEspPorCirurgiasDAO().pesquisarProcedimentosPartoCesarea(cirurgia.getSeq(), valorParto,
						valorCesareana);

				if (listaPartoCesarea.isEmpty()) {
					if (!DateUtil.validaDataMenorIgual(valorDthrEntSlPrep, valorDthrIniAnst)) {
						/*
						 * Data/hora de início da anestesia deve ser maior que a entrada em sala de preparo ou pode iniciar até 1 hora antes da entrada em sala cirurgica Data/hora
						 * de início da anestesia deve ser maior que a entrada em sala de preparo ou pode iniciar até 1 hora antes da entrada em sala cirurgica
						 */
						throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01079);
					}
					if (!(DateUtil.validaDataMenorIgual(valorDthrIniAnst, valorDthrIniCirg) && DateUtil.validaDataMenorIgual(valorDthrIniCirg, valorDthrFimCirg)
							&& DateUtil.validaDataMenorIgual(valorDthrFimCirg, valorDthrFimAnst) && DateUtil.validaDataMenor(valorDthrFimAnst, valorDthrSaiSala))) {
						throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01339);
					}
				}
			} else {
				boolean isDatasValidas = DateUtil.validaDataMenorIgual(valorDthrEntSala, valorDthrIniCirg) && DateUtil.validaDataMenorIgual(valorDthrIniCirg, valorDthrFimCirg)
				&& DateUtil.validaDataMenorIgual(valorDthrFimCirg, valorDthrSaiSala);
				if (!isDatasValidas) {
					// Data/hora de entrada na sala deve ser < inicio da cirurgia fim da cirurgia < saida da sala. Confira a sequencia de datas.
					throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01080);
				}
			}
		}
	}

	/*
	 * Os métodos a seguir dizem respeito as regras de ORADB PROCEDURE EVT_PRE_COMMIT RN7.11 FORAM CRIADOS PARA CORRIGIR AS VIOLAÇÕES DE PMD ENVOLVENDO NPATH COMPLEXITY
	 */

	private void validarNecessidadeAnestesia(Date valorDthrIniAnst, Date valorDthrFimAnst, Boolean deveTerAnestesista, Boolean exiteTanseq) throws ApplicationBusinessException {
		if ((valorDthrIniAnst == null || valorDthrFimAnst == null) && Boolean.TRUE.equals(deveTerAnestesista) && exiteTanseq) {
			// Quando tipo anestesia necessitar de anestesista, datas de início e fim da anestesia devem estar preenchidas.
			throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01048);
		}
	}

	private void validarDatasAnestesia(Date valorDthrFimAnst, Date valorDthrIniAnst) throws BaseException {
		if (DateUtil.validaDataMenorIgual(valorDthrFimAnst, valorDthrIniAnst)) {
			throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MBC_01338);
		}
	}

	private Date getValorDthrEntSlPrep(Date dataEntradaSala, List<MbcExtratoCirurgia> listaSolucaoPreparada) throws ApplicationBusinessException {
		if (listaSolucaoPreparada.isEmpty()) {
			AghParametros pMinIniAnestAntesEnt = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MIN_INI_ANEST_ANTES_ENT);
			final Integer valorMinIniAnestAntesEnt = pMinIniAnestAntesEnt.getVlrNumerico().intValue();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dataEntradaSala);
			calendar.add(Calendar.MINUTE, -valorMinIniAnestAntesEnt); // ATENÇÃO: Desconta tempo em MINUTOS
			return calendar.getTime();
		}
		return listaSolucaoPreparada.get(0).getCriadoEm();
	}
	
	public List<CirurgiaCodigoProcedimentoSusVO> codigoProcedimentoSusVOs(
			Integer pciSeq, Short espSeq, DominioOrigemPacienteCirurgia origem) throws ApplicationBusinessException {
		return getMbcfEscolheFatRN().calcularValorTotalEValorPerm(pciSeq, espSeq, origem);
	}


	@SuppressWarnings("PMD.NPathComplexity")
	public List<CirurgiaCodigoProcedimentoSusVO> listarCodigoProcedimentos(CirurgiaTelaProcedimentoVO procedimentoVO, DominioOrigemPacienteCirurgia origem)
	throws ApplicationBusinessException {

		List<CirurgiaCodigoProcedimentoSusVO> listRetorno = new ArrayList<CirurgiaCodigoProcedimentoSusVO>();

		Byte origemCodigo = getMbcfEscolheFatRN().getOrigemCodigo(origem);

		Date valorDtComp = getDataComp(origem);

		final Integer pciSeq = procedimentoVO.getProcedimentoCirurgico().getSeq();
		final AghParametros grupoSUS = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);

		List<Integer> phiSeqList = this.getFaturamentoFacade().buscarPhiSeqPorPciSeqOrigemPacienteCodigo(pciSeq, origemCodigo, grupoSUS.getVlrNumerico().shortValue());

		for (Integer phiSeq : phiSeqList) {
			List<CirurgiaCodigoProcedimentoSusVO> listCirurgiaCodigoProcedimento = this.getFaturamentoFacade().obterListaCirurgiaCodigoProcedimentoPorPhiSeqOrigemGrupo(phiSeq,
					grupoSUS.getVlrNumerico().shortValue(), origemCodigo);
			// Para cada phiSeq encontrado, realiza a consulta C1 e adiciona o resultado na lista final.
			listRetorno.addAll(listCirurgiaCodigoProcedimento);
		}
		// Percorre a lista final adicionando o valor total e valor perm
		for (CirurgiaCodigoProcedimentoSusVO vo : listRetorno) {
			List<FatVlrItemProcedHospComps> listaVlrItemProcedHospComps = this.getFaturamentoFacade().pesquisarVlrItemProcedHospCompsPorIphSeqDtComp(vo.getIphSeq(),
					vo.getIphPhoSeq(), valorDtComp);

			BigDecimal valorTotal = BigDecimal.ZERO;
			BigDecimal valorPerm = BigDecimal.ZERO;
			if (listaVlrItemProcedHospComps != null && !listaVlrItemProcedHospComps.isEmpty()) {
				FatVlrItemProcedHospComps itemSmm = listaVlrItemProcedHospComps.get(0);

				if (DominioOrigemPacienteCirurgia.I.equals(origem)) {

					// Faz o NVL do cursor aqui
					BigDecimal vlrServHospitalar = itemSmm.getVlrServHospitalar() != null ? itemSmm.getVlrServHospitalar() : BigDecimal.ZERO;
					BigDecimal vlrServProfissional = itemSmm.getVlrServProfissional() != null ? itemSmm.getVlrServProfissional() : BigDecimal.ZERO;
					BigDecimal vlrSadt = itemSmm.getVlrSadt() != null ? itemSmm.getVlrSadt() : BigDecimal.ZERO;
					BigDecimal vlrProcedimento = itemSmm.getVlrProcedimento() != null ? itemSmm.getVlrProcedimento() : BigDecimal.ZERO;
					BigDecimal vlrAnestesia = itemSmm.getVlrAnestesista() != null ? itemSmm.getVlrAnestesista() : BigDecimal.ZERO;

					valorTotal = valorTotal.add(vlrServHospitalar).add(vlrServProfissional).add(vlrSadt).add(vlrProcedimento).add(vlrAnestesia);

				} else {
					valorTotal = itemSmm.getVlrProcedimento() != null ? itemSmm.getVlrProcedimento() : BigDecimal.ZERO;
				}
				valorPerm = (new BigDecimal(itemSmm.getFatItensProcedHospitalar().getQuantDiasFaturamento() != null ? itemSmm.getFatItensProcedHospitalar()
						.getQuantDiasFaturamento() : 0));
			}
			vo.setValorTotal(valorTotal);
			vo.setValorPerm(valorPerm);
		}
		return listRetorno;
	}

	private Date getDataComp(DominioOrigemPacienteCirurgia origem) throws ApplicationBusinessException {
		return getDataCompetenciaRegistroCirurgiaRealizadoRN().getValorDataCompetencia(origem);
	}
		
	public void executaRotinaParaSetarResponsavelAoConfirmarNotaConsumo(List<CirurgiaTelaProfissionalVO> listaProfissionais, Short seqUnidadeCirurgica) throws ApplicationBusinessException{
		AghCaractUnidFuncionais unidadeCCA = 
		getAghuFacade().obterAghCaractUnidFuncionaisPorChavePrimaria(new AghCaractUnidFuncionaisId(seqUnidadeCirurgica, ConstanteAghCaractUnidFuncionais.CCA));			
		if(this.isHCPA() && unidadeCCA != null){ 
			if(listaProfissionais != null && listaProfissionais.size() > 0){
				for (CirurgiaTelaProfissionalVO vos : listaProfissionais) {
					if(vos.getIndResponsavel()){
						vos.setIndRealizou(true);
						return;
					}
				}
			}
		}
	}
		
		
	/**
	 * Seta o Cid ao procedimento principal quando o procedimento principal possui apenas um Cid
	 * @param listaProcedimentos
	 * @param tipoPlano
	 */
	public void executarRotinaVincularCidUnicoAoProcedimento(List<CirurgiaTelaProcedimentoVO> listaProcedimentos, DominioTipoPlano tipoPlano) 
			throws ApplicationBusinessException {
		List<AghCid> listCids = new ArrayList<AghCid>();
		List<CirurgiaTelaProcedimentoVO> listProcedimentos = listaProcedimentos;
		for (CirurgiaTelaProcedimentoVO cirurgiaTelaProcedimentoVO : listProcedimentos) {
			if(cirurgiaTelaProcedimentoVO.getIndPrincipal()){
				if (cirurgiaTelaProcedimentoVO.getPhiSeq()!=null){
					listCids = this.getBlocoCirurgicoFacade().pesquisarCidsPorPhiSeq(cirurgiaTelaProcedimentoVO.getPhiSeq(), tipoPlano, "");
				}	
				else {
					listCids = this.getBlocoCirurgicoFacade().pesquisarCidsPorPciSeq(cirurgiaTelaProcedimentoVO.getId().getEprPciSeq(), null  , tipoPlano, "");
				}

				if(listCids != null && listCids.size() == 1){
					cirurgiaTelaProcedimentoVO.setCid(listCids.get(0));
				}
				return;
			}
		}
	}	
	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.iFaturamentoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	protected IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return this.iBlocoCirurgicoCadastroApoioFacade;
	}

	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcExtratoCirurgiaDAO getMbcExtratoCirurgiaDAO() {
		return mbcExtratoCirurgiaDAO;
	}

	protected MbcProcEspPorCirurgiasRN getMbcProcEspPorCirurgiasRN() {
		return mbcProcEspPorCirurgiasRN;
	}

	protected MbcProfCirurgiasRN getMbcProfCirurgiasRN() {
		return mbcProfCirurgiasRN;
	}

	protected MbcCirurgiasVerificacoesRN getMbcCirurgiasVerificacoesRN() {
		return mbcCirurgiasVerificacoesRN;
	}

	protected MbcCirurgiasVerificacoes2RN getMbcCirurgiasVerificacoes2RN() {
		return mbcCirurgiasVerificacoes2RN;
	}

	protected VerificaTipoAnestesiaRN getVerificaTipoAnestesiaRN() {
		return verificaTipoAnestesiaRN;
	}

	protected VerificaEspecialidadeNotaRN getVerificaEspecialidadeNotaRN() {
		return verificaEspecialidadeNotaRN;
	}

	protected MbcCirurgiasRN getMbcCirurgiasRN() {
		return mbcCirurgiasRN;
	}

	protected PopulaProcedimentoHospitalarInternoRN getPopulaProcedimentoHospitalarInternoRN() {
		return populaProcedimentoHospitalarInternoRN;
	}

	protected AgendaProcedimentosON getAgendaProcedimentosON() {
		return agendaProcedimentosON;
	}

	protected PopulaCodigoSsmRN getPopulaCodigoSsmRN() {
		return populaCodigoSsmRN;
	}
	
	protected DataCompetenciaRegistroCirurgiaRealizadoRN getDataCompetenciaRegistroCirurgiaRealizadoRN() {
		return dataCompetenciaRegistroCirurgiaRealizadoRN;
	}
	
	protected ValorCadastralProcedimentoSusRN  getValorCadastralProcedimentoSusRN(){
		return valorCadastralProcedimentoSusRN;
	}
	
	protected MbcfEscolheFatRN  getMbcfEscolheFatRN(){
		return mbcfEscolheFatRN;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}
	
}