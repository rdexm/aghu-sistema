package br.gov.mec.aghu.faturamento.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoItemContaApac;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.faturamento.dao.FatItemContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * ORADB FUNCTION MBCC_VERIF_CIRG_FATD
 * 
 */
@Stateless
public class VerificarCirurgiaFaturadaRN extends BaseBusiness {


@Inject
private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;

@Inject
private FatItemContaApacDAO fatItemContaApacDAO;

@Inject
private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;

private static final Log LOG = LogFactory.getLog(VerificarCirurgiaFaturadaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 3097479813068763467L;

	/**
	 * ORADB MBCC_VERIF_CIRG_FATD
	 * 
	 * @return Boolean
	 */
	public Boolean verificarCirurgiaFaturada(final Integer crgSeq, final DominioOrigemPacienteCirurgia origem) {

		if (DominioOrigemPacienteCirurgia.A.equals(origem)) {
			return verificarUnionParaFatProcedAmbRealizadoEFatItemContaApac(crgSeq);
		} else if (DominioOrigemPacienteCirurgia.I.equals(origem)) {
			DominioSituacaoItenConta[] situacoes = { DominioSituacaoItenConta.V, DominioSituacaoItenConta.P, DominioSituacaoItenConta.N, DominioSituacaoItenConta.R };
			return !this.getFatItemContaHospitalarDAO().buscarPorCirurgiaESituacao(crgSeq, situacoes).isEmpty();
		}

		return Boolean.FALSE;
	}

	public Boolean verificarUnionParaFatProcedAmbRealizadoEFatItemContaApac(final Integer crgSeq) {
		DominioSituacaoProcedimentoAmbulatorio[] situacoes = { DominioSituacaoProcedimentoAmbulatorio.ENCERRADO, DominioSituacaoProcedimentoAmbulatorio.APRESENTADO };
		List<FatProcedAmbRealizado> listaFatProcedAmbRealizado = this.getFatProcedAmbRealizadoDAO().buscarPorCirurgia(crgSeq, situacoes);
		DominioSituacaoItemContaApac[] situacoesApac = { DominioSituacaoItemContaApac.E, DominioSituacaoItemContaApac.P };
		List<FatItemContaApac> listaFatItemContaApac = this.getFatItemContaApacDAO().buscarPorCirurgia(crgSeq, situacoesApac);

		if (!listaFatProcedAmbRealizado.isEmpty() && !listaFatItemContaApac.isEmpty()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}

	public FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}

	public FatItemContaApacDAO getFatItemContaApacDAO() {
		return fatItemContaApacDAO;
	}
}
