package br.gov.mec.aghu.casca.business;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.StringTokenizer;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.dao.TokensApiDAO;
import br.gov.mec.aghu.casca.dao.UsuarioApiDAO;
import br.gov.mec.aghu.casca.model.TokensApi;
import br.gov.mec.aghu.casca.model.UsuarioApi;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.seguranca.BearerToken;

@Stateless
public class TokenApiON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(TokenApiON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private TokensApiDAO tokensApiDAO;
	
	@Inject
	private UsuarioApiDAO usuarioApiDAO;
	
	private static final long serialVersionUID = -269643020564432693L;
	
	protected enum UsuarioApiONExceptionCode implements BusinessExceptionCode {
		CASCA_MENSAGEM_USUARIO_NAO_INFORMADO, 
		CASCA_MENSAGEM_LOGIN_EXISTENTE, 
		CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO, 
		CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, 
		CASCA_MENSAGEM_USUARIO_INATIVO,
		CASCA_MENSAGEM_USUARIO_SEM_PERFIL, CASCA_MENSAGEM_HORA_DIFERENTE,
		ERRO_AUTENTICACAO, CASCA_MENSAGEM_PERFIL_EXISTENTE,
		CASCA_USUARIO_NOME_EXISTENTE;
	}
	
	public TokensApi gerarTokenAcesso(UsuarioApi usuarioApi, String hostCliente, Integer limiteOperacoes) {
		TokensApi token = new TokensApi();
		token.setUsuarioApi(usuarioApi);
		token.setDataCriacao(new Date());
		token.setDataExpiracao(DateUtils.addMinutes(new Date(), usuarioApi.getTempoTokenMinutos()));
		token.setHostCliente(hostCliente);
		token.setLimiteOperacoes(limiteOperacoes);
		token.setToken(gerarIdentificador(32));
		token.setRefreshToken(gerarIdentificador(32));
		token.setIndRefresh(0);
		tokensApiDAO.persistir(token);
		
		return token;
	}

	public String gerarIdentificador(Integer length) {
		String ret = null;
		try {
			SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
			ret = RandomStringUtils.random(length, 0, 20, true, true, "a1b2c3d4e5f6g7h8i9jklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ*()_+-/[]|".toCharArray(), prng);
		} catch (NoSuchAlgorithmException e) {
			Random prng = new Random();
			ret = RandomStringUtils.random(length, 0, 20, true, true, "a1b2c3d4e5f6g7h8i9jklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ*()_+-/[]|".toCharArray(), prng);
		}
		return ret; 
	}
	
	public BearerToken obterBearerToken(String authInfo, String origem) {
		String cryptoUser = null;
		String cryptoKey = null;
		String cryptoLoginHcpa = null;
		
		BearerToken token = new BearerToken();
		
		token.setAcessToken(null);
		token.setExpirationTime(new Date());
		token.setRefreshToken(null);
		
		if (authInfo == null || !authInfo.contains("|")) {
			token.setAuthInfo("Requisição recebida sem informações de autenticação");
			LOG.info(token.getAuthInfo());
		} else {
			if (!authInfo.startsWith("Bearer ")) {
				token.setTokenType(null);
				token.setAuthInfo("API suporta somente Bearer Tokens. Para maiores informações leia a RFC 6750.");
				LOG.info(token.getAuthInfo());
			} else {
				token.setTokenType("Bearer");
				StringTokenizer st = new StringTokenizer(authInfo.substring(7), "|", false);
				
				while (st.hasMoreTokens()) {
					if (cryptoUser == null) {
						cryptoUser = st.nextToken();
						continue;
					}
					if (cryptoKey == null) {
						cryptoKey = st.nextToken();
						continue;
					}
					if (cryptoLoginHcpa == null) {
						cryptoLoginHcpa = st.nextToken();
						continue;
					}
			    }
				
				if (cryptoUser == null || cryptoKey == null) {
					token.setAuthInfo("Requisição recebida sem authUser e/ou AuthKey");
					LOG.info(token.getAuthInfo());
				} else {
					UsuarioApi usuarioApi = usuarioApiDAO.obterUsuarioApi(cryptoUser);
					if (usuarioApi == null || !usuarioApi.isAtivo() || !Objects.equals(usuarioApi.getAuthKey(), cryptoKey)) {
						token.setAuthInfo("Requisição recebida porem aplicativo eh inexistente, inativo ou possui authKey divergente");
						LOG.info(token.getAuthInfo());
					} else {
						if (tokensApiDAO.contarTokensAtivos(usuarioApi) >= usuarioApi.getLimiteTokensAtivos()) {
							token.setAuthInfo("Requisição recebida porem usuario possui mais de "+usuarioApi.getLimiteTokensAtivos()+" tokens ativos");
							LOG.info(token.getAuthInfo());
						} else {
							TokensApi tokenGerado = this.gerarTokenAcesso(usuarioApi, origem, null);
							token.setAcessToken(tokenGerado.getToken());
							token.setExpirationTime(tokenGerado.getDataExpiracao());
							token.setAuthInfo(null);
							token.setRefreshToken(tokenGerado.getRefreshToken());
							LOG.info("Token concedido a "+usuarioApi.getNome());
						}
					}
				}

			}
		}		
		return token;
	}
	
	public BearerToken refreshBearerToken(String authInfo, String origem) {
		String cryptoRefresh = null;
		
		BearerToken token = new BearerToken();
		
		token.setAcessToken(null);
		token.setExpirationTime(new Date());
		token.setRefreshToken(null);
		
		if (authInfo == null) {
			token.setAuthInfo("Requisição recebida sem informações de refresh");
			LOG.info(token.getAuthInfo());
		} else {
			if (!authInfo.startsWith("Bearer ")) {
				token.setTokenType(null);
				token.setAuthInfo("API suporta somente Bearer Tokens. Para maiores informações leia a RFC 6750.");
				LOG.info(token.getAuthInfo());
			} else {
				token.setTokenType("Bearer");
				cryptoRefresh = authInfo.substring(7);
				
				if (cryptoRefresh == null) {
					token.setAuthInfo("Requisição recebida sem informações de refresh");
					LOG.info(token.getAuthInfo());
				} else {
					TokensApi tokenAntigo = tokensApiDAO.obterTokenAcesso(cryptoRefresh);
					if (tokenAntigo == null) {
						token.setAuthInfo("Token inexistente ou já renovado");
						LOG.info(token.getAuthInfo());
					} else {
						if (!tokenAntigo.getUsuarioApi().isAtivo()) {
							token.setAuthInfo("Requisição recebida para aplicativo está inativo");
							LOG.info(token.getAuthInfo());
						} else {
							
							TokensApi tokenPersist = tokensApiDAO.obterPorChavePrimaria(tokenAntigo.getId());
							if (tokenPersist != null) {
								tokenPersist.setIndRefresh(1);
								tokenPersist.setDataExpiracao(DateUtils.addMinutes(new Date(), tokenAntigo.getUsuarioApi().getTempoTokenMinutos()));
								tokensApiDAO.persistir(tokenAntigo);
								
								token.setAcessToken(tokenPersist.getToken());
								token.setExpirationTime(tokenPersist.getDataExpiracao());								
								LOG.info("Token renovado para "+tokenAntigo.getUsuarioApi().getNome());	
							} else {
								token.setAuthInfo("Token inexistente");
								LOG.info(token.getAuthInfo());
							}
						}
					}
				}
			}
		}
				
		return token;
	}
}