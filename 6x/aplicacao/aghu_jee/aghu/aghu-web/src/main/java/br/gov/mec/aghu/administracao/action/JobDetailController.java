package br.gov.mec.aghu.administracao.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;


public class JobDetailController extends ActionController {
	
	private static final long serialVersionUID = -63589333669807066L;
	private static final Log LOG = LogFactory.getLog(JobDetailController.class);
	
	private static final String PAGE_PESQUISAR = "jobDetailList";
	
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private ISchedulerFacade schedulerFacade;
	
	private AghJobDetail jobDetail;

	private Integer seq;
	
		
	
	
	@PostConstruct
	protected void init() {
		this.jobDetail = new AghJobDetail();
	}
	
	
	public String cancelar() {
		this.seq = null;
		
		return PAGE_PESQUISAR;
	}
	
	public void initForm() throws BaseException {
		

			LOG.info(this.seq);
			if (this.seq != null) {
				this.jobDetail = this.schedulerFacade
						.obterAghJobDetailPorId(this.seq);
			}

		}
		
		
	
	/** GET/SET **/

	public AghJobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(AghJobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}


	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	public Integer getSeq() {
		return seq;
	}
	
}
