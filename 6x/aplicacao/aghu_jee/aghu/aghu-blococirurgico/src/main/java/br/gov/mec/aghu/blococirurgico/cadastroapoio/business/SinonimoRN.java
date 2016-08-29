package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSinonimoProcedCirurgicoDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcSinonimoProcCirg;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SinonimoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(SinonimoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcSinonimoProcedCirurgicoDAO mbcSinonimoProcedCirurgicoDAO;

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 3222500847490874728L;
	
	
	public enum SinonimoRNExceptionCode implements BusinessExceptionCode {
		MBC_00282, MBC_00234, MBC_00431, DESCRICAO_SINONIMO_JA_EXISTENTE;
	}
	
	public void persistir(MbcSinonimoProcCirg sinonimo, Boolean inserir) throws BaseException {
		if(inserir) {
			this.inserir(sinonimo);
		}
		else {
			this.atualizar(sinonimo);
		}
	}
	
	public void inserir(MbcSinonimoProcCirg sinonimo) throws BaseException {
		//PRE-INSERT do Datablock SNP (MBC_SINONIMO_PROC_CIRGS)
		sinonimo.getId().setSeqp(getMbcSinonimoProcedCirurgicoDAO().buscaProximoSeqpSinonimosPeloSeqProcedimento(sinonimo.getId().getPciSeq()));
		this.preInserir(sinonimo);
		//Valida UK - MBC_SNP_UK1
		 if(getMbcSinonimoProcedCirurgicoDAO().quantidadeSinonimosPelaDescricao(sinonimo.getDescricao()) > 0) {
			 throw new ApplicationBusinessException(SinonimoRNExceptionCode.DESCRICAO_SINONIMO_JA_EXISTENTE);
		 }
		this.getMbcSinonimoProcedCirurgicoDAO().persistir(sinonimo);
	}
	
	public void atualizar(MbcSinonimoProcCirg sinonimo) throws BaseException {
		this.evtPreUpdate(sinonimo);
		this.preAtualizar(sinonimo);
		this.getMbcSinonimoProcedCirurgicoDAO().atualizar(sinonimo);
	}
	
	
	private void evtPreUpdate(MbcSinonimoProcCirg sinonimo) throws BaseException {
		MbcProcedimentoCirurgicos procedimento =  getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(sinonimo.getId().getPciSeq());
		if(CoreUtil.igual(procedimento.getDescricao(), sinonimo.getDescricao())) {
			if(DominioSituacao.I.equals(sinonimo.getSituacao()) && DominioSituacao.A.equals(procedimento.getIndSituacao())) {
				throw new ApplicationBusinessException(SinonimoRNExceptionCode.MBC_00431);
			}
		}
	}
	
	/**
	 * ORADB: MBCT_SNP_BRI
	 * 
	 * @param sinonimo
	 * @param servidorLogado
	 */
	private void preInserir(MbcSinonimoProcCirg sinonimo) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		sinonimo.setCriadoEm(new Date());
		sinonimo.setCriadoPor(servidorLogado);
	}
	
	/**
	 * ORADB: MBCT_SNP_BRU
	 * 
	 * @param sinonimo
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void preAtualizar(MbcSinonimoProcCirg sinonimo) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcSinonimoProcCirg original = getMbcSinonimoProcedCirurgicoDAO().obterOriginal(sinonimo);
		
		if(!CoreUtil.igual(original.getDescricao(), sinonimo.getDescricao())) {
			 /*Não é permitido alterar a descrição  deste sinônimo de  procedimento*/
			 this.rnSnppVerUpd(sinonimo.getId().getSeqp());
			//Valida UK - MBC_SNP_UK1
			 if(getMbcSinonimoProcedCirurgicoDAO().quantidadeSinonimosPelaDescricao(sinonimo.getDescricao()) > 0) {
				 throw new ApplicationBusinessException(SinonimoRNExceptionCode.DESCRICAO_SINONIMO_JA_EXISTENTE);
			 }
		}
		
		if(!CoreUtil.igual(DateUtils.truncate(original.getCriadoEm(), Calendar.SECOND), DateUtils.truncate(sinonimo.getCriadoEm(), Calendar.SECOND)) ||
			!CoreUtil.igual(original.getCriadoPor(), sinonimo.getCriadoPor()))	{
			// Tentativa de alterar campos que não podem ser alterados
			throw new ApplicationBusinessException(SinonimoRNExceptionCode.MBC_00234);
		}
		
		/*Atualiza servidor que alterou o sinônimo do procedimento cirúrgico */
		sinonimo.setCriadoPor(servidorLogado);
	}
	
	/**
	 * @ORADB MBCK_SNP_RN : RN_SNPP_VER_UPD
	 */
	public void rnSnppVerUpd(Short seqp) throws BaseException {
		  /*Garante que descrição do sinonimo identificador da tabela SINONIMO PROC CIRC
		  	não será alterada quando seqp = 1*/
		if(seqp == 1) {
			//Não é permitido alterar sinônimo identificador deste  procedimento cirúrgico
			throw new ApplicationBusinessException(SinonimoRNExceptionCode.MBC_00282);
		}
	}
	
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}
	
	protected MbcSinonimoProcedCirurgicoDAO getMbcSinonimoProcedCirurgicoDAO() {
		return mbcSinonimoProcedCirurgicoDAO;
	}
}
