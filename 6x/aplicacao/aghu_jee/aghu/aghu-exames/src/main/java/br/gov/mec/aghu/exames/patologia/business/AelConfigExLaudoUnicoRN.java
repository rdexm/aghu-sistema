package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.exames.dao.AelConfigExLaudoUnicoDAO;
import br.gov.mec.aghu.exames.dao.AelConfigExLaudoUnicoJnDAO;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelConfigExLaudoUnicoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelConfigExLaudoUnicoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelConfigExLaudoUnicoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelConfigExLaudoUnicoJnDAO aelConfigExLaudoUnicoJnDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AelConfigExLaudoUnicoDAO aelConfigExLaudoUnicoDAO;

	private static final long serialVersionUID = -798406697870064652L;

	private enum AelConfigExLaudoUnicoRNCode implements
	BusinessExceptionCode {
		AEL_00353, ERRO_CLONE_CONFIG_EXAME, HA_REGISTROS_DEPENDENTES_CONFIG_EXAMES,
		ERRO_UNICIDADE_SIGLA_CONFIG_EXAME; 
	}

	public AelConfigExLaudoUnico persistir(AelConfigExLaudoUnico config) throws BaseException {
		AelConfigExLaudoUnico confLd;
		assertUnicidadeSigla(config);

		if (config.getSeq() == null) {
			inserir(config);
			confLd = config;
		} else {
			confLd = atualizar(config);
		}
		
		return confLd;
	}

	private void assertUnicidadeSigla(AelConfigExLaudoUnico a)
			throws BaseException {
		AelConfigExLaudoUnico b = 
				getAelConfigExLaudoUnicoDAO().obterPorSigla(a.getSigla());

		if (b != null && !a.equals(b)) {
			throw new BaseException(
					AelConfigExLaudoUnicoRNCode.ERRO_UNICIDADE_SIGLA_CONFIG_EXAME);
		}
	}

	public void inserir(AelConfigExLaudoUnico config) throws BaseException {
		preInserir(config);
		getAelConfigExLaudoUnicoDAO().persistir(config);
	}

	public AelConfigExLaudoUnico atualizar(AelConfigExLaudoUnico config) throws BaseException {
		AelConfigExLaudoUnico configOld = getAelConfigExLaudoUnicoDAO().obterOriginal(config.getSeq());
		getAelConfigExLaudoUnicoDAO().merge(config);
		posAtualizar(config, configOld);
		return config;
	}
	
	public void excluir(Integer seq) throws BaseException {

		try {
			AelConfigExLaudoUnico config = getAelConfigExLaudoUnicoDAO().obterPorChavePrimaria(seq);

			if (config == null) {
				throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
			}
			
			AelConfigExLaudoUnico configOld = getAelConfigExLaudoUnicoDAO().obterOriginal(config.getSeq());
			getAelConfigExLaudoUnicoDAO().remover(config);
			posDelete(configOld);
			getAelConfigExLaudoUnicoDAO().flush();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				final ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new ApplicationBusinessException(AelConfigExLaudoUnicoRNCode.HA_REGISTROS_DEPENDENTES_CONFIG_EXAMES, cve.getConstraintName());
			}
		}
	}

	/**
	 *  @ORADB : AELT_LU2_ARD
	 */
	private void posDelete(AelConfigExLaudoUnico configOld) throws BaseException {
		AelConfigExLaudoUnicoJn jn = getBaseJournal(configOld, DominioOperacoesJournal.DEL);
		getAelConfigExLaudoUnicoJnDAO().persistir(jn);
	}

	/**
	 *  @ORADB : AELT_LU2_ARU
	 */
	private void posAtualizar(AelConfigExLaudoUnico config, AelConfigExLaudoUnico configOld) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(CoreUtil.modificados(config.getSeq(), configOld.getSeq())
				|| CoreUtil.modificados(config.getNome(), configOld.getNome())
				|| CoreUtil.modificados(config.getSigla(), configOld.getSigla())
				|| CoreUtil.modificados(config.getImagem(), configOld.getImagem())
				|| CoreUtil.modificados(config.getMacro(), configOld.getMacro())
				|| CoreUtil.modificados(config.getMicro(), configOld.getMicro())
				|| CoreUtil.modificados(config.getLamina(), configOld.getLamina())
				|| CoreUtil.modificados(config.getTempoAposLib(), configOld.getTempoAposLib())
				|| CoreUtil.modificados(config.getUnidTempoLib(), configOld.getUnidTempoLib())
				|| CoreUtil.modificados(config.getSituacao(), configOld.getSituacao())
				|| CoreUtil.modificados(config.getCriadoEm(), configOld.getCriadoEm())
				|| CoreUtil.modificados(config.getLaudoAnterior(), configOld.getLaudoAnterior())
				|| CoreUtil.modificados(config.getUltimoNumero(), configOld.getUltimoNumero())
				|| CoreUtil.modificados(servidorLogado.getUsuario(), configOld.getServidor().getUsuario()) //em alguns casos dava lazy exception aqui
		) {
			AelConfigExLaudoUnicoJn jn = getBaseJournal(configOld, DominioOperacoesJournal.UPD);
			getAelConfigExLaudoUnicoJnDAO().persistir(jn);
		}
	}
	
	private AelConfigExLaudoUnicoJn getBaseJournal(
			AelConfigExLaudoUnico config, DominioOperacoesJournal op) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelConfigExLaudoUnicoJn jn = BaseJournalFactory.getBaseJournal(op, AelConfigExLaudoUnicoJn.class,servidorLogado.getUsuario());
		
		jn.setSeq(config.getSeq());
		jn.setNome(config.getNome());
		jn.setSigla(config.getSigla());
		jn.setImagem(config.getImagem());
		jn.setMacro(config.getMacro());
		jn.setMicro(config.getMicro());
		jn.setLamina(config.getLamina());
		jn.setTempoAposLib(config.getTempoAposLib());
		jn.setUnidTempoLib(config.getUnidTempoLib());
		jn.setSituacao(config.getSituacao());
		jn.setCriadoEm(config.getCriadoEm());
		jn.setLaudoAnterior(config.getLaudoAnterior());
		jn.setUltimoNumero(config.getUltimoNumero());
		
		jn.setServidor((config.getServidor() != null) ? this
				.getRegistroColaboradorFacade()
				.obterRapServidoresPorChavePrimaria(
						config.getServidor().getId()) : null);
		
		return jn;
	}
	
	/**
	 *  @ORADB : AELT_LU2_BRU
	 */
	// NÃO FAZ NADA
	
	/**
	 *  @ORADB : AELT_LU2_BRI
	 */
	public void preInserir(AelConfigExLaudoUnico config) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelConfigExLaudoUnicoRNCode.AEL_00353);
		}

		config.setImagem((Boolean) CoreUtil.nvl(config.getImagem(), true));
		config.setMicro((Boolean) CoreUtil.nvl(config.getMicro(), true));
		config.setMacro((Boolean) CoreUtil.nvl(config.getMacro(), true));
		config.setLamina((Boolean) CoreUtil.nvl(config.getLamina(), true));
		config.setUltimoNumero(0L);
		config.setCriadoEm(new Date());
		config.setServidor(servidorLogado);
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AelConfigExLaudoUnicoJnDAO getAelConfigExLaudoUnicoJnDAO() {
		return aelConfigExLaudoUnicoJnDAO;
	}
	
	protected AelConfigExLaudoUnicoDAO getAelConfigExLaudoUnicoDAO() {
		return aelConfigExLaudoUnicoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
