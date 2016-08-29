package br.gov.mec.aghu.casca.autenticacao;

import java.security.Principal;
import java.security.acl.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.PicketBoxMessages;
import org.jboss.security.SecurityConstants;
import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;


/**
 * Classe base para todos o LoginModules do AGHU.
 * 
 * @author geraldo
 * 
 */
public abstract class AghuBaseLoginModule extends UsernamePasswordLoginModule { //implements LoginModule 
	
	private static final Log LOG = LogFactory.getLog(AghuBaseLoginModule.class);
	
	protected static final String ROLE_AGHU_USER = "ROLE_AGHU_USER";
	protected static final String ROLE_AGHU_ADMIN = "ROLE_AGHU_ADMIN";
	
	private static final String DEBUG = "debug";
	private static final String DS_JNDI_NAME = "dsJndiName";
	private static final String VALIDAR_AUTENTICACAO_NEGOCIAL = "validarAutenticacaoNegocial";
	private static final String VERIFICAR_VINCULO_SERVIDOR = "verificarVinculoServidor";

	private static final String[] ALL_VALID_OPTIONS = { DEBUG, DS_JNDI_NAME,
			VALIDAR_AUTENTICACAO_NEGOCIAL, VERIFICAR_VINCULO_SERVIDOR };
	

	// initial state
	//protected Subject subject;
	//protected CallbackHandler callbackHandler;

	// configurable option
	protected boolean debug = false;
	
	// Atributos para validacao Negocial
	protected String dsJndiName;
	protected String validarAutenticacaoNegocial;
	protected String verificarVinculoServidor;


	// the authentication status
	protected boolean succeeded = false;
	protected boolean commitSucceeded = false;

	// username and password
	protected String username;
	protected String password;
	
	private AghuPrincipal aghuIdentity;

	/**
	 * <p>
	 * This method is called if the LoginContext's overall authentication
	 * failed. (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules did not succeed).
	 * 
	 * <p>
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> and
	 * <code>commit</code> methods), then this method cleans up any state that
	 * was originally saved.
	 * 
	 * <p>
	 * 
	 * @exception LoginException
	 *                if the abort fails.
	 * 
	 * @return false if this LoginModule's own login and/or commit attempts
	 *         failed, and true otherwise.
	 */
	@Override
	public boolean abort() throws LoginException {
		if (succeeded == false) {
			return false;
		} else if (succeeded == true && commitSucceeded == false) {
			// login succeeded but overall authentication failed
			succeeded = false;
			username = null;
			password = null;
			aghuIdentity = null;
		} else {
			// overall authentication succeeded and commit succeeded,
			// but someone else's commit failed
			logout();
		}
		return true;
	}
	
	
	/** Utility method to create a Principal for the given username. This
	 * creates an instance of the principalClassName type if this option was
	 * specified using the class constructor matching: ctor(String). If
	 * principalClassName was not specified, a SimplePrincipal is created.
	 *
	 * @param username the name of the principal
	 * @return the principal instance
	 * @throws java.lang.Exception thrown if the custom principal type cannot be created.
	 */ 
	@Override
	protected Principal createIdentity(String username) throws Exception { //NOPMD SignatureDeclareThrowsException
		return createAghuIdentity(username); 
	}
	
	private AghuPrincipal createAghuIdentity(String username) {
		return new AghuPrincipal(username); 
	}
	
	@Override
	protected Principal getIdentity() {
		return aghuIdentity;
	}
	
	@Override
	protected Group[] getRoleSets() throws LoginException {
	    Group[] groups = new Group[2];
	    
	    Group callerPrincipal = new SimpleGroup(SecurityConstants.CALLER_PRINCIPAL_GROUP);
		callerPrincipal.addMember(this.getIdentity());
	    
		groups[0] = callerPrincipal;
	    subject.getPrincipals().add(callerPrincipal);
	    
	    Group roles = new SimpleGroup(SecurityConstants.ROLES_IDENTIFIER);
	    roles.addMember(new SimpleGroup(ROLE_AGHU_USER));
	    
	    groups[1] = roles;
	    subject.getPrincipals().add(roles);
	    
	    return groups;
	}
	
