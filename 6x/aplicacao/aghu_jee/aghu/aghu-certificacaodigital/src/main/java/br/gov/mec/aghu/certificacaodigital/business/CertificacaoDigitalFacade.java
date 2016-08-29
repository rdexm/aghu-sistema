package br.gov.mec.aghu.certificacaodigital.business;

import java.io.IOException;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.certificacaodigital.dao.AghCertificadoDigitalDAO;
import br.gov.mec.aghu.certificacaodigital.dao.AghDocumentoCertificadoDAO;
import br.gov.mec.aghu.certificacaodigital.dao.AghDocumentoDAO;
import br.gov.mec.aghu.certificacaodigital.dao.AghVersaoDocumentoDAO;
import br.gov.mec.aghu.certificacaodigital.dao.VAghVersaoDocumentoDAO;
import br.gov.mec.aghu.certificacaodigital.vo.DocumentosPendentesVO;
import br.gov.mec.aghu.certificacaodigital.vo.RelatorioControlePendenciasVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioControlePendencias;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghCertificadoDigital;
import br.gov.mec.aghu.model.AghDocumento;
import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAghVersaoDocumento;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Modulo(ModuloEnum.CERTIFICACAO_DIGITAL)
@SuppressWarnings({"PMD.ExcessiveClassLength" })
@Stateless
public class CertificacaoDigitalFacade extends BaseFacade implements ICertificacaoDigitalFacade {

	@EJB
	private ManterDocumentoAssinadoON manterDocumentoAssinadoON;
	@EJB
	private CertificadoDigitalON certificadoDigitalON;
	@EJB
	private RelatorioCSVCertificacaoDigitalON relatorioCSVCertificacaoDigitalON;
	@EJB
	private GerirCertificadoDigitalON gerirCertificadoDigitalON;
	@EJB
	private RelatorioControlePendenciasON relatorioControlePendenciasON;
	@EJB
	private ListarPendenciasAssinaturaON listarPendenciasAssinaturaON;
	@EJB
	private AssinaturaDigitalON assinaturaDigitalON;
	@EJB
	private VisualizarDocumentoON visualizarDocumentoON;
	@EJB
	private DocumentosPacienteON documentosPacienteON;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AghDocumentoCertificadoDAO aghDocumentoCertificadoDAO;
	
	@Inject
	private AghDocumentoDAO aghDocumentoDAO;
	
	@Inject
	private AghCertificadoDigitalDAO aghCertificadoDigitalDAO;
	
	@Inject
	private AghVersaoDocumentoDAO aghVersaoDocumentoDAO;
	
	@Inject
	private VAghVersaoDocumentoDAO vAghVersaoDocumentoDAO;

