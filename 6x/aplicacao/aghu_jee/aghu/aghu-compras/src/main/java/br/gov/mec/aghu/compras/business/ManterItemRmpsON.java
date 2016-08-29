package br.gov.mec.aghu.compras.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterItemRmpsON extends BaseBusiness {


@EJB
private ManterItemRmpsRN manterItemRmpsRN;

private static final Log LOG = LogFactory.getLog(ManterItemRmpsON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IEstoqueFacade estoqueFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7640328701334959915L;


	private enum ManterItemRmpsONExceptionCode implements BusinessExceptionCode {
		ERRO_CLONE_SCEITEMRMPS;
	}
	
 /**
  * Metodo para persistir ItemRmps.
  * @param itemNew
  * @param itemOld
  * @param flush
  * @throws BaseException
  */
 public SceItemRmps persistirItemRmps(final SceItemRmps itemNew, final SceItemRmps itemOld, final Boolean flush) throws BaseException{
	 
	 if(itemNew == null || itemNew.getId() == null || itemNew.getId().getRmpSeq() == null){
		 throw new IllegalArgumentException("Parâmetro inválido.");
	 }
	 
	 if(itemNew.getId().getNumero() == null){
		 return this.inserirItemRmps(itemNew, flush);
	 }else{
		 return this.atualizarItemRmps(itemNew, itemOld, flush);
	 }
 }

 public SceItemRmps cloneSceItemRmps(final SceItemRmps itemRmps) throws BaseException {
	try {
		final SceItemRmps clone = (SceItemRmps) BeanUtils.cloneBean(itemRmps);
//		clone.setItensContasHospitalares(itemRmps.getItensContasHospitalares());
		return clone;
	} catch (final Exception e) {
		logError("Exceção capturada: ", e);
		throw new BaseException(ManterItemRmpsONExceptionCode.ERRO_CLONE_SCEITEMRMPS);
	}

 }
 
 /**
  * Metodo para inserir ItemRmps.
  * @param itemNew
  * @param flush
  * @throws BaseException
  */
 protected SceItemRmps inserirItemRmps(final SceItemRmps itemNew, final Boolean flush) throws BaseException{
	 IEstoqueFacade estoqueFacade = this.getEstoqueFacade();
	 
	 final ManterItemRmpsRN manterItemRmpsRN = getManterItemRmpsRN();
	 
	 itemNew.getId().setNumero(estoqueFacade.obterProximoNumero(itemNew.getId().getRmpSeq()));
	 
	 manterItemRmpsRN.executarAntesInserirItemRmps(itemNew);
	 
	 final SceItemRmps returno = estoqueFacade.inserirSceItemRmps(itemNew, flush);
	 
	 manterItemRmpsRN.executarStatementAposInserir(itemNew, new Date());
	 return returno;
 }

 
 /**
  * Metodo para atualizar ItemRmps.
  * @param itemNew
  * @param itemOld
  * @param flush
  * @throws BaseException
  */
 protected SceItemRmps atualizarItemRmps(final SceItemRmps itemNew, final SceItemRmps itemOld, final Boolean flush) throws BaseException{
	 final ManterItemRmpsRN manterItemRmpsRN = getManterItemRmpsRN();
	 
	 manterItemRmpsRN.executarAntesAtualizarItemRmps(itemOld, itemNew);
	 
	 final SceItemRmps returno = this.getEstoqueFacade().atualizarSceItemRmps(itemNew, flush);
	 
	 manterItemRmpsRN.executarStatementAposAtualizar(itemNew, itemOld, new Date());
	 return returno;
 }

	protected ManterItemRmpsRN getManterItemRmpsRN() {
		return manterItemRmpsRN;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}	
 
}


