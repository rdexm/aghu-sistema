package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcMotivoAtrasoDAO;
import br.gov.mec.aghu.model.MbcMotivoAtraso;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de BANCO para MBC_MOTIVO_ATRASOS
 * 
 * @author aghu
 * 
 */
@Stateless
public class MbcMotivoAtrasoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcMotivoAtrasoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private MbcMotivoAtrasoDAO mbcMotivoAtrasoDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 5334901747730952415L;

	public enum MbcDestinoPacienteRNExceptionCode implements BusinessExceptionCode {
		MBC_00206, MBC_00210, MBC_MOA_UK1;
	}

	/*
	 * Métodos para PERSISTIR
	 */

	/**
	 * Persistir MbcMotivoAtraso
	 * 
	 * @param motivoAtraso
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void persistirMbcMotivoAtraso(MbcMotivoAtraso motivoAtraso) throws BaseException {
		if (motivoAtraso.getSeq() == null) { // Inserir
			this.inserirMbcMotivoAtraso(motivoAtraso);
		} else { // Atualizar
			this.atualizarMbcMotivoAtraso(motivoAtraso);
		}
	}

	/*
	 * Métodos INSERIR
	 */

	/**
	 * ORADB MBCT_MOA_BRI (INSERT)
	 * 
	 * @param motivoAtraso
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void preInserirMbcMotivoAtraso(MbcMotivoAtraso motivoAtraso) throws BaseException {
		this.verificarMotivoAtrasDescricaoDuplicada(motivoAtraso); // CONSTRAINT MBC_DPA_UK1
		motivoAtraso.setCriadoEm(new Date()); // RN1
		motivoAtraso.setServidor(servidorLogadoFacade.obterServidorLogado()); // RN2: ORADB MBCK_MBC_RN.RN_MBCP_ATU_SERVIDOR
	}

	/**
	 * Inserir MbcMotivoAtraso
	 * 
	 * @param motivoAtraso
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserirMbcMotivoAtraso(MbcMotivoAtraso motivoAtraso) throws BaseException {
		this.preInserirMbcMotivoAtraso(motivoAtraso);
		this.getMbcMotivoAtrasoDAO().persistir(motivoAtraso);
		this.getMbcMotivoAtrasoDAO().flush();
	}

	/*
	 * Métodos ATUALIZAR
	 */

	/**
	 * ORADB TRIGGER MBCT_MOA_BRU (UPDATE)
	 * 
	 * @param motivoAtraso
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void preAtualizarMbcMotivoAtraso(MbcMotivoAtraso motivoAtraso) throws BaseException {

		MbcMotivoAtraso original = getMbcMotivoAtrasoDAO().obterOriginal(motivoAtraso.getSeq());

		// ORADB MBCK_MOA_RN.RN_MOAP_VER_DESC
		if ((motivoAtraso.getDescricao() != null && !motivoAtraso.getDescricao().equals(original.getDescricao()))
				|| (original.getDescricao() != null && !original.getDescricao().equals(motivoAtraso.getDescricao()))) {
			throw new ApplicationBusinessException(MbcDestinoPacienteRNExceptionCode.MBC_00206);
		}

		// ORADB MBCK_MBC_RN.RN_MBCP_VER_UPDATE
		if ((motivoAtraso.getCriadoEm() != null && !motivoAtraso.getCriadoEm().equals(original.getCriadoEm()))
				|| (motivoAtraso.getServidor() != null && !motivoAtraso.getServidor().equals(original.getServidor()))) {
			throw new ApplicationBusinessException(MbcDestinoPacienteRNExceptionCode.MBC_00210);
		}
	}

	/**
	 * Atualizar MbcMotivoAtraso
	 * 
	 * @param motivoAtraso
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void atualizarMbcMotivoAtraso(MbcMotivoAtraso motivoAtraso) throws BaseException {
		this.preAtualizarMbcMotivoAtraso(motivoAtraso);
		this.getMbcMotivoAtrasoDAO().merge(motivoAtraso);
		this.getMbcMotivoAtrasoDAO().flush();
	}

	/*
	 * CONSTRAINTS
	 */

	/**
	 * ORADB CONSTRAINT MBC_MOA_UK1 Evita a gravação de registros com a descrição duplicada
	 * 
	 * @param motivoAtraso
	 * @throws BaseException
	 */
	private void verificarMotivoAtrasDescricaoDuplicada(MbcMotivoAtraso motivoAtraso) throws BaseException {

		final MbcMotivoAtraso destinoPacienteMesmaDescricao = getMbcMotivoAtrasoDAO().pesquisarMotivoAtrasoPorDescricao(motivoAtraso.getDescricao());

		if (destinoPacienteMesmaDescricao != null) {
			throw new ApplicationBusinessException(MbcDestinoPacienteRNExceptionCode.MBC_MOA_UK1);
		}

	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected MbcMotivoAtrasoDAO getMbcMotivoAtrasoDAO() {
		return mbcMotivoAtrasoDAO;
	}

}
