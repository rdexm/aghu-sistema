package br.gov.mec.aghu.internacao.estornar.action;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.estornar.business.IEstornarInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class EstornarInternacaoController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1506303992089259075L;
	
	private final String PAGE_LISTAR_INTERNACAO = "internacao-estornarInternacaoList";
	private final String PAGE_ESTORNAR_INTERNACAO = "internacao-estornarInternacao";	

	@EJB
	private IEstornarInternacaoFacade estornarInternacaoFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	//Dados de Paciente
	private Integer prontuario;
	private String nomePaciente;
	//Prontuário para pesquisa
	private Integer prontuarioPesquisa;
	//Leito
	private String leitoID;
	//Quarto
	private String descricaoQuarto;
	//Unidade Funcional
	private Short unidadeFuncionalSeq;
	private String descricaoUnidadeFuncional;
	//Especialidade
	private AghEspecialidades especialidade = new AghEspecialidades();
	//Dados de Professor
	private String nroRegConselho;
	private String nomeProfessor;
	//Dados de Convênio
	private Short codigoConvenio;
	private Byte codigoPlano;
	private String descricaoConvenioPlano;
	//Data de Internação
	private String dthrInternacao;
	//Dados da alta médica
	private String dthrAltaMedica;
	private String codigoAltaMedica;
	private String descricaoAltaMedica;
	//Justificativa
	private String justificativa;
	private AinInternacao internacao;
	private List<AinInternacao> listaInternacoes = new ArrayList<AinInternacao>();
	private Integer seqInternacao;
	private boolean exibirListagem = false;
	
	@PostConstruct
	public void init(){
		begin(conversation);		
	}
	
	/**
	 * Método que realiza a pesquisa da internação
	 */
	public void pesquisar(){
		if (prontuarioPesquisa != null){
			listaInternacoes = pesquisaInternacaoFacade.pesquisarInternacoesPorProntuarioUnidade(prontuarioPesquisa);
			if (listaInternacoes.size() > 0){
				nomePaciente = listaInternacoes.get(0).getPaciente().getNome();
			}
		}
		else{
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PRONTUARIO_NAO_ENCONTRADO");
		}
		exibirListagem = true;
	}
	
	public void carregarDados() {
		this.prontuario = prontuarioPesquisa;
		nomePaciente = internacao.getPaciente().getNome();
		if (internacao.getLeito() != null){
			leitoID = internacao.getLeito().getLeitoID();
		}
		else if (internacao.getQuarto() != null){
			descricaoQuarto = internacao.getQuarto().getDescricao();
		}
		else{
			unidadeFuncionalSeq = internacao.getUnidadesFuncionais().getSeq();
			descricaoUnidadeFuncional = internacao.getUnidadesFuncionais().getAndarAlaDescricao();
		}
		
		especialidade = internacao.getEspecialidade();
		ProfessorCrmInternacaoVO professor = obterCrmProfessor(internacao);
		nroRegConselho = professor.getNroRegConselho();
		nomeProfessor = professor.getNome();
		codigoConvenio = internacao.getConvenioSaude().getCodigo();
		codigoPlano = internacao.getConvenioSaudePlano().getId().getSeq();
		descricaoConvenioPlano = internacao.getConvenioSaudePlano().getConvenioSaude().getDescricao() + " - " + 
		internacao.getConvenioSaudePlano().getDescricao();
		
		//Formata data de internação
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");        
		dthrInternacao = df.format(internacao.getDthrInternacao());  
		
		if (internacao.getDthrAltaMedica() != null){
			SimpleDateFormat dfAlta = new SimpleDateFormat("dd/MM/yyyy HH:mm");        
			dthrAltaMedica = dfAlta.format(internacao.getDthrAltaMedica());
		}
		if (internacao.getTipoAltaMedica() != null){
			codigoAltaMedica = internacao.getTipoAltaMedica().getCodigo();				
			descricaoAltaMedica = internacao.getTipoAltaMedica().getDescricao();				
		}
		justificativa = null;
	}
	
	/**
	 * Método que visualiza a internação em sua tela principal para realizar o
	 * estorno
	 * @param internacao
	 * @return
	 */
	public String visualizarInternacaoEstorno() {
		this.carregarDados();
		return PAGE_ESTORNAR_INTERNACAO;
	}
	
	public String obterIdLeito(AinLeitos leito){
		String retorno = "";
		if (leito != null){
			retorno = leito.getLeitoID();
		}
		return retorno;
	}
	
	public String obterDescricaoQuarto(AinQuartos quarto){
		String retorno = "";
		if (quarto != null){
			retorno = quarto.getDescricao();
		}
		return retorno;
	}
	
	public String obterDescricaoUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional){
		String retorno = "";
		if (unidadeFuncional != null){
			retorno = unidadeFuncional.getAndarAlaDescricao();
		}
		return retorno;
	}
	
	public String obterNroRegConselhoNomeProfessor(AinInternacao internacao){
		ProfessorCrmInternacaoVO professor = obterCrmProfessor(internacao);
		return professor.getNroRegConselho() + " - " + professor.getNome();
	}
	
	private ProfessorCrmInternacaoVO obterCrmProfessor(AinInternacao internacao){
		ProfessorCrmInternacaoVO professor = internacaoFacade
		.obterProfessorCrmInternacaoVO(internacao
				.getServidorProfessor(), internacao.getEspecialidade(), internacao.getConvenioSaude().getCodigo());
		return professor;
	}
	
	/**
	 * Limpa campos da tela
	 */
	public void limparCampos(){
		listaInternacoes = new ArrayList<AinInternacao>();
		prontuarioPesquisa = null;
		nomePaciente = null;
		this.exibirListagem = false;
	}
	
	/**
	 * Método que limpa os valores dos campos da tela
	 */
	private void limparValores(){
		internacao = null;
		//prontuario = null;
		//nomePaciente = null;
		leitoID = null;
		descricaoQuarto = null;
		unidadeFuncionalSeq = null;
		descricaoUnidadeFuncional = null;
		unidadeFuncionalSeq = null;
		especialidade = new AghEspecialidades();
		nroRegConselho = null;
		nomeProfessor = null;
		codigoConvenio = null;
		codigoPlano = null;
		descricaoConvenioPlano = null;
		dthrAltaMedica = null;
		codigoAltaMedica = null;
		descricaoAltaMedica = null;
		justificativa = null;
		dthrInternacao = null;
		
	}
	
	/**
	 * Solicita verificação das regras pré-estorno
	 */
	public String verificarRegrasAntesModal(){
		try{
			if (internacao != null){
				internacao.setJustificativaAltDel(justificativa);
				if (estornarInternacaoFacade.verificarRegrasAntesEstornar(internacao.getSeq(), internacao.getJustificativaAltDel())){
					super.openDialog("modalConfirmacaoWG");			
				}				
			}
			else{
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PESQUISAR_INTERNACAO_PARA_ESTORNAR");		
			}
		}
		catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} 
		return null;
	}
	
	/**
	 * Método que estorna a internação
	 */
	public String confirmar(){

		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = getEnderecoIPv4HostRemoto().toString();
			} catch (UnknownHostException e) {
				nomeMicrocomputador = null;
			}
			
			estornarInternacaoFacade.estornarInternacao(internacao, nomeMicrocomputador, new Date());
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ESTORNO_INTERNACAO");
			limparValores();
			this.pesquisar();
			
			return PAGE_LISTAR_INTERNACAO;
		}  catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} 

		return null;
	}
	
	public String voltar(){
		limparValores();
		this.pesquisar();
		return PAGE_LISTAR_INTERNACAO;
	}

	//GETTERS	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public Short getUnidadeFuncionalSeq() {
		return unidadeFuncionalSeq;
	}

	public void setUnidadeFuncionalSeq(Short unidadeFuncionalSeq) {
		this.unidadeFuncionalSeq = unidadeFuncionalSeq;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public String getNomeProfessor() {
		return nomeProfessor;
	}

	public void setNomeProfessor(String nomeProfessor) {
		this.nomeProfessor = nomeProfessor;
	}

	public Short getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(Short codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public Byte getCodigoPlano() {
		return codigoPlano;
	}

	public void setCodigoPlano(Byte codigoPlano) {
		this.codigoPlano = codigoPlano;
	}

	public String getDescricaoConvenioPlano() {
		return descricaoConvenioPlano;
	}

	public void setDescricaoConvenioPlano(String descricaoConvenioPlano) {
		this.descricaoConvenioPlano = descricaoConvenioPlano;
	}

	public String getDthrInternacao() {
		return dthrInternacao;
	}

	public void setDthrInternacao(String dthrInternacao) {
		this.dthrInternacao = dthrInternacao;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public String getDthrAltaMedica() {
		return dthrAltaMedica;
	}

	public void setDthrAltaMedica(String dthrAltaMedica) {
		this.dthrAltaMedica = dthrAltaMedica;
	}

	public String getCodigoAltaMedica() {
		return codigoAltaMedica;
	}

	public void setCodigoAltaMedica(String codigoAltaMedica) {
		this.codigoAltaMedica = codigoAltaMedica;
	}

	public String getDescricaoAltaMedica() {
		return descricaoAltaMedica;
	}

	public void setDescricaoAltaMedica(String descricaoAltaMedica) {
		this.descricaoAltaMedica = descricaoAltaMedica;
	}

	public List<AinInternacao> getListaInternacoes() {
		return listaInternacoes;
	}

	public void setListaInternacoes(List<AinInternacao> listaInternacoes) {
		this.listaInternacoes = listaInternacoes;
	}

	public Integer getSeqInternacao() {
		return seqInternacao;
	}

	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}

	public boolean isExibirListagem() {
		return exibirListagem;
	}

	public void setExibirListagem(boolean exibirListagem) {
		this.exibirListagem = exibirListagem;
	}

	public Integer getProntuarioPesquisa() {
		return prontuarioPesquisa;
	}

	public void setProntuarioPesquisa(Integer prontuarioPesquisa) {
		this.prontuarioPesquisa = prontuarioPesquisa;
	}

	public String getDescricaoUnidadeFuncional() {
		return descricaoUnidadeFuncional;
	}

	public void setDescricaoUnidadeFuncional(String descricaoUnidadeFuncional) {
		this.descricaoUnidadeFuncional = descricaoUnidadeFuncional;
	}

	public String getDescricaoQuarto() {
		return descricaoQuarto;
	}

	public void setDescricaoQuarto(String descricaoQuarto) {
		this.descricaoQuarto = descricaoQuarto;
	}

}
