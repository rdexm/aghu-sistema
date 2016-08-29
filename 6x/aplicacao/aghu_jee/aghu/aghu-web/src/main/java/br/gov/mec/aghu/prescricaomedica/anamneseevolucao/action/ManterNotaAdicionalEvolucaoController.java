package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.model.MpmNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;


public class ManterNotaAdicionalEvolucaoController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 1567897800989L;
	
	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private RapServidores servidor;
	private AghAtendimentos atendimento;
	private boolean permitirManterAnamneseEvolucao;
	private boolean evolucaoValidada = false;
	private String descricaoNotaAdicional;
	private List<MpmNotaAdicionalEvolucoes> listaNotasAdicionaisEvolucao;
	private MpmNotaAdicionalEvolucoes notaAdicionalEvolucao;
	private MpmEvolucoes evolucao;
	private MpmAnamneses anamneses;
	private AghEspecialidades especialidade;
	private List<AghEspecialidades> listaEspecialidades; 
	private boolean modoVisualizacao;
	
	public void iniciar(){
		
		this.permitirManterAnamneseEvolucao = cascaFacade.usuarioTemPermissao(servidor.getUsuario() , "manterAnamneseEvolucao" );

		this.notaAdicionalEvolucao = new MpmNotaAdicionalEvolucoes();
		this.modoVisualizacao = false;
		if(evolucao != null && DominioIndPendenteAmbulatorio.V.equals(evolucao.getPendente())){
			evolucaoValidada = true;
			this.listaNotasAdicionaisEvolucao = prescricaoMedicaFacade.listarNotasAdicionaisEvolucoes(evolucao.getSeq());
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
	
	public void inserirNotaAdicionalEvolucao(){
		if(this.especialidade == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ESPECIALIDADE_OBRIGATORIO");
			return;
		}
		if(descricaoNotaAdicional == null){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NOTA_ADICIONAL_OBRIGATORIO");
			return;
		}
		
		if(notaAdicionalEvolucao.getSeq() != null){
			prescricaoMedicaFacade.persistirNotaAdicionalEvolucao(notaAdicionalEvolucao, descricaoNotaAdicional, this.especialidade.getNomeEspecialidade());
			retornoEstadoIncialNotaAdicional();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOTA_ADICIONAL_SALVO_SUCESSO");
		}
		else {
			this.notaAdicionalEvolucao.setMpmEvolucoes(evolucao);
			prescricaoMedicaFacade.persistirNotaAdicionalEvolucao(notaAdicionalEvolucao, descricaoNotaAdicional, this.especialidade.getNomeEspecialidade());
			retornoEstadoIncialNotaAdicional();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOTA_ADICIONAL_SALVO_SUCESSO");
		}
		iniciar();
	}
	
	public void retornoEstadoIncialNotaAdicional(){
		this.notaAdicionalEvolucao = new MpmNotaAdicionalEvolucoes();
		this.descricaoNotaAdicional = null;
		obterEspecialidades();
		this.modoVisualizacao = false;
	}
		
	public boolean verificarAlteracao(){
		if(!this.modoVisualizacao && (this.descricaoNotaAdicional != null || 
			(this.listaEspecialidades != null && !this.listaEspecialidades.isEmpty() && this.listaEspecialidades.size() > 1
				&& this.especialidade != null))){
			return true;
		}
		return false;
	}
	
	public void visualizarNotaAdicional() {
		this.especialidade = null;
		this.modoVisualizacao = true;
	}
	
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public void setPermitirManterAnamneseEvolucao(boolean permitirManterAnamneseEvolucao) {
		this.permitirManterAnamneseEvolucao = permitirManterAnamneseEvolucao;
	}

	public boolean isPermitirManterAnamneseEvolucao() {
		return permitirManterAnamneseEvolucao;
	}
	
	public void setDescricaoNotaAdicional(String descricaoNotaAdicional) {
		this.descricaoNotaAdicional = descricaoNotaAdicional;
	}

	public String getDescricaoNotaAdicional() {
		return descricaoNotaAdicional;
	}

	public List<MpmNotaAdicionalEvolucoes> getListaNotasAdicionaisEvolucao() {
		return listaNotasAdicionaisEvolucao;
	}

	public void setListaNotasAdicionaisEvolucao(List<MpmNotaAdicionalEvolucoes> listaNotasAdicionaisEvolucao) {
		this.listaNotasAdicionaisEvolucao = listaNotasAdicionaisEvolucao;
	}

	public MpmNotaAdicionalEvolucoes getNotaAdicionalEvolucao() {
		return notaAdicionalEvolucao;
	}

	public void setNotaAdicionalEvolucao(MpmNotaAdicionalEvolucoes notaAdicionalEvolucao) {
		this.notaAdicionalEvolucao = notaAdicionalEvolucao;
	}

	public MpmEvolucoes getEvolucao() {
		return evolucao;
	}

	public void setEvolucao(MpmEvolucoes evolucao) {
		this.evolucao = evolucao;
	}

	public MpmAnamneses getAnamneses() {
		return anamneses;
	}

	public void setAnamneses(MpmAnamneses anamneses) {
		this.anamneses = anamneses;
	}

	public void setEvolucaoValidada(boolean evolucaoValidada) {
		this.evolucaoValidada = evolucaoValidada;
	}

	public boolean isEvolucaoValidada() {
		return evolucaoValidada;
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
