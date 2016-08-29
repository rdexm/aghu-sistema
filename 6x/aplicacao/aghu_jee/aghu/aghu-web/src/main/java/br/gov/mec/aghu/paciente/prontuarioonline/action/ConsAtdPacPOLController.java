package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por exibir os atendimentos do paciente na consulta de
 * pacientes do POL.
 * 
 * @author cristiane.barbado
 */


public class ConsAtdPacPOLController extends ActionController  {
	private static final long serialVersionUID = -8046443188033341103L;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	private List<AtendimentosVO> atendimentos;
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;

	/**
	 * Paciente consultado.
	 */
	private AipPacientes paciente;

	
	@PostConstruct
	protected void inicializar(){
		 this.begin(conversation, true);
		 inicio();
	}	
	
	public void inicio() {
		try {
			atendimentos  = this.prontuarioOnlineFacade.pesquisarAtendimentosPorProntuario(itemPOL.getProntuario());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public AipPacientes getPaciente() {
		return paciente;
	}

	public List<AtendimentosVO> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(List<AtendimentosVO> atendimentos) {
		this.atendimentos = atendimentos;
	}

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}}