	/**
	 * <p>
	 * This method is called if the LoginContext's overall authentication
	 * succeeded (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules succeeded).
	 * 
	 * <p>
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> method),
	 * then this method associates a <code>SamplePrincipal</code> with the
	 * <code>Subject</code> located in the <code>LoginModule</code>. If this
	 * LoginModule's own authentication attempted failed, then this method
	 * removes any state that was originally saved.
	 * 
	 * <p>
	 * 
	 * @exception LoginException
	 *                if the commit fails.
	 * 
	 * @return true if this LoginModule's own login and commit attempts
	 *         succeeded, or false otherwise.
	 */
	@Override
	public boolean commit() throws LoginException {
		if (succeeded == false) {
			return false;
		} else {
			// add a Principal (authenticated identity) to the Subject
			// assume the user we authenticated is the SamplePrincipal
		    // criar e setar o principal createIdentity(this.username)
			if (!subject.getPrincipals().contains(this.getIdentity())) {
				subject.getPrincipals().add(this.getIdentity());
			}

			if (debug && LOG.isDebugEnabled()) {
				LOG.debug("added SamplePrincipal to Subject");
			}

			// in any case, clean out state
			username = null;
			password = null;

			commitSucceeded = true;
			return true;
		}
	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		LOG.info("Login Module - initialize.");
		super.addValidOptions(ALL_VALID_OPTIONS);
		super.initialize(subject, callbackHandler, sharedState, options);

		// initialize any configured options
		debug = "true".equalsIgnoreCase((String) options.get(DEBUG));

		dsJndiName = (String) options.get(DS_JNDI_NAME);
		validarAutenticacaoNegocial = (String) options
				.get(VALIDAR_AUTENTICACAO_NEGOCIAL);
		verificarVinculoServidor = (String) options
				.get(VERIFICAR_VINCULO_SERVIDOR);
	}

	/**
	 * Authenticate the user by prompting for a user name and password.
	 * 
	 * <p>
	 * 
	 * @return true in all cases since this <code>LoginModule</code> should not
	 *         be ignored.
	 * 
	 * @exception javax.security.auth.login.FailedLoginException
	 *                if the authentication fails.
	 *                <p>
	 * 
	 * @exception LoginException
	 *                if this <code>LoginModule</code> is unable to perform the
	 *                authentication.
	 */
	@Override
	public boolean login() throws LoginException {
		LOG.info("Login Module - login.");
		// prompt for a user name and password
		if (callbackHandler == null) {
			throw new LoginException("Error: no CallbackHandler available "
					+ "to garner authentication information from the user");
		}

		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("user name: ");
		callbacks[1] = new PasswordCallback("password: ", false);

		try {
			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();
			char[] tmpPassword = ((PasswordCallback) callbacks[1])
					.getPassword();
			if (tmpPassword == null) {
				// treat a NULL password as an empty password
				tmpPassword = new char[0];
			}

			password = String.valueOf(tmpPassword);

			((PasswordCallback) callbacks[1]).clearPassword();
			
			this.aghuIdentity = createAghuIdentity(this.username);
		} catch (java.io.IOException ioe) {
			throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException uce) {
			throw new LoginException("Error: " + uce.getCallback().toString()
					+ " not available to garner authentication information "
					+ "from the user");
		}

		// print debugging information
		if (debug && LOG.isDebugEnabled()) {
			LOG.debug("user entered user name: " + username);
			LOG.debug("user entered password: ");
			if (password != null && password.length() > 0) {
				LOG.debug("password: *********************");
			} else {
				LOG.debug("password: nao informou password");
			}
		}
		
		LOG.info("Login Module - login - done!");
		return this.autenticar();
	}

