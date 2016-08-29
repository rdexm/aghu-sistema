package br.gov.mec.aghu.core.seguranca;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.criptografia.CriptografiaUtil;

/**
 * Token de autenticacao utilizado pelo TokenIdentity contendo informacoes do
 * usuario autenticado no AGH.<br />
 * 
 * -Dbr.gov.mec.aghu.core.seguranca.Token.timeout
 * 
 * @author JoseAntonio
 * 
 */
public class Token {
	
	private static final Log LOG = LogFactory.getLog(Token.class);	

	/**
	 * Default timeout para expiracao do token. O timeout deve ser grande o suficiente
	 * para suportar a diferenca entre os horarios do ambiente Oracle e JBoss
	 */
	private static final int DEFAULT_TIMEOUT = 60 * 5;

	private static final String SYSTEM_PROPERTY_TIMEOUT = "br.gov.mec.aghu.core.seguranca.Token.timeout";

	private static int timeout = DEFAULT_TIMEOUT;
		
	private String username;
	
	private String encryptedPassword;
	
	private Boolean proxyUser = false;

	private Date date = new Date();

	private String tokenString;
	
	static {
		timeout = Integer.getInteger(SYSTEM_PROPERTY_TIMEOUT, DEFAULT_TIMEOUT).intValue();
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date timestamp) {
		this.date = timestamp;
	}
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public Boolean getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(Boolean proxyUser) {
		this.proxyUser = proxyUser;
	}	

	public String getTokenString() {
		return tokenString;
	}

	public void setTokenString(String tokenString) {
		this.tokenString = tokenString;
	}

	/**
	 * Retorna a data de expiracao deste token, baseado no parametro TIMEOUT.
	 * 
	 * @return da data de expiracao do token
	 */
	public Date getDataExpiracao() {
		Calendar c = Calendar.getInstance();
		c.setTime(getDate());
		c.add(Calendar.SECOND, timeout);

		return c.getTime();
	}

	/**
	 * Verifica se o token nao expirou
	 * 
	 * @return true se o token ainda nao expirou
	 */
	public boolean isValid() {
		return getDataExpiracao().after(new Date());
	}

	/**
	 * Le a string criptografada e retorna um objeto Token contendo as
	 * informacoes.
	 * 
	 * @return O objeto token com os valores extraidos da string criptografada
	 * @throws TokenIdentityException
	 */
	public static Token createToken(String encryptedString)
			throws TokenIdentityException {
		
		try {
			
			String decryptedString = CriptografiaUtil.descriptografar(encryptedString);

			// Cria o token
			Token token = new Token();

			StringTokenizer tokenizer = new StringTokenizer(decryptedString, "|");
			
			// É esperado um token no seguinte formato:
			// FLAG_PROXY_USER|TIMESTAMP|USUARIO|SENHA_CRIPTOGRAFADA
			if (tokenizer.countTokens() < 4) {
				throw new TokenIdentityException("Token tem apenas "+tokenizer.countTokens()+" parâmetros quando deveria ter 4.");
			}
			
			Boolean proxyUser = Boolean.FALSE;
			String proxyUserString = tokenizer.nextToken();			
			if (proxyUserString != null && proxyUserString.equalsIgnoreCase("S")) {
				proxyUser = Boolean.TRUE;
			}
			token.setProxyUser(proxyUser);			
			token.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(tokenizer.nextToken()));
			token.setUsername(tokenizer.nextToken());
			token.setEncryptedPassword(tokenizer.nextToken());
			
			token.setTokenString(encryptedString);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Token.data: "+token.getDate());
				LOG.debug("Token.flagProxyUser: "+token.getProxyUser());
				LOG.debug("Token.usuario: "+token.getUsername());
				LOG.debug("Token.senhaCriptografada: "+token.getEncryptedPassword());
			}

			return token;
		} catch (Exception e) {
			throw new TokenIdentityException("Erro ao extrair token", e);
		}
	}
	
	
	public static String createToken(String login, String encryptedPassword) throws TokenIdentityException {
    	Token token = new Token();
    	token.setDate(new Date());
		token.setUsername(login);
		if (encryptedPassword != null && !encryptedPassword.isEmpty()) {
			token.setEncryptedPassword(encryptedPassword);
		}
		
		String encryptedStr = token.encrypt(); 
    	
    	try {
			return CoreUtil.encodeURL(encryptedStr);
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		}
		
		return encryptedStr;
    }

	/**
	 * Cria uma string criptografada que representa este objeto
	 * 
	 * @return uma string criptografada deste objeto
	 * @throws TokenIdentityException
	 */
	public String encrypt() throws TokenIdentityException {
		try {
			
			if (getProxyUser() == null) {
				throw new TokenIdentityException("Flag proxy user não informado.");
			}
			
			if (getDate() == null) {
				throw new TokenIdentityException("Data não informada.");
			}
			
			if (getUsername() == null) {
				throw new TokenIdentityException("Usuário não informado.");
			}
			
			if (getEncryptedPassword() == null) {
				throw new TokenIdentityException("Senha criptografada não informada.");
			}			

			// Monta uma string no formato: FLAG_PROXY_USER|TIMESTAMP|USUARIO|SENHA_CRIPTOGRAFADA
			StringBuffer sbTokenString = new StringBuffer(String.format("%s|%s|%s|%s", 
					getProxyUser() ? "S" : "N",
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate()),
					getUsername(),
					getEncryptedPassword()));

			return CriptografiaUtil.criptografar(sbTokenString.toString());
		} catch (Exception e) {
			throw new TokenIdentityException("Erro ao encriptografar token", e);
		}
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Token)) {
			return false;
		}

		Token t = (Token) obj;

		return t.getTokenString().equals(this.getTokenString());
	}

	@Override
	public int hashCode() {
		return getTokenString().hashCode();
	}
}
