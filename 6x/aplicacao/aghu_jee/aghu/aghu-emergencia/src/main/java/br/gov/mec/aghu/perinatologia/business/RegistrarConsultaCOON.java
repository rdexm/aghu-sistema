package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.service.ICascaService;
import br.gov.mec.aghu.certificacaodigital.service.ICertificacaoDigitalService;
import br.gov.mec.aghu.certificacaodigital.service.vo.DadosDocumentoVO;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.CidVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.dominio.DominioFormaRupturaBolsaRota;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.DesbloqueioConsultaCOVO;
import br.gov.mec.aghu.emergencia.vo.FiltroVerificaoInclusaoAnamneseVO;
import br.gov.mec.aghu.internacao.service.IInternacaoService;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoAnamneseEfsJn;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.McoDiagnostico;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoLogImpressoesId;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.perinatologia.dao.McoAnamneseEfsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoAnamneseEfsJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoBolsaRotasDAO;
import br.gov.mec.aghu.perinatologia.dao.McoCondutaDAO;
import br.gov.mec.aghu.perinatologia.dao.McoDiagnosticoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoLogImpressoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoPlanoIniciaisDAO;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;
import br.gov.mec.aghu.util.EmergenciaParametrosColunas;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;

@Stateless
public class RegistrarConsultaCOON extends BaseBusiness {

	private static final int NUMERO_HORAS_DIA = 24;
	private static final long serialVersionUID = 1370011984348347148L;
	private static final String NOME_MODULO_PRESCRICAO_MEDICA = "prescricaomedica";
	private static final Integer VALOR_DEFAULT_P_AGHU_IDADE_GEST_PRIM_ECO = 25;
	
	@EJB
	private IPacienteService pacienteService;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IConfiguracaoService configuracaoService;
	
	@EJB
	private IRegistroColaboradorService registroColaboradorService;
		
	@Inject
	private McoAnamneseEfsDAO anamneseEfsDAO;
	
	@Inject
	private McoGestacoesDAO gestacoesDAO;
	
	@Inject
	private McoDiagnosticoDAO diagnosticoDAO;
	
	@Inject
	private McoCondutaDAO condutaDAO;
	
	@Inject
	private McoPlanoIniciaisDAO mcoPlanoIniciaisDAO;
	
	@Inject
	private McoBolsaRotasDAO bolsaRotasDAO;
	
	@EJB
	private RegistrarConsultaCORN consultaCORN;
	
	@EJB
	private McoAnamneseEfsRN anamneseEfsRN;
	
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private McoAnamneseEfsJnDAO mcoAnamneseEfsJnDAO;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;

	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private McoLogImpressoesDAO mcoLogImpressoesDAO;
	
	@EJB
	private IInternacaoService internacaoService;
	
	@EJB
	private ICertificacaoDigitalService certificacaoDigitalService;
	
	@EJB
	private ICascaService cascaService;
	
	@Inject
	private AacConsultasDAO consultasDAO;
	
	private enum RegistrarConsultaCOONExceptionCode implements BusinessExceptionCode {
		FILTRO_INVALIDO,
		MENSAGEM_SERVICO_INDISPONIVEL,
		MENSAGEM_SERVICO_INDISPONIVEL_RELATORIO,
		MENSAGEM_ERRO_PARAMETRO,
		MSG_REG_CONS_CO_MCO_00537_1,
		MSG_REG_CONS_CO_MCO_00539_1,
		ERRO_DATA_HORA_OBRIGATORIA_AMNIOTOMIA,
		ERRO_DATA_HORA_IGNORADO_OBRIGATORIO_AMNIORREXIS,
		ERRO_SERVICO_INGRESSAR_PACIENTE_SO,
		MENSAGEM_ERRO_MODULO_INATIVO,
		MSG_MPM_01201,
		ERRO_SERVICO_MESSAGEM,
		MENSAGEM_ERRO_PACIENTE_INTERNADO,
		MSG_EXCECAO_MCO_00620;
	}

	@Override
	protected Log getLogger() {
		return null;
	}
	
	// C1
	public Boolean isAnamnese(FiltroVerificaoInclusaoAnamneseVO filtroVO) throws BaseException{
		
		try {
			Validate.notNull(filtroVO);
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.FILTRO_INVALIDO);
		}
		
