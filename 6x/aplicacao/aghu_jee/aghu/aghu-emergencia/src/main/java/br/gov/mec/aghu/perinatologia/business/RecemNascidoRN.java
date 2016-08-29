package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.PacAtendimentoVO;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.exames.vo.SolicitacaoExamesVO;
import br.gov.mec.aghu.model.McoApgarsId;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.perinatologia.dao.McoApgarsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoReanimacaoRnsDAO;
import br.gov.mec.aghu.perinatologia.vo.RecemNascidoVO;
import br.gov.mec.aghu.prescricaomedica.service.IPrescricaoMedicaService;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class RecemNascidoRN extends BaseBusiness {
	
	private static final long serialVersionUID = -7788997730226180998L;

	private static final Log LOG = LogFactory.getLog(RecemNascidoRN.class);
	private static final String ORIGEM_ATENDIMENTO = "N";
	private static final String TIPO_PARAMETRO_VLR_TEXTO = "vlrTexto";
	private static final String TIPO_PARAMETRO_VLR_NUMERICO = "vlrNumerico";
	
	@Inject
	private McoApgarsDAO mcoApgarsDAO;
	
	private McoReanimacaoRnsDAO mcoReanimacaoRnsDAO;
	
	@Inject
	private IRegistroColaboradorService registroColaboradorService;
	
	@Inject
	private IPrescricaoMedicaService prescricaoMedicaService;
	
	@Inject
	private IConfiguracaoService configuracaoService;
	
	@Inject
	private IPacienteService pacienteService;
	
	@Inject
	private IExamesService examesService;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	private enum RecemNascidoRNExceptionCode implements BusinessExceptionCode {
		MSG_REG_REC_NASC_SERVIDOR, MSG_REG_REC_NASC_PRESCRICAO_MEDICA, MSG_REG_REC_NASC_ATEND, MSG_REG_REC_NASC_PAC_PROTUARIO,
		MSG_REG_REC_NASC_SOLICITACAO_EXAMES, MSG_REG_REC_NASC_EXISTE_MEDICAMENTO,
		MENSAGEM_ERRO_PARAMETRO,
		MCO_00664, MCO_00574, MCO_00573;
	}

	/**
	 * Verificar se existe registro no grid de Recém Nascido
	 * @param vo
	 * @return
	 */
	public boolean verificarExistRegistroRecemNascido(RecemNascidoVO vo) {
		return vo.getGsoPacCodigoPK() != null && vo.getGsoSeqpPK() != null && vo.getSeqpPK() != null;
	}
	
	/**
	 * RN03
	 * @param vo
	 * @throws ApplicationBusinessException 
	 */
	public void excluirRecemNascido(RecemNascidoVO vo, List<RecemNascidoVO> listRecemNascidoVOs) throws ApplicationBusinessException {
		if(verificarExistRegistroRecemNascido(vo)){
			apgarFoiIncluidoPeloMesmoProfissional(vo);
			verificaRecemNascidoPossuiPrescricaoMedica(vo);
			verificarSolictacaoExame(vo);
			verificarExisteMedicamentoParaRecemNascido(vo.getPacCodigo(), vo.getSeqp(), vo.getSeqpPK());
		}
		
		listRecemNascidoVOs.remove(vo);
		// Excluir do banco o registro através da estória #41564
	}
	
	/**
	 * @param pacCodigoMae - Código do paciente da mãe do Recém Nascido selecionado na aba Gestação Atual
	 * @param seqpGestacao - SEQP da gestação selecionada na aba Gestação Atual
	 * @param seqpRecemNascido - Sequencial Do Recém Nascido No Grid Recém Nascido
	 * @throws ApplicationBusinessException
	 */
	private void verificarExisteMedicamentoParaRecemNascido(Integer pacCodigoMae, Short seqpGestacao, Byte seqpRecemNascido)throws ApplicationBusinessException { 
		if(mcoReanimacaoRnsDAO.quantidadeReanimacoesRns(pacCodigoMae, seqpGestacao, seqpRecemNascido) > 0){
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MSG_REG_REC_NASC_EXISTE_MEDICAMENTO);
		}
	}
	
	/**
	 * Verificar se o apgar foi incluído pelo mesmo profissional (matrícula e
	 * vínculo), se for de profissional diferente do usuário atual apresentar a
	 * mensagem “MCO-00664” e cancelar o processamento.
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	private void apgarFoiIncluidoPeloMesmoProfissional(RecemNascidoVO vo) throws ApplicationBusinessException {
		Servidor servidor = obterServidor();
		McoApgarsId id = new McoApgarsId(vo.getGsoPacCodigoPK(), vo.getGsoSeqpPK(), vo.getSeqpPK(), servidor.getMatricula(), servidor.getVinculo());
		
		if(mcoApgarsDAO.obterPorChavePrimaria(id) == null){
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MCO_00664);
		}
	}

	private Servidor obterServidor() throws ApplicationBusinessException {
		try {
			return registroColaboradorService.buscarServidor(super.obterLoginUsuarioLogado());
		} catch (ServiceBusinessException e) {
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MSG_REG_REC_NASC_SERVIDOR, e.getMessage());
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MSG_REG_REC_NASC_SERVIDOR, e.getMessage());
		}
	}
	
	/**
	 * Verificar se o Recém Nascido já possui prescrição médica, caso seja
	 * encontrado registro apresentar a mensagem “MCO-00574” e cancelar o processamento
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	private void verificaRecemNascidoPossuiPrescricaoMedica(RecemNascidoVO vo) throws ApplicationBusinessException{
		if(vo.getProntuario()!= null ){
			if(pesquisaPrescricoesMedicasPorAtendimento(obterAtendimento(vo))){
				throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MCO_00574);
			}
		}
	}
	
	private Integer obterAtendimento(RecemNascidoVO vo) throws ApplicationBusinessException {
		try {
			PacAtendimentoVO pacAtendimentoVO = configuracaoService.obterAtendimentoPorPacienteDataInicioOrigem(
							obterCodigoPacientePorProntuario(vo.getProntuario()), vo.getDataHora(), ORIGEM_ATENDIMENTO);
			
			return pacAtendimentoVO.getAtdSeq();
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MSG_REG_REC_NASC_SERVIDOR);
		}
	}
	
	private boolean pesquisaPrescricoesMedicasPorAtendimento(Integer atdSeq) throws ApplicationBusinessException {
		try {
			return !prescricaoMedicaService.obterPrescricaoTecnicaPorAtendimentoOrderCriadoEm(atdSeq).isEmpty();
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MSG_REG_REC_NASC_PRESCRICAO_MEDICA);
		}
	}
	
	private Integer obterCodigoPacientePorProntuario(Integer prontuario) throws  ApplicationBusinessException {		
		try {
			PacienteFiltro filtro = new PacienteFiltro();
			filtro.setProntuario(prontuario);
			Paciente paciente = pacienteService.obterPacientePorCodigoOuProntuario(filtro);
			return paciente.getCodigo();
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MSG_REG_REC_NASC_PAC_PROTUARIO);
		}
	}
	
	/**
	 * verificar se existe alguma Solicitação de exame para aquele atendimento,
	 * caso seja encontrado registro apresentar a mensagem “MCO-00573” e
	 * cancelar o processamento.
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	private void verificarSolictacaoExame(RecemNascidoVO vo) throws ApplicationBusinessException {
		try {
			String[] situacoes = { 
					obterParametroSituacaoAgendamento(),
					obterParametroSituacaoLiberado(),
					obterParametroSituacaoExecutando(),
					obterParametroSituacaoAreaExecutando(),
					obterParametroSituacaoExecutado() 
				};

			for (String param : situacoes) {
				validarParametro(param);
			}
			
			verificarSolicitacaoExame(obterAtendimento(vo), situacoes);

		} catch (BaseException e) {
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MCO_00573);
		}
	}
	
	/**
	 * Se o parâmetro não for encontrado ou o valor do campo VLR_TEXTO for nulo
	 * ou o serviço estiver indisponíveldisparar a mensagem
	 * “MENSAGEM_ERRO_PARAMETRO” passando o nome do parâmetro e cancelar o
	 * processamento.
	 * 
	 * @param param
	 * @throws ApplicationBusinessException
	 */
	private void validarParametro(String param) throws ApplicationBusinessException{
		validaParametroNotNulo(param);
		if(StringUtils.isBlank(param)){
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MENSAGEM_ERRO_PARAMETRO);
		}
	}

	private void validaParametroNotNulo(Object param) throws ApplicationBusinessException {
		try {
			Validate.notNull(param);
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MENSAGEM_ERRO_PARAMETRO);
		}
	}

	private String obterParametroSituacaoAgendamento() throws ApplicationBusinessException{
		return (String) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_SITUACAO_AGENDADO.toString(), TIPO_PARAMETRO_VLR_TEXTO);
	}

	private String obterParametroSituacaoLiberado() throws ApplicationBusinessException{
		return (String) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_SITUACAO_LIBERADO.toString(), TIPO_PARAMETRO_VLR_TEXTO);
	}
	
	private String obterParametroSituacaoExecutando() throws ApplicationBusinessException{
		return (String) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_SITUACAO_EXECUTANDO.toString(), TIPO_PARAMETRO_VLR_TEXTO);
	}
	
	private String obterParametroSituacaoAreaExecutando() throws ApplicationBusinessException{
		return (String) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA.toString(), TIPO_PARAMETRO_VLR_TEXTO);
	}
	
	private String obterParametroSituacaoExecutado() throws ApplicationBusinessException{
		return (String) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_SITUACAO_EXECUTADO.toString(), TIPO_PARAMETRO_VLR_TEXTO);
	}
	
	private void verificarSolicitacaoExame(Integer atdSeq, String ... situacoes) throws ApplicationBusinessException{
		if(listarSolicitacaoExamesPorSeqAtdSituacoes(atdSeq, situacoes).size() > 0){
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MCO_00573);
		}
	}
	
	private List<SolicitacaoExamesVO> listarSolicitacaoExamesPorSeqAtdSituacoes(Integer atdSeq, String ... situacoes) throws ApplicationBusinessException {
		try {
			return examesService.listarSolicitacaoExamesPorSeqAtdSituacoes(atdSeq, situacoes);
		} catch (Exception e) {
			throw new ApplicationBusinessException(RecemNascidoRNExceptionCode.MSG_REG_REC_NASC_SOLICITACAO_EXAMES);
		}
	}
	
	public Integer obterParametroTemperaturaSalaPartoMin() throws ApplicationBusinessException {
		Integer parametro = ((BigDecimal) parametroFacade.
				obterAghParametroPorNome(EmergenciaParametrosEnum.P_TEMPERATURA_SALA_PARTO_MIN.toString(), TIPO_PARAMETRO_VLR_NUMERICO)).intValue();
		validaParametroNotNulo(parametro);
		return parametro;
	}
	
	public Integer obterParametroTemperaturaSalaPartoMax() throws ApplicationBusinessException {
		Integer parametro = ((BigDecimal) parametroFacade.
				obterAghParametroPorNome(EmergenciaParametrosEnum.P_TEMPERATURA_SALA_PARTO_MAX.toString(), TIPO_PARAMETRO_VLR_NUMERICO)).intValue();
		validaParametroNotNulo(parametro);
		return parametro;
	}

	public Integer obterParametroTempoClampeamento() throws ApplicationBusinessException {
		Integer parametro = ((BigDecimal) parametroFacade.
				obterAghParametroPorNome(EmergenciaParametrosEnum.P_TEMPO_CLAMPEAMENTO.toString(), TIPO_PARAMETRO_VLR_NUMERICO)).intValue();
		validaParametroNotNulo(parametro);
		return parametro;
	}

}
