package br.gov.mec.aghu.internacao.solicitacao.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.dao.AinSolicitacoesInternacaoDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.ProfessorVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.internacao.vo.ServidoresCRMVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.AghuTooManyMethods","PMD.AtributoEmSeamContextManager"})
@Stateless
public class SolicitacaoInternacaoON extends BaseBusiness {

	@EJB
	private SolicitacaoInternacaoRN solicitacaoInternacaoRN;
	
	private static final Log LOG = LogFactory.getLog(SolicitacaoInternacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AinSolicitacoesInternacaoDAO ainSolicitacoesInternacaoDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1572803094943348385L;

	private enum SolicitaInternacaoONExceptionCode implements
			BusinessExceptionCode {
		DATA_PREV_INTERNACAO_MENOR_EXCEPTION, SITUACAO_PENDENTE_EXCEPTION, SITUACAO_LIBERADA_CANCELADA_EXCEPTION, PROFISSIONAL_NAO_INTERNA_EXCEPTION, MENSAGEM_ERRO_PERSISTIR_SOLICITACAO_INTERNACAO, AIN_ESPECIALIDADE_OBRIGATORIA, AIN_CRM_OBRIGATORIO, CRM_ESPECIALIDADE_INVALIDO, AIN_CONVENIO_PLANO_OBRIGATORIO, ERRO_INFORMAR_LEITO_QUARTO_UNIDADE;
	}

	private Short parametroProcedimento;

	@Secure("#{s:hasPermission('solicitacaoInternacao','pesquisar')}")
	public AinSolicitacoesInternacao obterSolicitacaoInternacao(Integer seq) {
		return getAinSolicitacoesInternacaoDAO().obterPorChavePrimaria(seq);		
	}
	
	public AinSolicitacoesInternacao obterPrimeiraSolicitacaoPendentePorPaciente(Integer codigoPaciente) throws ApplicationBusinessException {
		return getAinSolicitacoesInternacaoDAO().obterPrimeiraSolicitacaoPendentePorPaciente(codigoPaciente);	
	}
	
	@Secure("#{s:hasPermission('solicitacaoInternacao','pesquisar')}")
	public List<EspCrmVO> pesquisarEspCrmVO(Object nomeMedico, AghEspecialidades especialidade) throws ApplicationBusinessException {
		List<EspCrmVO> listaEspCrmVO = this.getSolicitacaoInternacaoRN().pesquisarEspCrmVO(nomeMedico, especialidade);
		return listaEspCrmVO;
	}

	public EspCrmVO obterEspCrmVOComAmbulatorio(Object nomeMedico, AghEspecialidades especialidade, DominioSimNao ambulatorio,
			RapServidores servidor) throws ApplicationBusinessException {
		EspCrmVO espCrmVo = this.getSolicitacaoInternacaoRN().obterEspCrmVO(nomeMedico, especialidade, ambulatorio, servidor);
		return espCrmVo;
	}		
	
	@Secure("#{s:hasPermission('procedimentoHospitalar','pesquisar')}")
	public List<FatVlrItemProcedHospComps> pesquisarFatVlrItemProcedHospComps(
			Object descricao, AipPacientes paciente, Integer cidSeq)
			throws ApplicationBusinessException {
		AghParametros parametroAux = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		this.parametroProcedimento = parametroAux.getVlrNumerico().shortValue();
		List<FatVlrItemProcedHospComps> listaFatVlrItemProcedHospComps = this.getSolicitacaoInternacaoRN()
				.pesquisarProcedimentos(descricao, this.parametroProcedimento,
						paciente, cidSeq);
		return listaFatVlrItemProcedHospComps;
	}

	/**
	 * Retorna a lista de FatItensProcedHospitalar
	 * 
	 * @param descricao
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('procedimentoHospitalar','pesquisar')}")
	public List<FatItensProcedHospitalar> pesquisarFatItensProcedHospitalar(
			Object descricao, AipPacientes paciente, Integer cidSeq)
			throws ApplicationBusinessException {
		
		if (CoreUtil.isNumeroInteger(descricao)){
			descricao = truncarCodigoTabela(descricao);
		}
		
		AghParametros parametroAux = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		this.parametroProcedimento = parametroAux.getVlrNumerico().shortValue();
		List<FatVlrItemProcedHospComps> listaFatVlrItemProcedHospComps = this.getSolicitacaoInternacaoRN()
				.pesquisarProcedimentos(descricao, this.parametroProcedimento,
						paciente, cidSeq);

		List<FatItensProcedHospitalar> listaItens = new ArrayList<FatItensProcedHospitalar>();
		for (FatVlrItemProcedHospComps valor : listaFatVlrItemProcedHospComps) {
			listaItens.add(valor.getFatItensProcedHospitalar());
		}
		return listaItens;
	}
	
	/**
	 * Método que retira os zeros da frente do códigoTabela
	 * @param descricao
	 * @return
	 */
	private String truncarCodigoTabela(Object descricao){
		String strDesc = (String)descricao;
		String strDescTruncado = strDesc;
		for (int i=0; i<strDesc.length(); i++){
			if (strDesc.charAt(i) == '0'){
				if (strDesc.length() > (i+1)){
					strDescTruncado = strDesc.substring(i+1);						
				}
				else{
					strDescTruncado = strDesc;
					break;
				}
			}
			else{
				break;
			}
		}
		return strDescTruncado;
	}

	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public ProfessorVO pesquisarProfessorVO(RapServidoresId idProfessor) {
		ProfessorVO professorVO = null;

		if (idProfessor != null && idProfessor.getMatricula() != null
				&& idProfessor.getVinCodigo() != null) {
			Integer matricula = idProfessor.getMatricula();
			Short vinCodigo = idProfessor.getVinCodigo();

			String nomeUsual = this.getPesquisaInternacaoFacade().buscarNomeUsual(
					vinCodigo, matricula);
			String nroRegConselho = this.getPesquisaInternacaoFacade()
					.buscarNroRegistroConselho(vinCodigo, matricula);

			professorVO = new ProfessorVO();
			professorVO.setNomeUsual(nomeUsual);
			professorVO.setNroRegConselho(nroRegConselho);
		}

		return professorVO;
	}

	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public EspCrmVO obterCrmENomeMedicoPorServidor(RapServidores servidor, AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		// Obtém o parâmetro de qualificação de Medicina
		AghuParametrosEnum parametroEnum = AghuParametrosEnum.P_TIPO_QLF_MEDICINA;
		AghParametros aghParametro = this.getParametroFacade().buscarAghParametro(parametroEnum);
		Integer qualificacaoMedicina = aghParametro.getVlrNumerico().intValue();

		return getAghuFacade().obterCrmENomeMedicoPorServidor(servidor, especialidade, qualificacaoMedicina);
	}

	@Secure("#{s:hasPermission('solicitacaoInternacao','pesquisar')}")
	public List<AinSolicitacoesInternacao> pesquisarSolicitacaoInternacao(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao, AghClinicas clinica,
			Date criadoEm, Date dtPrevistaInternacao, AipPacientes paciente, ServidorConselhoVO crm, AghEspecialidades especialidade,
			ConvenioPlanoVO convenio) throws ApplicationBusinessException {

		return getAinSolicitacoesInternacaoDAO().pesquisarSolicitacaoInternacao(firstResult, maxResults, orderProperty, asc,
				indSolicitacaoInternacao, clinica, criadoEm, dtPrevistaInternacao, paciente, crm, especialidade, convenio);
	}

	public Long pesquisarSolicitacaoInternacaoCount(DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao,
			AghClinicas clinica, Date criadoEm, Date dtPrevistaInternacao, AipPacientes paciente, ServidorConselhoVO crm,
			AghEspecialidades especialidade, ConvenioPlanoVO convenio) throws ApplicationBusinessException {

		return getAinSolicitacoesInternacaoDAO().pesquisarSolicitacaoInternacaoCount(indSolicitacaoInternacao, clinica, criadoEm,
				dtPrevistaInternacao, paciente, crm, especialidade, convenio);
	}

	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<ServidorConselhoVO> pesquisarServidorConselhoVOPorNomeeCRM(Object strPesquisa) {
		return getRegistroColaboradorFacade().pesquisarServidorConselhoVOPorNomeeCRM(strPesquisa);
	}

	public List<ServidoresCRMVO> pesquisarServidorCRMVOPorNomeeCRM(Object strPesquisa) {
		return getRegistroColaboradorFacade().pesquisarServidorCRMVOPorNomeeCRM(strPesquisa);
	}

	public void validarCrmEspecialidade(String nomeMedico,
			AghEspecialidades especialidade) throws ApplicationBusinessException {
		List<EspCrmVO> listaMedicos = this.pesquisarEspCrmVO(nomeMedico,
				especialidade);
		if (listaMedicos.size() == 0) {
			throw new ApplicationBusinessException(
					SolicitaInternacaoONExceptionCode.CRM_ESPECIALIDADE_INVALIDO);
		}
	}

	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public List<ConvenioPlanoVO> pesquisarConvenioPlanoVOPorCodigoDescricao(Object strPesquisa) {
		return getFaturamentoFacade().pesquisarConvenioPlanoVOPorCodigoDescricao(strPesquisa);
	}

	@Secure("#{s:hasPermission('solicitacaoInternacao','pesquisar')}")
	public AinSolicitacoesInternacao obterSolicitacaoInternacaoDetalhada(Integer seq) {
		return this.getAinSolicitacoesInternacaoDAO().obterSolicitacoesInternacaoDetalhada(seq);
	}

	
	@Secure("#{s:hasPermission('solicitacaoInternacao','alterar')}")
	public void persistirSolicitacaoInternacao(
			AinSolicitacoesInternacao solicitacaoInternacao,
			AinSolicitacoesInternacao solicitacaoInternacaoTemp, Boolean substituirProntuario)
			throws ApplicationBusinessException {
		if (solicitacaoInternacao.getSeq() == null) {
			// inclusão
			this.validarCamposObrigatorios(solicitacaoInternacao);
			this.validarDados(solicitacaoInternacao.getPaciente().getCodigo(),
					solicitacaoInternacao.getDtPrevInternacao(),
					solicitacaoInternacao.getIndSitSolicInternacao());
			this.incluirSolicitacaoInternacao(solicitacaoInternacao);
		} else {
			// edição
			this.validarCamposObrigatorios(solicitacaoInternacao);
			this.atualizarSolicitacaoInternacao(solicitacaoInternacao,
					solicitacaoInternacaoTemp, substituirProntuario);
		}
	}

	
	private void incluirSolicitacaoInternacao(AinSolicitacoesInternacao solicitacaoInternacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		solicitacaoInternacao.setCriadoEm(Calendar.getInstance().getTime());
		if (solicitacaoInternacao.getIndSitSolicInternacao() == null) {
			solicitacaoInternacao.setIndSitSolicInternacao(DominioSituacaoSolicitacaoInternacao.P);
		}
		solicitacaoInternacao.setServidorDigitador(servidorLogado);
		try {
			AinSolicitacoesInternacaoDAO ainSolicitacoesInternacaoDAO = this.getAinSolicitacoesInternacaoDAO();
			ainSolicitacoesInternacaoDAO.persistir(solicitacaoInternacao);
			ainSolicitacoesInternacaoDAO.flush();
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					SolicitaInternacaoONExceptionCode.MENSAGEM_ERRO_PERSISTIR_SOLICITACAO_INTERNACAO);
		}
	}

	
	public void atualizarSolicitacaoInternacao(AinSolicitacoesInternacao solicitacaoInternacao,
			AinSolicitacoesInternacao solicitacaoInternacaoTemp, Boolean substituirProntuario) throws ApplicationBusinessException {
		this.getSolicitacaoInternacaoRN().validarDados(solicitacaoInternacao, solicitacaoInternacaoTemp, substituirProntuario);
		this.getSolicitacaoInternacaoRN().validarDadosAtualizacao(solicitacaoInternacao, solicitacaoInternacaoTemp);
		try {
			AinSolicitacoesInternacaoDAO ainSolicitacoesInternacaoDAO = this.getAinSolicitacoesInternacaoDAO();
			ainSolicitacoesInternacaoDAO.merge(solicitacaoInternacao);
			ainSolicitacoesInternacaoDAO.flush();
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					SolicitaInternacaoONExceptionCode.MENSAGEM_ERRO_PERSISTIR_SOLICITACAO_INTERNACAO);
		}
	}

	public void validarDados(Integer pacienteCodigo, Date dtPrevInternacao,
			DominioSituacaoSolicitacaoInternacao indSitSolicInternacao)
			throws BaseRuntimeException, ApplicationBusinessException {

		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
		String dataPrevInternacao = null;
		if (dtPrevInternacao != null) {
			dataPrevInternacao = formatter.format(dtPrevInternacao);
		}

		String dataAtual = formatter.format(Calendar.getInstance().getTime());
		if (dtPrevInternacao != null
				&& (dtPrevInternacao.before(Calendar.getInstance().getTime()) && !dataPrevInternacao
						.equals(dataAtual))) {
			throw new ApplicationBusinessException(
					SolicitaInternacaoONExceptionCode.DATA_PREV_INTERNACAO_MENOR_EXCEPTION);
		} else if (indSitSolicInternacao != null
				&& !indSitSolicInternacao
						.equals(DominioSituacaoSolicitacaoInternacao.P)) {
			indSitSolicInternacao = DominioSituacaoSolicitacaoInternacao.P;
			throw new ApplicationBusinessException(
					SolicitaInternacaoONExceptionCode.SITUACAO_PENDENTE_EXCEPTION);

		} else if (indSitSolicInternacao != null
				&& !indSitSolicInternacao
						.equals(DominioSituacaoSolicitacaoInternacao.P)
				&& !indSitSolicInternacao
						.equals(DominioSituacaoSolicitacaoInternacao.C)
				&& !indSitSolicInternacao
						.equals(DominioSituacaoSolicitacaoInternacao.L)) {
			throw new ApplicationBusinessException(
					SolicitaInternacaoONExceptionCode.SITUACAO_LIBERADA_CANCELADA_EXCEPTION);
		}
	}

	private void validarCamposObrigatorios(
			AinSolicitacoesInternacao solicitacaoInternacao)
			throws ApplicationBusinessException {

		if (solicitacaoInternacao.getConvenio().getId() == null) {
			throw new ApplicationBusinessException(
					SolicitaInternacaoONExceptionCode.AIN_CONVENIO_PLANO_OBRIGATORIO);
		}

		if (solicitacaoInternacao.getEspecialidade() == null) {
			throw new ApplicationBusinessException(
					SolicitaInternacaoONExceptionCode.AIN_ESPECIALIDADE_OBRIGATORIA);
		}

		if (solicitacaoInternacao.getServidor() == null) {
			throw new ApplicationBusinessException(
					SolicitaInternacaoONExceptionCode.AIN_CRM_OBRIGATORIO);
		}
	}

	/**
	 * @return the parametroProcedimento
	 */
	public Short getParametroProcedimento() {
		return this.parametroProcedimento;
	}

	/**
	 * @param parametroProcedimento
	 *            the parametroProcedimento to set
	 */
	public void setParametroProcedimento(Short parametroProcedimento) {
		this.parametroProcedimento = parametroProcedimento;
	}

	public AinSolicitacoesInternacao obterSolicitacaoInternacaoDetached(AinSolicitacoesInternacao solicitacaoInternacao) {
		return getAinSolicitacoesInternacaoDAO().obterSolicitacaoInternacaoDetached(solicitacaoInternacao);
	}

	@Secure("#{s:hasPermission('solicitacaoInternacao','atender')}")
	public void atenderSolicitacaoInternacao(
			AinSolicitacoesInternacao solicitacaoInternacao, Boolean substituirProntuario)
			throws ApplicationBusinessException {
		solicitacaoInternacao
				.setIndSitSolicInternacao(DominioSituacaoSolicitacaoInternacao.A);

		this
				.validarDadosAtendimentoSolicitacaoInternacao(solicitacaoInternacao);
		AinSolicitacoesInternacao solicitacaoInternacaoOriginal = ainSolicitacoesInternacaoDAO.obterOriginal(solicitacaoInternacao);

		this.persistirSolicitacaoInternacao(solicitacaoInternacao,
				solicitacaoInternacaoOriginal, substituirProntuario);
	}

	private void validarDadosAtendimentoSolicitacaoInternacao(
			AinSolicitacoesInternacao solicitacaoInternacao)
			throws ApplicationBusinessException {
		if (solicitacaoInternacao.getQuarto() == null
				&& solicitacaoInternacao.getLeito() == null
				&& solicitacaoInternacao.getUnidadeFuncional() == null) {
			throw new ApplicationBusinessException(
					SolicitaInternacaoONExceptionCode.ERRO_INFORMAR_LEITO_QUARTO_UNIDADE);
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected SolicitacaoInternacaoRN getSolicitacaoInternacaoRN(){
		return solicitacaoInternacaoRN;
	}
		
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade(){
		return pesquisaInternacaoFacade;
	}
	
	protected AinSolicitacoesInternacaoDAO getAinSolicitacoesInternacaoDAO() {
		return ainSolicitacoesInternacaoDAO;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
