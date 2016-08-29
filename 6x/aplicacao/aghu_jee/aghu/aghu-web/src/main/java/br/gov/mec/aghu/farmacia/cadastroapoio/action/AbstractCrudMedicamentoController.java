package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public abstract class AbstractCrudMedicamentoController<E> extends AbstractCrudController<E>{
	
	private static final long serialVersionUID = 3458245297805378072L;
	
	private static final Log LOG = LogFactory.getLog(AbstractCrudMedicamentoController.class);
	
	private AfaMedicamento medicamento;
	private Boolean edicao;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
		
	public IFarmaciaFacade getFarmaciaFacade() {
	
		return this.farmaciaFacade;
	}
	
	public void setFarmaciaFacade(IFarmaciaFacade farmaciaFacade) {
		
		this.farmaciaFacade = farmaciaFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public AfaMedicamento getMedicamento() {

		return this.medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {

		this.medicamento = medicamento;
	}

	/**
	 * Returnar uma nova instancia padrao, mas com os campos preenchidos como especificado nos argumentos.
	 * 
	 * @param seq
	 * @param matCodigo
	 * @param medicamento
	 * @return
	 */
	protected abstract void instanciarEntidade();
	
	protected AfaMedicamento obterMedicamentoPorId(Integer matCodigo) {
		
		return farmaciaFacade.obterMedicamento(matCodigo);
	}

	/**
	 * Providencia uma nova entidade para ser trabalhada.
	 * @see #instanciarEntidade(Integer, Integer, AfaMedicamento)
	 */
	@Override
	@PostConstruct
	public void init() {
		//this.setMedicamento(farmaciaFacade.obterMedicamento(this.getMatCodigo()));
		/*this.setEntidade(this.instanciarEntidade(this.getSeq(),
				this.getMatCodigo(), this.getMedicamento()));*/
	}
	
	@Override
	protected void procederPreExclusao() {
		this.init();
	}
	
	@Override
	protected void procederPosExclusao() {
		//this.setSeq(null);		
	}
	
	@Override
	protected void prepararInclusao() {
		//this.setSeq(null);
		//this.setIsUpdate(Boolean.FALSE);
	}
		
	@Override
	protected boolean efetuarInclusao() {
		return !edicao;
	}
	
	@Override
	protected void prepararCancelamento() {
		//this.setSeq(null);
	}

	public IFarmaciaApoioFacade getFarmaciaApoioFacade() {
		return farmaciaApoioFacade;
	}

	public void setFarmaciaApoioFacade(IFarmaciaApoioFacade farmaciaApoioFacade) {
		this.farmaciaApoioFacade = farmaciaApoioFacade;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}
	
	public String getNomeMicroComputador() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
		return nomeMicrocomputador;
	}
}