	protected boolean autenticar() throws LoginException {
		LOG.info("Login Module - autenticar.");
		try {

			this.efetuarAutenticacao();
			this.executarAutenticacaoNegocial();
			
			this.getRoleSets();
		} catch (LoginException e) {
			succeeded = false;
			this.username = null;
			this.password = null;
			throw e;
		}

		succeeded = true;
		return true;

	}

	protected abstract void efetuarAutenticacao() throws LoginException;

	/**
	 * Logout the user.
	 * 
	 * <p>
	 * This method removes the <code>SamplePrincipal</code> that was added by
	 * the <code>commit</code> method.
	 * 
	 * <p>
	 * 
	 * @exception LoginException
	 *                if the logout fails.
	 * 
	 * @return true in all cases since this <code>LoginModule</code> should not
	 *         be ignored.
	 */
	@Override
	public boolean logout() throws LoginException {

		subject.getPrincipals().remove(aghuIdentity);
		succeeded = false;
		succeeded = commitSucceeded;
		username = null;
		password = null;
		aghuIdentity = null;
		return true;
	}

	/**
	 * Método usado para encapsular a autenticacao negocial do aghu.
	 * 
	 * @throws LoginException
	 */
	public void executarAutenticacaoNegocial() throws LoginException {
		LOG.info("Login Module - executarAutenticacaoNegocial.");
		
		if (this.dsJndiName == null) {
			throw new LoginException("Validacao negocial nao informada na configuracao inicial: dsJndiName.");			
		}
		if (this.validarAutenticacaoNegocial == null) {
			throw new LoginException("Validacao negocial nao informada na configuracao inicial: validarAutenticacaoNegocial.");
		}		
		if (this.verificarVinculoServidor == null) {
			throw new LoginException("Validacao negocial nao informada na configuracao inicial: verificarVinculoServidor.");
		}
		
		if (this.username == null || "".equals(this.username)) {
			throw new LoginException("Login nao informado.");			
		}
		
		LOG.info("Login Module - executarAutenticacaoNegocial - campos obrigatorios.");
		
		validarAutenticacaoNegocial(username, dsJndiName, validarAutenticacaoNegocial);
		verificarVinculoServidor(username, dsJndiName, verificarVinculoServidor);
		
		LOG.info("Login Module - executarAutenticacaoNegocial - done.");
	}
	
