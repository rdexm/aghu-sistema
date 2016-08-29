package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;

public class PesquisarEspecialidadeProfissionalController extends ActionController {

	private static final long serialVersionUID = 5980768760902420585L;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private RapServidores servidor;
	
	private List<AghEspecialidades> especialidades;

	private String nome;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}	
	
	public void inicio(){
		this.servidor = this.servidorLogadoFacade.obterServidorLogado();
		this.nome = this.registroColaboradorFacade.obterNomePessoaServidor(this.servidor.getId().getVinCodigo(), this.servidor.getId().getMatricula());
		this.especialidades = this.prescricaoMedicaFacade.listarEspecialidadesPorServidor(this.servidor);
	}
	
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}	

	public List<AghEspecialidades> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(List<AghEspecialidades> especialidades) {
		this.especialidades = especialidades;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}	
}
