package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcMotivoDemoraSalaRecDAO;
import br.gov.mec.aghu.model.MbcMotivoDemoraSalaRec;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcMotivoDemoraSalaRecRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcMotivoDemoraSalaRecRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMotivoDemoraSalaRecDAO mbcMotivoDemoraSalaRecDAO;


	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6127575774577396866L;

	protected enum MotivoDemoraSalaRecRNExceptionCode implements BusinessExceptionCode {
		MBC_00095,MBC_00211;
	}

	/**
	 * RN1: Motivo Demora Sala Rec com este Descricao já cadastrado.
	 * ORADB MBC_MSR_UK1
	 * @param motivoDemoraSalaRec
	 * @throws BaseException
	 */
	private void verificarExistenciaRegistro(MbcMotivoDemoraSalaRec motivoDemoraSalaRec) throws BaseException {

		MbcMotivoDemoraSalaRec motivoDemora = this.getMbcMotivoDemoraSalaRecDAO().obterMbcMotivoDemoraSalaRecPorDescricao(motivoDemoraSalaRec.getDescricao());

		if (motivoDemora !=null) {
			throw new ApplicationBusinessException(MotivoDemoraSalaRecRNExceptionCode.MBC_00095);
		}

	}
	
	/**
	 * ORADB MBCT_MSR_BRI
	 * @param motivoDemoraSalaRec
	 * @throws BaseException
	 */
	private void preInserir(MbcMotivoDemoraSalaRec motivoDemoraSalaRec) throws BaseException  {
	
		this.verificarExistenciaRegistro(motivoDemoraSalaRec);
		motivoDemoraSalaRec.setCriadoEm(new Date());
		motivoDemoraSalaRec.setServidor(servidorLogadoFacade.obterServidorLogado());
		
	}
	
	private void verificarDadosAtualizados(MbcMotivoDemoraSalaRec motivoDemoraSalaRec) throws BaseException {
		MbcMotivoDemoraSalaRec original = getMbcMotivoDemoraSalaRecDAO().obterOriginal(motivoDemoraSalaRec);

		/**
		 * MBC_00211 “Tentativa de alterar campos que não podem ser alterados.”
		 */
		if (CoreUtil.modificados(motivoDemoraSalaRec.getDescricao(),original.getDescricao())
				|| CoreUtil.modificados(motivoDemoraSalaRec.getCriadoEm(), original.getCriadoEm())
				|| CoreUtil.modificados(motivoDemoraSalaRec.getServidor(), original.getServidor())) {
			throw new ApplicationBusinessException(MotivoDemoraSalaRecRNExceptionCode.MBC_00211);
		}

	}
	

	private void preAtualizar(MbcMotivoDemoraSalaRec motivoDemoraSalaRec) throws BaseException  {
		this.verificarDadosAtualizados(motivoDemoraSalaRec);
	}
	
	
	/*
	 * ORADB MBCT_MSR_BRI
	 */
	public void inserir(MbcMotivoDemoraSalaRec motivoDemoraSalaRec) throws BaseException {
		preInserir(motivoDemoraSalaRec);
		this.getMbcMotivoDemoraSalaRecDAO().persistir(motivoDemoraSalaRec);
	}
	

	/**
	 * ORADB MBCT_MSR_BRU
	 * @param motivoDemoraSalaRec
	 * @throws BaseException
	 */
	public void atualizar(MbcMotivoDemoraSalaRec motivoDemoraSalaRec) throws BaseException {
		this.preAtualizar(motivoDemoraSalaRec);
		this.getMbcMotivoDemoraSalaRecDAO().atualizar(motivoDemoraSalaRec);
		this.getMbcMotivoDemoraSalaRecDAO().flush();
		
	}
	
	
	private MbcMotivoDemoraSalaRecDAO getMbcMotivoDemoraSalaRecDAO() {
		return mbcMotivoDemoraSalaRecDAO;
	}
	

	
	
}
