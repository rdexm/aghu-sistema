package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ConsultarHistoricoDiagnosticosAtendimentoController extends ActionController {

	private static final long serialVersionUID = -2132062367200469108L;
	private static final Log LOG = LogFactory.getLog(ConsultarHistoricoDiagnosticosAtendimentoController.class);
	private static final String PAGINA_MANTER_DIAGNOSTICOS_ATENDIMENTO = "manterDiagnosticosAtendimento";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private PrescricaoMedicaVO prescricaoMedicaVO;
	
	private RapServidores servidor;
		
	private Integer pmeSeq;
	private Integer atendimentoSeq;
	private Integer cidSeq;
	
	private AghAtendimentos atendimento = null;
	
	private List<MpmCidAtendimento> listaMpmCidAtendimentos = new ArrayList<MpmCidAtendimento>();

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void carregar() {
	 

		setServidor(this.servidorLogadoFacade.obterServidorLogado());
		if (getAtendimentoSeq() != null) {
			setAtendimento(aghuFacade.obterAtendimentoPeloSeq(getAtendimentoSeq()));
		}
		if (getAtendimento() == null) {
			apresentarMsgNegocio(Severity.ERROR, "ATENDIMENTO_NULO");
			LOG.warn("ATENDIMENTO_NULO");
		}
		// carregar a lista de mpmCidAtendimentos
		try {
			setListaMpmCidAtendimentos(prescricaoMedicaFacade.buscarHistoricoMpmCidsPorAtendimento(getAtendimento()));
//			Collections.sort(getListaMpmCidAtendimentos(), mpmCidAtendimentoComparator);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("ERRO",e);
		}
	
	}

	
	public String voltarDiagnosticosAtendimento(){
		return PAGINA_MANTER_DIAGNOSTICOS_ATENDIMENTO;
	}


	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}


	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}


	public void setListaMpmCidAtendimentos(List<MpmCidAtendimento> listaMpmCidAtendimentos) {
		this.listaMpmCidAtendimentos = listaMpmCidAtendimentos;
	}

	public List<MpmCidAtendimento> getListaMpmCidAtendimentos() {
		return listaMpmCidAtendimentos;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setPmeSeq(Integer pmeSeq) {
		this.pmeSeq = pmeSeq;
	}

	public Integer getPmeSeq() {
		return pmeSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}


	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}


	private static class MpmCidAtendimentoComparator implements Comparator<MpmCidAtendimento> {
		@Override
		public int compare(MpmCidAtendimento a1, MpmCidAtendimento a2) {
			int result = a1.getCid().getCodigo().compareToIgnoreCase(a2.getCid().getCodigo());
			return result; 
		}
	}
	
}