	private static final long serialVersionUID = -2687416457968003217L;
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#verificarServidorPossuiPermissaoAssinaturaDigital(java.lang.Object)
	 */
	@Override
	public boolean verificarServidorPossuiPermissaoAssinaturaDigital(final Object paramPesquisa) {
		return this.getGerirCertificadoDigitalON().validarServidorPossuiPermissaoAssinaturaDigital(paramPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#transferirResponsavelDocumento(br.gov.mec.aghu.model.AghVersaoDocumento)
	 */
	@Override
	public void transferirResponsavelDocumento(
			final AghVersaoDocumento aghVersaoDocumento) {
		this.getAghVersaoDocumentoDAO().merge(aghVersaoDocumento);
		this.getAghVersaoDocumentoDAO().flush();
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#buscarAghVersaoDocumento(java.lang.Integer)
	 */
	@Override
	public AghVersaoDocumento buscarAghVersaoDocumento(
			final Integer seqAghVersaoDocumento) {
		return this.getAghVersaoDocumentoDAO().obterPorChavePrimariaTranferirResponsavel(
				seqAghVersaoDocumento);
	}
	
	@Override
	public AghVersaoDocumento obterPorChavePrimariaTranferirResponsavel(
			final Integer seqAghVersaoDocumento) {
		return this.getAghVersaoDocumentoDAO().obterPorChavePrimariaTranferirResponsavel(
				seqAghVersaoDocumento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarDocumentosPaciente(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AipPacientes, java.util.Date, java.util.Date, br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.FccCentroCustos, br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento, br.gov.mec.aghu.dominio.DominioTipoDocumento)
	 */
	@Override
	@BypassInactiveModule
	public List<VAghVersaoDocumento> pesquisarDocumentosPaciente(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final AipPacientes paciente, final Date dataInicial,
			final Date dataFinal, final RapServidores responsavel, final FccCentroCustos servico,
			final DominioSituacaoVersaoDocumento situacao,
			final DominioTipoDocumento tipoDocumento) {
		return this.getVAghVersaoDocumentoDAO().pesquisarDocumentosPaciente(
				firstResult, maxResult, orderProperty, asc, paciente,
				dataInicial, dataFinal, responsavel, servico, situacao,
				tipoDocumento);
	}

	protected VAghVersaoDocumentoDAO getVAghVersaoDocumentoDAO() {
		return vAghVersaoDocumentoDAO;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#validarFiltrosPesquisaDocumentosPaciente(br.gov.mec.aghu.model.AipPacientes, java.util.Date, java.util.Date, br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.FccCentroCustos, br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento, br.gov.mec.aghu.dominio.DominioTipoDocumento)
	 */
	@Override
	@BypassInactiveModule
	public void validarFiltrosPesquisaDocumentosPaciente(final AipPacientes paciente,
			final Date dataInicial, final Date dataFinal, final RapServidores responsavel,
			final FccCentroCustos servico, final DominioSituacaoVersaoDocumento situacao,
			final DominioTipoDocumento tipoDocumento) throws BaseException {
		this.getDocumentosPacienteON().validarFiltrosPesquisa(paciente,
				dataInicial, dataFinal, responsavel, servico, situacao,
				tipoDocumento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#obterPeriodoMaximoPesquisa()
	 */
	@Override
	@BypassInactiveModule
	public Integer obterPeriodoMaximoPesquisa() 
			throws BaseException{
		return this.getDocumentosPacienteON().obterPeriodoMaximoPesquisa();
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#verificarPeriodoPesquisa(java.util.Date, java.util.Date)
	 */
	@Override
	@BypassInactiveModule
	public void verificarPeriodoPesquisa(final Date dataInicial, final Date dataFinal)
			throws BaseException{
		this.getDocumentosPacienteON().verificarPeriodoPesquisa(dataInicial, dataFinal);
	}

	@Override
	@BypassInactiveModule
	public Integer obterPeriodoPadraoPesquisa() throws BaseException {
		return this.getDocumentosPacienteON().obterPeriodoPadraoPesquisa();
	}
	
	protected DocumentosPacienteON getDocumentosPacienteON() {
		return documentosPacienteON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarCentroCustoComCertificadoDigital(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<FccCentroCustos> pesquisarCentroCustoComCertificadoDigital(
			final Object objPesquisa) {
		return this.getAghCertificadoDigitalDAO()
				.pesquisarCentroCustoComCertificadoDigital(objPesquisa);
	}
	
	@Override
	public Long pesquisarCentroCustoComCertificadoDigitalCount(
			Object objPesquisa) {
		return this.getAghCertificadoDigitalDAO()
				.pesquisarCentroCustoComCertificadoDigitalCount(objPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarServidorComCertificacaoDigital(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<RapServidores> pesquisarServidorComCertificacaoDigital(
			final Object objPesquisa) {
		return this.getAghCertificadoDigitalDAO()
				.pesquisarServidorComCertificacaoDigital(objPesquisa);
	}
	
	@Override
	public Long pesquisarServidorComCertificacaoDigitalCount(Object param) {
		return this.getAghCertificadoDigitalDAO().pesquisarServidorComCertificacaoDigitalCount(param);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarDocumentosCertificados(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AghDocumentoCertificado)
	 */
	@Override
	public List<AghDocumentoCertificado> pesquisarDocumentosCertificados(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final AghDocumentoCertificado elemento) {

		return this.getAghDocumentoCertificadoDAO().pesquisar(firstResult,
				maxResult, orderProperty, asc, elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarDocumentosCertificadosCount(br.gov.mec.aghu.model.AghDocumentoCertificado)
	 */
	@Override
	public Long pesquisarDocumentosCertificadosCount(
			final AghDocumentoCertificado aghDocumentoCertificado) {
		return getAghDocumentoCertificadoDAO().pesquisarCount(
				aghDocumentoCertificado);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#obterAghDocumentoCertificadoPeloId(java.lang.Integer)
	 */
	@Override
	public AghDocumentoCertificado obterAghDocumentoCertificadoPeloId(
			final Integer codigo) {
		return getAghDocumentoCertificadoDAO().obterPeloId(codigo);
	}



	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#persistirAghDocumentoCertificado(br.gov.mec.aghu.model.AghDocumentoCertificado)
	 */
	@Override
	public AghDocumentoCertificado persistirAghDocumentoCertificado(
			final AghDocumentoCertificado aghDocumentoCertificado)
			throws BaseException {
		return getManterDocumentoAssinadoON().persistirAghDocumentoCertificado(
				aghDocumentoCertificado);
	}

	/**
	 * Atualiza o envelope com a assinatura digital do usuário.
	 * 
	 * @param chave
	 * @param envelope
	 *(non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#atualizarEnvelope(java.lang.Integer, byte[])
	 */
	@Override
	
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void atualizarEnvelope(final Integer chave, final byte[] envelope)
			throws Exception {
		this.getAssinaturaDigitalON().atualizarEnvelope(chave, envelope);
		
	}

	private AssinaturaDigitalON getAssinaturaDigitalON() {
		return assinaturaDigitalON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#listarPendentesResponsavelCount(br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	public Long listarPendentesResponsavelCount(final RapServidores responsavel) {
		return getAghVersaoDocumentoDAO().listarPendentesResponsavelCount(
				responsavel);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarPendentesResponsavel(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	public List<DocumentosPendentesVO> pesquisarPendentesResponsavel(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final RapServidores responsavel) {

		return this.getListarPendenciasAssinaturaON()
				.pesquisarPendentesResponsavel(firstResult, maxResult,
						orderProperty, asc, responsavel);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarPendentesResponsavelPaciente(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.AipPacientes)
	 */
	@Override
	public List<DocumentosPendentesVO> pesquisarPendentesResponsavelPaciente(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final RapServidores responsavel, final AipPacientes paciente) {

		return this.getListarPendenciasAssinaturaON()
				.pesquisarPendentesResponsavelPaciente(firstResult, maxResult,
						orderProperty, asc, responsavel, paciente);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarPendentesPaciente(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.AipPacientes)
	 */
	@Override
	public List<DocumentosPendentesVO> pesquisarPendentesPaciente(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final RapServidores responsavel, final AipPacientes paciente) {

		return this.getListarPendenciasAssinaturaON()
				.pesquisarPendentesPaciente(firstResult, maxResult,
						orderProperty, asc, responsavel, paciente);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarPendentes(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.AipPacientes)
	 */
	@Override
	public List<DocumentosPendentesVO> pesquisarPendentes(final Integer firstResult,
			final Integer maxResult, final String orderProperty, final boolean asc,
			final RapServidores responsavel, final AipPacientes paciente) {

		return this.getListarPendenciasAssinaturaON().pesquisarPendentes(
				firstResult, maxResult, orderProperty, asc, responsavel,
				paciente);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarPendentesPacienteePendentesResponsavelPaciente(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.AipPacientes)
	 */
	@Override
	public List<DocumentosPendentesVO> pesquisarPendentesPacienteePendentesResponsavelPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores responsavel, AipPacientes paciente) {

		return this.getListarPendenciasAssinaturaON()
				.pesquisarPendentesPacienteePendentesResponsavelPaciente(
						firstResult, maxResult, orderProperty, asc,
						responsavel, paciente);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarPendentesePendentesResponsavelPaciente(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.AipPacientes)
	 */
	@Override
	public List<DocumentosPendentesVO> pesquisarPendentesePendentesResponsavelPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores responsavel, AipPacientes paciente) {

		return this.getListarPendenciasAssinaturaON()
				.pesquisarPendentesePendentesResponsavelPaciente(firstResult,
						maxResult, orderProperty, asc, responsavel, paciente);
	}			

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#listarPendentesResponsavelPacienteCount(br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.AipPacientes)
	 */
	@Override
	public Long listarPendentesResponsavelPacienteCount(
			final RapServidores responsavel, final AipPacientes paciente) {
		return getAghVersaoDocumentoDAO()
				.listarPendentesResponsavelPacienteCount(responsavel, paciente);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#listarPendentesPacienteCount(br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.AipPacientes)
	 */
	@Override
	public Long listarPendentesPacienteCount(final RapServidores responsavel,
			final AipPacientes paciente) {
		return getAghVersaoDocumentoDAO().listarPendentesPacienteCount(
				responsavel, paciente);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#listarPendentesCount(br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.AipPacientes)
	 */
	@Override
	public Long listarPendentesCount(final RapServidores responsavel,
			final AipPacientes paciente) {
		return getAghVersaoDocumentoDAO().listarPendentesCount(responsavel,
				paciente);
	}

	@Override
	@BypassInactiveModule
	public List<DominioTipoDocumento[]> listarTipoDocumentoPacienteAtivo() {	
		return getDocumentosPacienteON().listarTipoDocumentosAtivos();
	}
	
	@Override
	@BypassInactiveModule
	public List<DominioTipoDocumento> listarTipoDocumentoPacienteAtivoDistinct() {	
		return aghDocumentoCertificadoDAO.listarTipoDocumentosAtivosDistinct();
	}
	
	protected ListarPendenciasAssinaturaON getListarPendenciasAssinaturaON() {
		return listarPendenciasAssinaturaON;
	}

	protected AghVersaoDocumentoDAO getAghVersaoDocumentoDAO() {
		return aghVersaoDocumentoDAO;
	}

	protected AghDocumentoCertificadoDAO getAghDocumentoCertificadoDAO() {
		return aghDocumentoCertificadoDAO;
	}

	protected ManterDocumentoAssinadoON getManterDocumentoAssinadoON() {
		return manterDocumentoAssinadoON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarCertificadoDigital(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AghCertificadoDigital)
	 */
	@Override
	public List<AghCertificadoDigital> pesquisarCertificadoDigital(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final AghCertificadoDigital elemento) {

		return this.getAghCertificadoDigitalDAO().pesquisar(firstResult,
				maxResult, orderProperty, asc, elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarCertificadoDigitalCount(br.gov.mec.aghu.model.AghCertificadoDigital)
	 */
	@Override
	public Long pesquisarCertificadoDigitalCount(
			final AghCertificadoDigital aghCertificadoDigital) {
		return getAghCertificadoDigitalDAO().pesquisarCount(
				aghCertificadoDigital);
	}

	protected AghCertificadoDigitalDAO getAghCertificadoDigitalDAO() {
		return aghCertificadoDigitalDAO;
	}

	protected VisualizarDocumentoON getVisualizarDocumentoON() {
		return visualizarDocumentoON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#visualizarDocumentoOriginal(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public AghVersaoDocumento visualizarDocumentoOriginal(
			final Integer seqAghVersaoDocumentos) throws ApplicationBusinessException {
		return this.getVisualizarDocumentoON().visualizarDocumentoOriginal(
				seqAghVersaoDocumentos);

	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#visualizarDocumentoAssinado(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public AghVersaoDocumento visualizarDocumentoAssinado(
			final Integer seqAghVersaoDocumentos) throws ApplicationBusinessException {
		return this.getVisualizarDocumentoON().visualizarDocumentoAssinado(
				seqAghVersaoDocumentos);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#persistirAghCertificadoDigital(br.gov.mec.aghu.model.AghCertificadoDigital)
	 */
	@Override
	public AghCertificadoDigital persistirAghCertificadoDigital(
			final AghCertificadoDigital aghCertificadoDigital)
			throws ApplicationBusinessException {
		return getGerirCertificadoDigitalON().persistirAghCertificadoDigital(
				aghCertificadoDigital);

	}

	

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#gerarPendenciaAssinatura(byte[], java.lang.Object, br.gov.mec.aghu.model.AghDocumentoCertificado, br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	@BypassInactiveModule
	public void gerarPendenciaAssinatura(final byte[] arquivoGerado,
			final Object entidadePai, final AghDocumentoCertificado documentoCertificado, RapServidores servidor) throws ApplicationBusinessException {
		final AssinaturaDigitalON on = getAssinaturaDigitalON();

		on.gerarPendenciaAssinatura(arquivoGerado, entidadePai,
				documentoCertificado, servidor);

	}
	
	@Override
	@BypassInactiveModule
	public boolean verificaAssituraDigitalHabilitada() throws ApplicationBusinessException{
		return getAssinaturaDigitalON().verificaAssituraDigitalHabilitada();
	}
	
	/**
	 * Verifica se existe um AghDocumentoCertificado referente ao relatório em
	 * questão, o que indica necessidade de assinatura digital.
	 * 
	 * @param nomeRelatorio
	 * @return
	 *  
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#verificarRelatorioNecessitaAssinatura(java.lang.String, br.gov.mec.aghu.dominio.DominioTipoDocumento)
	 */
	@Override
	@BypassInactiveModule
	public AghDocumentoCertificado verificarRelatorioNecessitaAssinatura(
			final String nomeRelatorio, final DominioTipoDocumento tipoDocumento) throws ApplicationBusinessException {
		
		final AghDocumentoCertificadoDAO dao = this.getAghDocumentoCertificadoDAO();

		final AghDocumentoCertificado retorno = dao
				.verificarExistenciaDocumentoCertificadoPorNome(nomeRelatorio,tipoDocumento);

		return retorno;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisaPendenciaAssinaturaDigital(br.gov.mec.aghu.model.RapServidores, br.gov.mec.aghu.model.FccCentroCustos, br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioControlePendencias)
	 */
	@Override
	public List<RelatorioControlePendenciasVO> pesquisaPendenciaAssinaturaDigital(
			final RapServidores rapServidores, final FccCentroCustos fccCentroCustos,
			final DominioOrdenacaoRelatorioControlePendencias ordenacao)
			throws BaseException {
		return this.getRelatorioControlePendenciasON()
				.pesquisaPendenciaAssinaturaDigital(rapServidores,
						fccCentroCustos, ordenacao);
	}

	protected RelatorioControlePendenciasON getRelatorioControlePendenciasON() {
		return relatorioControlePendenciasON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#pesquisarDocumentosDoPaciente(br.gov.mec.aghu.model.AipPacientes)
	 */
	@Override
	public List<AghVersaoDocumento> pesquisarDocumentosDoPaciente(
			final AipPacientes paciente) {
		return this.getAghVersaoDocumentoDAO()
				.listarDocumentosDoPacienteOrdenadoPorCriadoEm(paciente);
	}
	
	@Override
	@BypassInactiveModule
	public boolean verificarExisteDocumentosPaciente(AipPacientes paciente){
		return this.getAghVersaoDocumentoDAO().verificarExisteDocumentosPaciente(paciente);
	}
	
	@Override
	@BypassInactiveModule
	public boolean verificarExisteDocumentosPacienteProntuario(Integer prontuario){
		return this.getVAghVersaoDocumentoDAO().existeVersaoDocumentoParaProntuario(prontuario);
	}


	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#verificarNumDiasPendente(br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	public Integer verificarNumDiasPendente(final RapServidores responsavel) {
		return this.getListarPendenciasAssinaturaON().verificarNumDiasPendente(
				responsavel);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#verificarServidorHabilitadoCertificacaoDigital(br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	public Boolean verificarServidorHabilitadoCertificacaoDigital(
			final RapServidores rapServidores) {
		return this.getAghCertificadoDigitalDAO()
				.verificaProfissionalHabilitado(rapServidores);
	}
	
	@Override
	public Boolean verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado() {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		return verificarServidorHabilitadoCertificacaoDigital(servidorLogado);
	}
	
	@Override
	public Boolean verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado(Integer matricula, Short vinCodigo) {
		RapServidores servidorAutenticado = getRegistroColaboradorFacade().buscarServidor(vinCodigo, matricula);
		return verificarServidorHabilitadoCertificacaoDigital(servidorAutenticado);
	}
	
	/**
	 * @author gandriotti
	 * @param resp
	 * @return
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#obterDiasPendenciaMaisAntiga()
	 */
	@Override
	@BypassInactiveModule
	public int obterDiasPendenciaMaisAntiga() {
		return this.getCertificadoDigitalON().obterDiasPendenciaMaisAntiga();
	}
	
	/**
	 * @author gandriotti
	 * @return
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#obterQuantidadeCertificadosPendentes()
	 */
	@Override
	@BypassInactiveModule
	public Long obterQuantidadeCertificadosPendentes() {
		return this.getCertificadoDigitalON().obterQuantidadeCertificadosPendentes();
	}

	/**
	 * Metodo que verificar a extrapolacao dos limites aceitaveis 
	 * para postergacao da assinatura digital de documentos.
	 * Criterios especificados em:
	 * http://redmine.mec.gov.br/issues/7896 
	 * @author gandriotti	 
	 * @return <code>true</code> se EXTRAPOLOU limites
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#verificarNecessidadeResolverPendencias()
	 */
	@Override
	@BypassInactiveModule
	public boolean verificarNecessidadeResolverPendencias() {
		return this.getCertificadoDigitalON()
				.verificarNecessidadeResolverPendencias();
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade#verificarCertificacaoDigitalHabilitado()
	 */
	@Override
	@BypassInactiveModule
	public boolean verificarCertificacaoDigitalHabilitado() {
		return this.getCertificadoDigitalON().obterParametroCertificacaoDigitalHabilitada();
	}
	
	
	protected GerirCertificadoDigitalON getGerirCertificadoDigitalON(){
		return gerirCertificadoDigitalON;
	}
	
	@Override
	public void persistirAghVersaoDocumento(
			AghVersaoDocumento aghVersaoDocumento) {
		this.getAghVersaoDocumentoDAO().persistir(aghVersaoDocumento);
	}

	@Override
	public void atualizarAghVersaoDocumento(
			AghVersaoDocumento aghVersaoDocumento) {
		this.getAghVersaoDocumentoDAO().atualizar(aghVersaoDocumento);
	}

	@Override
	public AghVersaoDocumento atualizarAghVersaoDocumento(
			AghVersaoDocumento aghVersaoDocumento, boolean flush) {
		AghVersaoDocumento retorno = this.getAghVersaoDocumentoDAO().atualizar(aghVersaoDocumento);
		if (flush) {
			this.getAghVersaoDocumentoDAO().flush();
		}
		return retorno;
	}

	@Override
	@BypassInactiveModule
	public AghVersaoDocumento verificarSituacaoDocumentoPorPaciente(
			Integer atdSeq, DominioTipoDocumento tipo) {
		return this.getAghVersaoDocumentoDAO()
				.verificarSituacaoDocumentoPorPaciente(atdSeq, tipo);
	}
	
	@Override
	public boolean verificaProfissionalHabilitado() {
		RapServidores profissional = getServidorLogadoFacade().obterServidorLogado();
		return this.getAghCertificadoDigitalDAO().verificaProfissionalHabilitado(profissional);
	}
	
	@Override
	public List<AghDocumento> buscarDocumentosPeloAtendimento(Integer seqAtendimento) {
		return getAghDocumentoDAO().buscarDocumentosPeloAtendimento(
				seqAtendimento);
	}
	
	
	@Override
	public Long contarVersaoDocumento(AipPacientes paciente) {
		return getAghVersaoDocumentoDAO().count(paciente);
	}
	
	@Override
	public List<Object[]> countVersaoPacientes(List<Integer> sListaCodigosPacientes){
		return this.getVAghVersaoDocumentoDAO().countVersaoPacientes(sListaCodigosPacientes);
	}
	
	
	private AghDocumentoDAO getAghDocumentoDAO() {
		return aghDocumentoDAO;
	}

	@Override
	@BypassInactiveModule
	public AghVersaoDocumento obterPrimeiroDocumentoAssinadoPorAtendimento(Integer atdSeq, DominioTipoDocumento tipoDocumento) {
		return getAghVersaoDocumentoDAO().obterPrimeiroDocumentoAssinadoPorAtendimento(atdSeq, tipoDocumento);
	}

	@Override
	@BypassInactiveModule
	public List<AghVersaoDocumento> pesquisarVersoesDocumentosNotasAdicionais(Integer seq) {
		return getAghVersaoDocumentoDAO().pesquisarVersoesDocumentosNotasAdicionais(seq);

	}
	
	@Override
	@BypassInactiveModule
	public List<AghVersaoDocumento> verificaImprime(Integer seqAgenda,
			DominioTipoDocumento tipo,
			List<DominioSituacaoVersaoDocumento> situacoes) {
		return getAghVersaoDocumentoDAO().verificaImprime(seqAgenda,
				tipo, situacoes);
	}

	@Override
	public List<AghVersaoDocumento> buscarVersaoSeqDoc(Integer seqAgenda,
			DominioTipoDocumento tipo,
			List<DominioSituacaoVersaoDocumento> situacoes) {
		return getAghVersaoDocumentoDAO().buscarVersaoSeqDoc(seqAgenda,
				tipo, situacoes);
	}

	@Override
	public String geraCSVRelatorioControlePendencias(
			final RapServidores rapServidores,
			final FccCentroCustos fccCentroCustos,
			final DominioOrdenacaoRelatorioControlePendencias ordenacao) throws BaseException, IOException {
		return getRelatorioCSVCertificacaoDigitalON()
				.geraCSVRelatorioControlePendencias(rapServidores,
						fccCentroCustos, ordenacao);
	}

	protected RelatorioCSVCertificacaoDigitalON getRelatorioCSVCertificacaoDigitalON() {
		return relatorioCSVCertificacaoDigitalON;
	}

	@Override
	public String nameHeaderDownloadedCSVRelatorioControlePendencias() {
		return getRelatorioCSVCertificacaoDigitalON().nameHeaderDownloadedCSVRelatorioControlePendencias();
	}

	@Override
	@BypassInactiveModule
	public List<Integer> pesquisarAghVersaoDocumentoPorCirurgia(final Integer crgSeq) {
		return getAghVersaoDocumentoDAO().pesquisarAghVersaoDocumentoPorCirurgia(crgSeq);
	}
	
	@Override
	public List<AghVersaoDocumento> obterAghVersaoDocumentoPorCirurgia(final Integer crgSeq, final DominioTipoDocumento tipo) {
		return getAghVersaoDocumentoDAO().obterAghVersaoDocumentoPorCirurgia(crgSeq, tipo);
	}

	@Override
	public List<AghVersaoDocumento> obterAghVersaoDocumentoPorAgenda(final Integer agdSeq, final DominioTipoDocumento tipo) {
		return getAghVersaoDocumentoDAO().obterAghVersaoDocumentoPorAgenda(agdSeq, tipo);
	}

	@Override
	public boolean valida(Certificate[] chain)
			throws ApplicationBusinessException {
		return this.getAssinaturaDigitalON().validaCadeiaCertificados(chain);
	}
	
	protected CertificadoDigitalON getCertificadoDigitalON() {
		return certificadoDigitalON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	@Override
	public List<AghVersaoDocumento> pesquisarVersoesLaudoAih(Long seq) {
		return getAghVersaoDocumentoDAO().pesquisarVersoesLaudoAih(seq);
	}
	
	/**
	 * #39011 - Seqs dos documentos atendidos
	 * @param seqAtendimento
	 * @return
	 */
	@Override
	public List<Integer> buscarSeqDocumentosAtendidos(Integer seqAtendimento) {
		return getAghDocumentoDAO().buscarSeqDocumentosAtendidos(seqAtendimento);
	}
	
	/**
	 * #38995 - Verifica se existe pendência de assinatura digital
	 * @param seqAtendimento
	 * @return
	 */
	@Override
	public Boolean existePendenciaAssinaturaDigital(Integer seqAtendimento) {
		return getAghVersaoDocumentoDAO().existePendenciaAssinaturaDigital(seqAtendimento);
	}
	
	/**
	 *  #39017 - Inativa versões de documentos
	 * @param seq
	 */
	@Override
	public void inativarVersaoDocumento(Integer seq){
		getCertificadoDigitalON().inativarVersaoDocumento(seq);
	}
	
	@Override
	public List<AghVersaoDocumento> obterAghVersaoDocumentoPorAtendimentoTipoDocumento(Integer atdSeq, DominioTipoDocumento tipo) {
		return getAghVersaoDocumentoDAO().obterAghVersaoDocumentoPorAtendimentoTipoDocumento(atdSeq, tipo);		
	}
	
	@Override
	public void inativarVersaoDocumentos(List<Integer> listSeq) {
		getCertificadoDigitalON().inativarVersaoDocumentos(listSeq);
	}
	
	@Override
	public void verificaConfiguracaoCertificacaoDigital()
			throws ApplicationBusinessException {
		this.getAssinaturaDigitalON().verificaConfiguracaoCertificacaoDigital();
	}
	
	@Override
	public boolean permite(String cpf, Short vinculo, Integer matricula,
			String usuario) {
		return this.getAssinaturaDigitalON().permite(cpf, vinculo, matricula,
				usuario);
	}

	@Override
	@BypassInactiveModule
	public List<AghVersaoDocumento> pesquisarAghVersaoDocumentoPorAtendimento(final Integer atdSeq, final DominioTipoDocumento tipo,
			Integer seqDocCertificado, DominioSituacaoVersaoDocumento ... situacoes) {
		return getAghVersaoDocumentoDAO().pesquisarAghVersaoDocumentoPorAtendimento(atdSeq, tipo, seqDocCertificado, situacoes);
	}
	
}
