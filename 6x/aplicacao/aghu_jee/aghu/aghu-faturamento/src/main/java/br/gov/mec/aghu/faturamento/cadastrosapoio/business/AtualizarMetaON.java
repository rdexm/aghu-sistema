package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.util.Date;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatMetaDAO;
import br.gov.mec.aghu.model.FatMeta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AtualizarMetaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AtualizarMetaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatMetaDAO fatMetaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1427752600076780582L;

	public enum AtualizarMetaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG, MENSAGEM_ERRO_HIBERNATE_VALIDATION, MENSAGEM_ERRO_PERSISTIR_DADOS;
	}

	/**
	 * Atualiza registro em FAT_METAS
	 * 
	 * @param _meta
	 * 		registro a ser alterado.
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMeta(FatMeta _meta, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (_meta == null) {
			throw new ApplicationBusinessException(
					AtualizarMetaONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		_meta.setAlteradoEm(new Date());
		_meta.setServidorAltera(servidorLogado);

		try {
			this.getFatMetaDAO().atualizar(_meta);
			this.getFatMetaDAO().flush();
		} catch (ConstraintViolationException ise) {
			String mensagem = "";
			Set<ConstraintViolation<?>> arr = ise.getConstraintViolations();
			for (ConstraintViolation item : arr) {
				if (!"".equals(item)) {
					mensagem = item.getMessage();
					if (mensagem.isEmpty()) {
						mensagem = " Valor inválido para o campo "
								+ item.getPropertyPath();
					}
				}
			}
			throw new ApplicationBusinessException(
					AtualizarMetaONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);

		} catch (Exception e) {
			throw new ApplicationBusinessException(
					AtualizarMetaONExceptionCode.MENSAGEM_ERRO_PERSISTIR_DADOS,
					e.getMessage());
		}
	}

	/**
	 * Insere um novo registro em FAT_METAS.
	 * 
	 * @param _meta
	 * @param _grupo
	 * @param _subGrupo
	 * @param _formaOrganizacao
	 * @param _financiamento
	 * @param _procedimento
	 * @param _quantidade
	 * @param _valor
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserirMeta(FatMeta _meta, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (_meta == null) {
			throw new ApplicationBusinessException(
					AtualizarMetaONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		_meta.setCriadoEm(new Date());
		_meta.setServidor(servidorLogado);
		
		try{
			this.getFatMetaDAO().persistir(_meta);
			this.getFatMetaDAO().flush();
		} catch (ConstraintViolationException ise) {
			String mensagem = "";
			Set<ConstraintViolation<?>> arr = ise.getConstraintViolations();
			for (ConstraintViolation item : arr) {
				if (!"".equals(item)) {
					mensagem = item.getMessage();
					if (mensagem.isEmpty()) {
						mensagem = " Valor inválido para o campo "
								+ item.getPropertyPath();
					}
				}
			}
			throw new ApplicationBusinessException(
					AtualizarMetaONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);

		} catch (Exception e) {
			throw new ApplicationBusinessException(
					AtualizarMetaONExceptionCode.MENSAGEM_ERRO_PERSISTIR_DADOS,
					e.getMessage());
		}
	}
	
	protected FatMetaDAO getFatMetaDAO() {
		return fatMetaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
