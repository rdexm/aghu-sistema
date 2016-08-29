package br.gov.mec.aghu.certificacaodigital.business;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cms.CMSException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.dao.AghDocumentoDAO;
import br.gov.mec.aghu.certificacaodigital.dao.AghVersaoDocumentoDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghDocumento;
import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PlanejamentoCirurgicoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.AuthorizationException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.ufrgs.hcpa.crypto.CMSValidator;
import br.ufrgs.hcpa.crypto.CRLManager;
import br.ufrgs.hcpa.crypto.CertValidator;
import br.ufrgs.hcpa.crypto.ProviderManager;
import br.ufrgs.hcpa.crypto.TrustAnchorManager;

/**
 * Classe responsável por encaminhar o processo de assinatura digital.
 * 
 * @author gmneto
 * 
 */
@Stateless
public class AssinaturaDigitalON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AssinaturaDigitalON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private AghDocumentoDAO aghDocumentoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AghVersaoDocumentoDAO aghVersaoDocumentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8986539163785534590L;

	private enum AssinaturaDigitalONExceptionCode implements BusinessExceptionCode { ERRO_ATUALIZACAO_CERTIFICADOS_REVOGADOS,
		USUARIO_SEM_PERMISSAO_ASSINAR, CPF_INCOMPATIVEL_USUARIO_LOGADO, ERRO_VALIDACAO_ENVELOPE_CRIPTOGRAFICO, ERRO_VALIDACAO_CADEIA, ERRO_CERTIFICADO_EXPIRADO, 
		ERRO_CERTIFICADO_INVALIDO, ERRO_PENDENCIA_ASSINATURA, ERRO_CONFIGURACAO_CERTIFICACAO_DIGITAL
	}

	/**
	 * Verifica as configurações necessárias para a assinatura digital como
	 * existência dos parâmetros, validade dos parâmetros, etc.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaConfiguracaoCertificacaoDigital()
			throws ApplicationBusinessException {

		try {
			this.getPastaCertificados();
			this.getPastaCRLs();
		} catch (ApplicationBusinessException e) {
			LOG.warn("A certificação digital não está configurada corretamente."
					+ e.getCode().toString());
			throw new ApplicationBusinessException(
					AssinaturaDigitalON.AssinaturaDigitalONExceptionCode.ERRO_CONFIGURACAO_CERTIFICACAO_DIGITAL);
		} catch (FileNotFoundException e) {
			LOG.warn(e.getMessage());
			throw new ApplicationBusinessException(
					AssinaturaDigitalON.AssinaturaDigitalONExceptionCode.ERRO_CONFIGURACAO_CERTIFICACAO_DIGITAL);
		}

	}

	public boolean validaCadeiaCertificados(Certificate[] chain)
			throws ApplicationBusinessException {
		ProviderManager.addBouncyCastelProvider();
		try {

			String caminhoCertificados = this.getPastaCertificados();

			String caminhoCRLs = this.getPastaCRLs();

			TrustAnchorManager anchorManager = getTrustAnchorManager(caminhoCertificados);

			CertValidator validator = new CertValidator(
					anchorManager.getTrustAnchors(), new CRLManager(caminhoCRLs));

			validator.validate(Arrays.asList(chain));

		} catch (ApplicationBusinessException e) {
			LOG.error("Erro validação da cadeia.", e);
			throw new ApplicationBusinessException(
					AssinaturaDigitalON.AssinaturaDigitalONExceptionCode.ERRO_VALIDACAO_CADEIA);
		} catch (FileNotFoundException e){
			LOG.warn(e.getMessage());
			throw new ApplicationBusinessException(
					AssinaturaDigitalON.AssinaturaDigitalONExceptionCode.ERRO_CONFIGURACAO_CERTIFICACAO_DIGITAL);
		} catch (IOException e) {
			LOG.error("Erro ao validar cadeia de certificados", e);
			throw new ApplicationBusinessException(
					AssinaturaDigitalON.AssinaturaDigitalONExceptionCode.ERRO_ATUALIZACAO_CERTIFICADOS_REVOGADOS,
					e.getMessage());
		} catch (CertPathValidatorException e) {
			if (e.getCause() instanceof CertificateExpiredException) {
				LOG.error("Certificado Expirado " + e.getMessage());
				throw new ApplicationBusinessException(
						AssinaturaDigitalON.AssinaturaDigitalONExceptionCode.ERRO_CERTIFICADO_EXPIRADO,
						e.getMessage());
			} else if (e.getCause() instanceof CertificateNotYetValidException) {
				LOG.error("Certificado ainda não é válido "
						+ e.getMessage());
				throw new ApplicationBusinessException(
						AssinaturaDigitalON.AssinaturaDigitalONExceptionCode.ERRO_CERTIFICADO_INVALIDO,
						e.getMessage());
			} else {
				LOG.error("Erro ao validar cadeia de certificados", e);
				throw new ApplicationBusinessException(
						AssinaturaDigitalON.AssinaturaDigitalONExceptionCode.ERRO_VALIDACAO_CADEIA);
			}
		} catch (GeneralSecurityException e) {
			LOG.error("Erro ao validar cadeia de certificados", e);
			throw new ApplicationBusinessException(
					AssinaturaDigitalON.AssinaturaDigitalONExceptionCode.ERRO_VALIDACAO_CADEIA);
		}
		return true;
	}

	/**
	 * Retorna o caminho da pasta para armazenamento das listas de certificados
	 * revogados cadastrado no parâmetro P_CAMINHO_PASTA_CRL
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 *             se o parâmetro não foi encontrado
	 * @throws FileNotFoundException
	 *             se o caminho cadastrado não existe ou é inválido
	 */
	private String getPastaCRLs()
			throws ApplicationBusinessException, FileNotFoundException {
		IParametroFacade parametrosFacade = this.getParametroFacade();
		String caminhoCRLs = parametrosFacade.buscarAghParametro(
				AghuParametrosEnum.P_CAMINHO_PASTA_CRL).getVlrTexto();

		java.io.File directory = new java.io.File(caminhoCRLs);
		if (!directory.exists() || !directory.isDirectory()
				|| !directory.canRead() || !directory.canWrite()) {
			LOG.warn("O parâmetro P_CAMINHO_PASTA_CRL não está configurado corretamente.");
			throw new FileNotFoundException("O path "
					+ caminhoCRLs
					+ " não existe, não é um diretorio, não pode ser lido ou não pode ser escrito.");
		}
		return caminhoCRLs;
	}

	/**
	 * Retorna o caminho da pasta que contem os certificados das CAs emissoras
	 * dos certificados de usuário cadastrado no parâmetro
	 * P_CAMINHO_PASTA_CERTIFICADOS
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 *             se o parâmetro não foi encontrado
	 * @throws FileNotFoundException
	 *             se o caminho cadastrado não existe ou é inválido
	 */
	private String getPastaCertificados()
			throws ApplicationBusinessException, FileNotFoundException {
		IParametroFacade parametrosFacade = this.getParametroFacade();
		String caminhoCertificados = parametrosFacade.buscarAghParametro(
				AghuParametrosEnum.P_CAMINHO_PASTA_CERTIFICADOS)
				.getVlrTexto();
		
		java.io.File directory = new java.io.File(caminhoCertificados);
		if (!directory.exists() || !directory.isDirectory()
				|| !directory.canRead()) {
			LOG.warn("O parâmetro P_CAMINHO_PASTA_CERTIFICADOS não está configurado corretamente.");
			throw new FileNotFoundException("O path "
					+ caminhoCertificados
					+ " não existe, não é um diretorio ou não pode ser lido.");
		}
		return caminhoCertificados;
	}
	
	/** 
	 * Se o usuário logado possui permissao samis, então verifica se o dono do certificado é o dono dos documentos
	 * @deprecated utilizar {@link #permite(String, Short, Integer, String)}
	 * @param parametros
	 * @return
	 */
	public boolean permite(Object[] parametros) {
		String cpfAssinador = (String) parametros[1];
		Short vincResponsavel = (Short) parametros[2];
		Integer matResponsavel = (Integer) parametros[3];
		String usuarioLogado = (String) parametros[4];

		RapServidores servidor = getServidorValido(cpfAssinador,
				vincResponsavel, matResponsavel, usuarioLogado);

		if (servidor == null) {
			return false;
		}

		return true;
	}
	
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
	public boolean permite(String cpf, Short vinculo, Integer matricula,
			String usuario){
		RapServidores servidor = getServidorValido(cpf,
				vinculo, matricula, usuario);

		if (servidor == null) {
			return false;
		}

		return true;
		
	}

	private void verificaPermissaoUsuario(RapServidores servidor) {
		final String componenteAssinaturaDigital = "assinaturaDigital";
		final String metodoAssinaturaDigital = "assinar";
		
		if (!getICascaFacade().usuarioTemPermissao(servidor.getUsuario(),
				componenteAssinaturaDigital, metodoAssinaturaDigital)) {
			throw new AuthorizationException(
					"Seu usuário não tem permissão para realizar esta operação. Contate o administrador do sistema e peça-lhe para associar o componente '"
							+ componenteAssinaturaDigital
							+ "' e método '"
							+ metodoAssinaturaDigital
							+ "' a uma das suas permissões e após tente novamente.");
		}

	}

	/**
	 * Retorna o servidor com permissao para assinar os documentos.
	 * 
	 * @param cpfAssinador
	 * @param vincResponsavel
	 * @param matResponsavel
	 * @param usuarioLogado
	 * @return null se não houver permissão
	 */
	private RapServidores getServidorValido(String cpfAssinador,
			Short vincResponsavel, Integer matResponsavel, String usuarioLogado) {
		
		List<RapServidores> listaServidores = getRegistroColaboradorFacade()
				.obterServidorPorCPF(Long.valueOf(cpfAssinador));

		for (RapServidores servidorTitularCartao : listaServidores) {

			if (this.hasQualificacao(servidorTitularCartao)) {
				Date hoje = Calendar.getInstance().getTime();

				if (this.getRegistroColaboradorFacade().isAtivo(
						servidorTitularCartao, hoje)) {
					// o servidor do cpf fornecido(cartão) está ativo
					return servidorTitularCartao;
				}
	
				
				boolean usuarioLogadoSamis = getICascaFacade()
						.usuarioTemPermissao(usuarioLogado,
								"samisAssinaturaDigital");
				
				if (usuarioLogadoSamis) {
					// o usuário logado tem permissão samis
					if (this.verificaCpfResponsavel(vincResponsavel,
							matResponsavel, servidorTitularCartao)) {
						// cpf do responsavel pelos documentos é o mesmo do
						// cartão
						return servidorTitularCartao;
					} else if (usuarioLogadoSamis
							&& (servidorTitularCartao.getIndSituacao().equals(
									DominioSituacaoVinculo.I) && servidorTitularCartao
									.getDtFimVinculo().before(hoje))) {
						// Se o usuário logado tem permisao samis,
						// verifica se o dono do certificado é o mesmo dono dos
						// documentos
						if (verificaCpfResponsavel(vincResponsavel, matResponsavel,
								servidorTitularCartao)) {
							return servidorTitularCartao;
						} else {
							continue;
						}
					} else {
						continue;
					}
				}

			}
		}

		return null;
	}

	/**
	 * Retorna true se o cpf do responsavel for igual ao do servidor fornecido.
	 * 
	 * @param vincResponsavel
	 * @param matResponsavel
	 * @param servidor
	 * @return
	 */
	private boolean verificaCpfResponsavel(Short vincResponsavel,
			Integer matResponsavel, RapServidores servidor) {
		RapServidores responsavel = null;
		if (vincResponsavel != null && matResponsavel != null) {
			responsavel = getRegistroColaboradorFacade().obterServidor(
					vincResponsavel, matResponsavel);
		}
		
		if (responsavel == null){
			LOG.warn("Vinculo/Matricula do servidor responsável não informado ou não foi encontrado.");
			return false;
		}

		if (responsavel.getPessoaFisica().getCpf() != null
				&& responsavel.getPessoaFisica().getCpf()
						.equals(servidor.getPessoaFisica().getCpf())) {
			return true;
		}

		return false;

	}

	/**
	 * Retorna true se o servidor tem qualificação necessária, para assinar os
	 * documentos
	 * 
	 * @param temQualificacao
	 * @param servidor
	 * @return
	 */
	private boolean hasQualificacao(RapServidores servidor) {
		List<RapQualificacao> listaQualificacao = getRegistroColaboradorFacade()

		.pesquisarQualificacoes(servidor.getPessoaFisica());

		// Verifica a existencia de pessoa com curso de graduaçao ou tecnico
		// (ccc_niveis 3 ou 7) regulamentado (tipo CCC)
		for (RapQualificacao qualificacao : listaQualificacao) {
			if ((qualificacao.getTipoQualificacao() != null
					&& qualificacao.getTipoQualificacao().getCccNivelCurso() != null && ((qualificacao
					.getTipoQualificacao().getCccNivelCurso() == (short) 3) || qualificacao
					.getTipoQualificacao().getCccNivelCurso() == (short) 7))
					&& (qualificacao.getTipoQualificacao()
							.getTipoQualificacao() != null && (qualificacao
							.getTipoQualificacao().getTipoQualificacao() == DominioTipoQualificacao.CCC))) {
				return true;
			}
		}

		return false;

	}
	
	/**
	 * Atualiza o envelope criptográfico.
	 * 
	 * @param chave
	 * @param envelope
	 * @throws ApplicationBusinessException
	 * @throws GeneralSecurityException
	 * @throws CMSException
	 */
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void atualizarEnvelope(Integer chave, byte[] envelope) throws Exception {

		try {
			CMSValidator.validate(envelope);
		} catch (GeneralSecurityException e) {
			LOG.error("Erro ao validar envelope criptográfico", e);
			throw e;
		} catch (CMSException e) {
			LOG.error("Erro ao validar envelope criptográfico", e);
			throw e;
		}

		AghVersaoDocumento documento = this.getAghVersaoDocumentoDAO().obterPorChavePrimaria(chave);

		documento.setEnvelope(envelope);
		documento.setSituacao(DominioSituacaoVersaoDocumento.A);

	}

	public TrustAnchorManager getTrustAnchorManager(String caminho) throws FileNotFoundException {
		return new TrustAnchorManager(caminho);
	}

	public void gerarPendenciaAssinatura(byte[] arquivoGerado, Object entidadePai,
			AghDocumentoCertificado documentoCertificado, RapServidores servidorLogado) throws ApplicationBusinessException {
		AghDocumento documento = new AghDocumento();

		documento.setTipo(documentoCertificado.getTipo());

		switch (documentoCertificado.getIdentificador()) {
		case ATD_SEQ:
			if (entidadePai instanceof AghAtendimentos) {
				documento.setAghAtendimento((AghAtendimentos) entidadePai);
			} else if(entidadePai instanceof AelAtendimentoDiversos){
				documento.setAelAtendimentoDiversos((AelAtendimentoDiversos) entidadePai);
			}else{
				LOG.error("Erro ao gerar pendência de assinatura: a entidade pai do documento deve ser uma instância de AghAtendimentos");
				throw new ApplicationBusinessException(AssinaturaDigitalON.AssinaturaDigitalONExceptionCode.ERRO_PENDENCIA_ASSINATURA, "AghAtendimentos");
			}
			break;
		case FIC_SEQ:
			if (entidadePai instanceof MbcFichaAnestesias) {
				documento.setFichaAnestesia((MbcFichaAnestesias) entidadePai);
			} else {
				LOG.error("Erro ao gerar pendência de assinatura: a entidade pai do documento deve ser uma instância de MbcFichaAnestesias");
				throw new IllegalArgumentException(
						"A entidade pai deve ser uma instância de MbcFichaAnestesias");
			}
			break;
		case NPO_SEQ:
			if (entidadePai instanceof MamNotaAdicionalEvolucoes) {
				documento.setNotaAdicionalEvolucao((MamNotaAdicionalEvolucoes) entidadePai);
			} else {
				LOG.error("Erro ao gerar pendência de assinatura: a entidade pai do documento deve ser uma instância de MamNotaAdicionalEvolucoes");
				throw new IllegalArgumentException(
						"A entidade pai deve ser uma instância de MamNotaAdicionalEvolucoes");
			}
			break;
		case CRG_SEQ:
			if (entidadePai instanceof MbcCirurgias) {
				documento.setCirurgia((MbcCirurgias) entidadePai);
			} else {
				LOG.error("Erro ao gerar pendência de assinatura: a entidade pai do documento deve ser uma instância de MbcCirurgias");
				throw new IllegalArgumentException(
						"A entidade pai deve ser uma instância de MbcCirurgias");
			}
			break;
		case AGD_SEQ:
			if (entidadePai instanceof PlanejamentoCirurgicoVO) {
				MbcAgendas agenda = getBlocoCirurgicoFacade().obterAgendaPorChavePrimaria(((PlanejamentoCirurgicoVO) entidadePai).getAgdSeq());
				documento.setAgenda(agenda);
			} else {
				LOG.error("Erro ao gerar pendência de assinatura: a entidade pai do documento deve ser uma instância de MbcAgendas");
				throw new IllegalArgumentException(
						"A entidade pai deve ser uma instância de MbcAgendas");
			}
			break;
		case AIH_SEQ:
			if (entidadePai instanceof MamLaudoAih) {
				documento.setLaudoAih((MamLaudoAih) entidadePai);
			} else {
				LOG.error("Erro ao gerar pendência de assinatura: a entidade pai do documento deve ser uma instância de MamLaudoAih");
				throw new IllegalArgumentException(
						"A entidade pai deve ser uma instância de MamLaudoAih");
			}
			break;
		case LAI_SEQ:
			if (entidadePai instanceof MamLaudoAih) {
				documento.setLaudoAih((MamLaudoAih) entidadePai);
			} else {
				LOG.error("Erro ao gerar pendência de assinatura: a entidade pai do documento deve ser uma instância de MamLaudoAih");
				throw new IllegalArgumentException(
						"A entidade pai deve ser uma instância de MamLaudoAih");
			}
			break;
		default:
			LOG.error("Erro ao gerar pendência de assinatura: identificador inválido");
			throw new IllegalArgumentException(
					"Identificador de tipo do documento inválido");

		}

		AghDocumentoDAO documentoDAO = getAghDocumentoDAO();

		documentoDAO.persistir(documento);

		AghVersaoDocumento versaoDocumento = new AghVersaoDocumento();

		versaoDocumento.setAghDocumentos(documento);
		versaoDocumento.setCriadoEm(new Date());
		versaoDocumento.setOriginal(arquivoGerado);
		versaoDocumento.setServidorResp(servidorLogado);

		versaoDocumento.setSituacao(DominioSituacaoVersaoDocumento.P);

		AghVersaoDocumentoDAO documentoVersaoDAO = this.getAghVersaoDocumentoDAO();

		documentoVersaoDAO.persistir(versaoDocumento);

	}
	
	/**
	 * ORADB: AGHK_CERTIF_DIGITAL.AGHC_HABILITA_CERTIF
	 * 
	 * Verificar se a assinatura digital de documentos está habilitada para uso nos sistemas
	 * 
	 */
	public boolean verificaAssituraDigitalHabilitada() throws ApplicationBusinessException{
//		aghp_get_parametro ('P_HABILITA_CERTIF', p_nome_parametro,'N','S',v_vlr_data, v_vlr_numerico, v_vlr_texto, v_msg);
		
		String vlrHabilitaCertificado = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_HABILITA_CERTIF);
		
		if(String.valueOf('S').equalsIgnoreCase(vlrHabilitaCertificado)){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			verificaPermissaoUsuario(servidorLogado);
			return true;
			/*
			 * 
			 * CURSOR c_servidor (c_matricula In Number, c_vinculo In Number) IS
SELECT   ind_habilita_certif
FROM agh_certificados_digitais
WHERE SER_MATRICULA = c_matricula
  AND SER_VIN_CODIGO = c_vinculo;
			 *  FOR r IN c_servidor(v_matricula, v_vinculo) LOOP
					IF r.ind_habilita_certif = 'S' THEN
				    		v_achou:='S';
				  	END IF;
			 */
		}
		
		return false;
	}
	
	private AghDocumentoDAO getAghDocumentoDAO() {
		return aghDocumentoDAO;
	}
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	public AghVersaoDocumentoDAO getAghVersaoDocumentoDAO() {
		return aghVersaoDocumentoDAO;
	}
	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}