package br.gov.mec.aghu.exames.solicitacao.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class HorariosRotinaColetaFactory extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(HorariosRotinaColetaFactory.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private HorariosRotinaColetaDia horariosRotinaColetaDia;

	@Inject
	private HorariosRotinaColetaFER horariosRotinaColetaFER;

	@Inject
	private HorariosRotinaColetaFERM horariosRotinaColetaFERM;

	@Inject
	private HorariosRotinaColetaFERT horariosRotinaColetaFERT;
	
	public HorariosRotinaColeta getFactory(AghFeriados feriado) {
		HorariosRotinaColeta coleta;
		
		if (feriado == null) { //Não é feriado
			coleta = getHorariosRotinaColetaDia();
		} else if (feriado.getTurno() == null) { //É feriado o dia inteiro
			coleta = getHorariosRotinaColetaFER();
		} else if (feriado.getTurno() == DominioTurno.M) { //É feriado pela manhã
			coleta = getHorariosRotinaColetaFERM();
		} else if (feriado.getTurno() == DominioTurno.T) { // É feriado pela tarde
			coleta = getHorariosRotinaColetaFERT();
		} else {
			coleta = getHorariosRotinaColetaDia(); //DEFAULT é não feriado
		}
		
		return coleta;
	}
	
	protected HorariosRotinaColetaDia getHorariosRotinaColetaDia() {
		return horariosRotinaColetaDia;
	}
	
	protected HorariosRotinaColetaFER getHorariosRotinaColetaFER() {
		return horariosRotinaColetaFER;
	}

	protected HorariosRotinaColetaFERM getHorariosRotinaColetaFERM() {
		return horariosRotinaColetaFERM;
	}

	protected HorariosRotinaColetaFERT getHorariosRotinaColetaFERT() {
		return horariosRotinaColetaFERT;
	}
	
}
