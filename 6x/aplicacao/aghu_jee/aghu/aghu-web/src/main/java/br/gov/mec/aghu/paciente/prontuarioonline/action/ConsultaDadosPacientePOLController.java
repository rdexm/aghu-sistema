package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;

/**
 * Classe responsável por exibir as informações do paciente na consulta de
 * pacientes do POL.
 * 
 * @author cqsilva
 */


public class ConsultaDadosPacientePOLController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7587279785971104473L;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private SecurityController security;
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;

	private AipPacientes paciente;
	private AipEnderecosPacientes endereco;
	private List<AipConveniosSaudePaciente> convenios = new ArrayList<AipConveniosSaudePaciente>(0);
	
	@PostConstruct
	public void inicializar(){
		this.begin(conversation, true);
		inicio();
	}
	
	
	public void inicio() {
		this.paciente = this.pacienteFacade.obterPacientePorCodigo((Integer)itemPOL.getParametros().get("codPaciente"),
				null,
				new Enum[]{AipPacientes.Fields.CIDADE,
					AipPacientes.Fields.NACIONALIDADE,
					AipPacientes.Fields.OCUPACOES});

		if (this.paciente != null) {
			if(security.usuarioTemPermissao("convenioSaude", "pesquisar")){
				this.convenios = pacienteFacade.pesquisarConveniosPaciente(this.paciente);
			}

			List<AipEnderecosPacientes> enderecos = pacienteFacade.obterEnderecosPacientes(paciente.getCodigo());

			this.endereco = null;			
			if (enderecos != null && !enderecos.isEmpty()) {				
				AipEnderecosPacientes enderecoResidencial=null;
				
				// Deverá ser selecionado o endereço marcado como padrão e,
				// caso o mesmo não exista, deverá ser selecionado o
				// endereço marcado como Residencial.

				// Procura pelo endereço padrão
				for (AipEnderecosPacientes _endereco : enderecos) {
					if (_endereco.isPadrao()) {
						endereco = _endereco;
						break;
					}else if (enderecoResidencial==null && _endereco.getTipoEndereco().equals(DominioTipoEndereco.R)) {
						enderecoResidencial=_endereco;
					}
				}
				if (endereco==null && enderecoResidencial==null){
					endereco=enderecos.get(0);
				}else if(endereco==null && enderecoResidencial!=null){
					endereco=enderecoResidencial;
				}
			}
		} else {
			this.convenios = new ArrayList<AipConveniosSaudePaciente>(0);
		}
	}

	// ### GETs e SETs ###

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public List<AipConveniosSaudePaciente> getConvenios() {
		return convenios;
	}

	public void setConvenios(List<AipConveniosSaudePaciente> convenios) {
		this.convenios = convenios;
	}

	public AipEnderecosPacientes getEndereco() {
		return endereco;
	}

	public void setEndereco(AipEnderecosPacientes endereco) {
		this.endereco = endereco;
	}

}
