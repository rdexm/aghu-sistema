package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;


public class ManterNotaAdicionalAnamneseController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 1567897800989L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	private String descricaoNotaAdicional;
	private MpmNotaAdicionalAnamneses notaAdicionalAnamnese;
	private List<MpmNotaAdicionalAnamneses> listaNotasAdicionaisAnamnese;
	private boolean permitirManterAnamneseEvolucao;
	private boolean anamneseValidada = false;
	private RapServidores servidor;
	private MpmAnamneses anamneseCorrente;
	private AghEspecialidades especialidade;
	private List<AghEspecialidades> listaEspecialidades; 
	private boolean modoVisualizacao;
		
	public void iniciar(){

		permitirManterAnamneseEvolucao = cascaFacade.usuarioTemPermissao(servidor.getUsuario() , "manterAnamneseEvolucao" );
		
		notaAdicionalAnamnese = new MpmNotaAdicionalAnamneses();
		this.modoVisualizacao = false;
		if(anamneseCorrente != null){
			anamneseValidada = true;
			setListaNotasAdicionaisAnamnese(prescricaoMedicaFacade.listarNotasAdicionaisAnamnese(anamneseCorrente.getSeq()));
			obterEspecialidades();
		}
	}

	private void obterEspecialidades() {
		this.listaEspecialidades = this.aghuFacade.listarEspecialidadesPorServidor(this.servidor.getId().getMatricula(), this.servidor.getId().getVinCodigo());
		this.especialidade = null;
		if(this.listaEspecialidades != null && !this.listaEspecialidades.isEmpty() && this.listaEspecialidades.size() == 1) {
			this.especialidade = this.listaEspecialidades.get(0);
		}
	}

	public void inserirNotaAdicionalAnamnese() {
		if(this.especialidade == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ESPECIALIDADE_OBRIGATORIO");
			return;
		}		
		if(descricaoNotaAdicional == null){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NOTA_ADICIONAL_OBRIGATORIO");
			return;
		}
		
		if(notaAdicionalAnamnese.getSeq() != null){
			prescricaoMedicaFacade.persistirNotaAdicionalAnamnese(notaAdicionalAnamnese, descricaoNotaAdicional, servidor, this.especialidade.getNomeEspecialidade());
			retornarEstadoInicial();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOTA_ADICIONAL_SALVO_SUCESSO");
		}
		else {
			notaAdicionalAnamnese.setMpmAnamneses(anamneseCorrente);
			prescricaoMedicaFacade.persistirNotaAdicionalAnamnese(notaAdicionalAnamnese, descricaoNotaAdicional, servidor, this.especialidade.getNomeEspecialidade());
			retornarEstadoInicial();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOTA_ADICIONAL_SALVO_SUCESSO");
		}
		iniciar();
	}
		
	public boolean verificarAlteracao(){
		if(!this.modoVisualizacao && (this.descricaoNotaAdicional != null || 
				(this.listaEspecialidades != null && !this.listaEspecialidades.isEmpty() && this.listaEspecialidades.size() > 1
						&& this.especialidade != null))){
			return true;
		}
		return false;
	}

	public void retornarEstadoInicial(){
		descricaoNotaAdicional = null;
		notaAdicionalAnamnese = new MpmNotaAdicionalAnamneses();	
		obterEspecialidades();
		this.modoVisualizacao = false;
	}
	
	public void visualizarNotaAdicional() {
		this.especialidade = null;
		this.modoVisualizacao = true;
	}
	
	public String getDescricaoNotaAdicional() {
		return descricaoNotaAdicional;
	}

	public void setDescricaoNotaAdicional(String descricaoNotaAdicional) {
		this.descricaoNotaAdicional = descricaoNotaAdicional;
	}

	public boolean isPermitirManterAnamneseEvolucao() {
		return permitirManterAnamneseEvolucao;
	}

	public void setPermitirManterAnamneseEvolucao(boolean permitirManterAnamneseEvolucao) {
		this.permitirManterAnamneseEvolucao = permitirManterAnamneseEvolucao;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public void setListaNotasAdicionaisAnamnese(List<MpmNotaAdicionalAnamneses> listaNotasAdicionaisAnamnese) {
		this.listaNotasAdicionaisAnamnese = listaNotasAdicionaisAnamnese;
	}

	public List<MpmNotaAdicionalAnamneses> getListaNotasAdicionaisAnamnese() {
		return listaNotasAdicionaisAnamnese;
	}

	public MpmAnamneses getAnamneseCorrente() {
		return anamneseCorrente;
	}

	public void setAnamneseCorrente(MpmAnamneses anamneseCorrente) {
		this.anamneseCorrente = anamneseCorrente;
	}

	public boolean isAnamneseValidada() {
		return anamneseValidada;
	}

	public void setAnamneseValidada(boolean anamneseValidada) {
		this.anamneseValidada = anamneseValidada;
	}

	public MpmNotaAdicionalAnamneses getNotaAdicionalAnamnese() {
		return notaAdicionalAnamnese;
	}

	public void setNotaAdicionalAnamnese(MpmNotaAdicionalAnamneses notaAdicionalAnamnese) {
		this.notaAdicionalAnamnese = notaAdicionalAnamnese;
	}
	
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
	
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public List<AghEspecialidades> getListaEspecialidades() {
		return listaEspecialidades;
	}

	public boolean isModoVisualizacao() {
		return modoVisualizacao;
	}

	public void setModoVisualizacao(boolean modoVisualizacao) {
		this.modoVisualizacao = modoVisualizacao;
	}	
	
}
