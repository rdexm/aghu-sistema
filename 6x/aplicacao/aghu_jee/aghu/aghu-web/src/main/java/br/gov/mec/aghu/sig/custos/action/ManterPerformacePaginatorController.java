package br.gov.mec.aghu.sig.custos.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;

public class ManterPerformacePaginatorController extends ActionController {

	private static final String DD_MM_YYYY_HH_MM_SS_SSSS = "dd/MM/yyyy hh:mm:ss:SSSS";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterPerformacePaginatorController.class);

	private static final long serialVersionUID = 1449586944657299128L;

	private List<String> retorno;

	private String tempoInicio;

	private String tempoFim;

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	public void executarCenario1() {		
		SimpleDateFormat sdf2 = new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS_SSSS);
		this.tempoInicio = sdf2.format(new Date());
		this.retorno = this.cadastrosBasicosSiconFacade.executarCenario1();
		LOG.info(retorno);
		this.tempoFim = sdf2.format(new Date());
	}

	public void executarCenario11() {
		SimpleDateFormat sdf2 = new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS_SSSS);
		this.tempoInicio = sdf2.format(new Date());
		this.retorno = this.cadastrosBasicosSiconFacade.executarCenario11();
		LOG.info(retorno);
		this.tempoFim = sdf2.format(new Date());
	}

	public void executarCenario12() {
		SimpleDateFormat sdf2 = new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS_SSSS);
		this.tempoInicio = sdf2.format(new Date());
		this.retorno = this.cadastrosBasicosSiconFacade.executarCenario12();
		LOG.info(retorno);
		this.tempoFim = sdf2.format(new Date());
	}

	public void executarCenario2() {
		SimpleDateFormat sdf2 = new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS_SSSS);
		this.tempoInicio = sdf2.format(new Date());
		this.retorno = this.cadastrosBasicosSiconFacade.executarCenario2();
		LOG.info(retorno);
		this.tempoFim = sdf2.format(new Date());
	}

	public void executarCenario3() {
		SimpleDateFormat sdf2 = new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS_SSSS);
		this.tempoInicio = sdf2.format(new Date());
		this.retorno = this.cadastrosBasicosSiconFacade.executarCenario3();
		LOG.info(retorno);
		this.tempoFim = sdf2.format(new Date());
	}

	public void executarCenario31() {
		SimpleDateFormat sdf2 = new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS_SSSS);
		this.tempoInicio = sdf2.format(new Date());
		this.retorno = this.cadastrosBasicosSiconFacade.executarCenario31();
		LOG.info(retorno);
		this.tempoFim = sdf2.format(new Date());
	}

	public List<String> getRetorno() {
		return retorno;
	}

	public void setRetorno(List<String> retorno) {
		this.retorno = retorno;
	}

	public String getTempoInicio() {
		return tempoInicio;
	}

	public void setTempoInicio(String tempoInicio) {
		this.tempoInicio = tempoInicio;
	}

	public String getTempoFim() {
		return tempoFim;
	}

	public void setTempoFim(String tempoFim) {
		this.tempoFim = tempoFim;
	}

}
