package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


@Stateless
public class ValorItemProcedimentoRN extends BaseBusiness implements Serializable {

	@EJB
	private ValorItemProcedHospCompsRN valorItemProcedHospCompsRN;
	
	private static final Log LOG = LogFactory.getLog(ValorItemProcedimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3021148750853976555L;

	/**
	 * ORADB Trigger FATT_IPC_BRI Chamada antes de inserir uma linha na tabela
	 * FAT_VLR_ITEM_PROCED_HOSP_COMPS
	 * 
	 * @param FatVlrItemProcedHospComps
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void processarAntesInserirFattIpcBri(final FatVlrItemProcedHospComps vlrItemProcedHospComps) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		vlrItemProcedHospComps.setAlteradoEm(getSysDate());
		vlrItemProcedHospComps.setCriadoEm(getSysDate());
		vlrItemProcedHospComps.setAlteradoPor(servidorLogado.getUsuario());
		vlrItemProcedHospComps.setCriadoPor(servidorLogado.getUsuario());
	}

	// método para facilitar testes
	protected Date getSysDate() {
		return new Date();
	}

	/**
	 * ORADB Trigger FATT_IPC_ASU Chamada antes de atualizar uma linha na tabela
	 * FAT_VLR_ITEM_PROCED_HOSP_COMPS;
	 * 
	 * @param FatVlrItemProcedHospComps
	 * @throws BaseException
	 */
	public void processarAntesAtualizarFattIpcAsu(final FatVlrItemProcedHospComps vlrItemProcedHospComps) throws BaseException {
		this.fatpEnforceIpcRules(vlrItemProcedHospComps, DominioOperacoesJournal.UPD);
	}

	/**
	 * ORADB Trigger FATT_IPC_BRU Chamada antes de atualizar uma linha na tabela
	 * FAT_VLR_ITEM_PROCED_HOSP_COMPS;
	 * 
	 * @param FatVlrItemProcedHospComps
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void processarAntesAtualizarFattIpcBru(final FatVlrItemProcedHospComps vlrItemProcedHospComps) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		vlrItemProcedHospComps.setAlteradoEm(getSysDate());
		vlrItemProcedHospComps.setAlteradoPor(servidorLogado.getUsuario().toUpperCase());
	}

	/**
	 * ORADB Trigger FATT_IPC_ASI Chamada após inserir na tabela
	 * FAT_VLR_ITEM_PROCED_HOSP_COMPS;
	 * 
	 * @param FatVlrItemProcedHospComps
	 * @throws BaseException
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void processarAposInserirFattIpcAsi(final FatVlrItemProcedHospComps vlrItemProcedHospComps) throws BaseException {
		this.fatpEnforceIpcRules(vlrItemProcedHospComps, DominioOperacoesJournal.INS);
	}

	/**
	 * ORADB Trigger FATP_ENFORCE_IPC_RULES Realiza a chamada à implementação de
	 * FATK_IPC_RN.RN_IPCP_ATU_VLR_PESP
	 * 
	 * @param FatVlrItemProcedHospComps
	 * @throws BaseException
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void fatpEnforceIpcRules(final FatVlrItemProcedHospComps vlrItemProcedHospComps, DominioOperacoesJournal operacao) throws BaseException {
		getValorItemProcedHospCompsRN().atualizarVlrResp(vlrItemProcedHospComps, operacao);
	}

	protected ValorItemProcedHospCompsRN getValorItemProcedHospCompsRN() {
		return valorItemProcedHospCompsRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