	/**
	 * ORADB: Procedure RAPK_SER_RN.RN_SERP_VER_VINCULO
	 * Chama validações para averiguar se o vínculo do servidor ainda é válido.
	 * 
	 * verificarVinculoServidor - Retorna um servidor ativo no sistema conforme o login do usuário
	 *                         e validada para averiguar se o vínculo do servidor ainda é válido.
	 * 
	 * select serv.matricula, serv.vin_codigo, serv.dt_inicio_vinculo
	 * , serv.CCT_CODIGO
	 * , serv.HTR_CODIGO
	 * , serv.OCA_CAR_CODIGO, serv.OCA_CODIGO
	 * , vin.IND_SITUACAO -- <> A MENSAGEM_VINCULO_INATIVO
	 * , vin.IND_CCUST_LOTACAO -- S MENSAGEM_INFORMAR_CCUSTO_LOTACAO
	 * , vin.IND_HORARIO -- S MENSAGEM_INFORMAR_HORARIO
	 * , vin.IND_OCUPACAO  -- S MENSAGEM_INFORMAR_OCUPACAO
	 * , vin.NRO_DIAS_ADMISSAO
	 * from agh.RAP_SERVIDORES serv
	 * inner join agh.RAP_PESSOAS_FISICAS pes on pes.codigo = serv.PES_CODIGO
	 * inner join agh.RAP_VINCULOS vin on vin.codigo = serv.vin_codigo
	 * where usuario = upper('rcorvalao')
	 * and (
	 *    serv.ind_situacao = 'A'
	 *    or (serv.ind_situacao = 'P' and serv.dt_fim_vinculo >= now())
	 * )
	 *	
	 *  
	 * @param user
	 * @param ds
	 * @param sqlValidacao
	 * @throws LoginException
	 */
	@SuppressWarnings("PMD.UnusedFormalParameter")
	private void verificarVinculoServidor(String user, String dsName, String sqlValidacao) throws LoginException {
		LOG.info("Login Module - verificarVinculoServidor.");
		
		LOG.debug("user: " + user);
		LOG.debug("ds: " + dsName);
		LOG.debug("validacao: " + sqlValidacao);
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			// buscar usuario ativo
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(dsName);
			conn = ds.getConnection();
			ps = conn.prepareStatement(sqlValidacao);
			ps.setString(1, user);
			rs = ps.executeQuery();
			 
			//busca de servidor ativo pelo user.
			if (!rs.next()) {
				throw new LoginException("Nenhum Servidor do HU encontrado associado ao usuário " + user);			
			}
			
			// , vin.IND_SITUACAO -- <> A MENSAGEM_VINCULO_INATIVO
			String indSituacao = rs.getString("IND_SITUACAO");
			if (!"A".equalsIgnoreCase(indSituacao)) {
				throw new LoginException("Vínculo informado não está ativo.");
			}

			// , vin.IND_CCUST_LOTACAO -- S MENSAGEM_INFORMAR_CCUSTO_LOTACAO
			String indCustLotacao = rs.getString("IND_CCUST_LOTACAO");
			Object cttCodigo = rs.getObject("CCT_CODIGO");
			if ("S".equalsIgnoreCase(indCustLotacao)
					&& cttCodigo == null) {
				throw new LoginException("É obrigatório informar centro de custo de lotação para este tipo de vínculo.");
			}
			
			// , vin.IND_HORARIO -- S MENSAGEM_INFORMAR_HORARIO
			String indHorario = rs.getString("IND_HORARIO");
			Object htrCodigo = rs.getObject("HTR_CODIGO");
			if ("S".equalsIgnoreCase(indHorario)
					&& htrCodigo == null) {
				throw new LoginException("É obrigatório informar horário para este vínculo.");
			}

			// , vin.IND_OCUPACAO  -- S MENSAGEM_INFORMAR_OCUPACAO
			String indOcupacao = rs.getString("IND_HORARIO");
			Object ocaCarCodigo = rs.getObject("OCA_CAR_CODIGO");
			Object ocaCodigo = rs.getObject("OCA_CODIGO");
			if ("S".equalsIgnoreCase(indOcupacao)
					&& (ocaCarCodigo == null || ocaCodigo == null)) {
				throw new LoginException("É obrigatório informar ocupação para este tipo de vínculo.");
			}
			
			// dt_inicio_vinculo
			Date dataAtual = getDataAtualHoraZerada();
			Date dtInicioVin = rs.getDate("dt_inicio_vinculo");
			if (dtInicioVin.after(dataAtual)) {
				// vin.NRO_DIAS_ADMISSAO
				Integer nroDiasAdmissao = rs.getInt("NRO_DIAS_ADMISSAO");
				
				if (nroDiasAdmissao != null && nroDiasAdmissao > 0) {
					Date dataAtualDiasAdicionado = addDias(dataAtual, nroDiasAdmissao);
					
					if (dtInicioVin.after(dataAtualDiasAdicionado)) {
						throw new LoginException("Data de início de vínculo superior ao permitido pelo vínculo.");
					}
				} else {
					throw new LoginException("Não é permitido data de início do vínculo posterior a data atual.");
				}
			}
			
			
			
		} catch(NamingException ex) {
			LoginException le = new LoginException(PicketBoxMessages.MESSAGES.failedToLookupDataSourceMessage(dsJndiName));
			le.initCause(ex);
			throw le;
		} catch(SQLException ex) {
			LoginException le = new LoginException(PicketBoxMessages.MESSAGES.failedToProcessQueryMessage());
			le.initCause(ex);
			throw le;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch(SQLException e) {}//NOPMD
			}
			if ( ps != null ) {
				try {
					ps.close();
				} catch(SQLException e) {}//NOPMD
			}
			if ( conn != null ) {
				try {
					conn.close();
				} catch (SQLException ex) {}//NOPMD
			}		
		}
		
		LOG.info("Login Module - verificarVinculoServidor - done.");
	}
	
	private Date addDias(Date dataAtual, Integer nroDiasAdmissao) {
		Calendar calAtual = Calendar.getInstance();
		
		calAtual.setTime(dataAtual);
		
		calAtual.add(Calendar.DAY_OF_MONTH, nroDiasAdmissao);
		
		return calAtual.getTime();
	}


	private Date getDataAtualHoraZerada() {
		Calendar calendarAtual = Calendar.getInstance();
		
		calendarAtual.set(Calendar.HOUR_OF_DAY, 0);
		calendarAtual.set(Calendar.MINUTE, 0);
		calendarAtual.set(Calendar.SECOND, 0);
		calendarAtual.set(Calendar.MILLISECOND, 0);
		
		return calendarAtual.getTime();
	}


	/**
	 * validarAutenticacaoNegocial - usuario ativo e com perfils.
	 *  
	 * SQL de exemplo:
	 * select usuario.id, usuario.login, usuario.ATIVO , usuario.id
	 * , (
	 * select count(*)
	 * from casca.CSC_PERFIS_USUARIOS perfilusuario
	 * inner join casca.CSC_PERFIL perfil on perfil.id = perfilusuario.id_perfil
	 * where perfilusuario.id_usuario = usuario.id
	 * and (perfilusuario.dthr_expiracao is null or perfilusuario.dthr_expiracao > now())
	 * and perfil.SITUACAO = 'A'
	 * ) perfilcount
	 * from CASCA.CSC_USUARIO usuario
	 * where login = upper('rcorvalao')
	 *  
	 * 
	 * @param user
	 * @param ds
	 * @param sqlValidacao
	 * @throws LoginException
	 */
	private void validarAutenticacaoNegocial(String user, String dsName, String sqlValidacao) throws LoginException {
		LOG.info("Login Module - validarAutenticacaoNegocial.");
		
		LOG.debug("user: " + user);
		LOG.debug("ds: " + dsName);
		LOG.debug("validacao: " + sqlValidacao);
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			// buscar usuario ativo
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(dsName);
			conn = ds.getConnection();
			ps = conn.prepareStatement(sqlValidacao);
			ps.setString(1, user);
			rs = ps.executeQuery();
			 
			if (!rs.next()) {
				throw new LoginException("Este usuário não foi encontrado no sistema: " + user);			
			}
			
			boolean ususarioAtivo = rs.getBoolean("ATIVO");
			if (!ususarioAtivo) {
				throw new LoginException("Este usuário está inativo no sistema: " + user);			
			}
			
			Long perfilCount = rs.getLong("perfilcount");
			if (!(perfilCount > 0)) {
				throw new LoginException("Usuário [" + user + "] não tem perfil associado ou todos seus perfis estão inativos.");			
			}
			
		} catch(NamingException ex) {
			LoginException le = new LoginException(PicketBoxMessages.MESSAGES.failedToLookupDataSourceMessage(dsJndiName));
			le.initCause(ex);
			throw le;
		} catch(SQLException ex) {
			LoginException le = new LoginException(PicketBoxMessages.MESSAGES.failedToProcessQueryMessage());
			le.initCause(ex);
			throw le;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch(SQLException e) {}//NOPMD
			}
			if ( ps != null ) {
				try {
					ps.close();
				} catch(SQLException e) {}//NOPMD
			}
			if ( conn != null ) {
				try {
					conn.close();
				} catch (SQLException ex) {}//NOPMD
			}		
		}
		
		LOG.info("Login Module - validarAutenticacaoNegocial - done.");
	}
	
	@Override
	protected String getUsersPassword() throws LoginException {
		return this.password;
	}
	
}