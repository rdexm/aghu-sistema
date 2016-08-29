package br.gov.mec.aghu.certificacaodigital.business;

import java.io.IOException;
import java.io.Serializable;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.certificacaodigital.vo.DocumentosPendentesVO;
import br.gov.mec.aghu.certificacaodigital.vo.RelatorioControlePendenciasVO;
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
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface ICertificacaoDigitalFacade extends Serializable {
	
	boolean verificarServidorPossuiPermissaoAssinaturaDigital(
			final Object paramPesquisa);

	void transferirResponsavelDocumento(
			final AghVersaoDocumento aghVersaoDocumento);

	AghVersaoDocumento buscarAghVersaoDocumento(
			final Integer seqAghVersaoDocumento);

	List<VAghVersaoDocumento> pesquisarDocumentosPaciente(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AipPacientes paciente, final Date dataInicial,
			final Date dataFinal, final RapServidores responsavel,
			final FccCentroCustos servico,
			final DominioSituacaoVersaoDocumento situacao,
			final DominioTipoDocumento tipoDocumento);

	void validarFiltrosPesquisaDocumentosPaciente(final AipPacientes paciente,
			final Date dataInicial, final Date dataFinal,
			final RapServidores responsavel, final FccCentroCustos servico,
			final DominioSituacaoVersaoDocumento situacao,
			final DominioTipoDocumento tipoDocumento) throws BaseException;

	Integer obterPeriodoMaximoPesquisa() throws BaseException;

	Integer obterPeriodoPadraoPesquisa()  throws BaseException;
	
	void verificarPeriodoPesquisa(final Date dataInicial, final Date dataFinal)
			throws BaseException;

	List<FccCentroCustos> pesquisarCentroCustoComCertificadoDigital(
			final Object objPesquisa);

	List<RapServidores> pesquisarServidorComCertificacaoDigital(
			final Object objPesquisa);

	List<AghDocumentoCertificado> pesquisarDocumentosCertificados(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AghDocumentoCertificado elemento);

	Long pesquisarDocumentosCertificadosCount(
			final AghDocumentoCertificado aghDocumentoCertificado);

	AghDocumentoCertificado obterAghDocumentoCertificadoPeloId(
			final Integer codigo);

	AghDocumentoCertificado persistirAghDocumentoCertificado(
			final AghDocumentoCertificado aghDocumentoCertificado)
			throws BaseException;

	/**
	 * Atualiza o envelope com a assinatura digital do usuário.
	 * 
	 * @param chave
	 * @param envelope
	 */	
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	void atualizarEnvelope(final Integer chave, final byte[] envelope)
			throws Exception;

	Long listarPendentesResponsavelCount(final RapServidores responsavel);

	List<DocumentosPendentesVO> pesquisarPendentesResponsavel(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final RapServidores responsavel);

	List<DocumentosPendentesVO> pesquisarPendentesResponsavelPaciente(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final RapServidores responsavel, final AipPacientes paciente);

	List<DocumentosPendentesVO> pesquisarPendentesPaciente(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final RapServidores responsavel, final AipPacientes paciente);

	List<DocumentosPendentesVO> pesquisarPendentes(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final RapServidores responsavel,
			final AipPacientes paciente);

	List<DocumentosPendentesVO> pesquisarPendentesPacienteePendentesResponsavelPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores responsavel, AipPacientes paciente);

	List<DocumentosPendentesVO> pesquisarPendentesePendentesResponsavelPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores responsavel, AipPacientes paciente);

	Long listarPendentesResponsavelPacienteCount(
			final RapServidores responsavel, final AipPacientes paciente);

	Long listarPendentesPacienteCount(final RapServidores responsavel,
			final AipPacientes paciente);

	Long listarPendentesCount(final RapServidores responsavel,
			final AipPacientes paciente);

	List<AghCertificadoDigital> pesquisarCertificadoDigital(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AghCertificadoDigital elemento);

	Long pesquisarCertificadoDigitalCount(
			final AghCertificadoDigital aghCertificadoDigital);

	AghVersaoDocumento visualizarDocumentoOriginal(
			final Integer seqAghVersaoDocumentos) throws ApplicationBusinessException;

	AghVersaoDocumento visualizarDocumentoAssinado(
			final Integer seqAghVersaoDocumentos) throws ApplicationBusinessException;

	AghCertificadoDigital persistirAghCertificadoDigital(
			final AghCertificadoDigital aghCertificadoDigital)
			throws ApplicationBusinessException;

	void gerarPendenciaAssinatura(final byte[] arquivoGerado,
			final Object entidadePai,
			final AghDocumentoCertificado documentoCertificado,
			final RapServidores servidor) throws ApplicationBusinessException;
	
	boolean verificaAssituraDigitalHabilitada() throws ApplicationBusinessException;

	/**
	 * Verifica se existe um AghDocumentoCertificado referente ao relatório em
	 * questão, o que indica necessidade de assinatura digital.
	 * 
	 * @param nomeRelatorio
	 * @return
	 *  
	 */
	
	AghDocumentoCertificado verificarRelatorioNecessitaAssinatura(
			final String nomeRelatorio, final DominioTipoDocumento tipoDocumento)
			throws ApplicationBusinessException;

	List<RelatorioControlePendenciasVO> pesquisaPendenciaAssinaturaDigital(
			final RapServidores rapServidores,
			final FccCentroCustos fccCentroCustos,
			final DominioOrdenacaoRelatorioControlePendencias ordenacao)
			throws BaseException;

	List<AghVersaoDocumento> pesquisarDocumentosDoPaciente(
			final AipPacientes paciente);

	Integer verificarNumDiasPendente(final RapServidores responsavel);

	Boolean verificarServidorHabilitadoCertificacaoDigital(
			final RapServidores rapServidores);

	public Boolean verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado();

	/**
	 * @author gandriotti
	 * @param resp
	 * @return
	 */
	
	int obterDiasPendenciaMaisAntiga();

	/**
	 * @author gandriotti
	 * @return
	 */
	Long obterQuantidadeCertificadosPendentes();

	/**
	 * Metodo que verificar a extrapolacao dos limites aceitaveis 
	 * para postergacao da assinatura digital de documentos.
	 * Criterios especificados em:
	 * http://redmine.mec.gov.br/issues/7896 
	 * @author gandriotti	 
	 * @return <code>true</code> se EXTRAPOLOU limites
	 */
	
	boolean verificarNecessidadeResolverPendencias();

	/**
	 * @author gandriotti
	 * @return
	 */
	
	boolean verificarCertificacaoDigitalHabilitado();

	void persistirAghVersaoDocumento(AghVersaoDocumento aghVersaoDocumento);

	void atualizarAghVersaoDocumento(AghVersaoDocumento aghVersaoDocumento);

	//void atualizarAghVersaoDocumento(AghVersaoDocumento aghVersaoDocumento, boolean flush);
	AghVersaoDocumento atualizarAghVersaoDocumento(AghVersaoDocumento aghVersaoDocumento, boolean flush);

	AghVersaoDocumento verificarSituacaoDocumentoPorPaciente(Integer atdSeq, DominioTipoDocumento tipo);

	boolean verificaProfissionalHabilitado();
	
	Long contarVersaoDocumento(AipPacientes paciente);
	
	List<Object[]> countVersaoPacientes(List<Integer> sListaCodigosPacientes);
	
	List<AghDocumento> buscarDocumentosPeloAtendimento(Integer seqAtendimento);
	
	AghVersaoDocumento obterPrimeiroDocumentoAssinadoPorAtendimento(Integer atdSeq, DominioTipoDocumento tipoDocumento);

	public List<AghVersaoDocumento> pesquisarVersoesDocumentosNotasAdicionais(Integer seq);
	
	public List<DominioTipoDocumento[]> listarTipoDocumentoPacienteAtivo();

	List<AghVersaoDocumento> verificaImprime(Integer seqAgenda,
			DominioTipoDocumento tipo,
			List<DominioSituacaoVersaoDocumento> situacoes);

	List<AghVersaoDocumento> buscarVersaoSeqDoc(Integer seqAgenda,
			DominioTipoDocumento tipo,
			List<DominioSituacaoVersaoDocumento> situacoes);

	boolean verificarExisteDocumentosPaciente(AipPacientes paciente);

	boolean valida(Certificate[] certChain)
			throws ApplicationBusinessException;
	
	public String geraCSVRelatorioControlePendencias(
			final RapServidores rapServidores,
			final FccCentroCustos fccCentroCustos,
			final DominioOrdenacaoRelatorioControlePendencias ordenacao) throws BaseException, IOException;

	List<Integer> pesquisarAghVersaoDocumentoPorCirurgia(final Integer crgSeq);
	
	List<AghVersaoDocumento> obterAghVersaoDocumentoPorCirurgia(final Integer crgSeq, final DominioTipoDocumento tipo);
	
	List<AghVersaoDocumento> obterAghVersaoDocumentoPorAgenda(final Integer agdSeq, final DominioTipoDocumento tipo);

	String nameHeaderDownloadedCSVRelatorioControlePendencias();

	AghVersaoDocumento obterPorChavePrimariaTranferirResponsavel(
			Integer seqAghVersaoDocumento);
	
	List<AghVersaoDocumento> pesquisarVersoesLaudoAih(Long seq);
	
	/**
	 * #39011 - Seqs dos documentos atendidos
	 * @param seqAtendimento
	 * @return
	 */
	List<Integer> buscarSeqDocumentosAtendidos(Integer seqAtendimento);
	
	/**
	 * #38995 - Verifica se existe pendência de assinatura digital
	 * @param seqAtendimento
	 * @return
	 */
	Boolean existePendenciaAssinaturaDigital(Integer seqAtendimento);
	
	/**
	 * #39017 - Inativa versões de documentos
	 * @param seq
	 */
	void inativarVersaoDocumento(Integer seq);

	/**
	 * Web Service #39100
	 * 
	 * Buscar o identificador de um documento e sua versão para determinado tipo de documento e atendimento
	 * 
	 * @param atdSeq
	 * @param codTipoDocumento
	 * @return
	 */
	List<AghVersaoDocumento> obterAghVersaoDocumentoPorAtendimentoTipoDocumento(Integer atdSeq, DominioTipoDocumento tipo);
	
	/**
	 * Inativa  registro em AGH_VERSOES_DOCUMENTOS
	 * 
	 * @param listSeq
	 */
	void inativarVersaoDocumentos(List<Integer> listSeq);
	
	Boolean verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado(Integer matricula, Short vinCodigo);
	
	/**
	 * Verifica configuração necessária para assinatura digital.
	 * 
	 * @throws AGHUNegocioException se não estiver configurado adequadamente
	 */
	public void verificaConfiguracaoCertificacaoDigital()
			throws ApplicationBusinessException;

	/**
	 * Retorna true se a assinatura for permitida para as condições indicadas
	 * pelos parâmetros formnecidos.
	 * 
	 * @param cpf
	 *            CPF do assinante, normalmente o cpf contido no certificado do
	 *            assinante
	 * @param vinculo
	 *            vinculo do responsável pelos documentos
	 * @param matricula
	 *            matricula do responsável pelos documentos
	 * @param usuario
	 *            usuário logado
	 * @return
	 */
	boolean permite(String cpf, Short vinculo, Integer matricula, String usuario);

	List<AghVersaoDocumento> pesquisarAghVersaoDocumentoPorAtendimento(
			Integer atdSeq, DominioTipoDocumento tipo, Integer seqDocCertificado,
			DominioSituacaoVersaoDocumento ... situacoes);

	Long pesquisarServidorComCertificacaoDigitalCount(Object param);

	Long pesquisarCentroCustoComCertificadoDigitalCount(Object objPesquisa);

	boolean verificarExisteDocumentosPacienteProntuario(Integer prontuario);

	List<DominioTipoDocumento> listarTipoDocumentoPacienteAtivoDistinct();

}
