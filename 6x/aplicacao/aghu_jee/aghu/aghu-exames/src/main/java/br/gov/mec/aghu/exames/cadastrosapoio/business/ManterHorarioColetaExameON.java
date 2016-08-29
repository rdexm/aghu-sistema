package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterHorarioColetaExameON extends BaseBusiness {


@EJB
private ManterHorarioColetaExameRN manterHorarioColetaExameRN;

private static final Log LOG = LogFactory.getLog(ManterHorarioColetaExameON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 449814046415248653L;


	public AelExameHorarioColeta persistirHorarioColetaExame(AelExameHorarioColeta exameHorarioColeta) throws ApplicationBusinessException {
		AelExameHorarioColeta retorno = null;
		
		Calendar calendarioTemp = Calendar.getInstance();
		calendarioTemp.setTime(new Date());
		
		exameHorarioColeta.setHorarioInicial(this.atribuirAnoAtual(exameHorarioColeta.getHorarioInicial()));
		exameHorarioColeta.setHorarioFinal(this.atribuirAnoAtual(exameHorarioColeta.getHorarioFinal()));
		
		if(exameHorarioColeta.getId() == null) {
			//Realiza inserção
			getManterHorarioColetaExameRN().inserir(exameHorarioColeta);
		} else {
			//Realiza atualização
			getManterHorarioColetaExameRN().atualizar(exameHorarioColeta);
		}
		
		return retorno;
	}
	
	
	private Date atribuirAnoAtual(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
		return cal.getTime();
	}
	
	
	/**
	 * Remove objeto
	 * @param horarioColetaExame
	 * @throws ApplicationBusinessException 
	 */
	public void removerHorarioColetaExame(AelExameHorarioColeta horarioColetaExame) throws ApplicationBusinessException {
		getManterHorarioColetaExameRN().remover(horarioColetaExame);
	}
	
	
	protected ManterHorarioColetaExameRN getManterHorarioColetaExameRN() {
		return manterHorarioColetaExameRN;
	}
	
}