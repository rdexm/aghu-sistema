package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcDestinoPacienteDAO;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de BANCO para MBC_DESTINO_PACIENTES
 * 
 * @author aghu
 * 
 */
@Stateless
public class MbcDestinoPacienteRN extends BaseBusiness {


	private static final Log LOG = LogFactory.getLog(MbcDestinoPacienteRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDestinoPacienteDAO mbcDestinoPacienteDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2269555460074320735L;

	public enum MbcDestinoPacienteRNExceptionCode implements BusinessExceptionCode {
		MBC_00215, MBC_00216, MBC_DPA_UK1;
	}

	/*
	 * Métodos para PERSISTIR
	 */

	/**
	 * Persistir MbcDestinoPaciente
	 * 
	 * @param destinoPaciente
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void persistirMbcDestinoPaciente(MbcDestinoPaciente destinoPaciente) throws BaseException {
		if (destinoPaciente.getSeq() == null) { // Inserir
			this.inserirMbcDestinoPaciente(destinoPaciente);
		} else { // Atualizar
			this.atualizarMbcDestinoPaciente(destinoPaciente);
		}
	}

	/*
	 * Métodos INSERIR
	 */

	/**
	 * ORADB TRIGGER MBCT_TAN_BRI (INSERT)
	 * 
	 * @param destinoPaciente
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void preInserirMbcDestinoPaciente(MbcDestinoPaciente destinoPaciente) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.verificarDestinoPacienteDescricaoDuplicada(destinoPaciente); // CONSTRAINT MBC_DPA_UK1
		destinoPaciente.setCriadoEm(new Date()); // RN1
		destinoPaciente.setServidor(servidorLogado); // RN2: ORADB MBCK_MBC_RN.RN_MBCP_ATU_SERVIDOR
	}

	/**
	 * Inserir MbcDestinoPaciente
	 * 
	 * @param destinoPaciente
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserirMbcDestinoPaciente(MbcDestinoPaciente destinoPaciente) throws BaseException {
		this.preInserirMbcDestinoPaciente(destinoPaciente);
		this.getMbcDestinoPacienteDAO().persistir(destinoPaciente);
		this.getMbcDestinoPacienteDAO().flush();
	}

	/*
	 * Métodos ATUALIZAR
	 */

	/**
	 * ORADB TRIGGER MBCT_DPA_BRU (UPDATE)
	 * 
	 * @param destinoPaciente
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void preAtualizarMbcDestinoPaciente(MbcDestinoPaciente destinoPaciente) throws BaseException {
		MbcDestinoPaciente original = getMbcDestinoPacienteDAO().obterOriginal(destinoPaciente.getSeq());

		// ORADB BCK_DPA_RN.RN_DPAP_VER_DESC
		if ((destinoPaciente.getDescricao() != null && !destinoPaciente.getDescricao().equals(original.getDescricao()))
				|| (original.getDescricao() != null && !original.getDescricao().equals(destinoPaciente.getDescricao()))) {
			throw new ApplicationBusinessException(MbcDestinoPacienteRNExceptionCode.MBC_00215);
		}

		// ORADB MBCK_MBC_RN.RN_MBCP_VER_UPDATE
		if ((destinoPaciente.getCriadoEm() != null && !destinoPaciente.getCriadoEm().equals(original.getCriadoEm()))
				|| (destinoPaciente.getServidor() != null && !destinoPaciente.getServidor().equals(original.getServidor()))) {
			throw new ApplicationBusinessException(MbcDestinoPacienteRNExceptionCode.MBC_00216);
		}
	}

	/**
	 * Atualizar MbcDestinoPaciente
	 * 
	 * @param destinoPaciente
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void atualizarMbcDestinoPaciente(MbcDestinoPaciente destinoPaciente) throws BaseException {
		this.preAtualizarMbcDestinoPaciente(destinoPaciente);
		this.getMbcDestinoPacienteDAO().merge(destinoPaciente);
		this.getMbcDestinoPacienteDAO().flush();
	}

	/*
	 * CONSTRAINTS
	 */

	/**
	 * ORADB CONSTRAINT MBC_DPA_UK1 Evita a gravação de registros com a descrição duplicada
	 * 
	 * @param destinoPaciente
	 * @throws BaseException
	 */
	private void verificarDestinoPacienteDescricaoDuplicada(MbcDestinoPaciente destinoPaciente) throws BaseException {

		final MbcDestinoPaciente destinoPacienteMesmaDescricao = getMbcDestinoPacienteDAO().pesquisarDestinoPacientePorDescricao(destinoPaciente.getDescricao());

		if (destinoPacienteMesmaDescricao != null) {
			throw new ApplicationBusinessException(MbcDestinoPacienteRNExceptionCode.MBC_DPA_UK1);
		}

	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	private MbcDestinoPacienteDAO getMbcDestinoPacienteDAO() {
		return mbcDestinoPacienteDAO;
	}
}
