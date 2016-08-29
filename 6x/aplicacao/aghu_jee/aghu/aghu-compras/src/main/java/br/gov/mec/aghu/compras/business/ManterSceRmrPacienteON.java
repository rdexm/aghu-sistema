package br.gov.mec.aghu.compras.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterSceRmrPacienteON extends BaseBusiness {


@EJB
private ManterItemRmpsRN manterItemRmpsRN;

private static final Log LOG = LogFactory.getLog(ManterSceRmrPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade faturamentoFacade;

@EJB
private IEstoqueFacade estoqueFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3410503054914673577L;

	private enum ManterSceRmrPacienteONExceptionCode implements BusinessExceptionCode {
		ERRO_CLONE_SCERMRPACIENTE;
	}

	public SceRmrPaciente persistirSceRmrPaciente(final SceRmrPaciente sceRmrPacienteNew, final SceRmrPaciente sceRmrPacienteOld, final Boolean flush)
			throws BaseException {
		if (sceRmrPacienteNew.getSeq() == null) {
			return this.inserirSceRmrPaciente(sceRmrPacienteNew, flush);
		} else {
			return this.atualizarSceRmrPaciente(sceRmrPacienteNew, sceRmrPacienteOld, flush);
		}
	}

	public SceRmrPaciente cloneSceRmrPaciente(final SceRmrPaciente paciente) throws BaseException {
		try {
			final SceRmrPaciente clone = (SceRmrPaciente) BeanUtils.cloneBean(paciente);
			// clone.setSceItemRmps(paciente.getSceItemRmps());
			return clone;
		} catch (final Exception e) {
			logError("Exceção capturada: ", e);
			throw new BaseException(ManterSceRmrPacienteONExceptionCode.ERRO_CLONE_SCERMRPACIENTE);
		}
	}

	public void removerSceItemRmpsParaNotaFiscal(final FatItemContaHospitalar fatItensContaHospitalar) throws BaseException {
		//IEstoqueFacade estoqueFacade = getEstoqueFacade();
		
		if (fatItensContaHospitalar != null && fatItensContaHospitalar.getItemRmps() != null) {
			//final SceItemRmps itemRmps = estoqueFacade.obterSceItemRmpsPorChavePrimaria(fatItensContaHospitalar.getItemRmps().getId());
			final SceItemRmps itemRmps = getEstoqueFacade().obterSceItemRmpsPorChavePrimaria(fatItensContaHospitalar.getItemRmps().getId());
			if (itemRmps.getItensContasHospitalar().size() == 1) {
				for (final FatItemContaHospitalar fatItensContaHospitalar2 : itemRmps.getItensContasHospitalar()) {
					removeSceItemRmps(fatItensContaHospitalar2);
				}
				final SceRmrPaciente paciente = itemRmps.getSceRmrPaciente();
				//estoqueFacade.removerSceItemRmps(itemRmps, false);
				getEstoqueFacade().removerSceItemRmps(itemRmps, false);
				//if (estoqueFacade.obterListaSceItemRmpsPorSceRmrPacienteCount(paciente.getSeq()) == 1) {
				if (getEstoqueFacade().obterListaSceItemRmpsPorSceRmrPacienteCount(paciente.getSeq()) == 1) {
					//getEstoqueFacade().removerSceRmrPaciente(paciente, false);
					getEstoqueFacade().removerSceRmrPaciente(paciente, false);
				}
			} else {
				removeSceItemRmps(fatItensContaHospitalar);
			}

		}
	}

	

	private void removeSceItemRmps(final FatItemContaHospitalar fatItensContaHospitalar) {
		final FatItemContaHospitalar fatItensContaHospitalar2 = getFaturamentoFacade().obterItemContaHospitalar(fatItensContaHospitalar.getId());
		fatItensContaHospitalar2.setItemRmps(null);
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected SceRmrPaciente inserirSceRmrPaciente(final SceRmrPaciente sceRmrPacienteNew, final Boolean flush) throws BaseException {
		getManterItemRmpsRN().preInserir(sceRmrPacienteNew);
		//return this.getEstoqueFacade().inserirSceRmrPaciente(sceRmrPacienteNew, flush);
		return this.getEstoqueFacade().inserirSceRmrPaciente(sceRmrPacienteNew, flush);
	}

	protected SceRmrPaciente atualizarSceRmrPaciente(final SceRmrPaciente sceRmrPacienteNew, final SceRmrPaciente sceRmrPacienteOld,
			final Boolean flush) throws BaseException {
		
		this.getManterItemRmpsRN().preAtualizar(sceRmrPacienteNew, sceRmrPacienteOld);
		//return this.getEstoqueFacade().atualizarSceRmrPaciente(sceRmrPacienteNew, flush);
		return this.getEstoqueFacade().atualizarSceRmrPaciente(sceRmrPacienteNew, flush);
	}

	protected ManterItemRmpsRN getManterItemRmpsRN() {
		return manterItemRmpsRN;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
}
