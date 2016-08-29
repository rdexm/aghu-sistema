package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirurgicoDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcEquipamentoCirurgicoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcEquipamentoCirurgicoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcEquipamentoCirurgicoDAO mbcEquipamentoCirurgicoDAO;


	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade iCadastrosBasicosPrescricaoMedicaFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8544695301802426034L;

	public enum MbcEquipamentoCirurgicoRNExceptionCode implements
			BusinessExceptionCode {
		MBC_00251,//
		;
	}
	
	
	/**
	 * Atualiza um registro em<br>
	 * MBC_EQUIPAMENTO_CIRURGICOS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void atualizar(MbcEquipamentoCirurgico elemento) throws BaseException {
		this.preAtualizar(elemento);
		this.getMbcEquipamentoCirurgicoDAO().atualizar(elemento);
	}
	
	
	
	/**
	 * Insere um registro em<br>
	 * MBC_EQUIPAMENTO_CIRURGICOS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void inserir(MbcEquipamentoCirurgico elemento) throws BaseException {
		this.preInserir(elemento);
		this.getMbcEquipamentoCirurgicoDAO().persistir(elemento);
		this.getMbcEquipamentoCirurgicoDAO().flush();
		this.posInserir(elemento);
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_TAN_BRI
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void preInserir(MbcEquipamentoCirurgico elemento) throws BaseException {
		//RN1
		elemento.setCriadoEm(new Date());
		//RN2
		this.verificarSituacaoEquipamentoCirurgico(elemento);
		//RN3
		elemento.setRapServidores(servidorLogadoFacade.obterServidorLogado());
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_EUU_ARI
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void posInserir(MbcEquipamentoCirurgico elemento) throws BaseException {
		this.getCadastrosBasicosPrescricaoMedicaFacade().criarFatProcedHospInternos(
				elemento.getSeq(), elemento.getDescricao(), elemento.getSituacao());
	}

	
	/**
	 * ORADB TRIGGER MBCT_EUU_BRU
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void preAtualizar(MbcEquipamentoCirurgico elemento) throws BaseException {
		MbcEquipamentoCirurgico old = 
			this.getMbcEquipamentoCirurgicoDAO().obterOriginal(elemento);
		
		//RN1
		this.verificarSituacaoEquipamentoCirurgico(elemento);
		//RN2
		if(CoreUtil.modificados(elemento.getDescricao(), old.getDescricao())){
			this.alterarDescricao(elemento);
		}
		//RN3
		if(CoreUtil.modificados(elemento.getSituacao(), old.getSituacao())){
			this.alterarSituacao(elemento);
		}
	}
	
	
	/**
	 * ORADB PROCEDURE RN_EUUP_VER_SIT
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void verificarSituacaoEquipamentoCirurgico(MbcEquipamentoCirurgico elemento) throws BaseException {
		if(DominioSituacao.I.equals(elemento.getSituacao())
				&& StringUtils.isBlank(elemento.getMotivoInat())) {
			throw new ApplicationBusinessException(MbcEquipamentoCirurgicoRNExceptionCode.MBC_00251);
		}
		if(DominioSituacao.A.equals(elemento.getSituacao())
				&& StringUtils.isNotBlank(elemento.getMotivoInat())) {
			elemento.setMotivoInat(null);
		}
	}
	
	/**
	 * ORADB PROCEDURE RN_PHIP_ATU_DESCR
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void alterarDescricao(MbcEquipamentoCirurgico elemento) throws BaseException {
		FatProcedHospInternos fatProcedHospInternos = this.getFaturamentoFacade()
			.pesquisarPorChaveGenericaFatProcedHospInternos(elemento.getSeq(), FatProcedHospInternos.Fields.EUU_SEQ);
		if(fatProcedHospInternos != null){
			fatProcedHospInternos.setDescricao(elemento.getDescricao());
			this.getFaturamentoFacade().atualizarFatProcedHospInternos(fatProcedHospInternos);
		}
	}
	
	
	/**
	 * ORADB PROCEDURE RN_PHIP_ATU_SITUACAO
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void alterarSituacao(MbcEquipamentoCirurgico elemento) throws BaseException {
		FatProcedHospInternos fatProcedHospInternos = this.getFaturamentoFacade()
			.pesquisarPorChaveGenericaFatProcedHospInternos(elemento.getSeq(), FatProcedHospInternos.Fields.EUU_SEQ);
		if(fatProcedHospInternos != null){
			fatProcedHospInternos.setSituacao(elemento.getSituacao());
			this.getFaturamentoFacade().atualizarFatProcedHospInternos(fatProcedHospInternos);
		}
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_EUU_BRD
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void remover(MbcEquipamentoCirurgico elemento) throws BaseException {
		this.preRemover(elemento);
		this.getMbcEquipamentoCirurgicoDAO().remover(elemento);
	}
	
	
	/**
	 * PROCEDURE RN_PHIP_ATU_DELETE
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void preRemover(MbcEquipamentoCirurgico elemento) throws BaseException {
		//RN1
		this.getFaturamentoFacade().deleteFatProcedHospInternos(
				null, null, null, null, null, elemento.getSeq(), null, null, null);
	}
	
	
	/** GET **/
	protected MbcEquipamentoCirurgicoDAO getMbcEquipamentoCirurgicoDAO() {
		return mbcEquipamentoCirurgicoDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	protected ICadastrosBasicosPrescricaoMedicaFacade getCadastrosBasicosPrescricaoMedicaFacade() {
		return iCadastrosBasicosPrescricaoMedicaFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}
}
