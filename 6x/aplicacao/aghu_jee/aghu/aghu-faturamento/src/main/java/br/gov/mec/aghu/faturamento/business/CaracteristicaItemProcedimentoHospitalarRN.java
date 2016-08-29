package br.gov.mec.aghu.faturamento.business;


import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * <p>
 * Linhas: 161 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 0 <br/>
 * Consultas: 5 tabelas <br/>
 * Alteracoes: 0 tabelas <br/>
 * Metodos: 2 <br/>
 * Metodos externos: 0 <br/>
 * Pontos: 1 <br/>
 * Horas: 7.7 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_CIH_RN</code>
 * </p>
 * @author gandriotti
 *
 */

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class CaracteristicaItemProcedimentoHospitalarRN extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(CaracteristicaItemProcedimentoHospitalarRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

@Inject
private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;

@EJB
private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4155864356493787898L;
	public static final int COBRA_BPA_FAT_TIPO_CARACT_ITEM_ID = 6;
	public static final int COBRA_BPI_FAT_TIPO_CARACT_ITEM_ID = 59;
	public static final int COBRA_SISCOLO_FAT_TIPO_CARACT_ITEM_ID = 8;
	public static final int PROC_PRINCIPAL_APAC_FAT_TIPO_CARACT_ITEM_ID = 5;
	public static final char MAGIC_FAT_TIPO_CARACT_ITEM_VALOR_CHAR_EQ_S = 'S';
		
	protected FatCaractItemProcHospDAO getFatCaractItemProcHospDAO() {
		
		return fatCaractItemProcHospDAO;
	}
	
	/**
	 * <p>
	 * TODO Migrar para DAO<br/>
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_CIH_RN.RN_CIHC_VER_CARAC</code>
	 * </p>
	 * @param cihId
	 * @return
	 * @see FatCaractItemProcHosp
	 */
	public FatCaractItemProcHosp obterCaracteristicaProcHospPorId(
			FatCaractItemProcHospId cihId) {
		
		FatCaractItemProcHosp result = null;
		FatCaractItemProcHospDAO dao = null;
		
		//arg check
		if (cihId == null) {
			throw new IllegalArgumentException("cihId não pode ser nulo.");
		}
		//algo
		dao = this.getFatCaractItemProcHospDAO();
		result = dao.obterPorChavePrimaria(cihId);		
				
		return result;
	}
	
	protected FatCaractItemProcHosp obterFatCaractItemProcHospBPA(FatItensProcedHospitalarId iphId) {
		
		FatCaractItemProcHosp result = null;
		FatCaractItemProcHospDAO dao = null;
		
		dao = this.getFatCaractItemProcHospDAO();
		result = dao.obterPorItemProcHospTipoCaract(
				iphId, 
				Integer.valueOf(COBRA_BPA_FAT_TIPO_CARACT_ITEM_ID));
		
		return result;
	}
	
	protected FatCaractItemProcHosp obterFatCaractItemProcHospBPI(FatItensProcedHospitalarId iphId) {
		
		FatCaractItemProcHosp result = null;
		FatCaractItemProcHospDAO dao = null;
		
		dao = this.getFatCaractItemProcHospDAO();
		result = dao.obterPorItemProcHospTipoCaract(
				iphId, 
				Integer.valueOf(COBRA_BPI_FAT_TIPO_CARACT_ITEM_ID));
		
		return result;
	}
	
	protected FatCaractItemProcHosp obterFatCaractItemProcHospSiscolo(FatItensProcedHospitalarId iphId) {
		
		FatCaractItemProcHosp result = null;
		FatCaractItemProcHospDAO dao = null;
		
		dao = this.getFatCaractItemProcHospDAO();
		result = dao.obterPorItemProcHospTipoCaract(
				iphId, 
				Integer.valueOf(COBRA_SISCOLO_FAT_TIPO_CARACT_ITEM_ID));
		
		return result;
	}
	
	protected FatCaractItemProcHosp obterFatCaractItemProcHospProcPrincApac(FatItensProcedHospitalarId iphId) {
		
		FatCaractItemProcHosp result = null;
		FatCaractItemProcHospDAO dao = null;
		
		dao = this.getFatCaractItemProcHospDAO();
		result = dao.obterPorItemProcHospTipoCaract(
				iphId, 
				Integer.valueOf(PROC_PRINCIPAL_APAC_FAT_TIPO_CARACT_ITEM_ID));
		
		return result;
	}
	
	protected boolean isValorCharEqS(FatCaractItemProcHosp cih) {
		
		boolean result = false; 
		
		result = (cih != null) && (cih.getValorChar() != null)
			&& (cih.getValorChar().charAt(0) 
					== MAGIC_FAT_TIPO_CARACT_ITEM_VALOR_CHAR_EQ_S);
		
		return result;
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_CIH_RN.RN_CIHP_VER_INCOMPAT</code>
	 * </p>
	 * @param iphId
	 * @param tctSeq
	 * @return
	 * @see FaturamentoExceptionCode#FAT_00775
	 * @see FaturamentoExceptionCode#FAT_00776
	 * @see FaturamentoExceptionCode#FAT_00777
	 * @see FaturamentoExceptionCode#FAT_01076
	 * @see FatCaractItemProcHosp
	 */
	public boolean verificaIncompatibilidadeCaracteristicaProcHosp(
			FatItensProcedHospitalarId iphId, 
			Integer tctSeq) throws  ApplicationBusinessException {
		
		boolean result = false;
		FatCaractItemProcHosp cihBPA = null;
		FatCaractItemProcHosp cihBPI = null;
		FatCaractItemProcHosp cihSiscolo = null;
		FatCaractItemProcHosp cihProcPrincApac = null;

		//arg check
		if (iphId == null) {
			throw new IllegalArgumentException("iphId não pode ser nulo.");
		}
		if (tctSeq == null) {
			throw new IllegalArgumentException("tctSeq não pode ser nulo.");
		}
		//algo
		switch (tctSeq.intValue()) {
		case COBRA_BPA_FAT_TIPO_CARACT_ITEM_ID:
			cihBPI = this.obterFatCaractItemProcHospBPI(iphId);
			cihProcPrincApac = this.obterFatCaractItemProcHospProcPrincApac(iphId);
			if (this.isValorCharEqS(cihBPI) 
					|| this.isValorCharEqS(cihProcPrincApac)) {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00775);
			}
			break;
		case COBRA_SISCOLO_FAT_TIPO_CARACT_ITEM_ID:
			cihProcPrincApac = this.obterFatCaractItemProcHospProcPrincApac(iphId);
			if (this.isValorCharEqS(cihProcPrincApac)){
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00776);
		}
			break;
		case PROC_PRINCIPAL_APAC_FAT_TIPO_CARACT_ITEM_ID:
			cihSiscolo = this.obterFatCaractItemProcHospSiscolo(iphId);
			cihBPA = this.obterFatCaractItemProcHospBPA(iphId);
			cihBPI = this.obterFatCaractItemProcHospBPI(iphId);
			if (this.isValorCharEqS(cihSiscolo) 
					|| this.isValorCharEqS(cihBPA) 
					|| this.isValorCharEqS(cihBPI)) {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00777);
			}
			break;
		case COBRA_BPI_FAT_TIPO_CARACT_ITEM_ID:
			cihBPA = this.obterFatCaractItemProcHospBPA(iphId);
			cihProcPrincApac = this.obterFatCaractItemProcHospProcPrincApac(iphId);
			if (this.isValorCharEqS(cihBPA) 
					|| this.isValorCharEqS(cihProcPrincApac)) {
				throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_01076);
			}
			break;
		default:
			this.logInfo("Opcao nao verificada " + tctSeq);
		}
		result = true;
		
		return result;
	}
	
	/**
	 * ORADB FATT_CIH_BRI on FAT_CARACT_ITEM_PROC_HOSP
	 * 
	 * @param FatCaractItemProcHosp
	 * 
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesDeInserirCaracteristicaItemProcedHosp(FatCaractItemProcHosp ciph, final Date dataFimVinculoServidor)throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Date hoje = new Date();
		ciph.setAlteradoEm(hoje);
		ciph.setCriadoEm(hoje);
		ciph.setServidor(servidorLogado);
		
		/*verificar se possui caracteristca incompatível*/
		//Conforme mapeamento ciph.getItemProcedimentoHospitalar() eh not null.
		this.verificaIncompatibilidadeCaracteristicaProcHosp(ciph.getItemProcedimentoHospitalar().getId(),	ciph.getTipoCaracteristicaItem().getSeq());
		
	}

	/**
	 * ORADB FATT_CIH_BRU on FAT_CARACT_ITEM_PROC_HOSP 
	 * @param ciph
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesDeAtualizarCaracteristicaItemProcedHosp(FatCaractItemProcHosp ciph, final Date dataFimVinculoServidor) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ciph.setAlteradoEm(new Date());
		ciph.setServidorAlterado(servidorLogado);
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