		return anamneseEfsDAO
				.countMcoAnamneseEfsPorNrConsultaECodPacGestanteESeqGestante(
						filtroVO.getConsulta(), filtroVO.getPaciente(), filtroVO.getSequence()) > 0 ? Boolean.TRUE : Boolean.FALSE;
	}
	
	// C2
	public List<McoAnamneseEfs> listarMcoAnamnese(Integer paciente, Short sequence){
		return anamneseEfsDAO.listarMcoAnamneseEfsPorCodPacGestanteESeqGestante(paciente, sequence);
	}
	
	//C3
	public Long obtemQuantidadeDefetosDaGestante(Integer pacCodigo, Short seqp){
		String qtd = gestacoesDAO.countMcoGestacaoPorId(pacCodigo, seqp);
		if(qtd!=null  && !qtd.isEmpty() && !qtd.equals("N")){
			return Long.valueOf(gestacoesDAO.countMcoGestacaoPorId(pacCodigo, seqp));
		}
		return Long.valueOf(0);
	}
	
	//C4
	public List<McoDiagnostico> pesquisarDiagnosticoSuggestion(String strPesquisa){
		return diagnosticoDAO.pesquisarDiagnosticoSuggestion(strPesquisa, DominioSituacao.A);
	}
	
	public Long pesquisarDiagnosticoSuggestionCount(String strPesquisa){
		return diagnosticoDAO.pesquisarDiagnosticoSuggestionCount(strPesquisa, DominioSituacao.A);
	}
	
	// C6
	public List<McoConduta> pesquisarMcoCondutaSuggestion(String strPesquisa) {
		return condutaDAO.pesquisarMcoCondutaSuggestion(strPesquisa, DominioSituacao.A);
	}
	
	public Long pesquisarMcoCondutaSuggestionCount(String strPesquisa) {
		return condutaDAO.pesquisarMcoCondutaSuggestionCount(strPesquisa, DominioSituacao.A);
	}
	
	// C7
	public List<McoPlanoIniciais> listarMcoPlanoIniciaisConduta(Integer efiConNumero, Short efiGsoSeqp, Integer efiGsoPacCodigo) {
		return mcoPlanoIniciaisDAO.listarMcoPlanoIniciaisCondutaPorConsultaCodigoSeqp(efiConNumero, efiGsoSeqp, efiGsoPacCodigo);
	}
	
	// C8
	public List<McoBolsaRotas> listarBolsasRotas(Integer codigoPaciente, Short sequence) {
		return bolsaRotasDAO.listarBolsasRotas(codigoPaciente, sequence);
	}
	
	// C5 SERVIÇO
	public List<CidVO> pesquisarCIDSuggestion(final String strPesquisa) throws ApplicationBusinessException {
		List<CidVO> result = null;
		try {
			result = this.configuracaoService.pesquisarCidPorCodigoDescricao(strPesquisa);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	public Long pesquisarCIDSuggestionCount(final String strPesquisa) throws ApplicationBusinessException {
		Long count = null;
		try {
			count = this.configuracaoService.pesquisarCidPorCodigoDescricaoCount(strPesquisa);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return count;
	}
	
	public void ingressarPacienteSO(Integer codigoPaciente, Integer numeroConsulta, String nomeMicroComputador) throws ApplicationBusinessException{
		
		try {
			this.pacienteFacade.ingressarPacienteEmergenciaObstetricaSalaObservacao(numeroConsulta, codigoPaciente, nomeMicroComputador);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.ERRO_SERVICO_INGRESSAR_PACIENTE_SO, 
					getResourceBundleValue(e.getMessage(), e.getParameters()));
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		
		BigDecimal sitiacaoObservacao = (BigDecimal) this.buscarParametroPorNome(EmergenciaParametrosEnum.P_SIT_OBSERVACAO, EmergenciaParametrosColunas.VLR_NUMERICO);
			
		MamTrgEncInterno mamTrgEncInterno = mamTrgEncInternoDAO.buscarTriagemPorConsulta(numeroConsulta);
		
		MamSituacaoEmergencia mamSituacaoEmergencia = null;
		
		if(sitiacaoObservacao != null){
			mamSituacaoEmergencia = mamSituacaoEmergenciaDAO.obterPorSeq(Short.valueOf(sitiacaoObservacao.toString()));
		}
		
		mamTrgEncInterno.getTriagem().setSituacaoEmergencia(mamSituacaoEmergencia);

		MamTriagens triagens = mamTriagensDAO.obterPorChavePrimaria(mamTrgEncInterno.getTriagem().getSeq());
		
		emergenciaFacade.atualizarSituacaoTriagem(mamTrgEncInterno.getTriagem(), triagens, nomeMicroComputador);
	
	}
	
	public void elaborarPrescricaoMedica(Integer numeroConsulta, Short seqUnidadeFuncional) throws ApplicationBusinessException {
		
		verificaModuloAtivo();
		
		unidadeFuncionalPossuiCaracteristica(numeroConsulta, seqUnidadeFuncional);
		
		verificaPacienteInternadoIngressoSO(numeroConsulta);	
		
	}

	public void verificaPacienteInternadoIngressoSO(Integer numeroConsulta) throws ApplicationBusinessException {
		Boolean pacienteInternado = Boolean.FALSE;
		Boolean pacienteIngressoSO = Boolean.FALSE;
		
		try {
			pacienteInternado = internacaoService.verificarPacienteInternadoPorConsulta(numeroConsulta);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.ERRO_SERVICO_MESSAGEM, e.getMessage());
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		
		try {
			pacienteIngressoSO = internacaoService.verificarPacienteIngressoSOPorConsulta(numeroConsulta);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.ERRO_SERVICO_MESSAGEM, e.getMessage());
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		
		if(!pacienteInternado && !pacienteIngressoSO){
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_ERRO_PACIENTE_INTERNADO);
		}
	}

	public void unidadeFuncionalPossuiCaracteristica(Integer numeroConsulta, Short seqUnidadeFuncional) throws ApplicationBusinessException {
		if (seqUnidadeFuncional == null) {
			MamTrgEncInterno mamTrgEncInterno = mamTrgEncInternoDAO.buscarTriagemPorConsulta(numeroConsulta);
			if (mamTrgEncInterno != null && mamTrgEncInterno.getTriagem() != null) {
				seqUnidadeFuncional = mamTrgEncInterno.getTriagem().getUnidadeFuncional().getSeq() ;
			}
		}
		
		Boolean unidadeFuncionalPossuiCaracteristica = Boolean.FALSE;
		
		unidadeFuncionalPossuiCaracteristica = this.aghuFacade.existeCaractUnidFuncionaisPorSeqCaracteristica(seqUnidadeFuncional, ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
		
		if (!unidadeFuncionalPossuiCaracteristica) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MSG_MPM_01201);
		}
		
	}

	private void verificaModuloAtivo() throws ApplicationBusinessException {
		
		Boolean moduloAtivo = cascaService.verificarSeModuloEstaAtivo(NOME_MODULO_PRESCRICAO_MEDICA);
		
		if(!moduloAtivo){
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_ERRO_MODULO_INATIVO, "Prescrição Médica");
		}
	}
	
	public void excluirCondutas(McoPlanoIniciais planoIniciais, McoAnamneseEfs anamneseEfs){
		consultaCORN.excluirCondutas(planoIniciais, anamneseEfs);
	}

	public void validarDataConsulta(Date dthrConsulta) throws ApplicationBusinessException {
		consultaCORN.validarDataConsulta(dthrConsulta);
	}

	public void persistirMcoAnamneseEfs(McoAnamneseEfs anamneseEfs, Integer conNumero, Integer pacCodigo, Short seqp) throws BaseException {
		anamneseEfsRN.persistir(anamneseEfs, conNumero, pacCodigo, seqp);
	}

	public void verificarDilatacao(String dilatacao) throws ApplicationBusinessException {
		consultaCORN.verificarDilatacao(dilatacao);
	}
	
	public void gravarConsultaCO(McoAnamneseEfs anamneseEfs, McoAnamneseEfs anamneseEfsOriginal) throws ApplicationBusinessException{
		
		this.verificarAlturaUterina(anamneseEfs);
		this.verificarBatimentoCardiacoFetal(anamneseEfs);
		this.verificarFormaRuptura(anamneseEfs);
		this.verificarAlteracaoFormaRuptura(anamneseEfs, anamneseEfsOriginal);

		anamneseEfs.setSerMatricula(usuario.getMatricula());
		anamneseEfs.setSerVinCodigo(usuario.getVinculo());
		this.anamneseEfsDAO.atualizar(anamneseEfs);
		if(objetoModificado(anamneseEfs, anamneseEfsOriginal)){
			this.inserirJournalAnamneseEfs(anamneseEfs);
		}
	}
	
	private boolean objetoModificado(McoAnamneseEfs anamneseEfs, McoAnamneseEfs anamneseEfsOriginal) {
		return CoreUtil.modificados(anamneseEfs.getDataHoraRompimento(), anamneseEfsOriginal.getDataHoraRompimento()) || 
		CoreUtil.modificados(anamneseEfs.getDthrConsulta(), anamneseEfsOriginal.getDthrConsulta()) ||
		CoreUtil.modificados(anamneseEfs.getMotivo(), anamneseEfsOriginal.getMotivo()) ||
		CoreUtil.modificados(anamneseEfs.getPressaoArtSistolica(), anamneseEfsOriginal.getPressaoArtSistolica()) ||
		CoreUtil.modificados(anamneseEfs.getPressaoArtDiastolica(), anamneseEfsOriginal.getPressaoArtDiastolica()) ||
		CoreUtil.modificados(anamneseEfs.getFreqCardiaca(), anamneseEfsOriginal.getFreqCardiaca()) ||
		CoreUtil.modificados(anamneseEfs.getFreqRespiratoria(), anamneseEfsOriginal.getFreqRespiratoria()) ||
		CoreUtil.modificados(anamneseEfs.getTemperatura(), anamneseEfsOriginal.getTemperatura()) ||
		CoreUtil.modificados(anamneseEfs.getEdema(), anamneseEfsOriginal.getEdema()) ||
		CoreUtil.modificados(anamneseEfs.getAlturaUterina(), anamneseEfsOriginal.getAlturaUterina()) ||
		CoreUtil.modificados(anamneseEfs.getDinamicaUterina(), anamneseEfsOriginal.getDinamicaUterina()) ||
		CoreUtil.modificados(anamneseEfs.getIntensidadeDinUterina(), anamneseEfsOriginal.getIntensidadeDinUterina()) ||
		CoreUtil.modificados(anamneseEfs.getIndAcelTrans(), anamneseEfsOriginal.getIndAcelTrans()) ||
		CoreUtil.modificados(anamneseEfs.getSitFetal(), anamneseEfsOriginal.getSitFetal()) ||
		CoreUtil.modificados(anamneseEfs.getExameEspecular(), anamneseEfsOriginal.getExameEspecular()) ||
		CoreUtil.modificados(anamneseEfs.getPosicaoCervice(), anamneseEfsOriginal.getPosicaoCervice()) ||
		CoreUtil.modificados(anamneseEfs.getEspessuraCervice(), anamneseEfsOriginal.getEspessuraCervice()) ||
		CoreUtil.modificados(anamneseEfs.getApagamento(), anamneseEfsOriginal.getApagamento()) ||
		CoreUtil.modificados(anamneseEfs.getDilatacao(), anamneseEfsOriginal.getDilatacao()) ||
		CoreUtil.modificados(anamneseEfs.getApresentacao(), anamneseEfsOriginal.getApresentacao()) ||
		CoreUtil.modificados(anamneseEfs.getPlanoDelee(), anamneseEfsOriginal.getPlanoDelee()) ||
		CoreUtil.modificados(anamneseEfs.getIndPromontorioAcessivel(), anamneseEfsOriginal.getIndPromontorioAcessivel()) ||
		CoreUtil.modificados(anamneseEfs.getIndEspCiaticaSaliente(), anamneseEfsOriginal.getIndEspCiaticaSaliente()) ||
		CoreUtil.modificados(anamneseEfs.getIndSubPubicoMenor90(), anamneseEfsOriginal.getIndSubPubicoMenor90()) ||
		CoreUtil.modificados(anamneseEfs.getAcv(), anamneseEfsOriginal.getAcv()) ||
		CoreUtil.modificados(anamneseEfs.getAr(), anamneseEfsOriginal.getAr()) ||
		CoreUtil.modificados(anamneseEfs.getObservacao(), anamneseEfsOriginal.getObservacao()) ||
		CoreUtil.modificados(anamneseEfs.getDigSeq(), anamneseEfsOriginal.getDigSeq()) ||
		CoreUtil.modificados(anamneseEfs.getCidSeq(), anamneseEfsOriginal.getCidSeq()) ||
		CoreUtil.modificados(anamneseEfs.getIndMovFetal(), anamneseEfsOriginal.getIndMovFetal()) ||	
		CoreUtil.modificados(anamneseEfs.getSerMatricula(), anamneseEfsOriginal.getSerMatricula()) ||
		CoreUtil.modificados(anamneseEfs.getSerVinCodigo(), anamneseEfsOriginal.getSerVinCodigo()) ||
		CoreUtil.modificados(anamneseEfs.getExFisicoGeral(), anamneseEfsOriginal.getExFisicoGeral()) ||
		CoreUtil.modificados(anamneseEfs.getPressaoSistRepouso(), anamneseEfsOriginal.getPressaoSistRepouso()) ||
		CoreUtil.modificados(anamneseEfs.getPressaoDiastRepouso(), anamneseEfsOriginal.getPressaoDiastRepouso()) ||
		CoreUtil.modificados(anamneseEfs.getPaciente(), anamneseEfsOriginal.getPaciente()) ||
		CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal(), anamneseEfsOriginal.getBatimentoCardiacoFetal()) ||
		CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal2(), anamneseEfsOriginal.getBatimentoCardiacoFetal2()) ||
		CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal3(), anamneseEfsOriginal.getBatimentoCardiacoFetal3()) ||
		CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal4(), anamneseEfsOriginal.getBatimentoCardiacoFetal4()) ||
		CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal5(), anamneseEfsOriginal.getBatimentoCardiacoFetal5()) ||
		CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal6(), anamneseEfsOriginal.getBatimentoCardiacoFetal6()) ||
		CoreUtil.modificados(anamneseEfs.getFormaRuptura(), anamneseEfsOriginal.getFormaRuptura()) ||
		CoreUtil.modificados(anamneseEfs.getDataHoraRompimento(), anamneseEfsOriginal.getDataHoraRompimento()) ||
		CoreUtil.modificados(anamneseEfs.getIndDthrIgnorada(), anamneseEfsOriginal.getIndDthrIgnorada()) ||
		CoreUtil.modificados(anamneseEfs.getLiquidoAmniotico(), anamneseEfsOriginal.getLiquidoAmniotico()) ||
		CoreUtil.modificados(anamneseEfs.getIndOdorFetido(), anamneseEfsOriginal.getIndOdorFetido());
	}
	private void verificarAlturaUterina(McoAnamneseEfs anamneseEfs) throws ApplicationBusinessException{
		if(anamneseEfs.getAlturaUterina() != null){
			if(anamneseEfs.getAlturaUterina() < 10 || anamneseEfs.getAlturaUterina() > 50){
				throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MSG_REG_CONS_CO_MCO_00537_1);
			}
		}
	}
	
	private void verificarBatimentoCardiacoFetal(McoAnamneseEfs anamneseEfs) throws ApplicationBusinessException{
		
		if(anamneseEfs.getBatimentoCardiacoFetal() != null && (anamneseEfs.getBatimentoCardiacoFetal() < 0 || anamneseEfs.getBatimentoCardiacoFetal() > 200)){
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MSG_REG_CONS_CO_MCO_00539_1);
			
		} else if(anamneseEfs.getBatimentoCardiacoFetal2() != null && (anamneseEfs.getBatimentoCardiacoFetal2() < 0 || anamneseEfs.getBatimentoCardiacoFetal2() > 200)){
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MSG_REG_CONS_CO_MCO_00539_1);
			
		} else if(anamneseEfs.getBatimentoCardiacoFetal3() != null && (anamneseEfs.getBatimentoCardiacoFetal3() < 0 || anamneseEfs.getBatimentoCardiacoFetal3() > 200)){
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MSG_REG_CONS_CO_MCO_00539_1);
			
		} else if(anamneseEfs.getBatimentoCardiacoFetal4() != null && (anamneseEfs.getBatimentoCardiacoFetal4() < 0 || anamneseEfs.getBatimentoCardiacoFetal4() > 200)){
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MSG_REG_CONS_CO_MCO_00539_1);
			
		} else if(anamneseEfs.getBatimentoCardiacoFetal5() != null && (anamneseEfs.getBatimentoCardiacoFetal5() < 0 || anamneseEfs.getBatimentoCardiacoFetal5() > 200)){
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MSG_REG_CONS_CO_MCO_00539_1);
			
		} else if(anamneseEfs.getBatimentoCardiacoFetal6() != null && (anamneseEfs.getBatimentoCardiacoFetal6() < 0 || anamneseEfs.getBatimentoCardiacoFetal6() > 200)){
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MSG_REG_CONS_CO_MCO_00539_1);
		}
	}
	
	private void verificarFormaRuptura(McoAnamneseEfs anamneseEfs) throws ApplicationBusinessException{
		if(DominioFormaRupturaBolsaRota.Amniotomia.equals(anamneseEfs.getFormaRuptura())){
			if(anamneseEfs.getDataHoraRompimento() == null){
				throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.ERRO_DATA_HORA_OBRIGATORIA_AMNIOTOMIA);
			}
		}
		
		if(DominioFormaRupturaBolsaRota.Amniorrexis.equals(anamneseEfs.getFormaRuptura())){ 
			if(anamneseEfs.getDataHoraRompimento() == null && anamneseEfs.getIndDthrIgnorada() == null){
				throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.ERRO_DATA_HORA_IGNORADO_OBRIGATORIO_AMNIORREXIS);
			}
		}
	}
	
	private void verificarAlteracaoFormaRuptura(McoAnamneseEfs anamneseEfs, McoAnamneseEfs anamneseEfsOriginal) throws ApplicationBusinessException {
		if(anamneseEfs.getFormaRuptura() != anamneseEfsOriginal.getFormaRuptura()
				|| anamneseEfs.getDataHoraRompimento() != anamneseEfsOriginal.getDataHoraRompimento()
				|| anamneseEfs.getIndDthrIgnorada() != anamneseEfsOriginal.getIndDthrIgnorada()
				|| anamneseEfs.getLiquidoAmniotico() != anamneseEfsOriginal.getLiquidoAmniotico()
				|| anamneseEfs.getIndOdorFetido() != anamneseEfsOriginal.getIndOdorFetido()){
			
			List<McoBolsaRotas> listaBolsas = this.listarBolsasRotas(anamneseEfs.getPaciente().getCodigo(), anamneseEfs.getId().getGsoSeqp());
			
			if(listaBolsas != null && !listaBolsas.isEmpty()){
				
				// Executa RN04 e RN05 da estória #25663
				for (McoBolsaRotas bolsa : listaBolsas) {
					bolsa.setDominioFormaRuptura(anamneseEfs.getFormaRuptura());
					bolsa.setDthrRompimento(anamneseEfs.getDataHoraRompimento());
					bolsa.setIndDataHoraIgnorada(anamneseEfs.getIndDthrIgnorada());
					bolsa.setLiquidoAmniotico(anamneseEfs.getLiquidoAmniotico());
					bolsa.setIndOdorFetido(anamneseEfs.getIndOdorFetido());
					bolsa.setIndAmnioscopia(false);
					bolsa.setSerMatricula(usuario.getMatricula());
					bolsa.setSerVinCodigo(usuario.getVinculo());
					this.emergenciaFacade.validarDadosConsultaCO(bolsa);
				}

			} else {
				// Executa RN03 da estória #25663
				McoGestacoesId id = new McoGestacoesId();
				id.setPacCodigo(anamneseEfs.getId().getGsoPacCodigo());
				id.setSeqp(anamneseEfs.getId().getGsoSeqp());
				
				McoBolsaRotas bolsaRota = new McoBolsaRotas();
				bolsaRota.setId(id);
				bolsaRota.setCriadoEm(new Date());
				bolsaRota.setDominioFormaRuptura(anamneseEfs.getFormaRuptura());
				bolsaRota.setDthrRompimento(anamneseEfs.getDataHoraRompimento());
				bolsaRota.setIndDataHoraIgnorada(anamneseEfs.getIndDthrIgnorada());
				bolsaRota.setLiquidoAmniotico(anamneseEfs.getLiquidoAmniotico());
				bolsaRota.setIndOdorFetido(anamneseEfs.getIndOdorFetido());
				bolsaRota.setIndAmnioscopia(Boolean.FALSE);
				bolsaRota.setSerMatricula(usuario.getMatricula());
				bolsaRota.setSerVinCodigo(usuario.getVinculo());
				
				this.emergenciaFacade.validarDadosBolsaRotas(bolsaRota);
			}
		}
		
	}

	private void inserirJournalAnamneseEfs(McoAnamneseEfs anamneseEfs) {
		
		McoAnamneseEfsJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, McoAnamneseEfsJn.class, usuario.getLogin());
		
		jn.setAcv(anamneseEfs.getAcv());
		jn.setAlturaUterina(anamneseEfs.getAlturaUterina());
		jn.setApagamento(anamneseEfs.getApagamento());
		jn.setApresentacao(anamneseEfs.getApresentacao());
		jn.setAr(anamneseEfs.getAr());
		jn.setBatimentoCardiacoFetal(anamneseEfs.getBatimentoCardiacoFetal());
		jn.setBatimentoCardiacoFetal2(anamneseEfs.getBatimentoCardiacoFetal2());
		jn.setBatimentoCardiacoFetal3(anamneseEfs.getBatimentoCardiacoFetal3());
		jn.setBatimentoCardiacoFetal4(anamneseEfs.getBatimentoCardiacoFetal4());
		jn.setBatimentoCardiacoFetal5(anamneseEfs.getBatimentoCardiacoFetal5());
		jn.setBatimentoCardiacoFetal6(anamneseEfs.getBatimentoCardiacoFetal6());
		jn.setCidSeq(anamneseEfs.getCidSeq());
		jn.setConNumero(anamneseEfs.getId().getConNumero());
		jn.setCriadoEm(anamneseEfs.getCriadoEm());
		jn.setDigSeq(anamneseEfs.getDigSeq());
		jn.setDilatacao(anamneseEfs.getDilatacao());
		jn.setDinamicaUterina(anamneseEfs.getDinamicaUterina());
		jn.setDthrConsulta(anamneseEfs.getDthrConsulta());
		jn.setEdema(anamneseEfs.getEdema());
		jn.setEspessuraCervice(anamneseEfs.getEspessuraCervice());
		jn.setExameEspecular(anamneseEfs.getExameEspecular());
		jn.setExFisicoGeral(anamneseEfs.getExFisicoGeral());
		jn.setFreqCardiaca(anamneseEfs.getFreqCardiaca());
		jn.setFreqRespiratoria(anamneseEfs.getFreqRespiratoria());
		jn.setGsoPacCodigo(anamneseEfs.getId().getGsoPacCodigo());
		jn.setGsoSeqp(anamneseEfs.getId().getGsoSeqp());
		jn.setIndAcelTrans(anamneseEfs.getIndAcelTrans());
		jn.setIndEspCiaticaSaliente(anamneseEfs.getIndEspCiaticaSaliente());
		jn.setIndMovFetal(anamneseEfs.getIndMovFetal());
		jn.setIndPromontorioAcessivel(anamneseEfs.getIndPromontorioAcessivel());
		jn.setIndSubPubicoMenor90(anamneseEfs.getIndSubPubicoMenor90());
		jn.setIntensidadeDinUterina(anamneseEfs.getIntensidadeDinUterina());
		jn.setMotivo(anamneseEfs.getMotivo());
		jn.setObservacao(anamneseEfs.getObservacao());
		jn.setPlanoDelee(anamneseEfs.getPlanoDelee());
		jn.setPosicaoCervice(anamneseEfs.getPosicaoCervice());
		jn.setPressaoArtDiastolica(anamneseEfs.getPressaoArtDiastolica());
		jn.setPressaoArtSistolica(anamneseEfs.getPressaoArtSistolica());
		jn.setPressaoDiastRepouso(anamneseEfs.getPressaoDiastRepouso());
		jn.setPressaoSistRepouso(anamneseEfs.getPressaoSistRepouso());
		jn.setSerMatricula(anamneseEfs.getSerMatricula());
		jn.setSerVinCodigo(anamneseEfs.getSerVinCodigo());
		jn.setSitFetal(anamneseEfs.getSitFetal());
		jn.setTemperatura(anamneseEfs.getTemperatura());
		
		this.mcoAnamneseEfsJnDAO.persistir(jn);
	}

	public boolean verificarAlteracaoTela(McoAnamneseEfs anamneseEfs, McoAnamneseEfs anamneseEfsOriginal) {
		
		return CoreUtil.modificados(anamneseEfs.getDataHoraRompimento(), anamneseEfsOriginal.getDataHoraRompimento()) || 
				CoreUtil.modificados(anamneseEfs.getDthrConsulta(), anamneseEfsOriginal.getDthrConsulta()) ||
				CoreUtil.modificados(anamneseEfs.getMotivo(), anamneseEfsOriginal.getMotivo()) ||
				CoreUtil.modificados(anamneseEfs.getPressaoArtSistolica(), anamneseEfsOriginal.getPressaoArtSistolica()) ||
				CoreUtil.modificados(anamneseEfs.getPressaoArtDiastolica(), anamneseEfsOriginal.getPressaoArtDiastolica()) ||
				CoreUtil.modificados(anamneseEfs.getFreqCardiaca(), anamneseEfsOriginal.getFreqCardiaca()) ||
				CoreUtil.modificados(anamneseEfs.getFreqRespiratoria(), anamneseEfsOriginal.getFreqRespiratoria()) ||
				CoreUtil.modificados(anamneseEfs.getTemperatura(), anamneseEfsOriginal.getTemperatura()) ||
				CoreUtil.modificados(anamneseEfs.getEdema(), anamneseEfsOriginal.getEdema()) ||
				CoreUtil.modificados(anamneseEfs.getAlturaUterina(), anamneseEfsOriginal.getAlturaUterina()) ||
				CoreUtil.modificados(anamneseEfs.getDinamicaUterina(), anamneseEfsOriginal.getDinamicaUterina()) ||
				CoreUtil.modificados(anamneseEfs.getIntensidadeDinUterina(), anamneseEfsOriginal.getIntensidadeDinUterina()) ||
				CoreUtil.modificados(anamneseEfs.getIndAcelTrans(), anamneseEfsOriginal.getIndAcelTrans()) ||
				CoreUtil.modificados(anamneseEfs.getSitFetal(), anamneseEfsOriginal.getSitFetal()) ||
				CoreUtil.modificados(anamneseEfs.getExameEspecular(), anamneseEfsOriginal.getExameEspecular()) ||
				CoreUtil.modificados(anamneseEfs.getPosicaoCervice(), anamneseEfsOriginal.getPosicaoCervice()) ||
				CoreUtil.modificados(anamneseEfs.getEspessuraCervice(), anamneseEfsOriginal.getEspessuraCervice()) ||
				CoreUtil.modificados(anamneseEfs.getApagamento(), anamneseEfsOriginal.getApagamento()) ||
				CoreUtil.modificados(anamneseEfs.getDilatacao(), anamneseEfsOriginal.getDilatacao()) ||
				CoreUtil.modificados(anamneseEfs.getApresentacao(), anamneseEfsOriginal.getApresentacao()) ||
				CoreUtil.modificados(anamneseEfs.getPlanoDelee(), anamneseEfsOriginal.getPlanoDelee()) ||
				CoreUtil.modificados(anamneseEfs.getIndPromontorioAcessivel(), anamneseEfsOriginal.getIndPromontorioAcessivel()) ||
				CoreUtil.modificados(anamneseEfs.getIndEspCiaticaSaliente(), anamneseEfsOriginal.getIndEspCiaticaSaliente()) ||
				CoreUtil.modificados(anamneseEfs.getIndSubPubicoMenor90(), anamneseEfsOriginal.getIndSubPubicoMenor90()) ||
				CoreUtil.modificados(anamneseEfs.getAcv(), anamneseEfsOriginal.getAcv()) ||
				CoreUtil.modificados(anamneseEfs.getAr(), anamneseEfsOriginal.getAr()) ||
				CoreUtil.modificados(anamneseEfs.getObservacao(), anamneseEfsOriginal.getObservacao()) ||
				CoreUtil.modificados(anamneseEfs.getDigSeq(), anamneseEfsOriginal.getDigSeq()) ||
				CoreUtil.modificados(anamneseEfs.getCidSeq(), anamneseEfsOriginal.getCidSeq()) ||
				CoreUtil.modificados(anamneseEfs.getIndMovFetal(), anamneseEfsOriginal.getIndMovFetal()) ||	
				CoreUtil.modificados(anamneseEfs.getSerMatricula(), anamneseEfsOriginal.getSerMatricula()) ||
				CoreUtil.modificados(anamneseEfs.getSerVinCodigo(), anamneseEfsOriginal.getSerVinCodigo()) ||
				CoreUtil.modificados(anamneseEfs.getExFisicoGeral(), anamneseEfsOriginal.getExFisicoGeral()) ||
				CoreUtil.modificados(anamneseEfs.getPressaoSistRepouso(), anamneseEfsOriginal.getPressaoSistRepouso()) ||
				CoreUtil.modificados(anamneseEfs.getPressaoDiastRepouso(), anamneseEfsOriginal.getPressaoDiastRepouso()) ||
				//CoreUtil.modificados(anamneseEfs.getPaciente(), anamneseEfsOriginal.getPaciente()) ||
				CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal(), anamneseEfsOriginal.getBatimentoCardiacoFetal()) ||
				CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal2(), anamneseEfsOriginal.getBatimentoCardiacoFetal2()) ||
				CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal3(), anamneseEfsOriginal.getBatimentoCardiacoFetal3()) ||
				CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal4(), anamneseEfsOriginal.getBatimentoCardiacoFetal4()) ||
				CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal5(), anamneseEfsOriginal.getBatimentoCardiacoFetal5()) ||
				CoreUtil.modificados(anamneseEfs.getBatimentoCardiacoFetal6(), anamneseEfsOriginal.getBatimentoCardiacoFetal6()) ||
				CoreUtil.modificados(anamneseEfs.getFormaRuptura(), anamneseEfsOriginal.getFormaRuptura()) ||
				CoreUtil.modificados(anamneseEfs.getDataHoraRompimento(), anamneseEfsOriginal.getDataHoraRompimento()) ||
				CoreUtil.modificados(anamneseEfs.getIndDthrIgnorada(), anamneseEfsOriginal.getIndDthrIgnorada()) ||
				CoreUtil.modificados(anamneseEfs.getLiquidoAmniotico(), anamneseEfsOriginal.getLiquidoAmniotico()) ||
				CoreUtil.modificados(anamneseEfs.getIndOdorFetido(), anamneseEfsOriginal.getIndOdorFetido());
	}

	public List<CidVO> obterCidPorSeq(List<Integer> cids) throws ServiceException {
		return this.configuracaoService.pesquisarCidAtivosPorSeq(cids);
	}

	/**
	 * Busca um parâmetro pelo nome
	 * 
	 * @param nome
	 * @param coluna
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Object buscarParametroPorNome(EmergenciaParametrosEnum nome, EmergenciaParametrosColunas coluna) throws ApplicationBusinessException {
		Object retorno = null;
		try {
			retorno = parametroFacade.obterAghParametroPorNome(nome.toString(), coluna.toString());
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_ERRO_PARAMETRO, nome);
		}
		if (retorno == null) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_ERRO_PARAMETRO, nome);
		}
		return retorno;
	}

	/**
	 * RN05 de 26349
	 * 
	 * @param desbloqueioConsultaCOVO
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @throws ApplicationBusinessException
	 */
	public void ajustarDesbloqueioConsultaCO(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero)
			throws ApplicationBusinessException {
		// 1. Obter o parâmetro de sistema P_DESBLOQUEIA_EXCLUI (serviço #34780)
		// 2. Se o parâmetro não for encontrado ou o campo VLR_NUMERICO for nulo disparar a mensagem “MENSAGEM_ERRO_PARAMETRO” passando o
		// nome do parâmetro e cancelar o processamento.
		Integer pDesbloqueiaExclui = ((BigDecimal) this.buscarParametroPorNome(EmergenciaParametrosEnum.P_DESBLOQUEIA_EXCLUI,
				EmergenciaParametrosColunas.VLR_NUMERICO)).intValue();

		// 3. Obter o parâmetro de sistema P_DIAS_NOTAS_ADICIONAIS (serviço #34780)
		// 4. Se o parâmetro não for encontrado ou o campo VLR_NUMERICO for nulo disparar a mensagem “MENSAGEM_ERRO_PARAMETRO” passando o
		// nome do parâmetro e cancelar o processamento.
		Integer pDiasNotasAdicionais = ((BigDecimal) this.buscarParametroPorNome(EmergenciaParametrosEnum.P_DIAS_NOTAS_ADICIONAIS,
				EmergenciaParametrosColunas.VLR_NUMERICO)).intValue();

		// 5. Executar consulta C4 passando o número da consulta selecionado no item 2 do quadro descritivo
		DominioEventoLogImpressao[] eventos = new DominioEventoLogImpressao[] { DominioEventoLogImpressao.MCOR_CONSULTA_OBS,
				DominioEventoLogImpressao.MCOR_ADMISSAO_OBS };
		McoLogImpressoes ultimaImpressao = mcoLogImpressoesDAO.buscarUltimaImpressao(gsoPacCodigo, gsoSeqp, conNumero, eventos);
		if (ultimaImpressao != null) {

			Date sysdate = new Date();
			Integer dias = DateUtil.obterQtdDiasEntreDuasDatas(ultimaImpressao.getCriadoEm(), sysdate);
			Integer horas = dias * NUMERO_HORAS_DIA;

			// Se o valor do campo HORAS for maior que o valor obtido do parâmetro P_DESBLOQUEIA_EXCLUI
			if (horas.intValue() > pDesbloqueiaExclui.intValue()) {

				// 1. Se o valor do campo DIAS for maior que o valor obtido do parâmetro P_DIAS_NOTAS_ADICIONAIS setar o label do botão
				// Desbloqueio da aba Consulta CO para “Desbloqueio” (caso seja outro label), desabilitar o botão Desbloqueio e desabilitar
				// as ações de exclusão nos grids da aba.
				if (dias.intValue() > pDiasNotasAdicionais.intValue()) {
					desbloqueioConsultaCOVO.setNotasAdicionais(false);
					desbloqueioConsultaCOVO.setHabilitaDesbloqueio(false);
					desbloqueioConsultaCOVO.setHabilitaExclusao(false);
				} else {
					// 2. Caso contrário, alterar o label do botão Desbloqueio/Notas Adicionais para “Notas adicionais”, habilitar o botão e
					// desabilitar as ações de exclusão nos grids da aba.
					desbloqueioConsultaCOVO.setNotasAdicionais(true);
					desbloqueioConsultaCOVO.setHabilitaDesbloqueio(true);
					desbloqueioConsultaCOVO.setHabilitaExclusao(false);
				}
			} else {
				// Caso contrário, alterar o label do botão Desbloqueio/Notas Adicionais para “Desbloqueio”, habilitar o botão Desbloqueio e
				// habilitar as ações de exclusão nos grids da aba consulta CO
				desbloqueioConsultaCOVO.setNotasAdicionais(false);
				desbloqueioConsultaCOVO.setHabilitaDesbloqueio(true);
				desbloqueioConsultaCOVO.setHabilitaExclusao(true);
			}
		} else {
			// Caso a consulta C4 não retornar resultados alterar o label do botão Desbloqueio/Notas Adicionais para “Desbloqueio”,
			// habilitar o botão Desbloqueio e habilitar as ações de exclusão nos grids da aba Consulta CO
			desbloqueioConsultaCOVO.setNotasAdicionais(false);
			desbloqueioConsultaCOVO.setHabilitaDesbloqueio(true);
			desbloqueioConsultaCOVO.setHabilitaExclusao(true);
		}
	}

	/**
	 * RN06 de 26349
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public void ajustarDesbloqueioConsultaCOSelecionada(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException {
		// Executar consulta C5
		DominioEventoLogImpressao[] eventos = new DominioEventoLogImpressao[] { DominioEventoLogImpressao.DESBLOQUEIO_PRIM,
				DominioEventoLogImpressao.DESBLOQUEIO_CONSULTA, DominioEventoLogImpressao.MCOR_ADMISSAO_OBS,
				DominioEventoLogImpressao.MCOR_CONSULTA_OBS };
		String evento = mcoLogImpressoesDAO.buscarUltimoEvento(gsoPacCodigo, gsoSeqp, conNumero, eventos);

		// Se não retornar resultados OU Se retornar resultados e o retorno for “DESBLOQUEIO CONSULTA”
		if (evento == null || DominioEventoLogImpressao.DESBLOQUEIO_CONSULTA.getCodigo().equals(evento)) {
			// 1. Habilitar os campos e permitir inserção, atualização e deleção na aba Gestação atual. 
			desbloqueioConsultaCOVO.setPermiteAlterarAbaGestAtual(true);
			// Desabilitar o botão Bloqueio.
			desbloqueioConsultaCOVO.setHabilitaBloqueio(false);
			// 2. Habilitar os campos e permitir inserção, atualização e deleção na aba Consulta CO. 
			desbloqueioConsultaCOVO.setPermiteAlterarAbaConsCO(true);
			// Desabilitar o botão Desbloqueio.
			desbloqueioConsultaCOVO.setHabilitaDesbloqueio(false);			
		} 
		
		if (evento == null || !DominioEventoLogImpressao.DESBLOQUEIO_CONSULTA.getCodigo().equals(evento)) {
			// 1. Desabilitar os campos e não permitir inserção, atualização (desabilitar botão Gravar) e deleção na aba Consulta CO.
			desbloqueioConsultaCOVO.setPermiteAlterarAbaConsCO(false);
			// 2. Desabilitar os campos e não permitir inserção, atualização e deleção (desabilitar botão Gravar) na aba Gestação Atual.
			desbloqueioConsultaCOVO.setPermiteAlterarAbaGestAtual(false);
		}
		
		// Executar RN08
		this.ajustarFinalizarInternarConsultaCOVO(desbloqueioConsultaCOVO, gsoPacCodigo, gsoSeqp, conNumero);
	}

	/**
	 * RN08 de 26349
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void ajustarFinalizarInternarConsultaCOVO(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero)
			throws ApplicationBusinessException {
		// Executar consulta C6
		DominioEventoLogImpressao[] eventos = new DominioEventoLogImpressao[] { DominioEventoLogImpressao.MCOR_CONSULTA_OBS };
		boolean existeImpressao = mcoLogImpressoesDAO.verificarExisteImpressao(gsoPacCodigo, gsoSeqp, conNumero, eventos);
		if (existeImpressao) {
			// 2. Se C6 retornar resultados
			// 3. Desabilitar os botões Finalizar consulta e Internar
			desbloqueioConsultaCOVO.setHabilitaFinalizarConsulta(false);
			desbloqueioConsultaCOVO.setHabilitaInternar(false);
		} else {
			// 4. Se C6 não retornar resultados executar a consulta C7
			eventos = new DominioEventoLogImpressao[] { DominioEventoLogImpressao.MCOR_ADMISSAO_OBS };
			existeImpressao = mcoLogImpressoesDAO.verificarExisteImpressao(gsoPacCodigo, gsoSeqp, conNumero, eventos);
			if (existeImpressao) {
				// 5. Se C7 retornar resultados desabilitar os botões Finalizar Consulta e Internar
				desbloqueioConsultaCOVO.setHabilitaFinalizarConsulta(false);
				desbloqueioConsultaCOVO.setHabilitaInternar(false);
			} else {
				// a. Verificar se o paciente está internado através da RN09.
				boolean pacienteInternado = this.verificarPacienteInternado(conNumero);
				if (pacienteInternado) {
					// b. Se pac estiver internado habilitar o botão Internar e desabilitar o botão Finalizar Consulta da aba Consulta CO.
					desbloqueioConsultaCOVO.setHabilitaFinalizarConsulta(false);
					desbloqueioConsultaCOVO.setHabilitaInternar(true);
				} else {
					// c. Se pac não estiver internado habilitar o botão Finalizar Consulta e desabilitar o botão Internar da aba Consulta
					// CO.
					desbloqueioConsultaCOVO.setHabilitaFinalizarConsulta(true);
					desbloqueioConsultaCOVO.setHabilitaInternar(false);
				}
			}
		}
	}

	/**
	 * RN09 de 26349
	 * 
	 * Regras para verificar se o paciente está “Internado”
	 * 
	 * @param conNumero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean verificarPacienteInternado(Integer conNumero) throws ApplicationBusinessException {
		// 1. Obter o parâmetro de sistema P_SIT_INTERNACAO através do serviço #34780
		// 2. Se o parâmetro não for encontrado ou o valor do campo VLR_NUMERICO for nulo ou o serviço estiver indisponível disparar a
		// mensagem “MENSAGEM_ERRO_PARAMETRO” passando o nome do parâmetro e cancelar o processamento.
		this.buscarParametroPorNome(EmergenciaParametrosEnum.P_SIT_INTERNACAO, EmergenciaParametrosColunas.VLR_NUMERICO);
		// 3. Executar consulta C8 para obter o objeto MAM_TRIAGENS associado a consulta
		MamTrgEncInterno mamTrgEncInterno = mamTrgEncInternoDAO.buscarTriagemPorConsulta(conNumero);
		// 4. Se o C8.IND_PAC_EMERGENCIA for igual “S” retonar que o paciente está internado, Senão retornar que o paciente não está
		// internado.
		if (mamTrgEncInterno != null && mamTrgEncInterno.getTriagem() != null
				&& Boolean.TRUE.equals(mamTrgEncInterno.getTriagem().getIndPacEmergencia())) {
			return true;
		}
		return false;
	}

	/**
	 * RN01 de 26349
	 * 
	 * @param finalizarInternarConsultaCOVO
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @throws ApplicationBusinessException
	 */
	public void desbloquearConsultaCO(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException {
		// 1. Verificar se o paciente está internado através da RN09
		boolean pacienteInternado = this.verificarPacienteInternado(conNumero);
		if (pacienteInternado) {
			// 2. Se o paciente estiver internado habilitar o botão Internar e deixar desabilitado o botão Finalizar
			desbloqueioConsultaCOVO.setHabilitaFinalizarConsulta(false);
			desbloqueioConsultaCOVO.setHabilitaInternar(true);
		} else {
			// 3. Se o paciente não estiver internado habilitar o botão Finalizar Consulta e deixar desabilitado o botão Internar da aba Consulta CO.
			desbloqueioConsultaCOVO.setHabilitaFinalizarConsulta(true);
			desbloqueioConsultaCOVO.setHabilitaInternar(false);
		}
		
		// Regra abaixo estava como primeira ação da RN02, porém é melhor ficar aqui.
		// 1. Executar consulta C3 para obter o número do atendimento associado à consulta atual marcada no item 2
		Integer seqAtendimento = this.buscarSeqAtendimento(conNumero);

		// 4. Executar RN02 passando como parâmetro “CEO” (DominioTipoDocumento.java)
		this.atualizaSituacaoDocumentosCertificados(seqAtendimento, DominioTipoDocumento.CEO);

		// 5. Executar RN02 passando como parâmetro “ACO” (DominioTipoDocumento.java)
		this.atualizaSituacaoDocumentosCertificados(seqAtendimento, DominioTipoDocumento.ACO);

		// 6. Executar RN03
		this.desbloquearOperacoesAbasEnvolvidas(desbloqueioConsultaCOVO);

		// 7. Executar RN04
		
	}
	
	/**
	 * RN04 de 26349
	 * 
	 * Insere log de impressão
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 */
	public void inserirLogImpressao(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		// 1. Obter SEQP do log de impressão através da consulta C2.
		McoLogImpressoes mcoLogImpressoes = new McoLogImpressoes();
		McoLogImpressoesId id = new McoLogImpressoesId();
		id.setGsoPacCodigo(gsoPacCodigo);
		id.setGsoSeqp(gsoSeqp);
		mcoLogImpressoes.setId(id);
		
		// 2. Executar RN07
		consultaCORN.preInserir(mcoLogImpressoes);
		
		// 3. Realizar inserção I1.
		// Código da consulta selecionado no datagrid
		mcoLogImpressoes.setConsulta(consultasDAO.obterPorChavePrimaria(conNumero));
		// SEQP da tabela MCO_RECEM_NASCIDOS, aba Recém Nascido (caso já tenha dados gravados, senão grava null
		mcoLogImpressoes.setRnaSeqp(null);
		// DESBLOQUEIO CONSULTA
		mcoLogImpressoes.setEvento(DominioEventoLogImpressao.DESBLOQUEIO_CONSULTA.toString());
		
		mcoLogImpressoesDAO.persistir(mcoLogImpressoes);
	}
	
	
	
	/**
	 * RN03 de 26349
	 * 
	 * Desbloqueio de operações nas abas envolvidas.
	 * 
	 * @param desbloqueioConsultaCOVO
	 */
	public void desbloquearOperacoesAbasEnvolvidas(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO) {
		// 1. Habilitar os campos e permitir inserção, atualização e deleção na aba Gestação atual. Desabilitar o botão Bloqueio.
		desbloqueioConsultaCOVO.setPermiteAlterarAbaGestAtual(true);
		desbloqueioConsultaCOVO.setHabilitaBloqueio(false);
		// 2. Habilitar os campos e permitir inserção, atualização e deleção na aba Consulta CO. Desabilitar o botão Desbloqueio.
		desbloqueioConsultaCOVO.setPermiteAlterarAbaConsCO(true);
		desbloqueioConsultaCOVO.setHabilitaDesbloqueio(false);
	}
	
	/**
	 * RN02 de 26349
	 *  
	 * Atualiza a situação dos documentos certificados
	 * 
	 * @param conNumero
	 * @throws ApplicationBusinessException
	 */
	public void atualizaSituacaoDocumentosCertificados(Integer seqAtendimento, DominioTipoDocumento tipo) throws ApplicationBusinessException{
		// 2. Executar consulta C1 passando como parâmetro o SEQ do atendimento e o tipo do documento recebido por parâmetro
		List<DadosDocumentoVO> listDadosDocumento = this.buscarDadosDocumento(seqAtendimento, tipo.toString());
		
		// 3. Se a consulta C1 retornar resultados executar, para cada resultado obtido, o update U1
		if(listDadosDocumento != null && !listDadosDocumento.isEmpty()){
			List<Integer> listVersDocs = new ArrayList<Integer>();
			for (DadosDocumentoVO dadosDocumentoVO : listDadosDocumento) {
				listVersDocs.add(dadosDocumentoVO.getVerSeq());
			}
			this.inativarVersaoDocumentos(listVersDocs);			
		}
	}

	/**
	 * Chama serviço para buscar seq do atendimento pelo número da consulta.
	 * 
	 * @param conNumero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Integer buscarSeqAtendimento(Integer conNumero) throws ApplicationBusinessException {
		try {
			Integer seqAtendimento = configuracaoService.buscarSeqAtendimentoPorConNumero(conNumero);
			return seqAtendimento;
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	/**
	 * Chama serviço para buscar seq do atendimento pelo número da consulta.
	 * 
	 * @param conNumero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<DadosDocumentoVO> buscarDadosDocumento(Integer atdSeq, String codigotipo) throws ApplicationBusinessException {
		try {
			List<DadosDocumentoVO> listDadosDocumentoVO = certificacaoDigitalService.obterAghVersaoDocumentoPorAtendimentoTipoDocumento(atdSeq, codigotipo);
			return listDadosDocumentoVO;
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	/**
	 * Chama serviço para inativar versões documento
	 * 
	 * @param listAtdSeq
	 * @throws ApplicationBusinessException
	 */
	private void inativarVersaoDocumentos(List<Integer> listVersDocs) throws ApplicationBusinessException {
		try {
			certificacaoDigitalService.inativarVersaoDocumentos(listVersDocs);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	public void validarDadosGestacional(Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		Calendar data = Calendar.getInstance();
		DateUtil.zeraHorario(data);
		
		Object paramentro;
		try {
			paramentro = parametroFacade.obterAghParametroPorNome("P_AGHU_IDADE_GEST_PRIM_ECO", "vlrNumerico");
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		
		Integer valorParamentro = obterParamentroGestacional(paramentro);
		
		McoGestacoes gestacao = this.buscarGestacao(pacCodigo, seqp);
					
		if(gestacao != null) {
			if((gestacao.getDtInformadaIg() != null && !DateUtil.isDatasIguais(data.getTime(), gestacao.getDtInformadaIg())) || (gestacao.getDtInformadaIg() == null && gestacao.getIgPrimEco()!=null && gestacao.getIgPrimEco().intValue() > valorParamentro)){
				throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MSG_EXCECAO_MCO_00620);	
			}
		} else {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MSG_EXCECAO_MCO_00620);			
		}
	}
	
	private Integer obterParamentroGestacional(Object paramentro) {
		Integer valorParamentro;
		if(paramentro == null){
			valorParamentro = VALOR_DEFAULT_P_AGHU_IDADE_GEST_PRIM_ECO;
		} else {
			valorParamentro = Integer.valueOf(paramentro.toString());
		}
		return valorParamentro;
	}
	
	
	private McoGestacoes buscarGestacao(Integer pacCodigo, Short seqp){
		return gestacoesDAO.pesquisarMcoGestacaoPorId(pacCodigo, seqp);
	}

	public void atualizarAtendimentoGestante(Integer numeroConsulta, Integer pacCodigo, Short seqp, String nomeMicroComputador,
			String obterLoginUsuarioLogado) throws ApplicationBusinessException {
		
		try {
			Integer atdSeq = this.emergenciaFacade.obterSeqAtendimentoPorConNumero(numeroConsulta);
			Servidor servidor = this.registroColaboradorService.buscarServidor(super.obterLoginUsuarioLogado());
			this.pacienteService.atualizarAtendimentoGestante(numeroConsulta, pacCodigo, seqp, nomeMicroComputador, servidor.getMatricula(), servidor.getVinculo(), atdSeq);
		} catch (ServiceBusinessException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.ERRO_SERVICO_MESSAGEM, e.getMessage());		
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCOONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		
	}
}
