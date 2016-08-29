package br.gov.mec.aghu.exames.vo;

import java.util.List;


public class SolicitacaoColetarVO implements Comparable<SolicitacaoColetarVO>{

	private String descricao;
	private String andar;
	private String indAla2;
	private String recemNascido;
	private String informacoesClinicas;
	private String ltoLtoId;
	private String prontuario;
	private String nome;
	private String convenio;
	private String soeAtdSeq;
	private String criadoEm;
	private String iseSeqp;
	private String dthrProgramada;
	private String dthrProgramadaOrd;
	private String soeSeq;
	private String seqp;
	private String tempo;
	private String ind;
	private String descricao1;
	private String descricao2;
	private String sigla;
	private String descricaoUsual;
	private String ufeUnfSeq;
	private String tipoColeta;
	private String driverId;
	private String nroUnico;
	private String tipoRegistro;
	private String sitCodigo;
	private Integer sheSeq;
	private String dtNascimento;
	private String nomePessoaFisica;
	private String nroRegConselho;
	private String siglaConselhoRegional;

	private List<SolicitacaoColetarVO> list;

	public int compareTo(SolicitacaoColetarVO other) {
		int result = this.getDescricao().compareTo(other.getDescricao());
        if (result == 0) {
                if(this.getLtoLtoId() != null && other.getLtoLtoId() != null){
                	result = this.getLtoLtoId().compareTo(other.getLtoLtoId());
                	 if (result == 0) {
                		 if(this.getProntuario() != null && other.getProntuario() != null){
                         	result = this.getProntuario().compareTo(other.getProntuario());
                         	 if (result == 0) {
                         		if(this.getNome() != null && other.getNome() != null){
                                 	result = this.getNome().compareTo(other.getNome());
                                 	 if (result == 0) {
                                 		if(this.getSoeSeq() != null && other.getSoeSeq() != null){
                                         	result = this.getSoeSeq().compareTo(other.getSoeSeq());
                                         	 if (result == 0) {
                                         		if(this.getSeqp() != null && other.getSeqp() != null){
                                                 	result = this.getSeqp().compareTo(other.getSeqp());
                                                 	 if (result == 0) {
                                                 		if(this.getDthrProgramada() != null && other.getDthrProgramada() != null){
                                                         	result = this.getDthrProgramada().compareTo(other.getDthrProgramada());
                                                         	if (result == 0) {
                                                         		if(this.getDescricaoUsual() != null && other.getDescricaoUsual() != null){
                                                                 	result = this.getDescricaoUsual().compareTo(other.getDescricaoUsual());
                                                                 } 
                                                         	 }
                                                         } 
                                                 	 }
                                                 }  
                                         	 }
                                         }      
                                 	 }
                                 }      
                         	 }
                         }      
                	 }
                }                                
        }
        return result;
	}
	

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getAndar() {
		return andar;
	}

	public void setAndar(String andar) {
		this.andar = andar;
	}

	public String getIndAla2() {
		return indAla2;
	}

	public void setIndAla2(String indAla2) {
		this.indAla2 = indAla2;
	}

	public String getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(String recemNascido) {
		this.recemNascido = recemNascido;
	}

	public String getInformacoesClinicas() {
		return informacoesClinicas;
	}

	public void setInformacoesClinicas(String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getSoeAtdSeq() {
		return soeAtdSeq;
	}

	public void setSoeAtdSeq(String soeAtdSeq) {
		this.soeAtdSeq = soeAtdSeq;
	}

	public String getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(String iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public String getDthrProgramada() {
		return dthrProgramada;
	}

	public void setDthrProgramada(String dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}

	public String getDthrProgramadaOrd() {
		return dthrProgramadaOrd;
	}

	public void setDthrProgramadaOrd(String dthrProgramadaOrd) {
		this.dthrProgramadaOrd = dthrProgramadaOrd;
	}

	public String getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(String soeSeq) {
		this.soeSeq = soeSeq;
	}

	public String getSeqp() {
		return seqp;
	}

	public void setSeqp(String seqp) {
		this.seqp = seqp;
	}

	public String getTempo() {
		return tempo;
	}

	public void setTempo(String tempo) {
		this.tempo = tempo;
	}

	public String getInd() {
		return ind;
	}

	public void setInd(String ind) {
		this.ind = ind;
	}

	public String getDescricao1() {
		return descricao1;
	}

	public void setDescricao1(String descricao1) {
		this.descricao1 = descricao1;
	}

	public String getDescricao2() {
		return descricao2;
	}

	public void setDescricao2(String descricao2) {
		this.descricao2 = descricao2;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricaoUsual() {
		return descricaoUsual;
	}

	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}

	public String getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(String ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}

	public String getTipoColeta() {
		return tipoColeta;
	}

	public void setTipoColeta(String tipoColeta) {
		this.tipoColeta = tipoColeta;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {	
		this.driverId = driverId;
	}

	public String getNroUnico() {
		return nroUnico;
	}

	public void setNroUnico(String nroUnico) {
		this.nroUnico = nroUnico;
	}
	
	public List<SolicitacaoColetarVO> getList() {
		return list;
	}

	public void setList(List<SolicitacaoColetarVO> list) {
		this.list = list;
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public String getSitCodigo() {
		return sitCodigo;
	}

	public void setSitCodigo(String sitCodigo) {
		this.sitCodigo = sitCodigo;
	}

	public Integer getSheSeq() {
		return sheSeq;
	}

	public void setSheSeq(Integer sheSeq) {
		this.sheSeq = sheSeq;
	}

	public String getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(String dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public String getNomePessoaFisica() {
		return nomePessoaFisica;
	}

	public void setNomePessoaFisica(String nomePessoaFisica) {
		this.nomePessoaFisica = nomePessoaFisica;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public String getSiglaConselhoRegional() {
		return siglaConselhoRegional;
	}

	public void setSiglaConselhoRegional(String siglaConselhoRegional) {
		this.siglaConselhoRegional = siglaConselhoRegional;
	}
	
	
	
}
