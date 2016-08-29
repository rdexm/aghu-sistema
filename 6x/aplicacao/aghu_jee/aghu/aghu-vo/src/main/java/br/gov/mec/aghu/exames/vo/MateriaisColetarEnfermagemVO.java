package br.gov.mec.aghu.exames.vo;

public class MateriaisColetarEnfermagemVO implements Comparable<MateriaisColetarEnfermagemVO>{

	private String titulo;
	private String unfDescricao;
	private String andarAlaDescricao;
	private String qrtLeito;
	private String prontuario;
	private String nomePaciente;
	private String dthrProgramada;
	private String solicitacao;
	private String descricaoExameMaterialAnalise;
	private String nroAmostras;
	private String descricao;
	
	public int compareTo(MateriaisColetarEnfermagemVO other) {
        int result = this.getQrtLeito().compareTo(other.getQrtLeito());
        
        if (result == 0){
        	result = this.getSolicitacao().compareTo(other.getSolicitacao());
        }

    	if (result == 0){
    		result = this.getDthrProgramada().compareTo(other.getDthrProgramada());
    	}
        
        if (result == 0) {
                if(this.getProntuario() != null && other.getProntuario() != null){
                         return this.getProntuario().compareTo(other.getProntuario());
                }                                
        }
        
        return result;
	}
	
	public MateriaisColetarEnfermagemVO() {
		// TODO Auto-generated constructor stub
	}
	
	public MateriaisColetarEnfermagemVO(String titulo, String unfDescricao,
			String andarAlaDescricao, String qrtLeito, String prontuario,
			String nomePaciente, String dthrProgramada, String solicitacao,
			String descricaoExameMaterialAnalise, String nroAmostras,
			String descricao) {
		super();
		this.titulo = titulo;
		this.unfDescricao = unfDescricao;
		this.andarAlaDescricao = andarAlaDescricao;
		this.qrtLeito = qrtLeito;
		this.prontuario = prontuario;
		this.nomePaciente = nomePaciente;
		this.dthrProgramada = dthrProgramada;
		this.solicitacao = solicitacao;
		this.descricaoExameMaterialAnalise = descricaoExameMaterialAnalise;
		this.nroAmostras = nroAmostras;
		this.descricao = descricao;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}

	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}

	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}

	public String getQrtLeito() {
		return qrtLeito;
	}

	public void setQrtLeito(String qrtLeito) {
		this.qrtLeito = qrtLeito;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getDthrProgramada() {
		return dthrProgramada;
	}

	public void setDthrProgramada(String dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}

	public String getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getDescricaoExameMaterialAnalise() {
		return descricaoExameMaterialAnalise;
	}

	public void setDescricaoExameMaterialAnalise(
			String descricaoExameMaterialAnalise) {
		this.descricaoExameMaterialAnalise = descricaoExameMaterialAnalise;
	}

	public String getNroAmostras() {
		return nroAmostras;
	}

	public void setNroAmostras(String nroAmostras) {
		this.nroAmostras = nroAmostras;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
